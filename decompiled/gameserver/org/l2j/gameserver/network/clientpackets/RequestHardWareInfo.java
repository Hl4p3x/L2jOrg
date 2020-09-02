// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.holders.ClientHardwareInfoHolder;
import org.l2j.gameserver.network.GameClient;

public final class RequestHardWareInfo extends ClientPacket
{
    private String _macAddress;
    private int _windowsPlatformId;
    private int _windowsMajorVersion;
    private int _windowsMinorVersion;
    private int _windowsBuildNumber;
    private int _directxVersion;
    private int _directxRevision;
    private String _cpuName;
    private int _cpuSpeed;
    private int _cpuCoreCount;
    private int _vgaCount;
    private int _vgaPcxSpeed;
    private int _physMemorySlot1;
    private int _physMemorySlot2;
    private int _physMemorySlot3;
    private int _videoMemory;
    private int _vgaVersion;
    private String _vgaName;
    private String _vgaDriverVersion;
    
    public void readImpl() {
        this._macAddress = this.readString();
        this._windowsPlatformId = this.readInt();
        this._windowsMajorVersion = this.readInt();
        this._windowsMinorVersion = this.readInt();
        this._windowsBuildNumber = this.readInt();
        this._directxVersion = this.readInt();
        this._directxRevision = this.readInt();
        this.readBytes(new byte[16]);
        this._cpuName = this.readString();
        this._cpuSpeed = this.readInt();
        this._cpuCoreCount = this.readByte();
        this.readInt();
        this._vgaCount = this.readInt();
        this._vgaPcxSpeed = this.readInt();
        this._physMemorySlot1 = this.readInt();
        this._physMemorySlot2 = this.readInt();
        this._physMemorySlot3 = this.readInt();
        this.readByte();
        this._videoMemory = this.readInt();
        this.readInt();
        this._vgaVersion = this.readShort();
        this._vgaName = this.readString();
        this._vgaDriverVersion = this.readString();
    }
    
    public void runImpl() {
        ((GameClient)this.client).setHardwareInfo(new ClientHardwareInfoHolder(this._macAddress, this._windowsPlatformId, this._windowsMajorVersion, this._windowsMinorVersion, this._windowsBuildNumber, this._directxVersion, this._directxRevision, this._cpuName, this._cpuSpeed, this._cpuCoreCount, this._vgaCount, this._vgaPcxSpeed, this._physMemorySlot1, this._physMemorySlot2, this._physMemorySlot3, this._videoMemory, this._vgaVersion, this._vgaName, this._vgaDriverVersion));
        if (Config.HARDWARE_INFO_ENABLED && Config.MAX_PLAYERS_PER_HWID > 0) {
            int count = 0;
            for (final Player player : World.getInstance().getPlayers()) {
                if (player.isOnline() && player.getClient().getHardwareInfo().equals(((GameClient)this.client).getHardwareInfo())) {
                    ++count;
                }
            }
            if (count >= Config.MAX_PLAYERS_PER_HWID) {
                Disconnection.of((GameClient)this.client).defaultSequence(false);
            }
        }
    }
}
