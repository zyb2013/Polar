package com.game.structs;



public enum Reasons {
	//交易模块
	STYBSHANGJIA	(1),//钻石上架
	STYBXIAJIA		(2),//钻石下架
	STYBGOUMAI		(3),//购买摊位钻石
	STYBGOUMAIDAOJU	(4),//用钻石购买摊位道具
	STYBHUOKUAN		(5),//摊主得到货款钻石
	JIAOYIYB		(6),//交易钻石
	STGOUMAISHIBAI	(7),//购买道具失败，返款
	QUCHUTMPYB		(8),//取出临时钻石
	QUCHUTMPGOLD	(9),//金币上架
	GOUMAISHIBAIGOLD (10),//购买失败，返还金币
	GETGOLD			(11),//摊主得到货款金币
	SUBGOLD			(12),//购买商品扣金币
	BUYITEM			(13),//买到道具
	BUYGOLD			(14),//买到金币
	SHANGJIAITEM    (15),//上架道具
	JIAOYIITEM		(16),//交易道具,从身上移除
	JIAOYIGOLD		(17),//交易金币
	JIAOYISHIBAI    (18),//交易失败返还
	JIAOYIITEMADD	(19),//交易道具,获得道具
	WEAREDORUNWEARED(20),//穿脱装备
	GOODUSE			(21),//物品使用
	GOODSDISCARD	(22),//物品丢弃
	TAKEUP			(23),//拾取
	STORETOBAG		(24),//仓库到包裹
	BAGTOSTORE		(25),//包裹到仓库
	CHAT_RESUME		(26),//聊天消耗
	SYSTEM_GIFT		(27),//系统赠送
	BAGTOGLOBAL     (28),//包裹到跨服包裹
	GLOBALTOBAG     (29),//跨服包裹到包裹
	OPENSTORE(30), // 打开远程仓库
	WORLDCHAT(31), //! 世界频道发言
	
	//商城 100-110
	YBBUY			(100),//钻石购买
	SHOPBUY			(101),//商城购买
	SHOPSELL		(102),//商店出售
	//NPC传送110-120
	YBNPCTRANS		(110),//NPC钻石传送
	NPCTRANS		(111),//NPC传送
	GMCOMMAND		(112),//GM命令
	YBTRANS			(113),//小地图钻石传送
	DJTRANS			(114),//道具传送
	//技能模块120-130
	YBSKILLSTUDY	(120),//技能学习快速完成
	SKILLSTUDY		(121),//技能学习
	SKILLLEVELUP	(122),//技能升级
	
	
	
	//背包仓库 130-140
	YBBAGKAIGE		(130),//钻石包裹开格
	YBSTOREKAIGE	(131),//钻石仓库开格
	
	//任务 140-150
	DAILYTASKQUCKFINSH 		(140),	//快速完成
	DAILYTASKSUPPERFINSH	(141),	//最优快速完成
	DAILYFINSHCURRENTLOOP	(142),	//完成本环
	DAILYTASKUPACHRIVE		(143),	//提升奖励
	DAILYTASKREDUCED		(144),	//调低难度
	TASKGOODSADD			(145),	//任务物品直接进包裹
	TASKGETKRECEIVEABLE		(146),	//获取可领取区物品
	TASKRESUME				(147),	//任务收回
	TASKREWARDS				(148),	//任务奖励
	CONQUERTASKDEVOUR		(149),	//讨伐任务吞噬
	DOUBLEREWARDS		(150),	//领取双倍奖励
	
	
	
	//点绛唇 151-159
	DIANJIANGCHUNCHANGLUCK	(151),	//点绛唇改运
	
