package com.hammertech.onlinecase.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class TestWhileService {

    boolean breakLoop = false;
    Map<String, Integer> map = new ConcurrentHashMap<>();
    /**
     *
     * @param threadName 指定线程名
     */
    private void whileTrue(String threadName) {    // 不设置退出条件，死循环
        while (true && !breakLoop) {
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
                whileTrue("bug-thread-" + finalI);
            }).start();

        }// end for
    }//  end testWhile

    public void disableLoop() {

        breakLoop = true;
        log.info("disableLoop|breakLoop|{}", breakLoop);
    }

    public void enableLoop() {
        breakLoop = false;
        log.info("enableLoop|enableLoop|{}", breakLoop);
    }
}
