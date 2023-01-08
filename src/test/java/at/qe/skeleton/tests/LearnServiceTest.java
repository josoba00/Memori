package at.qe.skeleton.tests;

import at.qe.skeleton.model.*;
import at.qe.skeleton.repositories.CardRepository;
import at.qe.skeleton.repositories.DeckRepository;
import at.qe.skeleton.repositories.UserCardInfoRepository;
import at.qe.skeleton.repositories.UserRepository;
import at.qe.skeleton.services.DeckService;
import at.qe.skeleton.services.LearnService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.management.InstanceAlreadyExistsException;
import javax.transaction.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@WebAppConfiguration
public class LearnServiceTest {
    @Autowired
    private LearnService learnService;
    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private DeckService deckService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private UserCardInfoRepository userCardInfoRepository;

@Transactional
    @Test
    @DirtiesContext
    public void setLearningCardsTest(){
        learnService.clearLearningCards();
        User testUser = userRepository.findFirstByUsername("user1");
        learnService.setCurrentUser(testUser);
        Deck deck = deckRepository.getReferenceById(1L);
        learnService.setLearningCards(deck.getContent());

        assertEquals(deck.getContent().size(), learnService.getLearningCards().size());

    }

    @Test
    @DirtiesContext
    @Transactional
    public void setNeverLearnedCardsTest() throws InstanceAlreadyExistsException {
        User testUser = userRepository.findFirstByUsername("user1");
        learnService.setCurrentUser(testUser);
        Deck deck = deckRepository.getReferenceById(1L);

        Card testCard = new Card();
        testCard.setContainer(deck);
        testCard.setId(500L);
        deckService.addCardToDeck(testCard, deck);

        learnService.setNeverLearnedCards(deck.getContent());

        assertEquals(1, learnService.getNeverLearnedCards().size());
    }

    @Test
    @Transactional
    @DisplayName("Testing if Card added back to queue when difficulty < 4.")
    public void addingBackToQueueTest(){
        User testUser = userRepository.findFirstByUsername("user1");
        learnService.setCurrentUser(testUser);
        Deck deck = deckRepository.getReferenceById(1L);

        Card testCard = cardRepository.getReferenceById(1L);

        learnService.updateLearnQueue(testCard, 3);
        assertEquals(List.of(testCard).toString(),learnService.getLearningCards().toString());
    }

    @Test
    @Transactional
    @DisplayName("Testing if Card not added back to queue when difficulty >= 4.")
    public void notAddingBackToQueueTest(){
        User testUser = userRepository.findFirstByUsername("user1");
        learnService.setCurrentUser(testUser);
        Deck deck = deckRepository.getReferenceById(1L);

        Card testCard = cardRepository.getReferenceById(1L);

        learnService.updateLearnQueue(testCard, 4);
        assertEquals(List.of().toString(),learnService.getLearningCards().toString());
    }


    @Test
    @Transactional
    @DisplayName("Testing modification of UserCardInfo when difficulty > 2 and n = 0.")
    public void learnIntervalN0Test(){
        User testUser = userRepository.findFirstByUsername("user1");
        learnService.setCurrentUser(testUser);

        UserCardInfoID testId = new UserCardInfoID(1L, "user1");

        UserCardInfo info = userCardInfoRepository.getReferenceById(testId);
        info.setNumberOfRepetitions(0);

        Card testCard = cardRepository.getReferenceById(1L);

        learnService.updateLearnQueue(testCard, 3);

        assertEquals(1, userCardInfoRepository.getReferenceById(testId).getLearnInterval());
    }

    @Test
    @Transactional
    @DisplayName("Testing modification of UserCardInfo when difficulty > 2 and n = 1.")
    public void learnIntervalN1Test(){
        User testUser = userRepository.findFirstByUsername("user1");
        learnService.setCurrentUser(testUser);

        UserCardInfoID testId = new UserCardInfoID(1L, "user1");

        UserCardInfo info = userCardInfoRepository.getReferenceById(testId);
        info.setNumberOfRepetitions(1);

        Card testCard = cardRepository.getReferenceById(1L);

        learnService.updateLearnQueue(testCard, 3);

        assertEquals(6, userCardInfoRepository.getReferenceById(testId).getLearnInterval());
    }

    @Test
    @Transactional
    @DisplayName("Testing modification of UserCardInfo when difficulty > 2 and n = 2.")
    public void learnIntervalN2Test(){
        User testUser = userRepository.findFirstByUsername("user1");
        learnService.setCurrentUser(testUser);

        UserCardInfoID testId = new UserCardInfoID(1L, "user1");

        UserCardInfo info = userCardInfoRepository.getReferenceById(testId);
        info.setNumberOfRepetitions(2);

        Card testCard = cardRepository.getReferenceById(1L);

        learnService.updateLearnQueue(testCard, 3);

        assertEquals(5, userCardInfoRepository.getReferenceById(testId).getLearnInterval());
    }

    @Test
    @Transactional
    public void incrementNumberRepetitionsTest(){
        User testUser = userRepository.findFirstByUsername("user1");
        learnService.setCurrentUser(testUser);

        UserCardInfoID testId = new UserCardInfoID(1L, "user1");

        UserCardInfo info = userCardInfoRepository.getReferenceById(testId);
        info.setNumberOfRepetitions(2);

        Card testCard = cardRepository.getReferenceById(1L);

        learnService.updateLearnQueue(testCard, 3);

        assertEquals(3, userCardInfoRepository.getReferenceById(testId).getNumberOfRepetitions());
    }

    @Test
    @Transactional
    public void modificationOfEfFactorDiff2Test(){
        User testUser = userRepository.findFirstByUsername("user1");
        learnService.setCurrentUser(testUser);

        UserCardInfoID testId = new UserCardInfoID(1L, "user1");

        UserCardInfo info = userCardInfoRepository.getReferenceById(testId);
        info.setNumberOfRepetitions(2);
        info.setEfFactor(2.4f);

        Card testCard = cardRepository.getReferenceById(1L);

        learnService.updateLearnQueue(testCard, 2);

        assertEquals(2.4f, info.getEfFactor());
    }

    @Test
    @Transactional
    public void modificationOfEfFactorDiff3Test(){
        User testUser = userRepository.findFirstByUsername("user1");
        learnService.setCurrentUser(testUser);

        UserCardInfoID testId = new UserCardInfoID(1L, "user1");

        UserCardInfo info = userCardInfoRepository.getReferenceById(testId);
        info.setNumberOfRepetitions(2);
        info.setEfFactor(2.4f);

        Card testCard = cardRepository.getReferenceById(1L);

        learnService.updateLearnQueue(testCard, 3);

        assertEquals(2.26f, info.getEfFactor());
    }

    @Test
    @Transactional
    public void getNextCardTest(){
        User testUser = userRepository.findFirstByUsername("user1");
        learnService.setCurrentUser(testUser);

        Deck deck = deckRepository.getReferenceById(1L);
        learnService.setLearningCards(deck.getContent());

        Card testCard = new Card();
        testCard.setCardId(3L);

        assertEquals(testCard, learnService.getNextCard());
    }


}
