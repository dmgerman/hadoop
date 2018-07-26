begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.container.closer
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
name|container
operator|.
name|closer
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
name|scm
operator|.
name|TestUtils
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
name|ContainerMapping
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
name|container
operator|.
name|TestContainerMapping
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
name|common
operator|.
name|helpers
operator|.
name|ContainerWithPipeline
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
name|common
operator|.
name|helpers
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
name|ozone
operator|.
name|OzoneConfigKeys
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
name|SCMTestUtils
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
name|AfterClass
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
name|concurrent
operator|.
name|TimeUnit
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
name|hdds
operator|.
name|HddsConfigKeys
operator|.
name|HDDS_CONTAINER_REPORT_INTERVAL
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
name|hdds
operator|.
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_SIZE_DEFAULT
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
name|hdds
operator|.
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_SIZE_GB
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
operator|.
name|LifeCycleEvent
operator|.
name|CREATE
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
operator|.
name|LifeCycleEvent
operator|.
name|CREATED
import|;
end_import

begin_comment
comment|/**  * Test class for Closing Container.  */
end_comment

begin_class
DECL|class|TestContainerCloser
specifier|public
class|class
name|TestContainerCloser
block|{
DECL|field|GIGABYTE
specifier|private
specifier|static
specifier|final
name|long
name|GIGABYTE
init|=
literal|1024L
operator|*
literal|1024L
operator|*
literal|1024L
decl_stmt|;
DECL|field|configuration
specifier|private
specifier|static
name|Configuration
name|configuration
decl_stmt|;
DECL|field|nodeManager
specifier|private
specifier|static
name|MockNodeManager
name|nodeManager
decl_stmt|;
DECL|field|mapping
specifier|private
specifier|static
name|ContainerMapping
name|mapping
decl_stmt|;
DECL|field|size
specifier|private
specifier|static
name|long
name|size
decl_stmt|;
DECL|field|testDir
specifier|private
specifier|static
name|File
name|testDir
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setUp ()
specifier|public
specifier|static
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|configuration
operator|=
name|SCMTestUtils
operator|.
name|getConf
argument_list|()
expr_stmt|;
name|size
operator|=
name|configuration
operator|.
name|getLong
argument_list|(
name|OZONE_SCM_CONTAINER_SIZE_GB
argument_list|,
name|OZONE_SCM_CONTAINER_SIZE_DEFAULT
argument_list|)
operator|*
literal|1024
operator|*
literal|1024
operator|*
literal|1024
expr_stmt|;
name|configuration
operator|.
name|setTimeDuration
argument_list|(
name|HDDS_CONTAINER_REPORT_INTERVAL
argument_list|,
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|testDir
operator|=
name|GenericTestUtils
operator|.
name|getTestDir
argument_list|(
name|TestContainerMapping
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|configuration
operator|.
name|set
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|,
name|testDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|nodeManager
operator|=
operator|new
name|MockNodeManager
argument_list|(
literal|true
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|mapping
operator|=
operator|new
name|ContainerMapping
argument_list|(
name|configuration
argument_list|,
name|nodeManager
argument_list|,
literal|128
argument_list|,
operator|new
name|EventQueue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|tearDown ()
specifier|public
specifier|static
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|mapping
operator|!=
literal|null
condition|)
block|{
name|mapping
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|testDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testClose ()
specifier|public
name|void
name|testClose
parameter_list|()
throws|throws
name|IOException
block|{
name|ContainerWithPipeline
name|containerWithPipeline
init|=
name|mapping
operator|.
name|allocateContainer
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
argument_list|,
literal|"ozone"
argument_list|)
decl_stmt|;
name|ContainerInfo
name|info
init|=
name|containerWithPipeline
operator|.
name|getContainerInfo
argument_list|()
decl_stmt|;
comment|//Execute these state transitions so that we can close the container.
name|mapping
operator|.
name|updateContainerState
argument_list|(
name|info
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|CREATE
argument_list|)
expr_stmt|;
name|mapping
operator|.
name|updateContainerState
argument_list|(
name|info
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|CREATED
argument_list|)
expr_stmt|;
name|long
name|currentCount
init|=
name|mapping
operator|.
name|getCloser
argument_list|()
operator|.
name|getCloseCount
argument_list|()
decl_stmt|;
name|long
name|runCount
init|=
name|mapping
operator|.
name|getCloser
argument_list|()
operator|.
name|getThreadRunCount
argument_list|()
decl_stmt|;
name|DatanodeDetails
name|datanode
init|=
name|containerWithPipeline
operator|.
name|getPipeline
argument_list|()
operator|.
name|getLeader
argument_list|()
decl_stmt|;
comment|// Send a container report with used set to 1 GB. This should not close.
name|sendContainerReport
argument_list|(
name|info
argument_list|,
literal|1
operator|*
name|GIGABYTE
argument_list|)
expr_stmt|;
comment|// with only one container the  cleaner thread should not run.
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|mapping
operator|.
name|getCloser
argument_list|()
operator|.
name|getThreadRunCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// With only 1 GB, the container should not be queued for closing.
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|mapping
operator|.
name|getCloser
argument_list|()
operator|.
name|getCloseCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// Assert that the Close command was not queued for this Datanode.
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|nodeManager
operator|.
name|getCommandCount
argument_list|(
name|datanode
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|newUsed
init|=
call|(
name|long
call|)
argument_list|(
name|size
operator|*
literal|0.91f
argument_list|)
decl_stmt|;
name|sendContainerReport
argument_list|(
name|info
argument_list|,
name|newUsed
argument_list|)
expr_stmt|;
comment|// with only one container the cleaner thread should not run.
name|Assert
operator|.
name|assertEquals
argument_list|(
name|runCount
argument_list|,
name|mapping
operator|.
name|getCloser
argument_list|()
operator|.
name|getThreadRunCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// and close count will be one.
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|mapping
operator|.
name|getCloser
argument_list|()
operator|.
name|getCloseCount
argument_list|()
operator|-
name|currentCount
argument_list|)
expr_stmt|;
comment|// Assert that the Close command was Queued for this Datanode.
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|nodeManager
operator|.
name|getCommandCount
argument_list|(
name|datanode
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRepeatedClose ()
specifier|public
name|void
name|testRepeatedClose
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
comment|// This test asserts that if we queue more than one report then the
comment|// second report is discarded by the system if it lands in the 3 * report
comment|// frequency window.
name|configuration
operator|.
name|setTimeDuration
argument_list|(
name|HDDS_CONTAINER_REPORT_INTERVAL
argument_list|,
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|ContainerWithPipeline
name|containerWithPipeline
init|=
name|mapping
operator|.
name|allocateContainer
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
argument_list|,
literal|"ozone"
argument_list|)
decl_stmt|;
name|ContainerInfo
name|info
init|=
name|containerWithPipeline
operator|.
name|getContainerInfo
argument_list|()
decl_stmt|;
comment|//Execute these state transitions so that we can close the container.
name|mapping
operator|.
name|updateContainerState
argument_list|(
name|info
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|CREATE
argument_list|)
expr_stmt|;
name|long
name|currentCount
init|=
name|mapping
operator|.
name|getCloser
argument_list|()
operator|.
name|getCloseCount
argument_list|()
decl_stmt|;
name|long
name|runCount
init|=
name|mapping
operator|.
name|getCloser
argument_list|()
operator|.
name|getThreadRunCount
argument_list|()
decl_stmt|;
name|DatanodeDetails
name|datanodeDetails
init|=
name|containerWithPipeline
operator|.
name|getPipeline
argument_list|()
operator|.
name|getLeader
argument_list|()
decl_stmt|;
comment|// Send this command twice and assert we have only one command in queue.
name|sendContainerReport
argument_list|(
name|info
argument_list|,
literal|5
operator|*
name|GIGABYTE
argument_list|)
expr_stmt|;
name|sendContainerReport
argument_list|(
name|info
argument_list|,
literal|5
operator|*
name|GIGABYTE
argument_list|)
expr_stmt|;
comment|// Assert that the Close command was Queued for this Datanode.
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|nodeManager
operator|.
name|getCommandCount
argument_list|(
name|datanodeDetails
argument_list|)
argument_list|)
expr_stmt|;
comment|// And close count will be one.
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|mapping
operator|.
name|getCloser
argument_list|()
operator|.
name|getCloseCount
argument_list|()
operator|-
name|currentCount
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
comment|//send another close and the system will queue this to the command queue.
name|sendContainerReport
argument_list|(
name|info
argument_list|,
literal|5
operator|*
name|GIGABYTE
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|nodeManager
operator|.
name|getCommandCount
argument_list|(
name|datanodeDetails
argument_list|)
argument_list|)
expr_stmt|;
comment|// but the close count will still be one, since from the point of view of
comment|// closer we are closing only one container even if we have send multiple
comment|// close commands to the datanode.
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|mapping
operator|.
name|getCloser
argument_list|()
operator|.
name|getCloseCount
argument_list|()
operator|-
name|currentCount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCleanupThreadRuns ()
specifier|public
name|void
name|testCleanupThreadRuns
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
comment|// This test asserts that clean up thread runs once we have closed a
comment|// number above cleanup water mark.
name|long
name|runCount
init|=
name|mapping
operator|.
name|getCloser
argument_list|()
operator|.
name|getThreadRunCount
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|ContainerCloser
operator|.
name|getCleanupWaterMark
argument_list|()
operator|+
literal|10
condition|;
name|x
operator|++
control|)
block|{
name|ContainerWithPipeline
name|containerWithPipeline
init|=
name|mapping
operator|.
name|allocateContainer
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
argument_list|,
literal|"ozone"
argument_list|)
decl_stmt|;
name|ContainerInfo
name|info
init|=
name|containerWithPipeline
operator|.
name|getContainerInfo
argument_list|()
decl_stmt|;
name|mapping
operator|.
name|updateContainerState
argument_list|(
name|info
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|CREATE
argument_list|)
expr_stmt|;
name|mapping
operator|.
name|updateContainerState
argument_list|(
name|info
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|CREATED
argument_list|)
expr_stmt|;
name|sendContainerReport
argument_list|(
name|info
argument_list|,
literal|5
operator|*
name|GIGABYTE
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// Assert that cleanup thread ran at least once.
name|Assert
operator|.
name|assertTrue
argument_list|(
name|mapping
operator|.
name|getCloser
argument_list|()
operator|.
name|getThreadRunCount
argument_list|()
operator|-
name|runCount
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|sendContainerReport (ContainerInfo info, long used)
specifier|private
name|void
name|sendContainerReport
parameter_list|(
name|ContainerInfo
name|info
parameter_list|,
name|long
name|used
parameter_list|)
throws|throws
name|IOException
block|{
name|ContainerReportsProto
operator|.
name|Builder
name|reports
init|=
name|ContainerReportsProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerInfo
operator|.
name|Builder
name|ciBuilder
init|=
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerInfo
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|ciBuilder
operator|.
name|setContainerID
argument_list|(
name|info
operator|.
name|getContainerID
argument_list|()
argument_list|)
operator|.
name|setFinalhash
argument_list|(
literal|"e16cc9d6024365750ed8dbd194ea46d2"
argument_list|)
operator|.
name|setSize
argument_list|(
name|size
argument_list|)
operator|.
name|setUsed
argument_list|(
name|used
argument_list|)
operator|.
name|setKeyCount
argument_list|(
literal|100000000L
argument_list|)
operator|.
name|setReadCount
argument_list|(
literal|100000000L
argument_list|)
operator|.
name|setWriteCount
argument_list|(
literal|100000000L
argument_list|)
operator|.
name|setReadBytes
argument_list|(
literal|2000000000L
argument_list|)
operator|.
name|setWriteBytes
argument_list|(
literal|2000000000L
argument_list|)
operator|.
name|setDeleteTransactionId
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|reports
operator|.
name|addReports
argument_list|(
name|ciBuilder
argument_list|)
expr_stmt|;
name|mapping
operator|.
name|processContainerReports
argument_list|(
name|TestUtils
operator|.
name|randomDatanodeDetails
argument_list|()
argument_list|,
name|reports
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

