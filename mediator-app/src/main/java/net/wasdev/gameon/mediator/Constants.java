/*******************************************************************************
 * Copyright (c) 2015 IBM Corp.
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
package net.wasdev.gameon.mediator;

/**
 * Protocol and message constants
 *
 */
public interface Constants {

    /**
     * Routing: Mediator sends hello when a player enters the room (new room)
     */
    String ROOM_HELLO = "roomHello";

    /** Routing: Mediator sends goodbye when a player leaves the room */
    String ROOM_GOODBYE = "roomGoodbye";

    /** Routing: Message directed from room to player */
    String PLAYER = "player";

    /**
     * Routing: Message from room to mediator indicating that the player
     * successfully opened a door. This will tell the mediator to attempt a room
     * change.
     */
    String PLAYER_LOCATION = "playerLocation";

    /**
     * Routing: sent by the player to attempt a "rescue" (a jump from one room
     * to a random other room if they get stuck (or feel like exploring).
     */
    String SOS = "sos";

    /**
     * The id of the first room (special, provided by the mediator itself).
     */
    String FIRST_ROOM = "TheFirstRoom";

    String ROOM_ID = "roomId";
    String BOOKMARK = "bookmark";

    String USERNAME = "username";
}