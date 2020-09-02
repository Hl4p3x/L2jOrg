// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;

public class HairAccessorySet extends AbstractEffect
{
    private HairAccessorySet() {
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (!GameUtils.isPlayer((WorldObject)effector) || !GameUtils.isPlayer((WorldObject)effected) || effected.isAlikeDead()) {
            return;
        }
        final Player player = effected.getActingPlayer();
        player.setHairAccessoryEnabled(!player.isHairAccessoryEnabled());
        player.broadcastUserInfo(new UserInfoType[] { UserInfoType.APPAREANCE });
        player.sendPacket(player.isHairAccessoryEnabled() ? SystemMessageId.HAIR_ACCESSORIES_WILL_BE_DISPLAYED_FROM_NOW_ON : SystemMessageId.HAIR_ACCESSORIES_WILL_NO_LONGER_BE_DISPLAYED);
    }
    
    public static class Factory implements SkillEffectFactory
    {
        private static final HairAccessorySet INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "HairAccessorySet";
        }
        
        static {
            INSTANCE = new HairAccessorySet();
        }
    }
}
