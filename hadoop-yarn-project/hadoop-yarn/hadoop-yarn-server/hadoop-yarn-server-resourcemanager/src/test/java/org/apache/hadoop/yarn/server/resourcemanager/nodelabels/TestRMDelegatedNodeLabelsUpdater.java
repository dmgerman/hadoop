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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

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
name|api
operator|.
name|records
operator|.
name|Resource
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
name|exceptions
operator|.
name|YarnException
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
name|NodeLabelTestBase
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
name|api
operator|.
name|protocolrecords
operator|.
name|RegisterNodeManagerRequest
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
name|MockRM
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
name|ResourceManager
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
name|ResourceTrackerService
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
name|utils
operator|.
name|BuilderUtils
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
name|util
operator|.
name|Records
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
name|util
operator|.
name|YarnVersionInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
import|;
end_import

begin_class
DECL|class|TestRMDelegatedNodeLabelsUpdater
specifier|public
class|class
name|TestRMDelegatedNodeLabelsUpdater
extends|extends
name|NodeLabelTestBase
block|{
DECL|field|conf
specifier|private
name|YarnConfiguration
name|conf
decl_stmt|;
DECL|field|nodeLabelsMap
specifier|private
specifier|static
name|Map
argument_list|<
name|NodeId
argument_list|,
name|Set
argument_list|<
name|NodeLabel
argument_list|>
argument_list|>
name|nodeLabelsMap
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|conf
operator|=
operator|new
name|YarnConfiguration
argument_list|()
expr_stmt|;
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
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NODELABEL_CONFIGURATION_TYPE
argument_list|,
name|YarnConfiguration
operator|.
name|DELEGATED_CENTALIZED_NODELABEL_CONFIGURATION_TYPE
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
name|YarnConfiguration
operator|.
name|RM_NODE_LABELS_PROVIDER_CONFIG
argument_list|,
name|DummyRMNodeLabelsMappingProvider
operator|.
name|class
argument_list|,
name|RMNodeLabelsMappingProvider
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRMNodeLabelsMappingProviderConfiguration ()
specifier|public
name|void
name|testRMNodeLabelsMappingProviderConfiguration
parameter_list|()
block|{
name|conf
operator|.
name|unset
argument_list|(
name|YarnConfiguration
operator|.
name|RM_NODE_LABELS_PROVIDER_CONFIG
argument_list|)
expr_stmt|;
try|try
block|{
name|MockRM
name|rm
init|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|rm
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Expected an exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// expected an exception
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"RMNodeLabelsMappingProvider should be configured"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testWithNodeLabelUpdateEnabled ()
specifier|public
name|void
name|testWithNodeLabelUpdateEnabled
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|setLong
argument_list|(
name|YarnConfiguration
operator|.
name|RM_NODE_LABELS_PROVIDER_FETCH_INTERVAL_MS
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|MockRM
name|rm
init|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|rm
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMDelegatedNodeLabelsUpdater
argument_list|()
operator|.
name|nodeLabelsUpdateInterval
operator|=
literal|3
operator|*
literal|1000
expr_stmt|;
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
name|RMNodeLabelsManager
name|mgr
init|=
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|getNodeLabelManager
argument_list|()
decl_stmt|;
name|mgr
operator|.
name|addToCluserNodeLabelsWithDefaultExclusivity
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"x"
argument_list|,
literal|"y"
argument_list|)
argument_list|)
expr_stmt|;
name|NodeId
name|nodeId
init|=
name|toNodeId
argument_list|(
literal|"h1:1234"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|mgr
operator|.
name|getLabelsOnNode
argument_list|(
name|nodeId
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|updateNodeLabels
argument_list|(
name|nodeId
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|registerNode
argument_list|(
name|rm
argument_list|,
name|nodeId
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|4000
argument_list|)
expr_stmt|;
name|assertCollectionEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"x"
argument_list|)
argument_list|,
name|mgr
operator|.
name|getLabelsOnNode
argument_list|(
name|nodeId
argument_list|)
argument_list|)
expr_stmt|;
comment|// Ensure that node labels are updated if NodeLabelsProvider
comment|// gives different labels
name|updateNodeLabels
argument_list|(
name|nodeId
argument_list|,
literal|"y"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|4000
argument_list|)
expr_stmt|;
name|assertCollectionEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"y"
argument_list|)
argument_list|,
name|mgr
operator|.
name|getLabelsOnNode
argument_list|(
name|nodeId
argument_list|)
argument_list|)
expr_stmt|;
name|rm
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWithNodeLabelUpdateDisabled ()
specifier|public
name|void
name|testWithNodeLabelUpdateDisabled
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|setLong
argument_list|(
name|YarnConfiguration
operator|.
name|RM_NODE_LABELS_PROVIDER_FETCH_INTERVAL_MS
argument_list|,
name|RMDelegatedNodeLabelsUpdater
operator|.
name|DISABLE_DELEGATED_NODE_LABELS_UPDATE
argument_list|)
expr_stmt|;
name|MockRM
name|rm
init|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|rm
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMDelegatedNodeLabelsUpdater
argument_list|()
operator|.
name|nodeLabelsUpdateInterval
operator|=
literal|3
operator|*
literal|1000
expr_stmt|;
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
name|RMNodeLabelsManager
name|mgr
init|=
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|getNodeLabelManager
argument_list|()
decl_stmt|;
name|mgr
operator|.
name|addToCluserNodeLabelsWithDefaultExclusivity
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|NodeId
name|nodeId
init|=
name|toNodeId
argument_list|(
literal|"h1:1234"
argument_list|)
decl_stmt|;
name|updateNodeLabels
argument_list|(
name|nodeId
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|registerNode
argument_list|(
name|rm
argument_list|,
name|nodeId
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|4000
argument_list|)
expr_stmt|;
comment|// Ensure that even though timer is not run, node labels are fetched
comment|// when node is registered
name|assertCollectionEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"x"
argument_list|)
argument_list|,
name|mgr
operator|.
name|getLabelsOnNode
argument_list|(
name|nodeId
argument_list|)
argument_list|)
expr_stmt|;
name|rm
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|registerNode (ResourceManager rm, NodeId nodeId)
specifier|private
name|void
name|registerNode
parameter_list|(
name|ResourceManager
name|rm
parameter_list|,
name|NodeId
name|nodeId
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|ResourceTrackerService
name|resourceTrackerService
init|=
name|rm
operator|.
name|getResourceTrackerService
argument_list|()
decl_stmt|;
name|RegisterNodeManagerRequest
name|req
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|RegisterNodeManagerRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|Resource
name|capability
init|=
name|BuilderUtils
operator|.
name|newResource
argument_list|(
literal|1024
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|req
operator|.
name|setResource
argument_list|(
name|capability
argument_list|)
expr_stmt|;
name|req
operator|.
name|setNodeId
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
name|req
operator|.
name|setHttpPort
argument_list|(
literal|1234
argument_list|)
expr_stmt|;
name|req
operator|.
name|setNMVersion
argument_list|(
name|YarnVersionInfo
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|resourceTrackerService
operator|.
name|registerNodeManager
argument_list|(
name|req
argument_list|)
expr_stmt|;
block|}
DECL|method|updateNodeLabels (NodeId nodeId, String... nodeLabelsStr)
specifier|private
name|void
name|updateNodeLabels
parameter_list|(
name|NodeId
name|nodeId
parameter_list|,
name|String
modifier|...
name|nodeLabelsStr
parameter_list|)
block|{
name|nodeLabelsMap
operator|.
name|put
argument_list|(
name|nodeId
argument_list|,
name|toNodeLabelSet
argument_list|(
name|nodeLabelsStr
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|DummyRMNodeLabelsMappingProvider
specifier|public
specifier|static
class|class
name|DummyRMNodeLabelsMappingProvider
extends|extends
name|RMNodeLabelsMappingProvider
block|{
DECL|method|DummyRMNodeLabelsMappingProvider ()
specifier|public
name|DummyRMNodeLabelsMappingProvider
parameter_list|()
block|{
name|super
argument_list|(
literal|"DummyRMNodeLabelsMappingProvider"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNodeLabels (Set<NodeId> nodes)
specifier|public
name|Map
argument_list|<
name|NodeId
argument_list|,
name|Set
argument_list|<
name|NodeLabel
argument_list|>
argument_list|>
name|getNodeLabels
parameter_list|(
name|Set
argument_list|<
name|NodeId
argument_list|>
name|nodes
parameter_list|)
block|{
name|Map
argument_list|<
name|NodeId
argument_list|,
name|Set
argument_list|<
name|NodeLabel
argument_list|>
argument_list|>
name|nodeLabels
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeId
name|node
range|:
name|nodes
control|)
block|{
name|nodeLabels
operator|.
name|put
argument_list|(
name|node
argument_list|,
name|nodeLabelsMap
operator|.
name|get
argument_list|(
name|node
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|nodeLabels
return|;
block|}
block|}
block|}
end_class

end_unit

