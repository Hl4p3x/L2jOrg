// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.eventengine.drop;

import org.l2j.gameserver.model.holders.ItemHolder;
import java.util.Collection;

public interface IEventDrop
{
    Collection<ItemHolder> calculateDrops();
}
