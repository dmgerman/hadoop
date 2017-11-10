begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.scm.node
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
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
name|ozone
operator|.
name|MiniOzoneClassicCluster
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
name|OzoneConfiguration
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
name|ozone
operator|.
name|OzoneConsts
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|OzoneProtos
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
name|scm
operator|.
name|XceiverClientManager
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
name|scm
operator|.
name|client
operator|.
name|ContainerOperationClient
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
name|test
operator|.
name|GenericTestUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|java
operator|.
name|util
operator|.
name|EnumSet
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|SECONDS
import|;
end_import

begin_import
import|import static
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
name|proto
operator|.
name|OzoneProtos
operator|.
name|NodeState
operator|.
name|DEAD
import|;
end_import

begin_import
import|import static
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
name|proto
operator|.
name|OzoneProtos
operator|.
name|NodeState
operator|.
name|HEALTHY
import|;
end_import

begin_import
import|import static
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
name|proto
operator|.
name|OzoneProtos
operator|.
name|NodeState
operator|.
name|STALE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_DEADNODE_INTERVAL_MS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_HEARTBEAT_INTERVAL_SECONDS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_HEARTBEAT_PROCESS_INTERVAL_MS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_STALENODE_INTERVAL_MS
import|;
end_import

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

begin_comment
comment|/**  * Test Query Node Operation.  */
end_comment

begin_class
DECL|class|TestQueryNode
specifier|public
class|class
name|TestQueryNode
block|{
DECL|field|numOfDatanodes
specifier|private
specifier|static
name|int
name|numOfDatanodes
init|=
literal|5
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniOzoneClassicCluster
name|cluster
decl_stmt|;
DECL|field|scmClient
specifier|private
name|ContainerOperationClient
name|scmClient
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
specifier|final
name|int
name|interval
init|=
literal|100
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|OZONE_SCM_HEARTBEAT_PROCESS_INTERVAL_MS
argument_list|,
name|interval
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setTimeDuration
argument_list|(
name|OZONE_SCM_HEARTBEAT_INTERVAL_SECONDS
argument_list|,
literal|1
argument_list|,
name|SECONDS
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|OZONE_SCM_STALENODE_INTERVAL_MS
argument_list|,
literal|3
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|OZONE_SCM_DEADNODE_INTERVAL_MS
argument_list|,
literal|6
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniOzoneClassicCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|numOfDatanodes
argument_list|)
operator|.
name|setHandlerType
argument_list|(
name|OzoneConsts
operator|.
name|OZONE_HANDLER_DISTRIBUTED
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitOzoneReady
argument_list|()
expr_stmt|;
name|scmClient
operator|=
operator|new
name|ContainerOperationClient
argument_list|(
name|cluster
operator|.
name|createStorageContainerLocationClient
argument_list|()
argument_list|,
operator|new
name|XceiverClientManager
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testHealthyNodesCount ()
specifier|public
name|void
name|testHealthyNodesCount
parameter_list|()
throws|throws
name|Exception
block|{
name|OzoneProtos
operator|.
name|NodePool
name|pool
init|=
name|scmClient
operator|.
name|queryNode
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|HEALTHY
argument_list|)
argument_list|,
name|OzoneProtos
operator|.
name|QueryScope
operator|.
name|CLUSTER
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Expected  live nodes"
argument_list|,
name|numOfDatanodes
argument_list|,
name|pool
operator|.
name|getNodesCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10
operator|*
literal|1000L
argument_list|)
DECL|method|testStaleNodesCount ()
specifier|public
name|void
name|testStaleNodesCount
parameter_list|()
throws|throws
name|Exception
block|{
name|cluster
operator|.
name|shutdownDataNode
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|shutdownDataNode
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
operator|.
name|getNodeCount
argument_list|(
name|STALE
argument_list|)
operator|==
literal|2
argument_list|,
literal|100
argument_list|,
literal|4
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|int
name|nodeCount
init|=
name|scmClient
operator|.
name|queryNode
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|STALE
argument_list|)
argument_list|,
name|OzoneProtos
operator|.
name|QueryScope
operator|.
name|CLUSTER
argument_list|,
literal|""
argument_list|)
operator|.
name|getNodesCount
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Mismatch of expected nodes count"
argument_list|,
literal|2
argument_list|,
name|nodeCount
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
operator|.
name|getNodeCount
argument_list|(
name|DEAD
argument_list|)
operator|==
literal|2
argument_list|,
literal|100
argument_list|,
literal|4
operator|*
literal|1000
argument_list|)
expr_stmt|;
comment|// Assert that we don't find any stale nodes.
name|nodeCount
operator|=
name|scmClient
operator|.
name|queryNode
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|STALE
argument_list|)
argument_list|,
name|OzoneProtos
operator|.
name|QueryScope
operator|.
name|CLUSTER
argument_list|,
literal|""
argument_list|)
operator|.
name|getNodesCount
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Mismatch of expected nodes count"
argument_list|,
literal|0
argument_list|,
name|nodeCount
argument_list|)
expr_stmt|;
comment|// Assert that we find the expected number of dead nodes.
name|nodeCount
operator|=
name|scmClient
operator|.
name|queryNode
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|DEAD
argument_list|)
argument_list|,
name|OzoneProtos
operator|.
name|QueryScope
operator|.
name|CLUSTER
argument_list|,
literal|""
argument_list|)
operator|.
name|getNodesCount
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Mismatch of expected nodes count"
argument_list|,
literal|2
argument_list|,
name|nodeCount
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

