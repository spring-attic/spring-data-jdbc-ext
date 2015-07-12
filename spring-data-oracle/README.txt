Install Oracle dependencies
===========================

This build depends on some proprietary Oracle jars that are not available in a public repo. You need to download and install these dependencies into you local Maven repository following the instructions below.

Download from Oracle Technology Network (registration is required) and then install the downloaded jars into your local Maven repository.
(The versions listed here are the ones that we use for building and testing - you should be able to substitute for more recent versions, especially when building your own applications)


1) Oracle JDBC driver version 12.1.0.2 -- com.oracle.jdbc:oracle.jdbc:jar:12.1.0.2

  Download from: http://download.oracle.com/otn/utilities_drivers/jdbc/121020/ojdbc7.jar

  Then, install it using the command: 
      mvn install:install-file -DgroupId=com.oracle.jdbc -DartifactId=oracle.jdbc -Dversion=12.1.0.2 -Dpackaging=jar -Dfile=/path/to/ojdbc7.jar


2) Oracle AQ API version 10.1.0.1 -- com.oracle.aq:oracle.aq:jar:10.1.0.1

  Part of Oracle Containers for J2EE 10g (OC4J) version 10.1.3.5.0 
  (No known direct download link for standalone package.)

  Download OC4J from: http://download.oracle.com/otn/java/oc4j/101350/oc4j_extended_101350.zip

  Extract the archive - we need:
    rdbms/jlib/aqapi.jar

  Then, install it using the command: 
      mvn install:install-file -DgroupId=com.oracle.aq -DartifactId=oracle.aq -Dversion=10.1.0.1 -Dpackaging=jar -Dfile=/path/to/aqapi.jar


3) Oracle XDB (JDBC4.0 java.sql.SQLXML support) version 12.1.0.2 -- com.oracle.xdb:oracle.xdb:jar:12.1.0.2

  Download from: http://download.oracle.com/otn/utilities_drivers/jdbc/121020/xdb6.jar

  Then, install using the command: 
      mvn install:install-file -DgroupId=com.oracle.xdb -DartifactId=oracle.xdb -Dversion=12.1.0.2 -Dpackaging=jar -Dfile=/path/to/xdb6.jar


4) Oracle XML Developers Kit version 12.1.3.0 -- com.oracle.xml:oracle.xml:jar:12.1.3.0

  Part of Oracle JDeveloper or SQLDeveloper downloads from Oracle 
  (No known direct download link for standalone package.)
  
  We did find it in a recent SQLDeveloper download - http://www.oracle.com/technetwork/developer-tools/sql-developer/downloads/index.html
  
  Extract the archive - we need:
    SQLDeveloper.app/Contents/Resources/sqldeveloper/modules/oracle.xdk/xmlparserv2.jar

  Then, install using the commands: 
      mvn install:install-file -DgroupId=com.oracle.xml -DartifactId=oracle.xml -Dversion=12.1.3.0 -Dpackaging=jar -Dfile=/path/to/xmlparserv2.jar

5) Oracle ONS 12.1.0.2 (only needed when using RAC) -- com.oracle.ons:oracle.ons:jar:12.1.0.2

  Download from: http://download.oracle.com/otn/utilities_drivers/jdbc/121020/ons.jar

  Then, install it using the command: 
      mvn install:install-file -DgroupId=com.oracle.ons -DartifactId=oracle.ons -Dversion=12.2.0.1 -Dpackaging=jar -Dfile=/path/to/ons.jar


