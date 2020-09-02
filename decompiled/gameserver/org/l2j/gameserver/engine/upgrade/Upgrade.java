// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.upgrade;

import org.l2j.gameserver.model.holders.ItemHolder;
import java.util.List;

public interface Upgrade
{
    List<ItemHolder> material();
    
    int item();
    
    int enchantment();
    
    long commission();
}
