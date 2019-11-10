package de.dowinter.checklist.api;

import de.dowinter.checklist.core.Checklist;
import de.dowinter.checklist.core.ChecklistRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.transaction.RollbackException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ChecklistsResourceTest {

    private static final String TEST_ID = "testlist";

    @InjectMocks
    private ChecklistsResource cut;

    @Mock
    private ChecklistRepository repository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private UriInfo uriInfo;

    @Nested
    class GetAll {
        @Test
        void whenGetAllShouldCallRepository() {
            cut.getAll();
            verify(repository).all();
        }

        @Test
        void whenGetAllShouldReturnOK() {
            int actual = cut.getAll().getStatus();
            assertEquals(actual, Response.Status.OK.getStatusCode());
        }
    }

    @Nested
    class GetSingle {
        @Test
        void whenGetSingleShouldCallRepositoryWithId() {
            cut.getChecklist(TEST_ID);
            verify(repository).withId(TEST_ID);
        }

        @Test
        void whenNoChecklistShouldReturnNotFound() {
            given(repository.withId(anyString())).willReturn(Optional.empty());
            int actual = cut.getChecklist(TEST_ID).getStatus();
            assertEquals(actual, Response.Status.NOT_FOUND.getStatusCode());
        }

        @Test
        void whenCheckableExistsShouldReturnOK() {
            given(repository.withId(anyString())).willReturn(Optional.ofNullable(mock(Checklist.class)));
            int actual = cut.getChecklist(TEST_ID).getStatus();
            assertEquals(actual, Response.Status.OK.getStatusCode());
        }
    }

    @Nested
    class Add {
        @Test
        void whenAddShouldCallRepositoryWithCheckable() throws RollbackException {
            Checklist givenChecklist = mock(Checklist.class);
            cut.addChecklist(givenChecklist);
            verify(repository).save(givenChecklist);
        }

        @Test
        void shouldReturnResponseWithStatusCreatedAndLocationHeader() throws URISyntaxException {
            given(uriInfo.getAbsolutePath())
                    .willReturn(new URI("http://dummy/check/" + TEST_ID));

            Checklist givenChecklist = mock(Checklist.class);
            Response actual = cut.addChecklist(givenChecklist);
            assertEquals(Response.Status.CREATED.getStatusCode(), actual.getStatus());
            assertThat(actual.getLocation().toString(), endsWith(TEST_ID));
        }
    }



}