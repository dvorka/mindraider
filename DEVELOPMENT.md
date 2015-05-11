How to Build MindRaider Distribution
-------------------------------------
Prerequisities to build MR distribution:

* JDK 1.7
* Maven 3.1.x
* Git

Method:

1. Clone MindRaider repository on GitHub: 
   `git clone https://github.com/dvorka/mindraider.git`
2. Build MindRaider distribution (make sure no repositories configured in ~/.m2/settings.xml):
   `cd ${GIT}/mindraider && mvn clean install`
3. Find the distribution in the directory:
   `${GIT}/mindraider/mr7-release/target`
4. Unpack the distribution (in a directory e.g. MR)
5. Change to directory:
   `MR/mindraider-7.1`
6. Run MindRaider:
   `java -jar run.jar`



How to start development in an IDE (e.g. Eclipse)
-------------------------------------------------

To start development in Eclipse:

1. Start `Eclipse`
2. Import POM files using Eclipse menu/Import.../Maven/Existing Maven Projects
   from directory `${GIT}/mindraider`
3. Open class `MindRaiderApplication` and run it as Java Application



Maven Quick Reference
---------------------
```
mvn clean
mvn compile
mvn package
mvn install
mvn clean install -Dmaven.test.skip=true
mvn eclipse:eclipse
mvn assembly:assembly

mvn clean package

mvn archetype:create -DarchetypeGroupId=org.apache.maven.archetypes -DgroupId=com.mindcognition.mindraider -DartifactId=mr7-

```



How to Build Release Distributions
----------------------------------

Windows:

* build distribution with Maven - from mindraider/ directory run:
```
mvn clean install
```
* build release distribution with NSIS 2.35
  *  extract (in place) distribution in `mindraider/mr7-release/target` to 
     directory mindraider/ (renamed it from mindraider-7.x/ to mindraider/)
     thus it is `mindraider/mr7-release/target/mindraider`
  * do not copy JRE to mindraider/ (it is done by NSIS)
  * change (if needed) the name of MindRaiders's JAR in mr7-runner manifest
    and/or add new jars to the manifest 
  * change to `mindraider/mr7-release/src/main/nsis`
  * run `mindraider.nsi`

Linux:
* build distribution with Maven - from mindraider7/ directory run:
```
mvn clean install
```
* take linux distribution (tar.gz) from mindraider/mr7-release/target
* complete & test it:
  * `scp` distribution to a Linux machine
  * complete it with JRE
  * make package



How to Build MindRaider JAR only
--------------------------------
```
cd mindraider/mr7
mvn -o clean install
```



Maven settings
--------------
```xml
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
```
