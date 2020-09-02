// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.quest.QuestState;
import java.util.List;
import org.l2j.gameserver.network.serverpackets.ExAcquirableSkillListByClass;
import org.l2j.gameserver.model.actor.instance.Fisherman;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerSkillLearn;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.network.serverpackets.ExStorageMaxCount;
import org.l2j.gameserver.network.serverpackets.ExBasicActionList;
import org.l2j.gameserver.network.serverpackets.ShortCutInit;
import org.l2j.gameserver.model.base.SubClass;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.network.serverpackets.UserInfo;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.skills.CommonSkill;
import java.util.Iterator;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.SkillLearn;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.network.serverpackets.AcquireSkillDone;
import org.l2j.gameserver.network.serverpackets.PledgeSkillList;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.model.actor.instance.VillageMaster;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.enums.IllegalActionPunishmentType;
import org.l2j.gameserver.network.SystemMessageId;
import java.util.Objects;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.base.AcquireSkillType;
import org.slf4j.Logger;

public final class RequestAcquireSkill extends ClientPacket
{
    private static final Logger LOGGER;
    private int id;
    private int level;
    private AcquireSkillType skillType;
    private int subType;
    
    public void readImpl() {
        this.id = this.readInt();
        this.level = this.readInt();
        this.skillType = AcquireSkillType.getAcquireSkillType(this.readInt());
        if (this.skillType == AcquireSkillType.SUBPLEDGE) {
            this.subType = this.readInt();
        }
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (this.level < 1 || this.level > 1000 || this.id < 1 || this.id > 64000) {
            GameUtils.handleIllegalPlayerAction(player, "Wrong Packet Data in Acquired Skill");
            RequestAcquireSkill.LOGGER.warn("Received Wrong Packet Data in Acquired Skill - id: {} level: {} from {}", new Object[] { this.id, this.level, player });
            return;
        }
        final Npc trainer = player.getLastFolkNPC();
        if (this.skillType != AcquireSkillType.CLASS && (!GameUtils.isNpc(trainer) || (!trainer.canInteract(player) && !player.isGM()))) {
            return;
        }
        final Skill skill = SkillEngine.getInstance().getSkill(this.id, this.level);
        if (Objects.isNull(skill)) {
            RequestAcquireSkill.LOGGER.warn("Player {} is trying to learn a null skill id: {} level: {}!", new Object[] { player, this.id, this.level });
            return;
        }
        if (this.skillType != AcquireSkillType.SUBPLEDGE) {
            final int prevSkillLevel = player.getSkillLevel(this.id);
            if (prevSkillLevel == this.level) {
                RequestAcquireSkill.LOGGER.warn("Player {} is trying to learn a skill that already knows {} !", (Object)player, (Object)skill);
                return;
            }
            if (prevSkillLevel != this.level - 1) {
                player.sendPacket(SystemMessageId.THE_PREVIOUS_LEVEL_SKILL_HAS_NOT_BEEN_LEARNED);
                GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;II)Ljava/lang/String;, player, this.id, this.level), IllegalActionPunishmentType.NONE);
                return;
            }
        }
        final SkillLearn skillLearn = SkillTreesData.getInstance().getSkillLearn(this.skillType, this.id, this.level, player);
        if (Objects.isNull(skillLearn)) {
            return;
        }
        switch (this.skillType) {
            case CLASS: {
                if (this.checkPlayerSkill(player, trainer, skillLearn)) {
                    this.giveSkill(player, trainer, skill);
                    break;
                }
                break;
            }
            case TRANSFORM: {
                if (!this.canTransform(player)) {
                    player.sendPacket(SystemMessageId.YOU_HAVE_NOT_COMPLETED_THE_NECESSARY_QUEST_FOR_SKILL_ACQUISITION);
                    GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, player.getName(), this.id, this.level), IllegalActionPunishmentType.NONE);
                    return;
                }
                if (this.checkPlayerSkill(player, trainer, skillLearn)) {
                    this.giveSkill(player, trainer, skill);
                    break;
                }
                break;
            }
            case FISHING: {
                if (this.checkPlayerSkill(player, trainer, skillLearn)) {
                    this.giveSkill(player, trainer, skill);
                    break;
                }
                break;
            }
            case PLEDGE: {
                if (!player.isClanLeader()) {
                    return;
                }
                final Clan clan = player.getClan();
                final int repCost = (skillLearn.getLevelUpSp() > 2147483647L) ? Integer.MAX_VALUE : ((int)skillLearn.getLevelUpSp());
                if (clan.getReputationScore() >= repCost) {
                    if (Config.LIFE_CRYSTAL_NEEDED) {
                        for (final ItemHolder item : skillLearn.getRequiredItems()) {
                            if (!player.destroyItemByItemId("Consume", item.getId(), item.getCount(), trainer, false)) {
                                player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ITEMS_TO_LEARN_THIS_SKILL);
                                VillageMaster.showPledgeSkillList(player);
                                return;
                            }
                            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S2_S1_S_DISAPPEARED);
                            sm.addItemName(item.getId());
                            sm.addLong(item.getCount());
                            player.sendPacket(sm);
                        }
                    }
                    clan.takeReputationScore(repCost, true);
                    final SystemMessage cr = SystemMessage.getSystemMessage(SystemMessageId.S1_POINT_S_HAVE_BEEN_DEDUCTED_FROM_THE_CLAN_S_REPUTATION);
                    cr.addInt(repCost);
                    player.sendPacket(cr);
                    clan.addNewSkill(skill);
                    clan.broadcastToOnlineMembers(new PledgeSkillList(clan));
                    player.sendPacket(new AcquireSkillDone());
                    VillageMaster.showPledgeSkillList(player);
                    break;
                }
                player.sendPacket(SystemMessageId.THE_ATTEMPT_TO_ACQUIRE_THE_SKILL_HAS_FAILED_BECAUSE_OF_AN_INSUFFICIENT_CLAN_REPUTATION);
                VillageMaster.showPledgeSkillList(player);
                break;
            }
            case SUBPLEDGE: {
                if (!player.isClanLeader() || !player.hasClanPrivilege(ClanPrivilege.CL_TROOPS_FAME)) {
                    return;
                }
                final Clan clan = player.getClan();
                if (clan.getCastleId() == 0) {
                    return;
                }
                if (!clan.isLearnableSubPledgeSkill(skill, this.subType)) {
                    player.sendPacket(SystemMessageId.THIS_SQUAD_SKILL_HAS_ALREADY_BEEN_LEARNED);
                    GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, player.getName(), this.id, this.level), IllegalActionPunishmentType.NONE);
                    return;
                }
                final int repCost = (skillLearn.getLevelUpSp() > 2147483647L) ? Integer.MAX_VALUE : ((int)skillLearn.getLevelUpSp());
                if (clan.getReputationScore() < repCost) {
                    player.sendPacket(SystemMessageId.THE_ATTEMPT_TO_ACQUIRE_THE_SKILL_HAS_FAILED_BECAUSE_OF_AN_INSUFFICIENT_CLAN_REPUTATION);
                    return;
                }
                for (final ItemHolder item : skillLearn.getRequiredItems()) {
                    if (!player.destroyItemByItemId("SubSkills", item.getId(), item.getCount(), trainer, false)) {
                        player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ITEMS_TO_LEARN_THIS_SKILL);
                        return;
                    }
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S2_S1_S_DISAPPEARED);
                    sm.addItemName(item.getId());
                    sm.addLong(item.getCount());
                    player.sendPacket(sm);
                }
                if (repCost > 0) {
                    clan.takeReputationScore(repCost, true);
                    final SystemMessage cr = SystemMessage.getSystemMessage(SystemMessageId.S1_POINT_S_HAVE_BEEN_DEDUCTED_FROM_THE_CLAN_S_REPUTATION);
                    cr.addInt(repCost);
                    player.sendPacket(cr);
                }
                clan.addNewSkill(skill, this.subType);
                clan.broadcastToOnlineMembers(new PledgeSkillList(clan));
                player.sendPacket(new AcquireSkillDone());
                this.showSubUnitSkillList(player);
                break;
            }
            default: {
                RequestAcquireSkill.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/base/AcquireSkillType;)Ljava/lang/String;, this.skillType));
                break;
            }
        }
    }
    
    private boolean checkPlayerSkill(final Player player, final Npc trainer, final SkillLearn skillLearn) {
        if (skillLearn == null || skillLearn.getSkillId() != this.id || skillLearn.getSkillLevel() != this.level) {
            return false;
        }
        if (skillLearn.getGetLevel() > player.getLevel()) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_SKILL_LEVEL_REQUIREMENTS);
            GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;IIII)Ljava/lang/String;, player.getName(), player.getLevel(), this.id, this.level, skillLearn.getGetLevel()), IllegalActionPunishmentType.NONE);
            return false;
        }
        if (skillLearn.getDualClassLevel() > 0) {
            final SubClass playerDualClass = player.getDualClass();
            if (playerDualClass == null || playerDualClass.getLevel() < skillLearn.getDualClassLevel()) {
                return false;
            }
        }
        final long levelUpSp = skillLearn.getLevelUpSp();
        if (levelUpSp > 0L && levelUpSp > player.getSp()) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_SP_TO_LEARN_THIS_SKILL);
            this.showSkillList(trainer, player);
            return false;
        }
        if (!Config.DIVINE_SP_BOOK_NEEDED && this.id == CommonSkill.DIVINE_INSPIRATION.getId()) {
            return true;
        }
        if (!skillLearn.getPreReqSkills().isEmpty()) {
            for (final SkillHolder skill : skillLearn.getPreReqSkills()) {
                if (player.getSkillLevel(skill.getSkillId()) < skill.getLevel()) {
                    if (skill.getSkillId() == CommonSkill.ONYX_BEAST_TRANSFORMATION.getId()) {
                        player.sendPacket(SystemMessageId.YOU_MUST_LEARN_THE_ONYX_BEAST_SKILL_BEFORE_YOU_CAN_LEARN_FURTHER_SKILLS);
                    }
                    else {
                        player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ITEMS_TO_LEARN_THIS_SKILL);
                    }
                    return false;
                }
            }
        }
        if (!skillLearn.getRequiredItems().isEmpty()) {
            long reqItemCount = 0L;
            for (final ItemHolder item : skillLearn.getRequiredItems()) {
                reqItemCount = player.getInventory().getInventoryItemCount(item.getId(), -1);
                if (reqItemCount < item.getCount()) {
                    player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ITEMS_TO_LEARN_THIS_SKILL);
                    this.showSkillList(trainer, player);
                    return false;
                }
            }
            for (final ItemHolder itemIdCount : skillLearn.getRequiredItems()) {
                if (!player.destroyItemByItemId("SkillLearn", itemIdCount.getId(), itemIdCount.getCount(), trainer, true)) {
                    GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;IIII)Ljava/lang/String;, player.getName(), player.getLevel(), itemIdCount.getId(), this.id, this.level), IllegalActionPunishmentType.NONE);
                }
            }
        }
        if (!skillLearn.getRemoveSkills().isEmpty()) {
            Skill skillToRemove;
            skillLearn.getRemoveSkills().forEach(skillId -> {
                if (player.getSkillLevel(skillId) > 0) {
                    skillToRemove = player.getKnownSkill(skillId);
                    if (skillToRemove != null) {
                        player.removeSkill(skillToRemove, true);
                    }
                }
                return;
            });
        }
        if (levelUpSp > 0L) {
            player.setSp(player.getSp() - levelUpSp);
            final UserInfo ui = new UserInfo(player);
            ui.addComponentType(UserInfoType.CURRENT_HPMPCP_EXP_SP);
            player.sendPacket(ui);
        }
        return true;
    }
    
    private void giveSkill(final Player player, final Npc trainer, final Skill skill) {
        this.giveSkill(player, trainer, skill, true);
    }
    
    private void giveSkill(final Player player, final Npc trainer, final Skill skill, final boolean store) {
        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.LEARNED_S1_LV_S2);
        sm.addSkillName(skill);
        player.sendPacket(sm);
        player.addSkill(skill, store);
        player.sendItemList();
        player.sendPacket(new ShortCutInit());
        player.sendPacket(new ExBasicActionList(ExBasicActionList.DEFAULT_ACTION_LIST));
        player.sendSkillList(skill.getId());
        player.updateShortCuts(this.id, this.level, 0);
        this.showSkillList(trainer, player);
        if (this.id >= 1368 && this.id <= 1372) {
            player.sendPacket(new ExStorageMaxCount(player));
        }
        if (trainer != null) {
            EventDispatcher.getInstance().notifyEventAsync(new OnPlayerSkillLearn(trainer, player, skill, this.skillType), trainer);
        }
        else {
            EventDispatcher.getInstance().notifyEventAsync(new OnPlayerSkillLearn(trainer, player, skill, this.skillType), player);
        }
    }
    
    private void showSkillList(final Npc trainer, final Player player) {
        if (trainer instanceof Fisherman) {
            Fisherman.showFishSkillList(player);
        }
    }
    
    private void showSubUnitSkillList(final Player activeChar) {
        final List<SkillLearn> skills = SkillTreesData.getInstance().getAvailableSubPledgeSkills(activeChar.getClan());
        if (skills.isEmpty()) {
            activeChar.sendPacket(SystemMessageId.THERE_ARE_NO_OTHER_SKILLS_TO_LEARN);
        }
        else {
            activeChar.sendPacket(new ExAcquirableSkillListByClass(skills, AcquireSkillType.SUBPLEDGE));
        }
    }
    
    private boolean canTransform(final Player player) {
        if (Config.ALLOW_TRANSFORM_WITHOUT_QUEST) {
            return true;
        }
        final QuestState qs = player.getQuestState("Q00136_MoreThanMeetsTheEye");
        return qs != null && qs.isCompleted();
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestAcquireSkill.class);
    }
}
