package pers.ervinse.ddmall.domain;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment implements Serializable {
    private Integer commodityID;
    private Integer commodityNum;
    private Integer orderPayState;
    private String merchantName;
    private String imageNumber;
    private String drugName;

}
