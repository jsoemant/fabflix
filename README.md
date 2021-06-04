## CS 122B Project 4

- # General
    - #### Team#: 91
    
    - #### Names: Josh Soemanto, Kevin Tran
    
    - #### Project 5 Video Demo Link:

    - #### Instruction of deployment: 

    - #### Collaborations and Work Distribution: 
    - Josh: Scaled Fablix with cluster of MySQL/Tomcat with load balancer, measured performance using JMeter.  
    - Kevin: Enabled JDBC connection pooling, fixed myqsl servlets, set up master/slave replication.


- # Connection Pooling
    - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
    (context.xml)
    - #### Explain how Connection Pooling is utilized in the Fabflix code.
     
    - #### Explain how Connection Pooling works with two backend SQL.
    We have 2 MySQL moviedb resources in our context.xml that are used, with the master-db one being used to be able to read and write and the other only being used to read. 

- # Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.
    (context.xml)
    - #### How read/write requests were routed to Master/Slave SQL?
    

- # JMeter TS/TJ Time Logs
    - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.
      Use log_processing.py on the log files, it will print out the average times for each.

- # JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**          | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | [Link](../main/img/Single%201%20Connection.png) | 117                        | ??                                  | ??                        | ??           |
| Case 2: HTTP/10 threads                        | [Link](../main/img/Single%2010%20Connection.png) | 104                        | ??                                  | ??                        | ??           |
| Case 3: HTTPS/10 threads                       | [Link](../main/img/Single%2010%20Connection%20HTTPS.png) | 108                        | ??                                  | ??                        | ??           |
| Case 4: HTTP/10 threads/No connection pooling  | [Link](../main/img/Single%2010%20No%20Connection.png) | 101                        | ??                                  | ??                        | ??           |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | [Link](../main/img/Scaled%201%20Connection.png) | 106                       | 4.854563397410359          | 1.5926229173306774           | ??           |
| Case 2: HTTP/10 threads                        | [Link](../main/img/Scaled%2010%20Connection.png) | 101                      | 3.327680721209213             | 1.339127297024952         | ??           |
| Case 3: HTTP/10 threads/No connection pooling  | [Link](../main/img/Scaled%2010%20No%20Connection.png) | 105                       | 4.668649873521383               | 0.960591898089172          | ??           |
