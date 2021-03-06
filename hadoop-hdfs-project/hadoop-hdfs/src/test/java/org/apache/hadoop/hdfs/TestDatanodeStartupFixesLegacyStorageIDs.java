begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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
name|hdfs
operator|.
name|server
operator|.
name|protocol
operator|.
name|DatanodeStorage
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
name|protocol
operator|.
name|StorageReport
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|TestDFSUpgradeFromImage
operator|.
name|ClusterVerifier
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
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * The test verifies that legacy storage IDs in older DataNode  * images are replaced with UUID-based storage IDs. The startup may  * or may not involve a Datanode Layout upgrade. Each test case uses  * the following resource files.  *  *    1. testCaseName.tgz - NN and DN directories corresponding  *                          to a specific layout version.  *    2. testCaseName.txt - Text file listing the checksum of each file  *                          in the cluster and overall checksum. See  *                          TestUpgradeFromImage for the file format.  *  * If any test case is renamed then the corresponding resource files must  * also be renamed.  */
end_comment

begin_class
DECL|class|TestDatanodeStartupFixesLegacyStorageIDs
specifier|public
class|class
name|TestDatanodeStartupFixesLegacyStorageIDs
block|{
comment|/**    * Perform a upgrade using the test image corresponding to    * testCaseName.    *    * @param testCaseName    * @param expectedStorageId if null, then the upgrade generates a new    *                          unique storage ID.    * @throws IOException    */
DECL|method|runLayoutUpgradeTest (final String testCaseName, final String expectedStorageId)
specifier|private
specifier|static
name|void
name|runLayoutUpgradeTest
parameter_list|(
specifier|final
name|String
name|testCaseName
parameter_list|,
specifier|final
name|String
name|expectedStorageId
parameter_list|)
throws|throws
name|IOException
block|{
name|TestDFSUpgradeFromImage
name|upgrade
init|=
operator|new
name|TestDFSUpgradeFromImage
argument_list|()
decl_stmt|;
name|upgrade
operator|.
name|unpackStorage
argument_list|(
name|testCaseName
operator|+
literal|".tgz"
argument_list|,
name|testCaseName
operator|+
literal|".txt"
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
name|TestDFSUpgradeFromImage
operator|.
name|upgradeConf
argument_list|)
decl_stmt|;
name|initStorageDirs
argument_list|(
name|conf
argument_list|,
name|testCaseName
argument_list|)
expr_stmt|;
name|upgradeAndVerify
argument_list|(
name|upgrade
argument_list|,
name|conf
argument_list|,
operator|new
name|ClusterVerifier
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|verifyClusterPostUpgrade
parameter_list|(
name|MiniDFSCluster
name|cluster
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Verify that a GUID-based storage ID was generated.
specifier|final
name|String
name|bpid
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
decl_stmt|;
name|StorageReport
index|[]
name|reports
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFSDataset
argument_list|()
operator|.
name|getStorageReports
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|reports
operator|.
name|length
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|storageID
init|=
name|reports
index|[
literal|0
index|]
operator|.
name|getStorage
argument_list|()
operator|.
name|getStorageID
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|DatanodeStorage
operator|.
name|isValidStorageId
argument_list|(
name|storageID
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|expectedStorageId
operator|!=
literal|null
condition|)
block|{
name|assertThat
argument_list|(
name|storageID
argument_list|,
name|is
argument_list|(
name|expectedStorageId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|initStorageDirs (final Configuration conf, final String testName)
specifier|private
specifier|static
name|void
name|initStorageDirs
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|String
name|testName
parameter_list|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|,
name|GenericTestUtils
operator|.
name|getTempPath
argument_list|(
name|testName
operator|+
name|File
operator|.
name|separator
operator|+
literal|"dfs"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"data"
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_NAME_DIR_KEY
argument_list|,
name|GenericTestUtils
operator|.
name|getTempPath
argument_list|(
name|testName
operator|+
name|File
operator|.
name|separator
operator|+
literal|"dfs"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|upgradeAndVerify (final TestDFSUpgradeFromImage upgrade, final Configuration conf, final ClusterVerifier verifier)
specifier|private
specifier|static
name|void
name|upgradeAndVerify
parameter_list|(
specifier|final
name|TestDFSUpgradeFromImage
name|upgrade
parameter_list|,
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|ClusterVerifier
name|verifier
parameter_list|)
throws|throws
name|IOException
block|{
name|upgrade
operator|.
name|upgradeAndVerify
argument_list|(
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|1
argument_list|)
operator|.
name|manageDataDfsDirs
argument_list|(
literal|false
argument_list|)
operator|.
name|manageNameDfsDirs
argument_list|(
literal|false
argument_list|)
argument_list|,
name|verifier
argument_list|)
expr_stmt|;
block|}
comment|/**    * Upgrade from 2.2 (no storage IDs per volume) correctly generates    * GUID-based storage IDs. Test case for HDFS-7575.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testUpgradeFrom22FixesStorageIDs ()
specifier|public
name|void
name|testUpgradeFrom22FixesStorageIDs
parameter_list|()
throws|throws
name|IOException
block|{
name|runLayoutUpgradeTest
argument_list|(
name|GenericTestUtils
operator|.
name|getMethodName
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Startup from a 2.6-layout that has legacy storage IDs correctly    * generates new storage IDs.    * Test case for HDFS-7575.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testUpgradeFrom22via26FixesStorageIDs ()
specifier|public
name|void
name|testUpgradeFrom22via26FixesStorageIDs
parameter_list|()
throws|throws
name|IOException
block|{
name|runLayoutUpgradeTest
argument_list|(
name|GenericTestUtils
operator|.
name|getMethodName
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Startup from a 2.6-layout that already has unique storage IDs does    * not regenerate the storage IDs.    * Test case for HDFS-7575.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testUpgradeFrom26PreservesStorageIDs ()
specifier|public
name|void
name|testUpgradeFrom26PreservesStorageIDs
parameter_list|()
throws|throws
name|IOException
block|{
comment|// StorageId present in the image testUpgradeFrom26PreservesStorageId.tgz
name|runLayoutUpgradeTest
argument_list|(
name|GenericTestUtils
operator|.
name|getMethodName
argument_list|()
argument_list|,
literal|"DS-a0e39cfa-930f-4abd-813c-e22b59223774"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

