begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.nodelabels
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
name|nodelabels
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|conf
operator|.
name|Configuration
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
name|NodeId
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
name|NodeLabel
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
name|conf
operator|.
name|YarnConfiguration
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
name|nodelabels
operator|.
name|CommonNodeLabelsManager
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
name|nodelabels
operator|.
name|NodeLabelsStore
import|;
end_import

begin_class
DECL|class|NullRMNodeLabelsManager
specifier|public
class|class
name|NullRMNodeLabelsManager
extends|extends
name|RMNodeLabelsManager
block|{
DECL|field|lastNodeToLabels
name|Map
argument_list|<
name|NodeId
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|lastNodeToLabels
init|=
literal|null
decl_stmt|;
DECL|field|lastAddedlabels
name|Collection
argument_list|<
name|String
argument_list|>
name|lastAddedlabels
init|=
literal|null
decl_stmt|;
DECL|field|lastRemovedlabels
name|Collection
argument_list|<
name|String
argument_list|>
name|lastRemovedlabels
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|initNodeLabelStore (Configuration conf)
specifier|public
name|void
name|initNodeLabelStore
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
operator|new
name|NodeLabelsStore
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|recover
parameter_list|()
throws|throws
name|IOException
block|{
comment|// do nothing
block|}
annotation|@
name|Override
specifier|public
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|CommonNodeLabelsManager
name|mgr
parameter_list|)
throws|throws
name|Exception
block|{        }
annotation|@
name|Override
specifier|public
name|void
name|removeClusterNodeLabels
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|labels
parameter_list|)
throws|throws
name|IOException
block|{
comment|// do nothing
block|}
annotation|@
name|Override
specifier|public
name|void
name|updateNodeToLabelsMappings
parameter_list|(
name|Map
argument_list|<
name|NodeId
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|nodeToLabels
parameter_list|)
throws|throws
name|IOException
block|{
comment|// do nothing
block|}
annotation|@
name|Override
specifier|public
name|void
name|storeNewClusterNodeLabels
parameter_list|(
name|List
argument_list|<
name|NodeLabel
argument_list|>
name|label
parameter_list|)
throws|throws
name|IOException
block|{
comment|// do nothing
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
comment|// do nothing
block|}
block|}
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|initDispatcher (Configuration conf)
specifier|protected
name|void
name|initDispatcher
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
operator|.
name|dispatcher
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startDispatcher ()
specifier|protected
name|void
name|startDispatcher
parameter_list|()
block|{
comment|// do nothing
block|}
annotation|@
name|Override
DECL|method|stopDispatcher ()
specifier|protected
name|void
name|stopDispatcher
parameter_list|()
block|{
comment|// do nothing
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|protected
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
comment|// always enable node labels while using MemoryRMNodeLabelsManager
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NODE_LABELS_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

