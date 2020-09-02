// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.html.styles;

import org.l2j.gameserver.model.html.IHtmlStyle;

public class ButtonsStyle implements IHtmlStyle
{
    public static final ButtonsStyle INSTANCE;
    private static final String DEFAULT_PAGE_LINK_FORMAT = "<td><button action=\"%s\" value=\"%s\" width=\"%d\" height=\"%d\" back=\"%s\" fore=\"%s\"></td>";
    private static final String DEFAULT_PAGE_TEXT_FORMAT = "<td>%s</td>";
    private static final String DEFAULT_PAGER_SEPARATOR = "<td align=center> | </td>";
    private final int _width;
    private final int _height;
    private final String _back;
    private final String _fore;
    
    public ButtonsStyle(final int width, final int height, final String back, final String fore) {
        this._width = width;
        this._height = height;
        this._back = back;
        this._fore = fore;
    }
    
    @Override
    public String applyBypass(final String bypass, final String name, final boolean isActive) {
        if (isActive) {
            return String.format("<td>%s</td>", name);
        }
        return String.format("<td><button action=\"%s\" value=\"%s\" width=\"%d\" height=\"%d\" back=\"%s\" fore=\"%s\"></td>", bypass, name, this._width, this._height, this._back, this._fore);
    }
    
    @Override
    public String applySeparator() {
        return "<td align=center> | </td>";
    }
    
    static {
        INSTANCE = new ButtonsStyle(40, 15, "L2UI_CT1.Button_DF", "L2UI_CT1.Button_DF");
    }
}
