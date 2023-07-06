package pers.ervinse.ddmall.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentInfo {
    private Integer CommodityID;
    private String ReviewText;
    private Integer CommentLevel;
    private Integer OrderID;
}
