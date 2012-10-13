Integration tests for spring-data-oracle
----------------------------------------

You will need at least an Oracle XE installation (10gR2 or later should work too) with the SCOTT user enabled. Next step is to run the db/setup.sql script to create users, tables, queues etc.

Once the database is set up create a dns alias of "grenada" to point to the host of the Oracle database server. 

Now you should be able to run "../gradlew test" in this subdirectory (note that this module is not part of the project build - it must be run explicitly).


Note:
-----

To avoid problems with the Oracle XML parser use the following JVM setting when running unit tests in an IDE or from the command line:
 -Djavax.xml.parsers.DocumentBuilderFactory=com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl 

This has already been incorporated in the Gradle build script.