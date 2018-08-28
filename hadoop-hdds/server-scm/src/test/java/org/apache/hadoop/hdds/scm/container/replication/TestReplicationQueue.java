begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.container.replication
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
name|replication
package|;
end_package

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

begin_comment
comment|/**  * Test class for ReplicationQueue.  */
end_comment

begin_class
DECL|class|TestReplicationQueue
specifier|public
class|class
name|TestReplicationQueue
block|{
DECL|field|replicationQueue
specifier|private
name|ReplicationQueue
name|replicationQueue
decl_stmt|;
DECL|field|random
specifier|private
name|Random
name|random
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|replicationQueue
operator|=
operator|new
name|ReplicationQueue
argument_list|()
expr_stmt|;
name|random
operator|=
operator|new
name|Random
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDuplicateAddOp ()
specifier|public
name|void
name|testDuplicateAddOp
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|long
name|contId
init|=
name|random
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|String
name|nodeId
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|ReplicationRequest
name|obj1
decl_stmt|,
name|obj2
decl_stmt|,
name|obj3
decl_stmt|;
name|long
name|time
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
name|obj1
operator|=
operator|new
name|ReplicationRequest
argument_list|(
name|contId
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|,
name|time
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|)
expr_stmt|;
name|obj2
operator|=
operator|new
name|ReplicationRequest
argument_list|(
name|contId
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|,
name|time
operator|+
literal|1
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|)
expr_stmt|;
name|obj3
operator|=
operator|new
name|ReplicationRequest
argument_list|(
name|contId
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
name|time
operator|+
literal|2
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|)
expr_stmt|;
name|replicationQueue
operator|.
name|add
argument_list|(
name|obj1
argument_list|)
expr_stmt|;
name|replicationQueue
operator|.
name|add
argument_list|(
name|obj2
argument_list|)
expr_stmt|;
name|replicationQueue
operator|.
name|add
argument_list|(
name|obj3
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Should add only 1 msg as second one is duplicate"
argument_list|,
literal|1
argument_list|,
name|replicationQueue
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ReplicationRequest
name|temp
init|=
name|replicationQueue
operator|.
name|take
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|temp
argument_list|,
name|obj3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPollOp ()
specifier|public
name|void
name|testPollOp
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|long
name|contId
init|=
name|random
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|String
name|nodeId
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|ReplicationRequest
name|msg1
decl_stmt|,
name|msg2
decl_stmt|,
name|msg3
decl_stmt|,
name|msg4
decl_stmt|,
name|msg5
decl_stmt|;
name|msg1
operator|=
operator|new
name|ReplicationRequest
argument_list|(
name|contId
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
name|Time
operator|.
name|monotonicNow
argument_list|()
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|)
expr_stmt|;
name|long
name|time
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
name|msg2
operator|=
operator|new
name|ReplicationRequest
argument_list|(
name|contId
operator|+
literal|1
argument_list|,
operator|(
name|short
operator|)
literal|4
argument_list|,
name|time
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|)
expr_stmt|;
name|msg3
operator|=
operator|new
name|ReplicationRequest
argument_list|(
name|contId
operator|+
literal|2
argument_list|,
operator|(
name|short
operator|)
literal|0
argument_list|,
name|time
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|)
expr_stmt|;
name|msg4
operator|=
operator|new
name|ReplicationRequest
argument_list|(
name|contId
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|,
name|time
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|)
expr_stmt|;
comment|// Replication message for same container but different nodeId
name|msg5
operator|=
operator|new
name|ReplicationRequest
argument_list|(
name|contId
operator|+
literal|1
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|,
name|time
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|)
expr_stmt|;
name|replicationQueue
operator|.
name|add
argument_list|(
name|msg1
argument_list|)
expr_stmt|;
name|replicationQueue
operator|.
name|add
argument_list|(
name|msg2
argument_list|)
expr_stmt|;
name|replicationQueue
operator|.
name|add
argument_list|(
name|msg3
argument_list|)
expr_stmt|;
name|replicationQueue
operator|.
name|add
argument_list|(
name|msg4
argument_list|)
expr_stmt|;
name|replicationQueue
operator|.
name|add
argument_list|(
name|msg5
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Should have 3 objects"
argument_list|,
literal|3
argument_list|,
name|replicationQueue
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Since Priority queue orders messages according to replication count,
comment|// message with lowest replication should be first
name|ReplicationRequest
name|temp
decl_stmt|;
name|temp
operator|=
name|replicationQueue
operator|.
name|take
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Should have 2 objects"
argument_list|,
literal|2
argument_list|,
name|replicationQueue
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|temp
argument_list|,
name|msg3
argument_list|)
expr_stmt|;
name|temp
operator|=
name|replicationQueue
operator|.
name|take
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Should have 1 objects"
argument_list|,
literal|1
argument_list|,
name|replicationQueue
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|temp
argument_list|,
name|msg5
argument_list|)
expr_stmt|;
comment|// Message 2 should be ordered before message 5 as both have same
comment|// replication number but message 2 has earlier timestamp.
name|temp
operator|=
name|replicationQueue
operator|.
name|take
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Should have 0 objects"
argument_list|,
name|replicationQueue
operator|.
name|size
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|temp
argument_list|,
name|msg4
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRemoveOp ()
specifier|public
name|void
name|testRemoveOp
parameter_list|()
block|{
name|long
name|contId
init|=
name|random
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|String
name|nodeId
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|ReplicationRequest
name|obj1
decl_stmt|,
name|obj2
decl_stmt|,
name|obj3
decl_stmt|;
name|obj1
operator|=
operator|new
name|ReplicationRequest
argument_list|(
name|contId
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
name|Time
operator|.
name|monotonicNow
argument_list|()
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|)
expr_stmt|;
name|obj2
operator|=
operator|new
name|ReplicationRequest
argument_list|(
name|contId
operator|+
literal|1
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|,
name|Time
operator|.
name|monotonicNow
argument_list|()
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|)
expr_stmt|;
name|obj3
operator|=
operator|new
name|ReplicationRequest
argument_list|(
name|contId
operator|+
literal|2
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|,
name|Time
operator|.
name|monotonicNow
argument_list|()
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|)
expr_stmt|;
name|replicationQueue
operator|.
name|add
argument_list|(
name|obj1
argument_list|)
expr_stmt|;
name|replicationQueue
operator|.
name|add
argument_list|(
name|obj2
argument_list|)
expr_stmt|;
name|replicationQueue
operator|.
name|add
argument_list|(
name|obj3
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Should have 3 objects"
argument_list|,
literal|3
argument_list|,
name|replicationQueue
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|replicationQueue
operator|.
name|remove
argument_list|(
name|obj3
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Should have 2 objects"
argument_list|,
literal|2
argument_list|,
name|replicationQueue
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|replicationQueue
operator|.
name|remove
argument_list|(
name|obj2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Should have 1 objects"
argument_list|,
literal|1
argument_list|,
name|replicationQueue
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|replicationQueue
operator|.
name|remove
argument_list|(
name|obj1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Should have 0 objects"
argument_list|,
literal|0
argument_list|,
name|replicationQueue
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

