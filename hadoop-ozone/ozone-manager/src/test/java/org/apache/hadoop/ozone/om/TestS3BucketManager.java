begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
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
name|test
operator|.
name|GenericTestUtils
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
name|Before
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

begin_comment
comment|/**  * Tests for S3 Bucket Manager.  */
end_comment

begin_class
DECL|class|TestS3BucketManager
specifier|public
class|class
name|TestS3BucketManager
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
DECL|field|conf
specifier|private
name|OzoneConfiguration
name|conf
decl_stmt|;
DECL|field|metaMgr
specifier|private
name|OmMetadataManagerImpl
name|metaMgr
decl_stmt|;
DECL|field|bucketManager
specifier|private
name|BucketManager
name|bucketManager
decl_stmt|;
DECL|field|volumeManager
specifier|private
name|VolumeManager
name|volumeManager
decl_stmt|;
annotation|@
name|Before
DECL|method|init ()
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|IOException
block|{
name|conf
operator|=
operator|new
name|OzoneConfiguration
argument_list|()
expr_stmt|;
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
name|metaMgr
operator|=
operator|new
name|OmMetadataManagerImpl
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|volumeManager
operator|=
operator|new
name|VolumeManagerImpl
argument_list|(
name|metaMgr
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|bucketManager
operator|=
operator|new
name|BucketManagerImpl
argument_list|(
name|metaMgr
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOzoneVolumeNameForUser ()
specifier|public
name|void
name|testOzoneVolumeNameForUser
parameter_list|()
throws|throws
name|IOException
block|{
name|S3BucketManager
name|s3BucketManager
init|=
operator|new
name|S3BucketManagerImpl
argument_list|(
name|conf
argument_list|,
name|metaMgr
argument_list|,
name|volumeManager
argument_list|,
name|bucketManager
argument_list|)
decl_stmt|;
name|String
name|userName
init|=
literal|"ozone"
decl_stmt|;
name|String
name|volumeName
init|=
name|s3BucketManager
operator|.
name|getOzoneVolumeNameForUser
argument_list|(
name|userName
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|OzoneConsts
operator|.
name|OM_S3_VOLUME_PREFIX
operator|+
name|userName
argument_list|,
name|volumeName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOzoneVolumeNameForUserFails ()
specifier|public
name|void
name|testOzoneVolumeNameForUserFails
parameter_list|()
throws|throws
name|IOException
block|{
name|S3BucketManager
name|s3BucketManager
init|=
operator|new
name|S3BucketManagerImpl
argument_list|(
name|conf
argument_list|,
name|metaMgr
argument_list|,
name|volumeManager
argument_list|,
name|bucketManager
argument_list|)
decl_stmt|;
name|String
name|userName
init|=
literal|null
decl_stmt|;
try|try
block|{
name|String
name|volumeName
init|=
name|s3BucketManager
operator|.
name|getOzoneVolumeNameForUser
argument_list|(
name|userName
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"testOzoneVolumeNameForUserFails failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|ex
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"UserName cannot be null"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testGetS3BucketMapping ()
specifier|public
name|void
name|testGetS3BucketMapping
parameter_list|()
throws|throws
name|IOException
block|{
name|S3BucketManager
name|s3BucketManager
init|=
operator|new
name|S3BucketManagerImpl
argument_list|(
name|conf
argument_list|,
name|metaMgr
argument_list|,
name|volumeManager
argument_list|,
name|bucketManager
argument_list|)
decl_stmt|;
name|String
name|userName
init|=
literal|"bilbo"
decl_stmt|;
name|metaMgr
operator|.
name|getS3Table
argument_list|()
operator|.
name|put
argument_list|(
literal|"newBucket"
argument_list|,
name|s3BucketManager
operator|.
name|formatOzoneVolumeName
argument_list|(
name|userName
argument_list|)
operator|+
literal|"/newBucket"
argument_list|)
expr_stmt|;
name|String
name|mapping
init|=
name|s3BucketManager
operator|.
name|getOzoneBucketMapping
argument_list|(
literal|"newBucket"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|mapping
operator|.
name|startsWith
argument_list|(
literal|"s3bilbo/"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|mapping
operator|.
name|endsWith
argument_list|(
literal|"/newBucket"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetOzoneNames ()
specifier|public
name|void
name|testGetOzoneNames
parameter_list|()
throws|throws
name|IOException
block|{
name|S3BucketManager
name|s3BucketManager
init|=
operator|new
name|S3BucketManagerImpl
argument_list|(
name|conf
argument_list|,
name|metaMgr
argument_list|,
name|volumeManager
argument_list|,
name|bucketManager
argument_list|)
decl_stmt|;
name|String
name|userName
init|=
literal|"batman"
decl_stmt|;
name|String
name|s3BucketName
init|=
literal|"gotham"
decl_stmt|;
name|metaMgr
operator|.
name|getS3Table
argument_list|()
operator|.
name|put
argument_list|(
name|s3BucketName
argument_list|,
name|s3BucketManager
operator|.
name|formatOzoneVolumeName
argument_list|(
name|userName
argument_list|)
operator|+
literal|"/"
operator|+
name|s3BucketName
argument_list|)
expr_stmt|;
name|String
name|volumeName
init|=
name|s3BucketManager
operator|.
name|getOzoneVolumeName
argument_list|(
name|s3BucketName
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|volumeName
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"s3"
operator|+
name|userName
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|bucketName
init|=
name|s3BucketManager
operator|.
name|getOzoneBucketName
argument_list|(
name|s3BucketName
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|bucketName
operator|.
name|equalsIgnoreCase
argument_list|(
name|s3BucketName
argument_list|)
argument_list|)
expr_stmt|;
comment|// try to get a bucket that does not exist.
name|thrown
operator|.
name|expectMessage
argument_list|(
literal|"No such S3 bucket."
argument_list|)
expr_stmt|;
name|s3BucketManager
operator|.
name|getOzoneBucketMapping
argument_list|(
literal|"raven"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

