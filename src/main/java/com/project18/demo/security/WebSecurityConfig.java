package com.project18.demo.security;

import com.project18.demo.service.UserDetailsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.AccessDeniedHandler;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String[] LOCATIONS = {"/", "/css/styles.css", "bulma.min.css"};
    private final AccessDeniedHandler accessDeniedHandler;
    private final UserDetailsProvider userDetailsProvider;

    @Autowired
    public WebSecurityConfig(AccessDeniedHandler accessDeniedHandler, UserDetailsProvider userDetailsProvider) {
        this.accessDeniedHandler = accessDeniedHandler;
        this.userDetailsProvider = userDetailsProvider;
    }

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .cors()
                .and().authorizeRequests()
                //WEB
                .antMatchers(LOCATIONS).permitAll()
                .antMatchers("/").hasAnyAuthority("USER", "CREATOR", "EDITOR", "ADMIN")
                .antMatchers("/new").hasAnyAuthority("ADMIN", "CREATOR")
                .antMatchers("/edit/**").hasAnyAuthority("ADMIN", "EDITOR")
                .antMatchers("/delete/**").hasAnyAuthority("ADMIN", "USER")
                .antMatchers("/settings").hasAuthority("ADMIN")
                .antMatchers("/settings/downloadCsv").hasAuthority("ADMIN")
                .antMatchers("/settings/downloadCsv/exported.csv").hasAuthority("ADMIN")
                .antMatchers("/posts").authenticated()
                .antMatchers("/posts/tylkomojeposty").authenticated()

                // REST API
                .antMatchers(HttpMethod.POST, "/api/signup").permitAll()
                .antMatchers(HttpMethod.POST, "/api/post/my").authenticated()
                .antMatchers(HttpMethod.POST, "/api/post").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/posts/{id}").hasAuthority("ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/posts/**").hasAuthority("USER")
                .antMatchers(HttpMethod.PUT, "/api/admin/posts/{id}").hasAuthority("ADMIN")
                .antMatchers(HttpMethod.POST, "/api/comments/my").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/comments**").hasAuthority("ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/comments/{id}").hasAuthority("USER")
                .antMatchers(HttpMethod.PUT, "/api/admin/comments/{id}").hasAuthority("ADMIN")
                // OTHER
                .anyRequest().permitAll()
                .and()
                .formLogin()
                .loginPage("/login")
                .usernameParameter("email")
                .defaultSuccessUrl("/posts")
                .permitAll()
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)
                .and()
                .logout().logoutSuccessUrl("/").permitAll()
                .and().httpBasic();
    }
}

