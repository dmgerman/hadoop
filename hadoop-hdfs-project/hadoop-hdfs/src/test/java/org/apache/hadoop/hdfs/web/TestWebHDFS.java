begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.web
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|web
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
name|commons
operator|.
name|logging
operator|.
name|impl
operator|.
name|Log4JLogger
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
name|hdfs
operator|.
name|TestDFSClientRetries
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
name|web
operator|.
name|resources
operator|.
name|NamenodeWebHdfsMethods
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
comment|/** Test WebHDFS */
end_comment

begin_class
DECL|class|TestWebHDFS
specifier|public
class|class
name|TestWebHDFS
block|{
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
name|TestWebHDFS
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|RANDOM
specifier|static
specifier|final
name|Random
name|RANDOM
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|systemStartTime
specifier|static
specifier|final
name|long
name|systemStartTime
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
comment|/** A timer for measuring performance. */
DECL|class|Ticker
specifier|static
class|class
name|Ticker
block|{
DECL|field|name
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|startTime
specifier|final
name|long
name|startTime
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
DECL|field|previousTick
specifier|private
name|long
name|previousTick
init|=
name|startTime
decl_stmt|;
DECL|method|Ticker (final String name, String format, Object... args)
name|Ticker
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
name|String
name|format
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"\n\n%s START: %s\n"
argument_list|,
name|name
argument_list|,
name|String
operator|.
name|format
argument_list|(
name|format
argument_list|,
name|args
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|tick (final long nBytes, String format, Object... args)
name|void
name|tick
parameter_list|(
specifier|final
name|long
name|nBytes
parameter_list|,
name|String
name|format
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
specifier|final
name|long
name|now
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|now
operator|-
name|previousTick
operator|>
literal|10000000000L
condition|)
block|{
name|previousTick
operator|=
name|now
expr_stmt|;
specifier|final
name|double
name|mintues
init|=
operator|(
name|now
operator|-
name|systemStartTime
operator|)
operator|/
literal|60000000000.0
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"\n\n%s %.2f min) %s %s\n"
argument_list|,
name|name
argument_list|,
name|mintues
argument_list|,
name|String
operator|.
name|format
argument_list|(
name|format
argument_list|,
name|args
argument_list|)
argument_list|,
name|toMpsString
argument_list|(
name|nBytes
argument_list|,
name|now
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|end (final long nBytes)
name|void
name|end
parameter_list|(
specifier|final
name|long
name|nBytes
parameter_list|)
block|{
specifier|final
name|long
name|now
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
specifier|final
name|double
name|seconds
init|=
operator|(
name|now
operator|-
name|startTime
operator|)
operator|/
literal|1000000000.0
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"\n\n%s END: duration=%.2fs %s\n"
argument_list|,
name|name
argument_list|,
name|seconds
argument_list|,
name|toMpsString
argument_list|(
name|nBytes
argument_list|,
name|now
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|toMpsString (final long nBytes, final long now)
name|String
name|toMpsString
parameter_list|(
specifier|final
name|long
name|nBytes
parameter_list|,
specifier|final
name|long
name|now
parameter_list|)
block|{
specifier|final
name|double
name|mb
init|=
name|nBytes
operator|/
call|(
name|double
call|)
argument_list|(
literal|1
operator|<<
literal|20
argument_list|)
decl_stmt|;
specifier|final
name|double
name|mps
init|=
name|mb
operator|*
literal|1000000000.0
operator|/
operator|(
name|now
operator|-
name|startTime
operator|)
decl_stmt|;
return|return
name|String
operator|.
name|format
argument_list|(
literal|"[nBytes=%.2fMB, speed=%.2fMB/s]"
argument_list|,
name|mb
argument_list|,
name|mps
argument_list|)
return|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testLargeFile ()
specifier|public
name|void
name|testLargeFile
parameter_list|()
throws|throws
name|Exception
block|{
name|largeFileTest
argument_list|(
literal|200L
operator|<<
literal|20
argument_list|)
expr_stmt|;
comment|//200MB file length
block|}
comment|/** Test read and write large files. */
DECL|method|largeFileTest (final long fileLength)
specifier|static
name|void
name|largeFileTest
parameter_list|(
specifier|final
name|long
name|fileLength
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|Configuration
name|conf
init|=
name|WebHdfsTestUtil
operator|.
name|createConf
argument_list|()
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
literal|3
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
name|FileSystem
name|fs
init|=
name|WebHdfsTestUtil
operator|.
name|getWebHdfsFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
literal|"/test/largeFile"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|fs
operator|.
name|mkdirs
argument_list|(
name|dir
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
literal|1
operator|<<
literal|20
index|]
decl_stmt|;
name|RANDOM
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
specifier|final
name|byte
index|[]
name|expected
init|=
operator|new
name|byte
index|[
literal|2
operator|*
name|data
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|expected
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|expected
argument_list|,
name|data
operator|.
name|length
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"file"
argument_list|)
decl_stmt|;
specifier|final
name|Ticker
name|t
init|=
operator|new
name|Ticker
argument_list|(
literal|"WRITE"
argument_list|,
literal|"fileLength="
operator|+
name|fileLength
argument_list|)
decl_stmt|;
specifier|final
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|p
argument_list|)
decl_stmt|;
try|try
block|{
name|long
name|remaining
init|=
name|fileLength
decl_stmt|;
for|for
control|(
init|;
name|remaining
operator|>
literal|0
condition|;
control|)
block|{
name|t
operator|.
name|tick
argument_list|(
name|fileLength
operator|-
name|remaining
argument_list|,
literal|"remaining=%d"
argument_list|,
name|remaining
argument_list|)
expr_stmt|;
specifier|final
name|int
name|n
init|=
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
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|remaining
operator|-=
name|n
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|t
operator|.
name|end
argument_list|(
name|fileLength
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|fileLength
argument_list|,
name|fs
operator|.
name|getFileStatus
argument_list|(
name|p
argument_list|)
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|long
name|smallOffset
init|=
name|RANDOM
operator|.
name|nextInt
argument_list|(
literal|1
operator|<<
literal|20
argument_list|)
operator|+
operator|(
literal|1
operator|<<
literal|20
operator|)
decl_stmt|;
specifier|final
name|long
name|largeOffset
init|=
name|fileLength
operator|-
name|smallOffset
decl_stmt|;
specifier|final
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|data
operator|.
name|length
index|]
decl_stmt|;
name|verifySeek
argument_list|(
name|fs
argument_list|,
name|p
argument_list|,
name|largeOffset
argument_list|,
name|fileLength
argument_list|,
name|buf
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|verifySeek
argument_list|(
name|fs
argument_list|,
name|p
argument_list|,
name|smallOffset
argument_list|,
name|fileLength
argument_list|,
name|buf
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|verifyPread
argument_list|(
name|fs
argument_list|,
name|p
argument_list|,
name|largeOffset
argument_list|,
name|fileLength
argument_list|,
name|buf
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|checkData (long offset, long remaining, int n, byte[] actual, byte[] expected)
specifier|static
name|void
name|checkData
parameter_list|(
name|long
name|offset
parameter_list|,
name|long
name|remaining
parameter_list|,
name|int
name|n
parameter_list|,
name|byte
index|[]
name|actual
parameter_list|,
name|byte
index|[]
name|expected
parameter_list|)
block|{
if|if
condition|(
name|RANDOM
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|==
literal|0
condition|)
block|{
name|int
name|j
init|=
call|(
name|int
call|)
argument_list|(
name|offset
operator|%
name|actual
operator|.
name|length
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
name|n
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|expected
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
name|Assert
operator|.
name|fail
argument_list|(
literal|"expected["
operator|+
name|j
operator|+
literal|"]="
operator|+
name|expected
index|[
name|j
index|]
operator|+
literal|" != actual["
operator|+
name|i
operator|+
literal|"]="
operator|+
name|actual
index|[
name|i
index|]
operator|+
literal|", offset="
operator|+
name|offset
operator|+
literal|", remaining="
operator|+
name|remaining
operator|+
literal|", n="
operator|+
name|n
argument_list|)
expr_stmt|;
block|}
name|j
operator|++
expr_stmt|;
block|}
block|}
block|}
comment|/** test seek */
DECL|method|verifySeek (FileSystem fs, Path p, long offset, long length, byte[] buf, byte[] expected)
specifier|static
name|void
name|verifySeek
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|p
parameter_list|,
name|long
name|offset
parameter_list|,
name|long
name|length
parameter_list|,
name|byte
index|[]
name|buf
parameter_list|,
name|byte
index|[]
name|expected
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|remaining
init|=
name|length
operator|-
name|offset
decl_stmt|;
name|long
name|checked
init|=
literal|0
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"XXX SEEK: offset="
operator|+
name|offset
operator|+
literal|", remaining="
operator|+
name|remaining
argument_list|)
expr_stmt|;
specifier|final
name|Ticker
name|t
init|=
operator|new
name|Ticker
argument_list|(
literal|"SEEK"
argument_list|,
literal|"offset=%d, remaining=%d"
argument_list|,
name|offset
argument_list|,
name|remaining
argument_list|)
decl_stmt|;
specifier|final
name|FSDataInputStream
name|in
init|=
name|fs
operator|.
name|open
argument_list|(
name|p
argument_list|,
literal|64
operator|<<
literal|10
argument_list|)
decl_stmt|;
name|in
operator|.
name|seek
argument_list|(
name|offset
argument_list|)
expr_stmt|;
for|for
control|(
init|;
name|remaining
operator|>
literal|0
condition|;
control|)
block|{
name|t
operator|.
name|tick
argument_list|(
name|checked
argument_list|,
literal|"offset=%d, remaining=%d"
argument_list|,
name|offset
argument_list|,
name|remaining
argument_list|)
expr_stmt|;
specifier|final
name|int
name|n
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|remaining
argument_list|,
name|buf
operator|.
name|length
argument_list|)
decl_stmt|;
name|in
operator|.
name|readFully
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|checkData
argument_list|(
name|offset
argument_list|,
name|remaining
argument_list|,
name|n
argument_list|,
name|buf
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|n
expr_stmt|;
name|remaining
operator|-=
name|n
expr_stmt|;
name|checked
operator|+=
name|n
expr_stmt|;
block|}
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|t
operator|.
name|end
argument_list|(
name|checked
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyPread (FileSystem fs, Path p, long offset, long length, byte[] buf, byte[] expected)
specifier|static
name|void
name|verifyPread
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|p
parameter_list|,
name|long
name|offset
parameter_list|,
name|long
name|length
parameter_list|,
name|byte
index|[]
name|buf
parameter_list|,
name|byte
index|[]
name|expected
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|remaining
init|=
name|length
operator|-
name|offset
decl_stmt|;
name|long
name|checked
init|=
literal|0
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"XXX PREAD: offset="
operator|+
name|offset
operator|+
literal|", remaining="
operator|+
name|remaining
argument_list|)
expr_stmt|;
specifier|final
name|Ticker
name|t
init|=
operator|new
name|Ticker
argument_list|(
literal|"PREAD"
argument_list|,
literal|"offset=%d, remaining=%d"
argument_list|,
name|offset
argument_list|,
name|remaining
argument_list|)
decl_stmt|;
specifier|final
name|FSDataInputStream
name|in
init|=
name|fs
operator|.
name|open
argument_list|(
name|p
argument_list|,
literal|64
operator|<<
literal|10
argument_list|)
decl_stmt|;
for|for
control|(
init|;
name|remaining
operator|>
literal|0
condition|;
control|)
block|{
name|t
operator|.
name|tick
argument_list|(
name|checked
argument_list|,
literal|"offset=%d, remaining=%d"
argument_list|,
name|offset
argument_list|,
name|remaining
argument_list|)
expr_stmt|;
specifier|final
name|int
name|n
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|remaining
argument_list|,
name|buf
operator|.
name|length
argument_list|)
decl_stmt|;
name|in
operator|.
name|readFully
argument_list|(
name|offset
argument_list|,
name|buf
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|checkData
argument_list|(
name|offset
argument_list|,
name|remaining
argument_list|,
name|n
argument_list|,
name|buf
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|n
expr_stmt|;
name|remaining
operator|-=
name|n
expr_stmt|;
name|checked
operator|+=
name|n
expr_stmt|;
block|}
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|t
operator|.
name|end
argument_list|(
name|checked
argument_list|)
expr_stmt|;
block|}
comment|/** Test client retry with namenode restarting. */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testNamenodeRestart ()
specifier|public
name|void
name|testNamenodeRestart
parameter_list|()
throws|throws
name|Exception
block|{
operator|(
operator|(
name|Log4JLogger
operator|)
name|NamenodeWebHdfsMethods
operator|.
name|LOG
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
specifier|final
name|Configuration
name|conf
init|=
name|WebHdfsTestUtil
operator|.
name|createConf
argument_list|()
decl_stmt|;
name|TestDFSClientRetries
operator|.
name|namenodeRestartTest
argument_list|(
name|conf
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