	//邮件 160-170
	MAILSHOUQUGOLD		(160),	//收取邮件钻石
	MAILRETURNGOLD		(161),	//回收邮件钻石
	MAILSENDGOLD		(162),	//发送邮件钻石
	MAILRETURNSENDGOLD	(163),	//回收发送失败钻石
	MAILSHOUQUITEM		(164),	//收取邮件物品
	MAILSHOUQUMONEY		(165),	//收取邮件金币
	MAILSHOUQUITEMFAIL	(166),	//收取邮件物品失败
	MAILSHOUQUMONEYFAIL	(167),	//收取邮件金币失败
	MAILSENDITEM		(168),	//发送邮件物品
	MAILSENDMONEY		(169),	//发送邮件金币
	MAILSENDITEMFAIL	(170),	//发送邮件物品失败
	MAILSENDMONEYFAIL	(171),	//发送邮件金币失败
	MAILSHOUQUBINDGOLD	(172),	//收取邮件绑钻
	
	
	//美人 175-180
	PET_HETI			(175),//美人合体
	PET_QINGDIAN		(176),//美人钦点
	
	
	//坐骑
	HORSE_LZ			(180),//幸运连珠
	HORSE_CD			(181),//清除CD
	HORSE_LG			(182),//幸运拉杆
	HORSE_UP			(183),//坐骑升阶
	
	LY_SUB_GOLD			(190),    //龙源心法扣金币
	
	//战盟
	GUILD_DRAGON		(200),//捐献忠诚徽章
	GUILD_WHITETIGER	(201),//捐献荣誉徽章
	GUILD_SUZAKU		(202),//捐献守护徽章
	GUILD_BASALTIC		(203),//捐献勇敢徽章
	GUILD_STOCKGOLD		(204),//捐献金币
	GUILD_CREATE		(205),//创建战盟
	
	// 
	REVIVE				(210), //手动复活收取道具
	LEVELUPGM			(211), //GM命令升级
	LEVELUPADDEXP		(212), //增加经验升级
	LEVELUPBUFF			(213), //buff效果升级
	LEVELUPCREATE		(214), //创建角色默认升级
	REVIVE_AUTO				(215), //自动复活收取道具
	
	STRENG_ITEM			(220),//装备强化 和升阶 收取材料
	STRENG_QH_YUANBAO		(221),//立即强化装备收钻石
	STRENG_QH_GOLD			(222),//装备强化收金币
	STRENG_SJ_GOLD			(223),//升阶收取金币
	STRENG_QH_TO_ITEM			(224),//强化失败给奖励
	ADDATTRIBUTE_ITEM(225), // 装备追加收取材料
	COMPOSE_EQUIP		(226),//合成需要材料
	COMPOSE_COIN		(227),//合成需要金币
	COMPOSE_BINDGOLD(228), // 合成需要绑钻
	COMPOSE_GOLD(229), // 合成需要钻石
	
	
	GEM_JH_ITEM				(230),	//宝石激活收取道具
	GEM_UP_ITEM				(231),	//宝石升级收取道具
	GEM_JH_ZHENQI				(232),	//宝石激活收取真气
	GEM_UP_ZHENQI			(233),	//宝石升级收取真气
	
	SPIRITTREE_MONEY		(240),	//灵树摘取果实得到金币
	SPIRITTREE_YUANBAO		(241),	//灵树摘取果实得到钻石
	SPIRITTREE_ITEM		(242),	//灵树摘取果实得到道具
	SPIRITTREE_EXP		(243),	//灵树摘取果实得到EXP
	SPIRITTREE_ZHENQI	(244),	//灵树摘取果实得到真气
	SPIRITTREE_WATER_EXP	(245),	//灵树浇水得到EXP
	SPIRITTREE_RIP_YUANBAO	(246),	//灵树果实催熟扣钻石
	SPIRITTREE_BIND_YUANBAO		(247),	//灵树摘取果实得到绑钻
	
	CARD_GIFT		(250),	//CDKEY领取礼包
	CARD_GIFT_MONEY		(251),	//CDKEY领取礼包金币
	CARD_GIFT_GOLD		(252),	//CDKEY领取礼包钻石
	CARD_GIFT_ITEM		(253),	//CDKEY领取礼包物品
	CARD_GIFT_REMOVE	(254),	//CDKEY领取礼包删除
	CARD_GIFT_BINDGOLD	(255),	//CDKEY领取礼包绑钻
	CARD_GIFT_PET		(256),	//CDKEY手机验证领取侍宠
	
	PLATFORM_GIFT       (257),  //领取平台礼包
	
