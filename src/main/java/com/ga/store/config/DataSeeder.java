package com.ga.store.config;

import com.ga.store.enums.UserRole;
import com.ga.store.model.Category;
import com.ga.store.model.Product;
import com.ga.store.model.User;
import com.ga.store.repository.CategoryRepository;
import com.ga.store.repository.ProductRepository;
import com.ga.store.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Profile("dev")
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        seedUsers();
        seedCategoriesAndProducts();
    }

    private void seedUsers() {

        if (!userRepository.existsByEmail(
                "admin@sedar.com")) {

            User admin = new User(
                    "Sedar",
                    "Admin",
                    "admin@sedar.com",
                    passwordEncoder.encode(
                            "Admin123!"
                    )
            );

            admin.setRole(UserRole.ADMIN);
            admin.setVerified(true);

            userRepository.save(admin);
        }

        if (!userRepository.existsByEmail(
                "customer@sedar.com")) {

            User customer = new User(
                    "Demo",
                    "Customer",
                    "customer@sedar.com",
                    passwordEncoder.encode(
                            "Customer123!"
                    )
            );

            customer.setVerified(true);

            userRepository.save(customer);
        }
    }

    private void seedCategoriesAndProducts() {

        Category barSoap =
                categoryRepository
                        .findByNameIgnoreCase(
                                "Bar Soap"
                        )
                        .orElseGet(() ->
                                categoryRepository.save(
                                        new Category(
                                                "Bar Soap",
                                                "Handmade bar soaps"
                                        )
                                ));

        Category liquidSoap =
                categoryRepository
                        .findByNameIgnoreCase(
                                "Liquid Soap"
                        )
                        .orElseGet(() ->
                                categoryRepository.save(
                                        new Category(
                                                "Liquid Soap",
                                                "Liquid soaps for daily use"
                                        )
                                ));

        Category giftSets =
                categoryRepository
                        .findByNameIgnoreCase(
                                "Gift Sets"
                        )
                        .orElseGet(() ->
                                categoryRepository.save(
                                        new Category(
                                                "Gift Sets",
                                                "Soap gift sets"
                                        )
                                ));

        createProductIfMissing(
                "Lavender Soap",
                "Lavender scented handmade soap",
                new BigDecimal("2.50"),
                20,
                barSoap
        );

        createProductIfMissing(
                "Rose Soap",
                "Rose scented handmade soap",
                new BigDecimal("2.50"),
                20,
                barSoap
        );

        createProductIfMissing(
                "Honey Liquid Soap",
                "Honey scented liquid soap",
                new BigDecimal("3.50"),
                15,
                liquidSoap
        );

        createProductIfMissing(
                "Sedar Gift Set",
                "A selection of Sedar soaps",
                new BigDecimal("8.00"),
                10,
                giftSets
        );
    }

    private void createProductIfMissing(
            String name,
            String description,
            BigDecimal price,
            int stockQuantity,
            Category category) {

        if (!productRepository
                .existsByNameIgnoreCase(name)) {

            Product product =
                    new Product(
                            name,
                            description,
                            price,
                            stockQuantity,
                            category
                    );

            productRepository.save(product);
        }
    }
}