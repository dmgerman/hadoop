begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.ââSee the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.ââThe ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.ââYou may obtain a copy of the License at  *  * ââââ http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.client.rpc
package|package
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
name|conf
operator|.
name|Configuration
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
name|crypto
operator|.
name|key
operator|.
name|KeyProvider
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
name|crypto
operator|.
name|key
operator|.
name|kms
operator|.
name|KMSClientProvider
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
name|crypto
operator|.
name|key
operator|.
name|kms
operator|.
name|server
operator|.
name|MiniKMS
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
name|hdds
operator|.
name|HddsConfigKeys
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
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
name|scm
operator|.
name|container
operator|.
name|ContainerInfo
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
name|scm
operator|.
name|protocolPB
operator|.
name|StorageContainerLocationProtocolClientSideTranslatorPB
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
name|client
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
name|client
operator|.
name|CertificateClientTestImpl
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
name|ObjectStore
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
name|OzoneClientFactory
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
name|OzoneKey
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
name|om
operator|.
name|OzoneManager
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
name|om
operator|.
name|helpers
operator|.
name|OmKeyArgs
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
name|om
operator|.
name|helpers
operator|.
name|OmKeyInfo
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
name|om
operator|.
name|helpers
operator|.
name|OmKeyLocationInfo
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|hdds
operator|.
name|HddsConfigKeys
operator|.
name|OZONE_METADATA_DIRS
import|;
end_import

begin_comment
comment|/**  * This class is to test all the public facing APIs of Ozone Client.  */
end_comment

