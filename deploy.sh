#!/usr/bin/env bash

# PID file
PID_FILE_NAME="pid.log"

# File to upload
WEB_PATH="codeforum-web"
EXECUTABLE_JAR_NAME="codeforum-web-0.0.1-SNAPSHOT.jar"
TMP_EXECUTABLE_JAR_NAME="${EXECUTABLE_JAR_NAME}.tmp"
BAK_EXECUTABLE_JAR_NAME="${EXECUTABLE_JAR_NAME}.bak"
EXECUTABLE_JAR_PATH="./${WEB_PATH}/target/${EXECUTABLE_JAR_NAME}"

DEPLOY_SCRIPT="deploy.sh"
START_FUNC_NAME="start"
STOP_FUNC_NAME="stop"
RESTART_FUNC_NAME="restart"

# Environment, SSH remote, working directory
ENV_PRO="prod"
SSH_HOST_PRO=("admin@39.105.208.175")
WORK_DIR_PRO="/home/admin/workspace/codeforum/"

# Log file
declare LOG_FILES
LOG_BACKUP_FOLDER="logs/"

function stop() {
    # Kill the application
    echo "--- Stopping the application ---"
    if [ -f "${PID_FILE_NAME}" ]; then
        pid=$(cat ${PID_FILE_NAME})
        echo "kill -9 ${pid}"
        kill -9 ${pid}
    fi
    echo "----------------"
}

function start() {
    work_dir=`dirname $0`
    cd ${work_dir}

    stop

    mv ${EXECUTABLE_JAR_NAME} ${BAK_EXECUTABLE_JAR_NAME}
    mv ${TMP_EXECUTABLE_JAR_NAME} ${EXECUTABLE_JAR_NAME}

    chmod 755 ${EXECUTABLE_JAR_NAME}
    # Run the application
    echo "===== Starting the application: ====="
    run
}

function restart() {
  work_dir=`dirname $0`
  cd ${work_dir}
  stop
  # Run the application
  echo "===== Restarting the application: ====="
  run
}

function run() {
  echo "nohup java -server -Xms512m -Xmx512m -Xmn512m -XX:NativeMemoryTracking=detail -XX:-OmitStackTraceInFastThrow -jar ${EXECUTABLE_JAR_NAME} > /dev/null 2>&1 &"
  echo "====================================="
  nohup java -server -Dspring.devtools.restart.enabled=false -Xms512m -Xmx512m -Xmn512m -XX:NativeMemoryTracking=detail -XX:-OmitStackTraceInFastThrow -jar ${EXECUTABLE_JAR_NAME} "$@" > /dev/null 2>&1 &
  echo $! > ${PID_FILE_NAME}
}

function compile() {
    echo "---- Starting to build the JAR file ----"
    echo "Installing dependencies: mvn clean install -Dmaven.test.skip=True -P${1}"
    mvn clean install -Dmaven.test.skip=True -P${1}
    cd ${WEB_PATH}
    echo "Building runnable JAR: mvn clean package spring-boot:repackage -Dmaven.test.skip=true -P${1}"
    mvn clean package spring-boot:repackage -Dmaven.test.skip=true -P${1}
    cd -
    ret=$?
    if [[ ${ret} -ne 0 ]] ; then
        return 1
    fi
    echo "---------- JAR file build completed -------------"
}

function upload() {
    # Upload the JAR file
    # Rename to *.jar.bak
    scp ${EXECUTABLE_JAR_PATH} $1:$2${TMP_EXECUTABLE_JAR_NAME}
    ret=$?
    if [[ ${ret} -ne 0 ]] ; then
        echo 'Failed to upload the JAR file'
        return 1
    fi

    # Upload the deploy.sh script
    scp ${DEPLOY_SCRIPT} $1:$2
    ret=$?
    if [[ ${ret} -ne 0 ]] ; then
        echo 'Failed to upload deploy.sh'
        return 1
    fi
}

function deploy() {
    # Package
    echo "******* Start to package *******"
    compile $1
    ret=$?
    if [[ ${ret} -ne 0 ]] ; then
        echo 'Failed to compile'
        exit ${ret}
    fi

    if [ "$1" = "${ENV_PRO}" ]; then
        SSH_HOST=${SSH_HOST_PRO[@]}
        WORK_DIR=${WORK_DIR_PRO}
    else
        echo "Unknown environment: $1"
        exit
    fi

    for host in ${SSH_HOST[@]}
    do
        # Upload JAR and deploy.sh
        echo "******* Start to upload: ${host} *******"
        upload ${host} ${WORK_DIR}
        ret=$?
        if [[ ${ret} -ne 0 ]] ; then
            echo 'Failed to upload files'
            exit ${ret}
        fi
    done

    for host in ${SSH_HOST[@]}
    do
        # Run the application
        echo "******* Start service: ${host} *******"
        ssh ${host} "bash ${WORK_DIR}${DEPLOY_SCRIPT} ${START_FUNC_NAME}"
        echo "******* Done *******"
    done
}

# Check for command-line arguments
if [ "$1" = "${START_FUNC_NAME}" ]; then
    start "$@"
elif [ "$1" = "${ENV_PRO}" ]; then
    deploy $1
elif [ "$1" = "${STOP_FUNC_NAME}" ]; then
    stop
elif [ "$1" = "${RESTART_FUNC_NAME}" ]; then
    restart
else
    echo "Deploy the JAR to the server:  ./deploy.sh prod"
    echo "Restart the application on the server: ./deploy.sh restart"
    echo "Stop the application on the server: ./deploy.sh stop"
fi
