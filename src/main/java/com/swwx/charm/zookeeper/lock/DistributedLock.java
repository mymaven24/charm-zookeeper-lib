package com.swwx.charm.zookeeper.lock;

import com.swwx.charm.commons.lang.exception.SystemErrorException;
import com.swwx.charm.zookeeper.exception.GetLockFailedException;
import com.swwx.charm.zookeeper.exception.ReleaseLockFailedException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.ChildReaper;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.Reaper;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


public class DistributedLock {

    private CuratorFramework client = null;

    private ChildReaper childReaper = null;

    public static Map<String, InterProcessMutex> THREAD_LOCKS = new ConcurrentHashMap();



    public DistributedLock(String connString, String nameSpace) {
        ExponentialBackoffRetry exponentialBackoffRetry = new ExponentialBackoffRetry(1000, 3);
        this.client = CuratorFrameworkFactory.builder().connectString(connString).namespace(nameSpace).retryPolicy(exponentialBackoffRetry).build();
        this.client.start();
        this.childReaper = new ChildReaper(this.client, "/lock", Reaper.Mode.REAP_UNTIL_GONE);
        try {
            this.childReaper.start();
        } catch (Exception e) {
            throw new SystemErrorException("reaper stared failed.", e);
        }
    }



    public void destroy() throws Exception {
        for (String key : THREAD_LOCKS.keySet()) {
            try {
                ((InterProcessMutex)THREAD_LOCKS.get(key)).release();
            } catch (Exception e) {
                throw e;
            }
        }

        if (this.client != null) {
            this.client.close();
        }

        if (this.childReaper != null) {
            this.childReaper.close();
        }
    }

    public boolean getLock(String lockPath) throws GetLockFailedException {
        try {
            InterProcessMutex lock = new InterProcessMutex(this.client, "/lock/" + lockPath);


            boolean flag = lock.acquire(0L, TimeUnit.NANOSECONDS);

            if (flag) {
                THREAD_LOCKS.put(lockPath, lock);
            }

            return flag;
        }
        catch (Exception e) {
            throw new GetLockFailedException("lock 获取失败", e);
        }
    }

    public void releaseLock(String lockPath) throws ReleaseLockFailedException {
        if (THREAD_LOCKS.get(lockPath) != null)
            try {
                ((InterProcessMutex)THREAD_LOCKS.get(lockPath)).release();
                THREAD_LOCKS.remove(lockPath);
            } catch (Exception e) {
                throw new ReleaseLockFailedException("lock 释放失败", e);
            }
    }
}
