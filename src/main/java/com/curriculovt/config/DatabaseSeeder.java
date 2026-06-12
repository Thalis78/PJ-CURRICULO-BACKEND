package com.curriculovt.config;

import com.curriculovt.models.User;
import com.curriculovt.models.UserRole;
import com.curriculovt.services.UserService;
import com.curriculovt.repositorys.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {

            User admin1 = new User();
            admin1.setNome("Thalisson Moura");
            admin1.setEmail("thalissonmoura138@gmail.com");
            admin1.setPassword("Gui7lima");
            admin1.setRole(UserRole.SUPER_ADMIN);

            User admin2 = new User();
            admin2.setNome("Vitoria Moura");
            admin2.setEmail("vitoriabf2006@gmail.com");
            admin2.setPassword("Vitoriabf02");
            admin2.setRole(UserRole.SUPER_ADMIN);

            userService.saveUser(admin1);
            userService.saveUser(admin2);

            userRepository.findAll().forEach(u -> {
                if (u.getRole() == UserRole.SUPER_ADMIN) {
                    userRepository.save(u);
                }
            });

            System.out.println(">>> Banco virgem : Thalisson e Vitoria criados como SUPER_ADMIN!");
        }
    }
}