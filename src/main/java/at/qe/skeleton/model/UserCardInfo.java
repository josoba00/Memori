package at.qe.skeleton.model;


import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.function.Function;

@Entity
@Table(name = "user_card_info")
public class UserCardInfo implements Persistable<UserCardInfoID>, Serializable {
    
    @EmbeddedId
    private UserCardInfoID id;
    
    @ManyToOne(
        fetch = FetchType.EAGER
    )
    @MapsId("cardId")
    private Card card;
    
    @ManyToOne(
        fetch = FetchType.EAGER
    )
    @MapsId("username")
    private User user;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    
    private Date repetitionDate;
    private boolean isFlipped;
    private float efFactor;
    private int numberOfRepetitions;

    private int learnInterval;

    public int getLearnInterval() {
        return learnInterval;
    }

    public void setLearnInterval(int learnInterval) {
        this.learnInterval = learnInterval;
    }

    @Override
    public UserCardInfoID getId() {
        return id;
    }
    
    @Override
    public boolean isNew() {
        return creationDate == null;
    }
    
    public Date getRepetitionDate() {
        return repetitionDate;
    }
    
    public void setRepetitionDate(Date repetitionDate) {
        this.repetitionDate = repetitionDate;
    }
    
    public void setRepetitionDate(Function<UserCardInfo, Date> function) {
        if (function == null) throw new NullPointerException();
        this.repetitionDate = function.apply(this);
    }
    
    public boolean isFlipped() {
        return isFlipped;
    }
    
    public void setFlipped(boolean flipped) {
        isFlipped = flipped;
    }
    
    public float getEfFactor() {
        return efFactor;
    }
    
    public void setEfFactor(float efFactor) {
        this.efFactor = efFactor;
    }
    
    public int getNumberOfRepetitions() {
        return numberOfRepetitions;
    }
    
    public void setNumberOfRepetitions(int numberOfRepetitions) {
        this.numberOfRepetitions = numberOfRepetitions;
    }
    
    private UserCardInfoID getNotNullableId() {
        return this.id;
    }
    
    public void setId(UserCardInfoID id) {
        this.id = id;
    }
    
    public Card getCard() {
        return card;
    }
    
    public void setCard(Card card) {
        this.card = card;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Date getCreationDate() {
        return creationDate;
    }
    
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        return 59 * hash + Objects.hashCode(this.getNotNullableId().hashCode());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof UserCardInfo other)) {
            return false;
        }
        return this.getNotNullableId().equals(other.getNotNullableId());
    }
    
    @Override
    public String toString() {
        return "at.qe.skeleton.model.UserCardInfo[ id = " + getNotNullableId().toString() + "]";
    }
}
