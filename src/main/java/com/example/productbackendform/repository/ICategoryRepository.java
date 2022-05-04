package com.example.productbackendform.repository;

import com.example.productbackendform.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface ICategoryRepository extends JpaRepository<Category, Long> {

    @Modifying
    @Query(value = "call deleteCategory(?1)", nativeQuery = true)
    void deleteCategoryByProcedure(Long id);
}
