# MindRaider - GPL prefix appender
# UltraDvorka@gmail.com
#

use File::Find qw(finddepth);
use File::Copy;

# constants
$fileGplHeader="E:/e-mentality.02.2005.01.02/MindRaider/cvs/mindraider.distribution/gplHeader.txt";
# backup of java files
$directoryJavaFiles="./src/com/emental/mindraider";

# - functions --------------------------------------------------------------------------

sub processFile {
    if (!-l && -d _) {
        print STDOUT "\nDIR : $name...";
    } else {
        print STDOUT "\nFILE: $name... ";

        # process java files only
        if(index($name,".java") > 0) {
          # must strip file name out of the path
          $myname=reverse($name);
          $myname =~ /([^\/]*)/;
          $name=reverse($1);

          print STDOUT "\n  Java: ".$name;

          # read first line of java file - if OK:
          #   copy header; append java file to header; rename java; rename header

          $fileTempac="tempac.java";

          # open file
          open( SOURCATOR, $name)
            or die "\nError: Unable to open source $name!\n";

          $line=<SOURCATOR>;
          #print STDOUT "\nLine: ".$line;
          if($line =~ /\/\* \=/) {
            print STDOUT " \nAlready prefixed: ".$name;
          } else {
            # copy header
            copy($fileGplHeader,$fileTempac);
            open( NEWHEADER, ">>".$fileTempac);
            print NEWHEADER $line;
            while($line=<SOURCATOR>) {
              print NEWHEADER $line;
            }
            close(NEWHEADER);

            # rename old java file
            move($name,$name.".old");
            move($fileTempac,$name);
          }
          # open header
          close(SOURCATOR);
        }

        # obsolete code
        # must strip file name out of the path
        #$myname=reverse($name);
        #$myname =~ /([^\/]*)/;
        #$name=reverse($1);
        #print STDOUT "unlink $name\n";
        #unlink($name) or warn "couldn't unlink $name: $!";
    }
}

# - main -------------------------------------------------------------------------------

print STDOUT "\n MindRaider - GPL header \n";

print STDOUT "\n   Processing from ".$directoryJavaFiles." ";
*name = *File::Find::name;
finddepth \&processFile, $directoryJavaFiles;

print STDOUT "\n\n Successfuly done!\n"

# - eof ---------------------------------------------------------------------------------
