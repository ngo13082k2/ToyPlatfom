package com.example.toyplatform_swp_project.dto;


public class SupplierDto {
    private Long supplierId;
    private String imageShop;
    private String name;
    private String description;
    private String backgroundImage;


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }


    private Long userId;

//    public Set<Long> getToyIds() {
//        return toyIds;
//    }
//
//    public void setToyIds(Set<Long> toyIds) {
//        this.toyIds = toyIds;
//    }
//
//    private Set<Long> toyIds;





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


}
