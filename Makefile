compile:
	./compile.sh
run:
	java -cp .:bin/:gson-2.2.4.jar:javax.mail.jar core.Start

clean:
	rm -rf bin
	rm -rf sources.txt
	echo "CLEANED!!!"

prep:
	./prep.sh
