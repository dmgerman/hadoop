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
name|protocol
operator|.
name|HdfsConstants
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
name|LocatedBlock
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
name|LocatedBlocks
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
name|QuotaExceededException
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
comment|/**  * Test abandoning blocks, which clients do on pipeline creation failure.  */
end_comment

begin_class
DECL|class|TestAbandonBlock
specifier|public
class|class
name|TestAbandonBlock
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestAbandonBlock
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
DECL|field|FILE_NAME_PREFIX
specifier|static
specifier|final
name|String
name|FILE_NAME_PREFIX
init|=
literal|"/"
operator|+
name|TestAbandonBlock
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"_"
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|fs
specifier|private
name|DistributedFileSystem
name|fs
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|CONF
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|2
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
name|cluster
operator|.
name|waitActive
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
throws|throws
name|Exception
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
comment|/** Abandon a block while creating a file */
DECL|method|testAbandonBlock ()
specifier|public
name|void
name|testAbandonBlock
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|src
init|=
name|FILE_NAME_PREFIX
operator|+
literal|"foo"
decl_stmt|;
comment|// Start writing a file but do not close it
name|FSDataOutputStream
name|fout
init|=
name|fs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|src
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|4096
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|512L
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
literal|1024
condition|;
name|i
operator|++
control|)
block|{
name|fout
operator|.
name|write
argument_list|(
literal|123
argument_list|)
expr_stmt|;
block|}
name|fout
operator|.
name|hflush
argument_list|()
expr_stmt|;
comment|// Now abandon the last block
name|DFSClient
name|dfsclient
init|=
name|DFSClientAdapter
operator|.
name|getDFSClient
argument_list|(
name|fs
argument_list|)
decl_stmt|;
name|LocatedBlocks
name|blocks
init|=
name|dfsclient
operator|.
name|getNamenode
argument_list|()
operator|.
name|getBlockLocations
argument_list|(
name|src
argument_list|,
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|int
name|orginalNumBlocks
init|=
name|blocks
operator|.
name|locatedBlockCount
argument_list|()
decl_stmt|;
name|LocatedBlock
name|b
init|=
name|blocks
operator|.
name|getLastLocatedBlock
argument_list|()
decl_stmt|;
name|dfsclient
operator|.
name|getNamenode
argument_list|()
operator|.
name|abandonBlock
argument_list|(
name|b
operator|.
name|getBlock
argument_list|()
argument_list|,
name|src
argument_list|,
name|dfsclient
operator|.
name|clientName
argument_list|)
expr_stmt|;
comment|// call abandonBlock again to make sure the operation is idempotent
name|dfsclient
operator|.
name|getNamenode
argument_list|()
operator|.
name|abandonBlock
argument_list|(
name|b
operator|.
name|getBlock
argument_list|()
argument_list|,
name|src
argument_list|,
name|dfsclient
operator|.
name|clientName
argument_list|)
expr_stmt|;
comment|// And close the file
name|fout
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Close cluster and check the block has been abandoned after restart
name|cluster
operator|.
name|restartNameNode
argument_list|()
expr_stmt|;
name|blocks
operator|=
name|dfsclient
operator|.
name|getNamenode
argument_list|()
operator|.
name|getBlockLocations
argument_list|(
name|src
argument_list|,
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Blocks "
operator|+
name|b
operator|+
literal|" has not been abandoned."
argument_list|,
name|orginalNumBlocks
argument_list|,
name|blocks
operator|.
name|locatedBlockCount
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|/** Make sure that the quota is decremented correctly when a block is abandoned */
DECL|method|testQuotaUpdatedWhenBlockAbandoned ()
specifier|public
name|void
name|testQuotaUpdatedWhenBlockAbandoned
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Setting diskspace quota to 3MB
name|fs
operator|.
name|setQuota
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
argument_list|,
name|HdfsConstants
operator|.
name|QUOTA_DONT_SET
argument_list|,
literal|3
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
comment|// Start writing a file with 2 replicas to ensure each datanode has one.
comment|// Block Size is 1MB.
name|String
name|src
init|=
name|FILE_NAME_PREFIX
operator|+
literal|"test_quota1"
decl_stmt|;
name|FSDataOutputStream
name|fout
init|=
name|fs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|src
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|4096
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|,
literal|1024
operator|*
literal|1024
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
literal|1024
condition|;
name|i
operator|++
control|)
block|{
name|fout
operator|.
name|writeByte
argument_list|(
literal|123
argument_list|)
expr_stmt|;
block|}
comment|// Shutdown one datanode, causing the block abandonment.
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|shutdown
argument_list|()
expr_stmt|;
comment|// Close the file, new block will be allocated with 2MB pending size.
try|try
block|{
name|fout
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|QuotaExceededException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Unexpected quota exception when closing fout"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

