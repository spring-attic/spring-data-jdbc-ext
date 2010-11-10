Integration tests for spring-data-oracle
----------------------------------------

You will need at least an Oracle XE installation (10gR2 or later should work too) with the SCOTT user enabled. Next step is to run the db/setup.sql script to create users, tables, queues etc.

Once the database is set up create a dns alias of "grenada" to point to the host of the Oracle database server. 

Now you should be able to run "mvn clean test" in this subdirectory (note that this module is not part pf the project build - it must be run explicitly).
