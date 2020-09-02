// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network;

import org.l2j.gameserver.network.clientpackets.teleport.ExRequestTeleportFavoritesAddDel;
import org.l2j.gameserver.network.clientpackets.teleport.ExRequestTeleportFavoritesUIToggle;
import org.l2j.gameserver.network.clientpackets.teleport.ExRequestTeleportFavoriteList;
import org.l2j.gameserver.network.clientpackets.olympiad.ExRequestOlympiadHeroes;
import org.l2j.gameserver.network.clientpackets.olympiad.ExRequestOlympiadRanking;
import org.l2j.gameserver.network.clientpackets.olympiad.ExRequestOlympiadMyRank;
import org.l2j.gameserver.network.clientpackets.stats.ExResetStatusBonus;
import org.l2j.gameserver.network.clientpackets.stats.ExSetStatusBonus;
import org.l2j.gameserver.network.clientpackets.pvpbook.ExTeleportToKiller;
import org.l2j.gameserver.network.clientpackets.pvpbook.ExRequestKillerLocation;
import org.l2j.gameserver.network.clientpackets.pvpbook.ExRequestPvpBookList;
import org.l2j.gameserver.network.clientpackets.rank.ExRankingCharRankers;
import org.l2j.gameserver.network.clientpackets.rank.ExRequestRankingCharHistory;
import org.l2j.gameserver.network.clientpackets.rank.ExRankCharInfo;
import org.l2j.gameserver.network.clientpackets.sessionzones.ExTimedHuntingZoneEnter;
import org.l2j.gameserver.network.clientpackets.sessionzones.ExTimedHuntingZoneList;
import org.l2j.gameserver.network.clientpackets.autoplay.ExAutoPlaySetting;
import org.l2j.gameserver.network.clientpackets.autoplay.ExRequestActivateAutoShortcut;
import org.l2j.gameserver.network.clientpackets.costume.ExRequestCostumeLock;
import org.l2j.gameserver.network.clientpackets.costume.ExRequestCostumeExtract;
import org.l2j.gameserver.network.clientpackets.costume.ExRequestCostumeEvolution;
import org.l2j.gameserver.network.clientpackets.costume.ExRequestCostumeCollectSkillActive;
import org.l2j.gameserver.network.clientpackets.costume.ExRequestCostumeList;
import org.l2j.gameserver.network.clientpackets.costume.ExRequestCostumeUseItem;
import org.l2j.gameserver.network.clientpackets.teleport.ExRequestTeleport;
import org.l2j.gameserver.network.clientpackets.ExRequestClassChange;
import org.l2j.gameserver.network.clientpackets.ExOpenHtml;
import org.l2j.gameserver.network.clientpackets.RequestPurchaseLimitShopItemBuy;
import org.l2j.gameserver.network.clientpackets.l2coin.RequestPurchaseLimitShopItemList;
import org.l2j.gameserver.network.clientpackets.upgrade.ExUpgradeSystemNormalRequest;
import org.l2j.gameserver.network.clientpackets.ExRequestBlockListForAD;
import org.l2j.gameserver.network.clientpackets.elementalspirits.ExElementalSpiritChangeType;
import org.l2j.gameserver.network.clientpackets.elementalspirits.ExElementalSpiritAbsorb;
import org.l2j.gameserver.network.clientpackets.elementalspirits.ExElementalSpiritAbsorbInfo;
import org.l2j.gameserver.network.clientpackets.elementalspirits.ExElementalInitTalent;
import org.l2j.gameserver.network.clientpackets.elementalspirits.ExElementalSpiritSetTalent;
import org.l2j.gameserver.network.clientpackets.elementalspirits.ExElementalSpiritEvolution;
import org.l2j.gameserver.network.clientpackets.elementalspirits.ExElementalSpiritEvolutionInfo;
import org.l2j.gameserver.network.clientpackets.elementalspirits.ExElementalSpiritExtract;
import org.l2j.gameserver.network.clientpackets.elementalspirits.ExElementalSpiritExtractInfo;
import org.l2j.gameserver.network.clientpackets.elementalspirits.ExElementalSpiritInfo;
import org.l2j.gameserver.network.clientpackets.upgrade.ExUpgradeSystemRequest;
import org.l2j.gameserver.network.clientpackets.raidbossinfo.RequestRaidServerInfo;
import org.l2j.gameserver.network.clientpackets.raidbossinfo.RequestRaidBossSpawnInfo;
import org.l2j.gameserver.network.clientpackets.ensoul.RequestTryEnSoulExtraction;
import org.l2j.gameserver.network.clientpackets.pledgebonus.RequestPledgeBonusReward;
import org.l2j.gameserver.network.clientpackets.pledgebonus.RequestPledgeBonusRewardList;
import org.l2j.gameserver.network.clientpackets.pledgebonus.RequestPledgeBonusOpen;
import org.l2j.gameserver.network.clientpackets.mission.RequestOneDayRewardReceive;
import org.l2j.gameserver.network.clientpackets.mission.RequestTodoList;
import org.l2j.gameserver.network.clientpackets.RequestPledgeSignInForOpenJoiningMethod;
import org.l2j.gameserver.network.clientpackets.captcha.RequestRefreshCaptcha;
import org.l2j.gameserver.network.clientpackets.captcha.RequestCaptchaAnswer;
import org.l2j.gameserver.network.clientpackets.vip.ExRequestVipInfo;
import org.l2j.gameserver.network.clientpackets.vip.RequestVipLuckGameInfo;
import org.l2j.gameserver.network.clientpackets.vip.RequestVipProductList;
import org.l2j.gameserver.network.clientpackets.ensoul.RequestItemEnsoul;
import org.l2j.gameserver.network.clientpackets.attendance.RequestVipAttendanceCheck;
import org.l2j.gameserver.network.clientpackets.attendance.RequestVipAttendanceItemList;
import org.l2j.gameserver.network.clientpackets.ExRequestAutoFish;
import org.l2j.gameserver.network.clientpackets.ExSendSelectedQuestZoneID;
import org.l2j.gameserver.network.clientpackets.RequestTargetActionMenu;
import org.l2j.gameserver.network.clientpackets.compound.RequestNewEnchantRetryToPutItems;
import org.l2j.gameserver.network.clientpackets.compound.RequestNewEnchantTry;
import org.l2j.gameserver.network.clientpackets.compound.RequestNewEnchantClose;
import org.l2j.gameserver.network.clientpackets.compound.RequestNewEnchantRemoveTwo;
import org.l2j.gameserver.network.clientpackets.compound.RequestNewEnchantPushTwo;
import org.l2j.gameserver.network.clientpackets.compound.RequestNewEnchantRemoveOne;
import org.l2j.gameserver.network.clientpackets.compound.RequestNewEnchantPushOne;
import org.l2j.gameserver.network.clientpackets.training.NotifyTrainingRoomEnd;
import org.l2j.gameserver.network.clientpackets.luckygame.RequestLuckyGamePlay;
import org.l2j.gameserver.network.clientpackets.luckygame.RequestLuckyGameStartInfo;
import org.l2j.gameserver.network.clientpackets.RequestStopMove;
import org.l2j.gameserver.network.clientpackets.adenadistribution.RequestDivideAdena;
import org.l2j.gameserver.network.clientpackets.adenadistribution.RequestDivideAdenaCancel;
import org.l2j.gameserver.network.clientpackets.adenadistribution.RequestDivideAdenaStart;
import org.l2j.gameserver.network.clientpackets.RequestExRemoveEnchantSupportItem;
import org.l2j.gameserver.network.clientpackets.RequestExAddEnchantScrollItem;
import org.l2j.gameserver.network.clientpackets.NotifyExitBeautyShop;
import org.l2j.gameserver.network.clientpackets.RequestPledgeRecruitApplyInfo;
import org.l2j.gameserver.network.clientpackets.RequestPledgeDraftListApply;
import org.l2j.gameserver.network.clientpackets.RequestPledgeDraftListSearch;
import org.l2j.gameserver.network.clientpackets.RequestPledgeWaitingUserAccept;
import org.l2j.gameserver.network.clientpackets.RequestPledgeWaitingUser;
import org.l2j.gameserver.network.clientpackets.RequestPledgeWaitingList;
import org.l2j.gameserver.network.clientpackets.RequestPledgeWaitingApplied;
import org.l2j.gameserver.network.clientpackets.RequestPledgeWaitingApply;
import org.l2j.gameserver.network.clientpackets.RequestPledgeRecruitBoardDetail;
import org.l2j.gameserver.network.clientpackets.RequestPledgeRecruitBoardAccess;
import org.l2j.gameserver.network.clientpackets.RequestPledgeRecruitBoardSearch;
import org.l2j.gameserver.network.clientpackets.RequestPledgeRecruitInfo;
import org.l2j.gameserver.network.clientpackets.RequestShowResetShopList;
import org.l2j.gameserver.network.clientpackets.RequestRegistBeauty;
import org.l2j.gameserver.network.clientpackets.RequestShowBeautyList;
import org.l2j.gameserver.network.clientpackets.ceremonyofchaos.RequestCuriousHouseHtml;
import org.l2j.gameserver.network.clientpackets.ceremonyofchaos.RequestCancelCuriousHouse;
import org.l2j.gameserver.network.clientpackets.ceremonyofchaos.RequestJoinCuriousHouse;
import org.l2j.gameserver.network.clientpackets.RequestInzoneWaitingTime;
import org.l2j.gameserver.network.clientpackets.RequestClanAskJoinByName;
import org.l2j.gameserver.network.clientpackets.mentoring.RequestMenteeWaitingList;
import org.l2j.gameserver.network.clientpackets.mentoring.RequestMenteeAdd;
import org.l2j.gameserver.network.clientpackets.mentoring.RequestMentorList;
import org.l2j.gameserver.network.clientpackets.mentoring.RequestMentorCancel;
import org.l2j.gameserver.network.clientpackets.mentoring.ConfirmMenteeAdd;
import org.l2j.gameserver.network.clientpackets.primeshop.RequestBRPresentBuyProduct;
import org.l2j.gameserver.network.clientpackets.attributechange.RequestChangeAttributeCancel;
import org.l2j.gameserver.network.clientpackets.attributechange.SendChangeAttributeTargetItem;
import org.l2j.gameserver.network.clientpackets.attributechange.RequestChangeAttributeItem;
import org.l2j.gameserver.network.clientpackets.RequestHardWareInfo;
import org.l2j.gameserver.network.clientpackets.RequestCharacterNameCreatable;
import org.l2j.gameserver.network.clientpackets.RequestEx2ndPasswordReq;
import org.l2j.gameserver.network.clientpackets.RequestEx2ndPasswordVerify;
import org.l2j.gameserver.network.clientpackets.RequestEx2ndPasswordCheck;
import org.l2j.gameserver.network.clientpackets.RequestChangeToAwakenedClass;
import org.l2j.gameserver.network.clientpackets.commission.RequestCommissionRegisteredItem;
import org.l2j.gameserver.network.clientpackets.commission.RequestCommissionBuyItem;
import org.l2j.gameserver.network.clientpackets.commission.RequestCommissionBuyInfo;
import org.l2j.gameserver.network.clientpackets.commission.RequestCommissionList;
import org.l2j.gameserver.network.clientpackets.commission.RequestCommissionDelete;
import org.l2j.gameserver.network.clientpackets.commission.RequestCommissionCancel;
import org.l2j.gameserver.network.clientpackets.commission.RequestCommissionRegister;
import org.l2j.gameserver.network.clientpackets.commission.RequestCommissionInfo;
import org.l2j.gameserver.network.clientpackets.commission.RequestCommissionRegistrableItemList;
import org.l2j.gameserver.network.clientpackets.friend.RequestFriendDetailInfo;
import org.l2j.gameserver.network.clientpackets.RequestExEscapeScene;
import org.l2j.gameserver.network.clientpackets.crystalization.RequestCrystallizeItemCancel;
import org.l2j.gameserver.network.clientpackets.crystalization.RequestCrystallizeEstimate;
import org.l2j.gameserver.network.clientpackets.primeshop.RequestBRRecentProductList;
import org.l2j.gameserver.network.clientpackets.primeshop.RequestBRBuyProduct;
import org.l2j.gameserver.network.clientpackets.primeshop.RequestBRProductInfo;
import org.l2j.gameserver.network.clientpackets.primeshop.RequestBRProductList;
import org.l2j.gameserver.network.clientpackets.primeshop.RequestBRGamePoint;
import org.l2j.gameserver.network.clientpackets.RequestExOlympiadMatchListRefresh;
import org.l2j.gameserver.network.clientpackets.RequestExFriendListExtended;
import org.l2j.gameserver.network.clientpackets.RequestExShowContactList;
import org.l2j.gameserver.network.clientpackets.RequestExDeleteContactFromContactList;
import org.l2j.gameserver.network.clientpackets.RequestExAddContactToContactList;
import org.l2j.gameserver.network.clientpackets.shuttle.CannotMoveAnymoreInShuttle;
import org.l2j.gameserver.network.clientpackets.shuttle.MoveToLocationInShuttle;
import org.l2j.gameserver.network.clientpackets.shuttle.RequestShuttleGetOff;
import org.l2j.gameserver.network.clientpackets.shuttle.RequestShuttleGetOn;
import org.l2j.gameserver.network.clientpackets.RequestVoteNew;
import org.l2j.gameserver.network.clientpackets.RequestAddExpandQuestAlarm;
import org.l2j.gameserver.network.clientpackets.BrEventRankerList;
import org.l2j.gameserver.network.clientpackets.AnswerCoupleAction;
import org.l2j.gameserver.network.clientpackets.AnswerPartyLootModification;
import org.l2j.gameserver.network.clientpackets.RequestPartyLootModification;
import org.l2j.gameserver.network.clientpackets.RequestBuySellUIClose;
import org.l2j.gameserver.network.clientpackets.RequestRefundItem;
import org.l2j.gameserver.network.clientpackets.RequestCancelPostAttachment;
import org.l2j.gameserver.network.clientpackets.RequestSentPost;
import org.l2j.gameserver.network.clientpackets.RequestDeleteSentPost;
import org.l2j.gameserver.network.clientpackets.RequestSentPostList;
import org.l2j.gameserver.network.clientpackets.RequestRejectPostAttachment;
import org.l2j.gameserver.network.clientpackets.RequestPostAttachment;
import org.l2j.gameserver.network.clientpackets.RequestReceivedPost;
import org.l2j.gameserver.network.clientpackets.RequestDeleteReceivedPost;
import org.l2j.gameserver.network.clientpackets.RequestReceivedPostList;
import org.l2j.gameserver.network.clientpackets.RequestSendPost;
import org.l2j.gameserver.network.clientpackets.RequestPostItemList;
import org.l2j.gameserver.network.clientpackets.RequestExMpccPartymasterList;
import org.l2j.gameserver.network.clientpackets.RequestSeedPhase;
import org.l2j.gameserver.network.clientpackets.RequestExWithdrawMpccRoom;
import org.l2j.gameserver.network.clientpackets.RequestExDismissMpccRoom;
import org.l2j.gameserver.network.clientpackets.RequestExOustFromMpccRoom;
import org.l2j.gameserver.network.clientpackets.RequestExJoinMpccRoom;
import org.l2j.gameserver.network.clientpackets.RequestExManageMpccRoom;
import org.l2j.gameserver.network.clientpackets.RequestExListMpccWaiting;
import org.l2j.gameserver.network.clientpackets.EndScenePlayer;
import org.l2j.gameserver.network.clientpackets.RequestStopShowKrateisCubeRank;
import org.l2j.gameserver.network.clientpackets.RequestStartShowKrateisCubeRank;
import org.l2j.gameserver.network.clientpackets.RequestWithDrawPremiumItem;
import org.l2j.gameserver.network.clientpackets.RequestResetNickname;
import org.l2j.gameserver.network.clientpackets.RequestChangeNicknameColor;
import org.l2j.gameserver.network.clientpackets.RequestExCancelEnchantItem;
import org.l2j.gameserver.network.clientpackets.RequestExTryToPutEnchantSupportItem;
import org.l2j.gameserver.network.clientpackets.RequestExTryToPutEnchantTargetItem;
import org.l2j.gameserver.network.clientpackets.RequestDispel;
import org.l2j.gameserver.network.clientpackets.SetPrivateStoreWholeMsg;
import org.l2j.gameserver.network.clientpackets.RequestPVPMatchRecord;
import org.l2j.gameserver.network.clientpackets.RequestFortressMapInfo;
import org.l2j.gameserver.network.clientpackets.RequestExEnchantSkillInfoDetail;
import org.l2j.gameserver.network.clientpackets.RequestDuelSurrender;
import org.l2j.gameserver.network.clientpackets.RequestExMagicSkillUseGround;
import org.l2j.gameserver.network.clientpackets.RequestRefineCancel;
import org.l2j.gameserver.network.clientpackets.RequestConfirmCancelItem;
import org.l2j.gameserver.network.clientpackets.RequestRefine;
import org.l2j.gameserver.network.clientpackets.RequestGetBossRecord;
import org.l2j.gameserver.network.clientpackets.RequestFortressSiegeInfo;
import org.l2j.gameserver.network.clientpackets.RequestAllAgitInfo;
import org.l2j.gameserver.network.clientpackets.RequestAllFortressInfo;
import org.l2j.gameserver.network.clientpackets.RequestAllCastleInfo;
import org.l2j.gameserver.network.clientpackets.RequestExChangeName;
import org.l2j.gameserver.network.clientpackets.RequestInfoItemAuction;
import org.l2j.gameserver.network.clientpackets.RequestBidItemAuction;
import org.l2j.gameserver.network.clientpackets.RequestGotoLobby;
import org.l2j.gameserver.network.clientpackets.RequestExEnchantItemAttribute;
import org.l2j.gameserver.network.clientpackets.RequestListPartyMatchingWaitingRoom;
import org.l2j.gameserver.network.clientpackets.AnswerJoinPartyRoom;
import org.l2j.gameserver.network.clientpackets.RequestAskJoinPartyRoom;
import org.l2j.gameserver.network.clientpackets.olympiad.RequestOlympiadMatchList;
import org.l2j.gameserver.network.clientpackets.RequestExMPCCShowPartyMembersInfo;
import org.l2j.gameserver.network.clientpackets.RequestPledgeReorganizeMember;
import org.l2j.gameserver.network.clientpackets.olympiad.RequestOlympiadObserverEnd;
import org.l2j.gameserver.network.clientpackets.RequestConfirmGemStone;
import org.l2j.gameserver.network.clientpackets.RequestConfirmRefinerItem;
import org.l2j.gameserver.network.clientpackets.RequestConfirmTargetItem;
import org.l2j.gameserver.network.clientpackets.RequestExitPartyMatchingWaitingRoom;
import org.l2j.gameserver.network.clientpackets.RequestSaveInventoryOrder;
import org.l2j.gameserver.network.clientpackets.RequestExRemoveItemAttribute;
import org.l2j.gameserver.network.clientpackets.RequestSaveKeyMapping;
import org.l2j.gameserver.network.clientpackets.RequestKeyMapping;
import org.l2j.gameserver.network.clientpackets.RequestExRqItemLink;
import org.l2j.gameserver.network.clientpackets.RequestDuelAnswerStart;
import org.l2j.gameserver.network.clientpackets.RequestDuelStart;
import org.l2j.gameserver.network.clientpackets.RequestPCCafeCouponUse;
import org.l2j.gameserver.network.clientpackets.RequestExFishRanking;
import org.l2j.gameserver.network.clientpackets.RequestPledgeWarList;
import org.l2j.gameserver.network.clientpackets.RequestPledgeMemberInfo;
import org.l2j.gameserver.network.clientpackets.RequestPledgeSetMemberPowerGrade;
import org.l2j.gameserver.network.clientpackets.RequestPledgeMemberPowerInfo;
import org.l2j.gameserver.network.clientpackets.RequestPledgePowerGradeList;
import org.l2j.gameserver.network.clientpackets.RequestPledgeSetAcademyMaster;
import org.l2j.gameserver.network.clientpackets.RequestExSetPledgeCrestLarge;
import org.l2j.gameserver.network.clientpackets.RequestExPledgeCrestLarge;
import org.l2j.gameserver.network.clientpackets.RequestExEnchantSkill;
import org.l2j.gameserver.network.clientpackets.RequestExEnchantSkillInfo;
import org.l2j.gameserver.network.clientpackets.RequestAutoSoulShot;
import org.l2j.gameserver.network.clientpackets.RequestChangePartyLeader;
import org.l2j.gameserver.network.clientpackets.RequestWithdrawPartyRoom;
import org.l2j.gameserver.network.clientpackets.RequestDismissPartyRoom;
import org.l2j.gameserver.network.clientpackets.RequestOustFromPartyRoom;
import org.l2j.gameserver.network.clientpackets.RequestExOustFromMPCC;
import org.l2j.gameserver.network.clientpackets.RequestExAcceptJoinMPCC;
import org.l2j.gameserver.network.clientpackets.RequestExAskJoinMPCC;
import org.l2j.gameserver.network.clientpackets.RequestWriteHeroWords;
import org.l2j.gameserver.network.clientpackets.RequestSetCrop;
import org.l2j.gameserver.network.clientpackets.RequestSetSeed;
import org.l2j.gameserver.network.clientpackets.RequestProcureCropList;
import org.l2j.gameserver.network.clientpackets.RequestManorList;
import org.l2j.gameserver.network.clientpackets.RequestChangeBookMarkSlot;
import org.l2j.gameserver.network.clientpackets.RequestTeleportBookMark;
import org.l2j.gameserver.network.clientpackets.RequestDeleteBookMarkSlot;
import org.l2j.gameserver.network.clientpackets.RequestModifyBookMarkSlot;
import org.l2j.gameserver.network.clientpackets.RequestSaveBookMarkSlot;
import org.l2j.gameserver.network.clientpackets.RequestBookMarkSlotInfo;
import io.github.joealisson.mmocore.ReadableBuffer;
import java.util.Objects;
import java.util.EnumSet;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import java.util.function.Supplier;

