cd ../
make compile
cd bin/
java -cp .:gson-2.2.4.jar:javax.mail.jar core.Start
