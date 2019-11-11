package de.dowinter.checklist;

import de.dowinter.checklist.core.Checklist;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
public class ChecklistIT {
    private Checklist checklist;

    @BeforeEach
    void setUp() {
        String checklistId = UUID.randomUUID().toString();
        checklist = new Checklist();
        checklist.setId(checklistId);
    }

    @Test
    void shouldCreateChecklist() {
        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(checklist)
        .when()
            .put(String.format("/checklists/%s", checklist.getId()))
        .then()
            .statusCode(Status.CREATED.getStatusCode());
    }

    @Test
    void shouldRetrieveChecklist() {
        shouldCreateChecklist();

        given()
        .when().get(String.format("/checklists/%s", checklist.getId()))
        .then()
            .statusCode(Status.OK.getStatusCode())
            .body("id", equalTo(checklist.getId()));
    }
}
