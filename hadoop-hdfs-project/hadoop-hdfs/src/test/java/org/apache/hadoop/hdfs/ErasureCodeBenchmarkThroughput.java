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
name|Preconditions
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
name|conf
operator|.
name|Configured
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
name|fs
operator|.
name|PathFilter
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
name|ErasureCodingPolicy
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
name|ErasureCodingPolicyManager
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
name|StopWatch
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
name|Tool
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
name|ToolRunner
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
name|text
operator|.
name|DecimalFormat
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
name|List
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
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
name|CompletionService
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
name|ExecutorCompletionService
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
name|ExecutorService
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
name|Executors
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
name|Future
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
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  * This class benchmarks the throughput of client read/write for both replica  * and Erasure Coding.  *<p/>  * Currently 4 operations are supported: read, write, generate and cleanup data.  * Users should specify an operation, the amount of data in MB for a single  * client, and which storage policy to use, i.e. EC or replication.  * Optionally, users can specify the number of clients to launch concurrently.  * The tool launches 1 thread for each client. Number of client is 1 by default.  * For reading, users can also specify whether stateful or positional read  * should be used. Stateful read is chosen by default.  *<p/>  * Each client reads and writes different files.  * For writing, client writes a temporary file at the desired amount, and the  * file will be cleaned up when the test finishes.  * For reading, each client tries to read the file specific to itself. And the  * client simply returns if such file does not exist. Therefore, users should  * generate the files before testing read. Generating data is essentially the  * same as writing, except that the files won't be cleared at the end.  * For example, if the user wants to test reading 1024MB data with 10 clients,  * he/she should firstly generate 1024MB data with 10 (or more) clients.  */
end_comment

