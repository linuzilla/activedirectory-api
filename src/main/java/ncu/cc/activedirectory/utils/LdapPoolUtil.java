package ncu.cc.activedirectory.utils;

import ncu.cc.activedirectory.properties.ActiveDirectoryProperties;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.directory.ldap.client.api.DefaultLdapConnectionFactory;
import org.apache.directory.ldap.client.api.DefaultPoolableLdapConnectionFactory;
import org.apache.directory.ldap.client.api.LdapConnectionConfig;
import org.apache.directory.ldap.client.api.LdapConnectionPool;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;

public class LdapPoolUtil {
    private static LdapConnectionPool pool = null;

    public synchronized static LdapConnectionPool getPool(ActiveDirectoryProperties activeDirectoryProperties) {
        if (pool == null) {
            trustSelfSignedSSL();

            String password = new String(Base64.getDecoder().decode(activeDirectoryProperties.getPassword()));

            LdapConnectionConfig config = new LdapConnectionConfig();
            config.setLdapHost(activeDirectoryProperties.getHost());
            config.setLdapPort(activeDirectoryProperties.getPort());
            config.setUseSsl(activeDirectoryProperties.isSsl());
            config.setName(activeDirectoryProperties.getManager());
            config.setCredentials(password);

            DefaultLdapConnectionFactory factory = new DefaultLdapConnectionFactory(config);
            factory.setTimeOut(activeDirectoryProperties.getConnectionTimeout());

            // optional, values below are defaults
            GenericObjectPool.Config poolConfig = new GenericObjectPool.Config();
            poolConfig.lifo = true;
            poolConfig.maxActive = 8;
            poolConfig.maxIdle = 8;
            poolConfig.maxWait = -1L;
            poolConfig.minEvictableIdleTimeMillis = 1000L * 60L * 30L;
            poolConfig.minIdle = 0;
            poolConfig.numTestsPerEvictionRun = 3;
            poolConfig.softMinEvictableIdleTimeMillis = -1L;
            poolConfig.testOnBorrow = true;
            poolConfig.testOnReturn = false;
            poolConfig.testWhileIdle = false;
            poolConfig.timeBetweenEvictionRunsMillis = -1L;
            poolConfig.whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_BLOCK;

            pool = new LdapConnectionPool(
                    new DefaultPoolableLdapConnectionFactory(factory), poolConfig);
        }

        return pool;
    }

    private static void trustSelfSignedSSL() {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {

                public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            ctx.init(null, new TrustManager[]{tm}, null);
            SSLContext.setDefault(ctx);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
