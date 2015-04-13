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
name|io
operator|.
name|OutputStream
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
name|hdfs
operator|.
name|MiniDFSCluster
operator|.
name|DataNodeProperties
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
name|util
operator|.
name|ThreadUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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

begin_comment
comment|/**  * These tests make sure that DFSClient excludes writing data to  * a DN properly in case of errors.  */
end_comment

begin_class
DECL|class|TestDFSClientExcludedNodes
specifier|public
class|class
name|TestDFSClientExcludedNodes
block|{
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|cluster
operator|=
literal|null
expr_stmt|;
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
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
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testExcludedNodes ()
specifier|public
name|void
name|testExcludedNodes
parameter_list|()
throws|throws
name|IOException
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
literal|3
argument_list|)
operator|.
name|build
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
name|filePath
init|=
operator|new
name|Path
argument_list|(
literal|"/testExcludedNodes"
argument_list|)
decl_stmt|;
comment|// kill a datanode
name|cluster
operator|.
name|stopDataNode
argument_list|(
name|AppendTestUtil
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|OutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|filePath
argument_list|,
literal|true
argument_list|,
literal|4096
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|,
name|fs
operator|.
name|getDefaultBlockSize
argument_list|(
name|filePath
argument_list|)
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|20
argument_list|)
expr_stmt|;
try|try
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Single DN failure should not result in a block abort: \n"
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testExcludedNodesForgiveness ()
specifier|public
name|void
name|testExcludedNodesForgiveness
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Forgive nodes in under 2.5s for this test case.
name|conf
operator|.
name|setLong
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|Write
operator|.
name|EXCLUDE_NODES_CACHE_EXPIRY_INTERVAL_KEY
argument_list|,
literal|2500
argument_list|)
expr_stmt|;
comment|// We'll be using a 512 bytes block size just for tests
comment|// so making sure the checksum bytes too match it.
name|conf
operator|.
name|setInt
argument_list|(
literal|"io.bytes.per.checksum"
argument_list|,
literal|512
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
literal|3
argument_list|)
operator|.
name|build
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
name|filePath
init|=
operator|new
name|Path
argument_list|(
literal|"/testForgivingExcludedNodes"
argument_list|)
decl_stmt|;
comment|// 256 bytes data chunk for writes
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
literal|256
index|]
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
name|bytes
operator|.
name|length
condition|;
name|index
operator|++
control|)
block|{
name|bytes
index|[
name|index
index|]
operator|=
literal|'0'
expr_stmt|;
block|}
comment|// File with a 512 bytes block size
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|filePath
argument_list|,
literal|true
argument_list|,
literal|4096
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|,
literal|512
argument_list|)
decl_stmt|;
comment|// Write a block to all 3 DNs (2x256bytes).
name|out
operator|.
name|write
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|out
operator|.
name|hflush
argument_list|()
expr_stmt|;
comment|// Remove two DNs, to put them into the exclude list.
name|DataNodeProperties
name|two
init|=
name|cluster
operator|.
name|stopDataNode
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|DataNodeProperties
name|one
init|=
name|cluster
operator|.
name|stopDataNode
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// Write another block.
comment|// At this point, we have two nodes already in excluded list.
name|out
operator|.
name|write
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|out
operator|.
name|hflush
argument_list|()
expr_stmt|;
comment|// Bring back the older DNs, since they are gonna be forgiven only
comment|// afterwards of this previous block write.
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|cluster
operator|.
name|restartDataNode
argument_list|(
name|one
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|cluster
operator|.
name|restartDataNode
argument_list|(
name|two
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
comment|// Sleep for 5s, to let the excluded nodes be expired
comment|// from the excludes list (i.e. forgiven after the configured wait period).
comment|// [Sleeping just in case the restart of the DNs completed< 5s cause
comment|// otherwise, we'll end up quickly excluding those again.]
name|ThreadUtil
operator|.
name|sleepAtLeastIgnoreInterrupts
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
comment|// Terminate the last good DN, to assert that there's no
comment|// single-DN-available scenario, caused by not forgiving the other
comment|// two by now.
name|cluster
operator|.
name|stopDataNode
argument_list|(
literal|0
argument_list|)
expr_stmt|;
try|try
block|{
comment|// Attempt writing another block, which should still pass
comment|// cause the previous two should have been forgiven by now,
comment|// while the last good DN added to excludes this time.
name|out
operator|.
name|write
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|out
operator|.
name|hflush
argument_list|()
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Excluded DataNodes should be forgiven after a while and "
operator|+
literal|"not cause file writing exception of: '"
operator|+
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

