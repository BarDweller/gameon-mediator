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
package net.wasdev.gameon.player.ws;

import java.io.IOException;
import java.util.logging.Level;

import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

/**
 * Server-side endpoint for the Player Client (phone/browser).
 * This class is versioned to allow for controlled changes to data
 * sent over the wire. It is also scoped to the player's id:
 * a broadcast can be used to send responses back to all of a
 * player's devices if connected to more than one.
 *
 */
@ServerEndpoint(value = "/ws1/{userId}",
decoders = RoutedMessageDecoder.class,
encoders = RoutedMessageEncoder.class)
public class PlayerServerEndpoint {
	
	/** CDI injection of player session manager */
	@Inject
	PlayerSessionManager playerSessionManager;

	/** CDI injection of Connection Utilities (consistent send/receive/error handling) */
	@Inject
	ConnectionUtils connectionUtils;


	/**
	 * Called when a new connection has been established to this endpoint.
	 *
	 * @param session
	 * @param ec
	 */
	@OnOpen
	public void onOpen(@PathParam("userId") String userId, Session session, EndpointConfig ec) {
		Log.log(Level.FINER, session, "client open - {0}", userId);

		System.out.println(session);
		System.out.println(session.getQueryString());
		System.out.println(session.getUserProperties());
	}

	/**
	 * Called when the connection is closed (cleanup)
	 *
	 * @param session
	 * @param reason
	 */
	@OnClose
	public void onClose(@PathParam("userId") String userId, Session session, CloseReason reason) {
		Log.log(Level.FINER, session, "client closed - {0}: {1}", userId, reason);

		PlayerConnectionMediator ps = playerSessionManager.getPlayerSession(session);
		playerSessionManager.suspendSession(ps);
	}

	/**
	 * Message is received from the JS client
	 *
	 * @param message
	 * @param session
	 * @throws IOException
	 */
	@OnMessage
	public void onMessage(@PathParam("userId") String userId, RoutedMessage message, Session session) throws IOException {
		Log.log(Level.FINEST, session, "received from client {0}: {1}", userId, message);

		switch(message.getFlowTarget()) {
			case Constants.CLIENT_READY : {
				// create a new or resume an existing player session
				PlayerConnectionMediator ps = playerSessionManager.startSession(session, userId, message);
				playerSessionManager.setPlayerSession(session, ps);
				break;
			}
			default : {
				PlayerConnectionMediator ps = playerSessionManager.getPlayerSession(session);

				// after a restart we may get messages before we've re-established a
				// session or connection to a room. These are dropped.
				if( ps == null) {
					Log.log(Level.FINEST, session, "no session, dropping message from client {0}: {1}", userId, message);
				} else {
					ps.sendToRoom(message);
				}
				break;
			}
		}
	}

	@OnError
	public void onError(@PathParam("userId") String userId, Session session, Throwable t) {
		Log.log(Level.FINER, session, "oops for client "+userId+" connection", t);

		connectionUtils.tryToClose(session,
				new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, t.getClass().getName()));
	}
}
