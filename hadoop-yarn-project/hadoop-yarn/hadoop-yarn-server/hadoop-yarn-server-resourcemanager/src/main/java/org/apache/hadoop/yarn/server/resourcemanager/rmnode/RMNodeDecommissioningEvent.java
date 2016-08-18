begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.rmnode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|rmnode
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|NodeId
import|;
end_import

begin_comment
comment|/**  * RMNode Decommissioning Event.  *  */
end_comment

begin_class
DECL|class|RMNodeDecommissioningEvent
specifier|public
class|class
name|RMNodeDecommissioningEvent
extends|extends
name|RMNodeEvent
block|{
comment|// Optional decommissioning timeout in second.
DECL|field|decommissioningTimeout
specifier|private
specifier|final
name|Integer
name|decommissioningTimeout
decl_stmt|;
comment|// Create instance with optional timeout
comment|// (timeout could be null which means use default).
DECL|method|RMNodeDecommissioningEvent (NodeId nodeId, Integer timeout)
specifier|public
name|RMNodeDecommissioningEvent
parameter_list|(
name|NodeId
name|nodeId
parameter_list|,
name|Integer
name|timeout
parameter_list|)
block|{
name|super
argument_list|(
name|nodeId
argument_list|,
name|RMNodeEventType
operator|.
name|GRACEFUL_DECOMMISSION
argument_list|)
expr_stmt|;
name|this
operator|.
name|decommissioningTimeout
operator|=
name|timeout
expr_stmt|;
block|}
DECL|method|getDecommissioningTimeout ()
specifier|public
name|Integer
name|getDecommissioningTimeout
parameter_list|()
block|{
return|return
name|this
operator|.
name|decommissioningTimeout
return|;
block|}
block|}
end_class

end_unit

