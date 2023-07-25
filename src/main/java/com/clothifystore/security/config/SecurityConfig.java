package com.clothifystore.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests()
                    .antMatchers(HttpMethod.POST,"/customer").permitAll()
                    .antMatchers(HttpMethod.GET,"/customer").hasRole("ADMIN")
                    .antMatchers("/customer/{id}").hasAnyRole("ADMIN","CUSTOMER")

                    .antMatchers(HttpMethod.POST,"/order").permitAll()
                    .antMatchers(HttpMethod.GET,"/order").hasRole("ADMIN")
                    .antMatchers("/order/{id}").hasRole("ADMIN")
                    .antMatchers("/order/status/{status}").hasRole("ADMIN")
                    .antMatchers("/order/customer/{id}").hasRole("CUSTOMER")
                    .antMatchers("/order/{id}/{status}").hasRole("ADMIN")

                    .antMatchers("/product").hasRole("ADMIN")
                    .antMatchers(HttpMethod.GET,"/product/{id}").permitAll()
                    .antMatchers(HttpMethod.PUT,"/product/{id}").hasRole("ADMIN")
                    .antMatchers(HttpMethod.DELETE,"/product/{id}").hasRole("ADMIN")
                    .antMatchers("/product/category/{category}").permitAll()
                    .antMatchers("/product/image/{filename}").permitAll()
                    .anyRequest().authenticated()

                .and()
                .httpBasic();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }
}
