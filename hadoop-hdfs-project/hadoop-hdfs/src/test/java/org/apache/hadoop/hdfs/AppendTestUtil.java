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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|*
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
name|security
operator|.
name|UserGroupInformation
import|;
end_import

begin_comment
comment|/** Utilities for append-related tests */
end_comment

begin_class
DECL|class|AppendTestUtil
specifier|public
class|class
name|AppendTestUtil
block|{
comment|/** For specifying the random number generator seed,    *  change the following value:    */
DECL|field|RANDOM_NUMBER_GENERATOR_SEED
specifier|static
specifier|final
name|Long
name|RANDOM_NUMBER_GENERATOR_SEED
init|=
literal|null
decl_stmt|;
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|AppendTestUtil
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SEED
specifier|private
specifier|static
specifier|final
name|Random
name|SEED
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
static|static
block|{
specifier|final
name|long
name|seed
init|=
name|RANDOM_NUMBER_GENERATOR_SEED
operator|==
literal|null
condition|?
name|SEED
operator|.
name|nextLong
argument_list|()
else|:
name|RANDOM_NUMBER_GENERATOR_SEED
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"seed="
operator|+
name|seed
argument_list|)
expr_stmt|;
name|SEED
operator|.
name|setSeed
argument_list|(
name|seed
argument_list|)
expr_stmt|;
block|}
DECL|field|RANDOM
specifier|private
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|Random
argument_list|>
name|RANDOM
init|=
operator|new
name|ThreadLocal
argument_list|<
name|Random
argument_list|>
argument_list|()
block|{
specifier|protected
name|Random
name|initialValue
parameter_list|()
block|{
specifier|final
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|SEED
init|)
block|{
specifier|final
name|long
name|seed
init|=
name|SEED
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|r
operator|.
name|setSeed
argument_list|(
name|seed
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": seed="
operator|+
name|seed
argument_list|)
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
block|}
decl_stmt|;
DECL|field|BLOCK_SIZE
specifier|static
specifier|final
name|int
name|BLOCK_SIZE
init|=
literal|1024
decl_stmt|;
DECL|field|NUM_BLOCKS
specifier|static
specifier|final
name|int
name|NUM_BLOCKS
init|=
literal|10
decl_stmt|;
DECL|field|FILE_SIZE
specifier|static
specifier|final
name|int
name|FILE_SIZE
init|=
name|NUM_BLOCKS
operator|*
name|BLOCK_SIZE
operator|+
literal|1
decl_stmt|;
DECL|field|seed
specifier|static
name|long
name|seed
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|nextInt ()
specifier|static
name|int
name|nextInt
parameter_list|()
block|{
return|return
name|RANDOM
operator|.
name|get
argument_list|()
operator|.
name|nextInt
argument_list|()
return|;
block|}
DECL|method|nextInt (int n)
specifier|static
name|int
name|nextInt
parameter_list|(
name|int
name|n
parameter_list|)
block|{
return|return
name|RANDOM
operator|.
name|get
argument_list|()
operator|.
name|nextInt
argument_list|(
name|n
argument_list|)
return|;
block|}
DECL|method|nextLong ()
specifier|static
name|int
name|nextLong
parameter_list|()
block|{
return|return
name|RANDOM
operator|.
name|get
argument_list|()
operator|.
name|nextInt
argument_list|()
return|;
block|}
DECL|method|randomBytes (long seed, int size)
specifier|static
name|byte
index|[]
name|randomBytes
parameter_list|(
name|long
name|seed
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"seed="
operator|+
name|seed
operator|+
literal|", size="
operator|+
name|size
argument_list|)
expr_stmt|;
specifier|final
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
name|size
index|]
decl_stmt|;
specifier|final
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
name|b
argument_list|)
expr_stmt|;
return|return
name|b
return|;
block|}
DECL|method|sleep (long ms)
specifier|static
name|void
name|sleep
parameter_list|(
name|long
name|ms
parameter_list|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|ms
argument_list|)
expr_stmt|;
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
literal|"ms="
operator|+
name|ms
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Returns the reference to a new instance of FileSystem created     * with different user name    * @param conf current Configuration    * @return FileSystem instance    * @throws IOException    * @throws InterruptedException     */
DECL|method|createHdfsWithDifferentUsername (final Configuration conf )
specifier|public
specifier|static
name|FileSystem
name|createHdfsWithDifferentUsername
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|String
name|username
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
operator|+
literal|"_XXX"
decl_stmt|;
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
name|username
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"supergroup"
block|}
argument_list|)
decl_stmt|;
return|return
name|DFSTestUtil
operator|.
name|getFileSystemAs
argument_list|(
name|ugi
argument_list|,
name|conf
argument_list|)
return|;
block|}
DECL|method|write (OutputStream out, int offset, int length)
specifier|public
specifier|static
name|void
name|write
parameter_list|(
name|OutputStream
name|out
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|length
index|]
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
name|length
condition|;
name|i
operator|++
control|)
block|{
name|bytes
index|[
name|i
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|offset
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|write
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
DECL|method|check (FileSystem fs, Path p, long length)
specifier|static
name|void
name|check
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|p
parameter_list|,
name|long
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|i
init|=
operator|-
literal|1
decl_stmt|;
try|try
block|{
specifier|final
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|TestCase
operator|.
name|assertEquals
argument_list|(
name|length
argument_list|,
name|status
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|InputStream
name|in
init|=
name|fs
operator|.
name|open
argument_list|(
name|p
argument_list|)
decl_stmt|;
for|for
control|(
name|i
operator|++
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
name|TestCase
operator|.
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|,
operator|(
name|byte
operator|)
name|in
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|i
operator|=
operator|-
operator|(
name|int
operator|)
name|length
expr_stmt|;
name|TestCase
operator|.
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|in
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
comment|//EOF
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
throw|throw
operator|new
name|IOException
argument_list|(
literal|"p="
operator|+
name|p
operator|+
literal|", length="
operator|+
name|length
operator|+
literal|", i="
operator|+
name|i
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
comment|/**    *  create a buffer that contains the entire test file data.    */
DECL|method|initBuffer (int size)
specifier|static
name|byte
index|[]
name|initBuffer
parameter_list|(
name|int
name|size
parameter_list|)
block|{
if|if
condition|(
name|seed
operator|==
operator|-
literal|1
condition|)
name|seed
operator|=
name|nextLong
argument_list|()
expr_stmt|;
return|return
name|randomBytes
argument_list|(
name|seed
argument_list|,
name|size
argument_list|)
return|;
block|}
comment|/**    *  Creates a file but does not close it    *  Make sure to call close() on the returned stream    *  @throws IOException an exception might be thrown    */
DECL|method|createFile (FileSystem fileSys, Path name, int repl)
specifier|public
specifier|static
name|FSDataOutputStream
name|createFile
parameter_list|(
name|FileSystem
name|fileSys
parameter_list|,
name|Path
name|name
parameter_list|,
name|int
name|repl
parameter_list|)
throws|throws
name|IOException
block|{
return|return
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
name|repl
argument_list|,
name|BLOCK_SIZE
argument_list|)
return|;
block|}
comment|/**    *  Compare the content of a file created from FileSystem and Path with    *  the specified byte[] buffer's content    *  @throws IOException an exception might be thrown    */
DECL|method|checkFullFile (FileSystem fs, Path name, int len, final byte[] compareContent, String message)
specifier|public
specifier|static
name|void
name|checkFullFile
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|name
parameter_list|,
name|int
name|len
parameter_list|,
specifier|final
name|byte
index|[]
name|compareContent
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|IOException
block|{
name|FSDataInputStream
name|stm
init|=
name|fs
operator|.
name|open
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|byte
index|[]
name|actual
init|=
operator|new
name|byte
index|[
name|len
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
name|compareContent
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|checkData (final byte[] actual, int from, final byte[] expected, String message)
specifier|private
specifier|static
name|void
name|checkData
parameter_list|(
specifier|final
name|byte
index|[]
name|actual
parameter_list|,
name|int
name|from
parameter_list|,
specifier|final
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
name|Assert
operator|.
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
name|expected
index|[
name|from
operator|+
name|idx
index|]
argument_list|,
name|actual
index|[
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
block|}
end_class

end_unit

