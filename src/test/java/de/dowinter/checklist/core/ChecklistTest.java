package de.dowinter.checklist.core;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ChecklistTest {
    private static final String TEST_ID = "testlist";

    private Checklist cut;

    @BeforeEach
    void setUp() {
        cut = new Checklist();
    }

    @Test
    void shouldSetAndGetId() {
        cut.setId(TEST_ID);
        assertEquals(TEST_ID, cut.getId());
    }

    @Test
    void shouldSetAndGetCheckables() {
        ArrayList<Checkable> checkables = Lists.newArrayList();
        cut.setCheckables(checkables);
        assertThat(cut.getCheckables(), is(sameInstance(checkables)));
    }
}