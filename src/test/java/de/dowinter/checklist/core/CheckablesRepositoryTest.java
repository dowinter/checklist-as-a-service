package de.dowinter.checklist.core;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CheckablesRepositoryTest {
    private static final String TEST_CHECKLIST_ID = "test-checklist";
    private static final Long TEST_CHECKABLE_ID = 123L;

    @InjectMocks
    private CheckablesRepository cut;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private EntityManager em;

    @Mock
    private Checklist checklist;

    @Mock
    private ChecklistRepository checklists;

    @Nested
    class GivenCheckableExists {

        @BeforeEach
        void setUp() {
            given(checklists.withId(anyString())).willReturn(Optional.of(checklist));
        }

        @Test
        void thenShouldGetChecklistFromRepository() {
            cut.getCheckableFromChecklist(TEST_CHECKLIST_ID, TEST_CHECKABLE_ID);
            verify(checklists).withId(TEST_CHECKLIST_ID);
        }

        @Test
        void thenShouldReturnCheckableWithMatchingId() {
            Checkable expected = new Checkable();
            expected.setId(TEST_CHECKABLE_ID);
            given(checklist.getCheckables()).willReturn(Lists.newArrayList(expected));

            Optional<Checkable> actual = cut.getCheckableFromChecklist(TEST_CHECKLIST_ID, TEST_CHECKABLE_ID);
            assertThat(actual.isPresent(), is(true));
            assertThat(actual.get().getId(), is(TEST_CHECKABLE_ID));
        }
    }

    @Nested
    class GivenUnsavedCheckable {
        @Test
        void whenChecklistExistsShouldPersistAndReturnTrue() {
            given(checklists.withId(TEST_CHECKLIST_ID)).willReturn(Optional.of(checklist));

            List<Checkable> savedList = Lists.newArrayList();
            given(checklist.getCheckables()).willReturn(savedList);

            Checkable toSave = new Checkable();

            boolean actual = cut.addCheckable(TEST_CHECKLIST_ID, toSave);
            assertThat(actual, is(true));
            verify(em).persist(toSave);
            assertThat(savedList, contains(toSave));
        }

        @Test
        void whenNoChecklistExistsShouldNotPersistAndReturnFalse() {
            given(checklists.withId(TEST_CHECKLIST_ID)).willReturn(Optional.empty());

            Checkable toSave = new Checkable();

            boolean actual = cut.addCheckable(TEST_CHECKLIST_ID, toSave);
            assertThat(actual, is(false));
            verify(em, never()).persist(any());
        }
    }

    @ParameterizedTest
    @MethodSource("createLists")
    void whenGetAllShouldReturnFullList(ArrayList<? extends Checkable> givenList) {
        Checklist checklist = mock(Checklist.class);
        given(checklist.getCheckables()).willAnswer(iom -> givenList);
        given(checklists.withId(anyString())).willReturn(Optional.of(checklist));

        List<Checkable> actual = cut.getAllCheckablesFromChecklist(TEST_CHECKLIST_ID);
        assertEquals(actual, givenList);

    }

    private static Stream<Arguments> createLists() {
        return Stream.of(
                Arguments.of(Lists.newArrayList()),
                Arguments.of(Lists.newArrayList(mock(Checkable.class))),
                Arguments.of(Lists.newArrayList(mock(Checkable.class), mock(Checkable.class))));
    }
}