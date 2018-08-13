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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
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
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|PipelineID
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
name|states
operator|.
name|ContainerStateMap
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
name|exceptions
operator|.
name|SCMException
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
name|Time
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
name|Benchmark
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
name|Level
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
name|Scope
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
name|Setup
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
name|State
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
name|IOException
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
name|List
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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
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
name|atomic
operator|.
name|AtomicInteger
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
name|LifeCycleState
operator|.
name|OPEN
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
name|LifeCycleState
operator|.
name|CLOSED
import|;
end_import

begin_class
annotation|@
name|State
argument_list|(
name|Scope
operator|.
name|Thread
argument_list|)
DECL|class|BenchMarkContainerStateMap
specifier|public
class|class
name|BenchMarkContainerStateMap
block|{
DECL|field|stateMap
specifier|private
name|ContainerStateMap
name|stateMap
decl_stmt|;
DECL|field|containerID
specifier|private
name|AtomicInteger
name|containerID
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
name|void
name|initialize
parameter_list|()
throws|throws
name|IOException
block|{
name|stateMap
operator|=
operator|new
name|ContainerStateMap
argument_list|()
expr_stmt|;
name|Pipeline
name|pipeline
init|=
name|createSingleNodePipeline
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|pipeline
argument_list|,
literal|"Pipeline cannot be null."
argument_list|)
expr_stmt|;
name|int
name|currentCount
init|=
literal|1
decl_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|1
init|;
name|x
operator|<
literal|1000
condition|;
name|x
operator|++
control|)
block|{
try|try
block|{
name|ContainerInfo
name|containerInfo
init|=
operator|new
name|ContainerInfo
operator|.
name|Builder
argument_list|()
operator|.
name|setState
argument_list|(
name|CLOSED
argument_list|)
operator|.
name|setPipelineID
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|setReplicationType
argument_list|(
name|pipeline
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|setReplicationFactor
argument_list|(
name|pipeline
operator|.
name|getFactor
argument_list|()
argument_list|)
comment|// This is bytes allocated for blocks inside container, not the
comment|// container size
operator|.
name|setAllocatedBytes
argument_list|(
literal|0
argument_list|)
operator|.
name|setUsedBytes
argument_list|(
literal|0
argument_list|)
operator|.
name|setNumberOfKeys
argument_list|(
literal|0
argument_list|)
operator|.
name|setStateEnterTime
argument_list|(
name|Time
operator|.
name|monotonicNow
argument_list|()
argument_list|)
operator|.
name|setOwner
argument_list|(
literal|"OZONE"
argument_list|)
operator|.
name|setContainerID
argument_list|(
name|x
argument_list|)
operator|.
name|setDeleteTransactionId
argument_list|(
literal|0
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|stateMap
operator|.
name|addContainer
argument_list|(
name|containerInfo
argument_list|)
expr_stmt|;
name|currentCount
operator|++
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SCMException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|y
init|=
name|currentCount
init|;
name|y
operator|<
literal|2000
condition|;
name|y
operator|++
control|)
block|{
try|try
block|{
name|ContainerInfo
name|containerInfo
init|=
operator|new
name|ContainerInfo
operator|.
name|Builder
argument_list|()
operator|.
name|setState
argument_list|(
name|OPEN
argument_list|)
operator|.
name|setPipelineID
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|setReplicationType
argument_list|(
name|pipeline
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|setReplicationFactor
argument_list|(
name|pipeline
operator|.
name|getFactor
argument_list|()
argument_list|)
comment|// This is bytes allocated for blocks inside container, not the
comment|// container size
operator|.
name|setAllocatedBytes
argument_list|(
literal|0
argument_list|)
operator|.
name|setUsedBytes
argument_list|(
literal|0
argument_list|)
operator|.
name|setNumberOfKeys
argument_list|(
literal|0
argument_list|)
operator|.
name|setStateEnterTime
argument_list|(
name|Time
operator|.
name|monotonicNow
argument_list|()
argument_list|)
operator|.
name|setOwner
argument_list|(
literal|"OZONE"
argument_list|)
operator|.
name|setContainerID
argument_list|(
name|y
argument_list|)
operator|.
name|setDeleteTransactionId
argument_list|(
literal|0
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|stateMap
operator|.
name|addContainer
argument_list|(
name|containerInfo
argument_list|)
expr_stmt|;
name|currentCount
operator|++
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SCMException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
try|try
block|{
name|ContainerInfo
name|containerInfo
init|=
operator|new
name|ContainerInfo
operator|.
name|Builder
argument_list|()
operator|.
name|setState
argument_list|(
name|OPEN
argument_list|)
operator|.
name|setPipelineID
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|setReplicationType
argument_list|(
name|pipeline
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|setReplicationFactor
argument_list|(
name|pipeline
operator|.
name|getFactor
argument_list|()
argument_list|)
comment|// This is bytes allocated for blocks inside container, not the
comment|// container size
operator|.
name|setAllocatedBytes
argument_list|(
literal|0
argument_list|)
operator|.
name|setUsedBytes
argument_list|(
literal|0
argument_list|)
operator|.
name|setNumberOfKeys
argument_list|(
literal|0
argument_list|)
operator|.
name|setStateEnterTime
argument_list|(
name|Time
operator|.
name|monotonicNow
argument_list|()
argument_list|)
operator|.
name|setOwner
argument_list|(
literal|"OZONE"
argument_list|)
operator|.
name|setContainerID
argument_list|(
name|currentCount
operator|++
argument_list|)
operator|.
name|setDeleteTransactionId
argument_list|(
literal|0
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|stateMap
operator|.
name|addContainer
argument_list|(
name|containerInfo
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SCMException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|containerID
operator|=
operator|new
name|AtomicInteger
argument_list|(
name|currentCount
operator|++
argument_list|)
expr_stmt|;
block|}
DECL|method|createSingleNodePipeline (String containerName)
specifier|public
specifier|static
name|Pipeline
name|createSingleNodePipeline
parameter_list|(
name|String
name|containerName
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|createPipeline
argument_list|(
name|containerName
argument_list|,
literal|1
argument_list|)
return|;
block|}
comment|/**    * Create a pipeline with single node replica.    *    * @return Pipeline with single node in it.    * @throws IOException    */
DECL|method|createPipeline (String containerName, int numNodes)
specifier|public
specifier|static
name|Pipeline
name|createPipeline
parameter_list|(
name|String
name|containerName
parameter_list|,
name|int
name|numNodes
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|numNodes
operator|>=
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|ids
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numNodes
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numNodes
condition|;
name|i
operator|++
control|)
block|{
name|ids
operator|.
name|add
argument_list|(
name|GenesisUtil
operator|.
name|createDatanodeDetails
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|createPipeline
argument_list|(
name|containerName
argument_list|,
name|ids
argument_list|)
return|;
block|}
DECL|method|createPipeline (String containerName, Iterable<DatanodeDetails> ids)
specifier|public
specifier|static
name|Pipeline
name|createPipeline
parameter_list|(
name|String
name|containerName
parameter_list|,
name|Iterable
argument_list|<
name|DatanodeDetails
argument_list|>
name|ids
parameter_list|)
throws|throws
name|IOException
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|ids
argument_list|,
literal|"ids == null"
argument_list|)
expr_stmt|;
specifier|final
name|Iterator
argument_list|<
name|DatanodeDetails
argument_list|>
name|i
init|=
name|ids
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|i
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|DatanodeDetails
name|leader
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
specifier|final
name|Pipeline
name|pipeline
init|=
operator|new
name|Pipeline
argument_list|(
name|leader
operator|.
name|getUuidString
argument_list|()
argument_list|,
name|OPEN
argument_list|,
name|ReplicationType
operator|.
name|STAND_ALONE
argument_list|,
name|ReplicationFactor
operator|.
name|ONE
argument_list|,
name|PipelineID
operator|.
name|randomId
argument_list|()
argument_list|)
decl_stmt|;
name|pipeline
operator|.
name|addMember
argument_list|(
name|leader
argument_list|)
expr_stmt|;
for|for
control|(
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|pipeline
operator|.
name|addMember
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|pipeline
return|;
block|}
annotation|@
name|Benchmark
DECL|method|createContainerBenchMark (BenchMarkContainerStateMap state, Blackhole bh)
specifier|public
name|void
name|createContainerBenchMark
parameter_list|(
name|BenchMarkContainerStateMap
name|state
parameter_list|,
name|Blackhole
name|bh
parameter_list|)
throws|throws
name|IOException
block|{
name|Pipeline
name|pipeline
init|=
name|createSingleNodePipeline
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|cid
init|=
name|state
operator|.
name|containerID
operator|.
name|incrementAndGet
argument_list|()
decl_stmt|;
name|ContainerInfo
name|containerInfo
init|=
operator|new
name|ContainerInfo
operator|.
name|Builder
argument_list|()
operator|.
name|setState
argument_list|(
name|CLOSED
argument_list|)
operator|.
name|setPipelineID
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|setReplicationType
argument_list|(
name|pipeline
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|setReplicationFactor
argument_list|(
name|pipeline
operator|.
name|getFactor
argument_list|()
argument_list|)
comment|// This is bytes allocated for blocks inside container, not the
comment|// container size
operator|.
name|setAllocatedBytes
argument_list|(
literal|0
argument_list|)
operator|.
name|setUsedBytes
argument_list|(
literal|0
argument_list|)
operator|.
name|setNumberOfKeys
argument_list|(
literal|0
argument_list|)
operator|.
name|setStateEnterTime
argument_list|(
name|Time
operator|.
name|monotonicNow
argument_list|()
argument_list|)
operator|.
name|setOwner
argument_list|(
literal|"OZONE"
argument_list|)
operator|.
name|setContainerID
argument_list|(
name|cid
argument_list|)
operator|.
name|setDeleteTransactionId
argument_list|(
literal|0
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|state
operator|.
name|stateMap
operator|.
name|addContainer
argument_list|(
name|containerInfo
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Benchmark
DECL|method|getMatchingContainerBenchMark (BenchMarkContainerStateMap state, Blackhole bh)
specifier|public
name|void
name|getMatchingContainerBenchMark
parameter_list|(
name|BenchMarkContainerStateMap
name|state
parameter_list|,
name|Blackhole
name|bh
parameter_list|)
block|{
name|bh
operator|.
name|consume
argument_list|(
name|state
operator|.
name|stateMap
operator|.
name|getMatchingContainerIDs
argument_list|(
name|OPEN
argument_list|,
literal|"BILBO"
argument_list|,
name|ReplicationFactor
operator|.
name|ONE
argument_list|,
name|ReplicationType
operator|.
name|STAND_ALONE
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

