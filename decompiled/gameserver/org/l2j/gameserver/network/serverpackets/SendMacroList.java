// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.model.MacroCmd;
import java.util.Objects;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.enums.MacroUpdateType;
import org.l2j.gameserver.model.Macro;

public class SendMacroList extends ServerPacket
{
    private final int _count;
    private final Macro _macro;
    private final MacroUpdateType _updateType;
    
    public SendMacroList(final int count, final Macro macro, final MacroUpdateType updateType) {
        this._count = count;
        this._macro = macro;
        this._updateType = updateType;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.MACRO_LIST);
        this.writeByte(this._updateType.getId());
        this.writeInt((this._updateType != MacroUpdateType.LIST) ? this._macro.getId() : 0);
        this.writeByte(this._count);
        this.writeByte(Objects.nonNull(this._macro));
        if (this._macro != null && this._updateType != MacroUpdateType.DELETE) {
            this.writeInt(this._macro.getId());
            this.writeString((CharSequence)this._macro.getName());
            this.writeString((CharSequence)this._macro.getDescr());
            this.writeString((CharSequence)this._macro.getAcronym());
            this.writeInt(this._macro.getIcon());
            this.writeByte(this._macro.getCommands().size());
            int i = 1;
            for (final MacroCmd cmd : this._macro.getCommands()) {
                this.writeByte(i++);
                this.writeByte(cmd.getType().ordinal());
                this.writeInt(cmd.getD1());
                this.writeByte(cmd.getD2());
                this.writeString((CharSequence)cmd.getCmd());
            }
        }
    }
}
