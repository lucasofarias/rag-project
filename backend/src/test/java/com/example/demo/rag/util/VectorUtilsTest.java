package com.example.demo.rag.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VectorUtilsTest {

    @Test
    @DisplayName("Should convert float array to PostgreSQL vector string format")
    void shouldFormatFloatArrayToVectorString() {
        // Test Goal: Verify that a valid float array is formatted correctly into PostgreSQL vector string format.
        // Given: An array of float numbers
        float[] vector = new float[]{1.0f, 2.5f, -3.14f};

        // When: Converting the float array using VectorUtils
        String result = VectorUtils.toVectorString(vector);

        // Then: The resulting string should follow the "[v1,v2,...]" syntax
        assertNotNull(result);
        assertEquals("[1.0,2.5,-3.14]", result);
    }

    @Test
    @DisplayName("Should return null when input float array is null")
    void shouldReturnNullWhenInputIsNull() {
        // Test Goal: Verify that a null float array returns null gracefully.
        // Given: A null float array
        float[] vector = null;

        // When: Converting the null vector
        String result = VectorUtils.toVectorString(vector);

        // Then: The result should be null
        assertNull(result);
    }

    @Test
    @DisplayName("Should handle empty float array")
    void shouldHandleEmptyFloatArray() {
        // Test Goal: Verify that an empty float array returns "[]".
        // Given: An empty float array
        float[] vector = new float[0];

        // When: Converting the empty vector
        String result = VectorUtils.toVectorString(vector);

        // Then: The result should be "[]"
        assertEquals("[]", result);
    }
}
