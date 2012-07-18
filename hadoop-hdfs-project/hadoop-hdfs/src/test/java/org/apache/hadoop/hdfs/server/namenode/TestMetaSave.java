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
name|assertTrue
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
name|Random
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
name|CommonConfigurationKeys
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
name|FSDataOutputStream
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
name|junit
operator|.
name|AfterClass
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
DECL|method|createFile (FileSystem fileSys, Path name)
specifier|private
name|void
name|createFile
parameter_list|(
name|FileSystem
name|fileSys
parameter_list|,
name|Path
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|FSDataOutputStream
name|stm
init|=
name|fileSys
operator|.
name|create
argument_list|(
name|name
argument_list|,
literal|true
argument_list|,
name|fileSys
operator|.
name|getConf
argument_list|()
operator|.
name|getInt
argument_list|(
name|CommonConfigurationKeys
operator|.
name|IO_FILE_BUFFER_SIZE_KEY
argument_list|,
literal|4096
argument_list|)
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|,
name|blockSize
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
name|rand
operator|.
name|nextBytes
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|stm
operator|.
name|write
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|BeforeClass
DECL|method|setUp ()
specifier|public
specifier|static
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
comment|// so that blocks remain under-replicated
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REPLICATION_INTERVAL_KEY
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
block|{
specifier|final
name|FSNamesystem
name|namesystem
init|=
name|cluster
operator|.
name|getNamesystem
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
name|createFile
argument_list|(
name|fileSys
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
name|cluster
operator|.
name|stopDataNode
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// wait for namenode to discover that a datanode is dead
name|Thread
operator|.
name|sleep
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
name|namesystem
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
name|namesystem
operator|.
name|metaSave
argument_list|(
literal|"metasave.out.txt"
argument_list|)
expr_stmt|;
comment|// Verification
name|String
name|logFile
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"hadoop.log.dir"
argument_list|)
operator|+
literal|"/"
operator|+
literal|"metasave.out.txt"
decl_stmt|;
name|FileInputStream
name|fstream
init|=
operator|new
name|FileInputStream
argument_list|(
name|logFile
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
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|in
argument_list|)
argument_list|)
decl_stmt|;
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
literal|"3 files and directories, 2 blocks = 5 total"
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
annotation|@
name|AfterClass
DECL|method|tearDown ()
specifier|public
specifier|static
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
block|}
end_class

end_unit

