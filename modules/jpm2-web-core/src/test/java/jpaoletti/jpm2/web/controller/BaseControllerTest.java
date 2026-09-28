package jpaoletti.jpm2.web.controller;

import java.util.Arrays;
import java.util.Collections;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class BaseControllerTest {

    @Test
    public void textFieldIds() {
        assertEquals(Collections.emptyList(), BaseController.getTextFieldIds(null));
        assertEquals(Collections.emptyList(), BaseController.getTextFieldIds(""));
        assertEquals(Collections.emptyList(), BaseController.getTextFieldIds("null"));
        assertEquals(Arrays.asList("name"), BaseController.getTextFieldIds("name"));
        assertEquals(Arrays.asList("code", "name", "office"), BaseController.getTextFieldIds("[{code}] {name} ({!office})"));
        assertEquals(Arrays.asList("obs", "doc"), BaseController.getTextFieldIds("{obs|-}|{doc}"));
        assertEquals(Arrays.asList("codigo"), BaseController.getTextFieldIds("<td>{codigo}</td>"));
    }
}
