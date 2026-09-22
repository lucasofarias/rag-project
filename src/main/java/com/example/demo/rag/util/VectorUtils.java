package com.example.demo.rag.util;

import com.pgvector.PGvector;

public final class VectorUtils {

    private VectorUtils() {
    }

    /**
     * Converts a float array to PostgreSQL pgvector string format: "[v1,v2,...]".
     */
    public static String toVectorString(float[] vector) {
        if (vector == null) {
            return null;
        }
        return new PGvector(vector).toString();
    }
}
