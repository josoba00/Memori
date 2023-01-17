package at.qe.skeleton.tests;

import at.qe.skeleton.model.*;
import at.qe.skeleton.repositories.UserCardInfoRepository;
import at.qe.skeleton.services.LearnService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@SpringBootTest
@WebAppConfiguration
public class LearnServiceTest {
    @Autowired
    LearnService mockLearnService;
    @Mock
    UserCardInfoRepository mockUserCardInfoRepository;

    User testUser = new User();
    Deck testDeck = new Deck();
    Card testCard1 = new Card();
    UserCardInfo testInfo1 = new UserCardInfo();
    Card testCard2 = new Card();
    UserCardInfo testInfo2 = new UserCardInfo();


    @BeforeEach
    void setUp(){
        testUser.setUsername("testUser");

        testCard1.setCardId(-10L);
        testInfo1.setCard(testCard1);
        testInfo1.setUser(testUser);
        testInfo1.setRepetitionDate(new Date());
        testInfo1.setEfFactor(2.5f);
        testInfo1.setLearnInterval(1);

        testCard2.setCardId(-11L);
        testInfo2.setCard(testCard2);
        testInfo2.setUser(testUser);
        testInfo2.setRepetitionDate(Date.from(new Date().toInstant().plus(2, ChronoUnit.DAYS)));

        testDeck.setContent(List.of(testCard1, testCard2));

        mockLearnService = new LearnService();
        ReflectionTestUtils.setField(mockLearnService, "userCardInfoRepository", mockUserCardInfoRepository);
    }



    @Test
    void findCardsToLearnTest(){
        when(mockUserCardInfoRepository.findFirstByUserAndCard(testUser, testCard1)).thenReturn(testInfo1);
        when(mockUserCardInfoRepository.findFirstByUserAndCard(testUser, testCard2)).thenReturn(testInfo2);

        assertEquals(Set.of(testCard1), mockLearnService.findCardsToLearn(testDeck.getContent(), testUser));
    }

    @Test
    void findNeverLearnedCardTest(){
        when(mockUserCardInfoRepository.findFirstByUserAndCard(testUser, testCard1)).thenReturn(testInfo1);
        when(mockUserCardInfoRepository.findFirstByUserAndCard(testUser, testCard2)).thenReturn(null);

        assertEquals(Set.of(testCard2), mockLearnService.findNeverLearnedCards(testDeck.getContent(), testUser));
    }

    @ParameterizedTest
    @CsvSource({"0,0,1","0,1,1", "0,2,1",
                "1,0,1","1,1,1","1,2,1",
                "2,0,1","2,1,1","2,2,1",
                "3,0,1","3,1,6","3,2,2",
                "4,0,1","4,1,6","4,2,2"
            })
    void updateIntervalTest(int difficulty, int repetitions, int expected){
        testInfo1.setNumberOfRepetitions(repetitions);
        when(mockUserCardInfoRepository.findFirstByUserAndCard(testUser, testCard1)).thenReturn(testInfo1);

       mockLearnService.updateUserCardInfo(testCard1, testUser, difficulty);


        assertEquals(expected, testInfo1.getLearnInterval());

    }

    @ParameterizedTest
    @CsvSource({"0,0,2.5","0,1,2.5", "0,2,2.5",
                "1,0,2.5","1,1,2.5","1,2,2.5",
                "2,0,2.5","2,1,2.5","2,2,2.5",
                "3,0,2.5","3,1,2.5","3,2,2.36",
                "4,0,2.5","4,1,2.5","4,2,2.5",
                "5,0,2.5","5,1,2.5","5,2,2.6"
    })
    void updateEfFactorTest(int difficulty, int repetitions, float expected){
        testInfo1.setNumberOfRepetitions(repetitions);
        testInfo1.setEfFactor(2.5f);
        when(mockUserCardInfoRepository.findFirstByUserAndCard(testUser, testCard1)).thenReturn(testInfo1);

        mockLearnService.updateUserCardInfo(testCard1, testUser, difficulty);


        assertEquals(expected, testInfo1.getEfFactor());
    }

    @Test
    void updateNumberRepetitionsTest(){
        testInfo1.setNumberOfRepetitions(1);
        when(mockUserCardInfoRepository.findFirstByUserAndCard(testUser, testCard1)).thenReturn(testInfo1);

        mockLearnService.updateUserCardInfo(testCard1, testUser, 4);

        assertEquals(2, testInfo1.getNumberOfRepetitions());

    }
}
