compile:
	./compile.sh
run:
	java -cp .:bin/:gson-2.2.4.jar:javax.mail.jar core.Start

clean:
	rm -r bin/**/*.class
	echo "CLEANED!!!"

prep:
	./prep.sh
