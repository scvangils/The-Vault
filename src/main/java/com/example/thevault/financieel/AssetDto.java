// Created by carme
// Creation date 08/12/2021

package com.example.thevault.financieel;

import com.example.thevault.handelingen.CryptoWaarde;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Author: Carmen
 * Beschrijving: Hier wordt een datatransfer object van Asset aangemaakt
 */

public class AssetDto {
    @JsonIgnore
    private final Logger logger = LoggerFactory.getLogger(AssetDto.class);

    private String name;
    private double price;
    private double aantal;

    /**
     * No-args constructor voor AssetDto
     */
    public AssetDto() {
        super();
        logger.info("New AssetDto, no args constructor");
    }

    /**
     * All-args constructor voor AssetDto waarin zowel de
     * @param asset
     * @param cryptoWaarde
     */
    public AssetDto(Asset asset, CryptoWaarde cryptoWaarde){
        this.name = asset.getCryptomunt().getName();
        this.price = cryptoWaarde.getWaarde();
        this.aantal = asset.getAantal();
        logger.info("AssetDto: " + this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAantal() {
        return aantal;
    }

    public void setAantal(double aantal) {
        this.aantal = aantal;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "AssetDto{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", aantal=" + aantal +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssetDto assetDto = (AssetDto) o;
        return Double.compare(assetDto.price, price) == 0 && Double.compare(assetDto.aantal, aantal) == 0 && name.equals(assetDto.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price, aantal);
    }
}
