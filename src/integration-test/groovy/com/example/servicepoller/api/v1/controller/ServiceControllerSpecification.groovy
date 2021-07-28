package com.example.servicepoller.api.v1.controller

import com.example.servicepoller.IntegrationTestBase
import com.example.servicepoller.api.v1.model.ApiService
import com.example.servicepoller.api.v1.model.CreateServiceRequest
import com.example.servicepoller.api.v1.model.HealthCheck
import com.example.servicepoller.mock.server.MockService
import com.example.servicepoller.mock.server.ServiceMockManager
import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.mockserver.matchers.Times
import org.mockserver.model.HttpResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class ServiceControllerSpecification extends IntegrationTestBase {

    @Autowired
    private ObjectMapper objectMapper
    @Autowired
    private ServiceMockManager serviceMockManager

    def "Test create, get one, get all, update, delete service API"() {
        given:
        def lorServer = serviceMockManager.serverFor(MockService.LOR)
        def createRequest = CreateServiceRequest.builder()
                .name("lord-of-the-rings-service")
                .url("http://localhost:"+lorServer.getLocalPort()+"/ping")
                .build()
        mockService("/ping")
        when: "Service is created"
        def createResponse = RestAssured.given()
                .body(objectMapper.writeValueAsString(createRequest))
                .contentType(ContentType.JSON)
                .post("/v1/services")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .as(ApiService.class)
        then:
        createResponse
        createResponse.getId()
        createResponse.getName() == "lord-of-the-rings-service"
        createResponse.getStatus() == HealthCheck.OK.name()

        when: "Service is fetched"
        def getResponse = RestAssured.given()
                .get("/v1/services/${createResponse.getId()}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .as(ApiService.class)
        then:
        getResponse
        getResponse.getId() == createResponse.getId()
        getResponse.getName() == "lord-of-the-rings-service"

        when: "All services are fetched"
        def getAllResponse = RestAssured.given()
                .get("/v1/services")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .as(List.class)
        then:
        getAllResponse
        getAllResponse.size() == 1

        when: "Service is updated"
        def updateRequest = CreateServiceRequest.builder()
                .name("hobbit-service")
                .build()

        def updateResponse = RestAssured.given()
                .body(objectMapper.writeValueAsString(updateRequest))
                .contentType(ContentType.JSON)
                .patch("/v1/services/${createResponse.getId()}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .as(ApiService.class)
        then:
        updateResponse
        updateResponse.getId() == createResponse.getId()
        updateResponse.getName() == "hobbit-service"

        when: "Service is deleted"

        RestAssured.given()
                .delete("/v1/services/${createResponse.getId()}")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
        then:
        RestAssured.given()
                .get("/v1/services/${createResponse.getId()}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
    }

    def mockService(String url) {
        serviceMockManager.serverFor(MockService.LOR)
                .when(createRequestMatcher(url, HttpMethod.GET.name()), Times.once())
                .respond(HttpResponse.response()
                        .withStatusCode(200));
    }
}