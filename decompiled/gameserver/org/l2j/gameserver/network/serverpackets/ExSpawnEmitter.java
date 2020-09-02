// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.Creature;

public class ExSpawnEmitter extends ServerPacket
{
    private final int attackerId;
    private final int targetId;
    private final SpawnEmitterType type;
    
    private ExSpawnEmitter(final int attackerId, final int targetId, final SpawnEmitterType type) {
        this.attackerId = attackerId;
        this.targetId = targetId;
        this.type = type;
    }
    
    public ExSpawnEmitter(final Creature attacker, final Creature target, final SpawnEmitterType type) {
        this(attacker.getObjectId(), target.getObjectId(), type);
    }
    
    public ExSpawnEmitter(final Creature creature, final SpawnEmitterType type) {
        this(creature.getObjectId(), creature.getObjectId(), type);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SPAWN_EMITTER);
        this.writeInt(this.targetId);
        this.writeInt(this.attackerId);
        this.writeInt(this.type.ordinal());
    }
    
    public enum SpawnEmitterType
    {
        BLUE_SOUL_EATEN, 
        YELLOW_UNK, 
        WHITE_SOUL, 
        BLACK_SOUL;
    }
}
