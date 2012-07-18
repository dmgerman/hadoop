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
name|Random
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
name|util
operator|.
name|Time
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
comment|/**  * Ensure during large directory delete, namenode does not block until the   * deletion completes and handles new requests from other clients  */
end_comment

begin_class
DECL|class|TestLargeDirectoryDelete
specifier|public
class|class
name|TestLargeDirectoryDelete
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
name|TestLargeDirectoryDelete
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|CONF
specifier|private
specifier|static
specifier|final
name|Configuration
name|CONF
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
DECL|field|TOTAL_BLOCKS
specifier|private
specifier|static
specifier|final
name|int
name|TOTAL_BLOCKS
init|=
literal|10000
decl_stmt|;
DECL|field|mc
specifier|private
name|MiniDFSCluster
name|mc
init|=
literal|null
decl_stmt|;
DECL|field|createOps
specifier|private
name|int
name|createOps
init|=
literal|0
decl_stmt|;
DECL|field|lockOps
specifier|private
name|int
name|lockOps
init|=
literal|0
decl_stmt|;
static|static
block|{
name|CONF
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|CONF
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BYTES_PER_CHECKSUM_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/** create a file with a length of<code>filelen</code> */
DECL|method|createFile (final String fileName, final long filelen)
specifier|private
name|void
name|createFile
parameter_list|(
specifier|final
name|String
name|fileName
parameter_list|,
specifier|final
name|long
name|filelen
parameter_list|)
throws|throws
name|IOException
block|{
name|FileSystem
name|fs
init|=
name|mc
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|filePath
argument_list|,
name|filelen
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/** Create a large number of directories and files */
DECL|method|createFiles ()
specifier|private
name|void
name|createFiles
parameter_list|()
throws|throws
name|IOException
block|{
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
comment|// Create files in a directory with random depth
comment|// ranging from 0-10.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|TOTAL_BLOCKS
condition|;
name|i
operator|+=
literal|100
control|)
block|{
name|String
name|filename
init|=
literal|"/root/"
decl_stmt|;
name|int
name|dirs
init|=
name|rand
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
decl_stmt|;
comment|// Depth of the directory
for|for
control|(
name|int
name|j
init|=
name|i
init|;
name|j
operator|>=
operator|(
name|i
operator|-
name|dirs
operator|)
condition|;
name|j
operator|--
control|)
block|{
name|filename
operator|+=
name|j
operator|+
literal|"/"
expr_stmt|;
block|}
name|filename
operator|+=
literal|"file"
operator|+
name|i
expr_stmt|;
name|createFile
argument_list|(
name|filename
argument_list|,
literal|100
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getBlockCount ()
specifier|private
name|int
name|getBlockCount
parameter_list|()
block|{
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"Null cluster"
argument_list|,
name|mc
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"No Namenode in cluster"
argument_list|,
name|mc
operator|.
name|getNameNode
argument_list|()
argument_list|)
expr_stmt|;
name|FSNamesystem
name|namesystem
init|=
name|mc
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"Null Namesystem in cluster"
argument_list|,
name|namesystem
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"Null Namesystem.blockmanager"
argument_list|,
name|namesystem
operator|.
name|getBlockManager
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|(
name|int
operator|)
name|namesystem
operator|.
name|getBlocksTotal
argument_list|()
return|;
block|}
comment|/** Run multiple threads doing simultaneous operations on the namenode    * while a large directory is being deleted.    */
DECL|method|runThreads ()
specifier|private
name|void
name|runThreads
parameter_list|()
throws|throws
name|Throwable
block|{
specifier|final
name|TestThread
name|threads
index|[]
init|=
operator|new
name|TestThread
index|[
literal|2
index|]
decl_stmt|;
comment|// Thread for creating files
name|threads
index|[
literal|0
index|]
operator|=
operator|new
name|TestThread
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|execute
parameter_list|()
throws|throws
name|Throwable
block|{
while|while
condition|(
name|live
condition|)
block|{
try|try
block|{
name|int
name|blockcount
init|=
name|getBlockCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|blockcount
argument_list|<
name|TOTAL_BLOCKS
operator|&&
name|blockcount
argument_list|>
literal|0
condition|)
block|{
name|String
name|file
init|=
literal|"/tmp"
operator|+
name|createOps
decl_stmt|;
name|createFile
argument_list|(
name|file
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|mc
operator|.
name|getFileSystem
argument_list|()
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|file
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|createOps
operator|++
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"createFile exception "
argument_list|,
name|ex
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
expr_stmt|;
comment|// Thread that periodically acquires the FSNamesystem lock
name|threads
index|[
literal|1
index|]
operator|=
operator|new
name|TestThread
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|execute
parameter_list|()
throws|throws
name|Throwable
block|{
while|while
condition|(
name|live
condition|)
block|{
try|try
block|{
name|int
name|blockcount
init|=
name|getBlockCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|blockcount
argument_list|<
name|TOTAL_BLOCKS
operator|&&
name|blockcount
argument_list|>
literal|0
condition|)
block|{
name|mc
operator|.
name|getNamesystem
argument_list|()
operator|.
name|writeLock
argument_list|()
expr_stmt|;
try|try
block|{
name|lockOps
operator|++
expr_stmt|;
block|}
finally|finally
block|{
name|mc
operator|.
name|getNamesystem
argument_list|()
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"lockOperation exception "
argument_list|,
name|ex
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
expr_stmt|;
name|threads
index|[
literal|0
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
name|threads
index|[
literal|1
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|long
name|start
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
name|FSNamesystem
operator|.
name|BLOCK_DELETION_INCREMENT
operator|=
literal|1
expr_stmt|;
name|mc
operator|.
name|getFileSystem
argument_list|()
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/root"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// recursive delete
specifier|final
name|long
name|end
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
name|threads
index|[
literal|0
index|]
operator|.
name|endThread
argument_list|()
expr_stmt|;
name|threads
index|[
literal|1
index|]
operator|.
name|endThread
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Deletion took "
operator|+
operator|(
name|end
operator|-
name|start
operator|)
operator|+
literal|"msecs"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"createOperations "
operator|+
name|createOps
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"lockOperations "
operator|+
name|lockOps
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|lockOps
operator|+
name|createOps
operator|>
literal|0
argument_list|)
expr_stmt|;
name|threads
index|[
literal|0
index|]
operator|.
name|rethrow
argument_list|()
expr_stmt|;
name|threads
index|[
literal|1
index|]
operator|.
name|rethrow
argument_list|()
expr_stmt|;
block|}
comment|/**    * An abstract class for tests that catches exceptions and can     * rethrow them on a different thread, and has an {@link #endThread()}     * operation that flips a volatile boolean before interrupting the thread.    * Also: after running the implementation of {@link #execute()} in the     * implementation class, the thread is notified: other threads can wait    * for it to terminate    */
DECL|class|TestThread
specifier|private
specifier|abstract
class|class
name|TestThread
extends|extends
name|Thread
block|{
DECL|field|thrown
specifier|volatile
name|Throwable
name|thrown
decl_stmt|;
DECL|field|live
specifier|protected
specifier|volatile
name|boolean
name|live
init|=
literal|true
decl_stmt|;
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
name|execute
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|throwable
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|throwable
argument_list|)
expr_stmt|;
name|setThrown
argument_list|(
name|throwable
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|this
operator|.
name|notify
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|execute ()
specifier|protected
specifier|abstract
name|void
name|execute
parameter_list|()
throws|throws
name|Throwable
function_decl|;
DECL|method|setThrown (Throwable thrown)
specifier|protected
specifier|synchronized
name|void
name|setThrown
parameter_list|(
name|Throwable
name|thrown
parameter_list|)
block|{
name|this
operator|.
name|thrown
operator|=
name|thrown
expr_stmt|;
block|}
comment|/**      * Rethrow anything caught      * @throws Throwable any non-null throwable raised by the execute method.      */
DECL|method|rethrow ()
specifier|public
specifier|synchronized
name|void
name|rethrow
parameter_list|()
throws|throws
name|Throwable
block|{
if|if
condition|(
name|thrown
operator|!=
literal|null
condition|)
block|{
throw|throw
name|thrown
throw|;
block|}
block|}
comment|/**      * End the thread by setting the live p      */
DECL|method|endThread ()
specifier|public
specifier|synchronized
name|void
name|endThread
parameter_list|()
block|{
name|live
operator|=
literal|false
expr_stmt|;
name|interrupt
argument_list|()
expr_stmt|;
try|try
block|{
name|wait
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Ignoring "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|largeDelete ()
specifier|public
name|void
name|largeDelete
parameter_list|()
throws|throws
name|Throwable
block|{
name|mc
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|CONF
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
try|try
block|{
name|mc
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"No Namenode in cluster"
argument_list|,
name|mc
operator|.
name|getNameNode
argument_list|()
argument_list|)
expr_stmt|;
name|createFiles
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TOTAL_BLOCKS
argument_list|,
name|getBlockCount
argument_list|()
argument_list|)
expr_stmt|;
name|runThreads
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|mc
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

