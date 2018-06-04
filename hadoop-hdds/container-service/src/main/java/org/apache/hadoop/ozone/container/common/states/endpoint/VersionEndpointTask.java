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
name|commons
operator|.
name|lang
operator|.
name|StringUtils
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
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|StorageLocation
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
name|common
operator|.
name|InconsistentStorageStateException
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
name|common
operator|.
name|Storage
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
name|DataNodeLayoutVersion
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
name|DatanodeVersionFile
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
name|Time
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
DECL|field|datanodeContainerManager
specifier|private
specifier|final
name|OzoneContainer
name|datanodeContainerManager
decl_stmt|;
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
name|VersionEndpointTask
operator|.
name|class
argument_list|)
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
name|datanodeContainerManager
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
name|String
name|scmUuid
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
name|Preconditions
operator|.
name|checkState
argument_list|(
operator|!
name|StringUtils
operator|.
name|isBlank
argument_list|(
name|scmUuid
argument_list|)
argument_list|,
literal|"Invalid SCM UuiD in the response."
argument_list|)
expr_stmt|;
name|rpcEndPoint
operator|.
name|setVersion
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"scmUuid is {}"
argument_list|,
name|scmUuid
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|StorageLocation
argument_list|>
name|locations
init|=
name|datanodeContainerManager
operator|.
name|getLocations
argument_list|()
decl_stmt|;
for|for
control|(
name|StorageLocation
name|location
range|:
name|locations
control|)
block|{
name|String
name|path
init|=
name|location
operator|.
name|getUri
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|File
name|parentPath
init|=
operator|new
name|File
argument_list|(
name|path
operator|+
name|File
operator|.
name|separator
operator|+
name|Storage
operator|.
name|STORAGE_DIR_HDDS
operator|+
name|File
operator|.
name|separator
operator|+
name|scmUuid
operator|+
name|File
operator|.
name|separator
operator|+
name|Storage
operator|.
name|STORAGE_DIR_CURRENT
argument_list|)
decl_stmt|;
name|File
name|versionFile
init|=
name|DatanodeVersionFile
operator|.
name|getVersionFile
argument_list|(
name|location
argument_list|,
name|scmUuid
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|parentPath
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|parentPath
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Directory doesn't exist and cannot be created. Path: {}"
argument_list|,
name|parentPath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
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
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Directory doesn't exist and "
operator|+
literal|"cannot be created. "
operator|+
name|parentPath
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
else|else
block|{
if|if
condition|(
name|versionFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|Properties
name|properties
init|=
name|DatanodeVersionFile
operator|.
name|readFrom
argument_list|(
name|versionFile
argument_list|)
decl_stmt|;
name|DatanodeVersionFile
operator|.
name|verifyScmUuid
argument_list|(
name|properties
operator|.
name|getProperty
argument_list|(
name|OzoneConsts
operator|.
name|SCM_ID
argument_list|)
argument_list|,
name|scmUuid
argument_list|)
expr_stmt|;
name|DatanodeVersionFile
operator|.
name|verifyCreationTime
argument_list|(
name|properties
operator|.
name|getProperty
argument_list|(
name|OzoneConsts
operator|.
name|CTIME
argument_list|)
argument_list|)
expr_stmt|;
name|DatanodeVersionFile
operator|.
name|verifyLayOutVersion
argument_list|(
name|properties
operator|.
name|getProperty
argument_list|(
name|OzoneConsts
operator|.
name|LAYOUTVERSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|DatanodeVersionFile
name|dnVersionFile
init|=
operator|new
name|DatanodeVersionFile
argument_list|(
name|scmUuid
argument_list|,
name|Time
operator|.
name|now
argument_list|()
argument_list|,
name|DataNodeLayoutVersion
operator|.
name|getLatestVersion
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
decl_stmt|;
name|dnVersionFile
operator|.
name|createVersionFile
argument_list|(
name|versionFile
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
name|InconsistentStorageStateException
name|ex
parameter_list|)
block|{
throw|throw
name|ex
throw|;
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

