package com.mjwsolucoes.sistemanutricao.service;

import com.mjwsolucoes.sistemanutricao.dto.UserAdminDTO;
import com.mjwsolucoes.sistemanutricao.model.Role;
import com.mjwsolucoes.sistemanutricao.model.User;
import com.mjwsolucoes.sistemanutricao.repository.ReceitaRepository;
import com.mjwsolucoes.sistemanutricao.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

/** Regras do painel administrativo de usuários. */
@Service
public class AdminUserService {

    private final UserRepository userRepository;
    private final ReceitaRepository receitaRepository;

    public AdminUserService(UserRepository userRepository, ReceitaRepository receitaRepository) {
        this.userRepository = userRepository;
        this.receitaRepository = receitaRepository;
    }

    /** Solicitações de acesso aguardando um cargo. */
    public List<UserAdminDTO> listarPendentes() {
        return userRepository.findByRoleOrderByCriadoEmAsc(Role.PENDENTE).stream()
                .map(this::converter)
                .collect(Collectors.toList());
    }

    /** Contas já avaliadas (com cargo definido), mais recentes primeiro. */
    public List<UserAdminDTO> listarAvaliados() {
        return userRepository.findAllByOrderByCriadoEmDesc().stream()
                .filter(u -> u.getRole() != Role.PENDENTE)
                .map(this::converter)
                .collect(Collectors.toList());
    }

    /**
     * Define o cargo de um usuário. Atribuir um cargo a uma solicitação pendente
     * também ativa o acesso — é o gesto de aprovação do admin.
     */
    @Transactional
    public void definirCargo(Long id, Role novoCargo, String usernameAdmin) {
        if (novoCargo == Role.PENDENTE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PENDENTE não é um cargo atribuível.");
        }
        User user = buscar(id);
        boolean eraPendente = user.getRole() == Role.PENDENTE;

        if (user.getUsername().equals(usernameAdmin) && novoCargo != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Você não pode remover o seu próprio acesso de administrador.");
        }
        if (user.getRole() == Role.ADMIN && novoCargo != Role.ADMIN && ultimoAdminAtivo(user)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "É preciso manter ao menos um administrador ativo no sistema.");
        }

        user.setRole(novoCargo);
        if (eraPendente) {
            user.setAtivo(true);
        }
        userRepository.save(user);
    }

    /** Liga/desliga o acesso sem mexer no cargo. */
    @Transactional
    public void alterarAtivacao(Long id, boolean ativo, String usernameAdmin) {
        User user = buscar(id);

        if (!ativo) {
            if (user.getUsername().equals(usernameAdmin)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Você não pode desativar a sua própria conta.");
            }
            if (user.getRole() == Role.ADMIN && ultimoAdminAtivo(user)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "É preciso manter ao menos um administrador ativo no sistema.");
            }
        }
        if (ativo && user.getRole() == Role.PENDENTE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Defina um cargo antes de liberar o acesso desta conta.");
        }

        user.setAtivo(ativo);
        userRepository.save(user);
    }

    /** Recusa uma solicitação de acesso removendo o cadastro. */
    @Transactional
    public void recusarSolicitacao(Long id) {
        User user = buscar(id);
        if (user.getRole() != Role.PENDENTE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Só é possível recusar solicitações ainda pendentes.");
        }
        userRepository.delete(user);
    }

    public long contarPendentes() {
        return userRepository.countByRole(Role.PENDENTE);
    }

    private boolean ultimoAdminAtivo(User candidato) {
        return userRepository.findAll().stream()
                .noneMatch(u -> u.getRole() == Role.ADMIN
                        && u.isAtivo()
                        && !u.getId().equals(candidato.getId()));
    }

    private User buscar(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }

    private UserAdminDTO converter(User user) {
        UserAdminDTO dto = new UserAdminDTO();
        dto.setId(user.getId());
        dto.setNome(user.getNomeExibicao());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        dto.setRoleDescricao(user.getRole().getDescricao());
        dto.setAtivo(user.isAtivo());
        dto.setCriadoEm(user.getCriadoEm());
        dto.setTotalFichas(receitaRepository.countByNutricionistaId(user.getId()));
        return dto;
    }
}
