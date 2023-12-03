package com.og.templateback.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;

/**
 * @author ogbozoyan
 * @since 20.10.2023
 */
@SuppressWarnings("unused")
@Configuration
@EnableFeignClients
public class ApplicationConfiguration {
    public static final SimpleDateFormat MM_DD_YYYY_FORMATTER = new SimpleDateFormat("MM-dd-yyyy");
    public static final SimpleDateFormat HH_mm_MM_DD_YYYY_FORMATTER = new SimpleDateFormat("HH-mm MM-dd-yyyy");

    @Bean(name = "defaultMapper")
    ModelMapper patchingModelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setAmbiguityIgnored(true)
                .setSkipNullEnabled(true)
                .setCollectionsMergeEnabled(false)
                .setPropertyCondition(Conditions.isNotNull());
        return modelMapper;
    }

    @Bean(name = "defaultObjectWriter")
    ObjectWriter objectWriter() {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        return mapper.writer().withDefaultPrettyPrinter();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
