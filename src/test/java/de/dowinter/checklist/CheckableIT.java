package de.dowinter.checklist;

import de.dowinter.checklist.core.Checkable;
import de.dowinter.checklist.core.Checklist;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class CheckableIT {
    String checklistId;


    @BeforeEach
    void setUp() {
        createChecklist();
    }

    @Test
    void shouldAddCheckables() {
        Checkable checkable = new Checkable();
        checkable.setChecked(false);

        postCheckable(checkable);
    }

    @Test
    void shouldGetAllCheckables() {
        Checkable firstCheckable = new Checkable();
        firstCheckable.setChecked(false);
        Checkable secondCheckable = new Checkable();
        secondCheckable.setChecked(false);

        postCheckable(firstCheckable);
        postCheckable(secondCheckable);

        given()
        .when()
            .get(String.format("/checklists/%s/checkables", checklistId))
        .then()
            .statusCode(Status.OK.getStatusCode())
            .body("", hasSize(2));
    }

    @Test
    void shouldGetSingleCheckable() {
        Checkable checkable = new Checkable();
        checkable.setChecked(false);

        String location = postCheckable(checkable);

        verifyCheckableIsChecked(location, false);
    }

    @Test
    void shouldToggleCheckable() {
        Checkable checkable = new Checkable();
        checkable.setChecked(false);

        String location = postCheckable(checkable);

        verifyCheckableIsChecked(location, false);

        given()
        .when()
            .post(location + "/toggle")
        .then()
            .statusCode(Status.NO_CONTENT.getStatusCode());

        verifyCheckableIsChecked(location, true);
    }

    private void verifyCheckableIsChecked(String location, boolean expectedCheckedStatus) {
        given()
                .when()
                .get(location)
                .then()
                .body("id", is(notNullValue()))
                .body("checked", is(expectedCheckedStatus));
    }

    private String postCheckable(Checkable checkable) {
        return given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(checkable)
        .when()
                .post(String.format("/checklists/%s/checkables", checklistId))
        .then()
                .statusCode(Status.CREATED.getStatusCode())
                .header("Location", not(emptyOrNullString()))
        .extract()
                .header("Location");
    }

    private void createChecklist() {
        checklistId = UUID.randomUUID().toString();
        Checklist checklist = new Checklist();
        checklist.setId(checklistId);

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(checklist)
        .when()
            .put(String.format("/checklists/%s", checklist.getId()))
        .then()
            .statusCode(Status.CREATED.getStatusCode());
    }
}
