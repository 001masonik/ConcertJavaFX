package org.example.entity;

import javax.persistence.*;

@Entity
@Table(name = "config")
public class Config {

    @Id
    @Column(name = "id")
    private Long id = 1L;   // фіксований ідентифікатор, один запис на всю систему

    @Column(name = "max_votes_per_user", nullable = false)
    private int maxVotesPerUser = 3;   // максимальна кількість пісень, за які може проголосувати один слухач

    @Column(name = "voting_closed", nullable = false)
    private boolean votingClosed = false;  // чи закрите голосування

    // Конструктор за замовчуванням (потрібен для Hibernate)
    public Config() {
    }

    // Геттери та сеттери
    public Long getId() {
        return id;
    }

    // Setter для id робимо або protected, або взагалі без нього (рекомендовано)
    // Бо id не повинен змінюватись після створення
    protected void setId(Long id) {
        this.id = id;
    }

    public int getMaxVotesPerUser() {
        return maxVotesPerUser;
    }

    public void setMaxVotesPerUser(int maxVotesPerUser) {
        this.maxVotesPerUser = maxVotesPerUser;
    }

    public boolean isVotingClosed() {
        return votingClosed;
    }

    public void setVotingClosed(boolean votingClosed) {
        this.votingClosed = votingClosed;
    }

    // Допоміжний метод для зручності (опціонально)
    @Override
    public String toString() {
        return "Config{" +
                "id=" + id +
                ", maxVotesPerUser=" + maxVotesPerUser +
                ", votingClosed=" + votingClosed +
                '}';
    }
}