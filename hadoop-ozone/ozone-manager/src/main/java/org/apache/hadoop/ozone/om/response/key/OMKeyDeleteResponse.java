begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.response.key
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
operator|.
name|key
package|;
end_package

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
name|OmUtils
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
name|om
operator|.
name|helpers
operator|.
name|OmKeyInfo
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
name|helpers
operator|.
name|OmKeyLocationInfoGroup
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
name|response
operator|.
name|OMClientResponse
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|OzoneManagerProtocolProtos
operator|.
name|OMResponse
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
name|db
operator|.
name|BatchOperation
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

begin_comment
comment|/**  * Response for DeleteKey request.  */
end_comment

begin_class
DECL|class|OMKeyDeleteResponse
specifier|public
class|class
name|OMKeyDeleteResponse
extends|extends
name|OMClientResponse
block|{
DECL|field|omKeyInfo
specifier|private
name|OmKeyInfo
name|omKeyInfo
decl_stmt|;
DECL|field|deleteTimestamp
specifier|private
name|long
name|deleteTimestamp
decl_stmt|;
DECL|method|OMKeyDeleteResponse (OmKeyInfo omKeyInfo, long deletionTime, OMResponse omResponse)
specifier|public
name|OMKeyDeleteResponse
parameter_list|(
name|OmKeyInfo
name|omKeyInfo
parameter_list|,
name|long
name|deletionTime
parameter_list|,
name|OMResponse
name|omResponse
parameter_list|)
block|{
name|super
argument_list|(
name|omResponse
argument_list|)
expr_stmt|;
name|this
operator|.
name|omKeyInfo
operator|=
name|omKeyInfo
expr_stmt|;
name|this
operator|.
name|deleteTimestamp
operator|=
name|deletionTime
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
comment|// For OmResponse with failure, this should do nothing. This method is
comment|// not called in failure scenario in OM code.
if|if
condition|(
name|getOMResponse
argument_list|()
operator|.
name|getStatus
argument_list|()
operator|==
name|OzoneManagerProtocolProtos
operator|.
name|Status
operator|.
name|OK
condition|)
block|{
name|String
name|ozoneKey
init|=
name|omMetadataManager
operator|.
name|getOzoneKey
argument_list|(
name|omKeyInfo
operator|.
name|getVolumeName
argument_list|()
argument_list|,
name|omKeyInfo
operator|.
name|getBucketName
argument_list|()
argument_list|,
name|omKeyInfo
operator|.
name|getKeyName
argument_list|()
argument_list|)
decl_stmt|;
name|omMetadataManager
operator|.
name|getKeyTable
argument_list|()
operator|.
name|deleteWithBatch
argument_list|(
name|batchOperation
argument_list|,
name|ozoneKey
argument_list|)
expr_stmt|;
comment|// If Key is not empty add this to delete table.
if|if
condition|(
operator|!
name|isKeyEmpty
argument_list|(
name|omKeyInfo
argument_list|)
condition|)
block|{
comment|// If a deleted key is put in the table where a key with the same
comment|// name already exists, then the old deleted key information would be
comment|// lost. To differentiate between keys with same name in
comment|// deletedTable, we add the timestamp to the key name.
name|String
name|deleteKeyName
init|=
name|OmUtils
operator|.
name|getDeletedKeyName
argument_list|(
name|ozoneKey
argument_list|,
name|deleteTimestamp
argument_list|)
decl_stmt|;
name|omMetadataManager
operator|.
name|getDeletedTable
argument_list|()
operator|.
name|putWithBatch
argument_list|(
name|batchOperation
argument_list|,
name|deleteKeyName
argument_list|,
name|omKeyInfo
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Check if the key is empty or not. Key will be empty if it does not have    * blocks.    * @param keyInfo    * @return if empty true, else false.    */
DECL|method|isKeyEmpty (OmKeyInfo keyInfo)
specifier|private
name|boolean
name|isKeyEmpty
parameter_list|(
name|OmKeyInfo
name|keyInfo
parameter_list|)
block|{
for|for
control|(
name|OmKeyLocationInfoGroup
name|keyLocationList
range|:
name|keyInfo
operator|.
name|getKeyLocationVersions
argument_list|()
control|)
block|{
if|if
condition|(
name|keyLocationList
operator|.
name|getLocationList
argument_list|()
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

