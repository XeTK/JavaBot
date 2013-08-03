compile:
	./compile.sh
run:
	java -cp .:bin/:libs/gson-2.2.4.jar:libs/javax.mail.jar core.Start

clean:
	rm -rf bin
	rm -rf sources.txt
	echo "CLEANED!!!"

prep:
	./prep.sh
