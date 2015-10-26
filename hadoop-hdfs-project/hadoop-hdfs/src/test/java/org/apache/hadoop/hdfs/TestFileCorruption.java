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
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|Map
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
name|ChecksumException
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
name|FileSystem
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
name|protocol
operator|.
name|BlockListAsLongs
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
name|BlockListAsLongs
operator|.
name|BlockReportReplica
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
name|DatanodeInfo
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
name|ExtendedBlock
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
name|DataNode
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
name|DataNodeTestUtils
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
name|FSNamesystem
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
name|NameNode
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
name|DatanodeRegistration
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
name|PathUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_comment
comment|/**  * A JUnit test for corrupted file handling.  */
end_comment

begin_class
DECL|class|TestFileCorruption
specifier|public
class|class
name|TestFileCorruption
block|{
block|{
name|DFSTestUtil
operator|.
name|setNameNodeLogLevel
parameter_list|(
name|Level
operator|.
name|ALL
parameter_list|)
constructor_decl|;
name|GenericTestUtils
operator|.
name|setLogLevel
parameter_list|(
name|DataNode
operator|.
name|LOG
parameter_list|,
name|Level
operator|.
name|ALL
parameter_list|)
constructor_decl|;
name|GenericTestUtils
operator|.
name|setLogLevel
parameter_list|(
name|DFSClient
operator|.
name|LOG
parameter_list|,
name|Level
operator|.
name|ALL
parameter_list|)
constructor_decl|;
block|}
DECL|field|LOG
specifier|static
name|Logger
name|LOG
init|=
name|NameNode
operator|.
name|stateChangeLog
decl_stmt|;
comment|/** check if DFS can handle corrupted blocks properly */
annotation|@
name|Test
DECL|method|testFileCorruption ()
specifier|public
name|void
name|testFileCorruption
parameter_list|()
throws|throws
name|Exception
block|{
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
name|DFSTestUtil
name|util
init|=
operator|new
name|DFSTestUtil
operator|.
name|Builder
argument_list|()
operator|.
name|setName
argument_list|(
literal|"TestFileCorruption"
argument_list|)
operator|.
name|setNumFiles
argument_list|(
literal|20
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
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
literal|3
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|util
operator|.
name|createFiles
argument_list|(
name|fs
argument_list|,
literal|"/srcdat"
argument_list|)
expr_stmt|;
comment|// Now deliberately remove the blocks
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
name|DataNode
name|dn
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|DatanodeStorage
argument_list|,
name|BlockListAsLongs
argument_list|>
name|blockReports
init|=
name|dn
operator|.
name|getFSDataset
argument_list|()
operator|.
name|getBlockReports
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Blocks do not exist on data-dir"
argument_list|,
operator|!
name|blockReports
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|BlockListAsLongs
name|report
range|:
name|blockReports
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|BlockReportReplica
name|brr
range|:
name|report
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Deliberately removing block {}"
argument_list|,
name|brr
operator|.
name|getBlockName
argument_list|()
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getFsDatasetTestUtils
argument_list|(
literal|2
argument_list|)
operator|.
name|getMaterializedReplica
argument_list|(
operator|new
name|ExtendedBlock
argument_list|(
name|bpid
argument_list|,
name|brr
argument_list|)
argument_list|)
operator|.
name|deleteData
argument_list|()
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"Corrupted replicas not handled properly."
argument_list|,
name|util
operator|.
name|checkFiles
argument_list|(
name|fs
argument_list|,
literal|"/srcdat"
argument_list|)
argument_list|)
expr_stmt|;
name|util
operator|.
name|cleanup
argument_list|(
name|fs
argument_list|,
literal|"/srcdat"
argument_list|)
expr_stmt|;
block|}
finally|finally
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
block|}
comment|/** check if local FS can handle corrupted blocks properly */
annotation|@
name|Test
DECL|method|testLocalFileCorruption ()
specifier|public
name|void
name|testLocalFileCorruption
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|PathUtils
operator|.
name|getTestDirName
argument_list|(
name|getClass
argument_list|()
argument_list|)
argument_list|,
literal|"corruptFile"
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|DataOutputStream
name|dos
init|=
name|fs
operator|.
name|create
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|dos
operator|.
name|writeBytes
argument_list|(
literal|"original bytes"
argument_list|)
expr_stmt|;
name|dos
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Now deliberately corrupt the file
name|dos
operator|=
operator|new
name|DataOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|file
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|dos
operator|.
name|writeBytes
argument_list|(
literal|"corruption"
argument_list|)
expr_stmt|;
name|dos
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Now attempt to read the file
name|DataInputStream
name|dis
init|=
name|fs
operator|.
name|open
argument_list|(
name|file
argument_list|,
literal|512
argument_list|)
decl_stmt|;
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"A ChecksumException is expected to be logged."
argument_list|)
expr_stmt|;
name|dis
operator|.
name|readByte
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ChecksumException
name|ignore
parameter_list|)
block|{
comment|//expect this exception but let any NPE get thrown
block|}
name|fs
operator|.
name|delete
argument_list|(
name|file
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/** Test the case that a replica is reported corrupt while it is not    * in blocksMap. Make sure that ArrayIndexOutOfBounds does not thrown.    * See Hadoop-4351.    */
annotation|@
name|Test
DECL|method|testArrayOutOfBoundsException ()
specifier|public
name|void
name|testArrayOutOfBoundsException
parameter_list|()
throws|throws
name|Exception
block|{
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
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
literal|2
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|FILE_PATH
init|=
operator|new
name|Path
argument_list|(
literal|"/tmp.txt"
argument_list|)
decl_stmt|;
specifier|final
name|long
name|FILE_LEN
init|=
literal|1L
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|FILE_PATH
argument_list|,
name|FILE_LEN
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
comment|// get the block
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
name|ExtendedBlock
name|blk
init|=
name|getFirstBlock
argument_list|(
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|bpid
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"Data directory does not contain any blocks or there was an "
operator|+
literal|"IO error"
argument_list|,
name|blk
operator|==
literal|null
argument_list|)
expr_stmt|;
comment|// start a third datanode
name|cluster
operator|.
name|startDataNodes
argument_list|(
name|conf
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|DataNode
argument_list|>
name|datanodes
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|datanodes
operator|.
name|size
argument_list|()
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|DataNode
name|dataNode
init|=
name|datanodes
operator|.
name|get
argument_list|(
literal|2
argument_list|)
decl_stmt|;
comment|// report corrupted block by the third datanode
name|DatanodeRegistration
name|dnR
init|=
name|DataNodeTestUtils
operator|.
name|getDNRegistrationForBP
argument_list|(
name|dataNode
argument_list|,
name|blk
operator|.
name|getBlockPoolId
argument_list|()
argument_list|)
decl_stmt|;
name|FSNamesystem
name|ns
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
name|ns
operator|.
name|writeLock
argument_list|()
expr_stmt|;
try|try
block|{
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
operator|.
name|findAndMarkBlockAsCorrupt
argument_list|(
name|blk
argument_list|,
operator|new
name|DatanodeInfo
argument_list|(
name|dnR
argument_list|)
argument_list|,
literal|"TEST"
argument_list|,
literal|"STORAGE_ID"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|ns
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
comment|// open the file
name|fs
operator|.
name|open
argument_list|(
name|FILE_PATH
argument_list|)
expr_stmt|;
comment|//clean up
name|fs
operator|.
name|delete
argument_list|(
name|FILE_PATH
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
finally|finally
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
block|}
DECL|method|getFirstBlock (DataNode dn, String bpid)
specifier|private
specifier|static
name|ExtendedBlock
name|getFirstBlock
parameter_list|(
name|DataNode
name|dn
parameter_list|,
name|String
name|bpid
parameter_list|)
block|{
name|Map
argument_list|<
name|DatanodeStorage
argument_list|,
name|BlockListAsLongs
argument_list|>
name|blockReports
init|=
name|dn
operator|.
name|getFSDataset
argument_list|()
operator|.
name|getBlockReports
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
for|for
control|(
name|BlockListAsLongs
name|blockLongs
range|:
name|blockReports
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|BlockReportReplica
name|block
range|:
name|blockLongs
control|)
block|{
return|return
operator|new
name|ExtendedBlock
argument_list|(
name|bpid
argument_list|,
name|block
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

