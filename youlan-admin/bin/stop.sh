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

# 判断是否存在pid文件
if [ ! -f "pid" ]; then
  log_info "服务可能未启动"
  exit 0
fi

# 获取进程ID
pid=$(cat pid)
pid=$(ps -ef | grep java | grep "$pid" | grep -v grep | awk '{print $2}')

if [[ -n $pid ]]; then
  # 打印进程ID
  log_info "服务进程ID[$pid]"
  kill -9 $pid
fi

# 删除pid文件
rm -f pid
log_info "服务停止成功"
