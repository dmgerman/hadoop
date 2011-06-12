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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
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
name|net
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
name|hdfs
operator|.
name|protocol
operator|.
name|DatanodeInfo
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
name|FSConstants
operator|.
name|DatanodeReportType
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
name|fs
operator|.
name|FileStatus
import|;
end_import

begin_comment
comment|/**  * This class tests the decommissioning of nodes.  */
end_comment

begin_class
DECL|class|TestModTime
specifier|public
class|class
name|TestModTime
extends|extends
name|TestCase
block|{
DECL|field|seed
specifier|static
specifier|final
name|long
name|seed
init|=
literal|0xDEADBEEFL
decl_stmt|;
DECL|field|blockSize
specifier|static
specifier|final
name|int
name|blockSize
init|=
literal|8192
decl_stmt|;
DECL|field|fileSize
specifier|static
specifier|final
name|int
name|fileSize
init|=
literal|16384
decl_stmt|;
DECL|field|numDatanodes
specifier|static
specifier|final
name|int
name|numDatanodes
init|=
literal|6
decl_stmt|;
DECL|field|myrand
name|Random
name|myrand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|hostsFile
name|Path
name|hostsFile
decl_stmt|;
DECL|field|excludeFile
name|Path
name|excludeFile
decl_stmt|;
DECL|method|writeFile (FileSystem fileSys, Path name, int repl)
specifier|private
name|void
name|writeFile
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
comment|// create and write a file that contains three blocks of data
name|FSDataOutputStream
name|stm
init|=
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
literal|"io.file.buffer.size"
argument_list|,
literal|4096
argument_list|)
argument_list|,
operator|(
name|short
operator|)
name|repl
argument_list|,
operator|(
name|long
operator|)
name|blockSize
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|fileSize
index|]
decl_stmt|;
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
name|buffer
argument_list|)
expr_stmt|;
name|stm
operator|.
name|write
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|cleanupFile (FileSystem fileSys, Path name)
specifier|private
name|void
name|cleanupFile
parameter_list|(
name|FileSystem
name|fileSys
parameter_list|,
name|Path
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|assertTrue
argument_list|(
name|fileSys
operator|.
name|exists
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|fileSys
operator|.
name|delete
argument_list|(
name|name
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|fileSys
operator|.
name|exists
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|printDatanodeReport (DatanodeInfo[] info)
specifier|private
name|void
name|printDatanodeReport
parameter_list|(
name|DatanodeInfo
index|[]
name|info
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"-------------------------------------------------"
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
name|info
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|info
index|[
name|i
index|]
operator|.
name|getDatanodeReport
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Tests modification time in DFS.    */
DECL|method|testModTime ()
specifier|public
name|void
name|testModTime
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
name|numDatanodes
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|InetSocketAddress
name|addr
init|=
operator|new
name|InetSocketAddress
argument_list|(
literal|"localhost"
argument_list|,
name|cluster
operator|.
name|getNameNodePort
argument_list|()
argument_list|)
decl_stmt|;
name|DFSClient
name|client
init|=
operator|new
name|DFSClient
argument_list|(
name|addr
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|DatanodeInfo
index|[]
name|info
init|=
name|client
operator|.
name|datanodeReport
argument_list|(
name|DatanodeReportType
operator|.
name|LIVE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Number of Datanodes "
argument_list|,
name|numDatanodes
argument_list|,
name|info
operator|.
name|length
argument_list|)
expr_stmt|;
name|FileSystem
name|fileSys
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|int
name|replicas
init|=
name|numDatanodes
operator|-
literal|1
decl_stmt|;
name|assertTrue
argument_list|(
name|fileSys
operator|instanceof
name|DistributedFileSystem
argument_list|)
expr_stmt|;
try|try
block|{
comment|//
comment|// create file and record ctime and mtime of test file
comment|//
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Creating testdir1 and testdir1/test1.dat."
argument_list|)
expr_stmt|;
name|Path
name|dir1
init|=
operator|new
name|Path
argument_list|(
literal|"testdir1"
argument_list|)
decl_stmt|;
name|Path
name|file1
init|=
operator|new
name|Path
argument_list|(
name|dir1
argument_list|,
literal|"test1.dat"
argument_list|)
decl_stmt|;
name|writeFile
argument_list|(
name|fileSys
argument_list|,
name|file1
argument_list|,
name|replicas
argument_list|)
expr_stmt|;
name|FileStatus
name|stat
init|=
name|fileSys
operator|.
name|getFileStatus
argument_list|(
name|file1
argument_list|)
decl_stmt|;
name|long
name|mtime1
init|=
name|stat
operator|.
name|getModificationTime
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|mtime1
operator|!=
literal|0
argument_list|)
expr_stmt|;
comment|//
comment|// record dir times
comment|//
name|stat
operator|=
name|fileSys
operator|.
name|getFileStatus
argument_list|(
name|dir1
argument_list|)
expr_stmt|;
name|long
name|mdir1
init|=
name|stat
operator|.
name|getModificationTime
argument_list|()
decl_stmt|;
comment|//
comment|// create second test file
comment|//
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Creating testdir1/test2.dat."
argument_list|)
expr_stmt|;
name|Path
name|file2
init|=
operator|new
name|Path
argument_list|(
name|dir1
argument_list|,
literal|"test2.dat"
argument_list|)
decl_stmt|;
name|writeFile
argument_list|(
name|fileSys
argument_list|,
name|file2
argument_list|,
name|replicas
argument_list|)
expr_stmt|;
name|stat
operator|=
name|fileSys
operator|.
name|getFileStatus
argument_list|(
name|file2
argument_list|)
expr_stmt|;
comment|//
comment|// verify that mod time of dir remains the same
comment|// as before. modification time of directory has increased.
comment|//
name|stat
operator|=
name|fileSys
operator|.
name|getFileStatus
argument_list|(
name|dir1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|stat
operator|.
name|getModificationTime
argument_list|()
operator|>=
name|mdir1
argument_list|)
expr_stmt|;
name|mdir1
operator|=
name|stat
operator|.
name|getModificationTime
argument_list|()
expr_stmt|;
comment|//
comment|// create another directory
comment|//
name|Path
name|dir2
init|=
name|fileSys
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
literal|"testdir2/"
argument_list|)
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Creating testdir2 "
operator|+
name|dir2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fileSys
operator|.
name|mkdirs
argument_list|(
name|dir2
argument_list|)
argument_list|)
expr_stmt|;
name|stat
operator|=
name|fileSys
operator|.
name|getFileStatus
argument_list|(
name|dir2
argument_list|)
expr_stmt|;
name|long
name|mdir2
init|=
name|stat
operator|.
name|getModificationTime
argument_list|()
decl_stmt|;
comment|//
comment|// rename file1 from testdir into testdir2
comment|//
name|Path
name|newfile
init|=
operator|new
name|Path
argument_list|(
name|dir2
argument_list|,
literal|"testnew.dat"
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Moving "
operator|+
name|file1
operator|+
literal|" to "
operator|+
name|newfile
argument_list|)
expr_stmt|;
name|fileSys
operator|.
name|rename
argument_list|(
name|file1
argument_list|,
name|newfile
argument_list|)
expr_stmt|;
comment|//
comment|// verify that modification time of file1 did not change.
comment|//
name|stat
operator|=
name|fileSys
operator|.
name|getFileStatus
argument_list|(
name|newfile
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|stat
operator|.
name|getModificationTime
argument_list|()
operator|==
name|mtime1
argument_list|)
expr_stmt|;
comment|//
comment|// verify that modification time of  testdir1 and testdir2
comment|// were changed.
comment|//
name|stat
operator|=
name|fileSys
operator|.
name|getFileStatus
argument_list|(
name|dir1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|stat
operator|.
name|getModificationTime
argument_list|()
operator|!=
name|mdir1
argument_list|)
expr_stmt|;
name|mdir1
operator|=
name|stat
operator|.
name|getModificationTime
argument_list|()
expr_stmt|;
name|stat
operator|=
name|fileSys
operator|.
name|getFileStatus
argument_list|(
name|dir2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|stat
operator|.
name|getModificationTime
argument_list|()
operator|!=
name|mdir2
argument_list|)
expr_stmt|;
name|mdir2
operator|=
name|stat
operator|.
name|getModificationTime
argument_list|()
expr_stmt|;
comment|//
comment|// delete newfile
comment|//
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Deleting testdir2/testnew.dat."
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fileSys
operator|.
name|delete
argument_list|(
name|newfile
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|//
comment|// verify that modification time of testdir1 has not changed.
comment|//
name|stat
operator|=
name|fileSys
operator|.
name|getFileStatus
argument_list|(
name|dir1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|stat
operator|.
name|getModificationTime
argument_list|()
operator|==
name|mdir1
argument_list|)
expr_stmt|;
comment|//
comment|// verify that modification time of testdir2 has changed.
comment|//
name|stat
operator|=
name|fileSys
operator|.
name|getFileStatus
argument_list|(
name|dir2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|stat
operator|.
name|getModificationTime
argument_list|()
operator|!=
name|mdir2
argument_list|)
expr_stmt|;
name|mdir2
operator|=
name|stat
operator|.
name|getModificationTime
argument_list|()
expr_stmt|;
name|cleanupFile
argument_list|(
name|fileSys
argument_list|,
name|file2
argument_list|)
expr_stmt|;
name|cleanupFile
argument_list|(
name|fileSys
argument_list|,
name|dir1
argument_list|)
expr_stmt|;
name|cleanupFile
argument_list|(
name|fileSys
argument_list|,
name|dir2
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|info
operator|=
name|client
operator|.
name|datanodeReport
argument_list|(
name|DatanodeReportType
operator|.
name|ALL
argument_list|)
expr_stmt|;
name|printDatanodeReport
argument_list|(
name|info
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
name|fileSys
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
operator|new
name|TestModTime
argument_list|()
operator|.
name|testModTime
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

