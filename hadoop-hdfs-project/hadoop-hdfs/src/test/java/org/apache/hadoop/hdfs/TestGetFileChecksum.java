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
name|FileChecksum
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

begin_class
DECL|class|TestGetFileChecksum
specifier|public
class|class
name|TestGetFileChecksum
block|{
DECL|field|BLOCKSIZE
specifier|private
specifier|static
specifier|final
name|int
name|BLOCKSIZE
init|=
literal|1024
decl_stmt|;
DECL|field|REPLICATION
specifier|private
specifier|static
specifier|final
name|short
name|REPLICATION
init|=
literal|3
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|dfs
specifier|private
name|DistributedFileSystem
name|dfs
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
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|BLOCKSIZE
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
name|REPLICATION
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
name|dfs
operator|=
name|cluster
operator|.
name|getFileSystem
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
DECL|method|testGetFileChecksum (final Path foo, final int appendLength)
specifier|public
name|void
name|testGetFileChecksum
parameter_list|(
specifier|final
name|Path
name|foo
parameter_list|,
specifier|final
name|int
name|appendLength
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|int
name|appendRounds
init|=
literal|16
decl_stmt|;
name|FileChecksum
index|[]
name|fc
init|=
operator|new
name|FileChecksum
index|[
name|appendRounds
operator|+
literal|1
index|]
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|foo
argument_list|,
name|appendLength
argument_list|,
name|REPLICATION
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|fc
index|[
literal|0
index|]
operator|=
name|dfs
operator|.
name|getFileChecksum
argument_list|(
name|foo
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
name|appendRounds
condition|;
name|i
operator|++
control|)
block|{
name|DFSTestUtil
operator|.
name|appendFile
argument_list|(
name|dfs
argument_list|,
name|foo
argument_list|,
name|appendLength
argument_list|)
expr_stmt|;
name|fc
index|[
name|i
operator|+
literal|1
index|]
operator|=
name|dfs
operator|.
name|getFileChecksum
argument_list|(
name|foo
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
name|appendRounds
operator|+
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|FileChecksum
name|checksum
init|=
name|dfs
operator|.
name|getFileChecksum
argument_list|(
name|foo
argument_list|,
name|appendLength
operator|*
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|checksum
operator|.
name|equals
argument_list|(
name|fc
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testGetFileChecksumForBlocksUnderConstruction ()
specifier|public
name|void
name|testGetFileChecksumForBlocksUnderConstruction
parameter_list|()
block|{
try|try
block|{
name|FSDataOutputStream
name|file
init|=
name|dfs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/testFile"
argument_list|)
argument_list|)
decl_stmt|;
name|file
operator|.
name|write
argument_list|(
literal|"Performance Testing"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|getFileChecksum
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/testFile"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"getFileChecksum should fail for files "
operator|+
literal|"with blocks under construction"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ie
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|ie
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Fail to get checksum, since file /testFile "
operator|+
literal|"is under construction."
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testGetFileChecksum ()
specifier|public
name|void
name|testGetFileChecksum
parameter_list|()
throws|throws
name|Exception
block|{
name|testGetFileChecksum
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/foo"
argument_list|)
argument_list|,
name|BLOCKSIZE
operator|/
literal|4
argument_list|)
expr_stmt|;
name|testGetFileChecksum
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/bar"
argument_list|)
argument_list|,
name|BLOCKSIZE
operator|/
literal|4
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

