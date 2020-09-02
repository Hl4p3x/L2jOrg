// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExEnchantSkillInfoDetail;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.enums.SkillEnchantType;

public final class RequestExEnchantSkillInfoDetail extends ClientPacket
{
    private SkillEnchantType _type;
    private int _skillId;
    private int _skillLvl;
    private int _skillSubLvl;
    
    public void readImpl() {
        this._type = SkillEnchantType.values()[this.readInt()];
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
        activeChar.sendPacket(new ExEnchantSkillInfoDetail(this._type, this._skillId, this._skillLvl, this._skillSubLvl, activeChar));
    }
}
