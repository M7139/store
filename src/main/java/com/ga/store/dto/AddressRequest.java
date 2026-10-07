package com.ga.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AddressRequest {

    @Schema(
            description = "House or building number",
            example = "123"
    )
    @NotBlank(message = "House is required")
    @Size(max = 20, message = "House must be 20 characters or less")
    private String house;

    @Schema(
            description = "Road number",
            example = "456"
    )
    @NotBlank(message = "Road is required")
    @Size(max = 20, message = "Road must be 20 characters or less")
    private String road;

    @Schema(
            description = "Block number",
            example = "789"
    )
    @NotBlank(message = "Block is required")
    @Size(max = 20, message = "Block must be 20 characters or less")
    private String block;

    @Schema(
            description = "Area or neighborhood",
            example = "Manama"
    )
    @Size(max = 100, message = "Area must be 100 characters or less")
    private String area;

    @Schema(
            description = "Customer phone number",
            example = "+97333123456"
    )
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