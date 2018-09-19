begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.ââSee the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.ââThe ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.ââYou may obtain a copy of the License at  *  * ââââ http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|IOException
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
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
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
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
name|server
operator|.
name|StorageContainerManager
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
name|TestStorageContainerManagerHelper
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
name|helpers
operator|.
name|OmBucketInfo
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
name|OmVolumeArgs
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
name|test
operator|.
name|LambdaTestUtils
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
name|After
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
name|Timeout
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
comment|/**  * Test Ozone Manager operation in distributed handler scenario.  */
end_comment

begin_class
DECL|class|TestScmChillMode
specifier|public
class|class
name|TestScmChillMode
block|{
DECL|field|cluster
specifier|private
specifier|static
name|MiniOzoneCluster
name|cluster
init|=
literal|null
decl_stmt|;
DECL|field|builder
specifier|private
specifier|static
name|MiniOzoneCluster
operator|.
name|Builder
name|builder
init|=
literal|null
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|OzoneConfiguration
name|conf
decl_stmt|;
DECL|field|om
specifier|private
specifier|static
name|OzoneManager
name|om
decl_stmt|;
annotation|@
name|Rule
DECL|field|timeout
specifier|public
name|Timeout
name|timeout
init|=
operator|new
name|Timeout
argument_list|(
literal|1000
operator|*
literal|200
argument_list|)
decl_stmt|;
comment|/**    * Create a MiniDFSCluster for testing.    *<p>    * Ozone is made active by setting OZONE_ENABLED = true and    * OZONE_HANDLER_TYPE_KEY = "distributed"    *    * @throws IOException    */
annotation|@
name|Before
DECL|method|init ()
specifier|public
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
name|builder
operator|=
name|MiniOzoneCluster
operator|.
name|newBuilder
argument_list|(
name|conf
argument_list|)
operator|.
name|setHbInterval
argument_list|(
literal|1000
argument_list|)
operator|.
name|setHbProcessorInterval
argument_list|(
literal|500
argument_list|)
operator|.
name|setStartDataNodes
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|cluster
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|startHddsDatanodes
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitForClusterToBeReady
argument_list|()
expr_stmt|;
name|om
operator|=
name|cluster
operator|.
name|getOzoneManager
argument_list|()
expr_stmt|;
block|}
comment|/**    * Shutdown MiniDFSCluster.    */
annotation|@
name|After
DECL|method|shutdown ()
specifier|public
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
DECL|method|testChillModeOperations ()
specifier|public
name|void
name|testChillModeOperations
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AtomicReference
argument_list|<
name|MiniOzoneCluster
argument_list|>
name|miniCluster
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
decl_stmt|;
try|try
block|{
comment|// Create {numKeys} random names keys.
name|TestStorageContainerManagerHelper
name|helper
init|=
operator|new
name|TestStorageContainerManagerHelper
argument_list|(
name|cluster
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|OmKeyInfo
argument_list|>
name|keyLocations
init|=
name|helper
operator|.
name|createKeys
argument_list|(
literal|100
argument_list|,
literal|4096
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|ContainerInfo
argument_list|>
name|containers
init|=
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
operator|.
name|getScmContainerManager
argument_list|()
operator|.
name|getStateManager
argument_list|()
operator|.
name|getAllContainers
argument_list|()
decl_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
return|return
name|containers
operator|.
name|size
argument_list|()
operator|>
literal|10
return|;
block|}
argument_list|,
literal|100
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
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
name|setDataSize
argument_list|(
literal|1000
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|OmVolumeArgs
name|volArgs
init|=
operator|new
name|OmVolumeArgs
operator|.
name|Builder
argument_list|()
operator|.
name|setAdminName
argument_list|(
name|adminName
argument_list|)
operator|.
name|setCreationTime
argument_list|(
name|Time
operator|.
name|monotonicNow
argument_list|()
argument_list|)
operator|.
name|setQuotaInBytes
argument_list|(
literal|10000
argument_list|)
operator|.
name|setVolume
argument_list|(
name|volumeName
argument_list|)
operator|.
name|setOwnerName
argument_list|(
name|userName
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|OmBucketInfo
name|bucketInfo
init|=
operator|new
name|OmBucketInfo
operator|.
name|Builder
argument_list|()
operator|.
name|setBucketName
argument_list|(
name|bucketName
argument_list|)
operator|.
name|setIsVersionEnabled
argument_list|(
literal|false
argument_list|)
operator|.
name|setVolumeName
argument_list|(
name|volumeName
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|om
operator|.
name|createVolume
argument_list|(
name|volArgs
argument_list|)
expr_stmt|;
name|om
operator|.
name|createBucket
argument_list|(
name|bucketInfo
argument_list|)
expr_stmt|;
name|om
operator|.
name|openKey
argument_list|(
name|keyArgs
argument_list|)
expr_stmt|;
comment|//om.commitKey(keyArgs, 1);
name|cluster
operator|.
name|stop
argument_list|()
expr_stmt|;
operator|new
name|Thread
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
name|miniCluster
operator|.
name|set
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"failed"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|StorageContainerManager
name|scm
decl_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
return|return
name|miniCluster
operator|.
name|get
argument_list|()
operator|!=
literal|null
return|;
block|}
argument_list|,
literal|100
argument_list|,
literal|1000
operator|*
literal|3
argument_list|)
expr_stmt|;
name|scm
operator|=
name|miniCluster
operator|.
name|get
argument_list|()
operator|.
name|getStorageContainerManager
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|scm
operator|.
name|isInChillMode
argument_list|()
argument_list|)
expr_stmt|;
name|om
operator|=
name|miniCluster
operator|.
name|get
argument_list|()
operator|.
name|getOzoneManager
argument_list|()
expr_stmt|;
name|LambdaTestUtils
operator|.
name|intercept
argument_list|(
name|OMException
operator|.
name|class
argument_list|,
literal|"ChillModePrecheck failed for allocateBlock"
argument_list|,
parameter_list|()
lambda|->
name|om
operator|.
name|openKey
argument_list|(
name|keyArgs
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|miniCluster
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|miniCluster
operator|.
name|get
argument_list|()
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// do nothing.
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

