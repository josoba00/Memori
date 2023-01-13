package at.qe.skeleton.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.*;

import org.springframework.data.domain.Persistable;

/**
 * Entity representing users.
 * <p>
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 */
@Entity
@Table(name = "users")
public class User implements Persistable<String>, Serializable, Comparable<User> {
    
    @Id
    @Column(length = 100)
    private String username;
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    boolean enabled;
    
    @ElementCollection(targetClass = UserRole.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_user_role")
    @Enumerated(EnumType.STRING)
    private Set<UserRole> roles;
    
    @OneToMany(
        mappedBy = "creator",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private Set<Deck> createdDecks = new HashSet<>();
    
    @ManyToMany(
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL
    )
    @JoinTable(name = "bookmarks",
        joinColumns = @JoinColumn(name = "user_username"),
        inverseJoinColumns = @JoinColumn(name = "deck_id")
    )
    private Set<Deck> bookmarks = new HashSet<>();
    
    @OneToMany(
        mappedBy = "user",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private Set<UserCardInfo> cardInfos;
    
    public User() {
    }
    
    public User(String username, String password, String firstName, String lastName, String email, boolean enabled, Set<UserRole> roles) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.enabled = enabled;
        this.roles = roles;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public Set<UserRole> getRoles() {
        return roles;
    }
    
    public void setRoles(Set<UserRole> roles) {
        this.roles = roles;
    }
    
    
    public Date getCreationDate() {
        return creationDate;
    }
    
    public void setCreationDate(Date createDate) {
        this.creationDate = createDate;
    }
    
    public Set<Deck> getCreatedDecks() {
        return createdDecks;
    }
    
    public void setCreatedDecks(Set<Deck> createdDecks) {
        this.createdDecks = createdDecks;
    }
    
    public Set<Deck> getBookmarks() {
        return bookmarks;
    }
    
    public void setBookmarks(Set<Deck> bookmarks) {
        this.bookmarks = bookmarks;
    }
    
    public Set<UserCardInfo> getCardInfos() {
        return cardInfos;
    }
    
    public void setCardInfos(Set<UserCardInfo> cardInfos) {
        this.cardInfos = cardInfos;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.username);
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof User)) {
            return false;
        }
        final User other = (User) obj;
        return Objects.equals(this.username, other.username);
    }
    
    @Override
    public String toString() {
        return "at.qe.skeleton.model.User[ id=" + username + " ]";
    }
    
    @Override
    public String getId() {
        return getUsername();
    }
    
    public void setId(String id) {
        setUsername(id);
    }
    
    @Override
    public boolean isNew() {
        return (null == creationDate);
    }
    
    @Override
    public int compareTo(User o) {
        return this.username.compareTo(o.getUsername());
    }
    
}
