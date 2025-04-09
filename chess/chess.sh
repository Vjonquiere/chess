#!/bin/sh

VERSION=$(grep -oPm1 "(?<=<version>)[^<]+" pom.xml)

JAR="target/chess-${VERSION}.jar"
CHESS_CONFIG_DIR="$HOME/.chessSettings"
OLD_CHESS_CONFIG_DIR="$HOME/.chessThemes"

if echo "$@" | grep -q -- "--clean"; then
    echo "$0: Running mvn clean"
    mvn clean
    if [ $? -ne 0 ]; then
        echo "$0: mvn clean failed!"
        exit 1
    fi
    
    if [ -d "$CHESS_CONFIG_DIR" ]; then
        echo "$0: Deleting $CHESS_CONFIG_DIR"
        rm -rf "$CHESS_CONFIG_DIR"
        if [ $? -eq 0 ]; then
            echo "$0: Successfully deleted $CHESS_CONFIG_DIR"
        else
            echo "$0: Failed to delete $CHESS_CONFIG_DIR"
            exit 1
        fi
    else
        echo "$0: $CHESS_CONFIG_DIR does not exist"
    fi

    if [ -d "$OLD_CHESS_CONFIG_DIR" ]; then
        echo "$0: Deleting old config in $OLD_CHESS_CONFIG_DIR"
        rm -rf "$OLD_CHESS_CONFIG_DIR"
    fi
    
    exit 0
fi

if [ ! -f "$JAR" ]; then
    echo "$0: jar not found! Running mvn clean package..."
    mvn clean package -DskipTests
    if [ $? -ne 0 ]; then
        echo "$0: mvn clean package failed!"
        exit 1
    fi
fi

java -jar $JAR "$@"
