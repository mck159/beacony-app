package com.example.maciek.beacony.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by maciek on 2015-11-15.
 */
public class ContentDTO {
    @JsonProperty("img_url")
    String imgUrl;
    @JsonProperty("description")
    String description;
    @JsonProperty("name")
    String name;
    @JsonProperty("url")
    String url;

    public ContentDTO() {
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
