package com.mjwsolucoes.sistemanutricao.configuration;

import com.mjwsolucoes.sistemanutricao.model.Nutricionista;
import com.mjwsolucoes.sistemanutricao.repository.NutricionistaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// A anotação @Component indica que esta classe é um componente gerenciado pelo Spring.
@Component
public class DataInitializer implements CommandLineRunner {

    // Repositório para interagir com a entidade Nutricionista no banco de dados.
    private final NutricionistaRepository nutricionistaRepository;

    // Encoder para criptografar as senhas dos nutricionistas.
    private final PasswordEncoder passwordEncoder;

    // Construtor para injetar as dependências necessárias.
    public DataInitializer(NutricionistaRepository nutricionistaRepository, PasswordEncoder passwordEncoder) {
        this.nutricionistaRepository = nutricionistaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Método executado automaticamente ao iniciar a aplicação.
    @Override
    public void run (String... args) {
        // Verifica se o nutricionista "admin" já existe no banco de dados.
        if (nutricionistaRepository.findByUsername("admin").isEmpty()) {
            // Cria um novo nutricionista com a role ADMIN.
            Nutricionista admin = new Nutricionista();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456")); // Criptografa a senha.
            admin.setRole("ADMIN");
            nutricionistaRepository.save(admin); // Salva o nutricionista no banco de dados.
            System.out.println("Nutricionista admin criado.");
        }

        // Verifica se o nutricionista "user" já existe no banco de dados.
        if (nutricionistaRepository.findByUsername("user").isEmpty()) {
            // Cria um novo nutricionista com a role USER.
            Nutricionista nutricionista = new Nutricionista();
            nutricionista.setUsername("user");
            nutricionista.setPassword(passwordEncoder.encode("123456")); // Criptografa a senha.
            nutricionista.setRole("USER");
            nutricionistaRepository.save(nutricionista); // Salva o nutricionista no banco de dados.
            System.out.println("Nutricionista user criado.");
        }
    }
}
