begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.report
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|report
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ThreadFactoryBuilder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|Descriptors
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|GeneratedMessage
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
name|concurrent
operator|.
name|ConcurrentHashMap
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
name|hdds
operator|.
name|HddsIdFactory
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
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|CommandStatus
operator|.
name|Status
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
name|ContainerReportsProto
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
name|NodeReportProto
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
name|SCMCommandProto
operator|.
name|Type
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
name|SCMHeartbeatRequestProto
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
name|container
operator|.
name|common
operator|.
name|statemachine
operator|.
name|StateContext
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
name|commands
operator|.
name|CommandStatus
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
name|util
operator|.
name|concurrent
operator|.
name|HadoopExecutors
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
name|BeforeClass
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
name|java
operator|.
name|util
operator|.
name|Random
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ScheduledExecutorService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|times
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
name|verify
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
name|when
import|;
end_import

begin_comment
comment|/**  * Test cases to test {@link ReportPublisher}.  */
end_comment

begin_class
DECL|class|TestReportPublisher
specifier|public
class|class
name|TestReportPublisher
block|{
DECL|field|config
specifier|private
specifier|static
name|Configuration
name|config
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setup ()
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
block|{
name|config
operator|=
operator|new
name|OzoneConfiguration
argument_list|()
expr_stmt|;
block|}
comment|/**    * Dummy report publisher for testing.    */
DECL|class|DummyReportPublisher
specifier|private
class|class
name|DummyReportPublisher
extends|extends
name|ReportPublisher
block|{
DECL|field|frequency
specifier|private
specifier|final
name|long
name|frequency
decl_stmt|;
DECL|field|getReportCount
specifier|private
name|int
name|getReportCount
init|=
literal|0
decl_stmt|;
DECL|method|DummyReportPublisher (long frequency)
name|DummyReportPublisher
parameter_list|(
name|long
name|frequency
parameter_list|)
block|{
name|this
operator|.
name|frequency
operator|=
name|frequency
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getReportFrequency ()
specifier|protected
name|long
name|getReportFrequency
parameter_list|()
block|{
return|return
name|frequency
return|;
block|}
annotation|@
name|Override
DECL|method|getReport ()
specifier|protected
name|GeneratedMessage
name|getReport
parameter_list|()
block|{
name|getReportCount
operator|++
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testReportPublisherInit ()
specifier|public
name|void
name|testReportPublisherInit
parameter_list|()
block|{
name|ReportPublisher
name|publisher
init|=
operator|new
name|DummyReportPublisher
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|StateContext
name|dummyContext
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|StateContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|ScheduledExecutorService
name|dummyExecutorService
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|ScheduledExecutorService
operator|.
name|class
argument_list|)
decl_stmt|;
name|publisher
operator|.
name|init
argument_list|(
name|dummyContext
argument_list|,
name|dummyExecutorService
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|dummyExecutorService
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|schedule
argument_list|(
name|publisher
argument_list|,
literal|0
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testScheduledReport ()
specifier|public
name|void
name|testScheduledReport
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|ReportPublisher
name|publisher
init|=
operator|new
name|DummyReportPublisher
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|StateContext
name|dummyContext
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|StateContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|ScheduledExecutorService
name|executorService
init|=
name|HadoopExecutors
operator|.
name|newScheduledThreadPool
argument_list|(
literal|1
argument_list|,
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
operator|.
name|setNameFormat
argument_list|(
literal|"Unit test ReportManager Thread - %d"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|publisher
operator|.
name|init
argument_list|(
name|dummyContext
argument_list|,
name|executorService
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|150
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
operator|(
operator|(
name|DummyReportPublisher
operator|)
name|publisher
operator|)
operator|.
name|getReportCount
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
operator|(
operator|(
name|DummyReportPublisher
operator|)
name|publisher
operator|)
operator|.
name|getReportCount
argument_list|)
expr_stmt|;
name|executorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPublishReport ()
specifier|public
name|void
name|testPublishReport
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|ReportPublisher
name|publisher
init|=
operator|new
name|DummyReportPublisher
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|StateContext
name|dummyContext
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|StateContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|ScheduledExecutorService
name|executorService
init|=
name|HadoopExecutors
operator|.
name|newScheduledThreadPool
argument_list|(
literal|1
argument_list|,
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
operator|.
name|setNameFormat
argument_list|(
literal|"Unit test ReportManager Thread - %d"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|publisher
operator|.
name|init
argument_list|(
name|dummyContext
argument_list|,
name|executorService
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|150
argument_list|)
expr_stmt|;
name|executorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
operator|(
operator|(
name|DummyReportPublisher
operator|)
name|publisher
operator|)
operator|.
name|getReportCount
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|dummyContext
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|addReport
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCommandStatusPublisher ()
specifier|public
name|void
name|testCommandStatusPublisher
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|StateContext
name|dummyContext
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|StateContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|ReportPublisher
name|publisher
init|=
operator|new
name|CommandStatusReportPublisher
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|Long
argument_list|,
name|CommandStatus
argument_list|>
name|cmdStatusMap
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|when
argument_list|(
name|dummyContext
operator|.
name|getCommandStatusMap
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|cmdStatusMap
argument_list|)
expr_stmt|;
name|publisher
operator|.
name|setConf
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|ScheduledExecutorService
name|executorService
init|=
name|HadoopExecutors
operator|.
name|newScheduledThreadPool
argument_list|(
literal|1
argument_list|,
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
operator|.
name|setNameFormat
argument_list|(
literal|"Unit test ReportManager Thread - %d"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|publisher
operator|.
name|init
argument_list|(
name|dummyContext
argument_list|,
name|executorService
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
operator|(
operator|(
name|CommandStatusReportPublisher
operator|)
name|publisher
operator|)
operator|.
name|getReport
argument_list|()
operator|.
name|getCmdStatusCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// Insert to status object to state context map and then get the report.
name|CommandStatus
name|obj1
init|=
name|CommandStatus
operator|.
name|CommandStatusBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdId
argument_list|(
name|HddsIdFactory
operator|.
name|getLongId
argument_list|()
argument_list|)
operator|.
name|setType
argument_list|(
name|Type
operator|.
name|deleteBlocksCommand
argument_list|)
operator|.
name|setStatus
argument_list|(
name|Status
operator|.
name|PENDING
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|CommandStatus
name|obj2
init|=
name|CommandStatus
operator|.
name|CommandStatusBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdId
argument_list|(
name|HddsIdFactory
operator|.
name|getLongId
argument_list|()
argument_list|)
operator|.
name|setType
argument_list|(
name|Type
operator|.
name|closeContainerCommand
argument_list|)
operator|.
name|setStatus
argument_list|(
name|Status
operator|.
name|EXECUTED
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|cmdStatusMap
operator|.
name|put
argument_list|(
name|obj1
operator|.
name|getCmdId
argument_list|()
argument_list|,
name|obj1
argument_list|)
expr_stmt|;
name|cmdStatusMap
operator|.
name|put
argument_list|(
name|obj2
operator|.
name|getCmdId
argument_list|()
argument_list|,
name|obj2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Should publish report with 2 status objects"
argument_list|,
literal|2
argument_list|,
operator|(
operator|(
name|CommandStatusReportPublisher
operator|)
name|publisher
operator|)
operator|.
name|getReport
argument_list|()
operator|.
name|getCmdStatusCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Next report should have 1 status objects as command status o"
operator|+
literal|"bjects are still in Pending state"
argument_list|,
literal|1
argument_list|,
operator|(
operator|(
name|CommandStatusReportPublisher
operator|)
name|publisher
operator|)
operator|.
name|getReport
argument_list|()
operator|.
name|getCmdStatusCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Next report should have 1 status objects as command status "
operator|+
literal|"objects are still in Pending state"
argument_list|,
operator|(
operator|(
name|CommandStatusReportPublisher
operator|)
name|publisher
operator|)
operator|.
name|getReport
argument_list|()
operator|.
name|getCmdStatusList
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStatus
argument_list|()
operator|.
name|equals
argument_list|(
name|Status
operator|.
name|PENDING
argument_list|)
argument_list|)
expr_stmt|;
name|executorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAddingReportToHeartbeat ()
specifier|public
name|void
name|testAddingReportToHeartbeat
parameter_list|()
block|{
name|GeneratedMessage
name|nodeReport
init|=
name|NodeReportProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
name|GeneratedMessage
name|containerReport
init|=
name|ContainerReportsProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
name|SCMHeartbeatRequestProto
operator|.
name|Builder
name|heartbeatBuilder
init|=
name|SCMHeartbeatRequestProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|heartbeatBuilder
operator|.
name|setDatanodeDetails
argument_list|(
name|getDatanodeDetails
argument_list|()
operator|.
name|getProtoBufMessage
argument_list|()
argument_list|)
expr_stmt|;
name|addReport
argument_list|(
name|heartbeatBuilder
argument_list|,
name|nodeReport
argument_list|)
expr_stmt|;
name|addReport
argument_list|(
name|heartbeatBuilder
argument_list|,
name|containerReport
argument_list|)
expr_stmt|;
name|SCMHeartbeatRequestProto
name|heartbeat
init|=
name|heartbeatBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|heartbeat
operator|.
name|hasNodeReport
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|heartbeat
operator|.
name|hasContainerReport
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get a datanode details.    *    * @return DatanodeDetails    */
DECL|method|getDatanodeDetails ()
specifier|private
specifier|static
name|DatanodeDetails
name|getDatanodeDetails
parameter_list|()
block|{
name|String
name|uuid
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|String
name|ipAddress
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|256
argument_list|)
operator|+
literal|"."
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|256
argument_list|)
operator|+
literal|"."
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|256
argument_list|)
operator|+
literal|"."
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|256
argument_list|)
decl_stmt|;
name|DatanodeDetails
operator|.
name|Port
name|containerPort
init|=
name|DatanodeDetails
operator|.
name|newPort
argument_list|(
name|DatanodeDetails
operator|.
name|Port
operator|.
name|Name
operator|.
name|STANDALONE
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|DatanodeDetails
operator|.
name|Port
name|ratisPort
init|=
name|DatanodeDetails
operator|.
name|newPort
argument_list|(
name|DatanodeDetails
operator|.
name|Port
operator|.
name|Name
operator|.
name|RATIS
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|DatanodeDetails
operator|.
name|Port
name|restPort
init|=
name|DatanodeDetails
operator|.
name|newPort
argument_list|(
name|DatanodeDetails
operator|.
name|Port
operator|.
name|Name
operator|.
name|REST
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|DatanodeDetails
operator|.
name|Builder
name|builder
init|=
name|DatanodeDetails
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setUuid
argument_list|(
name|uuid
argument_list|)
operator|.
name|setHostName
argument_list|(
literal|"localhost"
argument_list|)
operator|.
name|setIpAddress
argument_list|(
name|ipAddress
argument_list|)
operator|.
name|addPort
argument_list|(
name|containerPort
argument_list|)
operator|.
name|addPort
argument_list|(
name|ratisPort
argument_list|)
operator|.
name|addPort
argument_list|(
name|restPort
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Adds the report to heartbeat.    *    * @param requestBuilder builder to which the report has to be added.    * @param report         the report to be added.    */
DECL|method|addReport (SCMHeartbeatRequestProto.Builder requestBuilder, GeneratedMessage report)
specifier|private
specifier|static
name|void
name|addReport
parameter_list|(
name|SCMHeartbeatRequestProto
operator|.
name|Builder
name|requestBuilder
parameter_list|,
name|GeneratedMessage
name|report
parameter_list|)
block|{
name|String
name|reportName
init|=
name|report
operator|.
name|getDescriptorForType
argument_list|()
operator|.
name|getFullName
argument_list|()
decl_stmt|;
for|for
control|(
name|Descriptors
operator|.
name|FieldDescriptor
name|descriptor
range|:
name|SCMHeartbeatRequestProto
operator|.
name|getDescriptor
argument_list|()
operator|.
name|getFields
argument_list|()
control|)
block|{
name|String
name|heartbeatFieldName
init|=
name|descriptor
operator|.
name|getMessageType
argument_list|()
operator|.
name|getFullName
argument_list|()
decl_stmt|;
if|if
condition|(
name|heartbeatFieldName
operator|.
name|equals
argument_list|(
name|reportName
argument_list|)
condition|)
block|{
name|requestBuilder
operator|.
name|setField
argument_list|(
name|descriptor
argument_list|,
name|report
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

