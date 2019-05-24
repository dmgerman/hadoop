begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.response
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
operator|.
name|response
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
name|om
operator|.
name|OMMetadataManager
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
name|OzoneManagerProtocolProtos
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
name|db
operator|.
name|BatchOperation
import|;
end_import

begin_comment
comment|/**  * Response for CreateVolume request.  */
end_comment

begin_class
DECL|class|OMVolumeDeleteResponse
specifier|public
class|class
name|OMVolumeDeleteResponse
implements|implements
name|OMClientResponse
block|{
DECL|field|volume
specifier|private
name|String
name|volume
decl_stmt|;
DECL|field|owner
specifier|private
name|String
name|owner
decl_stmt|;
DECL|field|updatedVolumeList
specifier|private
name|OzoneManagerProtocolProtos
operator|.
name|VolumeList
name|updatedVolumeList
decl_stmt|;
DECL|method|OMVolumeDeleteResponse (String volume, String owner, OzoneManagerProtocolProtos.VolumeList updatedVolumeList)
specifier|public
name|OMVolumeDeleteResponse
parameter_list|(
name|String
name|volume
parameter_list|,
name|String
name|owner
parameter_list|,
name|OzoneManagerProtocolProtos
operator|.
name|VolumeList
name|updatedVolumeList
parameter_list|)
block|{
name|this
operator|.
name|volume
operator|=
name|volume
expr_stmt|;
name|this
operator|.
name|owner
operator|=
name|owner
expr_stmt|;
name|this
operator|.
name|updatedVolumeList
operator|=
name|updatedVolumeList
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addToDBBatch (OMMetadataManager omMetadataManager, BatchOperation batchOperation)
specifier|public
name|void
name|addToDBBatch
parameter_list|(
name|OMMetadataManager
name|omMetadataManager
parameter_list|,
name|BatchOperation
name|batchOperation
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|dbUserKey
init|=
name|omMetadataManager
operator|.
name|getUserKey
argument_list|(
name|owner
argument_list|)
decl_stmt|;
name|OzoneManagerProtocolProtos
operator|.
name|VolumeList
name|volumeList
init|=
name|updatedVolumeList
decl_stmt|;
if|if
condition|(
name|updatedVolumeList
operator|.
name|getVolumeNamesList
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|omMetadataManager
operator|.
name|getUserTable
argument_list|()
operator|.
name|deleteWithBatch
argument_list|(
name|batchOperation
argument_list|,
name|dbUserKey
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|omMetadataManager
operator|.
name|getUserTable
argument_list|()
operator|.
name|putWithBatch
argument_list|(
name|batchOperation
argument_list|,
name|dbUserKey
argument_list|,
name|volumeList
argument_list|)
expr_stmt|;
block|}
name|omMetadataManager
operator|.
name|getVolumeTable
argument_list|()
operator|.
name|deleteWithBatch
argument_list|(
name|batchOperation
argument_list|,
name|omMetadataManager
operator|.
name|getVolumeKey
argument_list|(
name|volume
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

