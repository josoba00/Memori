# Memori - Plant your Memory
## Software Architecture Project from Group G1T2

## 1. Introduction and Goals

Memori is a programming project of the software architecture course to develop a web platform that enables learning by Spaced Repetition and Active Recall. For this purpose, it should be possible for learners to create decks of flashcards.

Memori is a Project for
- efficient learning with the help of learning algorithms
- simple representation of content with flashcards
- creating and editing decks and card
- using flashcards of other users
  
This project was realized in a group of five, by Johanna Backer, Marco Fröhlich, Lukas Kirchmair, Emanuel Prader and Nikolaus Schennach.

### 1.1 Requirements Overview
#### What is Memori?
The main idea behind Memori is to create a helpful web platform where students can summarize their content from lectures with the help of flashcards.

1. Users create a deck in which they can add an amount of cards.
2. The cards consist of a question and an answer.
3. In a learning view, decks can be learned and the cards can be repeated at a calculated time interval using the algorithm.

The learning cycle is determined by students' own assessment. Thus, there are 6 different levels of how well or poorly a question was answered. Based on this and other criteria, it can then be calculated when the card is to be learned again.

### 1.2 Use Cases  
| Identifier | Anforderungen       | Erklärung                                                                                                                               |
|------------|---------------------|-----------------------------------------------------------------------------------------------------------------------------------------|
| M-1        | Create decks/cards  | An input window allows learners to enter a name and description for the new deck and then create new cards in that deck.                |
| M-2        | Edit decks/cards    | Name and description, as well as face and back of a card can be changed in the detailed view of the deck.                               |
| M-3        | Publish deck        | The deck is available to all registered students. They can add the deck to their saved-list of decks to learn.                          |
| M-4        | Delete decks/cards  | When deleting a deck, all cards will also be deleted and other students who have bookmarked this deck will no longer have access to it. |
| M-5        | Show cards in deck  | The detail view gives learners access to the list of all cards in the deck.                                                             |
| M-6        | Learn deck          | Learners are directed to the learning view.                                                                                             |
| M-7        | Search for decks    | All published decks of other students can be displayed and bookmarked.                                                                  |
| M-8        | Repetitive learning | With the help of Spaced Repetitions and Active Recall, learners receive their learnable cards again and again at calculated intervals.  |

![User Interaction](UserInteraction1.png)

<br/>

### 1.3 Quality Goals
| Identifier | Priority | Requirements | Explanation                                                                                                                                                                                                |
|------------|----------|--------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Q-1        | 1        | Usability    | Learners can intuitively use the web platform and understand the individual features.                                                                                                                      |
| Q-2        | 2        | Correctness  | The algorithm calculates the correct date of the learned card and stores it persistently. Cards for today or before are correctly recognized and displayed by the program.                                 |
| Q-3        | 3        | Performance  | Even with a large number of active users and/or decks being learned, the plattform remains stable and new cards take less than a second to be displayed.                                                   |
| Q-4        | 4        | Security     | Other learners are only able to delete or edit their own decks/cards. If there is not allowed content in the decks, the admin can lock them. Learners are not able to add code-injections via text fields. |
| Q-5        | 5        | Testability  | The architecture should allow easy testing of all main building blocks.                                                                                                                                    |

### 1.4 Stakeholders
| Role/Name | Goal                                                                                           | Expectations                                                                                                                                         |
|-----------|------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------|
| Learner   | Would like to use Memori to divide content into decks and summarize on flashcards.             | Has no interest in the architecture of the system, only in the features (creating/editing decks/cards).                                              |
| Admin     | Controls the content of learner-created decks and oversees the web platform to prevent errors. | Can use the features of Memori itself, but it's not a necessary functionality and therefore not of concern. He just wants a running, smooth program. |

<br/>

