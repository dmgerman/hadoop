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
name|assertTrue
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
name|BlockLocation
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
name|FSDataInputStream
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
name|client
operator|.
name|HdfsClientConfigKeys
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
name|InterDatanodeProtocol
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

begin_comment
comment|/**  * This class tests that pipelines survive data node death and recovery.  */
end_comment

begin_class
DECL|class|TestDatanodeDeath
specifier|public
class|class
name|TestDatanodeDeath
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
name|GenericTestUtils
operator|.
name|setLogLevel
parameter_list|(
name|InterDatanodeProtocol
operator|.
name|LOG
parameter_list|,
name|org
operator|.
name|slf4j
operator|.
name|event
operator|.
name|Level
operator|.
name|TRACE
parameter_list|)
constructor_decl|;
block|}
DECL|field|blockSize
specifier|static
specifier|final
name|int
name|blockSize
init|=
literal|8192
decl_stmt|;
DECL|field|numBlocks
specifier|static
specifier|final
name|int
name|numBlocks
init|=
literal|2
decl_stmt|;
DECL|field|fileSize
specifier|static
specifier|final
name|int
name|fileSize
init|=
name|numBlocks
operator|*
name|blockSize
operator|+
literal|1
decl_stmt|;
DECL|field|numDatanodes
specifier|static
specifier|final
name|int
name|numDatanodes
init|=
literal|15
decl_stmt|;
DECL|field|replication
specifier|static
specifier|final
name|short
name|replication
init|=
literal|3
decl_stmt|;
DECL|field|numberOfFiles
specifier|final
name|int
name|numberOfFiles
init|=
literal|3
decl_stmt|;
DECL|field|numThreads
specifier|final
name|int
name|numThreads
init|=
literal|5
decl_stmt|;
DECL|field|workload
name|Workload
index|[]
name|workload
init|=
literal|null
decl_stmt|;
comment|//
comment|// an object that does a bunch of transactions
comment|//
DECL|class|Workload
specifier|static
class|class
name|Workload
extends|extends
name|Thread
block|{
DECL|field|replication
specifier|private
specifier|final
name|short
name|replication
decl_stmt|;
DECL|field|numberOfFiles
specifier|private
specifier|final
name|int
name|numberOfFiles
decl_stmt|;
DECL|field|id
specifier|private
specifier|final
name|int
name|id
decl_stmt|;
DECL|field|fs
specifier|private
specifier|final
name|FileSystem
name|fs
decl_stmt|;
DECL|field|stamp
specifier|private
name|long
name|stamp
decl_stmt|;
DECL|field|myseed
specifier|private
specifier|final
name|long
name|myseed
decl_stmt|;
DECL|method|Workload (long myseed, FileSystem fs, int threadIndex, int numberOfFiles, short replication, long stamp)
name|Workload
parameter_list|(
name|long
name|myseed
parameter_list|,
name|FileSystem
name|fs
parameter_list|,
name|int
name|threadIndex
parameter_list|,
name|int
name|numberOfFiles
parameter_list|,
name|short
name|replication
parameter_list|,
name|long
name|stamp
parameter_list|)
block|{
name|this
operator|.
name|myseed
operator|=
name|myseed
expr_stmt|;
name|id
operator|=
name|threadIndex
expr_stmt|;
name|this
operator|.
name|fs
operator|=
name|fs
expr_stmt|;
name|this
operator|.
name|numberOfFiles
operator|=
name|numberOfFiles
expr_stmt|;
name|this
operator|.
name|replication
operator|=
name|replication
expr_stmt|;
name|this
operator|.
name|stamp
operator|=
name|stamp
expr_stmt|;
block|}
comment|// create a bunch of files. Write to them and then verify.
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Workload starting "
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
name|numberOfFiles
condition|;
name|i
operator|++
control|)
block|{
name|Path
name|filename
init|=
operator|new
name|Path
argument_list|(
name|id
operator|+
literal|"."
operator|+
name|i
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
literal|"Workload processing file "
operator|+
name|filename
argument_list|)
expr_stmt|;
name|FSDataOutputStream
name|stm
init|=
name|createFile
argument_list|(
name|fs
argument_list|,
name|filename
argument_list|,
name|replication
argument_list|)
decl_stmt|;
name|DFSOutputStream
name|dfstream
init|=
call|(
name|DFSOutputStream
call|)
argument_list|(
name|stm
operator|.
name|getWrappedStream
argument_list|()
argument_list|)
decl_stmt|;
name|dfstream
operator|.
name|setArtificialSlowdown
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|writeFile
argument_list|(
name|stm
argument_list|,
name|myseed
argument_list|)
expr_stmt|;
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
name|checkFile
argument_list|(
name|fs
argument_list|,
name|filename
argument_list|,
name|replication
argument_list|,
name|numBlocks
argument_list|,
name|fileSize
argument_list|,
name|myseed
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Workload exception "
operator|+
name|e
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// increment the stamp to indicate that another file is done.
synchronized|synchronized
init|(
name|this
init|)
block|{
name|stamp
operator|++
expr_stmt|;
block|}
block|}
block|}
DECL|method|resetStamp ()
specifier|public
specifier|synchronized
name|void
name|resetStamp
parameter_list|()
block|{
name|this
operator|.
name|stamp
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|getStamp ()
specifier|public
specifier|synchronized
name|long
name|getStamp
parameter_list|()
block|{
return|return
name|stamp
return|;
block|}
block|}
comment|//
comment|// creates a file and returns a descriptor for writing to it.
comment|//
DECL|method|createFile (FileSystem fileSys, Path name, short repl)
specifier|static
specifier|private
name|FSDataOutputStream
name|createFile
parameter_list|(
name|FileSystem
name|fileSys
parameter_list|,
name|Path
name|name
parameter_list|,
name|short
name|repl
parameter_list|)
throws|throws
name|IOException
block|{
comment|// create and write a file that contains three blocks of data
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
name|repl
argument_list|,
name|blockSize
argument_list|)
decl_stmt|;
return|return
name|stm
return|;
block|}
comment|//
comment|// writes to file
comment|//
DECL|method|writeFile (FSDataOutputStream stm, long seed)
specifier|static
specifier|private
name|void
name|writeFile
parameter_list|(
name|FSDataOutputStream
name|stm
parameter_list|,
name|long
name|seed
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|buffer
init|=
name|AppendTestUtil
operator|.
name|randomBytes
argument_list|(
name|seed
argument_list|,
name|fileSize
argument_list|)
decl_stmt|;
name|int
name|mid
init|=
name|fileSize
operator|/
literal|2
decl_stmt|;
name|stm
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|mid
argument_list|)
expr_stmt|;
name|stm
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
name|mid
argument_list|,
name|fileSize
operator|-
name|mid
argument_list|)
expr_stmt|;
block|}
comment|//
comment|// verify that the data written are sane
comment|//
DECL|method|checkFile (FileSystem fileSys, Path name, int repl, int numblocks, int filesize, long seed)
specifier|static
specifier|private
name|void
name|checkFile
parameter_list|(
name|FileSystem
name|fileSys
parameter_list|,
name|Path
name|name
parameter_list|,
name|int
name|repl
parameter_list|,
name|int
name|numblocks
parameter_list|,
name|int
name|filesize
parameter_list|,
name|long
name|seed
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|done
init|=
literal|false
decl_stmt|;
name|int
name|attempt
init|=
literal|0
decl_stmt|;
name|long
name|len
init|=
name|fileSys
operator|.
name|getFileStatus
argument_list|(
name|name
argument_list|)
operator|.
name|getLen
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|name
operator|+
literal|" should be of size "
operator|+
name|filesize
operator|+
literal|" but found to be of size "
operator|+
name|len
argument_list|,
name|len
operator|==
name|filesize
argument_list|)
expr_stmt|;
comment|// wait till all full blocks are confirmed by the datanodes.
while|while
condition|(
operator|!
name|done
condition|)
block|{
name|attempt
operator|++
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{}
name|done
operator|=
literal|true
expr_stmt|;
name|BlockLocation
index|[]
name|locations
init|=
name|fileSys
operator|.
name|getFileBlockLocations
argument_list|(
name|fileSys
operator|.
name|getFileStatus
argument_list|(
name|name
argument_list|)
argument_list|,
literal|0
argument_list|,
name|filesize
argument_list|)
decl_stmt|;
if|if
condition|(
name|locations
operator|.
name|length
operator|<
name|numblocks
condition|)
block|{
if|if
condition|(
name|attempt
operator|>
literal|100
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"File "
operator|+
name|name
operator|+
literal|" has only "
operator|+
name|locations
operator|.
name|length
operator|+
literal|" blocks, "
operator|+
literal|" but is expected to have "
operator|+
name|numblocks
operator|+
literal|" blocks."
argument_list|)
expr_stmt|;
block|}
name|done
operator|=
literal|false
expr_stmt|;
continue|continue;
block|}
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|locations
operator|.
name|length
condition|;
name|idx
operator|++
control|)
block|{
if|if
condition|(
name|locations
index|[
name|idx
index|]
operator|.
name|getHosts
argument_list|()
operator|.
name|length
operator|<
name|repl
condition|)
block|{
if|if
condition|(
name|attempt
operator|>
literal|100
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"File "
operator|+
name|name
operator|+
literal|" has "
operator|+
name|locations
operator|.
name|length
operator|+
literal|" blocks: "
operator|+
literal|" The "
operator|+
name|idx
operator|+
literal|" block has only "
operator|+
name|locations
index|[
name|idx
index|]
operator|.
name|getHosts
argument_list|()
operator|.
name|length
operator|+
literal|" replicas but is expected to have "
operator|+
name|repl
operator|+
literal|" replicas."
argument_list|)
expr_stmt|;
block|}
name|done
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
block|}
name|FSDataInputStream
name|stm
init|=
name|fileSys
operator|.
name|open
argument_list|(
name|name
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|expected
init|=
name|AppendTestUtil
operator|.
name|randomBytes
argument_list|(
name|seed
argument_list|,
name|fileSize
argument_list|)
decl_stmt|;
comment|// do a sanity check. Read the file
name|byte
index|[]
name|actual
init|=
operator|new
name|byte
index|[
name|filesize
index|]
decl_stmt|;
name|stm
operator|.
name|readFully
argument_list|(
literal|0
argument_list|,
name|actual
argument_list|)
expr_stmt|;
name|checkData
argument_list|(
name|actual
argument_list|,
literal|0
argument_list|,
name|expected
argument_list|,
literal|"Read 1"
argument_list|)
expr_stmt|;
block|}
DECL|method|checkData (byte[] actual, int from, byte[] expected, String message)
specifier|private
specifier|static
name|void
name|checkData
parameter_list|(
name|byte
index|[]
name|actual
parameter_list|,
name|int
name|from
parameter_list|,
name|byte
index|[]
name|expected
parameter_list|,
name|String
name|message
parameter_list|)
block|{
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|actual
operator|.
name|length
condition|;
name|idx
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|message
operator|+
literal|" byte "
operator|+
operator|(
name|from
operator|+
name|idx
operator|)
operator|+
literal|" differs. expected "
operator|+
name|expected
index|[
name|from
operator|+
name|idx
index|]
operator|+
literal|" actual "
operator|+
name|actual
index|[
name|idx
index|]
argument_list|,
name|actual
index|[
name|idx
index|]
argument_list|,
name|expected
index|[
name|from
operator|+
name|idx
index|]
argument_list|)
expr_stmt|;
name|actual
index|[
name|idx
index|]
operator|=
literal|0
expr_stmt|;
block|}
block|}
comment|/**    * A class that kills one datanode and recreates a new one. It waits to    * ensure that that all workers have finished at least one file since the     * last kill of a datanode. This guarantees that all three replicas of    * a block do not get killed (otherwise the file will be corrupt and the    * test will fail).    */
DECL|class|Modify
class|class
name|Modify
extends|extends
name|Thread
block|{
DECL|field|running
specifier|volatile
name|boolean
name|running
decl_stmt|;
DECL|field|cluster
specifier|final
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|conf
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|method|Modify (Configuration conf, MiniDFSCluster cluster)
name|Modify
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|MiniDFSCluster
name|cluster
parameter_list|)
block|{
name|running
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|cluster
operator|=
name|cluster
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
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
while|while
condition|(
name|running
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
continue|continue;
block|}
comment|// check if all threads have a new stamp.
comment|// If so, then all workers have finished at least one file
comment|// since the last stamp.
name|boolean
name|loop
init|=
literal|false
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
name|numThreads
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|workload
index|[
name|i
index|]
operator|.
name|getStamp
argument_list|()
operator|==
literal|0
condition|)
block|{
name|loop
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|loop
condition|)
block|{
continue|continue;
block|}
comment|// Now it is guaranteed that there will be at least one valid
comment|// replica of a file.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|replication
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
comment|// pick a random datanode to shutdown
name|int
name|victim
init|=
name|AppendTestUtil
operator|.
name|nextInt
argument_list|(
name|numDatanodes
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
literal|"Stopping datanode "
operator|+
name|victim
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartDataNode
argument_list|(
name|victim
argument_list|)
expr_stmt|;
comment|// cluster.startDataNodes(conf, 1, true, null, null);
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TestDatanodeDeath Modify exception "
operator|+
name|e
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"TestDatanodeDeath Modify exception "
operator|+
name|e
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|running
operator|=
literal|false
expr_stmt|;
block|}
block|}
comment|// set a new stamp for all workers
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numThreads
condition|;
name|i
operator|++
control|)
block|{
name|workload
index|[
name|i
index|]
operator|.
name|resetStamp
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|// Make the thread exit.
DECL|method|close ()
name|void
name|close
parameter_list|()
block|{
name|running
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Test that writing to files is good even when datanodes in the pipeline    * dies.    */
DECL|method|complexTest ()
specifier|private
name|void
name|complexTest
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY
argument_list|,
literal|2000
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_RECONSTRUCTION_PENDING_TIMEOUT_SEC_KEY
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|DFS_CLIENT_SOCKET_TIMEOUT_KEY
argument_list|,
literal|5000
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
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
name|numDatanodes
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
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
name|Modify
name|modThread
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// Create threads and make them run workload concurrently.
name|workload
operator|=
operator|new
name|Workload
index|[
name|numThreads
index|]
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
name|numThreads
condition|;
name|i
operator|++
control|)
block|{
name|workload
index|[
name|i
index|]
operator|=
operator|new
name|Workload
argument_list|(
name|AppendTestUtil
operator|.
name|nextLong
argument_list|()
argument_list|,
name|fs
argument_list|,
name|i
argument_list|,
name|numberOfFiles
argument_list|,
name|replication
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|workload
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|// Create a thread that kills existing datanodes and creates new ones.
name|modThread
operator|=
operator|new
name|Modify
argument_list|(
name|conf
argument_list|,
name|cluster
argument_list|)
expr_stmt|;
name|modThread
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// wait for all transactions to get over
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numThreads
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Waiting for thread "
operator|+
name|i
operator|+
literal|" to complete..."
argument_list|)
expr_stmt|;
name|workload
index|[
name|i
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
comment|// if most of the threads are done, then stop restarting datanodes.
if|if
condition|(
name|i
operator|>=
name|numThreads
operator|/
literal|2
condition|)
block|{
name|modThread
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|i
operator|--
expr_stmt|;
comment|// retry
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|modThread
operator|!=
literal|null
condition|)
block|{
name|modThread
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|modThread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{}
block|}
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Write to one file, then kill one datanode in the pipeline and then    * close the file.    */
DECL|method|simpleTest (int datanodeToKill)
specifier|private
name|void
name|simpleTest
parameter_list|(
name|int
name|datanodeToKill
parameter_list|)
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY
argument_list|,
literal|2000
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_RECONSTRUCTION_PENDING_TIMEOUT_SEC_KEY
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|DFS_CLIENT_SOCKET_TIMEOUT_KEY
argument_list|,
literal|5000
argument_list|)
expr_stmt|;
name|int
name|myMaxNodes
init|=
literal|5
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"SimpleTest starting with DataNode to Kill "
operator|+
name|datanodeToKill
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
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
name|myMaxNodes
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
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
name|short
name|repl
init|=
literal|3
decl_stmt|;
name|Path
name|filename
init|=
operator|new
name|Path
argument_list|(
literal|"simpletest.dat"
argument_list|)
decl_stmt|;
try|try
block|{
comment|// create a file and write one block of data
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"SimpleTest creating file "
operator|+
name|filename
argument_list|)
expr_stmt|;
name|FSDataOutputStream
name|stm
init|=
name|createFile
argument_list|(
name|fs
argument_list|,
name|filename
argument_list|,
name|repl
argument_list|)
decl_stmt|;
name|DFSOutputStream
name|dfstream
init|=
call|(
name|DFSOutputStream
call|)
argument_list|(
name|stm
operator|.
name|getWrappedStream
argument_list|()
argument_list|)
decl_stmt|;
comment|// these are test settings
name|dfstream
operator|.
name|setChunksPerPacket
argument_list|(
literal|5
argument_list|)
expr_stmt|;
specifier|final
name|long
name|myseed
init|=
name|AppendTestUtil
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
name|AppendTestUtil
operator|.
name|randomBytes
argument_list|(
name|myseed
argument_list|,
name|fileSize
argument_list|)
decl_stmt|;
name|int
name|mid
init|=
name|fileSize
operator|/
literal|4
decl_stmt|;
name|stm
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|mid
argument_list|)
expr_stmt|;
name|DatanodeInfo
index|[]
name|targets
init|=
name|dfstream
operator|.
name|getPipeline
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|5
decl_stmt|;
while|while
condition|(
name|count
operator|--
operator|>
literal|0
operator|&&
name|targets
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"SimpleTest: Waiting for pipeline to be created."
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{         }
name|targets
operator|=
name|dfstream
operator|.
name|getPipeline
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|targets
operator|==
literal|null
condition|)
block|{
name|int
name|victim
init|=
name|AppendTestUtil
operator|.
name|nextInt
argument_list|(
name|myMaxNodes
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"SimpleTest stopping datanode random "
operator|+
name|victim
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|stopDataNode
argument_list|(
name|victim
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|victim
init|=
name|datanodeToKill
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"SimpleTest stopping datanode "
operator|+
name|targets
index|[
name|victim
index|]
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|stopDataNode
argument_list|(
name|targets
index|[
name|victim
index|]
operator|.
name|getXferAddr
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"SimpleTest stopping datanode complete"
argument_list|)
expr_stmt|;
comment|// write some more data to file, close and verify
name|stm
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
name|mid
argument_list|,
name|fileSize
operator|-
name|mid
argument_list|)
expr_stmt|;
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
name|checkFile
argument_list|(
name|fs
argument_list|,
name|filename
argument_list|,
name|repl
argument_list|,
name|numBlocks
argument_list|,
name|fileSize
argument_list|,
name|myseed
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Simple Workload exception "
operator|+
name|e
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSimple0 ()
specifier|public
name|void
name|testSimple0
parameter_list|()
throws|throws
name|IOException
block|{
name|simpleTest
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSimple1 ()
specifier|public
name|void
name|testSimple1
parameter_list|()
throws|throws
name|IOException
block|{
name|simpleTest
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSimple2 ()
specifier|public
name|void
name|testSimple2
parameter_list|()
throws|throws
name|IOException
block|{
name|simpleTest
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testComplex ()
specifier|public
name|void
name|testComplex
parameter_list|()
throws|throws
name|IOException
block|{
name|complexTest
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

