package com.game.timer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.game.utils.StringUtil;

/**
 * @author luminghua
 *
 * @date   2014年2月21日 下午5:55:44
 */
public class SchedulerParser {
	
	private static Logger logger = Logger.getLogger(SchedulerParser.class);
	
	public static LinkedList<SchedulerBean> parseFile(String filePath) throws FileNotFoundException {
		File file = new File(filePath);
		if(file != null && file.exists()) {
			LinkedList<SchedulerBean> list = new LinkedList<SchedulerBean>();
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;
			SchedulerBean bean = null;
			LinkedList<String[]> cronExpList= new LinkedList<String[]>();
			try {
				while((line = reader.readLine()) != null) {
					bean = parseLine(line,bean,list,cronExpList);
				}
			} catch (IOException e) {
				logger.error("The file name is "+filePath, e);
			}finally {
				if(reader!= null)
					try {
						reader.close();
					} catch (IOException e) {
						logger.error("The file name is "+filePath, e);
					}
			}
			return list;
		}
		return null;
	}

	public static LinkedList<SchedulerBean> parseDBString(String dbString) {
		if(!StringUtils.isBlank(dbString)) {
			dbString = dbString.replaceAll("\r\n", "\n");
			String[] split = dbString.split("\n");
			if(split.length == 1) {
				split = dbString.split(",|，");
			}
			LinkedList<SchedulerBean> list = new LinkedList<SchedulerBean>();
			SchedulerBean bean = null;
			LinkedList<String[]> cronExpList= new LinkedList<String[]>();
			
			for(String line:split) {
				bean = parseLine(line,bean,list,cronExpList);
			}
			return list;
		}
		return null;
	}
	
	private static SchedulerBean parseLine(String line,SchedulerBean bean,LinkedList<SchedulerBean> list,LinkedList<String[]> cronExpList) {
		logger.debug(line);
		if(line.equals("")) {
			return bean;
		}
		if(line.startsWith("#")) {
			return bean;
		}
		if(line.startsWith("}")) {
			if(cronExpList.size() > 0) {
				String[][] cronExps = new String[cronExpList.size()][3];
				int count = 0;
				for(String[] cronExp : cronExpList) {
					cronExps[count] = cronExp;
					count++;
				}
				bean.setCronExpression(cronExps);
				cronExpList.clear();
			}
			list.add(bean);
			bean = null;
			return bean;
		}
		int indexOf = line.indexOf(",");
		if(indexOf == -1) {
			indexOf = line.indexOf("#");
		}
		if(indexOf != -1) {
			line = line.substring(0, indexOf);
		}
		if(bean == null) {
			bean = new SchedulerBean();
			bean.setKey(line.substring(0, line.length()-1));
		}else {
			int leftIndex = line.indexOf("{");
			int rightIndex = line.indexOf("}");
			String expression,sub,param,method = null;
			String[] split;
			if(leftIndex == -1 || rightIndex == -1) {
				expression = line;
				param = "";
			}else {
				expression = line.substring(0, leftIndex-1);
				sub = line.substring(leftIndex+1, rightIndex);
				split = sub.split(" ");
				method = split[0];
				param = split.length>1?split[1]:"";
			}
			String[] cronExp = new String[3];
			cronExp[0] = expression;
			cronExp[1] = method == null?"start":method;
			cronExp[2] = param;
			cronExpList.add(cronExp);
		}
		return bean;
	}
	
	/**
	 * #seconds minutes hours day-of-month month day-of-week year(optional) launch(optional,开服字段)
	 * 
	 * @param cron
	 * 		yyyy-MM-dd HH:mm:ss 或者 固定时间的crontab表达式
	 * @return
	 * @throws ParseException 
	 */
	public static Date parse2Date(String cron) throws ParseException {
		if(StringUtil.isBlank(cron)) {
			return null;
		}
		String[] split = cron.split(" ");
		if(split.length == 2) {
			return STimeUtil.parse(cron);
		}else {
			int second = Integer.parseInt(split[0].equals("*")?"0":split[0]);
			int minute = Integer.parseInt(split[1].equals("*")?"0":split[1]);
			int hour = Integer.parseInt(split[2].equals("*")?"0":split[2]);
			if(split.length == 8) {
				//开服
				long addTime = (hour * 60 * 60 + minute * 60 + second) * 1000l;
				addTime += SchedulerManager.getInstance().getServerStartTime();
				int init = Integer.parseInt(split[7]) -1;
				if(init<0) {
					init = 0;
				}
				addTime += init * STimeUtil.ONE_DAY_MILLIS;
				return new Date(addTime);
			}else {
				int day_of_month = Integer.parseInt(split[3].equals("*") || split[3].equals("?")?"0":split[3]);
				int month = Integer.parseInt(split[4].equals("*")?"-1":split[4]);
				int day_of_week = Integer.parseInt(split[5].equals("*") || split[5].equals("?")?"0":split[5]);
				int year = Integer.parseInt(split[6].equals("*")?"0":split[6]);
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.SECOND, second);
				cal.set(Calendar.MINUTE, minute);
				cal.set(Calendar.HOUR_OF_DAY, hour);
				if(day_of_month !=0)
					cal.set(Calendar.DAY_OF_MONTH, day_of_month);
				if(month > 0)
					cal.set(Calendar.MONTH, month-1);//月份从0开始
				if(day_of_week !=0)
					cal.set(Calendar.DAY_OF_WEEK, day_of_week);
				if(year !=0)
					cal.set(Calendar.YEAR, year);
				return cal.getTime();
			}
		}
	}
	public static void main(String[] s) {
		/*try {
			LinkedList<SchedulerBean> parseFile = parseFile(SchedulerParser.class.getClassLoader().getResource("quartz.txt").getPath());
			SchedulerManager.getInstance().init();
			for(SchedulerBean bean : parseFile) {
				SchedulerManager.getInstance().addSchedulerBean(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			SchedulerManager.getInstance().stop();
		}*/
//		try {
//			System.out.println(parse2Date("2014-1-31 12:29:00"));
//			System.out.println(parse2Date("0 29 12 31 1 * 2014"));
//			Calendar cal = Calendar.getInstance();
//			cal.setTimeInMillis(System.currentTimeMillis());
//			cal.set(Calendar.SECOND, 0);
//			cal.set(Calendar.MINUTE, 29);
//			cal.set(Calendar.HOUR_OF_DAY, 12);
//			cal.set(Calendar.MONTH, 0);
//			cal.set(Calendar.DAY_OF_MONTH, 31);
////			cal.set(Calendar.DAY_OF_WEEK, 0);
//			cal.set(Calendar.YEAR, 2014);
//			System.out.println(cal.getTime());
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
	}
}
