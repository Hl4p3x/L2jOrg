// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.l2j.gameserver.network.serverpackets.ShortCutRegister;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.l2j.commons.util.Util;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.ShortcutDAO;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.Objects;
import org.l2j.gameserver.enums.ShortcutType;
import io.github.joealisson.primitive.CHashIntMap;
import java.util.BitSet;
import org.l2j.gameserver.data.database.data.Shortcut;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.model.actor.instance.Player;

public class Shortcuts
{
    private final Player owner;
    private final IntMap<Shortcut> shortcuts;
    private final BitSet activeShortcuts;
    private int nextAutoShortcut;
    
    public Shortcuts(final Player owner) {
        this.shortcuts = (IntMap<Shortcut>)new CHashIntMap();
        this.activeShortcuts = new BitSet(240);
        this.nextAutoShortcut = 0;
        this.owner = owner;
    }
    
    public void registerShortCut(final Shortcut shortcut) {
        if (shortcut.getType() == ShortcutType.ITEM) {
            final Item item = this.owner.getInventory().getItemByObjectId(shortcut.getShortcutId());
            if (Objects.isNull(item)) {
                return;
            }
            shortcut.setSharedReuseGroup(item.getSharedReuseGroup());
        }
        this.registerShortCutInDb(shortcut, (Shortcut)this.shortcuts.put(shortcut.getClientId(), (Object)shortcut));
    }
    
    private void registerShortCutInDb(final Shortcut shortcut, final Shortcut oldShortCut) {
        if (Objects.nonNull(oldShortCut)) {
            this.deleteShortcutFromDb(oldShortCut);
        }
        shortcut.setPlayerId(this.owner.getObjectId());
        shortcut.setClassIndex(this.owner.getClassIndex());
        ((ShortcutDAO)DatabaseAccess.getDAO((Class)ShortcutDAO.class)).save((Object)shortcut);
    }
    
    public void setActive(final int room, final boolean active) {
        Util.doIfNonNull((Object)this.shortcuts.get(room), s -> {
            s.setActive(active);
            if (Shortcut.AUTO_POTION_ROOM != room) {
                if (active) {
                    this.activeShortcuts.set(room);
                }
                else {
                    this.activeShortcuts.clear(room);
                }
            }
        });
    }
    
    public Shortcut nextAutoShortcut() {
        if (this.activeShortcuts.isEmpty()) {
            return null;
        }
        Shortcut shortcut = null;
        int next = this.activeShortcuts.nextSetBit(this.nextAutoShortcut);
        if (next == -1) {
            final BitSet activeShortcuts = this.activeShortcuts;
            final int n = 0;
            this.nextAutoShortcut = n;
            next = activeShortcuts.nextSetBit(n);
        }
        if (next >= 0) {
            shortcut = (Shortcut)this.shortcuts.get(next);
            if (Objects.isNull(shortcut)) {
                this.deleteShortcut(next);
                this.activeShortcuts.clear(next);
            }
            this.nextAutoShortcut = next + 1;
        }
        return shortcut;
    }
    
    public void resetNextAutoShortcut() {
        this.nextAutoShortcut = 0;
    }
    
    private void deleteShortcutFromDb(final Shortcut shortcut) {
        ((ShortcutDAO)DatabaseAccess.getDAO((Class)ShortcutDAO.class)).delete(this.owner.getObjectId(), shortcut.getClientId(), this.owner.getClassIndex());
    }
    
    public void deleteShortcuts(final Predicate<Shortcut> filter) {
        this.shortcuts.values().stream().filter(filter).forEach(s -> this.deleteShortcut(s.getClientId()));
    }
    
    public void deleteShortcuts() {
        this.shortcuts.clear();
        ((ShortcutDAO)DatabaseAccess.getDAO((Class)ShortcutDAO.class)).deleteFromSubclass(this.owner.getObjectId(), this.owner.getClassIndex());
    }
    
    public void forEachShortcut(final Consumer<Shortcut> action) {
        this.shortcuts.values().forEach(action);
    }
    
    public int getAmount() {
        return this.shortcuts.size();
    }
    
    public Shortcut getShortcut(final int room) {
        Shortcut sc = (Shortcut)this.shortcuts.get(room);
        if (Objects.nonNull(sc) && sc.getType() == ShortcutType.ITEM && Objects.isNull(this.owner.getInventory().getItemByObjectId(sc.getShortcutId()))) {
            this.deleteShortcut(sc.getClientId());
            sc = null;
        }
        return sc;
    }
    
    public void deleteShortcut(final int room) {
        final Shortcut old = (Shortcut)this.shortcuts.remove(room);
        if (Objects.isNull(old) || Objects.isNull(this.owner)) {
            return;
        }
        this.deleteShortcutFromDb(old);
    }
    
    public void deleteShortCutByObjectId(final int objectId) {
        for (final Shortcut shortcut : this.shortcuts.values()) {
            if (shortcut.getType() == ShortcutType.ITEM && shortcut.getShortcutId() == objectId) {
                this.deleteShortcut(shortcut.getClientId());
                break;
            }
        }
    }
    
    public void restoreMe() {
        this.shortcuts.clear();
        ((ShortcutDAO)DatabaseAccess.getDAO((Class)ShortcutDAO.class)).findByPlayer(this.owner.getObjectId(), this.owner.getClassIndex()).forEach(s -> this.shortcuts.put(s.getClientId(), (Object)s));
        Item item;
        this.forEachShortcut(s -> {
            if (s.getType() == ShortcutType.ITEM) {
                item = this.owner.getInventory().getItemByObjectId(s.getShortcutId());
                if (Objects.isNull(item)) {
                    this.deleteShortcut(s.getClientId());
                }
                else if (item.isEtcItem()) {
                    s.setSharedReuseGroup(item.getSharedReuseGroup());
                }
            }
            if (s.isActive()) {
                this.activeShortcuts.set(s.getClientId());
            }
        });
    }
    
    public void storeMe() {
        final ShortcutDAO dao = (ShortcutDAO)DatabaseAccess.getDAO((Class)ShortcutDAO.class);
        final Collection values = this.shortcuts.values();
        final ShortcutDAO obj = dao;
        Objects.requireNonNull(obj);
        values.forEach(obj::save);
    }
    
    public void updateShortCuts(final int skillId, final int skillLevel, final int skillSubLevel) {
        for (final Shortcut sc : this.shortcuts.values()) {
            if (sc.getShortcutId() == skillId && sc.getType() == ShortcutType.SKILL) {
                final Shortcut newsc = new Shortcut(sc.getClientId(), sc.getType(), sc.getShortcutId(), skillLevel, skillSubLevel, 1);
                this.owner.sendPacket(new ShortCutRegister(newsc));
                this.owner.registerShortCut(newsc);
            }
        }
    }
}
