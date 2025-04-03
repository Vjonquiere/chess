#!/bin/sh

JAR=chess-0.0.4.jar

if [ ! -f "$JAR" ]; then
    echo "$0: jar not found!"
    exit 1
fi

java -jar $JAR $@
