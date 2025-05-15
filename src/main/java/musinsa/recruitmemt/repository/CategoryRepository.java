package musinsa.recruitmemt.repository;

import musinsa.recruitmemt.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}