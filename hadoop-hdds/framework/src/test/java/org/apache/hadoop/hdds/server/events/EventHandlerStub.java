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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Dummy class for testing to collect all the received events.  */
end_comment

begin_class
DECL|class|EventHandlerStub
specifier|public
class|class
name|EventHandlerStub
parameter_list|<
name|PAYLOAD
parameter_list|>
implements|implements
name|EventHandler
argument_list|<
name|PAYLOAD
argument_list|>
block|{
DECL|field|receivedEvents
specifier|private
name|List
argument_list|<
name|PAYLOAD
argument_list|>
name|receivedEvents
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|onMessage (PAYLOAD payload, EventPublisher publisher)
specifier|public
name|void
name|onMessage
parameter_list|(
name|PAYLOAD
name|payload
parameter_list|,
name|EventPublisher
name|publisher
parameter_list|)
block|{
name|receivedEvents
operator|.
name|add
argument_list|(
name|payload
argument_list|)
expr_stmt|;
block|}
DECL|method|getReceivedEvents ()
specifier|public
name|List
argument_list|<
name|PAYLOAD
argument_list|>
name|getReceivedEvents
parameter_list|()
block|{
return|return
name|receivedEvents
return|;
block|}
block|}
end_class

end_unit

