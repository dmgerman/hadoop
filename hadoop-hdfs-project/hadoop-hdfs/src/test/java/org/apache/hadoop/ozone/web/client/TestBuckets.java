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
name|hdfs
operator|.
name|MiniDFSCluster
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
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|DataNode
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
name|ozone
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
name|web
operator|.
name|exceptions
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
name|Test
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
name|net
operator|.
name|URL
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
name|fail
import|;
end_import

begin_class
DECL|class|TestBuckets
specifier|public
class|class
name|TestBuckets
block|{
DECL|field|cluster
specifier|static
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
DECL|field|port
specifier|static
name|int
name|port
init|=
literal|0
decl_stmt|;
DECL|field|client
specifier|private
specifier|static
name|OzoneClient
name|client
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
name|URL
name|p
init|=
name|conf
operator|.
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|p
operator|.
name|getPath
argument_list|()
operator|.
name|concat
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
name|conf
operator|.
name|setBoolean
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_HANDLER_TYPE_KEY
argument_list|,
literal|"local"
argument_list|)
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|DataNode
name|dataNode
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|port
operator|=
name|dataNode
operator|.
name|getInfoPort
argument_list|()
expr_stmt|;
name|client
operator|=
operator|new
name|OzoneClient
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
comment|/**    * shutdown MiniDFSCluster    */
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
block|}
annotation|@
name|Test
DECL|method|testAddBucketAcls ()
specifier|public
name|void
name|testAddBucketAcls
parameter_list|()
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
argument_list|()
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
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

