// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.sessionzones.TimedHuntingZoneList;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public class AddHuntingTime extends AbstractEffect
{
    private final int _zoneId;
    private final long _time;
    
    private AddHuntingTime(final StatsSet params) {
        this._zoneId = params.getInt("zoneId", 0);
        this._time = params.getLong("time", 3600000L);
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        final Player player = effected.getActingPlayer();
        if (player == null) {
            return;
        }
        final long currentTime = System.currentTimeMillis();
        long endTime = player.getHuntingZoneResetTime(this._zoneId);
        if (endTime > currentTime && endTime - currentTime + this._time >= Config.TIME_LIMITED_MAX_ADDED_TIME) {
            player.getInventory().addItem("AddHuntingTime effect refund", item.getId(), 1L, player, (Object)false);
            player.sendMessage("You cannot exceed the time zone limit.");
            return;
        }
        if (player.isInTimedHuntingZone(this._zoneId)) {
            endTime = this._time + player.getTimedHuntingZoneRemainingTime();
            player.setHuntingZoneResetTime(this._zoneId, currentTime + endTime);
            player.startTimedHuntingZone(this._zoneId, endTime);
        }
        else {
            if (endTime + Config.TIME_LIMITED_ZONE_RESET_DELAY < currentTime) {
                endTime = currentTime + Config.TIME_LIMITED_ZONE_INITIAL_TIME;
            }
            else if (endTime < currentTime) {
                endTime = currentTime;
            }
            player.setHuntingZoneResetTime(this._zoneId, endTime + this._time);
        }
        player.sendPacket(new ServerPacket[] { (ServerPacket)new TimedHuntingZoneList(player) });
    }
    
    public static final class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new AddHuntingTime(data);
        }
        
        public String effectName() {
            return "AddHuntingTime";
        }
    }
}
