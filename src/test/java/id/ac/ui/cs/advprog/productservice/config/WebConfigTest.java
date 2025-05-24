package id.ac.ui.cs.advprog.productservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class WebConfigTest {

    @Test
    void testImplementsWebMvcConfigurer() {
        WebConfig webConfig = new WebConfig();
        assertTrue(webConfig instanceof org.springframework.web.servlet.config.annotation.WebMvcConfigurer);
    }

    @Test
    void testAddCorsMappingsMethodExists() throws Exception {
        Method method = WebConfig.class.getDeclaredMethod("addCorsMappings", CorsRegistry.class);
        assertNotNull(method);
        assertEquals(void.class, method.getReturnType());
    }

    @Test
    void testHasConfigurationAnnotation() {
        assertTrue(WebConfig.class.isAnnotationPresent(org.springframework.context.annotation.Configuration.class));
    }
}