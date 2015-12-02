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

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * @author elh
 *
 */
public class RoutedMessageEncoder implements Encoder.Text<RoutedMessage> {

	/**
	 * Simple encoder: relies in the RoutedMessage to stringify itself
	 * @see javax.websocket.Encoder.Text#encode(java.lang.Object)
	 */
	@Override
	public String encode(RoutedMessage object) throws EncodeException {
		return object.toString();
	}

	@Override
	public void init(EndpointConfig ec) {
	}

	@Override
	public void destroy() {
	}
}
