package at.qe.skeleton.services;

import at.qe.skeleton.model.Card;
import at.qe.skeleton.model.User;
import at.qe.skeleton.model.UserCardInfo;
import at.qe.skeleton.repositories.CardRepository;
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

    private Queue<Card> learningCards = new LinkedList<>();

    private Queue<Card> neverLearnedCards = new LinkedList<>();

    private User currentUser;
    @Autowired
    private CardRepository cardRepository;

    public Queue<Card> getLearningCards(){
        return this.learningCards;
    }

    public void setLearningCards(Set<Card> cards){
        this.learningCards.addAll(findCardsToLearn(cards));
    }

    public void clearLearningCards(){
        this.learningCards.clear();
    }

    public Queue<Card> getNeverLearnedCards(){
        return this.neverLearnedCards;
    }

    public void setNeverLearnedCards(Set<Card> cards){
        this.neverLearnedCards.addAll(findNeverLearnedCards(cards));
    }

    public void clearNeverLearnedCards(){
        this.neverLearnedCards.clear();
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }


    /**
     * Methods looks through all Cards in a Deck to find those that have repetition date today and before.
     *
     * @param cardSet
     * @return found cards
     */
    private Set<Card> findCardsToLearn(Set<Card> cardSet){
        Set<Card> temp = new HashSet<>();
        for(Card card: cardSet){
            UserCardInfo userCardInfo = userCardInfoRepository.findFirstByUserAndCard(this.currentUser, card);
            if( userCardInfo != null){
                if(userCardInfo.getRepetitionDate().before(new Date())){
                    temp.add(card);
                }
            }
        }
        return temp;
    }

    /**
     * Methods to find Card that have no UserCardInfo aka have never been learned by this user.
     *
     * @param cardSet
     * @return Set of Cards
     */
    private Set<Card> findNeverLearnedCards(Set<Card> cardSet){
        Set<Card> temp = new HashSet<>();
        for(Card card: cardSet){
            if(userCardInfoRepository.findFirstByUserAndCard(this.currentUser, card)==null){
                temp.add(card);
            }
        }
        return temp;
    }

    //TODO: Think of better method name.
    /**
     * Method updates UserCardInfo and adds card back to queue if necessary.
     * @param learnedCard
     * @param difficulty
     */
    public void updateLearnQueue(Card learnedCard, int difficulty){
        UserCardInfo userCardInfo = userCardInfoRepository.findFirstByUserAndCard(currentUser, learnedCard);
        userCardInfo.setNumberOfRepetitions(userCardInfo.getNumberOfRepetitions()+1);
        if(difficulty < 4){
            this.learningCards.add(learnedCard);
        }
        updateUserCardInfo(userCardInfo, difficulty);
    }

    /**
     * Method calculates new learn Interval according to given algorithm.
     *
     * @param userCardInfo
     * @param difficulty
     * @return new learn interval
     */
    private int findNewLearnInterval(UserCardInfo userCardInfo, int difficulty){
        if(difficulty < 3){
           return 1;
        }
        return switch (userCardInfo.getNumberOfRepetitions()) {
            case 1 -> 1;
            case 2 -> 6;
            default -> (int) (userCardInfo.getLearnInterval() * userCardInfo.getEfFactor());
        };
    }

    /**
     * Method to update UserCardInfo Repetition-Date, Learn-Interval and if necessary Ef-Factor.
     *
     * @param userCardInfo
     * @param difficulty
     */
    private void updateUserCardInfo(UserCardInfo userCardInfo, int difficulty){
        userCardInfo.setLearnInterval(findNewLearnInterval(userCardInfo, difficulty));
        userCardInfo.setRepetitionDate(calculateNewDate(userCardInfo.getLearnInterval(), userCardInfo.getRepetitionDate()));
        if(difficulty > 2){
            userCardInfo.setEfFactor(calculateNewEfFactor(userCardInfo.getEfFactor(), difficulty));
        }
    }

    /**
     * Method to calculate new Ef-Factor according to given algorithm.
     *
     * @param oldValue
     * @param difficulty
     * @return new Ef-Factor
     */
    private float calculateNewEfFactor(float oldValue, int difficulty){
        return (float) java.lang.Math.max(1.3, oldValue-0.8+0.28*difficulty-0.02*difficulty*difficulty);
    }

    /**
     * Method to add days to given date.
     *
     * @param days
     * @param oldDate
     * @return new Date
     */
    private Date calculateNewDate(int days, Date oldDate){
        return Date.from(oldDate.toInstant().plus(days, ChronoUnit.DAYS));
    }

    private void generateNewUserCardInfo(Card card){
        UserCardInfo userCardInfo = new UserCardInfo();
        userCardInfo.setCard(card);
        userCardInfo.setUser(currentUser);
        userCardInfoRepository.save(userCardInfo);
    }

    /**
     * Method returns next card to learn.
     * @return null if queue is empty.
     */
    public Card getNextCard(){
        return learningCards.poll();
    }

    /**
     * Method returns next nerverLearnedCard and generates UserCardInfo for it.
     * @return card with UserCardInfo or null if queue is empty
     */
    public Card getNextNewCard(){
        Card card = neverLearnedCards.poll();
        if(card != null){
            generateNewUserCardInfo(card);
        }
        return card;
    }


}
