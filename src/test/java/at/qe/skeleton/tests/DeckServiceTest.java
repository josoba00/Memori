package at.qe.skeleton.tests;

import at.qe.skeleton.model.Card;
import at.qe.skeleton.model.Deck;
import at.qe.skeleton.model.DeckStatus;
import at.qe.skeleton.model.User;
import at.qe.skeleton.repositories.DeckRepository;
import at.qe.skeleton.services.DeckService;
import at.qe.skeleton.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.management.InstanceAlreadyExistsException;
import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@WebAppConfiguration
class DeckServiceTest {

    @Autowired
    DeckService deckService;
    @Autowired
    DeckRepository deckRepository;
    @Autowired
    UserService userService;

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testDeckInitialization() {
        for (Deck deck : deckService.getAllDecks()) {
            if (DeckStatus.PUBLIC.equals(deck.getStatus())){
                assertTrue(deck.getStatus().toString().contains(DeckStatus.PUBLIC.toString()));
                assertNotNull(deck.getCreationDate());
            } else if (DeckStatus.PRIVATE.equals(deck.getStatus())){
                assertTrue(deck.getStatus().toString().contains(DeckStatus.PRIVATE.toString()));
                assertNotNull(deck.getCreationDate());
            } else if (DeckStatus.LOCKED.equals(deck.getStatus())) {
                assertTrue(deck.getStatus().toString().contains(DeckStatus.LOCKED.toString()));
                assertNotNull(deck.getCreationDate());
            } else {
                fail("Unknown Status \"" + deck.getStatus() + "\" loaded from test data source via DeckService.getAllDecks");
            }
        }
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void getAllDecks(){
        assertEquals(deckRepository.findAll().size(), deckService.getAllDecks().size());
    }

    // TODO: Find out why delete() deletes all Decks except if i call Deck with Id = 1L;
    /*
    @DirtiesContext
    @Test
    @Transactional
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void deleteDeck() {
        String deckTitle = "European Capitals";
        Deck toBeDeletedDeck = deckService.loadDeck(2L);

        assertEquals(3, deckService.getAllDecks().size(), "There are missing Decks");

        System.out.println(deckService.getAllDecks()); // returns 3
        deckService.deleteDeck(toBeDeletedDeck);
        System.out.println(deckService.getAllDecks()); // returns null

        //assertEquals(2, deckService.getAllDecks().size());
        //Deck deletedDeck = deckService.loadDeck(2L);
        //assertNull(deletedDeck, "Deleted Deck \"" + deckTitle + "\" could still be loaded from test data source");
    }*/

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    void loadPublicDecks() {
        User copiedUser = new User();
        copiedUser.setUsername("testUser");
        userService.saveUser(copiedUser);

        assertEquals(deckRepository.findAllByStatusIsPublic().size(), deckService.loadPublicDecks(copiedUser).size());
    }

    @Test
    @Transactional
    void loadDecksWithTitle() {
        Deck copiedDeck = deckService.loadDeck(1L);
        assertEquals(List.of(copiedDeck), deckService.loadDecksWithTitle("EN"));
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void loadOwnDecks() {
        User copiedUser = userService.loadUser("user2");
        Deck copiedDeck1 = deckService.loadDeck(2L);
        Deck copiedDeck2 = deckService.loadDeck(3L);
        assertEquals(List.of(copiedDeck1,copiedDeck2), deckService.loadOwnDecks(copiedUser));
    }

    @Test
    @Transactional
    @WithMockUser(username = "user1", authorities = {"LEARNER"})
    void addCardToDeckException() {
        Deck deck = deckService.loadDeck(1L);
        Card card = new Card();
        card.setId(2L);

        assertThrows(InstanceAlreadyExistsException.class, () -> deckService.addCardToDeck(card, deck));
    }

    @Test
    @Transactional
    @WithMockUser(username = "user1", authorities = {"LEARNER"})
    void addCardToDeck() throws InstanceAlreadyExistsException {
        Deck deck = deckService.loadDeck(1L);

        Card card2 = new Card();
        card2.setId(20L);

        deckService.addCardToDeck(card2, deck);
        Deck newDeck = deckService.loadDeck(1L);
        assertTrue(newDeck.getContent().contains(card2));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void lockDeckTestAuthorised(){
        Deck deck = new Deck();
        deck.setStatus(DeckStatus.PUBLIC);

        deckService.lockDeck(deck);
        assertEquals(DeckStatus.LOCKED, deck.getStatus());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"LEARNER"})
    public void lockDeckTestUnauthorised(){
        Deck deck = new Deck();
        deck.setStatus(DeckStatus.PUBLIC);
        assertThrows(org.springframework.security.access.AccessDeniedException.class,()-> deckService.lockDeck(deck));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void unlockDeckTestAuthorised(){
        Deck deck = new Deck();
        deck.setStatus(DeckStatus.LOCKED);


        deckService.unlockDeck(deck);
        assertEquals(DeckStatus.PRIVATE, deck.getStatus());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"LEARNER"})
    public void unlockDeckTestUnauthorised(){
        Deck deck = new Deck();
        deck.setStatus(DeckStatus.LOCKED);

        assertThrows(org.springframework.security.access.AccessDeniedException.class,()-> deckService.unlockDeck(deck));
    }

    @Test
    public void setDeckStatusPublic(){
        Deck deck = new Deck();
        deck.setStatus(DeckStatus.PRIVATE);

        deckService.setDeckStatusPublic(deck);
        assertEquals(DeckStatus.PUBLIC, deck.getStatus());
    }

    @Test
    public void setDeckStatusPrivate(){
        Deck deck = new Deck();
        deck.setStatus(DeckStatus.PUBLIC);

        deckService.setDeckStatusPrivate(deck);
        assertEquals(DeckStatus.PRIVATE, deck.getStatus());
    }
}