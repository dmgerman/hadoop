begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.ksm
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|ksm
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|RandomStringUtils
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
name|server
operator|.
name|datanode
operator|.
name|ObjectStoreHandler
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
name|metrics2
operator|.
name|MetricsRecordBuilder
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
name|OzoneConsts
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
name|handlers
operator|.
name|BucketArgs
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
name|handlers
operator|.
name|KeyArgs
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
name|handlers
operator|.
name|UserArgs
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
name|handlers
operator|.
name|VolumeArgs
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
name|interfaces
operator|.
name|StorageHandler
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
name|scm
operator|.
name|ScmConfigKeys
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
name|Ignore
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
name|ExpectedException
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|test
operator|.
name|MetricsAsserts
operator|.
name|assertCounter
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
name|test
operator|.
name|MetricsAsserts
operator|.
name|getMetrics
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

begin_comment
comment|/**  * Test key write/read where a key can span multiple containers.  */
end_comment

begin_class
DECL|class|TestMultipleContainerReadWrite
specifier|public
class|class
name|TestMultipleContainerReadWrite
block|{
DECL|field|cluster
specifier|private
specifier|static
name|MiniOzoneCluster
name|cluster
init|=
literal|null
decl_stmt|;
DECL|field|storageHandler
specifier|private
specifier|static
name|StorageHandler
name|storageHandler
decl_stmt|;
DECL|field|userArgs
specifier|private
specifier|static
name|UserArgs
name|userArgs
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|OzoneConfiguration
name|conf
decl_stmt|;
annotation|@
name|Rule
DECL|field|exception
specifier|public
name|ExpectedException
name|exception
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
comment|/**    * Create a MiniDFSCluster for testing.    *<p>    * Ozone is made active by setting OZONE_ENABLED = true and    * OZONE_HANDLER_TYPE_KEY = "distributed"    *    * @throws IOException    */
annotation|@
name|BeforeClass
DECL|method|init ()
specifier|public
specifier|static
name|void
name|init
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
operator|new
name|OzoneConfiguration
argument_list|()
expr_stmt|;
comment|// set to as small as 100 bytes per block.
name|conf
operator|.
name|setLong
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_SCM_BLOCK_SIZE_IN_MB
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_PROVISION_BATCH_SIZE
argument_list|,
literal|5
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
name|OzoneConsts
operator|.
name|OZONE_HANDLER_DISTRIBUTED
argument_list|)
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniOzoneCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|setHandlerType
argument_list|(
name|OzoneConsts
operator|.
name|OZONE_HANDLER_DISTRIBUTED
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|storageHandler
operator|=
operator|new
name|ObjectStoreHandler
argument_list|(
name|conf
argument_list|)
operator|.
name|getStorageHandler
argument_list|()
expr_stmt|;
name|userArgs
operator|=
operator|new
name|UserArgs
argument_list|(
literal|null
argument_list|,
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Shutdown MiniDFSCluster.    */
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
DECL|method|testWriteRead ()
specifier|public
name|void
name|testWriteRead
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|userName
init|=
literal|"user"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|String
name|adminName
init|=
literal|"admin"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|String
name|volumeName
init|=
literal|"volume"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|String
name|bucketName
init|=
literal|"bucket"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|String
name|keyName
init|=
literal|"key"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|VolumeArgs
name|createVolumeArgs
init|=
operator|new
name|VolumeArgs
argument_list|(
name|volumeName
argument_list|,
name|userArgs
argument_list|)
decl_stmt|;
name|createVolumeArgs
operator|.
name|setUserName
argument_list|(
name|userName
argument_list|)
expr_stmt|;
name|createVolumeArgs
operator|.
name|setAdminName
argument_list|(
name|adminName
argument_list|)
expr_stmt|;
name|storageHandler
operator|.
name|createVolume
argument_list|(
name|createVolumeArgs
argument_list|)
expr_stmt|;
name|BucketArgs
name|bucketArgs
init|=
operator|new
name|BucketArgs
argument_list|(
name|bucketName
argument_list|,
name|createVolumeArgs
argument_list|)
decl_stmt|;
name|bucketArgs
operator|.
name|setAddAcls
argument_list|(
operator|new
name|LinkedList
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|bucketArgs
operator|.
name|setRemoveAcls
argument_list|(
operator|new
name|LinkedList
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|bucketArgs
operator|.
name|setStorageType
argument_list|(
name|StorageType
operator|.
name|DISK
argument_list|)
expr_stmt|;
name|storageHandler
operator|.
name|createBucket
argument_list|(
name|bucketArgs
argument_list|)
expr_stmt|;
name|String
name|dataString
init|=
name|RandomStringUtils
operator|.
name|randomAscii
argument_list|(
literal|3
operator|*
operator|(
name|int
operator|)
name|OzoneConsts
operator|.
name|MB
argument_list|)
decl_stmt|;
name|KeyArgs
name|keyArgs
init|=
operator|new
name|KeyArgs
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|,
name|userArgs
argument_list|)
decl_stmt|;
name|keyArgs
operator|.
name|setSize
argument_list|(
literal|3
operator|*
operator|(
name|int
operator|)
name|OzoneConsts
operator|.
name|MB
argument_list|)
expr_stmt|;
try|try
init|(
name|OutputStream
name|outputStream
init|=
name|storageHandler
operator|.
name|newKeyWriter
argument_list|(
name|keyArgs
argument_list|)
init|)
block|{
name|outputStream
operator|.
name|write
argument_list|(
name|dataString
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|dataString
operator|.
name|length
argument_list|()
index|]
decl_stmt|;
try|try
init|(
name|InputStream
name|inputStream
init|=
name|storageHandler
operator|.
name|newKeyReader
argument_list|(
name|keyArgs
argument_list|)
init|)
block|{
name|inputStream
operator|.
name|read
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|dataString
argument_list|,
operator|new
name|String
argument_list|(
name|data
argument_list|)
argument_list|)
expr_stmt|;
comment|// checking whether container meta data has the chunk file persisted.
name|MetricsRecordBuilder
name|containerMetrics
init|=
name|getMetrics
argument_list|(
literal|"StorageContainerMetrics"
argument_list|)
decl_stmt|;
name|assertCounter
argument_list|(
literal|"numWriteChunk"
argument_list|,
literal|3L
argument_list|,
name|containerMetrics
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"numReadChunk"
argument_list|,
literal|3L
argument_list|,
name|containerMetrics
argument_list|)
expr_stmt|;
block|}
comment|// Disable this test, because this tests assumes writing beyond a specific
comment|// size is not allowed. Which is not true for now. Keeping this test in case
comment|// we add this restrict in the future.
annotation|@
name|Ignore
annotation|@
name|Test
DECL|method|testErrorWrite ()
specifier|public
name|void
name|testErrorWrite
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|userName
init|=
literal|"user"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|String
name|adminName
init|=
literal|"admin"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|String
name|volumeName
init|=
literal|"volume"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|String
name|bucketName
init|=
literal|"bucket"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|String
name|keyName
init|=
literal|"key"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|VolumeArgs
name|createVolumeArgs
init|=
operator|new
name|VolumeArgs
argument_list|(
name|volumeName
argument_list|,
name|userArgs
argument_list|)
decl_stmt|;
name|createVolumeArgs
operator|.
name|setUserName
argument_list|(
name|userName
argument_list|)
expr_stmt|;
name|createVolumeArgs
operator|.
name|setAdminName
argument_list|(
name|adminName
argument_list|)
expr_stmt|;
name|storageHandler
operator|.
name|createVolume
argument_list|(
name|createVolumeArgs
argument_list|)
expr_stmt|;
name|BucketArgs
name|bucketArgs
init|=
operator|new
name|BucketArgs
argument_list|(
name|bucketName
argument_list|,
name|createVolumeArgs
argument_list|)
decl_stmt|;
name|bucketArgs
operator|.
name|setAddAcls
argument_list|(
operator|new
name|LinkedList
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|bucketArgs
operator|.
name|setRemoveAcls
argument_list|(
operator|new
name|LinkedList
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|bucketArgs
operator|.
name|setStorageType
argument_list|(
name|StorageType
operator|.
name|DISK
argument_list|)
expr_stmt|;
name|storageHandler
operator|.
name|createBucket
argument_list|(
name|bucketArgs
argument_list|)
expr_stmt|;
name|String
name|dataString1
init|=
name|RandomStringUtils
operator|.
name|randomAscii
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|String
name|dataString2
init|=
name|RandomStringUtils
operator|.
name|randomAscii
argument_list|(
literal|500
argument_list|)
decl_stmt|;
name|KeyArgs
name|keyArgs
init|=
operator|new
name|KeyArgs
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|,
name|userArgs
argument_list|)
decl_stmt|;
name|keyArgs
operator|.
name|setSize
argument_list|(
literal|500
argument_list|)
expr_stmt|;
try|try
init|(
name|OutputStream
name|outputStream
init|=
name|storageHandler
operator|.
name|newKeyWriter
argument_list|(
name|keyArgs
argument_list|)
init|)
block|{
comment|// first write will write succeed
name|outputStream
operator|.
name|write
argument_list|(
name|dataString1
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
comment|// second write
name|exception
operator|.
name|expect
argument_list|(
name|IOException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"Can not write 500 bytes with only 400 byte space"
argument_list|)
expr_stmt|;
name|outputStream
operator|.
name|write
argument_list|(
name|dataString2
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testPartialRead ()
specifier|public
name|void
name|testPartialRead
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|userName
init|=
literal|"user"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|String
name|adminName
init|=
literal|"admin"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|String
name|volumeName
init|=
literal|"volume"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|String
name|bucketName
init|=
literal|"bucket"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|String
name|keyName
init|=
literal|"key"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|VolumeArgs
name|createVolumeArgs
init|=
operator|new
name|VolumeArgs
argument_list|(
name|volumeName
argument_list|,
name|userArgs
argument_list|)
decl_stmt|;
name|createVolumeArgs
operator|.
name|setUserName
argument_list|(
name|userName
argument_list|)
expr_stmt|;
name|createVolumeArgs
operator|.
name|setAdminName
argument_list|(
name|adminName
argument_list|)
expr_stmt|;
name|storageHandler
operator|.
name|createVolume
argument_list|(
name|createVolumeArgs
argument_list|)
expr_stmt|;
name|BucketArgs
name|bucketArgs
init|=
operator|new
name|BucketArgs
argument_list|(
name|bucketName
argument_list|,
name|createVolumeArgs
argument_list|)
decl_stmt|;
name|bucketArgs
operator|.
name|setAddAcls
argument_list|(
operator|new
name|LinkedList
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|bucketArgs
operator|.
name|setRemoveAcls
argument_list|(
operator|new
name|LinkedList
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|bucketArgs
operator|.
name|setStorageType
argument_list|(
name|StorageType
operator|.
name|DISK
argument_list|)
expr_stmt|;
name|storageHandler
operator|.
name|createBucket
argument_list|(
name|bucketArgs
argument_list|)
expr_stmt|;
name|String
name|dataString
init|=
name|RandomStringUtils
operator|.
name|randomAscii
argument_list|(
literal|500
argument_list|)
decl_stmt|;
name|KeyArgs
name|keyArgs
init|=
operator|new
name|KeyArgs
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|,
name|userArgs
argument_list|)
decl_stmt|;
name|keyArgs
operator|.
name|setSize
argument_list|(
literal|500
argument_list|)
expr_stmt|;
try|try
init|(
name|OutputStream
name|outputStream
init|=
name|storageHandler
operator|.
name|newKeyWriter
argument_list|(
name|keyArgs
argument_list|)
init|)
block|{
name|outputStream
operator|.
name|write
argument_list|(
name|dataString
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
literal|600
index|]
decl_stmt|;
try|try
init|(
name|InputStream
name|inputStream
init|=
name|storageHandler
operator|.
name|newKeyReader
argument_list|(
name|keyArgs
argument_list|)
init|)
block|{
name|int
name|readLen
init|=
name|inputStream
operator|.
name|read
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
literal|340
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|340
argument_list|,
name|readLen
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dataString
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|340
argument_list|)
argument_list|,
operator|new
name|String
argument_list|(
name|data
argument_list|)
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|340
argument_list|)
argument_list|)
expr_stmt|;
name|readLen
operator|=
name|inputStream
operator|.
name|read
argument_list|(
name|data
argument_list|,
literal|340
argument_list|,
literal|260
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|160
argument_list|,
name|readLen
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dataString
argument_list|,
operator|new
name|String
argument_list|(
name|data
argument_list|)
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|500
argument_list|)
argument_list|)
expr_stmt|;
name|readLen
operator|=
name|inputStream
operator|.
name|read
argument_list|(
name|data
argument_list|,
literal|500
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|readLen
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

