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

Josh: Set up the single movie/star servlets, set up single movie/star html and js files, edited queries, added front-end styling on style.css.
