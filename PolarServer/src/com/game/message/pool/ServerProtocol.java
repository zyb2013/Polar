package com.game.message.pool;

public abstract interface  ServerProtocol {
	/********* 登录流程 *********/
	public static final int ReqLoadFinish = 100204;//登录完成
	public static final int ReqLoginCharacterToGame = 100301;//请求登录
	public static final int ReqCreateCharacterToGame = 100304;//请求创建角色
	public static final int ReqQuitToGame = 100305;//请求退出游戏

	public static final int ResPlayerWorldInfo = 100308;
	public static final int ResPlayerNonageToGame = 100310;
	public static final int ReqRunning = 101201;
	public static final int ReqJump = 101202;
	public static final int ReqStartBlock = 101203;
	public static final int ReqStopBlock = 101204;
	public static final int ReqPlayerStop = 101205;
	public static final int ReqChangeMapByMove = 101206;
	public static final int ReqEnterMap = 101208;
	public static final int ReqNpcTrans = 101209;
	public static final int ReqGoldMapTrans = 101210;
	public static final int ReqGetLines = 101211;
	public static final int ReqSelectLine = 101212;
	public static final int ReqChangeDirect = 101213;
	public static final int ReqMonsterInfo = 101214;
	public static final int ReqLoadFinishForChangeMapToGame = 101301;
	public static final int ReqAttackMonster = 102201;
	public static final int ReqAttackPlayer = 102202;
	public static final int ReqChangeAttackTarget = 102203;
	public static final int ReqAttackPet = 102204;
	public static final int ReqAttackSummonPet = 102205;
	public static final int ReqRevive = 103201;
	public static final int ReqLocalRevive = 103202;
	public static final int ReqOtherPlayerInfo = 103203;
	public static final int ReqChangePKState = 103204;	//请求更改pk状态
	public static final int ReqAutoStart = 103205;
	public static final int ReqAutoEnd = 103206;
	public static final int ReqNonageTime = 103207;
	public static final int ReqPlayerNonageRegister = 103208;
	public static final int ReqPlayerAvatarChange = 103209;
	public static final int ReqPlayerCheckOnline = 103210;
	public static final int ReqGetPlayerAppearanceInfo = 103211;
	public static final int ReqChangePlayerName = 103212;
	public static final int ReqScriptCommonPlayerToServer = 103213;
	public static final int ReqVipPlayerChangeMapToServer = 103214;
	public static final int ReqOtherPlayerInfoToGame = 103308;
	public static final int ReqDelPlayerToGame = 103314;
	public static final int ResPlayerNameInfoToGame = 103316;
	public static final int ResChangePlayerNameToGame = 103319;
	public static final int ResScriptCommonServerToServer = 103329;
	public static final int ReqChangeOneAttribute = 103890;	//属性加点
	public static final int ReqAddAttributeItemToServer = 103892;//装备追加
	public static final int ReqComposeEquipToServer = 103893;	//道具合成
	/****** 背包 ******/
	public static final int ReqGetItems = 104201;// 获取背包物品信息
	public static final int ReqMoveItem = 104202;// 移动背包物品
	public static final int ReqUseItem = 104203;// 使用物品
	public static final int ReqClearUpGoods = 104205;// 背包整理
	public static final int ReqOpenCell = 104207;// 背包开格子
	public static final int ReqCellTimeQuery = 104208;// 查看背包格子开启剩余时间
	public static final int ReqDiscard = 104209;// 物品丢弃
	public static final int ReqDestroy = 104210;// 销毁
	public static final int ReqTakeUp = 104211;
	public static final int ReqOpenTimeCell = 104213;// 废用
	public static final int BuyItem = 105201;
	public static final int SellItem = 105202;
	public static final int ReqNotSale = 105203;
	public static final int ReqRebuy = 105204;
	public static final int ReqRebuyList = 105205;
	public static final int ReqShopList = 105206;
	public static final int ReqSellItems = 105207;
	public static final int WearEquip = 106201;// 穿戴装备
	public static final int UnwearEquip = 106202;// 卸载装备
	public static final int EquipTimeLost = 106203;// 过期装备
	public static final int StudySkill = 107201;		//学习技能
	public static final int LevelUpSkill = 107202;      //技能升级
	public static final int SetDefaultSkill = 107203;   //设置默认技能
	public static final int ReqCompleteMoment = 107204; //技能升级马上完成
	public static final int NowLearnSkillQuery = 107205;//当前正在学习的技能查询
	/********* 快捷键 *********/
	public static final int AddShortCut = 108201;		//增加快捷键
	public static final int RemoveShortCut = 108202;	//删除快捷键
	public static final int MoveShortCut = 108203;  	//移动快捷键
	public static final int ReqShowPet = 110201;
	public static final int ReqHiddenPet = 110202;
	public static final int ReqPetListQuery = 110203;
	public static final int ReqPetHeTi = 110204;
	public static final int ReqPetChangeSkill = 110205;
	public static final int ReqGoldReive = 110206;
	public static final int ReqGotPet = 110207;
	public static final int ReqHTCDClear = 110208;
	
