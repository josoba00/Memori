package at.qe.skeleton.services;

import at.qe.skeleton.model.Card;
import at.qe.skeleton.model.User;
import at.qe.skeleton.model.UserCardInfo;
import at.qe.skeleton.model.UserCardInfoID;
import at.qe.skeleton.repositories.UserCardInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
@Scope("application")
public class LearnService {

    @Autowired
    private UserCardInfoRepository userCardInfoRepository;
    @Autowired
    private UserService userService;


    /**
     * Method to find cards that have repetition date today and before.
     *
     * @param cardSet
     * @return found cards
     */
    public Set<Card> findCardsToLearn(List<Card> cardSet, User currentUser){
        Set<Card> storage = new HashSet<>();
        for(Card card: cardSet){
            UserCardInfo userCardInfo = userCardInfoRepository.findFirstByUserAndCard(currentUser, card);
            if( userCardInfo != null && userCardInfo.getRepetitionDate().before(new Date()) ){
                    storage.add(card);
            }
        }
        return storage;
    }

    /**
     * Method to find Cards without UserCardInfos aka those that have never been learned by this user.
     * @param cardSet
     * @param currentUser
     * @return
     */
    public Set<Card> findNeverLearnedCards(List<Card> cardSet, User currentUser){
        Set<Card> storage = new HashSet<>();
        for(Card card: cardSet){
            UserCardInfo userCardInfo = userCardInfoRepository.findFirstByUserAndCard(currentUser, card);
            if( userCardInfo == null){
                storage.add(card);
            }
        }
        return storage;
    }


    /**
     * Method calculates new learn Interval according to given algorithm.
     *
     * @param userCardInfo
     * @param difficulty
     * @return new learn interval
     */
    private int findNewLearnInterval(UserCardInfo userCardInfo, int difficulty) {
        if (difficulty < 3) {
            return 1;
        }
        if(userCardInfo.getNumberOfRepetitions() == 1){
            return 1;
        }
        else if(userCardInfo.getNumberOfRepetitions() == 2){
            return 6;
        }
        else{
            return (int)(userCardInfo.getLearnInterval()*userCardInfo.getEfFactor());
        }
    }

    /**
     * Method to update UserCardInfo Repetition-Date, Learn-Interval and if necessary Ef-Factor.
     * If no UserCardInfo exists for card and currentUser one gets created.
     *
     * @param difficulty
     * @param card
     * @param currentUser
     */
    public void updateUserCardInfo(Card card, User currentUser, int difficulty){
        UserCardInfo userCardInfo = userCardInfoRepository.findFirstByUserAndCard(currentUser, card);
        if(userCardInfo == null){
            userCardInfo = generateNewUserCardInfo(card, currentUser);
        }
        Set<UserCardInfo> infos = currentUser.getCardInfos();
        infos.remove(userCardInfo);
        userCardInfo.setNumberOfRepetitions(userCardInfo.getNumberOfRepetitions()+1);
        userCardInfo.setLearnInterval(findNewLearnInterval(userCardInfo, difficulty));
        userCardInfo.setRepetitionDate(calculateNewDate(userCardInfo.getLearnInterval()));
        if(difficulty > 2 && userCardInfo.getNumberOfRepetitions()>2){
            userCardInfo.setEfFactor(calculateNewEfFactor(userCardInfo.getEfFactor(), difficulty));
        }
        infos.add(userCardInfo);
        currentUser.setCardInfos(infos);
        userService.saveUser(currentUser);
    }

    /**
     * Method to calculate new Ef-Factor according to given algorithm.
     *
     * @param oldValue
     * @param difficulty
     * @return new Ef-Factor
     */
    private float calculateNewEfFactor(float oldValue, int difficulty) {
        return (float) java.lang.Math.max(1.3, oldValue - 0.8 + 0.28 * difficulty - 0.02 * difficulty * difficulty);
    }

    /**
     * Method to find new Date in given amount of days from today.
     *
     * @param days number of days
     * @return new Date
     */
    private Date calculateNewDate(int days){
        return Date.from(new Date().toInstant().plus(days, ChronoUnit.DAYS));
    }

    private UserCardInfo generateNewUserCardInfo(Card card, User currentUser){
        UserCardInfo userCardInfo = new UserCardInfo(new Date());
        UserCardInfoID id = new UserCardInfoID();
        id.setUsername(currentUser.getUsername());
        id.setCardId(card.getId());
        userCardInfo.setId(id);
        userCardInfo.setCard(card);
        userCardInfo.setUser(currentUser);
        userCardInfo.setLearnInterval(0);
        userCardInfo.setEfFactor(2.5f);
        userCardInfo.setNumberOfRepetitions(0);
        Set<UserCardInfo>  infos = currentUser.getCardInfos();
        infos.add(userCardInfo);
        currentUser.setCardInfos(infos);
        userService.saveUser(currentUser);
        return userCardInfo;
    }

}
