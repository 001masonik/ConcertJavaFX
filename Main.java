package org.example;

import org.example.dao.UserDAO;
import org.example.entity.User;
import org.example.gui.LoginFrame;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        createAdminIfNotExists();
        SwingUtilities.invokeLater(LoginFrame::new);
    }

    private static void createAdminIfNotExists() {
        UserDAO dao = new UserDAO();
        final String LOGIN = "admin";
        final String PASS = "admin123";

        if (dao.findByLogin(LOGIN) == null) {
            User admin = new User();
            admin.setLogin(LOGIN);
            admin.setPassword(PASS);
            admin.setRole("Admin");
            admin.setVoted(false);

            try {
                dao.save(admin);
                System.out.println("Створено адміністратора: " + LOGIN + " / " + PASS);
            } catch (Exception e) {
                System.err.println("Помилка створення адміна: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Адмін уже існує");
        }
    }
}