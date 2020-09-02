// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Servitor;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Summon;

public class PetStatusUpdate extends ServerPacket
{
    private final Summon summon;
    private int maxFed;
    private int currentFed;
    
    public PetStatusUpdate(final Summon summon) {
        this.summon = summon;
        if (GameUtils.isPet(this.summon)) {
            final Pet pet = (Pet)this.summon;
            this.currentFed = pet.getCurrentFed();
            this.maxFed = pet.getMaxFed();
        }
        else if (this.summon.isServitor()) {
            final Servitor sum = (Servitor)this.summon;
            this.currentFed = sum.getLifeTimeRemaining();
            this.maxFed = sum.getLifeTime();
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.PET_STATUS_UPDATE);
        this.writeInt(this.summon.getSummonType());
        this.writeInt(this.summon.getObjectId());
        this.writeInt(this.summon.getX());
        this.writeInt(this.summon.getY());
        this.writeInt(this.summon.getZ());
        this.writeString((CharSequence)this.summon.getTitle());
        this.writeInt(this.currentFed);
        this.writeInt(this.maxFed);
        this.writeInt((int)this.summon.getCurrentHp());
        this.writeInt(this.summon.getMaxHp());
        this.writeInt((int)this.summon.getCurrentMp());
        this.writeInt(this.summon.getMaxMp());
        this.writeInt(this.summon.getLevel());
        this.writeLong(this.summon.getStats().getExp());
        this.writeLong(this.summon.getExpForThisLevel());
        this.writeLong(this.summon.getExpForNextLevel());
        this.writeInt(0);
    }
}
