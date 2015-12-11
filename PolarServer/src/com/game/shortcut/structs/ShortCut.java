package com.game.shortcut.structs;

import com.game.object.GameObject;

public class ShortCut extends GameObject {

	private static final long serialVersionUID = 4154833186911183651L;

	// 快捷类型 1-物品 2-技能
	private int shortcutType;
	
	// 快捷对象 唯一id
	private long shortcutSource;
	
	// 快捷对象 模板id
	private int shortcutSourceModel;

	// 战斗小助手
	private String assistant;

	public int getShortcutType() {
		return shortcutType;
	}

	public void setShortcutType(int shortcutType) {
		this.shortcutType = shortcutType;
	}

	public long getShortcutSource() {
		return shortcutSource;
	}

	public void setShortcutSource(long shortcutSource) {
		this.shortcutSource = shortcutSource;
	}

	public int getShortcutSourceModel() {
		return shortcutSourceModel;
	}

	public void setShortcutSourceModel(int shortcutSourceModel) {
		this.shortcutSourceModel = shortcutSourceModel;
	}

	/**
	 * @return the assistant
	 */
	public String getAssistant() {
		return assistant;
	}

	/**
	 * @param assistant
	 *            the assistant to set
	 */
	public void setAssistant(String assistant) {
		this.assistant = assistant;
	}
	
	
}
