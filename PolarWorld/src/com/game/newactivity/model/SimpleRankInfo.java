package com.game.newactivity.model;

/**
 * @author luminghua
 *
 * @date   2014年2月27日 下午5:16:05
 */
public class SimpleRankInfo {

	private long playerId;
	private String name;
	private int job;
	private int rank;//排名
	private String data;//不同排行榜不同的数据
	
	public SimpleRankInfo() {
		
	}

	/**
	 * @param playerId
	 * @param name
	 * @param data
	 */
	public SimpleRankInfo(long playerId, String name,int job,int rank, String data) {
		super();
		this.playerId = playerId;
		this.name = name;
		this.job = job;
		this.data = data;
		this.rank = rank;
	}

	
	public int getJob() {
		return job;
	}

	public void setJob(int job) {
		this.job = job;
	}

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}
	
	
}
