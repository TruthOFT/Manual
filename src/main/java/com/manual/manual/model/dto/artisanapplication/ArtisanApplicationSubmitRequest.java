package com.manual.manual.model.dto.artisanapplication;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class ArtisanApplicationSubmitRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String artisanName;

    private String shopName;

    private String artisanAvatar;

    private String coverUrl;

    private String artisanStory;

    private String craftCategory;

    private String originPlace;

    private Integer experienceYears;

    private Integer supportCustom;

    private String contactPhone;

    private String qualificationDesc;

    private List<String> qualificationImages = new ArrayList<>();
}