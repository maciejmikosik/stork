package com.mikosik.stork.test;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.quackery.QuackeryException.check;
import static org.quackery.help.Helpers.traverseBodies;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.quackery.Body;
import org.quackery.Test;

public class Runners {
  public static Test timeout(double time, Test test) {
    check(time >= 0);
    check(test != null);
    return traverseBodies(test, body -> timeout(time, body));
  }

  private static Body timeout(double time, Body body) {
    return () -> {
      Future<?> alarm = interruptIn(time, Thread.currentThread());
      try {
        body.run();
      } finally {
        alarm.cancel(true);
        if (Thread.interrupted()) {
          throw new InterruptedException();
        }
      }
    };
  }

  private static Future<?> interruptIn(double time, Thread thread) {
    return timeoutExecutor.submit(() -> {
      try {
        long milliseconds = (long) (time * 1e3);
        int nanoseconds = (int) (time * 1e9 % 1e6);
        Thread.sleep(milliseconds, nanoseconds);
        thread.interrupt();
      } catch (InterruptedException e) {}
    });
  }

  private static final ExecutorService timeoutExecutor = timeoutExecutor();

  private static ThreadPoolExecutor timeoutExecutor() {
    int corePoolSize = 0;
    int maximumPoolSize = 1;
    int keepAliveTime = 1;
    TimeUnit keepAliveTimeUnit = NANOSECONDS;
    ThreadPoolExecutor executor = new ThreadPoolExecutor(
        corePoolSize, maximumPoolSize,
        keepAliveTime, keepAliveTimeUnit,
        new LinkedBlockingQueue<Runnable>());
    executor.allowCoreThreadTimeOut(true);
    return executor;
  }
}
