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
name|IOException
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
name|mapred
operator|.
name|JobConf
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
name|StringUtils
import|;
end_import

begin_comment
comment|/**  * This program executes a specified operation that applies load to   * the NameNode. Possible operations include create/writing files,  * opening/reading files, renaming files, and deleting files.  *   * When run simultaneously on multiple nodes, this program functions   * as a stress-test and benchmark for namenode, especially when   * the number of bytes written to each file is small.  *   * This version does not use the map reduce framework  *   */
end_comment

begin_class
DECL|class|NNBenchWithoutMR
specifier|public
class|class
name|NNBenchWithoutMR
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
literal|"org.apache.hadoop.hdfs.NNBench"
argument_list|)
decl_stmt|;
comment|// variable initialzed from command line arguments
DECL|field|startTime
specifier|private
specifier|static
name|long
name|startTime
init|=
literal|0
decl_stmt|;
DECL|field|numFiles
specifier|private
specifier|static
name|int
name|numFiles
init|=
literal|0
decl_stmt|;
DECL|field|bytesPerBlock
specifier|private
specifier|static
name|long
name|bytesPerBlock
init|=
literal|1
decl_stmt|;
DECL|field|blocksPerFile
specifier|private
specifier|static
name|long
name|blocksPerFile
init|=
literal|0
decl_stmt|;
DECL|field|bytesPerFile
specifier|private
specifier|static
name|long
name|bytesPerFile
init|=
literal|1
decl_stmt|;
DECL|field|baseDir
specifier|private
specifier|static
name|Path
name|baseDir
init|=
literal|null
decl_stmt|;
comment|// variables initialized in main()
DECL|field|fileSys
specifier|private
specifier|static
name|FileSystem
name|fileSys
init|=
literal|null
decl_stmt|;
DECL|field|taskDir
specifier|private
specifier|static
name|Path
name|taskDir
init|=
literal|null
decl_stmt|;
DECL|field|uniqueId
specifier|private
specifier|static
name|String
name|uniqueId
init|=
literal|null
decl_stmt|;
DECL|field|buffer
specifier|private
specifier|static
name|byte
index|[]
name|buffer
decl_stmt|;
DECL|field|maxExceptionsPerFile
specifier|private
specifier|static
name|long
name|maxExceptionsPerFile
init|=
literal|200
decl_stmt|;
comment|/**    * Returns when the current number of seconds from the epoch equals    * the command line argument given by<code>-startTime</code>.    * This allows multiple instances of this program, running on clock    * synchronized nodes, to start at roughly the same time.    */
DECL|method|barrier ()
specifier|static
name|void
name|barrier
parameter_list|()
block|{
name|long
name|sleepTime
decl_stmt|;
while|while
condition|(
operator|(
name|sleepTime
operator|=
name|startTime
operator|-
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|)
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepTime
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{       }
block|}
block|}
DECL|method|handleException (String operation, Throwable e, int singleFileExceptions)
specifier|static
specifier|private
name|void
name|handleException
parameter_list|(
name|String
name|operation
parameter_list|,
name|Throwable
name|e
parameter_list|,
name|int
name|singleFileExceptions
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception while "
operator|+
name|operation
operator|+
literal|": "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|singleFileExceptions
operator|>=
name|maxExceptionsPerFile
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|singleFileExceptions
operator|+
literal|" exceptions for a single file exceeds threshold. Aborting"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Create and write to a given number of files.  Repeat each remote    * operation until is suceeds (does not throw an exception).    *    * @return the number of exceptions caught    */
DECL|method|createWrite ()
specifier|static
name|int
name|createWrite
parameter_list|()
block|{
name|int
name|totalExceptions
init|=
literal|0
decl_stmt|;
name|FSDataOutputStream
name|out
init|=
literal|null
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|index
init|=
literal|0
init|;
name|index
operator|<
name|numFiles
condition|;
name|index
operator|++
control|)
block|{
name|int
name|singleFileExceptions
init|=
literal|0
decl_stmt|;
do|do
block|{
comment|// create file until is succeeds or max exceptions reached
try|try
block|{
name|out
operator|=
name|fileSys
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|taskDir
argument_list|,
literal|""
operator|+
name|index
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|512
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
name|bytesPerBlock
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|success
operator|=
literal|false
expr_stmt|;
name|totalExceptions
operator|++
expr_stmt|;
name|handleException
argument_list|(
literal|"creating file #"
operator|+
name|index
argument_list|,
name|ioe
argument_list|,
operator|++
name|singleFileExceptions
argument_list|)
expr_stmt|;
block|}
block|}
do|while
condition|(
operator|!
name|success
condition|)
do|;
name|long
name|toBeWritten
init|=
name|bytesPerFile
decl_stmt|;
while|while
condition|(
name|toBeWritten
operator|>
literal|0
condition|)
block|{
name|int
name|nbytes
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|buffer
operator|.
name|length
argument_list|,
name|toBeWritten
argument_list|)
decl_stmt|;
name|toBeWritten
operator|-=
name|nbytes
expr_stmt|;
try|try
block|{
comment|// only try once
name|out
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|nbytes
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|totalExceptions
operator|++
expr_stmt|;
name|handleException
argument_list|(
literal|"writing to file #"
operator|+
name|index
argument_list|,
name|ioe
argument_list|,
operator|++
name|singleFileExceptions
argument_list|)
expr_stmt|;
block|}
block|}
do|do
block|{
comment|// close file until is succeeds
try|try
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|success
operator|=
literal|false
expr_stmt|;
name|totalExceptions
operator|++
expr_stmt|;
name|handleException
argument_list|(
literal|"closing file #"
operator|+
name|index
argument_list|,
name|ioe
argument_list|,
operator|++
name|singleFileExceptions
argument_list|)
expr_stmt|;
block|}
block|}
do|while
condition|(
operator|!
name|success
condition|)
do|;
block|}
return|return
name|totalExceptions
return|;
block|}
comment|/**    * Open and read a given number of files.    *    * @return the number of exceptions caught    */
DECL|method|openRead ()
specifier|static
name|int
name|openRead
parameter_list|()
block|{
name|int
name|totalExceptions
init|=
literal|0
decl_stmt|;
name|FSDataInputStream
name|in
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|index
init|=
literal|0
init|;
name|index
operator|<
name|numFiles
condition|;
name|index
operator|++
control|)
block|{
name|int
name|singleFileExceptions
init|=
literal|0
decl_stmt|;
try|try
block|{
name|in
operator|=
name|fileSys
operator|.
name|open
argument_list|(
operator|new
name|Path
argument_list|(
name|taskDir
argument_list|,
literal|""
operator|+
name|index
argument_list|)
argument_list|,
literal|512
argument_list|)
expr_stmt|;
name|long
name|toBeRead
init|=
name|bytesPerFile
decl_stmt|;
while|while
condition|(
name|toBeRead
operator|>
literal|0
condition|)
block|{
name|int
name|nbytes
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|buffer
operator|.
name|length
argument_list|,
name|toBeRead
argument_list|)
decl_stmt|;
name|toBeRead
operator|-=
name|nbytes
expr_stmt|;
try|try
block|{
comment|// only try once
name|in
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|nbytes
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|totalExceptions
operator|++
expr_stmt|;
name|handleException
argument_list|(
literal|"reading from file #"
operator|+
name|index
argument_list|,
name|ioe
argument_list|,
operator|++
name|singleFileExceptions
argument_list|)
expr_stmt|;
block|}
block|}
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|totalExceptions
operator|++
expr_stmt|;
name|handleException
argument_list|(
literal|"opening file #"
operator|+
name|index
argument_list|,
name|ioe
argument_list|,
operator|++
name|singleFileExceptions
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|totalExceptions
return|;
block|}
comment|/**    * Rename a given number of files.  Repeat each remote    * operation until is suceeds (does not throw an exception).    *    * @return the number of exceptions caught    */
DECL|method|rename ()
specifier|static
name|int
name|rename
parameter_list|()
block|{
name|int
name|totalExceptions
init|=
literal|0
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|index
init|=
literal|0
init|;
name|index
operator|<
name|numFiles
condition|;
name|index
operator|++
control|)
block|{
name|int
name|singleFileExceptions
init|=
literal|0
decl_stmt|;
do|do
block|{
comment|// rename file until is succeeds
try|try
block|{
name|boolean
name|result
init|=
name|fileSys
operator|.
name|rename
argument_list|(
operator|new
name|Path
argument_list|(
name|taskDir
argument_list|,
literal|""
operator|+
name|index
argument_list|)
argument_list|,
operator|new
name|Path
argument_list|(
name|taskDir
argument_list|,
literal|"A"
operator|+
name|index
argument_list|)
argument_list|)
decl_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|success
operator|=
literal|false
expr_stmt|;
name|totalExceptions
operator|++
expr_stmt|;
name|handleException
argument_list|(
literal|"creating file #"
operator|+
name|index
argument_list|,
name|ioe
argument_list|,
operator|++
name|singleFileExceptions
argument_list|)
expr_stmt|;
block|}
block|}
do|while
condition|(
operator|!
name|success
condition|)
do|;
block|}
return|return
name|totalExceptions
return|;
block|}
comment|/**    * Delete a given number of files.  Repeat each remote    * operation until is suceeds (does not throw an exception).    *    * @return the number of exceptions caught    */
DECL|method|delete ()
specifier|static
name|int
name|delete
parameter_list|()
block|{
name|int
name|totalExceptions
init|=
literal|0
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|index
init|=
literal|0
init|;
name|index
operator|<
name|numFiles
condition|;
name|index
operator|++
control|)
block|{
name|int
name|singleFileExceptions
init|=
literal|0
decl_stmt|;
do|do
block|{
comment|// delete file until is succeeds
try|try
block|{
name|boolean
name|result
init|=
name|fileSys
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|taskDir
argument_list|,
literal|"A"
operator|+
name|index
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|success
operator|=
literal|false
expr_stmt|;
name|totalExceptions
operator|++
expr_stmt|;
name|handleException
argument_list|(
literal|"creating file #"
operator|+
name|index
argument_list|,
name|ioe
argument_list|,
operator|++
name|singleFileExceptions
argument_list|)
expr_stmt|;
block|}
block|}
do|while
condition|(
operator|!
name|success
condition|)
do|;
block|}
return|return
name|totalExceptions
return|;
block|}
comment|/**    * This launches a given namenode operation (<code>-operation</code>),    * starting at a given time (<code>-startTime</code>).  The files used    * by the openRead, rename, and delete operations are the same files    * created by the createWrite operation.  Typically, the program    * would be run four times, once for each operation in this order:    * createWrite, openRead, rename, delete.    *    *<pre>    * Usage: nnbench     *          -operation<one of createWrite, openRead, rename, or delete>    *          -baseDir<base output/input DFS path>    *          -startTime<time to start, given in seconds from the epoch>    *          -numFiles<number of files to create, read, rename, or delete>    *          -blocksPerFile<number of blocks to create per file>    *         [-bytesPerBlock<number of bytes to write to each block, default is 1>]    *         [-bytesPerChecksum<value for io.bytes.per.checksum>]    *</pre>    *    * @throws IOException indicates a problem with test startup    */
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
name|IOException
block|{
name|String
name|version
init|=
literal|"NameNodeBenchmark.0.3"
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|version
argument_list|)
expr_stmt|;
name|int
name|bytesPerChecksum
init|=
operator|-
literal|1
decl_stmt|;
name|String
name|usage
init|=
literal|"Usage: nnbench "
operator|+
literal|"  -operation<one of createWrite, openRead, rename, or delete> "
operator|+
literal|"  -baseDir<base output/input DFS path> "
operator|+
literal|"  -startTime<time to start, given in seconds from the epoch> "
operator|+
literal|"  -numFiles<number of files to create> "
operator|+
literal|"  -blocksPerFile<number of blocks to create per file> "
operator|+
literal|"  [-bytesPerBlock<number of bytes to write to each block, default is 1>] "
operator|+
literal|"  [-bytesPerChecksum<value for io.bytes.per.checksum>]"
operator|+
literal|"Note: bytesPerBlock MUST be a multiple of bytesPerChecksum"
decl_stmt|;
name|String
name|operation
init|=
literal|null
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
literal|"-baseDir"
argument_list|)
condition|)
block|{
name|baseDir
operator|=
operator|new
name|Path
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
literal|"-numFiles"
argument_list|)
condition|)
block|{
name|numFiles
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
literal|"-blocksPerFile"
argument_list|)
condition|)
block|{
name|blocksPerFile
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
literal|"-bytesPerBlock"
argument_list|)
condition|)
block|{
name|bytesPerBlock
operator|=
name|Long
operator|.
name|parseLong
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
literal|"-bytesPerChecksum"
argument_list|)
condition|)
block|{
name|bytesPerChecksum
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
literal|"-startTime"
argument_list|)
condition|)
block|{
name|startTime
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|args
index|[
operator|++
name|i
index|]
argument_list|)
operator|*
literal|1000
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
literal|"-operation"
argument_list|)
condition|)
block|{
name|operation
operator|=
name|args
index|[
operator|++
name|i
index|]
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
block|}
name|bytesPerFile
operator|=
name|bytesPerBlock
operator|*
name|blocksPerFile
expr_stmt|;
name|JobConf
name|jobConf
init|=
operator|new
name|JobConf
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|,
name|NNBench
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|bytesPerChecksum
operator|<
literal|0
condition|)
block|{
comment|// if it is not set in cmdline
name|bytesPerChecksum
operator|=
name|jobConf
operator|.
name|getInt
argument_list|(
literal|"io.bytes.per.checksum"
argument_list|,
literal|512
argument_list|)
expr_stmt|;
block|}
name|jobConf
operator|.
name|set
argument_list|(
literal|"io.bytes.per.checksum"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|bytesPerChecksum
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Inputs: "
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"   operation: "
operator|+
name|operation
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"   baseDir: "
operator|+
name|baseDir
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"   startTime: "
operator|+
name|startTime
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"   numFiles: "
operator|+
name|numFiles
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"   blocksPerFile: "
operator|+
name|blocksPerFile
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"   bytesPerBlock: "
operator|+
name|bytesPerBlock
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"   bytesPerChecksum: "
operator|+
name|bytesPerChecksum
argument_list|)
expr_stmt|;
if|if
condition|(
name|operation
operator|==
literal|null
operator|||
comment|// verify args
name|baseDir
operator|==
literal|null
operator|||
name|numFiles
operator|<
literal|1
operator|||
name|blocksPerFile
operator|<
literal|1
operator|||
name|bytesPerBlock
operator|<
literal|0
operator|||
name|bytesPerBlock
operator|%
name|bytesPerChecksum
operator|!=
literal|0
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
name|fileSys
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
name|jobConf
argument_list|)
expr_stmt|;
name|uniqueId
operator|=
name|java
operator|.
name|net
operator|.
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
operator|.
name|getHostName
argument_list|()
expr_stmt|;
name|taskDir
operator|=
operator|new
name|Path
argument_list|(
name|baseDir
argument_list|,
name|uniqueId
argument_list|)
expr_stmt|;
comment|// initialize buffer used for writing/reading file
name|buffer
operator|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|bytesPerFile
argument_list|,
literal|32768L
argument_list|)
index|]
expr_stmt|;
name|Date
name|execTime
decl_stmt|;
name|Date
name|endTime
decl_stmt|;
name|long
name|duration
decl_stmt|;
name|int
name|exceptions
init|=
literal|0
decl_stmt|;
name|barrier
argument_list|()
expr_stmt|;
comment|// wait for coordinated start time
name|execTime
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Job started: "
operator|+
name|startTime
argument_list|)
expr_stmt|;
if|if
condition|(
name|operation
operator|.
name|equals
argument_list|(
literal|"createWrite"
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|fileSys
operator|.
name|mkdirs
argument_list|(
name|taskDir
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Mkdirs failed to create "
operator|+
name|taskDir
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|exceptions
operator|=
name|createWrite
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|operation
operator|.
name|equals
argument_list|(
literal|"openRead"
argument_list|)
condition|)
block|{
name|exceptions
operator|=
name|openRead
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|operation
operator|.
name|equals
argument_list|(
literal|"rename"
argument_list|)
condition|)
block|{
name|exceptions
operator|=
name|rename
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|operation
operator|.
name|equals
argument_list|(
literal|"delete"
argument_list|)
condition|)
block|{
name|exceptions
operator|=
name|delete
argument_list|()
expr_stmt|;
block|}
else|else
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
name|endTime
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Job ended: "
operator|+
name|endTime
argument_list|)
expr_stmt|;
name|duration
operator|=
operator|(
name|endTime
operator|.
name|getTime
argument_list|()
operator|-
name|execTime
operator|.
name|getTime
argument_list|()
operator|)
operator|/
literal|1000
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"The "
operator|+
name|operation
operator|+
literal|" job took "
operator|+
name|duration
operator|+
literal|" seconds."
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"The job recorded "
operator|+
name|exceptions
operator|+
literal|" exceptions."
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