	ACTIVITY_GIFT			(260), //活动赠送
	ACTIVITY_YBCARD		(261), //公测钻石卡赠送
	ACTIVITY_JINGYANDAN		(262), //上线送经验丹
	ACTIVITY_YBCARD_USE		(263),//公测钻石卡使用
	ACTIVITY_TESTYB		(264), //公测钻石使用后立即得到的钻石
	ACTIVITY_RNDYB		(265), //七夕活动，红色缘分袋 随机钻石
	ACTIVITY_RNDITEM	(266), //七夕活动，缘分袋 随机道具
	ACTIVITY_MONEY		(267), //七夕活动，缘分袋 随机金币
	ACTIVITY_LIJIN_GIFT		(268), //封测领取绑钻
	ACTIVITY_DEL		(269),		//活动移除
	
	FLOP_YB		(270), //副本奖励翻牌扣钻石
	RAID_YB		(271),	//立即完成扫荡
	
	
	
	RAID_MONEY		(272),	//副本奖励获得金币
	RAID_YUANBAO		(273),	//翻牌得到钻石
	RAID_ITEM		(274),	//翻牌得到道具
	RAID_EXP		(275),	//翻牌得到EXP
	RAID_ZHENQI	(277),	//翻牌得到真气
	RAID_BIND_YUANBAO		(276),	//翻牌得到绑钻
	RAID_LIANXU_YB   	(277), //立即完成连续扫荡
	
	ZONE_MONEY		(278),	//副本通关得到金币
	ZONE_EXP		(279),	//副本通关得到经验
	ZONE_ITEM		(279),	//副本通关得到道具
	
	ZONE_MIZONG_GETITEM	(280),	//迷踪副本获得物品奖励
	DIGONG_YBTASK	(290),	//地宫探险钻石完成任务
	DIGONG_GIVE_BINDGOLD (291),//地宫探险奖励绑定钻石
	DIGONG_GIVE_MONEY (292),//地宫探险奖励金币
	DIGONG_GIVE_GOLD (293),//地宫探险奖励钻石
	DIGONG_GIVE_ITEM (294),//地宫探险奖励道具
	
	TOPLIST_AWARD		(300),	//排行榜崇拜奖励
	TOPLIST_CHEST		(301),  //排行榜宝箱
	
	JIAOCHANGE_GETBINDGOLD	(310),	//校场获得绑钻
	
	KINGCITY_GETCHEST	(320),	//圣盟宝箱
	KINGCITY_ZHANCHE	(321),//攻城车发射
	KINGCITY_MONEY		(322),	//王城得到金币
	KINGCITY_YUANBAO	(323),	//王城得到钻石
	KINGCITY_ITEM		(324),	//王城得到道具
	KINGCITY_EXP		(325),	//王城得到EXP
	KINGCITY_ZHENQI		(326),	//王城得到真气
	KINGCITY_BIND_YUANBAO	(327),	//王城得到绑钻
	
	VIP_DAILYGIFT 		(330),  //VIP每日礼包
	VIP_DAILYMONEY 		(331),  //VIP每日金币
	VIP_DAILYBINDGOLD	(332),  //VIP每日绑钻
	VIP_CARD(333), // 使用vip卡
	
	REGISTRARREWARD    (340), //登录器每日奖励
	RECHARGEFIRST      (341), //账号首次充值奖励
	
	GEMQIANGHUA      (350), //宝石6级使用强化符;
	GEMACT      (351), //宝石激活符使用;
	GEMQIANGHUA_WUCAI      (352), //	五彩坚硬石;
	GEMQIANGHUA_QICAI      (353), //	七彩晶石;
	
	OFFLINE_RETREATDELITEM	(360),	//离线系统闭关删除物品
	OFFLINE_COST_GOLD(360), // 离线系统扣除钻石
	
	ARROW_BOWLVUPDELITEM	(370),	//弓箭系统箭支升级删除物品
	ARROW_DELGOLD		(371),	//弓箭系统七曜战魂消耗钻石
	
	RANK_DELGOLD		(380),	//军衔任务一键完成
	
	CONQUERTASKQUICKFINSH	(400),	//讨伐任务快速完成
	
	WAGE_OLD_LIJING    (410),//领取工资，上个月
	
	WAGE_CUR_LIJING    (411),//领取工资,当前月
	RANK_FANBEI     (421),//军功翻倍丹
	
