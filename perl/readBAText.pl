#!/usr/bin/perl

$filename = "broccoli-bolognese.txt";

# Open in read only mode
open(DATA, "<$filename") or die "Couldn't open file $filename, $!";

print "\n$filename contents:";
print "\n----------------------------------------------------------------------\n";
while(<DATA>) {
   print "$_";
}
print "\n----------------------------------------------------------------------\n";

