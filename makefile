all: main

main: 
	javac *.java 

run:
	java -cp mysql-connector-java-5.1.47.jar:. Main