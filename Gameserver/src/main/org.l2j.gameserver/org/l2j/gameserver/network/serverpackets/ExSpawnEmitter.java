/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author KenM
 * @author JoeAlisson
 */
public class ExSpawnEmitter extends ServerPacket {
    private final int attackerId;
    private final int targetId;
    private final SpawnEmitterType type;

    private ExSpawnEmitter(int attackerId, int targetId, SpawnEmitterType type) {
        this.attackerId = attackerId;
        this.targetId = targetId;
        this.type = type;

    }

    public ExSpawnEmitter(Creature attacker, Creature target, SpawnEmitterType type) {
        this(attacker.getObjectId(), target.getObjectId(), type);
    }

    public ExSpawnEmitter(Creature creature, SpawnEmitterType type) {
        this(creature.getObjectId(), creature.getObjectId(), type);
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_SPAWN_EMITTER);

        writeInt(targetId);
        writeInt(attackerId);
        writeInt(type.ordinal());
    }

    public enum SpawnEmitterType {
        BLUE_SOUL_EATEN,
        YELLOW_UNK,
        WHITE_SOUL,
        BLACK_SOUL,
    }

}