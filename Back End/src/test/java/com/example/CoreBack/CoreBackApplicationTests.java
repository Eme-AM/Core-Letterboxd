package com.example.CoreBack;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.example.CoreBack.config.TestConfig;

@SpringBootTest
@Import(TestConfig.class)
@ActiveProfiles("test")
class CoreBackApplicationTests {

	@Test
	void contextLoads() {
	}

}
