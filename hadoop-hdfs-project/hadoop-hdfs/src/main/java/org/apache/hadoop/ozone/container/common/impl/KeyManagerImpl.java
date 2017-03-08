begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.impl
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
name|impl
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|ContainerProtos
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|ContainerData
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
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|KeyData
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
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|KeyUtils
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
name|container
operator|.
name|common
operator|.
name|interfaces
operator|.
name|ContainerManager
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
name|container
operator|.
name|common
operator|.
name|interfaces
operator|.
name|KeyManager
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
name|container
operator|.
name|common
operator|.
name|utils
operator|.
name|ContainerCache
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
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|Pipeline
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
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|StorageContainerException
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
name|utils
operator|.
name|LevelDBStore
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|Result
operator|.
name|IO_EXCEPTION
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|Result
operator|.
name|NO_SUCH_KEY
import|;
end_import

begin_comment
comment|/**  * Key Manager impl.  */
end_comment

begin_class
DECL|class|KeyManagerImpl
specifier|public
class|class
name|KeyManagerImpl
implements|implements
name|KeyManager
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|KeyManagerImpl
operator|.
name|class
argument_list|)
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
DECL|field|containerManager
specifier|private
specifier|final
name|ContainerManager
name|containerManager
decl_stmt|;
DECL|field|containerCache
specifier|private
specifier|final
name|ContainerCache
name|containerCache
decl_stmt|;
comment|/**    * Constructs a key Manager.    *    * @param containerManager - Container Manager.    */
DECL|method|KeyManagerImpl (ContainerManager containerManager, Configuration conf)
specifier|public
name|KeyManagerImpl
parameter_list|(
name|ContainerManager
name|containerManager
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|containerManager
argument_list|,
literal|"Container manager cannot be"
operator|+
literal|" null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|conf
argument_list|,
literal|"Config cannot be null"
argument_list|)
expr_stmt|;
name|int
name|cacheSize
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_KEY_CACHE
argument_list|,
name|OzoneConfigKeys
operator|.
name|OZONE_KEY_CACHE_DEFAULT
argument_list|)
decl_stmt|;
name|this
operator|.
name|containerManager
operator|=
name|containerManager
expr_stmt|;
name|containerCache
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
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|putKey (Pipeline pipeline, KeyData data)
specifier|public
name|void
name|putKey
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|,
name|KeyData
name|data
parameter_list|)
throws|throws
name|StorageContainerException
block|{
name|containerManager
operator|.
name|readLock
argument_list|()
expr_stmt|;
try|try
block|{
comment|// We are not locking the key manager since LevelDb serializes all actions
comment|// against a single DB. We rely on DB level locking to avoid conflicts.
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|pipeline
argument_list|,
literal|"Pipeline cannot be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|pipeline
operator|.
name|getContainerName
argument_list|()
argument_list|,
literal|"Container name cannot be null"
argument_list|)
expr_stmt|;
name|ContainerData
name|cData
init|=
name|containerManager
operator|.
name|readContainer
argument_list|(
name|pipeline
operator|.
name|getContainerName
argument_list|()
argument_list|)
decl_stmt|;
name|LevelDBStore
name|db
init|=
name|KeyUtils
operator|.
name|getDB
argument_list|(
name|cData
argument_list|,
name|containerCache
argument_list|)
decl_stmt|;
comment|// This is a post condition that acts as a hint to the user.
comment|// Should never fail.
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|db
argument_list|,
literal|"DB cannot be null here"
argument_list|)
expr_stmt|;
name|db
operator|.
name|put
argument_list|(
name|data
operator|.
name|getKeyName
argument_list|()
operator|.
name|getBytes
argument_list|(
name|KeyUtils
operator|.
name|ENCODING
argument_list|)
argument_list|,
name|data
operator|.
name|getProtoBufMessage
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|containerManager
operator|.
name|readUnlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|getKey (KeyData data)
specifier|public
name|KeyData
name|getKey
parameter_list|(
name|KeyData
name|data
parameter_list|)
throws|throws
name|StorageContainerException
block|{
name|containerManager
operator|.
name|readLock
argument_list|()
expr_stmt|;
try|try
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|data
argument_list|,
literal|"Key data cannot be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|data
operator|.
name|getContainerName
argument_list|()
argument_list|,
literal|"Container name cannot be null"
argument_list|)
expr_stmt|;
name|ContainerData
name|cData
init|=
name|containerManager
operator|.
name|readContainer
argument_list|(
name|data
operator|.
name|getContainerName
argument_list|()
argument_list|)
decl_stmt|;
name|LevelDBStore
name|db
init|=
name|KeyUtils
operator|.
name|getDB
argument_list|(
name|cData
argument_list|,
name|containerCache
argument_list|)
decl_stmt|;
comment|// This is a post condition that acts as a hint to the user.
comment|// Should never fail.
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|db
argument_list|,
literal|"DB cannot be null here"
argument_list|)
expr_stmt|;
name|byte
index|[]
name|kData
init|=
name|db
operator|.
name|get
argument_list|(
name|data
operator|.
name|getKeyName
argument_list|()
operator|.
name|getBytes
argument_list|(
name|KeyUtils
operator|.
name|ENCODING
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|kData
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|StorageContainerException
argument_list|(
literal|"Unable to find the key."
argument_list|,
name|NO_SUCH_KEY
argument_list|)
throw|;
block|}
name|ContainerProtos
operator|.
name|KeyData
name|keyData
init|=
name|ContainerProtos
operator|.
name|KeyData
operator|.
name|parseFrom
argument_list|(
name|kData
argument_list|)
decl_stmt|;
return|return
name|KeyData
operator|.
name|getFromProtoBuf
argument_list|(
name|keyData
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|StorageContainerException
argument_list|(
name|ex
argument_list|,
name|IO_EXCEPTION
argument_list|)
throw|;
block|}
finally|finally
block|{
name|containerManager
operator|.
name|readUnlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|deleteKey (Pipeline pipeline, String keyName)
specifier|public
name|void
name|deleteKey
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|,
name|String
name|keyName
parameter_list|)
throws|throws
name|StorageContainerException
block|{
name|containerManager
operator|.
name|readLock
argument_list|()
expr_stmt|;
try|try
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|pipeline
argument_list|,
literal|"Pipeline cannot be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|pipeline
operator|.
name|getContainerName
argument_list|()
argument_list|,
literal|"Container name cannot be null"
argument_list|)
expr_stmt|;
name|ContainerData
name|cData
init|=
name|containerManager
operator|.
name|readContainer
argument_list|(
name|pipeline
operator|.
name|getContainerName
argument_list|()
argument_list|)
decl_stmt|;
name|LevelDBStore
name|db
init|=
name|KeyUtils
operator|.
name|getDB
argument_list|(
name|cData
argument_list|,
name|containerCache
argument_list|)
decl_stmt|;
comment|// This is a post condition that acts as a hint to the user.
comment|// Should never fail.
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|db
argument_list|,
literal|"DB cannot be null here"
argument_list|)
expr_stmt|;
comment|// Note : There is a race condition here, since get and delete
comment|// are not atomic. Leaving it here since the impact is refusing
comment|// to delete a key which might have just gotten inserted after
comment|// the get check.
name|byte
index|[]
name|kData
init|=
name|db
operator|.
name|get
argument_list|(
name|keyName
operator|.
name|getBytes
argument_list|(
name|KeyUtils
operator|.
name|ENCODING
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|kData
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|StorageContainerException
argument_list|(
literal|"Unable to find the key."
argument_list|,
name|NO_SUCH_KEY
argument_list|)
throw|;
block|}
name|db
operator|.
name|delete
argument_list|(
name|keyName
operator|.
name|getBytes
argument_list|(
name|KeyUtils
operator|.
name|ENCODING
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|containerManager
operator|.
name|readUnlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|listKey (Pipeline pipeline, String prefix, String prevKey, int count)
specifier|public
name|List
argument_list|<
name|KeyData
argument_list|>
name|listKey
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|,
name|String
name|prefix
parameter_list|,
name|String
name|prevKey
parameter_list|,
name|int
name|count
parameter_list|)
block|{
comment|// TODO : Implement listKey function.
return|return
literal|null
return|;
block|}
comment|/**    * Shutdown keyManager.    */
annotation|@
name|Override
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|this
operator|.
name|containerManager
operator|.
name|hasWriteLock
argument_list|()
argument_list|,
literal|"asserts "
operator|+
literal|"that we are holding the container manager lock when shutting down."
argument_list|)
expr_stmt|;
name|KeyUtils
operator|.
name|shutdownCache
argument_list|(
name|containerCache
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

