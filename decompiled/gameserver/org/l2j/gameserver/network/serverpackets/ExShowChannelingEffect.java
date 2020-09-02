// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.Creature;

public class ExShowChannelingEffect extends ServerPacket
{
    private final Creature _caster;
    private final Creature _target;
    private final int _state;
    
    public ExShowChannelingEffect(final Creature caster, final Creature target, final int state) {
        this._caster = caster;
        this._target = target;
        this._state = state;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SHOW_CHANNELING_EFFECT);
        this.writeInt(this._caster.getObjectId());
        this.writeInt(this._target.getObjectId());
        this.writeInt(this._state);
    }
}
