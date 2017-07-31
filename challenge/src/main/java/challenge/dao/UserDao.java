package challenge.dao;

import challenge.model.People;

import java.util.List;
import java.util.Optional;

public interface UserDao {


    List<People> getFollowers(String currentUser);

    List<String> getMessages(String currentUser, Optional<String> keyword);

    List<People> getFollowing(String currentUser);

    String follow(String currentUser, String handle);

    String unfollow(String currentUser, String handle);

    int getHops(String currentUser, String handle);
}
