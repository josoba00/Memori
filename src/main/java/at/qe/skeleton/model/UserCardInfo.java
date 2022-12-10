package at.qe.skeleton.model;


import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;
import java.util.function.Function;

@Entity
@Table(name = "user_card_info")
public class UserCardInfo implements Persistable<UserCardInfoID> {

    @EmbeddedId
    private UserCardInfoID id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    private Date repetitionDate;
    private boolean isFlipped;
    private float efFactor;
    private int numberOfRepetitions;

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
        assert function != null;
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
        if (!(obj instanceof UserCardInfo)) {
            return false;
        }
        UserCardInfo other = (UserCardInfo) obj;
        return this.getNotNullableId().equals(other.getNotNullableId());
    }

    @Override
    public String toString() {
        return "at.qe.skeleton.model.UserCardInfo[ id = " + getNotNullableId().toString() + "]";
    }
}
