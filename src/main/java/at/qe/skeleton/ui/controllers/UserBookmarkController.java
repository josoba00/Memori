package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.Deck;
import at.qe.skeleton.model.User;
import at.qe.skeleton.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

/**
 * Controller to handle and manipulate bookmarks of the current User.
 *
 */
@Component
@Scope("view")
public class UserBookmarkController implements Serializable {

    @Autowired
    private UserService userService;

    private User currentUser;

    public void doSetUser(User user){
        this.currentUser = user;
    }

    /**
     * Action to get bookmarks of current user.
     * @return
     */
    public List<Deck> doGetBookmarks(){
        return currentUser.getBookmarks().stream().toList();
    }

    /**
     * Action to add new bookmark for the current.
     * @param bookmark
     */
    public void doAddBookmark(Deck bookmark){
        userService.addNewBookmark(currentUser, bookmark);
    }


    /**
     * Action to delete a bookmark for the current user.
     * @param bookmark
     */
    public void doDeleteBookmark(Deck bookmark){
        userService.deleteBookmark(currentUser, bookmark);
    }

}
