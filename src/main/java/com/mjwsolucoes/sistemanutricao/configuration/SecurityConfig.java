package com.mjwsolucoes.sistemanutricao.configuration;

import com.mjwsolucoes.sistemanutricao.model.AlvoAtividade;
import com.mjwsolucoes.sistemanutricao.model.TipoAtividade;
import com.mjwsolucoes.sistemanutricao.service.AtividadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
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
    private final AtividadeService atividadeService;

    /** Cargos que podem criar e editar conteúdo próprio. */
    private static final String[] AUTORES = {"ADMIN", "NUTRICIONISTA", "ESTUDANTE"};
    /** Cargos que podem arquivar/desarquivar fichas e refeições. */
    private static final String[] CURADORES = {"ADMIN", "NUTRICIONISTA"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // ─── 1. Rotas públicas ────────────────────────────────────────────
                        .requestMatchers(
                                "/",
                                "/home",
                                "/login",
                                "/registro",
                                "/registro/**",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/support"
                        ).permitAll()

                        // ─── 2. Painel administrativo ─────────────────────────────────────
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")

                        // ─── 3. Exclusão definitiva: só o admin ───────────────────────────
                        .requestMatchers("/receita/excluir/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/**").hasAuthority("ADMIN")

                        // ─── 4. Arquivar/desarquivar: nutricionista e admin ───────────────
                        .requestMatchers("/receita/arquivar/**", "/receita/desarquivar/**",
                                         "/refeicao/arquivar/**", "/refeicao/desarquivar/**")
                            .hasAnyAuthority(CURADORES)

                        // ─── 5. Criação e edição de conteúdo ──────────────────────────────
                        .requestMatchers("/fichatecnica", "/fichatecnica/**",
                                         "/receita/editar/**",
                                         "/criarIngrediente", "/alimentos/novo")
                            .hasAnyAuthority(AUTORES)
                        .requestMatchers(HttpMethod.POST, "/api/**").hasAnyAuthority(AUTORES)
                        .requestMatchers(HttpMethod.PUT, "/api/**").hasAnyAuthority(AUTORES)

                        // ─── 6. Consulta: qualquer usuário aprovado (inclui COZINHA) ──────
                        .requestMatchers("/dashboard", "/visualizar", "/alimentos", "/alimentos/**",
                                         "/refeicoes", "/receita/detalhes/**")
                            .authenticated()

                        // ─── 7. Default deny ──────────────────────────────────────────────
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .failureHandler((request, response, exception) -> {
                            // Conta ainda não aprovada pelo admin recebe uma mensagem própria
                            if (exception instanceof org.springframework.security.authentication.DisabledException) {
                                response.sendRedirect("/login?disabled");
                            } else {
                                response.sendRedirect("/login?error");
                            }
                        })
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"))
                        // Anota a saída antes da sessão ser invalidada
                        .addLogoutHandler((request, response, authentication) -> {
                            if (authentication != null) {
                                atividadeService.registrarComo(authentication.getName(),
                                        TipoAtividade.LOGOUT, AlvoAtividade.SESSAO,
                                        "Saiu do sistema", null);
                            }
                        })
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .csrf(csrf -> csrf.disable()) // Desabilitado para dev, ATIVAR em produção!
                .cors(cors -> cors.disable());

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
}
