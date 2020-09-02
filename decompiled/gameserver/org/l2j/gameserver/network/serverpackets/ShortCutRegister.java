// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.data.database.data.Shortcut;

public final class ShortCutRegister extends ServerPacket
{
    private final Shortcut shortcut;
    
    public ShortCutRegister(final Shortcut shortcut) {
        this.shortcut = shortcut;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.SHORTCUT_REG);
        this.writeInt(this.shortcut.getType().ordinal());
        this.writeInt(this.shortcut.getClientId());
        this.writeByte(0);
        switch (this.shortcut.getType()) {
            case ITEM: {
                this.writeShortcutItem();
                break;
            }
            case SKILL: {
                this.writeShortcutSkill();
                break;
            }
            case ACTION:
            case MACRO:
            case RECIPE:
            case BOOKMARK: {
                this.writeInt(this.shortcut.getShortcutId());
                this.writeInt(this.shortcut.getCharacterType());
                break;
            }
        }
    }
    
    private void writeShortcutSkill() {
        this.writeInt(this.shortcut.getShortcutId());
        this.writeShort(this.shortcut.getLevel());
        this.writeShort(this.shortcut.getSubLevel());
        this.writeInt(this.shortcut.getSharedReuseGroup());
        this.writeByte(0);
        this.writeInt(this.shortcut.getCharacterType());
        this.writeInt(0);
        this.writeInt(0);
    }
    
    private void writeShortcutItem() {
        this.writeInt(this.shortcut.getShortcutId());
        this.writeInt(this.shortcut.getCharacterType());
        this.writeInt(this.shortcut.getSharedReuseGroup());
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
    }
}
