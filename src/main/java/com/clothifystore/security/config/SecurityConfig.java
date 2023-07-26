package com.clothifystore.security.config;

import com.clothifystore.security.filter.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(getPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests()
                    .antMatchers("/authenticate").permitAll()

                    .antMatchers(HttpMethod.POST,"/customer").permitAll()
                    .antMatchers(HttpMethod.GET,"/customer").hasRole("ADMIN")
                    .antMatchers("/customer/{id}").hasAnyRole("ADMIN","CUSTOMER")
                    .antMatchers("/customer/email/{email}").hasAnyRole("ADMIN","CUSTOMER")

                    .antMatchers(HttpMethod.POST,"/order").permitAll()
                    .antMatchers(HttpMethod.GET,"/order", "/order/").hasRole("ADMIN")
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
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

    }

    @Bean
    public PasswordEncoder getPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
