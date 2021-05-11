## CS 122B Project 3

### Demo video link

https://www.youtube.com/watch?v=g2kXnW7BHyY  

### Deploy your web application on AWS instance:

 -  inside your repo, where the pom.xml file locates, build the war file:
    ```
    mvn package
    ```
 -  Deploy the .war file:
    ```
    sudo cp ./target/*.war /var/lib/tomcat9/webapps/
    ```
 -  Link to the AWS instance: (UPDATE HERE)
    ```
    Credentials:
    User: tomcat
    Password: pass
    ```
 -  The webapp is now deployed!
 
### Queries
https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-91/blob/main/backend/AddMovieServlet.java  
https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-91/blob/main/backend/AddStarServlet.java  
https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-91/blob/main/backend/LoginServlet.java  
https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-91/blob/main/backend/MovieListServlet.java  
https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-91/blob/main/backend/OrderServlet.java  
https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-91/blob/main/backend/PaymentServlet.java  
https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-91/blob/main/backend/SingleMovieServlet.java  
https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-91/blob/main/backend/SingleStarServlet.java
 
### Parsing Optimization Strategies

Batch Inserting: Batch statements will help save internet and connection cost versus executing each statement separately, and they will also be executed concurrently which can also improve speed.  
HashMap: Minimizes the amount of queries and searches that need to be done and searches become O(1) time.  

(Local Machine Timings)
Optimized Times:  
Actor: 1.847  
Cast: 2.052   
Movies: 1.9832  

Unoptimized Times:  
Actor: 101.5734  
Cast: 102.3122 
Movies: 101.8921  

### Inconsistent Data Report
https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-91/blob/main/actor.txt  
https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-91/blob/main/cast.txt  
https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-91/blob/main/main.txt  

### Contributions

Josh: Updated and fixed back-end servlets, created employee enpoints/dashboard, updated front-end.  
Kevin: Created XML parsers, created SQL stored procedure, updated front-end.     
