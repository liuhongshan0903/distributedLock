package redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;


/**
 * 分销中心
 *
 * @author: liuhongshan
 * @email: liuhongshan@sinovatech.com
 * @date:2020/4/15 23:07
 */
public class RedisDistributedRedLockTest {
    static int n = 5;

    public static void secskill() {
        if (n <= 0) {
            System.out.println("抢购完成");
            return;
        }
        System.out.println(--n);
    }

    public static void main(String[] args) {

        Config config = new Config();
        //支持单机，主从，哨兵，集群等模式
        //此为哨兵模式
        config.useSentinelServers()
                .setMasterName("mymaster")
                .addSentinelAddress("127.0.0.1:26369", "127.0.0.1:26379", "127.0.0.1:26389")
                .setDatabase(0);
        Runnable runnable = () -> {
            RedisDistributedRedLock redisDistributedRedLock = null;
            RedissonClient redissonClient = null;
            try {
                redissonClient = Redisson.create(config);
                redisDistributedRedLock = new RedisDistributedRedLock(redissonClient, "stock_lock");
                redisDistributedRedLock.acquire();
                secskill();
                System.out.println(Thread.currentThread().getName() + "正在运行");
            } finally {
                if (redisDistributedRedLock != null) {
                    redisDistributedRedLock.release(null);
                }

                redissonClient.shutdown();
            }
        };

        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(runnable);
            t.start();
        }
    }

}