package pers.ervinse.ddmall.domain;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address_ implements Serializable {
    private Integer AddressID;
    private String CountryforAddress;
    private String DetailAddress;
    private String DistrictforAddress;
    private String ProvinceforAddress;
    private String ReceiveName;
    private String ReceiveTel;
    private String StreetforAddress;
    private String TownforAddress;
    private Integer UserID;
}