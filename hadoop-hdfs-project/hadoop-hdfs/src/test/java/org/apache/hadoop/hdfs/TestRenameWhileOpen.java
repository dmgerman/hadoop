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
name|server
operator|.
name|namenode
operator|.
name|FSNamesystem
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
name|LeaseManager
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
name|NameNode
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

begin_class
DECL|class|TestRenameWhileOpen
specifier|public
class|class
name|TestRenameWhileOpen
extends|extends
name|junit
operator|.
name|framework
operator|.
name|TestCase
block|{
block|{
operator|(
operator|(
name|Log4JLogger
operator|)
name|NameNode
operator|.
name|stateChangeLog
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
operator|(
operator|(
name|Log4JLogger
operator|)
name|LeaseManager
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
operator|(
operator|(
name|Log4JLogger
operator|)
name|LogFactory
operator|.
name|getLog
argument_list|(
name|FSNamesystem
operator|.
name|class
argument_list|)
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
block|}
comment|//TODO: un-comment checkFullFile once the lease recovery is done
DECL|method|checkFullFile (FileSystem fs, Path p)
specifier|private
specifier|static
name|void
name|checkFullFile
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|p
parameter_list|)
throws|throws
name|IOException
block|{
comment|//TestFileCreation.checkFullFile(fs, p);
block|}
comment|/**    * open /user/dir1/file1 /user/dir2/file2    * mkdir /user/dir3    * move /user/dir1 /user/dir3    */
DECL|method|testWhileOpenRenameParent ()
specifier|public
name|void
name|testWhileOpenRenameParent
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
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_SAFEMODE_THRESHOLD_PCT_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_SUPPORT_APPEND_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// create cluster
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Test 1*****************************"
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
specifier|final
name|int
name|nnport
init|=
name|cluster
operator|.
name|getNameNodePort
argument_list|()
decl_stmt|;
comment|// create file1.
name|Path
name|dir1
init|=
operator|new
name|Path
argument_list|(
literal|"/user/a+b/dir1"
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
argument_list|)
expr_stmt|;
name|stm1
operator|.
name|hflush
argument_list|()
expr_stmt|;
comment|// create file2.
name|Path
name|dir2
init|=
operator|new
name|Path
argument_list|(
literal|"/user/dir2"
argument_list|)
decl_stmt|;
name|Path
name|file2
init|=
operator|new
name|Path
argument_list|(
name|dir2
argument_list|,
literal|"file2"
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
argument_list|)
expr_stmt|;
name|stm2
operator|.
name|hflush
argument_list|()
expr_stmt|;
comment|// move dir1 while file1 is open
name|Path
name|dir3
init|=
operator|new
name|Path
argument_list|(
literal|"/user/dir3"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|dir3
argument_list|)
expr_stmt|;
name|fs
operator|.
name|rename
argument_list|(
name|dir1
argument_list|,
name|dir3
argument_list|)
expr_stmt|;
comment|// create file3
name|Path
name|file3
init|=
operator|new
name|Path
argument_list|(
name|dir3
argument_list|,
literal|"file3"
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|stm3
init|=
name|TestFileCreation
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|file3
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|TestFileCreation
operator|.
name|writeFile
argument_list|(
name|stm3
argument_list|)
expr_stmt|;
comment|// rename file3 to some bad name
try|try
block|{
name|fs
operator|.
name|rename
argument_list|(
name|file3
argument_list|,
operator|new
name|Path
argument_list|(
name|dir3
argument_list|,
literal|"$ "
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
comment|// restart cluster with the same namenode port as before.
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
name|nameNodePort
argument_list|(
name|nnport
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
name|nameNodePort
argument_list|(
name|nnport
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
name|Path
name|newfile
init|=
operator|new
name|Path
argument_list|(
literal|"/user/dir3/dir1"
argument_list|,
literal|"file1"
argument_list|)
decl_stmt|;
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
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|newfile
argument_list|)
argument_list|)
expr_stmt|;
name|checkFullFile
argument_list|(
name|fs
argument_list|,
name|newfile
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
comment|/**    * open /user/dir1/file1 /user/dir2/file2    * move /user/dir1 /user/dir3    */
DECL|method|testWhileOpenRenameParentToNonexistentDir ()
specifier|public
name|void
name|testWhileOpenRenameParentToNonexistentDir
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
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_SAFEMODE_THRESHOLD_PCT_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_SUPPORT_APPEND_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Test 2************************************"
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
specifier|final
name|int
name|nnport
init|=
name|cluster
operator|.
name|getNameNodePort
argument_list|()
decl_stmt|;
comment|// create file1.
name|Path
name|dir1
init|=
operator|new
name|Path
argument_list|(
literal|"/user/dir1"
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
argument_list|)
expr_stmt|;
name|stm1
operator|.
name|hflush
argument_list|()
expr_stmt|;
comment|// create file2.
name|Path
name|dir2
init|=
operator|new
name|Path
argument_list|(
literal|"/user/dir2"
argument_list|)
decl_stmt|;
name|Path
name|file2
init|=
operator|new
name|Path
argument_list|(
name|dir2
argument_list|,
literal|"file2"
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
argument_list|)
expr_stmt|;
name|stm2
operator|.
name|hflush
argument_list|()
expr_stmt|;
comment|// move dir1 while file1 is open
name|Path
name|dir3
init|=
operator|new
name|Path
argument_list|(
literal|"/user/dir3"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|rename
argument_list|(
name|dir1
argument_list|,
name|dir3
argument_list|)
expr_stmt|;
comment|// restart cluster with the same namenode port as before.
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
name|nameNodePort
argument_list|(
name|nnport
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
name|nameNodePort
argument_list|(
name|nnport
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
name|Path
name|newfile
init|=
operator|new
name|Path
argument_list|(
literal|"/user/dir3"
argument_list|,
literal|"file1"
argument_list|)
decl_stmt|;
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
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|newfile
argument_list|)
argument_list|)
expr_stmt|;
name|checkFullFile
argument_list|(
name|fs
argument_list|,
name|newfile
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
comment|/**    * open /user/dir1/file1     * mkdir /user/dir2    * move /user/dir1/file1 /user/dir2/    */
DECL|method|testWhileOpenRenameToExistentDirectory ()
specifier|public
name|void
name|testWhileOpenRenameToExistentDirectory
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
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_SAFEMODE_THRESHOLD_PCT_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_SUPPORT_APPEND_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Test 3************************************"
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
specifier|final
name|int
name|nnport
init|=
name|cluster
operator|.
name|getNameNodePort
argument_list|()
decl_stmt|;
comment|// create file1.
name|Path
name|dir1
init|=
operator|new
name|Path
argument_list|(
literal|"/user/dir1"
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
argument_list|)
expr_stmt|;
name|stm1
operator|.
name|hflush
argument_list|()
expr_stmt|;
name|Path
name|dir2
init|=
operator|new
name|Path
argument_list|(
literal|"/user/dir2"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|dir2
argument_list|)
expr_stmt|;
name|fs
operator|.
name|rename
argument_list|(
name|file1
argument_list|,
name|dir2
argument_list|)
expr_stmt|;
comment|// restart cluster with the same namenode port as before.
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
name|nameNodePort
argument_list|(
name|nnport
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
name|nameNodePort
argument_list|(
name|nnport
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
name|Path
name|newfile
init|=
operator|new
name|Path
argument_list|(
literal|"/user/dir2"
argument_list|,
literal|"file1"
argument_list|)
decl_stmt|;
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
name|newfile
argument_list|)
argument_list|)
expr_stmt|;
name|checkFullFile
argument_list|(
name|fs
argument_list|,
name|newfile
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
comment|/**    * open /user/dir1/file1     * move /user/dir1/file1 /user/dir2/    */
DECL|method|testWhileOpenRenameToNonExistentDirectory ()
specifier|public
name|void
name|testWhileOpenRenameToNonExistentDirectory
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
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_SAFEMODE_THRESHOLD_PCT_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_SUPPORT_APPEND_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Test 4************************************"
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
specifier|final
name|int
name|nnport
init|=
name|cluster
operator|.
name|getNameNodePort
argument_list|()
decl_stmt|;
comment|// create file1.
name|Path
name|dir1
init|=
operator|new
name|Path
argument_list|(
literal|"/user/dir1"
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
argument_list|)
expr_stmt|;
name|stm1
operator|.
name|hflush
argument_list|()
expr_stmt|;
name|Path
name|dir2
init|=
operator|new
name|Path
argument_list|(
literal|"/user/dir2"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|rename
argument_list|(
name|file1
argument_list|,
name|dir2
argument_list|)
expr_stmt|;
comment|// restart cluster with the same namenode port as before.
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
name|nameNodePort
argument_list|(
name|nnport
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
name|nameNodePort
argument_list|(
name|nnport
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
name|Path
name|newfile
init|=
operator|new
name|Path
argument_list|(
literal|"/user"
argument_list|,
literal|"dir2"
argument_list|)
decl_stmt|;
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
name|newfile
argument_list|)
argument_list|)
expr_stmt|;
name|checkFullFile
argument_list|(
name|fs
argument_list|,
name|newfile
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

