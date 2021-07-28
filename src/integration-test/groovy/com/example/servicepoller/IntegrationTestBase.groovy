package com.example.servicepoller


import io.restassured.RestAssured
import io.zonky.test.db.AutoConfigureEmbeddedDatabase
import org.mockserver.model.HttpRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.util.DefaultUriTemplateHandler
import org.springframework.web.util.UriComponentsBuilder
import org.springframework.web.util.UriTemplateHandler
import spock.lang.Specification

@AutoConfigureEmbeddedDatabase(provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.OPENTABLE)
@SpringBootTest(classes = [ServicePollerApplication],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integrationtest")
class IntegrationTestBase extends Specification {

    private static final UriTemplateHandler uriTemplateHandler = new DefaultUriTemplateHandler()

    @LocalServerPort
    private int serverPort
    @Autowired
    private JdbcTemplate jdbcTemplate

    def setup() {
        RestAssured.port = serverPort;
    }

    def cleanup() {
        jdbcTemplate.update("DELETE FROM service_details")
    }

    HttpRequest createRequestMatcher(final String requestTemplate,
                                     final String method) {
        def uri = createUri(requestTemplate);
        return HttpRequest.request(uri.getPath())
                .withMethod(method);
    }

    URI createUri(final String pathTemplate) {
        return UriComponentsBuilder.fromHttpUrl(uriTemplateHandler.expand("http://doesntmatter" + pathTemplate)
                .toString())
                .build(true)
                .toUri();
    }
}