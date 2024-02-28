# 使用 Ubuntu 作为基础镜像
FROM ubuntu:latest

# 更新系统并安装 top 命令、telnet 和 Java 17
RUN apt-get update && \
    apt-get install -y procps telnet openjdk-17-jdk

# 安装 curl
RUN apt-get install -y curl

# 安装 unzip
RUN apt-get install -y unzip

# 设置工作目录
WORKDIR /app

# 复制应用程序 JAR 文件到镜像中
COPY target/online-case-0.0.1-SNAPSHOT.jar /app/online-case-0.0.1-SNAPSHOT.jar

# 定义入口命令，运行 Java 应用程序
CMD ["java", "-jar", "online-case-0.0.1-SNAPSHOT.jar"]
