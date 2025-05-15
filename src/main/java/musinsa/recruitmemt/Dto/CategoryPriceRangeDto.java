package musinsa.recruitmemt.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryPriceRangeDto {
    private String categoryName;
    private BrandPrice lowestPrice;
    private BrandPrice highestPrice;

    @Getter
    @Setter
    public static class BrandPrice {
        private String brandName;
        private Integer price;

        public BrandPrice(String brandName, Integer price) {
            this.brandName = brandName;
            this.price = price;
        }
    }
}
