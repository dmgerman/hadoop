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
name|File
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
name|RandomAccessFile
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
name|nio
operator|.
name|channels
operator|.
name|FileChannel
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
name|Block
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
name|ExtendedBlock
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
name|junit
operator|.
name|Before
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

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_comment
comment|/**  * A JUnit test for corrupted file handling.  * This test creates a bunch of files/directories with replication   * factor of 2. Then verifies that a client can automatically   * access the remaining valid replica inspite of the following   * types of simulated errors:  *  *  1. Delete meta file on one replica  *  2. Truncates meta file on one replica  *  3. Corrupts the meta file header on one replica  *  4. Corrupts any random offset and portion of the meta file  *  5. Swaps two meta files, i.e the format of the meta files   *     are valid but their CRCs do not match with their corresponding   *     data blocks  * The above tests are run for varied values of dfs.bytes-per-checksum   * and dfs.blocksize. It tests for the case when the meta file is   * multiple blocks.  *  * Another portion of the test is commented out till HADOOP-1557   * is addressed:  *  1. Create file with 2 replica, corrupt the meta file of replica,   *     decrease replication factor from 2 to 1. Validate that the   *     remaining replica is the good one.  *  2. Create file with 2 replica, corrupt the meta file of one replica,   *     increase replication factor of file to 3. verify that the new   *     replica was created from the non-corrupted replica.  */
end_comment

