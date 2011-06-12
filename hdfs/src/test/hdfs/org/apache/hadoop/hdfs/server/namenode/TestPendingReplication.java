begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|namenode
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
name|java
operator|.
name|lang
operator|.
name|System
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

begin_comment
comment|/**  * This class tests the internals of PendingReplicationBlocks.java  */
end_comment

begin_class
DECL|class|TestPendingReplication
specifier|public
class|class
name|TestPendingReplication
extends|extends
name|TestCase
block|{
DECL|field|TIMEOUT
specifier|final
specifier|static
name|int
name|TIMEOUT
init|=
literal|3
decl_stmt|;
comment|// 3 seconds
DECL|method|testPendingReplication ()
specifier|public
name|void
name|testPendingReplication
parameter_list|()
block|{
name|PendingReplicationBlocks
name|pendingReplications
decl_stmt|;
name|pendingReplications
operator|=
operator|new
name|PendingReplicationBlocks
argument_list|(
name|TIMEOUT
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|pendingReplications
operator|.
name|start
argument_list|()
expr_stmt|;
comment|//
comment|// Add 10 blocks to pendingReplications.
comment|//
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
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
argument_list|,
name|i
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|pendingReplications
operator|.
name|add
argument_list|(
name|block
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Size of pendingReplications "
argument_list|,
literal|10
argument_list|,
name|pendingReplications
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|//
comment|// remove one item and reinsert it
comment|//
name|Block
name|blk
init|=
operator|new
name|Block
argument_list|(
literal|8
argument_list|,
literal|8
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|pendingReplications
operator|.
name|remove
argument_list|(
name|blk
argument_list|)
expr_stmt|;
comment|// removes one replica
name|assertEquals
argument_list|(
literal|"pendingReplications.getNumReplicas "
argument_list|,
literal|7
argument_list|,
name|pendingReplications
operator|.
name|getNumReplicas
argument_list|(
name|blk
argument_list|)
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
literal|7
condition|;
name|i
operator|++
control|)
block|{
name|pendingReplications
operator|.
name|remove
argument_list|(
name|blk
argument_list|)
expr_stmt|;
comment|// removes all replicas
block|}
name|assertTrue
argument_list|(
name|pendingReplications
operator|.
name|size
argument_list|()
operator|==
literal|9
argument_list|)
expr_stmt|;
name|pendingReplications
operator|.
name|add
argument_list|(
name|blk
argument_list|,
literal|8
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pendingReplications
operator|.
name|size
argument_list|()
operator|==
literal|10
argument_list|)
expr_stmt|;
comment|//
comment|// verify that the number of replicas returned
comment|// are sane.
comment|//
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
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
argument_list|,
name|i
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|int
name|numReplicas
init|=
name|pendingReplications
operator|.
name|getNumReplicas
argument_list|(
name|block
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|numReplicas
operator|==
name|i
argument_list|)
expr_stmt|;
block|}
comment|//
comment|// verify that nothing has timed out so far
comment|//
name|assertTrue
argument_list|(
name|pendingReplications
operator|.
name|getTimedOutBlocks
argument_list|()
operator|==
literal|null
argument_list|)
expr_stmt|;
comment|//
comment|// Wait for one second and then insert some more items.
comment|//
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{     }
for|for
control|(
name|int
name|i
init|=
literal|10
init|;
name|i
operator|<
literal|15
condition|;
name|i
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
argument_list|,
name|i
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|pendingReplications
operator|.
name|add
argument_list|(
name|block
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|pendingReplications
operator|.
name|size
argument_list|()
operator|==
literal|15
argument_list|)
expr_stmt|;
comment|//
comment|// Wait for everything to timeout.
comment|//
name|int
name|loop
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|pendingReplications
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{       }
name|loop
operator|++
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Had to wait for "
operator|+
name|loop
operator|+
literal|" seconds for the lot to timeout"
argument_list|)
expr_stmt|;
comment|//
comment|// Verify that everything has timed out.
comment|//
name|assertEquals
argument_list|(
literal|"Size of pendingReplications "
argument_list|,
literal|0
argument_list|,
name|pendingReplications
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Block
index|[]
name|timedOut
init|=
name|pendingReplications
operator|.
name|getTimedOutBlocks
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|timedOut
operator|!=
literal|null
operator|&&
name|timedOut
operator|.
name|length
operator|==
literal|15
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
name|timedOut
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|timedOut
index|[
name|i
index|]
operator|.
name|getBlockId
argument_list|()
operator|<
literal|15
argument_list|)
expr_stmt|;
block|}
name|pendingReplications
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

