package musinsa.recruitmemt.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class BrandTotalPriceDto {
    private String brandName;
    private Integer totalPrice;
    private Map<String, Integer> categoryPrices;

    public BrandTotalPriceDto(String brandName, Integer totalPrice) {
        this.brandName = brandName;
        this.totalPrice = totalPrice;
        this.categoryPrices = new HashMap<>();
    }
}
