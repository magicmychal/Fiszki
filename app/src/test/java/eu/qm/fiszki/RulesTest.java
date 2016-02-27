package eu.qm.fiszki;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by mBoiler on 26.02.2016.
 */
public class RulesTest {

    Rules tRules;

    @Before
    public void setUp() throws Exception {
        tRules = new Rules();
    }

    @Test
    public void testCheck() throws Exception {
        assertTrue(tRules.Check(new String("chuj"),new String("chuj")));
        assertFalse(tRules.Check(new String("chuj"),new String("chuj")));
    }
}