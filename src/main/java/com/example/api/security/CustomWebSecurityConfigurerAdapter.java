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
        auth.inMemoryAuthentication().withUser("admin").password("abc").roles("ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .httpBasic()
              .and()
                .authorizeRequests()
                  .antMatchers(HttpMethod.POST, "/api/**").permitAll()//hasRole("ADMIN")
                  .antMatchers(HttpMethod.GET, "/api/**").permitAll();
                  //.anyRequest().authenticated();
        /*http
                .authorizeRequests()
                .antMatchers("/", "/index.html").permitAll()
                .and()
                .anyRequest().authenticated();
        /*
        http.authorizeRequests()
                .antMatchers("/", "/home").permitAll()
                //.antMatchers("/admin/**").access("hasRole('ADMIN')")
                //.antMatchers("/db/**").access("hasRole('ADMIN') and hasRole('DBA')")
                .and().formLogin().loginPage("/login")
                .usernameParameter("user").passwordParameter("password")
                .and().csrf()
                .and().exceptionHandling().accessDeniedPage("/Access_Denied");
                */

    }

}
