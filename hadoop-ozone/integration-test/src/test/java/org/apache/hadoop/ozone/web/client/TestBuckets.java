begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.web.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|web
operator|.
name|client
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
name|fs
operator|.
name|StorageType
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
name|MiniOzoneCluster
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
name|OzoneConfigKeys
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
name|client
operator|.
name|rest
operator|.
name|OzoneException
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
name|web
operator|.
name|request
operator|.
name|OzoneQuota
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
name|web
operator|.
name|utils
operator|.
name|OzoneUtils
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
name|test
operator|.
name|GenericTestUtils
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
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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
name|Timeout
import|;
end_import

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
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
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
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
comment|/**  * Test Ozone Bucket Lifecycle.  */
end_comment

begin_class
DECL|class|TestBuckets
specifier|public
class|class
name|TestBuckets
block|{
comment|/**    * Set the timeout for every test.    */
annotation|@
name|Rule
DECL|field|testTimeout
specifier|public
name|Timeout
name|testTimeout
init|=
operator|new
name|Timeout
argument_list|(
literal|300000
argument_list|)
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MiniOzoneCluster
name|cluster
init|=
literal|null
decl_stmt|;
DECL|field|ozoneRestClient
specifier|private
specifier|static
name|OzoneRestClient
name|ozoneRestClient
init|=
literal|null
decl_stmt|;
comment|/**    * Create a MiniDFSCluster for testing.    *<p>    * Ozone is made active by setting OZONE_ENABLED = true and    * OZONE_HANDLER_TYPE_KEY = "local" , which uses a local directory to    * emulate Ozone backend.    *    * @throws IOException    */
annotation|@
name|BeforeClass
DECL|method|init ()
specifier|public
specifier|static
name|void
name|init
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
throws|,
name|OzoneException
block|{
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|GenericTestUtils
operator|.
name|getTempPath
argument_list|(
name|TestBuckets
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
name|path
operator|+=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_LOCALSTORAGE_ROOT
argument_list|,
name|OzoneConfigKeys
operator|.
name|OZONE_LOCALSTORAGE_ROOT_DEFAULT
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_LOCALSTORAGE_ROOT
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|cluster
operator|=
name|MiniOzoneCluster
operator|.
name|newBuilder
argument_list|(
name|conf
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
specifier|final
name|int
name|port
init|=
name|cluster
operator|.
name|getHddsDatanodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getDatanodeDetails
argument_list|()
operator|.
name|getOzoneRestPort
argument_list|()
decl_stmt|;
name|ozoneRestClient
operator|=
operator|new
name|OzoneRestClient
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"http://localhost:%d"
argument_list|,
name|port
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * shutdown MiniDFSCluster.    */
annotation|@
name|AfterClass
DECL|method|shutdown ()
specifier|public
specifier|static
name|void
name|shutdown
parameter_list|()
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testCreateBucket ()
specifier|public
name|void
name|testCreateBucket
parameter_list|()
throws|throws
name|Exception
block|{
name|runTestCreateBucket
argument_list|(
name|ozoneRestClient
argument_list|)
expr_stmt|;
block|}
DECL|method|runTestCreateBucket (OzoneRestClient client)
specifier|static
name|void
name|runTestCreateBucket
parameter_list|(
name|OzoneRestClient
name|client
parameter_list|)
throws|throws
name|OzoneException
throws|,
name|IOException
throws|,
name|ParseException
block|{
name|String
name|volumeName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
name|client
operator|.
name|setUserAuth
argument_list|(
literal|"hdfs"
argument_list|)
expr_stmt|;
name|OzoneVolume
name|vol
init|=
name|client
operator|.
name|createVolume
argument_list|(
name|volumeName
argument_list|,
literal|"bilbo"
argument_list|,
literal|"100TB"
argument_list|)
decl_stmt|;
name|String
index|[]
name|acls
init|=
block|{
literal|"user:frodo:rw"
block|,
literal|"user:samwise:rw"
block|}
decl_stmt|;
comment|// create 10 buckets under same volume
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
literal|10
condition|;
name|x
operator|++
control|)
block|{
name|long
name|currentTime
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
name|String
name|bucketName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
name|OzoneBucket
name|bucket
init|=
name|vol
operator|.
name|createBucket
argument_list|(
name|bucketName
argument_list|,
name|acls
argument_list|,
name|StorageType
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|bucket
operator|.
name|getBucketName
argument_list|()
argument_list|,
name|bucketName
argument_list|)
expr_stmt|;
comment|// verify the bucket creation time
name|assertTrue
argument_list|(
operator|(
name|OzoneUtils
operator|.
name|formatDate
argument_list|(
name|bucket
operator|.
name|getCreatedOn
argument_list|()
argument_list|)
operator|/
literal|1000
operator|)
operator|>=
operator|(
name|currentTime
operator|/
literal|1000
operator|)
argument_list|)
expr_stmt|;
block|}
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|vol
operator|.
name|getVolumeName
argument_list|()
argument_list|,
name|volumeName
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|vol
operator|.
name|getCreatedby
argument_list|()
argument_list|,
literal|"hdfs"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|vol
operator|.
name|getOwnerName
argument_list|()
argument_list|,
literal|"bilbo"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|vol
operator|.
name|getQuota
argument_list|()
operator|.
name|getUnit
argument_list|()
argument_list|,
name|OzoneQuota
operator|.
name|Units
operator|.
name|TB
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|vol
operator|.
name|getQuota
argument_list|()
operator|.
name|getSize
argument_list|()
argument_list|,
literal|100
argument_list|)
expr_stmt|;
comment|// Test create a bucket with invalid bucket name,
comment|// not use Rule here because the test method is static.
try|try
block|{
name|String
name|invalidBucketName
init|=
literal|"#"
operator|+
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
name|vol
operator|.
name|createBucket
argument_list|(
name|invalidBucketName
argument_list|,
name|acls
argument_list|,
name|StorageType
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Except the bucket creation to be failed because the"
operator|+
literal|" bucket name starts with an invalid char #"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|instanceof
name|OzoneRestClientException
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Bucket or Volume name"
operator|+
literal|" has an unsupported character : #"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testAddBucketAcls ()
specifier|public
name|void
name|testAddBucketAcls
parameter_list|()
throws|throws
name|Exception
block|{
name|runTestAddBucketAcls
argument_list|(
name|ozoneRestClient
argument_list|)
expr_stmt|;
block|}
DECL|method|runTestAddBucketAcls (OzoneRestClient client)
specifier|static
name|void
name|runTestAddBucketAcls
parameter_list|(
name|OzoneRestClient
name|client
parameter_list|)
throws|throws
name|OzoneException
throws|,
name|IOException
throws|,
name|ParseException
block|{
name|String
name|volumeName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
name|client
operator|.
name|setUserAuth
argument_list|(
literal|"hdfs"
argument_list|)
expr_stmt|;
name|OzoneVolume
name|vol
init|=
name|client
operator|.
name|createVolume
argument_list|(
name|volumeName
argument_list|,
literal|"bilbo"
argument_list|,
literal|"100TB"
argument_list|)
decl_stmt|;
name|String
index|[]
name|acls
init|=
block|{
literal|"user:frodo:rw"
block|,
literal|"user:samwise:rw"
block|}
decl_stmt|;
name|String
name|bucketName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
name|vol
operator|.
name|createBucket
argument_list|(
name|bucketName
argument_list|)
expr_stmt|;
name|vol
operator|.
name|addAcls
argument_list|(
name|bucketName
argument_list|,
name|acls
argument_list|)
expr_stmt|;
name|OzoneBucket
name|updatedBucket
init|=
name|vol
operator|.
name|getBucket
argument_list|(
name|bucketName
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|updatedBucket
operator|.
name|getAcls
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
comment|// verify if the creation time is missing after update operation
name|assertTrue
argument_list|(
operator|(
name|OzoneUtils
operator|.
name|formatDate
argument_list|(
name|updatedBucket
operator|.
name|getCreatedOn
argument_list|()
argument_list|)
operator|/
literal|1000
operator|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRemoveBucketAcls ()
specifier|public
name|void
name|testRemoveBucketAcls
parameter_list|()
throws|throws
name|Exception
block|{
name|runTestRemoveBucketAcls
argument_list|(
name|ozoneRestClient
argument_list|)
expr_stmt|;
block|}
DECL|method|runTestRemoveBucketAcls (OzoneRestClient client)
specifier|static
name|void
name|runTestRemoveBucketAcls
parameter_list|(
name|OzoneRestClient
name|client
parameter_list|)
throws|throws
name|OzoneException
throws|,
name|IOException
throws|,
name|ParseException
block|{
name|String
name|volumeName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
name|client
operator|.
name|setUserAuth
argument_list|(
literal|"hdfs"
argument_list|)
expr_stmt|;
name|OzoneVolume
name|vol
init|=
name|client
operator|.
name|createVolume
argument_list|(
name|volumeName
argument_list|,
literal|"bilbo"
argument_list|,
literal|"100TB"
argument_list|)
decl_stmt|;
name|String
index|[]
name|acls
init|=
block|{
literal|"user:frodo:rw"
block|,
literal|"user:samwise:rw"
block|}
decl_stmt|;
name|String
name|bucketName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
name|OzoneBucket
name|bucket
init|=
name|vol
operator|.
name|createBucket
argument_list|(
name|bucketName
argument_list|,
name|acls
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|bucket
operator|.
name|getAcls
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|vol
operator|.
name|removeAcls
argument_list|(
name|bucketName
argument_list|,
name|acls
argument_list|)
expr_stmt|;
name|OzoneBucket
name|updatedBucket
init|=
name|vol
operator|.
name|getBucket
argument_list|(
name|bucketName
argument_list|)
decl_stmt|;
comment|// We removed all acls
name|assertEquals
argument_list|(
name|updatedBucket
operator|.
name|getAcls
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// verify if the creation time is missing after update operation
name|assertTrue
argument_list|(
operator|(
name|OzoneUtils
operator|.
name|formatDate
argument_list|(
name|updatedBucket
operator|.
name|getCreatedOn
argument_list|()
argument_list|)
operator|/
literal|1000
operator|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDeleteBucket ()
specifier|public
name|void
name|testDeleteBucket
parameter_list|()
throws|throws
name|OzoneException
throws|,
name|IOException
block|{
name|runTestDeleteBucket
argument_list|(
name|ozoneRestClient
argument_list|)
expr_stmt|;
block|}
DECL|method|runTestDeleteBucket (OzoneRestClient client)
specifier|static
name|void
name|runTestDeleteBucket
parameter_list|(
name|OzoneRestClient
name|client
parameter_list|)
throws|throws
name|OzoneException
throws|,
name|IOException
block|{
name|String
name|volumeName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
name|client
operator|.
name|setUserAuth
argument_list|(
literal|"hdfs"
argument_list|)
expr_stmt|;
name|OzoneVolume
name|vol
init|=
name|client
operator|.
name|createVolume
argument_list|(
name|volumeName
argument_list|,
literal|"bilbo"
argument_list|,
literal|"100TB"
argument_list|)
decl_stmt|;
name|String
index|[]
name|acls
init|=
block|{
literal|"user:frodo:rw"
block|,
literal|"user:samwise:rw"
block|}
decl_stmt|;
name|String
name|bucketName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
name|vol
operator|.
name|createBucket
argument_list|(
name|bucketName
argument_list|,
name|acls
argument_list|)
expr_stmt|;
name|vol
operator|.
name|deleteBucket
argument_list|(
name|bucketName
argument_list|)
expr_stmt|;
try|try
block|{
name|OzoneBucket
name|updatedBucket
init|=
name|vol
operator|.
name|getBucket
argument_list|(
name|bucketName
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"Fetching deleted bucket, Should not reach here."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
comment|// must throw
name|assertNotNull
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testListBucket ()
specifier|public
name|void
name|testListBucket
parameter_list|()
throws|throws
name|Exception
block|{
name|runTestListBucket
argument_list|(
name|ozoneRestClient
argument_list|)
expr_stmt|;
block|}
DECL|method|runTestListBucket (OzoneRestClient client)
specifier|static
name|void
name|runTestListBucket
parameter_list|(
name|OzoneRestClient
name|client
parameter_list|)
throws|throws
name|OzoneException
throws|,
name|IOException
throws|,
name|ParseException
block|{
name|String
name|volumeName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
name|client
operator|.
name|setUserAuth
argument_list|(
literal|"hdfs"
argument_list|)
expr_stmt|;
name|OzoneVolume
name|vol
init|=
name|client
operator|.
name|createVolume
argument_list|(
name|volumeName
argument_list|,
literal|"bilbo"
argument_list|,
literal|"100TB"
argument_list|)
decl_stmt|;
name|String
index|[]
name|acls
init|=
block|{
literal|"user:frodo:rw"
block|,
literal|"user:samwise:rw"
block|}
decl_stmt|;
name|long
name|currentTime
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
literal|10
condition|;
name|x
operator|++
control|)
block|{
name|String
name|bucketName
init|=
literal|"listbucket-test-"
operator|+
name|x
decl_stmt|;
name|vol
operator|.
name|createBucket
argument_list|(
name|bucketName
argument_list|,
name|acls
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|OzoneBucket
argument_list|>
name|bucketList
init|=
name|vol
operator|.
name|listBuckets
argument_list|(
literal|"100"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|bucketList
operator|.
name|size
argument_list|()
argument_list|,
literal|10
argument_list|)
expr_stmt|;
for|for
control|(
name|OzoneBucket
name|bucket
range|:
name|bucketList
control|)
block|{
name|assertTrue
argument_list|(
operator|(
name|OzoneUtils
operator|.
name|formatDate
argument_list|(
name|bucket
operator|.
name|getCreatedOn
argument_list|()
argument_list|)
operator|/
literal|1000
operator|)
operator|>=
operator|(
name|currentTime
operator|/
literal|1000
operator|)
argument_list|)
expr_stmt|;
block|}
name|bucketList
operator|=
name|vol
operator|.
name|listBuckets
argument_list|(
literal|"3"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|bucketList
operator|.
name|size
argument_list|()
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|bucketList
operator|=
name|vol
operator|.
name|listBuckets
argument_list|(
literal|"100"
argument_list|,
literal|"listbucket-test-4"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|bucketList
operator|.
name|size
argument_list|()
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|bucketList
operator|=
name|vol
operator|.
name|listBuckets
argument_list|(
literal|"100"
argument_list|,
literal|null
argument_list|,
literal|"listbucket-test-3"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|bucketList
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

