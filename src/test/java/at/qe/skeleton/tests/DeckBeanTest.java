package at.qe.skeleton.tests;

import at.qe.skeleton.model.Deck;
import at.qe.skeleton.model.DeckStatus;
import at.qe.skeleton.ui.beans.DeckBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Tests for {@link DeckBean}
 *
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 */

public class DeckBeanTest {

    DeckBean deckBean = new DeckBean();

    Deck deck1;
    Deck deck2;

    @BeforeEach
    public void init(){
        deck1 = new Deck();
        deck2 = new Deck();
        deck1.setDescription("Spanish Vocabulary for Beginners.");
        deck1.setTitle("spanish");
        deck1.setStatus(DeckStatus.PUBLIC);
        deck2.setDescription("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");
        deck2.setTitle("german");
        deck2.setStatus(DeckStatus.PRIVATE);
    }

    @Test
    @DisplayName("Global Filter Function returns True when Filter is null or empty")
    public void globalFilterNull(){
        assertTrue(deckBean.globalFilterFunction(deck1,null,null));
        assertTrue(deckBean.globalFilterFunction(deck2,null,null));
        assertTrue(deckBean.globalFilterFunction(null,null,null));

        assertTrue(deckBean.globalFilterFunction(deck1,"",null));
        assertTrue(deckBean.globalFilterFunction(deck2,"",null));
        assertTrue(deckBean.globalFilterFunction(null,"",null));
    }

    @Test
    @DisplayName("Global Filter Function filters name correctly")
    public void globalFilterName(){
        deck1.setDescription("");
        deck2.setDescription("");
        assertTrue(deckBean.globalFilterFunction(deck1,"spanish",null));
        assertFalse(deckBean.globalFilterFunction(deck2,"spanish",null));
        assertTrue(deckBean.globalFilterFunction(deck1,"i",null));
        assertFalse(deckBean.globalFilterFunction(deck2,"i",null));
        assertTrue(deckBean.globalFilterFunction(deck2,"man",null));
        assertFalse(deckBean.globalFilterFunction(deck1,"man",null));
    }

    @Test
    @DisplayName("Global Filter Function filters description correctly")
    public void globalFilterDescription(){
        deck1.setTitle("");
        deck2.setTitle("");
        assertTrue(deckBean.globalFilterFunction(deck1,"vocab",null));
        assertFalse(deckBean.globalFilterFunction(deck2,"vocab",null));
        assertTrue(deckBean.globalFilterFunction(deck1,"BEGINNERS",null));
        assertFalse(deckBean.globalFilterFunction(deck2,"BEGINNERS",null));
        assertFalse(deckBean.globalFilterFunction(deck1,"ipsum dolor",null));
        assertTrue(deckBean.globalFilterFunction(deck2,"ipsum dolor",null));
    }
}

