package com.dette.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dette.model.Article;
@Repository
public interface ArticleRepository  extends  JpaRepository<Article, Long>{
    boolean existsByLibeller(String libeller); 

}
