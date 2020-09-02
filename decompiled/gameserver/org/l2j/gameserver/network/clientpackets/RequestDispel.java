// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.network.GameClient;

public class RequestDispel extends ClientPacket
{
    private int _objectId;
    private int _skillId;
    private int _skillLevel;
    private int _skillSubLevel;
    
    public void readImpl() {
        this._objectId = this.readInt();
        this._skillId = this.readInt();
        this._skillLevel = this.readShort();
        this._skillSubLevel = this.readShort();
    }
    
    public void runImpl() {
        if (this._skillId <= 0 || this._skillLevel <= 0) {
            return;
        }
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final Skill skill = SkillEngine.getInstance().getSkill(this._skillId, this._skillLevel);
        if (skill == null) {
            return;
        }
        if (!skill.canBeDispelled() || skill.isStayAfterDeath() || skill.isDebuff()) {
            return;
        }
        if (skill.getAbnormalType() == AbnormalType.TRANSFORM) {
            return;
        }
        if (skill.isDance() && !Config.DANCE_CANCEL_BUFF) {
            return;
        }
        if (activeChar.getObjectId() == this._objectId) {
            activeChar.stopSkillEffects(true, this._skillId);
        }
        else {
            final Summon pet = activeChar.getPet();
            if (pet != null && pet.getObjectId() == this._objectId) {
                pet.stopSkillEffects(true, this._skillId);
            }
            final Summon servitor = activeChar.getServitor(this._objectId);
            if (servitor != null) {
                servitor.stopSkillEffects(true, this._skillId);
            }
        }
    }
}
