package musinsa.recruitmemt;

import musinsa.recruitmemt.model.Brand;
import musinsa.recruitmemt.model.Category;
import musinsa.recruitmemt.model.Item;
import musinsa.recruitmemt.repository.BrandRepository;
import musinsa.recruitmemt.repository.CategoryRepository;
import musinsa.recruitmemt.repository.ItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(
            BrandRepository brandRepository,
            CategoryRepository categoryRepository,
            ItemRepository itemRepository) {
        return args -> {
            // 카테고리 초기화
            List<String> categoryNames = Arrays.asList(
                    "상의", "아우터", "바지", "스니커즈", "가방", "모자", "양말", "액세서리"
            );

            List<Category> categories = categoryNames.stream()
                    .map(name -> {
                        Category category = new Category();
                        category.setCategoryName(name);
                        return categoryRepository.save(category);
                    })
                    .toList();

            // 브랜드 초기화
            List<String> brandNames = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I");

            // 가격 데이터 초기화 (브랜드별 카테고리별 가격)
            int[][] prices = {
                    {11200, 5500, 4200, 9000, 2000, 1700, 1800, 2300},  // A
                    {10500, 5900, 3800, 9100, 2100, 2000, 2000, 2200},  // B
                    {10000, 6200, 3300, 9200, 2200, 1900, 2200, 2100},  // C
                    {10100, 5100, 3000, 9500, 2500, 1500, 2400, 2000},  // D
                    {10700, 5000, 3800, 9900, 2300, 1800, 2100, 2100},  // E
                    {11200, 7200, 4000, 9300, 2100, 1600, 2300, 1900},  // F
                    {10500, 5800, 3900, 9000, 2200, 1700, 2100, 2000},  // G
                    {10800, 6300, 3100, 9700, 2100, 1600, 2000, 2000},  // H
                    {11400, 6700, 3200, 9500, 2400, 1700, 1700, 2400}   // I
            };

            // 브랜드와 상품 데이터 생성
            for (int i = 0; i < brandNames.size(); i++) {
                Brand brand = new Brand();
                brand.setBrandName(brandNames.get(i));
                brand = brandRepository.save(brand);

                for (int j = 0; j < categories.size(); j++) {
                    Item item = new Item();
                    item.setName(categories.get(j).getCategoryName());
                    item.setBrand(brand);
                    item.setCategory(categories.get(j));
                    item.setPrice(prices[i][j]);
                    itemRepository.save(item);
                }
            }
        };
    }
}