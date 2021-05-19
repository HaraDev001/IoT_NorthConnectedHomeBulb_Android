package com.guohua.north_bulb.net;

import android.os.Handler;

import com.guohua.north_bulb.AppContext;
import com.guohua.north_bulb.util.CodeUtils;
import com.guohua.north_bulb.util.Constant;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 此类用于顺序执行线程 如此使用： ThreadPool pool = ThreadPool.getInstance();
 * pool.addTask(runnable);//顺序执行线程 pool.addOtherTask(runnable);//正常执行线程
 *
 * @author Leo
 * @time 2015-11-04
 */
public class ThreadPool {
    /* 單例 */
    private volatile static ThreadPool pool = null;

    public static ThreadPool getInstance() {
        if (pool == null) {
            synchronized (ThreadPool.class) {
                if (pool == null) {
                    pool = new ThreadPool();
                }
            }
        }
        return pool;
    }

    private ExecutorService taskService;//Java线程池

    public ThreadPool() {
        taskService = Executors.newSingleThreadExecutor();
//        taskService = Executors.newCachedThreadPool();//无界限带缓冲的线程池
    }

    /**
     * 添加任務
     *
     * @param r
     */
    public void addTask(Runnable r) {
        taskService.execute(r);//线程池执行
    }

    public void addMusicOffTask(Runnable r) {

        taskService.execute(r);//线程池执行

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AppContext.getInstance().sendAll(CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_SWITCH, new Object[]{CodeUtils.SWITCH_CLOSE}));
            }
        }, Constant.HANDLERDELAY);
    }

    public void addOtherTask(Runnable r) {
        new Thread(r).start();//随意执行
    }

    /**
     * 停止任務
     */
    public void stopTask() {
        taskService.shutdown();//停止线程池
    }

    /**
     * 立刻停止任務
     */
    public void stopTaskNow() {
        taskService.shutdownNow();//立刻停止
    }
}
