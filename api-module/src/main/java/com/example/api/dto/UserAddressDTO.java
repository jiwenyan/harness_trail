package com.example.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 用户地址数据传输对象
 */
@NoArgsConstructor
@AllArgsConstructor
public class UserAddressDTO {

    private Long id;

    private Long userId;

    @NotBlank(message = "联系人姓名不能为空")
    @Size(max = 50, message = "联系人姓名不能超过50个字符")
    private String contactName;

    @NotBlank(message = "联系人电话不能为空")
    @Size(max = 20, message = "联系人电话不能超过20个字符")
    private String contactPhone;

    @NotBlank(message = "街道地址不能为空")
    @Size(max = 200, message = "街道地址不能超过200个字符")
    private String street;

    @NotBlank(message = "城市不能为空")
    @Size(max = 50, message = "城市不能超过50个字符")
    private String city;

    @NotBlank(message = "州/省不能为空")
    @Size(max = 50, message = "州/省不能超过50个字符")
    private String state;

    @NotBlank(message = "邮政编码不能为空")
    @Size(max = 20, message = "邮政编码不能超过20个字符")
    private String zipCode;

    private Boolean isDefault;

    @Size(max = 30, message = "标签不能超过30个字符")
    private String label;

    // Manual getters and setters for Lombok compatibility
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
