// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.returns;

import org.l2j.gameserver.enums.ChatType;

public class ChatFilterReturn extends AbstractEventReturn
{
    private final String _filteredText;
    private final ChatType _chatType;
    
    public ChatFilterReturn(final String filteredText, final ChatType newChatType, final boolean override, final boolean abort) {
        super(override, abort);
        this._filteredText = filteredText;
        this._chatType = newChatType;
    }
    
    public String getFilteredText() {
        return this._filteredText;
    }
    
    public ChatType getChatType() {
        return this._chatType;
    }
}