public enum ExIncomingPackets implements PacketFactory
{
    EX_DUMMY((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_REQ_MANOR_LIST((Supplier<ClientPacket>)RequestManorList::new, ConnectionState.JOINING_GAME_STATES), 
    EX_PROCURE_CROP_LIST((Supplier<ClientPacket>)RequestProcureCropList::new, ConnectionState.IN_GAME_STATES), 
    EX_SET_SEED((Supplier<ClientPacket>)RequestSetSeed::new, ConnectionState.IN_GAME_STATES), 
    EX_SET_CROP((Supplier<ClientPacket>)RequestSetCrop::new, ConnectionState.IN_GAME_STATES), 
    EX_WRITE_HERO_WORDS((Supplier<ClientPacket>)RequestWriteHeroWords::new, ConnectionState.IN_GAME_STATES), 
    EX_ASK_JOIN_MPCC((Supplier<ClientPacket>)RequestExAskJoinMPCC::new, ConnectionState.IN_GAME_STATES), 
    EX_ACCEPT_JOIN_MPCC((Supplier<ClientPacket>)RequestExAcceptJoinMPCC::new, ConnectionState.IN_GAME_STATES), 
    EX_OUST_FROM_MPCC((Supplier<ClientPacket>)RequestExOustFromMPCC::new, ConnectionState.IN_GAME_STATES), 
    EX_OUST_FROM_PARTY_ROOM((Supplier<ClientPacket>)RequestOustFromPartyRoom::new, ConnectionState.IN_GAME_STATES), 
    EX_DISMISS_PARTY_ROOM((Supplier<ClientPacket>)RequestDismissPartyRoom::new, ConnectionState.IN_GAME_STATES), 
    EX_WITHDRAW_PARTY_ROOM((Supplier<ClientPacket>)RequestWithdrawPartyRoom::new, ConnectionState.IN_GAME_STATES), 
    EX_HAND_OVER_PARTY_MASTER((Supplier<ClientPacket>)RequestChangePartyLeader::new, ConnectionState.IN_GAME_STATES), 
    EX_AUTO_SOULSHOT((Supplier<ClientPacket>)RequestAutoSoulShot::new, ConnectionState.IN_GAME_STATES), 
    EX_ENCHANT_SKILL_INFO((Supplier<ClientPacket>)RequestExEnchantSkillInfo::new, ConnectionState.IN_GAME_STATES), 
    EX_REQ_ENCHANT_SKILL((Supplier<ClientPacket>)RequestExEnchantSkill::new, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_EMBLEM((Supplier<ClientPacket>)RequestExPledgeCrestLarge::new, ConnectionState.IN_GAME_STATES), 
    EX_SET_PLEDGE_EMBLEM((Supplier<ClientPacket>)RequestExSetPledgeCrestLarge::new, ConnectionState.IN_GAME_STATES), 
    EX_SET_ACADEMY_MASTER((Supplier<ClientPacket>)RequestPledgeSetAcademyMaster::new, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_POWER_GRADE_LIST((Supplier<ClientPacket>)RequestPledgePowerGradeList::new, ConnectionState.IN_GAME_STATES), 
    EX_VIEW_PLEDGE_POWER((Supplier<ClientPacket>)RequestPledgeMemberPowerInfo::new, ConnectionState.IN_GAME_STATES), 
    EX_SET_PLEDGE_POWER_GRADE((Supplier<ClientPacket>)RequestPledgeSetMemberPowerGrade::new, ConnectionState.IN_GAME_STATES), 
    EX_VIEW_PLEDGE_MEMBER_INFO((Supplier<ClientPacket>)RequestPledgeMemberInfo::new, ConnectionState.IN_GAME_STATES), 
    EX_VIEW_PLEDGE_WARLIST((Supplier<ClientPacket>)RequestPledgeWarList::new, ConnectionState.IN_GAME_STATES), 
    EX_FISH_RANKING((Supplier<ClientPacket>)RequestExFishRanking::new, ConnectionState.IN_GAME_STATES), 
    EX_PCCAFE_COUPON_USE((Supplier<ClientPacket>)RequestPCCafeCouponUse::new, ConnectionState.IN_GAME_STATES), 
    EX_ORC_MOVE((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_DUEL_ASK_START((Supplier<ClientPacket>)RequestDuelStart::new, ConnectionState.IN_GAME_STATES), 
    EX_DUEL_ACCEPT_START((Supplier<ClientPacket>)RequestDuelAnswerStart::new, ConnectionState.IN_GAME_STATES), 
    EX_SET_TUTORIAL((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_RQ_ITEMLINK((Supplier<ClientPacket>)RequestExRqItemLink::new, ConnectionState.IN_GAME_STATES), 
    EX_CAN_NOT_MOVE_ANYMORE_IN_AIRSHIP((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_MOVE_TO_LOCATION_IN_AIRSHIP((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_LOAD_UI_SETTING((Supplier<ClientPacket>)RequestKeyMapping::new, ConnectionState.JOINING_GAME_AND_IN_GAME), 
    EX_SAVE_UI_SETTING((Supplier<ClientPacket>)RequestSaveKeyMapping::new, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_BASE_ATTRIBUTE_CANCEL((Supplier<ClientPacket>)RequestExRemoveItemAttribute::new, ConnectionState.IN_GAME_STATES), 
    EX_CHANGE_INVENTORY_SLOT((Supplier<ClientPacket>)RequestSaveInventoryOrder::new, ConnectionState.IN_GAME_STATES), 
    EX_EXIT_PARTY_MATCHING_WAITING_ROOM((Supplier<ClientPacket>)RequestExitPartyMatchingWaitingRoom::new, ConnectionState.IN_GAME_STATES), 
    EX_TRY_TO_PUT_ITEM_FOR_VARIATION_MAKE((Supplier<ClientPacket>)RequestConfirmTargetItem::new, ConnectionState.IN_GAME_STATES), 
    EX_TRY_TO_PUT_INTENSIVE_FOR_VARIATION_MAKE((Supplier<ClientPacket>)RequestConfirmRefinerItem::new, ConnectionState.IN_GAME_STATES), 
    EX_TRY_TO_PUT_COMMISSION_FOR_VARIATION_MAKE((Supplier<ClientPacket>)RequestConfirmGemStone::new, ConnectionState.IN_GAME_STATES), 
    EX_OLYMPIAD_OBSERVER_END((Supplier<ClientPacket>)RequestOlympiadObserverEnd::new, ConnectionState.IN_GAME_STATES), 
    EX_CURSED_WEAPON_LIST((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_EXISTING_CURSED_WEAPON_LOCATION((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_REORGANIZE_PLEDGE_MEMBER((Supplier<ClientPacket>)RequestPledgeReorganizeMember::new, ConnectionState.IN_GAME_STATES), 
    EX_MPCC_SHOW_PARTY_MEMBERS_INFO((Supplier<ClientPacket>)RequestExMPCCShowPartyMembersInfo::new, ConnectionState.IN_GAME_STATES), 
    EX_OLYMPIAD_MATCH_LIST((Supplier<ClientPacket>)RequestOlympiadMatchList::new, ConnectionState.IN_GAME_STATES), 
    EX_ASK_JOIN_PARTY_ROOM((Supplier<ClientPacket>)RequestAskJoinPartyRoom::new, ConnectionState.IN_GAME_STATES), 
    EX_ANSWER_JOIN_PARTY_ROOM((Supplier<ClientPacket>)AnswerJoinPartyRoom::new, ConnectionState.IN_GAME_STATES), 
    EX_LIST_PARTY_MATCHING_WAITING_ROOM((Supplier<ClientPacket>)RequestListPartyMatchingWaitingRoom::new, ConnectionState.IN_GAME_STATES), 
    EX_CHOOSE_INVENTORY_ATTRIBUTE_ITEM((Supplier<ClientPacket>)RequestExEnchantItemAttribute::new, ConnectionState.IN_GAME_STATES), 
    EX_CHARACTER_BACK((Supplier<ClientPacket>)RequestGotoLobby::new, ConnectionState.AUTHENTICATED_STATES), 
    EX_CANNOT_AIRSHIP_MOVE_ANYMORE((Supplier<ClientPacket>)null, ConnectionState.AUTHENTICATED_STATES), 
    EX_MOVE_TO_LOCATION_AIRSHIP((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_ITEM_AUCTION_BID((Supplier<ClientPacket>)RequestBidItemAuction::new, ConnectionState.IN_GAME_STATES), 
    EX_ITEM_AUCTION_INFO((Supplier<ClientPacket>)RequestInfoItemAuction::new, ConnectionState.IN_GAME_STATES), 
    EX_CHANGE_NAME((Supplier<ClientPacket>)RequestExChangeName::new, ConnectionState.IN_GAME_STATES), 
    EX_SHOW_CASTLE_INFO((Supplier<ClientPacket>)RequestAllCastleInfo::new, ConnectionState.IN_GAME_STATES), 
    EX_SHOW_FORTRESS_INFO((Supplier<ClientPacket>)RequestAllFortressInfo::new, ConnectionState.IN_GAME_STATES), 
    EX_SHOW_AGIT_INFO((Supplier<ClientPacket>)RequestAllAgitInfo::new, ConnectionState.IN_GAME_STATES), 
    EX_SHOW_FORTRESS_SIEGE_INFO((Supplier<ClientPacket>)RequestFortressSiegeInfo::new, ConnectionState.IN_GAME_STATES), 
    EX_GET_BOSS_RECORD((Supplier<ClientPacket>)RequestGetBossRecord::new, ConnectionState.IN_GAME_STATES), 
    EX_TRY_TO_MAKE_VARIATION((Supplier<ClientPacket>)RequestRefine::new, ConnectionState.IN_GAME_STATES), 
    EX_TRY_TO_PUT_ITEM_FOR_VARIATION_CANCEL((Supplier<ClientPacket>)RequestConfirmCancelItem::new, ConnectionState.IN_GAME_STATES), 
    EX_CLICK_VARIATION_CANCEL_BUTTON((Supplier<ClientPacket>)RequestRefineCancel::new, ConnectionState.IN_GAME_STATES), 
    EX_MAGIC_SKILL_USE_GROUND((Supplier<ClientPacket>)RequestExMagicSkillUseGround::new, ConnectionState.IN_GAME_STATES), 
    EX_DUEL_SURRENDER((Supplier<ClientPacket>)RequestDuelSurrender::new, ConnectionState.IN_GAME_STATES), 
    EX_ENCHANT_SKILL_INFO_DETAIL((Supplier<ClientPacket>)RequestExEnchantSkillInfoDetail::new, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_ANTI_FREE_SERVER((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_SHOW_FORTRESS_MAP_INFO((Supplier<ClientPacket>)RequestFortressMapInfo::new, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_PVPMATCH_RECORD((Supplier<ClientPacket>)RequestPVPMatchRecord::new, ConnectionState.IN_GAME_STATES), 
    EX_PRIVATE_STORE_WHOLE_SET_MSG((Supplier<ClientPacket>)SetPrivateStoreWholeMsg::new, ConnectionState.IN_GAME_STATES), 
    EX_DISPEL((Supplier<ClientPacket>)RequestDispel::new, ConnectionState.IN_GAME_STATES), 
    EX_TRY_TO_PUT_ENCHANT_TARGET_ITEM((Supplier<ClientPacket>)RequestExTryToPutEnchantTargetItem::new, ConnectionState.IN_GAME_STATES), 
    EX_TRY_TO_PUT_ENCHANT_SUPPORT_ITEM((Supplier<ClientPacket>)RequestExTryToPutEnchantSupportItem::new, ConnectionState.IN_GAME_STATES), 
    EX_CANCEL_ENCHANT_ITEM((Supplier<ClientPacket>)RequestExCancelEnchantItem::new, ConnectionState.IN_GAME_STATES), 
    EX_CHANGE_NICKNAME_COLOR((Supplier<ClientPacket>)RequestChangeNicknameColor::new, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_RESET_NICKNAME((Supplier<ClientPacket>)RequestResetNickname::new, ConnectionState.IN_GAME_STATES), 
    EX_USER_BOOKMARK((Supplier<ClientPacket>)null, true, ConnectionState.IN_GAME_STATES), 
    EX_WITHDRAW_PREMIUM_ITEM((Supplier<ClientPacket>)RequestWithDrawPremiumItem::new, ConnectionState.IN_GAME_STATES), 
    EX_JUMP((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_START_REQUEST_PVPMATCH_CC_RANK((Supplier<ClientPacket>)RequestStartShowKrateisCubeRank::new, ConnectionState.IN_GAME_STATES), 
    EX_STOP_REQUEST_PVPMATCH_CC_RANK((Supplier<ClientPacket>)RequestStopShowKrateisCubeRank::new, ConnectionState.IN_GAME_STATES), 
    EX_NOTIFY_START_MINIGAME((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_REGISTER_DOMINION((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_DOMINION_INFO((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_CLEFT_ENTER((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_BLOCK_UPSET_ENTER((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_END_SCENE_PLAYER((Supplier<ClientPacket>)EndScenePlayer::new, ConnectionState.IN_GAME_STATES), 
    EX_BLOCK_UPSET_VOTE((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_LIST_MPCC_WAITING((Supplier<ClientPacket>)RequestExListMpccWaiting::new, ConnectionState.IN_GAME_STATES), 
    EX_MANAGE_MPCC_ROOM((Supplier<ClientPacket>)RequestExManageMpccRoom::new, ConnectionState.IN_GAME_STATES), 
    EX_JOIN_MPCC_ROOM((Supplier<ClientPacket>)RequestExJoinMpccRoom::new, ConnectionState.IN_GAME_STATES), 
    EX_OUST_FROM_MPCC_ROOM((Supplier<ClientPacket>)RequestExOustFromMpccRoom::new, ConnectionState.IN_GAME_STATES), 
    EX_DISMISS_MPCC_ROOM((Supplier<ClientPacket>)RequestExDismissMpccRoom::new, ConnectionState.IN_GAME_STATES), 
    EX_WITHDRAW_MPCC_ROOM((Supplier<ClientPacket>)RequestExWithdrawMpccRoom::new, ConnectionState.IN_GAME_STATES), 
    EX_SEED_PHASE((Supplier<ClientPacket>)RequestSeedPhase::new, ConnectionState.IN_GAME_STATES), 
    EX_MPCC_PARTYMASTER_LIST((Supplier<ClientPacket>)RequestExMpccPartymasterList::new, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_POST_ITEM_LIST((Supplier<ClientPacket>)RequestPostItemList::new, ConnectionState.IN_GAME_STATES), 
    EX_SEND_POST((Supplier<ClientPacket>)RequestSendPost::new, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_RECEIVED_POST_LIST((Supplier<ClientPacket>)RequestReceivedPostList::new, ConnectionState.IN_GAME_STATES), 
    EX_DELETE_RECEIVED_POST((Supplier<ClientPacket>)RequestDeleteReceivedPost::new, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_RECEIVED_POST((Supplier<ClientPacket>)RequestReceivedPost::new, ConnectionState.IN_GAME_STATES), 
    EX_RECEIVE_POST((Supplier<ClientPacket>)RequestPostAttachment::new, ConnectionState.IN_GAME_STATES), 
    EX_REJECT_POST((Supplier<ClientPacket>)RequestRejectPostAttachment::new, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_SENT_POST_LIST((Supplier<ClientPacket>)RequestSentPostList::new, ConnectionState.IN_GAME_STATES), 
    EX_DELETE_SENT_POST((Supplier<ClientPacket>)RequestDeleteSentPost::new, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_SENT_POST((Supplier<ClientPacket>)RequestSentPost::new, ConnectionState.IN_GAME_STATES), 
    EX_CANCEL_SEND_POST((Supplier<ClientPacket>)RequestCancelPostAttachment::new, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_SHOW_PETITION((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_SHOWSTEP_TWO((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_SHOWSTEP_THREE((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_CONNECT_TO_RAID_SERVER((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_RETURN_FROM_RAID((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_REFUND_REQ((Supplier<ClientPacket>)RequestRefundItem::new, ConnectionState.IN_GAME_STATES), 
    EX_BUY_SELL_UI_CLOSE_REQ((Supplier<ClientPacket>)RequestBuySellUIClose::new, ConnectionState.IN_GAME_STATES), 
    EX_EVENT_MATCH((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_PARTY_LOOTING_MODIFY((Supplier<ClientPacket>)RequestPartyLootModification::new, ConnectionState.IN_GAME_STATES), 
    EX_PARTY_LOOTING_MODIFY_AGREEMENT((Supplier<ClientPacket>)AnswerPartyLootModification::new, ConnectionState.IN_GAME_STATES), 
    EX_ANSWER_COUPLE_ACTION((Supplier<ClientPacket>)AnswerCoupleAction::new, ConnectionState.IN_GAME_STATES), 
    EX_BR_LOAD_EVENT_TOP_RANKERS_REQ((Supplier<ClientPacket>)BrEventRankerList::new, ConnectionState.IN_GAME_STATES), 
    EX_ASK_MY_MEMBERSHIP((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_QUEST_NPC_LOG_LIST((Supplier<ClientPacket>)RequestAddExpandQuestAlarm::new, ConnectionState.IN_GAME_STATES), 
    EX_VOTE_SYSTEM((Supplier<ClientPacket>)RequestVoteNew::new, ConnectionState.IN_GAME_STATES), 
    EX_GETON_SHUTTLE((Supplier<ClientPacket>)RequestShuttleGetOn::new, ConnectionState.IN_GAME_STATES), 
    EX_GETOFF_SHUTTLE((Supplier<ClientPacket>)RequestShuttleGetOff::new, ConnectionState.IN_GAME_STATES), 
    EX_MOVE_TO_LOCATION_IN_SHUTTLE((Supplier<ClientPacket>)MoveToLocationInShuttle::new, ConnectionState.IN_GAME_STATES), 
    EX_CAN_NOT_MOVE_ANYMORE_IN_SHUTTLE((Supplier<ClientPacket>)CannotMoveAnymoreInShuttle::new, ConnectionState.IN_GAME_STATES), 
    EX_AGITAUCTION_CMD((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_ADD_POST_FRIEND((Supplier<ClientPacket>)RequestExAddContactToContactList::new, ConnectionState.IN_GAME_STATES), 
    EX_DELETE_POST_FRIEND((Supplier<ClientPacket>)RequestExDeleteContactFromContactList::new, ConnectionState.IN_GAME_STATES), 
    EX_SHOW_POST_FRIEND((Supplier<ClientPacket>)RequestExShowContactList::new, ConnectionState.IN_GAME_STATES), 
    EX_FRIEND_LIST_FOR_POSTBOX((Supplier<ClientPacket>)RequestExFriendListExtended::new, ConnectionState.IN_GAME_STATES), 
    EX_GFX_OLYMPIAD((Supplier<ClientPacket>)RequestExOlympiadMatchListRefresh::new, ConnectionState.IN_GAME_STATES), 
    EX_BR_GAME_POINT_REQ((Supplier<ClientPacket>)RequestBRGamePoint::new, ConnectionState.IN_GAME_STATES), 
    EX_BR_PRODUCT_LIST_REQ((Supplier<ClientPacket>)RequestBRProductList::new, ConnectionState.IN_GAME_STATES), 
    EX_BR_PRODUCT_INFO_REQ((Supplier<ClientPacket>)RequestBRProductInfo::new, ConnectionState.IN_GAME_STATES), 
    EX_BR_BUY_PRODUCT_REQ((Supplier<ClientPacket>)RequestBRBuyProduct::new, ConnectionState.IN_GAME_STATES), 
    EX_BR_RECENT_PRODUCT_REQ((Supplier<ClientPacket>)RequestBRRecentProductList::new, ConnectionState.IN_GAME_STATES), 
    EX_BR_MINIGAME_LOAD_SCORES_REQ((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_BR_MINIGAME_INSERT_SCORE_REQ((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_BR_SET_LECTURE_MARK_REQ((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_CRYSTALITEM_INFO((Supplier<ClientPacket>)RequestCrystallizeEstimate::new, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_CRYSTALITEM_CANCEL((Supplier<ClientPacket>)RequestCrystallizeItemCancel::new, ConnectionState.IN_GAME_STATES), 
    EX_STOP_SCENE_PLAYER((Supplier<ClientPacket>)RequestExEscapeScene::new, ConnectionState.IN_GAME_STATES), 
    EX_FLY_MOVE((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_SURRENDER_PLEDGE_WAR((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_DYNAMIC_QUEST((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_FRIEND_DETAIL_INFO((Supplier<ClientPacket>)RequestFriendDetailInfo::new, ConnectionState.IN_GAME_STATES), 
    EX_UPDATE_FRIEND_MEMO((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_UPDATE_BLOCK_MEMO((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_LOAD_INZONE_PARTY_HISTORY((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_COMMISSION_ITEM_LIST((Supplier<ClientPacket>)RequestCommissionRegistrableItemList::new, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_COMMISSION_INFO((Supplier<ClientPacket>)RequestCommissionInfo::new, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_COMMISSION_REGISTER((Supplier<ClientPacket>)RequestCommissionRegister::new, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_COMMISSION_CANCEL((Supplier<ClientPacket>)RequestCommissionCancel::new, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_COMMISSION_DELETE((Supplier<ClientPacket>)RequestCommissionDelete::new, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_COMMISSION_SEARCH((Supplier<ClientPacket>)RequestCommissionList::new, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_COMMISSION_BUY_INFO((Supplier<ClientPacket>)RequestCommissionBuyInfo::new, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_COMMISSION_BUY_ITEM((Supplier<ClientPacket>)RequestCommissionBuyItem::new, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_COMMISSION_REGISTERED_ITEM((Supplier<ClientPacket>)RequestCommissionRegisteredItem::new, ConnectionState.IN_GAME_STATES), 
    EX_CALL_TO_CHANGE_CLASS((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_CHANGE_TO_AWAKENED_CLASS((Supplier<ClientPacket>)RequestChangeToAwakenedClass::new, ConnectionState.IN_GAME_STATES), 
    EX_NOT_USED_163((Supplier<ClientPacket>)ExIncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    EX_NOT_USED_164((Supplier<ClientPacket>)ExIncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_WEB_SESSION_ID((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_2ND_PASSWORD_CHECK((Supplier<ClientPacket>)RequestEx2ndPasswordCheck::new, ConnectionState.AUTHENTICATED_STATES), 
    EX_2ND_PASSWORD_VERIFY((Supplier<ClientPacket>)RequestEx2ndPasswordVerify::new, ConnectionState.AUTHENTICATED_STATES), 
    EX_2ND_PASSWORD_REQ((Supplier<ClientPacket>)RequestEx2ndPasswordReq::new, ConnectionState.AUTHENTICATED_STATES), 
    EX_CHECK_CHAR_NAME((Supplier<ClientPacket>)RequestCharacterNameCreatable::new, ConnectionState.AUTHENTICATED_STATES), 
    EX_REQUEST_GOODS_INVENTORY_INFO((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_USE_GOODS_IVENTORY_ITEM((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_NOTIFY_PLAY_START((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_FLY_MOVE_START((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_USER_HARDWARE_INFO((Supplier<ClientPacket>)RequestHardWareInfo::new, ConnectionState.ALL), 
    EX_USER_INTERFACE_INFO((Supplier<ClientPacket>)null, ConnectionState.ALL), 
    EX_CHANGE_ATTRIBUTE_ITEM((Supplier<ClientPacket>)RequestChangeAttributeItem::new, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_CHANGE_ATTRIBUTE((Supplier<ClientPacket>)SendChangeAttributeTargetItem::new, ConnectionState.IN_GAME_STATES), 
    EX_CHANGE_ATTRIBUTE_CANCEL((Supplier<ClientPacket>)RequestChangeAttributeCancel::new, ConnectionState.IN_GAME_STATES), 
    EX_BR_BUY_PRODUCT_GIFT_REQ((Supplier<ClientPacket>)RequestBRPresentBuyProduct::new, ConnectionState.IN_GAME_STATES), 
    EX_MENTOR_ADD((Supplier<ClientPacket>)ConfirmMenteeAdd::new, ConnectionState.IN_GAME_STATES), 
    EX_MENTOR_CANCEL((Supplier<ClientPacket>)RequestMentorCancel::new, ConnectionState.IN_GAME_STATES), 
    EX_MENTOR_LIST((Supplier<ClientPacket>)RequestMentorList::new, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_MENTOR_ADD((Supplier<ClientPacket>)RequestMenteeAdd::new, ConnectionState.IN_GAME_STATES), 
    EX_MENTEE_WAITING_LIST((Supplier<ClientPacket>)RequestMenteeWaitingList::new, ConnectionState.IN_GAME_STATES), 
    EX_JOIN_PLEDGE_BY_NAME((Supplier<ClientPacket>)RequestClanAskJoinByName::new, ConnectionState.IN_GAME_STATES), 
    EX_INZONE_WAITING_TIME((Supplier<ClientPacket>)RequestInzoneWaitingTime::new, ConnectionState.IN_GAME_STATES), 
    EX_JOIN_CURIOUS_HOUSE((Supplier<ClientPacket>)RequestJoinCuriousHouse::new, ConnectionState.IN_GAME_STATES), 
    EX_CANCEL_CURIOUS_HOUSE((Supplier<ClientPacket>)RequestCancelCuriousHouse::new, ConnectionState.IN_GAME_STATES), 
    EX_LEAVE_CURIOUS_HOUSE((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_OBSERVE_LIST_CURIOUS_HOUSE((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_OBSERVE_CURIOUS_HOUSE((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_EXIT_OBSERVE_CURIOUS_HOUSE((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_REQ_CURIOUS_HOUSE_HTML((Supplier<ClientPacket>)RequestCuriousHouseHtml::new, ConnectionState.IN_GAME_STATES), 
    EX_REQ_CURIOUS_HOUSE_RECORD((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_SYS_STRING((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_TRY_TO_PUT_SHAPE_SHIFTING_TARGET_ITEM((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_TRY_TO_PUT_SHAPE_SHIFTING_EXTRACTION_ITEM((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_CANCEL_SHAPE_SHIFTING((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_SHAPE_SHIFTING((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_NCGUARD((Supplier<ClientPacket>)ExIncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_KALIE_TOKEN((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_SHOW_REGIST_BEAUTY((Supplier<ClientPacket>)RequestShowBeautyList::new, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_REGIST_BEAUTY((Supplier<ClientPacket>)RequestRegistBeauty::new, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_SHOW_RESET_BEAUTY((Supplier<ClientPacket>)RequestShowResetShopList::new, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_RESET_BEAUTY((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_CHECK_SPEEDHACK((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_BR_ADD_INTERESTED_PRODUCT((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_BR_DELETE_INTERESTED_PRODUCT((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_BR_EXIST_NEW_PRODUCT_REQ((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_EVENT_CAMPAIGN_INFO((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_RECRUIT_INFO((Supplier<ClientPacket>)RequestPledgeRecruitInfo::new, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_RECRUIT_BOARD_SEARCH((Supplier<ClientPacket>)RequestPledgeRecruitBoardSearch::new, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_RECRUIT_BOARD_APPLY((Supplier<ClientPacket>)RequestPledgeRecruitBoardAccess::new, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_RECRUIT_BOARD_DETAIL((Supplier<ClientPacket>)RequestPledgeRecruitBoardDetail::new, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_WAITING_LIST_APPLY((Supplier<ClientPacket>)RequestPledgeWaitingApply::new, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_WAITING_LIST_APPLIED((Supplier<ClientPacket>)RequestPledgeWaitingApplied::new, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_WAITING_LIST((Supplier<ClientPacket>)RequestPledgeWaitingList::new, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_WAITING_USER((Supplier<ClientPacket>)RequestPledgeWaitingUser::new, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_WAITING_USER_ACCEPT((Supplier<ClientPacket>)RequestPledgeWaitingUserAccept::new, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_DRAFT_LIST_SEARCH((Supplier<ClientPacket>)RequestPledgeDraftListSearch::new, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_DRAFT_LIST_APPLY((Supplier<ClientPacket>)RequestPledgeDraftListApply::new, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_RECRUIT_APPLY_INFO((Supplier<ClientPacket>)RequestPledgeRecruitApplyInfo::new, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_JOIN_SYS((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_RESPONSE_WEB_PETITION_ALARM((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_NOTIFY_EXIT_BEAUTYSHOP((Supplier<ClientPacket>)NotifyExitBeautyShop::new, ConnectionState.IN_GAME_STATES), 
    EX_EVENT_REGISTER_XMAS_WISHCARD((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_ENCHANT_SCROLL_ITEM_ADD((Supplier<ClientPacket>)RequestExAddEnchantScrollItem::new, ConnectionState.IN_GAME_STATES), 
    EX_ENCHANT_SUPPORT_ITEM_REMOVE((Supplier<ClientPacket>)RequestExRemoveEnchantSupportItem::new, ConnectionState.IN_GAME_STATES), 
    EX_SELECT_CARD_REWARD((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_DIVIDE_ADENA_START((Supplier<ClientPacket>)RequestDivideAdenaStart::new, ConnectionState.IN_GAME_STATES), 
    EX_DIVIDE_ADENA_CANCEL((Supplier<ClientPacket>)RequestDivideAdenaCancel::new, ConnectionState.IN_GAME_STATES), 
    EX_DIVIDE_ADENA((Supplier<ClientPacket>)RequestDivideAdena::new, ConnectionState.IN_GAME_STATES), 
    EX_ACQUIRE_POTENTIAL_SKILL((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_POTENTIAL_SKILL_LIST((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_RESET_POTENTIAL_SKILL((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_CHANGE_POTENTIAL_POINT((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_STOP_MOVE((Supplier<ClientPacket>)RequestStopMove::new, ConnectionState.IN_GAME_STATES), 
    EX_ABILITY_WND_OPEN((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_ABILITY_WND_CLOSE((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_START_LUCKY_GAME((Supplier<ClientPacket>)RequestLuckyGameStartInfo::new, ConnectionState.IN_GAME_STATES), 
    EX_BETTING_LUCKY_GAME((Supplier<ClientPacket>)RequestLuckyGamePlay::new, ConnectionState.IN_GAME_STATES), 
    EX_TRAININGZONE_LEAVING((Supplier<ClientPacket>)NotifyTrainingRoomEnd::new, ConnectionState.IN_GAME_STATES), 
    EX_ENCHANT_ONE((Supplier<ClientPacket>)RequestNewEnchantPushOne::new, ConnectionState.IN_GAME_STATES), 
    EX_ENCHANT_ONE_REMOVE((Supplier<ClientPacket>)RequestNewEnchantRemoveOne::new, ConnectionState.IN_GAME_STATES), 
    EX_ENCHANT_TWO((Supplier<ClientPacket>)RequestNewEnchantPushTwo::new, ConnectionState.IN_GAME_STATES), 
    EX_ENCHANT_TWO_REMOVE((Supplier<ClientPacket>)RequestNewEnchantRemoveTwo::new, ConnectionState.IN_GAME_STATES), 
    EX_ENCHANT_CLOSE((Supplier<ClientPacket>)RequestNewEnchantClose::new, ConnectionState.IN_GAME_STATES), 
    EX_ENCHANT_TRY((Supplier<ClientPacket>)RequestNewEnchantTry::new, ConnectionState.IN_GAME_STATES), 
    EX_ENCHANT_RETRY_TO_PUT_ITEMS((Supplier<ClientPacket>)RequestNewEnchantRetryToPutItems::new, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_CARD_REWARD_LIST((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_ACCOUNT_ATTENDANCE_INFO((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_ACCOUNT_ATTENDANCE_REWARD((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_TARGET((Supplier<ClientPacket>)RequestTargetActionMenu::new, ConnectionState.IN_GAME_STATES), 
    EX_SELECTED_QUEST_ZONEID((Supplier<ClientPacket>)ExSendSelectedQuestZoneID::new, ConnectionState.IN_GAME_STATES), 
    EX_ALCHEMY_SKILL_LIST((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_TRY_MIX_CUBE((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_ALCHEMY_CONVERSION((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_EXECUTED_UIEVENTS_COUNT((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_CLIENT_INI((Supplier<ClientPacket>)ExIncomingPackets.DISCARD, ConnectionState.AUTHENTICATED_STATES), 
    EX_REQUEST_AUTOFISH((Supplier<ClientPacket>)ExRequestAutoFish::new, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_VIP_ATTENDANCE_ITEMLIST((Supplier<ClientPacket>)RequestVipAttendanceItemList::new, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_VIP_ATTENDANCE_CHECK((Supplier<ClientPacket>)RequestVipAttendanceCheck::new, ConnectionState.IN_GAME_STATES), 
    EX_TRY_ENSOUL((Supplier<ClientPacket>)RequestItemEnsoul::new, ConnectionState.IN_GAME_STATES), 
    EX_CASTLEWAR_SEASON_REWARD((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_BR_VIP_PRODUCT_LIST_REQ((Supplier<ClientPacket>)RequestVipProductList::new, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_LUCKY_GAME_INFO((Supplier<ClientPacket>)RequestVipLuckGameInfo::new, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_LUCKY_GAME_ITEMLIST((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_LUCKY_GAME_BONUS((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_VIP_INFO((Supplier<ClientPacket>)ExRequestVipInfo::new, ConnectionState.IN_GAME_STATES), 
    EX_CAPTCHA_ANSWER((Supplier<ClientPacket>)RequestCaptchaAnswer::new, ConnectionState.IN_GAME_STATES), 
    EX_REFRESH_CAPTCHA_IMAGE((Supplier<ClientPacket>)RequestRefreshCaptcha::new, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_SIGNIN((Supplier<ClientPacket>)RequestPledgeSignInForOpenJoiningMethod::new, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_MATCH_ARENA((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_CONFIRM_MATCH_ARENA((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_CANCEL_MATCH_ARENA((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_CHANGE_CLASS_ARENA((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_CONFIRM_CLASS_ARENA((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_DECO_NPC_INFO((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_DECO_NPC_SET((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_FACTION_INFO((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_EXIT_ARENA((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_BALTHUS_TOKEN((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_PARTY_MATCHING_ROOM_HISTORY((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_ARENA_CUSTOM_NOTIFICATION((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_TODOLIST((Supplier<ClientPacket>)RequestTodoList::new, ConnectionState.JOINING_GAME_AND_IN_GAME), 
    EX_TODOLIST_HTML((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_ONE_DAY_RECEIVE_REWARD((Supplier<ClientPacket>)RequestOneDayRewardReceive::new, ConnectionState.IN_GAME_STATES), 
    EX_QUEUETICKET((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_BONUS_UI_OPEN((Supplier<ClientPacket>)RequestPledgeBonusOpen::new, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_BONUS_REWARD_LIST((Supplier<ClientPacket>)RequestPledgeBonusRewardList::new, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_BONUS_RECEIVE_REWARD((Supplier<ClientPacket>)RequestPledgeBonusReward::new, ConnectionState.IN_GAME_STATES), 
    EX_SSO_AUTHNTOKEN_REQ((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_QUEUETICKET_LOGIN((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_BLOCK_DETAIL_INFO((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_TRY_ENSOUL_EXTRACTION((Supplier<ClientPacket>)RequestTryEnSoulExtraction::new, ConnectionState.IN_GAME_STATES), 
    EX_RAID_BOSS_SPAWN_INFO((Supplier<ClientPacket>)RequestRaidBossSpawnInfo::new, ConnectionState.IN_GAME_STATES), 
    EX_RAID_SERVER_INFO((Supplier<ClientPacket>)RequestRaidServerInfo::new, ConnectionState.IN_GAME_STATES), 
    EX_SHOW_AGIT_SIEGE_INFO((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_ITEM_AUCTION_STATUS((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_MONSTER_BOOK_OPEN((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_MONSTER_BOOK_CLOSE((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_REQ_MONSTER_BOOK_REWARD((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_MATCHGROUP((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_MATCHGROUP_ASK((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_MATCHGROUP_ANSWER((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_MATCHGROUP_WITHDRAW((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_MATCHGROUP_OUST((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_MATCHGROUP_CHANGE_MASTER((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_UPGRADE_SYSTEM_REQUEST((Supplier<ClientPacket>)ExUpgradeSystemRequest::new, ConnectionState.IN_GAME_STATES), 
    EX_CARD_UPDOWN_PICK_NUMB((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_CARD_UPDOWN_GAME_REWARD_REQUEST((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_CARD_UPDOWN_GAME_RETRY((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_CARD_UPDOWN_GAME_QUIT((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_ARENA_RANK_ALL((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_ARENA_MYRANK((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_SWAP_AGATHION_SLOT_ITEMS((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_CONTRIBUTION_RANK((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_CONTRIBUTION_INFO((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_CONTRIBUTION_REWARD((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_LEVEL_UP((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_MISSION_INFO((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_MISSION_REWARD((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_MASTERY_INFO((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_MASTERY_SET((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_MASTERY_RESET((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_SKILL_INFO((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_SKILL_ACTIVATE((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_ITEM_LIST((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_ITEM_ACTIVATE((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_ANNOUNCE((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_ANNOUNCE_SET((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_CREATE_PLEDGE((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_ITEM_INFO((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_ITEM_BUY((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_ELEMENTAL_SPIRIT_INFO((Supplier<ClientPacket>)ExElementalSpiritInfo::new, ConnectionState.IN_GAME_STATES), 
    EX_ELEMENTAL_SPIRIT_EXTRACT_INFO((Supplier<ClientPacket>)ExElementalSpiritExtractInfo::new, ConnectionState.IN_GAME_STATES), 
    EX_ELEMENTAL_SPIRIT_EXTRACT((Supplier<ClientPacket>)ExElementalSpiritExtract::new, ConnectionState.IN_GAME_STATES), 
    EX_ELEMENTAL_SPIRIT_EVOLUTION_INFO((Supplier<ClientPacket>)ExElementalSpiritEvolutionInfo::new, ConnectionState.IN_GAME_STATES), 
    EX_ELEMENTAL_SPIRIT_EVOLUTION((Supplier<ClientPacket>)ExElementalSpiritEvolution::new, ConnectionState.IN_GAME_STATES), 
    EX_ELEMENTAL_SPIRIT_SET_TALENT((Supplier<ClientPacket>)ExElementalSpiritSetTalent::new, ConnectionState.IN_GAME_STATES), 
    EX_ELEMENTAL_SPIRIT_INIT_TALENT((Supplier<ClientPacket>)ExElementalInitTalent::new, ConnectionState.IN_GAME_STATES), 
    EX_ELEMENTAL_SPIRIT_ABSORB_INFO((Supplier<ClientPacket>)ExElementalSpiritAbsorbInfo::new, ConnectionState.IN_GAME_STATES), 
    EX_ELEMENTAL_SPIRIT_ABSORB((Supplier<ClientPacket>)ExElementalSpiritAbsorb::new, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_LOCKED_ITEM((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_UNLOCKED_ITEM((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_LOCKED_ITEM_CANCEL((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_UNLOCKED_ITEM_CANCEL((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_ELEMENTAL_SPIRIT_CHANGE_TYPE((Supplier<ClientPacket>)ExElementalSpiritChangeType::new, ConnectionState.IN_GAME_STATES), 
    EX_BLOCK_PACKET_FOR_AD((Supplier<ClientPacket>)ExRequestBlockListForAD::new, ConnectionState.IN_GAME_STATES), 
    EX_USER_BAN_INFO((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_INTERACT_MODIFY((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_TRY_ENCHANT_ARTIFACT((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_UPGRADE_SYSTEM_NORMAL_REQUEST((Supplier<ClientPacket>)ExUpgradeSystemNormalRequest::new, ConnectionState.IN_GAME_STATES), 
    EX_PURCHASE_LIMIT_SHOP_ITEM_LIST((Supplier<ClientPacket>)RequestPurchaseLimitShopItemList::new, ConnectionState.IN_GAME_STATES), 
    EX_PURCHASE_LIMIT_SHOP_ITEM_BUY((Supplier<ClientPacket>)RequestPurchaseLimitShopItemBuy::new, ConnectionState.IN_GAME_STATES), 
    EX_OPEN_HTML((Supplier<ClientPacket>)ExOpenHtml::new, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_CLASS_CHANGE((Supplier<ClientPacket>)ExRequestClassChange::new, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_CLASS_CHANGE_VERIFYING((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_TELEPORT((Supplier<ClientPacket>)ExRequestTeleport::new, ConnectionState.IN_GAME_STATES), 
    EX_COSTUME_USE_ITEM((Supplier<ClientPacket>)ExRequestCostumeUseItem::new, ConnectionState.IN_GAME_STATES), 
    EX_COSTUME_LIST((Supplier<ClientPacket>)ExRequestCostumeList::new, ConnectionState.IN_GAME_STATES), 
    EX_COSTUME_COLLECTION_SKILL_ACTIVE((Supplier<ClientPacket>)ExRequestCostumeCollectSkillActive::new, ConnectionState.IN_GAME_STATES), 
    EX_COSTUME_EVOLUTION((Supplier<ClientPacket>)ExRequestCostumeEvolution::new, ConnectionState.IN_GAME_STATES), 
    EX_COSTUME_EXTRACT((Supplier<ClientPacket>)ExRequestCostumeExtract::new, ConnectionState.IN_GAME_STATES), 
    EX_COSTUME_LOCK((Supplier<ClientPacket>)ExRequestCostumeLock::new, ConnectionState.IN_GAME_STATES), 
    EX_COSTUME_CHANGE_SHORTCUT((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_MAGICLAMP_GAME_INFO((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_MAGICLAMP_GAME_START((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_ACTIVATE_AUTO_SHORTCUT((Supplier<ClientPacket>)ExRequestActivateAutoShortcut::new, ConnectionState.IN_GAME_STATES), 
    EX_PREMIUM_MANAGER_LINK_HTML((Supplier<ClientPacket>)ExRequestActivateAutoShortcut::new, ConnectionState.IN_GAME_STATES), 
    EX_PREMIUM_MANAGER_PASS_CMD_TO_SERVER((Supplier<ClientPacket>)ExRequestActivateAutoShortcut::new, ConnectionState.IN_GAME_STATES), 
    EX_ACTIVATED_CURSED_TREASURE_BOX_LOCATION((Supplier<ClientPacket>)ExRequestActivateAutoShortcut::new, ConnectionState.IN_GAME_STATES), 
    EX_PAYBACK_LIST((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_PAYBACK_GIVE_REWARD((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_AUTOPLAY_SETTING((Supplier<ClientPacket>)ExAutoPlaySetting::new, ConnectionState.IN_GAME_STATES), 
    EX_OLYMPIAD_MATCH_MAKING((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_OLYMPIAD_MATCH_MAKING_CANCEL((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_FESTIVAL_BM_INFO((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_FESTIVAL_BM_GAME((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_GACHA_SHOP_INFO((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_GACHA_SHOP_GACHA_GROUP((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_GACHA_SHOP_GACHA_ITEM((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_TIME_RESTRICT_FIELD_LIST((Supplier<ClientPacket>)ExTimedHuntingZoneList::new, ConnectionState.IN_GAME_STATES), 
    EX_TIME_RESTRICT_FIELD_USER_ENTER((Supplier<ClientPacket>)ExTimedHuntingZoneEnter::new, ConnectionState.IN_GAME_STATES), 
    EX_RANKING_CHAR_INFO((Supplier<ClientPacket>)ExRankCharInfo::new, ConnectionState.IN_GAME_STATES), 
    EX_RANKING_CHAR_HISTORY((Supplier<ClientPacket>)ExRequestRankingCharHistory::new, ConnectionState.IN_GAME_STATES), 
    EX_RANKING_CHAR_RANKERS((Supplier<ClientPacket>)ExRankingCharRankers::new, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_MERCENARY_RECRUIT_INFO_SET((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_MERCENARY_CASTLEWAR_CASTLE_INFO((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_MERCENARY_CASTLEWAR_CASTLE_SIEGE_INFO((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_MERCENARY_CASTLEWAR_CASTLE_SIEGE_ATTACKER_LIST((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_MERCENARY_CASTLEWAR_CASTLE_SIEGE_DEFENDER_LIST((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_MERCENARY_MEMBER_LIST((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_PLEDGE_MERCENARY_MEMBER_JOIN((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_PVPBOOK_LIST((Supplier<ClientPacket>)ExRequestPvpBookList::new, ConnectionState.IN_GAME_STATES), 
    EX_PVPBOOK_KILLER_LOCATION((Supplier<ClientPacket>)ExRequestKillerLocation::new, ConnectionState.IN_GAME_STATES), 
    EX_PVPBOOK_TELEPORT_TO_KILLER((Supplier<ClientPacket>)ExTeleportToKiller::new, ConnectionState.IN_GAME_STATES), 
    EX_LETTER_COLLECTOR_TAKE_REWARD((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_SET_STATUS_BONUS((Supplier<ClientPacket>)ExSetStatusBonus::new, ConnectionState.IN_GAME_STATES), 
    EX_RESET_STATUS_BONUS((Supplier<ClientPacket>)ExResetStatusBonus::new, ConnectionState.IN_GAME_STATES), 
    EX_OLYMPIAD_MY_RANKING_INFO((Supplier<ClientPacket>)ExRequestOlympiadMyRank::new, ConnectionState.IN_GAME_STATES), 
    EX_OLYMPIAD_RANKING_INFO((Supplier<ClientPacket>)ExRequestOlympiadRanking::new, ConnectionState.IN_GAME_STATES), 
    EX_OLYMPIAD_HERO_AND_LEGEND_INFO((Supplier<ClientPacket>)ExRequestOlympiadHeroes::new, ConnectionState.IN_GAME_STATES), 
    EX_CASTLEWAR_OBSERVER_START((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_RAID_TELEPORT_INFO((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_TELEPORT_TO_RAID_POSITION((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_CRAFT_EXTRACT((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_CRAFT_RANDOM_INFO((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_CRAFT_RANDOM_LOCK_SLOT((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_CRAFT_RANDOM_REFRESH((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_CRAFT_RANDOM_MAKE((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_MULTI_SELL_LIST((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_SAVE_ITEM_ANNOUNCE_SETTING((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_OLYMPIAD_UI((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_SHARED_POSITION_TELEPORT_UI((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_SHARED_POSITION_TELEPORT((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_AUTH_RECONNECT((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_PET_EQUIP_ITEM((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_PET_UNEQUIP_ITEM((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_SHOW_HOMUNCULUS_INFO((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_HOMUNCULUS_CREATE_START((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_HOMUNCULUS_INSERT((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_HOMUNCULUS_SUMMON((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_DELETE_HOMUNCULUS_DATA((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_REQUEST_ACTIVATE_HOMUNCULUS((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_HOMUNCULUS_GET_ENCHANT_POINT((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_HOMUNCULUS_INIT_POINT((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_EVOLVE_PET((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_ENCHANT_HOMUNCULUS_SKILL((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_HOMUNCULUS_ENCHANT_EXP((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_TELEPORT_FAVORITES_LIST((Supplier<ClientPacket>)ExRequestTeleportFavoriteList::new, ConnectionState.IN_GAME_STATES), 
    EX_TELEPORT_FAVORITES_UI_TOGGLE((Supplier<ClientPacket>)ExRequestTeleportFavoritesUIToggle::new, ConnectionState.IN_GAME_STATES), 
    EX_TELEPORT_FAVORITES_ADD_DEL((Supplier<ClientPacket>)ExRequestTeleportFavoritesAddDel::new, ConnectionState.IN_GAME_STATES), 
    EX_ANTIBOT((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_DPSVR((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_TENPROTECT_DECRYPT_ERROR((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_NET_LATENCY((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_MABLE_GAME_OPEN((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_MABLE_GAME_ROLL_DICE((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_MABLE_GAME_POPUP_OK((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_MABLE_GAME_RESET((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_MABLE_GAME_CLOSE((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_RETURN_TO_ORIGIN((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_PK_PENALTY_LIST((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    EX_PK_PENALTY_LIST_ONLY_LOC((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES);
    
    static final ExIncomingPackets[] PACKET_ARRAY;
    private final boolean hasExtension;
    private final Supplier<ClientPacket> incomingPacketFactory;
    private final EnumSet<ConnectionState> connectionStates;
    
    private ExIncomingPackets(final Supplier<ClientPacket> incomingPacketFactory, final boolean hasExtension, final EnumSet<ConnectionState> connectionStates) {
        this.incomingPacketFactory = Objects.requireNonNullElse(incomingPacketFactory, ExIncomingPackets.NULL_PACKET_SUPLIER);
        this.connectionStates = connectionStates;
        this.hasExtension = hasExtension;
    }
    
    private ExIncomingPackets(final Supplier<ClientPacket> incomingPacketFactory, final EnumSet<ConnectionState> connectionStates) {
        this(incomingPacketFactory, false, connectionStates);
    }
    
    @Override
    public int getPacketId() {
        return this.ordinal();
    }
    
    @Override
    public ClientPacket newIncomingPacket() {
        return this.incomingPacketFactory.get();
    }
    
    @Override
    public boolean canHandleState(final ConnectionState state) {
        return this.connectionStates.contains(state);
    }
    
    @Override
    public boolean hasExtension() {
        return this.hasExtension;
    }
    
    @Override
    public PacketFactory handleExtension(final ReadableBuffer buffer) {
        if (ExIncomingPackets.EX_USER_BOOKMARK == this) {
            return this.handleBookMarkPaket(buffer);
        }
        return ExIncomingPackets.NULLABLE_PACKET_FACTORY;
    }
    
    private PacketFactory handleBookMarkPaket(final ReadableBuffer packet) {
        PacketFactory nullable_PACKET_FACTORY = null;
        switch (packet.readInt()) {
            case 0: {
                nullable_PACKET_FACTORY = new DynamicPacketFactory((Supplier<ClientPacket>)RequestBookMarkSlotInfo::new);
                break;
            }
            case 1: {
                nullable_PACKET_FACTORY = new DynamicPacketFactory((Supplier<ClientPacket>)RequestSaveBookMarkSlot::new);
                break;
            }
            case 2: {
                nullable_PACKET_FACTORY = new DynamicPacketFactory((Supplier<ClientPacket>)RequestModifyBookMarkSlot::new);
                break;
            }
            case 3: {
                nullable_PACKET_FACTORY = new DynamicPacketFactory((Supplier<ClientPacket>)RequestDeleteBookMarkSlot::new);
                break;
            }
            case 4: {
                nullable_PACKET_FACTORY = new DynamicPacketFactory((Supplier<ClientPacket>)RequestTeleportBookMark::new);
                break;
            }
            case 5: {
                nullable_PACKET_FACTORY = new DynamicPacketFactory((Supplier<ClientPacket>)RequestChangeBookMarkSlot::new);
                break;
            }
            default: {
                nullable_PACKET_FACTORY = ExIncomingPackets.NULLABLE_PACKET_FACTORY;
                break;
            }
        }
        return nullable_PACKET_FACTORY;
    }
    
    @Override
    public EnumSet<ConnectionState> getConnectionStates() {
        return this.connectionStates;
    }
    
    static {
        PACKET_ARRAY = values();
    }
    
    static class DynamicPacketFactory implements PacketFactory
    {
        private final Supplier<ClientPacket> supplier;
        
        DynamicPacketFactory(final Supplier<ClientPacket> supplier) {
            this.supplier = supplier;
        }
        
        @Override
        public boolean canHandleState(final ConnectionState state) {
            return true;
        }
        
        @Override
        public ClientPacket newIncomingPacket() {
            return this.supplier.get();
        }
    }
}
