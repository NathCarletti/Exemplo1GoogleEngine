package br.com.caroltr.gae_exemplo1.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

    @Profile("auth_basic")
    @Configuration
    @EnableWebSecurity
    @EnableGlobalMethodSecurity (prePostEnabled = true)
    public class SpringSecurityConfigHttpBasic extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable()
                    .authorizeRequests()
                    .antMatchers("_ah/**").permitAll()
                    .antMatchers("/api/cron/testcron/**").anonymous()
                    .anyRequest().authenticated()
                    .and().httpBasic()
                    .and().sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        }


        /*@Override

        protected	void	configure(HttpSecurity	http)	throws	Exception	{
            http.csrf().disable()
                    .authorizeRequests()
                    .antMatchers("_ah/**").permitAll()
                    .anyRequest().authenticated()
                    .and().httpBasic()
                    .and().sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        }

      /*  @Autowired
        public	void configureGlobalSecurity(AuthenticationManagerBuilder auth)throws Exception {
            auth.inMemoryAuthentication()
                    .withUser("admin")
                    .password("admin")
                    .authorities("ADMIN");
            auth.inMemoryAuthentication()
                    .withUser("user")
                    .password("user")
                    .authorities("USER");
//ao inves de usar esses dois usuarios em memoria, vc vai chamar \/


        }*/

        @Autowired
        public	void	configureGlobalSecurity(AuthenticationManagerBuilder auth)
                throws	Exception	{
            auth.userDetailsService(userDetailsService());
        }


    }
