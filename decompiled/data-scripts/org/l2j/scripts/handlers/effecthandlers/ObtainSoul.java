// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.EtcStatusUpdate;
import org.l2j.gameserver.network.serverpackets.ExSpawnEmitter;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public class ObtainSoul extends AbstractEffect
{
    private final int power;
    private final boolean isShine;
    
    private ObtainSoul(final StatsSet data) {
        this.power = data.getInt("power");
        this.isShine = data.getBoolean("is-shine");
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (!GameUtils.isPlayer((WorldObject)effected) || effected.isAlikeDead()) {
            return;
        }
        if (effected.hasAbnormalType(AbnormalType.KAMAEL_SPECIAL)) {
            return;
        }
        final Player player = effected.getActingPlayer();
        if (this.isShine) {
            final byte souls = player.getShineSouls();
            if (souls + this.power >= 100) {
                player.setShineSouls((byte)0);
                final int level = player.getSkillLevel(CommonSkill.SHINE_MASTERY.getId());
                SkillCaster.triggerCast((Creature)player, (Creature)player, SkillEngine.getInstance().getSkill(CommonSkill.SHINE_SIDE.getId(), level));
            }
            else {
                player.setShineSouls((byte)(souls + this.power));
            }
        }
        else {
            final byte souls = player.getShadowSouls();
            if (souls + this.power >= 100) {
                player.setShadowSouls((byte)0);
                final int level = player.getSkillLevel(CommonSkill.SHADOW_MASTERY.getId());
                SkillCaster.triggerCast((Creature)player, (Creature)player, SkillEngine.getInstance().getSkill(CommonSkill.SHADOW_SIDE.getId(), level));
            }
            else {
                player.setShadowSouls((byte)(souls + this.power));
            }
        }
        player.sendPacket(new ServerPacket[] { (ServerPacket)new ExSpawnEmitter((Creature)player, this.isShine ? ExSpawnEmitter.SpawnEmitterType.WHITE_SOUL : ExSpawnEmitter.SpawnEmitterType.BLACK_SOUL) });
        player.sendPacket(new ServerPacket[] { (ServerPacket)new EtcStatusUpdate(player) });
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new ObtainSoul(data);
        }
        
        public String effectName() {
            return "obtain-soul";
        }
    }
}
