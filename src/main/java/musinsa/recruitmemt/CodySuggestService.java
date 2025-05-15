package musinsa.recruitmemt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.HiddenHttpMethodFilter;

@SpringBootApplication
public class CodySuggestService {
    public static void main(String[] args) {
        SpringApplication.run(CodySuggestService.class, args);
    }

    /**
     * HTML form에서 PUT, DELETE 메소드를 사용하기 위한 필터 설정
     */
    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }
}
