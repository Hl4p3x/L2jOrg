// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.autoplay.ExActivateAutoShortcut;
import org.l2j.gameserver.engine.autoplay.AutoPlayEngine;
import java.util.Objects;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.data.database.data.Shortcut;
import org.l2j.gameserver.enums.ShortcutType;

public final class RequestShortCutReg extends ClientPacket
{
    private ShortcutType type;
    private int id;
    private int lvl;
    private int subLvl;
    private int characterType;
    private int room;
    
    public void readImpl() {
        final int typeId = this.readInt();
        this.type = ShortcutType.values()[(typeId < 1 || typeId > 6) ? 0 : typeId];
        this.room = this.readInt();
        this.readByte();
        this.id = this.readInt();
        this.lvl = this.readShort();
        this.subLvl = this.readShort();
        this.characterType = this.readInt();
    }
    
    public void runImpl() {
        if (this.room < 0 || (this.room > 240 && this.room != Shortcut.AUTO_POTION_ROOM)) {
            return;
        }
        final Player player = ((GameClient)this.client).getPlayer();
        Item item = null;
        if (this.type == ShortcutType.ITEM && Objects.isNull(item = player.getInventory().getItemByObjectId(this.id))) {
            return;
        }
        if (this.room == Shortcut.AUTO_POTION_ROOM && (Objects.isNull(item) || !item.isAutoPotion())) {
            return;
        }
        player.registerShortCut(new Shortcut(this.room, this.type, this.id, this.lvl, this.subLvl, this.characterType));
        if (this.room == Shortcut.AUTO_POTION_ROOM && AutoPlayEngine.getInstance().setActiveAutoShortcut(player, this.room, true)) {
            ((GameClient)this.client).sendPacket(new ExActivateAutoShortcut(this.room, true));
        }
    }
}
