# 如需强制指定镜像的操作系统架构, 可在FROM后添加--platform参数，例如--platform=linux/amd64
# 以下列举了常用的OpenJDK版本供使用
# FROM adoptopenjdk/openjdk8:debian
# FROM openjdk:17.0.2-oraclelinux8

FROM openjdk:11.0.16-oraclelinux8 AS builder

# 指定jar包名称
ENV JAR_NAME=youlan-admin.jar

# 指定工作目录
WORKDIR application

# 拷贝target目录中的jar
COPY target/$JAR_NAME application.jar

# 对原始jar包做分层拆分处理，此步骤可显著降低镜像push/pull时间
RUN java -Djarmode=layertools -jar application.jar extract

FROM openjdk:11.0.16-oraclelinux8

# 指定工作目录
WORKDIR application

# 复制分层目录
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/application/ ./

# 指定时区
ENV TZ=Asia/Shanghai

# 不建议使用ENTRYPOINT，CMD会更灵活
CMD java -Dspring.profiles.active=prod $JAR_OPTS $JVM_OPTS org.springframework.boot.loader.JarLauncher