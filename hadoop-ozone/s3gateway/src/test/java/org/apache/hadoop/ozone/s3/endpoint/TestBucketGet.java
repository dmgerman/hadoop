begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  *  */
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
name|client
operator|.
name|OzoneBucket
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
name|client
operator|.
name|OzoneClient
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

begin_comment
comment|/**  * Testing basic object list browsing.  */
end_comment

begin_class
DECL|class|TestBucketGet
specifier|public
class|class
name|TestBucketGet
block|{
annotation|@
name|Test
DECL|method|listRoot ()
specifier|public
name|void
name|listRoot
parameter_list|()
throws|throws
name|OS3Exception
throws|,
name|IOException
block|{
name|BucketEndpoint
name|getBucket
init|=
operator|new
name|BucketEndpoint
argument_list|()
decl_stmt|;
name|OzoneClient
name|client
init|=
name|createClientWithKeys
argument_list|(
literal|"file1"
argument_list|,
literal|"dir1/file2"
argument_list|)
decl_stmt|;
name|getBucket
operator|.
name|setClient
argument_list|(
name|client
argument_list|)
expr_stmt|;
name|ListObjectResponse
name|getBucketResponse
init|=
operator|(
name|ListObjectResponse
operator|)
name|getBucket
operator|.
name|list
argument_list|(
literal|"b1"
argument_list|,
literal|"/"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|100
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|getEntity
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getBucketResponse
operator|.
name|getCommonPrefixes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"dir1/"
argument_list|,
name|getBucketResponse
operator|.
name|getCommonPrefixes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getPrefix
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getBucketResponse
operator|.
name|getContents
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"file1"
argument_list|,
name|getBucketResponse
operator|.
name|getContents
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|listDir ()
specifier|public
name|void
name|listDir
parameter_list|()
throws|throws
name|OS3Exception
throws|,
name|IOException
block|{
name|BucketEndpoint
name|getBucket
init|=
operator|new
name|BucketEndpoint
argument_list|()
decl_stmt|;
name|OzoneClient
name|client
init|=
name|createClientWithKeys
argument_list|(
literal|"dir1/file2"
argument_list|,
literal|"dir1/dir2/file2"
argument_list|)
decl_stmt|;
name|getBucket
operator|.
name|setClient
argument_list|(
name|client
argument_list|)
expr_stmt|;
name|ListObjectResponse
name|getBucketResponse
init|=
operator|(
name|ListObjectResponse
operator|)
name|getBucket
operator|.
name|list
argument_list|(
literal|"b1"
argument_list|,
literal|"/"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|100
argument_list|,
literal|"dir1"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|getEntity
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getBucketResponse
operator|.
name|getCommonPrefixes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"dir1/"
argument_list|,
name|getBucketResponse
operator|.
name|getCommonPrefixes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getPrefix
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|getBucketResponse
operator|.
name|getContents
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|listSubDir ()
specifier|public
name|void
name|listSubDir
parameter_list|()
throws|throws
name|OS3Exception
throws|,
name|IOException
block|{
name|BucketEndpoint
name|getBucket
init|=
operator|new
name|BucketEndpoint
argument_list|()
decl_stmt|;
name|OzoneClient
name|ozoneClient
init|=
name|createClientWithKeys
argument_list|(
literal|"dir1/file2"
argument_list|,
literal|"dir1/dir2/file2"
argument_list|,
literal|"dir1bh/file"
argument_list|,
literal|"dir1bha/file2"
argument_list|)
decl_stmt|;
name|getBucket
operator|.
name|setClient
argument_list|(
name|ozoneClient
argument_list|)
expr_stmt|;
name|ListObjectResponse
name|getBucketResponse
init|=
operator|(
name|ListObjectResponse
operator|)
name|getBucket
operator|.
name|list
argument_list|(
literal|"b1"
argument_list|,
literal|"/"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|100
argument_list|,
literal|"dir1/"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|getEntity
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getBucketResponse
operator|.
name|getCommonPrefixes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"dir1/dir2/"
argument_list|,
name|getBucketResponse
operator|.
name|getCommonPrefixes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getPrefix
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getBucketResponse
operator|.
name|getContents
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"dir1/file2"
argument_list|,
name|getBucketResponse
operator|.
name|getContents
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|listWithPrefixAndDelimiter ()
specifier|public
name|void
name|listWithPrefixAndDelimiter
parameter_list|()
throws|throws
name|OS3Exception
throws|,
name|IOException
block|{
name|BucketEndpoint
name|getBucket
init|=
operator|new
name|BucketEndpoint
argument_list|()
decl_stmt|;
name|OzoneClient
name|ozoneClient
init|=
name|createClientWithKeys
argument_list|(
literal|"dir1/file2"
argument_list|,
literal|"dir1/dir2/file2"
argument_list|,
literal|"dir1bh/file"
argument_list|,
literal|"dir1bha/file2"
argument_list|,
literal|"file2"
argument_list|)
decl_stmt|;
name|getBucket
operator|.
name|setClient
argument_list|(
name|ozoneClient
argument_list|)
expr_stmt|;
name|ListObjectResponse
name|getBucketResponse
init|=
operator|(
name|ListObjectResponse
operator|)
name|getBucket
operator|.
name|list
argument_list|(
literal|"b1"
argument_list|,
literal|"/"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|100
argument_list|,
literal|"dir1"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|getEntity
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|getBucketResponse
operator|.
name|getCommonPrefixes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|listWithPrefixAndDelimiter1 ()
specifier|public
name|void
name|listWithPrefixAndDelimiter1
parameter_list|()
throws|throws
name|OS3Exception
throws|,
name|IOException
block|{
name|BucketEndpoint
name|getBucket
init|=
operator|new
name|BucketEndpoint
argument_list|()
decl_stmt|;
name|OzoneClient
name|ozoneClient
init|=
name|createClientWithKeys
argument_list|(
literal|"dir1/file2"
argument_list|,
literal|"dir1/dir2/file2"
argument_list|,
literal|"dir1bh/file"
argument_list|,
literal|"dir1bha/file2"
argument_list|,
literal|"file2"
argument_list|)
decl_stmt|;
name|getBucket
operator|.
name|setClient
argument_list|(
name|ozoneClient
argument_list|)
expr_stmt|;
name|ListObjectResponse
name|getBucketResponse
init|=
operator|(
name|ListObjectResponse
operator|)
name|getBucket
operator|.
name|list
argument_list|(
literal|"b1"
argument_list|,
literal|"/"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|100
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|getEntity
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|getBucketResponse
operator|.
name|getCommonPrefixes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"file2"
argument_list|,
name|getBucketResponse
operator|.
name|getContents
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|listWithPrefixAndDelimiter2 ()
specifier|public
name|void
name|listWithPrefixAndDelimiter2
parameter_list|()
throws|throws
name|OS3Exception
throws|,
name|IOException
block|{
name|BucketEndpoint
name|getBucket
init|=
operator|new
name|BucketEndpoint
argument_list|()
decl_stmt|;
name|OzoneClient
name|ozoneClient
init|=
name|createClientWithKeys
argument_list|(
literal|"dir1/file2"
argument_list|,
literal|"dir1/dir2/file2"
argument_list|,
literal|"dir1bh/file"
argument_list|,
literal|"dir1bha/file2"
argument_list|,
literal|"file2"
argument_list|)
decl_stmt|;
name|getBucket
operator|.
name|setClient
argument_list|(
name|ozoneClient
argument_list|)
expr_stmt|;
name|ListObjectResponse
name|getBucketResponse
init|=
operator|(
name|ListObjectResponse
operator|)
name|getBucket
operator|.
name|list
argument_list|(
literal|"b1"
argument_list|,
literal|"/"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|100
argument_list|,
literal|"dir1bh"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|"dir1/dir2/file2"
argument_list|,
literal|null
argument_list|)
operator|.
name|getEntity
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|getBucketResponse
operator|.
name|getCommonPrefixes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|listWithContinuationToken ()
specifier|public
name|void
name|listWithContinuationToken
parameter_list|()
throws|throws
name|OS3Exception
throws|,
name|IOException
block|{
name|BucketEndpoint
name|getBucket
init|=
operator|new
name|BucketEndpoint
argument_list|()
decl_stmt|;
name|OzoneClient
name|ozoneClient
init|=
name|createClientWithKeys
argument_list|(
literal|"dir1/file2"
argument_list|,
literal|"dir1/dir2/file2"
argument_list|,
literal|"dir1bh/file"
argument_list|,
literal|"dir1bha/file2"
argument_list|,
literal|"file2"
argument_list|)
decl_stmt|;
name|getBucket
operator|.
name|setClient
argument_list|(
name|ozoneClient
argument_list|)
expr_stmt|;
name|int
name|maxKeys
init|=
literal|2
decl_stmt|;
comment|// As we have 5 keys, with max keys 2 we should call list 3 times.
comment|// First time
name|ListObjectResponse
name|getBucketResponse
init|=
operator|(
name|ListObjectResponse
operator|)
name|getBucket
operator|.
name|list
argument_list|(
literal|"b1"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|maxKeys
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|getEntity
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|getBucketResponse
operator|.
name|isTruncated
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|getBucketResponse
operator|.
name|getContents
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
comment|// 2nd time
name|String
name|continueToken
init|=
name|getBucketResponse
operator|.
name|getNextToken
argument_list|()
decl_stmt|;
name|getBucketResponse
operator|=
operator|(
name|ListObjectResponse
operator|)
name|getBucket
operator|.
name|list
argument_list|(
literal|"b1"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|maxKeys
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|,
name|continueToken
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|getEntity
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|getBucketResponse
operator|.
name|isTruncated
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|getBucketResponse
operator|.
name|getContents
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|continueToken
operator|=
name|getBucketResponse
operator|.
name|getNextToken
argument_list|()
expr_stmt|;
comment|//3rd time
name|getBucketResponse
operator|=
operator|(
name|ListObjectResponse
operator|)
name|getBucket
operator|.
name|list
argument_list|(
literal|"b1"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|maxKeys
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|,
name|continueToken
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|getEntity
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|getBucketResponse
operator|.
name|isTruncated
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|getBucketResponse
operator|.
name|getContents
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|listWithContinuationTokenDirBreak ()
specifier|public
name|void
name|listWithContinuationTokenDirBreak
parameter_list|()
throws|throws
name|OS3Exception
throws|,
name|IOException
block|{
name|BucketEndpoint
name|getBucket
init|=
operator|new
name|BucketEndpoint
argument_list|()
decl_stmt|;
name|OzoneClient
name|ozoneClient
init|=
name|createClientWithKeys
argument_list|(
literal|"test/dir1/file1"
argument_list|,
literal|"test/dir1/file2"
argument_list|,
literal|"test/dir1/file3"
argument_list|,
literal|"test/dir2/file4"
argument_list|,
literal|"test/dir2/file5"
argument_list|,
literal|"test/dir2/file6"
argument_list|,
literal|"test/dir3/file7"
argument_list|,
literal|"test/file8"
argument_list|)
decl_stmt|;
name|getBucket
operator|.
name|setClient
argument_list|(
name|ozoneClient
argument_list|)
expr_stmt|;
name|int
name|maxKeys
init|=
literal|2
decl_stmt|;
name|ListObjectResponse
name|getBucketResponse
decl_stmt|;
name|getBucketResponse
operator|=
operator|(
name|ListObjectResponse
operator|)
name|getBucket
operator|.
name|list
argument_list|(
literal|"b1"
argument_list|,
literal|"/"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|maxKeys
argument_list|,
literal|"test/"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|getEntity
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|getBucketResponse
operator|.
name|getContents
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|getBucketResponse
operator|.
name|getCommonPrefixes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"test/dir1/"
argument_list|,
name|getBucketResponse
operator|.
name|getCommonPrefixes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getPrefix
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"test/dir2/"
argument_list|,
name|getBucketResponse
operator|.
name|getCommonPrefixes
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getPrefix
argument_list|()
argument_list|)
expr_stmt|;
name|getBucketResponse
operator|=
operator|(
name|ListObjectResponse
operator|)
name|getBucket
operator|.
name|list
argument_list|(
literal|"b1"
argument_list|,
literal|"/"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|maxKeys
argument_list|,
literal|"test/"
argument_list|,
literal|null
argument_list|,
name|getBucketResponse
operator|.
name|getNextToken
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|getEntity
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getBucketResponse
operator|.
name|getContents
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getBucketResponse
operator|.
name|getCommonPrefixes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"test/dir3/"
argument_list|,
name|getBucketResponse
operator|.
name|getCommonPrefixes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getPrefix
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"test/file8"
argument_list|,
name|getBucketResponse
operator|.
name|getContents
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|/**    * This test is with prefix and delimiter and verify continuation-token    * behavior.    */
DECL|method|listWithContinuationToken1 ()
specifier|public
name|void
name|listWithContinuationToken1
parameter_list|()
throws|throws
name|OS3Exception
throws|,
name|IOException
block|{
name|BucketEndpoint
name|getBucket
init|=
operator|new
name|BucketEndpoint
argument_list|()
decl_stmt|;
name|OzoneClient
name|ozoneClient
init|=
name|createClientWithKeys
argument_list|(
literal|"dir1/file1"
argument_list|,
literal|"dir1bh/file1"
argument_list|,
literal|"dir1bha/file1"
argument_list|,
literal|"dir0/file1"
argument_list|,
literal|"dir2/file1"
argument_list|)
decl_stmt|;
name|getBucket
operator|.
name|setClient
argument_list|(
name|ozoneClient
argument_list|)
expr_stmt|;
name|int
name|maxKeys
init|=
literal|2
decl_stmt|;
comment|// As we have 5 keys, with max keys 2 we should call list 3 times.
comment|// First time
name|ListObjectResponse
name|getBucketResponse
init|=
operator|(
name|ListObjectResponse
operator|)
name|getBucket
operator|.
name|list
argument_list|(
literal|"b1"
argument_list|,
literal|"/"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|maxKeys
argument_list|,
literal|"dir"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|getEntity
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|getBucketResponse
operator|.
name|isTruncated
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|getBucketResponse
operator|.
name|getCommonPrefixes
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
comment|// 2nd time
name|String
name|continueToken
init|=
name|getBucketResponse
operator|.
name|getNextToken
argument_list|()
decl_stmt|;
name|getBucketResponse
operator|=
operator|(
name|ListObjectResponse
operator|)
name|getBucket
operator|.
name|list
argument_list|(
literal|"b1"
argument_list|,
literal|"/"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|maxKeys
argument_list|,
literal|"dir"
argument_list|,
literal|null
argument_list|,
name|continueToken
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|getEntity
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|getBucketResponse
operator|.
name|isTruncated
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|getBucketResponse
operator|.
name|getCommonPrefixes
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
comment|//3rd time
name|continueToken
operator|=
name|getBucketResponse
operator|.
name|getNextToken
argument_list|()
expr_stmt|;
name|getBucketResponse
operator|=
operator|(
name|ListObjectResponse
operator|)
name|getBucket
operator|.
name|list
argument_list|(
literal|"b1"
argument_list|,
literal|"/"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|maxKeys
argument_list|,
literal|"dir"
argument_list|,
literal|null
argument_list|,
name|continueToken
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|getEntity
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|getBucketResponse
operator|.
name|isTruncated
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|getBucketResponse
operator|.
name|getCommonPrefixes
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|listWithContinuationTokenFail ()
specifier|public
name|void
name|listWithContinuationTokenFail
parameter_list|()
throws|throws
name|OS3Exception
throws|,
name|IOException
block|{
name|BucketEndpoint
name|getBucket
init|=
operator|new
name|BucketEndpoint
argument_list|()
decl_stmt|;
name|OzoneClient
name|ozoneClient
init|=
name|createClientWithKeys
argument_list|(
literal|"dir1/file2"
argument_list|,
literal|"dir1/dir2/file2"
argument_list|,
literal|"dir1bh/file"
argument_list|,
literal|"dir1bha/file2"
argument_list|,
literal|"dir1"
argument_list|,
literal|"dir2"
argument_list|,
literal|"dir3"
argument_list|)
decl_stmt|;
name|getBucket
operator|.
name|setClient
argument_list|(
name|ozoneClient
argument_list|)
expr_stmt|;
try|try
block|{
name|ListObjectResponse
name|getBucketResponse
init|=
operator|(
name|ListObjectResponse
operator|)
name|getBucket
operator|.
name|list
argument_list|(
literal|"b1"
argument_list|,
literal|"/"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|2
argument_list|,
literal|"dir"
argument_list|,
literal|null
argument_list|,
literal|"random"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|getEntity
argument_list|()
decl_stmt|;
name|fail
argument_list|(
literal|"listWithContinuationTokenFail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OS3Exception
name|ex
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"random"
argument_list|,
name|ex
operator|.
name|getResource
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Invalid Argument"
argument_list|,
name|ex
operator|.
name|getErrorMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testStartAfter ()
specifier|public
name|void
name|testStartAfter
parameter_list|()
throws|throws
name|IOException
throws|,
name|OS3Exception
block|{
name|BucketEndpoint
name|getBucket
init|=
operator|new
name|BucketEndpoint
argument_list|()
decl_stmt|;
name|OzoneClient
name|ozoneClient
init|=
name|createClientWithKeys
argument_list|(
literal|"dir1/file1"
argument_list|,
literal|"dir1bh/file1"
argument_list|,
literal|"dir1bha/file1"
argument_list|,
literal|"dir0/file1"
argument_list|,
literal|"dir2/file1"
argument_list|)
decl_stmt|;
name|getBucket
operator|.
name|setClient
argument_list|(
name|ozoneClient
argument_list|)
expr_stmt|;
name|ListObjectResponse
name|getBucketResponse
init|=
operator|(
name|ListObjectResponse
operator|)
name|getBucket
operator|.
name|list
argument_list|(
literal|"b1"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|getEntity
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|getBucketResponse
operator|.
name|isTruncated
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|getBucketResponse
operator|.
name|getContents
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|5
argument_list|)
expr_stmt|;
comment|//As our list output is sorted, after seeking to startAfter, we shall
comment|// have 4 keys.
name|String
name|startAfter
init|=
literal|"dir0/file1"
decl_stmt|;
name|getBucketResponse
operator|=
operator|(
name|ListObjectResponse
operator|)
name|getBucket
operator|.
name|list
argument_list|(
literal|"b1"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|startAfter
argument_list|,
literal|null
argument_list|)
operator|.
name|getEntity
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|getBucketResponse
operator|.
name|isTruncated
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|getBucketResponse
operator|.
name|getContents
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|4
argument_list|)
expr_stmt|;
name|getBucketResponse
operator|=
operator|(
name|ListObjectResponse
operator|)
name|getBucket
operator|.
name|list
argument_list|(
literal|"b1"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|"random"
argument_list|,
literal|null
argument_list|)
operator|.
name|getEntity
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|getBucketResponse
operator|.
name|isTruncated
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|getBucketResponse
operator|.
name|getContents
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|createClientWithKeys (String... keys)
specifier|private
name|OzoneClient
name|createClientWithKeys
parameter_list|(
name|String
modifier|...
name|keys
parameter_list|)
throws|throws
name|IOException
block|{
name|OzoneClient
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
literal|"bilbo"
argument_list|,
literal|"b1"
argument_list|)
expr_stmt|;
name|String
name|volume
init|=
name|client
operator|.
name|getObjectStore
argument_list|()
operator|.
name|getOzoneVolumeName
argument_list|(
literal|"b1"
argument_list|)
decl_stmt|;
name|client
operator|.
name|getObjectStore
argument_list|()
operator|.
name|getVolume
argument_list|(
name|volume
argument_list|)
operator|.
name|createBucket
argument_list|(
literal|"b1"
argument_list|)
expr_stmt|;
name|OzoneBucket
name|bucket
init|=
name|client
operator|.
name|getObjectStore
argument_list|()
operator|.
name|getVolume
argument_list|(
name|volume
argument_list|)
operator|.
name|getBucket
argument_list|(
literal|"b1"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|keys
control|)
block|{
name|bucket
operator|.
name|createKey
argument_list|(
name|key
argument_list|,
literal|0
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|client
return|;
block|}
block|}
end_class

end_unit

