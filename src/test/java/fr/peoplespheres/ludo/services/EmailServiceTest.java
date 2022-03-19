package fr.peoplespheres.ludo.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

class EmailServiceTest {

    @Test
    void getArguments() {
    }

    /**
     * Tests for {@link EmailService#filterProhibitedEmailCharacters(String)}.
     */
    @Test
    void filterProhibitedEmailCharacters() {
        EmailService service = new EmailService();
        Assertions.assertEquals("Jean-Louis", service.filterProhibitedEmailCharacters("Jean-Louis"));
        Assertions.assertEquals("Jean-Louis", service.filterProhibitedEmailCharacters("Jea<n-Lo@uis"));
    }

    /**
     * Tests for {@link EmailService#eachWordFirstChars(java.lang.String, int)}.
     */
    @Test
    void eachWordFirstChars() {
        EmailService service = new EmailService();

        Assertions.assertEquals("", service.eachWordFirstChars(null, 1));

        Assertions.assertEquals("jl", service.eachWordFirstChars("Jean-Louis", 1));
        Assertions.assertEquals("jl", service.eachWordFirstChars("Jean Louis", 1));
        Assertions.assertEquals("jl", service.eachWordFirstChars("Jean   Louis", 1));
        Assertions.assertEquals("jl", service.eachWordFirstChars("Jean - Louis", 1));
        Assertions.assertEquals("j", service.eachWordFirstChars("JeanLouis", 1));

        Assertions.assertEquals("jelo", service.eachWordFirstChars("Jean-Louis", 2));
        Assertions.assertEquals("jelo", service.eachWordFirstChars("Jean- Louis", 2));
        Assertions.assertEquals("jelo", service.eachWordFirstChars("Jean Louis", 2));
        Assertions.assertEquals("jelo", service.eachWordFirstChars("Jean   Louis", 2));
        Assertions.assertEquals("jelo", service.eachWordFirstChars("Jean - Louis", 2));
        Assertions.assertEquals("je", service.eachWordFirstChars("JeanLouis", 2));

        Assertions.assertEquals("jean", service.eachWordFirstChars("Jean", 10));
    }

    /**
     * Tests for {@link EmailService#wordsCount(String)}.
     */
    @Test
    void wordsCount() {
        EmailService service = new EmailService();

        Assertions.assertEquals(0, service.wordsCount(null));
        Assertions.assertEquals(3, service.wordsCount("Jean-Charles Mignard"));
        Assertions.assertEquals(3, service.wordsCount("Jean- Charles Mignard"));
        Assertions.assertEquals(3, service.wordsCount("Jean - Charles Mignard"));
        Assertions.assertEquals(3, service.wordsCount("Jean-   Charles    Mignard"));
        Assertions.assertEquals(3, service.wordsCount(" Jean-   Charles    Mignard"));
        Assertions.assertEquals(1, service.wordsCount(" Jean-   "));
        Assertions.assertEquals(0, service.wordsCount(" - - - - "));
    }

    /**
     * Tests for {@link EmailService#evaluateExpression(Map, String)}.
     */
    @Test
    void evaluateExpression() {
        EmailService service = new EmailService();
        Map<String, String> inputsList = new HashMap<>();
        inputsList.put("input1", "Jean-Louis");
        inputsList.put("input2", "Jean-Charles Mignard");

        Assertions.assertEquals("jl", service.evaluateExpression(inputsList, "input1.eachWordFirstChars(1)"));
        Assertions.assertEquals("JeanCharles", service.evaluateExpression(inputsList, "input2.lastWords(-1)"));
    }

    /**
     * Tests for {@link EmailService#lastWords(String, int)}.
     */
    @Test
    void lastWords() {
        EmailService service = new EmailService();
        String inputWord = "Jean-Charles Mignard";

        Assertions.assertEquals("Mignard", service.lastWords(inputWord, 1));
        Assertions.assertEquals("JeanCharles", service.lastWords(inputWord, -1));
        Assertions.assertEquals("JeanCharlesMignard", service.lastWords(inputWord, 0));
    }

    @Test
    void getEmailFromArgs() {
        EmailService service = new EmailService();
        Map<String, String> inputsList = new LinkedHashMap<>();
        inputsList.put("input1", "Jean-Louis");
        inputsList.put("input2", "Jean-Charles Mignard");
        inputsList.put("input3", "external");
        inputsList.put("input4", "peoplespheres.fr");
        inputsList.put("input5", "fr");
        inputsList.put("input6", null);

        String expression = "input1.eachWordFirstChars(1) ~ '.' ~ (input2.wordsCount() > 1 ? input2.lastWords(-1).eachWordFirstChars(1) ~ input2.lastWords(1) : input2 ) ~ '@' ~ input3 ~ '.' ~ input4 ~ '.' ~ input5";
        service.getEmailFromArgs(inputsList, expression);
    }
}