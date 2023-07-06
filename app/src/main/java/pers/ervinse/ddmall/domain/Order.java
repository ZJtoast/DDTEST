package pers.ervinse.ddmall.domain;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order implements Serializable {
    private Integer commodityID;
    private Integer commodityNum;
    private Integer logisticsID;
    private Integer orderFullAmount;
    private Integer orderID;
    private Integer orderPayState;
    private String orderTime;
    private Integer orLogID;
    private Integer userID;

}