	//聊天
	public static final int ChatRequest = 111201;		//聊天请求
	
	public static final int RoleQuery = 111202;
	public static final int ReqLoadBlackListWS = 111320;
	public static final int ReqStoreGetItems = 112200;//请求仓库物品信息
	public static final int ReqStoreMoveItem = 112201;//仓库移动物品
	public static final int ReqStoreClearUp = 112202;//整理仓库
	public static final int ReqStoreOpenCell = 112203;//仓库开格子
	public static final int ReqStoreCellTimeQuery = 112204;//废用
	public static final int ReqBagToStore = 112206;//从背包转移物品到仓库
	public static final int ReqStoreToBag = 112207;//从仓库转移物品到背包
	public static final int ReqOpenStore = 112208;
	public static final int ReqRemoveBuff = 113201;//请求移除buff
	public static final int ReqBuffInfo = 113202;//请求buff信息
	public static final int ReqQueryBossList = 114201;
	public static final int ReqQueryBossStateList = 114203;
	public static final int ResMonsterSync = 114302;
	public static final int ReqMonsterDoubleTimeToGame = 114304;
	public static final int ReqLongYuanOpen = 115201;
	public static final int ReqLongYuanActivation = 115202;
	public static final int ReqLongYuanTips = 115203;
	public static final int ReqLongYuanStarMapTips = 115204;
	public static final int GetDianjiangchunInfoToServer = 116201;
	public static final int GetBeginDianjiangchunToServer = 116202;
	public static final int GetChangeLuckToServer = 116203;
	public static final int GetReceiveintinfuriatingvalueToServer = 116204;
	
	//组队
	public static final int ReqCreateateamGame = 118201;      	//单独-创建队伍
	public static final int ReqApplyGame = 118202;  			//玩家申请入队
	public static final int ReqApplyGameSelect = 118203;		//申请入队-队长选择
	public static final int ReqInviteGame = 118204;				//邀请入队
	public static final int ReqInviteGameSelect = 118205;		//邀请入队-玩家选择
	public static final int ReqToleaveGame = 118206;			//玩家离队
	public static final int ReqAppointGame = 118207;			//任命新队长
	public static final int ReqAppointGameSelect = 118208;		//任命新队长-玩家选择
	public static final int ReqAutoTeaminvitedGame = 118209;	//自动组队邀请
	public static final int ReqAutoIntoTeamApplyGame = 118210;	//自动接受入队申请
	public static final int ReqUpdateTeaminfoGame = 118211;		//前端请求更新队伍消息，转发到世界服务器
	public static final int ReqMapSearchTeamInfoGame = 118212;  //前端请求搜索本地图队伍信息，转发到世界服务器 
	public static final int ReqMapSearchPlayerInfoGame = 118213;//前端请求搜索本地图玩家信息，转发到世界服务器
	public static final int ReqMapSearchMemberNameGame = 118214;//废用
	public static final int ReqGenericSearchToGame = 118215;	//废用
	public static final int ReqIntoTeamToGame = 118216;			//废用
	public static final int ResTeamGame = 118302;				//废用
	public static final int ResToleaveWorld = 118310;			//玩家离队后更新GAME
	public static final int ResTeamchangeToGame = 118321;		//队伍改变发送给改变的人
	
