begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
package|;
end_package

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
name|util
operator|.
name|LinkedList
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
name|KeyProviderCryptoExtension
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
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
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
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
name|server
operator|.
name|ServerUtils
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
name|OzoneAcl
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
name|exceptions
operator|.
name|OMException
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
name|exceptions
operator|.
name|OMException
operator|.
name|ResultCodes
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
name|*
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
name|junit
operator|.
name|rules
operator|.
name|TemporaryFolder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
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
name|org
operator|.
name|mockito
operator|.
name|runners
operator|.
name|MockitoJUnitRunner
import|;
end_import

begin_comment
comment|/**  * Tests BucketManagerImpl, mocks OMMetadataManager for testing.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|MockitoJUnitRunner
operator|.
name|class
argument_list|)
DECL|class|TestBucketManagerImpl
specifier|public
class|class
name|TestBucketManagerImpl
block|{
annotation|@
name|Rule
DECL|field|thrown
specifier|public
name|ExpectedException
name|thrown
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
annotation|@
name|Rule
DECL|field|folder
specifier|public
name|TemporaryFolder
name|folder
init|=
operator|new
name|TemporaryFolder
argument_list|()
decl_stmt|;
DECL|method|createNewTestPath ()
specifier|private
name|OzoneConfiguration
name|createNewTestPath
parameter_list|()
throws|throws
name|IOException
block|{
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|File
name|newFolder
init|=
name|folder
operator|.
name|newFolder
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|newFolder
operator|.
name|exists
argument_list|()
condition|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|newFolder
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ServerUtils
operator|.
name|setOzoneMetaDirPath
argument_list|(
name|conf
argument_list|,
name|newFolder
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
DECL|method|createSampleVol ()
specifier|private
name|OmMetadataManagerImpl
name|createSampleVol
parameter_list|()
throws|throws
name|IOException
block|{
name|OzoneConfiguration
name|conf
init|=
name|createNewTestPath
argument_list|()
decl_stmt|;
name|OmMetadataManagerImpl
name|metaMgr
init|=
operator|new
name|OmMetadataManagerImpl
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|String
name|volumeKey
init|=
name|metaMgr
operator|.
name|getVolumeKey
argument_list|(
literal|"sampleVol"
argument_list|)
decl_stmt|;
comment|// This is a simple hack for testing, we just test if the volume via a
comment|// null check, do not parse the value part. So just write some dummy value.
name|OmVolumeArgs
name|args
init|=
name|OmVolumeArgs
operator|.
name|newBuilder
argument_list|()
operator|.
name|setVolume
argument_list|(
literal|"sampleVol"
argument_list|)
operator|.
name|setAdminName
argument_list|(
literal|"bilbo"
argument_list|)
operator|.
name|setOwnerName
argument_list|(
literal|"bilbo"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|metaMgr
operator|.
name|getVolumeTable
argument_list|()
operator|.
name|put
argument_list|(
name|volumeKey
argument_list|,
name|args
argument_list|)
expr_stmt|;
return|return
name|metaMgr
return|;
block|}
annotation|@
name|Test
DECL|method|testCreateBucketWithoutVolume ()
specifier|public
name|void
name|testCreateBucketWithoutVolume
parameter_list|()
throws|throws
name|Exception
block|{
name|thrown
operator|.
name|expectMessage
argument_list|(
literal|"Volume doesn't exist"
argument_list|)
expr_stmt|;
name|OzoneConfiguration
name|conf
init|=
name|createNewTestPath
argument_list|()
decl_stmt|;
name|OmMetadataManagerImpl
name|metaMgr
init|=
operator|new
name|OmMetadataManagerImpl
argument_list|(
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|BucketManager
name|bucketManager
init|=
operator|new
name|BucketManagerImpl
argument_list|(
name|metaMgr
argument_list|)
decl_stmt|;
name|OmBucketInfo
name|bucketInfo
init|=
name|OmBucketInfo
operator|.
name|newBuilder
argument_list|()
operator|.
name|setVolumeName
argument_list|(
literal|"sampleVol"
argument_list|)
operator|.
name|setBucketName
argument_list|(
literal|"bucketOne"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|bucketManager
operator|.
name|createBucket
argument_list|(
name|bucketInfo
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OMException
name|omEx
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ResultCodes
operator|.
name|VOLUME_NOT_FOUND
argument_list|,
name|omEx
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|omEx
throw|;
block|}
finally|finally
block|{
name|metaMgr
operator|.
name|getStore
argument_list|()
operator|.
name|close
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
name|OmMetadataManagerImpl
name|metaMgr
init|=
name|createSampleVol
argument_list|()
decl_stmt|;
name|KeyProviderCryptoExtension
name|kmsProvider
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|KeyProviderCryptoExtension
operator|.
name|class
argument_list|)
decl_stmt|;
name|String
name|testBekName
init|=
literal|"key1"
decl_stmt|;
name|String
name|testCipherName
init|=
literal|"AES/CTR/NoPadding"
decl_stmt|;
name|KeyProvider
operator|.
name|Metadata
name|mockMetadata
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|KeyProvider
operator|.
name|Metadata
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|kmsProvider
operator|.
name|getMetadata
argument_list|(
name|testBekName
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mockMetadata
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|mockMetadata
operator|.
name|getCipher
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|testCipherName
argument_list|)
expr_stmt|;
name|BucketManager
name|bucketManager
init|=
operator|new
name|BucketManagerImpl
argument_list|(
name|metaMgr
argument_list|,
name|kmsProvider
argument_list|)
decl_stmt|;
name|OmBucketInfo
name|bucketInfo
init|=
name|OmBucketInfo
operator|.
name|newBuilder
argument_list|()
operator|.
name|setVolumeName
argument_list|(
literal|"sampleVol"
argument_list|)
operator|.
name|setBucketName
argument_list|(
literal|"bucketOne"
argument_list|)
operator|.
name|setBucketEncryptionKey
argument_list|(
operator|new
name|BucketEncryptionKeyInfo
operator|.
name|Builder
argument_list|()
operator|.
name|setKeyName
argument_list|(
literal|"key1"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|bucketManager
operator|.
name|createBucket
argument_list|(
name|bucketInfo
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|bucketManager
operator|.
name|getBucketInfo
argument_list|(
literal|"sampleVol"
argument_list|,
literal|"bucketOne"
argument_list|)
argument_list|)
expr_stmt|;
name|OmBucketInfo
name|bucketInfoRead
init|=
name|bucketManager
operator|.
name|getBucketInfo
argument_list|(
literal|"sampleVol"
argument_list|,
literal|"bucketOne"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|bucketInfoRead
operator|.
name|getEncryptionKeyInfo
argument_list|()
operator|.
name|getKeyName
argument_list|()
operator|.
name|equals
argument_list|(
name|bucketInfo
operator|.
name|getEncryptionKeyInfo
argument_list|()
operator|.
name|getKeyName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|metaMgr
operator|.
name|getStore
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateEncryptedBucket ()
specifier|public
name|void
name|testCreateEncryptedBucket
parameter_list|()
throws|throws
name|Exception
block|{
name|OmMetadataManagerImpl
name|metaMgr
init|=
name|createSampleVol
argument_list|()
decl_stmt|;
name|BucketManager
name|bucketManager
init|=
operator|new
name|BucketManagerImpl
argument_list|(
name|metaMgr
argument_list|)
decl_stmt|;
name|OmBucketInfo
name|bucketInfo
init|=
name|OmBucketInfo
operator|.
name|newBuilder
argument_list|()
operator|.
name|setVolumeName
argument_list|(
literal|"sampleVol"
argument_list|)
operator|.
name|setBucketName
argument_list|(
literal|"bucketOne"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|bucketManager
operator|.
name|createBucket
argument_list|(
name|bucketInfo
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|bucketManager
operator|.
name|getBucketInfo
argument_list|(
literal|"sampleVol"
argument_list|,
literal|"bucketOne"
argument_list|)
argument_list|)
expr_stmt|;
name|metaMgr
operator|.
name|getStore
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateAlreadyExistingBucket ()
specifier|public
name|void
name|testCreateAlreadyExistingBucket
parameter_list|()
throws|throws
name|Exception
block|{
name|thrown
operator|.
name|expectMessage
argument_list|(
literal|"Bucket already exist"
argument_list|)
expr_stmt|;
name|OmMetadataManagerImpl
name|metaMgr
init|=
name|createSampleVol
argument_list|()
decl_stmt|;
try|try
block|{
name|BucketManager
name|bucketManager
init|=
operator|new
name|BucketManagerImpl
argument_list|(
name|metaMgr
argument_list|)
decl_stmt|;
name|OmBucketInfo
name|bucketInfo
init|=
name|OmBucketInfo
operator|.
name|newBuilder
argument_list|()
operator|.
name|setVolumeName
argument_list|(
literal|"sampleVol"
argument_list|)
operator|.
name|setBucketName
argument_list|(
literal|"bucketOne"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|bucketManager
operator|.
name|createBucket
argument_list|(
name|bucketInfo
argument_list|)
expr_stmt|;
name|bucketManager
operator|.
name|createBucket
argument_list|(
name|bucketInfo
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OMException
name|omEx
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ResultCodes
operator|.
name|BUCKET_ALREADY_EXISTS
argument_list|,
name|omEx
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|omEx
throw|;
block|}
finally|finally
block|{
name|metaMgr
operator|.
name|getStore
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testGetBucketInfoForInvalidBucket ()
specifier|public
name|void
name|testGetBucketInfoForInvalidBucket
parameter_list|()
throws|throws
name|Exception
block|{
name|thrown
operator|.
name|expectMessage
argument_list|(
literal|"Bucket not found"
argument_list|)
expr_stmt|;
name|OmMetadataManagerImpl
name|metaMgr
init|=
name|createSampleVol
argument_list|()
decl_stmt|;
try|try
block|{
name|BucketManager
name|bucketManager
init|=
operator|new
name|BucketManagerImpl
argument_list|(
name|metaMgr
argument_list|)
decl_stmt|;
name|bucketManager
operator|.
name|getBucketInfo
argument_list|(
literal|"sampleVol"
argument_list|,
literal|"bucketOne"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OMException
name|omEx
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ResultCodes
operator|.
name|BUCKET_NOT_FOUND
argument_list|,
name|omEx
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|omEx
throw|;
block|}
finally|finally
block|{
name|metaMgr
operator|.
name|getStore
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testGetBucketInfo ()
specifier|public
name|void
name|testGetBucketInfo
parameter_list|()
throws|throws
name|Exception
block|{
name|OmMetadataManagerImpl
name|metaMgr
init|=
name|createSampleVol
argument_list|()
decl_stmt|;
name|BucketManager
name|bucketManager
init|=
operator|new
name|BucketManagerImpl
argument_list|(
name|metaMgr
argument_list|)
decl_stmt|;
name|OmBucketInfo
name|bucketInfo
init|=
name|OmBucketInfo
operator|.
name|newBuilder
argument_list|()
operator|.
name|setVolumeName
argument_list|(
literal|"sampleVol"
argument_list|)
operator|.
name|setBucketName
argument_list|(
literal|"bucketOne"
argument_list|)
operator|.
name|setStorageType
argument_list|(
name|StorageType
operator|.
name|DISK
argument_list|)
operator|.
name|setIsVersionEnabled
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|bucketManager
operator|.
name|createBucket
argument_list|(
name|bucketInfo
argument_list|)
expr_stmt|;
name|OmBucketInfo
name|result
init|=
name|bucketManager
operator|.
name|getBucketInfo
argument_list|(
literal|"sampleVol"
argument_list|,
literal|"bucketOne"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"sampleVol"
argument_list|,
name|result
operator|.
name|getVolumeName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"bucketOne"
argument_list|,
name|result
operator|.
name|getBucketName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|StorageType
operator|.
name|DISK
argument_list|,
name|result
operator|.
name|getStorageType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|result
operator|.
name|getIsVersionEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|metaMgr
operator|.
name|getStore
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSetBucketPropertyAddACL ()
specifier|public
name|void
name|testSetBucketPropertyAddACL
parameter_list|()
throws|throws
name|Exception
block|{
name|OmMetadataManagerImpl
name|metaMgr
init|=
name|createSampleVol
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|acls
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|OzoneAcl
name|ozoneAcl
init|=
operator|new
name|OzoneAcl
argument_list|(
name|OzoneAcl
operator|.
name|OzoneACLType
operator|.
name|USER
argument_list|,
literal|"root"
argument_list|,
name|OzoneAcl
operator|.
name|OzoneACLRights
operator|.
name|READ
argument_list|)
decl_stmt|;
name|acls
operator|.
name|add
argument_list|(
name|ozoneAcl
argument_list|)
expr_stmt|;
name|BucketManager
name|bucketManager
init|=
operator|new
name|BucketManagerImpl
argument_list|(
name|metaMgr
argument_list|)
decl_stmt|;
name|OmBucketInfo
name|bucketInfo
init|=
name|OmBucketInfo
operator|.
name|newBuilder
argument_list|()
operator|.
name|setVolumeName
argument_list|(
literal|"sampleVol"
argument_list|)
operator|.
name|setBucketName
argument_list|(
literal|"bucketOne"
argument_list|)
operator|.
name|setAcls
argument_list|(
name|acls
argument_list|)
operator|.
name|setStorageType
argument_list|(
name|StorageType
operator|.
name|DISK
argument_list|)
operator|.
name|setIsVersionEnabled
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|bucketManager
operator|.
name|createBucket
argument_list|(
name|bucketInfo
argument_list|)
expr_stmt|;
name|OmBucketInfo
name|result
init|=
name|bucketManager
operator|.
name|getBucketInfo
argument_list|(
literal|"sampleVol"
argument_list|,
literal|"bucketOne"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"sampleVol"
argument_list|,
name|result
operator|.
name|getVolumeName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"bucketOne"
argument_list|,
name|result
operator|.
name|getBucketName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|getAcls
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|addAcls
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|OzoneAcl
name|newAcl
init|=
operator|new
name|OzoneAcl
argument_list|(
name|OzoneAcl
operator|.
name|OzoneACLType
operator|.
name|USER
argument_list|,
literal|"ozone"
argument_list|,
name|OzoneAcl
operator|.
name|OzoneACLRights
operator|.
name|READ
argument_list|)
decl_stmt|;
name|addAcls
operator|.
name|add
argument_list|(
name|newAcl
argument_list|)
expr_stmt|;
name|OmBucketArgs
name|bucketArgs
init|=
name|OmBucketArgs
operator|.
name|newBuilder
argument_list|()
operator|.
name|setVolumeName
argument_list|(
literal|"sampleVol"
argument_list|)
operator|.
name|setBucketName
argument_list|(
literal|"bucketOne"
argument_list|)
operator|.
name|setAddAcls
argument_list|(
name|addAcls
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|bucketManager
operator|.
name|setBucketProperty
argument_list|(
name|bucketArgs
argument_list|)
expr_stmt|;
name|OmBucketInfo
name|updatedResult
init|=
name|bucketManager
operator|.
name|getBucketInfo
argument_list|(
literal|"sampleVol"
argument_list|,
literal|"bucketOne"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|updatedResult
operator|.
name|getAcls
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|updatedResult
operator|.
name|getAcls
argument_list|()
operator|.
name|contains
argument_list|(
name|newAcl
argument_list|)
argument_list|)
expr_stmt|;
name|metaMgr
operator|.
name|getStore
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSetBucketPropertyRemoveACL ()
specifier|public
name|void
name|testSetBucketPropertyRemoveACL
parameter_list|()
throws|throws
name|Exception
block|{
name|OmMetadataManagerImpl
name|metaMgr
init|=
name|createSampleVol
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|acls
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|OzoneAcl
name|aclOne
init|=
operator|new
name|OzoneAcl
argument_list|(
name|OzoneAcl
operator|.
name|OzoneACLType
operator|.
name|USER
argument_list|,
literal|"root"
argument_list|,
name|OzoneAcl
operator|.
name|OzoneACLRights
operator|.
name|READ
argument_list|)
decl_stmt|;
name|OzoneAcl
name|aclTwo
init|=
operator|new
name|OzoneAcl
argument_list|(
name|OzoneAcl
operator|.
name|OzoneACLType
operator|.
name|USER
argument_list|,
literal|"ozone"
argument_list|,
name|OzoneAcl
operator|.
name|OzoneACLRights
operator|.
name|READ
argument_list|)
decl_stmt|;
name|acls
operator|.
name|add
argument_list|(
name|aclOne
argument_list|)
expr_stmt|;
name|acls
operator|.
name|add
argument_list|(
name|aclTwo
argument_list|)
expr_stmt|;
name|BucketManager
name|bucketManager
init|=
operator|new
name|BucketManagerImpl
argument_list|(
name|metaMgr
argument_list|)
decl_stmt|;
name|OmBucketInfo
name|bucketInfo
init|=
name|OmBucketInfo
operator|.
name|newBuilder
argument_list|()
operator|.
name|setVolumeName
argument_list|(
literal|"sampleVol"
argument_list|)
operator|.
name|setBucketName
argument_list|(
literal|"bucketOne"
argument_list|)
operator|.
name|setAcls
argument_list|(
name|acls
argument_list|)
operator|.
name|setStorageType
argument_list|(
name|StorageType
operator|.
name|DISK
argument_list|)
operator|.
name|setIsVersionEnabled
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|bucketManager
operator|.
name|createBucket
argument_list|(
name|bucketInfo
argument_list|)
expr_stmt|;
name|OmBucketInfo
name|result
init|=
name|bucketManager
operator|.
name|getBucketInfo
argument_list|(
literal|"sampleVol"
argument_list|,
literal|"bucketOne"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|result
operator|.
name|getAcls
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|removeAcls
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|removeAcls
operator|.
name|add
argument_list|(
name|aclTwo
argument_list|)
expr_stmt|;
name|OmBucketArgs
name|bucketArgs
init|=
name|OmBucketArgs
operator|.
name|newBuilder
argument_list|()
operator|.
name|setVolumeName
argument_list|(
literal|"sampleVol"
argument_list|)
operator|.
name|setBucketName
argument_list|(
literal|"bucketOne"
argument_list|)
operator|.
name|setRemoveAcls
argument_list|(
name|removeAcls
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|bucketManager
operator|.
name|setBucketProperty
argument_list|(
name|bucketArgs
argument_list|)
expr_stmt|;
name|OmBucketInfo
name|updatedResult
init|=
name|bucketManager
operator|.
name|getBucketInfo
argument_list|(
literal|"sampleVol"
argument_list|,
literal|"bucketOne"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|updatedResult
operator|.
name|getAcls
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|updatedResult
operator|.
name|getAcls
argument_list|()
operator|.
name|contains
argument_list|(
name|aclTwo
argument_list|)
argument_list|)
expr_stmt|;
name|metaMgr
operator|.
name|getStore
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSetBucketPropertyChangeStorageType ()
specifier|public
name|void
name|testSetBucketPropertyChangeStorageType
parameter_list|()
throws|throws
name|Exception
block|{
name|OmMetadataManagerImpl
name|metaMgr
init|=
name|createSampleVol
argument_list|()
decl_stmt|;
name|BucketManager
name|bucketManager
init|=
operator|new
name|BucketManagerImpl
argument_list|(
name|metaMgr
argument_list|)
decl_stmt|;
name|OmBucketInfo
name|bucketInfo
init|=
name|OmBucketInfo
operator|.
name|newBuilder
argument_list|()
operator|.
name|setVolumeName
argument_list|(
literal|"sampleVol"
argument_list|)
operator|.
name|setBucketName
argument_list|(
literal|"bucketOne"
argument_list|)
operator|.
name|setStorageType
argument_list|(
name|StorageType
operator|.
name|DISK
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|bucketManager
operator|.
name|createBucket
argument_list|(
name|bucketInfo
argument_list|)
expr_stmt|;
name|OmBucketInfo
name|result
init|=
name|bucketManager
operator|.
name|getBucketInfo
argument_list|(
literal|"sampleVol"
argument_list|,
literal|"bucketOne"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|StorageType
operator|.
name|DISK
argument_list|,
name|result
operator|.
name|getStorageType
argument_list|()
argument_list|)
expr_stmt|;
name|OmBucketArgs
name|bucketArgs
init|=
name|OmBucketArgs
operator|.
name|newBuilder
argument_list|()
operator|.
name|setVolumeName
argument_list|(
literal|"sampleVol"
argument_list|)
operator|.
name|setBucketName
argument_list|(
literal|"bucketOne"
argument_list|)
operator|.
name|setStorageType
argument_list|(
name|StorageType
operator|.
name|SSD
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|bucketManager
operator|.
name|setBucketProperty
argument_list|(
name|bucketArgs
argument_list|)
expr_stmt|;
name|OmBucketInfo
name|updatedResult
init|=
name|bucketManager
operator|.
name|getBucketInfo
argument_list|(
literal|"sampleVol"
argument_list|,
literal|"bucketOne"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|StorageType
operator|.
name|SSD
argument_list|,
name|updatedResult
operator|.
name|getStorageType
argument_list|()
argument_list|)
expr_stmt|;
name|metaMgr
operator|.
name|getStore
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSetBucketPropertyChangeVersioning ()
specifier|public
name|void
name|testSetBucketPropertyChangeVersioning
parameter_list|()
throws|throws
name|Exception
block|{
name|OmMetadataManagerImpl
name|metaMgr
init|=
name|createSampleVol
argument_list|()
decl_stmt|;
name|BucketManager
name|bucketManager
init|=
operator|new
name|BucketManagerImpl
argument_list|(
name|metaMgr
argument_list|)
decl_stmt|;
name|OmBucketInfo
name|bucketInfo
init|=
name|OmBucketInfo
operator|.
name|newBuilder
argument_list|()
operator|.
name|setVolumeName
argument_list|(
literal|"sampleVol"
argument_list|)
operator|.
name|setBucketName
argument_list|(
literal|"bucketOne"
argument_list|)
operator|.
name|setIsVersionEnabled
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|bucketManager
operator|.
name|createBucket
argument_list|(
name|bucketInfo
argument_list|)
expr_stmt|;
name|OmBucketInfo
name|result
init|=
name|bucketManager
operator|.
name|getBucketInfo
argument_list|(
literal|"sampleVol"
argument_list|,
literal|"bucketOne"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|result
operator|.
name|getIsVersionEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|OmBucketArgs
name|bucketArgs
init|=
name|OmBucketArgs
operator|.
name|newBuilder
argument_list|()
operator|.
name|setVolumeName
argument_list|(
literal|"sampleVol"
argument_list|)
operator|.
name|setBucketName
argument_list|(
literal|"bucketOne"
argument_list|)
operator|.
name|setIsVersionEnabled
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|bucketManager
operator|.
name|setBucketProperty
argument_list|(
name|bucketArgs
argument_list|)
expr_stmt|;
name|OmBucketInfo
name|updatedResult
init|=
name|bucketManager
operator|.
name|getBucketInfo
argument_list|(
literal|"sampleVol"
argument_list|,
literal|"bucketOne"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|updatedResult
operator|.
name|getIsVersionEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|metaMgr
operator|.
name|getStore
argument_list|()
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
name|Exception
block|{
name|thrown
operator|.
name|expectMessage
argument_list|(
literal|"Bucket not found"
argument_list|)
expr_stmt|;
name|OmMetadataManagerImpl
name|metaMgr
init|=
name|createSampleVol
argument_list|()
decl_stmt|;
name|BucketManager
name|bucketManager
init|=
operator|new
name|BucketManagerImpl
argument_list|(
name|metaMgr
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|OmBucketInfo
name|bucketInfo
init|=
name|OmBucketInfo
operator|.
name|newBuilder
argument_list|()
operator|.
name|setVolumeName
argument_list|(
literal|"sampleVol"
argument_list|)
operator|.
name|setBucketName
argument_list|(
literal|"bucket_"
operator|+
name|i
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|bucketManager
operator|.
name|createBucket
argument_list|(
name|bucketInfo
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"bucket_"
operator|+
name|i
argument_list|,
name|bucketManager
operator|.
name|getBucketInfo
argument_list|(
literal|"sampleVol"
argument_list|,
literal|"bucket_"
operator|+
name|i
argument_list|)
operator|.
name|getBucketName
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|bucketManager
operator|.
name|deleteBucket
argument_list|(
literal|"sampleVol"
argument_list|,
literal|"bucket_1"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|bucketManager
operator|.
name|getBucketInfo
argument_list|(
literal|"sampleVol"
argument_list|,
literal|"bucket_2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|bucketManager
operator|.
name|getBucketInfo
argument_list|(
literal|"sampleVol"
argument_list|,
literal|"bucket_1"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OMException
name|omEx
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ResultCodes
operator|.
name|BUCKET_NOT_FOUND
argument_list|,
name|omEx
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|omEx
throw|;
block|}
name|metaMgr
operator|.
name|getStore
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDeleteNonEmptyBucket ()
specifier|public
name|void
name|testDeleteNonEmptyBucket
parameter_list|()
throws|throws
name|Exception
block|{
name|thrown
operator|.
name|expectMessage
argument_list|(
literal|"Bucket is not empty"
argument_list|)
expr_stmt|;
name|OmMetadataManagerImpl
name|metaMgr
init|=
name|createSampleVol
argument_list|()
decl_stmt|;
name|BucketManager
name|bucketManager
init|=
operator|new
name|BucketManagerImpl
argument_list|(
name|metaMgr
argument_list|)
decl_stmt|;
name|OmBucketInfo
name|bucketInfo
init|=
name|OmBucketInfo
operator|.
name|newBuilder
argument_list|()
operator|.
name|setVolumeName
argument_list|(
literal|"sampleVol"
argument_list|)
operator|.
name|setBucketName
argument_list|(
literal|"bucketOne"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|bucketManager
operator|.
name|createBucket
argument_list|(
name|bucketInfo
argument_list|)
expr_stmt|;
comment|//Create keys in bucket
name|metaMgr
operator|.
name|getKeyTable
argument_list|()
operator|.
name|put
argument_list|(
literal|"/sampleVol/bucketOne/key_one"
argument_list|,
operator|new
name|OmKeyInfo
operator|.
name|Builder
argument_list|()
operator|.
name|setBucketName
argument_list|(
literal|"bucketOne"
argument_list|)
operator|.
name|setVolumeName
argument_list|(
literal|"sampleVol"
argument_list|)
operator|.
name|setKeyName
argument_list|(
literal|"key_one"
argument_list|)
operator|.
name|setReplicationFactor
argument_list|(
name|ReplicationFactor
operator|.
name|ONE
argument_list|)
operator|.
name|setReplicationType
argument_list|(
name|ReplicationType
operator|.
name|STAND_ALONE
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|metaMgr
operator|.
name|getKeyTable
argument_list|()
operator|.
name|put
argument_list|(
literal|"/sampleVol/bucketOne/key_two"
argument_list|,
operator|new
name|OmKeyInfo
operator|.
name|Builder
argument_list|()
operator|.
name|setBucketName
argument_list|(
literal|"bucketOne"
argument_list|)
operator|.
name|setVolumeName
argument_list|(
literal|"sampleVol"
argument_list|)
operator|.
name|setKeyName
argument_list|(
literal|"key_two"
argument_list|)
operator|.
name|setReplicationFactor
argument_list|(
name|ReplicationFactor
operator|.
name|ONE
argument_list|)
operator|.
name|setReplicationType
argument_list|(
name|ReplicationType
operator|.
name|STAND_ALONE
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|bucketManager
operator|.
name|deleteBucket
argument_list|(
literal|"sampleVol"
argument_list|,
literal|"bucketOne"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OMException
name|omEx
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ResultCodes
operator|.
name|BUCKET_NOT_EMPTY
argument_list|,
name|omEx
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|omEx
throw|;
block|}
name|metaMgr
operator|.
name|getStore
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

