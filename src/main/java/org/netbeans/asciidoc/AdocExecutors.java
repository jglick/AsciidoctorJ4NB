package org.netbeans.asciidoc;

import java.util.concurrent.TimeUnit;
import org.jtrim.concurrent.DelegatedTaskExecutorService;
import org.jtrim.concurrent.MonitorableTaskExecutorService;
import org.jtrim.concurrent.SingleThreadedExecutor;
import org.jtrim.concurrent.ThreadPoolTaskExecutor;
import org.jtrim.utils.ExceptionHelper;

public final class AdocExecutors {
    public static final MonitorableTaskExecutorService DEFAULT_EXECUTOR
            = newExecutor("Asciidoc-Executor", getDefaultThreadCount(), 5000);

    private static final long DEFAULT_IDLE_TIMEOUT_MS = 1000;

    public static MonitorableTaskExecutorService newExecutor(String name, int threadCount) {
        return newExecutor(name, threadCount, DEFAULT_IDLE_TIMEOUT_MS);
    }

    public static MonitorableTaskExecutorService newExecutor(String name, int threadCount, long timeoutMs) {
        return new Unstoppable(newStoppableExecutor(name, threadCount));
    }

    public static MonitorableTaskExecutorService newStoppableExecutor(String name, int threadCount) {
        return newStoppableExecutor(name, threadCount, DEFAULT_IDLE_TIMEOUT_MS);
    }

    public static MonitorableTaskExecutorService newStoppableExecutor(String name, int threadCount, long timeoutMs) {
        ExceptionHelper.checkArgumentInRange(threadCount, 1, Integer.MAX_VALUE, "threadCount");

        if (threadCount == 1) {
            return new SingleThreadedExecutor(name, Integer.MAX_VALUE, timeoutMs, TimeUnit.MILLISECONDS);
        }
        else {
            return new ThreadPoolTaskExecutor(name, threadCount, Integer.MAX_VALUE, timeoutMs, TimeUnit.MILLISECONDS);
        }
    }

    private static int getDefaultThreadCount() {
        // We don't want too much thread, because there is little benefit
        // and many threads might need much more memory.
        return Math.min(Runtime.getRuntime().availableProcessors(), 8);
    }

    private static final class Unstoppable
    extends
            DelegatedTaskExecutorService
    implements
            MonitorableTaskExecutorService {

        private final MonitorableTaskExecutorService wrappedMonitorable;

        public Unstoppable(MonitorableTaskExecutorService wrappedExecutor) {
            super(wrappedExecutor);

            this.wrappedMonitorable = wrappedExecutor;
        }

        @Override
        public long getNumberOfQueuedTasks() {
            return wrappedMonitorable.getNumberOfQueuedTasks();
        }

        @Override
        public long getNumberOfExecutingTasks() {
            return wrappedMonitorable.getNumberOfExecutingTasks();
        }

        @Override
        public boolean isExecutingInThis() {
            return wrappedMonitorable.isExecutingInThis();
        }

        @Override
        public void shutdownAndCancel() {
            shutdown();
        }

        @Override
        public void shutdown() {
            throw new UnsupportedOperationException("This executor cannot be shutted down.");
        }
    }

    private AdocExecutors() {
        throw new AssertionError();
    }
}
