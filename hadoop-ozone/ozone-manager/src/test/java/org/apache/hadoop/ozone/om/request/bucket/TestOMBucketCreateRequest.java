begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.request.bucket
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
name|bucket
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
name|After
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
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|junit
operator|.
name|rules
operator|.
name|TemporaryFolder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
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
name|conf
operator|.
name|OzoneConfiguration
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
name|OMConfigKeys
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
name|OMMetrics
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
name|OmMetadataManagerImpl
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
name|OzoneManager
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|OzoneManagerProtocolProtos
operator|.
name|StorageTypeProto
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
name|helpers
operator|.
name|OmVolumeArgs
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
name|util
operator|.
name|Time
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_comment
comment|/**  * Tests OMBucketCreateRequest class, which handles CreateBucket request.  */
end_comment

begin_class
DECL|class|TestOMBucketCreateRequest
specifier|public
class|class
name|TestOMBucketCreateRequest
block|{
annotation|@
name|Rule
DECL|field|folder
specifier|public
name|TemporaryFolder
name|folder
init|=
operator|new
name|TemporaryFolder
argument_list|()
decl_stmt|;
DECL|field|ozoneManager
specifier|private
name|OzoneManager
name|ozoneManager
decl_stmt|;
DECL|field|omMetrics
specifier|private
name|OMMetrics
name|omMetrics
decl_stmt|;
DECL|field|omMetadataManager
specifier|private
name|OMMetadataManager
name|omMetadataManager
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|ozoneManager
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|OzoneManager
operator|.
name|class
argument_list|)
expr_stmt|;
name|omMetrics
operator|=
name|OMMetrics
operator|.
name|create
argument_list|()
expr_stmt|;
name|OzoneConfiguration
name|ozoneConfiguration
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|ozoneConfiguration
operator|.
name|set
argument_list|(
name|OMConfigKeys
operator|.
name|OZONE_OM_DB_DIRS
argument_list|,
name|folder
operator|.
name|newFolder
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|omMetadataManager
operator|=
operator|new
name|OmMetadataManagerImpl
argument_list|(
name|ozoneConfiguration
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|ozoneManager
operator|.
name|getMetrics
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|omMetrics
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|ozoneManager
operator|.
name|getMetadataManager
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|omMetadataManager
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|omMetrics
operator|.
name|unRegister
argument_list|()
expr_stmt|;
block|}
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
name|doPreExecute
argument_list|(
name|volumeName
argument_list|,
name|bucketName
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
name|OMBucketCreateRequest
name|omBucketCreateRequest
init|=
name|doPreExecute
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|)
decl_stmt|;
name|doValidateAndUpdateCache
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|omBucketCreateRequest
operator|.
name|getOmRequest
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testValidateAndUpdateCacheWithNoVolume ()
specifier|public
name|void
name|testValidateAndUpdateCacheWithNoVolume
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
name|OMRequest
name|originalRequest
init|=
name|TestOMRequestUtils
operator|.
name|createBucketRequest
argument_list|(
name|bucketName
argument_list|,
name|volumeName
argument_list|,
literal|false
argument_list|,
name|StorageTypeProto
operator|.
name|SSD
argument_list|)
decl_stmt|;
name|OMBucketCreateRequest
name|omBucketCreateRequest
init|=
operator|new
name|OMBucketCreateRequest
argument_list|(
name|originalRequest
argument_list|)
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
comment|// As we have not still called validateAndUpdateCache, get() should
comment|// return null.
name|Assert
operator|.
name|assertNull
argument_list|(
name|omMetadataManager
operator|.
name|getBucketTable
argument_list|()
operator|.
name|get
argument_list|(
name|bucketKey
argument_list|)
argument_list|)
expr_stmt|;
name|OMClientResponse
name|omClientResponse
init|=
name|omBucketCreateRequest
operator|.
name|validateAndUpdateCache
argument_list|(
name|ozoneManager
argument_list|,
literal|1
argument_list|)
decl_stmt|;
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
name|getCreateBucketResponse
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
comment|// As request is invalid bucket table should not have entry.
name|Assert
operator|.
name|assertNull
argument_list|(
name|omMetadataManager
operator|.
name|getBucketTable
argument_list|()
operator|.
name|get
argument_list|(
name|bucketKey
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testValidateAndUpdateCacheWithBucketAlreadyExists ()
specifier|public
name|void
name|testValidateAndUpdateCacheWithBucketAlreadyExists
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
name|OMBucketCreateRequest
name|omBucketCreateRequest
init|=
name|doPreExecute
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|)
decl_stmt|;
name|doValidateAndUpdateCache
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|omBucketCreateRequest
operator|.
name|getOmRequest
argument_list|()
argument_list|)
expr_stmt|;
comment|// Try create same bucket again
name|OMClientResponse
name|omClientResponse
init|=
name|omBucketCreateRequest
operator|.
name|validateAndUpdateCache
argument_list|(
name|ozoneManager
argument_list|,
literal|2
argument_list|)
decl_stmt|;
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
name|getCreateBucketResponse
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
name|BUCKET_ALREADY_EXISTS
argument_list|,
name|omResponse
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|doPreExecute (String volumeName, String bucketName)
specifier|private
name|OMBucketCreateRequest
name|doPreExecute
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|)
throws|throws
name|Exception
block|{
name|addCreateVolumeToTable
argument_list|(
name|volumeName
argument_list|,
name|omMetadataManager
argument_list|)
expr_stmt|;
name|OMRequest
name|originalRequest
init|=
name|TestOMRequestUtils
operator|.
name|createBucketRequest
argument_list|(
name|bucketName
argument_list|,
name|volumeName
argument_list|,
literal|false
argument_list|,
name|StorageTypeProto
operator|.
name|SSD
argument_list|)
decl_stmt|;
name|OMBucketCreateRequest
name|omBucketCreateRequest
init|=
operator|new
name|OMBucketCreateRequest
argument_list|(
name|originalRequest
argument_list|)
decl_stmt|;
name|OMRequest
name|modifiedRequest
init|=
name|omBucketCreateRequest
operator|.
name|preExecute
argument_list|(
name|ozoneManager
argument_list|)
decl_stmt|;
name|verifyRequest
argument_list|(
name|modifiedRequest
argument_list|,
name|originalRequest
argument_list|)
expr_stmt|;
return|return
operator|new
name|OMBucketCreateRequest
argument_list|(
name|modifiedRequest
argument_list|)
return|;
block|}
DECL|method|doValidateAndUpdateCache (String volumeName, String bucketName, OMRequest modifiedRequest)
specifier|private
name|void
name|doValidateAndUpdateCache
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|OMRequest
name|modifiedRequest
parameter_list|)
throws|throws
name|Exception
block|{
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
comment|// As we have not still called validateAndUpdateCache, get() should
comment|// return null.
name|Assert
operator|.
name|assertNull
argument_list|(
name|omMetadataManager
operator|.
name|getBucketTable
argument_list|()
operator|.
name|get
argument_list|(
name|bucketKey
argument_list|)
argument_list|)
expr_stmt|;
name|OMBucketCreateRequest
name|omBucketCreateRequest
init|=
operator|new
name|OMBucketCreateRequest
argument_list|(
name|modifiedRequest
argument_list|)
decl_stmt|;
name|OMClientResponse
name|omClientResponse
init|=
name|omBucketCreateRequest
operator|.
name|validateAndUpdateCache
argument_list|(
name|ozoneManager
argument_list|,
literal|1
argument_list|)
decl_stmt|;
comment|// As now after validateAndUpdateCache it should add entry to cache, get
comment|// should return non null value.
name|OmBucketInfo
name|omBucketInfo
init|=
name|omMetadataManager
operator|.
name|getBucketTable
argument_list|()
operator|.
name|get
argument_list|(
name|bucketKey
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|omMetadataManager
operator|.
name|getBucketTable
argument_list|()
operator|.
name|get
argument_list|(
name|bucketKey
argument_list|)
argument_list|)
expr_stmt|;
comment|// verify table data with actual request data.
name|Assert
operator|.
name|assertEquals
argument_list|(
name|OmBucketInfo
operator|.
name|getFromProtobuf
argument_list|(
name|modifiedRequest
operator|.
name|getCreateBucketRequest
argument_list|()
operator|.
name|getBucketInfo
argument_list|()
argument_list|)
argument_list|,
name|omBucketInfo
argument_list|)
expr_stmt|;
comment|// verify OMResponse.
name|verifySuccessCreateBucketResponse
argument_list|(
name|omClientResponse
operator|.
name|getOMResponse
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyRequest (OMRequest modifiedOmRequest, OMRequest originalRequest)
specifier|private
name|void
name|verifyRequest
parameter_list|(
name|OMRequest
name|modifiedOmRequest
parameter_list|,
name|OMRequest
name|originalRequest
parameter_list|)
block|{
name|OzoneManagerProtocolProtos
operator|.
name|BucketInfo
name|original
init|=
name|originalRequest
operator|.
name|getCreateBucketRequest
argument_list|()
operator|.
name|getBucketInfo
argument_list|()
decl_stmt|;
name|OzoneManagerProtocolProtos
operator|.
name|BucketInfo
name|updated
init|=
name|modifiedOmRequest
operator|.
name|getCreateBucketRequest
argument_list|()
operator|.
name|getBucketInfo
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|original
operator|.
name|getBucketName
argument_list|()
argument_list|,
name|updated
operator|.
name|getBucketName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|original
operator|.
name|getVolumeName
argument_list|()
argument_list|,
name|updated
operator|.
name|getVolumeName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|original
operator|.
name|getIsVersionEnabled
argument_list|()
argument_list|,
name|updated
operator|.
name|getIsVersionEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|original
operator|.
name|getStorageType
argument_list|()
argument_list|,
name|updated
operator|.
name|getStorageType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|original
operator|.
name|getMetadataList
argument_list|()
argument_list|,
name|updated
operator|.
name|getMetadataList
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotEquals
argument_list|(
name|original
operator|.
name|getCreationTime
argument_list|()
argument_list|,
name|updated
operator|.
name|getCreationTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|verifySuccessCreateBucketResponse (OMResponse omResponse)
specifier|public
specifier|static
name|void
name|verifySuccessCreateBucketResponse
parameter_list|(
name|OMResponse
name|omResponse
parameter_list|)
block|{
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|omResponse
operator|.
name|getCreateBucketResponse
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|OzoneManagerProtocolProtos
operator|.
name|Type
operator|.
name|CreateBucket
argument_list|,
name|omResponse
operator|.
name|getCmdType
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
block|}
DECL|method|addCreateVolumeToTable (String volumeName, OMMetadataManager omMetadataManager)
specifier|public
specifier|static
name|void
name|addCreateVolumeToTable
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|OMMetadataManager
name|omMetadataManager
parameter_list|)
throws|throws
name|Exception
block|{
name|OmVolumeArgs
name|omVolumeArgs
init|=
name|OmVolumeArgs
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCreationTime
argument_list|(
name|Time
operator|.
name|now
argument_list|()
argument_list|)
operator|.
name|setVolume
argument_list|(
name|volumeName
argument_list|)
operator|.
name|setAdminName
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
name|setOwnerName
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
decl_stmt|;
name|omMetadataManager
operator|.
name|getVolumeTable
argument_list|()
operator|.
name|put
argument_list|(
name|omMetadataManager
operator|.
name|getVolumeKey
argument_list|(
name|volumeName
argument_list|)
argument_list|,
name|omVolumeArgs
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

