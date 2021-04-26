## CS 122B Project 2

### Demo video link

https://www.youtube.com/watch?v=pGhRQUsbOZM

### Deploy your web application on AWS instance:

 -  inside your repo, where the pom.xml file locates, build the war file:
    ```
    mvn package
    ```
 -  Deploy the .war file:
    ```
    sudo cp ./target/*.war /var/lib/tomcat9/webapps/
    ```
 -  Link to the AWS instance: http://18.224.70.246:8080/manager
    ```
    Credentials:
    User: tomcat
    Password: pass
    ```
 -  The webapp is now deployed!

### Contributions

Josh: Remade the movie list, single star, single movie pages and servlets, set up the genre/order servlets, set up front-end styling on style.css.  
Kevin: Set up and created the index, cart, browse, confirm, and login pages, set up and created the index, login, movie-list, and payment servlets. 
