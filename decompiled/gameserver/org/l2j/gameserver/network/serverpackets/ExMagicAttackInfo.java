// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExMagicAttackInfo extends ServerPacket
{
    public static final int CRITICAL = 1;
    public static final int CRITICAL_HEAL = 2;
    public static final int OVERHIT = 3;
    public static final int EVADED = 4;
    public static final int BLOCKED = 5;
    public static final int RESISTED = 6;
    public static final int IMMUNE = 7;
    public static final int IMMUNE2 = 8;
    private final int _caster;
    private final int _target;
    private final int _type;
    
    public ExMagicAttackInfo(final int caster, final int target, final int type) {
        this._caster = caster;
        this._target = target;
        this._type = type;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_MAGIC_ATTACK_INFO);
        this.writeInt(this._caster);
        this.writeInt(this._target);
        this.writeInt(this._type);
    }
}
