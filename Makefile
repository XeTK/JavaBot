all:
	echo "Compiling"
	javac -cp "gson-2.2.4.jar:javax.mail.jar" -d bin/ -sourcepath src/ src/**/*.java
	echo "Running"
	java -cp .:bin/:gson-2.2.4.jar:javax.mail.jar core.Start

clean:
	rm -r bin/**/*.class
	echo "CLEANED!!!"

prep:
	wget http://google-gson.googlecode.com/files/google-gson-2.2.4-release.zip
	unzip google-gson-2.2.4-release.zip
	rm google-gson-2.2.4-release.zip
	mv google-gson-2.2.4/gson-2.2.4.jar .
	rm -R google-gson-2.2.4
	wget http://java.net/projects/javamail/downloads/download/javax.mail.jar
	mkdir bin/
	echo "Directory Preped"
