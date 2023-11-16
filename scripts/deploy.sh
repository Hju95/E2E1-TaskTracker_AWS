#!/usr/bin/env bash

echo "> 현재 시간: $(date)" >> /home/ec2-user/E2E1-TaskTracker/deploy.log

REPOSITORY=/home/ec2-user/E2E1-TaskTracker
cd $REPOSITORY

APP_NAME=E2E1-TaskTracker
JAR_NAME=$(ls $REPOSITORY/build/libs/ | grep 'SNAPSHOT.jar' | tail -n 1)
JAR_PATH=$REPOSITORY/build/libs/$JAR_NAME

echo "> build 파일명: $JAR_NAME" >> /home/ec2-user/E2E1-TaskTracker/deploy.log
echo "> build 파일 복사" >> /home/ec2-user/E2E1-TaskTracker/deploy.log

echo "> 현재 실행중인 애플리케이션 pid 확인" >> /home/ec2-user/E2E1-TaskTracker/deploy.log
CURRENT_PID=$(pgrep -f $APP_NAME)

if [ -z $CURRENT_PID ]
then
  echo "> 종료할것 없음."
else
  echo "> kill -9 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

echo "> $JAR_PATH 배포"    >> /home/ec2-user/E2E1-TaskTracker/deploy.log
echo "> $JAR_PATH 배포"
sudo nohup java -jar $JAR_PATH >> /home/ec2-user/E2E1-TaskTracker/tomcat.log 2> /home/ec2-user/E2E1-TaskTracker/deploy_err.log &
