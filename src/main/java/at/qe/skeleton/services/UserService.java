package at.qe.skeleton.services;

import at.qe.skeleton.model.Deck;
import at.qe.skeleton.model.User;
import at.qe.skeleton.model.UserRole;
import at.qe.skeleton.repositories.UserRepository;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for accessing and manipulating user data.
 * <p>
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 */
@Service
@Scope("application")
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Returns a collection of all users.
     *
     * @return
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public Collection<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Collection<User> getAllAdmins() {
        return userRepository.findByRole(UserRole.ADMIN);
    }

    /**
     * Loads a single user identified by its username.
     *
     * @param username the username to search for
     * @return the user with the given username
     */
    @PreAuthorize("hasAuthority('ADMIN') or principal.username eq #username")
    public User loadUser(String username) {
        return userRepository.findFirstByUsername(username);
    }

    /**
     * Loads all users (should be only one) by their email
     * @param eMail The email to look for in the users
     * @return All users that have that email
     */
    @PreAuthorize("hasAuthority('ADMIN') or principal.username eq #username")
    public List<User> loadUserByMail(String eMail) {
        return userRepository.findByMail(eMail);
    }

    /**
     * Saves the user. This method will also set {@link User#creationDate} for new
     * entities.
     *
     * @param user the user to save
     * @return the updated user
     */
    @PreAuthorize("hasAuthority('ADMIN') or principal.username eq #user.username")
    public User saveUser(User user) {
        if (user.isNew()) {
            user.setCreationDate(new Date());
        }
        return userRepository.save(user);
    }

    /**
     * Deletes the user.
     *
     * @param user the user to delete
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findFirstByUsername(auth.getName());
    }

    /**
     * Create a new user.
     * For now no restriction to role
     *
     * @param username must be unique
     * @param email    must be unique
     * @param enabled  If the user can log in to the account
     * @param roles    The roles the user has assigned ({@link UserRole})
     * @return The {@link User} that is saved
     */
    public User createUser(String username, String password, String firstName, String lastName, String email, boolean enabled, Collection<UserRole> roles) {
        return saveUser(new User(username, new BCryptPasswordEncoder().encode(password), firstName, lastName, email, enabled, new TreeSet<>(roles)));
    }
    /**
     * Adds new Deck to currentUsers bookmarks.
     * Database gets updated automatically.
     *
     * @param currentUser
     * @param newBookmark
     */
    public void addNewBookmark(User currentUser, Deck newBookmark){
        Set<Deck> userBookmarks = currentUser.getBookmarks();
        userBookmarks.add(newBookmark);
        currentUser.setBookmarks(userBookmarks);
    }

    /**
     * Removes Deck from currentUser bookmarks.
     *Database gets updated automatically.
     *
     * @param currentUser
     * @param deckToRemove
     */
    public void deleteBookmark(User currentUser, Deck deckToRemove){
        Set<Deck> userBookmarks = currentUser.getBookmarks();
        userBookmarks.remove(deckToRemove);
        currentUser.setBookmarks(userBookmarks);
    }

    /**
     * Returns true if deck is bookmarked by user
     *
     * @param user
     * @param deck
     *
     */
    public boolean isBookmarked(User user, Deck deck){
        return user.getBookmarks().contains(deck);
    }
}
