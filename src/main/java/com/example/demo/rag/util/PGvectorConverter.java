package com.example.demo.rag.util;

import com.pgvector.PGvector;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.sql.SQLException;

@Converter
public class PGvectorConverter implements AttributeConverter<String, PGvector> {

    @Override
    public PGvector convertToDatabaseColumn(String attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return new PGvector(attribute);
        } catch (SQLException e) {
            throw new IllegalArgumentException("Failed to convert attribute to PGvector: " + attribute, e);
        }
    }

    @Override
    public String convertToEntityAttribute(PGvector dbData) {
        return dbData != null ? dbData.getValue() : null;
    }
}
