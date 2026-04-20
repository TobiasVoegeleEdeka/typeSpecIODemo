package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "spring.data.mongodb.uri=mongodb://localhost:27017/test")
class DemoApplicationTests {

	@Test
	void contextLoads() {
        // Just verify context starts up. 
        // We'll skip deep mongo testing here to keep the build light and no-magic.
	}

}
