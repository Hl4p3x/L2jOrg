// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.model.Fishing;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.network.serverpackets.fishing.ExAutoFishAvailable;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.util.Objects;
import org.l2j.gameserver.model.actor.instance.Player;
import java.lang.ref.WeakReference;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.zone.Zone;

public class FishingZone extends Zone
{
    public FishingZone(final int id) {
        super(id);
    }
    
    @Override
    protected void onEnter(final Creature creature) {
        if (GameUtils.isPlayer(creature) && (Config.ALLOW_FISHING || creature.canOverrideCond(PcCondOverride.ZONE_CONDITIONS)) && !creature.isInsideZone(ZoneType.FISHING)) {
            final WeakReference<Player> weakPlayer = new WeakReference<Player>(creature.getActingPlayer());
            ThreadPool.execute((Runnable)new Runnable() {
                @Override
                public void run() {
                    final Player player = (Player)weakPlayer.get();
                    if (Objects.nonNull(player)) {
                        if (player.isInsideZone(ZoneType.FISHING)) {
                            final Fishing fishing = player.getFishing();
                            if (fishing.canFish() && !fishing.isFishing()) {
                                player.sendPacket(fishing.isAtValidLocation() ? ExAutoFishAvailable.YES : ExAutoFishAvailable.NO);
                            }
                            ThreadPool.schedule((Runnable)this, 1500L);
                        }
                        else {
                            player.sendPacket(ExAutoFishAvailable.NO);
                        }
                    }
                }
            });
            creature.setInsideZone(ZoneType.FISHING, true);
        }
    }
    
    @Override
    protected void onExit(final Creature character) {
        if (GameUtils.isPlayer(character)) {
            character.setInsideZone(ZoneType.FISHING, false);
            character.sendPacket(ExAutoFishAvailable.NO);
        }
    }
}
