package com.example.CoreBack;

import com.example.CoreBack.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestConfig.class)
class CoreBackApplicationTests {

	@Test
	void contextLoads() {
	}

}
