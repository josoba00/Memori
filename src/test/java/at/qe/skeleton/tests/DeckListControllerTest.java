package at.qe.skeleton.tests;

import at.qe.skeleton.model.Deck;
import at.qe.skeleton.services.DeckService;
import at.qe.skeleton.services.UserService;
import at.qe.skeleton.ui.controllers.DeckListController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@WebAppConfiguration
public class DeckListControllerTest {
    DeckListController deckListController;
    @Autowired
    DeckService deckService;

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DirtiesContext
    void testSearchDecksOnDecklistNULL() {
        deckListController = new DeckListController();
        try {
            Field fieldDeckList = Class.forName("at.qe.skeleton.ui.controllers.DeckListController").getDeclaredField("deckList");
            Field fieldDeckService = Class.forName("at.qe.skeleton.ui.controllers.DeckListController").getDeclaredField("deckService");
            fieldDeckList.setAccessible(true);
            fieldDeckService.setAccessible(true);
            fieldDeckList.set(deckListController, null);
            fieldDeckService.set(deckListController, this.deckService);

        } catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
        }
        String searchString = deckService.getAllDecks().iterator().next().getTitle().substring(0, 2);
        Collection<Deck> deckCollection = deckListController.searchDecks(searchString);
        List<Deck> foundDecks = deckService.loadDecksBySearch(searchString);
        assertTrue(foundDecks.containsAll(deckCollection));
        assertEquals(foundDecks.size(), deckCollection.size());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DirtiesContext
    void testSearchDeckOnDecklistNotNullReturnsStoredList() {
        deckListController = new DeckListController();
        try {
            Field fieldDeckList = Class.forName("at.qe.skeleton.ui.controllers.DeckListController").getDeclaredField("deckList");
            Field fieldDeckService = Class.forName("at.qe.skeleton.ui.controllers.DeckListController").getDeclaredField("deckService");
            fieldDeckList.setAccessible(true);
            fieldDeckService.setAccessible(true);
            fieldDeckList.set(deckListController, null);
            fieldDeckService.set(deckListController, this.deckService);
        } catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
        }

        String searchString = deckService.getAllDecks().iterator().next().getTitle().substring(0, 2);
        Collection<Deck> deckCollection = deckListController.searchDecks(searchString);
        List<Deck> foundDecks = deckService.loadDecksBySearch(searchString);
        // Assert that it was set correctly
        assertTrue(foundDecks.containsAll(deckCollection));
        assertEquals(foundDecks.size(), deckCollection.size());

        searchString = "WillNotBeFound.............-..............-..............";
        deckCollection = deckListController.searchDecks(searchString);
        assertTrue(foundDecks.containsAll(deckCollection));
        assertEquals(foundDecks.size(), deckCollection.size());
    }
}
