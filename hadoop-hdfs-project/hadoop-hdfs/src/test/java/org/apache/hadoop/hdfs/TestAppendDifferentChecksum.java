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
comment|/**  * Test cases for trying to append to a file with a different  * checksum than the file was originally written with.  */
end_comment

begin_class
DECL|class|TestAppendDifferentChecksum
specifier|public
class|class
name|TestAppendDifferentChecksum
block|{
DECL|field|SEGMENT_LENGTH
specifier|private
specifier|static
specifier|final
name|int
name|SEGMENT_LENGTH
init|=
literal|1500
decl_stmt|;
comment|// run the randomized test for 5 seconds
DECL|field|RANDOM_TEST_RUNTIME
specifier|private
specifier|static
specifier|final
name|long
name|RANDOM_TEST_RUNTIME
init|=
literal|5000
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|fs
specifier|private
specifier|static
name|FileSystem
name|fs
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setupCluster ()
specifier|public
specifier|static
name|void
name|setupCluster
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
name|DFS_BLOCK_SIZE_KEY
argument_list|,
literal|4096
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"fs.hdfs.impl.disable.cache"
argument_list|,
literal|"true"
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
literal|1
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|teardown ()
specifier|public
specifier|static
name|void
name|teardown
parameter_list|()
throws|throws
name|IOException
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
comment|/**    * This test does not run, since switching chunksize with append    * is not implemented. Please see HDFS-2130 for a discussion of the    * difficulties in doing so.    */
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"this is not implemented! See HDFS-2130"
argument_list|)
DECL|method|testSwitchChunkSize ()
specifier|public
name|void
name|testSwitchChunkSize
parameter_list|()
throws|throws
name|IOException
block|{
name|FileSystem
name|fsWithSmallChunk
init|=
name|createFsWithChecksum
argument_list|(
literal|"CRC32"
argument_list|,
literal|512
argument_list|)
decl_stmt|;
name|FileSystem
name|fsWithBigChunk
init|=
name|createFsWithChecksum
argument_list|(
literal|"CRC32"
argument_list|,
literal|1024
argument_list|)
decl_stmt|;
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
literal|"/testSwitchChunkSize"
argument_list|)
decl_stmt|;
name|appendWithTwoFs
argument_list|(
name|p
argument_list|,
name|fsWithSmallChunk
argument_list|,
name|fsWithBigChunk
argument_list|)
expr_stmt|;
name|AppendTestUtil
operator|.
name|check
argument_list|(
name|fsWithSmallChunk
argument_list|,
name|p
argument_list|,
name|SEGMENT_LENGTH
operator|*
literal|2
argument_list|)
expr_stmt|;
name|AppendTestUtil
operator|.
name|check
argument_list|(
name|fsWithBigChunk
argument_list|,
name|p
argument_list|,
name|SEGMENT_LENGTH
operator|*
literal|2
argument_list|)
expr_stmt|;
block|}
comment|/**    * Simple unit test which writes some data with one algorithm,    * then appends with another.    */
annotation|@
name|Test
DECL|method|testSwitchAlgorithms ()
specifier|public
name|void
name|testSwitchAlgorithms
parameter_list|()
throws|throws
name|IOException
block|{
name|FileSystem
name|fsWithCrc32
init|=
name|createFsWithChecksum
argument_list|(
literal|"CRC32"
argument_list|,
literal|512
argument_list|)
decl_stmt|;
name|FileSystem
name|fsWithCrc32C
init|=
name|createFsWithChecksum
argument_list|(
literal|"CRC32C"
argument_list|,
literal|512
argument_list|)
decl_stmt|;
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
literal|"/testSwitchAlgorithms"
argument_list|)
decl_stmt|;
name|appendWithTwoFs
argument_list|(
name|p
argument_list|,
name|fsWithCrc32
argument_list|,
name|fsWithCrc32C
argument_list|)
expr_stmt|;
comment|// Regardless of which FS is used to read, it should pick up
comment|// the on-disk checksum!
name|AppendTestUtil
operator|.
name|check
argument_list|(
name|fsWithCrc32C
argument_list|,
name|p
argument_list|,
name|SEGMENT_LENGTH
operator|*
literal|2
argument_list|)
expr_stmt|;
name|AppendTestUtil
operator|.
name|check
argument_list|(
name|fsWithCrc32
argument_list|,
name|p
argument_list|,
name|SEGMENT_LENGTH
operator|*
literal|2
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test which randomly alternates between appending with    * CRC32 and with CRC32C, crossing several block boundaries.    * Then, checks that all of the data can be read back correct.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|RANDOM_TEST_RUNTIME
operator|*
literal|2
argument_list|)
DECL|method|testAlgoSwitchRandomized ()
specifier|public
name|void
name|testAlgoSwitchRandomized
parameter_list|()
throws|throws
name|IOException
block|{
name|FileSystem
name|fsWithCrc32
init|=
name|createFsWithChecksum
argument_list|(
literal|"CRC32"
argument_list|,
literal|512
argument_list|)
decl_stmt|;
name|FileSystem
name|fsWithCrc32C
init|=
name|createFsWithChecksum
argument_list|(
literal|"CRC32C"
argument_list|,
literal|512
argument_list|)
decl_stmt|;
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
literal|"/testAlgoSwitchRandomized"
argument_list|)
decl_stmt|;
name|long
name|seed
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"seed: "
operator|+
name|seed
argument_list|)
expr_stmt|;
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
comment|// Create empty to start
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|fsWithCrc32
operator|.
name|create
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|st
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
name|int
name|len
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|Time
operator|.
name|now
argument_list|()
operator|-
name|st
operator|<
name|RANDOM_TEST_RUNTIME
condition|)
block|{
name|int
name|thisLen
init|=
name|r
operator|.
name|nextInt
argument_list|(
literal|500
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
operator|(
name|r
operator|.
name|nextBoolean
argument_list|()
condition|?
name|fsWithCrc32
else|:
name|fsWithCrc32C
operator|)
decl_stmt|;
name|FSDataOutputStream
name|stm
init|=
name|fs
operator|.
name|append
argument_list|(
name|p
argument_list|)
decl_stmt|;
try|try
block|{
name|AppendTestUtil
operator|.
name|write
argument_list|(
name|stm
argument_list|,
name|len
argument_list|,
name|thisLen
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|len
operator|+=
name|thisLen
expr_stmt|;
block|}
name|AppendTestUtil
operator|.
name|check
argument_list|(
name|fsWithCrc32
argument_list|,
name|p
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|AppendTestUtil
operator|.
name|check
argument_list|(
name|fsWithCrc32C
argument_list|,
name|p
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
DECL|method|createFsWithChecksum (String type, int bytes)
specifier|private
name|FileSystem
name|createFsWithChecksum
parameter_list|(
name|String
name|type
parameter_list|,
name|int
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
name|fs
operator|.
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_CHECKSUM_TYPE_KEY
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BYTES_PER_CHECKSUM_KEY
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
return|return
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
return|;
block|}
DECL|method|appendWithTwoFs (Path p, FileSystem fs1, FileSystem fs2)
specifier|private
name|void
name|appendWithTwoFs
parameter_list|(
name|Path
name|p
parameter_list|,
name|FileSystem
name|fs1
parameter_list|,
name|FileSystem
name|fs2
parameter_list|)
throws|throws
name|IOException
block|{
name|FSDataOutputStream
name|stm
init|=
name|fs1
operator|.
name|create
argument_list|(
name|p
argument_list|)
decl_stmt|;
try|try
block|{
name|AppendTestUtil
operator|.
name|write
argument_list|(
name|stm
argument_list|,
literal|0
argument_list|,
name|SEGMENT_LENGTH
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|stm
operator|=
name|fs2
operator|.
name|append
argument_list|(
name|p
argument_list|)
expr_stmt|;
try|try
block|{
name|AppendTestUtil
operator|.
name|write
argument_list|(
name|stm
argument_list|,
name|SEGMENT_LENGTH
argument_list|,
name|SEGMENT_LENGTH
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

