rm bin/*
javac -source 1.8 -target 1.8 *.java -d ./bin
java -cp ./bin Main
