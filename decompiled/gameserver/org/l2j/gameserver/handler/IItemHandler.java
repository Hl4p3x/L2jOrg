// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.handler;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.Playable;
import org.slf4j.Logger;

public interface IItemHandler
{
    public static final Logger LOGGER = LoggerFactory.getLogger(IItemHandler.class.getName());
    
    boolean useItem(final Playable playable, final Item item, final boolean forceUse);
}
