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
name|NNStorage
operator|.
name|getImageFileName
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
name|namenode
operator|.
name|NNStorage
operator|.
name|getInProgressEditsFileName
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
name|test
operator|.
name|GenericTestUtils
operator|.
name|assertExists
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
name|regex
operator|.
name|Pattern
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
name|protocol
operator|.
name|HdfsConstants
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
name|Storage
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
name|TestParallelImageWrite
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
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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
name|base
operator|.
name|Joiner
import|;
end_import

begin_comment
comment|/** * This test ensures the appropriate response (successful or failure) from * the system when the system is upgraded under various storage state and * version conditions. */
end_comment

begin_class
DECL|class|TestDFSUpgrade
specifier|public
class|class
name|TestDFSUpgrade
block|{
comment|// TODO: Avoid hard-coding expected_txid. The test should be more robust.
DECL|field|EXPECTED_TXID
specifier|private
specifier|static
specifier|final
name|int
name|EXPECTED_TXID
init|=
literal|61
decl_stmt|;
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
name|TestDFSUpgrade
operator|.
name|class
operator|.
name|getName
argument_list|()
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
comment|/**    * For namenode, Verify that the current and previous directories exist.    * Verify that previous hasn't been modified by comparing the checksum of all    * its files with their original checksum. It is assumed that the    * server has recovered and upgraded.    */
DECL|method|checkNameNode (String[] baseDirs, long imageTxId)
name|void
name|checkNameNode
parameter_list|(
name|String
index|[]
name|baseDirs
parameter_list|,
name|long
name|imageTxId
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|String
name|baseDir
range|:
name|baseDirs
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Checking namenode directory "
operator|+
name|baseDir
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"==== Contents ====:\n  "
operator|+
name|Joiner
operator|.
name|on
argument_list|(
literal|"  \n"
argument_list|)
operator|.
name|join
argument_list|(
operator|new
name|File
argument_list|(
name|baseDir
argument_list|,
literal|"current"
argument_list|)
operator|.
name|list
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"=================="
argument_list|)
expr_stmt|;
name|assertExists
argument_list|(
operator|new
name|File
argument_list|(
name|baseDir
argument_list|,
literal|"current"
argument_list|)
argument_list|)
expr_stmt|;
name|assertExists
argument_list|(
operator|new
name|File
argument_list|(
name|baseDir
argument_list|,
literal|"current/VERSION"
argument_list|)
argument_list|)
expr_stmt|;
name|assertExists
argument_list|(
operator|new
name|File
argument_list|(
name|baseDir
argument_list|,
literal|"current/"
operator|+
name|getInProgressEditsFileName
argument_list|(
name|imageTxId
operator|+
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertExists
argument_list|(
operator|new
name|File
argument_list|(
name|baseDir
argument_list|,
literal|"current/"
operator|+
name|getImageFileName
argument_list|(
name|imageTxId
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertExists
argument_list|(
operator|new
name|File
argument_list|(
name|baseDir
argument_list|,
literal|"current/seen_txid"
argument_list|)
argument_list|)
expr_stmt|;
name|File
name|previous
init|=
operator|new
name|File
argument_list|(
name|baseDir
argument_list|,
literal|"previous"
argument_list|)
decl_stmt|;
name|assertExists
argument_list|(
name|previous
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|UpgradeUtilities
operator|.
name|checksumContents
argument_list|(
name|NAME_NODE
argument_list|,
name|previous
argument_list|)
argument_list|,
name|UpgradeUtilities
operator|.
name|checksumMasterNameNodeContents
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * For datanode, for a block pool, verify that the current and previous    * directories exist. Verify that previous hasn't been modified by comparing    * the checksum of all its files with their original checksum. It    * is assumed that the server has recovered and upgraded.    */
DECL|method|checkDataNode (String[] baseDirs, String bpid)
name|void
name|checkDataNode
parameter_list|(
name|String
index|[]
name|baseDirs
parameter_list|,
name|String
name|bpid
parameter_list|)
throws|throws
name|IOException
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
name|baseDirs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|File
name|current
init|=
operator|new
name|File
argument_list|(
name|baseDirs
index|[
name|i
index|]
argument_list|,
literal|"current/"
operator|+
name|bpid
operator|+
literal|"/current"
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
name|current
argument_list|)
argument_list|,
name|UpgradeUtilities
operator|.
name|checksumMasterDataNodeContents
argument_list|()
argument_list|)
expr_stmt|;
comment|// block files are placed under<sd>/current/<bpid>/current/finalized
name|File
name|currentFinalized
init|=
name|MiniDFSCluster
operator|.
name|getFinalizedDir
argument_list|(
operator|new
name|File
argument_list|(
name|baseDirs
index|[
name|i
index|]
argument_list|)
argument_list|,
name|bpid
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
name|currentFinalized
argument_list|)
argument_list|,
name|UpgradeUtilities
operator|.
name|checksumMasterBlockPoolFinalizedContents
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|previous
init|=
operator|new
name|File
argument_list|(
name|baseDirs
index|[
name|i
index|]
argument_list|,
literal|"current/"
operator|+
name|bpid
operator|+
literal|"/previous"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|previous
operator|.
name|isDirectory
argument_list|()
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
name|previous
argument_list|)
argument_list|,
name|UpgradeUtilities
operator|.
name|checksumMasterDataNodeContents
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|previousFinalized
init|=
operator|new
name|File
argument_list|(
name|baseDirs
index|[
name|i
index|]
argument_list|,
literal|"current/"
operator|+
name|bpid
operator|+
literal|"/previous"
operator|+
literal|"/finalized"
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
name|previousFinalized
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
comment|/**    * Attempts to start a NameNode with the given operation.  Starting    * the NameNode should throw an exception.    */
DECL|method|startNameNodeShouldFail (StartupOption operation)
name|void
name|startNameNodeShouldFail
parameter_list|(
name|StartupOption
name|operation
parameter_list|)
block|{
name|startNameNodeShouldFail
argument_list|(
name|operation
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Attempts to start a NameNode with the given operation.  Starting    * the NameNode should throw an exception.    * @param operation - NameNode startup operation    * @param exceptionClass - if non-null, will check that the caught exception    *     is assignment-compatible with exceptionClass    * @param messagePattern - if non-null, will check that a substring of the     *     message from the caught exception matches this pattern, via the    *     {@link Matcher#find()} method.    */
DECL|method|startNameNodeShouldFail (StartupOption operation, Class<? extends Exception> exceptionClass, Pattern messagePattern)
name|void
name|startNameNodeShouldFail
parameter_list|(
name|StartupOption
name|operation
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Exception
argument_list|>
name|exceptionClass
parameter_list|,
name|Pattern
name|messagePattern
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
name|fail
argument_list|(
literal|"NameNode should have failed to start"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// expect exception
if|if
condition|(
name|exceptionClass
operator|!=
literal|null
condition|)
block|{
name|assertTrue
argument_list|(
literal|"Caught exception is not of expected class "
operator|+
name|exceptionClass
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|": "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|,
name|exceptionClass
operator|.
name|isInstance
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|messagePattern
operator|!=
literal|null
condition|)
block|{
name|assertTrue
argument_list|(
literal|"Caught exception message string does not match expected pattern \""
operator|+
name|messagePattern
operator|.
name|pattern
argument_list|()
operator|+
literal|"\" : "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|,
name|messagePattern
operator|.
name|matcher
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
operator|.
name|find
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Successfully detected expected NameNode startup failure."
argument_list|)
expr_stmt|;
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
comment|/**    * Create an instance of a newly configured cluster for testing that does    * not manage its own directories or files    */
DECL|method|createCluster ()
specifier|private
name|MiniDFSCluster
name|createCluster
parameter_list|()
throws|throws
name|IOException
block|{
return|return
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
return|;
block|}
annotation|@
name|BeforeClass
DECL|method|initialize ()
specifier|public
specifier|static
name|void
name|initialize
parameter_list|()
throws|throws
name|Exception
block|{
name|UpgradeUtilities
operator|.
name|initialize
argument_list|()
expr_stmt|;
block|}
comment|/**    * This test attempts to upgrade the NameNode and DataNode under    * a number of valid and invalid conditions.    */
annotation|@
name|Test
DECL|method|testUpgrade ()
specifier|public
name|void
name|testUpgrade
parameter_list|()
throws|throws
name|Exception
block|{
name|File
index|[]
name|baseDirs
decl_stmt|;
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
literal|"Normal NameNode upgrade"
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
name|createCluster
argument_list|()
expr_stmt|;
name|checkNameNode
argument_list|(
name|nameNodeDirs
argument_list|,
name|EXPECTED_TXID
argument_list|)
expr_stmt|;
if|if
condition|(
name|numDirs
operator|>
literal|1
condition|)
name|TestParallelImageWrite
operator|.
name|checkImages
argument_list|(
name|cluster
operator|.
name|getNamesystem
argument_list|()
argument_list|,
name|numDirs
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
literal|"Normal DataNode upgrade"
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
name|createCluster
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
name|REGULAR
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkDataNode
argument_list|(
name|dataNodeDirs
argument_list|,
name|UpgradeUtilities
operator|.
name|getCurrentBlockPoolID
argument_list|(
literal|null
argument_list|)
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
literal|"NameNode upgrade with existing previous dir"
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
name|startNameNodeShouldFail
argument_list|(
name|StartupOption
operator|.
name|UPGRADE
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
literal|"DataNode upgrade with existing previous dir"
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
name|createCluster
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
name|REGULAR
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkDataNode
argument_list|(
name|dataNodeDirs
argument_list|,
name|UpgradeUtilities
operator|.
name|getCurrentBlockPoolID
argument_list|(
literal|null
argument_list|)
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
literal|"DataNode upgrade with future stored layout version in current"
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
name|createCluster
argument_list|()
expr_stmt|;
name|baseDirs
operator|=
name|UpgradeUtilities
operator|.
name|createDataNodeStorageDirs
argument_list|(
name|dataNodeDirs
argument_list|,
literal|"current"
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
argument_list|,
name|NodeType
operator|.
name|DATA_NODE
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
name|REGULAR
argument_list|,
name|UpgradeUtilities
operator|.
name|getCurrentBlockPoolID
argument_list|(
literal|null
argument_list|)
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
literal|"DataNode upgrade with newer fsscTime in current"
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
name|createCluster
argument_list|()
expr_stmt|;
name|baseDirs
operator|=
name|UpgradeUtilities
operator|.
name|createDataNodeStorageDirs
argument_list|(
name|dataNodeDirs
argument_list|,
literal|"current"
argument_list|)
expr_stmt|;
name|storageInfo
operator|=
operator|new
name|StorageInfo
argument_list|(
name|HdfsConstants
operator|.
name|DATANODE_LAYOUT_VERSION
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
argument_list|,
name|NodeType
operator|.
name|DATA_NODE
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
comment|// Ensure corresponding block pool failed to initialized
name|startBlockPoolShouldFail
argument_list|(
name|StartupOption
operator|.
name|REGULAR
argument_list|,
name|UpgradeUtilities
operator|.
name|getCurrentBlockPoolID
argument_list|(
literal|null
argument_list|)
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
literal|"NameNode upgrade with no edits file"
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
name|deleteStorageFilesWithPrefix
argument_list|(
name|nameNodeDirs
argument_list|,
literal|"edits_"
argument_list|)
expr_stmt|;
name|startNameNodeShouldFail
argument_list|(
name|StartupOption
operator|.
name|UPGRADE
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
literal|"NameNode upgrade with no image file"
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
name|deleteStorageFilesWithPrefix
argument_list|(
name|nameNodeDirs
argument_list|,
literal|"fsimage_"
argument_list|)
expr_stmt|;
name|startNameNodeShouldFail
argument_list|(
name|StartupOption
operator|.
name|UPGRADE
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
literal|"NameNode upgrade with corrupt version file"
argument_list|,
name|numDirs
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
literal|"current"
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
name|UPGRADE
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
literal|"NameNode upgrade with old layout version in current"
argument_list|,
name|numDirs
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
literal|"current"
argument_list|)
expr_stmt|;
name|storageInfo
operator|=
operator|new
name|StorageInfo
argument_list|(
name|Storage
operator|.
name|LAST_UPGRADABLE_LAYOUT_VERSION
operator|+
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
argument_list|,
name|NodeType
operator|.
name|NAME_NODE
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
name|UPGRADE
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
literal|"NameNode upgrade with future layout version in current"
argument_list|,
name|numDirs
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
literal|"current"
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
argument_list|,
name|NodeType
operator|.
name|NAME_NODE
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
name|UPGRADE
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
comment|// One more check: normal NN upgrade with 4 directories, concurrent write
name|int
name|numDirs
init|=
literal|4
decl_stmt|;
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
name|log
argument_list|(
literal|"Normal NameNode upgrade"
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
name|createCluster
argument_list|()
expr_stmt|;
name|checkNameNode
argument_list|(
name|nameNodeDirs
argument_list|,
name|EXPECTED_TXID
argument_list|)
expr_stmt|;
name|TestParallelImageWrite
operator|.
name|checkImages
argument_list|(
name|cluster
operator|.
name|getNamesystem
argument_list|()
argument_list|,
name|numDirs
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
block|}
block|}
comment|/*    * Stand-alone test to detect failure of one SD during parallel upgrade.    * At this time, can only be done with manual hack of {@link FSImage.doUpgrade()}    */
annotation|@
name|Ignore
DECL|method|testUpgrade4 ()
specifier|public
name|void
name|testUpgrade4
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numDirs
init|=
literal|4
decl_stmt|;
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
name|log
argument_list|(
literal|"NameNode upgrade with one bad storage dir"
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
try|try
block|{
comment|// assert("storage dir has been prepared for failure before reaching this point");
name|startNameNodeShouldFail
argument_list|(
name|StartupOption
operator|.
name|UPGRADE
argument_list|,
name|IOException
operator|.
name|class
argument_list|,
name|Pattern
operator|.
name|compile
argument_list|(
literal|"failed in 1 storage"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// assert("storage dir shall be returned to normal state before exiting");
name|UpgradeUtilities
operator|.
name|createEmptyDirs
argument_list|(
name|nameNodeDirs
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|deleteStorageFilesWithPrefix (String[] nameNodeDirs, String prefix)
specifier|private
name|void
name|deleteStorageFilesWithPrefix
parameter_list|(
name|String
index|[]
name|nameNodeDirs
parameter_list|,
name|String
name|prefix
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|String
name|baseDirStr
range|:
name|nameNodeDirs
control|)
block|{
name|File
name|baseDir
init|=
operator|new
name|File
argument_list|(
name|baseDirStr
argument_list|)
decl_stmt|;
name|File
name|currentDir
init|=
operator|new
name|File
argument_list|(
name|baseDir
argument_list|,
literal|"current"
argument_list|)
decl_stmt|;
for|for
control|(
name|File
name|f
range|:
name|currentDir
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
name|startsWith
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
literal|"Deleting "
operator|+
name|f
argument_list|,
name|f
operator|.
name|delete
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IOException
operator|.
name|class
argument_list|)
DECL|method|testUpgradeFromPreUpgradeLVFails ()
specifier|public
name|void
name|testUpgradeFromPreUpgradeLVFails
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Upgrade from versions prior to Storage#LAST_UPGRADABLE_LAYOUT_VERSION
comment|// is not allowed
name|Storage
operator|.
name|checkVersionUpgradable
argument_list|(
name|Storage
operator|.
name|LAST_PRE_UPGRADE_LAYOUT_VERSION
operator|+
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected IOException is not thrown"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
DECL|method|test203LayoutVersion ()
specifier|public
name|void
name|test203LayoutVersion
parameter_list|()
block|{
for|for
control|(
name|int
name|lv
range|:
name|Storage
operator|.
name|LAYOUT_VERSIONS_203
control|)
block|{
name|assertTrue
argument_list|(
name|Storage
operator|.
name|is203LayoutVersion
argument_list|(
name|lv
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|TestDFSUpgrade
name|t
init|=
operator|new
name|TestDFSUpgrade
argument_list|()
decl_stmt|;
name|TestDFSUpgrade
operator|.
name|initialize
argument_list|()
expr_stmt|;
name|t
operator|.
name|testUpgrade
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

