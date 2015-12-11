package com.game.timer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.CronTriggerImpl;

import com.game.utils.CollectionUtil;
import com.game.utils.StringUtil;
import com.game.utils.TimeUtil;

/**
 * @author luminghua
 *
 * @date   2014年2月21日 下午2:25:00
 * 
 * 定时调度器
 * 
 */
public class SchedulerManager {

	//已注册的调用者，类名-类实例
	private Map<String,Schedulerable> invokerMap = new HashMap<String,Schedulerable>();
	//已进行中的timer任务，类名-任务列表
	private Map<String,List<TimerTask>> timerMap = new HashMap<String,List<TimerTask>>();
	//已经行中的quartz任务，类名-jobKey
	private Map<String,List<JobKey>> quartzMap = new HashMap<String,List<JobKey>>();
	
	private Scheduler scheduler;
	private Timer timer;
	private long serverStartTime = 0;
	
	private static SchedulerManager instance;
	private static Object obj = new Object();
	private static Logger logger = Logger.getLogger(SchedulerManager.class);
	
	public static SchedulerManager getInstance() {
		if(instance == null) {
			synchronized(obj) {
				if(instance == null) {
					instance = new SchedulerManager();
				}
			}
		}
		return instance;
	}
	
	public void init() throws SchedulerException {
		//初始化quartz和timer
		scheduler = StdSchedulerFactory.getDefaultScheduler();
		scheduler.start();
		timer = new Timer(this.getClass().getSimpleName()+"-timer");
	}
	
	/**
	 * 注册调用者，每个需要时间调度功能的类都需要手动注册
	 * 如果有spring的话，就可以用spring的对象实例化功能并通过注解等方式自动注册
	 * 
	 * @param name
	 * @param instance
	 * @return
	 * 		是否注册成功
	 * @throws Exception 
	 */
	public boolean register(String name,Schedulerable instance) throws Exception {
		if(invokerMap.containsKey(name)) {
			logger.equals(name+" is repeated scheduler!");
		}
		invokerMap.put(name, instance);
		return true;
	}

	public void addSchedulerBean(List<SchedulerBean> beans) throws Exception {
		if(beans == null) {
			return;
		}
		for(SchedulerBean bean:beans) {
			addSchedulerBean(bean);
		}
	}
	/**
	 * 添加调度任务
	 * 
	 * @param bean
	 * @throws Exception
	 */
	public void addSchedulerBean(SchedulerBean bean) throws Exception {
		if(bean == null ) {
			throw new NullPointerException("error");
		}
		if( StringUtils.isBlank(bean.getKey()) || !invokerMap.containsKey(bean.getKey())) {
			throw new NullPointerException("null Key or null invoker :"+bean.getKey());
		}
		
		if(CollectionUtil.isBlank(bean.getCronExpression())) {
			throw new NullPointerException(bean.getKey()+" bean has't data.");
		}
		//装载调用者的定时任务
		Schedulerable schedulerable = invokerMap.get(bean.getKey());
		schedulerable.initBean(bean);
		//处理crontab表达式
		if(CollectionUtil.isNotBlank(bean.getCronExpression())){
			//quartz表达式，包含方法和参数[[cronExp,method,param][cronExp,method,param]...]
			processCronExp(bean);
		}
	}
	