## 2. Contraints and Conventions
### 2.1 Technical Contraints
| Identifier | Constraint                          | Background/Motivation                                                                                                         |
|------------|-------------------------------------|-------------------------------------------------------------------------------------------------------------------------------|
| TC-1       | Implementation in Java              | Memori must be developed in Java.                                                                                             |
| TC-2       | Use of Primefaces and Spring Boot   | Memori must be able to integrate Primefaces and Spring Boot Framework.                                                        |
| TC-3       | OS independent development          | Memori should run on all three mayor operation systems (Mac OS, Linux and Windows)                                            |
| TC-4       | H2-Database                         | The internal database structure is based on a h2-Database                                                                     |
| TC-5       | Spaced reposition and Active Recall | Implement Spaced Repetitions and Active Recall algorithms so that cards can be presented with ideal intervals for repetition. |


### 2.2. Organizational Contraints
| Identifier | Constraint    | Background/Motivation                                                                                                                                                                                                   |
|------------|---------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| OC-1       | Team          | Johanna Backer, Marco Fröhlich, Lukas Kirchmair, Emanuel Prader and Nikolaus Schennach.                                                                                                                                 |
| OC-2       | Time schedule | Start in early Dezember with skeleton project from proseminar. A first version should be ready by the end of january 2023. There are weekly meetings where the progress and problems of each team member are discussed. |
| OC-3       | GitLab        | Memori is created in a private git repository with a complete commit history.                                                                                                                                           |
| OC-4       | Testing       | Using JUnit/Mockito to prove functional correctness and SonarQube to ensure a high test coverage                                                                                                                        |
| OC-5       | Issue-Tracker | Bugs and new requirements, are maintained with help of the issue tracker from Gitlab.                                                                                                                                   |


### 2.3 Conventions
| Identifier | Convention                 | Background/Motivation                                          |
|------------|----------------------------|----------------------------------------------------------------|
| C-1        | Architecture documentation | Structure based on english arc42-Template.                     |
| C-2        | Language                   | The internal language throughout the whole project is english. |
| C-3        | Naming conventions         | There are some naming conventions listed below.                |

The following naming conventions are used throughout the project and are related to the convention C-3 and are used throughout the project:

All Java files follow a fixed folder structure.
<pre><code>src/main/java...:           java code
src/main/resources...:      no-sourcecode artifact
src/main/webapp...:         xhtml, frontend code
src/test/java...:           test sourcecode
</code></pre>
All Java types that represent a repository must have a name that ends with the suffix **Repository**. <br/> All Java types that represent a service must have a name that ends with the suffix **Service**. <br/> All Java types that represent a bean or controller must have a name that ends with the suffix **Bean** or **Controller**.
<pre><code>src/main/java/.../repositories/DeckRepository
src/main/java/.../services/DeckService
src/main/java/.../ui/bean/DeckBean
src/main/java/.../ui/bean/DeckDetailController
</code></pre>

<br/>

## 3. System Scope and Context
This chapter describes the environment and context of Memori: Who uses the system and on which other system does Memori depend.

### 3.1 Business Context

![Business Context](BusinessContext1.png)

<br/>

