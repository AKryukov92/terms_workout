package ru.ominit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

/**
 * Created by Александр on 29.03.2018.
 */
@Configuration
@EnableOAuth2Sso
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String HOME_URL = "/home";
    public static final String SPHINX_URL = "/sphinx";
    public static final String JOURNEY_URL = "/journey";

    @Autowired
    private OAuth2ClientContextFilter oauth2ClientContextFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String[] staticResources = {
            "favicon.ico",
            "code.js",
            "jquery-3.3.1.min.js",
            "styles.css"
        };
        http
            .authorizeRequests()
                .antMatchers("/", "/login", "/error").permitAll()
                .antMatchers(staticResources).anonymous()
                .antMatchers(HOME_URL, SPHINX_URL, JOURNEY_URL).authenticated()
                .anyRequest().authenticated().and()
                .addFilterAfter(oauth2ClientContextFilter, SecurityContextPersistenceFilter.class)
            .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
            .logout()
                .permitAll();
    }
}
