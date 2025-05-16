package musinsa.recruitmemt.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class BrandCreateDto {
    private String brandName;
    private Map<String, ItemInfo> items;

    @Getter
    @Setter
    public static class ItemInfo {
        private String name;
        private Integer price;
    }
} 