	private void processCronExp(SchedulerBean bean) throws SchedulerException, ParseException {
		String Key = bean.getKey();
		String[][] cronExp = bean.getCronExpression();
		long now = System.currentTimeMillis();
		for(String[] exp : cronExp) {
			//如果是开服类型，用timer特殊处理
			String[] split = exp[0].split(" ");
			if(split.length == 2) {
				//2014-1-1 00:00:00这种格式的直接timer
				Date date = STimeUtil.parse(exp[0]);
				if(date.getTime() < now) {
					continue;
				}
				TimerJob timerJob = new TimerJob(Key,exp[1],exp[2]);
				timer.schedule(timerJob,date);
				this.addTimerMap(Key, timerJob);
				logger.info("Key:"+Key+",methodName:"+exp[1]+" will execute at "+date);
			}else if(split.length == 8) {
				int second = Integer.parseInt(split[0].equals("*")?"0":split[0]);
				int minute = Integer.parseInt(split[1].equals("*")?"0":split[1]);
				int hour = Integer.parseInt(split[2].equals("*")?"0":split[2]);
				long addTime = (hour * 60 * 60 + minute * 60 + second) * 1000l;
				String[] split2 = split[7].split("/");
				if(split2.length > 1) {
					//循环类型
					//加上起始日时间,开服天数从1开始
					int init = Integer.parseInt(split2[0]) -1;
					if(init<0) {
						init = 0;
					}
					addTime += (init * STimeUtil.ONE_DAY_MILLIS);
					//加上开服时间
					addTime += getServerStartTime();
					//循环间隔
					long period = Integer.parseInt(split2[1]) * STimeUtil.ONE_DAY_MILLIS;
					//找出下一次运行时间
					while(true) {
						if(addTime >= now) {
							break;
						}
						addTime += period;
					}
					Date date = new Date(addTime);
					TimerJob timerJob = new TimerJob(Key,exp[1],exp[2]);
					timer.scheduleAtFixedRate(timerJob,date, period);
					this.addTimerMap(Key, timerJob);
					logger.info("Key:"+Key+",methodName:"+exp[1]+" will execute at "+date);
				}else {
					//单日类型
					//加上开服时间
					addTime += getServerStartTime();
					//加上起始日时间,开服天数从1开始
					int init = Integer.parseInt(split2[0]) -1;
					if(init<0) {
						init = 0;
					}
					addTime += init * STimeUtil.ONE_DAY_MILLIS;
					if(addTime > now) {
						Date date = new Date(addTime);
						TimerJob timerJob = new TimerJob(Key,exp[1],exp[2]);
						timer.schedule(timerJob,date);
						this.addTimerMap(Key, timerJob);
						logger.info("Key:"+Key+",methodName:"+exp[1]+" will execute at "+date);
					}
				}
			}else {
				//crontab表达式
				JobDetail jobDetail = JobBuilder.newJob(QuartzJob.class).withIdentity("job-"+exp[0], "group-"+Key)
						.usingJobData("Key", Key)
						.usingJobData("methodName", exp[1])
						.usingJobData("param", exp[2])
						.build();
				CronTriggerImpl trigger = (CronTriggerImpl) TriggerBuilder.newTrigger().withIdentity("trigger-"+exp[0], "group-"+Key)
						.withSchedule(CronScheduleBuilder.cronSchedule(exp[0])).build();
				try {
					scheduler.scheduleJob(jobDetail, trigger);
					this.addQuartzMap(Key, jobDetail.getKey());
					logger.info("Key:"+Key+",methodName:"+exp[1]+" will execute at "+trigger.getNextFireTime());
				} catch (SchedulerException e) {
					//一般是因为时间过期了
					logger.error("some matters occur by "+Key+" "+exp[0]);
//					throw e;
				}
			}
		}
	}
	
	
	/**
	 * 执行调用方法
	 * 
	 * @param Key
	 * @param methodName
	 * @param param
	 */
	private void execute(String Key,String methodName,String param) {
		Object object = invokerMap.get(Key);
		if(object != null) {
			try {
				if(StringUtil.isBlank(methodName)) {
					methodName = "trigger";
				}
				if(StringUtils.isBlank(param)) {
					Method declaredMethod = object.getClass().getMethod(methodName);
					declaredMethod.invoke(object);
				}else {
					Method declaredMethod = object.getClass().getMethod(methodName, String.class);
					declaredMethod.invoke(object, (Object)param);
				}
			} catch ( SecurityException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				logger.error("class:"+Key+",method:"+methodName+",param:"+param, e);
			}
		}else {
			logger.error("null class:"+Key+",method:"+methodName+",param:"+param);
		}
	}
	
