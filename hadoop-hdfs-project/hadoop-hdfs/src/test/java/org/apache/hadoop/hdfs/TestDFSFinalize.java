begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
import|import static
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
name|NodeType
operator|.
name|DATA_NODE
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
name|assertFalse
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
name|List
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
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|FSImageTestUtil
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
name|BlockPoolSliceStorage
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
name|DataStorage
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
name|Test
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_comment
comment|/**  * This test ensures the appropriate response from the system when   * the system is finalized.  */
end_comment

begin_class
DECL|class|TestDFSFinalize
specifier|public
class|class
name|TestDFSFinalize
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
literal|"org.apache.hadoop.hdfs.TestDFSFinalize"
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|testCounter
specifier|private
name|int
name|testCounter
init|=
literal|0
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
comment|/**    * Writes an INFO log message containing the parameters.    */
DECL|method|log (String label, int numDirs)
name|void
name|log
parameter_list|(
name|String
name|label
parameter_list|,
name|int
name|numDirs
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"============================================================"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"***TEST "
operator|+
operator|(
name|testCounter
operator|++
operator|)
operator|+
literal|"*** "
operator|+
name|label
operator|+
literal|":"
operator|+
literal|" numDirs="
operator|+
name|numDirs
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify that the current directory exists and that the previous directory    * does not exist.  Verify that current hasn't been modified by comparing     * the checksum of all it's containing files with their original checksum.    */
DECL|method|checkResult (String[] nameNodeDirs, String[] dataNodeDirs, String bpid)
specifier|static
name|void
name|checkResult
parameter_list|(
name|String
index|[]
name|nameNodeDirs
parameter_list|,
name|String
index|[]
name|dataNodeDirs
parameter_list|,
name|String
name|bpid
parameter_list|)
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|File
argument_list|>
name|dirs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
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
name|nameNodeDirs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|File
name|curDir
init|=
operator|new
name|File
argument_list|(
name|nameNodeDirs
index|[
name|i
index|]
argument_list|,
literal|"current"
argument_list|)
decl_stmt|;
name|dirs
operator|.
name|add
argument_list|(
name|curDir
argument_list|)
expr_stmt|;
name|FSImageTestUtil
operator|.
name|assertReasonableNameCurrentDir
argument_list|(
name|curDir
argument_list|)
expr_stmt|;
block|}
name|FSImageTestUtil
operator|.
name|assertParallelFilesAreIdentical
argument_list|(
name|dirs
argument_list|,
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|dnCurDirs
index|[]
init|=
operator|new
name|File
index|[
name|dataNodeDirs
operator|.
name|length
index|]
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
name|dataNodeDirs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|dnCurDirs
index|[
name|i
index|]
operator|=
operator|new
name|File
argument_list|(
name|dataNodeDirs
index|[
name|i
index|]
argument_list|,
literal|"current"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|UpgradeUtilities
operator|.
name|checksumContents
argument_list|(
name|DATA_NODE
argument_list|,
name|dnCurDirs
index|[
name|i
index|]
argument_list|)
argument_list|,
name|UpgradeUtilities
operator|.
name|checksumMasterDataNodeContents
argument_list|()
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
name|nameNodeDirs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertFalse
argument_list|(
operator|new
name|File
argument_list|(
name|nameNodeDirs
index|[
name|i
index|]
argument_list|,
literal|"previous"
argument_list|)
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bpid
operator|==
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|dataNodeDirs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertFalse
argument_list|(
operator|new
name|File
argument_list|(
name|dataNodeDirs
index|[
name|i
index|]
argument_list|,
literal|"previous"
argument_list|)
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|dataNodeDirs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|File
name|bpRoot
init|=
name|BlockPoolSliceStorage
operator|.
name|getBpRoot
argument_list|(
name|bpid
argument_list|,
name|dnCurDirs
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
operator|new
name|File
argument_list|(
name|bpRoot
argument_list|,
literal|"previous"
argument_list|)
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|bpCurFinalizeDir
init|=
operator|new
name|File
argument_list|(
name|bpRoot
argument_list|,
literal|"current/"
operator|+
name|DataStorage
operator|.
name|STORAGE_DIR_FINALIZED
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|UpgradeUtilities
operator|.
name|checksumContents
argument_list|(
name|DATA_NODE
argument_list|,
name|bpCurFinalizeDir
argument_list|)
argument_list|,
name|UpgradeUtilities
operator|.
name|checksumMasterBlockPoolFinalizedContents
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * This test attempts to finalize the NameNode and DataNode.    */
annotation|@
name|Test
DECL|method|testFinalize ()
specifier|public
name|void
name|testFinalize
parameter_list|()
throws|throws
name|Exception
block|{
name|UpgradeUtilities
operator|.
name|initialize
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|numDirs
init|=
literal|1
init|;
name|numDirs
operator|<=
literal|2
condition|;
name|numDirs
operator|++
control|)
block|{
comment|/* This test requires that "current" directory not change after        * the upgrade. Actually it is ok for those contents to change.        * For now disabling block verification so that the contents are         * not changed.        */
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_SCAN_PERIOD_HOURS_KEY
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|=
name|UpgradeUtilities
operator|.
name|initializeStorageStateConf
argument_list|(
name|numDirs
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|String
index|[]
name|nameNodeDirs
init|=
name|conf
operator|.
name|getStrings
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_NAME_DIR_KEY
argument_list|)
decl_stmt|;
name|String
index|[]
name|dataNodeDirs
init|=
name|conf
operator|.
name|getStrings
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|)
decl_stmt|;
name|log
argument_list|(
literal|"Finalize NN& DN with existing previous dir"
argument_list|,
name|numDirs
argument_list|)
expr_stmt|;
name|UpgradeUtilities
operator|.
name|createNameNodeStorageDirs
argument_list|(
name|nameNodeDirs
argument_list|,
literal|"current"
argument_list|)
expr_stmt|;
name|UpgradeUtilities
operator|.
name|createNameNodeStorageDirs
argument_list|(
name|nameNodeDirs
argument_list|,
literal|"previous"
argument_list|)
expr_stmt|;
name|UpgradeUtilities
operator|.
name|createDataNodeStorageDirs
argument_list|(
name|dataNodeDirs
argument_list|,
literal|"current"
argument_list|)
expr_stmt|;
name|UpgradeUtilities
operator|.
name|createDataNodeStorageDirs
argument_list|(
name|dataNodeDirs
argument_list|,
literal|"previous"
argument_list|)
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|format
argument_list|(
literal|false
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
operator|.
name|startupOption
argument_list|(
name|StartupOption
operator|.
name|REGULAR
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|finalizeCluster
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|triggerBlockReports
argument_list|()
expr_stmt|;
comment|// 1 second should be enough for asynchronous DN finalize
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|checkResult
argument_list|(
name|nameNodeDirs
argument_list|,
name|dataNodeDirs
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|log
argument_list|(
literal|"Finalize NN& DN without existing previous dir"
argument_list|,
name|numDirs
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|finalizeCluster
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|triggerBlockReports
argument_list|()
expr_stmt|;
comment|// 1 second should be enough for asynchronous DN finalize
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|checkResult
argument_list|(
name|nameNodeDirs
argument_list|,
name|dataNodeDirs
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|UpgradeUtilities
operator|.
name|createEmptyDirs
argument_list|(
name|nameNodeDirs
argument_list|)
expr_stmt|;
name|UpgradeUtilities
operator|.
name|createEmptyDirs
argument_list|(
name|dataNodeDirs
argument_list|)
expr_stmt|;
name|log
argument_list|(
literal|"Finalize NN& BP with existing previous dir"
argument_list|,
name|numDirs
argument_list|)
expr_stmt|;
name|String
name|bpid
init|=
name|UpgradeUtilities
operator|.
name|getCurrentBlockPoolID
argument_list|(
name|cluster
argument_list|)
decl_stmt|;
name|UpgradeUtilities
operator|.
name|createNameNodeStorageDirs
argument_list|(
name|nameNodeDirs
argument_list|,
literal|"current"
argument_list|)
expr_stmt|;
name|UpgradeUtilities
operator|.
name|createNameNodeStorageDirs
argument_list|(
name|nameNodeDirs
argument_list|,
literal|"previous"
argument_list|)
expr_stmt|;
name|UpgradeUtilities
operator|.
name|createDataNodeStorageDirs
argument_list|(
name|dataNodeDirs
argument_list|,
literal|"current"
argument_list|)
expr_stmt|;
name|UpgradeUtilities
operator|.
name|createBlockPoolStorageDirs
argument_list|(
name|dataNodeDirs
argument_list|,
literal|"current"
argument_list|,
name|bpid
argument_list|)
expr_stmt|;
name|UpgradeUtilities
operator|.
name|createBlockPoolStorageDirs
argument_list|(
name|dataNodeDirs
argument_list|,
literal|"previous"
argument_list|,
name|bpid
argument_list|)
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|format
argument_list|(
literal|false
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
operator|.
name|startupOption
argument_list|(
name|StartupOption
operator|.
name|REGULAR
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|finalizeCluster
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|triggerBlockReports
argument_list|()
expr_stmt|;
comment|// 1 second should be enough for asynchronous BP finalize
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|checkResult
argument_list|(
name|nameNodeDirs
argument_list|,
name|dataNodeDirs
argument_list|,
name|bpid
argument_list|)
expr_stmt|;
name|log
argument_list|(
literal|"Finalize NN& BP without existing previous dir"
argument_list|,
name|numDirs
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|finalizeCluster
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|triggerBlockReports
argument_list|()
expr_stmt|;
comment|// 1 second should be enough for asynchronous BP finalize
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|checkResult
argument_list|(
name|nameNodeDirs
argument_list|,
name|dataNodeDirs
argument_list|,
name|bpid
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|UpgradeUtilities
operator|.
name|createEmptyDirs
argument_list|(
name|nameNodeDirs
argument_list|)
expr_stmt|;
name|UpgradeUtilities
operator|.
name|createEmptyDirs
argument_list|(
name|dataNodeDirs
argument_list|)
expr_stmt|;
block|}
comment|// end numDir loop
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Shutting down MiniDFSCluster"
argument_list|)
expr_stmt|;
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
operator|new
name|TestDFSFinalize
argument_list|()
operator|.
name|testFinalize
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

