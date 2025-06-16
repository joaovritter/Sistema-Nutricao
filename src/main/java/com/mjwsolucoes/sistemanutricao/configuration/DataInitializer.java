package com.mjwsolucoes.sistemanutricao.configuration;

import com.mjwsolucoes.sistemanutricao.model.User;
import com.mjwsolucoes.sistemanutricao.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static com.mjwsolucoes.sistemanutricao.model.Role.*;

// A anotação @Component indica que esta classe é um componente gerenciado pelo Spring.
@Component
public class DataInitializer implements CommandLineRunner {

    // Repositório para interagir com a entidade Nutricionista no banco de dados.
    private final UserRepository userRepository;

    // Encoder para criptografar as senhas dos nutricionistas.
    private final PasswordEncoder passwordEncoder;

    // Construtor para injetar as dependências necessárias.
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Método executado automaticamente ao iniciar a aplicação.
    @Override
    public void run (String... args) {
        // Verifica se o nutricionista "admin" já existe no banco de dados.
        if (userRepository.findByUsername("admin").isEmpty()) {
            // Cria um novo nutricionista com a role ADMIN.
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456")); // Criptografa a senha.
            admin.setRole(ADMIN);
            userRepository.save(admin); // Salva o nutricionista no banco de dados.
            System.out.println("Admin Criado.");
        }

        // Verifica se o nutricionista "user" já existe no banco de dados.
        if (userRepository.findByUsername("user").isEmpty()) {
            // Cria um novo nutricionista com a role USER.
            User nutricionista = new User();
            nutricionista.setUsername("nutricionista");
            nutricionista.setPassword(passwordEncoder.encode("nutricionista")); // Criptografa a senha.
            nutricionista.setRole(NUTRICIONISTA);
            userRepository.save(nutricionista); // Salva o nutricionista no banco de dados.
            System.out.println("Nutricionista Criado.");
        }


    }
}
