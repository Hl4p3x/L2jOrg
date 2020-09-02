// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.data.database.data.SiegeClanData;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Objects;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;

public class Die extends ServerPacket
{
    private final int objectId;
    private final boolean isSweepable;
    private int flags;
    
    public Die(final Creature creature) {
        this.flags = 0;
        this.objectId = creature.getObjectId();
        this.isSweepable = (GameUtils.isAttackable(creature) && creature.isSweepActive());
        if (GameUtils.isPlayer(creature)) {
            final Player player = creature.getActingPlayer();
            final Clan clan = player.getClan();
            boolean isInCastleDefense = false;
            SiegeClanData siegeClan = null;
            final Castle castle = CastleManager.getInstance().getCastle(creature);
            if (castle != null && castle.getSiege().isInProgress()) {
                siegeClan = castle.getSiege().getAttackerClan(clan);
                isInCastleDefense = (siegeClan == null && castle.getSiege().checkIsDefender(clan));
            }
            this.flags += ((Objects.nonNull(clan) && clan.getHideoutId() > 0) ? 2 : 0);
            this.flags += (((Objects.nonNull(clan) && clan.getCastleId() > 0) || isInCastleDefense) ? 4 : 0);
            this.flags += ((Objects.nonNull(siegeClan) && !siegeClan.getFlags().isEmpty()) ? 16 : 0);
            this.flags += ((creature.getAccessLevel().allowFixedRes() || player.getInventory().haveItemForSelfResurrection()) ? 32 : 0);
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.DIE);
        this.writeInt(this.objectId);
        this.writeInt(this.flags);
        this.writeInt(0);
        this.writeInt(this.isSweepable);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeByte(0);
    }
}
