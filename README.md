## CS 122B Project 1

### Demo video link

https://youtu.be/Uilzos-Oh1w

### Deploy your web application on AWS instance:

 -  inside your repo, where the pom.xml file locates, build the war file:
    ```
    mvn package
    ```
 -  Deploy the .war file:
    ```
    sudo cp ./target/*.war /var/lib/tomcat9/webapps/
    ```
 -  The webapp is now deployed!

### Contributions

Josh: Set up the single movie/star servlets, set up single movie/star html and js files, edited queries, set up front-end styling on style.css.  
Kevin: Set up movie list servlet, set up index html and js files, created sql queries, set up files, added front-end styling on style.css.
