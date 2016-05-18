package helpers;

/**
 * Created by Sasha on 21.04.16.
 */
public class TimeHelper {
    public static void sleep(long period){
        if (period <= 0) {
            return;
        }
        try{
            Thread.sleep(period);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
