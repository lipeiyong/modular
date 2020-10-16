package com.komlin.libcommon.util;

import androidx.lifecycle.MutableLiveData;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import timber.log.Timber;

/**
 * @author lipeiyong
 */
public class AppExecutors {

    private static volatile AppExecutors instance;

    private static final int SCHEDULED_CORE_POOL_SIZE = 10;

    private final ExecutorService diskIO;

    private final ExecutorService networkIO;

    private final ScheduledExecutorService mainScheduledExecutor;

    private final ScheduledExecutorService scheduledExecutor;

    /**
     * 单一实例
     */
    public static AppExecutors getInstance() {
        if (instance == null) {
            instance = new AppExecutors();
        }
        return instance;
    }


    public AppExecutors(ExecutorService diskIO, ExecutorService networkIO, ScheduledExecutorService mainScheduledExecutor, ScheduledExecutorService scheduledExecutor) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainScheduledExecutor = mainScheduledExecutor;
        this.scheduledExecutor = scheduledExecutor;
    }

    public AppExecutors() {
        this(diskIoExecutor(), networkExecutor(), new MainScheduledExecutorService(), scheduledThreadPoolExecutor());
    }


    /**
     * 使用原则:
     * <p>
     * 1,和磁盘操作有关的进行使用此线程
     * <p>
     * 2,此线程禁止延迟,避免等待
     * <p>
     * 3,如果需要获取此线程的执行进度,那么必须再线程开始执行前调用{@link MutableLiveData#postValue(Object)}
     * (注意不要在非主线程调用{@link MutableLiveData#setValue(Object)})
     */
    public ExecutorService diskIO() {
        /*Timber.i("diskIO %s", getMethods());*/
        return diskIO;
    }

    /**
     * 使用原则:
     * <p>
     * 1,此线程不要延迟,可以等待
     * <p>
     * 2,如果需要获取此线程的执行进度,那么必须再线程开始执行前调用{@link MutableLiveData#postValue(Object)}
     * (注意不要在非主线程调用{@link MutableLiveData#setValue(Object)})
     */
    public ExecutorService networkIO() {
        /*Timber.i("networkIO %s", getMethods());*/
        return networkIO;
    }

    public ScheduledExecutorService scheduledExecutor() {
        /*Timber.i("scheduledExecutor %s", getMethods());*/
        return scheduledExecutor;
    }

    public ScheduledExecutorService mainThread() {
        /*Timber.i("mainScheduledExecutor %s", getMethods());*/
        return mainScheduledExecutor;
    }

    private static ScheduledExecutorService scheduledThreadPoolExecutor() {
        return new ScheduledThreadPoolExecutor(SCHEDULED_CORE_POOL_SIZE, r -> new Thread(r, "scheduled_executor"), (r, executor) -> Timber.e("rejectedExecution: scheduled executor queue overflow"));
    }

    private static ExecutorService diskIoExecutor() {
        return new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(1024), r -> new Thread(r, "disk_executor"), (r, executor) -> Timber.e("rejectedExecution: disk io executor queue overflow"));
    }

    private static ExecutorService networkExecutor() {
        return new ThreadPoolExecutor(3, 8, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(8), r -> new Thread(r, "network_executor"), (r, executor) -> Timber.e("rejectedExecution: network executor queue overflow"));
    }

    private static class MainThreadExecutor implements Executor {
        private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }

    private static class MainScheduledExecutorService implements ScheduledExecutorService {

        private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
        private ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1, r -> new Thread(r, "scheduled_executor"), (r, executor) -> Timber.e("rejectedExecution: scheduled executor queue overflow"));

        @NonNull
        @Override
        public ScheduledFuture<?> schedule(@NonNull Runnable command, long delay, @NonNull TimeUnit unit) {
            return scheduledExecutorService.schedule(() -> {
                execute(command);
            }, delay, unit);
        }

        @NonNull
        @Override
        public <V> ScheduledFuture<V> schedule(@NonNull Callable<V> callable, long delay, @NonNull TimeUnit unit) {
            // TODO: 18-4-23  remove  UnsupportedOperationException
            throw new UnsupportedOperationException("UnSupport !");
        }

        @NonNull
        @Override
        public ScheduledFuture<?> scheduleAtFixedRate(@NonNull Runnable command, long initialDelay, long period, @NonNull TimeUnit unit) {
            return scheduledExecutorService.scheduleAtFixedRate(() -> execute(command), initialDelay, period, unit);
        }

        @NonNull
        @Override
        public ScheduledFuture<?> scheduleWithFixedDelay(@NonNull Runnable command, long initialDelay, long delay, @NonNull TimeUnit unit) {
            return scheduledExecutorService.scheduleWithFixedDelay(() -> execute(command), initialDelay, delay, unit);
        }

        @Override
        public void shutdown() {
            scheduledExecutorService.shutdown();
        }

        @NonNull
        @Override
        public List<Runnable> shutdownNow() {
            return scheduledExecutorService.shutdownNow();
        }

        @Override
        public boolean isShutdown() {
            return scheduledExecutorService.isShutdown();
        }

        @Override
        public boolean isTerminated() {
            return scheduledExecutorService.isTerminated();
        }

        @Override
        public boolean awaitTermination(long timeout, @NonNull TimeUnit unit) throws InterruptedException {
            return scheduledExecutorService.awaitTermination(timeout, unit);
        }

        @NonNull
        @Override
        public <T> Future<T> submit(@NonNull Callable<T> task) {
            return scheduledExecutorService.submit(task);
        }

        @NonNull
        @Override
        public <T> Future<T> submit(@NonNull Runnable task, T result) {
            return scheduledExecutorService.submit(task, result);
        }

        @NonNull
        @Override
        public Future<?> submit(@NonNull Runnable task) {
            return scheduledExecutorService.submit(task);
        }

        @NonNull
        @Override
        public <T> List<Future<T>> invokeAll(@NonNull Collection<? extends Callable<T>> tasks) throws InterruptedException {
            return scheduledExecutorService.invokeAll(tasks);
        }

        @NonNull
        @Override
        public <T> List<Future<T>> invokeAll(@NonNull Collection<? extends Callable<T>> tasks, long timeout, @NonNull TimeUnit unit) throws InterruptedException {
            return scheduledExecutorService.invokeAll(tasks, timeout, unit);
        }

        @NonNull
        @Override
        public <T> T invokeAny(@NonNull Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
            return scheduledExecutorService.invokeAny(tasks);
        }

        @Override
        public <T> T invokeAny(@NonNull Collection<? extends Callable<T>> tasks, long timeout, @NonNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return scheduledExecutorService.invokeAny(tasks, timeout, unit);
        }

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }


}
