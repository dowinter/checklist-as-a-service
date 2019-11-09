package de.dowinter.checklist.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CheckableTest {
    private static final long TEST_ID = 123L;

    private Checkable cut;

    @BeforeEach
    void setUp() {
        cut = new Checkable();
    }

    @Test
    void shouldSetAndGetId() {
        cut.setId(TEST_ID);
        assertEquals(TEST_ID, cut.getId());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldSetAndGetChecked(boolean state) {
        cut.setChecked(state);
        assertEquals(state, cut.isChecked());
    }
}