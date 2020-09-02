// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.api.item;

import org.l2j.gameserver.network.serverpackets.item.upgrade.ExShowUpgradeSystemNormal;
import org.l2j.gameserver.network.serverpackets.item.upgrade.ExShowUpgradeSystem;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.instance.Player;

public class UpgradeAPI
{
    public static boolean showUpgradeUI(final Player player, final UpgradeType type) {
        if (type == UpgradeType.RARE) {
            player.sendPacket(new ExShowUpgradeSystem());
        }
        else {
            player.sendPacket(new ExShowUpgradeSystemNormal(type));
        }
        return true;
    }
}
