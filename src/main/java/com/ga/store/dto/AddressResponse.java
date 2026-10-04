package com.ga.store.dto;

public class AddressResponse {

    private Long id;
    private String house;
    private String road;
    private String block;
    private String area;
    private String phoneNumber;

    public AddressResponse() {
    }

    public AddressResponse(
            Long id,
            String house,
            String road,
            String block,
            String area,
            String phoneNumber) {

        this.id = id;
        this.house = house;
        this.road = road;
        this.block = block;
        this.area = area;
        this.phoneNumber = phoneNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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