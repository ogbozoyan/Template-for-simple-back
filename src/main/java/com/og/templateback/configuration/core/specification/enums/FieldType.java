package com.og.templateback.configuration.core.specification.enums;

import lombok.extern.slf4j.Slf4j;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Define enum of field type which is can be used to parse into data type.
 *
 * @author ogbozoyan
 * @since 01.03.2023
 */
@Slf4j
public enum FieldType {

    BOOLEAN {
        public Object parse(String value) {
            return Boolean.valueOf(value);
        }
    },

    TIMESTAMP {
        public Object parse(String value) {
            Object date = null;
            try {
                date = Timestamp.valueOf(value);
            } catch (Exception e) {
                log.info("Failed parse field type DATE {}", e.getMessage());
            }

            return date;
        }
    },
    DATE {
        public Object parse(String value) {
            Object date = null;
            try {
                date = Date.valueOf(value);
            } catch (Exception e) {
                log.info("Failed parse field type DATE {}", e.getMessage());
            }

            return date;
        }
    },
    DOUBLE {
        public Object parse(String value) {
            try {
                return Double.valueOf(value);
            } catch (Exception e) {
                log.info("Failed parse field type DOUBLE {}", e.getMessage());
            }
            return value;
        }
    },

    INTEGER {
        public Object parse(String value) {
            try {
                return Integer.valueOf(value);
            } catch (Exception e) {
                log.info("Failed parse field type INTEGER {}", e.getMessage());
            }
            return value;
        }
    },

    LONG {
        public Object parse(String value) {
            try {
                return Long.valueOf(value);
            } catch (Exception e) {
                log.info("Failed parse field type LONG {}", e.getMessage());
            }
            return value;
        }
    },

    STRING {
        public Object parse(String value) {
            try {
                return value;
            } catch (Exception e) {
                log.info("Failed parse field type STRING {}", e.getMessage());
            }
            return value;
        }
    };

    public abstract Object parse(String value);

}