> **NOTICE: This project has been archived as of March 27th, 2019. There will be no maintenance or new development going forward.**

Spring Data JDBC Extensions for the Oracle database
===================================================

The primary goal of the [Spring Data](https://projects.spring.io/spring-data/) project is to make it easier to build Spring-powered
applications that use new data access technologies such as non-relational databases, map-reduce frameworks, and cloud based data
services.

There are still many good uses for a relational database and this modules provides an improved
programming model for working with legacy and advanced features of the Oracle database.

# Docs

You can find out more details from the [user documentation](https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/) or by
browsing the [javadocs](https://docs.spring.io/spring-data/jdbc/docs/current/api/).

# Artifacts

All you need to do to include the project in your project is shown on the [project page](https://projects.spring.io/spring-data-jdbc-ext/)

# Building

NOTE: Before you build the project you will need to download the necessary Oracle jars and import them into your local Maven repository.
See spring-data-oracle/README.txt for more details on how to obtain and install these jars.

For building the project you should use Java SDK version 1.7.

Spring Data JDBC Extensions uses Gradle as its build system. To build the project run:

    ./gradlew build

from the project root folder. This will compile the sources, run the tests and create the artifacts.  

To generate IDE-specific files, use

    ./gradlew eclipse
 
or

    ./gradlew idea

depending on your editor.

# Contributing

Here are some ways for you to get involved in the community:

* Get involved with the Spring community by helping out on [stackoverflow](https://stackoverflow.com/questions/tagged/spring-data-jdbc) by responding to questions and joining the debate.
* Create [GitHub issues](https://github.com/spring-projects/spring-data-jdbc-ext/issues) for bugs and new features and comment and vote on the ones that you are interested in.  
* Github is for social coding: if you want to write code, we encourage contributions through pull requests from [forks of this repository](https://help.github.com/forking/). If you want to contribute code this way, please reference a JIRA ticket as well covering the specific issue you are addressing.
* Watch for upcoming articles on Spring by [subscribing](https://spring.io/blog.atom) to spring.io

Before we accept a non-trivial patch or pull request we will need you to sign the [contributor's agreement](https://support.springsource.com/spring_committer_signup).  Signing the contributor's agreement does not grant anyone commit rights to the main repository, but it does mean that we can accept your contributions, and you will get an author credit if we do.  Active contributors might be asked to join the core team, and given the ability to merge pull requests.
