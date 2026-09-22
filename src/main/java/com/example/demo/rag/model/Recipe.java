package com.example.demo.rag.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

@Entity
@Table(name = "recipe")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recipe_name", nullable = false)
    private String recipeName;

    @Column(name = "cuisine_path")
    private String cuisinePath;

    @Column(name = "servings")
    private Integer servings;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "img_src", columnDefinition = "TEXT")
    private String imgSrc;

    @Column(name = "ingredients", nullable = false, columnDefinition = "TEXT")
    private String ingredients;

    @Column(name = "directions", nullable = false, columnDefinition = "TEXT")
    private String directions;

    @Column(name = "search_content", nullable = false, columnDefinition = "TEXT")
    private String searchContent;

    @Column(name = "embedding", nullable = false, columnDefinition = "vector(768)")
    @ColumnTransformer(read = "embedding::text", write = "cast(? as vector)")
    private String embedding;
}
