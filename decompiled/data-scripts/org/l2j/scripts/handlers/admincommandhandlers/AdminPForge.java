// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import java.math.BigInteger;
import org.l2j.gameserver.model.actor.instance.Boat;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.util.GameUtils;
import java.nio.ByteBuffer;
import org.l2j.gameserver.network.serverpackets.AdminForgePacket;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Collection;
import java.util.LinkedList;
import java.util.StringTokenizer;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public final class AdminPForge implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    private String[] getOpCodes(final StringTokenizer st) {
        Collection<String> opCodes = null;
        while (st.hasMoreTokens()) {
            final String token = st.nextToken();
            if (";".equals(token)) {
                break;
            }
            if (opCodes == null) {
                opCodes = new LinkedList<String>();
            }
            opCodes.add(token);
        }
        if (opCodes == null) {
            return null;
        }
        return opCodes.toArray(new String[opCodes.size()]);
    }
    
    private boolean validateOpCodes(final String[] opCodes) {
        if (opCodes == null || opCodes.length == 0 || opCodes.length > 3) {
            return false;
        }
        for (int i = 0; i < opCodes.length; ++i) {
            final String opCode = opCodes[i];
            long opCodeLong;
            try {
                opCodeLong = Long.decode(opCode);
            }
            catch (Exception e) {
                return i > 0;
            }
            if (opCodeLong < 0L) {
                return false;
            }
            if (i == 0 && opCodeLong > 255L) {
                return false;
            }
            if (i == 1 && opCodeLong > 65535L) {
                return false;
            }
            if (i == 2 && opCodeLong > 4294967295L) {
                return false;
            }
        }
        return true;
    }
    
    private boolean validateFormat(final String format) {
        for (int chIdx = 0; chIdx < format.length(); ++chIdx) {
            switch (format.charAt(chIdx)) {
                case 'B':
                case 'X':
                case 'b':
                case 'x': {
                    break;
                }
                case 'C':
                case 'c': {
                    break;
                }
                case 'H':
                case 'h': {
                    break;
                }
                case 'D':
                case 'd': {
                    break;
                }
                case 'Q':
                case 'q': {
                    break;
                }
                case 'F':
                case 'f': {
                    break;
                }
                case 'S':
                case 's': {
                    break;
                }
                default: {
                    return false;
                }
            }
        }
        return true;
    }
    
    private boolean validateMethod(final String method) {
        boolean b = false;
        switch (method) {
            case "sc":
            case "sb":
            case "cs": {
                b = true;
                break;
            }
            default: {
                b = false;
                break;
            }
        }
        return b;
    }
    
    private void showValuesUsage(final Player activeChar) {
        BuilderUtil.sendSysMessage(activeChar, "Usage: //forge_values opcode1[ opcode2[ opcode3]] ;[ format]");
        this.showMainPage(activeChar);
    }
    
    private void showSendUsage(final Player activeChar, final String[] opCodes, final String format) {
        BuilderUtil.sendSysMessage(activeChar, "Usage: //forge_send sc|sb|cs opcode1[;opcode2[;opcode3]][ format value1 ... valueN] ");
        if (opCodes == null) {
            this.showMainPage(activeChar);
        }
        else {
            this.showValuesPage(activeChar, opCodes, format);
        }
    }
    
    private void showMainPage(final Player activeChar) {
        AdminHtml.showAdminHtml(activeChar, "pforge/main.htm");
    }
    
    private void showValuesPage(final Player activeChar, final String[] opCodes, final String format) {
        String sendBypass = null;
        String valuesHtml = HtmCache.getInstance().getHtmForce(activeChar, "data/html/admin/pforge/values.htm");
        if (opCodes.length == 3) {
            valuesHtml = valuesHtml.replace("%opformat%", "chd");
            sendBypass = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, opCodes[0], opCodes[1], opCodes[2]);
        }
        else if (opCodes.length == 2) {
            valuesHtml = valuesHtml.replace("%opformat%", "ch");
            sendBypass = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, opCodes[0], opCodes[1]);
        }
        else {
            valuesHtml = valuesHtml.replace("%opformat%", "c");
            sendBypass = opCodes[0];
        }
        valuesHtml = valuesHtml.replace("%opcodes%", sendBypass);
        String editorsHtml = "";
        if (format == null) {
            valuesHtml = valuesHtml.replace("%format%", "");
            editorsHtml = "";
        }
        else {
            valuesHtml = valuesHtml.replace("%format%", format);
            sendBypass = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, sendBypass, format);
            final String editorTemplate = HtmCache.getInstance().getHtm(activeChar, "data/html/admin/pforge/inc/editor.htm");
            if (editorTemplate != null) {
                final StringBuilder singleCharSequence = new StringBuilder(1);
                singleCharSequence.append(' ');
                for (int chIdx = 0; chIdx < format.length(); ++chIdx) {
                    final char ch = format.charAt(chIdx);
                    singleCharSequence.setCharAt(0, ch);
                    editorsHtml = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, editorsHtml, editorTemplate.replace("%format%", singleCharSequence).replace("%editor_index%", String.valueOf(chIdx)));
                    sendBypass = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, sendBypass, chIdx);
                }
            }
            else {
                editorsHtml = "";
            }
        }
        valuesHtml = valuesHtml.replace("%editors%", editorsHtml);
        valuesHtml = valuesHtml.replace("%send_bypass%", sendBypass);
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)new NpcHtmlMessage(0, 1, valuesHtml) });
    }
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        if (command.equals("admin_forge")) {
            this.showMainPage(activeChar);
        }
        else {
            if (command.startsWith("admin_forge_values ")) {
                try {
                    final StringTokenizer st = new StringTokenizer(command);
                    st.nextToken();
                    if (!st.hasMoreTokens()) {
                        this.showValuesUsage(activeChar);
                        return false;
                    }
                    final String[] opCodes = this.getOpCodes(st);
                    if (!this.validateOpCodes(opCodes)) {
                        BuilderUtil.sendSysMessage(activeChar, "Invalid op codes!");
                        this.showValuesUsage(activeChar);
                        return false;
                    }
                    String format = null;
                    if (st.hasMoreTokens()) {
                        format = st.nextToken();
                        if (!this.validateFormat(format)) {
                            BuilderUtil.sendSysMessage(activeChar, "Format invalid!");
                            this.showValuesUsage(activeChar);
                            return false;
                        }
                    }
                    this.showValuesPage(activeChar, opCodes, format);
                    return true;
                }
                catch (Exception e) {
                    e.printStackTrace();
                    this.showValuesUsage(activeChar);
                    return false;
                }
            }
            if (command.startsWith("admin_forge_send ")) {
                try {
                    final StringTokenizer st = new StringTokenizer(command);
                    st.nextToken();
                    if (!st.hasMoreTokens()) {
                        this.showSendUsage(activeChar, null, null);
                        return false;
                    }
                    final String method = st.nextToken();
                    if (!this.validateMethod(method)) {
                        BuilderUtil.sendSysMessage(activeChar, "Invalid method!");
                        this.showSendUsage(activeChar, null, null);
                        return false;
                    }
                    final String[] opCodes2 = st.nextToken().split(";");
                    if (!this.validateOpCodes(opCodes2)) {
                        BuilderUtil.sendSysMessage(activeChar, "Invalid op codes!");
                        this.showSendUsage(activeChar, null, null);
                        return false;
                    }
                    String format2 = null;
                    if (st.hasMoreTokens()) {
                        format2 = st.nextToken();
                        if (!this.validateFormat(format2)) {
                            BuilderUtil.sendSysMessage(activeChar, "Format invalid!");
                            this.showSendUsage(activeChar, null, null);
                            return false;
                        }
                    }
                    AdminForgePacket afp = null;
                    ByteBuffer bb = null;
                    for (int i = 0; i < opCodes2.length; ++i) {
                        char type;
                        if (i == 0) {
                            type = 'c';
                        }
                        else if (i == 1) {
                            type = 'h';
                        }
                        else {
                            type = 'd';
                        }
                        if (method.equals("sc") || method.equals("sb")) {
                            if (afp == null) {
                                afp = new AdminForgePacket();
                            }
                            afp.addPart((byte)type, opCodes2[i]);
                        }
                        else {
                            if (bb == null) {
                                bb = ByteBuffer.allocate(32767);
                            }
                            this.write((byte)type, opCodes2[i], bb);
                        }
                    }
                    if (format2 != null) {
                        for (int i = 0; i < format2.length(); ++i) {
                            if (!st.hasMoreTokens()) {
                                BuilderUtil.sendSysMessage(activeChar, "Not enough values!");
                                this.showSendUsage(activeChar, null, null);
                                return false;
                            }
                            WorldObject target = null;
                            Boat boat = null;
                            final String nextToken;
                            String value = nextToken = st.nextToken();
                            switch (nextToken) {
                                case "$oid": {
                                    value = String.valueOf(activeChar.getObjectId());
                                    break;
                                }
                                case "$boid": {
                                    boat = activeChar.getBoat();
                                    if (boat != null) {
                                        value = String.valueOf(boat.getObjectId());
                                        break;
                                    }
                                    value = "0";
                                    break;
                                }
                                case "$title": {
                                    value = activeChar.getTitle();
                                    break;
                                }
                                case "$name": {
                                    value = activeChar.getName();
                                    break;
                                }
                                case "$x": {
                                    value = String.valueOf(activeChar.getX());
                                    break;
                                }
                                case "$y": {
                                    value = String.valueOf(activeChar.getY());
                                    break;
                                }
                                case "$z": {
                                    value = String.valueOf(activeChar.getZ());
                                    break;
                                }
                                case "$heading": {
                                    value = String.valueOf(activeChar.getHeading());
                                    break;
                                }
                                case "$toid": {
                                    value = String.valueOf(activeChar.getTargetId());
                                    break;
                                }
                                case "$tboid": {
                                    target = activeChar.getTarget();
                                    if (!GameUtils.isPlayable(target)) {
                                        break;
                                    }
                                    boat = target.getActingPlayer().getBoat();
                                    if (boat != null) {
                                        value = String.valueOf(boat.getObjectId());
                                        break;
                                    }
                                    value = "0";
                                    break;
                                }
                                case "$ttitle": {
                                    target = activeChar.getTarget();
                                    if (GameUtils.isCreature(target)) {
                                        value = ((Creature)target).getTitle();
                                        break;
                                    }
                                    value = "";
                                    break;
                                }
                                case "$tname": {
                                    target = activeChar.getTarget();
                                    if (target != null) {
                                        value = target.getName();
                                        break;
                                    }
                                    value = "";
                                    break;
                                }
                                case "$tx": {
                                    target = activeChar.getTarget();
                                    if (target != null) {
                                        value = String.valueOf(target.getX());
                                        break;
                                    }
                                    value = "0";
                                    break;
                                }
                                case "$ty": {
                                    target = activeChar.getTarget();
                                    if (target != null) {
                                        value = String.valueOf(target.getY());
                                        break;
                                    }
                                    value = "0";
                                    break;
                                }
                                case "$tz": {
                                    target = activeChar.getTarget();
                                    if (target != null) {
                                        value = String.valueOf(target.getZ());
                                        break;
                                    }
                                    value = "0";
                                    break;
                                }
                                case "$theading": {
                                    target = activeChar.getTarget();
                                    if (target != null) {
                                        value = String.valueOf(target.getHeading());
                                        break;
                                    }
                                    value = "0";
                                    break;
                                }
                            }
                            if (method.equals("sc") || method.equals("sb")) {
                                if (afp != null) {
                                    afp.addPart((byte)format2.charAt(i), value);
                                }
                            }
                            else {
                                this.write((byte)format2.charAt(i), value, bb);
                            }
                        }
                    }
                    if (method.equals("sc")) {
                        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)afp });
                    }
                    else if (method.equals("sb")) {
                        activeChar.broadcastPacket((ServerPacket)afp);
                    }
                    else if (bb != null) {
                        throw new UnsupportedOperationException("Not implemented yet!");
                    }
                    this.showValuesPage(activeChar, opCodes2, format2);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    this.showSendUsage(activeChar, null, null);
                    return false;
                }
            }
        }
        return true;
    }
    
    private boolean write(final byte b, final String string, final ByteBuffer buf) {
        if (b == 67 || b == 99) {
            buf.put(Integer.decode(string).byteValue());
            return true;
        }
        if (b == 68 || b == 100) {
            buf.putInt(Integer.decode(string));
            return true;
        }
        if (b == 72 || b == 104) {
            buf.putShort(Integer.decode(string).shortValue());
            return true;
        }
        if (b == 70 || b == 102) {
            buf.putDouble(Double.parseDouble(string));
            return true;
        }
        if (b == 83 || b == 115) {
            for (int len = string.length(), i = 0; i < len; ++i) {
                buf.putChar(string.charAt(i));
            }
            buf.putChar('\0');
            return true;
        }
        if (b == 66 || b == 98 || b == 88 || b == 120) {
            buf.put(new BigInteger(string).toByteArray());
            return true;
        }
        if (b == 81 || b == 113) {
            buf.putLong(Long.decode(string));
            return true;
        }
        return false;
    }
    
    public String[] getAdminCommandList() {
        return AdminPForge.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_forge", "admin_forge_values", "admin_forge_send" };
    }
}
