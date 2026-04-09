package com.appointmentsystem.ui;

import com.appointmentsystem.ai.ProductRepository;
import com.appointmentsystem.model.*;
import com.appointmentsystem.observer.EmailService;
import com.appointmentsystem.observer.NotificationManager;
import com.appointmentsystem.persistence.StorageManager;
import com.appointmentsystem.repository.InMemoryAppointmentRepository;
import com.appointmentsystem.repository.InMemoryUserRepository;
import com.appointmentsystem.service.AppointmentService;
import com.appointmentsystem.service.AuthService;
import com.appointmentsystem.strategy.BookingRuleStrategy;
import com.appointmentsystem.strategy.CapacityRule;
import com.appointmentsystem.strategy.DurationRule;
import com.appointmentsystem.strategy.TypeSpecificRule;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

public class MainApp {
	
    public static void main(String[] args) {
        StorageManager storageManager = new StorageManager();

        InMemoryUserRepository userRepo = new InMemoryUserRepository();
        InMemoryAppointmentRepository appointmentRepo = new InMemoryAppointmentRepository();
        ProductRepository productRepository = new ProductRepository();

        List<User> loadedUsers = storageManager.loadData("users.json", new com.google.gson.reflect.TypeToken<List<User>>() {});
        List<Appointment> loadedAppointments = storageManager.loadData("appointments.json", new com.google.gson.reflect.TypeToken<List<Appointment>>() {});
        List<Product> loadedProducts = storageManager.loadData("products.json", new com.google.gson.reflect.TypeToken<List<Product>>() {});

        userRepo.loadAll(loadedUsers);
        appointmentRepo.loadAll(loadedAppointments);
        
        if (loadedUsers.isEmpty()) {
            populateBasicUsers(userRepo);
        }

        try {
            List<User> allUsers = userRepo.findAll();
            for (Product p : loadedProducts) {
                productRepository.addProduct(p);
                for (User u : allUsers) {
                    if (u instanceof Supplier && u.getId() == p.getSupplierId()) {
                        ((Supplier) u).addProduct(p);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error linking products to suppliers: " + e.getMessage());
        }

        NotificationManager notifier = new NotificationManager();
        notifier.attach(new EmailService());

        List<BookingRuleStrategy> rules = Arrays.asList(
            new DurationRule(120),
            new CapacityRule(10),
            new TypeSpecificRule()
        );

        AuthService authService = new AuthService(userRepo);
        AppointmentService appointmentService = new AppointmentService(appointmentRepo, notifier, rules);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Saving data before exit...");
            storageManager.saveData("users.json", userRepo.findAll());
            storageManager.saveData("appointments.json", appointmentRepo.findAll());
            storageManager.saveData("products.json", productRepository.getAllProducts());
            System.out.println("Data saved successfully.");
        }));

        SwingUtilities.invokeLater(() -> {
            new LoginFrame(authService, appointmentService, productRepository).setVisible(true);
        });
    }
    
    private static void populateBasicUsers(InMemoryUserRepository userRepo) {
        userRepo.save(new Admin(1, "admin", "123", "nosodatabase@gmail.com"));
        userRepo.save(new Supplier(2, "sup1", "123", "nosodatabase@gmail.com", "AutoMax Cars"));
        userRepo.save(new Supplier(4, "sup2", "123", "nosodatabase@gmail.com", "AutoMax Cars"));
        userRepo.save(new Supplier(6, "sup3", "123", "nosodatabase@gmail.com", "HomeComfort Furniture"));
        userRepo.save(new Customer(3, "u1", "123", "nosodatabase@gmail.com", "0501111111"));
        userRepo.save(new Customer(5, "u2", "123", "nosodatabase@gmail.com", "0502222222"));
    }
}