begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
package|package
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
name|namenode
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
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
name|net
operator|.
name|URI
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
name|hdfs
operator|.
name|HdfsConfiguration
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
name|protocol
operator|.
name|LayoutVersion
operator|.
name|Feature
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
name|common
operator|.
name|HdfsServerConstants
operator|.
name|StartupOption
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

begin_comment
comment|/**  * This class tests various upgrade cases from earlier versions to current  * version with and without clusterid.  */
end_comment

begin_class
DECL|class|TestStartupOptionUpgrade
specifier|public
class|class
name|TestStartupOptionUpgrade
block|{
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|startOpt
specifier|private
name|StartupOption
name|startOpt
decl_stmt|;
DECL|field|layoutVersion
specifier|private
name|int
name|layoutVersion
decl_stmt|;
DECL|field|storage
name|NNStorage
name|storage
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
name|HdfsConfiguration
argument_list|()
expr_stmt|;
name|startOpt
operator|=
name|StartupOption
operator|.
name|UPGRADE
expr_stmt|;
name|startOpt
operator|.
name|setClusterId
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|storage
operator|=
operator|new
name|NNStorage
argument_list|(
name|conf
argument_list|,
name|Collections
operator|.
expr|<
name|URI
operator|>
name|emptyList
argument_list|()
argument_list|,
name|Collections
operator|.
expr|<
name|URI
operator|>
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
literal|null
expr_stmt|;
name|startOpt
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Tests the upgrade from version 0.20.204 to Federation version Test without    * clusterid the case: -upgrade     * Expected to generate clusterid    *     * @throws Exception    */
annotation|@
name|Test
DECL|method|testStartupOptUpgradeFrom204 ()
specifier|public
name|void
name|testStartupOptUpgradeFrom204
parameter_list|()
throws|throws
name|Exception
block|{
name|layoutVersion
operator|=
name|Feature
operator|.
name|RESERVED_REL20_204
operator|.
name|getInfo
argument_list|()
operator|.
name|getLayoutVersion
argument_list|()
expr_stmt|;
name|storage
operator|.
name|processStartupOptionsForUpgrade
argument_list|(
name|startOpt
argument_list|,
name|layoutVersion
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Clusterid should start with CID"
argument_list|,
name|storage
operator|.
name|getClusterID
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"CID"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests the upgrade from version 0.22 to Federation version Test with    * clusterid case: -upgrade -clusterid<cid>     * Expected to reuse user given clusterid    *     * @throws Exception    */
annotation|@
name|Test
DECL|method|testStartupOptUpgradeFrom22WithCID ()
specifier|public
name|void
name|testStartupOptUpgradeFrom22WithCID
parameter_list|()
throws|throws
name|Exception
block|{
name|startOpt
operator|.
name|setClusterId
argument_list|(
literal|"cid"
argument_list|)
expr_stmt|;
name|layoutVersion
operator|=
name|Feature
operator|.
name|RESERVED_REL22
operator|.
name|getInfo
argument_list|()
operator|.
name|getLayoutVersion
argument_list|()
expr_stmt|;
name|storage
operator|.
name|processStartupOptionsForUpgrade
argument_list|(
name|startOpt
argument_list|,
name|layoutVersion
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Clusterid should match with the given clusterid"
argument_list|,
literal|"cid"
argument_list|,
name|storage
operator|.
name|getClusterID
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests the upgrade from one version of Federation to another Federation    * version Test without clusterid case: -upgrade    * Expected to reuse existing clusterid    *     * @throws Exception    */
annotation|@
name|Test
DECL|method|testStartupOptUpgradeFromFederation ()
specifier|public
name|void
name|testStartupOptUpgradeFromFederation
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Test assumes clusterid already exists, set the clusterid
name|storage
operator|.
name|setClusterID
argument_list|(
literal|"currentcid"
argument_list|)
expr_stmt|;
name|layoutVersion
operator|=
name|Feature
operator|.
name|FEDERATION
operator|.
name|getInfo
argument_list|()
operator|.
name|getLayoutVersion
argument_list|()
expr_stmt|;
name|storage
operator|.
name|processStartupOptionsForUpgrade
argument_list|(
name|startOpt
argument_list|,
name|layoutVersion
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Clusterid should match with the existing one"
argument_list|,
literal|"currentcid"
argument_list|,
name|storage
operator|.
name|getClusterID
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests the upgrade from one version of Federation to another Federation    * version Test with wrong clusterid case: -upgrade -clusterid<cid>     * Expected to reuse existing clusterid and ignore user given clusterid    *     * @throws Exception    */
annotation|@
name|Test
DECL|method|testStartupOptUpgradeFromFederationWithWrongCID ()
specifier|public
name|void
name|testStartupOptUpgradeFromFederationWithWrongCID
parameter_list|()
throws|throws
name|Exception
block|{
name|startOpt
operator|.
name|setClusterId
argument_list|(
literal|"wrong-cid"
argument_list|)
expr_stmt|;
name|storage
operator|.
name|setClusterID
argument_list|(
literal|"currentcid"
argument_list|)
expr_stmt|;
name|layoutVersion
operator|=
name|Feature
operator|.
name|FEDERATION
operator|.
name|getInfo
argument_list|()
operator|.
name|getLayoutVersion
argument_list|()
expr_stmt|;
name|storage
operator|.
name|processStartupOptionsForUpgrade
argument_list|(
name|startOpt
argument_list|,
name|layoutVersion
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Clusterid should match with the existing one"
argument_list|,
literal|"currentcid"
argument_list|,
name|storage
operator|.
name|getClusterID
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests the upgrade from one version of Federation to another Federation    * version Test with correct clusterid case: -upgrade -clusterid<cid>    * Expected to reuse existing clusterid and ignore user given clusterid    *     * @throws Exception    */
annotation|@
name|Test
DECL|method|testStartupOptUpgradeFromFederationWithCID ()
specifier|public
name|void
name|testStartupOptUpgradeFromFederationWithCID
parameter_list|()
throws|throws
name|Exception
block|{
name|startOpt
operator|.
name|setClusterId
argument_list|(
literal|"currentcid"
argument_list|)
expr_stmt|;
name|storage
operator|.
name|setClusterID
argument_list|(
literal|"currentcid"
argument_list|)
expr_stmt|;
name|layoutVersion
operator|=
name|Feature
operator|.
name|FEDERATION
operator|.
name|getInfo
argument_list|()
operator|.
name|getLayoutVersion
argument_list|()
expr_stmt|;
name|storage
operator|.
name|processStartupOptionsForUpgrade
argument_list|(
name|startOpt
argument_list|,
name|layoutVersion
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Clusterid should match with the existing one"
argument_list|,
literal|"currentcid"
argument_list|,
name|storage
operator|.
name|getClusterID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

