package at.qe.skeleton.tests;


import at.qe.skeleton.model.Card;
import at.qe.skeleton.model.Deck;
import at.qe.skeleton.model.User;
import at.qe.skeleton.services.LearnService;
import at.qe.skeleton.ui.controllers.LearnController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.LinkedList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@WebAppConfiguration
public class LearnControllerTest {

    LearnController learnController;

    @Mock
    LearnService mockLearnService;

    User testUser = new User();
    Deck testDeck = new Deck();
    Card testCard1 = new Card();
    Card testCard2 = new Card();
    @BeforeEach
    void setUp(){
        testUser.setUsername("testUser");
        learnController = new LearnController();
        ReflectionTestUtils.setField(learnController, "learnService", mockLearnService);
        ReflectionTestUtils.setField(learnController, "currentUser", testUser);



        testCard1.setId(-10L);

        testCard2.setId(-11L);

        testDeck.setContent(Set.of(testCard1, testCard2));
    }

    @Test
    void initializeQueuesBothEmptyTest(){
        when(mockLearnService.findNeverLearnedCards(testDeck.getContent(), testUser)).thenReturn(Set.of());
        when(mockLearnService.findCardsToLearn(testDeck.getContent(), testUser)).thenReturn(Set.of());

        assertEquals(LearnController.InitialisationError.NO_CARDS_TO_LEARN,learnController.doInitializeQueues(testDeck));
    }

    @Test
    void initializeQueuesNeverLearnedEmptyTest(){
        when(mockLearnService.findNeverLearnedCards(testDeck.getContent(), testUser)).thenReturn(Set.of());
        when(mockLearnService.findCardsToLearn(testDeck.getContent(), testUser)).thenReturn(Set.of(testCard1, testCard2));

        assertEquals(LearnController.InitialisationError.SUCCESS, learnController.doInitializeQueues(testDeck));
    }

    @Test
    void initializeQueuesCardToLearnEmptyTest(){
        when(mockLearnService.findNeverLearnedCards(testDeck.getContent(), testUser)).thenReturn(Set.of(testCard1, testCard2));
        when(mockLearnService.findCardsToLearn(testDeck.getContent(), testUser)).thenReturn(Set.of());

        assertEquals(LearnController.InitialisationError.SUCCESS, learnController.doInitializeQueues(testDeck));
    }

    @Test
    void getNextCardTest(){
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
    void addCardBackToQueueTest(){
        ReflectionTestUtils.setField(learnController, "cardsQueue", new LinkedList<>(Set.of(testCard1)));
        learnController.setDifficulty(4);
        learnController.doAddCardBackQueue(testCard2);
        assertEquals(1, learnController.getCardsToLearnAmount());
        learnController.setDifficulty(3);
        learnController.doAddCardBackQueue(testCard2);
        assertEquals(2, learnController.getCardsToLearnAmount());
    }

    @Test
    void getCardsToLearnAmountTest(){
        ReflectionTestUtils.setField(learnController, "cardsQueue", new LinkedList<>(Set.of(testCard1, testCard2)));
        assertEquals(2, learnController.getCardsToLearnAmount());
    }

    @Test
    void getNeverLearnedCardsAmountTest(){
        ReflectionTestUtils.setField(learnController, "neverLearnedCardsQueue", new LinkedList<>(Set.of(testCard1, testCard2)));
        assertEquals(2, learnController.getNeverLearnedCardsAmount());
    }

    @Test
    void getTotalAmountCardsTest(){
        ReflectionTestUtils.setField(learnController, "neverLearnedCardsQueue", new LinkedList<>(Set.of(testCard1, testCard2)));
        ReflectionTestUtils.setField(learnController, "cardsQueue", new LinkedList<>(Set.of(testCard1)));
        assertEquals(3, learnController.getTotalAmountCards());

    }

}
