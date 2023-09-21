package com.crawlerproject.CrawlingMobileBGWithSpring.Crawler.Utilities.model;

import jakarta.persistence.*;

@Entity
@Table(name = "car_ads")
public class CarAd {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String make;
    private String model;
    private String price;

    public CarAd(Long id, String make, String model, String price, boolean newFlag) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.price = price;
        this.newFlag = newFlag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean isNewFlag() {
        return newFlag;
    }

    public void setNewFlag(boolean newFlag) {
        this.newFlag = newFlag;
    }

    private boolean newFlag;

}
