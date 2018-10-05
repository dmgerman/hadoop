begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.states.endpoint
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
name|states
operator|.
name|endpoint
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMVersionResponseProto
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
name|OzoneConsts
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
name|statemachine
operator|.
name|EndpointStateMachine
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
name|HddsVolumeUtil
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
name|volume
operator|.
name|HddsVolume
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
name|volume
operator|.
name|VolumeSet
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
name|ozoneimpl
operator|.
name|OzoneContainer
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
name|protocol
operator|.
name|VersionResponse
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
name|DiskChecker
operator|.
name|DiskOutOfSpaceException
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
name|Map
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

begin_comment
comment|/**  * Task that returns version.  */
end_comment

begin_class
DECL|class|VersionEndpointTask
specifier|public
class|class
name|VersionEndpointTask
implements|implements
name|Callable
argument_list|<
name|EndpointStateMachine
operator|.
name|EndPointStates
argument_list|>
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|VersionEndpointTask
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|rpcEndPoint
specifier|private
specifier|final
name|EndpointStateMachine
name|rpcEndPoint
decl_stmt|;
DECL|field|configuration
specifier|private
specifier|final
name|Configuration
name|configuration
decl_stmt|;
DECL|field|ozoneContainer
specifier|private
specifier|final
name|OzoneContainer
name|ozoneContainer
decl_stmt|;
DECL|method|VersionEndpointTask (EndpointStateMachine rpcEndPoint, Configuration conf, OzoneContainer container)
specifier|public
name|VersionEndpointTask
parameter_list|(
name|EndpointStateMachine
name|rpcEndPoint
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|OzoneContainer
name|container
parameter_list|)
block|{
name|this
operator|.
name|rpcEndPoint
operator|=
name|rpcEndPoint
expr_stmt|;
name|this
operator|.
name|configuration
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|ozoneContainer
operator|=
name|container
expr_stmt|;
block|}
comment|/**    * Computes a result, or throws an exception if unable to do so.    *    * @return computed result    * @throws Exception if unable to compute a result    */
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|EndpointStateMachine
operator|.
name|EndPointStates
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|rpcEndPoint
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|SCMVersionResponseProto
name|versionResponse
init|=
name|rpcEndPoint
operator|.
name|getEndPoint
argument_list|()
operator|.
name|getVersion
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|VersionResponse
name|response
init|=
name|VersionResponse
operator|.
name|getFromProtobuf
argument_list|(
name|versionResponse
argument_list|)
decl_stmt|;
name|rpcEndPoint
operator|.
name|setVersion
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|String
name|scmId
init|=
name|response
operator|.
name|getValue
argument_list|(
name|OzoneConsts
operator|.
name|SCM_ID
argument_list|)
decl_stmt|;
name|String
name|clusterId
init|=
name|response
operator|.
name|getValue
argument_list|(
name|OzoneConsts
operator|.
name|CLUSTER_ID
argument_list|)
decl_stmt|;
comment|// Check volumes
name|VolumeSet
name|volumeSet
init|=
name|ozoneContainer
operator|.
name|getVolumeSet
argument_list|()
decl_stmt|;
name|volumeSet
operator|.
name|readLock
argument_list|()
expr_stmt|;
try|try
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|HddsVolume
argument_list|>
name|volumeMap
init|=
name|volumeSet
operator|.
name|getVolumeMap
argument_list|()
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|scmId
argument_list|,
literal|"Reply from SCM: scmId cannot be "
operator|+
literal|"null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|clusterId
argument_list|,
literal|"Reply from SCM: clusterId "
operator|+
literal|"cannot be null"
argument_list|)
expr_stmt|;
comment|// If version file does not exist create version file and also set scmId
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|HddsVolume
argument_list|>
name|entry
range|:
name|volumeMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|HddsVolume
name|hddsVolume
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|boolean
name|result
init|=
name|HddsVolumeUtil
operator|.
name|checkVolume
argument_list|(
name|hddsVolume
argument_list|,
name|scmId
argument_list|,
name|clusterId
argument_list|,
name|LOG
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|result
condition|)
block|{
name|volumeSet
operator|.
name|failVolume
argument_list|(
name|hddsVolume
operator|.
name|getHddsRootDir
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|volumeSet
operator|.
name|getVolumesList
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// All volumes are inconsistent state
throw|throw
operator|new
name|DiskOutOfSpaceException
argument_list|(
literal|"All configured Volumes are in "
operator|+
literal|"Inconsistent State"
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
name|volumeSet
operator|.
name|readUnlock
argument_list|()
expr_stmt|;
block|}
name|ozoneContainer
operator|.
name|getDispatcher
argument_list|()
operator|.
name|setScmId
argument_list|(
name|scmId
argument_list|)
expr_stmt|;
name|EndpointStateMachine
operator|.
name|EndPointStates
name|nextState
init|=
name|rpcEndPoint
operator|.
name|getState
argument_list|()
operator|.
name|getNextState
argument_list|()
decl_stmt|;
name|rpcEndPoint
operator|.
name|setState
argument_list|(
name|nextState
argument_list|)
expr_stmt|;
name|rpcEndPoint
operator|.
name|zeroMissedCount
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DiskOutOfSpaceException
name|ex
parameter_list|)
block|{
name|rpcEndPoint
operator|.
name|setState
argument_list|(
name|EndpointStateMachine
operator|.
name|EndPointStates
operator|.
name|SHUTDOWN
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|rpcEndPoint
operator|.
name|logIfNeeded
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|rpcEndPoint
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
return|return
name|rpcEndPoint
operator|.
name|getState
argument_list|()
return|;
block|}
block|}
end_class

end_unit