	public long getServerStartTime() throws ParseException {
		//模拟开服时间
		if(serverStartTime == 0) {
			Calendar cal = TimeUtil.getOpenTime();
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			serverStartTime = cal.getTimeInMillis();
		}
		return serverStartTime;
	}
	
	private void addTimerMap(String Key,TimerTask timerJob) {
		List<TimerTask> list = timerMap.get(Key);
		if(list == null) {
			list = new LinkedList<TimerTask>();
			timerMap.put(Key, list);
		}
		list.add(timerJob);
	}
	
	private void addQuartzMap(String Key,JobKey jobKey) {
		List<JobKey> list = quartzMap.get(Key);
		if(list == null) {
			list = new LinkedList<JobKey>();
			quartzMap.put(Key, list);
		}
		list.add(jobKey);
	}
	
	public void timerschedule(String Key,TimerTask timerTask,long delay,long period) {
		timer.schedule(timerTask, delay, period);
		if(StringUtils.isNotBlank(Key)) {
			addTimerMap(Key,timerTask);
		}
	}
	
	public void timerScheduleAtFixedRate(String Key,TimerTask timerTask,long delay,long period) {
		timer.scheduleAtFixedRate(timerTask, delay, period);
		if(StringUtils.isNotBlank(Key)) {
			addTimerMap(Key,timerTask);
		}
	}
	
	public void timerschedule(String Key,TimerTask timerTask,Date date) {
		timer.schedule(timerTask,date);
		if(StringUtils.isNotBlank(Key)) {
			addTimerMap(Key,timerTask);
		}
	}
	
	public void timerschedule(String Key,TimerTask timerTask,long delay) {
		timer.schedule(timerTask, delay);
		if(StringUtils.isNotBlank(Key)) {
			addTimerMap(Key,timerTask);
		}
	}
	
	/**
	 * 关闭某组任务
	 * 
	 * @param Key
	 * @throws SchedulerException
	 */
	public void shutTask(String Key) throws SchedulerException {
		List<TimerTask> list = timerMap.get(Key);
		if(CollectionUtil.isNotBlank(list)) {
			for(TimerTask job : list) {
				job.cancel();
			}
		}
		List<JobKey> list2 = quartzMap.get(Key);
		if(CollectionUtil.isNotBlank(list2)) {
			for(JobKey key : list2) {
				scheduler.deleteJob(key);
			}
		}
	}
	
	public void stop() {
		try {
			if(scheduler!=null && scheduler.isStarted())
				scheduler.shutdown();
		} catch (SchedulerException e) {
			logger.error("", e);
		}
		if(timer!=null)
			timer.cancel();
		logger.info(this.getClass().getSimpleName()+" is stop normally.");
	}
	
	public static class QuartzJob implements Job{

		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
			logger.info("before execute QuartzJob.");
			JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
			String Key = jobDataMap.getString("Key");
			String methodName = jobDataMap.getString("methodName");
			String param = jobDataMap.getString("param");
			SchedulerManager.getInstance().execute(Key,methodName,param);
			logger.info("after execute QuartzJob. class is "+Key +" ,method is "+methodName+" ,param"+param+" next execute is "+context.getTrigger().getNextFireTime());
		}
		
	}
	
	public static class TimerJob extends TimerTask{

		private String Key;
		private String methodName;
		private String param;
		
		TimerJob(String Key,String methodName,String param){
			this.Key = Key;
			this.methodName = methodName;
			this.param = param;
		}
		
		@Override
		public void run() {
			logger.info("before execute TimerJob.");
			SchedulerManager.getInstance().execute(Key,methodName,param);
			logger.info("after execute TimerJob. class is "+Key +" ,method is "+methodName+" ,param"+param+" pre execute is "+this.scheduledExecutionTime());
		}
		
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}

}
