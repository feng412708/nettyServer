import com.eemp.utils.JedisUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by A03742 on 2018-04-28.
 */
public class ClientThread extends Thread {
    int i = 0;

    public ClientThread(int i) {
        this.i = i;
    }

    public void run() {
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(date);
        long start = System.currentTimeMillis();

        String foo = JedisUtils.getString("foo");
        long end = System.currentTimeMillis();
        System.out.println("【输出>>>>】foo:" + foo + " 第："+i+"个线程" +" 存取耗时："+(end-start)+"ms");
    }

    public static void main(String[] args) {
        JedisUtils.setString("foo", "test");
        for (int i = 0; i < 2; i++) {

            ClientThread t = new ClientThread(i);
            t.start();
        }
    }
}
