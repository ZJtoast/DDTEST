package pers.ervinse.ddmall.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Photo {
    private Integer photosID;
    private String photoBytes;

    public String toString() {
        return photoBytes;
    }
}
