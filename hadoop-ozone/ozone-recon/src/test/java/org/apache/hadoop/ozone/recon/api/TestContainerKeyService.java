begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.recon.api
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|recon
operator|.
name|api
package|;
end_package

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
name|recon
operator|.
name|ReconServerConfigKeys
operator|.
name|OZONE_RECON_DB_DIR
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
name|recon
operator|.
name|ReconServerConfigKeys
operator|.
name|OZONE_RECON_OM_SNAPSHOT_DB_DIR
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
name|assertTrue
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
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Response
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
name|BlockID
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
name|pipeline
operator|.
name|Pipeline
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
name|OmUtils
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
name|OMMetadataManager
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
name|ozone
operator|.
name|om
operator|.
name|helpers
operator|.
name|OmKeyLocationInfoGroup
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
name|recon
operator|.
name|AbstractOMMetadataManagerTest
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
name|recon
operator|.
name|ReconUtils
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
name|recon
operator|.
name|api
operator|.
name|types
operator|.
name|ContainerMetadata
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
name|recon
operator|.
name|api
operator|.
name|types
operator|.
name|KeyMetadata
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
name|recon
operator|.
name|recovery
operator|.
name|ReconOMMetadataManager
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
name|recon
operator|.
name|spi
operator|.
name|ContainerDBServiceProvider
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
name|recon
operator|.
name|spi
operator|.
name|OzoneManagerServiceProvider
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
name|recon
operator|.
name|spi
operator|.
name|impl
operator|.
name|ContainerDBServiceProviderImpl
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
name|recon
operator|.
name|spi
operator|.
name|impl
operator|.
name|OzoneManagerServiceProviderImpl
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
name|recon
operator|.
name|spi
operator|.
name|impl
operator|.
name|ReconContainerDBProvider
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
name|recon
operator|.
name|tasks
operator|.
name|ContainerKeyMapperTask
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
name|utils
operator|.
name|db
operator|.
name|DBCheckpoint
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
name|utils
operator|.
name|db
operator|.
name|DBStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|impl
operator|.
name|client
operator|.
name|CloseableHttpClient
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
name|Test
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
name|powermock
operator|.
name|api
operator|.
name|mockito
operator|.
name|PowerMockito
import|;
end_import

begin_import
import|import
name|org
operator|.
name|powermock
operator|.
name|core
operator|.
name|classloader
operator|.
name|annotations
operator|.
name|PowerMockIgnore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|powermock
operator|.
name|core
operator|.
name|classloader
operator|.
name|annotations
operator|.
name|PrepareForTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|powermock
operator|.
name|modules
operator|.
name|junit4
operator|.
name|PowerMockRunner
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|AbstractModule
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Guice
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Injector
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Singleton
import|;
end_import

