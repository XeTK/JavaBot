all:
	echo Compiling
	javac -cp "gson-2.2.4.jar:javax.mail.jar" -d bin/ -sourcepath src/ src/**/*.java
	echo Copying dependencies
	cp -R *.jar bin/
	echo Running
	java -cp bin/ core.Start

clean:
	echo Deleting old files
	rm -r bin/**/*.class
	cp -R com bin/
	echo CLEANED!!!

compile:
	echo Compiling source
	find -name "*.java" > sources.txt
	javac -cp "gson-2.2.4.jar:javax.mail.jar" -d bin/ -sourcepath src/ @sources.txt
	cp -R *.jar bin/
prep:
	curl -O http://google-gson.googlecode.com/files/google-gson-2.2.4-release.zip
	unzip google-gson-2.2.4-release.zip
	rm google-gson-2.2.4-release.zip
	mv google-gson-2.2.4/gson-2.2.4.jar .
	rm -R google-gson-2.2.4
	wget http://java.net/projects/javamail/downloads/download/javax.mail.jar
	mkdir bin/
	cp run.sh bin/
	chmod 777 bin/run.sh

mac:
	brew install curl
	brew install wget
	brew install vim
	
linux:
	apt-get install curl
	apt-get install wget
	apt-get install vim
