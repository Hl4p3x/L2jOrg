// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.enums.MountType;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public class Feed extends AbstractEffect
{
    private final int normal;
    private final int ride;
    private final int wyvern;
    
    private Feed(final StatsSet params) {
        this.normal = params.getInt("normal", 0);
        this.ride = params.getInt("ride", 0);
        this.wyvern = params.getInt("wyvern", 0);
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (GameUtils.isPet((WorldObject)effected)) {
            final Pet pet = (Pet)effected;
            pet.setCurrentFed(pet.getCurrentFed() + this.normal * Config.PET_FOOD_RATE);
        }
        else if (GameUtils.isPlayer((WorldObject)effected)) {
            final Player player = effected.getActingPlayer();
            if (player.getMountType() == MountType.WYVERN) {
                player.setCurrentFeed(player.getCurrentFeed() + this.wyvern);
            }
            else {
                player.setCurrentFeed(player.getCurrentFeed() + this.ride);
            }
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new Feed(data);
        }
        
        public String effectName() {
            return "feed";
        }
    }
}
