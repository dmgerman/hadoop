begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.protocol.commands
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|protocol
operator|.
name|commands
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|GeneratedMessage
import|;
end_import

begin_import
import|import
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
operator|.
name|IdentifiableEventPayload
import|;
end_import

begin_comment
comment|/**  * Command for the datanode with the destination address.  */
end_comment

begin_class
DECL|class|CommandForDatanode
specifier|public
class|class
name|CommandForDatanode
parameter_list|<
name|T
extends|extends
name|GeneratedMessage
parameter_list|>
implements|implements
name|IdentifiableEventPayload
block|{
DECL|field|datanodeId
specifier|private
specifier|final
name|UUID
name|datanodeId
decl_stmt|;
DECL|field|command
specifier|private
specifier|final
name|SCMCommand
argument_list|<
name|T
argument_list|>
name|command
decl_stmt|;
DECL|method|CommandForDatanode (UUID datanodeId, SCMCommand<T> command)
specifier|public
name|CommandForDatanode
parameter_list|(
name|UUID
name|datanodeId
parameter_list|,
name|SCMCommand
argument_list|<
name|T
argument_list|>
name|command
parameter_list|)
block|{
name|this
operator|.
name|datanodeId
operator|=
name|datanodeId
expr_stmt|;
name|this
operator|.
name|command
operator|=
name|command
expr_stmt|;
block|}
DECL|method|getDatanodeId ()
specifier|public
name|UUID
name|getDatanodeId
parameter_list|()
block|{
return|return
name|datanodeId
return|;
block|}
DECL|method|getCommand ()
specifier|public
name|SCMCommand
argument_list|<
name|T
argument_list|>
name|getCommand
parameter_list|()
block|{
return|return
name|command
return|;
block|}
DECL|method|getId ()
specifier|public
name|long
name|getId
parameter_list|()
block|{
return|return
name|command
operator|.
name|getId
argument_list|()
return|;
block|}
block|}
end_class

end_unit

