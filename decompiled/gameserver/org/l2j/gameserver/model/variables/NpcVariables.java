// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.variables;

import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;

public class NpcVariables extends AbstractVariables
{
    @Override
    public int getInt(final String key) {
        return super.getInt(key, 0);
    }
    
    @Override
    public boolean restoreMe() {
        return true;
    }
    
    @Override
    public boolean storeMe() {
        return true;
    }
    
    @Override
    public boolean deleteMe() {
        return true;
    }
    
    public Player getPlayer(final String name) {
        return this.getObject(name, Player.class);
    }
    
    public Summon getSummon(final String name) {
        return this.getObject(name, Summon.class);
    }
}
