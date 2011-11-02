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
name|FileOutputStream
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
name|HashMap
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
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|io
operator|.
name|IOUtils
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
name|ContainerStatus
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
name|factories
operator|.
name|RecordFactory
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
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
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
name|api
operator|.
name|protocolrecords
operator|.
name|RegisterNodeManagerResponse
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
name|records
operator|.
name|HeartbeatResponse
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
name|records
operator|.
name|NodeAction
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
name|Test
import|;
end_import

begin_class
DECL|class|TestResourceTrackerService
specifier|public
class|class
name|TestResourceTrackerService
block|{
DECL|field|TEMP_DIR
specifier|private
specifier|final
specifier|static
name|File
name|TEMP_DIR
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/tmp"
argument_list|)
argument_list|,
literal|"decommision"
argument_list|)
decl_stmt|;
DECL|field|hostFile
specifier|private
name|File
name|hostFile
init|=
operator|new
name|File
argument_list|(
name|TEMP_DIR
operator|+
name|File
operator|.
name|separator
operator|+
literal|"hostFile.txt"
argument_list|)
decl_stmt|;
DECL|field|rm
specifier|private
name|MockRM
name|rm
decl_stmt|;
DECL|field|recordFactory
specifier|private
specifier|static
specifier|final
name|RecordFactory
name|recordFactory
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
literal|null
argument_list|)
decl_stmt|;
comment|/**    * decommissioning using a include hosts file    */
annotation|@
name|Test
DECL|method|testDecommissionWithIncludeHosts ()
specifier|public
name|void
name|testDecommissionWithIncludeHosts
parameter_list|()
throws|throws
name|Exception
block|{
name|writeToHostsFile
argument_list|(
literal|"host1"
argument_list|,
literal|"host2"
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"yarn.resourcemanager.nodes.include-path"
argument_list|,
name|hostFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|rm
operator|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
name|MockNM
name|nm1
init|=
name|rm
operator|.
name|registerNode
argument_list|(
literal|"host1:1234"
argument_list|,
literal|5120
argument_list|)
decl_stmt|;
name|MockNM
name|nm2
init|=
name|rm
operator|.
name|registerNode
argument_list|(
literal|"host2:5678"
argument_list|,
literal|10240
argument_list|)
decl_stmt|;
name|ClusterMetrics
name|metrics
init|=
name|ClusterMetrics
operator|.
name|getMetrics
argument_list|()
decl_stmt|;
assert|assert
operator|(
name|metrics
operator|!=
literal|null
operator|)
assert|;
name|int
name|initialMetricCount
init|=
name|metrics
operator|.
name|getNumDecommisionedNMs
argument_list|()
decl_stmt|;
name|HeartbeatResponse
name|nodeHeartbeat
init|=
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|NodeAction
operator|.
name|NORMAL
operator|.
name|equals
argument_list|(
name|nodeHeartbeat
operator|.
name|getNodeAction
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|nodeHeartbeat
operator|=
name|nm2
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|NodeAction
operator|.
name|NORMAL
operator|.
name|equals
argument_list|(
name|nodeHeartbeat
operator|.
name|getNodeAction
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|writeToHostsFile
argument_list|(
literal|"host1"
argument_list|)
expr_stmt|;
name|rm
operator|.
name|getNodesListManager
argument_list|()
operator|.
name|refreshNodes
argument_list|()
expr_stmt|;
name|nodeHeartbeat
operator|=
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|NodeAction
operator|.
name|NORMAL
operator|.
name|equals
argument_list|(
name|nodeHeartbeat
operator|.
name|getNodeAction
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ClusterMetrics
operator|.
name|getMetrics
argument_list|()
operator|.
name|getNumDecommisionedNMs
argument_list|()
argument_list|)
expr_stmt|;
name|nodeHeartbeat
operator|=
name|nm2
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Node is not decommisioned."
argument_list|,
name|NodeAction
operator|.
name|SHUTDOWN
operator|.
name|equals
argument_list|(
name|nodeHeartbeat
operator|.
name|getNodeAction
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|checkDecommissionedNMCount
argument_list|(
name|rm
argument_list|,
operator|++
name|initialMetricCount
argument_list|)
expr_stmt|;
block|}
comment|/**    * decommissioning using a exclude hosts file    */
annotation|@
name|Test
DECL|method|testDecommissionWithExcludeHosts ()
specifier|public
name|void
name|testDecommissionWithExcludeHosts
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"yarn.resourcemanager.nodes.exclude-path"
argument_list|,
name|hostFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|writeToHostsFile
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|rm
operator|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
name|MockNM
name|nm1
init|=
name|rm
operator|.
name|registerNode
argument_list|(
literal|"host1:1234"
argument_list|,
literal|5120
argument_list|)
decl_stmt|;
name|MockNM
name|nm2
init|=
name|rm
operator|.
name|registerNode
argument_list|(
literal|"host2:5678"
argument_list|,
literal|10240
argument_list|)
decl_stmt|;
name|int
name|initialMetricCount
init|=
name|ClusterMetrics
operator|.
name|getMetrics
argument_list|()
operator|.
name|getNumDecommisionedNMs
argument_list|()
decl_stmt|;
name|HeartbeatResponse
name|nodeHeartbeat
init|=
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|NodeAction
operator|.
name|NORMAL
operator|.
name|equals
argument_list|(
name|nodeHeartbeat
operator|.
name|getNodeAction
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|nodeHeartbeat
operator|=
name|nm2
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|NodeAction
operator|.
name|NORMAL
operator|.
name|equals
argument_list|(
name|nodeHeartbeat
operator|.
name|getNodeAction
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|writeToHostsFile
argument_list|(
literal|"host2"
argument_list|)
expr_stmt|;
name|rm
operator|.
name|getNodesListManager
argument_list|()
operator|.
name|refreshNodes
argument_list|()
expr_stmt|;
name|nodeHeartbeat
operator|=
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|NodeAction
operator|.
name|NORMAL
operator|.
name|equals
argument_list|(
name|nodeHeartbeat
operator|.
name|getNodeAction
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|nodeHeartbeat
operator|=
name|nm2
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"The decommisioned metrics are not updated"
argument_list|,
name|NodeAction
operator|.
name|SHUTDOWN
operator|.
name|equals
argument_list|(
name|nodeHeartbeat
operator|.
name|getNodeAction
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|checkDecommissionedNMCount
argument_list|(
name|rm
argument_list|,
operator|++
name|initialMetricCount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNodeRegistrationFailure ()
specifier|public
name|void
name|testNodeRegistrationFailure
parameter_list|()
throws|throws
name|Exception
block|{
name|writeToHostsFile
argument_list|(
literal|"host1"
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"yarn.resourcemanager.nodes.include-path"
argument_list|,
name|hostFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|rm
operator|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
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
name|NodeId
name|nodeId
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|NodeId
operator|.
name|class
argument_list|)
decl_stmt|;
name|nodeId
operator|.
name|setHost
argument_list|(
literal|"host2"
argument_list|)
expr_stmt|;
name|nodeId
operator|.
name|setPort
argument_list|(
literal|1234
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
comment|// trying to register a invalid node.
name|RegisterNodeManagerResponse
name|response
init|=
name|resourceTrackerService
operator|.
name|registerNodeManager
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|NodeAction
operator|.
name|SHUTDOWN
argument_list|,
name|response
operator|.
name|getRegistrationResponse
argument_list|()
operator|.
name|getNodeAction
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReboot ()
specifier|public
name|void
name|testReboot
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|rm
operator|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
name|MockNM
name|nm1
init|=
name|rm
operator|.
name|registerNode
argument_list|(
literal|"host1:1234"
argument_list|,
literal|5120
argument_list|)
decl_stmt|;
name|MockNM
name|nm2
init|=
operator|new
name|MockNM
argument_list|(
literal|"host2:1234"
argument_list|,
literal|2048
argument_list|,
name|rm
operator|.
name|getResourceTrackerService
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|initialMetricCount
init|=
name|ClusterMetrics
operator|.
name|getMetrics
argument_list|()
operator|.
name|getNumRebootedNMs
argument_list|()
decl_stmt|;
name|HeartbeatResponse
name|nodeHeartbeat
init|=
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|NodeAction
operator|.
name|NORMAL
operator|.
name|equals
argument_list|(
name|nodeHeartbeat
operator|.
name|getNodeAction
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|nodeHeartbeat
operator|=
name|nm2
operator|.
name|nodeHeartbeat
argument_list|(
operator|new
name|HashMap
argument_list|<
name|ApplicationId
argument_list|,
name|List
argument_list|<
name|ContainerStatus
argument_list|>
argument_list|>
argument_list|()
argument_list|,
literal|true
argument_list|,
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|NodeId
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|NodeAction
operator|.
name|REBOOT
operator|.
name|equals
argument_list|(
name|nodeHeartbeat
operator|.
name|getNodeAction
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|checkRebootedNMCount
argument_list|(
name|rm
argument_list|,
operator|++
name|initialMetricCount
argument_list|)
expr_stmt|;
block|}
DECL|method|checkRebootedNMCount (MockRM rm2, int count)
specifier|private
name|void
name|checkRebootedNMCount
parameter_list|(
name|MockRM
name|rm2
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|int
name|waitCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|ClusterMetrics
operator|.
name|getMetrics
argument_list|()
operator|.
name|getNumRebootedNMs
argument_list|()
operator|!=
name|count
operator|&&
name|waitCount
operator|++
operator|<
literal|20
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|wait
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"The rebooted metrics are not updated"
argument_list|,
name|count
argument_list|,
name|ClusterMetrics
operator|.
name|getMetrics
argument_list|()
operator|.
name|getNumRebootedNMs
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUnhealthyNodeStatus ()
specifier|public
name|void
name|testUnhealthyNodeStatus
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"yarn.resourcemanager.nodes.exclude-path"
argument_list|,
name|hostFile
operator|.
name|getAbsolutePath
argument_list|()
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
name|start
argument_list|()
expr_stmt|;
name|MockNM
name|nm1
init|=
name|rm
operator|.
name|registerNode
argument_list|(
literal|"host1:1234"
argument_list|,
literal|5120
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ClusterMetrics
operator|.
name|getMetrics
argument_list|()
operator|.
name|getUnhealthyNMs
argument_list|()
argument_list|)
expr_stmt|;
comment|// node healthy
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// node unhealthy
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|checkUnealthyNMCount
argument_list|(
name|rm
argument_list|,
name|nm1
argument_list|,
literal|true
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// node healthy again
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|checkUnealthyNMCount
argument_list|(
name|rm
argument_list|,
name|nm1
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|checkUnealthyNMCount (MockRM rm, MockNM nm1, boolean health, int count)
specifier|private
name|void
name|checkUnealthyNMCount
parameter_list|(
name|MockRM
name|rm
parameter_list|,
name|MockNM
name|nm1
parameter_list|,
name|boolean
name|health
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|waitCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMNodes
argument_list|()
operator|.
name|get
argument_list|(
name|nm1
operator|.
name|getNodeId
argument_list|()
argument_list|)
operator|.
name|getNodeHealthStatus
argument_list|()
operator|.
name|getIsNodeHealthy
argument_list|()
operator|==
name|health
operator|&&
name|waitCount
operator|++
operator|<
literal|20
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|wait
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
block|}
name|Assert
operator|.
name|assertFalse
argument_list|(
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMNodes
argument_list|()
operator|.
name|get
argument_list|(
name|nm1
operator|.
name|getNodeId
argument_list|()
argument_list|)
operator|.
name|getNodeHealthStatus
argument_list|()
operator|.
name|getIsNodeHealthy
argument_list|()
operator|==
name|health
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Unhealthy metrics not incremented"
argument_list|,
name|count
argument_list|,
name|ClusterMetrics
operator|.
name|getMetrics
argument_list|()
operator|.
name|getUnhealthyNMs
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|writeToHostsFile (String... hosts)
specifier|private
name|void
name|writeToHostsFile
parameter_list|(
name|String
modifier|...
name|hosts
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|hostFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|TEMP_DIR
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|hostFile
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
block|}
name|FileOutputStream
name|fStream
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fStream
operator|=
operator|new
name|FileOutputStream
argument_list|(
name|hostFile
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|hosts
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|fStream
operator|.
name|write
argument_list|(
name|hosts
index|[
name|i
index|]
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|fStream
operator|.
name|write
argument_list|(
literal|"\n"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|fStream
operator|!=
literal|null
condition|)
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|fStream
argument_list|)
expr_stmt|;
name|fStream
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
DECL|method|checkDecommissionedNMCount (MockRM rm, int count)
specifier|private
name|void
name|checkDecommissionedNMCount
parameter_list|(
name|MockRM
name|rm
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|int
name|waitCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|ClusterMetrics
operator|.
name|getMetrics
argument_list|()
operator|.
name|getNumDecommisionedNMs
argument_list|()
operator|!=
name|count
operator|&&
name|waitCount
operator|++
operator|<
literal|20
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|wait
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|count
argument_list|,
name|ClusterMetrics
operator|.
name|getMetrics
argument_list|()
operator|.
name|getNumDecommisionedNMs
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"The decommisioned metrics are not updated"
argument_list|,
name|count
argument_list|,
name|ClusterMetrics
operator|.
name|getMetrics
argument_list|()
operator|.
name|getNumDecommisionedNMs
argument_list|()
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
block|{
if|if
condition|(
name|hostFile
operator|!=
literal|null
operator|&&
name|hostFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|hostFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|rm
operator|!=
literal|null
condition|)
block|{
name|rm
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

