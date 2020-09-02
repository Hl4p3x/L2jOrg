// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.bypasshandlers;

import org.l2j.gameserver.api.item.UpgradeAPI;
import org.l2j.gameserver.api.item.UpgradeType;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import java.util.Objects;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IBypassHandler;

public class UpgradeHandler implements IBypassHandler
{
    public boolean useBypass(final String command, final Player player, final Creature npc) {
        if (Objects.isNull(npc) || !MathUtil.isInsideRadius3D((ILocational)player, (ILocational)npc, 250) || !command.contains(" ")) {
            return false;
        }
        final String typeName = command.split(" ")[1];
        return UpgradeAPI.showUpgradeUI(player, UpgradeType.valueOf(typeName));
    }
    
    public String[] getBypassList() {
        return new String[] { "upgrade_item" };
    }
}
