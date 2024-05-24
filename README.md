# <img src="/API-Blitz/src/main/resources/static/logo.png" width="25px" style="vertical-align: middle;"> API-Blitz

## **PROJECT DESCRIPTION**

<img alt="Static Badge" src="https://badgen.net/badge/Java/17/?icon=java&color=4c7491"> <img src="https://img.shields.io/badge/Spring%20Boot-3.2.4-65b842?style=flat&logo=spring boot" alt="Spring Boot" />

A website that provides API testing services, with automated testing functions to monitor server health, aiming to help developers be more efficient during the development process.

## **Outlines**

[Project Description](#1)

[Features](#2)

[Getting Started](#3)

[Architecture](#4)

[Contact](#5)

<p id="1"></p>

## **Project Description**
API Blitz is a website that provides API testing services, came along with automated testing feature to server health check, aiming to help developers be more efficient during the development process.

<p id="2"></p>

## **Features**

- ### **API Test**

  Provides an user-interface that developers can easily create and test APIs. In addition, they can also get the response content of the API by sending requests.

  ![image](https://github.com/YangDou613/API-Blitz/blob/main/assets/APITest.gif)

- ### **History**
  
	Show API test history.

	**The `Test` button** - It will lead to the API Test page, and the API data has been inserted in the field. You can directly click send to test.

  ![image](https://github.com/YangDou613/API-Blitz/blob/main/assets/History_Test.gif)
  
	**The `+ Collection` button** - You can add API to the existing Collection. After clicking, a form will pop up. In this form, you can select the collection you want to add and set the request name of the API.

  ![image](https://github.com/YangDou613/API-Blitz/blob/main/assets/History_Add_to_collection.gif)

- ### **Collections**
  
	After creating a collection, you can add APIs to the collection for easier management of the APIs.

	**The `Test All` button** - After clicking, you can test all APIs in the collection at the same time, and when the test is completed, you will be directed to the test results page.

  ![image](https://github.com/YangDou613/API-Blitz/blob/main/assets/Collection_Create_new_collection.gif)

- ### **Monitor**
  
	After configuring the test case, the system will schedule automated testing of the API at regular intervals and can monitor the server health status in the report.

  ![image](https://github.com/YangDou613/API-Blitz/blob/main/assets/Monitor.gif)

- ### **Report**
  
	Display the test results of test case and collection.

  ![image](https://github.com/YangDou613/API-Blitz/blob/main/assets/Report_Test_case.gif)
  ![image](https://github.com/YangDou613/API-Blitz/blob/main/assets/Report_Collection.gif)

<p id="3"></p>

## **Getting Started**
Website link: [https://apiblitz.site](https://apiblitz.site)
1. Sign up or sign in to start.
    
    |                 |          Email         |   Password   | 
    |-----------------|------------------------|--------------| 
    | Testing Account | apiblitz0222@gmail.com | apiblitz0222 | 

2. Enter the API URL and select the HTTP method.
3. Fill the Query Params, Authorization, Headers and Body fields according to the API requirements.
4. Click send to start testing.

<p id="4"></p>

## **Architecture**

  ![image](https://github.com/YangDou613/API-Blitz/blob/main/assets/architecture.jpg)

- Built the backend with Java Spring Boot, while using HTML, CSS, and JavaScript for front-end development.
- Producer-Consumer model by single code base with Spring's active profiles
	* Used SQS as queue. 
	* Used Redis as a Message broker to notify the test result from consumer to producer.
	* Used WebSocket to notify the user upon completion of testing.
- Used a thread pool to process group tests simultaneously.
- Used mail service to send email notifications to users upon test failures.

<p id="5"></p>

## **Contact**

👩🏻‍💻 Yang Dou

Email: d860613@gmail.com
