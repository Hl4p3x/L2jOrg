// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.function.Consumer;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.DamageInfo;
import java.util.Collection;

public class ExDieInfo extends ServerPacket
{
    private final Collection<DamageInfo> damages;
    private final Collection<Item> drop;
    
    public ExDieInfo(final Collection<DamageInfo> damages, final Collection<Item> drop) {
        this.damages = damages;
        this.drop = drop;
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_DIE_INFO);
        this.writeShort(this.drop.size());
        this.drop.forEach(this::writeDrop);
        this.writeShort(this.damages.size());
        this.damages.forEach(this::writeDamage);
    }
    
    private void writeDrop(final Item item) {
        this.writeInt(item.getId());
        this.writeInt(item.getEnchantLevel());
        this.writeInt((int)item.getCount());
    }
    
    private void writeDamage(final DamageInfo damageInfo) {
        this.writeShort(damageInfo.attackerType());
        final DamageInfo.PlayerDamage playerDamage;
        if (damageInfo instanceof DamageInfo.PlayerDamage && (playerDamage = (DamageInfo.PlayerDamage)damageInfo) == damageInfo) {
            this.writePlayerDamage(playerDamage);
        }
        else {
            final DamageInfo.NpcDamage npcDamage;
            if (damageInfo instanceof DamageInfo.NpcDamage && (npcDamage = (DamageInfo.NpcDamage)damageInfo) == damageInfo) {
                this.writeNpcDamage(npcDamage);
            }
            else {
                this.writeOthersDamage(damageInfo);
            }
        }
        this.writeDouble(damageInfo.damage());
        this.writeShort(damageInfo.damageType());
    }
    
    private void writeNpcDamage(final DamageInfo.NpcDamage npcDamage) {
        this.writeInt(npcDamage.attackerId());
        this.writeShort(0);
        this.writeInt(npcDamage.skillId());
    }
    
    private void writePlayerDamage(final DamageInfo.PlayerDamage playerDamage) {
        this.writeString((CharSequence)playerDamage.attackerName());
        this.writeString((CharSequence)playerDamage.clanName());
        this.writeInt(playerDamage.skillId());
    }
    
    private void writeOthersDamage(final DamageInfo damageInfo) {
        this.writeInt(0);
        this.writeInt(0);
    }
}
