package com.example.toyplatform_swp_project.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Column(nullable = false)
    private String name;


    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    @OneToMany(mappedBy = "category")
    private List<Toy> toys;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
