package com.mjwsolucoes.sistemanutricao.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration; // Mantenha este se usar authenticationManager bean
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Define as rotas que não exigem autenticação
                        // A página de login e registro estão aqui
                        .requestMatchers("/login", "/registro",
                                "/css/**", "/js/**", "/images/**", "/support").permitAll()
                        // Protege rotas baseadas na autoridade
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")
                        .requestMatchers("/fichatecnica/**").hasAnyAuthority("NUTRICIONISTA", "ADMIN", "USER")
                        // Qualquer outra requisição que não foi explicitamente permitida ou protegida deve ser autenticada
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .failureHandler((request, response, exception) -> {
                            // Este failureHandler é bom para depuração de erros específicos
                            if (exception.getClass().getSimpleName().equals("DisabledException")) {
                                response.sendRedirect("/login?disabled");
                            } else {
                                response.sendRedirect("/login?error");
                            }
                        })
                        .defaultSuccessUrl("/dashboard", true) // Redireciona para o dashboard após login bem-sucedido
                        .permitAll() // Permite acesso às URLs do formLogin (GET /login e POST /login)
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET")) // URL para logout via GET
                        .logoutSuccessUrl("/login?logout") // Redireciona para /login com status de logout
                        .permitAll()
                )
                .csrf(csrf -> csrf.disable()) // Desabilita CSRF (para desenvolvimento, reativar em produção)
                .cors(cors -> cors.disable()); // Desabilita CORS (para desenvolvimento, reativar em produção)

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // O bean AuthenticationManager é útil se você for injetá-lo em algum lugar
    // para autenticação manual, mas o DaoAuthenticationProvider já cuida do fluxo padrão.
    // @Bean
    // public AuthenticationManager authenticationManager(
    //         AuthenticationConfiguration config) throws Exception {
    //     return config.getAuthenticationManager();
    // }
}