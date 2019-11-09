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
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CheckablesRepositoryTest {
    private static final Long TEST_ID = 123L;

    @InjectMocks
    private
    CheckablesRepository cut;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private
    EntityManager em;

    @Mock
    private
    Checkable checkable;

    @Nested
    class GivenCheckableExists {

        @BeforeEach
        void setUp() {
            given(em.find(ArgumentMatchers.<Class<Checkable>>any(), any(Long.class))).willReturn(checkable);
        }

        @Test
        void thenShouldCallFindOnEntityManagerWithId() {
            cut.withId(TEST_ID);
            verify(em).find(ArgumentMatchers.<Class<Checkable>>any(), eq(TEST_ID));
        }

        @Test
        void thenWithIdShouldReturnCheckable() {
            Optional<Checkable> actual = cut.withId(TEST_ID);
            assertThat(actual.isPresent(), is(true));
            assertThat(actual.get(), is(sameInstance(checkable)));
        }
    }

    @Nested
    class GivenCheckableDoesNotExist {
        @BeforeEach
        void setUp() {
            given(em.find(ArgumentMatchers.<Class<Checkable>>any(), any(Long.class))).willReturn(null);
        }

        @Test
        void thenShouldCallFindOnEntityManagerWithId() {
            cut.withId(TEST_ID);
            verify(em).find(ArgumentMatchers.<Class<Checkable>>any(), eq(TEST_ID));
        }

        @Test
        void thenWithIdShouldReturnCheckable() {
            Optional<Checkable> actual = cut.withId(TEST_ID);
            assertThat(actual.isPresent(), is(false));
        }
    }

    @Nested
    class GivenCheckableCanBeSaved {
        @Test
        void whenSavedShouldCallEntityManager() {
            cut.save(checkable);
            verify(em).persist(any());
        }

        @Test
        void whenSavedShouldReturnCheckableId() {
            given(checkable.getId()).willReturn(TEST_ID);
            Long actual = cut.save(checkable);
            verify(checkable).getId();

            assertEquals(actual, TEST_ID);
        }
    }

    @ParameterizedTest
    @MethodSource("createLists")
    void whenGetAllShouldReturnFullList(ArrayList<? extends Checkable> givenList) {
        given(em.createQuery(any(), any()).getResultList()).will(iom -> givenList);
        List<Checkable> actual = cut.all();
        assertEquals(actual, givenList);

    }

    private static Stream<Arguments> createLists() {
        return Stream.of(
                Arguments.of(Lists.newArrayList()),
                Arguments.of(Lists.newArrayList(mock(Checkable.class))),
                Arguments.of(Lists.newArrayList(mock(Checkable.class), mock(Checkable.class))));
    }
}