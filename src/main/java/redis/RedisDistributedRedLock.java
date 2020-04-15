package redis;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * 分销中心
 *
 * @author: liuhongshan
 * @email: liuhongshan@sinovatech.com
 * @date:2020/4/15 22:44
 */
public class RedisDistributedRedLock implements IRedisDistributedLock {

    /**
     * redis 客户端
     */
    private RedissonClient redissonClient;

    /**
     * 分布式锁的键值
     */
    private String lockKey;

    private RLock redLock;

    /**
     * 锁的有效时间 10s
     */
    int expireTime = 10 * 1000;

    /**
     * 获取锁的超时时间
     */
    int acquireTimeout  = 500;

    public RedisDistributedRedLock(RedissonClient redissonClient, String lockKey) {
        this.redissonClient = redissonClient;
        this.lockKey = lockKey;
    }


    public String acquire() {
        redLock = redissonClient.getLock(lockKey);
        boolean isLock;
        try{
            isLock = redLock.tryLock(acquireTimeout, expireTime, TimeUnit.MILLISECONDS);
            if(isLock){
                System.out.println(Thread.currentThread().getName() + " " + lockKey + "获得了锁");
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public boolean release(String indentifier) {
        if(null != redLock){
            redLock.unlock();
            return true;
        }

        return false;
    }
}