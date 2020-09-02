// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.actor.instance.EffectPoint;
import org.l2j.gameserver.model.skills.targets.TargetType;
import java.util.Objects;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class SummonNpc extends AbstractEffect
{
    private int despawnDelay;
    private final int npcId;
    
    private SummonNpc(final StatsSet params) {
        this.despawnDelay = params.getInt("despawn-delay", 20000);
        this.npcId = params.getInt("npc", 0);
    }
    
    public EffectType getEffectType() {
        return EffectType.SUMMON_NPC;
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (!GameUtils.isPlayer((WorldObject)effected) || effected.isAlikeDead() || effected.getActingPlayer().inObserverMode()) {
            return;
        }
        if (this.npcId <= 0) {
            SummonNpc.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, SummonNpc.class.getSimpleName(), skill.getId()));
            return;
        }
        final Player player = effected.getActingPlayer();
        if (player.isMounted()) {
            return;
        }
        final NpcTemplate npcTemplate = NpcData.getInstance().getTemplate(this.npcId);
        if (Objects.isNull(npcTemplate)) {
            SummonNpc.LOGGER.warn("Spawn of the nonexisting NPC ID: {}, skill ID: {}", (Object)this.npcId, (Object)skill.getId());
            return;
        }
        int x = player.getX();
        int y = player.getY();
        int z = player.getZ();
        if (skill.getTargetType() == TargetType.GROUND) {
            final Location wordPosition = player.getActingPlayer().getCurrentSkillWorldPosition();
            if (Objects.nonNull(wordPosition)) {
                x = wordPosition.getX();
                y = wordPosition.getY();
                z = wordPosition.getZ();
            }
        }
        else {
            x = effected.getX();
            y = effected.getY();
            z = effected.getZ();
        }
        final String type = npcTemplate.getType();
        switch (type) {
            case "EffectPoint": {
                final EffectPoint effectPoint = new EffectPoint(npcTemplate, (Creature)player);
                effectPoint.setCurrentHp((double)effectPoint.getMaxHp());
                effectPoint.setCurrentMp((double)effectPoint.getMaxMp());
                effectPoint.setIsInvul(true);
                effectPoint.setSummoner((Creature)player);
                effectPoint.setTitle(player.getName());
                effectPoint.spawnMe(x, y, z);
                this.despawnDelay = effectPoint.getParameters().getInt("despawn_time", 0) * 1000;
                if (this.despawnDelay > 0) {
                    effectPoint.scheduleDespawn((long)this.despawnDelay);
                }
                break;
            }
            default: {
                Spawn spawn;
                try {
                    spawn = new Spawn(npcTemplate);
                }
                catch (Exception e) {
                    SummonNpc.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
                    return;
                }
                spawn.setXYZ(x, y, z);
                spawn.setHeading(player.getHeading());
                spawn.stopRespawn();
                final Npc npc = spawn.doSpawn(false);
                player.addSummonedNpc(npc);
                npc.setName(npcTemplate.getName());
                npc.setTitle(npcTemplate.getName());
                if (this.despawnDelay > 0) {
                    npc.scheduleDespawn((long)this.despawnDelay);
                }
                npc.broadcastInfo();
                break;
            }
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new SummonNpc(data);
        }
        
        public String effectName() {
            return "summon-npc";
        }
    }
}
