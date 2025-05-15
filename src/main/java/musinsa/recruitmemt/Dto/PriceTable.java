package musinsa.recruitmemt.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class PriceTable {
    private String brandName;
    private Map<String, Integer> categoryPrices;

    public PriceTable(String brandName) {
        this.brandName = brandName;
        this.categoryPrices = new HashMap<>();
    }
}
