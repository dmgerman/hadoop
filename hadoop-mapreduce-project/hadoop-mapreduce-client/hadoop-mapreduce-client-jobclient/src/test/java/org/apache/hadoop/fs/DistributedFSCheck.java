begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
package|;
end_package

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
name|FileNotFoundException
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
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringTokenizer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
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
name|io
operator|.
name|LongWritable
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
name|SequenceFile
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
name|Text
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
name|SequenceFile
operator|.
name|CompressionType
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
name|mapred
operator|.
name|*
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

begin_comment
comment|/**  * Distributed checkup of the file system consistency.  *<p>  * Test file system consistency by reading each block of each file  * of the specified file tree.   * Report corrupted blocks and general file statistics.  *<p>  * Optionally displays statistics on read performance.  *   */
end_comment

begin_class
annotation|@
name|Ignore
DECL|class|DistributedFSCheck
specifier|public
class|class
name|DistributedFSCheck
extends|extends
name|TestCase
block|{
comment|// Constants
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
name|DistributedFSCheck
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|TEST_TYPE_READ
specifier|private
specifier|static
specifier|final
name|int
name|TEST_TYPE_READ
init|=
literal|0
decl_stmt|;
DECL|field|TEST_TYPE_CLEANUP
specifier|private
specifier|static
specifier|final
name|int
name|TEST_TYPE_CLEANUP
init|=
literal|2
decl_stmt|;
DECL|field|DEFAULT_BUFFER_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_BUFFER_SIZE
init|=
literal|1000000
decl_stmt|;
DECL|field|DEFAULT_RES_FILE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_RES_FILE_NAME
init|=
literal|"DistributedFSCheck_results.log"
decl_stmt|;
DECL|field|MEGA
specifier|private
specifier|static
specifier|final
name|long
name|MEGA
init|=
literal|0x100000
decl_stmt|;
DECL|field|fsConfig
specifier|private
specifier|static
name|Configuration
name|fsConfig
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|field|TEST_ROOT_DIR
specifier|private
specifier|static
name|Path
name|TEST_ROOT_DIR
init|=
operator|new
name|Path
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/benchmarks/DistributedFSCheck"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|MAP_INPUT_DIR
specifier|private
specifier|static
name|Path
name|MAP_INPUT_DIR
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"map_input"
argument_list|)
decl_stmt|;
DECL|field|READ_DIR
specifier|private
specifier|static
name|Path
name|READ_DIR
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"io_read"
argument_list|)
decl_stmt|;
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
DECL|field|nrFiles
specifier|private
name|long
name|nrFiles
decl_stmt|;
DECL|method|DistributedFSCheck (Configuration conf)
name|DistributedFSCheck
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|fsConfig
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|fs
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * Run distributed checkup for the entire files system.    *     * @throws Exception    */
DECL|method|testFSBlocks ()
specifier|public
name|void
name|testFSBlocks
parameter_list|()
throws|throws
name|Exception
block|{
name|testFSBlocks
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Run distributed checkup for the specified directory.    *     * @param rootName root directory name    * @throws Exception    */
DECL|method|testFSBlocks (String rootName)
specifier|public
name|void
name|testFSBlocks
parameter_list|(
name|String
name|rootName
parameter_list|)
throws|throws
name|Exception
block|{
name|createInputFile
argument_list|(
name|rootName
argument_list|)
expr_stmt|;
name|runDistributedFSCheck
argument_list|()
expr_stmt|;
name|cleanup
argument_list|()
expr_stmt|;
comment|// clean up after all to restore the system state
block|}
DECL|method|createInputFile (String rootName)
specifier|private
name|void
name|createInputFile
parameter_list|(
name|String
name|rootName
parameter_list|)
throws|throws
name|IOException
block|{
name|cleanup
argument_list|()
expr_stmt|;
comment|// clean up if previous run failed
name|Path
name|inputFile
init|=
operator|new
name|Path
argument_list|(
name|MAP_INPUT_DIR
argument_list|,
literal|"in_file"
argument_list|)
decl_stmt|;
name|SequenceFile
operator|.
name|Writer
name|writer
init|=
name|SequenceFile
operator|.
name|createWriter
argument_list|(
name|fs
argument_list|,
name|fsConfig
argument_list|,
name|inputFile
argument_list|,
name|Text
operator|.
name|class
argument_list|,
name|LongWritable
operator|.
name|class
argument_list|,
name|CompressionType
operator|.
name|NONE
argument_list|)
decl_stmt|;
try|try
block|{
name|nrFiles
operator|=
literal|0
expr_stmt|;
name|listSubtree
argument_list|(
operator|new
name|Path
argument_list|(
name|rootName
argument_list|)
argument_list|,
name|writer
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Created map input files."
argument_list|)
expr_stmt|;
block|}
DECL|method|listSubtree (Path rootFile, SequenceFile.Writer writer )
specifier|private
name|void
name|listSubtree
parameter_list|(
name|Path
name|rootFile
parameter_list|,
name|SequenceFile
operator|.
name|Writer
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
name|FileStatus
name|rootStatus
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|rootFile
argument_list|)
decl_stmt|;
name|listSubtree
argument_list|(
name|rootStatus
argument_list|,
name|writer
argument_list|)
expr_stmt|;
block|}
DECL|method|listSubtree (FileStatus rootStatus, SequenceFile.Writer writer )
specifier|private
name|void
name|listSubtree
parameter_list|(
name|FileStatus
name|rootStatus
parameter_list|,
name|SequenceFile
operator|.
name|Writer
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|rootFile
init|=
name|rootStatus
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|rootStatus
operator|.
name|isFile
argument_list|()
condition|)
block|{
name|nrFiles
operator|++
expr_stmt|;
comment|// For a regular file generate<fName,offset> pairs
name|long
name|blockSize
init|=
name|fs
operator|.
name|getDefaultBlockSize
argument_list|(
name|rootFile
argument_list|)
decl_stmt|;
name|long
name|fileLength
init|=
name|rootStatus
operator|.
name|getLen
argument_list|()
decl_stmt|;
for|for
control|(
name|long
name|offset
init|=
literal|0
init|;
name|offset
operator|<
name|fileLength
condition|;
name|offset
operator|+=
name|blockSize
control|)
name|writer
operator|.
name|append
argument_list|(
operator|new
name|Text
argument_list|(
name|rootFile
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
operator|new
name|LongWritable
argument_list|(
name|offset
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|FileStatus
index|[]
name|children
init|=
literal|null
decl_stmt|;
try|try
block|{
name|children
operator|=
name|fs
operator|.
name|listStatus
argument_list|(
name|rootFile
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|fnfe
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not get listing for "
operator|+
name|rootFile
argument_list|)
throw|;
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
name|children
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|listSubtree
argument_list|(
name|children
index|[
name|i
index|]
argument_list|,
name|writer
argument_list|)
expr_stmt|;
block|}
comment|/**    * DistributedFSCheck mapper class.    */
DECL|class|DistributedFSCheckMapper
specifier|public
specifier|static
class|class
name|DistributedFSCheckMapper
extends|extends
name|IOMapperBase
argument_list|<
name|Object
argument_list|>
block|{
DECL|method|DistributedFSCheckMapper ()
specifier|public
name|DistributedFSCheckMapper
parameter_list|()
block|{      }
DECL|method|doIO (Reporter reporter, String name, long offset )
specifier|public
name|Object
name|doIO
parameter_list|(
name|Reporter
name|reporter
parameter_list|,
name|String
name|name
parameter_list|,
name|long
name|offset
parameter_list|)
throws|throws
name|IOException
block|{
comment|// open file
name|FSDataInputStream
name|in
init|=
literal|null
decl_stmt|;
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
name|name
argument_list|)
decl_stmt|;
try|try
block|{
name|in
operator|=
name|fs
operator|.
name|open
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
name|name
operator|+
literal|"@(missing)"
return|;
block|}
name|in
operator|.
name|seek
argument_list|(
name|offset
argument_list|)
expr_stmt|;
name|long
name|actualSize
init|=
literal|0
decl_stmt|;
try|try
block|{
name|long
name|blockSize
init|=
name|fs
operator|.
name|getDefaultBlockSize
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|reporter
operator|.
name|setStatus
argument_list|(
literal|"reading "
operator|+
name|name
operator|+
literal|"@"
operator|+
name|offset
operator|+
literal|"/"
operator|+
name|blockSize
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|curSize
init|=
name|bufferSize
init|;
name|curSize
operator|==
name|bufferSize
operator|&&
name|actualSize
operator|<
name|blockSize
condition|;
name|actualSize
operator|+=
name|curSize
control|)
block|{
name|curSize
operator|=
name|in
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|bufferSize
argument_list|)
expr_stmt|;
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
name|info
argument_list|(
literal|"Corrupted block detected in \""
operator|+
name|name
operator|+
literal|"\" at "
operator|+
name|offset
argument_list|)
expr_stmt|;
return|return
name|name
operator|+
literal|"@"
operator|+
name|offset
return|;
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|Long
argument_list|(
name|actualSize
argument_list|)
return|;
block|}
DECL|method|collectStats (OutputCollector<Text, Text> output, String name, long execTime, Object corruptedBlock)
name|void
name|collectStats
parameter_list|(
name|OutputCollector
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
name|output
parameter_list|,
name|String
name|name
parameter_list|,
name|long
name|execTime
parameter_list|,
name|Object
name|corruptedBlock
parameter_list|)
throws|throws
name|IOException
block|{
name|output
operator|.
name|collect
argument_list|(
operator|new
name|Text
argument_list|(
name|AccumulatingReducer
operator|.
name|VALUE_TYPE_LONG
operator|+
literal|"blocks"
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|corruptedBlock
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"String"
argument_list|)
condition|)
block|{
name|output
operator|.
name|collect
argument_list|(
operator|new
name|Text
argument_list|(
name|AccumulatingReducer
operator|.
name|VALUE_TYPE_STRING
operator|+
literal|"badBlocks"
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
operator|(
name|String
operator|)
name|corruptedBlock
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|long
name|totalSize
init|=
operator|(
operator|(
name|Long
operator|)
name|corruptedBlock
operator|)
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|float
name|ioRateMbSec
init|=
operator|(
name|float
operator|)
name|totalSize
operator|*
literal|1000
operator|/
operator|(
name|execTime
operator|*
literal|0x100000
operator|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Number of bytes processed = "
operator|+
name|totalSize
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Exec time = "
operator|+
name|execTime
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"IO rate = "
operator|+
name|ioRateMbSec
argument_list|)
expr_stmt|;
name|output
operator|.
name|collect
argument_list|(
operator|new
name|Text
argument_list|(
name|AccumulatingReducer
operator|.
name|VALUE_TYPE_LONG
operator|+
literal|"size"
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|totalSize
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|output
operator|.
name|collect
argument_list|(
operator|new
name|Text
argument_list|(
name|AccumulatingReducer
operator|.
name|VALUE_TYPE_LONG
operator|+
literal|"time"
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|execTime
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|output
operator|.
name|collect
argument_list|(
operator|new
name|Text
argument_list|(
name|AccumulatingReducer
operator|.
name|VALUE_TYPE_FLOAT
operator|+
literal|"rate"
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|ioRateMbSec
operator|*
literal|1000
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|runDistributedFSCheck ()
specifier|private
name|void
name|runDistributedFSCheck
parameter_list|()
throws|throws
name|Exception
block|{
name|JobConf
name|job
init|=
operator|new
name|JobConf
argument_list|(
name|fs
operator|.
name|getConf
argument_list|()
argument_list|,
name|DistributedFSCheck
operator|.
name|class
argument_list|)
decl_stmt|;
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|job
argument_list|,
name|MAP_INPUT_DIR
argument_list|)
expr_stmt|;
name|job
operator|.
name|setInputFormat
argument_list|(
name|SequenceFileInputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMapperClass
argument_list|(
name|DistributedFSCheckMapper
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setReducerClass
argument_list|(
name|AccumulatingReducer
operator|.
name|class
argument_list|)
expr_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|job
argument_list|,
name|READ_DIR
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputKeyClass
argument_list|(
name|Text
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputValueClass
argument_list|(
name|Text
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setNumReduceTasks
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|JobClient
operator|.
name|runJob
argument_list|(
name|job
argument_list|)
expr_stmt|;
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
name|int
name|testType
init|=
name|TEST_TYPE_READ
decl_stmt|;
name|int
name|bufferSize
init|=
name|DEFAULT_BUFFER_SIZE
decl_stmt|;
name|String
name|resFileName
init|=
name|DEFAULT_RES_FILE_NAME
decl_stmt|;
name|String
name|rootName
init|=
literal|"/"
decl_stmt|;
name|boolean
name|viewStats
init|=
literal|false
decl_stmt|;
name|String
name|usage
init|=
literal|"Usage: DistributedFSCheck [-root name] [-clean] [-resFile resultFileName] [-bufferSize Bytes] [-stats] "
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|1
operator|&&
name|args
index|[
literal|0
index|]
operator|.
name|startsWith
argument_list|(
literal|"-h"
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|usage
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
operator|-
literal|1
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
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// parse command line
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-root"
argument_list|)
condition|)
block|{
name|rootName
operator|=
name|args
index|[
operator|++
name|i
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|startsWith
argument_list|(
literal|"-clean"
argument_list|)
condition|)
block|{
name|testType
operator|=
name|TEST_TYPE_CLEANUP
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-bufferSize"
argument_list|)
condition|)
block|{
name|bufferSize
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
operator|++
name|i
index|]
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-resFile"
argument_list|)
condition|)
block|{
name|resFileName
operator|=
name|args
index|[
operator|++
name|i
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|startsWith
argument_list|(
literal|"-stat"
argument_list|)
condition|)
block|{
name|viewStats
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"root = "
operator|+
name|rootName
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"bufferSize = "
operator|+
name|bufferSize
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
literal|"test.io.file.buffer.size"
argument_list|,
name|bufferSize
argument_list|)
expr_stmt|;
name|DistributedFSCheck
name|test
init|=
operator|new
name|DistributedFSCheck
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|testType
operator|==
name|TEST_TYPE_CLEANUP
condition|)
block|{
name|test
operator|.
name|cleanup
argument_list|()
expr_stmt|;
return|return;
block|}
name|test
operator|.
name|createInputFile
argument_list|(
name|rootName
argument_list|)
expr_stmt|;
name|long
name|tStart
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|test
operator|.
name|runDistributedFSCheck
argument_list|()
expr_stmt|;
name|long
name|execTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|tStart
decl_stmt|;
name|test
operator|.
name|analyzeResult
argument_list|(
name|execTime
argument_list|,
name|resFileName
argument_list|,
name|viewStats
argument_list|)
expr_stmt|;
comment|// test.cleanup();  // clean up after all to restore the system state
block|}
DECL|method|analyzeResult (long execTime, String resFileName, boolean viewStats )
specifier|private
name|void
name|analyzeResult
parameter_list|(
name|long
name|execTime
parameter_list|,
name|String
name|resFileName
parameter_list|,
name|boolean
name|viewStats
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|reduceFile
init|=
operator|new
name|Path
argument_list|(
name|READ_DIR
argument_list|,
literal|"part-00000"
argument_list|)
decl_stmt|;
name|DataInputStream
name|in
decl_stmt|;
name|in
operator|=
operator|new
name|DataInputStream
argument_list|(
name|fs
operator|.
name|open
argument_list|(
name|reduceFile
argument_list|)
argument_list|)
expr_stmt|;
name|BufferedReader
name|lines
decl_stmt|;
name|lines
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
name|long
name|blocks
init|=
literal|0
decl_stmt|;
name|long
name|size
init|=
literal|0
decl_stmt|;
name|long
name|time
init|=
literal|0
decl_stmt|;
name|float
name|rate
init|=
literal|0
decl_stmt|;
name|StringTokenizer
name|badBlocks
init|=
literal|null
decl_stmt|;
name|long
name|nrBadBlocks
init|=
literal|0
decl_stmt|;
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|lines
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|StringTokenizer
name|tokens
init|=
operator|new
name|StringTokenizer
argument_list|(
name|line
argument_list|,
literal|" \t\n\r\f%"
argument_list|)
decl_stmt|;
name|String
name|attr
init|=
name|tokens
operator|.
name|nextToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|attr
operator|.
name|endsWith
argument_list|(
literal|"blocks"
argument_list|)
condition|)
name|blocks
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|tokens
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|attr
operator|.
name|endsWith
argument_list|(
literal|"size"
argument_list|)
condition|)
name|size
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|tokens
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|attr
operator|.
name|endsWith
argument_list|(
literal|"time"
argument_list|)
condition|)
name|time
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|tokens
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|attr
operator|.
name|endsWith
argument_list|(
literal|"rate"
argument_list|)
condition|)
name|rate
operator|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|tokens
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|attr
operator|.
name|endsWith
argument_list|(
literal|"badBlocks"
argument_list|)
condition|)
block|{
name|badBlocks
operator|=
operator|new
name|StringTokenizer
argument_list|(
name|tokens
operator|.
name|nextToken
argument_list|()
argument_list|,
literal|";"
argument_list|)
expr_stmt|;
name|nrBadBlocks
operator|=
name|badBlocks
operator|.
name|countTokens
argument_list|()
expr_stmt|;
block|}
block|}
name|Vector
argument_list|<
name|String
argument_list|>
name|resultLines
init|=
operator|new
name|Vector
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|resultLines
operator|.
name|add
argument_list|(
literal|"----- DistributedFSCheck ----- : "
argument_list|)
expr_stmt|;
name|resultLines
operator|.
name|add
argument_list|(
literal|"               Date& time: "
operator|+
operator|new
name|Date
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|resultLines
operator|.
name|add
argument_list|(
literal|"    Total number of blocks: "
operator|+
name|blocks
argument_list|)
expr_stmt|;
name|resultLines
operator|.
name|add
argument_list|(
literal|"    Total number of  files: "
operator|+
name|nrFiles
argument_list|)
expr_stmt|;
name|resultLines
operator|.
name|add
argument_list|(
literal|"Number of corrupted blocks: "
operator|+
name|nrBadBlocks
argument_list|)
expr_stmt|;
name|int
name|nrBadFilesPos
init|=
name|resultLines
operator|.
name|size
argument_list|()
decl_stmt|;
name|TreeSet
argument_list|<
name|String
argument_list|>
name|badFiles
init|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|long
name|nrBadFiles
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|nrBadBlocks
operator|>
literal|0
condition|)
block|{
name|resultLines
operator|.
name|add
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|resultLines
operator|.
name|add
argument_list|(
literal|"----- Corrupted Blocks (file@offset) ----- : "
argument_list|)
expr_stmt|;
while|while
condition|(
name|badBlocks
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|String
name|curBlock
init|=
name|badBlocks
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|resultLines
operator|.
name|add
argument_list|(
name|curBlock
argument_list|)
expr_stmt|;
name|badFiles
operator|.
name|add
argument_list|(
name|curBlock
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|curBlock
operator|.
name|indexOf
argument_list|(
literal|'@'
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|nrBadFiles
operator|=
name|badFiles
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
name|resultLines
operator|.
name|insertElementAt
argument_list|(
literal|" Number of corrupted files: "
operator|+
name|nrBadFiles
argument_list|,
name|nrBadFilesPos
argument_list|)
expr_stmt|;
if|if
condition|(
name|viewStats
condition|)
block|{
name|resultLines
operator|.
name|add
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|resultLines
operator|.
name|add
argument_list|(
literal|"-----   Performance  ----- : "
argument_list|)
expr_stmt|;
name|resultLines
operator|.
name|add
argument_list|(
literal|"         Total MBytes read: "
operator|+
name|size
operator|/
name|MEGA
argument_list|)
expr_stmt|;
name|resultLines
operator|.
name|add
argument_list|(
literal|"         Throughput mb/sec: "
operator|+
operator|(
name|float
operator|)
name|size
operator|*
literal|1000.0
operator|/
operator|(
name|time
operator|*
name|MEGA
operator|)
argument_list|)
expr_stmt|;
name|resultLines
operator|.
name|add
argument_list|(
literal|"    Average IO rate mb/sec: "
operator|+
name|rate
operator|/
literal|1000
operator|/
name|blocks
argument_list|)
expr_stmt|;
name|resultLines
operator|.
name|add
argument_list|(
literal|"        Test exec time sec: "
operator|+
operator|(
name|float
operator|)
name|execTime
operator|/
literal|1000
argument_list|)
expr_stmt|;
block|}
name|PrintStream
name|res
init|=
operator|new
name|PrintStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|resFileName
argument_list|)
argument_list|,
literal|true
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
name|resultLines
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|cur
init|=
name|resultLines
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|cur
argument_list|)
expr_stmt|;
name|res
operator|.
name|println
argument_list|(
name|cur
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|cleanup ()
specifier|private
name|void
name|cleanup
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Cleaning up test files"
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

