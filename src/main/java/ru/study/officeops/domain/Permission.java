package ru.study.officeops.domain;

public enum Permission {
    ASSET_READ("Просмотр оборудования"), ASSET_WRITE("Изменение оборудования"),
    TICKET_READ("Просмотр заявок"), TICKET_CREATE("Создание заявок"),
    TICKET_ASSIGN("Назначение исполнителя"), TICKET_PROGRESS("Работа по заявке"),
    TICKET_CLOSE("Закрытие заявки"), ACCESS_MANAGE("Настройка доступа"),
    AUDIT_READ("Просмотр журнала");

    private final String title;
    Permission(String title) { this.title = title; }
    public String getTitle() { return title; }
}
