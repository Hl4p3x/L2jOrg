// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.model.actor.transform.Transform;
import org.l2j.gameserver.network.serverpackets.NpcInfo;
import org.l2j.gameserver.network.serverpackets.ServerObjectInfo;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.zone.Zone;

public class WaterZone extends Zone
{
    public WaterZone(final int id) {
        super(id);
    }
    
    @Override
    protected void onEnter(final Creature creature) {
        creature.setInsideZone(ZoneType.WATER, true);
        if (GameUtils.isPlayer(creature)) {
            final Player player2 = creature.getActingPlayer();
            if (player2.checkTransformed(transform -> !transform.canSwim())) {
                creature.stopTransformation(true);
            }
            else {
                player2.broadcastUserInfo();
            }
        }
        else if (GameUtils.isNpc(creature)) {
            World.getInstance().forEachVisibleObject(creature, Player.class, player -> {
                if (creature.getRunSpeed() == 0.0) {
                    player.sendPacket(new ServerObjectInfo((Npc)creature, player));
                }
                else {
                    player.sendPacket(new NpcInfo((Npc)creature));
                }
            });
        }
    }
    
    @Override
    protected void onExit(final Creature creature) {
        creature.setInsideZone(ZoneType.WATER, false);
        if (GameUtils.isPlayer(creature)) {
            if (!creature.isInsideZone(ZoneType.WATER)) {
                ((Player)creature).stopWaterTask();
            }
            creature.getActingPlayer().broadcastUserInfo();
        }
        else if (GameUtils.isNpc(creature)) {
            World.getInstance().forEachVisibleObject(creature, Player.class, player -> {
                if (creature.getRunSpeed() == 0.0) {
                    player.sendPacket(new ServerObjectInfo((Npc)creature, player));
                }
                else {
                    player.sendPacket(new NpcInfo((Npc)creature));
                }
            });
        }
    }
    
    public int getWaterZ() {
        return this.getArea().getHighZ();
    }
}
