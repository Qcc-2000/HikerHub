package edu.northeastern.hikerhub.hiker.fragment.home;

public enum Difficulty {
    EASY("Easy"),
    MODERATE("Moderate"),
    HARD("Hard");
    private final String name;
    private Difficulty(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
