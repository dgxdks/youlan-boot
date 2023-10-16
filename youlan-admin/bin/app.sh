#!/bin/bash

# 指定要启动的jar名称
JAR_NAME=youlan-admin.jar

# 指定Jar参数
JAR_OPTS="-Dspring.profiles.active=prod"

# 指定JVM参数
JVM_OPTS="-XX:+UseStringDeduplication -XX:+HeapDumpOnOutOfMemoryError -XX:+UseParallelGC -XX:+UseParallelOldGC"

# 获取当前脚本目录并进入
CURRENT_DIR=$(
  cd $(dirname $0)
  pwd
)
cd $CURRENT_DIR

# 定义INFO日志打印函数
log_info() {
  echo -e "\033[32m $1  \033[0m"
}

# 定义ERROR日志打印函数
log_error() {
  echo -e "\033[31m $1  \033[0m"
}

# 打印帮助信息
print_help() {
  log_error "使用方法：app.sh [命令]"
  log_error "支持以下命令："
  log_error "  start 启动服务"
  log_error "  stop 停止服务"
  log_error "  status 服务状态"
  log_error "  process 服务进程"
}

# 启动服务
start_app() {
  # 根据pid文件判断服务是否已启动
  if [ -f "pid" ]; then
    log_error "服务可能已启动，请先停止服务或删除${CURRENT_DIR}/pid文件后再试"
    exit 1
  fi
  # 使用nohup命令启动服务
  nohup java $JAR_OPTS $JVM_OPTS -jar $JAR_NAME >app.log 2>&1 &
  log_info "服务进程ID[$!]"
  log_info "服务启动成功，执行[ctrl + c]可安全退出当前日志查看"
  echo $! >pid
  tail -f app.log
}

# 停止服务
stop_app() {
  # 判断是否存在pid文件
  if [ ! -f "pid" ]; then
    log_error "服务可能未启动，未找到$CURRENT_DIR/pid文件"
    exit 1
  fi
  pid="$(cat pid)"
  pid=$(ps -ef | grep java | grep "$pid" | grep -v grep | awk '{print $2}')
  # 判断进程ID是否真的存在
  if [[ -n $pid ]]; then
    log_info "找到服务进程ID[$pid]"
    kill -9 "$(find_pid)"
  fi
  rm pid
  log_info "服务停止成功"
}

case $1 in
# 匹配启动指令
start)
  start_app
  ;;
# 匹配停止指令
stop)
  stop_app
  ;;
# 未匹配到直接打印帮助信息
*)
  print_help
  ;;
esac
