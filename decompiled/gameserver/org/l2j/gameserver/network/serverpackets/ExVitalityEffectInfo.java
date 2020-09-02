// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;

public class ExVitalityEffectInfo extends ServerPacket
{
    private final int _vitalityBonus;
    private final int _vitalityItemsRemaining;
    private final int _points;
    
    public ExVitalityEffectInfo(final Player cha) {
        this._points = cha.getVitalityPoints();
        this._vitalityBonus = (int)cha.getStats().getVitalityExpBonus() * 100;
        this._vitalityItemsRemaining = Config.VITALITY_MAX_ITEMS_ALLOWED - cha.getVitalityItemsUsed();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_VITALITY_EFFECT_INFO);
        this.writeInt(this._points);
        this.writeInt(this._vitalityBonus);
        this.writeShort((short)0);
        this.writeShort((short)this._vitalityItemsRemaining);
        this.writeShort((short)Config.VITALITY_MAX_ITEMS_ALLOWED);
    }
}
