#Introduction

JavaBot is a lightweight Oracle Java based IRC bot for managing and manipulating IRC channels, 
Its goal is to make management of an IRC channel simple, along with being flexable enough to allow users to add new plugins with ease.

It was decided from the start that Javabot should be written in Java as it is the standard teaching language in most UK universities,
and there for most users of the channels I use can add functionality to Javabot.

Javabot uses a plugin based arcutecture to allow new functionality to be easily added on at any time. it also uses a Singleton approch to core functionality to allow it to be easily interacted with by the rest of the program.


#Setup

To get all depedancies along with set up default json file, run this command

`make prep`

To compile the project, run this

`make compile`

To execute the project, run this

`make run`


#Compile by hand

Making sure that there is the relivant dependancies in the directory, also make sure that the `bin/` directory exists.

`javac -cp "gson-2.2.4.jar:javax.mail.jar" -d bin/ -sourcepath src/ src/**/*.java`


#Run by hand

Please make sure that the dependancies exist first and that the source has definatly been compiled.

`java -cp .:bin/:gson-2.2.4.jar:javax.mail.jar core.Start`


#Make commands

`Make prep` - Downloads depedancies and prepares the application to make it executable.

`Make clean` - Cleans the dir of crap.

`Make compile` - Compiles the code.

`Make run` - runs the compiled sourcecode.

#Coding standards

Method names - `methodName()` Should use camal case notation

Variable names - `variable_Name` should have an underscore seperating each word with letters capatalised after first word.

Class names - HelloWorld `Should be Capitals for every first letter of a word but no underscores.

Comments - Javadoc should be used for detailing a method along with `/ Helloworld` for detailing code.

Plugins - All plugins should be written in the root directory of `src/` and must use the PluginTemp interface, any additional classes must be put under package `addon.*pluginname*`.
