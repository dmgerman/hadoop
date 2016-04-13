begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.utils
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|utils
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|collections
operator|.
name|map
operator|.
name|LRUMap
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
name|concurrent
operator|.
name|locks
operator|.
name|Lock
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
name|ReentrantLock
import|;
end_import

begin_comment
comment|/**  * container cache is a LRUMap that maintains the DB handles.  */
end_comment

begin_class
DECL|class|ContainerCache
specifier|public
class|class
name|ContainerCache
extends|extends
name|LRUMap
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ContainerCache
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|lock
specifier|private
specifier|final
name|Lock
name|lock
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
comment|/**    * Constructs a cache that holds DBHandle references.    */
DECL|method|ContainerCache (int maxSize, float loadFactor, boolean scanUntilRemovable)
specifier|public
name|ContainerCache
parameter_list|(
name|int
name|maxSize
parameter_list|,
name|float
name|loadFactor
parameter_list|,
name|boolean
name|scanUntilRemovable
parameter_list|)
block|{
name|super
argument_list|(
name|maxSize
argument_list|,
name|loadFactor
argument_list|,
name|scanUntilRemovable
argument_list|)
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|removeLRU (LinkEntry entry)
specifier|protected
name|boolean
name|removeLRU
parameter_list|(
name|LinkEntry
name|entry
parameter_list|)
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|LevelDBStore
name|db
init|=
operator|(
name|LevelDBStore
operator|)
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|db
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error closing DB. Container: "
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Returns a DB handle if available, null otherwise.    *    * @param containerName - Name of the container.    * @return OzoneLevelDBStore.    */
DECL|method|getDB (String containerName)
specifier|public
name|LevelDBStore
name|getDB
parameter_list|(
name|String
name|containerName
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|containerName
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
operator|!
name|containerName
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
operator|(
name|LevelDBStore
operator|)
name|this
operator|.
name|get
argument_list|(
name|containerName
argument_list|)
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Add a new DB to the cache.    *    * @param containerName - Name of the container    * @param db            - DB handle    */
DECL|method|putDB (String containerName, LevelDBStore db)
specifier|public
name|void
name|putDB
parameter_list|(
name|String
name|containerName
parameter_list|,
name|LevelDBStore
name|db
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|containerName
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
operator|!
name|containerName
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|this
operator|.
name|put
argument_list|(
name|containerName
argument_list|,
name|db
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

