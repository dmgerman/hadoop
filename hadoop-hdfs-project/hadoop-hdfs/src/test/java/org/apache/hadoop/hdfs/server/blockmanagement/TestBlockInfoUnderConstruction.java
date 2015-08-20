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
name|common
operator|.
name|GenerationStamp
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
name|common
operator|.
name|HdfsServerConstants
operator|.
name|BlockUCState
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
comment|/**  * This class provides tests for BlockInfoUnderConstruction class  */
end_comment

begin_class
DECL|class|TestBlockInfoUnderConstruction
specifier|public
class|class
name|TestBlockInfoUnderConstruction
block|{
annotation|@
name|Test
DECL|method|testInitializeBlockRecovery ()
specifier|public
name|void
name|testInitializeBlockRecovery
parameter_list|()
throws|throws
name|Exception
block|{
name|DatanodeStorageInfo
name|s1
init|=
name|DFSTestUtil
operator|.
name|createDatanodeStorageInfo
argument_list|(
literal|"10.10.1.1"
argument_list|,
literal|"s1"
argument_list|)
decl_stmt|;
name|DatanodeDescriptor
name|dd1
init|=
name|s1
operator|.
name|getDatanodeDescriptor
argument_list|()
decl_stmt|;
name|DatanodeStorageInfo
name|s2
init|=
name|DFSTestUtil
operator|.
name|createDatanodeStorageInfo
argument_list|(
literal|"10.10.1.2"
argument_list|,
literal|"s2"
argument_list|)
decl_stmt|;
name|DatanodeDescriptor
name|dd2
init|=
name|s2
operator|.
name|getDatanodeDescriptor
argument_list|()
decl_stmt|;
name|DatanodeStorageInfo
name|s3
init|=
name|DFSTestUtil
operator|.
name|createDatanodeStorageInfo
argument_list|(
literal|"10.10.1.3"
argument_list|,
literal|"s3"
argument_list|)
decl_stmt|;
name|DatanodeDescriptor
name|dd3
init|=
name|s3
operator|.
name|getDatanodeDescriptor
argument_list|()
decl_stmt|;
name|dd1
operator|.
name|isAlive
operator|=
name|dd2
operator|.
name|isAlive
operator|=
name|dd3
operator|.
name|isAlive
operator|=
literal|true
expr_stmt|;
name|BlockInfoContiguousUnderConstruction
name|blockInfo
init|=
operator|new
name|BlockInfoContiguousUnderConstruction
argument_list|(
operator|new
name|Block
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|GenerationStamp
operator|.
name|LAST_RESERVED_STAMP
argument_list|)
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|,
name|BlockUCState
operator|.
name|UNDER_CONSTRUCTION
argument_list|,
operator|new
name|DatanodeStorageInfo
index|[]
block|{
name|s1
block|,
name|s2
block|,
name|s3
block|}
argument_list|)
decl_stmt|;
comment|// Recovery attempt #1.
name|DFSTestUtil
operator|.
name|resetLastUpdatesWithOffset
argument_list|(
name|dd1
argument_list|,
operator|-
literal|3
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|resetLastUpdatesWithOffset
argument_list|(
name|dd2
argument_list|,
operator|-
literal|1
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|resetLastUpdatesWithOffset
argument_list|(
name|dd3
argument_list|,
operator|-
literal|2
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|blockInfo
operator|.
name|initializeBlockRecovery
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|BlockInfoUnderConstruction
index|[]
name|blockInfoRecovery
init|=
name|dd2
operator|.
name|getLeaseRecoveryCommand
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|blockInfoRecovery
index|[
literal|0
index|]
argument_list|,
name|blockInfo
argument_list|)
expr_stmt|;
comment|// Recovery attempt #2.
name|DFSTestUtil
operator|.
name|resetLastUpdatesWithOffset
argument_list|(
name|dd1
argument_list|,
operator|-
literal|2
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|resetLastUpdatesWithOffset
argument_list|(
name|dd2
argument_list|,
operator|-
literal|1
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|resetLastUpdatesWithOffset
argument_list|(
name|dd3
argument_list|,
operator|-
literal|3
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|blockInfo
operator|.
name|initializeBlockRecovery
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|blockInfoRecovery
operator|=
name|dd1
operator|.
name|getLeaseRecoveryCommand
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blockInfoRecovery
index|[
literal|0
index|]
argument_list|,
name|blockInfo
argument_list|)
expr_stmt|;
comment|// Recovery attempt #3.
name|DFSTestUtil
operator|.
name|resetLastUpdatesWithOffset
argument_list|(
name|dd1
argument_list|,
operator|-
literal|2
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|resetLastUpdatesWithOffset
argument_list|(
name|dd2
argument_list|,
operator|-
literal|1
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|resetLastUpdatesWithOffset
argument_list|(
name|dd3
argument_list|,
operator|-
literal|3
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|blockInfo
operator|.
name|initializeBlockRecovery
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|blockInfoRecovery
operator|=
name|dd3
operator|.
name|getLeaseRecoveryCommand
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blockInfoRecovery
index|[
literal|0
index|]
argument_list|,
name|blockInfo
argument_list|)
expr_stmt|;
comment|// Recovery attempt #4.
comment|// Reset everything. And again pick DN with most recent heart beat.
name|DFSTestUtil
operator|.
name|resetLastUpdatesWithOffset
argument_list|(
name|dd1
argument_list|,
operator|-
literal|2
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|resetLastUpdatesWithOffset
argument_list|(
name|dd2
argument_list|,
operator|-
literal|1
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|resetLastUpdatesWithOffset
argument_list|(
name|dd3
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|blockInfo
operator|.
name|initializeBlockRecovery
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|blockInfoRecovery
operator|=
name|dd3
operator|.
name|getLeaseRecoveryCommand
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blockInfoRecovery
index|[
literal|0
index|]
argument_list|,
name|blockInfo
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

