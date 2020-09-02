// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.enums;

import java.util.Iterator;
import org.l2j.gameserver.data.xml.ClanRewardManager;
import org.l2j.gameserver.model.pledge.ClanRewardBonus;
import org.l2j.gameserver.model.Clan;
import java.util.function.ToIntFunction;

public enum ClanRewardType
{
    MEMBERS_ONLINE(0, Clan::getPreviousMaxOnlinePlayers), 
    HUNTING_MONSTERS(1, Clan::getPreviousHuntingPoints), 
    ARENA(-1, Clan::getArenaProgress);
    
    final int _clientId;
    final int _mask;
    final ToIntFunction<Clan> _pointsFunction;
    
    private ClanRewardType(final int clientId, final ToIntFunction<Clan> pointsFunction) {
        this._clientId = clientId;
        this._mask = 1 << clientId;
        this._pointsFunction = pointsFunction;
    }
    
    public static int getDefaultMask() {
        int mask = 0;
        for (final ClanRewardType type : values()) {
            mask |= type.getMask();
        }
        return mask;
    }
    
    public int getClientId() {
        return this._clientId;
    }
    
    public int getMask() {
        return this._mask;
    }
    
    public ClanRewardBonus getAvailableBonus(final Clan clan) {
        ClanRewardBonus availableBonus = null;
        for (final ClanRewardBonus bonus : ClanRewardManager.getInstance().getClanRewardBonuses(this)) {
            if (bonus.getRequiredAmount() <= this._pointsFunction.applyAsInt(clan) && (availableBonus == null || availableBonus.getLevel() < bonus.getLevel())) {
                availableBonus = bonus;
            }
        }
        return availableBonus;
    }
}
