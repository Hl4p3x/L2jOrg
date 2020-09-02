// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.slf4j.LoggerFactory;
import java.io.PrintStream;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Arrays;
import org.l2j.gameserver.network.SystemMessageId;
import org.slf4j.Logger;

public abstract class AbstractMessagePacket<T extends AbstractMessagePacket<?>> extends ServerPacket
{
    private static final Logger LOGGER;
    private static final SMParam[] EMPTY_PARAM_ARRAY;
    private static final byte TYPE_ELEMENTAL_SPIRIT = 26;
    private static final byte TYPE_FACTION_NAME = 24;
    private static final byte TYPE_BYTE = 20;
    private static final byte TYPE_POPUP_ID = 16;
    private static final byte TYPE_CLASS_ID = 15;
    private static final byte TYPE_SYSTEM_STRING = 13;
    private static final byte TYPE_PLAYER_NAME = 12;
    private static final byte TYPE_DOOR_NAME = 11;
    private static final byte TYPE_INSTANCE_NAME = 10;
    private static final byte TYPE_ELEMENT_NAME = 9;
    private static final byte TYPE_ZONE_NAME = 7;
    private static final byte TYPE_LONG_NUMBER = 6;
    private static final byte TYPE_CASTLE_NAME = 5;
    private static final byte TYPE_SKILL_NAME = 4;
    private static final byte TYPE_ITEM_NAME = 3;
    private static final byte TYPE_NPC_NAME = 2;
    private static final byte TYPE_INT_NUMBER = 1;
    private static final byte TYPE_TEXT = 0;
    private final SystemMessageId _smId;
    private SMParam[] _params;
    private int _paramIndex;
    
    public AbstractMessagePacket(final SystemMessageId smId) {
        if (smId == null) {
            throw new NullPointerException("SystemMessageId cannot be null!");
        }
        this._smId = smId;
        this._params = ((smId.getParamCount() > 0) ? new SMParam[smId.getParamCount()] : AbstractMessagePacket.EMPTY_PARAM_ARRAY);
    }
    
    public final int getId() {
        return this._smId.getId();
    }
    
    public final SystemMessageId getSystemMessageId() {
        return this._smId;
    }
    
    private void append(final SMParam param) {
        if (this._paramIndex >= this._params.length) {
            this._params = Arrays.copyOf(this._params, this._paramIndex + 1);
            this._smId.setParamCount(this._paramIndex + 1);
            if (param.getType() != 16) {
                AbstractMessagePacket.LOGGER.info(invokedynamic(makeConcatWithConstants:(ILorg/l2j/gameserver/network/SystemMessageId;)Ljava/lang/String;, this._paramIndex + 1, this._smId));
            }
        }
        this._params[this._paramIndex++] = param;
    }
    
    public final T addString(final String text) {
        this.append(new SMParam((byte)0, text));
        return (T)this;
    }
    
    public final T addCastleId(final int number) {
        this.append(new SMParam((byte)5, number));
        return (T)this;
    }
    
    public final T addInt(final int number) {
        this.append(new SMParam((byte)1, number));
        return (T)this;
    }
    
    public final T addLong(final long number) {
        this.append(new SMParam((byte)6, number));
        return (T)this;
    }
    
    public final T addPcName(final Player pc) {
        this.append(new SMParam((byte)12, pc.getAppearance().getVisibleName()));
        return (T)this;
    }
    
    public final T addDoorName(final int doorId) {
        this.append(new SMParam((byte)11, doorId));
        return (T)this;
    }
    
    public final T addNpcName(final Npc npc) {
        return this.addNpcName(npc.getTemplate());
    }
    
    public final T addNpcName(final Summon npc) {
        return this.addNpcName(npc.getId());
    }
    
    public final T addNpcName(final NpcTemplate template) {
        if (template.isUsingServerSideName()) {
            return this.addString(template.getName());
        }
        return this.addNpcName(template.getId());
    }
    
    public final T addNpcName(final int id) {
        this.append(new SMParam((byte)2, 1000000 + id));
        return (T)this;
    }
    
    public T addItemName(final Item item) {
        return this.addItemName(item.getTemplate());
    }
    
    public T addItemName(final ItemTemplate item) {
        if (item.getDisplayId() != item.getId()) {
            return this.addString(item.getName());
        }
        this.append(new SMParam((byte)3, item.getId()));
        return (T)this;
    }
    
    public final T addItemName(final int id) {
        return this.addItemName(ItemEngine.getInstance().getTemplate(id));
    }
    
    public final T addZoneName(final int x, final int y, final int z) {
        this.append(new SMParam((byte)7, new int[] { x, y, z }));
        return (T)this;
    }
    
