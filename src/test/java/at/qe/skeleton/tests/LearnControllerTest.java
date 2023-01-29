package at.qe.skeleton.tests;


import at.qe.skeleton.model.Card;
import at.qe.skeleton.model.Deck;
import at.qe.skeleton.model.User;
import at.qe.skeleton.services.DeckService;
import at.qe.skeleton.services.LearnService;
import at.qe.skeleton.services.UserService;
import at.qe.skeleton.ui.beans.SessionInfoBean;
import at.qe.skeleton.ui.controllers.LearnController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@WebAppConfiguration
public class LearnControllerTest {

    LearnController learnController;

    @Autowired
    UserService userService;
    @Autowired
    LearnService learnService;
    @Autowired
    DeckService deckService;


    @Mock
    SessionInfoBean mockedSessionInfoBean;
    @Mock
    LearnService mockedLearnService;
    @InjectMocks
    LearnController mockedLearnController;

    User testUser = new User();
    Deck testDeck = new Deck();
    Card testCard1 = new Card();
    Card testCard2 = new Card();

    @BeforeEach
    void setUp() {
        testUser.setUsername("testUser");
        learnController = new LearnController();
        ReflectionTestUtils.setField(learnController, "learnService", mockedLearnService);
        ReflectionTestUtils.setField(learnController, "currentUser", testUser);
        ReflectionTestUtils.setField(learnController, "sessionInfoBean", new SessionInfoBean());


        testCard1.setId(-10L);

        testCard2.setId(-11L);

        testDeck.setContent(List.of(testCard1, testCard2));
    }

    @Test
    void initializeQueuesBothEmptyTest() {
        when(mockedLearnService.findNeverLearnedCards(testDeck.getContent(), testUser)).thenReturn(Set.of());
        when(mockedLearnService.findCardsToLearn(testDeck.getContent(), testUser)).thenReturn(Set.of());

        assertEquals(LearnController.InitialisationError.NO_CARDS_TO_LEARN, learnController.doInitializeQueues(testDeck));
    }

    @Test
    void getNextCardTest() {
        ReflectionTestUtils.setField(learnController, "cardsQueue", new LinkedList<>(Set.of(testCard1)));
        ReflectionTestUtils.setField(learnController, "neverLearnedCardsQueue", new LinkedList<>(Set.of(testCard2)));

        assertEquals(testCard1, learnController.doGetNextCard());
        assertEquals(testCard2, learnController.doGetNextCard());
        assertNull(learnController.doGetNextCard());

    }

    @Test
    void hasNextCardTest() {
        assertEquals(LearnController.LearningCardsEnum.NO_NEXT_CARD, learnController.hasNextCard());
        ReflectionTestUtils.setField(learnController, "cardsQueue", new LinkedList<>(Set.of(testCard1)));
        assertEquals(LearnController.LearningCardsEnum.HAS_NEXT_CARD, learnController.hasNextCard());
        ReflectionTestUtils.setField(learnController, "neverLearnedCardsQueue", new LinkedList<>(Set.of(testCard2)));
        assertEquals(LearnController.LearningCardsEnum.HAS_NEXT_CARD, learnController.hasNextCard());
    }

    @Test
    void addCardBackToQueueTest() {
        ReflectionTestUtils.setField(learnController, "cardsQueue", new LinkedList<>(Set.of(testCard1)));
        learnController.setDifficulty(4);
        learnController.doAddCardBackQueue(testCard2);
        assertEquals(1, learnController.getCardsToLearnAmount());
        learnController.setDifficulty(3);
        learnController.doAddCardBackQueue(testCard2);
        assertEquals(2, learnController.getCardsToLearnAmount());
    }

    @Test
    void getCardsToLearnAmountTest() {
        ReflectionTestUtils.setField(learnController, "cardsQueue", new LinkedList<>(Set.of(testCard1, testCard2)));
        assertEquals(2, learnController.getCardsToLearnAmount());
    }

    @Test
    void getNeverLearnedCardsAmountTest() {
        ReflectionTestUtils.setField(learnController, "neverLearnedCardsQueue", new LinkedList<>(Set.of(testCard1, testCard2)));
        assertEquals(2, learnController.getNeverLearnedCardsAmount());
    }

    @Test
    void getTotalAmountCardsTest() {
        ReflectionTestUtils.setField(learnController, "neverLearnedCardsQueue", new LinkedList<>(Set.of(testCard1, testCard2)));
        ReflectionTestUtils.setField(learnController, "cardsQueue", new LinkedList<>(Set.of(testCard1)));
        assertEquals(3, learnController.getTotalAmountCards());

    }

    @ParameterizedTest
    @CsvSource({"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"})
    void getDifficultyReturnsStoredDifficulty(int setTo) {
        learnController.setDifficulty(setTo);
        assertEquals(setTo, learnController.getDifficulty());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testDoInitializeQueuesOnQueuesNotEmpty() {
        Iterator<User> userIterator = userService.getAllUsers().iterator();
        User user = null;
        Deck deck = null;
        boolean found = false;
        while (userIterator.hasNext() && !found) {
            user = userIterator.next();
            Iterator<Deck> deckIterator = user.getBookmarks().iterator();
            while (deckIterator.hasNext() && !found)
            {
                deck = deckIterator.next();
                if (learnService.findCardsToLearn(deck.getContent(), user).size() > 0){
                    found = true;
                }
            }
        }
        if (!found) throw new IllegalStateException("No user has a non empty queue of cards to learn.");

        when(mockedSessionInfoBean.getCurrentUser()).thenReturn(user);
        when(mockedLearnService.findNeverLearnedCards(any(List.class), any(User.class))).then(invocationOnMock -> {
            List<Card> list = invocationOnMock.getArgument(0);
            User userFindCards = invocationOnMock.getArgument(1);
            return learnService.findNeverLearnedCards(list, userFindCards);
        });
        assertEquals(LearnController.InitialisationError.SUCCESS, mockedLearnController.doInitializeQueues(deck));
    }

    @Test
    public void testDoNextCardChangesCurrentlyDisplayedCard(){
        String displayedText = learnController.getCurrentlyDisplayedText();
        learnController.doNext();
        assertNotEquals(displayedText, learnController.getCurrentlyDisplayedText());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testDoGetOtherSideOfCard(){
        Iterator<Deck> deckIterator = deckService.getAllDecks().iterator();
        Card foundCard = null;
        boolean found = false;
        while(deckIterator.hasNext() && !found){
        Deck deck = deckIterator.next();
            if(deck.getContent().size() > 1){
                found = true;
                foundCard = deck.getContent().get(0);
            }
        }
        try {
            Field field = Class.forName("at.qe.skeleton.ui.controllers.LearnController").getDeclaredField("currentlyDisplayedCard");
            field.setAccessible(true);
            field.set(learnController, foundCard);
        } catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
        }
        // Turn once to get any content
        learnController.doGetOtherSideOfCard();
        String firstSide = learnController.getCurrentlyDisplayedText();
        learnController.doGetOtherSideOfCard();
        String backSide = learnController.getCurrentlyDisplayedText();
        learnController.doGetOtherSideOfCard();
        String firstSideAgain = learnController.getCurrentlyDisplayedText();
        assertEquals(firstSide, firstSideAgain);
        assertNotEquals(firstSide, backSide);
    }

    @Test
    public void testSetAndGetLearningdeck(){
        Deck deck = new Deck();
        deck.setId(152475632L);
        learnController.setLearningDeck(deck);
        assertEquals(deck, learnController.getLearningDeck());
    }
}
