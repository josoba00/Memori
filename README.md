This project is part of the pro seminar course Software Architecture
and utilizes the provided skeleton project to implement a key card application.


## How to clone this repo
This repository makes use of git submodules to incorporate the wiki page of this repo for our documentation.

If you want to modify the wiki repo after cloning the repo please follow these steps:

`git submodule init` this command will add the wiki's repo to the wiki directory.

`git submodule update` this command will clone the wiki's repo into the wiki directory.

After these two commands navigate into the wiki directory and make sure you `checkout` the main branch
as the submodule initialization will create a detached `HEAD` branch for you to start on.

If you are working on a feature and have documentation to add to the wiki please refer to our [wiki documentation workflow](../../wikis/wiki_documentation_workflow).