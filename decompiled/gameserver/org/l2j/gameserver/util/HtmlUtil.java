// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util;

import org.l2j.commons.util.CommonUtil;

public class HtmlUtil
{
    public static String getCpGauge(final int width, final long current, final long max, final boolean displayAsPercentage) {
        return getGauge(width, current, max, displayAsPercentage, "L2UI_CT1.Gauges.Gauge_DF_Large_CP_bg_Center", "L2UI_CT1.Gauges.Gauge_DF_Large_CP_Center", 17L, -13L);
    }
    
    public static String getHpGauge(final int width, final long current, final long max, final boolean displayAsPercentage) {
        return getGauge(width, current, max, displayAsPercentage, "L2UI_CT1.Gauges.Gauge_DF_Large_HP_bg_Center", "L2UI_CT1.Gauges.Gauge_DF_Large_HP_Center", 21L, -13L);
    }
    
    public static String getHpWarnGauge(final int width, final long current, final long max, final boolean displayAsPercentage) {
        return getGauge(width, current, max, displayAsPercentage, "L2UI_CT1.Gauges.Gauge_DF_Large_HPWarn_bg_Center", "L2UI_CT1.Gauges.Gauge_DF_Large_HPWarn_Center", 17L, -13L);
    }
    
    public static String getHpFillGauge(final int width, final long current, final long max, final boolean displayAsPercentage) {
        return getGauge(width, current, max, displayAsPercentage, "L2UI_CT1.Gauges.Gauge_DF_Large_HPFill_bg_Center", "L2UI_CT1.Gauges.Gauge_DF_Large_HPFill_Center", 17L, -13L);
    }
    
    public static String getMpGauge(final int width, final long current, final long max, final boolean displayAsPercentage) {
        return getGauge(width, current, max, displayAsPercentage, "L2UI_CT1.Gauges.Gauge_DF_Large_MP_bg_Center", "L2UI_CT1.Gauges.Gauge_DF_Large_MP_Center", 17L, -13L);
    }
    
    public static String getExpGauge(final int width, final long current, final long max, final boolean displayAsPercentage) {
        return getGauge(width, current, max, displayAsPercentage, "L2UI_CT1.Gauges.Gauge_DF_Large_EXP_bg_Center", "L2UI_CT1.Gauges.Gauge_DF_Large_EXP_Center", 17L, -13L);
    }
    
    public static String getFoodGauge(final int width, final long current, final long max, final boolean displayAsPercentage) {
        return getGauge(width, current, max, displayAsPercentage, "L2UI_CT1.Gauges.Gauge_DF_Large_Food_Bg_Center", "L2UI_CT1.Gauges.Gauge_DF_Large_Food_Center", 17L, -13L);
    }
    
    public static String getWeightGauge(final int width, final long current, final long max, final boolean displayAsPercentage) {
        return getWeightGauge(width, current, max, displayAsPercentage, CommonUtil.map(current, 0L, max, 1L, 5L));
    }
    
    public static String getWeightGauge(final int width, final long current, final long max, final boolean displayAsPercentage, final long level) {
        return getGauge(width, current, max, displayAsPercentage, invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, level), invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, level), 17L, -13L);
    }
    
    private static String getGauge(final int width, long current, final long max, final boolean displayAsPercentage, final String backgroundImage, final String image, final long imageHeight, final long top) {
        current = Math.min(current, max);
        final StringBuilder sb = new StringBuilder();
        sb.append("<table width=").append(width).append(" cellpadding=0 cellspacing=0>");
        sb.append("<tr>").append("<td background=\"").append(backgroundImage).append("\">");
        sb.append("<img src=\"").append(image).append("\" width=").append(current / max * width);
        sb.append(" height=").append(imageHeight).append("></td></tr>");
        sb.append("<tr>");
        sb.append("<td align=center>");
        sb.append("<table cellpadding=0 cellspacing=").append(top).append(">");
        sb.append("<tr><td>");
        if (displayAsPercentage) {
            sb.append("<table cellpadding=0 cellspacing=2>");
            sb.append("<tr><td>");
            sb.append(String.format("%.2f%%", current / (double)max * 100.0));
            sb.append("</td></tr>");
            sb.append("</table>");
        }
        else {
            final int tdWidth = (width - 10) / 2;
            sb.append("<table cellpadding=0 cellspacing=0>");
            sb.append("<tr>");
            sb.append("<td width=").append(tdWidth).append(" align=right>").append(current).append("</td>");
            sb.append("<td width=10 align=center>/</td>");
            sb.append("<td width=").append(tdWidth).append(">").append(max).append("</td>").append("</tr>");
            sb.append("</table>");
        }
        sb.append("</td></tr></table></td></tr></table>");
        return sb.toString();
    }
}
