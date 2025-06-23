package com.mjwsolucoes.sistemanutricao.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // 1. ROTAS PÚBLICAS (acesso sem autenticação)
                        // Inclua todas as páginas de "entrada", recursos estáticos e suporte.
                        .requestMatchers(
                                "/",                // A raiz, que retorna 'index.html' (página de login)
                                "/home",            // /home, que também retorna 'index.html'
                                "/login",           // A página de login explicitamente
                                "/registro",        // A página de registro
                                "/css/**",          // Todos os arquivos CSS
                                "/js/**",           // Todos os arquivos JavaScript
                                "/images/**",       // Todas as imagens
                                "/support"          // Sua rota de suporte, se houver
                        ).permitAll()

                        // 2. ROTAS PROTEGIDAS POR AUTORIDADE ESPECÍFICA (precisam estar logado E ter a role)
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")
                        // Ajustado para incluir o endpoint EXATO /fichatecnica, além do padrão /**
                        .requestMatchers("/fichatecnica", "/fichatecnica/**").hasAnyAuthority("NUTRICIONISTA", "ADMIN", "USER")
                        // Se você tiver mais endpoints como /criarFichaTecnica que são separados,
                        // mas devem ter as mesmas roles, adicione-os aqui, ex:
                        // .requestMatchers("/criarFichaTecnica").hasAnyAuthority("NUTRICIONISTA", "ADMIN")


                        // 3. ROTAS PROTEGIDAS POR AUTENTICAÇÃO (apenas precisa estar logado, qualquer role)
                        // O dashboard é um exemplo típico de página que qualquer usuário logado deve ver
                        .requestMatchers("/dashboard").authenticated() // Protege explicitamente o dashboard

                        // 4. CATCH-ALL: Qualquer outra requisição que não foi explicitamente permitida acima, DEVE ser autenticada.
                        // Esta é a regra de "default deny" (negar por padrão).
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login") // O GET para exibir o formulário de login
                        .loginProcessingUrl("/login") // O POST para processar o login
                        .failureHandler((request, response, exception) -> {
                            // Handler para erros de login
                            if (exception.getClass().getSimpleName().equals("DisabledException")) {
                                response.sendRedirect("/login?disabled");
                            } else {
                                response.sendRedirect("/login?error");
                            }
                        })
                        .defaultSuccessUrl("/dashboard", true) // Redireciona após login bem-sucedido
                        .permitAll() // Permite acesso a todo o fluxo de login (GET e POST)
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST")) // Mude para POST, é mais seguro
                        // OU use .logoutUrl("/logout") se não quiser um matcher específico e quer o padrão POST
                        .logoutSuccessUrl("/login?logout") // Redireciona para /login com um parâmetro de sucesso
                        .invalidateHttpSession(true) // Garante que a sessão seja invalidada
                        .deleteCookies("JSESSIONID") // Garante que o cookie de sessão seja removido
                        .permitAll() // Permite que a URL de logout seja acessada
                )
                .csrf(csrf -> csrf.disable()) // Desabilitado para dev, ATIVAR em produção!
                .cors(cors -> cors.disable()); // Desabilitado para dev, CONFIGURAR em produção!

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