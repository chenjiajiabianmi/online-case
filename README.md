# cpu overload

# 代码

mock cpu overload 相关的 code

```java
package com.hammertech.onlinecase.controller;

import com.hammertech.onlinecase.service.TestWhileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RequestMapping
@Controller
public class testWhileController {

    @Autowired
    private TestWhileService testWhileService;
    @GetMapping("/testWhile")
    public String testWhile(@RequestParam int size) {
        log.info("enter testWhile with param | size | {}", size);
        testWhileService.testWhile(size);
        return "test triggered !!!";
    }
}

```

```java
package com.hammertech.onlinecase.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class TestWhileService {
    ConcurrentHashMap map = new ConcurrentHashMap();
    /**
     *
     * @param threadName 指定线程名
     */
    private void whileTrue(String threadName) {    // 不设置退出条件，死循环
        while (true) {
            // 在死循环中不断的对map执行put操作，导致内存gc
            for( int i = 0; i <= 100000; i ++) {
                map.put(Thread.currentThread().getName() + i, i);
            } // end for
        }
    }

    public void testWhile(int size) {        // 循环size，创建多线程，并发执行死循环
        for (int i = 0; i < size; i++) {
            int finalI = i;
            // 新建并启动线程，调用whileTrue方法
            new Thread(() -> {
                whileTrue("test cpu 100% -" + finalI);
            }).start();

        }// end for
    }//  end testWhile
}

```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.2.3</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.hammer-tech</groupId>
	<artifactId>online-case</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>online-case</name>
	<description>demo online cases</description>
	<properties>
		<java.version>17</java.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<version>1.18.30</version>
			<optional>true</optional>
		</dependency>

	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>

</project>

```

```docker
# 使用 Ubuntu 作为基础镜像
FROM ubuntu:latest

# 更新系统并安装top命令和Java 17
RUN apt-get update && \
    apt-get install -y procps && \
    apt-get install -y openjdk-17-jdk

# 设置工作目录
WORKDIR /app

# 复制应用程序 JAR 文件到镜像中
COPY target/online-case-0.0.1-SNAPSHOT.jar /app/online-case-0.0.1-SNAPSHOT.jar

# 定义入口命令，运行 Java 应用程序
CMD ["java", "-jar", "online-case-0.0.1-SNAPSHOT.jar"]
```

# Case 1

启动后

我们通过接口先创建一个死循环线程

```bash
http://localhost:9999/api/testWhile?size=1
```

看到日志中

```prolog
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.3)