	USE_EXP_DAN_A				(430),	//使用经验丹A
	USE_EXP_DAN_B				(431),	//使用经验丹B
	USE_ZHENQI_DAN_A				(432),	//使用真气丹A
	USE_ZHENQI_DAN_B				(433),	//使用真气丹B
	
	URGE_RIPE				(434),	//果实催熟
	URGE_YB_RIPE				(435),	//使用钻石果实连续催熟
	MEIREN				(440),	//购买美人
	
	COLLECT					(500),//物品收集扣除
	
	MELTING_GOLD			(520),	//熔炼收取钻石
	MELTING_MONEY			(521),	//熔炼收取金币
	MELTING_GETITEM			(522),	//出售紫装获得熔炼石
	MELTING_BUYITEM			(523),	//回购紫装花费钻石
	MELTING_ITEM			(524),	//熔炼收取熔炼石
	
	LONGHUANGNILING			(530),	//龙皇逆鳞使用
	
	HORSEWEAPON_ITEM		(540),	//骑战兵器使用物品
	HORSEWEAPON_GOLD		(541),	//骑战兵器使用钻石
	HORSEWEAPON_MONEY	    (542),	//骑战兵器使用金币
	
	CHESTBOX_ITEM			(550),	//宝箱获得物品
	CHESTBOX_MONEY			(551),	//宝箱得到金币
	CHESTBOX_YUANBAO		(552),	//宝箱得到钻石
	CHESTBOX_EXP			(553),	//宝箱得到EXP
	CHESTBOX_ZHENQI			(554),	//宝箱得到真气
	CHESTBOX_BIND_YUANBAO		(555),	//宝箱得到绑钻
	CHESTBOX_FIGHTSPIRIT		(556),	//宝箱得到战魂
	CHESTBOX_RANK			(557),	//宝箱得到军功
	CHESTBOX_DELGOLD		(558),	//宝箱扣除钻石
	CHESTBOX_DELITEM		(559),	//宝箱扣除物品
	
	BIWUDAO					(560), //比武岛
	DUANGUCAO				(562),	//坐骑锻骨草
	YUANDANFANPAI			(563),//元旦礼包翻牌	

	SHENGDAN				(570),//圣诞活动
	HOUSHENGDAN				(571),//后圣诞活动
	
	REALM_ITEM				(580),//境界突破收取道具
	REALM_GOLD				(581),//境界突破收取金币
	
	MARRIED_RING_MONEY			(590),//结婚购买戒指收取金币
	DIVORCE_GOLD			(591),//强行离婚收取金币

	WEDDING_GOLD			(592),//申请婚宴收取钻石
	WEDDING_MONEY			(593),//申请婚宴收取金币
	WEDDING_RED_MONEY		(594),//婚宴主人收取红包金币
	WEDDING_RED_NUM         (595),//婚宴收红包
	DIV_ITEM				(596),//离婚给戒指
	MARRIED_RING_GOLD		(597),//结婚购买戒指收取钻石

	HIDDENWEAPON_ITEM       (600),//暗器使用物品
	HIDDENWEAPON_GOLD       (601),//暗器使用钻石
	HIDDENWEAPON_MONEY      (602),//暗器使用金币

	NINGZDAN_ZHENQI			(610),//使用真气凝丹
	ADD_ZHENQI_DAN			(611),//使用真气凝丹
	
	MONTHBANK			        (701),//购买月卡钱庄
	MONTHBANK_FIST			    (702),//月卡即时奖励
	MONTHBANK_DAY			    (702),//月卡每日奖励
	LVBANK			        (701),//购买升级钱庄钱庄
	LVBANK_LV		        (702),//升级钱庄等级奖励
	
	PT_REWARD				(801),//20140102爬塔通关奖励
	LIVENESSS_REWARD		(802),//20140114活跃度宝箱领取
	PT_SWEEP				(803),//20140114扫荡通关奖励
	SHOW_ADD_ITEM_1			(808),//20140116道具入包新添加新表现定义

