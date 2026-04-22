package com.manual.manual.model.vo.order;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class UserAddressVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

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
