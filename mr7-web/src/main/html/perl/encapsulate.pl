#! /usr/bin/perl -w
#               Perl script for mindraider web processing, Dvorka, 2000
#

print STDOUT "\n\n";
print STDOUT "+-------------------------------------------------------------+\n";
print STDOUT "| Perl script for DVORKA which makes webpages reencapsulation |\n";
print STDOUT "+-------------------------------------------------------------+\n";
print STDOUT "=-> Note that processed files must be mentioned within source code!";

# 1 is Linux, 0 is Windows
$linuxMode=1;

# encapsulation template
$footTemplate="index.html";

# files to encapsulate
@footFiles= qw(
                update-8.0.html
                update-7.7.html
                update-7.6.html
                update-7.5.html
                update-7.3.html
                update-7.2.html
                mindforger.html
                inTheNews.html
                semantic-web.html
                analysis.html
                video.html
                construction.html
                license.html
                analysisBigPicture.html
                documentationMainWindow.html
                documentationDragAndDrop.html
                documentationTwiki.html
                documentationUriqa.html
                upgrade.html
                earlyAccess.html
                presentations.html
                testimonials.html
                features.html
                screenshots.html
                screenshotsPrototypes.html
                people.html
                links.html
                download.html
                installJavaHomeWindows.html
                installJavaHomeLinux.html
                );

&initFootScript;
&genPrefixPostfix( $footTemplate );
&genCores;

print STDOUT "\nBye!";

#------------------------------------------------------------------------------

sub initFootScript {
  print STDOUT "\n=-> Cleaning core/ and prepost/ directories...\n";
  if($linuxMode) {
    system("cd core && rm -vf * && cd ../prepost && rm -vf *");
  }
  else {
    if(chdir("core")) {
      unlink <*>;
      if(chdir ("..")) {
        if(chdir("prepost")) {
          unlink <*>;
          chdir ("..")
        }
      }
    }
  }
  print STDOUT "\n=-> Clean done...";
}

#------------------------------------------------------------------------------

sub genPrefixPostfix {
        print STDOUT "\n=-> Entering subroutine which generates prefix and postfix files...";
        my($femplate, $sourcator, $destinator);

        ($femplate)=@_;
        print STDOUT "\n Generating prepost from $femplate...";

        $sourcator="../".$femplate;

        $printingEnabled=1;
        # 1 ... print prefix
        # 2 ... do nothing
        # 3 ... print postfix

        open( SOURCATOR, $sourcator)
                or die "\nError: Unable to open file $sourcator!\n";

        # first open prefix
        $destinator="prepost/prefix.html";
        open( DESTINATOR, ">$destinator")
                or die "\nError: Unable to open file $destinator!\n";

        while ($radek = <SOURCATOR>)
        {
                if ( $radek =~ /FootCoreBegin/ )
                {
                        print STDOUT "\n Switching from *prefix* mode to *idle* mode...";
                        close( DESTINATOR );
                        $printingEnabled=2;
                }
                else
                {
                        if ( $radek =~ /FootCoreEnd/ )
                        {
                                print STDOUT "\n Switching from *idle* mode to *postfix* mode...";
                                $printingEnabled=3;
                                $destinator="prepost/postfix.html";
                                open( DESTINATOR, ">$destinator")
                                        or die "\nError: Unable to open file $destinator!\n";
                        }
                        else
                        {
                                if( $printingEnabled != 2 )
                                {
                                        print DESTINATOR $radek;
                                }
                        }
                }       
        }
        
        close( SOURCATOR );
        close( DESTINATOR );

        print STDOUT "\n=-> prefix/postfix successfuly generated!!\n";
}

#------------------------------------------------------------------------------

sub genCores
{
        # trim out encapsulation from original files and store them
        # in ./core

        print STDOUT "=-> Encapsulating cores with new prefix/postfix...\n";

        foreach $xyzyfile ( @footFiles )
        {
                makeCore($xyzyfile);
        }

        print STDOUT "=-> Pages successfuly re-generated!\n";
}

#------------------------------------------------------------------------------

sub makeCore
{
        # i)  cut javascript from <HEAD> element 
        # ii) cut text from <BODY> 

        my($sourcator, $destinator, $javadestinator, $inMode);

        ($sourcator)=@_;

        if($linuxMode) {
	    $destinator="core/".$sourcator;
	    $sourcator="../".$sourcator;
        } else {
	    $destinator="core\\".$sourcator;
	    $sourcator="..\\".$sourcator;
        }

        print STDOUT " $sourcator =-> $destinator\n";

        # mode:
        #  1  ... before js
        #  2  ... js
        #  3  ... between js and core
        #  4  ... core
        #  5  ... behind core
        $inMode=1;

        # put prefix
        if($linuxMode) {
          system("cp -vf prepost/prefix.html $destinator");
        }
        else {
          system("copy prepost\\prefix.html $destinator");
        }

        open( SOURCATOR, $sourcator)
                or die "\nError: Unable to open file $sourcator!\n";
        open( DESTINATOR, ">>$destinator")
                or die "\nError: Unable to open file $destinator!\n";

        while ($radek = <SOURCATOR>)
        {
                if ( $radek =~ /\<SCRIPT/ && $inMode == 1)
                { $inMode=2; }
                elsif ( $radek =~ /\<\/SCRIPT/  && $inMode == 2)
                   { $inMode=3; print DESTINATOR $radek; }
                   elsif ( $radek =~ /FootCoreBegin/ )
                      { $inMode=4; }
                      elsif ( $radek =~ /FootCoreEnd/ )
                         { $inMode=5; print DESTINATOR $radek; }

                if( $inMode % 2 == 0 )
                { print DESTINATOR $radek; }
        }

        close( SOURCATOR );
        close( DESTINATOR );

        # add postfix
        if($linuxMode) {
          system("cat prepost/postfix.html >> $destinator");
        }
        else {
          system("type prepost\\postfix.html >> $destinator");
        }
}

#- eof ------------------------------------------------------------------------
