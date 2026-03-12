package org.example.gui;

import org.example.entity.User;
import org.example.gui.MainFrame;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField loginField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("Авторизація");
        setSize(320, 220);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Логін:"), gbc);

        gbc.gridx = 1;
        loginField = new JTextField(18);
        add(loginField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Пароль:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(18);
        add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel btnPanel = new JPanel();
        JButton loginBtn = new JButton("Увійти");
        loginBtn.addActionListener(e -> login());
        btnPanel.add(loginBtn);

        JButton regBtn = new JButton("Реєстрація");
        regBtn.addActionListener(e -> new RegisterFrame(this));
        btnPanel.add(regBtn);

        add(btnPanel, gbc);

        setVisible(true);
    }

    private void login() {
        String login = loginField.getText().trim();
        String pass = new String(passwordField.getPassword()).trim();

        if (login.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Заповніть логін та пароль", "Помилка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = new User(); // тимчасово — для тестування
        user.setLogin(login);

        // Спрощена перевірка ролі для тестування (потім заміни на реальну БД)
        if (login.toLowerCase().contains("admin")) {
            user.setRole("Admin");
        } else {
            user.setRole("User");
        }

        user.setVoted(false);

        dispose();
        new MainFrame(user);
    }
}