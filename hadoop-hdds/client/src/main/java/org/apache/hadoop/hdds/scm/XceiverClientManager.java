begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm
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
name|annotations
operator|.
name|VisibleForTesting
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
name|hdds
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|Callable
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
import|import static
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
name|ScmConfigKeys
operator|.
name|SCM_CONTAINER_CLIENT_MAX_SIZE_DEFAULT
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
name|hdds
operator|.
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|SCM_CONTAINER_CLIENT_MAX_SIZE_KEY
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
name|hdds
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
name|hdds
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
implements|implements
name|Closeable
block|{
comment|//TODO : change this to SCM configuration class
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|clientCache
specifier|private
specifier|final
name|Cache
argument_list|<
name|Long
argument_list|,
name|XceiverClientSpi
argument_list|>
name|clientCache
decl_stmt|;
DECL|field|useRatis
specifier|private
specifier|final
name|boolean
name|useRatis
decl_stmt|;
DECL|field|metrics
specifier|private
specifier|static
name|XceiverClientMetrics
name|metrics
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
name|int
name|maxSize
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|SCM_CONTAINER_CLIENT_MAX_SIZE_KEY
argument_list|,
name|SCM_CONTAINER_CLIENT_MAX_SIZE_DEFAULT
argument_list|)
decl_stmt|;
name|long
name|staleThresholdMs
init|=
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
decl_stmt|;
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
name|clientCache
operator|=
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|expireAfterAccess
argument_list|(
name|staleThresholdMs
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|.
name|maximumSize
argument_list|(
name|maxSize
argument_list|)
operator|.
name|removalListener
argument_list|(
operator|new
name|RemovalListener
argument_list|<
name|Long
argument_list|,
name|XceiverClientSpi
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
name|Long
argument_list|,
name|XceiverClientSpi
argument_list|>
name|removalNotification
parameter_list|)
block|{
synchronized|synchronized
init|(
name|clientCache
init|)
block|{
comment|// Mark the entry as evicted
name|XceiverClientSpi
name|info
init|=
name|removalNotification
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|info
operator|.
name|setEvicted
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getClientCache ()
specifier|public
name|Cache
argument_list|<
name|Long
argument_list|,
name|XceiverClientSpi
argument_list|>
name|getClientCache
parameter_list|()
block|{
return|return
name|clientCache
return|;
block|}
comment|/**    * Acquires a XceiverClientSpi connected to a container capable of    * storing the specified key.    *    * If there is already a cached XceiverClientSpi, simply return    * the cached otherwise create a new one.    *    * @param pipeline the container pipeline for the client connection    * @return XceiverClientSpi connected to a container    * @throws IOException if a XceiverClientSpi cannot be acquired    */
DECL|method|acquireClient (Pipeline pipeline, long containerID)
specifier|public
name|XceiverClientSpi
name|acquireClient
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|,
name|long
name|containerID
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
synchronized|synchronized
init|(
name|clientCache
init|)
block|{
name|XceiverClientSpi
name|info
init|=
name|getClient
argument_list|(
name|pipeline
argument_list|,
name|containerID
argument_list|)
decl_stmt|;
name|info
operator|.
name|incrementReference
argument_list|()
expr_stmt|;
return|return
name|info
return|;
block|}
block|}
comment|/**    * Releases a XceiverClientSpi after use.    *    * @param client client to release    */
DECL|method|releaseClient (XceiverClientSpi client)
specifier|public
name|void
name|releaseClient
parameter_list|(
name|XceiverClientSpi
name|client
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|client
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|clientCache
init|)
block|{
name|client
operator|.
name|decrementReference
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getClient (Pipeline pipeline, long containerID)
specifier|private
name|XceiverClientSpi
name|getClient
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|,
name|long
name|containerID
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|clientCache
operator|.
name|get
argument_list|(
name|containerID
argument_list|,
operator|new
name|Callable
argument_list|<
name|XceiverClientSpi
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|XceiverClientSpi
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|XceiverClientSpi
name|client
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|pipeline
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|RATIS
case|:
name|client
operator|=
name|XceiverClientRatis
operator|.
name|newXceiverClientRatis
argument_list|(
name|pipeline
argument_list|,
name|conf
argument_list|)
expr_stmt|;
break|break;
case|case
name|STAND_ALONE
case|:
name|client
operator|=
operator|new
name|XceiverClientGrpc
argument_list|(
name|pipeline
argument_list|,
name|conf
argument_list|)
expr_stmt|;
break|break;
case|case
name|CHAINED
case|:
default|default:
throw|throw
operator|new
name|IOException
argument_list|(
literal|"not implemented"
operator|+
name|pipeline
operator|.
name|getType
argument_list|()
argument_list|)
throw|;
block|}
name|client
operator|.
name|connect
argument_list|()
expr_stmt|;
return|return
name|client
return|;
block|}
block|}
argument_list|)
return|;
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
literal|"Exception getting XceiverClient: "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Close and remove all the cached clients.    */
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|//closing is done through RemovalListener
name|clientCache
operator|.
name|invalidateAll
argument_list|()
expr_stmt|;
name|clientCache
operator|.
name|cleanUp
argument_list|()
expr_stmt|;
if|if
condition|(
name|metrics
operator|!=
literal|null
condition|)
block|{
name|metrics
operator|.
name|unRegister
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Tells us if Ratis is enabled for this cluster.    * @return True if Ratis is enabled.    */
DECL|method|isUseRatis ()
specifier|public
name|boolean
name|isUseRatis
parameter_list|()
block|{
return|return
name|useRatis
return|;
block|}
comment|/**    * Returns hard coded 3 as replication factor.    * @return 3    */
DECL|method|getFactor ()
specifier|public
name|HddsProtos
operator|.
name|ReplicationFactor
name|getFactor
parameter_list|()
block|{
if|if
condition|(
name|isUseRatis
argument_list|()
condition|)
block|{
return|return
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|THREE
return|;
block|}
return|return
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|ONE
return|;
block|}
comment|/**    * Returns the default replication type.    * @return Ratis or Standalone    */
DECL|method|getType ()
specifier|public
name|HddsProtos
operator|.
name|ReplicationType
name|getType
parameter_list|()
block|{
comment|// TODO : Fix me and make Ratis default before release.
comment|// TODO: Remove this as replication factor and type are pipeline properties
if|if
condition|(
name|isUseRatis
argument_list|()
condition|)
block|{
return|return
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|RATIS
return|;
block|}
return|return
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|STAND_ALONE
return|;
block|}
comment|/**    * Get xceiver client metric.    */
DECL|method|getXceiverClientMetrics ()
specifier|public
specifier|synchronized
specifier|static
name|XceiverClientMetrics
name|getXceiverClientMetrics
parameter_list|()
block|{
if|if
condition|(
name|metrics
operator|==
literal|null
condition|)
block|{
name|metrics
operator|=
name|XceiverClientMetrics
operator|.
name|create
argument_list|()
expr_stmt|;
block|}
return|return
name|metrics
return|;
block|}
block|}
end_class

end_unit

