## CS 122B Project 3

### Demo video link

(UPDATE HERE)

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
(UPDATE HERE)
 
### Parsing Optimization Strategies

Batch Inserting: Batch statements will help save internet and connection cost versus executing each statement separately, and they will also be executed concurrently which can also improve speed.

HaspMap: Minimizes the amount of queries and searches that need to be done and searches become O(1) time. 

### Inconsisdent Data Report
https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-91/blob/main/actor.txt  
https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-91/blob/main/cast.txt  
https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-91/blob/main/main.txt  

### Contributions

Josh: (UPDATE HERE)  
Kevin: (UPDATE HERE)
