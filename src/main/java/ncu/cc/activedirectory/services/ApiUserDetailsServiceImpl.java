package ncu.cc.activedirectory.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ncu.cc.activedirectory.constants.BeanIds;
import ncu.cc.activedirectory.entities.Apiuser;
import ncu.cc.activedirectory.models.ApiuserDetails;
import ncu.cc.activedirectory.models.MyAuthorityEnum;
import ncu.cc.activedirectory.repositories.ApiuserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@Component
@Qualifier(BeanIds.APIUSER_USER_DETAIL_SERVICE)
public class ApiUserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private ApiuserRepository apiuserRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<Apiuser> byId = apiuserRepository.findById(s);

        if (byId.isPresent()) {
            Apiuser apiuser = byId.get();
            ApiuserDetails apiuserDetails = new ApiuserDetails(apiuser);

            if (apiuser.getRoleset() != null && ! "".equals(apiuser.getRoleset())) {
                Type listType = new TypeToken<List<String>>() {}.getType();
                List<String> list = new Gson().fromJson(apiuser.getRoleset(), listType);

                list.forEach(role -> apiuserDetails.addAuthority(MyAuthorityEnum.byName("ROLE_" + role)));
            }

//            StackTraceUtil.print1(apiuserDetails);
            return apiuserDetails;
        } else {
//            StackTraceUtil.print1("User not found: " + s);
            throw new UsernameNotFoundException(s);
        }
//        return new ApiuserDetails();
    }
}
