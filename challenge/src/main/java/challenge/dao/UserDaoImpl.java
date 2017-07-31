package challenge.dao;

import challenge.model.People;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class UserDaoImpl implements UserDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;


    @Override
    public List<String> getMessages(String currentUser, Optional<String> keyword) {
        List<String> messages = new ArrayList<>();
        List<People> followers = getFollowers(currentUser);
        List<Integer> followersId = new ArrayList<>();
        followersId.add(Integer.parseInt(currentUser));
        for(People p : followers){
            followersId.add(p.getId());
        }
        String msgQuery;
        Map<String, Object> map = new HashMap<>();
        try {
            if (keyword.isPresent()) {
                msgQuery = "SELECT content FROM messages WHERE person_id IN (:peopleList) AND content like :keyword";
                map.put("peopleList", followersId);
                map.put("keyword", "%" + keyword.get() + "%");
                SqlParameterSource nameParameters = new MapSqlParameterSource(map);
                messages = jdbcTemplate.queryForList(msgQuery, nameParameters, String.class);
            } else {
                msgQuery = "SELECT content FROM messages WHERE person_id IN (:peopleList)";
                map.put("peopleList", followersId);
                SqlParameterSource nameParameters = new MapSqlParameterSource(map);
                messages = jdbcTemplate.queryForList(msgQuery, nameParameters, String.class);
            }
            return messages;
        }
        catch (EmptyResultDataAccessException e){
            return null;
        }

    }

    @Override
    public List<People> getFollowers(String currentUser) {
        List<People> followers = new ArrayList<>();
        String followerQuery = "SELECT p.* from people p, followers f where p.id = f.follower_person_id and f.person_id = :currentUser";
        SqlParameterSource nameParam = new MapSqlParameterSource("currentUser", currentUser);
        try {
            SqlRowSet rs = jdbcTemplate.queryForRowSet(followerQuery, nameParam);
            while (rs.next()) {
                People p = new People();
                p.setId(rs.getInt("id"));
                p.setHandle(rs.getString("handle"));
                p.setName(rs.getString("name"));
                followers.add(p);
            }
            return followers;
        }
        catch (EmptyResultDataAccessException e){
            return null;
        }
    }


    @Override
    public List<People> getFollowing(String currentUser) {
        List<People> following = new ArrayList<>();
        String followerQuery = "SELECT p.* from people p, followers f where p.id = f.person_id and f.follower_person_id = :currentUser";
        SqlParameterSource nameParam = new MapSqlParameterSource("currentUser", currentUser);
        try {
            SqlRowSet rs = jdbcTemplate.queryForRowSet(followerQuery, nameParam);
            while (rs.next()) {
                People p = new People();
                p.setId(rs.getInt("id"));
                p.setHandle(rs.getString("handle"));
                p.setName(rs.getString("name"));
                following.add(p);
            }
            return following;
        }
        catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    @Override
    public String follow(String currentUser, String handle) {
        // Query to get ID for the handle
        int id = getId(handle);

        // Query to add id to to followers list
        // First Check if already exist
        if(id != -1) {
            String existsQuery = "SELECT f.id from followers f where f.person_id = :currentUser and f.follower_person_id = :follower_person_id";
            String insertQuery = "INSERT INTO followers (person_id, follower_person_id) VALUES (:currentUser, :follower_person_id)";
            Map<String, Integer> map = new HashMap<String, Integer>();
            map.put("currentUser", (Integer.parseInt(currentUser)));
            map.put("follower_person_id", id);
            SqlParameterSource nameParameters = new MapSqlParameterSource(map);
            String existsId = jdbcTemplate.query(existsQuery, nameParameters, new ResultSetExtractor<String>() {
                @Override
                public String extractData(ResultSet rs) throws SQLException,
                        DataAccessException {
                    return rs.next() ? rs.getString("id") : null;
                }
            });

            if (existsId == null) {
                jdbcTemplate.update(insertQuery, nameParameters);
                return "You are now following " + handle;
            } else {
                return "Already following " + handle;
            }
        }
        else{
            return "This Twitter Handle Doesn't exist";
        }
    }

    @Override
    public String unfollow(String currentUser, String handle) {
        // Query to get ID for the handle
        int id = getId(handle);
        // Query to delete id to to followers list
        // First Check if exists then delete
        if(id != -1) {
            String existsQuery = "SELECT f.id from followers f where f.person_id = :currentUser and f.follower_person_id = :id";
            String deleteQuery = "DELETE from followers where person_id =:currentUser and follower_person_id= :id";
            Map<String, Integer> map = new HashMap<String, Integer>();
            map.put("currentUser", (Integer.parseInt(currentUser)));
            map.put("id", id);
            SqlParameterSource nameParameters = new MapSqlParameterSource(map);
            String existsId = jdbcTemplate.query(existsQuery, nameParameters, new ResultSetExtractor<String>() {
                @Override
                public String extractData(ResultSet rs) throws SQLException,
                        DataAccessException {
                    return rs.next() ? rs.getString("id") : null;
                }
            });
            if (existsId != null) {
                jdbcTemplate.update(deleteQuery, nameParameters);
                return "Unfollowed " + handle;
            } else {
                return "Cannot unfollow " + handle;
            }
        }
        else{
            return "This Twitter Handle Doesn't exist";
        }
    }

    @Override
    public int getHops(String currentUser, String handle) {

        int start =  Integer.parseInt(currentUser);
        int end = getId(handle);
        Map<Integer, Set<Integer>> followersMap = new HashMap<>();

        String query = "SELECT f.person_id, p.id from people p, followers f where p.id = f.follower_person_id and f.person_id IN (SELECT id from people)";
        SqlParameterSource nameParameters = new MapSqlParameterSource();
        if(end != -1) {
            try {
                SqlRowSet rs = jdbcTemplate.queryForRowSet(query, nameParameters);
                int curr;
                int follower;
                while (rs.next()) {
                    curr = (rs.getInt("person_id"));
                    follower = (rs.getInt("id"));

                    if (followersMap.containsKey(curr)) {
                        Set<Integer> fset = followersMap.get(curr);
                        fset.add(follower);
                        followersMap.put(curr, fset);
                    } else {
                        Set<Integer> fset = new HashSet<>();
                        fset.add(follower);
                        followersMap.put(curr, fset);
                        followersMap.put(curr, fset);
                    }
                }
            } catch (EmptyResultDataAccessException e) {
                return -1;
            }

            return gethopsHelper(followersMap, start, end);
        }
        else{
            return -1;
        }

    }

    private int gethopsHelper(Map<Integer,Set<Integer>> fmap, int start, int end){

        Set<Integer> visited = new HashSet<>();
        Queue<Integer> q = new LinkedList<>();
        q.offer(start);
        int len = 0;
        while(!q.isEmpty()) {
            int size = q.size();
            for (int i = 0; i < size; i++) {
                int w = q.poll();
                if (w == end) return len;
                Set<Integer> f = fmap.get(w);
                Iterator<Integer> itr = f.iterator();
                while (itr.hasNext()){
                    int id = itr.next();
                    if(id == w) continue;
                    if(fmap.containsKey(id) && visited.add(id)){
                        q.offer(id);
                    }
                }
            }
            len++;
        }
        return 0;

    }

    private int getId(String handle){
        String getIdQuery = "SELECT id from people where handle =:handle";
        SqlParameterSource nameParam = new MapSqlParameterSource("handle", handle );

        try {
            int id = jdbcTemplate.queryForObject(getIdQuery, nameParam, Integer.class);
            return id;
        }
         catch (EmptyResultDataAccessException e) {
            return -1;
    }

    }
}
