begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|*
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|GetObjectMetadataRequest
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|ListObjectsRequest
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|ListObjectsV2Request
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|ListObjectsV2Result
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|ObjectListing
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|ObjectMetadata
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|S3ObjectSummary
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
name|fs
operator|.
name|FileStatus
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
name|fs
operator|.
name|Path
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
name|fs
operator|.
name|contract
operator|.
name|ContractTestUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|BaseMatcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Description
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Matcher
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

begin_comment
comment|/**  * S3A tests for getFileStatus using mock S3 client.  */
end_comment

begin_class
DECL|class|TestS3AGetFileStatus
specifier|public
class|class
name|TestS3AGetFileStatus
extends|extends
name|AbstractS3AMockTest
block|{
annotation|@
name|Test
DECL|method|testFile ()
specifier|public
name|void
name|testFile
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/file"
argument_list|)
decl_stmt|;
name|String
name|key
init|=
name|path
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|ObjectMetadata
name|meta
init|=
operator|new
name|ObjectMetadata
argument_list|()
decl_stmt|;
name|meta
operator|.
name|setContentLength
argument_list|(
literal|1L
argument_list|)
expr_stmt|;
name|meta
operator|.
name|setLastModified
argument_list|(
operator|new
name|Date
argument_list|(
literal|2L
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|s3
operator|.
name|getObjectMetadata
argument_list|(
name|argThat
argument_list|(
name|correctGetMetadataRequest
argument_list|(
name|BUCKET
argument_list|,
name|key
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|meta
argument_list|)
expr_stmt|;
name|FileStatus
name|stat
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|stat
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fs
operator|.
name|makeQualified
argument_list|(
name|path
argument_list|)
argument_list|,
name|stat
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|stat
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|meta
operator|.
name|getContentLength
argument_list|()
argument_list|,
name|stat
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|meta
operator|.
name|getLastModified
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|,
name|stat
operator|.
name|getModificationTime
argument_list|()
argument_list|)
expr_stmt|;
name|ContractTestUtils
operator|.
name|assertNotErasureCoded
argument_list|(
name|fs
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|path
operator|+
literal|" should have erasure coding unset in "
operator|+
literal|"FileStatus#toString(): "
operator|+
name|stat
argument_list|,
name|stat
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"isErasureCoded=false"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFakeDirectory ()
specifier|public
name|void
name|testFakeDirectory
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/dir"
argument_list|)
decl_stmt|;
name|String
name|key
init|=
name|path
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|s3
operator|.
name|getObjectMetadata
argument_list|(
name|argThat
argument_list|(
name|correctGetMetadataRequest
argument_list|(
name|BUCKET
argument_list|,
name|key
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenThrow
argument_list|(
name|NOT_FOUND
argument_list|)
expr_stmt|;
name|ObjectMetadata
name|meta
init|=
operator|new
name|ObjectMetadata
argument_list|()
decl_stmt|;
name|meta
operator|.
name|setContentLength
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|s3
operator|.
name|getObjectMetadata
argument_list|(
name|argThat
argument_list|(
name|correctGetMetadataRequest
argument_list|(
name|BUCKET
argument_list|,
name|key
operator|+
literal|"/"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|meta
argument_list|)
expr_stmt|;
name|FileStatus
name|stat
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|stat
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fs
operator|.
name|makeQualified
argument_list|(
name|path
argument_list|)
argument_list|,
name|stat
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|stat
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testImplicitDirectory ()
specifier|public
name|void
name|testImplicitDirectory
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/dir"
argument_list|)
decl_stmt|;
name|String
name|key
init|=
name|path
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|s3
operator|.
name|getObjectMetadata
argument_list|(
name|argThat
argument_list|(
name|correctGetMetadataRequest
argument_list|(
name|BUCKET
argument_list|,
name|key
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenThrow
argument_list|(
name|NOT_FOUND
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|s3
operator|.
name|getObjectMetadata
argument_list|(
name|argThat
argument_list|(
name|correctGetMetadataRequest
argument_list|(
name|BUCKET
argument_list|,
name|key
operator|+
literal|"/"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenThrow
argument_list|(
name|NOT_FOUND
argument_list|)
expr_stmt|;
name|setupListMocks
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"dir/"
argument_list|)
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
name|FileStatus
name|stat
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|stat
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fs
operator|.
name|makeQualified
argument_list|(
name|path
argument_list|)
argument_list|,
name|stat
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|stat
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|ContractTestUtils
operator|.
name|assertNotErasureCoded
argument_list|(
name|fs
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|path
operator|+
literal|" should have erasure coding unset in "
operator|+
literal|"FileStatus#toString(): "
operator|+
name|stat
argument_list|,
name|stat
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"isErasureCoded=false"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRoot ()
specifier|public
name|void
name|testRoot
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|String
name|key
init|=
name|path
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|s3
operator|.
name|getObjectMetadata
argument_list|(
name|argThat
argument_list|(
name|correctGetMetadataRequest
argument_list|(
name|BUCKET
argument_list|,
name|key
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenThrow
argument_list|(
name|NOT_FOUND
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|s3
operator|.
name|getObjectMetadata
argument_list|(
name|argThat
argument_list|(
name|correctGetMetadataRequest
argument_list|(
name|BUCKET
argument_list|,
name|key
operator|+
literal|"/"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenThrow
argument_list|(
name|NOT_FOUND
argument_list|)
expr_stmt|;
name|setupListMocks
argument_list|(
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
name|FileStatus
name|stat
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|stat
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fs
operator|.
name|makeQualified
argument_list|(
name|path
argument_list|)
argument_list|,
name|stat
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|stat
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|stat
operator|.
name|getPath
argument_list|()
operator|.
name|isRoot
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNotFound ()
specifier|public
name|void
name|testNotFound
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/dir"
argument_list|)
decl_stmt|;
name|String
name|key
init|=
name|path
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|s3
operator|.
name|getObjectMetadata
argument_list|(
name|argThat
argument_list|(
name|correctGetMetadataRequest
argument_list|(
name|BUCKET
argument_list|,
name|key
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenThrow
argument_list|(
name|NOT_FOUND
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|s3
operator|.
name|getObjectMetadata
argument_list|(
name|argThat
argument_list|(
name|correctGetMetadataRequest
argument_list|(
name|BUCKET
argument_list|,
name|key
operator|+
literal|"/"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenThrow
argument_list|(
name|NOT_FOUND
argument_list|)
expr_stmt|;
name|setupListMocks
argument_list|(
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|FileNotFoundException
operator|.
name|class
argument_list|)
expr_stmt|;
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
DECL|method|setupListMocks (List<String> prefixes, List<S3ObjectSummary> summaries)
specifier|private
name|void
name|setupListMocks
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|prefixes
parameter_list|,
name|List
argument_list|<
name|S3ObjectSummary
argument_list|>
name|summaries
parameter_list|)
block|{
comment|// V1 list API mock
name|ObjectListing
name|objects
init|=
name|mock
argument_list|(
name|ObjectListing
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|objects
operator|.
name|getCommonPrefixes
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|prefixes
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|objects
operator|.
name|getObjectSummaries
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|summaries
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|s3
operator|.
name|listObjects
argument_list|(
name|any
argument_list|(
name|ListObjectsRequest
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|objects
argument_list|)
expr_stmt|;
comment|// V2 list API mock
name|ListObjectsV2Result
name|v2Result
init|=
name|mock
argument_list|(
name|ListObjectsV2Result
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|v2Result
operator|.
name|getCommonPrefixes
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|prefixes
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|v2Result
operator|.
name|getObjectSummaries
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|summaries
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|s3
operator|.
name|listObjectsV2
argument_list|(
name|any
argument_list|(
name|ListObjectsV2Request
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|v2Result
argument_list|)
expr_stmt|;
block|}
DECL|method|correctGetMetadataRequest ( String bucket, String key)
specifier|private
name|Matcher
argument_list|<
name|GetObjectMetadataRequest
argument_list|>
name|correctGetMetadataRequest
parameter_list|(
name|String
name|bucket
parameter_list|,
name|String
name|key
parameter_list|)
block|{
return|return
operator|new
name|BaseMatcher
argument_list|<
name|GetObjectMetadataRequest
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|describeTo
parameter_list|(
name|Description
name|description
parameter_list|)
block|{
name|description
operator|.
name|appendText
argument_list|(
literal|"bucket and key match"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|GetObjectMetadataRequest
condition|)
block|{
name|GetObjectMetadataRequest
name|getObjectMetadataRequest
init|=
operator|(
name|GetObjectMetadataRequest
operator|)
name|o
decl_stmt|;
return|return
name|getObjectMetadataRequest
operator|.
name|getBucketName
argument_list|()
operator|.
name|equals
argument_list|(
name|bucket
argument_list|)
operator|&&
name|getObjectMetadataRequest
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
name|key
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

