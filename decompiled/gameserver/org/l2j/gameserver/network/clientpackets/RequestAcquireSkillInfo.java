// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.SkillLearn;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.network.serverpackets.ExAcquireSkillInfo;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.AcquireSkillInfo;
import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.base.AcquireSkillType;
import org.slf4j.Logger;

public final class RequestAcquireSkillInfo extends ClientPacket
{
    private static final Logger LOGGER;
    private int _id;
    private int _level;
    private AcquireSkillType _skillType;
    
    public void readImpl() {
        this._id = this.readInt();
        this._level = this.readInt();
        this._skillType = AcquireSkillType.getAcquireSkillType(this.readInt());
    }
    
    public void runImpl() {
        if (this._id <= 0 || this._level <= 0) {
            RequestAcquireSkillInfo.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, RequestAcquireSkillInfo.class.getSimpleName(), this._id, this._level));
            return;
        }
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final Npc trainer = activeChar.getLastFolkNPC();
        if (this._skillType != AcquireSkillType.CLASS && (!GameUtils.isNpc(trainer) || (!trainer.canInteract(activeChar) && !activeChar.isGM()))) {
            return;
        }
        final Skill skill = SkillEngine.getInstance().getSkill(this._id, this._level);
        if (skill == null) {
            RequestAcquireSkillInfo.LOGGER.warn(invokedynamic(makeConcatWithConstants:(IILjava/lang/String;)Ljava/lang/String;, this._id, this._level, RequestAcquireSkillInfo.class.getName()));
            return;
        }
        final SkillLearn s = SkillTreesData.getInstance().getSkillLearn(this._skillType, this._id, this._level, activeChar);
        if (s == null) {
            return;
        }
        switch (this._skillType) {
            case TRANSFORM:
            case FISHING: {
                ((GameClient)this.client).sendPacket(new AcquireSkillInfo(this._skillType, s));
                break;
            }
            case CLASS: {
                ((GameClient)this.client).sendPacket(new ExAcquireSkillInfo(activeChar, s));
                break;
            }
            case PLEDGE: {
                if (!activeChar.isClanLeader()) {
                    return;
                }
                ((GameClient)this.client).sendPacket(new AcquireSkillInfo(this._skillType, s));
                break;
            }
            case SUBPLEDGE: {
                if (!activeChar.isClanLeader() || !activeChar.hasClanPrivilege(ClanPrivilege.CL_TROOPS_FAME)) {
                    return;
                }
                ((GameClient)this.client).sendPacket(new AcquireSkillInfo(this._skillType, s));
                break;
            }
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestAcquireSkillInfo.class);
    }
}
