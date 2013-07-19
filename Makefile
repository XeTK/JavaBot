compile:
	javac -cp "gson-2.2.4.jar:javax.mail.jar" -d bin/ -sourcepath src/ src/**/*.java

run:
	java -cp .:bin/:gson-2.2.4.jar:javax.mail.jar core.Start

clean:
	rm -r bin/**/*.class
	echo "CLEANED!!!"

prep:
	./prep.sh
