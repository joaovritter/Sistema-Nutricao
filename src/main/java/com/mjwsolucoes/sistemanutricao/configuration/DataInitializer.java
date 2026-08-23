package com.mjwsolucoes.sistemanutricao.configuration;

import com.mjwsolucoes.sistemanutricao.model.User;
import com.mjwsolucoes.sistemanutricao.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.mjwsolucoes.sistemanutricao.model.Role.*;

/**
 * Prepara a base na subida: migra cadastros antigos para o novo modelo de
 * cargos e garante que exista um administrador para aprovar os demais.
 */
@Component
@Order(1)
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    public DataInitializer(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void run(String... args) {
        migrarCadastrosAntigos();
        criarAdminPadrao();
    }

    /**
     * Contas criadas antes do controle de aprovação não têm 'criado_em'. Elas
     * são consideradas já aprovadas — do contrário o admin ficaria trancado do
     * lado de fora depois da atualização.
     */
    private void migrarCadastrosAntigos() {
        alargarColunaDeCargo();

        int legados = jdbcTemplate.update("UPDATE `user` SET role = 'ESTUDANTE' WHERE role = 'USER'");
        if (legados > 0) {
            System.out.println("Migração: " + legados + " conta(s) com cargo USER viraram ESTUDANTE.");
        }

        int reativados = jdbcTemplate.update(
                "UPDATE `user` SET ativo = 1, criado_em = ? WHERE criado_em IS NULL",
                LocalDateTime.now());
        if (reativados > 0) {
            System.out.println("Migração: " + reativados + " conta(s) anteriores marcadas como ativas.");
        }
    }

    /**
     * O schema antigo declarava 'role' como ENUM('ADMIN','NUTRICIONISTA','USER').
     * O Hibernate não altera enums existentes, então os cargos novos seriam
     * recusados pelo banco. Converte para varchar uma única vez.
     */
    private void alargarColunaDeCargo() {
        String tipo = jdbcTemplate.query(
                "SELECT DATA_TYPE FROM information_schema.COLUMNS "
                        + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user' AND COLUMN_NAME = 'role'",
                rs -> rs.next() ? rs.getString(1) : null);

        if ("enum".equalsIgnoreCase(tipo)) {
            jdbcTemplate.execute("ALTER TABLE `user` MODIFY COLUMN role VARCHAR(20) NOT NULL");
            System.out.println("Migração: coluna 'role' convertida de ENUM para VARCHAR(20).");
        }
    }

    private void criarAdminPadrao() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setNome("Administrador");
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setRole(ADMIN);
            admin.setAtivo(true);
            admin.setCriadoEm(LocalDateTime.now());
            userRepository.save(admin);
            System.out.println("Admin criado.");
        }

        if (userRepository.findByUsername("nutricionista").isEmpty()) {
            User nutricionista = new User();
            nutricionista.setNome("Nutricionista");
            nutricionista.setUsername("nutricionista");
            nutricionista.setPassword(passwordEncoder.encode("nutricionista"));
            nutricionista.setRole(NUTRICIONISTA);
            nutricionista.setAtivo(true);
            nutricionista.setCriadoEm(LocalDateTime.now());
            userRepository.save(nutricionista);
            System.out.println("Nutricionista criado.");
        }
    }
}
