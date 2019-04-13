begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.genesis
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|genesis
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|RandomStringUtils
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
name|HddsProtos
operator|.
name|ReplicationFactor
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
operator|.
name|ReplicationType
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
name|safemode
operator|.
name|SCMSafeModeManager
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
name|ExcludeList
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
name|server
operator|.
name|SCMConfigurator
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
name|StorageContainerManager
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
name|om
operator|.
name|OzoneManager
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
name|om
operator|.
name|helpers
operator|.
name|*
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
name|security
operator|.
name|UserGroupInformation
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
name|security
operator|.
name|authentication
operator|.
name|client
operator|.
name|AuthenticationException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|annotations
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|infra
operator|.
name|Blackhole
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
name|*
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
name|locks
operator|.
name|ReentrantLock
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
name|OZONE_SCM_PIPELINE_OWNER_CONTAINER_COUNT
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
name|OzoneConfigKeys
operator|.
name|OZONE_ENABLED
import|;
end_import

begin_comment
comment|/**  * Benchmarks OzoneManager.  */
end_comment

begin_class
annotation|@
name|State
argument_list|(
name|Scope
operator|.
name|Thread
argument_list|)
DECL|class|BenchMarkOzoneManager
specifier|public
class|class
name|BenchMarkOzoneManager
block|{
DECL|field|testDir
specifier|private
specifier|static
name|String
name|testDir
decl_stmt|;
DECL|field|om
specifier|private
specifier|static
name|OzoneManager
name|om
decl_stmt|;
DECL|field|scm
specifier|private
specifier|static
name|StorageContainerManager
name|scm
decl_stmt|;
DECL|field|lock
specifier|private
specifier|static
name|ReentrantLock
name|lock
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
DECL|field|volumeName
specifier|private
specifier|static
name|String
name|volumeName
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
DECL|field|bucketName
specifier|private
specifier|static
name|String
name|bucketName
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
DECL|field|keyNames
specifier|private
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|keyNames
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|clientIDs
specifier|private
specifier|static
name|List
argument_list|<
name|Long
argument_list|>
name|clientIDs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|numPipelines
specifier|private
specifier|static
name|int
name|numPipelines
init|=
literal|1
decl_stmt|;
DECL|field|numContainersPerPipeline
specifier|private
specifier|static
name|int
name|numContainersPerPipeline
init|=
literal|3
decl_stmt|;
annotation|@
name|Setup
argument_list|(
name|Level
operator|.
name|Trial
argument_list|)
DECL|method|initialize ()
specifier|public
specifier|static
name|void
name|initialize
parameter_list|()
throws|throws
name|IOException
throws|,
name|AuthenticationException
throws|,
name|InterruptedException
block|{
try|try
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
if|if
condition|(
name|scm
operator|==
literal|null
condition|)
block|{
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|OZONE_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|testDir
operator|=
name|GenesisUtil
operator|.
name|getTempPath
argument_list|()
operator|.
name|resolve
argument_list|(
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|7
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|HddsConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|,
name|testDir
argument_list|)
expr_stmt|;
name|GenesisUtil
operator|.
name|configureSCM
argument_list|(
name|conf
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|GenesisUtil
operator|.
name|configureOM
argument_list|(
name|conf
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|OZONE_SCM_PIPELINE_OWNER_CONTAINER_COUNT
argument_list|,
name|numContainersPerPipeline
argument_list|)
expr_stmt|;
name|GenesisUtil
operator|.
name|addPipelines
argument_list|(
name|ReplicationFactor
operator|.
name|THREE
argument_list|,
name|numPipelines
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|scm
operator|=
name|GenesisUtil
operator|.
name|getScm
argument_list|(
name|conf
argument_list|,
operator|new
name|SCMConfigurator
argument_list|()
argument_list|)
expr_stmt|;
name|scm
operator|.
name|start
argument_list|()
expr_stmt|;
name|om
operator|=
name|GenesisUtil
operator|.
name|getOm
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|om
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// prepare SCM
name|PipelineManager
name|pipelineManager
init|=
name|scm
operator|.
name|getPipelineManager
argument_list|()
decl_stmt|;
for|for
control|(
name|Pipeline
name|pipeline
range|:
name|pipelineManager
operator|.
name|getPipelines
argument_list|(
name|ReplicationType
operator|.
name|RATIS
argument_list|,
name|ReplicationFactor
operator|.
name|THREE
argument_list|)
control|)
block|{
name|pipelineManager
operator|.
name|openPipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|scm
operator|.
name|getEventQueue
argument_list|()
operator|.
name|fireEvent
argument_list|(
name|SCMEvents
operator|.
name|SAFE_MODE_STATUS
argument_list|,
operator|new
name|SCMSafeModeManager
operator|.
name|SafeModeStatus
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// prepare OM
name|om
operator|.
name|createVolume
argument_list|(
operator|new
name|OmVolumeArgs
operator|.
name|Builder
argument_list|()
operator|.
name|setVolume
argument_list|(
name|volumeName
argument_list|)
operator|.
name|setAdminName
argument_list|(
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
operator|.
name|getUserName
argument_list|()
argument_list|)
operator|.
name|setOwnerName
argument_list|(
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
operator|.
name|getUserName
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|om
operator|.
name|createBucket
argument_list|(
operator|new
name|OmBucketInfo
operator|.
name|Builder
argument_list|()
operator|.
name|setBucketName
argument_list|(
name|bucketName
argument_list|)
operator|.
name|setVolumeName
argument_list|(
name|volumeName
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|createKeys
argument_list|(
literal|100000
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|createKeys (int numKeys)
specifier|private
specifier|static
name|void
name|createKeys
parameter_list|(
name|int
name|numKeys
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numKeys
condition|;
name|i
operator|++
control|)
block|{
name|String
name|key
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|OmKeyArgs
name|omKeyArgs
init|=
operator|new
name|OmKeyArgs
operator|.
name|Builder
argument_list|()
operator|.
name|setVolumeName
argument_list|(
name|volumeName
argument_list|)
operator|.
name|setBucketName
argument_list|(
name|bucketName
argument_list|)
operator|.
name|setKeyName
argument_list|(
name|key
argument_list|)
operator|.
name|setDataSize
argument_list|(
literal|0
argument_list|)
operator|.
name|setFactor
argument_list|(
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|THREE
argument_list|)
operator|.
name|setType
argument_list|(
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|RATIS
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|OpenKeySession
name|keySession
init|=
name|om
operator|.
name|getKeyManager
argument_list|()
operator|.
name|openKey
argument_list|(
name|omKeyArgs
argument_list|)
decl_stmt|;
name|long
name|clientID
init|=
name|keySession
operator|.
name|getId
argument_list|()
decl_stmt|;
name|keyNames
operator|.
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|clientIDs
operator|.
name|add
argument_list|(
name|clientID
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|TearDown
argument_list|(
name|Level
operator|.
name|Trial
argument_list|)
DECL|method|tearDown ()
specifier|public
specifier|static
name|void
name|tearDown
parameter_list|()
block|{
try|try
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
if|if
condition|(
name|scm
operator|!=
literal|null
condition|)
block|{
name|scm
operator|.
name|stop
argument_list|()
expr_stmt|;
name|scm
operator|.
name|join
argument_list|()
expr_stmt|;
name|scm
operator|=
literal|null
expr_stmt|;
name|om
operator|.
name|stop
argument_list|()
expr_stmt|;
name|om
operator|.
name|join
argument_list|()
expr_stmt|;
name|om
operator|=
literal|null
expr_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
operator|new
name|File
argument_list|(
name|testDir
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Threads
argument_list|(
literal|4
argument_list|)
annotation|@
name|Benchmark
DECL|method|allocateBlockBenchMark (BenchMarkOzoneManager state, Blackhole bh)
specifier|public
name|void
name|allocateBlockBenchMark
parameter_list|(
name|BenchMarkOzoneManager
name|state
parameter_list|,
name|Blackhole
name|bh
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|index
init|=
call|(
name|int
call|)
argument_list|(
name|Math
operator|.
name|random
argument_list|()
operator|*
name|keyNames
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|key
init|=
name|keyNames
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|OmKeyArgs
name|omKeyArgs
init|=
operator|new
name|OmKeyArgs
operator|.
name|Builder
argument_list|()
operator|.
name|setVolumeName
argument_list|(
name|volumeName
argument_list|)
operator|.
name|setBucketName
argument_list|(
name|bucketName
argument_list|)
operator|.
name|setKeyName
argument_list|(
name|key
argument_list|)
operator|.
name|setDataSize
argument_list|(
literal|50
argument_list|)
operator|.
name|setFactor
argument_list|(
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|THREE
argument_list|)
operator|.
name|setType
argument_list|(
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|RATIS
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|state
operator|.
name|om
operator|.
name|allocateBlock
argument_list|(
name|omKeyArgs
argument_list|,
name|clientIDs
operator|.
name|get
argument_list|(
name|index
argument_list|)
argument_list|,
operator|new
name|ExcludeList
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Threads
argument_list|(
literal|4
argument_list|)
annotation|@
name|Benchmark
DECL|method|createAndCommitKeyBenchMark (BenchMarkOzoneManager state, Blackhole bh)
specifier|public
name|void
name|createAndCommitKeyBenchMark
parameter_list|(
name|BenchMarkOzoneManager
name|state
parameter_list|,
name|Blackhole
name|bh
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|key
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|OmKeyArgs
name|omKeyArgs
init|=
operator|new
name|OmKeyArgs
operator|.
name|Builder
argument_list|()
operator|.
name|setVolumeName
argument_list|(
name|volumeName
argument_list|)
operator|.
name|setBucketName
argument_list|(
name|bucketName
argument_list|)
operator|.
name|setKeyName
argument_list|(
name|key
argument_list|)
operator|.
name|setDataSize
argument_list|(
literal|50
argument_list|)
operator|.
name|setFactor
argument_list|(
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|THREE
argument_list|)
operator|.
name|setType
argument_list|(
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|RATIS
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|OpenKeySession
name|openKeySession
init|=
name|state
operator|.
name|om
operator|.
name|openKey
argument_list|(
name|omKeyArgs
argument_list|)
decl_stmt|;
name|state
operator|.
name|om
operator|.
name|allocateBlock
argument_list|(
name|omKeyArgs
argument_list|,
name|openKeySession
operator|.
name|getId
argument_list|()
argument_list|,
operator|new
name|ExcludeList
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

