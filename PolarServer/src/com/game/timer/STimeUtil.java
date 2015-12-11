/**
 * 
 */
package com.game.timer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author luminghua
 * @date 2014-1-31 下午7:38:23
 */

public class STimeUtil {
	
	public static final long ONE_DAY_MILLIS = 24*60*60*1000l; //一天的毫秒数
	
	private final static ThreadLocal<SimpleDateFormat> TIME_IN_DAY_FORMAT = new ThreadLocal<SimpleDateFormat>(){
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("HH:mm:ss");
		}
	};
	
	private final static ThreadLocal<SimpleDateFormat> DATE_FORMAT = new ThreadLocal<SimpleDateFormat>(){
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };
	
    //SimpleDateFormat不是线程安全的
	private final static ThreadLocal<SimpleDateFormat> DEFAULT_TIME_FORMAT = new ThreadLocal<SimpleDateFormat>(){
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    private final static Logger logger = LoggerFactory.getLogger(STimeUtil.class);
	
	public static String format(Date d){
		return format(DEFAULT_TIME_FORMAT, d);
	}
	
	public static String format(ThreadLocal<SimpleDateFormat> sdf, Date d){
		return sdf.get().format(d);
	}
	
	public static String formatCurrentDate(){
		return format(new Date());
	}
	
	public static Date parse(String dateStr){
		try {
			return DEFAULT_TIME_FORMAT.get().parse(dateStr);
		} catch (ParseException e) {
			logger .error("", e);
		}
		return null;
	}

	/**
	 * @param timeMillis
	 * @return
	 */
	public static String format(long timeMillis) {
		return format(new Date(timeMillis));
	}
    
    /**
     * 获取当天0点时间
     * @return
     */
    public static long getTodayZeroClock(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
    
    public static void main(String[] args) throws ParseException {
//    	System.out.println(String.class.getSimpleName());
//    	System.out.println(413248544 | 0x10);
    }

    /**
     * 判断两个时间是否同一天
     * @param lastDeadTimeMillis
     * @param lastDeadTimeMillis2
     * @return
     */
    public static boolean isSameDay(long lastDeadTimeMillis, long lastDeadTimeMillis2) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(lastDeadTimeMillis);
        int d1 = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.setTimeInMillis(lastDeadTimeMillis2);
        int d2 = calendar.get(Calendar.DAY_OF_YEAR);
        return d1==d2;
    }
}
