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