begin_class
DECL|class|TestCrcCorruption
specifier|public
class|class
name|TestCrcCorruption
block|{
DECL|field|faultInjector
specifier|private
name|DFSClientFaultInjector
name|faultInjector
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|faultInjector
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|DFSClientFaultInjector
operator|.
name|class
argument_list|)
expr_stmt|;
name|DFSClientFaultInjector
operator|.
name|instance
operator|=
name|faultInjector
expr_stmt|;
block|}
comment|/**     * Test case for data corruption during data transmission for    * create/write. To recover from corruption while writing, at    * least two replicas are needed.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|50000
argument_list|)
DECL|method|testCorruptionDuringWrt ()
specifier|public
name|void
name|testCorruptionDuringWrt
parameter_list|()
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
comment|// Set short retry timeouts so this test runs faster
name|conf
operator|.
name|setInt
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|Retry
operator|.
name|WINDOW_BASE_KEY
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
try|try
block|{
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
literal|10
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
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
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
literal|"/test_corruption_file"
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|file
argument_list|,
literal|true
argument_list|,
literal|8192
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|,
call|(
name|long
call|)
argument_list|(
literal|128
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
literal|65536
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
literal|65536
condition|;
name|i
operator|++
control|)
block|{
name|data
index|[
name|i
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|i
operator|%
literal|256
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|write
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
literal|65535
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|hflush
argument_list|()
expr_stmt|;
comment|// corrupt the packet once
name|Mockito
operator|.
name|when
argument_list|(
name|faultInjector
operator|.
name|corruptPacket
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|faultInjector
operator|.
name|uncorruptPacket
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|,
literal|false
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|write
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
literal|65535
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// read should succeed
name|FSDataInputStream
name|in
init|=
name|fs
operator|.
name|open
argument_list|(
name|file
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|c
init|;
operator|(
name|c
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
control|)
empty_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// test the retry limit
name|out
operator|=
name|fs
operator|.
name|create
argument_list|(
name|file
argument_list|,
literal|true
argument_list|,
literal|8192
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|,
call|(
name|long
call|)
argument_list|(
literal|128
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
argument_list|)
expr_stmt|;
comment|// corrupt the packet once and never fix it.
name|Mockito
operator|.
name|when
argument_list|(
name|faultInjector
operator|.
name|corruptPacket
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|faultInjector
operator|.
name|uncorruptPacket
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// the client should give up pipeline reconstruction after retries.
try|try
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|write
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
literal|65535
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Write did not fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// we should get an ioe
name|DFSClient
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"Got expected exception"
argument_list|,
name|ioe
argument_list|)
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
name|Mockito
operator|.
name|when
argument_list|(
name|faultInjector
operator|.
name|corruptPacket
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|faultInjector
operator|.
name|uncorruptPacket
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**     * check if DFS can handle corrupted CRC blocks    */
DECL|method|thistest (Configuration conf, DFSTestUtil util)
specifier|private
name|void
name|thistest
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|DFSTestUtil
name|util
parameter_list|)
throws|throws
name|Exception
block|{
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
name|int
name|numDataNodes
init|=
literal|2
decl_stmt|;
name|short
name|replFactor
init|=
literal|2
decl_stmt|;
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
comment|// Set short retry timeouts so this test runs faster
name|conf
operator|.
name|setInt
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|Retry
operator|.
name|WINDOW_BASE_KEY
argument_list|,
literal|10
argument_list|)
expr_stmt|;
try|try
block|{
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
name|numDataNodes
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
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
name|util
operator|.
name|createFiles
argument_list|(
name|fs
argument_list|,
literal|"/srcdat"
argument_list|,
name|replFactor
argument_list|)
expr_stmt|;
name|util
operator|.
name|waitReplication
argument_list|(
name|fs
argument_list|,
literal|"/srcdat"
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|)
expr_stmt|;
comment|// Now deliberately remove/truncate meta blocks from the first
comment|// directory of the first datanode. The complete absense of a meta
comment|// file disallows this Datanode to send data to another datanode.
comment|// However, a client is alowed access to this block.
comment|//
name|File
name|storageDir
init|=
name|cluster
operator|.
name|getInstanceStorageDir
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|String
name|bpid
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
decl_stmt|;
name|File
name|data_dir
init|=
name|MiniDFSCluster
operator|.
name|getFinalizedDir
argument_list|(
name|storageDir
argument_list|,
name|bpid
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"data directory does not exist"
argument_list|,
name|data_dir
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|File
index|[]
name|blocks
init|=
name|data_dir
operator|.
name|listFiles
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Blocks do not exist in data-dir"
argument_list|,
operator|(
name|blocks
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|blocks
operator|.
name|length
operator|>
literal|0
operator|)
argument_list|)
expr_stmt|;
name|int
name|num
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|blocks
operator|.
name|length
condition|;
name|idx
operator|++
control|)
block|{
if|if
condition|(
name|blocks
index|[
name|idx
index|]
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|Block
operator|.
name|BLOCK_FILE_PREFIX
argument_list|)
operator|&&
name|blocks
index|[
name|idx
index|]
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".meta"
argument_list|)
condition|)
block|{
name|num
operator|++
expr_stmt|;
if|if
condition|(
name|num
operator|%
literal|3
operator|==
literal|0
condition|)
block|{
comment|//
comment|// remove .meta file
comment|//
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Deliberately removing file "
operator|+
name|blocks
index|[
name|idx
index|]
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Cannot remove file."
argument_list|,
name|blocks
index|[
name|idx
index|]
operator|.
name|delete
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|num
operator|%
literal|3
operator|==
literal|1
condition|)
block|{
comment|//
comment|// shorten .meta file
comment|//
name|RandomAccessFile
name|file
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|blocks
index|[
name|idx
index|]
argument_list|,
literal|"rw"
argument_list|)
decl_stmt|;
name|FileChannel
name|channel
init|=
name|file
operator|.
name|getChannel
argument_list|()
decl_stmt|;
name|int
name|newsize
init|=
name|random
operator|.
name|nextInt
argument_list|(
operator|(
name|int
operator|)
name|channel
operator|.
name|size
argument_list|()
operator|/
literal|2
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Deliberately truncating file "
operator|+
name|blocks
index|[
name|idx
index|]
operator|.
name|getName
argument_list|()
operator|+
literal|" to size "
operator|+
name|newsize
operator|+
literal|" bytes."
argument_list|)
expr_stmt|;
name|channel
operator|.
name|truncate
argument_list|(
name|newsize
argument_list|)
expr_stmt|;
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|//
comment|// corrupt a few bytes of the metafile
comment|//
name|RandomAccessFile
name|file
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|blocks
index|[
name|idx
index|]
argument_list|,
literal|"rw"
argument_list|)
decl_stmt|;
name|FileChannel
name|channel
init|=
name|file
operator|.
name|getChannel
argument_list|()
decl_stmt|;
name|long
name|position
init|=
literal|0
decl_stmt|;
comment|//
comment|// The very first time, corrupt the meta header at offset 0
comment|//
if|if
condition|(
name|num
operator|!=
literal|2
condition|)
block|{
name|position
operator|=
operator|(
name|long
operator|)
name|random
operator|.
name|nextInt
argument_list|(
operator|(
name|int
operator|)
name|channel
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|int
name|length
init|=
name|random
operator|.
name|nextInt
argument_list|(
call|(
name|int
call|)
argument_list|(
name|channel
operator|.
name|size
argument_list|()
operator|-
name|position
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|random
operator|.
name|nextBytes
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|channel
operator|.
name|write
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|buffer
argument_list|)
argument_list|,
name|position
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Deliberately corrupting file "
operator|+
name|blocks
index|[
name|idx
index|]
operator|.
name|getName
argument_list|()
operator|+
literal|" at offset "
operator|+
name|position
operator|+
literal|" length "
operator|+
name|length
argument_list|)
expr_stmt|;
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|//
comment|// Now deliberately corrupt all meta blocks from the second
comment|// directory of the first datanode
comment|//
name|storageDir
operator|=
name|cluster
operator|.
name|getInstanceStorageDir
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|data_dir
operator|=
name|MiniDFSCluster
operator|.
name|getFinalizedDir
argument_list|(
name|storageDir
argument_list|,
name|bpid
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"data directory does not exist"
argument_list|,
name|data_dir
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|blocks
operator|=
name|data_dir
operator|.
name|listFiles
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Blocks do not exist in data-dir"
argument_list|,
operator|(
name|blocks
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|blocks
operator|.
name|length
operator|>
literal|0
operator|)
argument_list|)
expr_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|File
name|previous
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|blocks
operator|.
name|length
condition|;
name|idx
operator|++
control|)
block|{
if|if
condition|(
name|blocks
index|[
name|idx
index|]
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"blk_"
argument_list|)
operator|&&
name|blocks
index|[
name|idx
index|]
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".meta"
argument_list|)
condition|)
block|{
comment|//
comment|// Move the previous metafile into the current one.
comment|//
name|count
operator|++
expr_stmt|;
if|if
condition|(
name|count
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Deliberately insertimg bad crc into files "
operator|+
name|blocks
index|[
name|idx
index|]
operator|.
name|getName
argument_list|()
operator|+
literal|" "
operator|+
name|previous
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Cannot remove file."
argument_list|,
name|blocks
index|[
name|idx
index|]
operator|.
name|delete
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Cannot corrupt meta file."
argument_list|,
name|previous
operator|.
name|renameTo
argument_list|(
name|blocks
index|[
name|idx
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Cannot recreate empty meta file."
argument_list|,
name|previous
operator|.
name|createNewFile
argument_list|()
argument_list|)
expr_stmt|;
name|previous
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|previous
operator|=
name|blocks
index|[
name|idx
index|]
expr_stmt|;
block|}
block|}
block|}
comment|//
comment|// Only one replica is possibly corrupted. The other replica should still
comment|// be good. Verify.
comment|//
name|assertTrue
argument_list|(
literal|"Corrupted replicas not handled properly."
argument_list|,
name|util
operator|.
name|checkFiles
argument_list|(
name|fs
argument_list|,
literal|"/srcdat"
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"All File still have a valid replica"
argument_list|)
expr_stmt|;
comment|//
comment|// set replication factor back to 1. This causes only one replica of
comment|// of each block to remain in HDFS. The check is to make sure that
comment|// the corrupted replica generated above is the one that gets deleted.
comment|// This test is currently disabled until HADOOP-1557 is solved.
comment|//
name|util
operator|.
name|setReplication
argument_list|(
name|fs
argument_list|,
literal|"/srcdat"
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
comment|//util.waitReplication(fs, "/srcdat", (short)1);
comment|//System.out.println("All Files done with removing replicas");
comment|//assertTrue("Excess replicas deleted. Corrupted replicas found.",
comment|//           util.checkFiles(fs, "/srcdat"));
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"The excess-corrupted-replica test is disabled "
operator|+
literal|" pending HADOOP-1557"
argument_list|)
expr_stmt|;
name|util
operator|.
name|cleanup
argument_list|(
name|fs
argument_list|,
literal|"/srcdat"
argument_list|)
expr_stmt|;
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
DECL|method|testCrcCorruption ()
specifier|public
name|void
name|testCrcCorruption
parameter_list|()
throws|throws
name|Exception
block|{
comment|//
comment|// default parameters
comment|//
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TestCrcCorruption with default parameters"
argument_list|)
expr_stmt|;
name|Configuration
name|conf1
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf1
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCKREPORT_INTERVAL_MSEC_KEY
argument_list|,
literal|3
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|DFSTestUtil
name|util1
init|=
operator|new
name|DFSTestUtil
operator|.
name|Builder
argument_list|()
operator|.
name|setName
argument_list|(
literal|"TestCrcCorruption"
argument_list|)
operator|.
name|setNumFiles
argument_list|(
literal|40
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|thistest
argument_list|(
name|conf1
argument_list|,
name|util1
argument_list|)
expr_stmt|;
comment|//
comment|// specific parameters
comment|//
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TestCrcCorruption with specific parameters"
argument_list|)
expr_stmt|;
name|Configuration
name|conf2
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf2
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BYTES_PER_CHECKSUM_KEY
argument_list|,
literal|17
argument_list|)
expr_stmt|;
name|conf2
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
literal|34
argument_list|)
expr_stmt|;
name|DFSTestUtil
name|util2
init|=
operator|new
name|DFSTestUtil
operator|.
name|Builder
argument_list|()
operator|.
name|setName
argument_list|(
literal|"TestCrcCorruption"
argument_list|)
operator|.
name|setNumFiles
argument_list|(
literal|40
argument_list|)
operator|.
name|setMaxSize
argument_list|(
literal|400
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|thistest
argument_list|(
name|conf2
argument_list|,
name|util2
argument_list|)
expr_stmt|;
block|}
comment|/**    * Make a single-DN cluster, corrupt a block, and make sure    * there's no infinite loop, but rather it eventually    * reports the exception to the client.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
comment|// 5 min timeout
DECL|method|testEntirelyCorruptFileOneNode ()
specifier|public
name|void
name|testEntirelyCorruptFileOneNode
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestEntirelyCorruptFile
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Same thing with multiple datanodes - in history, this has    * behaved differently than the above.    *    * This test usually completes in around 15 seconds - if it    * times out, this suggests that the client is retrying    * indefinitely.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
comment|// 5 min timeout
DECL|method|testEntirelyCorruptFileThreeNodes ()
specifier|public
name|void
name|testEntirelyCorruptFileThreeNodes
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestEntirelyCorruptFile
argument_list|(
literal|3
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestEntirelyCorruptFile (int numDataNodes)
specifier|private
name|void
name|doTestEntirelyCorruptFile
parameter_list|(
name|int
name|numDataNodes
parameter_list|)
throws|throws
name|Exception
block|{
name|long
name|fileSize
init|=
literal|4096
decl_stmt|;
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
literal|"/testFile"
argument_list|)
decl_stmt|;
name|short
name|replFactor
init|=
operator|(
name|short
operator|)
name|numDataNodes
decl_stmt|;
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
name|DFSConfigKeys
operator|.
name|DFS_REPLICATION_KEY
argument_list|,
name|numDataNodes
argument_list|)
expr_stmt|;
comment|// Set short retry timeouts so this test runs faster
name|conf
operator|.
name|setInt
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|Retry
operator|.
name|WINDOW_BASE_KEY
argument_list|,
literal|10
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
name|numDataNodes
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
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|file
argument_list|,
name|fileSize
argument_list|,
name|replFactor
argument_list|,
literal|12345L
comment|/*seed*/
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|fs
argument_list|,
name|file
argument_list|,
name|replFactor
argument_list|)
expr_stmt|;
name|ExtendedBlock
name|block
init|=
name|DFSTestUtil
operator|.
name|getFirstBlock
argument_list|(
name|fs
argument_list|,
name|file
argument_list|)
decl_stmt|;
name|int
name|blockFilesCorrupted
init|=
name|cluster
operator|.
name|corruptBlockOnDataNodes
argument_list|(
name|block
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"All replicas not corrupted"
argument_list|,
name|replFactor
argument_list|,
name|blockFilesCorrupted
argument_list|)
expr_stmt|;
try|try
block|{
name|IOUtils
operator|.
name|copyBytes
argument_list|(
name|fs
operator|.
name|open
argument_list|(
name|file
argument_list|)
argument_list|,
operator|new
name|IOUtils
operator|.
name|NullOutputStream
argument_list|()
argument_list|,
name|conf
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Didn't get exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|DFSClient
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"Got expected exception"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
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
block|}
end_class

end_unit

