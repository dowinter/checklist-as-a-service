package de.dowinter.api;

import de.dowinter.core.Checkable;
import de.dowinter.core.CheckablesRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CheckablesResourceTest {

    private static final Long TEST_ID = 123L;
    @InjectMocks
    private CheckablesResource cut;

    @Mock
    private CheckablesRepository repository;

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
            cut.getCheckable(TEST_ID);
            verify(repository).withId(TEST_ID);
        }

        @Test
        void whenNoCheckableShouldReturnNotFound() {
            given(repository.withId(anyLong())).willReturn(Optional.empty());
            int actual = cut.getCheckable(TEST_ID).getStatus();
            assertEquals(actual, Response.Status.NOT_FOUND.getStatusCode());
        }

        @Test
        void whenCheckableExistsShouldReturnOK() {
            given(repository.withId(anyLong())).willReturn(Optional.ofNullable(mock(Checkable.class)));
            int actual = cut.getCheckable(TEST_ID).getStatus();
            assertEquals(actual, Response.Status.OK.getStatusCode());
        }
    }

    @Nested
    class Add {
        @Test
        void whenAddShouldCallRepositoryWithCheckable() {
            Checkable givenCheckable = mock(Checkable.class);
            cut.addCheckable(givenCheckable);
            verify(repository).save(givenCheckable);
        }

        @Test
        void shouldReturnResponseWithStatusCreatedAndLocationHeader() throws URISyntaxException {
            given(uriInfo
                    .getAbsolutePathBuilder()
                    .path(TEST_ID.toString())
                    .build())
                .willReturn(new URI("http://dummy/check/" + TEST_ID));

            Checkable givenCheckable = mock(Checkable.class);
            given(givenCheckable.getId()).willReturn(TEST_ID);

            Response actual = cut.addCheckable(givenCheckable);
            assertEquals(Response.Status.CREATED.getStatusCode(), actual.getStatus());
            assertThat(actual.getLocation().toString(), endsWith(TEST_ID.toString()));
        }
    }

    @Nested
    class Invert {
        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void whenToggleShouldInvertCheckable(boolean initialState) {
            Checkable givenCheckable = mock(Checkable.class);
            given(givenCheckable.isChecked()).willReturn(initialState);
            given(repository.withId(TEST_ID)).willReturn(Optional.of(givenCheckable));
            cut.toggleCheckable(TEST_ID);
            verify(givenCheckable).setChecked(!initialState);
        }

        @Test
        void whenNoCheckableShouldReturnNotFound() {
            given(repository.withId(anyLong())).willReturn(Optional.empty());
            int actual = cut.toggleCheckable(TEST_ID).getStatus();
            assertEquals(actual, Response.Status.NOT_FOUND.getStatusCode());
        }

        @Test
        void whenCheckableExistsShouldReturnNoContent() {
            given(repository.withId(anyLong())).willReturn(Optional.ofNullable(mock(Checkable.class)));
            int actual = cut.toggleCheckable(TEST_ID).getStatus();
            assertEquals(actual, Response.Status.NO_CONTENT.getStatusCode());
        }

    }

}