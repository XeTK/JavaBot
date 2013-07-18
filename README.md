#JavaBot

Full of lemons, trousers and your mother's cooking.

#Development Setup


-make prep | This retrives the dependancies from the internet and puts them in the correct place.

-make | This compiles and runs the project.

#Compile by hand

-javac -cp "gson-2.2.4.jar:javax.mail.jar" -d bin/ -sourcepath src/ src/**/*.java

#Run by hand

-java -cp .:bin/:gson-2.2.4.jar:javax.mail.jar core.Start


#Make commands

Make prep - Downloads depedancies and adds folders and scripts to make run
Make clean - Cleans the dir of crap
Make - Builds in current dir, will not load plugins this way

#Coding standards

Make it C++ like

User plugintemp for plugins design, any extra classes for the plugins put in addons