	public static final int ReqRelationGetListToServer = 119201;
	public static final int ReqRelationAddToServer = 119202;
	public static final int ReqRelationDeleteToServer = 119203;
	public static final int ReqRelationSortToServer = 119204;
	public static final int ReqRelationMoodToServer = 119205;
	public static final int ReqRelationConfigToServer = 119206;
	public static final int ResInnerRelationSendListToWorld = 119320;
	public static final int ResInnerRelationAddNotice = 119321;
	/********* 任务 *********/
	public static final int ResGiveUpTask = 120112;//放弃任务，废用
	public static final int ReqTaskReceive = 120201;//接受任务，废用
	public static final int ReqTaskFinsh = 120202;//请求完成任务
	public static final int ReqTaskUpAchieve = 120203;//日常任务提高奖励
	public static final int ReqTaskDownHard = 120204;//日常任务降低难度
	public static final int ReqReceiveRewards = 120205;//获取任务剩余奖励物品，废用
	public static final int ReqQuickFinsh = 120206;//日常任务快速完成
	public static final int ReqTaskInfoMation = 120207;//废用
	public static final int ReqConquerTaskDevour = 120208;//吞噬讨伐任务，废用
	public static final int ReqGiveUpTask = 120209;//放弃任务，废用
	public static final int ReqDailyTaskTrans = 120210;//日常任务传送
	public static final int ReqMapTrans = 120211;//地图传送
	public static final int ReqTreasureHuntTaskTrans = 120212;//废用
	public static final int ReqConquerTaskQuckFinsh = 120213;//讨伐任务快速完成，废用
	public static final int ReqRankTaskQuckFinsh = 120214;//军功任务快速完成，废用
	public static final int ReqRankTaskQuckFinshAll = 120215;//完成所有军功任务，废用
	public static final int ReqTaskGoldAddNum = 120216;//花费钻石改变讨伐任务接取次数上限
	public static final int ReqSaveGuides = 120217;//保存新手引导信息
	public static final int ReqGetGuides = 120218;//获取新手引导信息
	public static final int ResTargetMonsterToServer = 120303;
	
	//战盟
	public static final int ReqGuildCreateToServer = 121201;            	//创建战盟
	public static final int ReqGuildAutoArgeeAddGuildToServer = 121202; 	//玩家修改自动同意加入战盟设置
	public static final int ReqGuildGetGuildListToServer = 121203;      	//获取战盟列表
	public static final int ReqGuildApplyAddToServer = 121204;          	//申请加入战盟
	public static final int ReqGuildInviteAddToServer = 121205;         	//邀请加入战盟
	public static final int ReqGuildGetMemberListToServer = 121206;     	//获取成员列表
	public static final int ReqGuildAddMemberToServer = 121207;				//添加战盟成员
	public static final int ReqGuildQuitToServer = 121208;					//退出战盟
	public static final int ReqGuildChangeNickNameToServer = 121209;		//修改昵称
	public static final int ReqGuildChangePowerLevelToServer = 121210;		//修改职权
	public static final int ReqGuildDeleteMemberToServer = 121211;			//删除战盟成员
	public static final int ReqGuildAutoGuildArgeeAddToServer = 121212;		//盟主修改自动同意申请加入设置
	public static final int ReqGuildChangeBulletinToServer = 121213;		//修改公告
	public static final int ReqGuildSubmitItemToServer = 121214;			//提交战盟贡献物品
	public static final int ReqGuildChangeBannerIconToServer = 121215;		//更换盟旗造型
	public static final int ReqGuildChangeBannerNameToServer = 121216;		//更换盟旗名字
	public static final int ReqGuildBannerLevelUpToServer = 121217;			//盟旗升级
	public static final int ReqGuildAddDiplomaticToServer = 121218;			//废用
	public static final int ReqGuildDeleteDiplomaticToServer = 121219;		//废用
	public static final int ReqGuildDeleteGuildToServer = 121220;			//解散战盟
	public static final int ReqGuildGetEventListToServer = 121221;			//获取战盟事件列表
	public static final int ResInnerGuildAloneGuildInfoToServer = 121380; 	//通知游戏服务器单独战盟信息
	public static final int ResInnerGuildAloneMemberInfoToServer = 121381;	//通知游戏服务器单独成员信息
	
