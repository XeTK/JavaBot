#!/bin/bash
rm sources.txt
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
done | grep -E "\.java$" > sources.txt

javac -cp "gson-2.2.4.jar:javax.mail.jar" -d bin/ -sourcepath src/ @sources.txt
echo Javabot Compiled
