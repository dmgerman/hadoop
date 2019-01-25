begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.s3.endpoint
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|s3
operator|.
name|endpoint
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
name|client
operator|.
name|OzoneClientStub
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
name|s3
operator|.
name|exception
operator|.
name|OS3Exception
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
name|s3
operator|.
name|exception
operator|.
name|S3ErrorTable
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
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|HttpHeaders
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Response
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|s3
operator|.
name|util
operator|.
name|S3Consts
operator|.
name|STORAGE_CLASS_HEADER
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
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
comment|/**  * This class tests abort multipart upload request.  */
end_comment

begin_class
DECL|class|TestAbortMultipartUpload
specifier|public
class|class
name|TestAbortMultipartUpload
block|{
annotation|@
name|Test
DECL|method|testAbortMultipartUpload ()
specifier|public
name|void
name|testAbortMultipartUpload
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|bucket
init|=
literal|"s3bucket"
decl_stmt|;
name|String
name|key
init|=
literal|"key1"
decl_stmt|;
name|OzoneClientStub
name|client
init|=
operator|new
name|OzoneClientStub
argument_list|()
decl_stmt|;
name|client
operator|.
name|getObjectStore
argument_list|()
operator|.
name|createS3Bucket
argument_list|(
literal|"ozone"
argument_list|,
name|bucket
argument_list|)
expr_stmt|;
name|HttpHeaders
name|headers
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|HttpHeaders
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|headers
operator|.
name|getHeaderString
argument_list|(
name|STORAGE_CLASS_HEADER
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"STANDARD"
argument_list|)
expr_stmt|;
name|ObjectEndpoint
name|rest
init|=
operator|new
name|ObjectEndpoint
argument_list|()
decl_stmt|;
name|rest
operator|.
name|setHeaders
argument_list|(
name|headers
argument_list|)
expr_stmt|;
name|rest
operator|.
name|setClient
argument_list|(
name|client
argument_list|)
expr_stmt|;
name|Response
name|response
init|=
name|rest
operator|.
name|multipartUpload
argument_list|(
name|bucket
argument_list|,
name|key
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|response
operator|.
name|getStatus
argument_list|()
argument_list|,
literal|200
argument_list|)
expr_stmt|;
name|MultipartUploadInitiateResponse
name|multipartUploadInitiateResponse
init|=
operator|(
name|MultipartUploadInitiateResponse
operator|)
name|response
operator|.
name|getEntity
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|multipartUploadInitiateResponse
operator|.
name|getUploadID
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|uploadID
init|=
name|multipartUploadInitiateResponse
operator|.
name|getUploadID
argument_list|()
decl_stmt|;
comment|// Abort multipart upload
name|response
operator|=
name|rest
operator|.
name|delete
argument_list|(
name|bucket
argument_list|,
name|key
argument_list|,
name|uploadID
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|204
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
comment|// test with unknown upload Id.
try|try
block|{
name|rest
operator|.
name|delete
argument_list|(
name|bucket
argument_list|,
name|key
argument_list|,
literal|"random"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OS3Exception
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|S3ErrorTable
operator|.
name|NO_SUCH_UPLOAD
operator|.
name|getCode
argument_list|()
argument_list|,
name|ex
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|S3ErrorTable
operator|.
name|NO_SUCH_UPLOAD
operator|.
name|getErrorMessage
argument_list|()
argument_list|,
name|ex
operator|.
name|getErrorMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

