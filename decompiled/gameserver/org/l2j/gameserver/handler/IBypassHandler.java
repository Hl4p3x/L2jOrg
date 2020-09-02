// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.handler;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;

public interface IBypassHandler
{
    public static final Logger LOGGER = LoggerFactory.getLogger(IBypassHandler.class.getName());
    
    boolean useBypass(final String command, final Player player, final Creature bypassOrigin);
    
    String[] getBypassList();
}
