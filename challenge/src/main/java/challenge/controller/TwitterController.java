package challenge.controller;

import challenge.model.People;
import challenge.service.TwitterServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class TwitterController {

    @Autowired
    TwitterServiceImpl twitterService;

    @RequestMapping(value="messages", method = RequestMethod.GET)
    public List<String> getMessages(@RequestParam("search") Optional<String> keyword){
            return twitterService.getMessages(keyword);
    }

    // Return all the followers of the user
    @RequestMapping(value = "followers", method = RequestMethod.GET)
    public List<People> getFollowers(){
            return twitterService.getFollowers();

    }

    // Return all the handles the user is following
    @RequestMapping(value = "following", method = RequestMethod.GET)
    public List<People> getFollowing(){
            return twitterService.getFollowing();
    }

    @RequestMapping(value = "follow", method = RequestMethod.POST)
    public String follow(@RequestBody String handle){
        return twitterService.follow(handle);
    }

    @RequestMapping(value = "unfollow", method = RequestMethod.POST)
    public String unfollow(@RequestBody String handle){
        return twitterService.unfollow(handle);
    }

    @RequestMapping(value = "gethops", method = RequestMethod.POST)
    public int gethops(@RequestBody String handle){
        return twitterService.gethops(handle);
    }


}
