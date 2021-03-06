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
name|Arrays
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
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|FileStatus
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
name|HdfsDataOutputStream
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
name|datatransfer
operator|.
name|DataTransferProtocol
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
name|datatransfer
operator|.
name|ReplaceDatanodeOnFailure
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
name|datatransfer
operator|.
name|ReplaceDatanodeOnFailure
operator|.
name|Policy
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
name|Assert
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
comment|/**  * This class tests that data nodes are correctly replaced on failure.  */
end_comment

begin_class
DECL|class|TestReplaceDatanodeOnFailure
specifier|public
class|class
name|TestReplaceDatanodeOnFailure
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestReplaceDatanodeOnFailure
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|DIR
specifier|static
specifier|final
name|String
name|DIR
init|=
literal|"/"
operator|+
name|TestReplaceDatanodeOnFailure
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"/"
decl_stmt|;
DECL|field|REPLICATION
specifier|static
specifier|final
name|short
name|REPLICATION
init|=
literal|3
decl_stmt|;
DECL|field|RACK0
specifier|final
specifier|private
specifier|static
name|String
name|RACK0
init|=
literal|"/rack0"
decl_stmt|;
DECL|field|RACK1
specifier|final
specifier|private
specifier|static
name|String
name|RACK1
init|=
literal|"/rack1"
decl_stmt|;
block|{
name|GenericTestUtils
operator|.
name|setLogLevel
parameter_list|(
name|DataTransferProtocol
operator|.
name|LOG
parameter_list|,
name|Level
operator|.
name|ALL
parameter_list|)
constructor_decl|;
block|}
comment|/** Test DEFAULT ReplaceDatanodeOnFailure policy. */
annotation|@
name|Test
DECL|method|testDefaultPolicy ()
specifier|public
name|void
name|testDefaultPolicy
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
specifier|final
name|ReplaceDatanodeOnFailure
name|p
init|=
name|ReplaceDatanodeOnFailure
operator|.
name|get
argument_list|(
name|conf
argument_list|)
decl_stmt|;
specifier|final
name|DatanodeInfo
index|[]
name|infos
init|=
operator|new
name|DatanodeInfo
index|[
literal|5
index|]
decl_stmt|;
specifier|final
name|DatanodeInfo
index|[]
index|[]
name|datanodes
init|=
operator|new
name|DatanodeInfo
index|[
name|infos
operator|.
name|length
operator|+
literal|1
index|]
index|[]
decl_stmt|;
name|datanodes
index|[
literal|0
index|]
operator|=
operator|new
name|DatanodeInfo
index|[
literal|0
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
name|infos
operator|.
name|length
condition|;
control|)
block|{
name|infos
index|[
name|i
index|]
operator|=
name|DFSTestUtil
operator|.
name|getLocalDatanodeInfo
argument_list|(
literal|9867
operator|+
name|i
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
name|datanodes
index|[
name|i
index|]
operator|=
operator|new
name|DatanodeInfo
index|[
name|i
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|infos
argument_list|,
literal|0
argument_list|,
name|datanodes
index|[
name|i
index|]
argument_list|,
literal|0
argument_list|,
name|datanodes
index|[
name|i
index|]
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
specifier|final
name|boolean
index|[]
name|isAppend
init|=
block|{
literal|true
block|,
literal|true
block|,
literal|false
block|,
literal|false
block|}
decl_stmt|;
specifier|final
name|boolean
index|[]
name|isHflushed
init|=
block|{
literal|true
block|,
literal|false
block|,
literal|true
block|,
literal|false
block|}
decl_stmt|;
for|for
control|(
name|short
name|replication
init|=
literal|1
init|;
name|replication
operator|<=
name|infos
operator|.
name|length
condition|;
name|replication
operator|++
control|)
block|{
for|for
control|(
name|int
name|nExistings
init|=
literal|0
init|;
name|nExistings
operator|<
name|datanodes
operator|.
name|length
condition|;
name|nExistings
operator|++
control|)
block|{
specifier|final
name|DatanodeInfo
index|[]
name|existings
init|=
name|datanodes
index|[
name|nExistings
index|]
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|nExistings
argument_list|,
name|existings
operator|.
name|length
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
name|isAppend
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|isHflushed
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|int
name|half
init|=
name|replication
operator|/
literal|2
decl_stmt|;
specifier|final
name|boolean
name|enoughReplica
init|=
name|replication
operator|<=
name|nExistings
decl_stmt|;
specifier|final
name|boolean
name|noReplica
init|=
name|nExistings
operator|==
literal|0
decl_stmt|;
specifier|final
name|boolean
name|replicationL3
init|=
name|replication
operator|<
literal|3
decl_stmt|;
specifier|final
name|boolean
name|existingsLEhalf
init|=
name|nExistings
operator|<=
name|half
decl_stmt|;
specifier|final
name|boolean
name|isAH
init|=
name|isAppend
index|[
name|i
index|]
operator|||
name|isHflushed
index|[
name|j
index|]
decl_stmt|;
specifier|final
name|boolean
name|expected
decl_stmt|;
if|if
condition|(
name|enoughReplica
operator|||
name|noReplica
operator|||
name|replicationL3
condition|)
block|{
name|expected
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|expected
operator|=
name|isAH
operator|||
name|existingsLEhalf
expr_stmt|;
block|}
specifier|final
name|boolean
name|computed
init|=
name|p
operator|.
name|satisfy
argument_list|(
name|replication
argument_list|,
name|existings
argument_list|,
name|isAppend
index|[
name|i
index|]
argument_list|,
name|isHflushed
index|[
name|j
index|]
argument_list|)
decl_stmt|;
try|try
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|computed
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|e
parameter_list|)
block|{
specifier|final
name|String
name|s
init|=
literal|"replication="
operator|+
name|replication
operator|+
literal|"\nnExistings ="
operator|+
name|nExistings
operator|+
literal|"\nisAppend   ="
operator|+
name|isAppend
index|[
name|i
index|]
operator|+
literal|"\nisHflushed ="
operator|+
name|isHflushed
index|[
name|j
index|]
decl_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|s
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
block|}
comment|/** Test replace datanode on failure. */
annotation|@
name|Test
DECL|method|testReplaceDatanodeOnFailure ()
specifier|public
name|void
name|testReplaceDatanodeOnFailure
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
comment|// do not consider load factor when selecting a data node
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REDUNDANCY_CONSIDERLOAD_KEY
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|//always replace a datanode
name|ReplaceDatanodeOnFailure
operator|.
name|write
argument_list|(
name|Policy
operator|.
name|ALWAYS
argument_list|,
literal|true
argument_list|,
name|conf
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|racks
init|=
operator|new
name|String
index|[
name|REPLICATION
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|racks
argument_list|,
name|RACK0
argument_list|)
expr_stmt|;
specifier|final
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
name|racks
argument_list|(
name|racks
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|REPLICATION
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
specifier|final
name|DistributedFileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
name|DIR
argument_list|)
decl_stmt|;
specifier|final
name|int
name|NUM_WRITERS
init|=
literal|10
decl_stmt|;
specifier|final
name|int
name|FIRST_BATCH
init|=
literal|5
decl_stmt|;
specifier|final
name|SlowWriter
index|[]
name|slowwriters
init|=
operator|new
name|SlowWriter
index|[
name|NUM_WRITERS
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|slowwriters
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|//create slow writers in different speed
name|slowwriters
index|[
name|i
operator|-
literal|1
index|]
operator|=
operator|new
name|SlowWriter
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"file"
operator|+
name|i
argument_list|)
argument_list|,
name|i
operator|*
literal|200L
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
name|FIRST_BATCH
condition|;
name|i
operator|++
control|)
block|{
name|slowwriters
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|// Let slow writers write something.
comment|// Some of them are too slow and will be not yet started.
name|sleepSeconds
argument_list|(
literal|3
argument_list|)
expr_stmt|;
comment|//start new datanodes
name|cluster
operator|.
name|startDataNodes
argument_list|(
name|conf
argument_list|,
literal|2
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
operator|new
name|String
index|[]
block|{
name|RACK1
block|,
name|RACK1
block|}
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
comment|// wait for first block reports for up to 10 seconds
name|cluster
operator|.
name|waitFirstBRCompleted
argument_list|(
literal|0
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
comment|//stop an old datanode
name|MiniDFSCluster
operator|.
name|DataNodeProperties
name|dnprop
init|=
name|cluster
operator|.
name|stopDataNode
argument_list|(
name|AppendTestUtil
operator|.
name|nextInt
argument_list|(
name|REPLICATION
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|FIRST_BATCH
init|;
name|i
operator|<
name|slowwriters
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|slowwriters
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|waitForBlockReplication
argument_list|(
name|slowwriters
argument_list|)
expr_stmt|;
comment|//check replication and interrupt.
for|for
control|(
name|SlowWriter
name|s
range|:
name|slowwriters
control|)
block|{
name|s
operator|.
name|checkReplication
argument_list|()
expr_stmt|;
name|s
operator|.
name|interruptRunning
argument_list|()
expr_stmt|;
block|}
comment|//close files
for|for
control|(
name|SlowWriter
name|s
range|:
name|slowwriters
control|)
block|{
name|s
operator|.
name|joinAndClose
argument_list|()
expr_stmt|;
block|}
comment|//Verify the file
name|LOG
operator|.
name|info
argument_list|(
literal|"Verify the file"
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
name|slowwriters
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|slowwriters
index|[
name|i
index|]
operator|.
name|filepath
operator|+
literal|": length="
operator|+
name|fs
operator|.
name|getFileStatus
argument_list|(
name|slowwriters
index|[
name|i
index|]
operator|.
name|filepath
argument_list|)
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|FSDataInputStream
name|in
init|=
literal|null
decl_stmt|;
try|try
block|{
name|in
operator|=
name|fs
operator|.
name|open
argument_list|(
name|slowwriters
index|[
name|i
index|]
operator|.
name|filepath
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|,
name|x
init|;
operator|(
name|x
operator|=
name|in
operator|.
name|read
argument_list|()
operator|)
operator|!=
operator|-
literal|1
condition|;
name|j
operator|++
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|j
argument_list|,
name|x
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
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
DECL|method|waitForBlockReplication (final SlowWriter[] slowwriters)
name|void
name|waitForBlockReplication
parameter_list|(
specifier|final
name|SlowWriter
index|[]
name|slowwriters
parameter_list|)
throws|throws
name|TimeoutException
throws|,
name|InterruptedException
block|{
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
try|try
block|{
for|for
control|(
name|SlowWriter
name|s
range|:
name|slowwriters
control|)
block|{
if|if
condition|(
name|s
operator|.
name|out
operator|.
name|getCurrentBlockReplication
argument_list|()
operator|<
name|REPLICATION
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"IOException is thrown while getting the file block "
operator|+
literal|"replication factor"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
argument_list|,
literal|1000
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
block|}
DECL|method|sleepSeconds (final int waittime)
specifier|static
name|void
name|sleepSeconds
parameter_list|(
specifier|final
name|int
name|waittime
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Wait "
operator|+
name|waittime
operator|+
literal|" seconds"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|waittime
operator|*
literal|1000L
argument_list|)
expr_stmt|;
block|}
DECL|class|SlowWriter
specifier|static
class|class
name|SlowWriter
extends|extends
name|Thread
block|{
DECL|field|filepath
specifier|final
name|Path
name|filepath
decl_stmt|;
DECL|field|out
specifier|final
name|HdfsDataOutputStream
name|out
decl_stmt|;
DECL|field|sleepms
specifier|final
name|long
name|sleepms
decl_stmt|;
DECL|field|running
specifier|private
specifier|volatile
name|boolean
name|running
init|=
literal|true
decl_stmt|;
DECL|method|SlowWriter (DistributedFileSystem fs, Path filepath, final long sleepms )
name|SlowWriter
parameter_list|(
name|DistributedFileSystem
name|fs
parameter_list|,
name|Path
name|filepath
parameter_list|,
specifier|final
name|long
name|sleepms
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|SlowWriter
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|":"
operator|+
name|filepath
argument_list|)
expr_stmt|;
name|this
operator|.
name|filepath
operator|=
name|filepath
expr_stmt|;
name|this
operator|.
name|out
operator|=
operator|(
name|HdfsDataOutputStream
operator|)
name|fs
operator|.
name|create
argument_list|(
name|filepath
argument_list|,
name|REPLICATION
argument_list|)
expr_stmt|;
name|this
operator|.
name|sleepms
operator|=
name|sleepms
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
name|int
name|i
init|=
literal|0
decl_stmt|;
try|try
block|{
name|sleep
argument_list|(
name|sleepms
argument_list|)
expr_stmt|;
for|for
control|(
init|;
name|running
condition|;
name|i
operator|++
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|getName
argument_list|()
operator|+
literal|" writes "
operator|+
name|i
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|out
operator|.
name|hflush
argument_list|()
expr_stmt|;
name|sleep
argument_list|(
name|sleepms
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|getName
argument_list|()
operator|+
literal|" interrupted:"
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|LOG
operator|.
name|info
argument_list|(
name|getName
argument_list|()
operator|+
literal|" terminated: i="
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|interruptRunning ()
name|void
name|interruptRunning
parameter_list|()
block|{
name|running
operator|=
literal|false
expr_stmt|;
name|interrupt
argument_list|()
expr_stmt|;
block|}
DECL|method|joinAndClose ()
name|void
name|joinAndClose
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|LOG
operator|.
name|info
argument_list|(
name|getName
argument_list|()
operator|+
literal|" join and close"
argument_list|)
expr_stmt|;
name|join
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|checkReplication ()
name|void
name|checkReplication
parameter_list|()
throws|throws
name|IOException
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|REPLICATION
argument_list|,
name|out
operator|.
name|getCurrentBlockReplication
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testAppend ()
specifier|public
name|void
name|testAppend
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
specifier|final
name|short
name|REPLICATION
init|=
operator|(
name|short
operator|)
literal|3
decl_stmt|;
specifier|final
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
literal|1
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|DistributedFileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|f
init|=
operator|new
name|Path
argument_list|(
name|DIR
argument_list|,
literal|"testAppend"
argument_list|)
decl_stmt|;
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"create an empty file "
operator|+
name|f
argument_list|)
expr_stmt|;
name|fs
operator|.
name|create
argument_list|(
name|f
argument_list|,
name|REPLICATION
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|REPLICATION
argument_list|,
name|status
operator|.
name|getReplication
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|status
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
literal|1000
index|]
decl_stmt|;
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"append "
operator|+
name|bytes
operator|.
name|length
operator|+
literal|" bytes to "
operator|+
name|f
argument_list|)
expr_stmt|;
specifier|final
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|append
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|REPLICATION
argument_list|,
name|status
operator|.
name|getReplication
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|bytes
operator|.
name|length
argument_list|,
name|status
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"append another "
operator|+
name|bytes
operator|.
name|length
operator|+
literal|" bytes to "
operator|+
name|f
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|append
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"This exception is expected"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
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
annotation|@
name|Test
DECL|method|testBestEffort ()
specifier|public
name|void
name|testBestEffort
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
comment|//always replace a datanode but do not throw exception
name|ReplaceDatanodeOnFailure
operator|.
name|write
argument_list|(
name|Policy
operator|.
name|ALWAYS
argument_list|,
literal|true
argument_list|,
name|conf
argument_list|)
expr_stmt|;
specifier|final
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
literal|1
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|DistributedFileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|f
init|=
operator|new
name|Path
argument_list|(
name|DIR
argument_list|,
literal|"testIgnoreReplaceFailure"
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
literal|1000
index|]
decl_stmt|;
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"write "
operator|+
name|bytes
operator|.
name|length
operator|+
literal|" bytes to "
operator|+
name|f
argument_list|)
expr_stmt|;
specifier|final
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|f
argument_list|,
name|REPLICATION
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|REPLICATION
argument_list|,
name|status
operator|.
name|getReplication
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|bytes
operator|.
name|length
argument_list|,
name|status
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"append another "
operator|+
name|bytes
operator|.
name|length
operator|+
literal|" bytes to "
operator|+
name|f
argument_list|)
expr_stmt|;
specifier|final
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|append
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
block|}
end_class

end_unit

