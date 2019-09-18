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
name|MapIterator
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
name|ozone
operator|.
name|OzoneConfigKeys
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
name|hdds
operator|.
name|utils
operator|.
name|MetadataStore
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
name|hdds
operator|.
name|utils
operator|.
name|MetadataStoreBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
specifier|final
class|class
name|ContainerCache
extends|extends
name|LRUMap
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
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
DECL|field|cache
specifier|private
specifier|static
name|ContainerCache
name|cache
decl_stmt|;
DECL|field|LOAD_FACTOR
specifier|private
specifier|static
specifier|final
name|float
name|LOAD_FACTOR
init|=
literal|0.75f
decl_stmt|;
comment|/**    * Constructs a cache that holds DBHandle references.    */
DECL|method|ContainerCache (int maxSize, float loadFactor, boolean scanUntilRemovable)
specifier|private
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
comment|/**    * Return a singleton instance of {@link ContainerCache}    * that holds the DB handlers.    *    * @param conf - Configuration.    * @return A instance of {@link ContainerCache}.    */
DECL|method|getInstance (Configuration conf)
specifier|public
specifier|synchronized
specifier|static
name|ContainerCache
name|getInstance
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
if|if
condition|(
name|cache
operator|==
literal|null
condition|)
block|{
name|int
name|cacheSize
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_CONTAINER_CACHE_SIZE
argument_list|,
name|OzoneConfigKeys
operator|.
name|OZONE_CONTAINER_CACHE_DEFAULT
argument_list|)
decl_stmt|;
name|cache
operator|=
operator|new
name|ContainerCache
argument_list|(
name|cacheSize
argument_list|,
name|LOAD_FACTOR
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
name|cache
return|;
block|}
comment|/**    * Closes all the db instances and resets the cache.    */
DECL|method|shutdownCache ()
specifier|public
name|void
name|shutdownCache
parameter_list|()
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
comment|// iterate the cache and close each db
name|MapIterator
name|iterator
init|=
name|cache
operator|.
name|mapIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
name|ReferenceCountedDB
name|db
init|=
operator|(
name|ReferenceCountedDB
operator|)
name|iterator
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|db
operator|.
name|cleanup
argument_list|()
argument_list|,
literal|"refCount:"
argument_list|,
name|db
operator|.
name|getReferenceCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// reset the cache
name|cache
operator|.
name|clear
argument_list|()
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
name|ReferenceCountedDB
name|db
init|=
operator|(
name|ReferenceCountedDB
operator|)
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|db
operator|.
name|cleanup
argument_list|()
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
comment|/**    * Returns a DB handle if available, create the handler otherwise.    *    * @param containerID - ID of the container.    * @param containerDBType - DB type of the container.    * @param containerDBPath - DB path of the container.    * @param conf - Hadoop Configuration.    * @return ReferenceCountedDB.    */
DECL|method|getDB (long containerID, String containerDBType, String containerDBPath, Configuration conf)
specifier|public
name|ReferenceCountedDB
name|getDB
parameter_list|(
name|long
name|containerID
parameter_list|,
name|String
name|containerDBType
parameter_list|,
name|String
name|containerDBPath
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|containerID
operator|>=
literal|0
argument_list|,
literal|"Container ID cannot be negative."
argument_list|)
expr_stmt|;
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|ReferenceCountedDB
name|db
init|=
operator|(
name|ReferenceCountedDB
operator|)
name|this
operator|.
name|get
argument_list|(
name|containerDBPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|db
operator|==
literal|null
condition|)
block|{
name|MetadataStore
name|metadataStore
init|=
name|MetadataStoreBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|setDbFile
argument_list|(
operator|new
name|File
argument_list|(
name|containerDBPath
argument_list|)
argument_list|)
operator|.
name|setCreateIfMissing
argument_list|(
literal|false
argument_list|)
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
operator|.
name|setDBType
argument_list|(
name|containerDBType
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|db
operator|=
operator|new
name|ReferenceCountedDB
argument_list|(
name|metadataStore
argument_list|,
name|containerDBPath
argument_list|)
expr_stmt|;
name|this
operator|.
name|put
argument_list|(
name|containerDBPath
argument_list|,
name|db
argument_list|)
expr_stmt|;
block|}
comment|// increment the reference before returning the object
name|db
operator|.
name|incrementReference
argument_list|()
expr_stmt|;
return|return
name|db
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error opening DB. Container:{} ContainerPath:{}"
argument_list|,
name|containerID
argument_list|,
name|containerDBPath
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
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
comment|/**    * Remove a DB handler from cache.    *    * @param containerDBPath - path of the container db file.    */
DECL|method|removeDB (String containerDBPath)
specifier|public
name|void
name|removeDB
parameter_list|(
name|String
name|containerDBPath
parameter_list|)
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|ReferenceCountedDB
name|db
init|=
operator|(
name|ReferenceCountedDB
operator|)
name|this
operator|.
name|get
argument_list|(
name|containerDBPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|db
operator|!=
literal|null
condition|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|db
operator|.
name|cleanup
argument_list|()
argument_list|,
literal|"refCount:"
argument_list|,
name|db
operator|.
name|getReferenceCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|remove
argument_list|(
name|containerDBPath
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

