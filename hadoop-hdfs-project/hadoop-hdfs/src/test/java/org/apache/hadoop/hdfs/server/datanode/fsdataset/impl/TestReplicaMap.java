begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.fsdataset.impl
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
name|datanode
operator|.
name|fsdataset
operator|.
name|impl
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
name|fail
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
name|datanode
operator|.
name|FinalizedReplica
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
name|AutoCloseableLock
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

begin_comment
comment|/**  * Unit test for ReplicasMap class  */
end_comment

begin_class
DECL|class|TestReplicaMap
specifier|public
class|class
name|TestReplicaMap
block|{
DECL|field|map
specifier|private
specifier|final
name|ReplicaMap
name|map
init|=
operator|new
name|ReplicaMap
argument_list|(
operator|new
name|AutoCloseableLock
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|bpid
specifier|private
specifier|final
name|String
name|bpid
init|=
literal|"BP-TEST"
decl_stmt|;
DECL|field|block
specifier|private
specifier|final
name|Block
name|block
init|=
operator|new
name|Block
argument_list|(
literal|1234
argument_list|,
literal|1234
argument_list|,
literal|1234
argument_list|)
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|map
operator|.
name|add
argument_list|(
name|bpid
argument_list|,
operator|new
name|FinalizedReplica
argument_list|(
name|block
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test for ReplicasMap.get(Block) and ReplicasMap.get(long) tests    */
annotation|@
name|Test
DECL|method|testGet ()
specifier|public
name|void
name|testGet
parameter_list|()
block|{
comment|// Test 1: null argument throws invalid argument exception
try|try
block|{
name|map
operator|.
name|get
argument_list|(
name|bpid
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected exception not thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{ }
comment|// Test 2: successful lookup based on block
name|assertNotNull
argument_list|(
name|map
operator|.
name|get
argument_list|(
name|bpid
argument_list|,
name|block
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test 3: Lookup failure - generation stamp mismatch
name|Block
name|b
init|=
operator|new
name|Block
argument_list|(
name|block
argument_list|)
decl_stmt|;
name|b
operator|.
name|setGenerationStamp
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|map
operator|.
name|get
argument_list|(
name|bpid
argument_list|,
name|b
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test 4: Lookup failure - blockID mismatch
name|b
operator|.
name|setGenerationStamp
argument_list|(
name|block
operator|.
name|getGenerationStamp
argument_list|()
argument_list|)
expr_stmt|;
name|b
operator|.
name|setBlockId
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|map
operator|.
name|get
argument_list|(
name|bpid
argument_list|,
name|b
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test 5: successful lookup based on block ID
name|assertNotNull
argument_list|(
name|map
operator|.
name|get
argument_list|(
name|bpid
argument_list|,
name|block
operator|.
name|getBlockId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test 6: failed lookup for invalid block ID
name|assertNull
argument_list|(
name|map
operator|.
name|get
argument_list|(
name|bpid
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAdd ()
specifier|public
name|void
name|testAdd
parameter_list|()
block|{
comment|// Test 1: null argument throws invalid argument exception
try|try
block|{
name|map
operator|.
name|add
argument_list|(
name|bpid
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected exception not thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{ }
block|}
annotation|@
name|Test
DECL|method|testRemove ()
specifier|public
name|void
name|testRemove
parameter_list|()
block|{
comment|// Test 1: null argument throws invalid argument exception
try|try
block|{
name|map
operator|.
name|remove
argument_list|(
name|bpid
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected exception not thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{ }
comment|// Test 2: remove failure - generation stamp mismatch
name|Block
name|b
init|=
operator|new
name|Block
argument_list|(
name|block
argument_list|)
decl_stmt|;
name|b
operator|.
name|setGenerationStamp
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|map
operator|.
name|remove
argument_list|(
name|bpid
argument_list|,
name|b
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test 3: remove failure - blockID mismatch
name|b
operator|.
name|setGenerationStamp
argument_list|(
name|block
operator|.
name|getGenerationStamp
argument_list|()
argument_list|)
expr_stmt|;
name|b
operator|.
name|setBlockId
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|map
operator|.
name|remove
argument_list|(
name|bpid
argument_list|,
name|b
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test 4: remove success
name|assertNotNull
argument_list|(
name|map
operator|.
name|remove
argument_list|(
name|bpid
argument_list|,
name|block
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test 5: remove failure - invalid blockID
name|assertNull
argument_list|(
name|map
operator|.
name|remove
argument_list|(
name|bpid
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test 6: remove success
name|map
operator|.
name|add
argument_list|(
name|bpid
argument_list|,
operator|new
name|FinalizedReplica
argument_list|(
name|block
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|map
operator|.
name|remove
argument_list|(
name|bpid
argument_list|,
name|block
operator|.
name|getBlockId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

