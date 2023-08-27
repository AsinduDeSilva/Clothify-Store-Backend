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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

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
        http.csrf().disable().cors().and()
                .authorizeHttpRequests()
                    .antMatchers("/authenticate").permitAll()
                    .antMatchers("/admin/**").hasRole("ADMIN")

                    .antMatchers("/customer").permitAll()
                    .antMatchers("/customer/verify").permitAll()
                    .antMatchers("/customer/resend-otp").permitAll()
                    .antMatchers("/customer/page/{page}").hasRole("ADMIN")
                    .antMatchers("/customer/email").hasAnyRole("ADMIN","CUSTOMER")
                    .antMatchers("/customer/{id}").hasAnyRole("ADMIN","CUSTOMER")
                    .antMatchers("/customer/password/{id}").hasRole("CUSTOMER")
                    .antMatchers("/customer/cart/**").hasRole("CUSTOMER")

                    .antMatchers("/order").hasRole("CUSTOMER")
                    .antMatchers("/order/customer/**").hasRole("CUSTOMER")
                    .antMatchers("/order/{id}").hasRole("ADMIN")
                    .antMatchers("/order/status/{status}").hasRole("ADMIN")
                    .antMatchers("/order/stats").hasRole("ADMIN")
                    .antMatchers("/order/week").hasRole("ADMIN")

                    .antMatchers("/product/category/{category}").permitAll()
                    .antMatchers("/product/image/{filename}").permitAll()
                    .antMatchers(HttpMethod.GET,"/product/{id}").permitAll()
                    .antMatchers("/product").hasRole("ADMIN")
                    .antMatchers("/product/image/{id}").hasRole("ADMIN")
                    .antMatchers(HttpMethod.PUT,"/product/{id}").hasRole("ADMIN")
                    .antMatchers(HttpMethod.DELETE,"/product/{id}").hasRole("ADMIN")

                    .anyRequest().authenticated()

                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
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
