package at.qe.skeleton.tests;

import at.qe.skeleton.model.Card;
import at.qe.skeleton.model.Deck;
import at.qe.skeleton.model.User;
import at.qe.skeleton.model.UserRole;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

/**
 * Tests to ensure that each entity's implementation of equals conforms to the
 * contract. See {@linkplain http://www.jqno.nl/equalsverifier/} for more
 * information.
 * <p>
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 */
public class EqualsImplementationTest {
  
  @Test
  public void testUserEqualsContract() {
    User user1 = new User();
    user1.setUsername("user1");
    User user2 = new User();
    user2.setUsername("user2");
    Deck deck1 = new Deck();
    deck1.setId(1L);
    Deck deck2 = new Deck();
    deck2.setId(2L);
    Card card1 = new Card();
    card1.setId(1L);
    Card card2 = new Card();
    card2.setId(2L);
    EqualsVerifier.forClass(User.class).withPrefabValues(User.class, user1, user2).withPrefabValues(Deck.class, deck1, deck2).withPrefabValues(Card.class, card1, card2).suppress(Warning.STRICT_INHERITANCE, Warning.ALL_FIELDS_SHOULD_BE_USED).verify();
  }
  
  @Test
  public void testDeckEqualsContract() {
    User user1 = new User();
    user1.setUsername("user1");
    User user2 = new User();
    user2.setUsername("user2");
    Deck deck1 = new Deck();
    deck1.setId(1L);
    Deck deck2 = new Deck();
    deck2.setId(2L);
    Card card1 = new Card();
    card1.setId(1L);
    Card card2 = new Card();
    card2.setId(2L);
    EqualsVerifier.forClass(Deck.class).withPrefabValues(User.class, user1, user2).withPrefabValues(Deck.class, deck1, deck2).withPrefabValues(Card.class, card1, card2).suppress(Warning.STRICT_INHERITANCE, Warning.ALL_FIELDS_SHOULD_BE_USED).verify();
  }
  
  @Test
  public void testCardEqualsContract() {
    User user1 = new User();
    user1.setUsername("user1");
    User user2 = new User();
    user2.setUsername("user2");
    Card card1 = new Card();
    card1.setId(1L);
    Card card2 = new Card();
    card2.setId(2L);
    EqualsVerifier.forClass(Card.class).withPrefabValues(User.class, user1, user2).withPrefabValues(Card.class, card1, card2).suppress(Warning.STRICT_INHERITANCE, Warning.ALL_FIELDS_SHOULD_BE_USED).verify();
  }
  
  @Test
  public void testUserRoleEqualsContract() {
    EqualsVerifier.forClass(UserRole.class).verify();
  }
  
}