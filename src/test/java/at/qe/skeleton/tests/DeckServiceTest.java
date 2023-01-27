package at.qe.skeleton.tests;

import at.qe.skeleton.model.Card;
import at.qe.skeleton.model.Deck;
import at.qe.skeleton.model.DeckStatus;
import at.qe.skeleton.model.User;
import at.qe.skeleton.repositories.CardRepository;
import at.qe.skeleton.repositories.DeckRepository;
import at.qe.skeleton.services.DeckService;
import at.qe.skeleton.services.UserService;
import at.qe.skeleton.ui.beans.SessionInfoBean;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
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
    CardRepository cardRepository;
    
    @Autowired
    UserService userService;

    @Autowired
    SessionInfoBean sessionInfoBean;

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void testDeckInitialization() {
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
    void loadDeckBySearch(){
        String e = "e";
        List<Deck> containgLetterE = deckService.getAllDecks().stream().filter(u -> u.getStatus().equals(DeckStatus.PUBLIC)).filter(u -> u.getTitle().toLowerCase().contains("e")).toList();
        assertEquals(containgLetterE, deckService.loadDecksBySearch("e"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void getAllDecks(){
        assertEquals(deckRepository.findAll().size(), deckService.getAllDecks().size());
    }

    @DirtiesContext
    @Test
    
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void deleteDeckWithAuthorisedUser() {
        User copiedUser = userService.loadUser("elvis");
        Deck toBeDeletedDeck = deckService.loadDeck(2L);
        assertEquals(copiedUser, toBeDeletedDeck.getCreator(), "user is not the creator of the proposed deck");

        assertEquals(5, deckService.getAllDecks().size());
        deckService.deleteDeck(toBeDeletedDeck, copiedUser);
        assertEquals(4, deckService.getAllDecks().size(), "deck has not been deleted");

        assertFalse(deckService.getAllDecks().contains(toBeDeletedDeck));
    }
    @DirtiesContext
    @Test
    
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void deleteDeckWithUnauthorisedUser() {
        User unauthorised = userService.loadUser("user1");
        Deck toBeDeletedDeck = deckService.loadDeck(2L);

        assertEquals(5, deckService.getAllDecks().size());
        deckService.deleteDeck(toBeDeletedDeck, unauthorised);
        assertEquals(5, deckService.getAllDecks().size(), "There are missing Decks");

        assertTrue(deckService.getAllDecks().contains(toBeDeletedDeck));
    }

    @DirtiesContext
    @Test
    
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void deleteDeckWithAdmin() {
        User adminUser = userService.loadUser("admin");
        Deck toBeDeletedDeck = deckService.loadDeck(2L);

        assertEquals(5, deckService.getAllDecks().size());
        deckService.deleteDeck(toBeDeletedDeck, adminUser);
        assertEquals(4, deckService.getAllDecks().size(), "There are missing Decks");

        assertFalse(deckService.getAllDecks().contains(toBeDeletedDeck));
    }


    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    void loadPublicDecks() {
        User copiedUser = new User();
        copiedUser.setUsername("testUser");
        userService.saveUser(copiedUser);

        assertEquals(deckRepository.findAllByStatusIsPublic().size(), deckService.loadAllForeignPublicDecks(copiedUser).size());
    }

    @Test
    
    void loadDecksWithTitle() {
        Deck copiedDeck = deckService.loadDeck(1L);
        assertEquals(List.of(copiedDeck), deckService.loadDecksWithTitle("EN"));
    }

    @Test
    
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void loadOwnDecks() {
        User copiedUser = userService.loadUser("user2");
        Deck copiedDeck2 = deckService.loadDeck(3L);
        assertEquals(List.of(copiedDeck2), deckService.loadOwnDecks(copiedUser));
    }

    @Test
    
    @WithMockUser(username = "user1", authorities = {"LEARNER"})
    void addCardToDeckException() {
        Deck deck = deckService.loadDeck(1L);
        Card card = new Card();
        card.setId(2L);

        assertThrows(InstanceAlreadyExistsException.class, () -> deckService.addCardToDeck(card, deck));
    }

    @Test
    
    @WithMockUser(username = "user1", authorities = {"LEARNER"})
    void addCardToDeck() throws InstanceAlreadyExistsException {
        Deck deck = deckService.loadDeck(1L);
        int oldDeckSize = deck.getContent().size();

        Card card2 = new Card();
        deckService.addCardToDeck(card2, deck);
        deckService.saveDeck(deck);
        Deck newDeck = deckService.loadDeck(1L);
        assertEquals(oldDeckSize + 1 , newDeck.getContent().size());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void lockDeckTestAuthorised(){
        Deck deck = new Deck();
        // Deck locking results in an email to the creator, therefore we need a creator and a title
        deck.setCreator(new User(
                "Name",
                "passwd",
                "fstName",
                "lastName",
                "test@mail.com",
                true,
                null));
        deck.setTitle("DeckTitle");
        deck.setStatus(DeckStatus.PUBLIC);

        deckService.lockDeck(deck);
        assertEquals(DeckStatus.LOCKED, deck.getStatus());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void lockDeckNotLocked(){
        Deck deck = deckService.getAllDecks().stream().filter(u -> u.getStatus().equals(DeckStatus.PRIVATE)).toList().get(0);

        assertThrows(IllegalStateException.class, ()->deckService.lockDeck(deck));
    }

    @Test
    @WithMockUser(username = "user", authorities = {"LEARNER"})
    void lockDeckUnauthorised(){
        Deck deck = new Deck();
        deck.setStatus(DeckStatus.PUBLIC);
        assertThrows(org.springframework.security.access.AccessDeniedException.class,()-> deckService.lockDeck(deck));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void unlockDeckAuthorised(){
        Deck deck = new Deck();
        deck.setStatus(DeckStatus.LOCKED);

        deckService.unlockDeck(deck);
        assertEquals(DeckStatus.PUBLIC, deck.getStatus());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"LEARNER"})
    void unlockDeckUnauthorised(){
        Deck deck = new Deck();
        deck.setStatus(DeckStatus.LOCKED);

        assertThrows(org.springframework.security.access.AccessDeniedException.class,()-> deckService.unlockDeck(deck));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void unlockDeckNotLocked(){
        Deck deck = deckService.getAllDecks().get(0);

        assertThrows(IllegalStateException.class,()-> deckService.unlockDeck(deck));
    }


    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void setDeckStatusPublic(){
        Deck deck = deckService.getAllDecks().stream().filter(u -> u.getStatus().equals(DeckStatus.PRIVATE)).toList().get(0);

        deckService.setDeckStatusPublic(deck);
        assertEquals(DeckStatus.PUBLIC, deck.getStatus());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void setDeckStatusPublicAlreadyPublic(){
        Deck deck = deckService.getAllDecks().stream().filter(u -> u.getStatus().equals(DeckStatus.PUBLIC)).toList().get(0);

        assertThrows(IllegalStateException.class, ()->deckService.setDeckStatusPublic(deck));

    }

    @Test
    void setDeckStatusPrivate(){
        Deck deck = new Deck();
        deck.setStatus(DeckStatus.PUBLIC);

        deckService.setDeckStatusPrivate(deck);
        assertEquals(DeckStatus.PRIVATE, deck.getStatus());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void setDeckStatusPrivateAlreadyPrivate(){
        Deck deck = deckService.getAllDecks().stream().filter(u -> u.getStatus().equals(DeckStatus.PRIVATE)).toList().get(0);

        assertThrows(IllegalStateException.class, ()->deckService.setDeckStatusPrivate(deck));
    }
}
