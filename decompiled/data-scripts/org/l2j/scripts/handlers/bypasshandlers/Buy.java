// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.bypasshandlers;

import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Merchant;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IBypassHandler;

public class Buy implements IBypassHandler
{
    private static final String[] COMMANDS;
    
    public boolean useBypass(final String command, final Player player, final Creature target) {
        if (!(target instanceof Merchant)) {
            return false;
        }
        try {
            final StringTokenizer st = new StringTokenizer(command, " ");
            st.nextToken();
            if (st.countTokens() < 1) {
                return false;
            }
            ((Merchant)target).showBuyWindow(player, Integer.parseInt(st.nextToken()));
            return true;
        }
        catch (Exception e) {
            Buy.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()), (Throwable)e);
            return false;
        }
    }
    
    public String[] getBypassList() {
        return Buy.COMMANDS;
    }
    
    static {
        COMMANDS = new String[] { "Buy" };
    }
}
