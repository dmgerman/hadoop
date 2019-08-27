begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.keyvalue
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|keyvalue
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
name|StorageUnit
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
name|StorageContainerException
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
name|container
operator|.
name|common
operator|.
name|impl
operator|.
name|ContainerDataYaml
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
name|container
operator|.
name|common
operator|.
name|volume
operator|.
name|HddsVolume
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
name|container
operator|.
name|common
operator|.
name|volume
operator|.
name|RoundRobinVolumeChoosingPolicy
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
name|container
operator|.
name|common
operator|.
name|volume
operator|.
name|VolumeSet
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
name|After
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|protocol
operator|.
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|ContainerDataProto
operator|.
name|State
operator|.
name|OPEN
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
name|protocol
operator|.
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|ContainerDataProto
operator|.
name|State
operator|.
name|UNHEALTHY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|Is
operator|.
name|is
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
name|assertThat
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|anyList
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|anyLong
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_comment
comment|/**  * Tests unhealthy container functionality in the {@link KeyValueContainer}  * class.  */
end_comment

begin_class
DECL|class|TestKeyValueContainerMarkUnhealthy
specifier|public
class|class
name|TestKeyValueContainerMarkUnhealthy
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestKeyValueContainerMarkUnhealthy
operator|.
name|class
argument_list|)
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
literal|600_000
argument_list|)
decl_stmt|;
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
DECL|field|conf
specifier|private
name|OzoneConfiguration
name|conf
decl_stmt|;
DECL|field|scmId
specifier|private
name|String
name|scmId
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
DECL|field|volumeSet
specifier|private
name|VolumeSet
name|volumeSet
decl_stmt|;
DECL|field|volumeChoosingPolicy
specifier|private
name|RoundRobinVolumeChoosingPolicy
name|volumeChoosingPolicy
decl_stmt|;
DECL|field|keyValueContainerData
specifier|private
name|KeyValueContainerData
name|keyValueContainerData
decl_stmt|;
DECL|field|keyValueContainer
specifier|private
name|KeyValueContainer
name|keyValueContainer
decl_stmt|;
DECL|field|datanodeId
specifier|private
name|UUID
name|datanodeId
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
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
name|datanodeId
operator|=
name|UUID
operator|.
name|randomUUID
argument_list|()
expr_stmt|;
name|HddsVolume
name|hddsVolume
init|=
operator|new
name|HddsVolume
operator|.
name|Builder
argument_list|(
name|folder
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
operator|.
name|conf
argument_list|(
name|conf
argument_list|)
operator|.
name|datanodeUuid
argument_list|(
name|datanodeId
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|volumeSet
operator|=
name|mock
argument_list|(
name|VolumeSet
operator|.
name|class
argument_list|)
expr_stmt|;
name|volumeChoosingPolicy
operator|=
name|mock
argument_list|(
name|RoundRobinVolumeChoosingPolicy
operator|.
name|class
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|volumeChoosingPolicy
operator|.
name|chooseVolume
argument_list|(
name|anyList
argument_list|()
argument_list|,
name|anyLong
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|hddsVolume
argument_list|)
expr_stmt|;
name|keyValueContainerData
operator|=
operator|new
name|KeyValueContainerData
argument_list|(
literal|1L
argument_list|,
operator|(
name|long
operator|)
name|StorageUnit
operator|.
name|GB
operator|.
name|toBytes
argument_list|(
literal|5
argument_list|)
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|datanodeId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|File
name|metaDir
init|=
name|GenericTestUtils
operator|.
name|getRandomizedTestDir
argument_list|()
decl_stmt|;
name|metaDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|keyValueContainerData
operator|.
name|setMetadataPath
argument_list|(
name|metaDir
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|keyValueContainer
operator|=
operator|new
name|KeyValueContainer
argument_list|(
name|keyValueContainerData
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|teardown ()
specifier|public
name|void
name|teardown
parameter_list|()
block|{
name|volumeSet
operator|=
literal|null
expr_stmt|;
name|keyValueContainer
operator|=
literal|null
expr_stmt|;
name|keyValueContainerData
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Verify that the .container file is correctly updated when a    * container is marked as unhealthy.    *    * @throws IOException    */
annotation|@
name|Test
DECL|method|testMarkContainerUnhealthy ()
specifier|public
name|void
name|testMarkContainerUnhealthy
parameter_list|()
throws|throws
name|IOException
block|{
name|assertThat
argument_list|(
name|keyValueContainerData
operator|.
name|getState
argument_list|()
argument_list|,
name|is
argument_list|(
name|OPEN
argument_list|)
argument_list|)
expr_stmt|;
name|keyValueContainer
operator|.
name|markContainerUnhealthy
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|keyValueContainerData
operator|.
name|getState
argument_list|()
argument_list|,
name|is
argument_list|(
name|UNHEALTHY
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check metadata in the .container file
name|File
name|containerFile
init|=
name|keyValueContainer
operator|.
name|getContainerFile
argument_list|()
decl_stmt|;
name|keyValueContainerData
operator|=
operator|(
name|KeyValueContainerData
operator|)
name|ContainerDataYaml
operator|.
name|readContainerFile
argument_list|(
name|containerFile
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|keyValueContainerData
operator|.
name|getState
argument_list|()
argument_list|,
name|is
argument_list|(
name|UNHEALTHY
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Attempting to close an unhealthy container should fail.    * @throws IOException    */
annotation|@
name|Test
DECL|method|testCloseUnhealthyContainer ()
specifier|public
name|void
name|testCloseUnhealthyContainer
parameter_list|()
throws|throws
name|IOException
block|{
name|keyValueContainer
operator|.
name|markContainerUnhealthy
argument_list|()
expr_stmt|;
name|thrown
operator|.
name|expect
argument_list|(
name|StorageContainerException
operator|.
name|class
argument_list|)
expr_stmt|;
name|keyValueContainer
operator|.
name|markContainerForClose
argument_list|()
expr_stmt|;
block|}
comment|/**    * Attempting to mark a closed container as unhealthy should succeed.    */
annotation|@
name|Test
DECL|method|testMarkClosedContainerAsUnhealthy ()
specifier|public
name|void
name|testMarkClosedContainerAsUnhealthy
parameter_list|()
throws|throws
name|IOException
block|{
comment|// We need to create the container so the compact-on-close operation
comment|// does not NPE.
name|keyValueContainer
operator|.
name|create
argument_list|(
name|volumeSet
argument_list|,
name|volumeChoosingPolicy
argument_list|,
name|scmId
argument_list|)
expr_stmt|;
name|keyValueContainer
operator|.
name|close
argument_list|()
expr_stmt|;
name|keyValueContainer
operator|.
name|markContainerUnhealthy
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|keyValueContainerData
operator|.
name|getState
argument_list|()
argument_list|,
name|is
argument_list|(
name|UNHEALTHY
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Attempting to mark a quasi-closed container as unhealthy should succeed.    */
annotation|@
name|Test
DECL|method|testMarkQuasiClosedContainerAsUnhealthy ()
specifier|public
name|void
name|testMarkQuasiClosedContainerAsUnhealthy
parameter_list|()
throws|throws
name|IOException
block|{
comment|// We need to create the container so the sync-on-quasi-close operation
comment|// does not NPE.
name|keyValueContainer
operator|.
name|create
argument_list|(
name|volumeSet
argument_list|,
name|volumeChoosingPolicy
argument_list|,
name|scmId
argument_list|)
expr_stmt|;
name|keyValueContainer
operator|.
name|quasiClose
argument_list|()
expr_stmt|;
name|keyValueContainer
operator|.
name|markContainerUnhealthy
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|keyValueContainerData
operator|.
name|getState
argument_list|()
argument_list|,
name|is
argument_list|(
name|UNHEALTHY
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Attempting to mark a closing container as unhealthy should succeed.    */
annotation|@
name|Test
DECL|method|testMarkClosingContainerAsUnhealthy ()
specifier|public
name|void
name|testMarkClosingContainerAsUnhealthy
parameter_list|()
throws|throws
name|IOException
block|{
name|keyValueContainer
operator|.
name|markContainerForClose
argument_list|()
expr_stmt|;
name|keyValueContainer
operator|.
name|markContainerUnhealthy
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|keyValueContainerData
operator|.
name|getState
argument_list|()
argument_list|,
name|is
argument_list|(
name|UNHEALTHY
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

