package musinsa.recruitmemt.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LowestBrandResponseDto {
    private LowestBrand lowestBrand;

    @Getter
    @Setter
    public static class LowestBrand {
        private String brandName;
        private List<CategoryPrice> categoryPrices;
        private String totalPrice;
    }

    @Getter
    @Setter
    public static class CategoryPrice {
        private String category;
        private String price;
    }
} 