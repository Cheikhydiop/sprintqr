package com.dette.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.dette.model.Article;
import com.dette.service.ArticleService;



@RestController
@RequestMapping("/articles")
public class ArticleController {
    
    @Autowired
    ArticleService articleService;

    // Endpoint pour récupérer tous les articles
    @GetMapping
    public List<Article> getArticles() {
        return articleService.getAllArticles();
    }
    
    // Endpoint pour ajouter un nouvel article
    @PostMapping("/add")
    public Article saveArticle(@RequestBody Article article) {
        return articleService.saveArticle(article);
    }
}
