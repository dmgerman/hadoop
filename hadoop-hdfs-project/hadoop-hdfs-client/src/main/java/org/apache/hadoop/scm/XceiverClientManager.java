begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.scm
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|scm
package|;
end_package

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
name|TimeUnit
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
name|AtomicInteger
import|;
end_import

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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|Cache
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|CacheBuilder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|RemovalListener
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|RemovalNotification
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|SCM_CONTAINER_CLIENT_STALE_THRESHOLD_DEFAULT
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
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|SCM_CONTAINER_CLIENT_STALE_THRESHOLD_KEY
import|;
end_import

begin_comment
comment|/**  * XceiverClientManager is responsible for the lifecycle of XceiverClient  * instances.  Callers use this class to acquire an XceiverClient instance  * connected to the desired container pipeline.  When done, the caller also uses  * this class to release the previously acquired XceiverClient instance.  *  *  * This class caches connection to container for reuse purpose, such that  * accessing same container frequently will be through the same connection  * without reestablishing connection. But the connection will be closed if  * not being used for a period of time.  */
end_comment

begin_class
DECL|class|XceiverClientManager
specifier|public
class|class
name|XceiverClientManager
block|{
comment|//TODO : change this to SCM configuration class
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|openClient
specifier|private
name|Cache
argument_list|<
name|String
argument_list|,
name|XceiverClientWithAccessInfo
argument_list|>
name|openClient
decl_stmt|;
DECL|field|staleThresholdMs
specifier|private
specifier|final
name|long
name|staleThresholdMs
decl_stmt|;
DECL|field|useRatis
specifier|private
specifier|final
name|boolean
name|useRatis
decl_stmt|;
comment|/**    * Creates a new XceiverClientManager.    *    * @param conf configuration    */
DECL|method|XceiverClientManager (Configuration conf)
specifier|public
name|XceiverClientManager
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|staleThresholdMs
operator|=
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|SCM_CONTAINER_CLIENT_STALE_THRESHOLD_KEY
argument_list|,
name|SCM_CONTAINER_CLIENT_STALE_THRESHOLD_DEFAULT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|this
operator|.
name|useRatis
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_ENABLED_KEY
argument_list|,
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_ENABLED_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|openClient
operator|=
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|expireAfterAccess
argument_list|(
name|this
operator|.
name|staleThresholdMs
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|.
name|removalListener
argument_list|(
operator|new
name|RemovalListener
argument_list|<
name|String
argument_list|,
name|XceiverClientWithAccessInfo
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onRemoval
parameter_list|(
name|RemovalNotification
argument_list|<
name|String
argument_list|,
name|XceiverClientWithAccessInfo
argument_list|>
name|removalNotification
parameter_list|)
block|{
comment|// If the reference count is not 0, this xceiver client should not
comment|// be evicted, add it back to the cache.
name|XceiverClientWithAccessInfo
name|info
init|=
name|removalNotification
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|info
operator|.
name|hasRefence
argument_list|()
condition|)
block|{
synchronized|synchronized
init|(
name|XceiverClientManager
operator|.
name|this
operator|.
name|openClient
init|)
block|{
name|XceiverClientManager
operator|.
name|this
operator|.
name|openClient
operator|.
name|put
argument_list|(
name|removalNotification
operator|.
name|getKey
argument_list|()
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
comment|/**    * Acquires a XceiverClient connected to a container capable of storing the    * specified key.    *    * If there is already a cached XceiverClient, simply return the cached    * otherwise create a new one.    *    * @param pipeline the container pipeline for the client connection    * @return XceiverClient connected to a container    * @throws IOException if an XceiverClient cannot be acquired    */
DECL|method|acquireClient (Pipeline pipeline)
specifier|public
name|XceiverClientSpi
name|acquireClient
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|pipeline
operator|.
name|getMachines
argument_list|()
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
operator|!
name|pipeline
operator|.
name|getMachines
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|containerName
init|=
name|pipeline
operator|.
name|getContainerName
argument_list|()
decl_stmt|;
name|XceiverClientWithAccessInfo
name|info
init|=
name|openClient
operator|.
name|getIfPresent
argument_list|(
name|containerName
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
comment|// we do have this connection, add reference and return
name|info
operator|.
name|incrementReference
argument_list|()
expr_stmt|;
return|return
name|info
operator|.
name|getXceiverClient
argument_list|()
return|;
block|}
else|else
block|{
comment|// connection not found, create new, add reference and return
specifier|final
name|XceiverClientSpi
name|xceiverClient
init|=
name|useRatis
condition|?
name|XceiverClientRatis
operator|.
name|newXceiverClientRatis
argument_list|(
name|pipeline
argument_list|,
name|conf
argument_list|)
else|:
operator|new
name|XceiverClient
argument_list|(
name|pipeline
argument_list|,
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|xceiverClient
operator|.
name|connect
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Exception connecting XceiverClient."
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|info
operator|=
operator|new
name|XceiverClientWithAccessInfo
argument_list|(
name|xceiverClient
argument_list|)
expr_stmt|;
name|info
operator|.
name|incrementReference
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|openClient
init|)
block|{
name|openClient
operator|.
name|put
argument_list|(
name|containerName
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
return|return
name|xceiverClient
return|;
block|}
block|}
comment|/**    * Releases an XceiverClient after use.    *    * @param xceiverClient client to release    */
DECL|method|releaseClient (XceiverClientSpi xceiverClient)
specifier|public
name|void
name|releaseClient
parameter_list|(
name|XceiverClientSpi
name|xceiverClient
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|xceiverClient
argument_list|)
expr_stmt|;
name|String
name|containerName
init|=
name|xceiverClient
operator|.
name|getPipeline
argument_list|()
operator|.
name|getContainerName
argument_list|()
decl_stmt|;
name|XceiverClientWithAccessInfo
name|info
decl_stmt|;
synchronized|synchronized
init|(
name|openClient
init|)
block|{
name|info
operator|=
name|openClient
operator|.
name|getIfPresent
argument_list|(
name|containerName
argument_list|)
expr_stmt|;
block|}
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|info
operator|.
name|decrementReference
argument_list|()
expr_stmt|;
block|}
comment|/**    * A helper class for caching and cleaning XceiverClient. Three parameters:    * - the actual XceiverClient object    * - a time stamp representing the most recent access (acquire or release)    * - a reference count, +1 when acquire, -1 when release    */
DECL|class|XceiverClientWithAccessInfo
specifier|private
specifier|static
class|class
name|XceiverClientWithAccessInfo
block|{
DECL|field|xceiverClient
specifier|final
specifier|private
name|XceiverClientSpi
name|xceiverClient
decl_stmt|;
DECL|field|referenceCount
specifier|final
specifier|private
name|AtomicInteger
name|referenceCount
decl_stmt|;
DECL|method|XceiverClientWithAccessInfo (XceiverClientSpi xceiverClient)
name|XceiverClientWithAccessInfo
parameter_list|(
name|XceiverClientSpi
name|xceiverClient
parameter_list|)
block|{
name|this
operator|.
name|xceiverClient
operator|=
name|xceiverClient
expr_stmt|;
name|this
operator|.
name|referenceCount
operator|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|incrementReference ()
name|void
name|incrementReference
parameter_list|()
block|{
name|this
operator|.
name|referenceCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
DECL|method|decrementReference ()
name|void
name|decrementReference
parameter_list|()
block|{
name|this
operator|.
name|referenceCount
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
block|}
DECL|method|hasRefence ()
name|boolean
name|hasRefence
parameter_list|()
block|{
return|return
name|this
operator|.
name|referenceCount
operator|.
name|get
argument_list|()
operator|!=
literal|0
return|;
block|}
DECL|method|getXceiverClient ()
name|XceiverClientSpi
name|getXceiverClient
parameter_list|()
block|{
return|return
name|xceiverClient
return|;
block|}
block|}
block|}
end_class

end_unit

