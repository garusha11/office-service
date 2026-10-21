package ru.study.officeops.domain;

public enum TicketStatus {
    NEW("Новая"), ASSIGNED("Назначена"), IN_PROGRESS("В работе"), RESOLVED("Решена"), CLOSED("Закрыта");
    private final String title;
    TicketStatus(String title) { this.title = title; }
    public String getTitle() { return title; }
}
