#!/bin/bash

set -e # Fail if subcommands fail

SOURCES_LIST="sources.txt"

if [ -e $SOURCES_LIST ]
then
	rm $SOURCES_LIST
fi

if [ ! -d bin ]
then
	mkdir bin
fi

echo Finding Files
# This block of code from:
# http://stackoverflow.com/questions/1767384/ls-command-how-can-i-get-a-recursive-full-path-listing-one-line-per-file
ls -R1 | while read l;
do
	case $l in
		*:)
			d=${l%:}
			;;
		"")
			d=
			;;
		*)
			echo "$d/$l"
			;;
	esac
done | grep -E "\.java$" > $SOURCES_LIST

echo "List of files saved to $SOURCES_LIST"
javac -cp "libs/gson-2.2.4.jar:libs/javax.mail.jar" -d bin/ -sourcepath src/ @${SOURCES_LIST}
echo Javabot Compiled
rm $SOURCES_LIST
