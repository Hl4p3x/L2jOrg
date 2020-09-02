// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

public class ActionKey
{
    private final int _cat;
    private int _cmd;
    private int _key;
    private int _tgKey1;
    private int _tgKey2;
    private int _show;
    
    public ActionKey(final int cat) {
        this._cmd = 0;
        this._key = 0;
        this._tgKey1 = 0;
        this._tgKey2 = 0;
        this._show = 1;
        this._cat = cat;
    }
    
    public ActionKey(final int cat, final int cmd, final int key, final int tgKey1, final int tgKey2, final int show) {
        this._cmd = 0;
        this._key = 0;
        this._tgKey1 = 0;
        this._tgKey2 = 0;
        this._show = 1;
        this._cat = cat;
        this._cmd = cmd;
        this._key = key;
        this._tgKey1 = tgKey1;
        this._tgKey2 = tgKey2;
        this._show = show;
    }
    
    public int getCategory() {
        return this._cat;
    }
    
    public int getCommandId() {
        return this._cmd;
    }
    
    public void setCommandId(final int cmd) {
        this._cmd = cmd;
    }
    
    public int getKeyId() {
        return this._key;
    }
    
    public void setKeyId(final int key) {
        this._key = key;
    }
    
    public int getToogleKey1() {
        return this._tgKey1;
    }
    
    public void setToogleKey1(final int tKey1) {
        this._tgKey1 = tKey1;
    }
    
    public int getToogleKey2() {
        return this._tgKey2;
    }
    
    public void setToogleKey2(final int tKey2) {
        this._tgKey2 = tKey2;
    }
    
    public int getShowStatus() {
        return this._show;
    }
    
    public void setShowStatus(final int show) {
        this._show = show;
    }
    
    public String getSqlSaveString(final int playerId, final int order) {
        return invokedynamic(makeConcatWithConstants:(IIIIIIII)Ljava/lang/String;, playerId, this._cat, order, this._cmd, this._key, this._tgKey1, this._tgKey2, this._show);
    }
}
