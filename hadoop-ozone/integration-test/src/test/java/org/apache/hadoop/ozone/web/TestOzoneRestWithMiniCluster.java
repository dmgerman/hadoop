begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.web
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
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Charsets
operator|.
name|UTF_8
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
name|fs
operator|.
name|contract
operator|.
name|ContractTestUtils
operator|.
name|dataset
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
name|OzoneConsts
operator|.
name|CHUNK_SIZE
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
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
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
name|hdds
operator|.
name|client
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
name|hdds
operator|.
name|client
operator|.
name|ReplicationFactor
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
name|client
operator|.
name|ReplicationType
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
name|client
operator|.
name|io
operator|.
name|OzoneInputStream
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
name|io
operator|.
name|OzoneOutputStream
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
name|protocol
operator|.
name|ClientProtocol
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
name|OzoneVolume
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
name|rpc
operator|.
name|RpcClient
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
name|ExpectedException
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_comment
comment|/**  * End-to-end testing of Ozone REST operations.  */
end_comment

begin_class
DECL|class|TestOzoneRestWithMiniCluster
specifier|public
class|class
name|TestOzoneRestWithMiniCluster
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
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|OzoneConfiguration
name|conf
decl_stmt|;
DECL|field|client
specifier|private
specifier|static
name|ClientProtocol
name|client
decl_stmt|;
DECL|field|replicationFactor
specifier|private
specifier|static
name|ReplicationFactor
name|replicationFactor
init|=
name|ReplicationFactor
operator|.
name|ONE
decl_stmt|;
DECL|field|replicationType
specifier|private
specifier|static
name|ReplicationType
name|replicationType
init|=
name|ReplicationType
operator|.
name|RATIS
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
name|cluster
operator|.
name|waitForClusterToBeReady
argument_list|()
expr_stmt|;
name|client
operator|=
operator|new
name|RpcClient
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|shutdown ()
specifier|public
specifier|static
name|void
name|shutdown
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|IOException
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
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateAndGetVolume ()
specifier|public
name|void
name|testCreateAndGetVolume
parameter_list|()
throws|throws
name|Exception
block|{
name|createAndGetVolume
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateAndGetBucket ()
specifier|public
name|void
name|testCreateAndGetBucket
parameter_list|()
throws|throws
name|Exception
block|{
name|OzoneVolume
name|volume
init|=
name|createAndGetVolume
argument_list|()
decl_stmt|;
name|createAndGetBucket
argument_list|(
name|volume
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
name|Exception
block|{
name|String
name|keyName
init|=
name|nextId
argument_list|(
literal|"key"
argument_list|)
decl_stmt|;
name|String
name|keyData
init|=
name|nextId
argument_list|(
literal|"data"
argument_list|)
decl_stmt|;
name|OzoneVolume
name|volume
init|=
name|createAndGetVolume
argument_list|()
decl_stmt|;
name|OzoneBucket
name|bucket
init|=
name|createAndGetBucket
argument_list|(
name|volume
argument_list|)
decl_stmt|;
name|putKey
argument_list|(
name|bucket
argument_list|,
name|keyName
argument_list|,
name|keyData
argument_list|)
expr_stmt|;
block|}
DECL|method|putKey (OzoneBucket bucket, String keyName, String keyData)
specifier|private
name|void
name|putKey
parameter_list|(
name|OzoneBucket
name|bucket
parameter_list|,
name|String
name|keyName
parameter_list|,
name|String
name|keyData
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|OzoneOutputStream
name|ozoneOutputStream
init|=
name|bucket
operator|.
name|createKey
argument_list|(
name|keyName
argument_list|,
literal|0
argument_list|,
name|replicationType
argument_list|,
name|replicationFactor
argument_list|)
init|;
name|InputStream
name|inputStream
operator|=
name|IOUtils
operator|.
name|toInputStream
argument_list|(
name|keyData
argument_list|,
name|UTF_8
argument_list|)
init|)
block|{
name|IOUtils
operator|.
name|copy
argument_list|(
name|inputStream
argument_list|,
name|ozoneOutputStream
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|InputStream
name|inputStream
init|=
name|IOUtils
operator|.
name|toInputStream
argument_list|(
name|keyData
argument_list|,
name|UTF_8
argument_list|)
init|;
name|OzoneInputStream
name|ozoneInputStream
operator|=
name|bucket
operator|.
name|readKey
argument_list|(
name|keyName
argument_list|)
init|)
block|{
name|IOUtils
operator|.
name|contentEquals
argument_list|(
name|ozoneInputStream
argument_list|,
name|inputStream
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testPutAndGetEmptyKey ()
specifier|public
name|void
name|testPutAndGetEmptyKey
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|keyName
init|=
name|nextId
argument_list|(
literal|"key"
argument_list|)
decl_stmt|;
name|String
name|keyData
init|=
literal|""
decl_stmt|;
name|OzoneVolume
name|volume
init|=
name|createAndGetVolume
argument_list|()
decl_stmt|;
name|OzoneBucket
name|bucket
init|=
name|createAndGetBucket
argument_list|(
name|volume
argument_list|)
decl_stmt|;
name|putKey
argument_list|(
name|bucket
argument_list|,
name|keyName
argument_list|,
name|keyData
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPutAndGetMultiChunkKey ()
specifier|public
name|void
name|testPutAndGetMultiChunkKey
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|keyName
init|=
name|nextId
argument_list|(
literal|"key"
argument_list|)
decl_stmt|;
name|int
name|keyDataLen
init|=
literal|3
operator|*
name|CHUNK_SIZE
decl_stmt|;
name|String
name|keyData
init|=
name|buildKeyData
argument_list|(
name|keyDataLen
argument_list|)
decl_stmt|;
name|OzoneVolume
name|volume
init|=
name|createAndGetVolume
argument_list|()
decl_stmt|;
name|OzoneBucket
name|bucket
init|=
name|createAndGetBucket
argument_list|(
name|volume
argument_list|)
decl_stmt|;
name|putKey
argument_list|(
name|bucket
argument_list|,
name|keyName
argument_list|,
name|keyData
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPutAndGetMultiChunkKeyLastChunkPartial ()
specifier|public
name|void
name|testPutAndGetMultiChunkKeyLastChunkPartial
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|keyName
init|=
name|nextId
argument_list|(
literal|"key"
argument_list|)
decl_stmt|;
name|int
name|keyDataLen
init|=
call|(
name|int
call|)
argument_list|(
literal|2.5
operator|*
name|CHUNK_SIZE
argument_list|)
decl_stmt|;
name|String
name|keyData
init|=
name|buildKeyData
argument_list|(
name|keyDataLen
argument_list|)
decl_stmt|;
name|OzoneVolume
name|volume
init|=
name|createAndGetVolume
argument_list|()
decl_stmt|;
name|OzoneBucket
name|bucket
init|=
name|createAndGetBucket
argument_list|(
name|volume
argument_list|)
decl_stmt|;
name|putKey
argument_list|(
name|bucket
argument_list|,
name|keyName
argument_list|,
name|keyData
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReplaceKey ()
specifier|public
name|void
name|testReplaceKey
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|keyName
init|=
name|nextId
argument_list|(
literal|"key"
argument_list|)
decl_stmt|;
name|int
name|keyDataLen
init|=
call|(
name|int
call|)
argument_list|(
literal|2.5
operator|*
name|CHUNK_SIZE
argument_list|)
decl_stmt|;
name|String
name|keyData
init|=
name|buildKeyData
argument_list|(
name|keyDataLen
argument_list|)
decl_stmt|;
name|OzoneVolume
name|volume
init|=
name|createAndGetVolume
argument_list|()
decl_stmt|;
name|OzoneBucket
name|bucket
init|=
name|createAndGetBucket
argument_list|(
name|volume
argument_list|)
decl_stmt|;
name|putKey
argument_list|(
name|bucket
argument_list|,
name|keyName
argument_list|,
name|keyData
argument_list|)
expr_stmt|;
comment|// Replace key with data consisting of fewer chunks.
name|keyDataLen
operator|=
call|(
name|int
call|)
argument_list|(
literal|1.5
operator|*
name|CHUNK_SIZE
argument_list|)
expr_stmt|;
name|keyData
operator|=
name|buildKeyData
argument_list|(
name|keyDataLen
argument_list|)
expr_stmt|;
name|putKey
argument_list|(
name|bucket
argument_list|,
name|keyName
argument_list|,
name|keyData
argument_list|)
expr_stmt|;
comment|// Replace key with data consisting of more chunks.
name|keyDataLen
operator|=
call|(
name|int
call|)
argument_list|(
literal|3.5
operator|*
name|CHUNK_SIZE
argument_list|)
expr_stmt|;
name|keyData
operator|=
name|buildKeyData
argument_list|(
name|keyDataLen
argument_list|)
expr_stmt|;
name|putKey
argument_list|(
name|bucket
argument_list|,
name|keyName
argument_list|,
name|keyData
argument_list|)
expr_stmt|;
block|}
DECL|method|createAndGetVolume ()
specifier|private
name|OzoneVolume
name|createAndGetVolume
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|volumeName
init|=
name|nextId
argument_list|(
literal|"volume"
argument_list|)
decl_stmt|;
name|VolumeArgs
name|volumeArgs
init|=
name|VolumeArgs
operator|.
name|newBuilder
argument_list|()
operator|.
name|setOwner
argument_list|(
literal|"bilbo"
argument_list|)
operator|.
name|setQuota
argument_list|(
literal|"100TB"
argument_list|)
operator|.
name|setAdmin
argument_list|(
literal|"hdfs"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|client
operator|.
name|createVolume
argument_list|(
name|volumeName
argument_list|,
name|volumeArgs
argument_list|)
expr_stmt|;
name|OzoneVolume
name|volume
init|=
name|client
operator|.
name|getVolumeDetails
argument_list|(
name|volumeName
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|volumeName
argument_list|,
name|volume
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|volume
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bilbo"
argument_list|,
name|volume
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|volume
operator|.
name|getQuota
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|OzoneQuota
operator|.
name|parseQuota
argument_list|(
literal|"100TB"
argument_list|)
operator|.
name|sizeInBytes
argument_list|()
argument_list|,
name|volume
operator|.
name|getQuota
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|volume
return|;
block|}
DECL|method|createAndGetBucket (OzoneVolume vol)
specifier|private
name|OzoneBucket
name|createAndGetBucket
parameter_list|(
name|OzoneVolume
name|vol
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|bucketName
init|=
name|nextId
argument_list|(
literal|"bucket"
argument_list|)
decl_stmt|;
name|vol
operator|.
name|createBucket
argument_list|(
name|bucketName
argument_list|)
expr_stmt|;
name|OzoneBucket
name|bucket
init|=
name|vol
operator|.
name|getBucket
argument_list|(
name|bucketName
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|bucket
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|bucketName
argument_list|,
name|bucket
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|bucket
return|;
block|}
comment|/**    * Creates sample key data of the specified length.  The data is a string of    * printable ASCII characters.  This makes it easy to debug through visual    * inspection of the chunk files if a test fails.    *    * @param keyDataLen desired length of key data    * @return string of printable ASCII characters of the specified length    */
DECL|method|buildKeyData (int keyDataLen)
specifier|private
specifier|static
name|String
name|buildKeyData
parameter_list|(
name|int
name|keyDataLen
parameter_list|)
block|{
return|return
operator|new
name|String
argument_list|(
name|dataset
argument_list|(
name|keyDataLen
argument_list|,
literal|33
argument_list|,
literal|93
argument_list|)
argument_list|,
name|UTF_8
argument_list|)
return|;
block|}
comment|/**    * Generates identifiers unique enough for use in tests, so that individual    * tests don't collide on each others' data in the shared mini-cluster.    *    * @param idPrefix prefix to put in front of ID    * @return unique ID generated by appending a suffix to the given prefix    */
DECL|method|nextId (String idPrefix)
specifier|private
specifier|static
name|String
name|nextId
parameter_list|(
name|String
name|idPrefix
parameter_list|)
block|{
return|return
operator|(
name|idPrefix
operator|+
name|RandomStringUtils
operator|.
name|random
argument_list|(
literal|5
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
operator|)
operator|.
name|toLowerCase
argument_list|()
return|;
block|}
block|}
end_class

end_unit

