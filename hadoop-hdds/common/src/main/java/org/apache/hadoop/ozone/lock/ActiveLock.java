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
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReadWriteLock
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
name|ReentrantReadWriteLock
import|;
end_import

begin_comment
comment|/**  * Lock implementation which also maintains counter.  */
end_comment

begin_class
DECL|class|ActiveLock
specifier|public
specifier|final
class|class
name|ActiveLock
block|{
DECL|field|lock
specifier|private
name|ReadWriteLock
name|lock
decl_stmt|;
DECL|field|count
specifier|private
name|AtomicInteger
name|count
decl_stmt|;
comment|/**    * Use ActiveLock#newInstance to create instance.    */
DECL|method|ActiveLock ()
specifier|private
name|ActiveLock
parameter_list|()
block|{
name|this
operator|.
name|lock
operator|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
expr_stmt|;
name|this
operator|.
name|count
operator|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new instance of ActiveLock.    *    * @return new ActiveLock    */
DECL|method|newInstance ()
specifier|public
specifier|static
name|ActiveLock
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|ActiveLock
argument_list|()
return|;
block|}
comment|/**    * Acquires read lock.    *    *<p>Acquires the read lock if the write lock is not held by    * another thread and returns immediately.    *    *<p>If the write lock is held by another thread then    * the current thread becomes disabled for thread scheduling    * purposes and lies dormant until the read lock has been acquired.    */
DECL|method|readLock ()
name|void
name|readLock
parameter_list|()
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
block|}
comment|/**    * Attempts to release the read lock.    *    *<p>If the number of readers is now zero then the lock    * is made available for write lock attempts.    */
DECL|method|readUnlock ()
name|void
name|readUnlock
parameter_list|()
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
comment|/**    * Acquires write lock.    *    *<p>Acquires the write lock if neither the read nor write lock    * are held by another thread    * and returns immediately, setting the write lock hold count to    * one.    *    *<p>If the current thread already holds the write lock then the    * hold count is incremented by one and the method returns    * immediately.    *    *<p>If the lock is held by another thread then the current    * thread becomes disabled for thread scheduling purposes and    * lies dormant until the write lock has been acquired.    */
DECL|method|writeLock ()
name|void
name|writeLock
parameter_list|()
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
block|}
comment|/**    * Attempts to release the write lock.    *    *<p>If the current thread is the holder of this lock then    * the hold count is decremented. If the hold count is now    * zero then the lock is released.    */
DECL|method|writeUnlock ()
name|void
name|writeUnlock
parameter_list|()
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
comment|/**    * Increment the active count of the lock.    */
DECL|method|incrementActiveCount ()
name|void
name|incrementActiveCount
parameter_list|()
block|{
name|count
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
comment|/**    * Decrement the active count of the lock.    */
DECL|method|decrementActiveCount ()
name|void
name|decrementActiveCount
parameter_list|()
block|{
name|count
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns the active count on the lock.    *    * @return Number of active leases on the lock.    */
DECL|method|getActiveLockCount ()
name|int
name|getActiveLockCount
parameter_list|()
block|{
return|return
name|count
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Resets the active count on the lock.    */
DECL|method|resetCounter ()
name|void
name|resetCounter
parameter_list|()
block|{
name|count
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|lock
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

