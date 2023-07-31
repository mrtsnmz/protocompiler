#!/usr/bin/env bash

appDir="/Users/mertsonmez/Downloads/springboot-crud-demo-master/target/classes"
packageDir="/com/ensat/controllers"
outputDir="/Users/mertsonmez/Desktop/java/outputs"

java -jar ProtoCompiler.jar $appDir $packageDir $outputDir