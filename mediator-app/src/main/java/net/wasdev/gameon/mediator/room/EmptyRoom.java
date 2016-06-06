/*******************************************************************************
 * Copyright (c) 2016 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.wasdev.gameon.mediator.room;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.wasdev.gameon.mediator.MapClient;
import net.wasdev.gameon.mediator.MediatorNexus;
import net.wasdev.gameon.mediator.models.Site;

public class EmptyRoom extends AbstractRoomMediator {

    public static final String EMPTY_ROOMNAME = "emptyRoom";
    public static final String EMPTY_FULLNAME = "Empty Room";

    static final List<String> EMTPY_ROOMS = Collections.unmodifiableList(Arrays.asList(
            "Said your name, in an empty room",
            "Is that... padding on the walls?",
            "The center of the room is completely empty",
            "Nothing even remotely interesting is happening in here",
            "This room looks suspiciously like a bunch of other rooms; it's like they're all the same."));
    
    public EmptyRoom(MediatorNexus.View nexus, MapClient mapClient, Site site) {
        super(nexus, mapClient, site);
    }

    @Override
    public String getName() {
        return EMPTY_ROOMNAME;
    }

    @Override
    public String getFullName() {
        return EMPTY_FULLNAME;
    }

    @Override
    public String getDescription() {
        int index = RoomUtils.random.nextInt(EMTPY_ROOMS.size());
        return EMTPY_ROOMS.get(index);
    }
    
    @Override
    public Type getType() {
        return Type.EMPTY;
    }
}
