# compile
javac CtrlcTest.java -cp czmq-jni-0.1.0-SNAPSHOT.jar:zyre-jni-0.1.0-SNAPSHOT.jar

# run
java -Djava.library.path=./ -classpath .:czmq-jni-0.1.0-SNAPSHOT.jar:zyre-jni-0.1.0-SNAPSHOT.jar CtrlcTest 
