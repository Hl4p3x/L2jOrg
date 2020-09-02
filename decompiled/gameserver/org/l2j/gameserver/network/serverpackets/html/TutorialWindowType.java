// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.html;

public enum TutorialWindowType
{
    STANDARD, 
    COMPOSITE;
    
    public int getId() {
        return this.ordinal() + 1;
    }
}
