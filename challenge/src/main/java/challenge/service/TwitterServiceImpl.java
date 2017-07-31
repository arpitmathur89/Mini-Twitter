package challenge.service;

import challenge.dao.UserDao;
import challenge.model.People;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TwitterServiceImpl implements TwitterService {

    @Autowired
    UserDao userDao;


    @Override
    public List<String> getMessages(Optional<String> keyword) {

        return userDao.getMessages(currentUser(),keyword);
    }

    @Override
    public List<People> getFollowers() {
        return userDao.getFollowers(currentUser());
    }


    @Override
    public List<People> getFollowing() {

        return userDao.getFollowing(currentUser());
    }

    @Override
    public String follow(String handle) {
        return userDao.follow(currentUser(),handle);
    }

    @Override
    public String unfollow(String handle) {
        return userDao.unfollow(currentUser(),handle);
    }

    private String currentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public int gethops(String handle) {
        return userDao.getHops(currentUser(),handle);
    }
}
