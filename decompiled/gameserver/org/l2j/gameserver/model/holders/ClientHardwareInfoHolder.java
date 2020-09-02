// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

public class ClientHardwareInfoHolder
{
    private final String _macAddress;
    private final int _windowsPlatformId;
    private final int _windowsMajorVersion;
    private final int _windowsMinorVersion;
    private final int _windowsBuildNumber;
    private final int _directxVersion;
    private final int _directxRevision;
    private final String _cpuName;
    private final int _cpuSpeed;
    private final int _cpuCoreCount;
    private final int _vgaCount;
    private final int _vgaPcxSpeed;
    private final int _physMemorySlot1;
    private final int _physMemorySlot2;
    private final int _physMemorySlot3;
    private final int _videoMemory;
    private final int _vgaVersion;
    private final String _vgaName;
    private final String _vgaDriverVersion;
    
    public ClientHardwareInfoHolder(final String macAddress, final int windowsPlatformId, final int windowsMajorVersion, final int windowsMinorVersion, final int windowsBuildNumber, final int directxVersion, final int directxRevision, final String cpuName, final int cpuSpeed, final int cpuCoreCount, final int vgaCount, final int vgaPcxSpeed, final int physMemorySlot1, final int physMemorySlot2, final int physMemorySlot3, final int videoMemory, final int vgaVersion, final String vgaName, final String vgaDriverVersion) {
        this._macAddress = macAddress;
        this._windowsPlatformId = windowsPlatformId;
        this._windowsMajorVersion = windowsMajorVersion;
        this._windowsMinorVersion = windowsMinorVersion;
        this._windowsBuildNumber = windowsBuildNumber;
        this._directxVersion = directxVersion;
        this._directxRevision = directxRevision;
        this._cpuName = cpuName;
        this._cpuSpeed = cpuSpeed;
        this._cpuCoreCount = cpuCoreCount;
        this._vgaCount = vgaCount;
        this._vgaPcxSpeed = vgaPcxSpeed;
        this._physMemorySlot1 = physMemorySlot1;
        this._physMemorySlot2 = physMemorySlot2;
        this._physMemorySlot3 = physMemorySlot3;
        this._videoMemory = videoMemory;
        this._vgaVersion = vgaVersion;
        this._vgaName = vgaName;
        this._vgaDriverVersion = vgaDriverVersion;
    }
    
    public String getMacAddress() {
        return this._macAddress;
    }
    
    public int getWindowsPlatformId() {
        return this._windowsPlatformId;
    }
    
    public int getWindowsMajorVersion() {
        return this._windowsMajorVersion;
    }
    
    public int getWindowsMinorVersion() {
        return this._windowsMinorVersion;
    }
    
    public int getWindowsBuildNumber() {
        return this._windowsBuildNumber;
    }
    
    public int getDirectxVersion() {
        return this._directxVersion;
    }
    
    public int getDirectxRevision() {
        return this._directxRevision;
    }
    
    public String getCpuName() {
        return this._cpuName;
    }
    
    public int getCpuSpeed() {
        return this._cpuSpeed;
    }
    
    public int getCpuCoreCount() {
        return this._cpuCoreCount;
    }
    
    public int getVgaCount() {
        return this._vgaCount;
    }
    
    public int getVgaPcxSpeed() {
        return this._vgaPcxSpeed;
    }
    
    public int getPhysMemorySlot1() {
        return this._physMemorySlot1;
    }
    
    public int getPhysMemorySlot2() {
        return this._physMemorySlot2;
    }
    
    public int getPhysMemorySlot3() {
        return this._physMemorySlot3;
    }
    
    public int getVideoMemory() {
        return this._videoMemory;
    }
    
    public int getVgaVersion() {
        return this._vgaVersion;
    }
    
    public String getVgaName() {
        return this._vgaName;
    }
    
    public String getVgaDriverVersion() {
        return this._vgaDriverVersion;
    }
}
