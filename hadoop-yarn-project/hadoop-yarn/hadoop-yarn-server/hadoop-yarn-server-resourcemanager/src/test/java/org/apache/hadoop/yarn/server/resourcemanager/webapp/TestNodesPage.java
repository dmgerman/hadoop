begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.webapp
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
name|webapp
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
name|io
operator|.
name|PrintWriter
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
name|NodeState
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
name|RMContext
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
name|webapp
operator|.
name|NodesPage
operator|.
name|NodesBlock
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
name|webapp
operator|.
name|test
operator|.
name|WebAppTests
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
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Binder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Injector
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Module
import|;
end_import

begin_comment
comment|/**  * This tests the NodesPage block table that it should contain the table body  * data for all the columns in the table as specified in the header.  */
end_comment

begin_class
DECL|class|TestNodesPage
specifier|public
class|class
name|TestNodesPage
block|{
DECL|field|numberOfRacks
specifier|final
name|int
name|numberOfRacks
init|=
literal|2
decl_stmt|;
DECL|field|numberOfNodesPerRack
specifier|final
name|int
name|numberOfNodesPerRack
init|=
literal|8
decl_stmt|;
comment|// The following is because of the way TestRMWebApp.mockRMContext creates
comment|// nodes.
DECL|field|numberOfLostNodesPerRack
specifier|final
name|int
name|numberOfLostNodesPerRack
init|=
literal|1
decl_stmt|;
comment|// Number of Actual Table Headers for NodesPage.NodesBlock might change in
comment|// future. In that case this value should be adjusted to the new value.
DECL|field|numberOfThInMetricsTable
specifier|final
name|int
name|numberOfThInMetricsTable
init|=
literal|23
decl_stmt|;
DECL|field|numberOfActualTableHeaders
specifier|final
name|int
name|numberOfActualTableHeaders
init|=
literal|13
decl_stmt|;
DECL|field|injector
specifier|private
name|Injector
name|injector
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
specifier|final
name|RMContext
name|mockRMContext
init|=
name|TestRMWebApp
operator|.
name|mockRMContext
argument_list|(
literal|3
argument_list|,
name|numberOfRacks
argument_list|,
name|numberOfNodesPerRack
argument_list|,
literal|8
operator|*
name|TestRMWebApp
operator|.
name|GiB
argument_list|)
decl_stmt|;
name|injector
operator|=
name|WebAppTests
operator|.
name|createMockInjector
argument_list|(
name|RMContext
operator|.
name|class
argument_list|,
name|mockRMContext
argument_list|,
operator|new
name|Module
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|configure
parameter_list|(
name|Binder
name|binder
parameter_list|)
block|{
try|try
block|{
name|binder
operator|.
name|bind
argument_list|(
name|ResourceManager
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|TestRMWebApp
operator|.
name|mockRm
argument_list|(
name|mockRMContext
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNodesBlockRender ()
specifier|public
name|void
name|testNodesBlockRender
parameter_list|()
throws|throws
name|Exception
block|{
name|injector
operator|.
name|getInstance
argument_list|(
name|NodesBlock
operator|.
name|class
argument_list|)
operator|.
name|render
argument_list|()
expr_stmt|;
name|PrintWriter
name|writer
init|=
name|injector
operator|.
name|getInstance
argument_list|(
name|PrintWriter
operator|.
name|class
argument_list|)
decl_stmt|;
name|WebAppTests
operator|.
name|flushOutput
argument_list|(
name|injector
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|writer
argument_list|,
name|Mockito
operator|.
name|times
argument_list|(
name|numberOfActualTableHeaders
operator|+
name|numberOfThInMetricsTable
argument_list|)
argument_list|)
operator|.
name|print
argument_list|(
literal|"<th"
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|writer
argument_list|,
name|Mockito
operator|.
name|times
argument_list|(
name|numberOfRacks
operator|*
name|numberOfNodesPerRack
operator|*
name|numberOfActualTableHeaders
operator|+
name|numberOfThInMetricsTable
argument_list|)
argument_list|)
operator|.
name|print
argument_list|(
literal|"<td"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNodesBlockRenderForLostNodes ()
specifier|public
name|void
name|testNodesBlockRenderForLostNodes
parameter_list|()
block|{
name|NodesBlock
name|nodesBlock
init|=
name|injector
operator|.
name|getInstance
argument_list|(
name|NodesBlock
operator|.
name|class
argument_list|)
decl_stmt|;
name|nodesBlock
operator|.
name|set
argument_list|(
literal|"node.state"
argument_list|,
literal|"lost"
argument_list|)
expr_stmt|;
name|nodesBlock
operator|.
name|render
argument_list|()
expr_stmt|;
name|PrintWriter
name|writer
init|=
name|injector
operator|.
name|getInstance
argument_list|(
name|PrintWriter
operator|.
name|class
argument_list|)
decl_stmt|;
name|WebAppTests
operator|.
name|flushOutput
argument_list|(
name|injector
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|writer
argument_list|,
name|Mockito
operator|.
name|times
argument_list|(
name|numberOfActualTableHeaders
operator|+
name|numberOfThInMetricsTable
argument_list|)
argument_list|)
operator|.
name|print
argument_list|(
literal|"<th"
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|writer
argument_list|,
name|Mockito
operator|.
name|times
argument_list|(
name|numberOfRacks
operator|*
name|numberOfLostNodesPerRack
operator|*
name|numberOfActualTableHeaders
operator|+
name|numberOfThInMetricsTable
argument_list|)
argument_list|)
operator|.
name|print
argument_list|(
literal|"<td"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNodesBlockRenderForNodeLabelFilterWithNonEmptyLabel ()
specifier|public
name|void
name|testNodesBlockRenderForNodeLabelFilterWithNonEmptyLabel
parameter_list|()
block|{
name|NodesBlock
name|nodesBlock
init|=
name|injector
operator|.
name|getInstance
argument_list|(
name|NodesBlock
operator|.
name|class
argument_list|)
decl_stmt|;
name|nodesBlock
operator|.
name|set
argument_list|(
literal|"node.label"
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|nodesBlock
operator|.
name|render
argument_list|()
expr_stmt|;
name|PrintWriter
name|writer
init|=
name|injector
operator|.
name|getInstance
argument_list|(
name|PrintWriter
operator|.
name|class
argument_list|)
decl_stmt|;
name|WebAppTests
operator|.
name|flushOutput
argument_list|(
name|injector
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|writer
argument_list|,
name|Mockito
operator|.
name|times
argument_list|(
name|numberOfRacks
operator|*
name|numberOfActualTableHeaders
operator|+
name|numberOfThInMetricsTable
argument_list|)
argument_list|)
operator|.
name|print
argument_list|(
literal|"<td"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNodesBlockRenderForNodeLabelFilterWithEmptyLabel ()
specifier|public
name|void
name|testNodesBlockRenderForNodeLabelFilterWithEmptyLabel
parameter_list|()
block|{
name|NodesBlock
name|nodesBlock
init|=
name|injector
operator|.
name|getInstance
argument_list|(
name|NodesBlock
operator|.
name|class
argument_list|)
decl_stmt|;
name|nodesBlock
operator|.
name|set
argument_list|(
literal|"node.label"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|nodesBlock
operator|.
name|render
argument_list|()
expr_stmt|;
name|PrintWriter
name|writer
init|=
name|injector
operator|.
name|getInstance
argument_list|(
name|PrintWriter
operator|.
name|class
argument_list|)
decl_stmt|;
name|WebAppTests
operator|.
name|flushOutput
argument_list|(
name|injector
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|writer
argument_list|,
name|Mockito
operator|.
name|times
argument_list|(
name|numberOfRacks
operator|*
operator|(
name|numberOfNodesPerRack
operator|-
literal|1
operator|)
operator|*
name|numberOfActualTableHeaders
operator|+
name|numberOfThInMetricsTable
argument_list|)
argument_list|)
operator|.
name|print
argument_list|(
literal|"<td"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNodesBlockRenderForNodeLabelFilterWithAnyLabel ()
specifier|public
name|void
name|testNodesBlockRenderForNodeLabelFilterWithAnyLabel
parameter_list|()
block|{
name|NodesBlock
name|nodesBlock
init|=
name|injector
operator|.
name|getInstance
argument_list|(
name|NodesBlock
operator|.
name|class
argument_list|)
decl_stmt|;
name|nodesBlock
operator|.
name|set
argument_list|(
literal|"node.label"
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|nodesBlock
operator|.
name|render
argument_list|()
expr_stmt|;
name|PrintWriter
name|writer
init|=
name|injector
operator|.
name|getInstance
argument_list|(
name|PrintWriter
operator|.
name|class
argument_list|)
decl_stmt|;
name|WebAppTests
operator|.
name|flushOutput
argument_list|(
name|injector
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|writer
argument_list|,
name|Mockito
operator|.
name|times
argument_list|(
name|numberOfRacks
operator|*
name|numberOfNodesPerRack
operator|*
name|numberOfActualTableHeaders
operator|+
name|numberOfThInMetricsTable
argument_list|)
argument_list|)
operator|.
name|print
argument_list|(
literal|"<td"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

