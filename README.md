## CS 122B Project 5

- # General
    - #### Team#: 91
    
    - #### Names: Josh Soemanto, Kevin Tran
    
    - #### Project 5 Video Demo Link: https://youtu.be/29R6YXdnWyQ

    - #### Instruction of deployment: Run the project on local and deploy the war file on tomcat manager on the AWS server

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
| Case 1: HTTP/1 thread                          | ![Link](../main/img/Single%201%20Connection.png) | 101                        | 4.5403839587433765                 | 1.8364649087812264      | Seeing how this is with connection pooling and with only 1 thread this should be the fastest time single case time since the server is under the least amount of stress here.           |
| Case 2: HTTP/10 threads                        | ![Link](../main/img/Single%2010%20Connection.png) | 105                        | 4.208482203030303                  | 1.6115404234848485    | Seeing how this is with connection pooling with 10 threads it’s expected to be a little slower than the first case with only 1 thread, but it still shouldn’t be a significantly large difference.           |
| Case 3: HTTPS/10 threads                       | ![Link](../main/img/Single%2010%20Connection%20HTTPS.png) | 104                        | 3.965637562878788             | 1.825863778030303    | Seeing how this is almost identical to Case 2 with connection pooling with 10 threads it should have almost no difference, but with HTTPS it should realistically be “slightly” slower since it’s secure and it has to send extra information.           |
| Case 4: HTTP/10 threads/No connection pooling  | ![Link](../main/img/Single%2010%20No%20Connection.png) | 106                        | 5.115392984090909                  | 1.2669663950757575          | This should be the slowest out of all the cases seeing how it has no connection pooling and 10 threads, which proves to be the case here. This proves that connection pooling is indeed beneficial.           |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![Link](../main/img/Scaled%201%20Connection.png) | 104                       | 5.335480392884179          | 1.697641626040878           | This should be the fastest out of all the times since it’s with the scaled version and has connection pooling and only 1 thread sending requests.           |
| Case 2: HTTP/10 threads                        | ![Link](../main/img/Scaled%2010%20Connection.png) | 103                      | 3.417449090909091             | 1.3724243015151516         | This should be a bit slower than scaled Case 1 since it has 10 threads, but still faster than Case 2 of the single instance version seeing how we have scaled it upwards.           |
| Case 3: HTTP/10 threads/No connection pooling  | ![Link](../main/img/Scaled%2010%20No%20Connection.png) | 102                       | 3.2063843613636362               | 1.0459005606060605          | This should be a little slower than the previous scaled version cases since it doesn’t have connection pooling, but should still be near comparable to the other connection pooling single instance cases.           |
