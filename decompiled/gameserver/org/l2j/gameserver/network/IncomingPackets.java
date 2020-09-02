// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network;

import org.l2j.gameserver.network.clientpackets.RequestDeleteMacro;
import org.l2j.gameserver.network.clientpackets.RequestMakeMacro;
import org.l2j.gameserver.network.clientpackets.RequestPledgePower;
import org.l2j.gameserver.network.clientpackets.GameGuardReply;
import org.l2j.gameserver.network.clientpackets.RequestPetitionFeedback;
import org.l2j.gameserver.network.clientpackets.RequestPreviewItem;
import org.l2j.gameserver.network.clientpackets.DlgAnswer;
import org.l2j.gameserver.network.clientpackets.RequestBuySeed;
import org.l2j.gameserver.network.clientpackets.RequestHennaItemInfo;
import org.l2j.gameserver.network.clientpackets.RequestHennaItemList;
import org.l2j.gameserver.network.clientpackets.ObserverReturn;
import org.l2j.gameserver.network.clientpackets.RequestRecipeShopManagePrev;
import org.l2j.gameserver.network.clientpackets.RequestRecipeShopMakeItem;
import org.l2j.gameserver.network.clientpackets.RequestRecipeShopMakeInfo;
import org.l2j.gameserver.network.clientpackets.RequestRecipeShopManageQuit;
import org.l2j.gameserver.network.clientpackets.RequestRecipeShopListSet;
import org.l2j.gameserver.network.clientpackets.RequestRecipeShopMessageSet;
import org.l2j.gameserver.network.clientpackets.RequestRecipeItemMakeSelf;
import org.l2j.gameserver.network.clientpackets.RequestRecipeItemMakeInfo;
import org.l2j.gameserver.network.clientpackets.RequestRecipeBookDestroy;
import org.l2j.gameserver.network.clientpackets.RequestRecipeBookOpen;
import org.l2j.gameserver.network.clientpackets.SnoopQuit;
import org.l2j.gameserver.network.clientpackets.BypassUserCmd;
import org.l2j.gameserver.network.clientpackets.MultiSellChoose;
import org.l2j.gameserver.network.clientpackets.RequestSetCastleSiegeTime;
import org.l2j.gameserver.network.clientpackets.RequestConfirmSiegeWaitingList;
import org.l2j.gameserver.network.clientpackets.RequestJoinSiege;
import org.l2j.gameserver.network.clientpackets.RequestSiegeDefenderList;
import org.l2j.gameserver.network.clientpackets.RequestSiegeAttackerList;
import org.l2j.gameserver.network.clientpackets.RequestSiegeInfo;
import org.l2j.gameserver.network.clientpackets.RequestBlock;
import org.l2j.gameserver.network.clientpackets.RequestPackageSend;
import org.l2j.gameserver.network.clientpackets.RequestPackageSendableItemList;
import org.l2j.gameserver.network.clientpackets.RequestSkillCoolTime;
import org.l2j.gameserver.network.clientpackets.RequestPrivateStoreSell;
import org.l2j.gameserver.network.clientpackets.SetPrivateStoreMsgBuy;
import org.l2j.gameserver.network.clientpackets.RequestPrivateStoreQuitBuy;
import org.l2j.gameserver.network.clientpackets.SetPrivateStoreListBuy;
import org.l2j.gameserver.network.clientpackets.RequestPetGetItem;
import org.l2j.gameserver.network.clientpackets.SetPrivateStoreMsgSell;
import org.l2j.gameserver.network.clientpackets.RequestPrivateStoreQuitSell;
import org.l2j.gameserver.network.clientpackets.RequestGiveItemToPet;
import org.l2j.gameserver.network.clientpackets.RequestPetUseItem;
import org.l2j.gameserver.network.clientpackets.RequestChangePetName;
import org.l2j.gameserver.network.clientpackets.RequestAllyCrest;
import org.l2j.gameserver.network.clientpackets.RequestSetAllyCrest;
import org.l2j.gameserver.network.clientpackets.RequestDismissAlly;
import org.l2j.gameserver.network.clientpackets.AllyDismiss;
import org.l2j.gameserver.network.clientpackets.AllyLeave;
import org.l2j.gameserver.network.clientpackets.RequestAnswerJoinAlly;
import org.l2j.gameserver.network.clientpackets.RequestJoinAlly;
import org.l2j.gameserver.network.clientpackets.RequestGmList;
import org.l2j.gameserver.network.clientpackets.RequestPetitionCancel;
import org.l2j.gameserver.network.clientpackets.RequestPetition;
import org.l2j.gameserver.network.clientpackets.RequestTutorialClientEvent;
import org.l2j.gameserver.network.clientpackets.RequestTutorialQuestionMark;
import org.l2j.gameserver.network.clientpackets.RequestTutorialPassCmdToServer;
import org.l2j.gameserver.network.clientpackets.RequestTutorialLinkHtml;
import org.l2j.gameserver.network.clientpackets.RequestPrivateStoreBuy;
import org.l2j.gameserver.network.clientpackets.RequestPartyMatchDetail;
import org.l2j.gameserver.network.clientpackets.RequestPartyMatchList;
import org.l2j.gameserver.network.clientpackets.RequestPartyMatchConfig;
import org.l2j.gameserver.network.clientpackets.RequestGMCommand;
import org.l2j.gameserver.network.clientpackets.RequestRestartPoint;
import org.l2j.gameserver.network.clientpackets.RequestAcquireSkill;
import org.l2j.gameserver.network.clientpackets.CharacterRestore;
import org.l2j.gameserver.network.clientpackets.friend.RequestFriendDel;
import org.l2j.gameserver.network.clientpackets.friend.RequestFriendList;
import org.l2j.gameserver.network.clientpackets.friend.RequestAnswerFriendInvite;
import org.l2j.gameserver.network.clientpackets.friend.RequestFriendInvite;
import org.l2j.gameserver.network.clientpackets.CannotMoveAnymoreInVehicle;
import org.l2j.gameserver.network.clientpackets.RequestMoveToLocationInVehicle;
import org.l2j.gameserver.network.clientpackets.SendBypassBuildCmd;
import org.l2j.gameserver.network.clientpackets.RequestAcquireSkillInfo;
import org.l2j.gameserver.network.clientpackets.RequestHennaRemove;
import org.l2j.gameserver.network.clientpackets.RequestHennaItemRemoveInfo;
import org.l2j.gameserver.network.clientpackets.RequestHennaRemoveList;
import org.l2j.gameserver.network.clientpackets.RequestHennaEquip;
import org.l2j.gameserver.network.clientpackets.RequestRecordInfo;
import org.l2j.gameserver.network.clientpackets.RequestShowMiniMap;
import org.l2j.gameserver.network.clientpackets.friend.RequestSendFriendMsg;
import org.l2j.gameserver.network.clientpackets.RequestPledgeCrest;
import org.l2j.gameserver.network.clientpackets.RequestPledgeExtendedInfo;
import org.l2j.gameserver.network.clientpackets.RequestPledgeInfo;
import org.l2j.gameserver.network.clientpackets.RequestQuestAbort;
import org.l2j.gameserver.network.clientpackets.RequestQuestList;
import org.l2j.gameserver.network.clientpackets.RequestDestroyItem;
import org.l2j.gameserver.network.clientpackets.RequestEnchantItem;
import org.l2j.gameserver.network.clientpackets.RequestShowBoard;
import org.l2j.gameserver.network.clientpackets.FinishRotating;
import org.l2j.gameserver.network.clientpackets.StartRotating;
import org.l2j.gameserver.network.clientpackets.ValidatePosition;
import org.l2j.gameserver.network.clientpackets.RequestRestart;
import org.l2j.gameserver.network.clientpackets.RequestActionUse;
import org.l2j.gameserver.network.clientpackets.AnswerTradeRequest;
import org.l2j.gameserver.network.clientpackets.RequestGetOffVehicle;
import org.l2j.gameserver.network.clientpackets.RequestGetOnVehicle;
import org.l2j.gameserver.network.clientpackets.MoveWithDelta;
import org.l2j.gameserver.network.clientpackets.RequestSkillList;
import org.l2j.gameserver.network.clientpackets.RequestPledgeMemberList;
import org.l2j.gameserver.network.clientpackets.Say2;
import org.l2j.gameserver.network.clientpackets.RequestTargetCanceld;
import org.l2j.gameserver.network.clientpackets.CannotMoveAnymore;
import org.l2j.gameserver.network.clientpackets.RequestOustPartyMember;
import org.l2j.gameserver.network.clientpackets.RequestWithDrawalParty;
import org.l2j.gameserver.network.clientpackets.RequestAnswerJoinParty;
import org.l2j.gameserver.network.clientpackets.RequestJoinParty;
import org.l2j.gameserver.network.clientpackets.RequestBuyItem;
import org.l2j.gameserver.network.clientpackets.RequestShortCutDel;
import org.l2j.gameserver.network.clientpackets.RequestShortCutReg;
import org.l2j.gameserver.network.clientpackets.SendWareHouseWithDrawList;
import org.l2j.gameserver.network.clientpackets.SendWareHouseDepositList;
import org.l2j.gameserver.network.clientpackets.Appearing;
import org.l2j.gameserver.network.clientpackets.RequestMagicSkillUse;
import org.l2j.gameserver.network.clientpackets.RequestMagicSkillList;
import org.l2j.gameserver.network.clientpackets.RequestSellItem;
import org.l2j.gameserver.network.clientpackets.SetPrivateStoreListSell;
import org.l2j.gameserver.network.clientpackets.RequestPrivateStoreManageSell;
import org.l2j.gameserver.network.clientpackets.RequestCrystallizeItem;
import org.l2j.gameserver.network.clientpackets.RequestAllyInfo;
import org.l2j.gameserver.network.clientpackets.RequestGetItemFromPet;
import org.l2j.gameserver.network.clientpackets.AuthLogin;
import org.l2j.gameserver.network.clientpackets.RequestOustPledgeMember;
import org.l2j.gameserver.network.clientpackets.RequestWithdrawalPledge;
import org.l2j.gameserver.network.clientpackets.RequestAnswerJoinPledge;
import org.l2j.gameserver.network.clientpackets.RequestJoinPledge;
import org.l2j.gameserver.network.clientpackets.RequestBBSwrite;
import org.l2j.gameserver.network.clientpackets.RequestBypassToServer;
import org.l2j.gameserver.network.clientpackets.RequestLinkHtml;
import org.l2j.gameserver.network.clientpackets.Action;
import org.l2j.gameserver.network.clientpackets.TradeDone;
import org.l2j.gameserver.network.clientpackets.AddTradeItem;
import org.l2j.gameserver.network.clientpackets.TradeRequest;
import org.l2j.gameserver.network.clientpackets.UseItem;
import org.l2j.gameserver.network.clientpackets.RequestDropItem;
import org.l2j.gameserver.network.clientpackets.RequestUnEquipItem;
import org.l2j.gameserver.network.clientpackets.RequestItemList;
import org.l2j.gameserver.network.clientpackets.NewCharacter;
import org.l2j.gameserver.network.clientpackets.CharacterSelect;
import org.l2j.gameserver.network.clientpackets.EnterWorld;
import org.l2j.gameserver.network.clientpackets.MoveBackwardToLocation;
import org.l2j.gameserver.network.clientpackets.ProtocolVersion;
import org.l2j.gameserver.network.clientpackets.CharacterDelete;
import org.l2j.gameserver.network.clientpackets.CharacterCreate;
import org.l2j.gameserver.network.clientpackets.RequestGiveNickName;
import org.l2j.gameserver.network.clientpackets.RequestSetPledgeCrest;
import org.l2j.gameserver.network.clientpackets.RequestStopPledgeWar;
import org.l2j.gameserver.network.clientpackets.RequestReplyStartPledgeWar;
import org.l2j.gameserver.network.clientpackets.RequestStartPledgeWar;
import org.l2j.gameserver.network.clientpackets.Attack;
import org.l2j.gameserver.network.clientpackets.Logout;
import io.github.joealisson.mmocore.ReadableBuffer;
import java.util.Objects;
import java.util.EnumSet;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import java.util.function.Supplier;

