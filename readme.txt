Install Oracle dependencies
===========================

Download from Oracle Technology Network (registration is required) and then install the downloaded jars into your local Maven repository.
(The versions used are the ones that we use for building and testing - you should be able to substitute for more recent versions, especially when building your own applications)


1) Oracle JDBC driver version 10.2.0.2 -- com.oracle.jdbc:oracle.jdbc:jar:10.2.0.2

  Download from: http://download.oracle.com/otn/utilities_drivers/jdbc/10201/ojdbc14.jar

  Then, install it using the command: 
      mvn install:install-file -DgroupId=com.oracle.jdbc -DartifactId=oracle.jdbc -Dversion=10.2.0.2 -Dpackaging=jar -Dfile=/path/to/ojdbc14.jar


2) Oracle AQ support version 10.1.0.1 from Oracle Containers for J2EE 10g (OC4J) version 10.1.3.5.0 -- com.oracle.aq:oracle.aq:jar:10.1.0.1

  Download from: http://download.oracle.com/otn/java/oc4j/101350/oc4j_extended_101350.zip

  Extract the archive - we need:
    rdbms/jlib/aqapi.jar

  Then, install it using the command: 
      mvn install:install-file -DgroupId=com.oracle.aq -DartifactId=oracle.aq -Dversion=10.1.0.1 -Dpackaging=jar -Dfile=/path/to/aqapi.jar


3) Oracle XML Developers Kit version 10.2.0.2 -- com.oracle.xdb:oracle.xdb:jar:10.2.0.2 and com.oracle.xml:oracle.xml:jar:10.2.0.2

  Download from: http://www.oracle.com/technetwork/database/features/xmldb/java-utilsoft-087831.html

  Extract the archive - we need:
    lib/xdb.jar
    lib/xmlparserv2.jar

  Then, install using the commands: 
      mvn install:install-file -DgroupId=com.oracle.xdb -DartifactId=oracle.xdb -Dversion=10.2.0.2 -Dpackaging=jar -Dfile=/path/to/xdb.jar
      mvn install:install-file -DgroupId=com.oracle.xml -DartifactId=oracle.xml -Dversion=10.2.0.2 -Dpackaging=jar -Dfile=/path/to/xmlparserv2.jar


4) Oracle ONS 10.2.0.1 (only needed when using RAC) -- com.oracle.ons:oracle.ons:jar:10.2.0.1

  Download from: http://download.oracle.com/otn/utilities_drivers/jdbc/10201/ons.jar

  Then, install it using the command: 
      mvn install:install-file -DgroupId=com.oracle.ons -DartifactId=oracle.ons -Dversion=10.2.0.1 -Dpackaging=jar -Dfile=/path/to/ons.jar


