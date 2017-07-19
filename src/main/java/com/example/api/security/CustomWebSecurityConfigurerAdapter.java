package com.example.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Created by yaweiw on 7/18/2017.
 */
@Configuration
@EnableWebSecurity
public class CustomWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
    @Autowired
    public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("user").password("abc").roles("USER");
        auth.inMemoryAuthentication().withUser("admin").password("abc").roles("USER_ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                  .antMatchers("/").authenticated()
                  .antMatchers(HttpMethod.GET,"/api/todolist/**").hasRole("USER")
                  .antMatchers(HttpMethod.POST,"/api/todolist/**").hasRole("USER_ADMIN")
                  .antMatchers(HttpMethod.PUT,"/api/todolist/**").hasRole("USER_ADMIN")
                .and()
                  .formLogin()
                  .permitAll();
    }

}
