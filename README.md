## CS 122B Project 1

DEMO: https://youtu.be/Uilzos-Oh1w

#### If you do not have USER `mytestuser` setup in MySQL, follow the below steps to create it:

 - login to mysql as a root user 
    ```
    local> mysql -u root -p
    ```

 - create a test user and grant privileges:
    ```
    mysql> CREATE USER 'mytestuser'@'localhost' IDENTIFIED BY 'My6$Password';
    mysql> GRANT ALL PRIVILEGES ON * . * TO 'mytestuser'@'localhost';
    mysql> quit;
    ```

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




Josh: Set up the single movie/star servlets, set up single movie/star html and js files, edited queries, set up front-end styling on style.css.  
Kevin: Set up movie list servlet, set up index html and js files, created sql queries, set up files, added front-end styling on style.css.
