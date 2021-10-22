# Fabflix
A full stack web application for browsing, searching, and purchasing movies. The application’s database was created by parsing through data from Stanford’s library collection of movies.

This application runs on a Java VM with Apache Tomcat serving a REST API. The front-end is written with HTML, CSS, and JavaScript. The back-end is written in Java, using mySQL as the database. 

## Deployment
The application was originally hosted on a sole AWS EC2 instance. When putting scalability in the forefront, it was upgraded to use a total of 4 instances; a pair of AWS EC2 instances which would run as a master and slave combo, and an AWS EC2 instance and a GCP instance which would both run as load balancers to redirect traffic into the master and slave instances. This setup allowed the application to handle a high amount of simultaneous traffic with relative ease, as tested extensively through Apache JMeter.

## Features
- This application supports user authentication along with Google reCaptcha to bar any bots from logging in. There are two account types, customer and admin. Customers are able to browse and purchase movies. Admins are able to add new movies, genres, and stars to the database and listings through a separate dashboard page.

- A user can directly search for movies and filter results by title, director, year, and genre. A user can also browse by categories such as title and genre. 

- This application utilizes user sessions to keep shopping cart and login information persistent. 

- This application has a transactions system that allows users to select and purchase different movies. Upon checkout, users are presented a form where they can input payment information that will be verified against a predefined set of fake credit cards.

- This application also has a simple Android application written in Java to serve as an alternative front-end on any Android device. The application communicates with the original backend server with HTTPS connections through our developed API routes. Sessions are maintained by the CookieHandler and CookieManager functions set in the NetworkManager class.
