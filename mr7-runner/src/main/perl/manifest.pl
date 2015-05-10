#!/usr/bin/perl -w

opendir(DIR, ".");
@files = grep(/.*.jar/,readdir(DIR));
closedir(DIR);

foreach $file (@files) {
   print "  ./lib/$file\n";
}

