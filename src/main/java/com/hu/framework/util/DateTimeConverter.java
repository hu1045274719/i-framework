package com.hu.framework.util;

import org.apache.commons.beanutils.Converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeConverter implements Converter {
    @Override
    public Object convert(Class type, Object value) {
        if (value == null || "".equals(value)) {
            return null;
        }
        if (value instanceof String) {
            String dateValue = value.toString().trim();

            if (type.equals(LocalDate.class)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                return LocalDate.parse(dateValue, formatter);
            }
            if (type.equals(LocalDateTime.class)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                return LocalDateTime.parse(dateValue, formatter);
            }
        }
        return value;
    }
}
