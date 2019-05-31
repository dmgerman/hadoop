begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.csi
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|csi
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|client
operator|.
name|OzoneClient
import|;
end_import

begin_import
import|import
name|csi
operator|.
name|v1
operator|.
name|ControllerGrpc
operator|.
name|ControllerImplBase
import|;
end_import

begin_import
import|import
name|csi
operator|.
name|v1
operator|.
name|Csi
operator|.
name|CapacityRange
import|;
end_import

begin_import
import|import
name|csi
operator|.
name|v1
operator|.
name|Csi
operator|.
name|ControllerGetCapabilitiesRequest
import|;
end_import

begin_import
import|import
name|csi
operator|.
name|v1
operator|.
name|Csi
operator|.
name|ControllerGetCapabilitiesResponse
import|;
end_import

begin_import
import|import
name|csi
operator|.
name|v1
operator|.
name|Csi
operator|.
name|ControllerServiceCapability
import|;
end_import

begin_import
import|import
name|csi
operator|.
name|v1
operator|.
name|Csi
operator|.
name|ControllerServiceCapability
operator|.
name|RPC
import|;
end_import

begin_import
import|import
name|csi
operator|.
name|v1
operator|.
name|Csi
operator|.
name|ControllerServiceCapability
operator|.
name|RPC
operator|.
name|Type
import|;
end_import

begin_import
import|import
name|csi
operator|.
name|v1
operator|.
name|Csi
operator|.
name|CreateVolumeRequest
import|;
end_import

begin_import
import|import
name|csi
operator|.
name|v1
operator|.
name|Csi
operator|.
name|CreateVolumeResponse
import|;
end_import

begin_import
import|import
name|csi
operator|.
name|v1
operator|.
name|Csi
operator|.
name|DeleteVolumeRequest
import|;
end_import

begin_import
import|import
name|csi
operator|.
name|v1
operator|.
name|Csi
operator|.
name|DeleteVolumeResponse
import|;
end_import

begin_import
import|import
name|csi
operator|.
name|v1
operator|.
name|Csi
operator|.
name|Volume
import|;
end_import

begin_import
import|import
name|io
operator|.
name|grpc
operator|.
name|stub
operator|.
name|StreamObserver
import|;
end_import

begin_comment
comment|/**  * CSI controller service.  *<p>  * This service usually runs only once and responsible for the creation of  * the volume.  */
end_comment

begin_class
DECL|class|ControllerService
specifier|public
class|class
name|ControllerService
extends|extends
name|ControllerImplBase
block|{
DECL|field|volumeOwner
specifier|private
specifier|final
name|String
name|volumeOwner
decl_stmt|;
DECL|field|defaultVolumeSize
specifier|private
name|long
name|defaultVolumeSize
decl_stmt|;
DECL|field|ozoneClient
specifier|private
name|OzoneClient
name|ozoneClient
decl_stmt|;
DECL|method|ControllerService (OzoneClient ozoneClient, long volumeSize, String volumeOwner)
specifier|public
name|ControllerService
parameter_list|(
name|OzoneClient
name|ozoneClient
parameter_list|,
name|long
name|volumeSize
parameter_list|,
name|String
name|volumeOwner
parameter_list|)
block|{
name|this
operator|.
name|volumeOwner
operator|=
name|volumeOwner
expr_stmt|;
name|this
operator|.
name|defaultVolumeSize
operator|=
name|volumeSize
expr_stmt|;
name|this
operator|.
name|ozoneClient
operator|=
name|ozoneClient
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createVolume (CreateVolumeRequest request, StreamObserver<CreateVolumeResponse> responseObserver)
specifier|public
name|void
name|createVolume
parameter_list|(
name|CreateVolumeRequest
name|request
parameter_list|,
name|StreamObserver
argument_list|<
name|CreateVolumeResponse
argument_list|>
name|responseObserver
parameter_list|)
block|{
try|try
block|{
name|ozoneClient
operator|.
name|getObjectStore
argument_list|()
operator|.
name|createS3Bucket
argument_list|(
name|volumeOwner
argument_list|,
name|request
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|size
init|=
name|findSize
argument_list|(
name|request
operator|.
name|getCapacityRange
argument_list|()
argument_list|)
decl_stmt|;
name|CreateVolumeResponse
name|response
init|=
name|CreateVolumeResponse
operator|.
name|newBuilder
argument_list|()
operator|.
name|setVolume
argument_list|(
name|Volume
operator|.
name|newBuilder
argument_list|()
operator|.
name|setVolumeId
argument_list|(
name|request
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|setCapacityBytes
argument_list|(
name|size
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|responseObserver
operator|.
name|onNext
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|responseObserver
operator|.
name|onCompleted
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|responseObserver
operator|.
name|onError
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|findSize (CapacityRange capacityRange)
specifier|private
name|long
name|findSize
parameter_list|(
name|CapacityRange
name|capacityRange
parameter_list|)
block|{
if|if
condition|(
name|capacityRange
operator|.
name|getRequiredBytes
argument_list|()
operator|!=
literal|0
condition|)
block|{
return|return
name|capacityRange
operator|.
name|getRequiredBytes
argument_list|()
return|;
block|}
else|else
block|{
if|if
condition|(
name|capacityRange
operator|.
name|getLimitBytes
argument_list|()
operator|!=
literal|0
condition|)
block|{
return|return
name|Math
operator|.
name|min
argument_list|(
name|defaultVolumeSize
argument_list|,
name|capacityRange
operator|.
name|getLimitBytes
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
comment|//~1 gig
return|return
name|defaultVolumeSize
return|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|deleteVolume (DeleteVolumeRequest request, StreamObserver<DeleteVolumeResponse> responseObserver)
specifier|public
name|void
name|deleteVolume
parameter_list|(
name|DeleteVolumeRequest
name|request
parameter_list|,
name|StreamObserver
argument_list|<
name|DeleteVolumeResponse
argument_list|>
name|responseObserver
parameter_list|)
block|{
try|try
block|{
name|ozoneClient
operator|.
name|getObjectStore
argument_list|()
operator|.
name|deleteS3Bucket
argument_list|(
name|request
operator|.
name|getVolumeId
argument_list|()
argument_list|)
expr_stmt|;
name|DeleteVolumeResponse
name|response
init|=
name|DeleteVolumeResponse
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
name|responseObserver
operator|.
name|onNext
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|responseObserver
operator|.
name|onCompleted
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|responseObserver
operator|.
name|onError
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|controllerGetCapabilities ( ControllerGetCapabilitiesRequest request, StreamObserver<ControllerGetCapabilitiesResponse> responseObserver)
specifier|public
name|void
name|controllerGetCapabilities
parameter_list|(
name|ControllerGetCapabilitiesRequest
name|request
parameter_list|,
name|StreamObserver
argument_list|<
name|ControllerGetCapabilitiesResponse
argument_list|>
name|responseObserver
parameter_list|)
block|{
name|ControllerGetCapabilitiesResponse
name|response
init|=
name|ControllerGetCapabilitiesResponse
operator|.
name|newBuilder
argument_list|()
operator|.
name|addCapabilities
argument_list|(
name|ControllerServiceCapability
operator|.
name|newBuilder
argument_list|()
operator|.
name|setRpc
argument_list|(
name|RPC
operator|.
name|newBuilder
argument_list|()
operator|.
name|setType
argument_list|(
name|Type
operator|.
name|CREATE_DELETE_VOLUME
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|responseObserver
operator|.
name|onNext
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|responseObserver
operator|.
name|onCompleted
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

