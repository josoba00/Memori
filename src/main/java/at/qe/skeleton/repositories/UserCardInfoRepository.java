package at.qe.skeleton.repositories;

import at.qe.skeleton.model.Card;
import at.qe.skeleton.model.User;
import at.qe.skeleton.model.UserCardInfo;
import at.qe.skeleton.model.UserCardInfoID;
import java.util.Date;
import java.util.List;

/**
 * Repository for managing {@link UserCardInfo} entities.
 */

public interface UserCardInfoRepository extends AbstractRepository<UserCardInfo, UserCardInfoID>{
    List<UserCardInfo> findAllByCard(Card card);

    List<UserCardInfo> findAllByUser(User user);

    List<UserCardInfo> findAllByRepetitionDate(Date repetition);

    List<UserCardInfo> findAllByNumberOfRepetitions(int numberOfRepetitions);

    UserCardInfo findFirstByUserAndCard(User user, Card card);


}
