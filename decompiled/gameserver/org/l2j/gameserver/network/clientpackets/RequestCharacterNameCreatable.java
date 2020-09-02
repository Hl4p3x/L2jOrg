// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExIsCharNameCreatable;
import org.l2j.gameserver.network.GameClient;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;

public class RequestCharacterNameCreatable extends ClientPacket
{
    public static int CHARACTER_CREATE_FAILED;
    public static int NAME_ALREADY_EXISTS;
    public static int INVALID_LENGTH;
    public static int INVALID_NAME;
    public static int CANNOT_CREATE_SERVER;
    private String _name;
    private int result;
    
    public void readImpl() {
        this._name = this.readString();
    }
    
    public void runImpl() {
        final int charId = PlayerNameTable.getInstance().getIdByName(this._name);
        if (!Util.isAlphaNumeric(this._name) || !this.isValidName(this._name)) {
            this.result = RequestCharacterNameCreatable.INVALID_NAME;
        }
        else if (charId > 0) {
            this.result = RequestCharacterNameCreatable.NAME_ALREADY_EXISTS;
        }
        else if (this._name.length() > 16) {
            this.result = RequestCharacterNameCreatable.INVALID_LENGTH;
        }
        else {
            this.result = -1;
        }
        ((GameClient)this.client).sendPacket(new ExIsCharNameCreatable(this.result));
    }
    
    private boolean isValidName(final String text) {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).acceptPlayerName(text);
    }
    
    static {
        RequestCharacterNameCreatable.CHARACTER_CREATE_FAILED = 1;
        RequestCharacterNameCreatable.NAME_ALREADY_EXISTS = 2;
        RequestCharacterNameCreatable.INVALID_LENGTH = 3;
        RequestCharacterNameCreatable.INVALID_NAME = 4;
        RequestCharacterNameCreatable.CANNOT_CREATE_SERVER = 5;
    }
}
