// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.handler;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.punishment.PunishmentType;
import org.l2j.gameserver.model.punishment.PunishmentTask;
import org.slf4j.Logger;

public interface IPunishmentHandler
{
    public static final Logger LOGGER = LoggerFactory.getLogger(IPunishmentHandler.class.getName());
    
    void onStart(final PunishmentTask task);
    
    void onEnd(final PunishmentTask task);
    
    PunishmentType getType();
}
