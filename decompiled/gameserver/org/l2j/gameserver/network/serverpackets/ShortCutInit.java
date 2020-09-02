// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.database.data.Shortcut;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public final class ShortCutInit extends ServerPacket
{
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.INIT_SHORTCUT);
        final Player player = client.getPlayer();
        this.writeInt(player.getShortcutAmount());
        player.forEachShortcut(s -> {
            this.writeInt(s.getType().ordinal());
            this.writeInt(s.getClientId());
            this.writeByte(0);
            switch (s.getType()) {
                case ITEM: {
                    this.writeShortcutItem(s);
                    break;
                }
                case SKILL: {
                    this.writeShortcutSkill(s);
                    break;
                }
                case ACTION:
                case MACRO:
                case RECIPE:
                case BOOKMARK: {
                    this.writeInt(s.getShortcutId());
                    this.writeInt(s.getCharacterType());
                    break;
                }
            }
        });
    }
    
    private void writeShortcutSkill(final Shortcut sc) {
        this.writeInt(sc.getShortcutId());
        this.writeShort(sc.getLevel());
        this.writeShort(sc.getSubLevel());
        this.writeInt(sc.getSharedReuseGroup());
        this.writeByte(0);
        this.writeInt(1);
    }
    
    private void writeShortcutItem(final Shortcut sc) {
        this.writeInt(sc.getShortcutId());
        this.writeInt(1);
        this.writeInt(sc.getSharedReuseGroup());
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
    }
}
