#!/bin/bash
cd "$(dirname "$0")"
find src -name "*.java" > sources.txt
javac -cp "libs/*" -d out @sources.txt
java -cp "out:libs/*" Main
