package com.rudenko.socialmedia.repository

import com.rudenko.socialmedia.data.entity.User
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.spock.Testcontainers
import spock.lang.Shared
import spock.lang.Specification

@SpringBootTest
@Testcontainers
abstract class SpockDbIntegrationTest extends Specification {
    @Shared
    static def mongoContainer = new MongoDBContainer("mongo:6.0")
            .withExposedPorts(27017)
            .withCommand("--replSet", "rs0")
            .waitingFor(Wait.forLogMessage(".*Waiting for connections.*", 1))

    @DynamicPropertySource
    static void setMongoProperties(DynamicPropertyRegistry registry) {
        mongoContainer.start()
        String replicaSetUrl = "mongodb://" + mongoContainer.getHost() + ":" + mongoContainer.getMappedPort(27017)
        registry.add("spring.data.mongodb.uri", () -> replicaSetUrl)
    }
}
