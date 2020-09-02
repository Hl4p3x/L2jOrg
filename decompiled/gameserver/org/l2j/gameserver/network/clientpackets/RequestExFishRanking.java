// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public final class RequestExFishRanking extends ClientPacket
{
    private static final Logger LOGGER;
    
    public void readImpl() {
    }
    
    public void runImpl() {
        RequestExFishRanking.LOGGER.info("C5: RequestExFishRanking");
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestExFishRanking.class);
    }
}
