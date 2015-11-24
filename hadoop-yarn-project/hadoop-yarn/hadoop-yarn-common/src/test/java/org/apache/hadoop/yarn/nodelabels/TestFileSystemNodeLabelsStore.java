begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.nodelabels
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
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
name|File
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
name|Arrays
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
name|fs
operator|.
name|FileSystem
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
name|fs
operator|.
name|Path
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
name|event
operator|.
name|InlineDispatcher
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
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_class
DECL|class|TestFileSystemNodeLabelsStore
specifier|public
class|class
name|TestFileSystemNodeLabelsStore
extends|extends
name|NodeLabelTestBase
block|{
DECL|field|mgr
name|MockNodeLabelManager
name|mgr
init|=
literal|null
decl_stmt|;
DECL|field|conf
name|Configuration
name|conf
init|=
literal|null
decl_stmt|;
DECL|class|MockNodeLabelManager
specifier|private
specifier|static
class|class
name|MockNodeLabelManager
extends|extends
name|CommonNodeLabelsManager
block|{
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
operator|new
name|InlineDispatcher
argument_list|()
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
block|}
DECL|method|getStore ()
specifier|private
name|FileSystemNodeLabelsStore
name|getStore
parameter_list|()
block|{
return|return
operator|(
name|FileSystemNodeLabelsStore
operator|)
name|mgr
operator|.
name|store
return|;
block|}
annotation|@
name|Before
DECL|method|before ()
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|IOException
block|{
name|mgr
operator|=
operator|new
name|MockNodeLabelManager
argument_list|()
expr_stmt|;
name|conf
operator|=
operator|new
name|Configuration
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
name|File
name|tempDir
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"nlb"
argument_list|,
literal|".tmp"
argument_list|)
decl_stmt|;
name|tempDir
operator|.
name|delete
argument_list|()
expr_stmt|;
name|tempDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|tempDir
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|FS_NODE_LABELS_STORE_ROOT_DIR
argument_list|,
name|tempDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|after ()
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|IOException
block|{
name|getStore
argument_list|()
operator|.
name|fs
operator|.
name|delete
argument_list|(
name|getStore
argument_list|()
operator|.
name|fsWorkingPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testRecoverWithMirror ()
specifier|public
name|void
name|testRecoverWithMirror
parameter_list|()
throws|throws
name|Exception
block|{
name|mgr
operator|.
name|addToCluserNodeLabelsWithDefaultExclusivity
argument_list|(
name|toSet
argument_list|(
literal|"p1"
argument_list|,
literal|"p2"
argument_list|,
literal|"p3"
argument_list|)
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|addToCluserNodeLabelsWithDefaultExclusivity
argument_list|(
name|toSet
argument_list|(
literal|"p4"
argument_list|)
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|addToCluserNodeLabelsWithDefaultExclusivity
argument_list|(
name|toSet
argument_list|(
literal|"p5"
argument_list|,
literal|"p6"
argument_list|)
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|replaceLabelsOnNode
argument_list|(
operator|(
name|Map
operator|)
name|ImmutableMap
operator|.
name|of
argument_list|(
name|toNodeId
argument_list|(
literal|"n1"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p1"
argument_list|)
argument_list|,
name|toNodeId
argument_list|(
literal|"n2"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|replaceLabelsOnNode
argument_list|(
operator|(
name|Map
operator|)
name|ImmutableMap
operator|.
name|of
argument_list|(
name|toNodeId
argument_list|(
literal|"n3"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p3"
argument_list|)
argument_list|,
name|toNodeId
argument_list|(
literal|"n4"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p4"
argument_list|)
argument_list|,
name|toNodeId
argument_list|(
literal|"n5"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p5"
argument_list|)
argument_list|,
name|toNodeId
argument_list|(
literal|"n6"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p6"
argument_list|)
argument_list|,
name|toNodeId
argument_list|(
literal|"n7"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p6"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|/*      * node -> partition p1: n1 p2: n2 p3: n3 p4: n4 p5: n5 p6: n6, n7      */
name|mgr
operator|.
name|removeFromClusterNodeLabels
argument_list|(
name|toSet
argument_list|(
literal|"p1"
argument_list|)
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|removeFromClusterNodeLabels
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"p3"
argument_list|,
literal|"p5"
argument_list|)
argument_list|)
expr_stmt|;
comment|/*      * After removed p2: n2 p4: n4 p6: n6, n7      */
comment|// shutdown mgr and start a new mgr
name|mgr
operator|.
name|stop
argument_list|()
expr_stmt|;
name|mgr
operator|=
operator|new
name|MockNodeLabelManager
argument_list|()
expr_stmt|;
name|mgr
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// check variables
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|mgr
operator|.
name|getClusterNodeLabelNames
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|mgr
operator|.
name|getClusterNodeLabelNames
argument_list|()
operator|.
name|containsAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"p2"
argument_list|,
literal|"p4"
argument_list|,
literal|"p6"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertMapContains
argument_list|(
name|mgr
operator|.
name|getNodeLabels
argument_list|()
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
name|toNodeId
argument_list|(
literal|"n2"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p2"
argument_list|)
argument_list|,
name|toNodeId
argument_list|(
literal|"n4"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p4"
argument_list|)
argument_list|,
name|toNodeId
argument_list|(
literal|"n6"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p6"
argument_list|)
argument_list|,
name|toNodeId
argument_list|(
literal|"n7"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p6"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertLabelsToNodesEquals
argument_list|(
name|mgr
operator|.
name|getLabelsToNodes
argument_list|()
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"p6"
argument_list|,
name|toSet
argument_list|(
name|toNodeId
argument_list|(
literal|"n6"
argument_list|)
argument_list|,
name|toNodeId
argument_list|(
literal|"n7"
argument_list|)
argument_list|)
argument_list|,
literal|"p4"
argument_list|,
name|toSet
argument_list|(
name|toNodeId
argument_list|(
literal|"n4"
argument_list|)
argument_list|)
argument_list|,
literal|"p2"
argument_list|,
name|toSet
argument_list|(
name|toNodeId
argument_list|(
literal|"n2"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// stutdown mgr and start a new mgr
name|mgr
operator|.
name|stop
argument_list|()
expr_stmt|;
name|mgr
operator|=
operator|new
name|MockNodeLabelManager
argument_list|()
expr_stmt|;
name|mgr
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// check variables
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|mgr
operator|.
name|getClusterNodeLabelNames
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|mgr
operator|.
name|getClusterNodeLabelNames
argument_list|()
operator|.
name|containsAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"p2"
argument_list|,
literal|"p4"
argument_list|,
literal|"p6"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertMapContains
argument_list|(
name|mgr
operator|.
name|getNodeLabels
argument_list|()
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
name|toNodeId
argument_list|(
literal|"n2"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p2"
argument_list|)
argument_list|,
name|toNodeId
argument_list|(
literal|"n4"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p4"
argument_list|)
argument_list|,
name|toNodeId
argument_list|(
literal|"n6"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p6"
argument_list|)
argument_list|,
name|toNodeId
argument_list|(
literal|"n7"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p6"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertLabelsToNodesEquals
argument_list|(
name|mgr
operator|.
name|getLabelsToNodes
argument_list|()
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"p6"
argument_list|,
name|toSet
argument_list|(
name|toNodeId
argument_list|(
literal|"n6"
argument_list|)
argument_list|,
name|toNodeId
argument_list|(
literal|"n7"
argument_list|)
argument_list|)
argument_list|,
literal|"p4"
argument_list|,
name|toSet
argument_list|(
name|toNodeId
argument_list|(
literal|"n4"
argument_list|)
argument_list|)
argument_list|,
literal|"p2"
argument_list|,
name|toSet
argument_list|(
name|toNodeId
argument_list|(
literal|"n2"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testRecoverWithDistributedNodeLabels ()
specifier|public
name|void
name|testRecoverWithDistributedNodeLabels
parameter_list|()
throws|throws
name|Exception
block|{
name|mgr
operator|.
name|addToCluserNodeLabelsWithDefaultExclusivity
argument_list|(
name|toSet
argument_list|(
literal|"p1"
argument_list|,
literal|"p2"
argument_list|,
literal|"p3"
argument_list|)
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|addToCluserNodeLabelsWithDefaultExclusivity
argument_list|(
name|toSet
argument_list|(
literal|"p4"
argument_list|)
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|addToCluserNodeLabelsWithDefaultExclusivity
argument_list|(
name|toSet
argument_list|(
literal|"p5"
argument_list|,
literal|"p6"
argument_list|)
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|replaceLabelsOnNode
argument_list|(
operator|(
name|Map
operator|)
name|ImmutableMap
operator|.
name|of
argument_list|(
name|toNodeId
argument_list|(
literal|"n1"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p1"
argument_list|)
argument_list|,
name|toNodeId
argument_list|(
literal|"n2"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|replaceLabelsOnNode
argument_list|(
operator|(
name|Map
operator|)
name|ImmutableMap
operator|.
name|of
argument_list|(
name|toNodeId
argument_list|(
literal|"n3"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p3"
argument_list|)
argument_list|,
name|toNodeId
argument_list|(
literal|"n4"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p4"
argument_list|)
argument_list|,
name|toNodeId
argument_list|(
literal|"n5"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p5"
argument_list|)
argument_list|,
name|toNodeId
argument_list|(
literal|"n6"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p6"
argument_list|)
argument_list|,
name|toNodeId
argument_list|(
literal|"n7"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p6"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|removeFromClusterNodeLabels
argument_list|(
name|toSet
argument_list|(
literal|"p1"
argument_list|)
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|removeFromClusterNodeLabels
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"p3"
argument_list|,
literal|"p5"
argument_list|)
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|stop
argument_list|()
expr_stmt|;
name|mgr
operator|=
operator|new
name|MockNodeLabelManager
argument_list|()
expr_stmt|;
name|Configuration
name|cf
init|=
operator|new
name|Configuration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|cf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NODELABEL_CONFIGURATION_TYPE
argument_list|,
name|YarnConfiguration
operator|.
name|DISTRIBUTED_NODELABEL_CONFIGURATION_TYPE
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|init
argument_list|(
name|cf
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// check variables
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|mgr
operator|.
name|getClusterNodeLabels
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|mgr
operator|.
name|getClusterNodeLabelNames
argument_list|()
operator|.
name|containsAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"p2"
argument_list|,
literal|"p4"
argument_list|,
literal|"p6"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"During recovery in distributed node-labels setup, "
operator|+
literal|"node to labels mapping should not be recovered "
argument_list|,
name|mgr
operator|.
name|getNodeLabels
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testEditlogRecover ()
specifier|public
name|void
name|testEditlogRecover
parameter_list|()
throws|throws
name|Exception
block|{
name|mgr
operator|.
name|addToCluserNodeLabelsWithDefaultExclusivity
argument_list|(
name|toSet
argument_list|(
literal|"p1"
argument_list|,
literal|"p2"
argument_list|,
literal|"p3"
argument_list|)
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|addToCluserNodeLabelsWithDefaultExclusivity
argument_list|(
name|toSet
argument_list|(
literal|"p4"
argument_list|)
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|addToCluserNodeLabelsWithDefaultExclusivity
argument_list|(
name|toSet
argument_list|(
literal|"p5"
argument_list|,
literal|"p6"
argument_list|)
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|replaceLabelsOnNode
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
name|toNodeId
argument_list|(
literal|"n1"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p1"
argument_list|)
argument_list|,
name|toNodeId
argument_list|(
literal|"n2"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|replaceLabelsOnNode
argument_list|(
operator|(
name|Map
operator|)
name|ImmutableMap
operator|.
name|of
argument_list|(
name|toNodeId
argument_list|(
literal|"n3"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p3"
argument_list|)
argument_list|,
name|toNodeId
argument_list|(
literal|"n4"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p4"
argument_list|)
argument_list|,
name|toNodeId
argument_list|(
literal|"n5"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p5"
argument_list|)
argument_list|,
name|toNodeId
argument_list|(
literal|"n6"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p6"
argument_list|)
argument_list|,
name|toNodeId
argument_list|(
literal|"n7"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p6"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|/*      * node -> partition p1: n1 p2: n2 p3: n3 p4: n4 p5: n5 p6: n6, n7      */
name|mgr
operator|.
name|removeFromClusterNodeLabels
argument_list|(
name|toSet
argument_list|(
literal|"p1"
argument_list|)
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|removeFromClusterNodeLabels
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"p3"
argument_list|,
literal|"p5"
argument_list|)
argument_list|)
expr_stmt|;
comment|/*      * After removed p2: n2 p4: n4 p6: n6, n7      */
comment|// shutdown mgr and start a new mgr
name|mgr
operator|.
name|stop
argument_list|()
expr_stmt|;
name|mgr
operator|=
operator|new
name|MockNodeLabelManager
argument_list|()
expr_stmt|;
name|mgr
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// check variables
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|mgr
operator|.
name|getClusterNodeLabelNames
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|mgr
operator|.
name|getClusterNodeLabelNames
argument_list|()
operator|.
name|containsAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"p2"
argument_list|,
literal|"p4"
argument_list|,
literal|"p6"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertMapContains
argument_list|(
name|mgr
operator|.
name|getNodeLabels
argument_list|()
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
name|toNodeId
argument_list|(
literal|"n2"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p2"
argument_list|)
argument_list|,
name|toNodeId
argument_list|(
literal|"n4"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p4"
argument_list|)
argument_list|,
name|toNodeId
argument_list|(
literal|"n6"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p6"
argument_list|)
argument_list|,
name|toNodeId
argument_list|(
literal|"n7"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p6"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertLabelsToNodesEquals
argument_list|(
name|mgr
operator|.
name|getLabelsToNodes
argument_list|()
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"p6"
argument_list|,
name|toSet
argument_list|(
name|toNodeId
argument_list|(
literal|"n6"
argument_list|)
argument_list|,
name|toNodeId
argument_list|(
literal|"n7"
argument_list|)
argument_list|)
argument_list|,
literal|"p4"
argument_list|,
name|toSet
argument_list|(
name|toNodeId
argument_list|(
literal|"n4"
argument_list|)
argument_list|)
argument_list|,
literal|"p2"
argument_list|,
name|toSet
argument_list|(
name|toNodeId
argument_list|(
literal|"n2"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testSerilizationAfterRecovery ()
specifier|public
name|void
name|testSerilizationAfterRecovery
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Add to cluster node labels, p2/p6 are non-exclusive.
name|mgr
operator|.
name|addToCluserNodeLabels
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|NodeLabel
operator|.
name|newInstance
argument_list|(
literal|"p1"
argument_list|,
literal|true
argument_list|)
argument_list|,
name|NodeLabel
operator|.
name|newInstance
argument_list|(
literal|"p2"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|NodeLabel
operator|.
name|newInstance
argument_list|(
literal|"p3"
argument_list|,
literal|true
argument_list|)
argument_list|,
name|NodeLabel
operator|.
name|newInstance
argument_list|(
literal|"p4"
argument_list|,
literal|true
argument_list|)
argument_list|,
name|NodeLabel
operator|.
name|newInstance
argument_list|(
literal|"p5"
argument_list|,
literal|true
argument_list|)
argument_list|,
name|NodeLabel
operator|.
name|newInstance
argument_list|(
literal|"p6"
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|replaceLabelsOnNode
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
name|toNodeId
argument_list|(
literal|"n1"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p1"
argument_list|)
argument_list|,
name|toNodeId
argument_list|(
literal|"n2"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|replaceLabelsOnNode
argument_list|(
operator|(
name|Map
operator|)
name|ImmutableMap
operator|.
name|of
argument_list|(
name|toNodeId
argument_list|(
literal|"n3"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p3"
argument_list|)
argument_list|,
name|toNodeId
argument_list|(
literal|"n4"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p4"
argument_list|)
argument_list|,
name|toNodeId
argument_list|(
literal|"n5"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p5"
argument_list|)
argument_list|,
name|toNodeId
argument_list|(
literal|"n6"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p6"
argument_list|)
argument_list|,
name|toNodeId
argument_list|(
literal|"n7"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p6"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|/*      * node -> labels       * p1: n1       * p2: n2       * p3: n3      * p4: n4       * p5: n5       * p6: n6, n7      */
name|mgr
operator|.
name|removeFromClusterNodeLabels
argument_list|(
name|toSet
argument_list|(
literal|"p1"
argument_list|)
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|removeFromClusterNodeLabels
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"p3"
argument_list|,
literal|"p5"
argument_list|)
argument_list|)
expr_stmt|;
comment|/*      * After removed       * p2: n2       * p4: n4       * p6: n6, n7      */
comment|// shutdown mgr and start a new mgr
name|mgr
operator|.
name|stop
argument_list|()
expr_stmt|;
name|mgr
operator|=
operator|new
name|MockNodeLabelManager
argument_list|()
expr_stmt|;
name|mgr
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// check variables
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|mgr
operator|.
name|getClusterNodeLabelNames
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|mgr
operator|.
name|getClusterNodeLabelNames
argument_list|()
operator|.
name|containsAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"p2"
argument_list|,
literal|"p4"
argument_list|,
literal|"p6"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertMapContains
argument_list|(
name|mgr
operator|.
name|getNodeLabels
argument_list|()
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
name|toNodeId
argument_list|(
literal|"n2"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p2"
argument_list|)
argument_list|,
name|toNodeId
argument_list|(
literal|"n4"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p4"
argument_list|)
argument_list|,
name|toNodeId
argument_list|(
literal|"n6"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p6"
argument_list|)
argument_list|,
name|toNodeId
argument_list|(
literal|"n7"
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"p6"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertLabelsToNodesEquals
argument_list|(
name|mgr
operator|.
name|getLabelsToNodes
argument_list|()
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"p6"
argument_list|,
name|toSet
argument_list|(
name|toNodeId
argument_list|(
literal|"n6"
argument_list|)
argument_list|,
name|toNodeId
argument_list|(
literal|"n7"
argument_list|)
argument_list|)
argument_list|,
literal|"p4"
argument_list|,
name|toSet
argument_list|(
name|toNodeId
argument_list|(
literal|"n4"
argument_list|)
argument_list|)
argument_list|,
literal|"p2"
argument_list|,
name|toSet
argument_list|(
name|toNodeId
argument_list|(
literal|"n2"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|mgr
operator|.
name|isExclusiveNodeLabel
argument_list|(
literal|"p2"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|mgr
operator|.
name|isExclusiveNodeLabel
argument_list|(
literal|"p4"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|mgr
operator|.
name|isExclusiveNodeLabel
argument_list|(
literal|"p6"
argument_list|)
argument_list|)
expr_stmt|;
comment|/*      * Add label p7,p8 then shutdown      */
name|mgr
operator|=
operator|new
name|MockNodeLabelManager
argument_list|()
expr_stmt|;
name|mgr
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|start
argument_list|()
expr_stmt|;
name|mgr
operator|.
name|addToCluserNodeLabelsWithDefaultExclusivity
argument_list|(
name|toSet
argument_list|(
literal|"p7"
argument_list|,
literal|"p8"
argument_list|)
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|stop
argument_list|()
expr_stmt|;
comment|/*      * Restart, add label p9 and shutdown      */
name|mgr
operator|=
operator|new
name|MockNodeLabelManager
argument_list|()
expr_stmt|;
name|mgr
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|start
argument_list|()
expr_stmt|;
name|mgr
operator|.
name|addToCluserNodeLabelsWithDefaultExclusivity
argument_list|(
name|toSet
argument_list|(
literal|"p9"
argument_list|)
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|stop
argument_list|()
expr_stmt|;
comment|/*      * Recovery, and see if p9 added      */
name|mgr
operator|=
operator|new
name|MockNodeLabelManager
argument_list|()
expr_stmt|;
name|mgr
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// check variables
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|mgr
operator|.
name|getClusterNodeLabelNames
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|mgr
operator|.
name|getClusterNodeLabelNames
argument_list|()
operator|.
name|containsAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"p2"
argument_list|,
literal|"p4"
argument_list|,
literal|"p6"
argument_list|,
literal|"p7"
argument_list|,
literal|"p8"
argument_list|,
literal|"p9"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRootMkdirOnInitStore ()
specifier|public
name|void
name|testRootMkdirOnInitStore
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|FileSystem
name|mockFs
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|FileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|FileSystemNodeLabelsStore
name|mockStore
init|=
operator|new
name|FileSystemNodeLabelsStore
argument_list|(
name|mgr
argument_list|)
block|{
name|void
name|setFileSystem
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|fs
operator|=
name|mockFs
expr_stmt|;
block|}
block|}
decl_stmt|;
name|mockStore
operator|.
name|fs
operator|=
name|mockFs
expr_stmt|;
name|verifyMkdirsCount
argument_list|(
name|mockStore
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|verifyMkdirsCount
argument_list|(
name|mockStore
argument_list|,
literal|false
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|verifyMkdirsCount
argument_list|(
name|mockStore
argument_list|,
literal|true
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|verifyMkdirsCount
argument_list|(
name|mockStore
argument_list|,
literal|false
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyMkdirsCount (FileSystemNodeLabelsStore store, boolean existsRetVal, int expectedNumOfCalls)
specifier|private
name|void
name|verifyMkdirsCount
parameter_list|(
name|FileSystemNodeLabelsStore
name|store
parameter_list|,
name|boolean
name|existsRetVal
parameter_list|,
name|int
name|expectedNumOfCalls
parameter_list|)
throws|throws
name|Exception
block|{
name|Mockito
operator|.
name|when
argument_list|(
name|store
operator|.
name|fs
operator|.
name|exists
argument_list|(
name|Mockito
operator|.
name|any
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|existsRetVal
argument_list|)
expr_stmt|;
name|store
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|store
operator|.
name|fs
argument_list|,
name|Mockito
operator|.
name|times
argument_list|(
name|expectedNumOfCalls
argument_list|)
argument_list|)
operator|.
name|mkdirs
argument_list|(
name|Mockito
operator|.
name|any
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