begin_class
DECL|class|TestOzoneAtRestEncryption
specifier|public
class|class
name|TestOzoneAtRestEncryption
extends|extends
name|TestOzoneRpcClient
block|{
DECL|field|cluster
specifier|private
specifier|static
name|MiniOzoneCluster
name|cluster
init|=
literal|null
decl_stmt|;
DECL|field|miniKMS
specifier|private
specifier|static
name|MiniKMS
name|miniKMS
decl_stmt|;
DECL|field|ozClient
specifier|private
specifier|static
name|OzoneClient
name|ozClient
init|=
literal|null
decl_stmt|;
DECL|field|store
specifier|private
specifier|static
name|ObjectStore
name|store
init|=
literal|null
decl_stmt|;
DECL|field|ozoneManager
specifier|private
specifier|static
name|OzoneManager
name|ozoneManager
decl_stmt|;
specifier|private
specifier|static
name|StorageContainerLocationProtocolClientSideTranslatorPB
DECL|field|storageContainerLocationClient
name|storageContainerLocationClient
decl_stmt|;
DECL|field|SCM_ID
specifier|private
specifier|static
specifier|final
name|String
name|SCM_ID
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
DECL|field|testDir
specifier|private
specifier|static
name|File
name|testDir
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|OzoneConfiguration
name|conf
decl_stmt|;
DECL|field|TEST_KEY
specifier|private
specifier|static
specifier|final
name|String
name|TEST_KEY
init|=
literal|"key1"
decl_stmt|;
comment|/**      * Create a MiniOzoneCluster for testing.      *<p>      * Ozone is made active by setting OZONE_ENABLED = true      *      * @throws IOException      */
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
name|testDir
operator|=
name|GenericTestUtils
operator|.
name|getTestDir
argument_list|(
name|TestSecureOzoneRpcClient
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|kmsDir
init|=
operator|new
name|File
argument_list|(
name|testDir
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|kmsDir
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
name|MiniKMS
operator|.
name|Builder
name|miniKMSBuilder
init|=
operator|new
name|MiniKMS
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|miniKMS
operator|=
name|miniKMSBuilder
operator|.
name|setKmsConfDir
argument_list|(
name|kmsDir
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|miniKMS
operator|.
name|start
argument_list|()
expr_stmt|;
name|OzoneManager
operator|.
name|setTestSecureOmFlag
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|=
operator|new
name|OzoneConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SECURITY_KEY_PROVIDER_PATH
argument_list|,
name|getKeyProviderURI
argument_list|(
name|miniKMS
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|HddsConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|,
name|testDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|HddsConfigKeys
operator|.
name|HDDS_BLOCK_TOKEN_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OZONE_METADATA_DIRS
argument_list|,
name|testDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|CertificateClientTestImpl
name|certificateClientTest
init|=
operator|new
name|CertificateClientTestImpl
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|cluster
operator|=
name|MiniOzoneCluster
operator|.
name|newBuilder
argument_list|(
name|conf
argument_list|)
operator|.
name|setNumDatanodes
argument_list|(
literal|10
argument_list|)
operator|.
name|setScmId
argument_list|(
name|SCM_ID
argument_list|)
operator|.
name|setCertificateClient
argument_list|(
name|certificateClientTest
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|getOzoneManager
argument_list|()
operator|.
name|startSecretManager
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitForClusterToBeReady
argument_list|()
expr_stmt|;
name|ozClient
operator|=
name|OzoneClientFactory
operator|.
name|getRpcClient
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|store
operator|=
name|ozClient
operator|.
name|getObjectStore
argument_list|()
expr_stmt|;
name|storageContainerLocationClient
operator|=
name|cluster
operator|.
name|getStorageContainerLocationClient
argument_list|()
expr_stmt|;
name|ozoneManager
operator|=
name|cluster
operator|.
name|getOzoneManager
argument_list|()
expr_stmt|;
name|TestOzoneRpcClient
operator|.
name|setCluster
argument_list|(
name|cluster
argument_list|)
expr_stmt|;
name|TestOzoneRpcClient
operator|.
name|setOzClient
argument_list|(
name|ozClient
argument_list|)
expr_stmt|;
name|TestOzoneRpcClient
operator|.
name|setOzoneManager
argument_list|(
name|ozoneManager
argument_list|)
expr_stmt|;
name|TestOzoneRpcClient
operator|.
name|setStorageContainerLocationClient
argument_list|(
name|storageContainerLocationClient
argument_list|)
expr_stmt|;
name|TestOzoneRpcClient
operator|.
name|setStore
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|TestOzoneRpcClient
operator|.
name|setScmId
argument_list|(
name|SCM_ID
argument_list|)
expr_stmt|;
comment|// create test key
name|createKey
argument_list|(
name|TEST_KEY
argument_list|,
name|cluster
operator|.
name|getOzoneManager
argument_list|()
operator|.
name|getKmsProvider
argument_list|()
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**      * Close OzoneClient and shutdown MiniOzoneCluster.      */
annotation|@
name|AfterClass
DECL|method|shutdown ()
specifier|public
specifier|static
name|void
name|shutdown
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|ozClient
operator|!=
literal|null
condition|)
block|{
name|ozClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|storageContainerLocationClient
operator|!=
literal|null
condition|)
block|{
name|storageContainerLocationClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
if|if
condition|(
name|miniKMS
operator|!=
literal|null
condition|)
block|{
name|miniKMS
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testPutKeyWithEncryption ()
specifier|public
name|void
name|testPutKeyWithEncryption
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|volumeName
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|bucketName
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|long
name|currentTime
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
name|String
name|value
init|=
literal|"sample value"
decl_stmt|;
name|store
operator|.
name|createVolume
argument_list|(
name|volumeName
argument_list|)
expr_stmt|;
name|OzoneVolume
name|volume
init|=
name|store
operator|.
name|getVolume
argument_list|(
name|volumeName
argument_list|)
decl_stmt|;
name|BucketArgs
name|bucketArgs
init|=
name|BucketArgs
operator|.
name|newBuilder
argument_list|()
operator|.
name|setBucketEncryptionKey
argument_list|(
name|TEST_KEY
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|volume
operator|.
name|createBucket
argument_list|(
name|bucketName
argument_list|,
name|bucketArgs
argument_list|)
expr_stmt|;
name|OzoneBucket
name|bucket
init|=
name|volume
operator|.
name|getBucket
argument_list|(
name|bucketName
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|String
name|keyName
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
try|try
init|(
name|OzoneOutputStream
name|out
init|=
name|bucket
operator|.
name|createKey
argument_list|(
name|keyName
argument_list|,
name|value
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
operator|.
name|length
argument_list|,
name|ReplicationType
operator|.
name|STAND_ALONE
argument_list|,
name|ReplicationFactor
operator|.
name|ONE
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
init|)
block|{
name|out
operator|.
name|write
argument_list|(
name|value
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|OzoneKey
name|key
init|=
name|bucket
operator|.
name|getKey
argument_list|(
name|keyName
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|keyName
argument_list|,
name|key
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|fileContent
decl_stmt|;
name|int
name|len
init|=
literal|0
decl_stmt|;
try|try
init|(
name|OzoneInputStream
name|is
init|=
name|bucket
operator|.
name|readKey
argument_list|(
name|keyName
argument_list|)
init|)
block|{
name|fileContent
operator|=
operator|new
name|byte
index|[
name|value
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
operator|.
name|length
index|]
expr_stmt|;
name|len
operator|=
name|is
operator|.
name|read
argument_list|(
name|fileContent
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|len
argument_list|,
name|value
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|verifyRatisReplication
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|,
name|ReplicationType
operator|.
name|STAND_ALONE
argument_list|,
name|ReplicationFactor
operator|.
name|ONE
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|value
argument_list|,
operator|new
name|String
argument_list|(
name|fileContent
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|key
operator|.
name|getCreationTime
argument_list|()
operator|>=
name|currentTime
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|key
operator|.
name|getModificationTime
argument_list|()
operator|>=
name|currentTime
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|verifyRatisReplication (String volumeName, String bucketName, String keyName, ReplicationType type, ReplicationFactor factor)
specifier|private
name|boolean
name|verifyRatisReplication
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|String
name|keyName
parameter_list|,
name|ReplicationType
name|type
parameter_list|,
name|ReplicationFactor
name|factor
parameter_list|)
throws|throws
name|IOException
block|{
name|OmKeyArgs
name|keyArgs
init|=
operator|new
name|OmKeyArgs
operator|.
name|Builder
argument_list|()
operator|.
name|setVolumeName
argument_list|(
name|volumeName
argument_list|)
operator|.
name|setBucketName
argument_list|(
name|bucketName
argument_list|)
operator|.
name|setKeyName
argument_list|(
name|keyName
argument_list|)
operator|.
name|setRefreshPipeline
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|HddsProtos
operator|.
name|ReplicationType
name|replicationType
init|=
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|valueOf
argument_list|(
name|type
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|HddsProtos
operator|.
name|ReplicationFactor
name|replicationFactor
init|=
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|valueOf
argument_list|(
name|factor
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|OmKeyInfo
name|keyInfo
init|=
name|ozoneManager
operator|.
name|lookupKey
argument_list|(
name|keyArgs
argument_list|)
decl_stmt|;
for|for
control|(
name|OmKeyLocationInfo
name|info
range|:
name|keyInfo
operator|.
name|getLatestVersionLocations
argument_list|()
operator|.
name|getLocationList
argument_list|()
control|)
block|{
name|ContainerInfo
name|container
init|=
name|storageContainerLocationClient
operator|.
name|getContainer
argument_list|(
name|info
operator|.
name|getContainerID
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|container
operator|.
name|getReplicationFactor
argument_list|()
operator|.
name|equals
argument_list|(
name|replicationFactor
argument_list|)
operator|||
operator|(
name|container
operator|.
name|getReplicationType
argument_list|()
operator|!=
name|replicationType
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|getKeyProviderURI (MiniKMS kms)
specifier|private
specifier|static
name|String
name|getKeyProviderURI
parameter_list|(
name|MiniKMS
name|kms
parameter_list|)
block|{
return|return
name|KMSClientProvider
operator|.
name|SCHEME_NAME
operator|+
literal|"://"
operator|+
name|kms
operator|.
name|getKMSUrl
argument_list|()
operator|.
name|toExternalForm
argument_list|()
operator|.
name|replace
argument_list|(
literal|"://"
argument_list|,
literal|"@"
argument_list|)
return|;
block|}
DECL|method|createKey (String keyName, KeyProvider provider, Configuration config)
specifier|private
specifier|static
name|void
name|createKey
parameter_list|(
name|String
name|keyName
parameter_list|,
name|KeyProvider
name|provider
parameter_list|,
name|Configuration
name|config
parameter_list|)
throws|throws
name|NoSuchAlgorithmException
throws|,
name|IOException
block|{
specifier|final
name|KeyProvider
operator|.
name|Options
name|options
init|=
name|KeyProvider
operator|.
name|options
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|options
operator|.
name|setDescription
argument_list|(
name|keyName
argument_list|)
expr_stmt|;
name|options
operator|.
name|setBitLength
argument_list|(
literal|128
argument_list|)
expr_stmt|;
name|provider
operator|.
name|createKey
argument_list|(
name|keyName
argument_list|,
name|options
argument_list|)
expr_stmt|;
name|provider
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

