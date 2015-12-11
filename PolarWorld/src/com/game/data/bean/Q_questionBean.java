package com.game.data.bean;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_question Bean
 */
public class Q_questionBean {

	//题目编号
	private int q_id;
	
	//题目
	private String q_question_content;
	
	//答案1
	private String q_answer_content_1;
	
	//答案2
	private String q_answer_content_2;
	
	//答案3
	private String q_answer_content_3;
	
	//答案4
	private String q_answer_content_4;
	
	//正确答案
	private int q_right_answer_id;
	
	
	/**
	 * get 题目编号
	 * @return 
	 */
	public int getQ_id(){
		return q_id;
	}
	
	/**
	 * set 题目编号
	 */
	public void setQ_id(int q_id){
		this.q_id = q_id;
	}
	
	/**
	 * get 题目
	 * @return 
	 */
	public String getQ_question_content(){
		return q_question_content;
	}
	
	/**
	 * set 题目
	 */
	public void setQ_question_content(String q_question_content){
		this.q_question_content = q_question_content;
	}
	
	/**
	 * get 答案1
	 * @return 
	 */
	public String getQ_answer_content_1(){
		return q_answer_content_1;
	}
	
	/**
	 * set 答案1
	 */
	public void setQ_answer_content_1(String q_answer_content_1){
		this.q_answer_content_1 = q_answer_content_1;
	}
	
	/**
	 * get 答案2
	 * @return 
	 */
	public String getQ_answer_content_2(){
		return q_answer_content_2;
	}
	
	/**
	 * set 答案2
	 */
	public void setQ_answer_content_2(String q_answer_content_2){
		this.q_answer_content_2 = q_answer_content_2;
	}
	
	/**
	 * get 答案3
	 * @return 
	 */
	public String getQ_answer_content_3(){
		return q_answer_content_3;
	}
	
	/**
	 * set 答案3
	 */
	public void setQ_answer_content_3(String q_answer_content_3){
		this.q_answer_content_3 = q_answer_content_3;
	}
	
	/**
	 * get 答案4
	 * @return 
	 */
	public String getQ_answer_content_4(){
		return q_answer_content_4;
	}
	
	/**
	 * set 答案4
	 */
	public void setQ_answer_content_4(String q_answer_content_4){
		this.q_answer_content_4 = q_answer_content_4;
	}
	
	/**
	 * get 正确答案
	 * @return 
	 */
	public int getQ_right_answer_id(){
		return q_right_answer_id;
	}
	
	/**
	 * set 正确答案
	 */
	public void setQ_right_answer_id(int q_right_answer_id){
		this.q_right_answer_id = q_right_answer_id;
	}
	
}