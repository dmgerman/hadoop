begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.request.key
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
name|request
operator|.
name|key
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|request
operator|.
name|TestOMRequestUtils
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
name|OMRequest
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
name|KeyArgs
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
name|RenameKeyRequest
import|;
end_import

begin_comment
comment|/**  * Tests RenameKey request.  */
end_comment

begin_class
DECL|class|TestOMKeyRenameRequest
specifier|public
class|class
name|TestOMKeyRenameRequest
extends|extends
name|TestOMKeyRequest
block|{
annotation|@
name|Test
DECL|method|testPreExecute ()
specifier|public
name|void
name|testPreExecute
parameter_list|()
throws|throws
name|Exception
block|{
name|doPreExecute
argument_list|(
name|createRenameKeyRequest
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testValidateAndUpdateCache ()
specifier|public
name|void
name|testValidateAndUpdateCache
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|toKeyName
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|OMRequest
name|modifiedOmRequest
init|=
name|doPreExecute
argument_list|(
name|createRenameKeyRequest
argument_list|(
name|toKeyName
argument_list|)
argument_list|)
decl_stmt|;
name|TestOMRequestUtils
operator|.
name|addKeyToTable
argument_list|(
literal|false
argument_list|,
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|,
name|clientID
argument_list|,
name|replicationType
argument_list|,
name|replicationFactor
argument_list|,
name|omMetadataManager
argument_list|)
expr_stmt|;
name|OMKeyRenameRequest
name|omKeyRenameRequest
init|=
operator|new
name|OMKeyRenameRequest
argument_list|(
name|modifiedOmRequest
argument_list|)
decl_stmt|;
name|OMClientResponse
name|omKeyRenameResponse
init|=
name|omKeyRenameRequest
operator|.
name|validateAndUpdateCache
argument_list|(
name|ozoneManager
argument_list|,
literal|100L
argument_list|,
name|ozoneManagerDoubleBufferHelper
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|OzoneManagerProtocolProtos
operator|.
name|Status
operator|.
name|OK
argument_list|,
name|omKeyRenameResponse
operator|.
name|getOMResponse
argument_list|()
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|key
init|=
name|omMetadataManager
operator|.
name|getOzoneKey
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|)
decl_stmt|;
comment|// Original key should be deleted, toKey should exist.
name|OmKeyInfo
name|omKeyInfo
init|=
name|omMetadataManager
operator|.
name|getKeyTable
argument_list|()
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|omKeyInfo
argument_list|)
expr_stmt|;
name|omKeyInfo
operator|=
name|omMetadataManager
operator|.
name|getKeyTable
argument_list|()
operator|.
name|get
argument_list|(
name|omMetadataManager
operator|.
name|getOzoneKey
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|toKeyName
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|omKeyInfo
argument_list|)
expr_stmt|;
comment|// For new key modification time should be updated.
name|KeyArgs
name|keyArgs
init|=
name|modifiedOmRequest
operator|.
name|getRenameKeyRequest
argument_list|()
operator|.
name|getKeyArgs
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|keyArgs
operator|.
name|getModificationTime
argument_list|()
argument_list|,
name|omKeyInfo
operator|.
name|getModificationTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// KeyName should be updated in OmKeyInfo to toKeyName.
name|Assert
operator|.
name|assertEquals
argument_list|(
name|omKeyInfo
operator|.
name|getKeyName
argument_list|()
argument_list|,
name|toKeyName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testValidateAndUpdateCacheWithKeyNotFound ()
specifier|public
name|void
name|testValidateAndUpdateCacheWithKeyNotFound
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|toKeyName
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|OMRequest
name|modifiedOmRequest
init|=
name|doPreExecute
argument_list|(
name|createRenameKeyRequest
argument_list|(
name|toKeyName
argument_list|)
argument_list|)
decl_stmt|;
comment|// Add only volume and bucket entry to DB.
comment|// In actual implementation we don't check for bucket/volume exists
comment|// during delete key.
name|TestOMRequestUtils
operator|.
name|addVolumeAndBucketToDB
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|omMetadataManager
argument_list|)
expr_stmt|;
name|OMKeyRenameRequest
name|omKeyRenameRequest
init|=
operator|new
name|OMKeyRenameRequest
argument_list|(
name|modifiedOmRequest
argument_list|)
decl_stmt|;
name|OMClientResponse
name|omKeyRenameResponse
init|=
name|omKeyRenameRequest
operator|.
name|validateAndUpdateCache
argument_list|(
name|ozoneManager
argument_list|,
literal|100L
argument_list|,
name|ozoneManagerDoubleBufferHelper
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|OzoneManagerProtocolProtos
operator|.
name|Status
operator|.
name|KEY_NOT_FOUND
argument_list|,
name|omKeyRenameResponse
operator|.
name|getOMResponse
argument_list|()
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testValidateAndUpdateCacheWithOutVolumeAndBucket ()
specifier|public
name|void
name|testValidateAndUpdateCacheWithOutVolumeAndBucket
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|toKeyName
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|OMRequest
name|modifiedOmRequest
init|=
name|doPreExecute
argument_list|(
name|createRenameKeyRequest
argument_list|(
name|toKeyName
argument_list|)
argument_list|)
decl_stmt|;
comment|// In actual implementation we don't check for bucket/volume exists
comment|// during delete key. So it should still return error KEY_NOT_FOUND
name|OMKeyRenameRequest
name|omKeyRenameRequest
init|=
operator|new
name|OMKeyRenameRequest
argument_list|(
name|modifiedOmRequest
argument_list|)
decl_stmt|;
name|OMClientResponse
name|omKeyRenameResponse
init|=
name|omKeyRenameRequest
operator|.
name|validateAndUpdateCache
argument_list|(
name|ozoneManager
argument_list|,
literal|100L
argument_list|,
name|ozoneManagerDoubleBufferHelper
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|OzoneManagerProtocolProtos
operator|.
name|Status
operator|.
name|KEY_NOT_FOUND
argument_list|,
name|omKeyRenameResponse
operator|.
name|getOMResponse
argument_list|()
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testValidateAndUpdateCacheWithToKeyInvalid ()
specifier|public
name|void
name|testValidateAndUpdateCacheWithToKeyInvalid
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|toKeyName
init|=
literal|""
decl_stmt|;
name|OMRequest
name|modifiedOmRequest
init|=
name|doPreExecute
argument_list|(
name|createRenameKeyRequest
argument_list|(
name|toKeyName
argument_list|)
argument_list|)
decl_stmt|;
comment|// Add only volume and bucket entry to DB.
comment|// In actual implementation we don't check for bucket/volume exists
comment|// during delete key.
name|TestOMRequestUtils
operator|.
name|addVolumeAndBucketToDB
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|omMetadataManager
argument_list|)
expr_stmt|;
name|OMKeyRenameRequest
name|omKeyRenameRequest
init|=
operator|new
name|OMKeyRenameRequest
argument_list|(
name|modifiedOmRequest
argument_list|)
decl_stmt|;
name|OMClientResponse
name|omKeyRenameResponse
init|=
name|omKeyRenameRequest
operator|.
name|validateAndUpdateCache
argument_list|(
name|ozoneManager
argument_list|,
literal|100L
argument_list|,
name|ozoneManagerDoubleBufferHelper
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|OzoneManagerProtocolProtos
operator|.
name|Status
operator|.
name|INVALID_KEY_NAME
argument_list|,
name|omKeyRenameResponse
operator|.
name|getOMResponse
argument_list|()
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testValidateAndUpdateCacheWithFromKeyInvalid ()
specifier|public
name|void
name|testValidateAndUpdateCacheWithFromKeyInvalid
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|toKeyName
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|keyName
operator|=
literal|""
expr_stmt|;
name|OMRequest
name|modifiedOmRequest
init|=
name|doPreExecute
argument_list|(
name|createRenameKeyRequest
argument_list|(
name|toKeyName
argument_list|)
argument_list|)
decl_stmt|;
comment|// Add only volume and bucket entry to DB.
comment|// In actual implementation we don't check for bucket/volume exists
comment|// during delete key.
name|TestOMRequestUtils
operator|.
name|addVolumeAndBucketToDB
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|omMetadataManager
argument_list|)
expr_stmt|;
name|OMKeyRenameRequest
name|omKeyRenameRequest
init|=
operator|new
name|OMKeyRenameRequest
argument_list|(
name|modifiedOmRequest
argument_list|)
decl_stmt|;
name|OMClientResponse
name|omKeyRenameResponse
init|=
name|omKeyRenameRequest
operator|.
name|validateAndUpdateCache
argument_list|(
name|ozoneManager
argument_list|,
literal|100L
argument_list|,
name|ozoneManagerDoubleBufferHelper
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|OzoneManagerProtocolProtos
operator|.
name|Status
operator|.
name|INVALID_KEY_NAME
argument_list|,
name|omKeyRenameResponse
operator|.
name|getOMResponse
argument_list|()
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * This method calls preExecute and verify the modified request.    * @param originalOmRequest    * @return OMRequest - modified request returned from preExecute.    * @throws Exception    */
DECL|method|doPreExecute (OMRequest originalOmRequest)
specifier|private
name|OMRequest
name|doPreExecute
parameter_list|(
name|OMRequest
name|originalOmRequest
parameter_list|)
throws|throws
name|Exception
block|{
name|OMKeyRenameRequest
name|omKeyRenameRequest
init|=
operator|new
name|OMKeyRenameRequest
argument_list|(
name|originalOmRequest
argument_list|)
decl_stmt|;
name|OMRequest
name|modifiedOmRequest
init|=
name|omKeyRenameRequest
operator|.
name|preExecute
argument_list|(
name|ozoneManager
argument_list|)
decl_stmt|;
comment|// Will not be equal, as UserInfo will be set and modification time is
comment|// set in KeyArgs.
name|Assert
operator|.
name|assertNotEquals
argument_list|(
name|originalOmRequest
argument_list|,
name|modifiedOmRequest
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|modifiedOmRequest
operator|.
name|getRenameKeyRequest
argument_list|()
operator|.
name|getKeyArgs
argument_list|()
operator|.
name|getModificationTime
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
return|return
name|modifiedOmRequest
return|;
block|}
comment|/**    * Create OMRequest which encapsulates RenameKeyRequest.    * @return OMRequest    */
DECL|method|createRenameKeyRequest (String toKeyName)
specifier|private
name|OMRequest
name|createRenameKeyRequest
parameter_list|(
name|String
name|toKeyName
parameter_list|)
block|{
name|KeyArgs
name|keyArgs
init|=
name|KeyArgs
operator|.
name|newBuilder
argument_list|()
operator|.
name|setKeyName
argument_list|(
name|keyName
argument_list|)
operator|.
name|setVolumeName
argument_list|(
name|volumeName
argument_list|)
operator|.
name|setBucketName
argument_list|(
name|bucketName
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|RenameKeyRequest
name|renameKeyRequest
init|=
name|RenameKeyRequest
operator|.
name|newBuilder
argument_list|()
operator|.
name|setKeyArgs
argument_list|(
name|keyArgs
argument_list|)
operator|.
name|setToKeyName
argument_list|(
name|toKeyName
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|OMRequest
operator|.
name|newBuilder
argument_list|()
operator|.
name|setClientId
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|setRenameKeyRequest
argument_list|(
name|renameKeyRequest
argument_list|)
operator|.
name|setCmdType
argument_list|(
name|OzoneManagerProtocolProtos
operator|.
name|Type
operator|.
name|RenameKey
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

