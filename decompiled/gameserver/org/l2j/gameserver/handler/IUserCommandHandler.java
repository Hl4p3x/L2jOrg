// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.handler;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;

public interface IUserCommandHandler
{
    public static final Logger LOGGER = LoggerFactory.getLogger(IUserCommandHandler.class.getName());
    
    boolean useUserCommand(final int id, final Player player);
    
    int[] getUserCommandList();
}
