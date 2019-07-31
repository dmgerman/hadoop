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
name|ArrayList
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
name|UUID
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
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
name|OmKeyLocationInfo
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
name|CommitKeyRequest
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
name|KeyLocation
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

begin_comment
comment|/**  * Class tests OMKeyCommitRequest class.  */
end_comment

begin_class
DECL|class|TestOMKeyCommitRequest
specifier|public
class|class
name|TestOMKeyCommitRequest
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
name|createCommitKeyRequest
argument_list|()
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
name|OMRequest
name|modifiedOmRequest
init|=
name|doPreExecute
argument_list|(
name|createCommitKeyRequest
argument_list|()
argument_list|)
decl_stmt|;
name|OMKeyCommitRequest
name|omKeyCommitRequest
init|=
operator|new
name|OMKeyCommitRequest
argument_list|(
name|modifiedOmRequest
argument_list|)
decl_stmt|;
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
name|TestOMRequestUtils
operator|.
name|addKeyToTable
argument_list|(
literal|true
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
name|String
name|ozoneKey
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
comment|// Key should not be there in key table, as validateAndUpdateCache is
comment|// still not called.
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
name|ozoneKey
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|omKeyInfo
argument_list|)
expr_stmt|;
name|OMClientResponse
name|omClientResponse
init|=
name|omKeyCommitRequest
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
name|omClientResponse
operator|.
name|getOMResponse
argument_list|()
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
comment|// Entry should be deleted from openKey Table.
name|omKeyInfo
operator|=
name|omMetadataManager
operator|.
name|getOpenKeyTable
argument_list|()
operator|.
name|get
argument_list|(
name|ozoneKey
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|omKeyInfo
argument_list|)
expr_stmt|;
comment|// Now entry should be created in key Table.
name|omKeyInfo
operator|=
name|omMetadataManager
operator|.
name|getKeyTable
argument_list|()
operator|.
name|get
argument_list|(
name|ozoneKey
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|omKeyInfo
argument_list|)
expr_stmt|;
comment|// Check modification time
name|CommitKeyRequest
name|commitKeyRequest
init|=
name|modifiedOmRequest
operator|.
name|getCommitKeyRequest
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|commitKeyRequest
operator|.
name|getKeyArgs
argument_list|()
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
comment|// Check block location.
name|List
argument_list|<
name|OmKeyLocationInfo
argument_list|>
name|locationInfoListFromCommitKeyRequest
init|=
name|commitKeyRequest
operator|.
name|getKeyArgs
argument_list|()
operator|.
name|getKeyLocationsList
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|OmKeyLocationInfo
operator|::
name|getFromProtobuf
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|locationInfoListFromCommitKeyRequest
argument_list|,
name|omKeyInfo
operator|.
name|getLatestVersionLocations
argument_list|()
operator|.
name|getLocationList
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testValidateAndUpdateCacheWithVolumeNotFound ()
specifier|public
name|void
name|testValidateAndUpdateCacheWithVolumeNotFound
parameter_list|()
throws|throws
name|Exception
block|{
name|OMRequest
name|modifiedOmRequest
init|=
name|doPreExecute
argument_list|(
name|createCommitKeyRequest
argument_list|()
argument_list|)
decl_stmt|;
name|OMKeyCommitRequest
name|omKeyCommitRequest
init|=
operator|new
name|OMKeyCommitRequest
argument_list|(
name|modifiedOmRequest
argument_list|)
decl_stmt|;
name|String
name|ozoneKey
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
comment|// Key should not be there in key table, as validateAndUpdateCache is
comment|// still not called.
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
name|ozoneKey
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|omKeyInfo
argument_list|)
expr_stmt|;
name|OMClientResponse
name|omClientResponse
init|=
name|omKeyCommitRequest
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
name|VOLUME_NOT_FOUND
argument_list|,
name|omClientResponse
operator|.
name|getOMResponse
argument_list|()
operator|.
name|getStatus
argument_list|()
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
name|ozoneKey
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|omKeyInfo
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testValidateAndUpdateCacheWithBucketNotFound ()
specifier|public
name|void
name|testValidateAndUpdateCacheWithBucketNotFound
parameter_list|()
throws|throws
name|Exception
block|{
name|OMRequest
name|modifiedOmRequest
init|=
name|doPreExecute
argument_list|(
name|createCommitKeyRequest
argument_list|()
argument_list|)
decl_stmt|;
name|OMKeyCommitRequest
name|omKeyCommitRequest
init|=
operator|new
name|OMKeyCommitRequest
argument_list|(
name|modifiedOmRequest
argument_list|)
decl_stmt|;
name|TestOMRequestUtils
operator|.
name|addVolumeToDB
argument_list|(
name|volumeName
argument_list|,
literal|"ozone"
argument_list|,
name|omMetadataManager
argument_list|)
expr_stmt|;
name|String
name|ozoneKey
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
comment|// Key should not be there in key table, as validateAndUpdateCache is
comment|// still not called.
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
name|ozoneKey
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|omKeyInfo
argument_list|)
expr_stmt|;
name|OMClientResponse
name|omClientResponse
init|=
name|omKeyCommitRequest
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
name|BUCKET_NOT_FOUND
argument_list|,
name|omClientResponse
operator|.
name|getOMResponse
argument_list|()
operator|.
name|getStatus
argument_list|()
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
name|ozoneKey
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|omKeyInfo
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
name|OMRequest
name|modifiedOmRequest
init|=
name|doPreExecute
argument_list|(
name|createCommitKeyRequest
argument_list|()
argument_list|)
decl_stmt|;
name|OMKeyCommitRequest
name|omKeyCommitRequest
init|=
operator|new
name|OMKeyCommitRequest
argument_list|(
name|modifiedOmRequest
argument_list|)
decl_stmt|;
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
name|String
name|ozoneKey
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
comment|// Key should not be there in key table, as validateAndUpdateCache is
comment|// still not called.
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
name|ozoneKey
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|omKeyInfo
argument_list|)
expr_stmt|;
name|OMClientResponse
name|omClientResponse
init|=
name|omKeyCommitRequest
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
name|omClientResponse
operator|.
name|getOMResponse
argument_list|()
operator|.
name|getStatus
argument_list|()
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
name|ozoneKey
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|omKeyInfo
argument_list|)
expr_stmt|;
block|}
comment|/**    * This method calls preExecute and verify the modified request.    * @param originalOMRequest    * @return OMRequest - modified request returned from preExecute.    * @throws Exception    */
DECL|method|doPreExecute (OMRequest originalOMRequest)
specifier|private
name|OMRequest
name|doPreExecute
parameter_list|(
name|OMRequest
name|originalOMRequest
parameter_list|)
throws|throws
name|Exception
block|{
name|OMKeyCommitRequest
name|omKeyCommitRequest
init|=
operator|new
name|OMKeyCommitRequest
argument_list|(
name|originalOMRequest
argument_list|)
decl_stmt|;
name|OMRequest
name|modifiedOmRequest
init|=
name|omKeyCommitRequest
operator|.
name|preExecute
argument_list|(
name|ozoneManager
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|modifiedOmRequest
operator|.
name|hasCommitKeyRequest
argument_list|()
argument_list|)
expr_stmt|;
name|KeyArgs
name|originalKeyArgs
init|=
name|originalOMRequest
operator|.
name|getCommitKeyRequest
argument_list|()
operator|.
name|getKeyArgs
argument_list|()
decl_stmt|;
name|KeyArgs
name|modifiedKeyArgs
init|=
name|modifiedOmRequest
operator|.
name|getCommitKeyRequest
argument_list|()
operator|.
name|getKeyArgs
argument_list|()
decl_stmt|;
name|verifyKeyArgs
argument_list|(
name|originalKeyArgs
argument_list|,
name|modifiedKeyArgs
argument_list|)
expr_stmt|;
return|return
name|modifiedOmRequest
return|;
block|}
comment|/**    * Verify KeyArgs.    * @param originalKeyArgs    * @param modifiedKeyArgs    */
DECL|method|verifyKeyArgs (KeyArgs originalKeyArgs, KeyArgs modifiedKeyArgs)
specifier|private
name|void
name|verifyKeyArgs
parameter_list|(
name|KeyArgs
name|originalKeyArgs
parameter_list|,
name|KeyArgs
name|modifiedKeyArgs
parameter_list|)
block|{
comment|// Check modification time is set or not.
name|Assert
operator|.
name|assertTrue
argument_list|(
name|modifiedKeyArgs
operator|.
name|getModificationTime
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|originalKeyArgs
operator|.
name|getModificationTime
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|originalKeyArgs
operator|.
name|getVolumeName
argument_list|()
argument_list|,
name|modifiedKeyArgs
operator|.
name|getVolumeName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|originalKeyArgs
operator|.
name|getBucketName
argument_list|()
argument_list|,
name|modifiedKeyArgs
operator|.
name|getBucketName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|originalKeyArgs
operator|.
name|getKeyName
argument_list|()
argument_list|,
name|modifiedKeyArgs
operator|.
name|getKeyName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|originalKeyArgs
operator|.
name|getDataSize
argument_list|()
argument_list|,
name|modifiedKeyArgs
operator|.
name|getDataSize
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|originalKeyArgs
operator|.
name|getKeyLocationsList
argument_list|()
argument_list|,
name|modifiedKeyArgs
operator|.
name|getKeyLocationsList
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|originalKeyArgs
operator|.
name|getType
argument_list|()
argument_list|,
name|modifiedKeyArgs
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|originalKeyArgs
operator|.
name|getFactor
argument_list|()
argument_list|,
name|modifiedKeyArgs
operator|.
name|getFactor
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create OMRequest which encapsulates CommitKeyRequest.    */
DECL|method|createCommitKeyRequest ()
specifier|private
name|OMRequest
name|createCommitKeyRequest
parameter_list|()
block|{
name|KeyArgs
name|keyArgs
init|=
name|KeyArgs
operator|.
name|newBuilder
argument_list|()
operator|.
name|setDataSize
argument_list|(
name|dataSize
argument_list|)
operator|.
name|setVolumeName
argument_list|(
name|volumeName
argument_list|)
operator|.
name|setKeyName
argument_list|(
name|keyName
argument_list|)
operator|.
name|setBucketName
argument_list|(
name|bucketName
argument_list|)
operator|.
name|setType
argument_list|(
name|replicationType
argument_list|)
operator|.
name|setFactor
argument_list|(
name|replicationFactor
argument_list|)
operator|.
name|addAllKeyLocations
argument_list|(
name|getKeyLocation
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|CommitKeyRequest
name|commitKeyRequest
init|=
name|CommitKeyRequest
operator|.
name|newBuilder
argument_list|()
operator|.
name|setKeyArgs
argument_list|(
name|keyArgs
argument_list|)
operator|.
name|setClientID
argument_list|(
name|clientID
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
name|setCmdType
argument_list|(
name|OzoneManagerProtocolProtos
operator|.
name|Type
operator|.
name|CommitKey
argument_list|)
operator|.
name|setCommitKeyRequest
argument_list|(
name|commitKeyRequest
argument_list|)
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
name|build
argument_list|()
return|;
block|}
comment|/**    * Create KeyLocation list.    */
DECL|method|getKeyLocation ()
specifier|private
name|List
argument_list|<
name|KeyLocation
argument_list|>
name|getKeyLocation
parameter_list|()
block|{
name|List
argument_list|<
name|KeyLocation
argument_list|>
name|keyLocations
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|KeyLocation
name|keyLocation
init|=
name|KeyLocation
operator|.
name|newBuilder
argument_list|()
operator|.
name|setBlockID
argument_list|(
name|HddsProtos
operator|.
name|BlockID
operator|.
name|newBuilder
argument_list|()
operator|.
name|setContainerBlockID
argument_list|(
name|HddsProtos
operator|.
name|ContainerBlockID
operator|.
name|newBuilder
argument_list|()
operator|.
name|setContainerID
argument_list|(
name|i
operator|+
literal|1000
argument_list|)
operator|.
name|setLocalID
argument_list|(
name|i
operator|+
literal|100
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setOffset
argument_list|(
literal|0
argument_list|)
operator|.
name|setLength
argument_list|(
literal|200
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|keyLocations
operator|.
name|add
argument_list|(
name|keyLocation
argument_list|)
expr_stmt|;
block|}
return|return
name|keyLocations
return|;
block|}
block|}
end_class

end_unit

