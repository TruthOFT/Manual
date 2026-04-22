package com.manual.manual.model.dto.order;

import lombok.Data;

@Data
public class UserAddressSaveRequest {

    private String receiverName;

    private String receiverPhone;

    private String province;

    private String city;

    private String district;

    private String detailAddress;

    private String postalCode;

    private String tagName;

    private Integer isDefault;
}
