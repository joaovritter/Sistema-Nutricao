package com.mjwsolucoes.sistemanutricao.configuration;

import com.mjwsolucoes.sistemanutricao.model.AlvoAtividade;
import com.mjwsolucoes.sistemanutricao.model.TipoAtividade;
import com.mjwsolucoes.sistemanutricao.service.AtividadeService;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

/**
 * Anota entradas e saídas no histórico.
 *
 * Escuta os eventos publicados pelo Spring Security em vez de interceptar a
 * rota /login, assim qualquer caminho de autenticação passa por aqui.
 */
@Component
public class OuvinteAutenticacao {

    private final AtividadeService atividadeService;

    public OuvinteAutenticacao(AtividadeService atividadeService) {
        this.atividadeService = atividadeService;
    }

    @EventListener
    public void aoEntrar(AuthenticationSuccessEvent evento) {
        String usuario = evento.getAuthentication().getName();
        atividadeService.registrarComo(usuario, TipoAtividade.LOGIN, AlvoAtividade.SESSAO,
                "Entrou no sistema", null);
    }

    @EventListener
    public void aoFalhar(AbstractAuthenticationFailureEvent evento) {
        String usuario = evento.getAuthentication() == null
                ? "desconhecido" : String.valueOf(evento.getAuthentication().getName());
        String motivo = evento.getException() == null
                ? "credenciais inválidas"
                : traduzirFalha(evento.getException().getClass().getSimpleName());
        atividadeService.registrarComo(usuario, TipoAtividade.LOGIN_FALHOU, AlvoAtividade.SESSAO,
                "Tentativa de login sem sucesso (" + motivo + ")", null);
    }

    private String traduzirFalha(String excecao) {
        return switch (excecao) {
            case "DisabledException" -> "conta ainda não aprovada";
            case "BadCredentialsException" -> "usuário ou senha incorretos";
            case "LockedException" -> "conta bloqueada";
            default -> "credenciais inválidas";
        };
    }
}
