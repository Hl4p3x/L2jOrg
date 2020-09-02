// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.enums;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import java.util.function.Function;

public enum StatusUpdateType
{
    LEVEL(1, Creature::getLevel), 
    EXP(2, creature -> Integer.valueOf((int)creature.getStats().getExp())), 
    STR(3, Creature::getSTR), 
    DEX(4, Creature::getDEX), 
    CON(5, Creature::getCON), 
    INT(6, Creature::getINT), 
    WIT(7, Creature::getWIT), 
    MEN(8, Creature::getMEN), 
    CUR_HP(9, creature -> Integer.valueOf((int)creature.getCurrentHp())), 
    MAX_HP(10, Creature::getMaxHp), 
    CUR_MP(11, creature -> Integer.valueOf((int)creature.getCurrentMp())), 
    MAX_MP(12, Creature::getMaxMp), 
    P_ATK(17, Creature::getPAtk), 
    ATK_SPD(18, Creature::getPAtkSpd), 
    P_DEF(19, Creature::getPDef), 
    EVASION(20, Creature::getEvasionRate), 
    ACCURACY(21, Creature::getAccuracy), 
    CRITICAL(22, creature -> Integer.valueOf((int)creature.getCriticalDmg(1))), 
    M_ATK(23, Creature::getMAtk), 
    CAST_SPD(24, Creature::getMAtkSpd), 
    M_DEF(25, Creature::getMDef), 
    PVP_FLAG(26, creature -> Integer.valueOf(creature.getPvpFlag())), 
    REPUTATION(27, creature -> GameUtils.isPlayer(creature) ? creature.getActingPlayer().getReputation() : 0), 
    CUR_CP(33, creature -> Integer.valueOf((int)creature.getCurrentCp())), 
    MAX_CP(34, Creature::getMaxCp);
    
    private int _clientId;
    private Function<Creature, Integer> _valueSupplier;
    
    private StatusUpdateType(final int clientId, final Function<Creature, Integer> valueSupplier) {
        this._clientId = clientId;
        this._valueSupplier = valueSupplier;
    }
    
    public int getClientId() {
        return this._clientId;
    }
    
    public int getValue(final Creature creature) {
        return this._valueSupplier.apply(creature);
    }
}
