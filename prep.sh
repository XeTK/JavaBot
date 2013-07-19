#!/bin/bash
JSON="gson-2.2.4"
#Get GSON dependancy unzip and move to correct location.
if [ ! -f gson-2.2.4.jar ]; then
	wget http://google-gson.googlecode.com/files/google-$JSON-release.zip
	unzip google-$JSON-release.zip
	rm google-$JSON-release.zip
	mv google-$JSON/$JSON.jar .
	rm -R $JSON
	echo JSON dependancy downloaded.
else
	echo JSON dependancy already exists.
fi
if [ ! -f javax.mail.jar ]; then
	#Get Java mail dependancy
	wget http://java.net/projects/javamail/downloads/download/javax.mail.jar
	echo Email dependancy downloaded.
else
	echo Email dependancy already exists.
fi	
if [ ! -d bin/ ]; then
	mkdir bin/
	echo bin/ directory created.
else
	echo bin/ already exists.
fi
if [ ! -f Details.json ]; then
	cat Details.default > Details.json
	echo Please now edit Details.json.
	if [ ! type vim >& /dev/null ]; then
		vim Details.json
	else
		nano Details.json
	fi
else
	echo Details already exists!
fi
