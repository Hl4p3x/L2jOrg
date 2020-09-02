// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.mission;

public interface MissionHandlerFactory
{
    AbstractMissionHandler create(final MissionDataHolder data);
    
    String handlerName();
}
