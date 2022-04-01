package ncu.cc.activedirectory.services;

import ncu.cc.activedirectory.entities.Apilog;
import ncu.cc.activedirectory.models.ApiLogEventEnum;
import ncu.cc.activedirectory.models.ApiResult;
import ncu.cc.activedirectory.models.ApiResultCodeEnum;
import ncu.cc.activedirectory.models.ChangePasswordModel;
import ncu.cc.activedirectory.properties.ActiveDirectoryProperties;
import ncu.cc.activedirectory.repositories.ApilogRepository;
import ncu.cc.activedirectory.security.AuthenticationService;
import ncu.cc.activedirectory.utils.LdapPoolUtil;
import ncu.cc.commons.utils.StackTraceUtil;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.DefaultModification;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.Modification;
import org.apache.directory.api.ldap.model.entry.ModificationOperation;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;
import org.apache.directory.api.ldap.model.exception.LdapUnwillingToPerformException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapConnectionPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@EnableConfigurationProperties(ActiveDirectoryProperties.class)
public class ActiveDirectoryServiceImpl implements ActiveDirectoryService {
    public static final String UNICODE_PWD = "unicodePwd";
    public static final String USER_ACCOUNT_CONTROL = "userAccountControl";
    public static final String  USER_ACCOUNT_CONTROL_ENABLE_VALUE = "512";
    public static final String USER_ACCOUNT_CONTROL_DISABLE_VALUE = "514";
    public static final String CN_USERS = "CN=Users,";

    @Autowired
    private ActiveDirectoryProperties activeDirectoryProperties;
    @Autowired
    private ApilogRepository apilogRepository;
    @Autowired
    private AuthenticationService authenticationService;

    @FunctionalInterface
    public interface FindFirstAndDoFunction {
        Object foundAndDo(LdapConnection connection, Entry entry);
    }

    private void log(String account, ApiLogEventEnum apiLogEventEnum) {
        Apilog apilog = new Apilog();

        apilog.setAccount(account);
        apilog.setEvnet(apiLogEventEnum.getValue());
        apilog.setPerformBy(authenticationService.currentUser());

        apilogRepository.save(apilog);
    }

