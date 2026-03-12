package org.example.gui;

import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JDialog {

    private JTextField loginField;
    private JPasswordField passField;
    private JPasswordField confirmField;

    public RegisterFrame(JFrame parent) {
        super(parent, "Реєстрація", true);
        setSize(420, 320);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(14, 14, 14, 14);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Логін
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        content.add(new JLabel("Логін:"), gbc);

        gbc.gridx = 1;
        loginField = new JTextField(24);           // збільшено до 24 символів
        content.add(loginField, gbc);

        // Пароль
        gbc.gridx = 0; gbc.gridy = 1;
        content.add(new JLabel("Пароль:"), gbc);

        gbc.gridx = 1;
        passField = new JPasswordField(24);
        content.add(passField, gbc);

        // Повтор пароля
        gbc.gridx = 0; gbc.gridy = 2;
        content.add(new JLabel("Повтор пароля:"), gbc);

        gbc.gridx = 1;
        confirmField = new JPasswordField(24);
        content.add(confirmField, gbc);

        // Кнопка
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 0, 0, 0);

        JButton btnRegister = new JButton("Зареєструватися");
        btnRegister.setPreferredSize(new Dimension(240, 42));  // трохи більша кнопка
        btnRegister.addActionListener(e -> tryRegister());
        content.add(btnRegister, gbc);

        add(content);
        setVisible(true);
    }

    private void tryRegister() {
        String login = loginField.getText().trim();
        String pass = new String(passField.getPassword()).trim();
        String confirm = new String(confirmField.getPassword()).trim();

        if (login.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Заповніть усі поля", "Помилка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!pass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Паролі не співпадають", "Помилка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (login.length() < 3) {
            JOptionPane.showMessageDialog(this, "Логін має бути не коротшим за 3 символи", "Помилка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (pass.length() < 4) {
            JOptionPane.showMessageDialog(this, "Пароль має бути не коротшим за 4 символи", "Помилка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Тут має бути реальна перевірка унікальності логіну в базі
        // Поки що імітуємо успіх

        JOptionPane.showMessageDialog(this,
                "Реєстрація успішна!\nЛогін: " + login + "\n\nТепер увійдіть.",
                "Успіх",
                JOptionPane.INFORMATION_MESSAGE);

        dispose();
    }
}