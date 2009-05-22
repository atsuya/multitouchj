#!/bin/bash


libraryPath='./Library'

classPath=''
for fileName in $(ls $libraryPath); do
    if [ ${fileName:(-4)} = ".jar" ]; then
       	if [ "$classPath" != "" ]; then
       		classPath=$classPath':'
       	fi
       	classPath=${classPath}${libraryPath}'/'${fileName}
    fi
done

classPath='./Build:./Resources:'$classPath

java -cp $classPath -Djava.library.path=$libraryPath org.junit.runner.JUnitCore edu.csun.ecs.cs.multitouchj.$@
