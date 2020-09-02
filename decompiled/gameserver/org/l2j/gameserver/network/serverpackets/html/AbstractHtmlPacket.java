// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.html;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.enums.HtmlActionScope;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public abstract class AbstractHtmlPacket extends ServerPacket
{
    public static final char VAR_PARAM_START_CHAR = '$';
    private static final Logger LOGGER;
    private final int _npcObjId;
    private String html;
    private boolean _disabledValidation;
    
    protected AbstractHtmlPacket() {
        this.html = null;
        this._disabledValidation = false;
        this._npcObjId = 0;
    }
    
    protected AbstractHtmlPacket(final int npcObjId) {
        this.html = null;
        this._disabledValidation = false;
        if (npcObjId < 0) {
            throw new IllegalArgumentException();
        }
        this._npcObjId = npcObjId;
    }
    
    protected AbstractHtmlPacket(final String html) {
        this.html = null;
        this._disabledValidation = false;
        this._npcObjId = 0;
        this.setHtml(html);
    }
    
    protected AbstractHtmlPacket(final int npcObjId, final String html) {
        this.html = null;
        this._disabledValidation = false;
        if (npcObjId < 0) {
            throw new IllegalArgumentException();
        }
        this._npcObjId = npcObjId;
        this.setHtml(html);
    }
    
    public final void disableValidation() {
        this._disabledValidation = true;
    }
    
    public final boolean setFile(final Player player, final String path) {
        final String content = HtmCache.getInstance().getHtm(player, path);
        if (content == null) {
            this.setHtml(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, path));
            AbstractHtmlPacket.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, path));
            return false;
        }
        this.setHtml(content);
        return true;
    }
    
    public final void replace(final String pattern, final String value) {
        this.html = this.html.replaceAll(pattern, value.replaceAll("\\$", "\\\\\\$"));
    }
    
    public final void replace(final String pattern, final CharSequence value) {
        this.replace(pattern, String.valueOf(value));
    }
    
    public final void replace(final String pattern, final boolean val) {
        this.replace(pattern, String.valueOf(val));
    }
    
    public final void replace(final String pattern, final int val) {
        this.replace(pattern, String.valueOf(val));
    }
    
    public final void replace(final String pattern, final long val) {
        this.replace(pattern, String.valueOf(val));
    }
    
    public final void replace(final String pattern, final double val) {
        this.replace(pattern, String.valueOf(val));
    }
    
    @Override
    public final void runImpl(final Player player) {
        if (player != null) {
            player.clearHtmlActions(this.getScope());
        }
        if (this._disabledValidation) {
            return;
        }
        if (player != null) {
            GameUtils.buildHtmlActionCache(player, this.getScope(), this._npcObjId, this.html);
        }
    }
    
    public final int getNpcObjId() {
        return this._npcObjId;
    }
    
    public final String getHtml() {
        return this.html;
    }
    
    public final void setHtml(String html) {
        if (html.length() > 17200) {
            AbstractHtmlPacket.LOGGER.warn("Html is too long! this will crash the client!", new Throwable());
            this.html = html.substring(0, 17200);
        }
        if (!html.contains("<html") && !html.startsWith("..\\L2")) {
            html = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, html);
        }
        this.html = html;
    }
    
    public abstract HtmlActionScope getScope();
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)AbstractHtmlPacket.class);
    }
}
