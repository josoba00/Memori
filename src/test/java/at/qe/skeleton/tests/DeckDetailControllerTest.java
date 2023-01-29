package at.qe.skeleton.tests;


import at.qe.skeleton.model.Card;
import at.qe.skeleton.model.Deck;
import at.qe.skeleton.model.DeckStatus;
import at.qe.skeleton.model.User;
import at.qe.skeleton.services.DeckService;
import at.qe.skeleton.services.UserService;
import at.qe.skeleton.ui.beans.SessionInfoBean;
import at.qe.skeleton.ui.controllers.DeckDetailController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;

import java.lang.reflect.Field;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@WebAppConfiguration
public class DeckDetailControllerTest {

    DeckDetailController deckDetailController;
    @Autowired
    private UserService userService;
    @Autowired
    private DeckService deckService;

    @Mock
    private DeckService mockedDeckService;
    @Mock
    private SessionInfoBean mockedSessionInfoBean;
    @Mock
    private UserService mockedUserService;
    @InjectMocks
    private DeckDetailController mockedDeckDetailController;

    @BeforeEach
    public void setup() {
        deckDetailController = new DeckDetailController();
    }

    @Test
    @DirtiesContext
    public void testSetAndGetDeck() {
        Deck deck = new Deck();
        deckDetailController.setDeck(deck);
        assertEquals(deck, deckDetailController.getDeck());
    }

    @Test
    @DirtiesContext
    public void testSetAndGetCards() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(new Card());
        cardList.add(new Card());
        cardList.add(new Card());
        cardList.add(new Card());
        deckDetailController.setCards(cardList);
        assertEquals(cardList, deckDetailController.getCards());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DirtiesContext
    public void testLoadDeckOnIdNULL() {
        User user = userService.getAllUsers().iterator().next();
        when(mockedSessionInfoBean.getCurrentUserName()).thenReturn(user.getUsername());
        when(mockedUserService.loadUser(any(String.class))).then(invocationOnMock -> {
            String name = invocationOnMock.getArgument(0);
            return userService.loadUser(name);
        });
        mockedDeckDetailController.loadDeck(null);
        assertEquals(user, mockedDeckDetailController.getDeck().getCreator());
        assertEquals(DeckStatus.PRIVATE, mockedDeckDetailController.getDeck().getStatus());
        assertTrue(mockedDeckDetailController.getDeck().getCreator().getCreatedDecks().contains(mockedDeckDetailController.getDeck()));
        assertEquals(0, mockedDeckDetailController.getDeck().getContent().size());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DirtiesContext
    public void testLoadDeckOnIdNotNULL() {
        Deck deck = getDeckWithMinNumOfCards();
        when(mockedDeckService.loadDeck(any(Long.class))).then(invocationOnMock -> {
            Long id = invocationOnMock.getArgument(0);
            return deckService.loadDeck(id);
        });

        mockedDeckDetailController.loadDeck(deck.getId());
        assertEquals(deck, mockedDeckDetailController.getDeck());
        final Deck finalDeck = deck;
        assertAll(() -> {
            for (Card card : finalDeck.getContent()) {
                assertTrue(mockedDeckDetailController.getCards().contains(card));
            }
        });
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DirtiesContext
    public void testDoSaveDeck() {
        try {
        Field field = Class.forName("at.qe.skeleton.ui.controllers.DeckDetailController")
                .getDeclaredField("deckService");
        field.setAccessible(true);
        field.set(deckDetailController, deckService);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        Deck deck = getDeckWithMinNumOfCards();

        deckDetailController.loadDeck(deck.getId());
        List<Card> cardList = deckDetailController.getDeck().getContent();
        int size = cardList.size();
        cardList.remove(0);
        deckDetailController.doSaveDeck();
        assertEquals(size - 1, deckService.loadDeck(deck.getId()).getContent().size());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DirtiesContext
    public void testAddCardContainsAllPreviousCards() {
        Deck deck = getDeckWithMinNumOfCards();
        when(mockedDeckService.loadDeck(any(Long.class))).then(invocationOnMock -> {
            Long id = invocationOnMock.getArgument(0);
            return deckService.loadDeck(id);
        });
        mockedDeckDetailController.loadDeck(deck.getId());
        final List<Card> oldCardList = new ArrayList<>(mockedDeckDetailController.getCards());
        mockedDeckDetailController.addCard();
        List<Card> newCardList = mockedDeckDetailController.getCards();
        assertAll(() -> {
            for (Card card : oldCardList) {
                assertTrue(newCardList.contains(card));
            }
        });
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DirtiesContext
    public void testAddCardIncreasesSizeByOne() {
        Deck deck = getDeckWithMinNumOfCards();
        when(mockedDeckService.loadDeck(any(Long.class))).then(invocationOnMock -> {
            Long id = invocationOnMock.getArgument(0);
            return deckService.loadDeck(id);
        });
        mockedDeckDetailController.loadDeck(deck.getId());
        int oldSize = mockedDeckDetailController.getCards().size();
        mockedDeckDetailController.addCard();
        assertEquals(oldSize + 1, mockedDeckDetailController.getCards().size());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DirtiesContext
    public void testRemoveCard() {
        when(mockedDeckService.loadDeck(any(Long.class))).then(invocationOnMock -> {
            Long id = invocationOnMock.getArgument(0);
            return deckService.loadDeck(id);
        });

        Deck deck = getDeckWithMinNumOfCards();
        mockedDeckDetailController.loadDeck(deck.getId());
        final List<Card> oldCardList = new ArrayList<>(deck.getContent());
        Random random = new Random();
        Card toDelete = deck.getContent().get(random.nextInt(deck.getContent().size()));
        mockedDeckDetailController.removeCard(toDelete);
        final List<Card> newCardList = mockedDeckDetailController.getCards();

        assertAll(() -> {
            for (Card card : newCardList) {
                assertTrue(oldCardList.contains(card));
            }
        });
        assertFalse(newCardList.contains(toDelete));
    }

    // ---------- Other functions ---------- //

    public Deck getDeckWithMinNumOfCards() {
        Iterator<Deck> deckIterator = deckService.getAllDecks().iterator();
        boolean found = false;
        Deck deck = null;
        while (deckIterator.hasNext() && !found) {
            deck = deckIterator.next();
            if (deck.getContent().size() > 2) {
                found = true;
            }
        }
        if (!found) {
            throw new IllegalStateException("No deck with more than 2 card found");
        }
        return deck;
    }
}
