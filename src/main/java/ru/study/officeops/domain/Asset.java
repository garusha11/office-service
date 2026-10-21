package ru.study.officeops.domain;

import jakarta.persistence.*;

@Entity
public class Asset {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true) private String inventoryNumber;
    @Column(nullable = false) private String name;
    @Column(nullable = false) private String location;
    protected Asset() { }
    public Asset(String inventoryNumber, String name, String location) {
        this.inventoryNumber = inventoryNumber; this.name = name; this.location = location;
    }
    public Long getId() { return id; }
    public String getInventoryNumber() { return inventoryNumber; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public void update(String name, String location) { this.name = name; this.location = location; }
}
