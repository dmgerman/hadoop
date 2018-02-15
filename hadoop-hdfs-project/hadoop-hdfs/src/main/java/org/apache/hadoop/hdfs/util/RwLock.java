begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|util
package|;
end_package

begin_comment
comment|/** Read-write lock interface. */
end_comment

begin_interface
DECL|interface|RwLock
specifier|public
interface|interface
name|RwLock
block|{
comment|/** Acquire read lock. */
DECL|method|readLock ()
specifier|public
name|void
name|readLock
parameter_list|()
function_decl|;
comment|/** Acquire read lock, unless interrupted while waiting  */
DECL|method|readLockInterruptibly ()
name|void
name|readLockInterruptibly
parameter_list|()
throws|throws
name|InterruptedException
function_decl|;
comment|/** Release read lock. */
DECL|method|readUnlock ()
specifier|public
name|void
name|readUnlock
parameter_list|()
function_decl|;
comment|/** Check if the current thread holds read lock. */
DECL|method|hasReadLock ()
specifier|public
name|boolean
name|hasReadLock
parameter_list|()
function_decl|;
comment|/** Acquire write lock. */
DECL|method|writeLock ()
specifier|public
name|void
name|writeLock
parameter_list|()
function_decl|;
comment|/** Acquire write lock, unless interrupted while waiting  */
DECL|method|writeLockInterruptibly ()
name|void
name|writeLockInterruptibly
parameter_list|()
throws|throws
name|InterruptedException
function_decl|;
comment|/** Release write lock. */
DECL|method|writeUnlock ()
specifier|public
name|void
name|writeUnlock
parameter_list|()
function_decl|;
comment|/** Check if the current thread holds write lock. */
DECL|method|hasWriteLock ()
specifier|public
name|boolean
name|hasWriteLock
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

