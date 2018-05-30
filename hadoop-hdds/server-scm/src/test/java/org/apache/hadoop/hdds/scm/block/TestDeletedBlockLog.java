begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.block
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
name|block
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
name|io
operator|.
name|FileUtils
import|;
end_import

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
name|RandomUtils
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
name|Mapping
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
name|PipelineChannel
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
name|hdfs
operator|.
name|DFSUtil
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
name|HddsProtos
operator|.
name|LifeCycleState
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
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|DeletedBlocksTransaction
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
name|apache
operator|.
name|hadoop
operator|.
name|utils
operator|.
name|MetadataKeyFilters
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
name|utils
operator|.
name|MetadataStore
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
name|ArrayList
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|Map
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
name|stream
operator|.
name|Collectors
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
name|OZONE_SCM_BLOCK_DELETION_MAX_RETRY
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
name|OZONE_METADATA_DIRS
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
name|mock
import|;
end_import

begin_comment
comment|/**  * Tests for DeletedBlockLog.  */
end_comment

begin_class
DECL|class|TestDeletedBlockLog
specifier|public
class|class
name|TestDeletedBlockLog
block|{
DECL|field|deletedBlockLog
specifier|private
specifier|static
name|DeletedBlockLogImpl
name|deletedBlockLog
decl_stmt|;
DECL|field|conf
specifier|private
name|OzoneConfiguration
name|conf
decl_stmt|;
DECL|field|testDir
specifier|private
name|File
name|testDir
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|testDir
operator|=
name|GenericTestUtils
operator|.
name|getTestDir
argument_list|(
name|TestDeletedBlockLog
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|=
operator|new
name|OzoneConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|OZONE_SCM_BLOCK_DELETION_MAX_RETRY
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OZONE_METADATA_DIRS
argument_list|,
name|testDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|deletedBlockLog
operator|=
operator|new
name|DeletedBlockLogImpl
argument_list|(
name|conf
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
name|deletedBlockLog
operator|.
name|close
argument_list|()
expr_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|testDir
argument_list|)
expr_stmt|;
block|}
DECL|method|generateData (int dataSize)
specifier|private
name|Map
argument_list|<
name|Long
argument_list|,
name|List
argument_list|<
name|Long
argument_list|>
argument_list|>
name|generateData
parameter_list|(
name|int
name|dataSize
parameter_list|)
block|{
name|Map
argument_list|<
name|Long
argument_list|,
name|List
argument_list|<
name|Long
argument_list|>
argument_list|>
name|blockMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|int
name|continerIDBase
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|int
name|localIDBase
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|1000
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
name|dataSize
condition|;
name|i
operator|++
control|)
block|{
name|long
name|containerID
init|=
name|continerIDBase
operator|+
name|i
decl_stmt|;
name|List
argument_list|<
name|Long
argument_list|>
name|blocks
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|blockSize
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|30
argument_list|)
operator|+
literal|1
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|blockSize
condition|;
name|j
operator|++
control|)
block|{
name|long
name|localID
init|=
name|localIDBase
operator|+
name|j
decl_stmt|;
name|blocks
operator|.
name|add
argument_list|(
name|localID
argument_list|)
expr_stmt|;
block|}
name|blockMap
operator|.
name|put
argument_list|(
name|containerID
argument_list|,
name|blocks
argument_list|)
expr_stmt|;
block|}
return|return
name|blockMap
return|;
block|}
annotation|@
name|Test
DECL|method|testGetTransactions ()
specifier|public
name|void
name|testGetTransactions
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|DeletedBlocksTransaction
argument_list|>
name|blocks
init|=
name|deletedBlockLog
operator|.
name|getTransactions
argument_list|(
literal|30
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|blocks
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Creates 40 TX in the log.
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Long
argument_list|,
name|List
argument_list|<
name|Long
argument_list|>
argument_list|>
name|entry
range|:
name|generateData
argument_list|(
literal|40
argument_list|)
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|deletedBlockLog
operator|.
name|addTransaction
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Get first 30 TXs.
name|blocks
operator|=
name|deletedBlockLog
operator|.
name|getTransactions
argument_list|(
literal|30
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|30
argument_list|,
name|blocks
operator|.
name|size
argument_list|()
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
literal|30
condition|;
name|i
operator|++
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|i
operator|+
literal|1
argument_list|,
name|blocks
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getTxID
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Get another 30 TXs.
comment|// The log only 10 left, so this time it will only return 10 TXs.
name|blocks
operator|=
name|deletedBlockLog
operator|.
name|getTransactions
argument_list|(
literal|30
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|blocks
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|30
init|;
name|i
operator|<
literal|40
condition|;
name|i
operator|++
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|i
operator|+
literal|1
argument_list|,
name|blocks
operator|.
name|get
argument_list|(
name|i
operator|-
literal|30
argument_list|)
operator|.
name|getTxID
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Get another 50 TXs.
comment|// By now the position should have moved to the beginning,
comment|// this call will return all 40 TXs.
name|blocks
operator|=
name|deletedBlockLog
operator|.
name|getTransactions
argument_list|(
literal|50
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|40
argument_list|,
name|blocks
operator|.
name|size
argument_list|()
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
literal|40
condition|;
name|i
operator|++
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|i
operator|+
literal|1
argument_list|,
name|blocks
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getTxID
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|Long
argument_list|>
name|txIDs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|DeletedBlocksTransaction
name|block
range|:
name|blocks
control|)
block|{
name|txIDs
operator|.
name|add
argument_list|(
name|block
operator|.
name|getTxID
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|deletedBlockLog
operator|.
name|commitTransactions
argument_list|(
name|txIDs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIncrementCount ()
specifier|public
name|void
name|testIncrementCount
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|maxRetry
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|OZONE_SCM_BLOCK_DELETION_MAX_RETRY
argument_list|,
literal|20
argument_list|)
decl_stmt|;
comment|// Create 30 TXs in the log.
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Long
argument_list|,
name|List
argument_list|<
name|Long
argument_list|>
argument_list|>
name|entry
range|:
name|generateData
argument_list|(
literal|30
argument_list|)
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|deletedBlockLog
operator|.
name|addTransaction
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// This will return all TXs, total num 30.
name|List
argument_list|<
name|DeletedBlocksTransaction
argument_list|>
name|blocks
init|=
name|deletedBlockLog
operator|.
name|getTransactions
argument_list|(
literal|40
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Long
argument_list|>
name|txIDs
init|=
name|blocks
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|DeletedBlocksTransaction
operator|::
name|getTxID
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
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
name|maxRetry
condition|;
name|i
operator|++
control|)
block|{
name|deletedBlockLog
operator|.
name|incrementCount
argument_list|(
name|txIDs
argument_list|)
expr_stmt|;
block|}
comment|// Increment another time so it exceed the maxRetry.
comment|// On this call, count will be set to -1 which means TX eventually fails.
name|deletedBlockLog
operator|.
name|incrementCount
argument_list|(
name|txIDs
argument_list|)
expr_stmt|;
name|blocks
operator|=
name|deletedBlockLog
operator|.
name|getTransactions
argument_list|(
literal|40
argument_list|)
expr_stmt|;
for|for
control|(
name|DeletedBlocksTransaction
name|block
range|:
name|blocks
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|block
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// If all TXs are failed, getTransactions call will always return nothing.
name|blocks
operator|=
name|deletedBlockLog
operator|.
name|getTransactions
argument_list|(
literal|40
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|blocks
operator|.
name|size
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCommitTransactions ()
specifier|public
name|void
name|testCommitTransactions
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Long
argument_list|,
name|List
argument_list|<
name|Long
argument_list|>
argument_list|>
name|entry
range|:
name|generateData
argument_list|(
literal|50
argument_list|)
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|deletedBlockLog
operator|.
name|addTransaction
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|DeletedBlocksTransaction
argument_list|>
name|blocks
init|=
name|deletedBlockLog
operator|.
name|getTransactions
argument_list|(
literal|20
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Long
argument_list|>
name|txIDs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|DeletedBlocksTransaction
name|block
range|:
name|blocks
control|)
block|{
name|txIDs
operator|.
name|add
argument_list|(
name|block
operator|.
name|getTxID
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Add an invalid txID.
name|txIDs
operator|.
name|add
argument_list|(
literal|70L
argument_list|)
expr_stmt|;
name|deletedBlockLog
operator|.
name|commitTransactions
argument_list|(
name|txIDs
argument_list|)
expr_stmt|;
name|blocks
operator|=
name|deletedBlockLog
operator|.
name|getTransactions
argument_list|(
literal|50
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|30
argument_list|,
name|blocks
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRandomOperateTransactions ()
specifier|public
name|void
name|testRandomOperateTransactions
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|int
name|added
init|=
literal|0
decl_stmt|,
name|committed
init|=
literal|0
decl_stmt|;
name|List
argument_list|<
name|DeletedBlocksTransaction
argument_list|>
name|blocks
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Long
argument_list|>
name|txIDs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|byte
index|[]
name|latestTxid
init|=
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"#LATEST_TXID#"
argument_list|)
decl_stmt|;
name|MetadataKeyFilters
operator|.
name|MetadataKeyFilter
name|avoidLatestTxid
init|=
parameter_list|(
name|preKey
parameter_list|,
name|currentKey
parameter_list|,
name|nextKey
parameter_list|)
lambda|->
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|latestTxid
argument_list|,
name|currentKey
argument_list|)
decl_stmt|;
name|MetadataStore
name|store
init|=
name|deletedBlockLog
operator|.
name|getDeletedStore
argument_list|()
decl_stmt|;
comment|// Randomly add/get/commit/increase transactions.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|int
name|state
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
decl_stmt|;
if|if
condition|(
name|state
operator|==
literal|0
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Long
argument_list|,
name|List
argument_list|<
name|Long
argument_list|>
argument_list|>
name|entry
range|:
name|generateData
argument_list|(
literal|10
argument_list|)
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|deletedBlockLog
operator|.
name|addTransaction
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|added
operator|+=
literal|10
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|state
operator|==
literal|1
condition|)
block|{
name|blocks
operator|=
name|deletedBlockLog
operator|.
name|getTransactions
argument_list|(
literal|20
argument_list|)
expr_stmt|;
name|txIDs
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|DeletedBlocksTransaction
name|block
range|:
name|blocks
control|)
block|{
name|txIDs
operator|.
name|add
argument_list|(
name|block
operator|.
name|getTxID
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|deletedBlockLog
operator|.
name|incrementCount
argument_list|(
name|txIDs
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|state
operator|==
literal|2
condition|)
block|{
name|txIDs
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|DeletedBlocksTransaction
name|block
range|:
name|blocks
control|)
block|{
name|txIDs
operator|.
name|add
argument_list|(
name|block
operator|.
name|getTxID
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|blocks
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|committed
operator|+=
name|txIDs
operator|.
name|size
argument_list|()
expr_stmt|;
name|deletedBlockLog
operator|.
name|commitTransactions
argument_list|(
name|txIDs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// verify the number of added and committed.
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|>
name|result
init|=
name|store
operator|.
name|getRangeKVs
argument_list|(
literal|null
argument_list|,
name|added
argument_list|,
name|avoidLatestTxid
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|added
argument_list|,
name|result
operator|.
name|size
argument_list|()
operator|+
name|committed
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testPersistence ()
specifier|public
name|void
name|testPersistence
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Long
argument_list|,
name|List
argument_list|<
name|Long
argument_list|>
argument_list|>
name|entry
range|:
name|generateData
argument_list|(
literal|50
argument_list|)
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|deletedBlockLog
operator|.
name|addTransaction
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// close db and reopen it again to make sure
comment|// transactions are stored persistently.
name|deletedBlockLog
operator|.
name|close
argument_list|()
expr_stmt|;
name|deletedBlockLog
operator|=
operator|new
name|DeletedBlockLogImpl
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|DeletedBlocksTransaction
argument_list|>
name|blocks
init|=
name|deletedBlockLog
operator|.
name|getTransactions
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Long
argument_list|>
name|txIDs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|DeletedBlocksTransaction
name|block
range|:
name|blocks
control|)
block|{
name|txIDs
operator|.
name|add
argument_list|(
name|block
operator|.
name|getTxID
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|deletedBlockLog
operator|.
name|commitTransactions
argument_list|(
name|txIDs
argument_list|)
expr_stmt|;
name|blocks
operator|=
name|deletedBlockLog
operator|.
name|getTransactions
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|blocks
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDeletedBlockTransactions ()
specifier|public
name|void
name|testDeletedBlockTransactions
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|txNum
init|=
literal|10
decl_stmt|;
name|int
name|maximumAllowedTXNum
init|=
literal|5
decl_stmt|;
name|List
argument_list|<
name|DeletedBlocksTransaction
argument_list|>
name|blocks
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|Long
argument_list|>
name|containerIDs
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|long
name|containerID
init|=
literal|0L
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
name|dnId1
init|=
name|DatanodeDetails
operator|.
name|newBuilder
argument_list|()
operator|.
name|setUuid
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|setIpAddress
argument_list|(
literal|"127.0.0.1"
argument_list|)
operator|.
name|setHostName
argument_list|(
literal|"localhost"
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
operator|.
name|build
argument_list|()
decl_stmt|;
name|DatanodeDetails
name|dnId2
init|=
name|DatanodeDetails
operator|.
name|newBuilder
argument_list|()
operator|.
name|setUuid
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|setIpAddress
argument_list|(
literal|"127.0.0.1"
argument_list|)
operator|.
name|setHostName
argument_list|(
literal|"localhost"
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
operator|.
name|build
argument_list|()
decl_stmt|;
name|Mapping
name|mappingService
init|=
name|mock
argument_list|(
name|ContainerMapping
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Creates {TXNum} TX in the log.
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Long
argument_list|,
name|List
argument_list|<
name|Long
argument_list|>
argument_list|>
name|entry
range|:
name|generateData
argument_list|(
name|txNum
argument_list|)
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|count
operator|++
expr_stmt|;
name|containerID
operator|=
name|entry
operator|.
name|getKey
argument_list|()
expr_stmt|;
name|containerIDs
operator|.
name|add
argument_list|(
name|containerID
argument_list|)
expr_stmt|;
name|deletedBlockLog
operator|.
name|addTransaction
argument_list|(
name|containerID
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// make TX[1-6] for datanode1; TX[7-10] for datanode2
if|if
condition|(
name|count
operator|<=
operator|(
name|maximumAllowedTXNum
operator|+
literal|1
operator|)
condition|)
block|{
name|mockContainerInfo
argument_list|(
name|mappingService
argument_list|,
name|containerID
argument_list|,
name|dnId1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mockContainerInfo
argument_list|(
name|mappingService
argument_list|,
name|containerID
argument_list|,
name|dnId2
argument_list|)
expr_stmt|;
block|}
block|}
name|DatanodeDeletedBlockTransactions
name|transactions
init|=
operator|new
name|DatanodeDeletedBlockTransactions
argument_list|(
name|mappingService
argument_list|,
name|maximumAllowedTXNum
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|deletedBlockLog
operator|.
name|getTransactions
argument_list|(
name|transactions
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Long
argument_list|>
name|txIDs
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|UUID
name|id
range|:
name|transactions
operator|.
name|getDatanodeIDs
argument_list|()
control|)
block|{
name|List
argument_list|<
name|DeletedBlocksTransaction
argument_list|>
name|txs
init|=
name|transactions
operator|.
name|getDatanodeTransactions
argument_list|(
name|id
argument_list|)
decl_stmt|;
for|for
control|(
name|DeletedBlocksTransaction
name|tx
range|:
name|txs
control|)
block|{
name|txIDs
operator|.
name|add
argument_list|(
name|tx
operator|.
name|getTxID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// delete TX ID
name|deletedBlockLog
operator|.
name|commitTransactions
argument_list|(
name|txIDs
argument_list|)
expr_stmt|;
name|blocks
operator|=
name|deletedBlockLog
operator|.
name|getTransactions
argument_list|(
name|txNum
argument_list|)
expr_stmt|;
comment|// There should be one block remained since dnID1 reaches
comment|// the maximum value (5).
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|blocks
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|transactions
operator|.
name|isFull
argument_list|()
argument_list|)
expr_stmt|;
comment|// The number of TX in dnID1 won't more than maximum value.
name|Assert
operator|.
name|assertEquals
argument_list|(
name|maximumAllowedTXNum
argument_list|,
name|transactions
operator|.
name|getDatanodeTransactions
argument_list|(
name|dnId1
operator|.
name|getUuid
argument_list|()
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|size
init|=
name|transactions
operator|.
name|getDatanodeTransactions
argument_list|(
name|dnId2
operator|.
name|getUuid
argument_list|()
argument_list|)
operator|.
name|size
argument_list|()
decl_stmt|;
comment|// add duplicated container in dnID2, this should be failed.
name|DeletedBlocksTransaction
operator|.
name|Builder
name|builder
init|=
name|DeletedBlocksTransaction
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setTxID
argument_list|(
literal|11
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setContainerID
argument_list|(
name|containerID
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setCount
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|transactions
operator|.
name|addTransaction
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
comment|// The number of TX in dnID2 should not be changed.
name|Assert
operator|.
name|assertEquals
argument_list|(
name|size
argument_list|,
name|transactions
operator|.
name|getDatanodeTransactions
argument_list|(
name|dnId2
operator|.
name|getUuid
argument_list|()
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Add new TX in dnID2, then dnID2 will reach maximum value.
name|containerID
operator|=
name|RandomUtils
operator|.
name|nextLong
argument_list|()
expr_stmt|;
name|builder
operator|=
name|DeletedBlocksTransaction
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setTxID
argument_list|(
literal|12
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setContainerID
argument_list|(
name|containerID
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setCount
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|mockContainerInfo
argument_list|(
name|mappingService
argument_list|,
name|containerID
argument_list|,
name|dnId2
argument_list|)
expr_stmt|;
name|transactions
operator|.
name|addTransaction
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
comment|// Since all node are full, then transactions is full.
name|Assert
operator|.
name|assertTrue
argument_list|(
name|transactions
operator|.
name|isFull
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|mockContainerInfo (Mapping mappingService, long containerID, DatanodeDetails dd)
specifier|private
name|void
name|mockContainerInfo
parameter_list|(
name|Mapping
name|mappingService
parameter_list|,
name|long
name|containerID
parameter_list|,
name|DatanodeDetails
name|dd
parameter_list|)
throws|throws
name|IOException
block|{
name|PipelineChannel
name|pipelineChannel
init|=
operator|new
name|PipelineChannel
argument_list|(
literal|"fake"
argument_list|,
name|LifeCycleState
operator|.
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
literal|"fake"
argument_list|)
decl_stmt|;
name|pipelineChannel
operator|.
name|addMember
argument_list|(
name|dd
argument_list|)
expr_stmt|;
name|Pipeline
name|pipeline
init|=
operator|new
name|Pipeline
argument_list|(
name|pipelineChannel
argument_list|)
decl_stmt|;
name|ContainerInfo
operator|.
name|Builder
name|builder
init|=
operator|new
name|ContainerInfo
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setPipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|ContainerInfo
name|conatinerInfo
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
name|conatinerInfo
argument_list|)
operator|.
name|when
argument_list|(
name|mappingService
argument_list|)
operator|.
name|getContainer
argument_list|(
name|containerID
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