begin_comment
comment|/**  * Test for container key service.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|PowerMockRunner
operator|.
name|class
argument_list|)
annotation|@
name|PowerMockIgnore
argument_list|(
block|{
literal|"javax.management.*"
block|,
literal|"javax.net.ssl.*"
block|}
argument_list|)
annotation|@
name|PrepareForTest
argument_list|(
name|ReconUtils
operator|.
name|class
argument_list|)
DECL|class|TestContainerKeyService
specifier|public
class|class
name|TestContainerKeyService
extends|extends
name|AbstractOMMetadataManagerTest
block|{
DECL|field|containerDbServiceProvider
specifier|private
name|ContainerDBServiceProvider
name|containerDbServiceProvider
decl_stmt|;
DECL|field|omMetadataManager
specifier|private
name|OMMetadataManager
name|omMetadataManager
decl_stmt|;
DECL|field|reconOMMetadataManager
specifier|private
name|ReconOMMetadataManager
name|reconOMMetadataManager
decl_stmt|;
DECL|field|injector
specifier|private
name|Injector
name|injector
decl_stmt|;
DECL|field|ozoneManagerServiceProvider
specifier|private
name|OzoneManagerServiceProviderImpl
name|ozoneManagerServiceProvider
decl_stmt|;
DECL|field|containerKeyService
specifier|private
name|ContainerKeyService
name|containerKeyService
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
name|omMetadataManager
operator|=
name|initializeNewOmMetadataManager
argument_list|()
expr_stmt|;
name|injector
operator|=
name|Guice
operator|.
name|createInjector
argument_list|(
operator|new
name|AbstractModule
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configure
parameter_list|()
block|{
try|try
block|{
name|bind
argument_list|(
name|OzoneConfiguration
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|getTestOzoneConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|reconOMMetadataManager
operator|=
name|getTestMetadataManager
argument_list|(
name|omMetadataManager
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|ReconOMMetadataManager
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|reconOMMetadataManager
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|DBStore
operator|.
name|class
argument_list|)
operator|.
name|toProvider
argument_list|(
name|ReconContainerDBProvider
operator|.
name|class
argument_list|)
operator|.
name|in
argument_list|(
name|Singleton
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|ContainerDBServiceProvider
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|ContainerDBServiceProviderImpl
operator|.
name|class
argument_list|)
operator|.
name|in
argument_list|(
name|Singleton
operator|.
name|class
argument_list|)
expr_stmt|;
name|ozoneManagerServiceProvider
operator|=
operator|new
name|OzoneManagerServiceProviderImpl
argument_list|(
name|getTestOzoneConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|OzoneManagerServiceProvider
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|ozoneManagerServiceProvider
argument_list|)
expr_stmt|;
name|containerKeyService
operator|=
operator|new
name|ContainerKeyService
argument_list|()
expr_stmt|;
name|bind
argument_list|(
name|ContainerKeyService
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|containerKeyService
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|containerDbServiceProvider
operator|=
name|injector
operator|.
name|getInstance
argument_list|(
name|ContainerDBServiceProvider
operator|.
name|class
argument_list|)
expr_stmt|;
comment|//Write Data to OM
name|Pipeline
name|pipeline
init|=
name|getRandomPipeline
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|OmKeyLocationInfo
argument_list|>
name|omKeyLocationInfoList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|BlockID
name|blockID1
init|=
operator|new
name|BlockID
argument_list|(
literal|1
argument_list|,
literal|101
argument_list|)
decl_stmt|;
name|OmKeyLocationInfo
name|omKeyLocationInfo1
init|=
name|getOmKeyLocationInfo
argument_list|(
name|blockID1
argument_list|,
name|pipeline
argument_list|)
decl_stmt|;
name|omKeyLocationInfoList
operator|.
name|add
argument_list|(
name|omKeyLocationInfo1
argument_list|)
expr_stmt|;
name|BlockID
name|blockID2
init|=
operator|new
name|BlockID
argument_list|(
literal|2
argument_list|,
literal|102
argument_list|)
decl_stmt|;
name|OmKeyLocationInfo
name|omKeyLocationInfo2
init|=
name|getOmKeyLocationInfo
argument_list|(
name|blockID2
argument_list|,
name|pipeline
argument_list|)
decl_stmt|;
name|omKeyLocationInfoList
operator|.
name|add
argument_list|(
name|omKeyLocationInfo2
argument_list|)
expr_stmt|;
name|OmKeyLocationInfoGroup
name|omKeyLocationInfoGroup
init|=
operator|new
name|OmKeyLocationInfoGroup
argument_list|(
literal|0
argument_list|,
name|omKeyLocationInfoList
argument_list|)
decl_stmt|;
comment|//key = key_one, Blocks = [ {CID = 1, LID = 1}, {CID = 2, LID = 1} ]
name|writeDataToOm
argument_list|(
name|omMetadataManager
argument_list|,
literal|"key_one"
argument_list|,
literal|"bucketOne"
argument_list|,
literal|"sampleVol"
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|omKeyLocationInfoGroup
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|OmKeyLocationInfoGroup
argument_list|>
name|infoGroups
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|BlockID
name|blockID3
init|=
operator|new
name|BlockID
argument_list|(
literal|1
argument_list|,
literal|103
argument_list|)
decl_stmt|;
name|OmKeyLocationInfo
name|omKeyLocationInfo3
init|=
name|getOmKeyLocationInfo
argument_list|(
name|blockID3
argument_list|,
name|pipeline
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|OmKeyLocationInfo
argument_list|>
name|omKeyLocationInfoListNew
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|omKeyLocationInfoListNew
operator|.
name|add
argument_list|(
name|omKeyLocationInfo3
argument_list|)
expr_stmt|;
name|infoGroups
operator|.
name|add
argument_list|(
operator|new
name|OmKeyLocationInfoGroup
argument_list|(
literal|0
argument_list|,
name|omKeyLocationInfoListNew
argument_list|)
argument_list|)
expr_stmt|;
name|BlockID
name|blockID4
init|=
operator|new
name|BlockID
argument_list|(
literal|1
argument_list|,
literal|104
argument_list|)
decl_stmt|;
name|OmKeyLocationInfo
name|omKeyLocationInfo4
init|=
name|getOmKeyLocationInfo
argument_list|(
name|blockID4
argument_list|,
name|pipeline
argument_list|)
decl_stmt|;
name|omKeyLocationInfoListNew
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|omKeyLocationInfoListNew
operator|.
name|add
argument_list|(
name|omKeyLocationInfo4
argument_list|)
expr_stmt|;
name|infoGroups
operator|.
name|add
argument_list|(
operator|new
name|OmKeyLocationInfoGroup
argument_list|(
literal|1
argument_list|,
name|omKeyLocationInfoListNew
argument_list|)
argument_list|)
expr_stmt|;
comment|//key = key_two, Blocks = [ {CID = 1, LID = 2}, {CID = 1, LID = 3} ]
name|writeDataToOm
argument_list|(
name|omMetadataManager
argument_list|,
literal|"key_two"
argument_list|,
literal|"bucketOne"
argument_list|,
literal|"sampleVol"
argument_list|,
name|infoGroups
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|OmKeyLocationInfo
argument_list|>
name|omKeyLocationInfoList2
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|BlockID
name|blockID5
init|=
operator|new
name|BlockID
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|OmKeyLocationInfo
name|omKeyLocationInfo5
init|=
name|getOmKeyLocationInfo
argument_list|(
name|blockID5
argument_list|,
name|pipeline
argument_list|)
decl_stmt|;
name|omKeyLocationInfoList2
operator|.
name|add
argument_list|(
name|omKeyLocationInfo5
argument_list|)
expr_stmt|;
name|BlockID
name|blockID6
init|=
operator|new
name|BlockID
argument_list|(
literal|2
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|OmKeyLocationInfo
name|omKeyLocationInfo6
init|=
name|getOmKeyLocationInfo
argument_list|(
name|blockID6
argument_list|,
name|pipeline
argument_list|)
decl_stmt|;
name|omKeyLocationInfoList2
operator|.
name|add
argument_list|(
name|omKeyLocationInfo6
argument_list|)
expr_stmt|;
name|OmKeyLocationInfoGroup
name|omKeyLocationInfoGroup2
init|=
operator|new
name|OmKeyLocationInfoGroup
argument_list|(
literal|0
argument_list|,
name|omKeyLocationInfoList2
argument_list|)
decl_stmt|;
comment|//key = key_three, Blocks = [ {CID = 2, LID = 2}, {CID = 2, LID = 3} ]
name|writeDataToOm
argument_list|(
name|omMetadataManager
argument_list|,
literal|"key_three"
argument_list|,
literal|"bucketOne"
argument_list|,
literal|"sampleVol"
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|omKeyLocationInfoGroup2
argument_list|)
argument_list|)
expr_stmt|;
comment|//Take snapshot of OM DB and copy over to Recon OM DB.
name|DBCheckpoint
name|checkpoint
init|=
name|omMetadataManager
operator|.
name|getStore
argument_list|()
operator|.
name|getCheckpoint
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|File
name|tarFile
init|=
name|OmUtils
operator|.
name|createTarFile
argument_list|(
name|checkpoint
operator|.
name|getCheckpointLocation
argument_list|()
argument_list|)
decl_stmt|;
name|InputStream
name|inputStream
init|=
operator|new
name|FileInputStream
argument_list|(
name|tarFile
argument_list|)
decl_stmt|;
name|PowerMockito
operator|.
name|stub
argument_list|(
name|PowerMockito
operator|.
name|method
argument_list|(
name|ReconUtils
operator|.
name|class
argument_list|,
literal|"makeHttpCall"
argument_list|,
name|CloseableHttpClient
operator|.
name|class
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|toReturn
argument_list|(
name|inputStream
argument_list|)
expr_stmt|;
comment|//Generate Recon container DB data.
name|ContainerKeyMapperTask
name|containerKeyMapperTask
init|=
operator|new
name|ContainerKeyMapperTask
argument_list|(
name|containerDbServiceProvider
argument_list|,
name|ozoneManagerServiceProvider
operator|.
name|getOMMetadataManagerInstance
argument_list|()
argument_list|)
decl_stmt|;
name|ozoneManagerServiceProvider
operator|.
name|updateReconOmDBWithNewSnapshot
argument_list|()
expr_stmt|;
name|containerKeyMapperTask
operator|.
name|reprocess
argument_list|(
name|ozoneManagerServiceProvider
operator|.
name|getOMMetadataManagerInstance
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetKeysForContainer ()
specifier|public
name|void
name|testGetKeysForContainer
parameter_list|()
block|{
name|Response
name|response
init|=
name|containerKeyService
operator|.
name|getKeysForContainer
argument_list|(
literal|1L
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|KeyMetadata
argument_list|>
name|keyMetadataList
init|=
operator|(
name|Collection
argument_list|<
name|KeyMetadata
argument_list|>
operator|)
name|response
operator|.
name|getEntity
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|keyMetadataList
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|KeyMetadata
argument_list|>
name|iterator
init|=
name|keyMetadataList
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|KeyMetadata
name|keyMetadata
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|keyMetadata
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
literal|"key_one"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|keyMetadata
operator|.
name|getVersions
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|keyMetadata
operator|.
name|getBlockIds
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|Long
argument_list|,
name|List
argument_list|<
name|KeyMetadata
operator|.
name|ContainerBlockMetadata
argument_list|>
argument_list|>
name|blockIds
init|=
name|keyMetadata
operator|.
name|getBlockIds
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|blockIds
operator|.
name|get
argument_list|(
literal|0L
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getLocalID
argument_list|()
operator|==
literal|101
argument_list|)
expr_stmt|;
name|keyMetadata
operator|=
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|keyMetadata
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
literal|"key_two"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|keyMetadata
operator|.
name|getVersions
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|keyMetadata
operator|.
name|getVersions
argument_list|()
operator|.
name|contains
argument_list|(
literal|0L
argument_list|)
operator|&&
name|keyMetadata
operator|.
name|getVersions
argument_list|()
operator|.
name|contains
argument_list|(
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|keyMetadata
operator|.
name|getBlockIds
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|blockIds
operator|=
name|keyMetadata
operator|.
name|getBlockIds
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|blockIds
operator|.
name|get
argument_list|(
literal|0L
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getLocalID
argument_list|()
operator|==
literal|103
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|blockIds
operator|.
name|get
argument_list|(
literal|1L
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getLocalID
argument_list|()
operator|==
literal|104
argument_list|)
expr_stmt|;
name|response
operator|=
name|containerKeyService
operator|.
name|getKeysForContainer
argument_list|(
literal|3L
argument_list|)
expr_stmt|;
name|keyMetadataList
operator|=
operator|(
name|Collection
argument_list|<
name|KeyMetadata
argument_list|>
operator|)
name|response
operator|.
name|getEntity
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|keyMetadataList
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetContainers ()
specifier|public
name|void
name|testGetContainers
parameter_list|()
block|{
name|Response
name|response
init|=
name|containerKeyService
operator|.
name|getContainers
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ContainerMetadata
argument_list|>
name|containers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
operator|(
name|Collection
argument_list|<
name|ContainerMetadata
argument_list|>
operator|)
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|containers
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|ContainerMetadata
argument_list|>
name|iterator
init|=
name|containers
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|ContainerMetadata
name|containerMetadata
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|containerMetadata
operator|.
name|getContainerID
argument_list|()
operator|==
literal|1L
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|containerMetadata
operator|.
name|getNumberOfKeys
argument_list|()
operator|==
literal|3L
argument_list|)
expr_stmt|;
name|containerMetadata
operator|=
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|containerMetadata
operator|.
name|getContainerID
argument_list|()
operator|==
literal|2L
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|containerMetadata
operator|.
name|getNumberOfKeys
argument_list|()
operator|==
literal|2L
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get Test OzoneConfiguration instance.    * @return OzoneConfiguration    * @throws IOException ioEx.    */
DECL|method|getTestOzoneConfiguration ()
specifier|private
name|OzoneConfiguration
name|getTestOzoneConfiguration
parameter_list|()
throws|throws
name|IOException
block|{
name|OzoneConfiguration
name|configuration
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|configuration
operator|.
name|set
argument_list|(
name|OZONE_RECON_OM_SNAPSHOT_DB_DIR
argument_list|,
name|temporaryFolder
operator|.
name|newFolder
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|configuration
operator|.
name|set
argument_list|(
name|OZONE_RECON_DB_DIR
argument_list|,
name|temporaryFolder
operator|.
name|newFolder
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|configuration
return|;
block|}
block|}
end_class

end_unit

