// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import java.util.Set;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExEnchantSkillInfo;
import java.util.Collections;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.network.GameClient;

public final class RequestExEnchantSkillInfo extends ClientPacket
{
    private int _skillId;
    private int _skillLvl;
    private int _skillSubLvl;
    
    public void readImpl() {
        this._skillId = this.readInt();
        this._skillLvl = this.readShort();
        this._skillSubLvl = this.readShort();
    }
    
    public void runImpl() {
        if (this._skillId <= 0 || this._skillLvl <= 0 || this._skillSubLvl < 0) {
            return;
        }
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final Skill skill = SkillEngine.getInstance().getSkill(this._skillId, this._skillLvl);
        if (skill == null || skill.getId() != this._skillId) {
            return;
        }
        final Set<Integer> route = Collections.emptySet();
        if (route.isEmpty()) {
            return;
        }
        final Skill playerSkill = activeChar.getKnownSkill(this._skillId);
        if (playerSkill.getLevel() != this._skillLvl || playerSkill.getSubLevel() != this._skillSubLvl) {
            return;
        }
        ((GameClient)this.client).sendPacket(new ExEnchantSkillInfo(this._skillId, this._skillLvl, this._skillSubLvl, playerSkill.getSubLevel()));
    }
}
