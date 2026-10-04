package com.ga.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AddressRequest {

    @NotBlank(message = "House is required")
    @Size(max = 20, message = "House must be 20 characters or less")
    private String house;

    @NotBlank(message = "Road is required")
    @Size(max = 20, message = "Road must be 20 characters or less")
    private String road;

    @NotBlank(message = "Block is required")
    @Size(max = 20, message = "Block must be 20 characters or less")
    private String block;

    @Size(max = 100, message = "Area must be 100 characters or less")
    private String area;

    @NotBlank(message = "Phone number is required")
    @Size(max = 20, message = "Phone number must be 20 characters or less")
    private String phoneNumber;

    public AddressRequest() {
    }

    public AddressRequest(
            String house,
            String road,
            String block,
            String area,
            String phoneNumber) {

        this.house = house;
        this.road = road;
        this.block = block;
        this.area = area;
        this.phoneNumber = phoneNumber;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}