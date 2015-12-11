package com.game.newactivity.model;

import java.util.List;

/**
 * @author luminghua
 *
 * @date   2014年2月24日 下午4:09:06
 */
public class DetailActivityInfo{
	
	private int id;
	private int startTime;
	private int endTime;
	private String condDesc;
	
	private List<Row> rows;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getStartTime() {
		return startTime;
	}
	public int getEndTime() {
		return endTime;
	}
	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}
	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}
	public List<Row> getRows() {
		return rows;
	}
	public void setRows(List<Row> rows) {
		this.rows = rows;
	}


	public String getCondDesc() {
		return condDesc;
	}
	public void setCondDesc(String condDesc) {
		this.condDesc = condDesc;
	}




	public static class Row{
		String cond;
		String award;
		
		public Row(String cond, String award) {
			super();
			this.cond = cond;
			this.award = award;
		}

		public String getCond() {
			return cond;
		}

		public void setCond(String cond) {
			this.cond = cond;
		}

		public String getAward() {
			return award;
		}

		public void setAward(String award) {
			this.award = award;
		}
	}
}