	public static final int ReqTransactionsLaunch = 122201;
	public static final int ReqTransactionsAccept = 122202;
	public static final int ReqTransactionsRefuse = 122203;
	public static final int ReqTransactionsCanceled = 122204;
	public static final int ReqTransactionsIntoItem = 122205;
	public static final int ReqTransactionsRemoveItem = 122206;
	public static final int ReqTransactionsChangeGold = 122207;
	public static final int ReqTransactionsChangeYuanbao = 122208;
	public static final int ReqTransactionsSetState = 122209;
	public static final int ReqCanreceiveYuanbao = 122210;
	public static final int ReqAutorefusaldeal = 122211;
	public static final int ReqOpenTmpYuanbaoPanel = 122212;
	public static final int ReqGetTmpYuanbao = 122213;
	public static final int ReqStallsOpenUp = 123201;
	public static final int ReqStallsPlayerIdLook = 123202;
	public static final int ReqStallsSort = 123203;
	public static final int ReqStallsBuy = 123204;
	public static final int ReqStallsProductWasAdded = 123205;
	public static final int ReqStallsAdjustPrices = 123206;
	public static final int ReqStallsOffShelf = 123207;
	public static final int ReqStallsSearch = 123208;
	public static final int ReqStallsLooklog = 123209;
	public static final int ReqChangeStallsName = 123211;
	public static final int ResStallsBuycheckToGame = 123305;
	public static final int ResStallsBuyDeductingToGame = 123307;
	public static final int ResStallsBuyAddMoneyToGame = 123308;
	public static final int ResStallsBuyDeductingFailToGame = 123309;
	public static final int ResStallsProductWasAddedFailToGame = 123311;
	public static final int ResStallsOffShelfToGame = 123315;
	public static final int ReqMailQueryListToServer = 124151;
	public static final int ReqMailQueryDetailToServer = 124152;
	public static final int ReqMailGetItemToServer = 124153;
	public static final int ReqMailSendNewMailToServer = 124154;
	public static final int ReqMailDeleteMailToServer = 124155;
	public static final int ReqMailQueryUserToServer = 124156;
	public static final int ReqMailReturnToServer = 124157;
	public static final int ReqMailListGetItemToServer = 124158;
	public static final int ResMailSendSystemMailToServer = 124201;
	public static final int ReqGetMenuStatus = 125201;
	public static final int ReqSetMenuStatus = 125202;
	
	//坐骑
	public static final int ReqGethorseInfo = 126201;           //前端请求坐骑信息
	public static final int ReqChangeRidingState = 126202;      //改变骑乘状态
	public static final int ReqhorseStageUpPanel = 126203;		//废用
	public static final int ReqhorseStageUpStart = 126204;      //开始坐骑升级
	public static final int ReqhorseLuckyRod = 126205;			//废用
	public static final int ReqhorseLuckyPente = 126206;		//废用
	public static final int ReqhorseReceive = 126207;           //领取坐骑
	public static final int ReqhorseClearCD = 126208;			//废用
	public static final int ReqOpenSkillUpPanel = 126209;		//废用
	public static final int ReqSkillInfo = 126210;				//废用
	public static final int ReqChangeHorse = 126211;            //骑乘状态更换坐骑
	
	public static final int ReqFightPowerToServer = 127101;
	public static final int ReqZonePanelSelect = 128201;
	public static final int ReqZoneInto = 128202;
	public static final int ReqSelectAward = 128203;
	public static final int ReqZoneOut = 128204;
	public static final int ReqZoneImmediately = 128205;
	public static final int ReqZoneOpenPanel = 128206;
	public static final int ReqZoneReceiveawards = 128207;
	public static final int ReqZoneContinuousRaids = 128208;
	public static final int ReqZoneContinuousRaidsYB = 128209;
	public static final int ReqZoneContinuousRaidsStop = 128210;
	public static final int ReqZoneTeamEnterToGame = 128211;
	public static final int ReqZoneTeamSelectToGame = 128212;
	public static final int ReqZoneTeamOpenToGame = 128213;
	public static final int ReqZoneAutoAwardToGame = 128214;
	public static final int ReqZoneImmediatelyByCost = 128215;
	public static final int ReqZoneCancelSignupToGame = 128216;
	public static final int ResfastestClearanceToGame = 128301;
	public static final int ReqGetGiftItemsToServer = 129201;
	public static final int ReqGetOtherGiftItemsToServer = 129202;
	public static final int ReqGetPlatformGift = 129203;
	public static final int ReqPlatformGiftList = 129204;
	public static final int ReqStartShuffleRaffle = 129205;
	
