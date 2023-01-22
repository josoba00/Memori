package at.qe.skeleton.tests;

import at.qe.skeleton.model.*;
import at.qe.skeleton.repositories.UserCardInfoRepository;
import at.qe.skeleton.services.DeckService;
import at.qe.skeleton.services.LearnService;
import at.qe.skeleton.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@SpringBootTest
@WebAppConfiguration
class LearnServiceTest {
    @Autowired
    LearnService learnService;
    @Mock
    UserCardInfoRepository mockUserCardInfoRepository;
    @Autowired
    DeckService deckService;
    @Autowired
    UserService userService;
    @Autowired
    UserCardInfoRepository userCardInfoRepository;


    @Test
    @Transactional
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void findCardsToLearnTest() {
        Iterator<User> userIterator = userService.getAllUsers().iterator();
        boolean foundDeck = false;
        User user = null;
        Deck deck = null;
        // Look for a deck that has enough cards to test correct order
        while (userIterator.hasNext() && !foundDeck) {
            user = userIterator.next();
            Iterator<Deck> deckIterator = user.getCreatedDecks().iterator();
            // Look through users deck to find a valid one
            while (deckIterator.hasNext() && !foundDeck) {
                deck = deckIterator.next();
                if (deck.getContent().size() > 3) {
                    foundDeck = true;
                }
            }
        }
        if (!foundDeck) throw new IllegalStateException("No deck with enough cars found for test");
        List<Card> cardsInDeck = deck.getContent();
        // Set date to today for every second card and remember those cards
        // The others get the next date set to a few days later
        long laterRepetitionTime = (new Date().getTime()) + 3 * 24 * 3600 * 1000;
        Date laterRepetitionDate = new Date();
        laterRepetitionDate.setTime(laterRepetitionTime);

        // Set repetition date accordingly for the test on all cards from the chosen deck
        int cardNum = 0;
        Set<Card> cardsToLearnToday = new HashSet<>();
        for (Card card_i : cardsInDeck) {
            UserCardInfo userCardInfo = userCardInfoRepository.findFirstByUserAndCard(user, card_i);
            if (userCardInfo == null) {
                learnService.updateUserCardInfo(card_i, user, 0);
                userCardInfo = userCardInfoRepository.findFirstByUserAndCard(user, card_i);
            }
            Date chosenDate;
            // Choose every 2nd card as to learn today
            if (cardNum % 2 == 0) {
                chosenDate = laterRepetitionDate;
            } else {
                chosenDate = new Date();
                cardsToLearnToday.add(card_i);
            }
            cardNum++;
            userCardInfo.setRepetitionDate(chosenDate);
        }
        Set<Card> toLearn = learnService.findCardsToLearn(cardsInDeck, user);
        toLearn.addAll(learnService.findNeverLearnedCards(cardsInDeck, user));

        assertEquals(cardsToLearnToday.size() ,toLearn.size());
        for(Card card : toLearn){
            assertTrue(cardsToLearnToday.contains(card));
        }
    }

    @ParameterizedTest
    @CsvSource({"0,0,1", "0,1,1", "0,2,1",
            "1,0,1", "1,1,1", "1,2,1",
            "2,0,1", "2,1,1", "2,2,1",
            "3,0,1", "3,1,6", "3,2,2",
            "4,0,1", "4,1,6", "4,2,2"
    })
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Transactional
    void updateIntervalTest(int difficulty, int repetitions, int expected) {
        User user = userService.getAllUsers().iterator().next();
        Card card = deckService.getAllDecks().iterator().next().getContent().iterator().next();
        // In case userCardInfo exists already we delete it first
        UserCardInfo userCardInfo = userCardInfoRepository.findFirstByUserAndCard(user, card);
        if (userCardInfo != null) {
            userCardInfoRepository.delete(userCardInfo);
        }
        // Create userCardInfo to edit
        learnService.updateUserCardInfo(card, user, difficulty);
        userCardInfo = userCardInfoRepository.findFirstByUserAndCard(user, card);
        userCardInfo.setNumberOfRepetitions(repetitions);
        learnService.updateUserCardInfo(card, user, difficulty);
        assertEquals(expected, userCardInfoRepository.findFirstByUserAndCard(user, card).getLearnInterval());
    }

    @ParameterizedTest
    @CsvSource({"0,0,2.5", "0,1,2.5", "0,2,2.5",
            "1,0,2.5", "1,1,2.5", "1,2,2.5",
            "2,0,2.5", "2,1,2.5", "2,2,2.5",
            "3,0,2.5", "3,1,2.5", "3,2,2.36",
            "4,0,2.5", "4,1,2.5", "4,2,2.5",
            "5,0,2.5", "5,1,2.5", "5,2,2.6"
    })
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Transactional
    void updateEfFactorTest(int difficulty, int repetitions, float expected) {
        UserCardInfo userCardInfo = userCardInfoRepository.findAll().iterator().next();
        userCardInfo.setEfFactor(2.5f);
        userCardInfo.setNumberOfRepetitions(repetitions);
        learnService.updateUserCardInfo(userCardInfo.getCard(), userCardInfo.getUser(), difficulty);
        assertEquals(expected, userCardInfo.getEfFactor());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void updateNumberRepetitionsTest() {
        UserCardInfo userCardInfo = userCardInfoRepository.findAll().iterator().next();
        int oldRepetitionNum = userCardInfo.getNumberOfRepetitions();
        User user = userCardInfo.getUser();
        Card card = userCardInfo.getCard();
        learnService.updateUserCardInfo(card, user, 2);
        userCardInfo = userCardInfoRepository.findFirstByUserAndCard(user, card);
        assertEquals(oldRepetitionNum + 1, userCardInfo.getNumberOfRepetitions());
    }
}
