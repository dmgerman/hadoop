begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|DFSUtil
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
name|response
operator|.
name|BucketInfo
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
name|response
operator|.
name|VolumeInfo
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
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_comment
comment|/**  * Test Key Space Manager operation in distributed handler scenario.  */
end_comment

begin_class
DECL|class|TestKeySpaceManager
specifier|public
class|class
name|TestKeySpaceManager
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
DECL|field|ksmMetrics
specifier|private
specifier|static
name|KSMMetrics
name|ksmMetrics
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
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
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
name|ksmMetrics
operator|=
name|cluster
operator|.
name|getKeySpaceManager
argument_list|()
operator|.
name|getMetrics
argument_list|()
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
comment|// Create a volume and test its attribute after creating them
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testCreateVolume ()
specifier|public
name|void
name|testCreateVolume
parameter_list|()
throws|throws
name|IOException
throws|,
name|OzoneException
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
name|VolumeArgs
name|getVolumeArgs
init|=
operator|new
name|VolumeArgs
argument_list|(
name|volumeName
argument_list|,
name|userArgs
argument_list|)
decl_stmt|;
name|VolumeInfo
name|retVolumeinfo
init|=
name|storageHandler
operator|.
name|getVolumeInfo
argument_list|(
name|getVolumeArgs
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|retVolumeinfo
operator|.
name|getVolumeName
argument_list|()
operator|.
name|equals
argument_list|(
name|volumeName
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|retVolumeinfo
operator|.
name|getOwner
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|userName
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ksmMetrics
operator|.
name|getNumVolumeCreateFails
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Create a volume and modify the volume owner and then test its attributes
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testChangeVolumeOwner ()
specifier|public
name|void
name|testChangeVolumeOwner
parameter_list|()
throws|throws
name|IOException
throws|,
name|OzoneException
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
name|String
name|newUserName
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
name|createVolumeArgs
operator|.
name|setUserName
argument_list|(
name|newUserName
argument_list|)
expr_stmt|;
name|storageHandler
operator|.
name|setVolumeOwner
argument_list|(
name|createVolumeArgs
argument_list|)
expr_stmt|;
name|VolumeArgs
name|getVolumeArgs
init|=
operator|new
name|VolumeArgs
argument_list|(
name|volumeName
argument_list|,
name|userArgs
argument_list|)
decl_stmt|;
name|VolumeInfo
name|retVolumeInfo
init|=
name|storageHandler
operator|.
name|getVolumeInfo
argument_list|(
name|getVolumeArgs
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|retVolumeInfo
operator|.
name|getVolumeName
argument_list|()
operator|.
name|equals
argument_list|(
name|volumeName
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|retVolumeInfo
operator|.
name|getOwner
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|userName
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|retVolumeInfo
operator|.
name|getOwner
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|newUserName
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ksmMetrics
operator|.
name|getNumVolumeCreateFails
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ksmMetrics
operator|.
name|getNumVolumeInfoFails
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Create a volume and modify the volume owner and then test its attributes
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testChangeVolumeQuota ()
specifier|public
name|void
name|testChangeVolumeQuota
parameter_list|()
throws|throws
name|IOException
throws|,
name|OzoneException
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
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
comment|// Create a new volume with a quota
name|OzoneQuota
name|createQuota
init|=
operator|new
name|OzoneQuota
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
argument_list|,
name|OzoneQuota
operator|.
name|Units
operator|.
name|GB
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
name|createVolumeArgs
operator|.
name|setQuota
argument_list|(
name|createQuota
argument_list|)
expr_stmt|;
name|storageHandler
operator|.
name|createVolume
argument_list|(
name|createVolumeArgs
argument_list|)
expr_stmt|;
name|VolumeArgs
name|getVolumeArgs
init|=
operator|new
name|VolumeArgs
argument_list|(
name|volumeName
argument_list|,
name|userArgs
argument_list|)
decl_stmt|;
name|VolumeInfo
name|retVolumeInfo
init|=
name|storageHandler
operator|.
name|getVolumeInfo
argument_list|(
name|getVolumeArgs
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|retVolumeInfo
operator|.
name|getQuota
argument_list|()
operator|.
name|sizeInBytes
argument_list|()
argument_list|,
name|createQuota
operator|.
name|sizeInBytes
argument_list|()
argument_list|)
expr_stmt|;
comment|// Set a new quota and test it
name|OzoneQuota
name|setQuota
init|=
operator|new
name|OzoneQuota
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
argument_list|,
name|OzoneQuota
operator|.
name|Units
operator|.
name|GB
argument_list|)
decl_stmt|;
name|createVolumeArgs
operator|.
name|setQuota
argument_list|(
name|setQuota
argument_list|)
expr_stmt|;
name|storageHandler
operator|.
name|setVolumeQuota
argument_list|(
name|createVolumeArgs
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|getVolumeArgs
operator|=
operator|new
name|VolumeArgs
argument_list|(
name|volumeName
argument_list|,
name|userArgs
argument_list|)
expr_stmt|;
name|retVolumeInfo
operator|=
name|storageHandler
operator|.
name|getVolumeInfo
argument_list|(
name|getVolumeArgs
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|retVolumeInfo
operator|.
name|getQuota
argument_list|()
operator|.
name|sizeInBytes
argument_list|()
argument_list|,
name|setQuota
operator|.
name|sizeInBytes
argument_list|()
argument_list|)
expr_stmt|;
comment|// Remove the quota and test it again
name|storageHandler
operator|.
name|setVolumeQuota
argument_list|(
name|createVolumeArgs
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|getVolumeArgs
operator|=
operator|new
name|VolumeArgs
argument_list|(
name|volumeName
argument_list|,
name|userArgs
argument_list|)
expr_stmt|;
name|retVolumeInfo
operator|=
name|storageHandler
operator|.
name|getVolumeInfo
argument_list|(
name|getVolumeArgs
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|retVolumeInfo
operator|.
name|getQuota
argument_list|()
operator|.
name|sizeInBytes
argument_list|()
argument_list|,
name|OzoneConsts
operator|.
name|MAX_QUOTA_IN_BYTES
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ksmMetrics
operator|.
name|getNumVolumeCreateFails
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ksmMetrics
operator|.
name|getNumVolumeInfoFails
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testCreateBucket ()
specifier|public
name|void
name|testCreateBucket
parameter_list|()
throws|throws
name|IOException
throws|,
name|OzoneException
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
name|BucketArgs
name|getBucketArgs
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
name|BucketInfo
name|bucketInfo
init|=
name|storageHandler
operator|.
name|getBucketInfo
argument_list|(
name|getBucketArgs
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|bucketInfo
operator|.
name|getVolumeName
argument_list|()
operator|.
name|equals
argument_list|(
name|volumeName
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|bucketInfo
operator|.
name|getBucketName
argument_list|()
operator|.
name|equals
argument_list|(
name|bucketName
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ksmMetrics
operator|.
name|getNumVolumeCreateFails
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ksmMetrics
operator|.
name|getNumBucketCreateFails
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ksmMetrics
operator|.
name|getNumBucketInfoFails
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Basic test of both putKey and getKey from KSM, as one can not be tested    * without the other.    *    * @throws IOException    * @throws OzoneException    */
annotation|@
name|Test
DECL|method|testGetKeyWriterReader ()
specifier|public
name|void
name|testGetKeyWriterReader
parameter_list|()
throws|throws
name|IOException
throws|,
name|OzoneException
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
name|long
name|numKeyAllocates
init|=
name|ksmMetrics
operator|.
name|getNumKeyAllocates
argument_list|()
decl_stmt|;
name|long
name|numKeyLookups
init|=
name|ksmMetrics
operator|.
name|getNumKeyLookups
argument_list|()
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
literal|100
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
literal|100
argument_list|)
expr_stmt|;
try|try
init|(
name|OutputStream
name|stream
init|=
name|storageHandler
operator|.
name|newKeyWriter
argument_list|(
name|keyArgs
argument_list|)
init|)
block|{
name|stream
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
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
operator|+
name|numKeyAllocates
argument_list|,
name|ksmMetrics
operator|.
name|getNumKeyAllocates
argument_list|()
argument_list|)
expr_stmt|;
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
name|in
init|=
name|storageHandler
operator|.
name|newKeyReader
argument_list|(
name|keyArgs
argument_list|)
init|)
block|{
name|in
operator|.
name|read
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|dataString
argument_list|,
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|data
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
operator|+
name|numKeyLookups
argument_list|,
name|ksmMetrics
operator|.
name|getNumKeyLookups
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test write the same key twice, the second write should fail, as currently    * key overwrite is not supported.    *    * @throws IOException    * @throws OzoneException    */
annotation|@
name|Test
DECL|method|testKeyOverwrite ()
specifier|public
name|void
name|testKeyOverwrite
parameter_list|()
throws|throws
name|IOException
throws|,
name|OzoneException
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
name|long
name|numKeyAllocateFails
init|=
name|ksmMetrics
operator|.
name|getNumKeyAllocateFails
argument_list|()
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
literal|100
argument_list|)
expr_stmt|;
name|String
name|dataString
init|=
name|RandomStringUtils
operator|.
name|randomAscii
argument_list|(
literal|100
argument_list|)
decl_stmt|;
try|try
init|(
name|OutputStream
name|stream
init|=
name|storageHandler
operator|.
name|newKeyWriter
argument_list|(
name|keyArgs
argument_list|)
init|)
block|{
name|stream
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
comment|// try to put the same keyArg, should raise KEY_ALREADY_EXISTS exception
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
literal|"KEY_ALREADY_EXISTS"
argument_list|)
expr_stmt|;
name|KeyArgs
name|keyArgs2
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
name|storageHandler
operator|.
name|newKeyWriter
argument_list|(
name|keyArgs2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
operator|+
name|numKeyAllocateFails
argument_list|,
name|ksmMetrics
operator|.
name|getNumKeyAllocateFails
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test get a non-exiting key.    *    * @throws IOException    * @throws OzoneException    */
annotation|@
name|Test
DECL|method|testGetNonExistKey ()
specifier|public
name|void
name|testGetNonExistKey
parameter_list|()
throws|throws
name|IOException
throws|,
name|OzoneException
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
name|long
name|numKeyLookupFails
init|=
name|ksmMetrics
operator|.
name|getNumKeyLookupFails
argument_list|()
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
comment|// try to get the key, should fail as it hasn't been created
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
literal|"KEY_NOT_FOUND"
argument_list|)
expr_stmt|;
name|storageHandler
operator|.
name|newKeyReader
argument_list|(
name|keyArgs
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
operator|+
name|numKeyLookupFails
argument_list|,
name|ksmMetrics
operator|.
name|getNumKeyLookupFails
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

