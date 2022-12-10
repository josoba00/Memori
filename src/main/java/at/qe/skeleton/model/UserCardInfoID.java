package at.qe.skeleton.model;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
public class UserCardInfoID implements Serializable {
    public UserCardInfoID(Card card, User user) {
        this.card = card;
        this.user = user;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    final Card card;
    @ManyToOne(fetch = FetchType.LAZY)
    final User user;

    @Override
    public int hashCode() {
        return card.hashCode() + user.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof UserCardInfoID)) {
            return false;
        }
        UserCardInfoID other = (UserCardInfoID) obj;
        return this.card.equals(other.card) &&
                this.user.equals(other.user);
    }

    @Override
    public String toString() {
        return "at.qe.skeleton.model.UserCardInfoID[ " + this.card.toString() + " " + this.user.toString() + "]";
    }
}
