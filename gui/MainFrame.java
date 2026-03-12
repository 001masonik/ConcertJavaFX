package org.example.gui;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.example.dao.SongDAO;
import org.example.entity.Song;
import org.example.entity.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainFrame extends JFrame {

    private final User currentUser;
    private final boolean isAdmin;

    private JTable songTable;
    private DefaultTableModel tableModel;
    private JTextArea resultArea;

    private JButton btnAddSong, btnDeleteSong, btnVote, btnSetLimit, btnCloseVoting, btnGenerate;

    private int maxVotesPerUser = 3;
    private boolean votingClosed = false;
    private boolean currentUserVoted = false;

    private final SongDAO songDAO = new SongDAO();

    public MainFrame(User user) {
        this.currentUser = user;
        this.isAdmin = "Admin".equals(user.getRole());

        setTitle("Концерт на замовлення — " + user.getLogin());
        setSize(950, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Список пісень", createSongListPanel());

        if (!isAdmin) {
            tabs.addTab("Мій вибір", new JPanel()); // можна розширити пізніше
        }

        if (isAdmin) {
            tabs.addTab("Статистика та Звіт", createAdminPanel());
        }

        add(tabs);
        setVisible(true);

        refreshSongTable(); // одразу завантажуємо пісні з бази
    }

    private JPanel createSongListPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tableModel = new DefaultTableModel(
                new String[]{"ID", "Назва пісні", "Виконавець", "Тривалість (хв)", "Голосів"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        songTable = new JTable(tableModel);
        songTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        songTable.getColumnModel().getColumn(0).setMinWidth(0);
        songTable.getColumnModel().getColumn(0).setMaxWidth(0);

        JScrollPane scroll = new JScrollPane(songTable);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));

        btnAddSong = new JButton("Додати пісню");
        btnAddSong.setEnabled(isAdmin);
        btnAddSong.addActionListener(e -> addSong());
        controls.add(btnAddSong);

        btnDeleteSong = new JButton("Видалити пісню");
        btnDeleteSong.setEnabled(false);
        btnDeleteSong.addActionListener(e -> deleteSong());
        controls.add(btnDeleteSong);

        btnVote = new JButton("Проголосувати");
        btnVote.setEnabled(!isAdmin && !currentUserVoted && !votingClosed);
        btnVote.addActionListener(e -> vote());
        controls.add(btnVote);

        if (isAdmin) {
            btnSetLimit = new JButton("Встановити ліміт N");
            btnSetLimit.addActionListener(e -> setLimit());
            controls.add(btnSetLimit);

            btnCloseVoting = new JButton("Закрити голосування");
            btnCloseVoting.setEnabled(!votingClosed);
            btnCloseVoting.addActionListener(e -> closeVoting());
            controls.add(btnCloseVoting);

            btnGenerate = new JButton("Сформувати концерт");
            btnGenerate.addActionListener(e -> generateConcert());
            controls.add(btnGenerate);
        }

        panel.add(controls, BorderLayout.SOUTH);

        // Логіка ввімкнення/вимкнення кнопок
        songTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean selected = songTable.getSelectedRow() >= 0;
                btnDeleteSong.setEnabled(isAdmin && selected);
                btnVote.setEnabled(!isAdmin && !currentUserVoted && !votingClosed && selected);
            }
        });

        return panel;
    }

    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        panel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        return panel;
    }

    // ────────────────────────────────────────────────
    // Головна функція — оновлення таблиці з бази даних
    // ────────────────────────────────────────────────
    private void refreshSongTable() {
        tableModel.setRowCount(0);
        List<Song> songs = songDAO.findAll();  // всі пісні з БД

        for (Song s : songs) {
            tableModel.addRow(new Object[]{
                    s.getId(),
                    s.getName(),
                    s.getArtist(),
                    s.getDuration(),
                    s.getVoteCount()
            });
        }
    }

    private void addSong() {
        String name = JOptionPane.showInputDialog(this, "Назва пісні:");
        if (name == null || name.trim().isEmpty()) return;

        String artist = JOptionPane.showInputDialog(this, "Виконавець:");
        if (artist == null || artist.trim().isEmpty()) return;

        String durStr = JOptionPane.showInputDialog(this, "Тривалість (хв):");
        if (durStr == null) return;

        try {
            int duration = Integer.parseInt(durStr.trim());
            if (duration <= 0) throw new NumberFormatException();

            Song song = new Song();
            song.setName(name.trim());
            song.setArtist(artist.trim());
            song.setDuration(duration);
            song.setVoteCount(0);

            songDAO.save(song);          // ЗБЕРІГАЄМО В БАЗУ

            refreshSongTable();          // оновлюємо таблицю з бази

            JOptionPane.showMessageDialog(this, "Пісню додано!", "Успіх", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Тривалість — додатнє число", "Помилка", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Помилка збереження:\n" + ex.getMessage(), "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSong() {
        int row = songTable.getSelectedRow();
        if (row < 0) return;

        Long id = (Long) tableModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Видалити пісню?",
                "Підтвердження",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Song song = songDAO.findById(id);
            if (song != null) {
                songDAO.delete(song);
                refreshSongTable();
            }
        }
    }

    private void vote() {
        int[] rows = songTable.getSelectedRows();

        if (rows.length == 0) {
            JOptionPane.showMessageDialog(this, "Оберіть пісні", "Помилка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (rows.length > maxVotesPerUser) {
            JOptionPane.showMessageDialog(this,
                    "Максимум " + maxVotesPerUser + " пісень",
                    "Перевищено ліміт",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (int row : rows) {
            Long id = (Long) tableModel.getValueAt(row, 0);
            Song song = songDAO.findById(id);
            if (song != null) {
                song.incrementVoteCount();
                songDAO.save(song);
            }
        }

        currentUserVoted = true;
        btnVote.setEnabled(false);
        refreshSongTable();

        JOptionPane.showMessageDialog(this, "Голоси зараховано!", "Успіх", JOptionPane.INFORMATION_MESSAGE);
    }

    private void setLimit() {
        String input = JOptionPane.showInputDialog(this, "Ліміт голосів (N):", maxVotesPerUser);
        if (input == null) return;
        try {
            int n = Integer.parseInt(input.trim());
            if (n < 1) throw new NumberFormatException();
            maxVotesPerUser = n;
            JOptionPane.showMessageDialog(this, "Ліміт: " + n);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Введіть число", "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void closeVoting() {
        votingClosed = true;
        btnVote.setEnabled(false);
        btnCloseVoting.setEnabled(false);
        JOptionPane.showMessageDialog(this, "Голосування закрито");
    }

    private void generateConcert() {
        if (!votingClosed) {
            JOptionPane.showMessageDialog(this, "Спочатку закрийте голосування", "Помилка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String input = JOptionPane.showInputDialog(this, "Тривалість концерту (хв):", "90");
        if (input == null) return;

        try {
            int totalMin = Integer.parseInt(input.trim());

            List<Song> songs = songDAO.findAll();
            songs.sort(Comparator.comparingInt(Song::getVoteCount).reversed());

            List<Song> selected = new ArrayList<>();
            int sum = 0;

            for (Song s : songs) {
                if (sum + s.getDuration() <= totalMin) {
                    selected.add(s);
                    sum += s.getDuration();
                } else break;
            }

            StringBuilder sb = new StringBuilder("Програма концерту (" + sum + " хв):\n\n");
            int pos = 1;
            for (Song s : selected) {
                sb.append(String.format("%2d. %-35s %-25s %3d хв   (%d гол.)\n",
                        pos++, s.getName(), s.getArtist(), s.getDuration(), s.getVoteCount()));
            }

            resultArea.setText(sb.toString());

            // PDF
            Document doc = new Document();
            PdfWriter.getInstance(doc, new FileOutputStream("Програма_концерту.pdf"));
            doc.open();
            doc.add(new Paragraph("Програма концерту\n\n" + sb));
            doc.close();

            JOptionPane.showMessageDialog(this, "PDF збережено");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Помилка: " + ex.getMessage(), "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }
}