// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events;

import org.l2j.gameserver.model.events.impl.character.player.OnElementalSpiritLearn;
import org.l2j.gameserver.model.events.impl.character.OnElementalSpiritUpgrade;
import org.l2j.gameserver.model.events.impl.instance.OnInstanceStatusChange;
import org.l2j.gameserver.model.events.impl.instance.OnInstanceLeave;
import org.l2j.gameserver.model.events.impl.instance.OnInstanceEnter;
import org.l2j.gameserver.model.events.impl.instance.OnInstanceDestroy;
import org.l2j.gameserver.model.events.impl.instance.OnInstanceCreated;
import org.l2j.gameserver.model.events.impl.OnDayNightChange;
import org.l2j.gameserver.model.events.impl.character.player.OnTrapAction;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayeableChargeShots;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerPeaceZoneExit;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerPeaceZoneEnter;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerUnsummonAgathion;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerSummonAgathion;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerQuestComplete;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerQuestAbort;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerSubChange;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerTransform;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerSummonTalk;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerSummonSpawn;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerSkillLearn;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerSocialAction;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerPvPKill;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerPvPChanged;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerChangeToAwakenedClass;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerProfessionCancel;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerProfessionChange;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerMoveRequest;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerTutorialEvent;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerPressTutorialMark;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerPKChanged;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogout;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLoad;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerSelect;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerRestore;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLevelChanged;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerReputationChanged;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerMentorStatus;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerMenteeStatus;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerMenteeRemove;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerMenteeLeft;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerMenteeAdd;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerItemTransfer;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerItemPickup;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerItemDrop;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerItemDestroy;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerItemAdd;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerHennaRemove;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerHennaAdd;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerFishing;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerFameChanged;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerEquipItem;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerDlgAnswer;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerDelete;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerCreate;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanWHItemTransfer;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanWHItemDestroy;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanWHItemAdd;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanLvlUp;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanLeft;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanLeaderChange;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanJoin;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanDestroy;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanCreate;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerMpChange;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerHpChange;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerCpChange;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerAbilityPointsChanged;
import org.l2j.gameserver.model.events.returns.ChatFilterReturn;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerChat;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerCallToChangeClass;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerBypass;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerAugment;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayableExpChanged;
import org.l2j.gameserver.model.events.impl.olympiad.OnOlympiadMatchResult;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcTeleportRequest;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcDespawn;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcMenuSelect;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcManorBypass;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcTeleport;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcSpawn;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcSkillSee;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcSkillFinished;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcMoveRouteFinished;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcMoveFinished;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableHate;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcFirstTalk;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcEventReceived;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcCreatureSee;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcCanBeSeen;
import org.l2j.gameserver.model.events.impl.item.OnItemTalk;
import org.l2j.gameserver.model.events.impl.item.OnItemCreate;
import org.l2j.gameserver.model.events.impl.item.OnItemBypassEvent;
import org.l2j.gameserver.model.events.impl.character.OnCreatureZoneExit;
import org.l2j.gameserver.model.events.impl.character.OnCreatureZoneEnter;
import org.l2j.gameserver.model.events.impl.character.OnCreatureTeleported;
import org.l2j.gameserver.model.events.returns.LocationReturn;
import org.l2j.gameserver.model.events.impl.character.OnCreatureTeleport;
import org.l2j.gameserver.model.events.impl.character.OnCreatureSkillFinishCast;
import org.l2j.gameserver.model.events.impl.character.OnCreatureSkillUse;
import org.l2j.gameserver.model.events.impl.character.OnCreatureSee;
import org.l2j.gameserver.model.events.impl.character.OnCreatureKilled;
import org.l2j.gameserver.model.events.impl.character.OnCreatureDeath;
import org.l2j.gameserver.model.events.impl.character.OnCreatureHpChange;
import org.l2j.gameserver.model.events.impl.character.OnCreatureDamageDealt;
import org.l2j.gameserver.model.events.returns.DamageReturn;
import org.l2j.gameserver.model.events.impl.character.OnCreatureDamageReceived;
import org.l2j.gameserver.model.events.impl.character.OnCreatureAttacked;
import org.l2j.gameserver.model.events.impl.character.OnCreatureAttackAvoid;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.model.events.impl.character.OnCreatureAttack;
import org.l2j.gameserver.model.events.impl.clan.OnClanWarStart;
import org.l2j.gameserver.model.events.impl.clan.OnClanWarFinish;
import org.l2j.gameserver.model.events.impl.sieges.OnCastleSiegeStart;
import org.l2j.gameserver.model.events.impl.sieges.OnCastleSiegeOwnerChange;
import org.l2j.gameserver.model.events.impl.sieges.OnCastleSiegeFinish;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableKill;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableFactionCall;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableAttack;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableAggroRangeEnter;
import org.l2j.commons.util.CommonUtil;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public enum EventType
{
    ON_ATTACKABLE_AGGRO_RANGE_ENTER((Class<? extends IBaseEvent>)OnAttackableAggroRangeEnter.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_ATTACKABLE_ATTACK((Class<? extends IBaseEvent>)OnAttackableAttack.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_ATTACKABLE_FACTION_CALL((Class<? extends IBaseEvent>)OnAttackableFactionCall.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_ATTACKABLE_KILL((Class<? extends IBaseEvent>)OnAttackableKill.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_CASTLE_SIEGE_FINISH((Class<? extends IBaseEvent>)OnCastleSiegeFinish.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_CASTLE_SIEGE_OWNER_CHANGE((Class<? extends IBaseEvent>)OnCastleSiegeOwnerChange.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_CASTLE_SIEGE_START((Class<? extends IBaseEvent>)OnCastleSiegeStart.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_CLAN_WAR_FINISH((Class<? extends IBaseEvent>)OnClanWarFinish.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_CLAN_WAR_START((Class<? extends IBaseEvent>)OnClanWarStart.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_CREATURE_ATTACK((Class<? extends IBaseEvent>)OnCreatureAttack.class, (Class<?>[])new Class[] { Void.TYPE, TerminateReturn.class }), 
    ON_CREATURE_ATTACK_AVOID((Class<? extends IBaseEvent>)OnCreatureAttackAvoid.class, (Class<?>[])new Class[] { Void.TYPE, Void.TYPE }), 
    ON_CREATURE_ATTACKED((Class<? extends IBaseEvent>)OnCreatureAttacked.class, (Class<?>[])new Class[] { Void.TYPE, TerminateReturn.class }), 
    ON_CREATURE_DAMAGE_RECEIVED((Class<? extends IBaseEvent>)OnCreatureDamageReceived.class, (Class<?>[])new Class[] { Void.TYPE, DamageReturn.class }), 
    ON_CREATURE_DAMAGE_DEALT((Class<? extends IBaseEvent>)OnCreatureDamageDealt.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_CREATURE_HP_CHANGE((Class<? extends IBaseEvent>)OnCreatureHpChange.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_CREATURE_DEATH((Class<? extends IBaseEvent>)OnCreatureDeath.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_CREATURE_KILLED((Class<? extends IBaseEvent>)OnCreatureKilled.class, (Class<?>[])new Class[] { Void.TYPE, TerminateReturn.class }), 
    ON_CREATURE_SEE((Class<? extends IBaseEvent>)OnCreatureSee.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_CREATURE_SKILL_USE((Class<? extends IBaseEvent>)OnCreatureSkillUse.class, (Class<?>[])new Class[] { Void.TYPE, TerminateReturn.class }), 
    ON_CREATURE_SKILL_FINISH_CAST((Class<? extends IBaseEvent>)OnCreatureSkillFinishCast.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_CREATURE_TELEPORT((Class<? extends IBaseEvent>)OnCreatureTeleport.class, (Class<?>[])new Class[] { Void.TYPE, LocationReturn.class }), 
    ON_CREATURE_TELEPORTED((Class<? extends IBaseEvent>)OnCreatureTeleported.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_CREATURE_ZONE_ENTER((Class<? extends IBaseEvent>)OnCreatureZoneEnter.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_CREATURE_ZONE_EXIT((Class<? extends IBaseEvent>)OnCreatureZoneExit.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_ITEM_BYPASS_EVENT((Class<? extends IBaseEvent>)OnItemBypassEvent.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_ITEM_CREATE((Class<? extends IBaseEvent>)OnItemCreate.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_ITEM_TALK((Class<? extends IBaseEvent>)OnItemTalk.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_NPC_CAN_BE_SEEN((Class<? extends IBaseEvent>)OnNpcCanBeSeen.class, (Class<?>[])new Class[] { Void.TYPE, TerminateReturn.class }), 
    ON_NPC_CREATURE_SEE((Class<? extends IBaseEvent>)OnNpcCreatureSee.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_NPC_EVENT_RECEIVED((Class<? extends IBaseEvent>)OnNpcEventReceived.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_NPC_FIRST_TALK((Class<? extends IBaseEvent>)OnNpcFirstTalk.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_NPC_HATE((Class<? extends IBaseEvent>)OnAttackableHate.class, (Class<?>[])new Class[] { Void.TYPE, TerminateReturn.class }), 
    ON_NPC_MOVE_FINISHED((Class<? extends IBaseEvent>)OnNpcMoveFinished.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_NPC_MOVE_ROUTE_FINISHED((Class<? extends IBaseEvent>)OnNpcMoveRouteFinished.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_NPC_QUEST_START((Class<? extends IBaseEvent>)null, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_NPC_SKILL_FINISHED((Class<? extends IBaseEvent>)OnNpcSkillFinished.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_NPC_SKILL_SEE((Class<? extends IBaseEvent>)OnNpcSkillSee.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_NPC_SPAWN((Class<? extends IBaseEvent>)OnNpcSpawn.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_NPC_TALK((Class<? extends IBaseEvent>)null, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_NPC_TELEPORT((Class<? extends IBaseEvent>)OnNpcTeleport.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_NPC_MANOR_BYPASS((Class<? extends IBaseEvent>)OnNpcManorBypass.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_NPC_MENU_SELECT((Class<? extends IBaseEvent>)OnNpcMenuSelect.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_NPC_DESPAWN((Class<? extends IBaseEvent>)OnNpcDespawn.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_NPC_TELEPORT_REQUEST((Class<? extends IBaseEvent>)OnNpcTeleportRequest.class, (Class<?>[])new Class[] { Void.TYPE, TerminateReturn.class }), 
    ON_OLYMPIAD_MATCH_RESULT((Class<? extends IBaseEvent>)OnOlympiadMatchResult.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYABLE_EXP_CHANGED((Class<? extends IBaseEvent>)OnPlayableExpChanged.class, (Class<?>[])new Class[] { Void.TYPE, TerminateReturn.class }), 
    ON_PLAYER_AUGMENT((Class<? extends IBaseEvent>)OnPlayerAugment.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_BYPASS((Class<? extends IBaseEvent>)OnPlayerBypass.class, (Class<?>[])new Class[] { Void.TYPE, TerminateReturn.class }), 
    ON_PLAYER_CALL_TO_CHANGE_CLASS((Class<? extends IBaseEvent>)OnPlayerCallToChangeClass.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_CHAT((Class<? extends IBaseEvent>)OnPlayerChat.class, (Class<?>[])new Class[] { Void.TYPE, ChatFilterReturn.class }), 
    ON_PLAYER_ABILITY_POINTS_CHANGED((Class<? extends IBaseEvent>)OnPlayerAbilityPointsChanged.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_CP_CHANGE((Class<? extends IBaseEvent>)OnPlayerCpChange.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_HP_CHANGE((Class<? extends IBaseEvent>)OnPlayerHpChange.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_MP_CHANGE((Class<? extends IBaseEvent>)OnPlayerMpChange.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_CLAN_CREATE((Class<? extends IBaseEvent>)OnPlayerClanCreate.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_CLAN_DESTROY((Class<? extends IBaseEvent>)OnPlayerClanDestroy.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_CLAN_JOIN((Class<? extends IBaseEvent>)OnPlayerClanJoin.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_CLAN_LEADER_CHANGE((Class<? extends IBaseEvent>)OnPlayerClanLeaderChange.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_CLAN_LEFT((Class<? extends IBaseEvent>)OnPlayerClanLeft.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_CLAN_LVLUP((Class<? extends IBaseEvent>)OnPlayerClanLvlUp.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_CLAN_WH_ITEM_ADD((Class<? extends IBaseEvent>)OnPlayerClanWHItemAdd.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_CLAN_WH_ITEM_DESTROY((Class<? extends IBaseEvent>)OnPlayerClanWHItemDestroy.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_CLAN_WH_ITEM_TRANSFER((Class<? extends IBaseEvent>)OnPlayerClanWHItemTransfer.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_CREATE((Class<? extends IBaseEvent>)OnPlayerCreate.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_DELETE((Class<? extends IBaseEvent>)OnPlayerDelete.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_DLG_ANSWER((Class<? extends IBaseEvent>)OnPlayerDlgAnswer.class, (Class<?>[])new Class[] { Void.TYPE, TerminateReturn.class }), 
    ON_PLAYER_EQUIP_ITEM((Class<? extends IBaseEvent>)OnPlayerEquipItem.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_FAME_CHANGED((Class<? extends IBaseEvent>)OnPlayerFameChanged.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_FISHING((Class<? extends IBaseEvent>)OnPlayerFishing.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_HENNA_ADD((Class<? extends IBaseEvent>)OnPlayerHennaAdd.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_HENNA_REMOVE((Class<? extends IBaseEvent>)OnPlayerHennaRemove.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_ITEM_ADD((Class<? extends IBaseEvent>)OnPlayerItemAdd.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_ITEM_DESTROY((Class<? extends IBaseEvent>)OnPlayerItemDestroy.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_ITEM_DROP((Class<? extends IBaseEvent>)OnPlayerItemDrop.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_ITEM_PICKUP((Class<? extends IBaseEvent>)OnPlayerItemPickup.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_ITEM_TRANSFER((Class<? extends IBaseEvent>)OnPlayerItemTransfer.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_MENTEE_ADD((Class<? extends IBaseEvent>)OnPlayerMenteeAdd.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_MENTEE_LEFT((Class<? extends IBaseEvent>)OnPlayerMenteeLeft.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_MENTEE_REMOVE((Class<? extends IBaseEvent>)OnPlayerMenteeRemove.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_MENTEE_STATUS((Class<? extends IBaseEvent>)OnPlayerMenteeStatus.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_MENTOR_STATUS((Class<? extends IBaseEvent>)OnPlayerMentorStatus.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_REPUTATION_CHANGED((Class<? extends IBaseEvent>)OnPlayerReputationChanged.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_LEVEL_CHANGED((Class<? extends IBaseEvent>)OnPlayerLevelChanged.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_RESTORE((Class<? extends IBaseEvent>)OnPlayerRestore.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_SELECT((Class<? extends IBaseEvent>)OnPlayerSelect.class, (Class<?>[])new Class[] { Void.TYPE, TerminateReturn.class }), 
    ON_PLAYER_LOAD((Class<? extends IBaseEvent>)OnPlayerLoad.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_LOGIN((Class<? extends IBaseEvent>)OnPlayerLogin.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_LOGOUT((Class<? extends IBaseEvent>)OnPlayerLogout.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_PK_CHANGED((Class<? extends IBaseEvent>)OnPlayerPKChanged.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_PRESS_TUTORIAL_MARK((Class<? extends IBaseEvent>)OnPlayerPressTutorialMark.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_TUTORIAL_EVENT((Class<? extends IBaseEvent>)OnPlayerTutorialEvent.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_MOVE_REQUEST((Class<? extends IBaseEvent>)OnPlayerMoveRequest.class, (Class<?>[])new Class[] { Void.TYPE, TerminateReturn.class }), 
    ON_PLAYER_PROFESSION_CHANGE((Class<? extends IBaseEvent>)OnPlayerProfessionChange.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_PROFESSION_CANCEL((Class<? extends IBaseEvent>)OnPlayerProfessionCancel.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_CHANGE_TO_AWAKENED_CLASS((Class<? extends IBaseEvent>)OnPlayerChangeToAwakenedClass.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_PVP_CHANGED((Class<? extends IBaseEvent>)OnPlayerPvPChanged.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_PVP_KILL((Class<? extends IBaseEvent>)OnPlayerPvPKill.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_SOCIAL_ACTION((Class<? extends IBaseEvent>)OnPlayerSocialAction.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_SKILL_LEARN((Class<? extends IBaseEvent>)OnPlayerSkillLearn.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_SUMMON_SPAWN((Class<? extends IBaseEvent>)OnPlayerSummonSpawn.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_SUMMON_TALK((Class<? extends IBaseEvent>)OnPlayerSummonTalk.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_TRANSFORM((Class<? extends IBaseEvent>)OnPlayerTransform.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_SUB_CHANGE((Class<? extends IBaseEvent>)OnPlayerSubChange.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_QUEST_ABORT((Class<? extends IBaseEvent>)OnPlayerQuestAbort.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_QUEST_COMPLETE((Class<? extends IBaseEvent>)OnPlayerQuestComplete.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_SUMMON_AGATHION((Class<? extends IBaseEvent>)OnPlayerSummonAgathion.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_UNSUMMON_AGATHION((Class<? extends IBaseEvent>)OnPlayerUnsummonAgathion.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_PEACE_ZONE_ENTER((Class<? extends IBaseEvent>)OnPlayerPeaceZoneEnter.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_PEACE_ZONE_EXIT((Class<? extends IBaseEvent>)OnPlayerPeaceZoneExit.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_PLAYER_CHARGE_SHOTS((Class<? extends IBaseEvent>)OnPlayeableChargeShots.class, (Class<?>[])new Class[] { Boolean.class }), 
    ON_TRAP_ACTION((Class<? extends IBaseEvent>)OnTrapAction.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_DAY_NIGHT_CHANGE((Class<? extends IBaseEvent>)OnDayNightChange.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_INSTANCE_CREATED((Class<? extends IBaseEvent>)OnInstanceCreated.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_INSTANCE_DESTROY((Class<? extends IBaseEvent>)OnInstanceDestroy.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_INSTANCE_ENTER((Class<? extends IBaseEvent>)OnInstanceEnter.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_INSTANCE_LEAVE((Class<? extends IBaseEvent>)OnInstanceLeave.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_INSTANCE_STATUS_CHANGE((Class<? extends IBaseEvent>)OnInstanceStatusChange.class, (Class<?>[])new Class[] { Void.TYPE }), 
    ON_ELEMENTAL_SPIRIT_UPGRADE((Class<? extends IBaseEvent>)OnElementalSpiritUpgrade.class, (Class<?>[])new Class[] { Void.class }), 
    ON_ELEMENTAL_SPIRIT_LEARN((Class<? extends IBaseEvent>)OnElementalSpiritLearn.class, (Class<?>[])new Class[] { Void.class });
    
    private final Class<? extends IBaseEvent> _eventClass;
    private final Class<?>[] _returnClass;
    
    private EventType(final Class<? extends IBaseEvent> eventClass, final Class<?>[] returnClasss) {
        this._eventClass = eventClass;
        this._returnClass = returnClasss;
    }
    
    public Class<? extends IBaseEvent> getEventClass() {
        return this._eventClass;
    }
    
    public Class<?>[] getReturnClasses() {
        return this._returnClass;
    }
    
    public boolean isEventClass(final Class<?> clazz) {
        return this._eventClass == clazz;
    }
    
    public boolean isReturnClass(final Class<?> clazz) {
        return CommonUtil.contains((Object[])this._returnClass, (Object)clazz);
    }
}
