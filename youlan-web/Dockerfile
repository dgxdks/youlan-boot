# 如需强制指定镜像的操作系统架构, 可在FROM后添加--platform参数，例如--platform=linux/amd64
FROM node:14.21.3 AS builder

# 将当前项目复制到容器中准备构建
ADD . /youlan-web

# 构建前端项目
RUN cd /youlan-web && \
    npm install --registry=https://registry.npm.taobao.org && \
    npm run build:prod


FROM nginx:stable

# 复制nginx文件
COPY nginx/conf.d/ /etc/nginx/conf.d/
COPY nginx/nginx.conf /etc/nginx/
COPY nginx/ssl/ /etc/nginx/ssl/

# 复制部署文件
COPY --from=builder /youlan-web/dist/ /web/html


