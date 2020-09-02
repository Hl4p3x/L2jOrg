// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.data.xml.model.TeleportData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.data.xml.impl.TeleportEngine;
import org.l2j.gameserver.model.actor.request.TeleportRequest;
import java.util.Objects;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class Teleport extends AbstractEffect
{
    private final Location loc;
    
    private Teleport(final StatsSet params) {
        if (params.getBoolean("use-request")) {
            this.loc = null;
        }
        else {
            this.loc = new Location(params.getInt("x", 0), params.getInt("y", 0), params.getInt("z", 0));
        }
    }
    
    public EffectType getEffectType() {
        return EffectType.TELEPORT;
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (Objects.isNull(this.loc)) {
            final Player player = effected.getActingPlayer();
            final TeleportRequest request = (TeleportRequest)player.getRequest((Class)TeleportRequest.class);
            if (Objects.nonNull(request)) {
                player.removeRequest((Class)TeleportRequest.class);
                TeleportEngine.getInstance().getInfo(request.getTeleportId()).ifPresent(loc -> effected.teleToLocation((ILocational)loc.getLocation(), true, (Instance)null));
            }
        }
        else {
            effected.teleToLocation((ILocational)this.loc, true, (Instance)null);
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new Teleport(data);
        }
        
        public String effectName() {
            return "teleport";
        }
    }
}
