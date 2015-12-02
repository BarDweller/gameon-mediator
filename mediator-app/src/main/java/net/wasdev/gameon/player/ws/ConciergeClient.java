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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 */
@ApplicationScoped
public class ConciergeClient {

	@Resource(lookup="conciergeUrl")
	String conciergeLocation;
	
	@Resource(lookup="conciergeQueryApiKey")
	String querySecret;
	
	private Client client;
	private WebTarget root;

	@PostConstruct
	public void initClient() {
		this.client = ClientBuilder.newClient();
				
		//add the apikey handler for the lookup requests.
		ApiKey apikey = new ApiKey("roomQuery","MyQuerySecret");		
		
		this.root = this.client.target(conciergeLocation);	
		this.root.register(apikey);

		Log.log(Level.FINER, this, "Concierge initialized with {0}", conciergeLocation);
	}

	/**
	 * Query the concierge: given the current room and the selected exit, where do we go?
	 * @param currentRoom Current room mediator
	 * @return new Room Id
	 */
	public RoomEndpointList findNextRoom(RoomMediator currentRoom, String exit) {
		RoomEndpointList roomEndpoints = null;

		if ( exit == null ) {
			// SOS or First Room: randomly grab a new room (start over with starting rooms)
			roomEndpoints = getRoomEndpoints();
		} else {
			roomEndpoints = getRoomEndpoints(currentRoom.getId(), exit);
		}

		return roomEndpoints;
	}

	public RoomEndpointList getRoomEndpoints() {
		WebTarget target = this.root.path("startingRoom");
		return getRoomList(target);
	}

	public RoomEndpointList getRoomEndpoints(String roomId) {
		WebTarget target = this.root.path("rooms/{roomId}").resolveTemplate("roomId", roomId);
		return getRoomList(target);
	}

	public RoomEndpointList getRoomEndpoints(String roomId, String exit) {
		WebTarget target = this.root.path("rooms/{roomId}/{exit}").resolveTemplate("roomId", roomId).resolveTemplate("exit", exit);
		return getRoomList(target);
	}

	/**
	 * Get the list of viable endpoints for the specified room
	 * @param target
	 * @return
	 */
	protected RoomEndpointList getRoomList(WebTarget target) {
		Log.log(Level.FINER, this, "making request to {0} for room", target.getUri().toString());
		try {
			//pojo magic binding wasn't working..for some requests only it would complain it couldn't find the reader for the object
			//so we'll do it the long way until we figure out why..

			//TODO: debug why we can't use pojo bindings for room change requests..

			// Make GET request using the specified target (see methods above)
			String resultStr = target.request(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.header("Content-type", "application/json")
					.get(String.class);

			JsonReader reader = Json.createReader(new StringReader(resultStr));
			JsonObject result = reader.readObject();

			JsonObject rel = (JsonObject)result.get("rel"); // room endpoint list
			if( rel != null ) {
				RoomEndpointList res = new RoomEndpointList();

				JsonString id = rel.getJsonString("roomId");
				res.setRoomId(id.getString());

				JsonArray exits = rel.getJsonArray("endpoints");
				ArrayList<String> strexits = new ArrayList<String>();
				if(exits!=null){
					for(JsonValue e : exits){
						JsonString s = (JsonString)e;
						strexits.add(s.getString());
					}
				}
				res.setEndpoints(strexits);

				return res;
			} else {
				Log.log(Level.FINER, this, "Room list was lacking any rel element uri: {0} result: {1}",
						target.getUri().toString(),
						resultStr);
			}

			// Sadly, no endpoints found!
			return null;
		} catch (ResponseProcessingException rpe) {
			Response response = rpe.getResponse();
			Log.log(Level.FINER, this, "Exception fetching room list uri: {0} resp code: {1} data: {2}",
					target.getUri().toString(),
					response.getStatusInfo().getStatusCode()+" "+response.getStatusInfo().getReasonPhrase(),
					response.readEntity(String.class));
			Log.log(Level.FINEST, this, "Exception fetching room list", rpe);
		} catch ( ProcessingException|WebApplicationException ex ) {
			Log.log(Level.FINEST, this, "Exception fetching room list ("+target.getUri().toString()+")", ex);
		}

		// Sadly, badness happened while trying to get the endpoints
		return null;
	}

	public static class RoomEndpointListWrapper {
		RoomEndpointList rel;

		public RoomEndpointListWrapper(){}

		public RoomEndpointList getRel() {
			return rel;
		}

		public void setRel(RoomEndpointList rel) {
			this.rel = rel;
		}
	}

	public static class RoomEndpointList {
		String roomId;
		List<String> endpoints;

		public RoomEndpointList(){}

		/**
		 * @return the roomId
		 */
		public String getRoomId() {
			return roomId;
		}

		/**
		 * @param roomId the roomId to set
		 */
		public void setRoomId(String roomId) {
			this.roomId = roomId;
		}

		/**
		 * @return the endpoints
		 */
		public List<String> getEndpoints() {
			return endpoints;
		}

		/**
		 * @param endpoints the endpoints to set
		 */
		public void setEndpoints(List<String> endpoints) {
			this.endpoints = endpoints;
		}

		/**
		 * @return
		 */
		public String getRoomName() {
			return roomId;
		}
	}
}
