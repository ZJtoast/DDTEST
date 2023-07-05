package pers.ervinse.ddmall.domain;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Medicine implements Serializable {

    public Boolean isSelected = false;
    private String commodityName, commodityDesc, merchantLocation, merchantID;
    private Integer commodityID;

    private Integer commodityPurchaseNumber;//commodityID也作为图片索引
    private Integer commditySales;
    private Integer commodityPrice;

    @Override
    public String toString() {
        return "Medicine{" +
                "commodityName='" + commodityName + '\'' +
                ", commodityDesc='" + commodityDesc + '\'' +
                ", merchantLocation='" + merchantLocation + '\'' +
                ", commodityID='" + commodityID + '\'' +
                ", commditySales=" + commditySales +
                ", commodityPrice=" + commodityPrice +
                ", isSelected=" + isSelected +
                '}';
    }
}
