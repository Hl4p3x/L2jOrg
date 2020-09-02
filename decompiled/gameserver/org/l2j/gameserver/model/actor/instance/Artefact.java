// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.model.DamageInfo;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.actor.Npc;

public final class Artefact extends Npc
{
    public Artefact(final NpcTemplate template) {
        super(template);
        this.setInstanceType(InstanceType.L2ArtefactInstance);
    }
    
    @Override
    public void onSpawn() {
        super.onSpawn();
        this.getCastle().registerArtefact(this);
    }
    
    @Override
    public boolean isAutoAttackable(final Creature attacker) {
        return false;
    }
    
    @Override
    public boolean canBeAttacked() {
        return false;
    }
    
    @Override
    public void onForcedAttack(final Player player) {
        player.sendPacket(ActionFailed.STATIC_PACKET);
    }
    
    @Override
    public void reduceCurrentHp(final double damage, final Creature attacker, final Skill skill, final DamageInfo.DamageType zone) {
    }
    
    @Override
    public void reduceCurrentHp(final double value, final Creature attacker, final Skill skill, final boolean isDOT, final boolean directlyToHp, final boolean critical, final boolean reflect, final DamageInfo.DamageType drown) {
    }
}
