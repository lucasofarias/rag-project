CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS recipe (
    id BIGSERIAL PRIMARY KEY,
    recipe_name VARCHAR(255) NOT NULL,
    cuisine_path VARCHAR(255),
    servings INT,
    rating DOUBLE PRECISION,
    img_src TEXT,
    ingredients TEXT NOT NULL,
    directions TEXT NOT NULL,
    search_content TEXT NOT NULL,
    embedding vector(768) NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS recipe_name_unique_idx ON recipe (recipe_name);

CREATE INDEX IF NOT EXISTS recipe_embedding_hnsw_idx ON recipe USING hnsw (embedding vector_cosine_ops);