public enum IncomingPackets implements PacketFactory
{
    LOGOUT((Supplier<ClientPacket>)Logout::new, ConnectionState.AUTHENTICATED_AND_IN_GAME), 
    ATTACK((Supplier<ClientPacket>)Attack::new, ConnectionState.IN_GAME_STATES), 
    MOVE_BACKWARD_TO_LOCATION((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    START_PLEDGE_WAR((Supplier<ClientPacket>)RequestStartPledgeWar::new, ConnectionState.IN_GAME_STATES), 
    NOT_USE_10((Supplier<ClientPacket>)RequestReplyStartPledgeWar::new, ConnectionState.IN_GAME_STATES), 
    STOP_PLEDGE_WAR((Supplier<ClientPacket>)RequestStopPledgeWar::new, ConnectionState.IN_GAME_STATES), 
    NOT_USE_11((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    NOT_USE_12((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    NOT_USE_13((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    SET_PLEDGE_CREST((Supplier<ClientPacket>)RequestSetPledgeCrest::new, ConnectionState.IN_GAME_STATES), 
    NOT_USE_14((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    GIVE_NICKNAME((Supplier<ClientPacket>)RequestGiveNickName::new, ConnectionState.IN_GAME_STATES), 
    CHARACTER_CREATE((Supplier<ClientPacket>)CharacterCreate::new, ConnectionState.AUTHENTICATED_STATES), 
    CHARACTER_DELETE((Supplier<ClientPacket>)CharacterDelete::new, ConnectionState.AUTHENTICATED_STATES), 
    VERSION((Supplier<ClientPacket>)ProtocolVersion::new, ConnectionState.CONNECTED_STATES), 
    MOVE_TO_LOCATION((Supplier<ClientPacket>)MoveBackwardToLocation::new, ConnectionState.IN_GAME_STATES), 
    NOT_USE_34((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.JOINING_GAME_STATES), 
    ENTER_WORLD((Supplier<ClientPacket>)EnterWorld::new, ConnectionState.JOINING_GAME_STATES), 
    CHARACTER_SELECT((Supplier<ClientPacket>)CharacterSelect::new, ConnectionState.AUTHENTICATED_STATES), 
    NEW_CHARACTER((Supplier<ClientPacket>)NewCharacter::new, ConnectionState.AUTHENTICATED_STATES), 
    ITEMLIST((Supplier<ClientPacket>)RequestItemList::new, ConnectionState.IN_GAME_STATES), 
    NOT_USE_1((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    UNEQUIP_ITEM((Supplier<ClientPacket>)RequestUnEquipItem::new, ConnectionState.IN_GAME_STATES), 
    DROP_ITEM((Supplier<ClientPacket>)RequestDropItem::new, ConnectionState.IN_GAME_STATES), 
    GET_ITEM((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    USE_ITEM((Supplier<ClientPacket>)UseItem::new, ConnectionState.IN_GAME_STATES), 
    TRADE_REQUEST((Supplier<ClientPacket>)TradeRequest::new, ConnectionState.IN_GAME_STATES), 
    TRADE_ADD((Supplier<ClientPacket>)AddTradeItem::new, ConnectionState.IN_GAME_STATES), 
    TRADE_DONE((Supplier<ClientPacket>)TradeDone::new, ConnectionState.IN_GAME_STATES), 
    NOT_USE_35((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    NOT_USE_36((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    ACTION((Supplier<ClientPacket>)Action::new, ConnectionState.IN_GAME_STATES), 
    NOT_USE_37((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    NOT_USE_38((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    LINK_HTML((Supplier<ClientPacket>)RequestLinkHtml::new, ConnectionState.IN_GAME_STATES), 
    PASS_CMD_TO_SERVER((Supplier<ClientPacket>)RequestBypassToServer::new, ConnectionState.IN_GAME_STATES), 
    WRITE_BBS((Supplier<ClientPacket>)RequestBBSwrite::new, ConnectionState.IN_GAME_STATES), 
    NOT_USE_39((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    JOIN_PLEDGE((Supplier<ClientPacket>)RequestJoinPledge::new, ConnectionState.IN_GAME_STATES), 
    ANSWER_JOIN_PLEDGE((Supplier<ClientPacket>)RequestAnswerJoinPledge::new, ConnectionState.IN_GAME_STATES), 
    WITHDRAWAL_PLEDGE((Supplier<ClientPacket>)RequestWithdrawalPledge::new, ConnectionState.IN_GAME_STATES), 
    OUST_PLEDGE_MEMBER((Supplier<ClientPacket>)RequestOustPledgeMember::new, ConnectionState.IN_GAME_STATES), 
    NOT_USE_40((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    LOGIN((Supplier<ClientPacket>)AuthLogin::new, ConnectionState.CONNECTED_STATES), 
    GET_ITEM_FROM_PET((Supplier<ClientPacket>)RequestGetItemFromPet::new, ConnectionState.IN_GAME_STATES), 
    NOT_USE_22((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    ALLIANCE_INFO((Supplier<ClientPacket>)RequestAllyInfo::new, ConnectionState.IN_GAME_STATES), 
    CRYSTALLIZE_ITEM((Supplier<ClientPacket>)RequestCrystallizeItem::new, ConnectionState.IN_GAME_STATES), 
    NOT_USE_19((Supplier<ClientPacket>)RequestPrivateStoreManageSell::new, ConnectionState.IN_GAME_STATES), 
    PRIVATE_STORE_LIST_SET((Supplier<ClientPacket>)SetPrivateStoreListSell::new, ConnectionState.IN_GAME_STATES), 
    PRIVATE_STORE_MANAGE_CANCEL((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    NOT_USE_41((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    SOCIAL_ACTION((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    CHANGE_MOVE_TYPE((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    CHANGE_WAIT_TYPE((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    SELL_LIST((Supplier<ClientPacket>)RequestSellItem::new, ConnectionState.IN_GAME_STATES), 
    MAGIC_SKILL_LIST((Supplier<ClientPacket>)RequestMagicSkillList::new, ConnectionState.IN_GAME_STATES), 
    MAGIC_SKILL_USE((Supplier<ClientPacket>)RequestMagicSkillUse::new, ConnectionState.IN_GAME_STATES), 
    APPEARING((Supplier<ClientPacket>)Appearing::new, ConnectionState.IN_GAME_STATES), 
    WAREHOUSE_DEPOSIT_LIST((Supplier<ClientPacket>)SendWareHouseDepositList::new, ConnectionState.IN_GAME_STATES), 
    WAREHOUSE_WITHDRAW_LIST((Supplier<ClientPacket>)SendWareHouseWithDrawList::new, ConnectionState.IN_GAME_STATES), 
    SHORTCUT_REG((Supplier<ClientPacket>)RequestShortCutReg::new, ConnectionState.IN_GAME_STATES), 
    NOT_USE_3((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    DEL_SHORTCUT((Supplier<ClientPacket>)RequestShortCutDel::new, ConnectionState.IN_GAME_STATES), 
    BUY_LIST((Supplier<ClientPacket>)RequestBuyItem::new, ConnectionState.IN_GAME_STATES), 
    NOT_USE_2((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    JOIN_PARTY((Supplier<ClientPacket>)RequestJoinParty::new, ConnectionState.IN_GAME_STATES), 
    ANSWER_JOIN_PARTY((Supplier<ClientPacket>)RequestAnswerJoinParty::new, ConnectionState.IN_GAME_STATES), 
    WITHDRAWAL_PARTY((Supplier<ClientPacket>)RequestWithDrawalParty::new, ConnectionState.IN_GAME_STATES), 
    OUST_PARTY_MEMBER((Supplier<ClientPacket>)RequestOustPartyMember::new, ConnectionState.IN_GAME_STATES), 
    DISMISS_PARTY((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    CAN_NOT_MOVE_ANYMORE((Supplier<ClientPacket>)CannotMoveAnymore::new, ConnectionState.IN_GAME_STATES), 
    TARGET_UNSELECTED((Supplier<ClientPacket>)RequestTargetCanceld::new, ConnectionState.IN_GAME_STATES), 
    SAY2((Supplier<ClientPacket>)Say2::new, ConnectionState.IN_GAME_STATES), 
    NOT_USE_42((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    NOT_USE_4((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    NOT_USE_5((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    PLEDGE_REQ_SHOW_MEMBER_LIST_OPEN((Supplier<ClientPacket>)RequestPledgeMemberList::new, ConnectionState.IN_GAME_STATES), 
    NOT_USE_6((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    NOT_USE_7((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    SKILL_LIST((Supplier<ClientPacket>)RequestSkillList::new, ConnectionState.IN_GAME_STATES), 
    NOT_USE_8((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    MOVE_WITH_DELTA((Supplier<ClientPacket>)MoveWithDelta::new, ConnectionState.IN_GAME_STATES), 
    GETON_VEHICLE((Supplier<ClientPacket>)RequestGetOnVehicle::new, ConnectionState.IN_GAME_STATES), 
    GETOFF_VEHICLE((Supplier<ClientPacket>)RequestGetOffVehicle::new, ConnectionState.IN_GAME_STATES), 
    TRADE_START((Supplier<ClientPacket>)AnswerTradeRequest::new, ConnectionState.IN_GAME_STATES), 
    ICON_ACTION((Supplier<ClientPacket>)RequestActionUse::new, ConnectionState.IN_GAME_STATES), 
    RESTART((Supplier<ClientPacket>)RequestRestart::new, ConnectionState.IN_GAME_STATES), 
    NOT_USE_9((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    VALIDATE_POSITION((Supplier<ClientPacket>)ValidatePosition::new, ConnectionState.IN_GAME_STATES), 
    SEK_COSTUME((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    START_ROTATING((Supplier<ClientPacket>)StartRotating::new, ConnectionState.IN_GAME_STATES), 
    FINISH_ROTATING((Supplier<ClientPacket>)FinishRotating::new, ConnectionState.IN_GAME_STATES), 
    NOT_USE_15((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    SHOW_BOARD((Supplier<ClientPacket>)RequestShowBoard::new, ConnectionState.IN_GAME_STATES), 
    REQUEST_ENCHANT_ITEM((Supplier<ClientPacket>)RequestEnchantItem::new, ConnectionState.IN_GAME_STATES), 
    DESTROY_ITEM((Supplier<ClientPacket>)RequestDestroyItem::new, ConnectionState.IN_GAME_STATES), 
    TARGET_USER_FROM_MENU((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    QUESTLIST((Supplier<ClientPacket>)RequestQuestList::new, ConnectionState.IN_GAME_STATES), 
    DESTROY_QUEST((Supplier<ClientPacket>)RequestQuestAbort::new, ConnectionState.IN_GAME_STATES), 
    NOT_USE_16((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    PLEDGE_INFO((Supplier<ClientPacket>)RequestPledgeInfo::new, ConnectionState.IN_GAME_STATES), 
    PLEDGE_EXTENDED_INFO((Supplier<ClientPacket>)RequestPledgeExtendedInfo::new, ConnectionState.IN_GAME_STATES), 
    PLEDGE_CREST((Supplier<ClientPacket>)RequestPledgeCrest::new, ConnectionState.IN_GAME_STATES), 
    NOT_USE_17((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    NOT_USE_18((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    L2_FRIEND_LIST(PacketFactory.DISCARD, ConnectionState.IN_GAME_STATES), 
    L2_FRIEND_SAY((Supplier<ClientPacket>)RequestSendFriendMsg::new, ConnectionState.IN_GAME_STATES), 
    OPEN_MINIMAP((Supplier<ClientPacket>)RequestShowMiniMap::new, ConnectionState.IN_GAME_STATES), 
    MSN_CHAT_LOG((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    RELOAD((Supplier<ClientPacket>)RequestRecordInfo::new, ConnectionState.IN_GAME_STATES), 
    HENNA_EQUIP((Supplier<ClientPacket>)RequestHennaEquip::new, ConnectionState.IN_GAME_STATES), 
    HENNA_UNEQUIP_LIST((Supplier<ClientPacket>)RequestHennaRemoveList::new, ConnectionState.IN_GAME_STATES), 
    HENNA_UNEQUIP_INFO((Supplier<ClientPacket>)RequestHennaItemRemoveInfo::new, ConnectionState.IN_GAME_STATES), 
    HENNA_UNEQUIP((Supplier<ClientPacket>)RequestHennaRemove::new, ConnectionState.IN_GAME_STATES), 
    ACQUIRE_SKILL_INFO((Supplier<ClientPacket>)RequestAcquireSkillInfo::new, ConnectionState.IN_GAME_STATES), 
    SYS_CMD_2((Supplier<ClientPacket>)SendBypassBuildCmd::new, ConnectionState.IN_GAME_STATES), 
    MOVE_TO_LOCATION_IN_VEHICLE((Supplier<ClientPacket>)RequestMoveToLocationInVehicle::new, ConnectionState.IN_GAME_STATES), 
    CAN_NOT_MOVE_ANYMORE_IN_VEHICLE((Supplier<ClientPacket>)CannotMoveAnymoreInVehicle::new, ConnectionState.IN_GAME_STATES), 
    FRIEND_ADD_REQUEST((Supplier<ClientPacket>)RequestFriendInvite::new, ConnectionState.IN_GAME_STATES), 
    FRIEND_ADD_REPLY((Supplier<ClientPacket>)RequestAnswerFriendInvite::new, ConnectionState.IN_GAME_STATES), 
    FRIEND_LIST((Supplier<ClientPacket>)RequestFriendList::new, ConnectionState.IN_GAME_STATES), 
    FRIEND_REMOVE((Supplier<ClientPacket>)RequestFriendDel::new, ConnectionState.IN_GAME_STATES), 
    RESTORE_CHARACTER((Supplier<ClientPacket>)CharacterRestore::new, ConnectionState.AUTHENTICATED_STATES), 
    REQ_ACQUIRE_SKILL((Supplier<ClientPacket>)RequestAcquireSkill::new, ConnectionState.IN_GAME_STATES), 
    RESTART_POINT((Supplier<ClientPacket>)RequestRestartPoint::new, ConnectionState.IN_GAME_STATES), 
    GM_COMMAND_TYPE((Supplier<ClientPacket>)RequestGMCommand::new, ConnectionState.IN_GAME_STATES), 
    LIST_PARTY_WAITING((Supplier<ClientPacket>)RequestPartyMatchConfig::new, ConnectionState.IN_GAME_STATES), 
    MANAGE_PARTY_ROOM((Supplier<ClientPacket>)RequestPartyMatchList::new, ConnectionState.IN_GAME_STATES), 
    JOIN_PARTY_ROOM((Supplier<ClientPacket>)RequestPartyMatchDetail::new, ConnectionState.IN_GAME_STATES), 
    NOT_USE_20((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    PRIVATE_STORE_BUY_LIST_SEND((Supplier<ClientPacket>)RequestPrivateStoreBuy::new, ConnectionState.IN_GAME_STATES), 
    NOT_USE_21((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    TUTORIAL_LINK_HTML((Supplier<ClientPacket>)RequestTutorialLinkHtml::new, ConnectionState.IN_GAME_STATES), 
    TUTORIAL_PASS_CMD_TO_SERVER((Supplier<ClientPacket>)RequestTutorialPassCmdToServer::new, ConnectionState.IN_GAME_STATES), 
    TUTORIAL_MARK_PRESSED((Supplier<ClientPacket>)RequestTutorialQuestionMark::new, ConnectionState.IN_GAME_STATES), 
    TUTORIAL_CLIENT_EVENT((Supplier<ClientPacket>)RequestTutorialClientEvent::new, ConnectionState.IN_GAME_STATES), 
    PETITION((Supplier<ClientPacket>)RequestPetition::new, ConnectionState.IN_GAME_STATES), 
    PETITION_CANCEL((Supplier<ClientPacket>)RequestPetitionCancel::new, ConnectionState.IN_GAME_STATES), 
    GMLIST((Supplier<ClientPacket>)RequestGmList::new, ConnectionState.IN_GAME_STATES), 
    JOIN_ALLIANCE((Supplier<ClientPacket>)RequestJoinAlly::new, ConnectionState.IN_GAME_STATES), 
    ANSWER_JOIN_ALLIANCE((Supplier<ClientPacket>)RequestAnswerJoinAlly::new, ConnectionState.IN_GAME_STATES), 
    WITHDRAW_ALLIANCE((Supplier<ClientPacket>)AllyLeave::new, ConnectionState.IN_GAME_STATES), 
    OUST_ALLIANCE_MEMBER_PLEDGE((Supplier<ClientPacket>)AllyDismiss::new, ConnectionState.IN_GAME_STATES), 
    DISMISS_ALLIANCE((Supplier<ClientPacket>)RequestDismissAlly::new, ConnectionState.IN_GAME_STATES), 
    SET_ALLIANCE_CREST((Supplier<ClientPacket>)RequestSetAllyCrest::new, ConnectionState.IN_GAME_STATES), 
    ALLIANCE_CREST((Supplier<ClientPacket>)RequestAllyCrest::new, ConnectionState.IN_GAME_STATES), 
    CHANGE_PET_NAME((Supplier<ClientPacket>)RequestChangePetName::new, ConnectionState.IN_GAME_STATES), 
    PET_USE_ITEM((Supplier<ClientPacket>)RequestPetUseItem::new, ConnectionState.IN_GAME_STATES), 
    GIVE_ITEM_TO_PET((Supplier<ClientPacket>)RequestGiveItemToPet::new, ConnectionState.IN_GAME_STATES), 
    PRIVATE_STORE_QUIT((Supplier<ClientPacket>)RequestPrivateStoreQuitSell::new, ConnectionState.IN_GAME_STATES), 
    PRIVATE_STORE_SET_MSG((Supplier<ClientPacket>)SetPrivateStoreMsgSell::new, ConnectionState.IN_GAME_STATES), 
    PET_GET_ITEM((Supplier<ClientPacket>)RequestPetGetItem::new, ConnectionState.IN_GAME_STATES), 
    NOT_USE_23((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    PRIVATE_STORE_BUY_LIST_SET((Supplier<ClientPacket>)SetPrivateStoreListBuy::new, ConnectionState.IN_GAME_STATES), 
    PRIVATE_STORE_BUY_MANAGE_CANCEL((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    PRIVATE_STORE_BUY_QUIT((Supplier<ClientPacket>)RequestPrivateStoreQuitBuy::new, ConnectionState.IN_GAME_STATES), 
    PRIVATE_STORE_BUY_SET_MSG((Supplier<ClientPacket>)SetPrivateStoreMsgBuy::new, ConnectionState.IN_GAME_STATES), 
    NOT_USE_24((Supplier<ClientPacket>)SetPrivateStoreMsgBuy::new, ConnectionState.IN_GAME_STATES), 
    PRIVATE_STORE_BUY_BUY_LIST_SEND((Supplier<ClientPacket>)RequestPrivateStoreSell::new, ConnectionState.IN_GAME_STATES), 
    NOT_USE_25((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    NOT_USE_26((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    NOT_USE_27((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    NOT_USE_28((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    NOT_USE_29((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    NOT_USE_30((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    REQUEST_SKILL_COOL_TIME((Supplier<ClientPacket>)RequestSkillCoolTime::new, ConnectionState.JOINING_GAME_AND_IN_GAME), 
    REQUEST_PACKAGE_SENDABLE_ITEM_LIST((Supplier<ClientPacket>)RequestPackageSendableItemList::new, ConnectionState.IN_GAME_STATES), 
    REQUEST_PACKAGE_SEND((Supplier<ClientPacket>)RequestPackageSend::new, ConnectionState.IN_GAME_STATES), 
    BLOCK_PACKET((Supplier<ClientPacket>)RequestBlock::new, ConnectionState.IN_GAME_STATES), 
    CASTLE_SIEGE_INFO((Supplier<ClientPacket>)RequestSiegeInfo::new, ConnectionState.IN_GAME_STATES), 
    CASTLE_SIEGE_ATTACKER_LIST((Supplier<ClientPacket>)RequestSiegeAttackerList::new, ConnectionState.IN_GAME_STATES), 
    CASTLE_SIEGE_DEFENDER_LIST((Supplier<ClientPacket>)RequestSiegeDefenderList::new, ConnectionState.IN_GAME_STATES), 
    JOIN_CASTLE_SIEGE((Supplier<ClientPacket>)RequestJoinSiege::new, ConnectionState.IN_GAME_STATES), 
    CONFIRM_CASTLE_SIEGE_WAITING_LIST((Supplier<ClientPacket>)RequestConfirmSiegeWaitingList::new, ConnectionState.IN_GAME_STATES), 
    SET_CASTLE_SIEGE_TIME((Supplier<ClientPacket>)RequestSetCastleSiegeTime::new, ConnectionState.IN_GAME_STATES), 
    MULTI_SELL_CHOOSE((Supplier<ClientPacket>)MultiSellChoose::new, ConnectionState.IN_GAME_STATES), 
    NET_PING((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    REMAIN_TIME((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    USER_CMD_BYPASS((Supplier<ClientPacket>)BypassUserCmd::new, ConnectionState.IN_GAME_STATES), 
    SNOOP_QUIT((Supplier<ClientPacket>)SnoopQuit::new, ConnectionState.IN_GAME_STATES), 
    RECIPE_BOOK_OPEN((Supplier<ClientPacket>)RequestRecipeBookOpen::new, ConnectionState.IN_GAME_STATES), 
    RECIPE_ITEM_DELETE((Supplier<ClientPacket>)RequestRecipeBookDestroy::new, ConnectionState.IN_GAME_STATES), 
    RECIPE_ITEM_MAKE_INFO((Supplier<ClientPacket>)RequestRecipeItemMakeInfo::new, ConnectionState.IN_GAME_STATES), 
    RECIPE_ITEM_MAKE_SELF((Supplier<ClientPacket>)RequestRecipeItemMakeSelf::new, ConnectionState.IN_GAME_STATES), 
    NOT_USE_31((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    RECIPE_SHOP_MESSAGE_SET((Supplier<ClientPacket>)RequestRecipeShopMessageSet::new, ConnectionState.IN_GAME_STATES), 
    RECIPE_SHOP_LIST_SET((Supplier<ClientPacket>)RequestRecipeShopListSet::new, ConnectionState.IN_GAME_STATES), 
    RECIPE_SHOP_MANAGE_QUIT((Supplier<ClientPacket>)RequestRecipeShopManageQuit::new, ConnectionState.IN_GAME_STATES), 
    RECIPE_SHOP_MANAGE_CANCEL((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    RECIPE_SHOP_MAKE_INFO((Supplier<ClientPacket>)RequestRecipeShopMakeInfo::new, ConnectionState.IN_GAME_STATES), 
    RECIPE_SHOP_MAKE_DO((Supplier<ClientPacket>)RequestRecipeShopMakeItem::new, ConnectionState.IN_GAME_STATES), 
    RECIPE_SHOP_SELL_LIST((Supplier<ClientPacket>)RequestRecipeShopManagePrev::new, ConnectionState.IN_GAME_STATES), 
    OBSERVER_END((Supplier<ClientPacket>)ObserverReturn::new, ConnectionState.IN_GAME_STATES), 
    VOTE_SOCIALITY((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    HENNA_ITEM_LIST((Supplier<ClientPacket>)RequestHennaItemList::new, ConnectionState.IN_GAME_STATES), 
    HENNA_ITEM_INFO((Supplier<ClientPacket>)RequestHennaItemInfo::new, ConnectionState.IN_GAME_STATES), 
    BUY_SEED((Supplier<ClientPacket>)RequestBuySeed::new, ConnectionState.IN_GAME_STATES), 
    CONFIRM_DLG((Supplier<ClientPacket>)DlgAnswer::new, ConnectionState.IN_GAME_STATES), 
    BUY_PREVIEW_LIST((Supplier<ClientPacket>)RequestPreviewItem::new, ConnectionState.IN_GAME_STATES), 
    SSQ_STATUS((Supplier<ClientPacket>)null, ConnectionState.IN_GAME_STATES), 
    PETITION_VOTE((Supplier<ClientPacket>)RequestPetitionFeedback::new, ConnectionState.IN_GAME_STATES), 
    NOT_USE_33((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    GAMEGUARD_REPLY((Supplier<ClientPacket>)GameGuardReply::new, ConnectionState.IN_GAME_STATES), 
    MANAGE_PLEDGE_POWER((Supplier<ClientPacket>)RequestPledgePower::new, ConnectionState.IN_GAME_STATES), 
    MAKE_MACRO((Supplier<ClientPacket>)RequestMakeMacro::new, ConnectionState.IN_GAME_STATES), 
    DELETE_MACRO((Supplier<ClientPacket>)RequestDeleteMacro::new, ConnectionState.IN_GAME_STATES), 
    NOT_USE_32((Supplier<ClientPacket>)IncomingPackets.DISCARD, ConnectionState.IN_GAME_STATES), 
    EX((Supplier<ClientPacket>)null, true, ConnectionState.ALL);
    
    public static final IncomingPackets[] PACKET_ARRAY;
    private final Supplier<ClientPacket> incomingPacketFactory;
    private final EnumSet<ConnectionState> connectionStates;
    private final boolean hasExtension;
    
    private IncomingPackets(final Supplier<ClientPacket> incomingPacketFactory, final boolean hasExtension, final EnumSet<ConnectionState> connectionStates) {
        this.incomingPacketFactory = Objects.requireNonNullElse(incomingPacketFactory, IncomingPackets.NULL_PACKET_SUPLIER);
        this.connectionStates = connectionStates;
        this.hasExtension = hasExtension;
    }
    
    private IncomingPackets(final Supplier<ClientPacket> incomingPacketFactory, final EnumSet<ConnectionState> connectionStates) {
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
    public EnumSet<ConnectionState> getConnectionStates() {
        return this.connectionStates;
    }
    
    @Override
    public boolean canHandleState(final ConnectionState connectionState) {
        return this.connectionStates.contains(connectionState);
    }
    
    @Override
    public boolean hasExtension() {
        return this.hasExtension;
    }
    
    @Override
    public PacketFactory handleExtension(final ReadableBuffer buffer) {
        final int exPacketId = Short.toUnsignedInt(buffer.readShort());
        if (exPacketId >= ExIncomingPackets.PACKET_ARRAY.length) {
            return IncomingPackets.NULLABLE_PACKET_FACTORY;
        }
        return Objects.requireNonNullElse(ExIncomingPackets.PACKET_ARRAY[exPacketId], IncomingPackets.NULLABLE_PACKET_FACTORY);
    }
    
    static {
        PACKET_ARRAY = values();
    }
}