	public static final int ReqStrengItemToServer = 130201;//强化装备
	public static final int ReqStageUpItemToServer = 130202;//废用
	public static final int ReqStrengClearCooling = 130203;//废用
	public static final int ReqStrengthenState = 130204;//废用
	
	public static final int ReqPlugSetInfo = 131201;
	public static final int ReqPlugBackToCity = 131202;
	public static final int ReqGetAssistant = 131208;//获取战斗助手信息
	public static final int ReqSaveAssistant = 131210;//保存战斗助手信息
	public static final int ReqTaskTrans = 131212;//任务小飞鞋传送
	public static final int ReqOpenGemPanel = 132201;
	public static final int ReqGemInto = 132202;
	public static final int ReqGemActivationORUp = 132203;
	public static final int ReqGemUseStrengthen = 132204;
	public static final int ReqGetAllFruitInfoToGame = 133201;
	public static final int ReqGetSingleFruitInfoToGame = 133202;
	public static final int ReqRipeningFruitToGame = 133203;
	public static final int ReqOpenGuildFruitToGame = 133204;
	public static final int ReqGuildFruitOperatingToGame = 133205;
	public static final int ReqGuildFruitLogToGame = 133206;
	public static final int ReqContinuousRipeningToGame = 133207;
	public static final int ResGameMakeFruitInfo = 133303;
	public static final int ResFruitOperatingToGame = 133307;
	public static final int ReqRipeningDecYBToGame = 133308;
	public static final int ResGuildAccExpToWorld = 133311;
	public static final int ResActivityReturnFruitToGame = 133314;
	public static final int Recharge = 134301;
	public static final int ReqPlayerMoneyGoldToGame = 135301;
	public static final int ReqPlayerInfoToGame = 135302;
	public static final int ReqJinYan = 135305;
	public static final int ReqChangePlayerCurrency = 135306;
	public static final int ReqDazuo = 136201;
	public static final int ReqShuangXiu = 136202;
	public static final int ReqAgreeShuangXiu = 136203;
	public static final int ReqRefuseShuangXiu = 136204;
	public static final int ReqCardToServer = 137101;		//请求验证手机
	public static final int ReqCardPhoneToServer = 137102;
	public static final int ResInnerCardToServer = 137301;
	public static final int ReqGetReward = 138201;
	public static final int ReqActivitiesInfo = 138202;
	public static final int ReqReceiveLiJinGift = 138203;
	public static final int ReqYBCardToGame = 139201;
	public static final int ResYBCardReceiveToGame = 139302;
	public static final int ResYBCardNoticeToGame = 139303;
	public static final int ResYBCardAddYBPlayerToGame = 139304;
	public static final int ReqNpcServices = 140201;//请求获取npc身上的服务列表
	public static final int ReqService = 140202;
	public static final int ReqGetTopListToServer = 142201;
	public static final int ReqWorShipToServer = 142202;
	public static final int ReqChangeTitleToServer = 142203;
	public static final int ReqRecieveChestToServer = 142204;
	public static final int ResGetTopAwardToServer = 142351;
	public static final int ResGetTopTitleToServer = 142352;
	public static final int ReqEpalaceDiceToGame = 143201;
	public static final int ReqEpalaceOpenToGame = 143202;
	public static final int ReqEpalaceTaskEndToGame = 143203;
	public static final int ReqEpalaceAbandonTaskToGame = 143204;
	public static final int ReqOpenChallengeToGame = 144201;
	public static final int ReqSelectChallengeToGame = 144202;
	public static final int ResCountrySiegeSelectToGame = 146201;
	public static final int ReqCountryWarCarToGame = 146202;
	public static final int ReqCountryStructureInfoToGame = 146203;
	public static final int ReqCountrysalaryToGame = 146204;
	public static final int ReqKingCityChestSelectToGame = 146205;
	public static final int ReqCountryWarCarInAdvanceToGame = 146206;
	public static final int ResCountrySyncKingCityToGame = 146302;
	public static final int ReqReceiveVIPReward = 147201;//废用
	public static final int ReqPlayerVIPInfo = 147202;//获取vip信息
	public static final int ReqReceiveVIPTopReward = 147203;//废用
	public static final int ReqOpenVIP = 147205;//开通vip
	public static final int ReqScriptExcute = 148201;
	public static final int ReqScriptToGame = 148302;
	public static final int ReqOpenGuildFlagToGame = 149201;
	public static final int ReqInsertGuildFlagToGame = 149202;
	public static final int ReqRetreatInfo = 150201;
	public static final int ReqGetRetreatAward = 150202;
	public static final int ReqArrowInfoOpen = 151201;
	public static final int ReqStarActivation = 151202;
	public static final int ReqBowActivation = 151203;
	public static final int ReqFightSpiritOpen = 151204;
	public static final int ReqFightSpiritGet = 151205;
	
	
	public static final int ReqSubmitFragByType = 153201;
	public static final int ReqSubmitFrag = 153202;
	public static final int ReqMeltingItemToServer = 154201;
	public static final int ReqGetHorseWeaponInfo = 155201;			//废用
	public static final int ReqWearHorseWeaponState = 155202;		//废用
	public static final int ReqHorseWeaponStageUpPanel = 155203;	//废用
	public static final int ReqHorseWeaponStageUpStart = 155204;	//废用
	public static final int ReqSetHorseWeaponSkill = 155205;		//废用
	public static final int ReqChestBoxShowPanelToServer = 156201;	//废用
	public static final int ReqChestBoxOpenToServer = 156202;		//废用
	public static final int ReqChestBoxGetGridItemToServer = 156203;//废用
	public static final int ReqChestBoxAutoSetToServer = 156204;	//废用
	public static final int ReqBiWuDaoSelectToGame = 157201;
	public static final int ReqGetQuestionJoin = 158201;
	public static final int ReqGetQuestionChoose = 158202;
	public static final int GmCommand = 200201;
	public static final int GmCommandToServer = 200303;
	public static final int LogInfo = 201201;
	public static final int ResVersionUpdateToGame = 202302;
	public static final int ReqChangeServer = 203201;
	public static final int ReqSyncPlayerToDataServer = 203301;
	public static final int ResSyncPlayerToDataServer = 203302;
	public static final int ResRegisterGate = 300302;
	public static final int ResRegisterWorld = 300304;
	public static final int ReqCloseForGame = 300307;
	public static final int ReqRegisterGameForPublic = 300311;
	public static final int ResRegisterGameForPublic = 300312;
	public static final int ReqCheckToGame = 300313;
	public static final int ReqAttackFriend = 502201;
	public static final int ReqAttackPostion = 502202;
	public static final int ReqAttackSpecial = 502203;
	public static final int ReqAddBuff = 528316;
	public static final int ReqClearZoneCD = 528318;
	public static final int ReqInventedZone = 528320;
	
