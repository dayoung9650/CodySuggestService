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
    private Map<String, Integer> categoryPrices;
    private int totalPrice;

    public BrandTotalPriceDto(String brandName, Map<String, Integer> categoryPrices, int totalPrice) {
        this.brandName = brandName;
        this.categoryPrices = categoryPrices;
        this.totalPrice = totalPrice;
    }

    public BrandTotalPriceDto(String brandName, Integer totalPrice) {
        this.brandName = brandName;
        this.totalPrice = totalPrice;
        this.categoryPrices = new HashMap<>();
    }

    public static BrandTotalPriceDto of(Item item) {
        Map<String, Integer> categoryPrices = new HashMap<>();
        categoryPrices.put(item.getCategory().getCategoryName(), item.getPrice());
        
        return new BrandTotalPriceDto(
            item.getBrand().getBrandName(),
            categoryPrices,
            item.getPrice()
        );
    }
}
