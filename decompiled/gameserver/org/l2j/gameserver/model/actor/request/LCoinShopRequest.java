// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.request;

import org.l2j.gameserver.model.actor.instance.Player;

public class LCoinShopRequest extends AbstractRequest
{
    public LCoinShopRequest(final Player activeChar) {
        super(activeChar);
    }
    
    @Override
    public boolean isUsingItem(final int objectId) {
        return false;
    }
}
