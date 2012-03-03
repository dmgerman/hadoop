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
name|File
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|commons
operator|.
name|logging
operator|.
name|impl
operator|.
name|Log4JLogger
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
name|common
operator|.
name|GenerationStamp
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
name|log4j
operator|.
name|Level
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
extends|extends
name|TestCase
block|{
block|{
operator|(
operator|(
name|Log4JLogger
operator|)
name|NameNode
operator|.
name|stateChangeLog
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
operator|(
operator|(
name|Log4JLogger
operator|)
name|LogFactory
operator|.
name|getLog
argument_list|(
name|FSNamesystem
operator|.
name|class
argument_list|)
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
operator|(
operator|(
name|Log4JLogger
operator|)
name|DFSClient
operator|.
name|LOG
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
operator|(
operator|(
name|Log4JLogger
operator|)
name|DataNode
operator|.
name|LOG
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
block|}
DECL|field|LOG
specifier|static
name|Log
name|LOG
init|=
operator|(
operator|(
name|Log4JLogger
operator|)
name|NameNode
operator|.
name|stateChangeLog
operator|)
decl_stmt|;
comment|/** check if DFS can handle corrupted blocks properly */
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
argument_list|(
literal|"TestFileCorruption"
argument_list|,
literal|20
argument_list|,
literal|3
argument_list|,
literal|8
operator|*
literal|1024
argument_list|)
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
name|File
name|storageDir
init|=
name|cluster
operator|.
name|getInstanceStorageDir
argument_list|(
literal|2
argument_list|,
literal|0
argument_list|)
decl_stmt|;
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
name|File
name|data_dir
init|=
name|MiniDFSCluster
operator|.
name|getFinalizedDir
argument_list|(
name|storageDir
argument_list|,
name|bpid
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"data directory does not exist"
argument_list|,
name|data_dir
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|File
index|[]
name|blocks
init|=
name|data_dir
operator|.
name|listFiles
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Blocks do not exist in data-dir"
argument_list|,
operator|(
name|blocks
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|blocks
operator|.
name|length
operator|>
literal|0
operator|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|blocks
operator|.
name|length
condition|;
name|idx
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|blocks
index|[
name|idx
index|]
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"blk_"
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Deliberately removing file "
operator|+
name|blocks
index|[
name|idx
index|]
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Cannot remove file."
argument_list|,
name|blocks
index|[
name|idx
index|]
operator|.
name|delete
argument_list|()
argument_list|)
expr_stmt|;
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
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
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
name|System
operator|.
name|out
operator|.
name|println
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
name|File
name|storageDir
init|=
name|cluster
operator|.
name|getInstanceStorageDir
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|File
name|dataDir
init|=
name|MiniDFSCluster
operator|.
name|getFinalizedDir
argument_list|(
name|storageDir
argument_list|,
name|bpid
argument_list|)
decl_stmt|;
name|ExtendedBlock
name|blk
init|=
name|getBlock
argument_list|(
name|bpid
argument_list|,
name|dataDir
argument_list|)
decl_stmt|;
if|if
condition|(
name|blk
operator|==
literal|null
condition|)
block|{
name|storageDir
operator|=
name|cluster
operator|.
name|getInstanceStorageDir
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|dataDir
operator|=
name|MiniDFSCluster
operator|.
name|getFinalizedDir
argument_list|(
name|storageDir
argument_list|,
name|bpid
argument_list|)
expr_stmt|;
name|blk
operator|=
name|getBlock
argument_list|(
name|bpid
argument_list|,
name|dataDir
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
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
DECL|method|getBlock (String bpid, File dataDir)
specifier|private
name|ExtendedBlock
name|getBlock
parameter_list|(
name|String
name|bpid
parameter_list|,
name|File
name|dataDir
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"data directory does not exist"
argument_list|,
name|dataDir
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|File
index|[]
name|blocks
init|=
name|dataDir
operator|.
name|listFiles
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Blocks do not exist in dataDir"
argument_list|,
operator|(
name|blocks
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|blocks
operator|.
name|length
operator|>
literal|0
operator|)
argument_list|)
expr_stmt|;
name|int
name|idx
init|=
literal|0
decl_stmt|;
name|String
name|blockFileName
init|=
literal|null
decl_stmt|;
for|for
control|(
init|;
name|idx
operator|<
name|blocks
operator|.
name|length
condition|;
name|idx
operator|++
control|)
block|{
name|blockFileName
operator|=
name|blocks
index|[
name|idx
index|]
operator|.
name|getName
argument_list|()
expr_stmt|;
if|if
condition|(
name|blockFileName
operator|.
name|startsWith
argument_list|(
literal|"blk_"
argument_list|)
operator|&&
operator|!
name|blockFileName
operator|.
name|endsWith
argument_list|(
literal|".meta"
argument_list|)
condition|)
block|{
break|break;
block|}
block|}
if|if
condition|(
name|blockFileName
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|long
name|blockId
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|blockFileName
operator|.
name|substring
argument_list|(
literal|"blk_"
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|blockTimeStamp
init|=
name|GenerationStamp
operator|.
name|GRANDFATHER_GENERATION_STAMP
decl_stmt|;
for|for
control|(
name|idx
operator|=
literal|0
init|;
name|idx
operator|<
name|blocks
operator|.
name|length
condition|;
name|idx
operator|++
control|)
block|{
name|String
name|fileName
init|=
name|blocks
index|[
name|idx
index|]
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|fileName
operator|.
name|startsWith
argument_list|(
name|blockFileName
argument_list|)
operator|&&
name|fileName
operator|.
name|endsWith
argument_list|(
literal|".meta"
argument_list|)
condition|)
block|{
name|int
name|startIndex
init|=
name|blockFileName
operator|.
name|length
argument_list|()
operator|+
literal|1
decl_stmt|;
name|int
name|endIndex
init|=
name|fileName
operator|.
name|length
argument_list|()
operator|-
literal|".meta"
operator|.
name|length
argument_list|()
decl_stmt|;
name|blockTimeStamp
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|fileName
operator|.
name|substring
argument_list|(
name|startIndex
argument_list|,
name|endIndex
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
return|return
operator|new
name|ExtendedBlock
argument_list|(
name|bpid
argument_list|,
name|blockId
argument_list|,
name|blocks
index|[
name|idx
index|]
operator|.
name|length
argument_list|()
argument_list|,
name|blockTimeStamp
argument_list|)
return|;
block|}
block|}
end_class

end_unit

