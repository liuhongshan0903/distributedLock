package redis;

/**
 * Created by liuyang on 2017/4/20.
 */
public class ThreadA extends Thread {
    private SinglatonService service;

    public ThreadA(SinglatonService service) {
        this.service = service;
    }

    @Override
    public void run() {
        service.seckill();
    }
}
