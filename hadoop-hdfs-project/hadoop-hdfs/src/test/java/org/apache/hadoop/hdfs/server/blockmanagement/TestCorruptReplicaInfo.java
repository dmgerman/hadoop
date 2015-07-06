begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.blockmanagement
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|blockmanagement
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
name|assertEquals
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
name|assertNotNull
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
name|assertNull
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|DFSTestUtil
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
name|protocol
operator|.
name|Block
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
name|server
operator|.
name|blockmanagement
operator|.
name|CorruptReplicasMap
operator|.
name|Reason
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

begin_comment
comment|/**  * This test makes sure that   *   CorruptReplicasMap::numBlocksWithCorruptReplicas and  *   CorruptReplicasMap::getCorruptReplicaBlockIds  *   return the correct values  */
end_comment

begin_class
DECL|class|TestCorruptReplicaInfo
specifier|public
class|class
name|TestCorruptReplicaInfo
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestCorruptReplicaInfo
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|block_map
specifier|private
specifier|final
name|Map
argument_list|<
name|Long
argument_list|,
name|BlockInfo
argument_list|>
name|block_map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Allow easy block creation by block id
comment|// Return existing block if one with same block id already exists
DECL|method|getBlock (Long block_id)
specifier|private
name|BlockInfo
name|getBlock
parameter_list|(
name|Long
name|block_id
parameter_list|)
block|{
if|if
condition|(
operator|!
name|block_map
operator|.
name|containsKey
argument_list|(
name|block_id
argument_list|)
condition|)
block|{
name|block_map
operator|.
name|put
argument_list|(
name|block_id
argument_list|,
operator|new
name|BlockInfoContiguous
argument_list|(
operator|new
name|Block
argument_list|(
name|block_id
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|block_map
operator|.
name|get
argument_list|(
name|block_id
argument_list|)
return|;
block|}
DECL|method|getBlock (int block_id)
specifier|private
name|BlockInfo
name|getBlock
parameter_list|(
name|int
name|block_id
parameter_list|)
block|{
return|return
name|getBlock
argument_list|(
operator|(
name|long
operator|)
name|block_id
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testCorruptReplicaInfo ()
specifier|public
name|void
name|testCorruptReplicaInfo
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|CorruptReplicasMap
name|crm
init|=
operator|new
name|CorruptReplicasMap
argument_list|()
decl_stmt|;
comment|// Make sure initial values are returned correctly
name|assertEquals
argument_list|(
literal|"Number of corrupt blocks must initially be 0"
argument_list|,
literal|0
argument_list|,
name|crm
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Param n cannot be less than 0"
argument_list|,
name|crm
operator|.
name|getCorruptReplicaBlockIds
argument_list|(
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Param n cannot be greater than 100"
argument_list|,
name|crm
operator|.
name|getCorruptReplicaBlockIds
argument_list|(
literal|101
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|long
index|[]
name|l
init|=
name|crm
operator|.
name|getCorruptReplicaBlockIds
argument_list|(
literal|0
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"n = 0 must return non-null"
argument_list|,
name|l
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"n = 0 must return an empty list"
argument_list|,
literal|0
argument_list|,
name|l
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// create a list of block_ids. A list is used to allow easy validation of the
comment|// output of getCorruptReplicaBlockIds
name|int
name|NUM_BLOCK_IDS
init|=
literal|140
decl_stmt|;
name|List
argument_list|<
name|Long
argument_list|>
name|block_ids
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
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
name|NUM_BLOCK_IDS
condition|;
name|i
operator|++
control|)
block|{
name|block_ids
operator|.
name|add
argument_list|(
operator|(
name|long
operator|)
name|i
argument_list|)
expr_stmt|;
block|}
name|DatanodeDescriptor
name|dn1
init|=
name|DFSTestUtil
operator|.
name|getLocalDatanodeDescriptor
argument_list|()
decl_stmt|;
name|DatanodeDescriptor
name|dn2
init|=
name|DFSTestUtil
operator|.
name|getLocalDatanodeDescriptor
argument_list|()
decl_stmt|;
name|addToCorruptReplicasMap
argument_list|(
name|crm
argument_list|,
name|getBlock
argument_list|(
literal|0
argument_list|)
argument_list|,
name|dn1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Number of corrupt blocks not returning correctly"
argument_list|,
literal|1
argument_list|,
name|crm
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|addToCorruptReplicasMap
argument_list|(
name|crm
argument_list|,
name|getBlock
argument_list|(
literal|1
argument_list|)
argument_list|,
name|dn1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Number of corrupt blocks not returning correctly"
argument_list|,
literal|2
argument_list|,
name|crm
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|addToCorruptReplicasMap
argument_list|(
name|crm
argument_list|,
name|getBlock
argument_list|(
literal|1
argument_list|)
argument_list|,
name|dn2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Number of corrupt blocks not returning correctly"
argument_list|,
literal|2
argument_list|,
name|crm
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|crm
operator|.
name|removeFromCorruptReplicasMap
argument_list|(
name|getBlock
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Number of corrupt blocks not returning correctly"
argument_list|,
literal|1
argument_list|,
name|crm
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|crm
operator|.
name|removeFromCorruptReplicasMap
argument_list|(
name|getBlock
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Number of corrupt blocks not returning correctly"
argument_list|,
literal|0
argument_list|,
name|crm
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Long
name|block_id
range|:
name|block_ids
control|)
block|{
name|addToCorruptReplicasMap
argument_list|(
name|crm
argument_list|,
name|getBlock
argument_list|(
name|block_id
argument_list|)
argument_list|,
name|dn1
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Number of corrupt blocks not returning correctly"
argument_list|,
name|NUM_BLOCK_IDS
argument_list|,
name|crm
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"First five block ids not returned correctly "
argument_list|,
name|Arrays
operator|.
name|equals
argument_list|(
operator|new
name|long
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|}
argument_list|,
name|crm
operator|.
name|getCorruptReplicaBlockIds
argument_list|(
literal|5
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|crm
operator|.
name|getCorruptReplicaBlockIds
argument_list|(
literal|10
argument_list|,
literal|7L
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|block_ids
operator|.
name|subList
argument_list|(
literal|7
argument_list|,
literal|18
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"10 blocks after 7 not returned correctly "
argument_list|,
name|Arrays
operator|.
name|equals
argument_list|(
operator|new
name|long
index|[]
block|{
literal|8
block|,
literal|9
block|,
literal|10
block|,
literal|11
block|,
literal|12
block|,
literal|13
block|,
literal|14
block|,
literal|15
block|,
literal|16
block|,
literal|17
block|}
argument_list|,
name|crm
operator|.
name|getCorruptReplicaBlockIds
argument_list|(
literal|10
argument_list|,
literal|7L
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|addToCorruptReplicasMap (CorruptReplicasMap crm, BlockInfo blk, DatanodeDescriptor dn)
specifier|private
specifier|static
name|void
name|addToCorruptReplicasMap
parameter_list|(
name|CorruptReplicasMap
name|crm
parameter_list|,
name|BlockInfo
name|blk
parameter_list|,
name|DatanodeDescriptor
name|dn
parameter_list|)
block|{
name|crm
operator|.
name|addToCorruptReplicasMap
argument_list|(
name|blk
argument_list|,
name|dn
argument_list|,
literal|"TEST"
argument_list|,
name|Reason
operator|.
name|NONE
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