	PRAY			        (900),//祈愿
	GOLDRAFFLE_DEL_GOLD		(901),//钻石抽奖扣除钻石
	GOLDRAFFLE_DEL_ITEM		(902),//钻石抽奖扣除物品
	GOLDRAFFLE_GET_ITEM		(903),//钻石抽奖获得物品
	FRACTION			    (904),//积分兑换
	CASTING_REWARD          (905),//铸造工厂铸造出物品
	CASTING_ADD_BACKPACK    (906),//铸造工厂物品添加到背包
	CASTING_SEll            (907),//铸造工厂出售物品
	CASTING_EXCHANGE        (908),//铸造工厂兑换物品
	
	GOLD_RECHARGE			(999),//钻石充值			
	
	
	//后台 从10000开始
	BACKEND_GOLD (10000),		//后台修改玩家钻石
	BACKEND_BINDGOLD (10001), //后台修改玩家绑定钻石
	BACKEND_MONEY (10002),    //后台修改玩家金币
	
	TXITEM (10003), //腾讯发放道具
	TXGOLD (10004), //腾讯发放钻石
	
	
	
	
	
	
	/*活动占用号段 20001-25000*/
	ACTIVITY_XXBZD			(20001), //熊心豹子胆
	ACTIVITY_MRBBX			(20002), //美人百宝袋
	
	
	
	
	/*扩展类型*/
	def1					(100000),//激活极品宝石
	def2					(100001),//经验丹A 霸者经验丹
	def3					(100002),//经验丹B 王威经验丹
	def4					(100003),//真气丹A 霸者真气丹
	def5					(100004),//真气丹B 王威真气丹
	def6					(100005),//活动删除
	def7					(100006),//上线补偿
	def8					(100007),//经验丹C
	def9					(100008),//真气丹C
	def10					(100009),//百服礼包
	def11					(100010),//附加属性百宝箱
	def12					(100011),//花费钻石增加讨伐任务次数上限
	def13					(100012),//360礼包问题处理
	def14					(100013),//坐骑锻骨草
	def15					(100014),//单个属性百宝箱
	def16					(100015),//300百服活动使用美人江山王城帝王礼包。
	def17					(100016),//套装属性符
	def18					(100017),//开启绝世宝箱
	def19					(100018),//使用藏宝符
	def20					(100019),//使用精美礼包
	//1月16号下午 新加
	def21					(100021),//领取返还的绑钻
	def22					(100022),//激活对联
	def23					(100023),//使用春节大礼包
	def24					(100024),//骑兵隐藏属性激活
	def25					(100025),//多玩回档，10000绑钻补偿
	def26					(100026),//极品礼盒
	def27					(100027),//20130302活动礼包
	def28					(100028),
	def29					(100029),
	def30					(100030),
	def31					(100031),
	def32					(100032),
	def33					(100033),
	def34					(100034),
	def35					(100035),
	def36					(100036),
	def37					(100037),
	def38					(100038),
	def39					(100039),
	def40					(100040),
	//3月2日新加
	def41					(100041),
	def42					(100042),
	def43					(100043),
	def44					(100044),
	def45					(100045),
	def46					(100046),
	def47					(100047),
	def48					(100048),
	def49					(100049),
	def50					(100050),
	def51					(100051),
	def52					(100052),
	def53					(100053),
	def54					(100054),
	def55					(100055),
	def56					(100056),
	def57					(100057),
	def58					(100058),
	def59					(100059),
	def60					(100060),
	
	EQUIP_COMPOSE(100061),

	Pandora(100062), // 潘多拉宝盒
	
	FILL_SIGN(100063),	//补签
	SIGN_REWARD(100064),	//签到奖励领取

	PT_GAIN_AWARD(100065),		//领取爬塔奖励
	
	GRADE_GAIN_AWARD(100066),		//等级礼包领取奖励
	
	LOST_SKILL_STUDY(100067),		//遗落技能学习
	
	CHECK_ITEM_LOGIN(100068),//登录检查背包
	
	/*********新活动100100-100200***********/
	NEW_ACTIVITY_DEDUCT(100100),//活动扣除物品
	NEW_ACTIVITY_ADD(100101),//活动发放物品
	/************************/
	;
	
	private int value;
	
	Reasons(int value){
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
	public boolean compare(int value){
		return this.value == value;
	}
}
