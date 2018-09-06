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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
name|impl
operator|.
name|BlockReaderTestUtil
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
name|util
operator|.
name|Time
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
name|apache
operator|.
name|log4j
operator|.
name|LogManager
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

begin_comment
comment|/**  * Driver class for testing the use of DFSInputStream by multiple concurrent  * readers, using the different read APIs.  *  * This class is marked as @Ignore so that junit doesn't try to execute the  * tests in here directly.  They are executed from subclasses.  */
end_comment

begin_class
annotation|@
name|Ignore
DECL|class|TestParallelReadUtil
specifier|public
class|class
name|TestParallelReadUtil
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
name|TestParallelReadUtil
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|util
specifier|static
name|BlockReaderTestUtil
name|util
init|=
literal|null
decl_stmt|;
DECL|field|dfsClient
specifier|static
name|DFSClient
name|dfsClient
init|=
literal|null
decl_stmt|;
DECL|field|FILE_SIZE_K
specifier|static
specifier|final
name|int
name|FILE_SIZE_K
init|=
literal|256
decl_stmt|;
DECL|field|rand
specifier|static
name|Random
name|rand
init|=
literal|null
decl_stmt|;
DECL|field|DEFAULT_REPLICATION_FACTOR
specifier|static
specifier|final
name|int
name|DEFAULT_REPLICATION_FACTOR
init|=
literal|2
decl_stmt|;
DECL|field|verifyChecksums
specifier|protected
name|boolean
name|verifyChecksums
init|=
literal|true
decl_stmt|;
static|static
block|{
comment|// The client-trace log ends up causing a lot of blocking threads
comment|// in this when it's being used as a performance benchmark.
name|LogManager
operator|.
name|getLogger
argument_list|(
name|DataNode
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".clienttrace"
argument_list|)
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|WARN
argument_list|)
expr_stmt|;
block|}
DECL|class|TestFileInfo
specifier|private
class|class
name|TestFileInfo
block|{
DECL|field|dis
specifier|public
name|DFSInputStream
name|dis
decl_stmt|;
DECL|field|filepath
specifier|public
name|Path
name|filepath
decl_stmt|;
DECL|field|authenticData
specifier|public
name|byte
index|[]
name|authenticData
decl_stmt|;
block|}
DECL|method|setupCluster (int replicationFactor, HdfsConfiguration conf)
specifier|public
specifier|static
name|void
name|setupCluster
parameter_list|(
name|int
name|replicationFactor
parameter_list|,
name|HdfsConfiguration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|util
operator|=
operator|new
name|BlockReaderTestUtil
argument_list|(
name|replicationFactor
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|dfsClient
operator|=
name|util
operator|.
name|getDFSClient
argument_list|()
expr_stmt|;
name|long
name|seed
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Random seed: "
operator|+
name|seed
argument_list|)
expr_stmt|;
name|rand
operator|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
expr_stmt|;
block|}
comment|/**    * Providers of this interface implement two different read APIs. Instances of    * this interface are shared across all ReadWorkerThreads, so should be stateless.    */
DECL|interface|ReadWorkerHelper
specifier|static
interface|interface
name|ReadWorkerHelper
block|{
DECL|method|read (DFSInputStream dis, byte[] target, int startOff, int len)
specifier|public
name|int
name|read
parameter_list|(
name|DFSInputStream
name|dis
parameter_list|,
name|byte
index|[]
name|target
parameter_list|,
name|int
name|startOff
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|pRead (DFSInputStream dis, byte[] target, int startOff, int len)
specifier|public
name|int
name|pRead
parameter_list|(
name|DFSInputStream
name|dis
parameter_list|,
name|byte
index|[]
name|target
parameter_list|,
name|int
name|startOff
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
comment|/**    * Uses read(ByteBuffer...) style APIs    */
DECL|class|DirectReadWorkerHelper
specifier|static
class|class
name|DirectReadWorkerHelper
implements|implements
name|ReadWorkerHelper
block|{
annotation|@
name|Override
DECL|method|read (DFSInputStream dis, byte[] target, int startOff, int len)
specifier|public
name|int
name|read
parameter_list|(
name|DFSInputStream
name|dis
parameter_list|,
name|byte
index|[]
name|target
parameter_list|,
name|int
name|startOff
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteBuffer
name|bb
init|=
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|target
operator|.
name|length
argument_list|)
decl_stmt|;
name|int
name|cnt
init|=
literal|0
decl_stmt|;
synchronized|synchronized
init|(
name|dis
init|)
block|{
name|dis
operator|.
name|seek
argument_list|(
name|startOff
argument_list|)
expr_stmt|;
while|while
condition|(
name|cnt
operator|<
name|len
condition|)
block|{
name|int
name|read
init|=
name|dis
operator|.
name|read
argument_list|(
name|bb
argument_list|)
decl_stmt|;
if|if
condition|(
name|read
operator|==
operator|-
literal|1
condition|)
block|{
return|return
name|read
return|;
block|}
name|cnt
operator|+=
name|read
expr_stmt|;
block|}
block|}
name|bb
operator|.
name|clear
argument_list|()
expr_stmt|;
name|bb
operator|.
name|get
argument_list|(
name|target
argument_list|)
expr_stmt|;
return|return
name|cnt
return|;
block|}
annotation|@
name|Override
DECL|method|pRead (DFSInputStream dis, byte[] target, int startOff, int len)
specifier|public
name|int
name|pRead
parameter_list|(
name|DFSInputStream
name|dis
parameter_list|,
name|byte
index|[]
name|target
parameter_list|,
name|int
name|startOff
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
comment|// No pRead for bb read path
return|return
name|read
argument_list|(
name|dis
argument_list|,
name|target
argument_list|,
name|startOff
argument_list|,
name|len
argument_list|)
return|;
block|}
block|}
comment|/**    * Uses the read(byte[]...) style APIs    */
DECL|class|CopyingReadWorkerHelper
specifier|static
class|class
name|CopyingReadWorkerHelper
implements|implements
name|ReadWorkerHelper
block|{
annotation|@
name|Override
DECL|method|read (DFSInputStream dis, byte[] target, int startOff, int len)
specifier|public
name|int
name|read
parameter_list|(
name|DFSInputStream
name|dis
parameter_list|,
name|byte
index|[]
name|target
parameter_list|,
name|int
name|startOff
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|cnt
init|=
literal|0
decl_stmt|;
synchronized|synchronized
init|(
name|dis
init|)
block|{
name|dis
operator|.
name|seek
argument_list|(
name|startOff
argument_list|)
expr_stmt|;
while|while
condition|(
name|cnt
operator|<
name|len
condition|)
block|{
name|int
name|read
init|=
name|dis
operator|.
name|read
argument_list|(
name|target
argument_list|,
name|cnt
argument_list|,
name|len
operator|-
name|cnt
argument_list|)
decl_stmt|;
if|if
condition|(
name|read
operator|==
operator|-
literal|1
condition|)
block|{
return|return
name|read
return|;
block|}
name|cnt
operator|+=
name|read
expr_stmt|;
block|}
block|}
return|return
name|cnt
return|;
block|}
annotation|@
name|Override
DECL|method|pRead (DFSInputStream dis, byte[] target, int startOff, int len)
specifier|public
name|int
name|pRead
parameter_list|(
name|DFSInputStream
name|dis
parameter_list|,
name|byte
index|[]
name|target
parameter_list|,
name|int
name|startOff
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|cnt
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|cnt
operator|<
name|len
condition|)
block|{
name|int
name|read
init|=
name|dis
operator|.
name|read
argument_list|(
name|startOff
argument_list|,
name|target
argument_list|,
name|cnt
argument_list|,
name|len
operator|-
name|cnt
argument_list|)
decl_stmt|;
if|if
condition|(
name|read
operator|==
operator|-
literal|1
condition|)
block|{
return|return
name|read
return|;
block|}
name|cnt
operator|+=
name|read
expr_stmt|;
block|}
return|return
name|cnt
return|;
block|}
block|}
comment|/**    * Uses a mix of both copying    */
DECL|class|MixedWorkloadHelper
specifier|static
class|class
name|MixedWorkloadHelper
implements|implements
name|ReadWorkerHelper
block|{
DECL|field|bb
specifier|private
specifier|final
name|DirectReadWorkerHelper
name|bb
init|=
operator|new
name|DirectReadWorkerHelper
argument_list|()
decl_stmt|;
DECL|field|copy
specifier|private
specifier|final
name|CopyingReadWorkerHelper
name|copy
init|=
operator|new
name|CopyingReadWorkerHelper
argument_list|()
decl_stmt|;
DECL|field|COPYING_PROBABILITY
specifier|private
specifier|final
name|double
name|COPYING_PROBABILITY
init|=
literal|0.5
decl_stmt|;
annotation|@
name|Override
DECL|method|read (DFSInputStream dis, byte[] target, int startOff, int len)
specifier|public
name|int
name|read
parameter_list|(
name|DFSInputStream
name|dis
parameter_list|,
name|byte
index|[]
name|target
parameter_list|,
name|int
name|startOff
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|double
name|p
init|=
name|rand
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|>
name|COPYING_PROBABILITY
condition|)
block|{
return|return
name|bb
operator|.
name|read
argument_list|(
name|dis
argument_list|,
name|target
argument_list|,
name|startOff
argument_list|,
name|len
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|copy
operator|.
name|read
argument_list|(
name|dis
argument_list|,
name|target
argument_list|,
name|startOff
argument_list|,
name|len
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|pRead (DFSInputStream dis, byte[] target, int startOff, int len)
specifier|public
name|int
name|pRead
parameter_list|(
name|DFSInputStream
name|dis
parameter_list|,
name|byte
index|[]
name|target
parameter_list|,
name|int
name|startOff
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|double
name|p
init|=
name|rand
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|>
name|COPYING_PROBABILITY
condition|)
block|{
return|return
name|bb
operator|.
name|pRead
argument_list|(
name|dis
argument_list|,
name|target
argument_list|,
name|startOff
argument_list|,
name|len
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|copy
operator|.
name|pRead
argument_list|(
name|dis
argument_list|,
name|target
argument_list|,
name|startOff
argument_list|,
name|len
argument_list|)
return|;
block|}
block|}
block|}
comment|/**    * A worker to do one "unit" of read.    */
DECL|class|ReadWorker
specifier|static
class|class
name|ReadWorker
extends|extends
name|Thread
block|{
DECL|field|N_ITERATIONS
specifier|static
specifier|public
specifier|final
name|int
name|N_ITERATIONS
init|=
literal|1024
decl_stmt|;
DECL|field|PROPORTION_NON_POSITIONAL_READ
specifier|private
specifier|static
specifier|final
name|double
name|PROPORTION_NON_POSITIONAL_READ
init|=
literal|0.10
decl_stmt|;
DECL|field|testInfo
specifier|private
specifier|final
name|TestFileInfo
name|testInfo
decl_stmt|;
DECL|field|fileSize
specifier|private
specifier|final
name|long
name|fileSize
decl_stmt|;
DECL|field|bytesRead
specifier|private
name|long
name|bytesRead
decl_stmt|;
DECL|field|error
specifier|private
name|boolean
name|error
decl_stmt|;
DECL|field|helper
specifier|private
specifier|final
name|ReadWorkerHelper
name|helper
decl_stmt|;
DECL|method|ReadWorker (TestFileInfo testInfo, int id, ReadWorkerHelper helper)
name|ReadWorker
parameter_list|(
name|TestFileInfo
name|testInfo
parameter_list|,
name|int
name|id
parameter_list|,
name|ReadWorkerHelper
name|helper
parameter_list|)
block|{
name|super
argument_list|(
literal|"ReadWorker-"
operator|+
name|id
operator|+
literal|"-"
operator|+
name|testInfo
operator|.
name|filepath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|testInfo
operator|=
name|testInfo
expr_stmt|;
name|this
operator|.
name|helper
operator|=
name|helper
expr_stmt|;
name|fileSize
operator|=
name|testInfo
operator|.
name|dis
operator|.
name|getFileLength
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|fileSize
argument_list|,
name|testInfo
operator|.
name|authenticData
operator|.
name|length
argument_list|)
expr_stmt|;
name|bytesRead
operator|=
literal|0
expr_stmt|;
name|error
operator|=
literal|false
expr_stmt|;
block|}
comment|/**      * Randomly do one of (1) Small read; and (2) Large Pread.      */
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
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
name|N_ITERATIONS
condition|;
operator|++
name|i
control|)
block|{
name|int
name|startOff
init|=
name|rand
operator|.
name|nextInt
argument_list|(
operator|(
name|int
operator|)
name|fileSize
argument_list|)
decl_stmt|;
name|int
name|len
init|=
literal|0
decl_stmt|;
try|try
block|{
name|double
name|p
init|=
name|rand
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|<
name|PROPORTION_NON_POSITIONAL_READ
condition|)
block|{
comment|// Do a small regular read. Very likely this will leave unread
comment|// data on the socket and make the socket uncacheable.
name|len
operator|=
name|Math
operator|.
name|min
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|64
argument_list|)
argument_list|,
operator|(
name|int
operator|)
name|fileSize
operator|-
name|startOff
argument_list|)
expr_stmt|;
name|read
argument_list|(
name|startOff
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|bytesRead
operator|+=
name|len
expr_stmt|;
block|}
else|else
block|{
comment|// Do a positional read most of the time.
name|len
operator|=
name|rand
operator|.
name|nextInt
argument_list|(
call|(
name|int
call|)
argument_list|(
name|fileSize
operator|-
name|startOff
argument_list|)
argument_list|)
expr_stmt|;
name|pRead
argument_list|(
name|startOff
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|bytesRead
operator|+=
name|len
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|getName
argument_list|()
operator|+
literal|": Error while testing read at "
operator|+
name|startOff
operator|+
literal|" length "
operator|+
name|len
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|error
operator|=
literal|true
expr_stmt|;
name|fail
argument_list|(
name|t
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getBytesRead ()
specifier|public
name|long
name|getBytesRead
parameter_list|()
block|{
return|return
name|bytesRead
return|;
block|}
comment|/**      * Raising error in a thread doesn't seem to fail the test.      * So check afterwards.      */
DECL|method|hasError ()
specifier|public
name|boolean
name|hasError
parameter_list|()
block|{
return|return
name|error
return|;
block|}
DECL|field|readCount
specifier|static
name|int
name|readCount
init|=
literal|0
decl_stmt|;
comment|/**      * Seek to somewhere random and read.      */
DECL|method|read (int start, int len)
specifier|private
name|void
name|read
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
literal|"Bad args: "
operator|+
name|start
operator|+
literal|" + "
operator|+
name|len
operator|+
literal|" should be<= "
operator|+
name|fileSize
argument_list|,
name|start
operator|+
name|len
operator|<=
name|fileSize
argument_list|)
expr_stmt|;
name|readCount
operator|++
expr_stmt|;
name|DFSInputStream
name|dis
init|=
name|testInfo
operator|.
name|dis
decl_stmt|;
name|byte
name|buf
index|[]
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
name|helper
operator|.
name|read
argument_list|(
name|dis
argument_list|,
name|buf
argument_list|,
name|start
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|verifyData
argument_list|(
literal|"Read data corrupted"
argument_list|,
name|buf
argument_list|,
name|start
argument_list|,
name|start
operator|+
name|len
argument_list|)
expr_stmt|;
block|}
comment|/**      * Positional read.      */
DECL|method|pRead (int start, int len)
specifier|private
name|void
name|pRead
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
literal|"Bad args: "
operator|+
name|start
operator|+
literal|" + "
operator|+
name|len
operator|+
literal|" should be<= "
operator|+
name|fileSize
argument_list|,
name|start
operator|+
name|len
operator|<=
name|fileSize
argument_list|)
expr_stmt|;
name|DFSInputStream
name|dis
init|=
name|testInfo
operator|.
name|dis
decl_stmt|;
name|byte
name|buf
index|[]
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
name|helper
operator|.
name|pRead
argument_list|(
name|dis
argument_list|,
name|buf
argument_list|,
name|start
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|verifyData
argument_list|(
literal|"Pread data corrupted"
argument_list|,
name|buf
argument_list|,
name|start
argument_list|,
name|start
operator|+
name|len
argument_list|)
expr_stmt|;
block|}
comment|/**      * Verify read data vs authentic data      */
DECL|method|verifyData (String msg, byte actual[], int start, int end)
specifier|private
name|void
name|verifyData
parameter_list|(
name|String
name|msg
parameter_list|,
name|byte
name|actual
index|[]
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
throws|throws
name|Exception
block|{
name|byte
name|auth
index|[]
init|=
name|testInfo
operator|.
name|authenticData
decl_stmt|;
if|if
condition|(
name|end
operator|>
name|auth
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
name|msg
operator|+
literal|": Actual array ("
operator|+
name|end
operator|+
literal|") is past the end of authentic data ("
operator|+
name|auth
operator|.
name|length
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|int
name|j
init|=
name|start
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
name|actual
operator|.
name|length
condition|;
operator|++
name|i
operator|,
operator|++
name|j
control|)
block|{
if|if
condition|(
name|auth
index|[
name|j
index|]
operator|!=
name|actual
index|[
name|i
index|]
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
name|msg
operator|+
literal|": Arrays byte "
operator|+
name|i
operator|+
literal|" (at offset "
operator|+
name|j
operator|+
literal|") differs: expect "
operator|+
name|auth
index|[
name|j
index|]
operator|+
literal|" got "
operator|+
name|actual
index|[
name|i
index|]
argument_list|)
throw|;
block|}
block|}
block|}
block|}
comment|/**    * Start the parallel read with the given parameters.    */
DECL|method|runParallelRead (int nFiles, int nWorkerEach, ReadWorkerHelper helper)
name|boolean
name|runParallelRead
parameter_list|(
name|int
name|nFiles
parameter_list|,
name|int
name|nWorkerEach
parameter_list|,
name|ReadWorkerHelper
name|helper
parameter_list|)
throws|throws
name|IOException
block|{
name|ReadWorker
name|workers
index|[]
init|=
operator|new
name|ReadWorker
index|[
name|nFiles
operator|*
name|nWorkerEach
index|]
decl_stmt|;
name|TestFileInfo
name|testInfoArr
index|[]
init|=
operator|new
name|TestFileInfo
index|[
name|nFiles
index|]
decl_stmt|;
comment|// Prepare the files and workers
name|int
name|nWorkers
init|=
literal|0
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
name|nFiles
condition|;
operator|++
name|i
control|)
block|{
name|TestFileInfo
name|testInfo
init|=
operator|new
name|TestFileInfo
argument_list|()
decl_stmt|;
name|testInfoArr
index|[
name|i
index|]
operator|=
name|testInfo
expr_stmt|;
name|testInfo
operator|.
name|filepath
operator|=
operator|new
name|Path
argument_list|(
literal|"/TestParallelRead.dat."
operator|+
name|i
argument_list|)
expr_stmt|;
name|testInfo
operator|.
name|authenticData
operator|=
name|util
operator|.
name|writeFile
argument_list|(
name|testInfo
operator|.
name|filepath
argument_list|,
name|FILE_SIZE_K
argument_list|)
expr_stmt|;
name|testInfo
operator|.
name|dis
operator|=
name|dfsClient
operator|.
name|open
argument_list|(
name|testInfo
operator|.
name|filepath
operator|.
name|toString
argument_list|()
argument_list|,
name|dfsClient
operator|.
name|getConf
argument_list|()
operator|.
name|getIoBufferSize
argument_list|()
argument_list|,
name|verifyChecksums
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|nWorkerEach
condition|;
operator|++
name|j
control|)
block|{
name|workers
index|[
name|nWorkers
operator|++
index|]
operator|=
operator|new
name|ReadWorker
argument_list|(
name|testInfo
argument_list|,
name|nWorkers
argument_list|,
name|helper
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Start the workers and wait
name|long
name|starttime
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
for|for
control|(
name|ReadWorker
name|worker
range|:
name|workers
control|)
block|{
name|worker
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|ReadWorker
name|worker
range|:
name|workers
control|)
block|{
try|try
block|{
name|worker
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignored
parameter_list|)
block|{ }
block|}
name|long
name|endtime
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
comment|// Cleanup
for|for
control|(
name|TestFileInfo
name|testInfo
range|:
name|testInfoArr
control|)
block|{
name|testInfo
operator|.
name|dis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Report
name|boolean
name|res
init|=
literal|true
decl_stmt|;
name|long
name|totalRead
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ReadWorker
name|worker
range|:
name|workers
control|)
block|{
name|long
name|nread
init|=
name|worker
operator|.
name|getBytesRead
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"--- Report: "
operator|+
name|worker
operator|.
name|getName
argument_list|()
operator|+
literal|" read "
operator|+
name|nread
operator|+
literal|" B; "
operator|+
literal|"average "
operator|+
name|nread
operator|/
name|ReadWorker
operator|.
name|N_ITERATIONS
operator|+
literal|" B per read"
argument_list|)
expr_stmt|;
name|totalRead
operator|+=
name|nread
expr_stmt|;
if|if
condition|(
name|worker
operator|.
name|hasError
argument_list|()
condition|)
block|{
name|res
operator|=
literal|false
expr_stmt|;
block|}
block|}
name|double
name|timeTakenSec
init|=
operator|(
name|endtime
operator|-
name|starttime
operator|)
operator|/
literal|1000.0
decl_stmt|;
name|long
name|totalReadKB
init|=
name|totalRead
operator|/
literal|1024
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"=== Report: "
operator|+
name|nWorkers
operator|+
literal|" threads read "
operator|+
name|totalReadKB
operator|+
literal|" KB (across "
operator|+
name|nFiles
operator|+
literal|" file(s)) in "
operator|+
name|timeTakenSec
operator|+
literal|"s; average "
operator|+
name|totalReadKB
operator|/
name|timeTakenSec
operator|+
literal|" KB/s"
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
comment|/**    * Runs a standard workload using a helper class which provides the read    * implementation to use.    */
DECL|method|runTestWorkload (ReadWorkerHelper helper)
specifier|public
name|void
name|runTestWorkload
parameter_list|(
name|ReadWorkerHelper
name|helper
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|runParallelRead
argument_list|(
literal|1
argument_list|,
literal|4
argument_list|,
name|helper
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"Check log for errors"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|runParallelRead
argument_list|(
literal|1
argument_list|,
literal|16
argument_list|,
name|helper
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"Check log for errors"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|runParallelRead
argument_list|(
literal|2
argument_list|,
literal|4
argument_list|,
name|helper
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"Check log for errors"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|teardownCluster ()
specifier|public
specifier|static
name|void
name|teardownCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|util
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|/**    * Do parallel read several times with different number of files and threads.    *    * Note that while this is the only "test" in a junit sense, we're actually    * dispatching a lot more. Failures in the other methods (and other threads)    * need to be manually collected, which is inconvenient.    */
annotation|@
name|Test
DECL|method|testParallelReadCopying ()
specifier|public
name|void
name|testParallelReadCopying
parameter_list|()
throws|throws
name|IOException
block|{
name|runTestWorkload
argument_list|(
operator|new
name|CopyingReadWorkerHelper
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testParallelReadByteBuffer ()
specifier|public
name|void
name|testParallelReadByteBuffer
parameter_list|()
throws|throws
name|IOException
block|{
name|runTestWorkload
argument_list|(
operator|new
name|DirectReadWorkerHelper
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testParallelReadMixed ()
specifier|public
name|void
name|testParallelReadMixed
parameter_list|()
throws|throws
name|IOException
block|{
name|runTestWorkload
argument_list|(
operator|new
name|MixedWorkloadHelper
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testParallelNoChecksums ()
specifier|public
name|void
name|testParallelNoChecksums
parameter_list|()
throws|throws
name|IOException
block|{
name|verifyChecksums
operator|=
literal|false
expr_stmt|;
name|runTestWorkload
argument_list|(
operator|new
name|MixedWorkloadHelper
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

