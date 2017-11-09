begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.ozone
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|ozone
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
name|CommonConfigurationKeysPublic
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
name|FileSystem
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
name|FSDataOutputStream
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
name|FSDataInputStream
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
name|BeforeClass
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
name|Test
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Test OzoneFileSystem Interfaces.  *  * This test will test the various interfaces i.e.  * create, read, write, getFileStatus  */
end_comment

begin_class
DECL|class|TestOzoneFileInterfaces
specifier|public
class|class
name|TestOzoneFileInterfaces
block|{
DECL|field|cluster
specifier|private
specifier|static
name|MiniOzoneCluster
name|cluster
init|=
literal|null
decl_stmt|;
DECL|field|fs
specifier|private
specifier|static
name|FileSystem
name|fs
decl_stmt|;
DECL|field|storageHandler
specifier|private
specifier|static
name|StorageHandler
name|storageHandler
decl_stmt|;
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
name|OzoneException
block|{
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
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
comment|// create a volume and a bucket to be used by OzoneFileSystem
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
name|UserArgs
name|userArgs
init|=
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
decl_stmt|;
name|VolumeArgs
name|volumeArgs
init|=
operator|new
name|VolumeArgs
argument_list|(
name|volumeName
argument_list|,
name|userArgs
argument_list|)
decl_stmt|;
name|volumeArgs
operator|.
name|setUserName
argument_list|(
name|userName
argument_list|)
expr_stmt|;
name|volumeArgs
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
name|volumeArgs
argument_list|)
expr_stmt|;
name|BucketArgs
name|bucketArgs
init|=
operator|new
name|BucketArgs
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|userArgs
argument_list|)
decl_stmt|;
name|storageHandler
operator|.
name|createBucket
argument_list|(
name|bucketArgs
argument_list|)
expr_stmt|;
comment|// Fetch the host and port for File System init
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
name|int
name|port
init|=
name|dataNode
operator|.
name|getInfoPort
argument_list|()
decl_stmt|;
name|String
name|host
init|=
name|dataNode
operator|.
name|getDatanodeHostname
argument_list|()
decl_stmt|;
comment|// Set the fs.defaultFS and start the filesystem
name|String
name|uri
init|=
name|String
operator|.
name|format
argument_list|(
literal|"%s://%s:%d/%s/%s"
argument_list|,
name|Constants
operator|.
name|OZONE_URI_SCHEME
argument_list|,
name|host
argument_list|,
name|port
argument_list|,
name|volumeName
argument_list|,
name|bucketName
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|FS_DEFAULT_NAME_KEY
argument_list|,
name|uri
argument_list|)
expr_stmt|;
name|fs
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|teardown ()
specifier|public
specifier|static
name|void
name|teardown
parameter_list|()
throws|throws
name|IOException
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|storageHandler
operator|.
name|close
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileSystemInit ()
specifier|public
name|void
name|testFileSystemInit
parameter_list|()
throws|throws
name|IOException
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|fs
operator|instanceof
name|OzoneFileSystem
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|Constants
operator|.
name|OZONE_URI_SCHEME
argument_list|,
name|fs
operator|.
name|getUri
argument_list|()
operator|.
name|getScheme
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOzFsReadWrite ()
specifier|public
name|void
name|testOzFsReadWrite
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|currentTime
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
name|int
name|stringLen
init|=
literal|20
decl_stmt|;
name|String
name|data
init|=
name|RandomStringUtils
operator|.
name|randomAlphanumeric
argument_list|(
name|stringLen
argument_list|)
decl_stmt|;
name|String
name|filePath
init|=
name|RandomStringUtils
operator|.
name|randomAlphanumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/"
operator|+
name|filePath
argument_list|)
decl_stmt|;
try|try
init|(
name|FSDataOutputStream
name|stream
init|=
name|fs
operator|.
name|create
argument_list|(
name|path
argument_list|)
init|)
block|{
name|stream
operator|.
name|writeBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|status
operator|.
name|getModificationTime
argument_list|()
operator|<
name|currentTime
argument_list|)
expr_stmt|;
try|try
init|(
name|FSDataInputStream
name|inputStream
init|=
name|fs
operator|.
name|open
argument_list|(
name|path
argument_list|)
init|)
block|{
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|stringLen
index|]
decl_stmt|;
name|inputStream
operator|.
name|readFully
argument_list|(
literal|0
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
name|String
name|out
init|=
operator|new
name|String
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|buffer
operator|.
name|length
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|data
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDirectory ()
specifier|public
name|void
name|testDirectory
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|dirPath
init|=
name|RandomStringUtils
operator|.
name|randomAlphanumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/"
operator|+
name|dirPath
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|fs
operator|.
name|mkdirs
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|status
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|status
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|FileStatus
index|[]
name|statusList
init|=
name|fs
operator|.
name|listStatus
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|statusList
operator|.
name|length
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|status
argument_list|,
name|statusList
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

