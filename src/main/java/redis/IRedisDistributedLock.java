package redis;

/**
 * 分销中心
 *
 * @author: liuhongshan
 * @email: liuhongshan@sinovatech.com
 * @date:2020/4/15 22:43
 */
public interface IRedisDistributedLock {

    /**
     * 获取锁
     * @author zhi.li
     * @return 锁标识
     */
    String acquire();

    /**
     * 释放锁
     * @author zhi.li
     * @param indentifier
     * @return
     */
    boolean release(String indentifier);
}
