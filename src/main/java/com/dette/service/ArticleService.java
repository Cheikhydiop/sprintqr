package com.dette.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import com.dette.model.Article;
import com.dette.model.Client;
import com.dette.repository.ArticleRepository;

@Service
public class ArticleService {
    @Autowired
    private ArticleRepository articleRepository; // Assurez-vous que l'injection est faite ici

    public List<Article> getAllArticles() {
        return articleRepository.findAll(); // Récupération des articles
    }
    
    public Article saveArticle(Article article) {
        return articleRepository.save(article);
    }
    
    public boolean existsByLibeller(String libeller) {
        return articleRepository.existsByLibeller(libeller);
    }
}
