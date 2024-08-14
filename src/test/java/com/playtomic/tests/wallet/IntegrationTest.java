package com.playtomic.tests.wallet;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE)
@SpringBootTest
@Retention(RUNTIME)
@ActiveProfiles(profiles = "test")
public @interface IntegrationTest {
}
