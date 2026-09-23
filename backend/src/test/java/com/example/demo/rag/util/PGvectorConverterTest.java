package com.example.demo.rag.util;

import com.pgvector.PGvector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PGvectorConverterTest {

    private PGvectorConverter converter;

    @BeforeEach
    void setUp() {
        converter = new PGvectorConverter();
    }

    @Test
    @DisplayName("Should convert valid string to PGvector for database column")
    void shouldConvertToDatabaseColumn() {
        // Test Goal: Verify that a valid vector string is converted into a PGvector instance.
        // Given: A valid vector string
        String input = "[1.0,2.0,3.0]";

        // When: Converting to database column
        PGvector result = converter.convertToDatabaseColumn(input);

        // Then: The PGvector is not null and has type vector and matching value
        assertNotNull(result);
        assertEquals("vector", result.getType());
        assertEquals("[1.0,2.0,3.0]", result.getValue());
    }

    @Test
    @DisplayName("Should return null when string attribute is null")
    void shouldReturnNullWhenAttributeIsNull() {
        // Test Goal: Verify that null string attribute yields null database column representation.
        // Given: A null input string
        String input = null;

        // When: Converting to database column
        PGvector result = converter.convertToDatabaseColumn(input);

        // Then: Result should be null
        assertNull(result);
    }

    @Test
    @DisplayName("Should convert PGvector to entity attribute string")
    void shouldConvertToEntityAttribute() throws Exception {
        // Test Goal: Verify that a PGvector is converted back to its string representation.
        // Given: A PGvector object
        PGvector dbData = new PGvector("[4.5,5.5,6.5]");

        // When: Converting to entity attribute
        String result = converter.convertToEntityAttribute(dbData);

        // Then: The string matches the PGvector value
        assertEquals("[4.5,5.5,6.5]", result);
    }

    @Test
    @DisplayName("Should return null when PGvector dbData is null")
    void shouldReturnNullWhenDbDataIsNull() {
        // Test Goal: Verify that null PGvector converts to null entity attribute.
        // Given: A null PGvector
        PGvector dbData = null;

        // When: Converting to entity attribute
        String result = converter.convertToEntityAttribute(dbData);

        // Then: Result should be null
        assertNull(result);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when vector string is malformed")
    void shouldThrowExceptionWhenVectorStringIsMalformed() {
        // Test Goal: Verify that an invalid vector string throws IllegalArgumentException.
        // Given: An invalid vector string
        String malformed = "not_a_vector";

        // When & Then: Converting to database column throws IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> converter.convertToDatabaseColumn(malformed));
    }
}
