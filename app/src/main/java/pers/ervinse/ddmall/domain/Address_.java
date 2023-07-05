package pers.ervinse.ddmall.domain;

import lombok.Data;

@Data
public class Address_ {
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
//package pers.ervinse.ddmall.domain;
//
//public class Address_ {
//    /**
//     * 国家
//     */
//    private String countryforAddress;
//    /**
//     * 详细地址
//     */
//    private String detailAddress;
//    /**
//     * 县区
//     */
//    private String districtforAddress;
//    /**
//     * 省份
//     */
//    private String provinceforAddress;
//    /**
//     * 收货人姓名
//     */
//    private String receiveName;
//    /**
//     * 手机号
//     */
//    private String receiveTel;
//    /**
//     * 街道
//     */
//    private String streetforAddress;
//    /**
//     * 市
//     */
//    private String townforAddress;
//
//    public String getCountryforAddress() { return countryforAddress; }
//    public void setCountryforAddress(String value) { this.countryforAddress = value; }
//
//    public String getDetailAddress() { return detailAddress; }
//    public void setDetailAddress(String value) { this.detailAddress = value; }
//
//    public String getDistrictforAddress() { return districtforAddress; }
//    public void setDistrictforAddress(String value) { this.districtforAddress = value; }
//
//    public String getProvinceforAddress() { return provinceforAddress; }
//    public void setProvinceforAddress(String value) { this.provinceforAddress = value; }
//
//    public String getReceiveName() { return receiveName; }
//    public void setReceiveName(String value) { this.receiveName = value; }
//
//    public String getReceiveTel() { return receiveTel; }
//    public void setReceiveTel(String value) { this.receiveTel = value; }
//
//    public String getStreetforAddress() { return streetforAddress; }
//    public void setStreetforAddress(String value) { this.streetforAddress = value; }
//
//    public String getTownforAddress() { return townforAddress; }
//    public void setTownforAddress(String value) { this.townforAddress = value; }
//}
