begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.ââSee the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.ââThe ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.ââYou may obtain a copy of the License at  *  * ââââ http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.chillmode
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
name|chillmode
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
name|assertFalse
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
name|assertTrue
import|;
end_import

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
name|util
operator|.
name|ArrayList
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
name|UUID
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
name|FileUtil
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
name|HddsConfigKeys
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
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
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|PipelineReport
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
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|PipelineReportsProto
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
name|HddsTestUtils
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
name|container
operator|.
name|ContainerInfo
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
name|container
operator|.
name|MockNodeManager
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
name|events
operator|.
name|SCMEvents
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
name|pipeline
operator|.
name|Pipeline
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
name|pipeline
operator|.
name|PipelineManager
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
name|pipeline
operator|.
name|SCMPipelineManager
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
name|server
operator|.
name|SCMDatanodeHeartbeatDispatcher
operator|.
name|PipelineReportFromDatanode
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
name|EventQueue
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
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|junit
operator|.
name|rules
operator|.
name|Timeout
import|;
end_import

begin_comment
comment|/** Test class for SCMChillModeManager.  */
end_comment

begin_class
DECL|class|TestSCMChillModeManager
specifier|public
class|class
name|TestSCMChillModeManager
block|{
DECL|field|queue
specifier|private
specifier|static
name|EventQueue
name|queue
decl_stmt|;
DECL|field|scmChillModeManager
specifier|private
name|SCMChillModeManager
name|scmChillModeManager
decl_stmt|;
DECL|field|config
specifier|private
specifier|static
name|Configuration
name|config
decl_stmt|;
DECL|field|containers
specifier|private
name|List
argument_list|<
name|ContainerInfo
argument_list|>
name|containers
decl_stmt|;
annotation|@
name|Rule
DECL|field|timeout
specifier|public
name|Timeout
name|timeout
init|=
operator|new
name|Timeout
argument_list|(
literal|1000
operator|*
literal|35
argument_list|)
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setUp ()
specifier|public
specifier|static
name|void
name|setUp
parameter_list|()
block|{
name|queue
operator|=
operator|new
name|EventQueue
argument_list|()
expr_stmt|;
name|config
operator|=
operator|new
name|OzoneConfiguration
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testChillModeState ()
specifier|public
name|void
name|testChillModeState
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Test 1: test for 0 containers
name|testChillMode
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// Test 2: test for 20 containers
name|testChillMode
argument_list|(
literal|20
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testChillModeStateWithNullContainers ()
specifier|public
name|void
name|testChillModeStateWithNullContainers
parameter_list|()
block|{
operator|new
name|SCMChillModeManager
argument_list|(
name|config
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|queue
argument_list|)
expr_stmt|;
block|}
DECL|method|testChillMode (int numContainers)
specifier|private
name|void
name|testChillMode
parameter_list|(
name|int
name|numContainers
parameter_list|)
throws|throws
name|Exception
block|{
name|containers
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|containers
operator|.
name|addAll
argument_list|(
name|HddsTestUtils
operator|.
name|getContainerInfo
argument_list|(
name|numContainers
argument_list|)
argument_list|)
expr_stmt|;
comment|// Assign open state to containers to be included in the chill mode
comment|// container list
for|for
control|(
name|ContainerInfo
name|container
range|:
name|containers
control|)
block|{
name|container
operator|.
name|setState
argument_list|(
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|OPEN
argument_list|)
expr_stmt|;
block|}
name|scmChillModeManager
operator|=
operator|new
name|SCMChillModeManager
argument_list|(
name|config
argument_list|,
name|containers
argument_list|,
literal|null
argument_list|,
name|queue
argument_list|)
expr_stmt|;
name|queue
operator|.
name|addHandler
argument_list|(
name|SCMEvents
operator|.
name|NODE_REGISTRATION_CONT_REPORT
argument_list|,
name|scmChillModeManager
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|scmChillModeManager
operator|.
name|getInChillMode
argument_list|()
argument_list|)
expr_stmt|;
name|queue
operator|.
name|fireEvent
argument_list|(
name|SCMEvents
operator|.
name|NODE_REGISTRATION_CONT_REPORT
argument_list|,
name|HddsTestUtils
operator|.
name|createNodeRegistrationContainerReport
argument_list|(
name|containers
argument_list|)
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
return|return
operator|!
name|scmChillModeManager
operator|.
name|getInChillMode
argument_list|()
return|;
block|}
argument_list|,
literal|100
argument_list|,
literal|1000
operator|*
literal|5
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testChillModeExitRule ()
specifier|public
name|void
name|testChillModeExitRule
parameter_list|()
throws|throws
name|Exception
block|{
name|containers
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|containers
operator|.
name|addAll
argument_list|(
name|HddsTestUtils
operator|.
name|getContainerInfo
argument_list|(
literal|25
operator|*
literal|4
argument_list|)
argument_list|)
expr_stmt|;
comment|// Assign open state to containers to be included in the chill mode
comment|// container list
for|for
control|(
name|ContainerInfo
name|container
range|:
name|containers
control|)
block|{
name|container
operator|.
name|setState
argument_list|(
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|CLOSED
argument_list|)
expr_stmt|;
block|}
name|scmChillModeManager
operator|=
operator|new
name|SCMChillModeManager
argument_list|(
name|config
argument_list|,
name|containers
argument_list|,
literal|null
argument_list|,
name|queue
argument_list|)
expr_stmt|;
name|queue
operator|.
name|addHandler
argument_list|(
name|SCMEvents
operator|.
name|NODE_REGISTRATION_CONT_REPORT
argument_list|,
name|scmChillModeManager
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|scmChillModeManager
operator|.
name|getInChillMode
argument_list|()
argument_list|)
expr_stmt|;
name|testContainerThreshold
argument_list|(
name|containers
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
literal|25
argument_list|)
argument_list|,
literal|0.25
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|scmChillModeManager
operator|.
name|getInChillMode
argument_list|()
argument_list|)
expr_stmt|;
name|testContainerThreshold
argument_list|(
name|containers
operator|.
name|subList
argument_list|(
literal|25
argument_list|,
literal|50
argument_list|)
argument_list|,
literal|0.50
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|scmChillModeManager
operator|.
name|getInChillMode
argument_list|()
argument_list|)
expr_stmt|;
name|testContainerThreshold
argument_list|(
name|containers
operator|.
name|subList
argument_list|(
literal|50
argument_list|,
literal|75
argument_list|)
argument_list|,
literal|0.75
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|scmChillModeManager
operator|.
name|getInChillMode
argument_list|()
argument_list|)
expr_stmt|;
name|testContainerThreshold
argument_list|(
name|containers
operator|.
name|subList
argument_list|(
literal|75
argument_list|,
literal|100
argument_list|)
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
return|return
operator|!
name|scmChillModeManager
operator|.
name|getInChillMode
argument_list|()
return|;
block|}
argument_list|,
literal|100
argument_list|,
literal|1000
operator|*
literal|5
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"TODO:HDDS-1140"
argument_list|)
DECL|method|testDisableChillMode ()
specifier|public
name|void
name|testDisableChillMode
parameter_list|()
block|{
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|HddsConfigKeys
operator|.
name|HDDS_SCM_CHILLMODE_ENABLED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|scmChillModeManager
operator|=
operator|new
name|SCMChillModeManager
argument_list|(
name|conf
argument_list|,
name|containers
argument_list|,
literal|null
argument_list|,
name|queue
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|scmChillModeManager
operator|.
name|getInChillMode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testChillModeDataNodeExitRule ()
specifier|public
name|void
name|testChillModeDataNodeExitRule
parameter_list|()
throws|throws
name|Exception
block|{
name|containers
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|testChillModeDataNodes
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|testChillModeDataNodes
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|testChillModeDataNodes
argument_list|(
literal|5
argument_list|)
expr_stmt|;
block|}
comment|/**    * Check that containers in Allocated state are not considered while    * computing percentage of containers with at least 1 reported replica in    * chill mode exit rule.    */
annotation|@
name|Test
DECL|method|testContainerChillModeRule ()
specifier|public
name|void
name|testContainerChillModeRule
parameter_list|()
throws|throws
name|Exception
block|{
name|containers
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
comment|// Add 100 containers to the list of containers in SCM
name|containers
operator|.
name|addAll
argument_list|(
name|HddsTestUtils
operator|.
name|getContainerInfo
argument_list|(
literal|25
operator|*
literal|4
argument_list|)
argument_list|)
expr_stmt|;
comment|// Assign CLOSED state to first 25 containers and OPEM state to rest
comment|// of the containers
for|for
control|(
name|ContainerInfo
name|container
range|:
name|containers
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
literal|25
argument_list|)
control|)
block|{
name|container
operator|.
name|setState
argument_list|(
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|CLOSED
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|ContainerInfo
name|container
range|:
name|containers
operator|.
name|subList
argument_list|(
literal|25
argument_list|,
literal|100
argument_list|)
control|)
block|{
name|container
operator|.
name|setState
argument_list|(
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|OPEN
argument_list|)
expr_stmt|;
block|}
name|scmChillModeManager
operator|=
operator|new
name|SCMChillModeManager
argument_list|(
name|config
argument_list|,
name|containers
argument_list|,
literal|null
argument_list|,
name|queue
argument_list|)
expr_stmt|;
name|queue
operator|.
name|addHandler
argument_list|(
name|SCMEvents
operator|.
name|NODE_REGISTRATION_CONT_REPORT
argument_list|,
name|scmChillModeManager
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|scmChillModeManager
operator|.
name|getInChillMode
argument_list|()
argument_list|)
expr_stmt|;
comment|// When 10 CLOSED containers are reported by DNs, the computed container
comment|// threshold should be 10/25 as there are only 25 CLOSED containers.
comment|// Containers in OPEN state should not contribute towards list of
comment|// containers while calculating container threshold in SCMChillNodeManager
name|testContainerThreshold
argument_list|(
name|containers
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
literal|10
argument_list|)
argument_list|,
literal|0.4
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|scmChillModeManager
operator|.
name|getInChillMode
argument_list|()
argument_list|)
expr_stmt|;
comment|// When remaining 15 OPEN containers are reported by DNs, the container
comment|// threshold should be (10+15)/25.
name|testContainerThreshold
argument_list|(
name|containers
operator|.
name|subList
argument_list|(
literal|10
argument_list|,
literal|25
argument_list|)
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
return|return
operator|!
name|scmChillModeManager
operator|.
name|getInChillMode
argument_list|()
return|;
block|}
argument_list|,
literal|100
argument_list|,
literal|1000
operator|*
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testChillModeDataNodes (int numOfDns)
specifier|private
name|void
name|testChillModeDataNodes
parameter_list|(
name|int
name|numOfDns
parameter_list|)
throws|throws
name|Exception
block|{
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|HddsConfigKeys
operator|.
name|HDDS_SCM_CHILLMODE_MIN_DATANODE
argument_list|,
name|numOfDns
argument_list|)
expr_stmt|;
name|scmChillModeManager
operator|=
operator|new
name|SCMChillModeManager
argument_list|(
name|conf
argument_list|,
name|containers
argument_list|,
literal|null
argument_list|,
name|queue
argument_list|)
expr_stmt|;
name|queue
operator|.
name|addHandler
argument_list|(
name|SCMEvents
operator|.
name|NODE_REGISTRATION_CONT_REPORT
argument_list|,
name|scmChillModeManager
argument_list|)
expr_stmt|;
comment|// Assert SCM is in Chill mode.
name|assertTrue
argument_list|(
name|scmChillModeManager
operator|.
name|getInChillMode
argument_list|()
argument_list|)
expr_stmt|;
comment|// Register all DataNodes except last one and assert SCM is in chill mode.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numOfDns
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|queue
operator|.
name|fireEvent
argument_list|(
name|SCMEvents
operator|.
name|NODE_REGISTRATION_CONT_REPORT
argument_list|,
name|HddsTestUtils
operator|.
name|createNodeRegistrationContainerReport
argument_list|(
name|containers
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|scmChillModeManager
operator|.
name|getInChillMode
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|scmChillModeManager
operator|.
name|getCurrentContainerThreshold
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|numOfDns
operator|==
literal|0
condition|)
block|{
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
return|return
name|scmChillModeManager
operator|.
name|getInChillMode
argument_list|()
return|;
block|}
argument_list|,
literal|10
argument_list|,
literal|1000
operator|*
literal|10
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Register last DataNode and check that SCM is out of Chill mode.
name|queue
operator|.
name|fireEvent
argument_list|(
name|SCMEvents
operator|.
name|NODE_REGISTRATION_CONT_REPORT
argument_list|,
name|HddsTestUtils
operator|.
name|createNodeRegistrationContainerReport
argument_list|(
name|containers
argument_list|)
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
return|return
operator|!
name|scmChillModeManager
operator|.
name|getInChillMode
argument_list|()
return|;
block|}
argument_list|,
literal|10
argument_list|,
literal|1000
operator|*
literal|10
argument_list|)
expr_stmt|;
block|}
DECL|method|testContainerThreshold (List<ContainerInfo> dnContainers, double expectedThreshold)
specifier|private
name|void
name|testContainerThreshold
parameter_list|(
name|List
argument_list|<
name|ContainerInfo
argument_list|>
name|dnContainers
parameter_list|,
name|double
name|expectedThreshold
parameter_list|)
throws|throws
name|Exception
block|{
name|queue
operator|.
name|fireEvent
argument_list|(
name|SCMEvents
operator|.
name|NODE_REGISTRATION_CONT_REPORT
argument_list|,
name|HddsTestUtils
operator|.
name|createNodeRegistrationContainerReport
argument_list|(
name|dnContainers
argument_list|)
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
name|double
name|threshold
init|=
name|scmChillModeManager
operator|.
name|getCurrentContainerThreshold
argument_list|()
decl_stmt|;
return|return
name|threshold
operator|==
name|expectedThreshold
return|;
block|}
argument_list|,
literal|100
argument_list|,
literal|2000
operator|*
literal|9
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testChillModePipelineExitRule ()
specifier|public
name|void
name|testChillModePipelineExitRule
parameter_list|()
throws|throws
name|Exception
block|{
name|containers
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|containers
operator|.
name|addAll
argument_list|(
name|HddsTestUtils
operator|.
name|getContainerInfo
argument_list|(
literal|25
operator|*
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|storageDir
init|=
name|GenericTestUtils
operator|.
name|getTempPath
argument_list|(
name|TestSCMChillModeManager
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|MockNodeManager
name|nodeManager
init|=
operator|new
name|MockNodeManager
argument_list|(
literal|true
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|config
operator|.
name|set
argument_list|(
name|HddsConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|,
name|storageDir
argument_list|)
expr_stmt|;
comment|// enable pipeline check
name|config
operator|.
name|setBoolean
argument_list|(
name|HddsConfigKeys
operator|.
name|HDDS_SCM_CHILLMODE_PIPELINE_AVAILABILITY_CHECK
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|PipelineManager
name|pipelineManager
init|=
operator|new
name|SCMPipelineManager
argument_list|(
name|config
argument_list|,
name|nodeManager
argument_list|,
name|queue
argument_list|)
decl_stmt|;
name|scmChillModeManager
operator|=
operator|new
name|SCMChillModeManager
argument_list|(
name|config
argument_list|,
name|containers
argument_list|,
name|pipelineManager
argument_list|,
name|queue
argument_list|)
expr_stmt|;
name|queue
operator|.
name|addHandler
argument_list|(
name|SCMEvents
operator|.
name|NODE_REGISTRATION_CONT_REPORT
argument_list|,
name|scmChillModeManager
argument_list|)
expr_stmt|;
name|queue
operator|.
name|fireEvent
argument_list|(
name|SCMEvents
operator|.
name|NODE_REGISTRATION_CONT_REPORT
argument_list|,
name|HddsTestUtils
operator|.
name|createNodeRegistrationContainerReport
argument_list|(
name|containers
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|scmChillModeManager
operator|.
name|getInChillMode
argument_list|()
argument_list|)
expr_stmt|;
comment|// simulation a pipeline report to trigger the rule check
name|Pipeline
name|pipeline
init|=
name|pipelineManager
operator|.
name|createPipeline
argument_list|(
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|STAND_ALONE
argument_list|,
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|ONE
argument_list|)
decl_stmt|;
name|PipelineReportsProto
operator|.
name|Builder
name|reportBuilder
init|=
name|PipelineReportsProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|reportBuilder
operator|.
name|addPipelineReport
argument_list|(
name|PipelineReport
operator|.
name|newBuilder
argument_list|()
operator|.
name|setPipelineID
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
operator|.
name|getProtobuf
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|queue
operator|.
name|fireEvent
argument_list|(
name|SCMEvents
operator|.
name|PIPELINE_REPORT
argument_list|,
operator|new
name|PipelineReportFromDatanode
argument_list|(
name|pipeline
operator|.
name|getNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|reportBuilder
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
return|return
operator|!
name|scmChillModeManager
operator|.
name|getInChillMode
argument_list|()
return|;
block|}
argument_list|,
literal|100
argument_list|,
literal|1000
operator|*
literal|10
argument_list|)
expr_stmt|;
name|pipelineManager
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|config
operator|.
name|setBoolean
argument_list|(
name|HddsConfigKeys
operator|.
name|HDDS_SCM_CHILLMODE_PIPELINE_AVAILABILITY_CHECK
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
operator|new
name|File
argument_list|(
name|storageDir
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

