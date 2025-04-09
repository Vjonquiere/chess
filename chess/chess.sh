#!/bin/sh

VERSION=$(grep -oPm1 "(?<=<version>)[^<]+" pom.xml)

JAR="./target/chess-${VERSION}.jar"

if echo "$@" | grep -q -- "--clean"; then
    echo "$0: Running mvn clean"
    mvn clean
    if [ $? -ne 0 ]; then
        echo "$0: mvn clean failed!"
        exit 1
    fi
    exit 0
fi

if [ ! -f "$JAR" ]; then
    echo "$0: jar not found! Running mvn clean package..."
    mvn clean package
    if [ $? -ne 0 ]; then
        echo "$0: mvn clean package failed!"
        exit 1
    fi
fi

java -jar $JAR "$@"
