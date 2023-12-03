package com.og.templateback.configuration.swagger;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@SuppressWarnings("FieldCanBeLocal")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/")
@Hidden
public class SwaggerController {
    private final String SWAGGER_UI__URI = "swagger-ui/index.html";

    @GetMapping(value = "/")
    public void root(HttpServletResponse rsp) throws IOException {
        rsp.sendRedirect(SWAGGER_UI__URI);
    }
}