begin_class
DECL|class|ErasureCodeBenchmarkThroughput
specifier|public
class|class
name|ErasureCodeBenchmarkThroughput
extends|extends
name|Configured
implements|implements
name|Tool
block|{
DECL|field|BUFFER_SIZE_MB
specifier|private
specifier|static
specifier|final
name|int
name|BUFFER_SIZE_MB
init|=
literal|128
decl_stmt|;
DECL|field|DFS_TMP_DIR
specifier|private
specifier|static
specifier|final
name|String
name|DFS_TMP_DIR
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.benchmark.data"
argument_list|,
literal|"/tmp/benchmark/data"
argument_list|)
decl_stmt|;
DECL|field|REP_DIR
specifier|public
specifier|static
specifier|final
name|String
name|REP_DIR
init|=
name|DFS_TMP_DIR
operator|+
literal|"/replica"
decl_stmt|;
DECL|field|EC_DIR
specifier|public
specifier|static
specifier|final
name|String
name|EC_DIR
init|=
name|DFS_TMP_DIR
operator|+
literal|"/ec"
decl_stmt|;
DECL|field|REP_FILE_BASE
specifier|private
specifier|static
specifier|final
name|String
name|REP_FILE_BASE
init|=
literal|"rep-file-"
decl_stmt|;
DECL|field|EC_FILE_BASE
specifier|private
specifier|static
specifier|final
name|String
name|EC_FILE_BASE
init|=
literal|"ec-file-"
decl_stmt|;
DECL|field|TMP_FILE_SUFFIX
specifier|private
specifier|static
specifier|final
name|String
name|TMP_FILE_SUFFIX
init|=
literal|".tmp"
decl_stmt|;
DECL|field|ecPolicy
specifier|private
specifier|static
specifier|final
name|ErasureCodingPolicy
name|ecPolicy
init|=
name|ErasureCodingPolicyManager
operator|.
name|getSystemDefaultPolicy
argument_list|()
decl_stmt|;
DECL|field|data
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|BUFFER_SIZE_MB
operator|*
literal|1024
operator|*
literal|1024
index|]
decl_stmt|;
static|static
block|{
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|random
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
DECL|field|fs
specifier|private
specifier|final
name|FileSystem
name|fs
decl_stmt|;
DECL|method|getEcPolicy ()
specifier|public
specifier|static
name|ErasureCodingPolicy
name|getEcPolicy
parameter_list|()
block|{
return|return
name|ecPolicy
return|;
block|}
DECL|method|ErasureCodeBenchmarkThroughput (FileSystem fs)
specifier|public
name|ErasureCodeBenchmarkThroughput
parameter_list|(
name|FileSystem
name|fs
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|fs
operator|instanceof
name|DistributedFileSystem
argument_list|)
expr_stmt|;
name|this
operator|.
name|fs
operator|=
name|fs
expr_stmt|;
block|}
DECL|enum|OpType
enum|enum
name|OpType
block|{
DECL|enumConstant|READ
DECL|enumConstant|WRITE
DECL|enumConstant|GEN
DECL|enumConstant|CLEAN
name|READ
block|,
name|WRITE
block|,
name|GEN
block|,
name|CLEAN
block|;   }
DECL|method|getFilePath (int dataSizeMB, boolean isEc)
specifier|public
specifier|static
name|String
name|getFilePath
parameter_list|(
name|int
name|dataSizeMB
parameter_list|,
name|boolean
name|isEc
parameter_list|)
block|{
name|String
name|parent
init|=
name|isEc
condition|?
name|EC_DIR
else|:
name|REP_DIR
decl_stmt|;
name|String
name|file
init|=
name|isEc
condition|?
name|EC_FILE_BASE
else|:
name|REP_FILE_BASE
decl_stmt|;
return|return
name|parent
operator|+
literal|"/"
operator|+
name|file
operator|+
name|dataSizeMB
operator|+
literal|"MB"
return|;
block|}
DECL|method|printUsage (String msg)
specifier|private
specifier|static
name|void
name|printUsage
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
if|if
condition|(
name|msg
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: ErasureCodeBenchmarkThroughput "
operator|+
literal|"<read|write|gen|clean><size in MB><ec|rep> [num clients] [stf|pos]\n"
operator|+
literal|"Stateful and positional option is only available for read."
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|doBenchmark (boolean isRead, int dataSizeMB, int numClients, boolean isEc, boolean statefulRead, boolean isGen)
specifier|private
name|List
argument_list|<
name|Long
argument_list|>
name|doBenchmark
parameter_list|(
name|boolean
name|isRead
parameter_list|,
name|int
name|dataSizeMB
parameter_list|,
name|int
name|numClients
parameter_list|,
name|boolean
name|isEc
parameter_list|,
name|boolean
name|statefulRead
parameter_list|,
name|boolean
name|isGen
parameter_list|)
throws|throws
name|Exception
block|{
name|CompletionService
argument_list|<
name|Long
argument_list|>
name|cs
init|=
operator|new
name|ExecutorCompletionService
argument_list|<
name|Long
argument_list|>
argument_list|(
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|numClients
argument_list|)
argument_list|)
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
name|numClients
condition|;
name|i
operator|++
control|)
block|{
name|cs
operator|.
name|submit
argument_list|(
name|isRead
condition|?
operator|new
name|ReadCallable
argument_list|(
name|dataSizeMB
argument_list|,
name|isEc
argument_list|,
name|i
argument_list|,
name|statefulRead
argument_list|)
else|:
operator|new
name|WriteCallable
argument_list|(
name|dataSizeMB
argument_list|,
name|isEc
argument_list|,
name|i
argument_list|,
name|isGen
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|Long
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numClients
argument_list|)
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
name|numClients
condition|;
name|i
operator|++
control|)
block|{
name|results
operator|.
name|add
argument_list|(
name|cs
operator|.
name|take
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|results
return|;
block|}
DECL|method|setReadThreadPoolSize (int numClients)
specifier|private
name|void
name|setReadThreadPoolSize
parameter_list|(
name|int
name|numClients
parameter_list|)
block|{
name|int
name|numThread
init|=
name|numClients
operator|*
name|ecPolicy
operator|.
name|getNumDataUnits
argument_list|()
decl_stmt|;
name|getConf
argument_list|()
operator|.
name|setInt
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|StripedRead
operator|.
name|THREADPOOL_SIZE_KEY
argument_list|,
name|numThread
argument_list|)
expr_stmt|;
block|}
DECL|method|getDecimalFormat ()
specifier|private
name|DecimalFormat
name|getDecimalFormat
parameter_list|()
block|{
return|return
operator|new
name|DecimalFormat
argument_list|(
literal|"#.##"
argument_list|)
return|;
block|}
DECL|method|benchmark (OpType type, int dataSizeMB, int numClients, boolean isEc, boolean statefulRead)
specifier|private
name|void
name|benchmark
parameter_list|(
name|OpType
name|type
parameter_list|,
name|int
name|dataSizeMB
parameter_list|,
name|int
name|numClients
parameter_list|,
name|boolean
name|isEc
parameter_list|,
name|boolean
name|statefulRead
parameter_list|)
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Long
argument_list|>
name|sizes
init|=
literal|null
decl_stmt|;
name|StopWatch
name|sw
init|=
operator|new
name|StopWatch
argument_list|()
operator|.
name|start
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|READ
case|:
name|sizes
operator|=
name|doBenchmark
argument_list|(
literal|true
argument_list|,
name|dataSizeMB
argument_list|,
name|numClients
argument_list|,
name|isEc
argument_list|,
name|statefulRead
argument_list|,
literal|false
argument_list|)
expr_stmt|;
break|break;
case|case
name|WRITE
case|:
name|sizes
operator|=
name|doBenchmark
argument_list|(
literal|false
argument_list|,
name|dataSizeMB
argument_list|,
name|numClients
argument_list|,
name|isEc
argument_list|,
name|statefulRead
argument_list|,
literal|false
argument_list|)
expr_stmt|;
break|break;
case|case
name|GEN
case|:
name|sizes
operator|=
name|doBenchmark
argument_list|(
literal|false
argument_list|,
name|dataSizeMB
argument_list|,
name|numClients
argument_list|,
name|isEc
argument_list|,
name|statefulRead
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|long
name|elapsedSec
init|=
name|sw
operator|.
name|now
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
name|double
name|totalDataSizeMB
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Long
name|size
range|:
name|sizes
control|)
block|{
if|if
condition|(
name|size
operator|>=
literal|0
condition|)
block|{
name|totalDataSizeMB
operator|+=
name|size
operator|.
name|doubleValue
argument_list|()
operator|/
literal|1024
operator|/
literal|1024
expr_stmt|;
block|}
block|}
name|double
name|throughput
init|=
name|totalDataSizeMB
operator|/
name|elapsedSec
decl_stmt|;
name|DecimalFormat
name|df
init|=
name|getDecimalFormat
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|type
operator|+
literal|" "
operator|+
name|df
operator|.
name|format
argument_list|(
name|totalDataSizeMB
argument_list|)
operator|+
literal|" MB data takes: "
operator|+
name|elapsedSec
operator|+
literal|" s.\nTotal throughput: "
operator|+
name|df
operator|.
name|format
argument_list|(
name|throughput
argument_list|)
operator|+
literal|" MB/s."
argument_list|)
expr_stmt|;
block|}
DECL|method|setUpDir ()
specifier|private
name|void
name|setUpDir
parameter_list|()
throws|throws
name|IOException
block|{
name|DistributedFileSystem
name|dfs
init|=
operator|(
name|DistributedFileSystem
operator|)
name|fs
decl_stmt|;
name|dfs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
name|DFS_TMP_DIR
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|repPath
init|=
operator|new
name|Path
argument_list|(
name|REP_DIR
argument_list|)
decl_stmt|;
name|Path
name|ecPath
init|=
operator|new
name|Path
argument_list|(
name|EC_DIR
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|dfs
operator|.
name|exists
argument_list|(
name|repPath
argument_list|)
condition|)
block|{
name|dfs
operator|.
name|mkdirs
argument_list|(
name|repPath
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|dfs
operator|.
name|getClient
argument_list|()
operator|.
name|getErasureCodingPolicy
argument_list|(
name|repPath
operator|.
name|toString
argument_list|()
argument_list|)
operator|==
literal|null
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|dfs
operator|.
name|exists
argument_list|(
name|ecPath
argument_list|)
condition|)
block|{
name|dfs
operator|.
name|mkdirs
argument_list|(
name|ecPath
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|getClient
argument_list|()
operator|.
name|setErasureCodingPolicy
argument_list|(
name|ecPath
operator|.
name|toString
argument_list|()
argument_list|,
name|ecPolicy
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|dfs
operator|.
name|getClient
argument_list|()
operator|.
name|getErasureCodingPolicy
argument_list|(
name|ecPath
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|equals
argument_list|(
name|ecPolicy
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|run (String[] args)
specifier|public
name|int
name|run
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|OpType
name|type
init|=
literal|null
decl_stmt|;
name|int
name|dataSizeMB
init|=
literal|0
decl_stmt|;
name|boolean
name|isEc
init|=
literal|true
decl_stmt|;
name|int
name|numClients
init|=
literal|1
decl_stmt|;
name|boolean
name|statefulRead
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>=
literal|3
condition|)
block|{
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"read"
argument_list|)
condition|)
block|{
name|type
operator|=
name|OpType
operator|.
name|READ
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"write"
argument_list|)
condition|)
block|{
name|type
operator|=
name|OpType
operator|.
name|WRITE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"gen"
argument_list|)
condition|)
block|{
name|type
operator|=
name|OpType
operator|.
name|GEN
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"clean"
argument_list|)
condition|)
block|{
name|type
operator|=
name|OpType
operator|.
name|CLEAN
expr_stmt|;
block|}
else|else
block|{
name|printUsage
argument_list|(
literal|"Unknown operation: "
operator|+
name|args
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|dataSizeMB
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|dataSizeMB
operator|<=
literal|0
condition|)
block|{
name|printUsage
argument_list|(
literal|"Invalid data size: "
operator|+
name|dataSizeMB
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|printUsage
argument_list|(
literal|"Invalid data size: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|isEc
operator|=
name|args
index|[
literal|2
index|]
operator|.
name|equals
argument_list|(
literal|"ec"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|isEc
operator|&&
operator|!
name|args
index|[
literal|2
index|]
operator|.
name|equals
argument_list|(
literal|"rep"
argument_list|)
condition|)
block|{
name|printUsage
argument_list|(
literal|"Unknown storage policy: "
operator|+
name|args
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|printUsage
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>=
literal|4
operator|&&
name|type
operator|!=
name|OpType
operator|.
name|CLEAN
condition|)
block|{
try|try
block|{
name|numClients
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|args
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|numClients
operator|<=
literal|0
condition|)
block|{
name|printUsage
argument_list|(
literal|"Invalid num of clients: "
operator|+
name|numClients
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|printUsage
argument_list|(
literal|"Invalid num of clients: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>=
literal|5
operator|&&
name|type
operator|==
name|OpType
operator|.
name|READ
condition|)
block|{
name|statefulRead
operator|=
name|args
index|[
literal|4
index|]
operator|.
name|equals
argument_list|(
literal|"stf"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|statefulRead
operator|&&
operator|!
name|args
index|[
literal|4
index|]
operator|.
name|equals
argument_list|(
literal|"pos"
argument_list|)
condition|)
block|{
name|printUsage
argument_list|(
literal|"Unknown read mode: "
operator|+
name|args
index|[
literal|4
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|setUpDir
argument_list|()
expr_stmt|;
if|if
condition|(
name|type
operator|==
name|OpType
operator|.
name|CLEAN
condition|)
block|{
name|cleanUp
argument_list|(
name|dataSizeMB
argument_list|,
name|isEc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|type
operator|==
name|OpType
operator|.
name|READ
operator|&&
name|isEc
condition|)
block|{
name|setReadThreadPoolSize
argument_list|(
name|numClients
argument_list|)
expr_stmt|;
block|}
name|benchmark
argument_list|(
name|type
argument_list|,
name|dataSizeMB
argument_list|,
name|numClients
argument_list|,
name|isEc
argument_list|,
name|statefulRead
argument_list|)
expr_stmt|;
block|}
return|return
literal|0
return|;
block|}
DECL|method|cleanUp (int dataSizeMB, boolean isEc)
specifier|private
name|void
name|cleanUp
parameter_list|(
name|int
name|dataSizeMB
parameter_list|,
name|boolean
name|isEc
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|fileName
init|=
name|getFilePath
argument_list|(
name|dataSizeMB
argument_list|,
name|isEc
argument_list|)
decl_stmt|;
name|Path
name|path
init|=
name|isEc
condition|?
operator|new
name|Path
argument_list|(
name|EC_DIR
argument_list|)
else|:
operator|new
name|Path
argument_list|(
name|REP_DIR
argument_list|)
decl_stmt|;
name|FileStatus
name|fileStatuses
index|[]
init|=
name|fs
operator|.
name|listStatus
argument_list|(
name|path
argument_list|,
operator|new
name|PathFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
return|return
name|path
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
name|fileName
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
for|for
control|(
name|FileStatus
name|fileStatus
range|:
name|fileStatuses
control|)
block|{
name|fs
operator|.
name|delete
argument_list|(
name|fileStatus
operator|.
name|getPath
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * A Callable that returns the number of bytes read/written    */
DECL|class|CallableBase
specifier|private
specifier|abstract
class|class
name|CallableBase
implements|implements
name|Callable
argument_list|<
name|Long
argument_list|>
block|{
DECL|field|dataSizeMB
specifier|protected
specifier|final
name|int
name|dataSizeMB
decl_stmt|;
DECL|field|isEc
specifier|protected
specifier|final
name|boolean
name|isEc
decl_stmt|;
DECL|field|id
specifier|protected
specifier|final
name|int
name|id
decl_stmt|;
DECL|method|CallableBase (int dataSizeMB, boolean isEc, int id)
specifier|public
name|CallableBase
parameter_list|(
name|int
name|dataSizeMB
parameter_list|,
name|boolean
name|isEc
parameter_list|,
name|int
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|dataSizeMB
operator|=
name|dataSizeMB
expr_stmt|;
name|this
operator|.
name|isEc
operator|=
name|isEc
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
DECL|method|getFilePathForThread ()
specifier|protected
name|String
name|getFilePathForThread
parameter_list|()
block|{
return|return
name|getFilePath
argument_list|(
name|dataSizeMB
argument_list|,
name|isEc
argument_list|)
operator|+
literal|"_"
operator|+
name|id
return|;
block|}
block|}
DECL|class|WriteCallable
specifier|private
class|class
name|WriteCallable
extends|extends
name|CallableBase
block|{
DECL|field|isGen
specifier|private
specifier|final
name|boolean
name|isGen
decl_stmt|;
DECL|method|WriteCallable (int dataSizeMB, boolean isEc, int id, boolean isGen)
specifier|public
name|WriteCallable
parameter_list|(
name|int
name|dataSizeMB
parameter_list|,
name|boolean
name|isEc
parameter_list|,
name|int
name|id
parameter_list|,
name|boolean
name|isGen
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|dataSizeMB
argument_list|,
name|isEc
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|this
operator|.
name|isGen
operator|=
name|isGen
expr_stmt|;
block|}
DECL|method|writeFile (Path path)
specifier|private
name|long
name|writeFile
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|StopWatch
name|sw
init|=
operator|new
name|StopWatch
argument_list|()
operator|.
name|start
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Writing "
operator|+
name|path
argument_list|)
expr_stmt|;
name|long
name|dataSize
init|=
name|dataSizeMB
operator|*
literal|1024
operator|*
literal|1024L
decl_stmt|;
name|long
name|remaining
init|=
name|dataSize
decl_stmt|;
try|try
init|(
name|FSDataOutputStream
name|outputStream
init|=
name|fs
operator|.
name|create
argument_list|(
name|path
argument_list|)
init|)
block|{
if|if
condition|(
operator|!
name|isGen
condition|)
block|{
name|fs
operator|.
name|deleteOnExit
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
name|int
name|toWrite
decl_stmt|;
while|while
condition|(
name|remaining
operator|>
literal|0
condition|)
block|{
name|toWrite
operator|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|remaining
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
name|outputStream
operator|.
name|write
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|toWrite
argument_list|)
expr_stmt|;
name|remaining
operator|-=
name|toWrite
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Finished writing "
operator|+
name|path
operator|+
literal|". Time taken: "
operator|+
name|sw
operator|.
name|now
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
operator|+
literal|" s."
argument_list|)
expr_stmt|;
return|return
name|dataSize
operator|-
name|remaining
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|Long
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|pathStr
init|=
name|getFilePathForThread
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|isGen
condition|)
block|{
name|pathStr
operator|+=
name|TMP_FILE_SUFFIX
expr_stmt|;
block|}
specifier|final
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
name|pathStr
argument_list|)
decl_stmt|;
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|path
argument_list|)
condition|)
block|{
if|if
condition|(
name|isGen
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Data already generated at "
operator|+
name|path
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Previous tmp data not cleaned "
operator|+
name|path
argument_list|)
expr_stmt|;
block|}
return|return
literal|0L
return|;
block|}
return|return
name|writeFile
argument_list|(
name|path
argument_list|)
return|;
block|}
block|}
DECL|class|ReadCallable
specifier|private
class|class
name|ReadCallable
extends|extends
name|CallableBase
block|{
DECL|field|statefulRead
specifier|private
specifier|final
name|boolean
name|statefulRead
decl_stmt|;
DECL|method|ReadCallable (int dataSizeMB, boolean isEc, int id, boolean statefulRead)
specifier|public
name|ReadCallable
parameter_list|(
name|int
name|dataSizeMB
parameter_list|,
name|boolean
name|isEc
parameter_list|,
name|int
name|id
parameter_list|,
name|boolean
name|statefulRead
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|dataSizeMB
argument_list|,
name|isEc
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|this
operator|.
name|statefulRead
operator|=
name|statefulRead
expr_stmt|;
block|}
DECL|method|doStateful (FSDataInputStream inputStream)
specifier|private
name|long
name|doStateful
parameter_list|(
name|FSDataInputStream
name|inputStream
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|count
init|=
literal|0
decl_stmt|;
name|long
name|bytesRead
decl_stmt|;
name|ByteBuffer
name|buffer
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|BUFFER_SIZE_MB
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|bytesRead
operator|=
name|inputStream
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
if|if
condition|(
name|bytesRead
operator|<
literal|0
condition|)
block|{
break|break;
block|}
name|count
operator|+=
name|bytesRead
expr_stmt|;
name|buffer
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
DECL|method|doPositional (FSDataInputStream inputStream)
specifier|private
name|long
name|doPositional
parameter_list|(
name|FSDataInputStream
name|inputStream
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|count
init|=
literal|0
decl_stmt|;
name|long
name|bytesRead
decl_stmt|;
name|byte
name|buf
index|[]
init|=
operator|new
name|byte
index|[
name|BUFFER_SIZE_MB
operator|*
literal|1024
operator|*
literal|1024
index|]
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|bytesRead
operator|=
name|inputStream
operator|.
name|read
argument_list|(
name|count
argument_list|,
name|buf
argument_list|,
literal|0
argument_list|,
name|buf
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|bytesRead
operator|<
literal|0
condition|)
block|{
break|break;
block|}
name|count
operator|+=
name|bytesRead
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
DECL|method|readFile (Path path)
specifier|private
name|long
name|readFile
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|FSDataInputStream
name|inputStream
init|=
name|fs
operator|.
name|open
argument_list|(
name|path
argument_list|)
init|)
block|{
name|StopWatch
name|sw
init|=
operator|new
name|StopWatch
argument_list|()
operator|.
name|start
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
operator|(
name|statefulRead
condition|?
literal|"Stateful reading "
else|:
literal|"Positional reading "
operator|)
operator|+
name|path
argument_list|)
expr_stmt|;
name|long
name|totalRead
init|=
name|statefulRead
condition|?
name|doStateful
argument_list|(
name|inputStream
argument_list|)
else|:
name|doPositional
argument_list|(
name|inputStream
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
operator|(
name|statefulRead
condition|?
literal|"Finished stateful read "
else|:
literal|"Finished positional read "
operator|)
operator|+
name|path
operator|+
literal|". Time taken: "
operator|+
name|sw
operator|.
name|now
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
operator|+
literal|" s."
argument_list|)
expr_stmt|;
return|return
name|totalRead
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|Long
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
name|getFilePathForThread
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|fs
operator|.
name|exists
argument_list|(
name|path
argument_list|)
operator|||
name|fs
operator|.
name|isDirectory
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"File not found at "
operator|+
name|path
operator|+
literal|". Call gen first?"
argument_list|)
expr_stmt|;
return|return
literal|0L
return|;
block|}
name|long
name|bytesRead
init|=
name|readFile
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|long
name|dataSize
init|=
name|dataSizeMB
operator|*
literal|1024
operator|*
literal|1024L
decl_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|bytesRead
operator|==
name|dataSize
argument_list|,
literal|"Specified data size: "
operator|+
name|dataSize
operator|+
literal|", actually read "
operator|+
name|bytesRead
argument_list|)
expr_stmt|;
return|return
name|bytesRead
return|;
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
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|int
name|res
init|=
name|ToolRunner
operator|.
name|run
argument_list|(
name|conf
argument_list|,
operator|new
name|ErasureCodeBenchmarkThroughput
argument_list|(
name|fs
argument_list|)
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|System
operator|.
name|exit
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

