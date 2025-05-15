package musinsa.recruitmemt.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class PriceTableDto {
    private String brandName;
    private Map<String, Integer> categoryPrices;

    public PriceTableDto(String brandName) {
        this.brandName = brandName;
        this.categoryPrices = new HashMap<>();
    }
}