    @Override
    public String retrieve() {
        LdapConnectionPool pool = LdapPoolUtil.getPool(activeDirectoryProperties);

        try {
            LdapConnection connection = pool.getConnection();

            Dn systemDn = new Dn(CN_USERS + activeDirectoryProperties.getBaseDn());

            EntryCursor cursor = connection.search(systemDn, "(objectclass=user)", SearchScope.ONELEVEL);

            for (Entry entry : cursor) {
                System.out.println(entry.getDn().getName());
            }

            cursor.close();
            pool.releaseConnection(connection);
        } catch (LdapException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "done";
    }

    /*
     * Retrieves all the persons in the ldap server
     *
     * @return list of person names
     */
    @Override
    public ApiResult getAllPersonNames() {
        List<String> list = new ArrayList<>();

        Exception ex = findAllAndDo(activeDirectoryProperties, "(&(objectclass=user)(cn=*))", (connection, entry) -> {
            try {
                list.add(entry.get("CN").getString());
            } catch (LdapInvalidAttributeValueException e) {
            }
            return null;
        });

        if (ex != null) {
            return new ApiResult(ex);
        } else {
            ApiResult result = new ApiResult(ApiResultCodeEnum.SUCCESS);
            result.setResult(list);

            return result;
        }
    }

    @Override
    public ApiResult findById(String acct) {
        final Map<String,Object> results = new HashMap<>();

        findFirstAndDo(activeDirectoryProperties, acct, (connection, entry) -> {
            entry.getAttributes().forEach(attr -> {
                try {
                    results.put(attr.getId(), attr.getString());
                } catch (LdapInvalidAttributeValueException e) {}
            });
            return entry;
        });

        ApiResult result = new ApiResult(ApiResultCodeEnum.SUCCESS);
        result.setResult(results);

        return result;
    }

    private byte[] passwordConvert(String passwd) {
        return ("\"" + passwd + "\"").getBytes(StandardCharsets.UTF_16LE);
    }

    public static Object findFirstAndDo(ActiveDirectoryProperties properties, String account, FindFirstAndDoFunction actionHandler) {
        LdapConnectionPool pool = LdapPoolUtil.getPool(properties);

        LdapConnection connection = null;

        try {
            connection = pool.getConnection();

            Dn systemDn = new Dn(CN_USERS + properties.getBaseDn());

            EntryCursor cursor = connection.search(systemDn, "(&(objectclass=user)(cn=" + account + "))", SearchScope.ONELEVEL);

            Map<String,Object> results = new HashMap<>();

            try {
                for (Entry entry : cursor) {
                    return actionHandler.foundAndDo(connection, entry);
                }
            } finally {
                cursor.close();
            }
//            connection.close();
        } catch (LdapException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    pool.releaseConnection(connection);
                } catch (LdapException e) {
                }
            }
        }
        return null;
    }

    public static Exception findAllAndDo(ActiveDirectoryProperties properties, String filter, FindFirstAndDoFunction actionHandler) {
        LdapConnectionPool pool = LdapPoolUtil.getPool(properties);
        LdapConnection connection = null;

        try {
            connection = pool.getConnection();

            Dn systemDn = new Dn(CN_USERS + properties.getBaseDn());

            EntryCursor cursor = connection.search(systemDn, filter, SearchScope.ONELEVEL);

            Map<String,Object> results = new HashMap<>();

            try {
                for (Entry entry : cursor) {
                    actionHandler.foundAndDo(connection, entry);
                }
            } finally {
                cursor.close();
            }
//            connection.close();
            return null;
        } catch (Exception e) {
            return e;
        } finally {
            if (connection != null) {
                try {
                    pool.releaseConnection(connection);
                } catch (LdapException e) {
                }
            }
        }
    }

    @Override
    public ApiResult changePassword(final ChangePasswordModel model) {
        String original = new String(Base64.getDecoder().decode(model.getPassword())).trim();

        if (original.length() < 7) {
            return new ApiResult(ApiResultCodeEnum.PASSWORD_TOO_SHORT);
        }

        byte[] unicodePwd = passwordConvert(original);

//        System.out.println(Arrays.toString(unicodePwd));

        final Modification modifyUnicodePwd = new DefaultModification(
                ModificationOperation.REPLACE_ATTRIBUTE,
                UNICODE_PWD, unicodePwd);

        Object result = findFirstAndDo(activeDirectoryProperties, model.getAccount(), (connection, entry) -> {
            try {
                connection.modify(entry.getDn(), modifyUnicodePwd);
                StackTraceUtil.print1("password changed for: " + entry.getDn().getName());
                log(model.getAccount(), ApiLogEventEnum.PASSWORD);
                return new ApiResult(ApiResultCodeEnum.SUCCESS);
            } catch (LdapUnwillingToPerformException e) {
                return new ApiResult(e);
            } catch (LdapException e) {
                return new ApiResult(e);
            }
        });

        if (result == null) {
            return new ApiResult(ApiResultCodeEnum.USER_NOT_FOUND);
        } else {
            return (ApiResult) result;
        }
    }

    private ApiResult updateUserAccountControl(String account, String value) {
        final Modification modifyUserAccountControl = new DefaultModification(
                ModificationOperation.REPLACE_ATTRIBUTE,
                USER_ACCOUNT_CONTROL, value);

        Object result = findFirstAndDo(activeDirectoryProperties, account, (connection, entry) -> {
            try {
                connection.modify(entry.getDn(), modifyUserAccountControl);
                StackTraceUtil.print1("update " + USER_ACCOUNT_CONTROL + " to " + value + " for: " + entry.getDn().getName());

                switch (value) {
                    case USER_ACCOUNT_CONTROL_DISABLE_VALUE:
                        log(account, ApiLogEventEnum.SUSPEND);
                        break;
                    case USER_ACCOUNT_CONTROL_ENABLE_VALUE:
                        log(account, ApiLogEventEnum.RESUME);
                        break;
                }

                return new ApiResult(ApiResultCodeEnum.SUCCESS);
            } catch (LdapUnwillingToPerformException e) {
                return new ApiResult(e);
            } catch (LdapException e) {
                return new ApiResult(e);
            }
        });

        if (result == null) {
            return new ApiResult(ApiResultCodeEnum.USER_NOT_FOUND);
        } else {
            return (ApiResult) result;
        }
    }

    @Override
    public ApiResult suspendUser(String account) {
        return updateUserAccountControl(account, USER_ACCOUNT_CONTROL_DISABLE_VALUE);
    }

    @Override
    public ApiResult resumeUser(String account) {
        return updateUserAccountControl(account, USER_ACCOUNT_CONTROL_ENABLE_VALUE);
    }
}
