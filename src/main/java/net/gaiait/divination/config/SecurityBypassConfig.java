package net.gaiait.divination.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@Profile("insecure")
class SecurityBypassConfig extends WebSecurityConfigurerAdapter {

    /**
     * For development purposes, we enable all pages to anonymous users + so we can freely test
     * pages + submissions without having to authenticate. In SecurityConfig, we're much more
     * secure!
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.anonymous();
    }


}