| Neighbor               | Description                                                                                                                                                                                             |
|------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Learner                | Creates decks and cards with internal features of Memori. Can use the learn view of the program to study cards.                                                                                         |
| JavaServer Faces       | Is responsible for building the web application. PrimeFaces was used, which is a JavaServer Faces based application.                                                                                    |
| Webserver              | Memori is displayed on a webserver and reachable via http. It is as well responsible to send emails if a deck has been locked by an admin                                                               |
| Code Quality Tool      | The project is controlled with an external quality tool (SonarQube) to avoid bugs or code smells and to ensure good test coverage. See [Organizational contraints](#22-organizational-contraints) OC-4. |
| Version control system | To ensure good collaboration and management of changes to the program, documents or other information, as mention is [Organizational contraints]() OC-3, Git was used.                                  |
| Maven                  | Given by our proseminar leader, Maven was used as the build management tool and for the project management.                                                                                             |
| h2-database            | Also given was the h2 database, which was then manually filled with default data.                                                                                                                       |


### 3.2 Technical Context

![techical context](deployment_diagram/deployment_diagram.png)

 | Neighbor             | Description                                                                                                                                            |
|----------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------|
| Memori deployment    | The Memori deployment uploads its program with all its xhtml, css and java files to the webserver where the user can then access the complete program. |
 | Webserver            | The web server has the data uploaded by the Memori deployment and offers it to users who call it.                                                      |
| Memori user computer | The user uses a browser with different devices (laptop, cell phone) to access a web server and from there access the Memori program.                   |


<br/>

## 4. Solution Strategy
Memori has been implemented in Java and has few external dependencies as it relies on internal input from users and is simply stored in a h2 database.

To stay true to the three-tier architecture, the project is built on a model-view-controller pattern(MVC):

All important entities are stored in a model folder and implemented there with the necessary getters and setters. The different services can then access these, together with needed repositories, and implement more specific methods. The methods of the services can be accessed by the controllers to become even more specific and form a good interface for the UI.

Regarding the goal [usability (Q-1)](#13-quality-goals), the skeleton project of the software architecture lecture was used as a basis. This was supplemented with own xhtml pages and with css a more beautiful design was implemented. In advance, we looked at other similar projects to see what decisions were made there and what we could prefer or improve. Therefore, a design already existed at the beginning, which was only minimally modified.

[Correctness (Q-2)](#13-quality-goals) is achieved through multiple, extensive testing of the individual methods, as well as through manual use of the web application. This also helps to fulfill our goal of [testability (Q-5)](#13-quality-goals) to a certain extent. If an error or problem occurs, it is immediately recorded and processed by the Issue-Tracker.

To ensure that all team members were contributing to the project, weekly meetings were scheduled to discuss the current state of the project. One person reported for the UI, while the others worked on the backend programming first. If there were major problems, a solution was discussed and found as a team.

Also at the beginning of the project it had to be considered how to deal with the deletion of a deck or card. The following decisions were made:

//:: TODO: Adding deletion handling from wiki

<br/>

## 5. Building Block View

Memori can be divided into 3 blocks. The following diagram shows their interaction with each other and with the outside, the dashed arrows represent dependencies between the subsystems.

### 5.1 Memori - Level 1
The following diagram, has already been zoomed into the Memori from the previously defined  [business context](#31-business-context). It should be noted that only the database and the user are specified as interfaces, as the others have been declared irrelevant for this representation.


![cmp Memori](model_view_whiteboxes/level1/whole_whitebox-0.png)

| Building Block | Description                                                                           |
|----------------|---------------------------------------------------------------------------------------|
| Web-resources  | Include xhtml files, as well as css for the visual appearance of the website.         |
| System         | The internal system of Memori, with all its methods and classes used for the program. |
| Database       | The external h2 database where some default values are stored.                        |


### 5.2 Memori::System - Level 2
In this diagram, the Memori system was examined more closely. There are five relevant entities whose dependencies are represented by solid lines. The exact tasks of each class are described in the table below.

![cmp Memori::System - Level 2](model_view_whiteboxes/level2/system_whitebox-0.png)

| Building Block | Description                                                                                                                                 |
|----------------|---------------------------------------------------------------------------------------------------------------------------------------------|
| Controller     | The controller interacts with the web resources and waits for input from the user or other buttons. It then forwards these to the services. |
| Model          | Java represents real objects in these models that have been given certain properties.                                                       |
| Beans          | They contain simple methods that can be accessed from anywhere in the program.                                                              |
| Services       | Contain all functionalities of Memori, they interact with the database via repositories.                                                    |
| Configs        | This contains mainly configuration files.                                                                                                   |

### 5.3 Memori::System::... - Level 3

#### 5.3.1 Memori::System::Services

![cmp Memori::System::Services - Level 3](model_view_whiteboxes/level3/service_whitebox-0.png)

| Building Block     | Description                                                                                   |
|--------------------|-----------------------------------------------------------------------------------------------|
| UserService        | Provides all functionalities for a user object.                                               |
| DeckService        | Provides all functionalities for a deck object.                                               |
| LearnService       | Contains specified learn algorithm, provides all necessary functionalities for that           |
| EmailSenderService | Provides functionalities to send email when a deck object gets locked or unlocked by an admin |

#### 5.3.2 Memori::System::Controller

![cmp Memori::System::Controller - Level 3](model_view_whiteboxes/level3/controller_whitebox-0.png)

| Building Block         | Description                                                |
|------------------------|------------------------------------------------------------|
| UserListController     | Provides a list of all users for an admin.                 |
| UserBookMarkController | Provides all bookmarks of an user.                         |
| LearnController        | Provides learning environment for a specific deck and user |
| DeckListController     | Offers a search option for decks                           |
| DeckDetailController   | Provides a detailed view of a deck                         |
| UserDetailController   | Provides a detailed view of a user for an admin            |
| AdminController        | Provides all features for an admin                         |

#### 5.3.3 Memori::System::Bean

![cmp Memori::System::Bean - Level 3](model_view_whiteboxes/level3/bean_whitebox-0.png)

| Building Block  | Description                                                                                                                 |
|-----------------|-----------------------------------------------------------------------------------------------------------------------------|
| DeckBean        | This bean provides the display of decks for the home view, as well as filter functions for the primeface database.          |
| SessionInfoBean | With the help of this bean, you can request information about the current session, for example, who is currently logged in. |
| UserBean        | Provides options to create a new user.                                                                                      |

<br/>

## 6. Runtime view

Since we also have [usability Q-1](#13-quality-goals) as a quality goal, it is designed to be simple to interact with our system. Therefore, some use cases have been selected where an actual runtime view is shown below.
Three scenarios were selected for this purpose:
- creating a new deck,
- learning a deck,
- and the locking of a deck by an admin.

### 6.1 Create a deck
When creating a deck, the user can initialize this via the home view. There, after the internal creation, a new empty deck is displayed where a title and a description for the deck must be entered.
In this view, the user can as well create cards. If he is finished, the user can save the deck and which gets displayed in his home.

![](06_runtime_view/create_new_deck.png)

<br/>

### 6.2 Learn a deck
Learning a deck can also start via a button in the home view. First, the required cards are loaded internally into a container, and then each card is displayed individually to the user. The user can see the question and think about it, then turn over the card for the answer. After that he has to click on a difficulty before he gets the next card. Based on the selected difficulty, the program's algorithm can calculate how much time it will take to learn the card next. After that, the process repeats with the new card until all cards are learned or the user exits the learn view.
![](06_runtime_view/learn_deck.png)

<br/>

### 6.3 Admin locking a deck
The locking of a deck is only possible as admin. The admin can check the content of decks and decide whether the content does not comply with the usage guidelines and thus lock it.

![](06_runtime_view/lock_deck.png)

<br/>

## 7. Deployment view

![Deployment view](07_distribution/distribution-0.png)

| Node                 | Description                                                                                                            |
|----------------------|------------------------------------------------------------------------------------------------------------------------|
| Memori user computer | Users can interact with the server via http and therefore use the Memori Programm                                      |
| Webserver            | The Webserver contains the binary files of Memori and also handles the h2-database.                                    |
| Memori development   | Computer for development of Memori. Only important for software developer who want to modify or extend the source code |
 

## 8. Concepts
### 8.1 Domain models

![ER Diagram](08_database-schema/new_er_diagram.png)

| Building Block | Description                                                                                                                                                                                                                                                            |
|----------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| User           | Stores all the data of a user. It is important that each user can have multiple roles assigned to them, these can be either a Learner or an admin, both of which are users. users can create decks, or bookmark other decks. They can learn own decks or public decks. |
| Card           | A card stores its data, including which deck it belongs to. It can be learned by the user.                                                                                                                                                                             |
| Deck           | A deck stores its data and can be created by a user, or bookmarked by a user other than the creator. Each deck has a number of cards assigned to it.                                                                                                                   |

![Domain model](model_view_whiteboxes/level3/model_whitebox-0.png)

| Building Block | Description                                                                                                                                         |
|----------------|-----------------------------------------------------------------------------------------------------------------------------------------------------|
| UserCardInfo   | Parameters for the learn algorithm for specific card and user                                                                                       |
| UserCardInfoId | Defines id for UserCardInfo via id of user and card                                                                                                 |
| Card           | Contains all properties of a card, as well as the access possibilities to it.                                                                       |
| Deck           | Contains all properties of a deck, as well as the access possibilities to it.                                                                       |
| User           | Has all the information of a user and how to access these.                                                                                          |
| DeckStatus     | A deck can have different types of states, which are defined here.                                                                                  |
| UserRole       | A user can be either a learner or an administrator, but it should be noted that administrators can be both. Therefore, a set was used as container. |


### 8.2 Persistency

Memori uses a h2 database for storing relational data. It was decided to keep the default data for the first version (see [time schedule OC-2](#22-organizational-contraints)) in the file ./src/main/resources/data.sql and not to persist this h2 database.

Here is some input data from SQL for default user2:

``` sql
-- user2
INSERT INTO USERS (ENABLED, FIRST_NAME, LAST_NAME, PASSWORD, USERNAME, CREATION_DATE, EMAIL) VALUES(TRUE, 'Max', 'Mustermann', '$2a$10$q.fznS8tMEl8XrnYZ3ldleJvky17kNHD8mRks4l344P/CMicVLfne', 'user2', '2016-01-01 00:00:00', 'maxmustermann@mail.com')
INSERT INTO USER_USER_ROLE (USER_USERNAME, ROLES) VALUES ('user2', 'LEARNER')

-- deck 2 by user 2
INSERT INTO DECK (ID, TITLE, STATUS, DESCRIPTION, CREATION_DATE, CREATOR_USERNAME) VALUES(2, 'European Capitals', 'PUBLIC', 'for each european country there is its corresponding capital', '2016-02-20 00:00:00', 'user2')

-- for deck 2
INSERT INTO CARD (ID, CONTAINER_ID, FRONT_SIDE_CONTENT, BACK_SIDE_CONTENT, CREATION_DATE) VALUES(4, 2, 'Austria', 'Vienna', '2016-01-02 00:00:20')
INSERT INTO CARD (ID, CONTAINER_ID, FRONT_SIDE_CONTENT, BACK_SIDE_CONTENT, CREATION_DATE) VALUES(5, 2, 'France', 'Paris', '2016-01-02 00:00:50')
...

-- user 2 for deck 2
INSERT INTO USER_CARD_INFO(CARD_ID, USER_USERNAME, EF_FACTOR, IS_FLIPPED, NUMBER_OF_REPETITIONS, CREATION_DATE, REPETITION_DATE, LEARN_INTERVAL) VALUES(4, 'user2', 2.5, TRUE, 2,'2018-01-01 10:00:02', '2022-12-10', 2)
...
```

All access to the database is done through the JPA using hibernate as provider. The JPA is never accessed directly but only through the Spring Data JPA, which are the repositories.

### 8.3 User Interface
The default Memori UI included in the first version artifact is a single page application written in xhtml.

For visual representation we used css, which is based on our first design ideas.
Our design concept is to create a pleasant learning atmosphere, which is given a rather simple, dark design. The green nuances are meant to represent plant colors and are intended to have a calming effect, thus maintaining a longer motivation to learn.

### 8.4 Transaction Processing and Session Handling

To create all necessary beans for handling local transactions within the JPA, Memori relies on Spring Boot.

// TODO:: Add Session Handling

## 8.5 Security

// TODO:: Add Security Handling (Password encrypted..)

## 8.6 Email Messanger

// TODO:: Add the concept of the Email Messanger

##

## 9. Design Decisions

## 10. Quality Scenarios

## 11. Technical Risks

## 12. Glossary