    public final T addSkillName(final Skill skill) {
        if (skill.getId() != skill.getDisplayId()) {
            return this.addString(skill.getName());
        }
        return this.addSkillName(skill.getId(), skill.getLevel());
    }
    
    public final T addSkillName(final int id) {
        return this.addSkillName(id, 1);
    }
    
    public final T addSkillName(final int id, final int lvl) {
        this.append(new SMParam((byte)4, new int[] { id, lvl }));
        return (T)this;
    }
    
    public final T addAttribute(final int type) {
        this.append(new SMParam((byte)9, type));
        return (T)this;
    }
    
    public final T addSystemString(final int type) {
        this.append(new SMParam((byte)13, type));
        return (T)this;
    }
    
    public final T addClassId(final int type) {
        this.append(new SMParam((byte)15, type));
        return (T)this;
    }
    
    public final T addFactionName(final int factionId) {
        this.append(new SMParam((byte)24, factionId));
        return (T)this;
    }
    
    public final T addPopup(final int target, final int attacker, final int damage) {
        this.append(new SMParam((byte)16, new int[] { target, attacker, damage }));
        return (T)this;
    }
    
    public final T addByte(final int time) {
        this.append(new SMParam((byte)20, time));
        return (T)this;
    }
    
    public final T addInstanceName(final int type) {
        this.append(new SMParam((byte)10, type));
        return (T)this;
    }
    
    public final T addElementalSpirit(final int elementType) {
        this.append(new SMParam((byte)26, elementType));
        return (T)this;
    }
    
    protected void writeParamsSize(final int size) {
        this.writeByte(size);
    }
    
    protected void writeParamType(final int type) {
        this.writeByte(type);
    }
    
    protected final void writeMe() {
        this.writeParamsSize(this._params.length);
        for (int i = 0; i < this._paramIndex; ++i) {
            final SMParam param = this._params[i];
            this.writeParamType(param.getType());
            switch (param.getType()) {
                case 9:
                case 20:
                case 24:
                case 26: {
                    this.writeByte((byte)param.getIntValue());
                    break;
                }
                case 5:
                case 10:
                case 13:
                case 15: {
                    this.writeShort((short)param.getIntValue());
                    break;
                }
                case 1:
                case 2:
                case 3:
                case 11: {
                    this.writeInt(param.getIntValue());
                    break;
                }
                case 6: {
                    this.writeLong(param.getLongValue());
                    break;
                }
                case 0:
                case 12: {
                    this.writeString((CharSequence)param.getStringValue());
                    break;
                }
                case 4: {
                    final int[] array = param.getIntArrayValue();
                    this.writeInt(array[0]);
                    this.writeShort(array[1]);
                    break;
                }
                case 7:
                case 16: {
                    final int[] array = param.getIntArrayValue();
                    this.writeInt(array[0]);
                    this.writeInt(array[1]);
                    this.writeInt(array[2]);
                    break;
                }
            }
        }
    }
    
    public final void printMe(final PrintStream out) {
        out.println(98);
        out.println(this._smId.getId());
        out.println(this._params.length);
        for (final SMParam param : this._params) {
            switch (param.getType()) {
                case 0:
                case 12: {
                    out.println(param.getStringValue());
                    break;
                }
                case 6: {
                    out.println(param.getLongValue());
                    break;
                }
                case 1:
                case 2:
                case 3:
                case 5:
                case 9:
                case 10:
                case 11:
                case 13:
                case 15: {
                    out.println(param.getIntValue());
                    break;
                }
                case 16: {
                    final int[] array = param.getIntArrayValue();
                    out.println(array[0]);
                    out.println(array[1]);
                    out.println(array[2]);
                    break;
                }
                case 4: {
                    final int[] array = param.getIntArrayValue();
                    out.println(array[0]);
                    out.println(array[1]);
                    out.println(array[2]);
                    break;
                }
                case 7: {
                    final int[] array = param.getIntArrayValue();
                    out.println(array[0]);
                    out.println(array[1]);
                    out.println(array[2]);
                    break;
                }
            }
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)AbstractMessagePacket.class);
        EMPTY_PARAM_ARRAY = new SMParam[0];
    }
    
    private static final class SMParam
    {
        private final byte _type;
        private final Object _value;
        
        public SMParam(final byte type, final Object value) {
            this._type = type;
            this._value = value;
        }
        
        public final byte getType() {
            return this._type;
        }
        
        public final String getStringValue() {
            return (String)this._value;
        }
        
        public final int getIntValue() {
            return (int)this._value;
        }
        
        public final long getLongValue() {
            return (long)this._value;
        }
        
        public final int[] getIntArrayValue() {
            return (int[])this._value;
        }
    }
}