2024-02-27T07:01:41.200Z  INFO 1 --- [           main] c.h.onlinecase.OnlineCaseApplication     : Starting OnlineCaseApplication v0.0.1-SNAPSHOT using Java 17.0.10 with PID 1 (/app/online-case-0.0.1-SNAPSHOT.jar started by root in /app)
2024-02-27T07:01:41.203Z  INFO 1 --- [           main] c.h.onlinecase.OnlineCaseApplication     : No active profile set, falling back to 1 default profile: "default"
2024-02-27T07:01:41.886Z  INFO 1 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 9999 (http)
2024-02-27T07:01:41.899Z  INFO 1 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2024-02-27T07:01:41.899Z  INFO 1 --- [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.19]
2024-02-27T07:01:41.920Z  INFO 1 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/api]    : Initializing Spring embedded WebApplicationContext
2024-02-27T07:01:41.921Z  INFO 1 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 648 ms
2024-02-27T07:01:42.137Z  INFO 1 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 9999 (http) with context path '/api'
2024-02-27T07:01:42.146Z  INFO 1 --- [           main] c.h.onlinecase.OnlineCaseApplication     : Started OnlineCaseApplication in 1.241 seconds (process running for 1.616)
2024-02-27T07:02:06.686Z  INFO 1 --- [nio-9999-exec-1] o.a.c.c.C.[Tomcat].[localhost].[/api]    : Initializing Spring DispatcherServlet 'dispatcherServlet'
2024-02-27T07:02:06.687Z  INFO 1 --- [nio-9999-exec-1] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2024-02-27T07:02:06.691Z  INFO 1 --- [nio-9999-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 2 ms
2024-02-27T07:02:06.736Z  INFO 1 --- [nio-9999-exec-1] c.h.o.controller.testWhileController     : enter testWhile with param | size | 1
```

进入docker容器

```bash
hammer@hammerdeMBP ~ % docker exec -it a2cb565a75a0 bash
root@a2cb565a75a0:/app#
```

执行top,

```bash
root@a2cb565a75a0:/app# top
```

看到top的结果，进程1 cpu达到100%

```bash
top - 07:19:32 up 2 days, 21:22,  0 users,  load average: 1.48, 2.47, 4.52
Tasks:   3 total,   1 running,   2 sleeping,   0 stopped,   0 zombie
%Cpu(s): 32.2 us,  1.7 sy,  0.0 ni, 66.1 id,  0.0 wa,  0.0 hi,  0.0 si,  0.0 st
MiB Mem :   7851.6 total,   1397.9 free,   4109.4 used,   2344.2 buff/cache
MiB Swap:   1024.0 total,    959.0 free,     65.0 used.   3268.9 avail Mem 

  PID USER      PR  NI    VIRT    RES    SHR S  %CPU  %MEM     TIME+ COMMAND                                                                                                                
    1 root      20   0 5776096 983704  21692 S 100.0  12.2  12:55.92 java                                                                                                                   
   52 root      20   0    4136   3404   2912 S   0.0   0.0   0:00.00 bash                                                                                                                   
   60 root      20   0    6724   2408   2084 R   0.0   0.0   0:00.01 top         
```

在pid 1对应的 进程中找到cpu占用高的 线程

```bash
root@a2cb565a75a0:/app# top -H -p 1
```

看到前10个线程都有很高的cpu占用

```bash
top - 07:21:43 up 2 days, 21:24,  0 users,  load average: 1.21, 2.03, 4.09
Threads:  42 total,   1 running,  41 sleeping,   0 stopped,   0 zombie
%Cpu(s): 27.3 us,  0.7 sy,  0.0 ni, 71.9 id,  0.0 wa,  0.0 hi,  0.2 si,  0.0 st
MiB Mem :   7851.6 total,   1399.6 free,   4107.5 used,   2344.5 buff/cache
MiB Swap:   1024.0 total,    959.0 free,     65.0 used.   3270.9 avail Mem 

  PID USER      PR  NI    VIRT    RES    SHR S  %CPU  %MEM     TIME+ COMMAND                                                                                                                
   47 root      20   0 5776096 983968  21692 R  99.0  12.2  14:30.08 Thread-1                                                                                                               
    8 root      20   0 5776096 983968  21692 S   1.0  12.2   0:06.31 GC Thread#0                                                                                                            
   27 root      20   0 5776096 983968  21692 S   0.7  12.2   0:06.28 GC Thread#1                                                                                                            
   28 root      20   0 5776096 983968  21692 S   0.7  12.2   0:06.38 GC Thread#2                                                                                                            
   12 root      20   0 5776096 983968  21692 S   0.3  12.2   0:00.19 G1 Service                                                                                                             
   30 root      20   0 5776096 983968  21692 S   0.3  12.2   0:06.31 GC Thread#3                                                                                                            
    1 root      20   0 5776096 983968  21692 S   0.0  12.2   0:00.02 java                                                                                                                   
    7 root      20   0 5776096 983968  21692 S   0.0  12.2   0:01.35 java                                                                                                                   
    9 root      20   0 5776096 983968  21692 S   0.0  12.2   0:00.00 G1 Main Marker                                                                                                         
   10 root      20   0 5776096 983968  21692 S   0.0  12.2   0:00.00 G1 Conc#0                                                                                                              
   11 root      20   0 5776096 983968  21692 S   0.0  12.2   0:03.39 G1 Refine#0                                                                                                            
   13 root      20   0 5776096 983968  21692 S   0.0  12.2   0:01.71 VM Thread                                                                                                              
   14 root      20   0 5776096 983968  21692 S   0.0  12.2   0:00.00 Reference Handl                                                                                                        
   15 root      20   0 5776096 983968  21692 S   0.0  12.2   0:00.00 Finalizer                                                                                                              
   16 root      20   0 5776096 983968  21692 S   0.0  12.2   0:00.00 Signal Dispatch                                                                                                        
   17 root      20   0 5776096 983968  21692 S   0.0  12.2   0:00.00 Service Thread                                                                                                         
   18 root      20   0 5776096 983968  21692 S   0.0  12.2   0:00.11 Monitor Deflati                                                                                                        
   19 root      20   0 5776096 983968  21692 S   0.0  12.2   0:01.57 C2 CompilerThre                                                                                                        
   20 root      20   0 5776096 983968  21692 S   0.0  12.2   0:00.74 C1 CompilerThre                                                                                                        
   21 root      20   0 5776096 983968  21692 S   0.0  12.2   0:00.00 Sweeper thread                                                                                                         
   22 root      20   0 5776096 983968  21692 S   0.0  12.2   0:00.00 Notification Th                                                                                                        
   23 root      20   0 5776096 983968  21692 S   0.0  12.2   0:00.58 VM Periodic Tas                                                                                                        
   24 root      20   0 5776096 983968  21692 S   0.0  12.2   0:00.00 Common-Cleaner                                                                                                         
   25 root      20   0 5776096 983968  21692 S   0.0  12.2   0:00.00 Cleaner-0                                                                                                              
   32 root      20   0 5776096 983968  21692 S   0.0  12.2   0:00.15 Catalina-utilit                                                                                                        
   33 root      20   0 5776096 983968  21692 S   0.0  12.2   0:00.17 Catalina-utilit                                                                                                        
   34 root      20   0 5776096 983968  21692 S   0.0  12.2   0:00.00 container-0                                                                                                            
   35 root      20   0 5776096 983968  21692 S   0.0  12.2   0:00.14 http-nio-9999-e                                                                                                        
   36 root      20   0 5776096 983968  21692 S   0.0  12.2   0:00.00 http-nio-9999-e                                                                                                        
   37 root      20   0 5776096 983968  21692 S   0.0  12.2   0:00.00 http-nio-9999-e                                                                                                        
   38 root      20   0 5776096 983968  21692 S   0.0  12.2   0:00.00 http-nio-9999-e                                                                                                        
   39 root      20   0 5776096 983968  21692 S   0.0  12.2   0:00.00 http-nio-9999-e                                                                                                        
   40 root      20   0 5776096 983968  21692 S   0.0  12.2   0:00.00 http-nio-9999-e                                                                                                        
   41 root      20   0 5776096 983968  21692 S   0.0  12.2   0:00.00 http-nio-9999-e                                                                                                        
   42 root      20   0 5776096 983968  21692 S   0.0  12.2   0:00.00 http-nio-9999-e                                                                                                        
   43 root      20   0 5776096 983968  21692 S   0.0  12.2   0:00.00 http-nio-9999-e                                                                                                        
   44 root      20   0 5776096 983968  21692 S   0.0  12.2   0:00.00 http-nio-9999-e                                                                                                        
   45 root      20   0 5776096 983968  21692 S   0.0  12.2   0:00.09 http-nio-9999-P                                                                                                        
   46 root      20   0 5776096 983968  21692 S   0.0  12.2   0:00.04 http-nio-9999-A  
```

看到线程47的cpu占用率达到了100% ， 用47这个线程查看具体的堆栈，用printf把47转成16进制，

```bash
root@a2cb565a75a0:/app# printf '%x\n' 47
2f
```

之后用jstack查看进程1下的线程55，命令如下

```bash
jstack **processId** ｜ grep ‘0x**threadId**’ -A10
```

查看进程下 特定线程的执行过程堆栈，注意这个堆栈只是当前瞬间的堆栈，随着程序的执行，这个堆栈是在不停的动态变化的

```bash

root@a2cb565a75a0:/app# jstack 1 | grep '0x2f' -A10
"Thread-1" #34 daemon prio=5 os_prio=0 cpu=1085680.11ms elapsed=1098.75s tid=0x0000fffef8036160 nid=0x2f runnable  [0x0000ffff3b3f9000]
   java.lang.Thread.State: RUNNABLE
	at java.util.concurrent.ConcurrentHashMap.put(java.base@17.0.10/ConcurrentHashMap.java:1006)
	at com.hammertech.onlinecase.service.TestWhileService.whileTrue(TestWhileService.java:18)
	at com.hammertech.onlinecase.service.TestWhileService.lambda$testWhile$0(TestWhileService.java:28)
	at com.hammertech.onlinecase.service.TestWhileService$$Lambda$774/0x00000068013d8b78.run(Unknown Source)
	at java.lang.Thread.run(java.base@17.0.10/Thread.java:840)

"Attach Listener" #35 daemon prio=9 os_prio=0 cpu=0.65ms elapsed=10.69s tid=0x0000ffff48000f90 nid=0x54 waiting on condition  [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE
```

或者，你可以把线程堆栈导入到一个dump文件

```bash
root@a2cb565a75a0:/app# jstack 1 > onlinecase.dump
```

然后查看这个dump文件

```bash
root@a2cb565a75a0:/app# cat -n onlinecase.dump | grep -A10 '0x2f'
   315	"Thread-1" #34 daemon prio=5 os_prio=0 cpu=1271643.43ms elapsed=1286.75s tid=0x0000fffef8036160 nid=0x2f runnable  [0x0000ffff3b3f9000]
   316	   java.lang.Thread.State: RUNNABLE
   317		at java.lang.StringLatin1.hashCode(java.base@17.0.10/StringLatin1.java:196)
   318		at java.lang.String.hashCode(java.base@17.0.10/String.java:2344)
   319		at java.util.concurrent.ConcurrentHashMap.putVal(java.base@17.0.10/ConcurrentHashMap.java:1012)
   320		at java.util.concurrent.ConcurrentHashMap.put(java.base@17.0.10/ConcurrentHashMap.java:1006)
   321		at com.hammertech.onlinecase.service.TestWhileService.whileTrue(TestWhileService.java:18)
   322		at com.hammertech.onlinecase.service.TestWhileService.lambda$testWhile$0(TestWhileService.java:28)
   323		at com.hammertech.onlinecase.service.TestWhileService$$Lambda$774/0x00000068013d8b78.run(Unknown Source)
   324		at java.lang.Thread.run(java.base@17.0.10/Thread.java:840)
   325	
root@a2cb565a75a0:/app# 
```

# Case 2

现在的场景相对简单，我们尝试一下开100个线程，看看会有什么变化

```java
http://localhost:9999/api/testWhile?size=100
```

进入docker容器，看进程1下的线程

```bash

hammer@hammerdeMacBook-Pro online-case % docker exec -it a2cb565a75a0 bash
root@a2cb565a75a0:/app# top -H -p 1
```

发现堆栈的情况似乎不太一样了

```bash
top - 07:46:23 up 2 days, 21:49,  0 users,  load average: 14.55, 11.09, 6.49
Threads: 142 total,   4 running, 138 sleeping,   0 stopped,   0 zombie
%Cpu(s): 95.2 us,  2.8 sy,  0.0 ni,  2.0 id,  0.0 wa,  0.0 hi,  0.0 si,  0.0 st
MiB Mem :   7851.6 total,    138.9 free,   5362.1 used,   2350.5 buff/cache
MiB Swap:   1024.0 total,    959.0 free,     65.0 used.   2016.1 avail Mem 

  PID USER      PR  NI    VIRT    RES    SHR S  %CPU  %MEM     TIME+ COMMAND                                                                                                                
   27 root      20   0 5978848   2.1g  21824 R  47.0  28.0   3:18.86 GC Thread#1                                                                                                            
   28 root      20   0 5978848   2.1g  21824 R  46.1  28.0   3:18.93 GC Thread#2                                                                                                            
    8 root      20   0 5978848   2.1g  21824 R  44.5  28.0   3:19.07 GC Thread#0                                                                                                            
   30 root      20   0 5978848   2.1g  21824 R  43.9  28.0   3:18.65 GC Thread#3                                                                                                            
   10 root      20   0 5978848   2.1g  21824 S   6.3  28.0   0:31.58 G1 Conc#0                                                                                                              
   11 root      20   0 5978848   2.1g  21824 S   5.0  28.0   0:29.61 G1 Refine#0                                                                                                            
   71 root      20   0 5978848   2.1g  21824 S   4.7  28.0   0:27.58 G1 Refine#3                                                                                                            
   59 root      20   0 5978848   2.1g  21824 S   4.1  28.0   0:28.46 G1 Refine#1                                                                                                            
   69 root      20   0 5978848   2.1g  21824 S   4.1  28.0   0:27.56 G1 Refine#2                                                                                                            
   49 root      20   0 5978848   2.1g  21824 S   1.9  28.0   0:05.42 Thread-2                                                                                                               
   51 root      20   0 5978848   2.1g  21824 S   1.9  28.0   0:05.43 Thread-4                                                                                                               
   65 root      20   0 5978848   2.1g  21824 S   1.9  28.0   0:05.52 Thread-17                                                                                                              
   72 root      20   0 5978848   2.1g  21824 S   1.9  28.0   0:05.51 Thread-22                                                                                                              
   76 root      20   0 5978848   2.1g  21824 S   1.9  28.0   0:05.49 Thread-26                                                                                                              
  120 root      20   0 5978848   2.1g  21824 S   1.9  28.0   0:05.21 Thread-70                                                                                                              
   52 root      20   0 5978848   2.1g  21824 S   1.6  28.0   0:05.45 Thread-5                                                                                                               
   54 root      20   0 5978848   2.1g  21824 S   1.6  28.0   0:05.37 Thread-7                                                                                                               
   57 root      20   0 5978848   2.1g  21824 S   1.6  28.0   0:05.24 Thread-10                                                                                                              
   58 root      20   0 5978848   2.1g  21824 S   1.6  28.0   0:05.38 Thread-11                                                                                                              
   63 root      20   0 5978848   2.1g  21824 S   1.6  28.0   0:05.28 Thread-15                                                                                                              
   64 root      20   0 5978848   2.1g  21824 S   1.6  28.0   0:05.31 Thread-16                                                                                                              
   68 root      20   0 5978848   2.1g  21824 S   1.6  28.0   0:05.34 Thread-20                                                                                                              
   80 root      20   0 5978848   2.1g  21824 S   1.6  28.0   0:05.34 Thread-30                                                                                                              
   81 root      20   0 5978848   2.1g  21824 S   1.6  28.0   0:05.30 Thread-31                                                                                                              
   82 root      20   0 5978848   2.1g  21824 S   1.6  28.0   0:05.32 Thread-32                                                                                                              
   89 root      20   0 5978848   2.1g  21824 S   1.6  28.0   0:05.41 Thread-39                                                                                                              
   90 root      20   0 5978848   2.1g  21824 S   1.6  28.0   0:05.35 Thread-40                                                                                                              
   94 root      20   0 5978848   2.1g  21824 S   1.6  28.0   0:05.29 Thread-44                                                                                                              
   98 root      20   0 5978848   2.1g  21824 S   1.6  28.0   0:05.26 Thread-48                                                                                                              
  105 root      20   0 5978848   2.1g  21824 S   1.6  28.0   0:05.22 Thread-55                                                                                                              
  106 root      20   0 5978848   2.1g  21824 S   1.6  28.0   0:05.43 Thread-56                                                                                                              
  107 root      20   0 5978848   2.1g  21824 S   1.6  28.0   0:05.31 Thread-57                                                                                                              
  114 root      20   0 5978848   2.1g  21824 S   1.6  28.0   0:05.29 Thread-64                                                                                                              
  121 root      20   0 5978848   2.1g  21824 S   1.6  28.0   0:05.26 Thread-71                                                                                                              
  122 root      20   0 5978848   2.1g  21824 S   1.6  28.0   0:05.19 Thread-72                                                                                                              
  132 root      20   0 5978848   2.1g  21824 S   1.6  28.0   0:05.38 Thread-82                                                                                                              
  134 root      20   0 5978848   2.1g  21824 S   1.6  28.0   0:05.34 Thread-84                                                                                                              
  137 root      20   0 5978848   2.1g  21824 S   1.6  28.0   0:05.41 Thread-87                                                                                                              
  138 root      20   0 5978848   2.1g  21824 S   1.6  28.0   0:05.41 Thread-88                                                                                                              

```

注意到cpu占用最高的并不是业务型的thread，而是gc thread

查看一个gc thread堆栈

```bash
root@a2cb565a75a0:/app# printf '%x\n' 30    
1e
root@a2cb565a75a0:/app# jstack 1 | grep '0x1e' -A10
"GC Thread#3" os_prio=0 cpu=166119.51ms elapsed=360.96s tid=0x0000ffff480070b0 nid=0x1e runnable  

"G1 Main Marker" os_prio=0 cpu=18.68ms elapsed=361.55s tid=0x0000ffff8407d090 nid=0x9 runnable  

"G1 Conc#0" os_prio=0 cpu=26679.29ms elapsed=361.55s tid=0x0000ffff8407e010 nid=0xa runnable  

"G1 Refine#0" os_prio=0 cpu=25152.17ms elapsed=361.55s tid=0x0000ffff840e31a0 nid=0xb runnable  

"G1 Refine#1" os_prio=0 cpu=24001.61ms elapsed=339.28s tid=0x0000ffff54007c20 nid=0x3b runnable  

"G1 Refine#2" os_prio=0 cpu=23369.26ms elapsed=339.27s tid=0x0000ffff300013c0 nid=0x45 runnable
```

用jstat 查看gc的执行情况

```bash
jstat -gcutil 进程id 采样间隔毫秒 采样次数
```

我们对进程1进行1000毫秒一次的采样，总共进行20次，可以看到从服务启动开始，fgc的次数已经达到110次，并且最近的20秒进行了4次fullgc