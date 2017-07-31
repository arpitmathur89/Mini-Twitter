package challenge.service;

import challenge.model.People;

import java.util.List;
import java.util.Optional;

public interface TwitterService {

     List<People> getFollowers();

     List<String> getMessages(Optional<String> keyword);

     List<People> getFollowing();

     String follow(String handle);

    String unfollow(String handle);

    int gethops(String handle);
}
