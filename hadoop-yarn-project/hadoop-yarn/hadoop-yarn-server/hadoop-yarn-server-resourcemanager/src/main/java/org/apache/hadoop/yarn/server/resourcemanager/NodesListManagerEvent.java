begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager
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
name|event
operator|.
name|AbstractEvent
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
DECL|class|NodesListManagerEvent
specifier|public
class|class
name|NodesListManagerEvent
extends|extends
name|AbstractEvent
argument_list|<
name|NodesListManagerEventType
argument_list|>
block|{
DECL|field|node
specifier|private
specifier|final
name|RMNode
name|node
decl_stmt|;
DECL|method|NodesListManagerEvent (NodesListManagerEventType type, RMNode node)
specifier|public
name|NodesListManagerEvent
parameter_list|(
name|NodesListManagerEventType
name|type
parameter_list|,
name|RMNode
name|node
parameter_list|)
block|{
name|super
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|this
operator|.
name|node
operator|=
name|node
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
block|}
end_class

end_unit

