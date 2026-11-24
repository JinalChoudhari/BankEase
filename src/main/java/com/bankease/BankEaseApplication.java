package com.bankease;

import com.bankease.entity.User;
import com.bankease.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class BankEaseApplication {
    public static void main(String[] args) {
        SpringApplication.run(BankEaseApplication.class, args);
    }

    @Bean
    CommandLineRunner createDefaultAdmin(UserRepository users, PasswordEncoder encoder) {
        return args -> {
            if (users.findByEmail("admin@bankease.com").isEmpty()) {
                User admin = new User();
                admin.setFullName("System Administrator");
                admin.setEmail("admin@bankease.com");
                admin.setPassword(encoder.encode("Admin@123"));
                admin.setRole("ROLE_ADMIN");
                admin.setEnabled(true);
                users.save(admin);
            }
        };
    }
}
