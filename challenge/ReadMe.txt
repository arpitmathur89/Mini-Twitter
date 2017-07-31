Mini Twitter REST endpoints.

For basic HTTP authentication,
Username :  id from people table
Password :  handle from user table 
Example : Username :  1  , Password : batman

1. An endpoint to read the message list for the current user (as identified by their HTTP Basic authentication credentials). Include messages they have sent and messages sent by users they follow. Support a “search=” parameter that can be used to further filter messages based on keyword.

Request Method : GET 
http://localhost:8080/messages
http://localhost:8080/messages?search=Quisque
Parameter Name: search - The keyword to filter messages

2. Endpoints to get the list of people the user is following as well as the followers of the user.

Request Method : GET
List people user is following -  http://localhost:8080/following

Request Method : GET
List followers of the user  -  http://localhost:8080/followers

3. An endpoint to start following another user.

Request Method : POST
http://localhost:8080/follow
Request Body:  The handle of the person you want to follow

4. An endpoint to unfollow another user.

Request Method : POST
http://localhost:8080/unfollow
Request Body: The handle of the person you want to unfollow

5. An endpoint that returns the current user's "shortest distance" to some other user. The shortest distance is defined as the number of hops needed to reach a user through the users you are following (not through your followers; direction matters). For example, if you follow user B, your shortest distance to B is 1. If you do not follow user B, but you do follow user C who follows user B, your shortest distance to B is 2.

Request Method : POST
http://localhost:8080/gethops
Request Body: The handle of the person from whom you want shortest distance

