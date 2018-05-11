begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/**  * Identifier of an async event.  *  * @param<PAYLOAD> THe message payload type of this event.  */
end_comment

begin_interface
DECL|interface|Event
specifier|public
interface|interface
name|Event
parameter_list|<
name|PAYLOAD
parameter_list|>
block|{
comment|/**    * The type of the event payload. Payload contains all the required data    * to process the event.    *    */
DECL|method|getPayloadType ()
name|Class
argument_list|<
name|PAYLOAD
argument_list|>
name|getPayloadType
parameter_list|()
function_decl|;
comment|/**    * The human readable name of the event.    *    * Used for display in thread names    * and monitoring.    *    */
DECL|method|getName ()
name|String
name|getName
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

