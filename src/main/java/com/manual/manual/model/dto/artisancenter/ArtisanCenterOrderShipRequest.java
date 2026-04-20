package com.manual.manual.model.dto.artisancenter;

import lombok.Data;

@Data
public class ArtisanCenterOrderShipRequest {

    private String companyName;

    private String trackingNo;

    private String senderName;

    private String senderPhone;

    private String logisticsRemark;
}
