
## Level 1
| Building Block | Description                                 |
| -------------- | ------------------------------------------- |
| web-resources  | contains xhtml files for the web appearence |
| system         | Memori system                               |
| database       | h2 database                                 |


## Level 2
| Building Block | Description                                                                                |
| -------------- | ------------------------------------------------------------------------------------------ |
| Controller     | Control interaction with the user via xhtml, use the functionalities provided by Services. |
| Model          | Java representations of the used entities.                                                 |
| Beans          |                                                                                            |
| Services       | Contain all functionalities of Memori, interaction with the database via repositories.     |
| Configs        | Contain configuration files.                                                               |


## Level 3
### Model
| Building Block | Description                                                   |
| -------------- | ------------------------------------------------------------- |
| UserCardInfo   | parameters for the Learn Algorithm for specific Card and User |
| UserCardInfoId | defines ID for UserCardInfo via ID of User and Card           |
| Card           | contains the information of a Card                            |
| Deck           | contains information about a Deck                             |
| User           | contains the information of a User                            |
| DeckStatus     | defines status of a deck                                      |
| UserRole       | defines role of a User                                                              |


### Services
 | Building Block     | Description                                                                                                            |
 | ------------------ | ---------------------------------------------------------------------------------------------------------------------- |
 | UserService        | provides all funtionalities for a User object                                               |
 | DeckService        | provides all funtionalities for a Deck object                                              |
 | LearnService       | contains specified learn algorithm, provides all necessary functionalities for that |
 | EmailSenderService | provides funtionalities to send email when a Deck object gets locked or unlocked by an Admin                           |


### Controller
 | Building Block         | Description                                                |
 | ---------------------- | ---------------------------------------------------------- |
 | UserListController     | provides List of all Users for an Admin                    |
 | UserBookMarkController | provides all bookmarks of an User                          |
 | LearnController        | provides learning environment for a specific deck and user |
 | DeckListController     | provides search of decks                                   |
 | DeckDetailController   | provides detailed view of a deck                           |
 | UserDetailController   | provides an detailed view of an user for an admin          |
 | AdminController        | provides all action for an admin                                                           |


### Beans
 | Building Block  | Description                                                                               |
 | --------------- | ----------------------------------------------------------------------------------------- |
 | DeckBean        | provides Decks for home view of a User, provides filter function for Primefaces Datatable |
 | SessionInfoBean | retrieves session-specific parameters                                                     |
 | UserBean        | provides ability to create a new User                                                                                          |
