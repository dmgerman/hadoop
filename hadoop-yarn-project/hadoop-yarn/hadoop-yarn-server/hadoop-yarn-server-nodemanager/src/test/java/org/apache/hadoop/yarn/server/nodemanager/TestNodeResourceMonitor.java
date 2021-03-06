begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager
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
name|nodemanager
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|UnsupportedFileSystemException
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
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|BaseContainerManagerTest
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|monitor
operator|.
name|MockResourceCalculatorPlugin
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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|spy
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|timeout
import|;
end_import

begin_class
DECL|class|TestNodeResourceMonitor
specifier|public
class|class
name|TestNodeResourceMonitor
extends|extends
name|BaseContainerManagerTest
block|{
DECL|method|TestNodeResourceMonitor ()
specifier|public
name|TestNodeResourceMonitor
parameter_list|()
throws|throws
name|UnsupportedFileSystemException
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Enable node resource monitor with a mocked resource calculator.
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_MON_RESOURCE_CALCULATOR
argument_list|,
name|MockResourceCalculatorPlugin
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|setup
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMetricsUpdate ()
specifier|public
name|void
name|testMetricsUpdate
parameter_list|()
throws|throws
name|Exception
block|{
comment|// This test doesn't verify the correction of those metrics
comment|// updated by the monitor, it only verifies that the monitor
comment|// do publish these info to node manager metrics system in
comment|// each monitor interval.
name|Context
name|spyContext
init|=
name|spy
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|NodeResourceMonitor
name|nrm
init|=
operator|new
name|NodeResourceMonitorImpl
argument_list|(
name|spyContext
argument_list|)
decl_stmt|;
name|nrm
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|nrm
operator|.
name|start
argument_list|()
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|spyContext
argument_list|,
name|timeout
argument_list|(
literal|500
argument_list|)
operator|.
name|atLeastOnce
argument_list|()
argument_list|)
operator|.
name|getNodeManagerMetrics
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

