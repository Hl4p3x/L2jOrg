// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.effects;

public enum EffectFlag
{
    NONE, 
    RESURRECTION_SPECIAL, 
    NOBLESS_BLESSING, 
    SILENT_MOVE, 
    PROTECTION_BLESSING, 
    RELAXING, 
    BLOCK_CONTROL, 
    CONFUSED, 
    MUTED, 
    PSYCHICAL_MUTED, 
    PSYCHICAL_ATTACK_MUTED, 
    PASSIVE, 
    DISARMED, 
    ROOTED, 
    BLOCK_ACTIONS, 
    CONDITIONAL_BLOCK_ACTIONS, 
    BETRAYED, 
    HP_BLOCK, 
    MP_BLOCK, 
    BUFF_BLOCK, 
    DEBUFF_BLOCK, 
    ABNORMAL_SHIELD, 
    BLOCK_RESURRECTION, 
    UNTARGETABLE, 
    CANNOT_ESCAPE, 
    DOUBLE_CAST, 
    ATTACK_BEHIND, 
    TARGETING_DISABLED, 
    FACEOFF, 
    PHYSICAL_SHIELD_ANGLE_ALL, 
    CHEAPSHOT, 
    IGNORE_DEATH, 
    HPCPHEAL_CRITICAL, 
    PROTECT_DEATH_PENALTY, 
    CHAT_BLOCK, 
    FAKE_DEATH, 
    DUELIST_FURY, 
    FEAR, 
    STUNNED;
    
    public long getMask() {
        return 1L << this.ordinal();
    }
}
