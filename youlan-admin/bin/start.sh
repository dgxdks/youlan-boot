#!/bin/bash

# 定义INFO日志打印函数
log_info() {
  echo -e "\033[32m $1  \033[0m"
}

# 获取当前脚本目录
CURRENT_DIR=$(
  cd $(dirname $0)
  pwd
)

cd $CURRENT_DIR

# 根据pid文件判断服务是否已启动
if [ -f "pid" ]; then
  log_info "服务可能已启动，请先停止服务或删除${CURRENT_DIR}/pid文件后再试"
  exit 0
fi

# 使用nohup命令启动服务
nohup java \
  -Dspring.profiles.active=prod \
  -XX:+UseStringDeduplication \
  -XX:ErrorFile=./hs_err_pid%p.log \
  -XX:HeapDumpPath=./ \
  -XX:+HeapDumpOnOutOfMemoryError \
  -jar \
  youlan-admin.jar >app.log &

# 打印启动信息
log_info "服务进程ID[$!]"
log_info "服务启动成功"
log_info "执行[ctrl + c]可退出app.log日志的查看"
echo $! >pid
tail -f app.log
