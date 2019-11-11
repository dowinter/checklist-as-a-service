package de.dowinter.checklist.data;

import com.google.common.collect.Lists;
import de.dowinter.checklist.core.Checklist;
import de.dowinter.checklist.data.ChecklistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Answers;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ChecklistRepositoryTest {

    private static final String TEST_CHECKLIST_ID = "testlist";

    @InjectMocks
    private ChecklistRepository cut;

    @Mock
    private Checklist checklist;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private EntityManager em;

    @Nested
    class GivenCheckableCanBeSaved {
        @Test
        void whenSavedShouldCallEntityManager() {
            cut.save(checklist);
            verify(em).persist(any());
        }

        @Test
        void whenSavedShouldReturnTrue() {
            boolean actual = cut.save(checklist);
            assertThat(actual, is(true));
        }
    }

    @Nested
    class GivenCheckableCanNotBeSaved {

        @BeforeEach
        void setUp() {
            willThrow(new PersistenceException()).given(em).persist(any());
        }

        @Test
        void thenShouldReturnFalse() {
            boolean actual = cut.save(checklist);
            assertThat(actual, is(false));
        }
    }

    @Nested
    class GivenChecklistDoesNotExist {
        @BeforeEach
        void setUp() {
            given(em.find(ArgumentMatchers.<Class<Checklist>>any(), anyString())).willReturn(null);
        }

        @Test
        void thenShouldCallFindOnEntityManagerWithId() {
            cut.withId(TEST_CHECKLIST_ID);
            verify(em).find(ArgumentMatchers.<Class<Checklist>>any(), eq(TEST_CHECKLIST_ID));
        }

        @Test
        void thenWithIdShouldReturnCheckable() {
            Optional<Checklist> actual = cut.withId(TEST_CHECKLIST_ID);
            assertThat(actual.isPresent(), is(false));
        }
    }

    @ParameterizedTest
    @MethodSource("createLists")
    void whenGetAllShouldReturnAllChecklists(ArrayList<? extends Checklist> givenList) {
        given(em.createQuery(any(), any()).getResultList()).will(iom -> givenList);
        List<Checklist> actual = cut.all();
        assertEquals(actual, givenList);

    }

    private static Stream<Arguments> createLists() {
        return Stream.of(
                Arguments.of(Lists.newArrayList()),
                Arguments.of(Lists.newArrayList(mock(Checklist.class))),
                Arguments.of(Lists.newArrayList(mock(Checklist.class), mock(Checklist.class))));
    }

}