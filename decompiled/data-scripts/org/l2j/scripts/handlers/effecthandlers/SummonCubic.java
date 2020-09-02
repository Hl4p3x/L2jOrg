// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.actor.templates.CubicTemplate;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExUserInfoCubic;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.data.xml.impl.CubicData;
import java.util.function.Consumer;
import org.l2j.gameserver.model.cubic.CubicInstance;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.stats.Stat;
import java.util.Objects;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.slf4j.Logger;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class SummonCubic extends AbstractEffect
{
    private static final Logger LOGGER;
    private final int cubicId;
    private final int cubicLvl;
    
    private SummonCubic(final StatsSet params) {
        this.cubicId = params.getInt("id", -1);
        this.cubicLvl = (int)params.getDouble("power", 0.0);
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (!GameUtils.isPlayer((WorldObject)effected) || effected.isAlikeDead() || effected.getActingPlayer().inObserverMode()) {
            return;
        }
        if (this.cubicId < 0) {
            SummonCubic.LOGGER.warn("Invalid Cubic ID: {} in skill ID: {}", (Object)this.cubicId, (Object)skill.getId());
            return;
        }
        final Player player = effected.getActingPlayer();
        if (player.inObserverMode() || player.isMounted()) {
            return;
        }
        final CubicInstance cubic = player.getCubicById(this.cubicId);
        if (Objects.nonNull(cubic)) {
            if (cubic.getTemplate().getLevel() > this.cubicLvl) {
                return;
            }
            cubic.deactivate();
        }
        else {
            final double allowedCubicCount = player.getStats().getValue(Stat.MAX_CUBIC, 1.0);
            final int currentCubicCount = player.getCubics().size();
            if (currentCubicCount >= allowedCubicCount) {
                player.getCubics().values().stream().skip((int)(currentCubicCount * Rnd.nextDouble())).findAny().ifPresent(CubicInstance::deactivate);
            }
        }
        final CubicTemplate template = CubicData.getInstance().getCubicTemplate(this.cubicId, this.cubicLvl);
        if (Objects.isNull(template)) {
            SummonCubic.LOGGER.warn(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, this.cubicId, this.cubicLvl));
            return;
        }
        player.addCubic(new CubicInstance(player, effector.getActingPlayer(), template));
        player.sendPacket(new ServerPacket[] { (ServerPacket)new ExUserInfoCubic(player) });
        player.broadcastCharInfo();
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)SummonCubic.class);
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new SummonCubic(data);
        }
        
        public String effectName() {
            return "summon-cubic";
        }
    }
}
