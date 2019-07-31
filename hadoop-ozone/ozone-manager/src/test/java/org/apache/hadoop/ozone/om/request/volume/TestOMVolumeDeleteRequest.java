begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.request.volume
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
name|volume
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Optional
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

begin_empty_stmt
empty_stmt|;
end_empty_stmt

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
name|OmBucketInfo
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
name|utils
operator|.
name|db
operator|.
name|cache
operator|.
name|CacheKey
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
name|cache
operator|.
name|CacheValue
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
name|DeleteVolumeRequest
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
comment|/**  * Tests delete volume request.  */
end_comment

begin_class
DECL|class|TestOMVolumeDeleteRequest
specifier|public
class|class
name|TestOMVolumeDeleteRequest
extends|extends
name|TestOMVolumeRequest
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
name|String
name|volumeName
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
name|originalRequest
init|=
name|deleteVolumeRequest
argument_list|(
name|volumeName
argument_list|)
decl_stmt|;
name|OMVolumeDeleteRequest
name|omVolumeDeleteRequest
init|=
operator|new
name|OMVolumeDeleteRequest
argument_list|(
name|originalRequest
argument_list|)
decl_stmt|;
name|OMRequest
name|modifiedRequest
init|=
name|omVolumeDeleteRequest
operator|.
name|preExecute
argument_list|(
name|ozoneManager
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotEquals
argument_list|(
name|originalRequest
argument_list|,
name|modifiedRequest
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testValidateAndUpdateCacheSuccess ()
specifier|public
name|void
name|testValidateAndUpdateCacheSuccess
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|volumeName
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|ownerName
init|=
literal|"user1"
decl_stmt|;
name|OMRequest
name|originalRequest
init|=
name|deleteVolumeRequest
argument_list|(
name|volumeName
argument_list|)
decl_stmt|;
name|OMVolumeDeleteRequest
name|omVolumeDeleteRequest
init|=
operator|new
name|OMVolumeDeleteRequest
argument_list|(
name|originalRequest
argument_list|)
decl_stmt|;
name|omVolumeDeleteRequest
operator|.
name|preExecute
argument_list|(
name|ozoneManager
argument_list|)
expr_stmt|;
comment|// Add volume and user to DB
name|TestOMRequestUtils
operator|.
name|addVolumeToDB
argument_list|(
name|volumeName
argument_list|,
name|ownerName
argument_list|,
name|omMetadataManager
argument_list|)
expr_stmt|;
name|TestOMRequestUtils
operator|.
name|addUserToDB
argument_list|(
name|volumeName
argument_list|,
name|ownerName
argument_list|,
name|omMetadataManager
argument_list|)
expr_stmt|;
name|String
name|volumeKey
init|=
name|omMetadataManager
operator|.
name|getVolumeKey
argument_list|(
name|volumeName
argument_list|)
decl_stmt|;
name|String
name|ownerKey
init|=
name|omMetadataManager
operator|.
name|getUserKey
argument_list|(
name|ownerName
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|omMetadataManager
operator|.
name|getVolumeTable
argument_list|()
operator|.
name|get
argument_list|(
name|volumeKey
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|omMetadataManager
operator|.
name|getUserTable
argument_list|()
operator|.
name|get
argument_list|(
name|ownerKey
argument_list|)
argument_list|)
expr_stmt|;
name|OMClientResponse
name|omClientResponse
init|=
name|omVolumeDeleteRequest
operator|.
name|validateAndUpdateCache
argument_list|(
name|ozoneManager
argument_list|,
literal|1
argument_list|,
name|ozoneManagerDoubleBufferHelper
argument_list|)
decl_stmt|;
name|OzoneManagerProtocolProtos
operator|.
name|OMResponse
name|omResponse
init|=
name|omClientResponse
operator|.
name|getOMResponse
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|omResponse
operator|.
name|getCreateVolumeResponse
argument_list|()
argument_list|)
expr_stmt|;
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
name|omResponse
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|omMetadataManager
operator|.
name|getUserTable
argument_list|()
operator|.
name|get
argument_list|(
name|ownerKey
argument_list|)
operator|.
name|getVolumeNamesList
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
comment|// As now volume is deleted, table should not have those entries.
name|Assert
operator|.
name|assertNull
argument_list|(
name|omMetadataManager
operator|.
name|getVolumeTable
argument_list|()
operator|.
name|get
argument_list|(
name|volumeKey
argument_list|)
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
name|String
name|volumeName
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
name|originalRequest
init|=
name|deleteVolumeRequest
argument_list|(
name|volumeName
argument_list|)
decl_stmt|;
name|OMVolumeDeleteRequest
name|omVolumeDeleteRequest
init|=
operator|new
name|OMVolumeDeleteRequest
argument_list|(
name|originalRequest
argument_list|)
decl_stmt|;
name|omVolumeDeleteRequest
operator|.
name|preExecute
argument_list|(
name|ozoneManager
argument_list|)
expr_stmt|;
name|OMClientResponse
name|omClientResponse
init|=
name|omVolumeDeleteRequest
operator|.
name|validateAndUpdateCache
argument_list|(
name|ozoneManager
argument_list|,
literal|1
argument_list|,
name|ozoneManagerDoubleBufferHelper
argument_list|)
decl_stmt|;
name|OzoneManagerProtocolProtos
operator|.
name|OMResponse
name|omResponse
init|=
name|omClientResponse
operator|.
name|getOMResponse
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|omResponse
operator|.
name|getCreateVolumeResponse
argument_list|()
argument_list|)
expr_stmt|;
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
name|omResponse
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testValidateAndUpdateCacheWithVolumeNotEmpty ()
specifier|public
name|void
name|testValidateAndUpdateCacheWithVolumeNotEmpty
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|volumeName
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|ownerName
init|=
literal|"user1"
decl_stmt|;
name|OMRequest
name|originalRequest
init|=
name|deleteVolumeRequest
argument_list|(
name|volumeName
argument_list|)
decl_stmt|;
name|OMVolumeDeleteRequest
name|omVolumeDeleteRequest
init|=
operator|new
name|OMVolumeDeleteRequest
argument_list|(
name|originalRequest
argument_list|)
decl_stmt|;
name|omVolumeDeleteRequest
operator|.
name|preExecute
argument_list|(
name|ozoneManager
argument_list|)
expr_stmt|;
comment|// Add some bucket to bucket table cache.
name|String
name|bucketName
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|bucketKey
init|=
name|omMetadataManager
operator|.
name|getBucketKey
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|)
decl_stmt|;
name|OmBucketInfo
name|omBucketInfo
init|=
name|OmBucketInfo
operator|.
name|newBuilder
argument_list|()
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
name|omMetadataManager
operator|.
name|getBucketTable
argument_list|()
operator|.
name|addCacheEntry
argument_list|(
operator|new
name|CacheKey
argument_list|<>
argument_list|(
name|bucketKey
argument_list|)
argument_list|,
operator|new
name|CacheValue
argument_list|<>
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|omBucketInfo
argument_list|)
argument_list|,
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
comment|// Add user and volume to DB.
name|TestOMRequestUtils
operator|.
name|addUserToDB
argument_list|(
name|volumeName
argument_list|,
name|ownerName
argument_list|,
name|omMetadataManager
argument_list|)
expr_stmt|;
name|TestOMRequestUtils
operator|.
name|addVolumeToDB
argument_list|(
name|volumeName
argument_list|,
name|ownerName
argument_list|,
name|omMetadataManager
argument_list|)
expr_stmt|;
name|OMClientResponse
name|omClientResponse
init|=
name|omVolumeDeleteRequest
operator|.
name|validateAndUpdateCache
argument_list|(
name|ozoneManager
argument_list|,
literal|1L
argument_list|,
name|ozoneManagerDoubleBufferHelper
argument_list|)
decl_stmt|;
name|OzoneManagerProtocolProtos
operator|.
name|OMResponse
name|omResponse
init|=
name|omClientResponse
operator|.
name|getOMResponse
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|omResponse
operator|.
name|getCreateVolumeResponse
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|OzoneManagerProtocolProtos
operator|.
name|Status
operator|.
name|VOLUME_NOT_EMPTY
argument_list|,
name|omResponse
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create OMRequest for delete volume.    * @param volumeName    * @return OMRequest    */
DECL|method|deleteVolumeRequest (String volumeName)
specifier|private
name|OMRequest
name|deleteVolumeRequest
parameter_list|(
name|String
name|volumeName
parameter_list|)
block|{
name|DeleteVolumeRequest
name|deleteVolumeRequest
init|=
name|DeleteVolumeRequest
operator|.
name|newBuilder
argument_list|()
operator|.
name|setVolumeName
argument_list|(
name|volumeName
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
name|setCmdType
argument_list|(
name|OzoneManagerProtocolProtos
operator|.
name|Type
operator|.
name|DeleteVolume
argument_list|)
operator|.
name|setDeleteVolumeRequest
argument_list|(
name|deleteVolumeRequest
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

