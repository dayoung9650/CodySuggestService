package musinsa.recruitmemt.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import musinsa.recruitmemt.model.Item;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class BrandTotalPriceDto {
    private String brandName;
    private int totalPrice;
    private Map<String, Integer> categoryPrices = new HashMap<>();

    public BrandTotalPriceDto(String brandName, int totalPrice) {
        this.brandName = brandName;
        this.totalPrice = totalPrice;
    }

    public BrandTotalPriceDto(String brandName, int totalPrice, Map<String, Integer> categoryPrices) {
        this.brandName = brandName;
        this.totalPrice = totalPrice;
        this.categoryPrices = categoryPrices;
    }

    public static BrandTotalPriceDto of(Item item) {
        Map<String, Integer> categoryPrices = new HashMap<>();
        categoryPrices.put(item.getCategory().getCategoryName(), item.getPrice());
        
        return new BrandTotalPriceDto(
            item.getBrand().getBrandName(),
            item.getPrice()
        );
    }
}
