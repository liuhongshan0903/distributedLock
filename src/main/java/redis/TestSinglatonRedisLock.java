package redis;

/**
 * Created by liuyang on 2017/4/20.
 */
public class TestSinglatonRedisLock {
    public static void main(String[] args) {
        SinglatonService service = new SinglatonService();
        for (int i = 0; i < 220; i++) {
            ThreadA threadA = new ThreadA(service);
            threadA.start();
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
