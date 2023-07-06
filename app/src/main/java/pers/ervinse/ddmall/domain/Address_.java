package pers.ervinse.ddmall.domain;

import java.io.Serializable;

import lombok.Data;

@Data
public class Address_ implements Serializable {
    private String CountryforAddress;
    private String ProvinceforAddress;
    private String TownforAddress;
    private String DistrictforAddress;
    private String StreetforAddress;
    private String DetailAddress;
    private String ReceiveName;
    private String ReceiveTel;

    public Address_(String CountryforAddress, String ProvinceforAddress, String TownforAddress, String district, String StreetforAddress, String DetailAddress, String ReceiveName, String ReceiveTel) {
        this.CountryforAddress = CountryforAddress;
        this.ProvinceforAddress = ProvinceforAddress;
        this.TownforAddress = TownforAddress;
        this.DistrictforAddress = district;
        this.StreetforAddress = StreetforAddress;
        this.DetailAddress = DetailAddress;
        this.ReceiveName = ReceiveName;
        this.ReceiveTel = ReceiveTel;
    }
}