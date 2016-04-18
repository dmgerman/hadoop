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
name|commons
operator|.
name|codec
operator|.
name|digest
operator|.
name|DigestUtils
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
name|ErrorTable
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
name|log4j
operator|.
name|Level
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
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
name|Assert
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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|java
operator|.
name|util
operator|.
name|Random
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
DECL|class|TestKeys
specifier|public
class|class
name|TestKeys
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
DECL|field|path
specifier|static
specifier|private
name|String
name|path
decl_stmt|;
DECL|field|client
specifier|private
specifier|static
name|OzoneClient
name|client
init|=
literal|null
decl_stmt|;
comment|/**    * Create a MiniDFSCluster for testing.    *    * Ozone is made active by setting OZONE_ENABLED = true and    * OZONE_HANDLER_TYPE_KEY = "local" , which uses a local    * directory to emulate Ozone backend.    *    * @throws IOException    */
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
throws|,
name|URISyntaxException
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
name|path
operator|=
name|p
operator|.
name|getPath
argument_list|()
operator|.
name|concat
argument_list|(
name|TestKeys
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
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
name|conf
operator|.
name|setBoolean
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_TRACE_ENABLED_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Logger
operator|.
name|getLogger
argument_list|(
literal|"log4j.logger.org.apache.http"
argument_list|)
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|DEBUG
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
comment|/**    * Creates a file with Random Data    *    * @return File.    */
DECL|method|createRandomDataFile (String fileName, long size)
specifier|private
name|File
name|createRandomDataFile
parameter_list|(
name|String
name|fileName
parameter_list|,
name|long
name|size
parameter_list|)
block|{
name|File
name|tmpDir
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|tmpDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|File
name|tmpFile
init|=
operator|new
name|File
argument_list|(
name|path
operator|+
literal|"/"
operator|+
name|fileName
argument_list|)
decl_stmt|;
try|try
block|{
name|FileOutputStream
name|randFile
init|=
operator|new
name|FileOutputStream
argument_list|(
name|tmpFile
argument_list|)
decl_stmt|;
name|Random
name|r
init|=
operator|new
name|Random
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
name|size
condition|;
name|x
operator|++
control|)
block|{
name|char
name|c
init|=
call|(
name|char
call|)
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
literal|26
argument_list|)
operator|+
literal|'a'
argument_list|)
decl_stmt|;
name|randFile
operator|.
name|write
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|tmpFile
return|;
block|}
DECL|class|PutHelper
specifier|private
class|class
name|PutHelper
block|{
DECL|field|vol
name|OzoneVolume
name|vol
decl_stmt|;
DECL|field|bucket
name|OzoneBucket
name|bucket
decl_stmt|;
DECL|field|file
name|File
name|file
decl_stmt|;
DECL|method|getVol ()
specifier|public
name|OzoneVolume
name|getVol
parameter_list|()
block|{
return|return
name|vol
return|;
block|}
DECL|method|getBucket ()
specifier|public
name|OzoneBucket
name|getBucket
parameter_list|()
block|{
return|return
name|bucket
return|;
block|}
DECL|method|getFile ()
specifier|public
name|File
name|getFile
parameter_list|()
block|{
return|return
name|file
return|;
block|}
comment|/**      * This function is reused in all other tests.      *      * @return Returns the name of the new key that was created.      * @throws OzoneException      */
DECL|method|putKey ()
specifier|private
name|String
name|putKey
parameter_list|()
throws|throws
name|OzoneException
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
name|vol
operator|=
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
expr_stmt|;
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
name|bucket
operator|=
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
expr_stmt|;
name|String
name|keyName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
name|file
operator|=
name|createRandomDataFile
argument_list|(
name|keyName
argument_list|,
literal|1024
argument_list|)
expr_stmt|;
name|bucket
operator|.
name|putKey
argument_list|(
name|keyName
argument_list|,
name|file
argument_list|)
expr_stmt|;
return|return
name|keyName
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testPutKey ()
specifier|public
name|void
name|testPutKey
parameter_list|()
throws|throws
name|OzoneException
block|{
name|PutHelper
name|helper
init|=
operator|new
name|PutHelper
argument_list|()
decl_stmt|;
name|helper
operator|.
name|putKey
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|helper
operator|.
name|getBucket
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|helper
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPutAndGetKey ()
specifier|public
name|void
name|testPutAndGetKey
parameter_list|()
throws|throws
name|OzoneException
throws|,
name|IOException
block|{
name|PutHelper
name|helper
init|=
operator|new
name|PutHelper
argument_list|()
decl_stmt|;
name|String
name|keyName
init|=
name|helper
operator|.
name|putKey
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|helper
operator|.
name|getBucket
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|helper
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|newFileName
init|=
name|path
operator|+
literal|"/"
operator|+
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
name|Path
name|newPath
init|=
name|Paths
operator|.
name|get
argument_list|(
name|newFileName
argument_list|)
decl_stmt|;
name|helper
operator|.
name|getBucket
argument_list|()
operator|.
name|getKey
argument_list|(
name|keyName
argument_list|,
name|newPath
argument_list|)
expr_stmt|;
name|FileInputStream
name|original
init|=
operator|new
name|FileInputStream
argument_list|(
name|helper
operator|.
name|getFile
argument_list|()
argument_list|)
decl_stmt|;
name|FileInputStream
name|downloaded
init|=
operator|new
name|FileInputStream
argument_list|(
name|newPath
operator|.
name|toFile
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|originalHash
init|=
name|DigestUtils
operator|.
name|sha256Hex
argument_list|(
name|original
argument_list|)
decl_stmt|;
name|String
name|downloadedHash
init|=
name|DigestUtils
operator|.
name|sha256Hex
argument_list|(
name|downloaded
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Sha256 does not match between original file and downloaded file."
argument_list|,
name|originalHash
argument_list|,
name|downloadedHash
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPutAndDeleteKey ()
specifier|public
name|void
name|testPutAndDeleteKey
parameter_list|()
throws|throws
name|OzoneException
throws|,
name|IOException
block|{
name|PutHelper
name|helper
init|=
operator|new
name|PutHelper
argument_list|()
decl_stmt|;
name|String
name|keyName
init|=
name|helper
operator|.
name|putKey
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|helper
operator|.
name|getBucket
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|helper
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
name|helper
operator|.
name|getBucket
argument_list|()
operator|.
name|deleteKey
argument_list|(
name|keyName
argument_list|)
expr_stmt|;
try|try
block|{
name|helper
operator|.
name|getBucket
argument_list|()
operator|.
name|getKey
argument_list|(
name|keyName
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Get Key on a deleted key should have thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OzoneException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|ex
operator|.
name|getShortMessage
argument_list|()
argument_list|,
name|ErrorTable
operator|.
name|INVALID_KEY
operator|.
name|getShortMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testPutAndListKey ()
specifier|public
name|void
name|testPutAndListKey
parameter_list|()
throws|throws
name|OzoneException
throws|,
name|IOException
block|{
name|PutHelper
name|helper
init|=
operator|new
name|PutHelper
argument_list|()
decl_stmt|;
name|helper
operator|.
name|putKey
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|helper
operator|.
name|getBucket
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|helper
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
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
name|newkeyName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
name|helper
operator|.
name|getBucket
argument_list|()
operator|.
name|putKey
argument_list|(
name|newkeyName
argument_list|,
name|helper
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|OzoneKey
argument_list|>
name|keyList
init|=
name|helper
operator|.
name|getBucket
argument_list|()
operator|.
name|listKeys
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|keyList
operator|.
name|size
argument_list|()
argument_list|,
literal|11
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

