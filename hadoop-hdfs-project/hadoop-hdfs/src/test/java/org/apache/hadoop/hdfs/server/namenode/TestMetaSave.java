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
name|BufferedReader
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
name|InputStreamReader
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
name|concurrent
operator|.
name|TimeoutException
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
name|Supplier
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
name|server
operator|.
name|blockmanagement
operator|.
name|BlockManagerTestUtil
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
name|protocol
operator|.
name|NamenodeProtocols
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
name|io
operator|.
name|IOUtils
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
name|DFSConfigKeys
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
name|DFSTestUtil
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
name|MiniDFSCluster
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
name|After
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

begin_comment
comment|/**  * This class tests the creation and validation of metasave  */
end_comment

begin_class
DECL|class|TestMetaSave
specifier|public
class|class
name|TestMetaSave
block|{
DECL|field|NUM_DATA_NODES
specifier|static
specifier|final
name|int
name|NUM_DATA_NODES
init|=
literal|2
decl_stmt|;
DECL|field|seed
specifier|static
specifier|final
name|long
name|seed
init|=
literal|0xDEADBEEFL
decl_stmt|;
DECL|field|blockSize
specifier|static
specifier|final
name|int
name|blockSize
init|=
literal|8192
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
DECL|field|fileSys
specifier|private
specifier|static
name|FileSystem
name|fileSys
init|=
literal|null
decl_stmt|;
DECL|field|nnRpc
specifier|private
specifier|static
name|NamenodeProtocols
name|nnRpc
init|=
literal|null
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
comment|// start a cluster
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
comment|// High value of replication interval
comment|// so that blocks remain less redundant
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REDUNDANCY_INTERVAL_SECONDS_KEY
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_STALE_DATANODE_INTERVAL_KEY
argument_list|,
literal|5L
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
name|NUM_DATA_NODES
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
name|fileSys
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|nnRpc
operator|=
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
expr_stmt|;
block|}
comment|/**    * Tests metasave    */
annotation|@
name|Test
DECL|method|testMetaSave ()
specifier|public
name|void
name|testMetaSave
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|TimeoutException
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
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
literal|"/filestatus"
operator|+
name|i
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fileSys
argument_list|,
name|file
argument_list|,
literal|1024
argument_list|,
literal|1024
argument_list|,
name|blockSize
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|,
name|seed
argument_list|)
expr_stmt|;
block|}
comment|// stop datanode and wait for namenode to discover that a datanode is dead
name|stopDatanodeAndWait
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|nnRpc
operator|.
name|setReplication
argument_list|(
literal|"/filestatus0"
argument_list|,
operator|(
name|short
operator|)
literal|4
argument_list|)
expr_stmt|;
name|nnRpc
operator|.
name|metaSave
argument_list|(
literal|"metasave.out.txt"
argument_list|)
expr_stmt|;
comment|// Verification
name|FileInputStream
name|fstream
init|=
operator|new
name|FileInputStream
argument_list|(
name|getLogFile
argument_list|(
literal|"metasave.out.txt"
argument_list|)
argument_list|)
decl_stmt|;
name|DataInputStream
name|in
init|=
operator|new
name|DataInputStream
argument_list|(
name|fstream
argument_list|)
decl_stmt|;
name|BufferedReader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|line
init|=
name|reader
operator|.
name|readLine
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"3 files and directories, 2 blocks = 5 total filesystem objects"
argument_list|,
name|line
argument_list|)
expr_stmt|;
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|line
operator|.
name|equals
argument_list|(
literal|"Live Datanodes: 1"
argument_list|)
argument_list|)
expr_stmt|;
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|line
operator|.
name|equals
argument_list|(
literal|"Dead Datanodes: 1"
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|line
operator|.
name|matches
argument_list|(
literal|"^/filestatus[01]:.*"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Tests metasave after delete, to make sure there are no orphaned blocks    */
annotation|@
name|Test
DECL|method|testMetasaveAfterDelete ()
specifier|public
name|void
name|testMetasaveAfterDelete
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|TimeoutException
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
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
literal|"/filestatus"
operator|+
name|i
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fileSys
argument_list|,
name|file
argument_list|,
literal|1024
argument_list|,
literal|1024
argument_list|,
name|blockSize
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|,
name|seed
argument_list|)
expr_stmt|;
block|}
comment|// stop datanode and wait for namenode to discover that a datanode is dead
name|stopDatanodeAndWait
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|nnRpc
operator|.
name|setReplication
argument_list|(
literal|"/filestatus0"
argument_list|,
operator|(
name|short
operator|)
literal|4
argument_list|)
expr_stmt|;
name|nnRpc
operator|.
name|delete
argument_list|(
literal|"/filestatus0"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|nnRpc
operator|.
name|delete
argument_list|(
literal|"/filestatus1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|nnRpc
operator|.
name|metaSave
argument_list|(
literal|"metasaveAfterDelete.out.txt"
argument_list|)
expr_stmt|;
comment|// Verification
name|BufferedReader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|FileInputStream
name|fstream
init|=
operator|new
name|FileInputStream
argument_list|(
name|getLogFile
argument_list|(
literal|"metasaveAfterDelete.out.txt"
argument_list|)
argument_list|)
decl_stmt|;
name|DataInputStream
name|in
init|=
operator|new
name|DataInputStream
argument_list|(
name|fstream
argument_list|)
decl_stmt|;
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
name|String
name|line
init|=
name|reader
operator|.
name|readLine
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|line
operator|.
name|equals
argument_list|(
literal|"Live Datanodes: 1"
argument_list|)
argument_list|)
expr_stmt|;
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|line
operator|.
name|equals
argument_list|(
literal|"Dead Datanodes: 1"
argument_list|)
argument_list|)
expr_stmt|;
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|line
operator|.
name|equals
argument_list|(
literal|"Metasave: Blocks waiting for reconstruction: 0"
argument_list|)
argument_list|)
expr_stmt|;
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|line
operator|.
name|equals
argument_list|(
literal|"Metasave: Blocks currently missing: 0"
argument_list|)
argument_list|)
expr_stmt|;
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|line
operator|.
name|equals
argument_list|(
literal|"Mis-replicated blocks that have been postponed:"
argument_list|)
argument_list|)
expr_stmt|;
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|line
operator|.
name|equals
argument_list|(
literal|"Metasave: Blocks being reconstructed: 0"
argument_list|)
argument_list|)
expr_stmt|;
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|line
operator|.
name|equals
argument_list|(
literal|"Metasave: Blocks 2 waiting deletion from 1 datanodes."
argument_list|)
argument_list|)
expr_stmt|;
comment|//skip 2 lines to reach HDFS-9033 scenario.
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|line
operator|.
name|contains
argument_list|(
literal|"blk"
argument_list|)
argument_list|)
expr_stmt|;
comment|// skip 1 line for Corrupt Blocks section.
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|line
operator|.
name|equals
argument_list|(
literal|"Metasave: Number of datanodes: 2"
argument_list|)
argument_list|)
expr_stmt|;
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|line
operator|.
name|contains
argument_list|(
literal|"NaN"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Tests that metasave overwrites the output file (not append).    */
annotation|@
name|Test
DECL|method|testMetaSaveOverwrite ()
specifier|public
name|void
name|testMetaSaveOverwrite
parameter_list|()
throws|throws
name|Exception
block|{
comment|// metaSave twice.
name|nnRpc
operator|.
name|metaSave
argument_list|(
literal|"metaSaveOverwrite.out.txt"
argument_list|)
expr_stmt|;
name|nnRpc
operator|.
name|metaSave
argument_list|(
literal|"metaSaveOverwrite.out.txt"
argument_list|)
expr_stmt|;
comment|// Read output file.
name|FileInputStream
name|fis
init|=
literal|null
decl_stmt|;
name|InputStreamReader
name|isr
init|=
literal|null
decl_stmt|;
name|BufferedReader
name|rdr
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fis
operator|=
operator|new
name|FileInputStream
argument_list|(
name|getLogFile
argument_list|(
literal|"metaSaveOverwrite.out.txt"
argument_list|)
argument_list|)
expr_stmt|;
name|isr
operator|=
operator|new
name|InputStreamReader
argument_list|(
name|fis
argument_list|)
expr_stmt|;
name|rdr
operator|=
operator|new
name|BufferedReader
argument_list|(
name|isr
argument_list|)
expr_stmt|;
comment|// Validate that file was overwritten (not appended) by checking for
comment|// presence of only one "Live Datanodes" line.
name|boolean
name|foundLiveDatanodesLine
init|=
literal|false
decl_stmt|;
name|String
name|line
init|=
name|rdr
operator|.
name|readLine
argument_list|()
decl_stmt|;
while|while
condition|(
name|line
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
literal|"Live Datanodes"
argument_list|)
condition|)
block|{
if|if
condition|(
name|foundLiveDatanodesLine
condition|)
block|{
name|fail
argument_list|(
literal|"multiple Live Datanodes lines, output file not overwritten"
argument_list|)
expr_stmt|;
block|}
name|foundLiveDatanodesLine
operator|=
literal|true
expr_stmt|;
block|}
name|line
operator|=
name|rdr
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|rdr
argument_list|,
name|isr
argument_list|,
name|fis
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|MetaSaveThread
class|class
name|MetaSaveThread
extends|extends
name|Thread
block|{
DECL|field|nnRpc
name|NamenodeProtocols
name|nnRpc
decl_stmt|;
DECL|field|filename
name|String
name|filename
decl_stmt|;
DECL|method|MetaSaveThread (NamenodeProtocols nnRpc, String filename)
specifier|public
name|MetaSaveThread
parameter_list|(
name|NamenodeProtocols
name|nnRpc
parameter_list|,
name|String
name|filename
parameter_list|)
block|{
name|this
operator|.
name|nnRpc
operator|=
name|nnRpc
expr_stmt|;
name|this
operator|.
name|filename
operator|=
name|filename
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|nnRpc
operator|.
name|metaSave
argument_list|(
name|filename
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{       }
block|}
block|}
comment|/**    * Tests that metasave concurrent output file (not append).    */
annotation|@
name|Test
DECL|method|testConcurrentMetaSave ()
specifier|public
name|void
name|testConcurrentMetaSave
parameter_list|()
throws|throws
name|Exception
block|{
name|ArrayList
argument_list|<
name|MetaSaveThread
argument_list|>
name|threads
init|=
operator|new
name|ArrayList
argument_list|<>
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|threads
operator|.
name|add
argument_list|(
operator|new
name|MetaSaveThread
argument_list|(
name|nnRpc
argument_list|,
literal|"metaSaveConcurrent.out.txt"
argument_list|)
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|threads
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|start
argument_list|()
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|threads
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
comment|// Read output file.
name|FileInputStream
name|fis
init|=
literal|null
decl_stmt|;
name|InputStreamReader
name|isr
init|=
literal|null
decl_stmt|;
name|BufferedReader
name|rdr
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fis
operator|=
operator|new
name|FileInputStream
argument_list|(
name|getLogFile
argument_list|(
literal|"metaSaveConcurrent.out.txt"
argument_list|)
argument_list|)
expr_stmt|;
name|isr
operator|=
operator|new
name|InputStreamReader
argument_list|(
name|fis
argument_list|)
expr_stmt|;
name|rdr
operator|=
operator|new
name|BufferedReader
argument_list|(
name|isr
argument_list|)
expr_stmt|;
comment|// Validate that file was overwritten (not appended) by checking for
comment|// presence of only one "Live Datanodes" line.
name|boolean
name|foundLiveDatanodesLine
init|=
literal|false
decl_stmt|;
name|String
name|line
init|=
name|rdr
operator|.
name|readLine
argument_list|()
decl_stmt|;
while|while
condition|(
name|line
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
literal|"Live Datanodes"
argument_list|)
condition|)
block|{
if|if
condition|(
name|foundLiveDatanodesLine
condition|)
block|{
name|fail
argument_list|(
literal|"multiple Live Datanodes lines, output file not overwritten"
argument_list|)
expr_stmt|;
block|}
name|foundLiveDatanodesLine
operator|=
literal|true
expr_stmt|;
block|}
name|line
operator|=
name|rdr
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|rdr
argument_list|,
name|isr
argument_list|,
name|fis
argument_list|)
expr_stmt|;
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
name|IOException
block|{
if|if
condition|(
name|fileSys
operator|!=
literal|null
condition|)
name|fileSys
operator|.
name|close
argument_list|()
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
comment|/**    * Returns a File for the given name inside the log directory.    *     * @param name String file name    * @return File for given name inside log directory    */
DECL|method|getLogFile (String name)
specifier|private
specifier|static
name|File
name|getLogFile
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"hadoop.log.dir"
argument_list|)
argument_list|,
name|name
argument_list|)
return|;
block|}
comment|/**    * Stop a DN, notify NN the death of DN and wait for NN to remove the DN.    *    * @param dnIdx Index of the Datanode in MiniDFSCluster    * @throws TimeoutException    * @throws InterruptedException    */
DECL|method|stopDatanodeAndWait (final int dnIdx)
specifier|private
name|void
name|stopDatanodeAndWait
parameter_list|(
specifier|final
name|int
name|dnIdx
parameter_list|)
throws|throws
name|TimeoutException
throws|,
name|InterruptedException
block|{
specifier|final
name|DataNode
name|dnToStop
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
name|dnIdx
argument_list|)
decl_stmt|;
name|cluster
operator|.
name|stopDataNode
argument_list|(
name|dnIdx
argument_list|)
expr_stmt|;
name|BlockManagerTestUtil
operator|.
name|noticeDeadDatanode
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|()
argument_list|,
name|dnToStop
operator|.
name|getDatanodeId
argument_list|()
operator|.
name|getXferAddr
argument_list|()
argument_list|)
expr_stmt|;
comment|// wait for namenode to discover that a datanode is dead
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
operator|new
name|Supplier
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|get
parameter_list|()
block|{
return|return
name|BlockManagerTestUtil
operator|.
name|isDatanodeRemoved
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|()
argument_list|,
name|dnToStop
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|,
literal|1000
argument_list|,
literal|30000
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

