
- How to Checkout from CVS and Build MindRaider Distribution --------------------------

    Prerequisities to build MR distribution:

        * JDK 1.6
        * Maven 2
        * CVS

    Method:

       1. Log in to SourceForge CVS:
          cvs -d:pserver:anonymous@mindraider.cvs.sourceforge.net:/cvsroot/mindraider login
       2. When prompted for a password for anonymous, simply press the Enter key.
       3. Checkout MindRaider7 CVS module (to a directory e.g. CVS):
          cvs -z3 -d:pserver:anonymous@mindraider.cvs.sourceforge.net:/cvsroot/mindraider co -P mindraider7
       4. Build MindRaider distribution:
          cd CVS/mindraider7 && mvn install
       5. Find the distribution in the directory:
          CVS/mindraider7/mr7-release/target
       6. Unpack the distribution (in a directory e.g. MR)
       7. Change to directory:
          MR/mindraider-7.1
       8. Run MindRaider:
          java -jar run.jar


- How to start development in an IDE (e.g. Eclipse) ----------------------------------

    To start development in Eclipse:

       1. Create Eclipse project:
          cd CVS/mindraider7/mr7 && mvn eclipse:eclipse
       2. Import the created project to Eclipse and open the mr7 project.
       3. Open class MindRaiderApplication and run it as Java Application

- Maven 2 Quick Reference -----------------------------------------------------------

mvn clean
mvn compile
mvn package
mvn install
mvn eclipse:eclipse
mvn assembly:assembly

mvn clean package

mvn archetype:create -DarchetypeGroupId=org.apache.maven.archetypes -DgroupId=com.mindcognition.mindraider -DartifactId=mr7-

-Dmaven.test.skip=true

- CVS Setup for development @ Windows -----------------------------------------------

    CVS root for Tortoise CVS:
       :ext:mindraider@mindraider.cvs.sourceforge.net:/cvsroot/mindraider

    CVS root and configuration for Eclipse:
       :ext:mindraider@mindraider.cvs.sourceforge.net:22/cvsroot/mindraider

       o eclipse/preferences/ssh2:
          c:\programs\plink.exe
          -l {user} {host}
          cvs
       o eclipse/preferences/team/cvs/ext connection
          ssh2home C:\Documents and Settings\{user name}\ssh
          private keys: id_dsa,id_rsa,C:\.mr\sf\{cert file name}.ppk    

- How to Build Release Distributions ------------------------------------------------

  Windows

     o build distribution with Maven - from mindraider7/ directory run:

       mvn clean install

     o build release distribution with NSIS 2.35

       - extract (in place) distribution in mindraider7/mr7-release/target to 
         directory mindraider/ (renamed it from mindraider-7.x/ to mindraider/)
         thus it is mindraider7/mr7-release/target/mindraider
       - do not copy JRE to mindraider/ (it is done by NSIS)
       - change (if needed) the name of MindRaiders's JAR in mr7-runner manifest
         and/or add new jars to the manifest 
       - change to mindraider7/mr7-release/src/main/nsis
       - run mindraider.nsi

  Linux

     o build distribution with Maven - from mindraider7/ directory run:

       mvn clean install

     o take linux distribution (tar.gz)

     o complete & test it:

      - scp distribution to Savant
      - complete it with JRE
      - make package

- How to Build MR Jar only ------------------------------------------------

cd mindraider7/mr7
mvn -o clean install

- How to start development @ Linux ----------------------------------------
  Linux

    Eclipse

       o menu/preferences/ssh2
         SSH2 home: /home/forge/.ssh
         Primate keys: id_dsa,id_rsa,/home/$user/mr/sourceforge.ppk
           > Add Private Key
       o CVS Repository Exploring perspective > Add
         Host: mindraider.cvs.sourceforge.net
         Path: /cvsroot/mindraider
         User: mindraider
         Pass: SF password
         Type: ext
       o Browse newly created repository:
           :ext:mindraider@mindraider.cvs.sourceforge.net:22/cvsroot/mindraider
         and Checkout mindraider7
       o Maven:
         > mvn eclipse:eclipse
         > mvn clean install
       o Eclipse 
         > import projects
         > set M2_REPO variable
    
    Command line
       o CVS howto:
         http://sourceforge.net/apps/trac/sourceforge/wiki/CVS%20client%20instructions#Read-Writeaccess
       o Checkout:
         export CVS_RSH=ssh 
         $ cvs -z3 -d:ext:mindraider@mindraider.cvs.sourceforge.net:/cvsroot/mindraider checkout mindraider

- Maven settings ------------------------------------------------------------------------------------------

<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
          xsi:noNamespaceSchemaLocation="http://buildbox.svn.sourceforge.net/svnroot/buildbox/trunk/common/maven/settings-1.0.0-nons.xsd">
  <mirrors>

    <mirror>
      <id>mvnrepository</id>
      <mirrorOf>*</mirrorOf>
      <name>MVN repository</name>
      <url>http://repo1.maven.org/maven2</url>
    </mirror>

    <mirror>
      <id>mindcognition.com</id>
      <mirrorOf>*</mirrorOf>
      <name>MR repository</name>
      <url>http://www.mindcognition.com/maven</url>
    </mirror>

  </mirrors>

  <localRepository>/home/${YOUR USERNAME GOES HERE}/.m2/repository</localRepository>

</settings>

- eof -