	//祈愿
	public static final int ReqPrayInfo = 528330;               //打开祈愿界面
	public static final int ReqPray = 528331;                   //请求祈愿

	//钻石抽奖
	public static final int ReqOpenRaffleInfoToServer = 528201;         //客户端请求打开钻石抽奖界面
	public static final int ReqGoldRaffleToServer = 528202;             //客户端请求钻石抽奖
	public static final int ReqOpendGoldRaffleBoxInfoToServer = 528203; //客户端请求打开钻石抽奖宝箱
	public static final int ReqGoldRaffleBoxUseItemToServer = 528204;   //客户端请求使用钻石抽奖宝箱物品
	public static final int ReqFractionToServer = 528205;               //客户端请求积分兑换
	public static final int ReqGoldRaffleArrangeToServer = 528206;      //客户端请求整理钻石抽奖宝箱

	//铸造工厂
	public static final int ReqCastingOpenToServer = 529201;            //客户端请求打开铸造工厂界面消息
	public static final int ReqCastingRewardToServer = 529202;          //客户端请求铸造和一键锻造消息
	public static final int ReqCastingUseItemToServer = 529203;         //客户端请求使用铸造奖励仓库物品消息
	public static final int ReqCastingDecomposeToServer = 529204;       //客户端请求一键分解消息
	public static final int ReqCastingSellToServer = 529205;            //客户端请求一键出售消息
	public static final int ReqCastingExchangeToServer = 529206;        //客户端请求铸造兑换消息
	
