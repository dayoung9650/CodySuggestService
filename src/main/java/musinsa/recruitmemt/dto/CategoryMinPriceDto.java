package musinsa.recruitmemt.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryMinPriceDto {
    private String categoryName;
    private String brandName;
    private Integer price;

    public CategoryMinPriceDto(String categoryName, String brandName, Integer price) {
        this.categoryName = categoryName;
        this.brandName = brandName;
        this.price = price;
    }
}
