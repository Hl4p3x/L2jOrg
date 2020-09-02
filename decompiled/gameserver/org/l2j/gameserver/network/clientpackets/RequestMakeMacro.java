// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.GameClient;
import java.util.List;
import org.l2j.gameserver.enums.MacroType;
import org.l2j.gameserver.model.MacroCmd;
import java.util.ArrayList;
import org.l2j.gameserver.model.Macro;

public final class RequestMakeMacro extends ClientPacket
{
    private static final int MAX_MACRO_LENGTH = 12;
    private Macro _macro;
    private int _commandsLenght;
    
    public RequestMakeMacro() {
        this._commandsLenght = 0;
    }
    
    public void readImpl() {
        final int _id = this.readInt();
        final String _name = this.readString();
        final String _desc = this.readString();
        final String _acronym = this.readString();
        final int icon = this.readInt();
        int count = this.readByte();
        if (count > 12) {
            count = 12;
        }
        final List<MacroCmd> commands = new ArrayList<MacroCmd>(count);
        for (int i = 0; i < count; ++i) {
            final int entry = this.readByte();
            final int type = this.readByte();
            final int d1 = this.readInt();
            final int d2 = this.readByte();
            final String command = this.readString();
            this._commandsLenght += command.length();
            commands.add(new MacroCmd(entry, MacroType.values()[(type < 1 || type > 6) ? 0 : type], d1, d2, command));
        }
        this._macro = new Macro(_id, icon, _name, _desc, _acronym, commands);
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        if (this._commandsLenght > 255) {
            player.sendPacket(SystemMessageId.INVALID_MACRO_REFER_TO_THE_HELP_FILE_FOR_INSTRUCTIONS);
            return;
        }
        if (player.getMacros().size() > 48) {
            player.sendPacket(SystemMessageId.YOU_MAY_CREATE_UP_TO_48_MACROS);
            return;
        }
        if (this._macro.getName().isEmpty()) {
            player.sendPacket(SystemMessageId.ENTER_THE_NAME_OF_THE_MACRO);
            return;
        }
        if (this._macro.getDescr().length() > 32) {
            player.sendPacket(SystemMessageId.MACRO_DESCRIPTIONS_MAY_CONTAIN_UP_TO_32_CHARACTERS);
            return;
        }
        player.registerMacro(this._macro);
    }
}
