## CS 122B Project 4

### Demo video link

https://youtu.be/76abu0Hc8eY  

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
 
### Contributions

Josh: Did the android list view, search page, and single page view. 
Kevin: Did autocomplete servlet and js, fixed sql queries, updated style.css, fixed login and recaptcha.   
