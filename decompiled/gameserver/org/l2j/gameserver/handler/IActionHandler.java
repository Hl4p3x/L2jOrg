// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.handler;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;

public interface IActionHandler
{
    public static final Logger LOGGER = LoggerFactory.getLogger(IActionHandler.class.getName());
    
    boolean action(final Player activeChar, final WorldObject target, final boolean interact);
    
    InstanceType getInstanceType();
}
