package at.qe.skeleton.tests;


import at.qe.skeleton.model.Deck;
import at.qe.skeleton.model.User;
import at.qe.skeleton.services.DeckService;
import at.qe.skeleton.services.UserService;
import at.qe.skeleton.ui.beans.SessionInfoBean;
import at.qe.skeleton.ui.controllers.UserBookmarkController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@WebAppConfiguration
public class UserBookmarkControllerTest {

    UserBookmarkController userBookmarkController;

    @Autowired
    UserService userService;
    @Autowired
    DeckService deckService;

    @Mock
    SessionInfoBean mockedSessionInfoBean;
    @InjectMocks
    UserBookmarkController mockedUserBookmarkController;

    @BeforeEach
    public void initTest() {
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DirtiesContext
    void testInitSetsCorrectUser() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        User user = userService.getAllUsers().iterator().next();
        setUserServiceAndInitMockedControllerWithUser(user);
        Field fieldCurrentUser = Class.forName("at.qe.skeleton.ui.controllers.UserBookmarkController").getDeclaredField("currentUser");
        fieldCurrentUser.setAccessible(true);
        assertEquals(user, fieldCurrentUser.get(mockedUserBookmarkController));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DirtiesContext
    void testDoGetBookmarksFromUser() {
        Iterator<User> userIterator = userService.getAllUsers().iterator();
        User user = null;
        boolean found = false;
        while (userIterator.hasNext() && !found) {
            user = userIterator.next();
            if (!user.getBookmarks().isEmpty()) {
                found = true;
            }
        }
        if (!found) throw new IllegalStateException("No user with at least one bookmark found.");
        setUserServiceAndInitMockedControllerWithUser(user);
        assertTrue(user.getBookmarks().containsAll(mockedUserBookmarkController.doGetBookmarks()));
        assertEquals(user.getBookmarks().size(), mockedUserBookmarkController.doGetBookmarks().size());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DirtiesContext
    void testDoDeleteBookmarkFromUser() {
        userBookmarkController = new UserBookmarkController();
        Iterator<User> userIterator = userService.getAllUsers().iterator();
        User user = null;
        boolean found = false;
        while (userIterator.hasNext() && !found) {
            user = userIterator.next();
            if (!user.getBookmarks().isEmpty()) {
                found = true;
            }
        }
        if (!found) throw new IllegalStateException("No user with at least one bookmark found.");
        try {
            Field fieldCurrentUser = Class.forName("at.qe.skeleton.ui.controllers.UserBookmarkController").getDeclaredField("currentUser");
            Field fieldUserService = Class.forName("at.qe.skeleton.ui.controllers.UserBookmarkController").getDeclaredField("userService");
            fieldCurrentUser.setAccessible(true);
            fieldUserService.setAccessible(true);
            fieldCurrentUser.set(userBookmarkController, user);
            fieldUserService.set(userBookmarkController, userService);
        } catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
        }
        List<Deck> deckList = new ArrayList<>(userBookmarkController.doGetBookmarks());
        Deck toDelete = deckList.get(0);
        userBookmarkController.doDeleteBookmark(toDelete);
        assertEquals(deckList.size() - 1, userBookmarkController.doGetBookmarks().size());
        assertTrue(deckList.containsAll(userBookmarkController.doGetBookmarks()));
        assertFalse(userBookmarkController.doGetBookmarks().contains(toDelete));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DirtiesContext
    void testDoAddBookmark() {
        userBookmarkController = new UserBookmarkController();
        // Get a user with at least 2 decks
        Iterator<User> userIterator = userService.getAllUsers().iterator();
        User user = null;
        Deck newDeck = null;
        boolean found = false;
        while (userIterator.hasNext() && !found) {
            user = userIterator.next();
            if (user.getBookmarks().size() > 1) {
                Iterator<Deck> deckIterator = deckService.getAllDecks().iterator();
                while (deckIterator.hasNext() && !found) {
                    newDeck = deckIterator.next();
                    if (!user.getBookmarks().contains(newDeck)) {
                        found = true;
                    }
                }
            }
        }
        if (!found) throw new IllegalStateException("No user with at least two bookmarks who has none other deck not bookmarked.");
        try {
            Field fieldCurrentUser = Class.forName("at.qe.skeleton.ui.controllers.UserBookmarkController").getDeclaredField("currentUser");
            Field fieldUserService = Class.forName("at.qe.skeleton.ui.controllers.UserBookmarkController").getDeclaredField("userService");
            fieldCurrentUser.setAccessible(true);
            fieldUserService.setAccessible(true);
            fieldCurrentUser.set(userBookmarkController, user);
            fieldUserService.set(userBookmarkController, userService);
        } catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
        }
        List<Deck> deckList = new ArrayList<>(user.getBookmarks());
        userBookmarkController.doAddBookmark(newDeck);
        assertEquals(deckList.size() + 1, user.getBookmarks().size());
        assertTrue(user.getBookmarks().containsAll(deckList));
        assertTrue(user.getBookmarks().contains(newDeck));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DirtiesContext
    void testCheckIsBookmarked() {
        userBookmarkController = new UserBookmarkController();
        Iterator<User> userIterator = userService.getAllUsers().iterator();
        User user = null;
        boolean found = false;
        while (userIterator.hasNext() && !found) {
            user = userIterator.next();
            if (user.getBookmarks().size() > 1) {
                found = true;
            }
        }
        if (!found) throw new IllegalStateException("No user with at least two bookmarks found.");
        try {
            Field fieldCurrentUser = Class.forName("at.qe.skeleton.ui.controllers.UserBookmarkController").getDeclaredField("currentUser");
            Field fieldUserService = Class.forName("at.qe.skeleton.ui.controllers.UserBookmarkController").getDeclaredField("userService");
            fieldCurrentUser.setAccessible(true);
            fieldUserService.setAccessible(true);
            fieldCurrentUser.set(userBookmarkController, user);
            fieldUserService.set(userBookmarkController, userService);
        } catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
        }
        List<Deck> deckList = new ArrayList<>(userBookmarkController.doGetBookmarks());
        assertAll(() -> {
            for (Deck deck : deckList) {
                assertTrue(userBookmarkController.isBookmarked(deck));
            }
        });
        assertEquals(deckList.size(), userBookmarkController.doGetBookmarks().size());
        assertTrue(deckList.containsAll(userBookmarkController.doGetBookmarks()));
    }

    // ---------- functions ---------- //

    void setUserServiceAndInitMockedControllerWithUser(User user) {
        Mockito.when(mockedSessionInfoBean.getCurrentUserName()).thenReturn(user.getUsername());
        Field fieldUserService = null;
        try {
            fieldUserService = Class.forName("at.qe.skeleton.ui.controllers.UserBookmarkController").getDeclaredField("userService");
            fieldUserService.setAccessible(true);
            fieldUserService.set(mockedUserBookmarkController, this.userService);
        } catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
        }
        mockedUserBookmarkController.init();
    }
}
