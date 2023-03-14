#!/bin/bash
gradle app:build
java -jar ./app/build/libs/app-standalone.jar