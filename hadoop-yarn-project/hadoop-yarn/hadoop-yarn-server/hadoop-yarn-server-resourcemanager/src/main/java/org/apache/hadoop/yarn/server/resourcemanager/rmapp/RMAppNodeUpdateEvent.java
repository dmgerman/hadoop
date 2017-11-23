begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.rmapp
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
name|rmapp
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
name|ApplicationId
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|NodeUpdateType
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|rmnode
operator|.
name|RMNode
import|;
end_import

begin_class
DECL|class|RMAppNodeUpdateEvent
specifier|public
class|class
name|RMAppNodeUpdateEvent
extends|extends
name|RMAppEvent
block|{
DECL|enum|RMAppNodeUpdateType
specifier|public
enum|enum
name|RMAppNodeUpdateType
block|{
DECL|enumConstant|NODE_USABLE
name|NODE_USABLE
block|,
DECL|enumConstant|NODE_UNUSABLE
name|NODE_UNUSABLE
block|,
DECL|enumConstant|NODE_DECOMMISSIONING
name|NODE_DECOMMISSIONING
block|;
DECL|method|convertToNodeUpdateType ( RMAppNodeUpdateType rmAppNodeUpdateType)
specifier|public
specifier|static
name|NodeUpdateType
name|convertToNodeUpdateType
parameter_list|(
name|RMAppNodeUpdateType
name|rmAppNodeUpdateType
parameter_list|)
block|{
return|return
name|NodeUpdateType
operator|.
name|valueOf
argument_list|(
name|rmAppNodeUpdateType
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|field|node
specifier|private
specifier|final
name|RMNode
name|node
decl_stmt|;
DECL|field|updateType
specifier|private
specifier|final
name|RMAppNodeUpdateType
name|updateType
decl_stmt|;
DECL|method|RMAppNodeUpdateEvent (ApplicationId appId, RMNode node, RMAppNodeUpdateType updateType)
specifier|public
name|RMAppNodeUpdateEvent
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|RMNode
name|node
parameter_list|,
name|RMAppNodeUpdateType
name|updateType
parameter_list|)
block|{
name|super
argument_list|(
name|appId
argument_list|,
name|RMAppEventType
operator|.
name|NODE_UPDATE
argument_list|)
expr_stmt|;
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
name|this
operator|.
name|updateType
operator|=
name|updateType
expr_stmt|;
block|}
DECL|method|getNode ()
specifier|public
name|RMNode
name|getNode
parameter_list|()
block|{
return|return
name|node
return|;
block|}
DECL|method|getUpdateType ()
specifier|public
name|RMAppNodeUpdateType
name|getUpdateType
parameter_list|()
block|{
return|return
name|updateType
return|;
block|}
block|}
end_class

end_unit

