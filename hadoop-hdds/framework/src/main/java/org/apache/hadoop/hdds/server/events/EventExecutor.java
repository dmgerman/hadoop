begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.server.events
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|server
operator|.
name|events
package|;
end_package

begin_comment
comment|/**  * Executors defined the  way how an EventHandler should be called.  *<p>  * Executors are used only by the EventQueue and they do the thread separation  * between the caller and the EventHandler.  *<p>  * Executors should guarantee that only one thread is executing one  * EventHandler at the same time.  *  * @param<PAYLOAD> the payload type of the event.  */
end_comment

begin_interface
DECL|interface|EventExecutor
specifier|public
interface|interface
name|EventExecutor
parameter_list|<
name|PAYLOAD
parameter_list|>
extends|extends
name|AutoCloseable
block|{
comment|/**    * Process an event payload.    *    * @param handler      the handler to process the payload    * @param eventPayload to be processed.    * @param publisher    to send response/other message forward to the chain.    */
DECL|method|onMessage (EventHandler<PAYLOAD> handler, PAYLOAD eventPayload, EventPublisher publisher)
name|void
name|onMessage
parameter_list|(
name|EventHandler
argument_list|<
name|PAYLOAD
argument_list|>
name|handler
parameter_list|,
name|PAYLOAD
name|eventPayload
parameter_list|,
name|EventPublisher
name|publisher
parameter_list|)
function_decl|;
comment|/**    * Return the number of the failed events.    */
DECL|method|failedEvents ()
name|long
name|failedEvents
parameter_list|()
function_decl|;
comment|/**    * Return the number of the processed events.    */
DECL|method|successfulEvents ()
name|long
name|successfulEvents
parameter_list|()
function_decl|;
comment|/**    * Return the number of the not-yet processed events.    */
DECL|method|queuedEvents ()
name|long
name|queuedEvents
parameter_list|()
function_decl|;
comment|/**    * The human readable name for the event executor.    *<p>    * Used in monitoring and logging.    *    */
DECL|method|getName ()
name|String
name|getName
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

