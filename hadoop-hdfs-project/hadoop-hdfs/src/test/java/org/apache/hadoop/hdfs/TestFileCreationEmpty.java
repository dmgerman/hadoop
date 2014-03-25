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
name|assertFalse
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ConcurrentModificationException
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
name|LeaseManager
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
comment|/**  * Test empty file creation.  */
end_comment

begin_class
DECL|class|TestFileCreationEmpty
specifier|public
class|class
name|TestFileCreationEmpty
block|{
DECL|field|isConcurrentModificationException
specifier|private
name|boolean
name|isConcurrentModificationException
init|=
literal|false
decl_stmt|;
comment|/**    * This test creates three empty files and lets their leases expire.    * This triggers release of the leases.     * The empty files are supposed to be closed by that     * without causing ConcurrentModificationException.    */
annotation|@
name|Test
DECL|method|testLeaseExpireEmptyFiles ()
specifier|public
name|void
name|testLeaseExpireEmptyFiles
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Thread
operator|.
name|UncaughtExceptionHandler
name|oldUEH
init|=
name|Thread
operator|.
name|getDefaultUncaughtExceptionHandler
argument_list|()
decl_stmt|;
name|Thread
operator|.
name|setDefaultUncaughtExceptionHandler
argument_list|(
operator|new
name|Thread
operator|.
name|UncaughtExceptionHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|uncaughtException
parameter_list|(
name|Thread
name|t
parameter_list|,
name|Throwable
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|ConcurrentModificationException
condition|)
block|{
name|LeaseManager
operator|.
name|LOG
operator|.
name|error
argument_list|(
literal|"t="
operator|+
name|t
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|isConcurrentModificationException
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"testLeaseExpireEmptyFiles start"
argument_list|)
expr_stmt|;
specifier|final
name|long
name|leasePeriod
init|=
literal|1000
decl_stmt|;
specifier|final
name|int
name|DATANODE_NUM
init|=
literal|3
decl_stmt|;
specifier|final
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
name|numDataNodes
argument_list|(
name|DATANODE_NUM
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
name|DistributedFileSystem
name|dfs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
comment|// create a new file.
name|TestFileCreation
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/foo"
argument_list|)
argument_list|,
name|DATANODE_NUM
argument_list|)
expr_stmt|;
name|TestFileCreation
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/foo2"
argument_list|)
argument_list|,
name|DATANODE_NUM
argument_list|)
expr_stmt|;
name|TestFileCreation
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/foo3"
argument_list|)
argument_list|,
name|DATANODE_NUM
argument_list|)
expr_stmt|;
comment|// set the soft and hard limit to be 1 second so that the
comment|// namenode triggers lease recovery
name|cluster
operator|.
name|setLeasePeriod
argument_list|(
name|leasePeriod
argument_list|,
name|leasePeriod
argument_list|)
expr_stmt|;
comment|// wait for the lease to expire
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|5
operator|*
name|leasePeriod
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{}
name|assertFalse
argument_list|(
name|isConcurrentModificationException
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|Thread
operator|.
name|setDefaultUncaughtExceptionHandler
argument_list|(
name|oldUEH
argument_list|)
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

