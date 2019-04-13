begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.appcatalog.application
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|appcatalog
operator|.
name|application
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
name|yarn
operator|.
name|appcatalog
operator|.
name|model
operator|.
name|AppEntry
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
name|yarn
operator|.
name|appcatalog
operator|.
name|model
operator|.
name|AppStoreEntry
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
name|yarn
operator|.
name|appcatalog
operator|.
name|model
operator|.
name|Application
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|SolrClient
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
name|Test
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
import|import static
name|org
operator|.
name|powermock
operator|.
name|api
operator|.
name|mockito
operator|.
name|PowerMockito
operator|.
name|when
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|powermock
operator|.
name|api
operator|.
name|support
operator|.
name|membermodification
operator|.
name|MemberMatcher
operator|.
name|method
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Unit test for AppCatalogSolrClient.  */
end_comment

begin_class
DECL|class|TestAppCatalogSolrClient
specifier|public
class|class
name|TestAppCatalogSolrClient
block|{
DECL|field|CONFIGSET_DIR
specifier|static
specifier|final
name|String
name|CONFIGSET_DIR
init|=
literal|"src/test/resources/configsets"
decl_stmt|;
DECL|field|solrClient
specifier|private
specifier|static
name|SolrClient
name|solrClient
decl_stmt|;
DECL|field|spy
specifier|private
specifier|static
name|AppCatalogSolrClient
name|spy
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|targetLocation
init|=
name|EmbeddedSolrServerFactory
operator|.
name|class
operator|.
name|getProtectionDomain
argument_list|()
operator|.
name|getCodeSource
argument_list|()
operator|.
name|getLocation
argument_list|()
operator|.
name|getFile
argument_list|()
operator|+
literal|"/.."
decl_stmt|;
name|String
name|solrHome
init|=
name|targetLocation
operator|+
literal|"/solr"
decl_stmt|;
name|solrClient
operator|=
name|EmbeddedSolrServerFactory
operator|.
name|create
argument_list|(
name|solrHome
argument_list|,
name|CONFIGSET_DIR
argument_list|,
literal|"exampleCollection"
argument_list|)
expr_stmt|;
name|spy
operator|=
name|PowerMockito
operator|.
name|spy
argument_list|(
operator|new
name|AppCatalogSolrClient
argument_list|()
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|spy
argument_list|,
name|method
argument_list|(
name|AppCatalogSolrClient
operator|.
name|class
argument_list|,
literal|"getSolrClient"
argument_list|)
argument_list|)
operator|.
name|withNoArguments
argument_list|()
operator|.
name|thenReturn
argument_list|(
name|solrClient
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
throws|throws
name|Exception
block|{
try|try
block|{
name|solrClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{     }
block|}
annotation|@
name|Test
DECL|method|testRegister ()
specifier|public
name|void
name|testRegister
parameter_list|()
throws|throws
name|Exception
block|{
name|Application
name|example
init|=
operator|new
name|Application
argument_list|()
decl_stmt|;
name|example
operator|.
name|setOrganization
argument_list|(
literal|"jenkins-ci.org"
argument_list|)
expr_stmt|;
name|example
operator|.
name|setName
argument_list|(
literal|"jenkins"
argument_list|)
expr_stmt|;
name|example
operator|.
name|setDescription
argument_list|(
literal|"World leading open source automation system."
argument_list|)
expr_stmt|;
name|example
operator|.
name|setIcon
argument_list|(
literal|"/css/img/feather.png"
argument_list|)
expr_stmt|;
name|spy
operator|.
name|register
argument_list|(
name|example
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|AppStoreEntry
argument_list|>
name|apps
init|=
name|spy
operator|.
name|getRecommendedApps
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|apps
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSearch ()
specifier|public
name|void
name|testSearch
parameter_list|()
throws|throws
name|Exception
block|{
name|Application
name|example
init|=
operator|new
name|Application
argument_list|()
decl_stmt|;
name|example
operator|.
name|setOrganization
argument_list|(
literal|"jenkins-ci.org"
argument_list|)
expr_stmt|;
name|example
operator|.
name|setName
argument_list|(
literal|"jenkins"
argument_list|)
expr_stmt|;
name|example
operator|.
name|setDescription
argument_list|(
literal|"World leading open source automation system."
argument_list|)
expr_stmt|;
name|example
operator|.
name|setIcon
argument_list|(
literal|"/css/img/feather.png"
argument_list|)
expr_stmt|;
name|spy
operator|.
name|register
argument_list|(
name|example
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|AppStoreEntry
argument_list|>
name|results
init|=
name|spy
operator|.
name|search
argument_list|(
literal|"name_s:jenkins"
argument_list|)
decl_stmt|;
name|int
name|expected
init|=
literal|1
decl_stmt|;
name|int
name|actual
init|=
name|results
operator|.
name|size
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNotFoundSearch ()
specifier|public
name|void
name|testNotFoundSearch
parameter_list|()
throws|throws
name|Exception
block|{
name|Application
name|example
init|=
operator|new
name|Application
argument_list|()
decl_stmt|;
name|example
operator|.
name|setOrganization
argument_list|(
literal|"jenkins-ci.org"
argument_list|)
expr_stmt|;
name|example
operator|.
name|setName
argument_list|(
literal|"jenkins"
argument_list|)
expr_stmt|;
name|example
operator|.
name|setDescription
argument_list|(
literal|"World leading open source automation system."
argument_list|)
expr_stmt|;
name|example
operator|.
name|setIcon
argument_list|(
literal|"/css/img/feather.png"
argument_list|)
expr_stmt|;
name|spy
operator|.
name|register
argument_list|(
name|example
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|AppStoreEntry
argument_list|>
name|results
init|=
name|spy
operator|.
name|search
argument_list|(
literal|"name_s:abc"
argument_list|)
decl_stmt|;
name|int
name|expected
init|=
literal|0
decl_stmt|;
name|int
name|actual
init|=
name|results
operator|.
name|size
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetRecommendedApps ()
specifier|public
name|void
name|testGetRecommendedApps
parameter_list|()
throws|throws
name|Exception
block|{
name|AppStoreEntry
name|example
init|=
operator|new
name|AppStoreEntry
argument_list|()
decl_stmt|;
name|example
operator|.
name|setOrg
argument_list|(
literal|"jenkins-ci.org"
argument_list|)
expr_stmt|;
name|example
operator|.
name|setName
argument_list|(
literal|"jenkins"
argument_list|)
expr_stmt|;
name|example
operator|.
name|setDesc
argument_list|(
literal|"World leading open source automation system."
argument_list|)
expr_stmt|;
name|example
operator|.
name|setIcon
argument_list|(
literal|"/css/img/feather.png"
argument_list|)
expr_stmt|;
name|example
operator|.
name|setDownload
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|spy
operator|.
name|register
argument_list|(
name|example
argument_list|)
expr_stmt|;
name|AppStoreEntry
name|example2
init|=
operator|new
name|AppStoreEntry
argument_list|()
decl_stmt|;
name|example2
operator|.
name|setOrg
argument_list|(
literal|"Apache"
argument_list|)
expr_stmt|;
name|example2
operator|.
name|setName
argument_list|(
literal|"httpd"
argument_list|)
expr_stmt|;
name|example2
operator|.
name|setDesc
argument_list|(
literal|"Apache webserver"
argument_list|)
expr_stmt|;
name|example2
operator|.
name|setIcon
argument_list|(
literal|"/css/img/feather.png"
argument_list|)
expr_stmt|;
name|example2
operator|.
name|setDownload
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|spy
operator|.
name|register
argument_list|(
name|example2
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|AppStoreEntry
argument_list|>
name|actual
init|=
name|spy
operator|.
name|getRecommendedApps
argument_list|()
decl_stmt|;
name|long
name|previous
init|=
literal|1000L
decl_stmt|;
for|for
control|(
name|AppStoreEntry
name|app
range|:
name|actual
control|)
block|{
name|assertTrue
argument_list|(
literal|"Recommend app is not sort by download count."
argument_list|,
name|previous
operator|>
name|app
operator|.
name|getDownload
argument_list|()
argument_list|)
expr_stmt|;
name|previous
operator|=
name|app
operator|.
name|getDownload
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testUpgradeApp ()
specifier|public
name|void
name|testUpgradeApp
parameter_list|()
throws|throws
name|Exception
block|{
name|Application
name|example
init|=
operator|new
name|Application
argument_list|()
decl_stmt|;
name|String
name|expected
init|=
literal|"2.0"
decl_stmt|;
name|String
name|actual
init|=
literal|""
decl_stmt|;
name|example
operator|.
name|setOrganization
argument_list|(
literal|"jenkins-ci.org"
argument_list|)
expr_stmt|;
name|example
operator|.
name|setVersion
argument_list|(
literal|"1.0"
argument_list|)
expr_stmt|;
name|example
operator|.
name|setName
argument_list|(
literal|"jenkins"
argument_list|)
expr_stmt|;
name|example
operator|.
name|setDescription
argument_list|(
literal|"World leading open source automation system."
argument_list|)
expr_stmt|;
name|example
operator|.
name|setIcon
argument_list|(
literal|"/css/img/feather.png"
argument_list|)
expr_stmt|;
name|spy
operator|.
name|register
argument_list|(
name|example
argument_list|)
expr_stmt|;
name|spy
operator|.
name|deployApp
argument_list|(
literal|"test"
argument_list|,
name|example
argument_list|)
expr_stmt|;
name|example
operator|.
name|setVersion
argument_list|(
literal|"2.0"
argument_list|)
expr_stmt|;
name|spy
operator|.
name|upgradeApp
argument_list|(
name|example
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|AppEntry
argument_list|>
name|appEntries
init|=
name|spy
operator|.
name|listAppEntries
argument_list|()
decl_stmt|;
name|actual
operator|=
name|appEntries
operator|.
name|get
argument_list|(
name|appEntries
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|getYarnfile
argument_list|()
operator|.
name|getVersion
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

