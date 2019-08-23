begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.response.s3.bucket
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
name|s3
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
name|request
operator|.
name|s3
operator|.
name|bucket
operator|.
name|S3BucketCreateRequest
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
name|TestOMResponseUtils
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|OzoneManagerProtocolProtos
operator|.
name|S3DeleteBucketResponse
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
comment|/**  * Tests S3BucketDeleteResponse.  */
end_comment

begin_class
DECL|class|TestS3BucketDeleteResponse
specifier|public
class|class
name|TestS3BucketDeleteResponse
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
DECL|field|omMetadataManager
specifier|private
name|OMMetadataManager
name|omMetadataManager
decl_stmt|;
DECL|field|batchOperation
specifier|private
name|BatchOperation
name|batchOperation
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
name|batchOperation
operator|=
name|omMetadataManager
operator|.
name|getStore
argument_list|()
operator|.
name|initBatchOperation
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAddToDBBatch ()
specifier|public
name|void
name|testAddToDBBatch
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|s3BucketName
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
name|userName
init|=
literal|"ozone"
decl_stmt|;
name|String
name|volumeName
init|=
name|S3BucketCreateRequest
operator|.
name|formatOzoneVolumeName
argument_list|(
name|userName
argument_list|)
decl_stmt|;
name|S3BucketCreateResponse
name|s3BucketCreateResponse
init|=
name|TestOMResponseUtils
operator|.
name|createS3BucketResponse
argument_list|(
name|userName
argument_list|,
name|volumeName
argument_list|,
name|s3BucketName
argument_list|)
decl_stmt|;
name|s3BucketCreateResponse
operator|.
name|addToDBBatch
argument_list|(
name|omMetadataManager
argument_list|,
name|batchOperation
argument_list|)
expr_stmt|;
name|OMResponse
name|omResponse
init|=
name|OMResponse
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
name|DeleteS3Bucket
argument_list|)
operator|.
name|setStatus
argument_list|(
name|OzoneManagerProtocolProtos
operator|.
name|Status
operator|.
name|OK
argument_list|)
operator|.
name|setSuccess
argument_list|(
literal|true
argument_list|)
operator|.
name|setDeleteS3BucketResponse
argument_list|(
name|S3DeleteBucketResponse
operator|.
name|newBuilder
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|S3BucketDeleteResponse
name|s3BucketDeleteResponse
init|=
operator|new
name|S3BucketDeleteResponse
argument_list|(
name|s3BucketName
argument_list|,
name|volumeName
argument_list|,
name|omResponse
argument_list|)
decl_stmt|;
name|s3BucketDeleteResponse
operator|.
name|addToDBBatch
argument_list|(
name|omMetadataManager
argument_list|,
name|batchOperation
argument_list|)
expr_stmt|;
name|omMetadataManager
operator|.
name|getStore
argument_list|()
operator|.
name|commitBatchOperation
argument_list|(
name|batchOperation
argument_list|)
expr_stmt|;
comment|// Check now s3 bucket exists or not.
name|Assert
operator|.
name|assertNull
argument_list|(
name|omMetadataManager
operator|.
name|getS3Table
argument_list|()
operator|.
name|get
argument_list|(
name|s3BucketName
argument_list|)
argument_list|)
expr_stmt|;
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
name|omMetadataManager
operator|.
name|getBucketKey
argument_list|(
name|volumeName
argument_list|,
name|s3BucketName
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

