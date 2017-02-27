begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.protocolPB
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|protocolPB
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
name|Set
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
name|collect
operator|.
name|Sets
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|RpcController
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|ServiceException
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
name|classification
operator|.
name|InterfaceAudience
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
name|protocol
operator|.
name|DatanodeInfo
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
name|protocolPB
operator|.
name|PBHelperClient
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
name|LocatedContainer
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
name|StorageContainerLocationProtocol
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
name|proto
operator|.
name|StorageContainerLocationProtocolProtos
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
name|proto
operator|.
name|StorageContainerLocationProtocolProtos
operator|.
name|GetStorageContainerLocationsRequestProto
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
name|proto
operator|.
name|StorageContainerLocationProtocolProtos
operator|.
name|GetStorageContainerLocationsResponseProto
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
name|proto
operator|.
name|StorageContainerLocationProtocolProtos
operator|.
name|LocatedContainerProto
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
name|proto
operator|.
name|StorageContainerLocationProtocolProtos
operator|.
name|ContainerResponseProto
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

begin_comment
comment|/**  * This class is the server-side translator that forwards requests received on  * {@link StorageContainerLocationProtocolPB} to the  * {@link StorageContainerLocationProtocol} server implementation.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|StorageContainerLocationProtocolServerSideTranslatorPB
specifier|public
specifier|final
class|class
name|StorageContainerLocationProtocolServerSideTranslatorPB
implements|implements
name|StorageContainerLocationProtocolPB
block|{
DECL|field|impl
specifier|private
specifier|final
name|StorageContainerLocationProtocol
name|impl
decl_stmt|;
comment|/**    * Creates a new StorageContainerLocationProtocolServerSideTranslatorPB.    *    * @param impl {@link StorageContainerLocationProtocol} server implementation    */
DECL|method|StorageContainerLocationProtocolServerSideTranslatorPB ( StorageContainerLocationProtocol impl)
specifier|public
name|StorageContainerLocationProtocolServerSideTranslatorPB
parameter_list|(
name|StorageContainerLocationProtocol
name|impl
parameter_list|)
block|{
name|this
operator|.
name|impl
operator|=
name|impl
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getStorageContainerLocations ( RpcController unused, GetStorageContainerLocationsRequestProto req)
specifier|public
name|GetStorageContainerLocationsResponseProto
name|getStorageContainerLocations
parameter_list|(
name|RpcController
name|unused
parameter_list|,
name|GetStorageContainerLocationsRequestProto
name|req
parameter_list|)
throws|throws
name|ServiceException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|keys
init|=
name|Sets
operator|.
name|newLinkedHashSetWithExpectedSize
argument_list|(
name|req
operator|.
name|getKeysCount
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|req
operator|.
name|getKeysList
argument_list|()
control|)
block|{
name|keys
operator|.
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Set
argument_list|<
name|LocatedContainer
argument_list|>
name|locatedContainers
decl_stmt|;
try|try
block|{
name|locatedContainers
operator|=
name|impl
operator|.
name|getStorageContainerLocations
argument_list|(
name|keys
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|GetStorageContainerLocationsResponseProto
operator|.
name|Builder
name|resp
init|=
name|GetStorageContainerLocationsResponseProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|LocatedContainer
name|locatedContainer
range|:
name|locatedContainers
control|)
block|{
name|LocatedContainerProto
operator|.
name|Builder
name|locatedContainerProto
init|=
name|LocatedContainerProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setKey
argument_list|(
name|locatedContainer
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|setMatchedKeyPrefix
argument_list|(
name|locatedContainer
operator|.
name|getMatchedKeyPrefix
argument_list|()
argument_list|)
operator|.
name|setContainerName
argument_list|(
name|locatedContainer
operator|.
name|getContainerName
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|DatanodeInfo
name|location
range|:
name|locatedContainer
operator|.
name|getLocations
argument_list|()
control|)
block|{
name|locatedContainerProto
operator|.
name|addLocations
argument_list|(
name|PBHelperClient
operator|.
name|convert
argument_list|(
name|location
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|locatedContainerProto
operator|.
name|setLeader
argument_list|(
name|PBHelperClient
operator|.
name|convert
argument_list|(
name|locatedContainer
operator|.
name|getLeader
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|resp
operator|.
name|addLocatedContainers
argument_list|(
name|locatedContainerProto
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|resp
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|allocateContainer (RpcController unused, StorageContainerLocationProtocolProtos.ContainerRequestProto request)
specifier|public
name|ContainerResponseProto
name|allocateContainer
parameter_list|(
name|RpcController
name|unused
parameter_list|,
name|StorageContainerLocationProtocolProtos
operator|.
name|ContainerRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
name|Pipeline
name|pipeline
init|=
name|impl
operator|.
name|allocateContainer
argument_list|(
name|request
operator|.
name|getContainerName
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|ContainerResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setPipeline
argument_list|(
name|pipeline
operator|.
name|getProtobufMessage
argument_list|()
argument_list|)
operator|.
name|setErrorCode
argument_list|(
name|ContainerResponseProto
operator|.
name|Error
operator|.
name|success
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

