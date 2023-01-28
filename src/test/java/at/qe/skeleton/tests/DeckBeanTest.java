package at.qe.skeleton.tests;

import at.qe.skeleton.model.Deck;
import at.qe.skeleton.model.DeckStatus;
import at.qe.skeleton.model.User;
import at.qe.skeleton.services.DeckService;
import at.qe.skeleton.services.UserService;
import at.qe.skeleton.ui.beans.DeckBean;
import at.qe.skeleton.ui.beans.SessionInfoBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link DeckBean}
 * <p>
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 */

@SpringBootTest
@WebAppConfiguration
public class DeckBeanTest {

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
    private DeckBean mockedDeckBean;

    DeckBean deckBean = new DeckBean();

    Deck deck1;
    Deck deck2;
    Deck deck3;
    Deck deck4;

    @BeforeEach
    public void init() {
        deck1 = new Deck();
        deck2 = new Deck();
        deck3 = new Deck();
        deck4 = new Deck();

        deck1.setTitle("spanish");
        deck1.setDescription("Spanish Vocabulary for Beginners.");
        deck1.setStatus(DeckStatus.PUBLIC);

        deck2.setTitle("german");
        deck2.setDescription("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");
        deck2.setStatus(DeckStatus.PRIVATE);

        deck3.setTitle("To see for everyone");
        deck3.setDescription("Here could stand your advertisement");
        deck3.setStatus(DeckStatus.PUBLIC);

        deck4.setTitle("Should absolutely not be seen");
        deck4.setDescription("Is here you will find the most private stuff one could just have");
        deck4.setStatus(DeckStatus.PRIVATE);

    }

    @Test
    @DisplayName("Global Filter Function returns True when Filter is null or empty")
    public void globalFilterNull() {
        assertTrue(deckBean.globalFilterFunction(deck1, null, null));
        assertTrue(deckBean.globalFilterFunction(deck2, null, null));
        assertTrue(deckBean.globalFilterFunction(null, null, null));

        assertTrue(deckBean.globalFilterFunction(deck1, "", null));
        assertTrue(deckBean.globalFilterFunction(deck2, "", null));
        assertTrue(deckBean.globalFilterFunction(null, "", null));
    }

    @Test
    @DisplayName("Global Filter Function filters name correctly")
    public void globalFilterName() {
        deck1.setDescription("");
        deck2.setDescription("");
        assertTrue(deckBean.globalFilterFunction(deck1, "spanish", null));
        assertFalse(deckBean.globalFilterFunction(deck2, "spanish", null));
        assertTrue(deckBean.globalFilterFunction(deck1, "i", null));
        assertFalse(deckBean.globalFilterFunction(deck2, "i", null));
        assertTrue(deckBean.globalFilterFunction(deck2, "man", null));
        assertFalse(deckBean.globalFilterFunction(deck1, "man", null));
    }

