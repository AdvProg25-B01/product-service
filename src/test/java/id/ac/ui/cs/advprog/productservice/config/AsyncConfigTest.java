package id.ac.ui.cs.advprog.productservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AsyncConfigTest {

    @Autowired
    @Qualifier("customTaskExecutor")
    private Executor customTaskExecutor;

    @Test
    void testCustomTaskExecutorBeanExists() {
        assertNotNull(customTaskExecutor, "customTaskExecutor bean should not be null");
    }

    @Test
    void testCustomTaskExecutorRunsTask() throws Exception {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            String threadName = Thread.currentThread().getName();
            System.out.println("Running on thread: " + threadName);
            return threadName;
        }, customTaskExecutor);

        String threadUsed = future.get(); // blocking just for test
        assertTrue(threadUsed.startsWith("AsyncExecutor-"), "Thread name should start with 'AsyncExecutor-'");
    }
}
