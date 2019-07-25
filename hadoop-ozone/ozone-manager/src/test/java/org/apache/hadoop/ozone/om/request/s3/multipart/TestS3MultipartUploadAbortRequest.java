begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.ozone.om.request.s3.multipart
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
name|s3
operator|.
name|multipart
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

begin_comment
comment|/**  * Test Multipart upload abort request.  */
end_comment

begin_class
DECL|class|TestS3MultipartUploadAbortRequest
specifier|public
class|class
name|TestS3MultipartUploadAbortRequest
extends|extends
name|TestS3MultipartRequest
block|{
annotation|@
name|Test
DECL|method|testPreExecute ()
specifier|public
name|void
name|testPreExecute
parameter_list|()
throws|throws
name|IOException
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
name|String
name|keyName
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|doPreExecuteAbortMPU
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
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
name|String
name|keyName
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
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
name|OMRequest
name|initiateMPURequest
init|=
name|doPreExecuteInitiateMPU
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|)
decl_stmt|;
name|S3InitiateMultipartUploadRequest
name|s3InitiateMultipartUploadRequest
init|=
operator|new
name|S3InitiateMultipartUploadRequest
argument_list|(
name|initiateMPURequest
argument_list|)
decl_stmt|;
name|OMClientResponse
name|omClientResponse
init|=
name|s3InitiateMultipartUploadRequest
operator|.
name|validateAndUpdateCache
argument_list|(
name|ozoneManager
argument_list|,
literal|1L
argument_list|)
decl_stmt|;
name|String
name|multipartUploadID
init|=
name|omClientResponse
operator|.
name|getOMResponse
argument_list|()
operator|.
name|getInitiateMultiPartUploadResponse
argument_list|()
operator|.
name|getMultipartUploadID
argument_list|()
decl_stmt|;
name|OMRequest
name|abortMPURequest
init|=
name|doPreExecuteAbortMPU
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|,
name|multipartUploadID
argument_list|)
decl_stmt|;
name|S3MultipartUploadAbortRequest
name|s3MultipartUploadAbortRequest
init|=
operator|new
name|S3MultipartUploadAbortRequest
argument_list|(
name|abortMPURequest
argument_list|)
decl_stmt|;
name|omClientResponse
operator|=
name|s3MultipartUploadAbortRequest
operator|.
name|validateAndUpdateCache
argument_list|(
name|ozoneManager
argument_list|,
literal|2L
argument_list|)
expr_stmt|;
name|String
name|multipartKey
init|=
name|omMetadataManager
operator|.
name|getMultipartKey
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|,
name|multipartUploadID
argument_list|)
decl_stmt|;
comment|// Check table and response.
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
name|Assert
operator|.
name|assertNull
argument_list|(
name|omMetadataManager
operator|.
name|getMultipartInfoTable
argument_list|()
operator|.
name|get
argument_list|(
name|multipartKey
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|omMetadataManager
operator|.
name|getOpenKeyTable
argument_list|()
operator|.
name|get
argument_list|(
name|multipartKey
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testValidateAndUpdateCacheMultipartNotFound ()
specifier|public
name|void
name|testValidateAndUpdateCacheMultipartNotFound
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
name|String
name|keyName
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
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
name|multipartUploadID
init|=
literal|"randomMPU"
decl_stmt|;
name|OMRequest
name|abortMPURequest
init|=
name|doPreExecuteAbortMPU
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|,
name|multipartUploadID
argument_list|)
decl_stmt|;
name|S3MultipartUploadAbortRequest
name|s3MultipartUploadAbortRequest
init|=
operator|new
name|S3MultipartUploadAbortRequest
argument_list|(
name|abortMPURequest
argument_list|)
decl_stmt|;
name|OMClientResponse
name|omClientResponse
init|=
name|s3MultipartUploadAbortRequest
operator|.
name|validateAndUpdateCache
argument_list|(
name|ozoneManager
argument_list|,
literal|2L
argument_list|)
decl_stmt|;
comment|// Check table and response.
name|Assert
operator|.
name|assertEquals
argument_list|(
name|OzoneManagerProtocolProtos
operator|.
name|Status
operator|.
name|NO_SUCH_MULTIPART_UPLOAD_ERROR
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
block|}
annotation|@
name|Test
DECL|method|testValidateAndUpdateCacheVolumeNotFound ()
specifier|public
name|void
name|testValidateAndUpdateCacheVolumeNotFound
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
name|String
name|keyName
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
name|multipartUploadID
init|=
literal|"randomMPU"
decl_stmt|;
name|OMRequest
name|abortMPURequest
init|=
name|doPreExecuteAbortMPU
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|,
name|multipartUploadID
argument_list|)
decl_stmt|;
name|S3MultipartUploadAbortRequest
name|s3MultipartUploadAbortRequest
init|=
operator|new
name|S3MultipartUploadAbortRequest
argument_list|(
name|abortMPURequest
argument_list|)
decl_stmt|;
name|OMClientResponse
name|omClientResponse
init|=
name|s3MultipartUploadAbortRequest
operator|.
name|validateAndUpdateCache
argument_list|(
name|ozoneManager
argument_list|,
literal|2L
argument_list|)
decl_stmt|;
comment|// Check table and response.
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
block|}
annotation|@
name|Test
DECL|method|testValidateAndUpdateCacheBucketNotFound ()
specifier|public
name|void
name|testValidateAndUpdateCacheBucketNotFound
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
name|String
name|keyName
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|TestOMRequestUtils
operator|.
name|addVolumeToDB
argument_list|(
name|volumeName
argument_list|,
name|omMetadataManager
argument_list|)
expr_stmt|;
name|String
name|multipartUploadID
init|=
literal|"randomMPU"
decl_stmt|;
name|OMRequest
name|abortMPURequest
init|=
name|doPreExecuteAbortMPU
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|,
name|multipartUploadID
argument_list|)
decl_stmt|;
name|S3MultipartUploadAbortRequest
name|s3MultipartUploadAbortRequest
init|=
operator|new
name|S3MultipartUploadAbortRequest
argument_list|(
name|abortMPURequest
argument_list|)
decl_stmt|;
name|OMClientResponse
name|omClientResponse
init|=
name|s3MultipartUploadAbortRequest
operator|.
name|validateAndUpdateCache
argument_list|(
name|ozoneManager
argument_list|,
literal|2L
argument_list|)
decl_stmt|;
comment|// Check table and response.
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
block|}
block|}
end_class

end_unit

