begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.lock
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|lock
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
name|Test
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
name|AtomicBoolean
import|;
end_import

begin_comment
comment|/**  * Test-cases to test LockManager.  */
end_comment

begin_class
DECL|class|TestLockManager
specifier|public
class|class
name|TestLockManager
block|{
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
DECL|method|testWriteLockWithDifferentResource ()
specifier|public
name|void
name|testWriteLockWithDifferentResource
parameter_list|()
block|{
specifier|final
name|LockManager
argument_list|<
name|String
argument_list|>
name|manager
init|=
operator|new
name|LockManager
argument_list|<>
argument_list|(
operator|new
name|OzoneConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|manager
operator|.
name|writeLock
argument_list|(
literal|"/resourceOne"
argument_list|)
expr_stmt|;
comment|// This should work, as they are different resource.
name|manager
operator|.
name|writeLock
argument_list|(
literal|"/resourceTwo"
argument_list|)
expr_stmt|;
name|manager
operator|.
name|writeUnlock
argument_list|(
literal|"/resourceOne"
argument_list|)
expr_stmt|;
name|manager
operator|.
name|writeUnlock
argument_list|(
literal|"/resourceTwo"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWriteLockWithSameResource ()
specifier|public
name|void
name|testWriteLockWithSameResource
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|LockManager
argument_list|<
name|String
argument_list|>
name|manager
init|=
operator|new
name|LockManager
argument_list|<>
argument_list|(
operator|new
name|OzoneConfiguration
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|gotLock
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|manager
operator|.
name|writeLock
argument_list|(
literal|"/resourceOne"
argument_list|)
expr_stmt|;
operator|new
name|Thread
argument_list|(
parameter_list|()
lambda|->
block|{
name|manager
operator|.
name|writeLock
argument_list|(
literal|"/resourceOne"
argument_list|)
expr_stmt|;
name|gotLock
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|manager
operator|.
name|writeUnlock
argument_list|(
literal|"/resourceOne"
argument_list|)
expr_stmt|;
block|}
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Let's give some time for the other thread to run
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
comment|// Since the other thread is trying to get write lock on same object,
comment|// it will wait.
name|Assert
operator|.
name|assertFalse
argument_list|(
name|gotLock
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|manager
operator|.
name|writeUnlock
argument_list|(
literal|"/resourceOne"
argument_list|)
expr_stmt|;
comment|// Since we have released the write lock, the other thread should have
comment|// the lock now
comment|// Let's give some time for the other thread to run
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|gotLock
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
DECL|method|testReadLockWithDifferentResource ()
specifier|public
name|void
name|testReadLockWithDifferentResource
parameter_list|()
block|{
specifier|final
name|LockManager
argument_list|<
name|String
argument_list|>
name|manager
init|=
operator|new
name|LockManager
argument_list|<>
argument_list|(
operator|new
name|OzoneConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|manager
operator|.
name|readLock
argument_list|(
literal|"/resourceOne"
argument_list|)
expr_stmt|;
name|manager
operator|.
name|readLock
argument_list|(
literal|"/resourceTwo"
argument_list|)
expr_stmt|;
name|manager
operator|.
name|readUnlock
argument_list|(
literal|"/resourceOne"
argument_list|)
expr_stmt|;
name|manager
operator|.
name|readUnlock
argument_list|(
literal|"/resourceTwo"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReadLockWithSameResource ()
specifier|public
name|void
name|testReadLockWithSameResource
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|LockManager
argument_list|<
name|String
argument_list|>
name|manager
init|=
operator|new
name|LockManager
argument_list|<>
argument_list|(
operator|new
name|OzoneConfiguration
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|gotLock
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|manager
operator|.
name|readLock
argument_list|(
literal|"/resourceOne"
argument_list|)
expr_stmt|;
operator|new
name|Thread
argument_list|(
parameter_list|()
lambda|->
block|{
name|manager
operator|.
name|readLock
argument_list|(
literal|"/resourceOne"
argument_list|)
expr_stmt|;
name|gotLock
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|manager
operator|.
name|readUnlock
argument_list|(
literal|"/resourceOne"
argument_list|)
expr_stmt|;
block|}
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Let's give some time for the other thread to run
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
comment|// Since the new thread is trying to get read lock, it should work.
name|Assert
operator|.
name|assertTrue
argument_list|(
name|gotLock
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|manager
operator|.
name|readUnlock
argument_list|(
literal|"/resourceOne"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWriteReadLockWithSameResource ()
specifier|public
name|void
name|testWriteReadLockWithSameResource
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|LockManager
argument_list|<
name|String
argument_list|>
name|manager
init|=
operator|new
name|LockManager
argument_list|<>
argument_list|(
operator|new
name|OzoneConfiguration
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|gotLock
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|manager
operator|.
name|writeLock
argument_list|(
literal|"/resourceOne"
argument_list|)
expr_stmt|;
operator|new
name|Thread
argument_list|(
parameter_list|()
lambda|->
block|{
name|manager
operator|.
name|readLock
argument_list|(
literal|"/resourceOne"
argument_list|)
expr_stmt|;
name|gotLock
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|manager
operator|.
name|readUnlock
argument_list|(
literal|"/resourceOne"
argument_list|)
expr_stmt|;
block|}
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Let's give some time for the other thread to run
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
comment|// Since the other thread is trying to get read lock on same object,
comment|// it will wait.
name|Assert
operator|.
name|assertFalse
argument_list|(
name|gotLock
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|manager
operator|.
name|writeUnlock
argument_list|(
literal|"/resourceOne"
argument_list|)
expr_stmt|;
comment|// Since we have released the write lock, the other thread should have
comment|// the lock now
comment|// Let's give some time for the other thread to run
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|gotLock
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReadWriteLockWithSameResource ()
specifier|public
name|void
name|testReadWriteLockWithSameResource
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|LockManager
argument_list|<
name|String
argument_list|>
name|manager
init|=
operator|new
name|LockManager
argument_list|<>
argument_list|(
operator|new
name|OzoneConfiguration
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|gotLock
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|manager
operator|.
name|readLock
argument_list|(
literal|"/resourceOne"
argument_list|)
expr_stmt|;
operator|new
name|Thread
argument_list|(
parameter_list|()
lambda|->
block|{
name|manager
operator|.
name|writeLock
argument_list|(
literal|"/resourceOne"
argument_list|)
expr_stmt|;
name|gotLock
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|manager
operator|.
name|writeUnlock
argument_list|(
literal|"/resourceOne"
argument_list|)
expr_stmt|;
block|}
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Let's give some time for the other thread to run
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
comment|// Since the other thread is trying to get write lock on same object,
comment|// it will wait.
name|Assert
operator|.
name|assertFalse
argument_list|(
name|gotLock
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|manager
operator|.
name|readUnlock
argument_list|(
literal|"/resourceOne"
argument_list|)
expr_stmt|;
comment|// Since we have released the read lock, the other thread should have
comment|// the lock now
comment|// Let's give some time for the other thread to run
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|gotLock
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultiReadWriteLockWithSameResource ()
specifier|public
name|void
name|testMultiReadWriteLockWithSameResource
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|LockManager
argument_list|<
name|String
argument_list|>
name|manager
init|=
operator|new
name|LockManager
argument_list|<>
argument_list|(
operator|new
name|OzoneConfiguration
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|gotLock
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|manager
operator|.
name|readLock
argument_list|(
literal|"/resourceOne"
argument_list|)
expr_stmt|;
name|manager
operator|.
name|readLock
argument_list|(
literal|"/resourceOne"
argument_list|)
expr_stmt|;
operator|new
name|Thread
argument_list|(
parameter_list|()
lambda|->
block|{
name|manager
operator|.
name|writeLock
argument_list|(
literal|"/resourceOne"
argument_list|)
expr_stmt|;
name|gotLock
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|manager
operator|.
name|writeUnlock
argument_list|(
literal|"/resourceOne"
argument_list|)
expr_stmt|;
block|}
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Let's give some time for the other thread to run
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
comment|// Since the other thread is trying to get write lock on same object,
comment|// it will wait.
name|Assert
operator|.
name|assertFalse
argument_list|(
name|gotLock
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|manager
operator|.
name|readUnlock
argument_list|(
literal|"/resourceOne"
argument_list|)
expr_stmt|;
comment|//We have only released one read lock, we still hold another read lock.
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|gotLock
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|manager
operator|.
name|readUnlock
argument_list|(
literal|"/resourceOne"
argument_list|)
expr_stmt|;
comment|// Since we have released the read lock, the other thread should have
comment|// the lock now
comment|// Let's give some time for the other thread to run
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|gotLock
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

