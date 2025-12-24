This project is part of the proseminar course Software Architecture
and utilizes the provided skeleton project to implement a key card application.


## How to clone this repo
This repository makes use of git submodules to incorporate the wiki page of this repo for our documentation.

If you want to modify the wiki repo after cloning the repo please follow these steps:

`git submodule init` this command will add the wiki's repo to the wiki directory.
[deletion_and_locking](deletion_and_locking)
`git submodule update` this command will clone the wiki's repo into the wiki directory.

After these two commands navigate into the wiki directory and make sure you `checkout` the main branch
as the submodule initialization will create a detached `HEAD` branch for you to start on.

## Emailing
`spring-boot-starter-mail` is being used to send emails in this project. In order to use the `EmailSenderService` the following environment variables need to be set:
- EMAIL_HOST: something like `smtp.gmail.com`
- EMAIL_PORT: the `port` the email server is available on
- EMAIL_USERNAME: the `username` to log into the email server
- EMAIL_PASSWORD the corresponding `password`

For testing purposes it makes sense to use a sandbox mail server like [mailtrap](https://mailtrap.io).
If you want more information on how to set up mail trap to catch outgoing emails please refer to the [mailtrap getting started guide](https://help.mailtrap.io/article/12-getting-started-guide).

## Project/Test Information

### Deployment
Run the application with Maven using the Spring Boot plugin:
`mvn spring-boot:run`
This command compiles the project, resolves all dependencies, and starts the embedded application server so the backend is available on the configured port.

After that you can see the project by calling: `http://localhost:8080/`


### Login:
In the test data exist two *normal* users, who are only able to create, explore and learn decks.
- user1
- user2

And two administrators, who have all features of a user but can also lock decks and create new user.
- admin
- elvis

All have the same password: ***passwd***. 

### Required files:
The documentation can be either found in the folder [documentation](documentation) or in the [wiki](wiki).
The detailed description of our milestones, the technical UML, the deployment UML and the component UML can be found in the wiki.
