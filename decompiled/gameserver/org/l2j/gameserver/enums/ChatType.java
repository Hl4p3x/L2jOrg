// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.enums;

public enum ChatType
{
    GENERAL, 
    SHOUT, 
    WHISPER, 
    PARTY, 
    CLAN, 
    GM, 
    PETITION_PLAYER, 
    PETITION_GM, 
    TRADE, 
    ALLIANCE, 
    ANNOUNCEMENT, 
    BOAT, 
    FRIEND, 
    MSNCHAT, 
    PARTYMATCH_ROOM, 
    PARTYROOM_COMMANDER, 
    PARTYROOM_ALL, 
    HERO_VOICE, 
    CRITICAL_ANNOUNCE, 
    SCREEN_ANNOUNCE, 
    BATTLEFIELD, 
    MPCC_ROOM, 
    NPC_GENERAL, 
    NPC_SHOUT, 
    NPC_WHISPER, 
    WORLD;
    
    private static final ChatType[] CACHED;
    
    public static ChatType findByClientId(final int clientId) {
        for (final ChatType ChatType : ChatType.CACHED) {
            if (ChatType.getClientId() == clientId) {
                return ChatType;
            }
        }
        return null;
    }
    
    public int getClientId() {
        return this.ordinal();
    }
    
    static {
        CACHED = values();
    }
}
