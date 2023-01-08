This project is part of the pro seminar course Software Architecture
and utilizes the provided skeleton project to implement a key card application.


## How to clone this repo
This repository makes use of git submodules to incorporate the wiki page of this repo for our documentation.

If you want to modify the wiki repo after cloning the repo please follow these steps:

`git submodule init` this command will add the wiki's repo to the wiki directory.

`git submodule update` this command will clone the wiki's repo into the wiki directory.

After these two commands navigate into the wiki directory and make sure you `checkout` the main branch
as the submodule initialization will create a detached `HEAD` branch for you to start on.

If you are working on a feature and have documentation to add to the wiki please refer to our [wiki documentation workflow](https://git.uibk.ac.at/informatik/qe/swapsws22/group1/g1t2/-/wikis/wiki_documantation_workflow).

## Emailing
`spring-boot-starter-mail` is being used to send emails in this project. In order to use the `EmailSenderService` the following environment variables need to be set:
- EMAIL_HOST: something like `smtp.gmail.com`
- EMAIL_PORT: the `port` the email server is available on
- EMAIL_USERNAME: the `username` to log into the email server
- EMAIL_PASSWORD the corresponding `password`

For testing purposes it makes sense to use a sandbox mail server like [mailtrap](https://mailtrap.io).
If you want more information on how to set up mail trap to catch outgoing emails please refer to the [mailtrap getting started guide](https://help.mailtrap.io/article/12-getting-started-guide).