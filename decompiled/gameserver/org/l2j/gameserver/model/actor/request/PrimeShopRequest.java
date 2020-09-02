// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.request;

import org.l2j.gameserver.model.actor.instance.Player;

public class PrimeShopRequest extends AbstractRequest
{
    public PrimeShopRequest(final Player activeChar) {
        super(activeChar);
    }
    
    @Override
    public boolean isUsing(final int objectId) {
        return false;
    }
}