    @Test
    @DisplayName("Global Filter Function filters description correctly")
    public void globalFilterDescription() {
        deck1.setTitle("");
        deck2.setTitle("");
        assertTrue(deckBean.globalFilterFunction(deck1, "vocab", null));
        assertFalse(deckBean.globalFilterFunction(deck2, "vocab", null));
        assertTrue(deckBean.globalFilterFunction(deck1, "BEGINNERS", null));
        assertFalse(deckBean.globalFilterFunction(deck2, "BEGINNERS", null));
        assertFalse(deckBean.globalFilterFunction(deck1, "ipsum dolor", null));
        assertTrue(deckBean.globalFilterFunction(deck2, "ipsum dolor", null));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testSettingAndGettingPersonalDeck() {
        deckBean.setPersonalDecks(List.of(deck1, deck2, deck3, deck4));
        assertTrue(deckBean.getPersonalDecks().contains(deck1));
        assertTrue(deckBean.getPersonalDecks().contains(deck2));
        assertTrue(deckBean.getPersonalDecks().contains(deck3));
        assertTrue(deckBean.getPersonalDecks().contains(deck4));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testSettingAndGettingSavedDecks() {
        deckBean.setSavedDecks(List.of(deck1, deck2, deck3, deck4));
        assertTrue(deckBean.getSavedDecks().contains(deck1));
        assertTrue(deckBean.getSavedDecks().contains(deck2));
        assertTrue(deckBean.getSavedDecks().contains(deck3));
        assertTrue(deckBean.getSavedDecks().contains(deck4));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testFilterOnPersonalDecks() {
        deckBean.setFilteredPersonalDecks(List.of(deck1, deck2, deck3, deck4));
        assertTrue(deckBean.getFilteredPersonalDecks().contains(deck1));
        assertTrue(deckBean.getFilteredPersonalDecks().contains(deck2));
        assertTrue(deckBean.getFilteredPersonalDecks().contains(deck3));
        assertTrue(deckBean.getFilteredPersonalDecks().contains(deck4));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testFilterOnSavedDecks() {
        deckBean.setFilteredSavedDecks(List.of(deck1, deck2, deck3, deck4));
        assertTrue(deckBean.getFilteredSavedDecks().contains(deck1));
        assertTrue(deckBean.getFilteredSavedDecks().contains(deck2));
        assertTrue(deckBean.getFilteredSavedDecks().contains(deck3));
        assertTrue(deckBean.getFilteredSavedDecks().contains(deck4));
    }


    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testDeleteBookmark() {
        // Get first user that has bookmarks
        User user = null;
        var userIterator = userService.getAllUsers().iterator();
        boolean found = false;
        while (userIterator.hasNext() && !found) {
            user = userIterator.next();
            if (user.getBookmarks().size() > 1) found = true;
        }
        if (!found) throw new IllegalStateException("No user has more than 1 bookmarks");

        when(mockedUserService.loadUser(mockedSessionInfoBean.getCurrentUserName())).thenReturn(user);
        Iterator<Deck> deckIterator = user.getBookmarks().iterator();
        Deck bookmark1 = deckIterator.next();
        Deck bookmark2 = deckIterator.next();
        deck1.setId(1L);
        mockedDeckBean.setSavedDecks(new ArrayList<>(List.of(bookmark1, bookmark2)));


        mockedDeckBean.setSavedDecks(new ArrayList<>(List.of(bookmark1, bookmark2)));

        assertTrue(mockedDeckBean.getSavedDecks().contains(bookmark1));
        assertTrue(mockedDeckBean.getSavedDecks().contains(bookmark2));
        mockedDeckBean.deleteBookmark(bookmark1);
        assertTrue(mockedDeckBean.getSavedDecks().contains(bookmark2));
        assertFalse(mockedDeckBean.getSavedDecks().contains(bookmark1));

    }


    @Test
    public void testDeleteDeck() {
        // Create a mock user and set it in the sessionInfoBean
        User testUser = new User();
        when(mockedSessionInfoBean.getCurrentUser()).thenReturn(testUser);
        deck1.setId(0L);
        deck2.setId(1L);
        deck3.setId(2L);
        deck4.setId(3L);


        // Create a test deck
        List<Deck> deckList = new ArrayList<>(List.of(deck1, deck2, deck3, deck4));
        mockedDeckBean.setPersonalDecks(deckList);
        assertTrue(mockedDeckBean.getPersonalDecks().contains(deck1));
        assertTrue(mockedDeckBean.getPersonalDecks().contains(deck2));
        assertTrue(mockedDeckBean.getPersonalDecks().contains(deck3));
        assertTrue(mockedDeckBean.getPersonalDecks().contains(deck4));
        mockedDeckBean.deleteDeck(deck2);
        assertTrue(mockedDeckBean.getPersonalDecks().contains(deck1));
        assertTrue(mockedDeckBean.getPersonalDecks().contains(deck3));
        assertTrue(mockedDeckBean.getPersonalDecks().contains(deck4));

        assertFalse(mockedDeckBean.getPersonalDecks().contains(deck2));
    }

    @Test
    public void testMakeDeckPublicFromPrivate() {
        when(mockedDeckService.saveDeck(any(Deck.class))).thenReturn(deck1);
        doAnswer(invocationOnMock -> {
            Deck deck = invocationOnMock.getArgument(0);
            deckService.setDeckStatusPublic(deck);
            return null;
        }).when(mockedDeckService).setDeckStatusPublic(any(Deck.class));
        deck1.setStatus(DeckStatus.PRIVATE);
        assertEquals(DeckStatus.PRIVATE, deck1.getStatus());
        mockedDeckBean.makeDeckPublic(deck1);
        assertEquals(DeckStatus.PUBLIC, deck1.getStatus());
    }

    @Test
    public void testMakeDeckPublicFromLocked() {
        when(mockedDeckService.saveDeck(any(Deck.class))).thenReturn(deck1);
        doAnswer(invocationOnMock -> {
            Deck deck = invocationOnMock.getArgument(0);
            deckService.setDeckStatusPublic(deck);
            return null;
        }).when(mockedDeckService).setDeckStatusPublic(any(Deck.class));
        deck1.setStatus(DeckStatus.LOCKED);
        assertEquals(DeckStatus.LOCKED, deck1.getStatus());
        assertThrows(IllegalStateException.class, () -> mockedDeckBean.makeDeckPublic(deck1));
    }

    @Test
    public void testMakeDeckPublicFromPublic() {
        when(mockedDeckService.saveDeck(any(Deck.class))).thenReturn(deck1);
        doAnswer(invocationOnMock -> {
            Deck deck = invocationOnMock.getArgument(0);
            deckService.setDeckStatusPublic(deck);
            return null;
        }).when(mockedDeckService).setDeckStatusPublic(any(Deck.class));
        deck1.setStatus(DeckStatus.PUBLIC);
        assertEquals(DeckStatus.PUBLIC, deck1.getStatus());
        assertThrows(IllegalStateException.class, () -> mockedDeckBean.makeDeckPublic(deck1));
    }

    @Test
    public void testMakeDeckPrivateFromPrivate() {
        when(mockedDeckService.saveDeck(any(Deck.class))).thenReturn(deck1);
        doAnswer(invocationOnMock -> {
            Deck deck = invocationOnMock.getArgument(0);
            deckService.setDeckStatusPrivate(deck);
            return null;
        }).when(mockedDeckService).setDeckStatusPrivate(any(Deck.class));
        deck1.setStatus(DeckStatus.PRIVATE);
        assertEquals(DeckStatus.PRIVATE, deck1.getStatus());
        assertThrows(IllegalStateException.class, () -> mockedDeckBean.makeDeckPrivate(deck1));
    }

    @Test
    public void testMakeDeckPrivateFromLocked() {
        when(mockedDeckService.saveDeck(any(Deck.class))).thenReturn(deck1);
        doAnswer(invocationOnMock -> {
            Deck deck = invocationOnMock.getArgument(0);
            deckService.setDeckStatusPrivate(deck);
            return null;
        }).when(mockedDeckService).setDeckStatusPrivate(any(Deck.class));
        deck1.setStatus(DeckStatus.LOCKED);
        assertEquals(DeckStatus.LOCKED, deck1.getStatus());
        assertThrows(IllegalStateException.class, () -> mockedDeckBean.makeDeckPrivate(deck1));
    }

    @Test
    public void testMakeDeckPrivateFromPublic() {
        when(mockedDeckService.saveDeck(any(Deck.class))).thenReturn(deck1);
        doAnswer(invocationOnMock -> {
            Deck deck = invocationOnMock.getArgument(0);
            deckService.setDeckStatusPrivate(deck);
            return null;
        }).when(mockedDeckService).setDeckStatusPrivate(any(Deck.class));
        deck1.setStatus(DeckStatus.PUBLIC);
        assertEquals(DeckStatus.PUBLIC, deck1.getStatus());
        mockedDeckBean.makeDeckPrivate(deck1);
        assertEquals(DeckStatus.PRIVATE, deck1.getStatus());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testBeanInit() {
        Iterator<User> userIterator = userService.getAllUsers().iterator();
        boolean found = false;
        User user = null;
        while (userIterator.hasNext() && !found) {
            user = userIterator.next();
            if (user.getCreatedDecks().size() > 0 && user.getBookmarks().size() > 0) {
                found = true;
            }
        }
        if (!found) throw new IllegalStateException("No user with enough decks");
        when(mockedSessionInfoBean.getCurrentUserName()).thenReturn(user.getUsername());
        when(mockedUserService.loadUser(any(String.class)))
                .thenReturn(userService.loadUser(user.getUsername()));
        mockedDeckBean.init();
        assertIterableEquals(mockedDeckService.getCreatedDecks(user), mockedDeckBean.getPersonalDecks());
        assertIterableEquals(mockedDeckService.getBookmarkedDecks(user), mockedDeckBean.getSavedDecks());
    }
}






