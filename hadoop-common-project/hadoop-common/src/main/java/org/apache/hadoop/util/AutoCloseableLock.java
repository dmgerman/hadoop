begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
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
name|locks
operator|.
name|ReentrantLock
import|;
end_import

begin_comment
comment|/**  * This is a wrap class of a ReentrantLock. Extending AutoCloseable  * interface such that the users can use a try-with-resource syntax.  */
end_comment

begin_class
DECL|class|AutoCloseableLock
specifier|public
class|class
name|AutoCloseableLock
implements|implements
name|AutoCloseable
block|{
DECL|field|lock
specifier|private
specifier|final
name|ReentrantLock
name|lock
decl_stmt|;
comment|/**    * Creates an instance of {@code AutoCloseableLock}, initializes    * the underlying {@code ReentrantLock} object.    */
DECL|method|AutoCloseableLock ()
specifier|public
name|AutoCloseableLock
parameter_list|()
block|{
name|this
operator|.
name|lock
operator|=
operator|new
name|ReentrantLock
argument_list|()
expr_stmt|;
block|}
comment|/**    * A wrapper method that makes a call to {@code lock()} of the underlying    * {@code ReentrantLock} object.    *    * Acquire teh lock it is not held by another thread, then sets    * lock held count to one, then returns immediately.    *    * If the current thread already holds the lock, increase the lock    * help count by one and returns immediately.    *    * If the lock is held by another thread, the current thread is    * suspended until the lock has been acquired by current thread.    *    * @return The {@code ReentrantLock} object itself. This is to    * support try-with-resource syntax.    */
DECL|method|acquire ()
specifier|public
name|AutoCloseableLock
name|acquire
parameter_list|()
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * A wrapper method that makes a call to {@code unlock()} of the    * underlying {@code ReentrantLock} object.    *    * Attempts to release the lock.    *    * If the current thread holds the lock, decrements the hold    * count. If the hold count reaches zero, the lock is released.    *    * If the current thread does not hold the lock, then    * {@link IllegalMonitorStateException} is thrown.    */
DECL|method|release ()
specifier|public
name|void
name|release
parameter_list|()
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
comment|/**    * Attempts to release the lock by making a call to {@code release()}.    *    * This is to implement {@code close()} method from {@code AutoCloseable}    * interface. This allows users to user a try-with-resource syntax, where    * the lock can be automatically released.    */
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
name|release
argument_list|()
expr_stmt|;
block|}
comment|/**    * A wrapper method that makes a call to {@code tryLock()} of    * the underlying {@code ReentrantLock} object.    *    * If the lock is not held by another thread, acquires the lock, set the    * hold count to one and returns {@code true}.    *    * If the current thread already holds the lock, the increment the hold    * count by one and returns {@code true}.    *    * If the lock is held by another thread then the method returns    * immediately with {@code false}.    *    * @return {@code true} if the lock was free and was acquired by the    *          current thread, or the lock was already held by the current    *          thread; and {@code false} otherwise.    */
DECL|method|tryLock ()
specifier|public
name|boolean
name|tryLock
parameter_list|()
block|{
return|return
name|lock
operator|.
name|tryLock
argument_list|()
return|;
block|}
comment|/**    * A wrapper method that makes a call to {@code isLocked()} of    * the underlying {@code ReentrantLock} object.    *    * Queries if this lock is held by any thread. This method is    * designed for use in monitoring of the system state,    * not for synchronization control.    *    * @return {@code true} if any thread holds this lock and    *         {@code false} otherwise    */
DECL|method|isLocked ()
specifier|public
name|boolean
name|isLocked
parameter_list|()
block|{
return|return
name|lock
operator|.
name|isLocked
argument_list|()
return|;
block|}
block|}
end_class

end_unit

