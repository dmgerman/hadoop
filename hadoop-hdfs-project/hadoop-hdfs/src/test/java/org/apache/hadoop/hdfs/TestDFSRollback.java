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
name|NAME_NODE
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
name|fs
operator|.
name|Path
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
name|NodeType
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
name|common
operator|.
name|StorageInfo
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
name|util
operator|.
name|StringUtils
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
name|base
operator|.
name|Charsets
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
comment|/** * This test ensures the appropriate response (successful or failure) from * the system when the system is rolled back under various storage state and * version conditions. */
end_comment

begin_class
DECL|class|TestDFSRollback
specifier|public
class|class
name|TestDFSRollback
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
literal|"org.apache.hadoop.hdfs.TestDFSRollback"
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
comment|/**    * Verify that the new current directory is the old previous.      * It is assumed that the server has recovered and rolled back.    */
DECL|method|checkResult (NodeType nodeType, String[] baseDirs)
name|void
name|checkResult
parameter_list|(
name|NodeType
name|nodeType
parameter_list|,
name|String
index|[]
name|baseDirs
parameter_list|)
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|File
argument_list|>
name|curDirs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|baseDir
range|:
name|baseDirs
control|)
block|{
name|File
name|curDir
init|=
operator|new
name|File
argument_list|(
name|baseDir
argument_list|,
literal|"current"
argument_list|)
decl_stmt|;
name|curDirs
operator|.
name|add
argument_list|(
name|curDir
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|nodeType
condition|)
block|{
case|case
name|NAME_NODE
case|:
name|FSImageTestUtil
operator|.
name|assertReasonableNameCurrentDir
argument_list|(
name|curDir
argument_list|)
expr_stmt|;
break|break;
case|case
name|DATA_NODE
case|:
name|assertEquals
argument_list|(
name|UpgradeUtilities
operator|.
name|checksumContents
argument_list|(
name|nodeType
argument_list|,
name|curDir
argument_list|)
argument_list|,
name|UpgradeUtilities
operator|.
name|checksumMasterDataNodeContents
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
name|FSImageTestUtil
operator|.
name|assertParallelFilesAreIdentical
argument_list|(
name|curDirs
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|baseDirs
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
name|baseDirs
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
comment|/**    * Attempts to start a NameNode with the given operation.  Starting    * the NameNode should throw an exception.    */
DECL|method|startNameNodeShouldFail (StartupOption operation, String searchString)
name|void
name|startNameNodeShouldFail
parameter_list|(
name|StartupOption
name|operation
parameter_list|,
name|String
name|searchString
parameter_list|)
block|{
try|try
block|{
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
name|numDataNodes
argument_list|(
literal|0
argument_list|)
operator|.
name|startupOption
argument_list|(
name|operation
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
name|build
argument_list|()
expr_stmt|;
comment|// should fail
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"NameNode should have failed to start"
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|expected
parameter_list|)
block|{
if|if
condition|(
operator|!
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
name|searchString
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"Expected substring '"
operator|+
name|searchString
operator|+
literal|"' in exception "
operator|+
literal|"but got: "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// expected
block|}
block|}
comment|/**    * Attempts to start a DataNode with the given operation. Starting    * the given block pool should fail.    * @param operation startup option    * @param bpid block pool Id that should fail to start    * @throws IOException     */
DECL|method|startBlockPoolShouldFail (StartupOption operation, String bpid)
name|void
name|startBlockPoolShouldFail
parameter_list|(
name|StartupOption
name|operation
parameter_list|,
name|String
name|bpid
parameter_list|)
throws|throws
name|IOException
block|{
name|cluster
operator|.
name|startDataNodes
argument_list|(
name|conf
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|,
name|operation
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// should fail
name|assertFalse
argument_list|(
literal|"Block pool "
operator|+
name|bpid
operator|+
literal|" should have failed to start"
argument_list|,
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
name|isBPServiceAlive
argument_list|(
name|bpid
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * This test attempts to rollback the NameNode and DataNode under    * a number of valid and invalid conditions.    */
annotation|@
name|Test
DECL|method|testRollback ()
specifier|public
name|void
name|testRollback
parameter_list|()
throws|throws
name|Exception
block|{
name|File
index|[]
name|baseDirs
decl_stmt|;
name|UpgradeUtilities
operator|.
name|initialize
argument_list|()
expr_stmt|;
name|StorageInfo
name|storageInfo
init|=
literal|null
decl_stmt|;
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
literal|"Normal NameNode rollback"
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
name|numDataNodes
argument_list|(
literal|0
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
name|ROLLBACK
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|checkResult
argument_list|(
name|NAME_NODE
argument_list|,
name|nameNodeDirs
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
name|log
argument_list|(
literal|"Normal DataNode rollback"
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
name|numDataNodes
argument_list|(
literal|0
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
name|ROLLBACK
argument_list|)
operator|.
name|build
argument_list|()
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
operator|.
name|startDataNodes
argument_list|(
name|conf
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|,
name|StartupOption
operator|.
name|ROLLBACK
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkResult
argument_list|(
name|DATA_NODE
argument_list|,
name|dataNodeDirs
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
literal|"Normal BlockPool rollback"
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
name|numDataNodes
argument_list|(
literal|0
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
name|ROLLBACK
argument_list|)
operator|.
name|build
argument_list|()
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
name|UpgradeUtilities
operator|.
name|getCurrentBlockPoolID
argument_list|(
name|cluster
argument_list|)
argument_list|)
expr_stmt|;
comment|// Create a previous snapshot for the blockpool
name|UpgradeUtilities
operator|.
name|createBlockPoolStorageDirs
argument_list|(
name|dataNodeDirs
argument_list|,
literal|"previous"
argument_list|,
name|UpgradeUtilities
operator|.
name|getCurrentBlockPoolID
argument_list|(
name|cluster
argument_list|)
argument_list|)
expr_stmt|;
comment|// Older LayoutVersion to make it rollback
name|storageInfo
operator|=
operator|new
name|StorageInfo
argument_list|(
name|UpgradeUtilities
operator|.
name|getCurrentLayoutVersion
argument_list|()
operator|+
literal|1
argument_list|,
name|UpgradeUtilities
operator|.
name|getCurrentNamespaceID
argument_list|(
name|cluster
argument_list|)
argument_list|,
name|UpgradeUtilities
operator|.
name|getCurrentClusterID
argument_list|(
name|cluster
argument_list|)
argument_list|,
name|UpgradeUtilities
operator|.
name|getCurrentFsscTime
argument_list|(
name|cluster
argument_list|)
argument_list|)
expr_stmt|;
comment|// Create old VERSION file for each data dir
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
name|Path
name|bpPrevPath
init|=
operator|new
name|Path
argument_list|(
name|dataNodeDirs
index|[
name|i
index|]
operator|+
literal|"/current/"
operator|+
name|UpgradeUtilities
operator|.
name|getCurrentBlockPoolID
argument_list|(
name|cluster
argument_list|)
argument_list|)
decl_stmt|;
name|UpgradeUtilities
operator|.
name|createBlockPoolVersionFile
argument_list|(
operator|new
name|File
argument_list|(
name|bpPrevPath
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|storageInfo
argument_list|,
name|UpgradeUtilities
operator|.
name|getCurrentBlockPoolID
argument_list|(
name|cluster
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|cluster
operator|.
name|startDataNodes
argument_list|(
name|conf
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|,
name|StartupOption
operator|.
name|ROLLBACK
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cluster
operator|.
name|isDataNodeUp
argument_list|()
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
literal|"NameNode rollback without existing previous dir"
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
name|startNameNodeShouldFail
argument_list|(
name|StartupOption
operator|.
name|ROLLBACK
argument_list|,
literal|"None of the storage directories contain previous fs state"
argument_list|)
expr_stmt|;
name|UpgradeUtilities
operator|.
name|createEmptyDirs
argument_list|(
name|nameNodeDirs
argument_list|)
expr_stmt|;
name|log
argument_list|(
literal|"DataNode rollback without existing previous dir"
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
name|numDataNodes
argument_list|(
literal|0
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
name|UPGRADE
argument_list|)
operator|.
name|build
argument_list|()
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
name|cluster
operator|.
name|startDataNodes
argument_list|(
name|conf
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|,
name|StartupOption
operator|.
name|ROLLBACK
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
literal|"DataNode rollback with future stored layout version in previous"
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
name|numDataNodes
argument_list|(
literal|0
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
name|ROLLBACK
argument_list|)
operator|.
name|build
argument_list|()
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
name|baseDirs
operator|=
name|UpgradeUtilities
operator|.
name|createDataNodeStorageDirs
argument_list|(
name|dataNodeDirs
argument_list|,
literal|"previous"
argument_list|)
expr_stmt|;
name|storageInfo
operator|=
operator|new
name|StorageInfo
argument_list|(
name|Integer
operator|.
name|MIN_VALUE
argument_list|,
name|UpgradeUtilities
operator|.
name|getCurrentNamespaceID
argument_list|(
name|cluster
argument_list|)
argument_list|,
name|UpgradeUtilities
operator|.
name|getCurrentClusterID
argument_list|(
name|cluster
argument_list|)
argument_list|,
name|UpgradeUtilities
operator|.
name|getCurrentFsscTime
argument_list|(
name|cluster
argument_list|)
argument_list|)
expr_stmt|;
name|UpgradeUtilities
operator|.
name|createDataNodeVersionFile
argument_list|(
name|baseDirs
argument_list|,
name|storageInfo
argument_list|,
name|UpgradeUtilities
operator|.
name|getCurrentBlockPoolID
argument_list|(
name|cluster
argument_list|)
argument_list|)
expr_stmt|;
name|startBlockPoolShouldFail
argument_list|(
name|StartupOption
operator|.
name|ROLLBACK
argument_list|,
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
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
literal|"DataNode rollback with newer fsscTime in previous"
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
name|numDataNodes
argument_list|(
literal|0
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
name|ROLLBACK
argument_list|)
operator|.
name|build
argument_list|()
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
name|baseDirs
operator|=
name|UpgradeUtilities
operator|.
name|createDataNodeStorageDirs
argument_list|(
name|dataNodeDirs
argument_list|,
literal|"previous"
argument_list|)
expr_stmt|;
name|storageInfo
operator|=
operator|new
name|StorageInfo
argument_list|(
name|UpgradeUtilities
operator|.
name|getCurrentLayoutVersion
argument_list|()
argument_list|,
name|UpgradeUtilities
operator|.
name|getCurrentNamespaceID
argument_list|(
name|cluster
argument_list|)
argument_list|,
name|UpgradeUtilities
operator|.
name|getCurrentClusterID
argument_list|(
name|cluster
argument_list|)
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|UpgradeUtilities
operator|.
name|createDataNodeVersionFile
argument_list|(
name|baseDirs
argument_list|,
name|storageInfo
argument_list|,
name|UpgradeUtilities
operator|.
name|getCurrentBlockPoolID
argument_list|(
name|cluster
argument_list|)
argument_list|)
expr_stmt|;
name|startBlockPoolShouldFail
argument_list|(
name|StartupOption
operator|.
name|ROLLBACK
argument_list|,
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
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
literal|"NameNode rollback with no edits file"
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
name|baseDirs
operator|=
name|UpgradeUtilities
operator|.
name|createNameNodeStorageDirs
argument_list|(
name|nameNodeDirs
argument_list|,
literal|"previous"
argument_list|)
expr_stmt|;
name|deleteMatchingFiles
argument_list|(
name|baseDirs
argument_list|,
literal|"edits.*"
argument_list|)
expr_stmt|;
name|startNameNodeShouldFail
argument_list|(
name|StartupOption
operator|.
name|ROLLBACK
argument_list|,
literal|"Gap in transactions"
argument_list|)
expr_stmt|;
name|UpgradeUtilities
operator|.
name|createEmptyDirs
argument_list|(
name|nameNodeDirs
argument_list|)
expr_stmt|;
name|log
argument_list|(
literal|"NameNode rollback with no image file"
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
name|baseDirs
operator|=
name|UpgradeUtilities
operator|.
name|createNameNodeStorageDirs
argument_list|(
name|nameNodeDirs
argument_list|,
literal|"previous"
argument_list|)
expr_stmt|;
name|deleteMatchingFiles
argument_list|(
name|baseDirs
argument_list|,
literal|"fsimage_.*"
argument_list|)
expr_stmt|;
name|startNameNodeShouldFail
argument_list|(
name|StartupOption
operator|.
name|ROLLBACK
argument_list|,
literal|"No valid image files found"
argument_list|)
expr_stmt|;
name|UpgradeUtilities
operator|.
name|createEmptyDirs
argument_list|(
name|nameNodeDirs
argument_list|)
expr_stmt|;
name|log
argument_list|(
literal|"NameNode rollback with corrupt version file"
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
name|baseDirs
operator|=
name|UpgradeUtilities
operator|.
name|createNameNodeStorageDirs
argument_list|(
name|nameNodeDirs
argument_list|,
literal|"previous"
argument_list|)
expr_stmt|;
for|for
control|(
name|File
name|f
range|:
name|baseDirs
control|)
block|{
name|UpgradeUtilities
operator|.
name|corruptFile
argument_list|(
operator|new
name|File
argument_list|(
name|f
argument_list|,
literal|"VERSION"
argument_list|)
argument_list|,
literal|"layoutVersion"
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|,
literal|"xxxxxxxxxxxxx"
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|startNameNodeShouldFail
argument_list|(
name|StartupOption
operator|.
name|ROLLBACK
argument_list|,
literal|"file VERSION has layoutVersion missing"
argument_list|)
expr_stmt|;
name|UpgradeUtilities
operator|.
name|createEmptyDirs
argument_list|(
name|nameNodeDirs
argument_list|)
expr_stmt|;
name|log
argument_list|(
literal|"NameNode rollback with old layout version in previous"
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
name|baseDirs
operator|=
name|UpgradeUtilities
operator|.
name|createNameNodeStorageDirs
argument_list|(
name|nameNodeDirs
argument_list|,
literal|"previous"
argument_list|)
expr_stmt|;
name|storageInfo
operator|=
operator|new
name|StorageInfo
argument_list|(
literal|1
argument_list|,
name|UpgradeUtilities
operator|.
name|getCurrentNamespaceID
argument_list|(
literal|null
argument_list|)
argument_list|,
name|UpgradeUtilities
operator|.
name|getCurrentClusterID
argument_list|(
literal|null
argument_list|)
argument_list|,
name|UpgradeUtilities
operator|.
name|getCurrentFsscTime
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|UpgradeUtilities
operator|.
name|createNameNodeVersionFile
argument_list|(
name|conf
argument_list|,
name|baseDirs
argument_list|,
name|storageInfo
argument_list|,
name|UpgradeUtilities
operator|.
name|getCurrentBlockPoolID
argument_list|(
name|cluster
argument_list|)
argument_list|)
expr_stmt|;
name|startNameNodeShouldFail
argument_list|(
name|StartupOption
operator|.
name|ROLLBACK
argument_list|,
literal|"Cannot rollback to storage version 1 using this version"
argument_list|)
expr_stmt|;
name|UpgradeUtilities
operator|.
name|createEmptyDirs
argument_list|(
name|nameNodeDirs
argument_list|)
expr_stmt|;
block|}
comment|// end numDir loop
block|}
DECL|method|deleteMatchingFiles (File[] baseDirs, String regex)
specifier|private
name|void
name|deleteMatchingFiles
parameter_list|(
name|File
index|[]
name|baseDirs
parameter_list|,
name|String
name|regex
parameter_list|)
block|{
for|for
control|(
name|File
name|baseDir
range|:
name|baseDirs
control|)
block|{
for|for
control|(
name|File
name|f
range|:
name|baseDir
operator|.
name|listFiles
argument_list|()
control|)
block|{
if|if
condition|(
name|f
operator|.
name|getName
argument_list|()
operator|.
name|matches
argument_list|(
name|regex
argument_list|)
condition|)
block|{
name|f
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
block|}
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
name|TestDFSRollback
argument_list|()
operator|.
name|testRollback
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

