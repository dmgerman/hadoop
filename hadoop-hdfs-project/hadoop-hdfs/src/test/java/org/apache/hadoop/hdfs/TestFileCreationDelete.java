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
name|assertTrue
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
name|Test
import|;
end_import

begin_class
DECL|class|TestFileCreationDelete
specifier|public
class|class
name|TestFileCreationDelete
block|{
block|{
name|DFSTestUtil
operator|.
name|setNameNodeLogLevel
parameter_list|(
name|Level
operator|.
name|ALL
parameter_list|)
constructor_decl|;
block|}
annotation|@
name|Test
DECL|method|testFileCreationDeleteParent ()
specifier|public
name|void
name|testFileCreationDeleteParent
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
specifier|final
name|int
name|MAX_IDLE_TIME
init|=
literal|2000
decl_stmt|;
comment|// 2s
name|conf
operator|.
name|setInt
argument_list|(
literal|"ipc.client.connection.maxidletime"
argument_list|,
name|MAX_IDLE_TIME
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// create cluster
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
name|build
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
comment|// create file1.
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
literal|"/foo"
argument_list|)
decl_stmt|;
name|Path
name|file1
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"file1"
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|stm1
init|=
name|TestFileCreation
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|file1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"testFileCreationDeleteParent: "
operator|+
literal|"Created file "
operator|+
name|file1
argument_list|)
expr_stmt|;
name|TestFileCreation
operator|.
name|writeFile
argument_list|(
name|stm1
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|stm1
operator|.
name|hflush
argument_list|()
expr_stmt|;
comment|// create file2.
name|Path
name|file2
init|=
operator|new
name|Path
argument_list|(
literal|"/file2"
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|stm2
init|=
name|TestFileCreation
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|file2
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"testFileCreationDeleteParent: "
operator|+
literal|"Created file "
operator|+
name|file2
argument_list|)
expr_stmt|;
name|TestFileCreation
operator|.
name|writeFile
argument_list|(
name|stm2
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|stm2
operator|.
name|hflush
argument_list|()
expr_stmt|;
comment|// rm dir
name|fs
operator|.
name|delete
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// restart cluster.
comment|// This ensures that leases are persisted in fsimage.
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|2
operator|*
name|MAX_IDLE_TIME
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{}
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
name|format
argument_list|(
literal|false
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
comment|// restart cluster yet again. This triggers the code to read in
comment|// persistent leases from fsimage.
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{}
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
name|format
argument_list|(
literal|false
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
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|fs
operator|.
name|exists
argument_list|(
name|file1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|file2
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
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
block|}
block|}
end_class

end_unit

