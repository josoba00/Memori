package at.qe.skeleton.model;

import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "deck")
public class Deck implements DeckInterface, Persistable<Long>, Serializable, Comparable<Deck> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    
    private String description;
    
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    
    @Enumerated(EnumType.STRING)
    private DeckStatus status;
    
    @ManyToOne(
        fetch = FetchType.EAGER
    )
    private User creator;
    
    @OneToMany(
        mappedBy = "container",
        fetch = FetchType.EAGER,
        cascade = {CascadeType.MERGE, CascadeType.PERSIST},
        orphanRemoval = true
    )
    private List<Card> content = new ArrayList<>();
    
    @ManyToMany(
        fetch = FetchType.EAGER
    )
    @JoinTable(
        name = "bookmarks",
        joinColumns = @JoinColumn(name = "deck_id"),
        inverseJoinColumns = @JoinColumn(name = "user_username")
    )
    private Set<User> bookmarkedBy = new HashSet<>();
    
    public Deck() {
        this.creationDate = new Date();
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Date getCreationDate() {
        return creationDate;
    }
    
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
    
    public User getCreator() {
        return creator;
    }
    
    public void setCreator(User creator) {
        this.creator = creator;
    }
    
    public DeckStatus getStatus() {
        return status;
    }
    
    public void setStatus(DeckStatus status) {
        this.status = status;
    }
    
    public List<Card> getContent() {
        return content;
    }
    
    public void setContent(List<Card> content) {
        this.content = content;
    }
    
    public Set<User> getBookmarkedBy() {
        return bookmarkedBy;
    }
    
    public void setBookmarkedBy(Set<User> bookmarkedBy) {
        this.bookmarkedBy = bookmarkedBy;
    }
    
    @Override
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    // used in compareTo to evade null return
    private Long getDeckId() {
        return id;
    }
    
    public int size() {
        return this.content == null ? 0 : this.content.size();
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.getId());
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof final Deck other)) {
            return false;
        }
        return Objects.equals(this.getId(), other.getId());
    }
    
    @Override
    public String toString() {
        return "at.qe.skeleton.model.Deck[ id = " + getId() + " ]";
    }
    
    @Override
    public boolean isNew() {
        return (null == id);
    }
    
    @Override
    public int compareTo(Deck o) {
        return this.getDeckId().compareTo(o.getDeckId());
    }
}
