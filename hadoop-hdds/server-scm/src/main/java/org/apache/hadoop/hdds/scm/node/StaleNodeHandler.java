begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.node
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|node
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
name|hdds
operator|.
name|protocol
operator|.
name|DatanodeDetails
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
name|scm
operator|.
name|node
operator|.
name|states
operator|.
name|Node2ContainerMap
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
name|EventHandler
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
name|EventPublisher
import|;
end_import

begin_comment
comment|/**  * Handles Stale node event.  */
end_comment

begin_class
DECL|class|StaleNodeHandler
specifier|public
class|class
name|StaleNodeHandler
implements|implements
name|EventHandler
argument_list|<
name|DatanodeDetails
argument_list|>
block|{
DECL|field|node2ContainerMap
specifier|private
specifier|final
name|Node2ContainerMap
name|node2ContainerMap
decl_stmt|;
DECL|method|StaleNodeHandler (Node2ContainerMap node2ContainerMap)
specifier|public
name|StaleNodeHandler
parameter_list|(
name|Node2ContainerMap
name|node2ContainerMap
parameter_list|)
block|{
name|this
operator|.
name|node2ContainerMap
operator|=
name|node2ContainerMap
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onMessage (DatanodeDetails datanodeDetails, EventPublisher publisher)
specifier|public
name|void
name|onMessage
parameter_list|(
name|DatanodeDetails
name|datanodeDetails
parameter_list|,
name|EventPublisher
name|publisher
parameter_list|)
block|{
comment|//TODO: logic to handle stale node.
block|}
block|}
end_class

end_unit

