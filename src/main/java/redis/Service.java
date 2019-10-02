package redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Collections;
import java.util.UUID;


/**
  * jedis分布式锁
  * @auther: liuhongshan
  * @mail: liuhongshan@sinovatech.com
  * @Date:  2019/10/1 18:57
  */


public class Service {
    private static JedisPool pool = null;

    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";

    /**
     * redis连接
     */
    static {
        JedisPoolConfig config = new JedisPoolConfig();
        // 设置最大连接数
        config.setMaxTotal(200);
        // 设置最大空闲数
        config.setMaxIdle(8);
        // 设置最大等待时间
        config.setMaxWaitMillis(1000 * 100);
        // 在borrow一个jedis实例时，是否需要验证，若为true，则所有jedis实例均是可用的
        config.setTestOnBorrow(true);

        pool = new JedisPool(config, "192.168.99.100", 6380, 3000,"123456");
    }

    DistributedLock lock = new DistributedLock(pool);

    int n = 500;
    int ren = 0;


    /**
     * 加锁
     * 处理业务
     * 释放锁
     */
    public void seckill() {
        Jedis jedis = pool.getResource();
        String uuid = UUID.randomUUID().toString();
        boolean result = RedisTool.tryGetDistributedLock(jedis,"liuhs_fx_lock", uuid, 5000);
         //1加锁
        if (result) {
            System.out.println(Thread.currentThread().getName() + "获得了锁:"+uuid);
            if (n==0) {
                System.out.println("结束啦， 共有"+ren+"人参与");
                return;
            }

            //2开始处理业务
            // 成功处理业务
            System.out.println("还有票："+--n+"张");
            ren++;
            System.out.println("已抢到票："+ren+"人");
            //释放锁
            RedisTool.releaseDistributedLock(jedis,"liuhs_fx_lock",uuid);

        } else {
            System.out.println(Thread.currentThread().getName() + "没有获得了锁:"+uuid);
        }
    }



    /**
     * 正确示例
      * 加锁
      * 处理业务
      * 释放锁
      */
    public void seckill2() {
       Jedis jedis = pool.getResource();
        //1加锁 返回锁的value值，供释放锁时候进行判断
        String indentifier = lock.lockWithTimeout("liuhs_fx_lock", 5000, 1000);
        if (indentifier != null) {
            System.out.println(Thread.currentThread().getName() + "获得了锁:"+indentifier);
            if (n==0) {
                System.out.println("结束啦，共有"+ren+"人参与");
                return;
            }

            //2开始处理业务
            // 成功处理业务
            System.out.println("还有票："+--n+"张");
            ren++;
            System.out.println("已抢到票："+ren+"人");
            //3释放锁
            lock.releaseLock("liuhs_fx_lock", indentifier);

        } else {
            System.out.println(Thread.currentThread().getName() + "没有获得了锁:"+indentifier);
        }
    }


     /**
      * 最原始错误示例
      * 加锁
      * 处理业务
      * 释放锁
      */
     public void seckill1() {
             if (n==0) {
                 System.out.println("不加锁：结束啦，共有"+ren+"人参与");
                 return;
             }
             System.out.println("不加锁：还有票："+--n+"张");
             ren++;
             System.out.println("不加锁：已抢到票："+ren+"人");

     }




}
