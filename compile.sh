#!/bin/bash
rm sources.txt
echo Finding Files
ls -R1 | while read l; do case $l in *:) d=${l%:};; "") d=;; *) echo "$d/$l";; esac; done | grep -E "\.java$" > sources.txt 
javac -cp "gson-2.2.4.jar:javax.mail.jar" -d bin/ -sourcepath src/ @sources.txt
echo Javabot Compiled
