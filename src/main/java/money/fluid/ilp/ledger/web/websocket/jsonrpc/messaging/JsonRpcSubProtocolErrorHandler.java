/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package money.fluid.ilp.ledger.web.websocket.jsonrpc.messaging;

import org.springframework.messaging.Message;
import org.springframework.web.socket.messaging.SubProtocolErrorHandler;

/**
 * A {@link SubProtocolErrorHandler} for use with JsonRPC.
 */
public class JsonRpcSubProtocolErrorHandler implements SubProtocolErrorHandler<byte[]> {

    private static byte[] EMPTY_PAYLOAD = new byte[0];


    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {

        // TODO: Implement this!

        throw new RuntimeException("Not yet implented!");

//        JsonRpcHeaderAccessor accessor = JsonRpcHeaderAccessor.create(JsonRpcCommand.ERROR);
//        accessor.setMessage(ex.getMessage());
//        accessor.setLeaveMutable(true);
//
//        JsonRpcHeaderAccessor clientHeaderAccessor = null;
//        if (clientMessage != null) {
//            clientHeaderAccessor = MessageHeaderAccessor.getAccessor(clientMessage, JsonRpcHeaderAccessor.class);
//            String receiptId = clientHeaderAccessor.getReceipt();
//            if (receiptId != null) {
//                accessor.setReceiptId(receiptId);
//            }
//        }
//
//        return handleInternal(accessor, EMPTY_PAYLOAD, ex, clientHeaderAccessor);
    }

    @Override
    public Message<byte[]> handleErrorMessageToClient(Message<byte[]> errorMessage) {

        // TODO: Implement this!

        throw new RuntimeException("Not yet implented!");

//        JsonRpcHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(errorMessage, JsonRpcHeaderAccessor.class);
//        Assert.notNull(accessor, "Expected STOMP headers");
//        if (!accessor.isMutable()) {
//            accessor = JsonRpcHeaderAccessor.wrap(errorMessage);
//        }
//        return handleInternal(accessor, errorMessage.getPayload(), null, null);
    }

//    protected Message<byte[]> handleInternal(
//            JsonRpcHeaderAccessor errorHeaderAccessor,
//            byte[] errorPayload, Throwable cause, JsonRpcHeaderAccessor clientHeaderAccessor
//    ) {
//
//        return MessageBuilder.createMessage(errorPayload, errorHeaderAccessor.getMessageHeaders());
//    }

}
