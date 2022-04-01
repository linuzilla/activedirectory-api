package ncu.cc.activedirectory.config;

import ncu.cc.activedirectory.models.MyAuthorityEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static ncu.cc.activedirectory.constants.BeanIds.APIUSER_USER_DETAIL_SERVICE;
import static ncu.cc.activedirectory.constants.Constants.*;

/**
 * @author Jiann-Ching Liu (saber@g.ncu.edu.tw)
 * @version 1.0
 * @since 1.0
 */
@EnableWebSecurity
public class MultiHttpSecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(MultiHttpSecurityConfig.class);
    private static final String REALM = "AD Sync";
    public static final String USER = MyAuthorityEnum.ROLE_USER.shortName();
    public static final String ADMIN = MyAuthorityEnum.ROLE_ADMIN.shortName();
    public static final String DEVELOPER = MyAuthorityEnum.ROLE_DEVELOPER.shortName();
    private static String RESTFUL_API_ACCESS = "hasAnyRole('" + ADMIN + "', '" + DEVELOPER + "')";
    private static String LOCAL_ACCESS = "hasIpAddress('127.0.0.1') or hasIpAddress('0:0:0:0:0:0:0:1')";

    @Configuration
    @Order(1)
    public static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
        @Autowired
        @Qualifier("apiAccessDeniedHandler")
        private AccessDeniedHandler accessDeniedHandler;
        @Autowired
        @Qualifier(APIUSER_USER_DETAIL_SERVICE)
        private UserDetailsService userDetailsService;
        @Value("${application.api.ip-acl}")
        private String ipAcl;

        protected void configure(HttpSecurity http) throws Exception {
            String spExAcl = RESTFUL_API_ACCESS;

            if (ipAcl != null && ! "".equals(ipAcl)) {
                spExAcl = RESTFUL_API_ACCESS + " and ( "+ ipAcl + " )";
            }

            logger.info(spExAcl);
            http
                    .antMatcher(API_PATH + "/**")
                    .authorizeRequests()
                    .antMatchers(PUBLIC_API_PATH + "/**").permitAll()
                    .antMatchers(LOCAL_API_PATH + "/**").access(LOCAL_ACCESS)
                    .anyRequest().access(spExAcl)
                    .and()
                    .csrf().disable()
                    .httpBasic().realmName(REALM)
                    .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        }

        public BCryptPasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        };

        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        }
    }

    @Configuration
    public static class SecurityConfigure extends WebSecurityConfigurerAdapter {
        public static String ADMIN = MyAuthorityEnum.ROLE_ADMIN.shortName();
        public static String DEVELOPER = MyAuthorityEnum.ROLE_DEVELOPER.shortName();
        @Autowired
        @Qualifier("webAccessDeniedHandler")
        private AccessDeniedHandler accessDeniedHandler;
        @Autowired
        private UserDetailsService userDetailsService;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.headers().frameOptions().sameOrigin()
                    .and()
                    .authorizeRequests()
                    .antMatchers("/", "/home", "/about", "/webjars/**", "/resources/**").permitAll()
                    .antMatchers("/signon", "/flash").permitAll()
                    .antMatchers("/robot.txt", "/security.txt", "/favicon.ico").permitAll()
                    .antMatchers("/admin/**").hasAnyRole(ADMIN, DEVELOPER)
                    .antMatchers("/gsuite/**").hasAnyRole(ADMIN, DEVELOPER)
                    .antMatchers("/repos/**").hasAnyRole(ADMIN, DEVELOPER)
                    .anyRequest().authenticated()
                    .and()
                    .formLogin().loginPage("/login").permitAll()
                    .and()
                    .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .and()
                    .exceptionHandling().accessDeniedHandler(accessDeniedHandler);
        }

        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//            auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
            auth.inMemoryAuthentication()
                    .withUser("user").password("{noop}pass").roles("USER")
                    .and()
                    .withUser("admin").password("{noop}pass").roles("ADMIN");
        }

        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        };

        @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }
    }
}