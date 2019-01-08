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
name|junit
operator|.
name|BeforeClass
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
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
operator|.
name|HTTP_NOT_FOUND
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
name|assertNotEquals
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
name|junit
operator|.
name|Assert
operator|.
name|fail
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
comment|/**  * This class tests Upload part request.  */
end_comment

begin_class
DECL|class|TestPartUpload
specifier|public
class|class
name|TestPartUpload
block|{
DECL|field|REST
specifier|private
specifier|final
specifier|static
name|ObjectEndpoint
name|REST
init|=
operator|new
name|ObjectEndpoint
argument_list|()
decl_stmt|;
empty_stmt|;
DECL|field|BUCKET
specifier|private
specifier|final
specifier|static
name|String
name|BUCKET
init|=
literal|"s3bucket"
decl_stmt|;
DECL|field|KEY
specifier|private
specifier|final
specifier|static
name|String
name|KEY
init|=
literal|"key1"
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setUp ()
specifier|public
specifier|static
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
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
name|BUCKET
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
name|REST
operator|.
name|setHeaders
argument_list|(
name|headers
argument_list|)
expr_stmt|;
name|REST
operator|.
name|setClient
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPartUpload ()
specifier|public
name|void
name|testPartUpload
parameter_list|()
throws|throws
name|Exception
block|{
name|Response
name|response
init|=
name|REST
operator|.
name|multipartUpload
argument_list|(
name|BUCKET
argument_list|,
name|KEY
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|)
decl_stmt|;
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
name|String
name|content
init|=
literal|"Multipart Upload"
decl_stmt|;
name|ByteArrayInputStream
name|body
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|content
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|response
operator|=
name|REST
operator|.
name|put
argument_list|(
name|BUCKET
argument_list|,
name|KEY
argument_list|,
name|content
operator|.
name|length
argument_list|()
argument_list|,
literal|1
argument_list|,
name|uploadID
argument_list|,
name|body
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|response
operator|.
name|getHeaderString
argument_list|(
literal|"ETag"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPartUploadWithOverride ()
specifier|public
name|void
name|testPartUploadWithOverride
parameter_list|()
throws|throws
name|Exception
block|{
name|Response
name|response
init|=
name|REST
operator|.
name|multipartUpload
argument_list|(
name|BUCKET
argument_list|,
name|KEY
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|)
decl_stmt|;
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
name|String
name|content
init|=
literal|"Multipart Upload"
decl_stmt|;
name|ByteArrayInputStream
name|body
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|content
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|response
operator|=
name|REST
operator|.
name|put
argument_list|(
name|BUCKET
argument_list|,
name|KEY
argument_list|,
name|content
operator|.
name|length
argument_list|()
argument_list|,
literal|1
argument_list|,
name|uploadID
argument_list|,
name|body
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|response
operator|.
name|getHeaderString
argument_list|(
literal|"ETag"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|eTag
init|=
name|response
operator|.
name|getHeaderString
argument_list|(
literal|"ETag"
argument_list|)
decl_stmt|;
comment|// Upload part again with same part Number, the ETag should be changed.
name|content
operator|=
literal|"Multipart Upload Changed"
expr_stmt|;
name|response
operator|=
name|REST
operator|.
name|put
argument_list|(
name|BUCKET
argument_list|,
name|KEY
argument_list|,
name|content
operator|.
name|length
argument_list|()
argument_list|,
literal|1
argument_list|,
name|uploadID
argument_list|,
name|body
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|response
operator|.
name|getHeaderString
argument_list|(
literal|"ETag"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|eTag
argument_list|,
name|response
operator|.
name|getHeaderString
argument_list|(
literal|"ETag"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPartUploadWithIncorrectUploadID ()
specifier|public
name|void
name|testPartUploadWithIncorrectUploadID
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|String
name|content
init|=
literal|"Multipart Upload With Incorrect uploadID"
decl_stmt|;
name|ByteArrayInputStream
name|body
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|content
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|REST
operator|.
name|put
argument_list|(
name|BUCKET
argument_list|,
name|KEY
argument_list|,
name|content
operator|.
name|length
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|"random"
argument_list|,
name|body
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"testPartUploadWithIncorrectUploadID failed"
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
literal|"NoSuchUpload"
argument_list|,
name|ex
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HTTP_NOT_FOUND
argument_list|,
name|ex
operator|.
name|getHttpCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

