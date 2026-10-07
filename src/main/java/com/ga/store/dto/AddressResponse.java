package com.ga.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class AddressResponse {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "123")
    private String house;

    @Schema(example = "456")
    private String road;

    @Schema(example = "789")
    private String block;

    @Schema(example = "Manama")
    private String area;

    @Schema(example = "+97333123456")
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