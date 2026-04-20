package com.manual.manual.model.vo.artisancenter;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class ArtisanCenterOrderLogisticsVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String companyName;

    private String trackingNo;

    private String senderName;

    private String senderPhone;

    private String receiverName;

    private String receiverPhone;

    private String logisticsRemark;

    private String shipTime;

    private String signTime;

    private Integer status;
}