	public static final int ResGetTopGuildListToGame = 542331;
	
	
	//活跃度模块
	public static final int ReqLivenessToServer = 600100;		//前端请求活跃度值
	public static final int ReqLivenessEventsToServer = 600102;	//前端请求活跃度事件进度列表
	public static final int ReqGainStateToServer = 600106;		//前端请求活跃度奖励箱子的领取状态列表
	public static final int ReqGainBoxToServer = 600104;		//前端请求领取活跃度箱子奖励
	
	//签到模块
	public static final int ReqClickSignToClient = 152206;		//角色今天签到
	public static final int ReqFillSign = 601000;				//角色补签
	public static final int ReqSignToClient = 152201;			//角色请求本月的签到信息
	public static final int ReqReceiveAwardsToClient = 152203;	//角色请求领取签到奖励
	
	//手机验证
//	public static final int ReqPhoneVerifyToServer = 620001;	//请求手机绑定验证
	
	//爬塔副本
//	public static final int ReqPtMain = 610000;	废弃功能
	public static final int ReqTowerIndex = 600200;
	public static final int ReqTowerReward = 600201;
	public static final int ReqTowerNextLv = 600202;
	public static final int ReqSpirit = 600203;
	
	//遗落技能
	public static final int ReqActivateLostSkill = 600206;
	public static final int ReqLostSkillInfos    = 600205;
	
	//等级礼包
	public static final int ReqGainGradeGift = 600204;

	public static final int TestReqLoadFinish = 900204;
	
	//领取收藏夹奖励
	public static final int CollectionReward = 55002;
	
	public static final int ReqBuyBank = 510003; //购买钱庄
	public static final int ReqGetLvBank = 510004;//获得升级钱庄奖励
	public static final int ReqGetMonthBank = 510006;//获得月卡钱庄奖励
	public static final int ReqQueryBank = 510002;//查询钱庄状态
	public static final int ReqQueryBankLog = 510005;//查询钱庄记录
	
	public static final int ReqCsysTopList = 550202;//请求 排行榜信息
	
	
	public static final int ReqGetAngle = 550121;//请求 获得大天使武器
	public static final int ReqCJinYan = 550122;// 盟主 特权  禁言
	
	/************活动*************/
	public static final int ResNewActivityList = 511000;//推送活动列表包含每个活动可领奖励数字（所有活动列表）
	public static final int ReqGetNewActivityInfo = 511001;//获取具体活动信息
	public static final int ResGetNewActivityInfo = 511002;//返回具体活动信息
	public static final int ReqGetNewActivityAward = 511003;//领取奖励
	public static final int ResGetNewActivityAward = 511004;//返回领取结果
	//public static final int ResNewActivityListChange = 511005;//推送活动列表变化
	public static final int ReqGetRankInfoMessage = 511006;//玩家请求排行榜信息
	public static final int ResGetRankInfoMessage = 511007;//返回排行榜信息
	public static final int ResAddActivityMessage = 511008;//增加活动
	public static final int ResRemoveActivityMessage = 511009;//删除活动
	public static final int ReqGetRankInfo2WorldMessage = 511308;//向世界服请求排行榜信息
	public static final int ResGetRankInfo2WorldMessage = 511309;//世界服返回排行榜信息
	public static final int ReqGetActivityList2WorldMessage = 511310;//世界服请求活动列表信息
	public static final int ResGetActivityList2WorldMessage = 511311;//向世界服返回活动列表信息
	public static final int ReqUpdateActivity2WorldMessage = 511312;//世界服请求修改活动信息
	public static final int ResUpdateActivity2WorldMessage = 511313;//向世界服返回修改活动信息
	
	//激活属性变更
	public static final int ResAttributeChangeMessage = 511010;
	//职业变更
	public static final int ResRoundChangeJobMessage = 511011;
	
}
