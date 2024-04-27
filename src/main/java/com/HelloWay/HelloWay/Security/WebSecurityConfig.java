    package com.HelloWay.HelloWay.Security;

    import com.HelloWay.HelloWay.Security.Jwt.*;
    import com.HelloWay.HelloWay.services.UserService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.http.HttpMethod;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
    import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
    import org.springframework.security.config.http.SessionCreationPolicy;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

    @Configuration
    @EnableWebSecurity
    @EnableGlobalMethodSecurity(
            // securedEnabled = true,
            // jsr250Enabled = true,
            prePostEnabled = true)
    public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
        @Autowired
        UserService userDetailsService;


        @Autowired
        private AuthEntryPointJwt unauthorizedHandler;

        @Bean
        public AuthTokenFilter authenticationJwtTokenFilter() {
            return new AuthTokenFilter();
        }

        @Override
        public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
            authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());

        }

        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public QrCodeAuthFilter qrCodeAuthFilter() throws Exception {
            QrCodeAuthFilter filter = new QrCodeAuthFilter(authenticationManager());
            filter.setAuthenticationManager(authenticationManagerBean());
            filter.setFilterProcessesUrl("/api/auth/login-qr");
            return filter;
        }


        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.cors().and().csrf().disable()
                    .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).and()
                    .authorizeRequests()
                    .antMatchers("/api/auth/**").permitAll()
                    .antMatchers("/api/test/**").permitAll()
                    .antMatchers(HttpMethod.POST, "/api/auth/login-qr").permitAll() // Allow QR code authentication endpoint
                    .antMatchers(HttpMethod.GET, "/api/spaces/all/dto").permitAll() // Allow QR code authentication endpoint
            //        .antMatchers("/api/users").hasRole("ADMIN") // Allow access for users with ROLE_ADMIN
                    .anyRequest().authenticated();

            http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);


            // ...
        }
            //  http.addFilterAfter(qrCodeAuthFilter(), UsernamePasswordAuthenticationFilter.class);
    }
