begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.recon.types
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
name|types
package|;
end_package

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
name|ozone
operator|.
name|recon
operator|.
name|persistence
operator|.
name|AbstractSqlDatabaseTest
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
name|persistence
operator|.
name|DataSourceConfiguration
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
name|persistence
operator|.
name|JooqPersistenceModule
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
name|hdds
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

begin_comment
comment|/**  * Utility methods to get guice injector and ozone configuration.  */
end_comment

begin_interface
DECL|interface|GuiceInjectorUtilsForTests
specifier|public
interface|interface
name|GuiceInjectorUtilsForTests
block|{
comment|/**    * Get Guice Injector with bindings.    * @param ozoneManagerServiceProvider    * @param reconOMMetadataManager    * @param temporaryFolder    * @return Injector    * @throws IOException ioEx.    */
DECL|method|getInjector ( OzoneManagerServiceProviderImpl ozoneManagerServiceProvider, ReconOMMetadataManager reconOMMetadataManager, TemporaryFolder temporaryFolder )
specifier|default
name|Injector
name|getInjector
parameter_list|(
name|OzoneManagerServiceProviderImpl
name|ozoneManagerServiceProvider
parameter_list|,
name|ReconOMMetadataManager
name|reconOMMetadataManager
parameter_list|,
name|TemporaryFolder
name|temporaryFolder
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|tempDir
init|=
name|temporaryFolder
operator|.
name|newFolder
argument_list|()
decl_stmt|;
name|AbstractSqlDatabaseTest
operator|.
name|DataSourceConfigurationProvider
name|configurationProvider
init|=
operator|new
name|AbstractSqlDatabaseTest
operator|.
name|DataSourceConfigurationProvider
argument_list|(
name|tempDir
argument_list|)
decl_stmt|;
name|JooqPersistenceModule
name|jooqPersistenceModule
init|=
operator|new
name|JooqPersistenceModule
argument_list|(
name|configurationProvider
argument_list|)
decl_stmt|;
return|return
name|Guice
operator|.
name|createInjector
argument_list|(
name|jooqPersistenceModule
argument_list|,
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
name|DataSourceConfiguration
operator|.
name|class
argument_list|)
operator|.
name|toProvider
argument_list|(
name|configurationProvider
argument_list|)
expr_stmt|;
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
argument_list|(
name|temporaryFolder
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|reconOMMetadataManager
operator|!=
literal|null
condition|)
block|{
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
block|}
if|if
condition|(
name|ozoneManagerServiceProvider
operator|!=
literal|null
condition|)
block|{
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
block|}
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
return|;
block|}
comment|/**    * Get Test OzoneConfiguration instance.    * @return OzoneConfiguration    * @throws IOException ioEx.    */
DECL|method|getTestOzoneConfiguration ( TemporaryFolder temporaryFolder)
specifier|default
name|OzoneConfiguration
name|getTestOzoneConfiguration
parameter_list|(
name|TemporaryFolder
name|temporaryFolder
parameter_list|)
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
end_interface

end_unit

