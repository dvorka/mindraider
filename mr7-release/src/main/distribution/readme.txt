MindRaider, http://mindraider.sourceforge.net

  This program is released under Apache 2.0 license and comes with no warranty.
  See license.txt for more details.
  
   



Install
-------
  Either download MindRaider with embedded JRE
   or
  You must have installed Java 1.6.0 or later:

    http://java.sun.com/j2se/1.6.0/download.html (choose Download J2SE JRE)





Upgrade
-------
Howto upgrade to new version of MindRaider:
 o Note that important data (your profile and data repository) is not
   stored within the MindRaider installation to be preserved on upgrade. Profile 
   is typically stored in $HOME/.mindraider.profile/$USERNAME.rdf.xml, and repository 
   in the directory according to your choice, that is specified during the
   first installation.
 o You may backup your repository (by default stored in the $HOME/MindRaider)
   before upgrade from MR menu (Tools/Backup Repository)

Fresh version of MindRaider should be installed as follows:

 o Windows
  o Unistall MindRaider using unistaller (which can be found
    in "Start/All Programs/MindRaider").
  o Install new version.

 o Linux
  o Extract distribution archive somewhere.
  o Run MindRaider... and data should be upgraded (if needed) automatically.
  o If everything is OK, delete previous MindRaider version.



CVS
---
 You may checkout MR from the sourceforge CVS repository as follows:

   cvs -d:pserver:anonymous@cvs.sourceforge.net:/cvsroot/mindraider login
   cvs -z3 -d:pserver:anonymous@cvs.sourceforge.net:/cvsroot/mindraider co -P mindraider7

 For details please refer to:
   https://sourceforge.net/cvs/?group_id=128454



Contribute
----------
 Do you like MindRaider?
   Would you like to improve it?
     Are your ideas cool?
       Do you want to help in development?

 I encourage you to check out MR from CVS, read howtoBuild.txt and code to somethin' ;-)
Do not hesitate to contact me at MindRaider@users.sourceforge.net!



3rd Party Code
--------------
MindRaider benefits from the following libraries:

  Jena
   Semantic Web Framework by HP (http://jena.sourceforge.net/)

  TouchGraph LLC
   by Alexander Shapiro (http://www.touchgraph.com)

  XML Pull Parser 3
   by Aleksander Slominski (http://www.extreme.indiana.edu/soap/xpp/)

  Crystal Icons
   by  Everaldo Coelho (http://www.everaldo.com/crystal.html)

  Browser Launcher
   by Eric Albert (ejalbert@cs.stanford.edu)

  TableTree SWING component
   by Philip Milne and Scott Violet (http://java.sun.com)

  Log4J
   http://logging.apache.org

  L2FProd.com Common Components
    http://common.L2FProd.com

  OPML CSS/JS/XSLT
    Joshua Allen <joshuaa@netcrucible.com>

  TWiki2Html and Html2Twiki
    by Frederic Luddeni

  Lucene
    http://jakarta.apache.org/lucene

  Nullsoft Scriptable Install System
    by Robert Rainwater (http://nsis.sourceforge.net)

  JTidy
    http://sourceforge.net/projects/jtidy

  commons-lang
    http://jakarta.apache.org/commons/lang/

  commons-io
    http://jakarta.apache.org/commons/io/

  Localization messages helper
    by Fabrizio Giustina


Author
------

  MindRaider
   by Martin Dvorak <MindRaider@users.sourceforge.net>

  Blog:
   http://e-mentality.blogspot.com/

- eof -
