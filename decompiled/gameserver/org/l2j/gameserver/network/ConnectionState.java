// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network;

import java.util.EnumSet;

public enum ConnectionState
{
    CONNECTED, 
    DISCONNECTED, 
    CLOSING, 
    AUTHENTICATED, 
    JOINING_GAME, 
    IN_GAME;
    
    static final EnumSet<ConnectionState> IN_GAME_STATES;
    static final EnumSet<ConnectionState> AUTHENTICATED_STATES;
    static final EnumSet<ConnectionState> CONNECTED_STATES;
    static final EnumSet<ConnectionState> JOINING_GAME_STATES;
    static final EnumSet<ConnectionState> AUTHENTICATED_AND_IN_GAME;
    static final EnumSet<ConnectionState> JOINING_GAME_AND_IN_GAME;
    static final EnumSet<ConnectionState> ALL;
    static final EnumSet<ConnectionState> EMPTY;
    
    static {
        IN_GAME_STATES = EnumSet.of(ConnectionState.IN_GAME);
        AUTHENTICATED_STATES = EnumSet.of(ConnectionState.AUTHENTICATED);
        CONNECTED_STATES = EnumSet.of(ConnectionState.CONNECTED);
        JOINING_GAME_STATES = EnumSet.of(ConnectionState.JOINING_GAME);
        AUTHENTICATED_AND_IN_GAME = EnumSet.of(ConnectionState.AUTHENTICATED, ConnectionState.IN_GAME);
        JOINING_GAME_AND_IN_GAME = EnumSet.of(ConnectionState.JOINING_GAME, ConnectionState.IN_GAME);
        ALL = EnumSet.allOf(ConnectionState.class);
        EMPTY = EnumSet.noneOf(ConnectionState.class);
    }
}
