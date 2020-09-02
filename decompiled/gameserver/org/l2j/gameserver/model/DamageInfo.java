// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import java.util.function.Function;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Creature;
import java.util.Objects;
import org.l2j.gameserver.engine.skill.api.Skill;

public class DamageInfo
{
    private final int attackerId;
    private final int skillId;
    private final double damage;
    private final DamageType damageType;
    
    private DamageInfo(final int attackerId, final Skill skill, final double damage, final DamageType damageType) {
        this.attackerId = attackerId;
        this.skillId = (Objects.nonNull(skill) ? skill.getId() : 0);
        this.damage = damage;
        this.damageType = damageType;
    }
    
    public short attackerType() {
        return 0;
    }
    
    public int attackerId() {
        return this.attackerId;
    }
    
    public String attackerName() {
        return "";
    }
    
    public int skillId() {
        return this.skillId;
    }
    
    public double damage() {
        return this.damage;
    }
    
    public short damageType() {
        return (short)this.damageType.clientId;
    }
    
    public static DamageInfo of(final Creature attacker, final Skill skill, final double damage, final DamageType damageType) {
        final Player player;
        if (attacker instanceof Player && (player = (Player)attacker) == attacker) {
            return new PlayerDamage(player, skill, damage, damageType);
        }
        final Npc npc;
        if (attacker instanceof Npc && (npc = (Npc)attacker) == attacker) {
            return new NpcDamage(npc, skill, damage, damageType);
        }
        return new DamageInfo(0, skill, damage, damageType);
    }
    
    public static final class NpcDamage extends DamageInfo
    {
        private NpcDamage(final Npc npc, final Skill skill, final double damage, final DamageType damageType) {
            super(npc.getId(), skill, damage, damageType);
        }
        
        @Override
        public short attackerType() {
            return 1;
        }
    }
    
    public static final class PlayerDamage extends DamageInfo
    {
        private final String playerName;
        private final String clanName;
        
        private PlayerDamage(final Player player, final Skill skill, final double damage, final DamageType damageType) {
            super(player.getObjectId(), skill, damage, damageType);
            this.playerName = player.getAppearance().getVisibleName();
            this.clanName = Util.emptyIfNullOrElse((Object)player.getClan(), (Function)Clan::getName);
        }
        
        @Override
        public short attackerType() {
            return 2;
        }
        
        @Override
        public String attackerName() {
            return this.playerName;
        }
        
        public String clanName() {
            return this.clanName;
        }
    }
    
    public enum DamageType
    {
        OTHER(0), 
        ATTACK(1), 
        FALL(2), 
        DROWN(3), 
        ZONE(6), 
        POISON(8), 
        TRANSFERED_DAMAGE(9), 
        REFLECT(14);
        
        private final int clientId;
        
        private DamageType(final int clientId) {
            this.clientId = clientId;
        }
    }
}
