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
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|hdfs
operator|.
name|HdfsConfiguration
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
name|MiniDFSCluster
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
name|BlockManager
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
name|DatanodeDescriptor
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
name|namenode
operator|.
name|FSNamesystem
import|;
end_import

begin_comment
comment|/**  * Test if FSNamesystem handles heartbeat right  */
end_comment

begin_class
DECL|class|TestComputeInvalidateWork
specifier|public
class|class
name|TestComputeInvalidateWork
extends|extends
name|TestCase
block|{
comment|/**    * Test if {@link FSNamesystem#computeInvalidateWork(int)}    * can schedule invalidate work correctly     */
DECL|method|testCompInvalidate ()
specifier|public
name|void
name|testCompInvalidate
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
specifier|final
name|int
name|NUM_OF_DATANODES
init|=
literal|3
decl_stmt|;
specifier|final
name|MiniDFSCluster
name|cluster
init|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|NUM_OF_DATANODES
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
specifier|final
name|FSNamesystem
name|namesystem
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
specifier|final
name|BlockManager
name|bm
init|=
name|namesystem
operator|.
name|getBlockManager
argument_list|()
decl_stmt|;
specifier|final
name|int
name|blockInvalidateLimit
init|=
name|bm
operator|.
name|getDatanodeManager
argument_list|()
operator|.
name|blockInvalidateLimit
decl_stmt|;
specifier|final
name|DatanodeDescriptor
index|[]
name|nodes
init|=
name|bm
operator|.
name|getDatanodeManager
argument_list|(           )
operator|.
name|getHeartbeatManager
argument_list|()
operator|.
name|getDatanodes
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|nodes
operator|.
name|length
argument_list|,
name|NUM_OF_DATANODES
argument_list|)
expr_stmt|;
name|namesystem
operator|.
name|writeLock
argument_list|()
expr_stmt|;
try|try
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
name|nodes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|3
operator|*
name|blockInvalidateLimit
operator|+
literal|1
condition|;
name|j
operator|++
control|)
block|{
name|Block
name|block
init|=
operator|new
name|Block
argument_list|(
name|i
operator|*
operator|(
name|blockInvalidateLimit
operator|+
literal|1
operator|)
operator|+
name|j
argument_list|,
literal|0
argument_list|,
name|GenerationStamp
operator|.
name|FIRST_VALID_STAMP
argument_list|)
decl_stmt|;
name|bm
operator|.
name|addToInvalidates
argument_list|(
name|block
argument_list|,
name|nodes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|blockInvalidateLimit
operator|*
name|NUM_OF_DATANODES
argument_list|,
name|bm
operator|.
name|computeInvalidateWork
argument_list|(
name|NUM_OF_DATANODES
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blockInvalidateLimit
operator|*
name|NUM_OF_DATANODES
argument_list|,
name|bm
operator|.
name|computeInvalidateWork
argument_list|(
name|NUM_OF_DATANODES
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blockInvalidateLimit
operator|*
operator|(
name|NUM_OF_DATANODES
operator|-
literal|1
operator|)
argument_list|,
name|bm
operator|.
name|computeInvalidateWork
argument_list|(
name|NUM_OF_DATANODES
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|workCount
init|=
name|bm
operator|.
name|computeInvalidateWork
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|workCount
operator|==
literal|1
condition|)
block|{
name|assertEquals
argument_list|(
name|blockInvalidateLimit
operator|+
literal|1
argument_list|,
name|bm
operator|.
name|computeInvalidateWork
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|workCount
argument_list|,
name|blockInvalidateLimit
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|bm
operator|.
name|computeInvalidateWork
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|namesystem
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

