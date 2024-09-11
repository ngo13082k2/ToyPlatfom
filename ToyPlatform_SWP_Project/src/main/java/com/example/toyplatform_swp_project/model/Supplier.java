package com.example.toyplatform_swp_project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor

@Builder
@Entity
@Table(name = "supplier")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplier_id")
    private Long supplierId;

    @Column(name = "image_shop")
    private String imageShop;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "background_image")
    private String backgroundImage;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;



    @ManyToMany
    @JoinTable(
            name = "supplier_toy",
            joinColumns = @JoinColumn(name = "supplier_id"),
            inverseJoinColumns = @JoinColumn(name = "toy_id")
    )
    private Set<Toy> toys = new HashSet<>();


    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getImageShop() {
        return imageShop;
    }

    public void setImageShop(String imageShop) {
        this.imageShop = imageShop;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public Set<Toy> getToys() {
        return toys;
    }

    public void setToys(Set<Toy> toys) {
        this.toys = toys;
    }

}

