#!/usr/bin/env bash

WEB_PATH="codeforum-web"
JAR_NAME="codeforum-web-0.0.1-SNAPSHOT.jar"

# Deployment
function start() {
    git pull

    # Kill previous processes
    cat pid.log | xargs -I {} kill {}
    mv ${JAR_NAME} ${JAR_NAME}.bak

    mvn clean install -Dmaven.test.skip=True -Pprod
    cd ${WEB_PATH}
    mvn clean package spring-boot:repackage -Dmaven.test.skip=true -Pprod
    cd -

    mv ${WEB_PATH}/target/${JAR_NAME} ./
    run
}

# Restart
function restart() {
    # Kill previous processes
    cat pid.log | xargs -I {} kill {}
    # Restart
    run
}

# Run the application
function run() {
    echo "Start Script: =========="
    echo "nohup java -server -Xms1g -Xmx1g -Xmn512m -XX:NativeMemoryTracking=detail -XX:-OmitStackTraceInFastThrow -jar ${JAR_NAME} > /dev/null 2>&1 &"
    echo "========="
    # -Xms heap size
    # -Xmx maximum heap size
    # -Xmn new generation size
    nohup java -server -Dspring.devtools.restart.enabled=false -Xms1g -Xmx1g -Xmn256m -XX:NativeMemoryTracking=detail -XX:-OmitStackTraceInFastThrow -jar ${JAR_NAME} > /dev/null 2>&1 &
    echo $! 1> pid.log
}

# Check for command-line arguments
if [ $# == 0 ]; then
    echo "Missing command: start | restart"
elif [ $1 == 'start' ]; then
    start
elif [ $1 == 'restart' ];then
    restart
else
    echo 'Illegal command, supported commands: start | restart'
fi
