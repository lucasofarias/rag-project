package com.example.demo.rag.repository;

import com.example.demo.rag.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    boolean existsByRecipeName(String recipeName);

    @Query(value = """
        SELECT id, recipe_name, cuisine_path, servings, rating, img_src, ingredients, directions,
               (1 - (embedding <=> cast(:queryEmbedding as vector))) AS similarity
        FROM recipe
        WHERE (:cuisineFilter IS NULL OR cuisine_path ILIKE CONCAT('%', :cuisineFilter, '%'))
        ORDER BY embedding <=> cast(:queryEmbedding as vector) ASC
        LIMIT :topK
    """, nativeQuery = true)
    List<Object[]> findSimilarRecipes(
            @Param("queryEmbedding") String queryEmbedding,
            @Param("cuisineFilter") String cuisineFilter,
            @Param("topK") int topK
    );
}
