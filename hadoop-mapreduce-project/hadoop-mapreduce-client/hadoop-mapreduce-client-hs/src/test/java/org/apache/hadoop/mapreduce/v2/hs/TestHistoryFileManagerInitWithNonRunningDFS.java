begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.hs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|hs
package|;
end_package

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
name|mapreduce
operator|.
name|v2
operator|.
name|jobhistory
operator|.
name|JHAdminConfig
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
name|yarn
operator|.
name|exceptions
operator|.
name|YarnRuntimeException
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
comment|/**  * Test service initialization of HistoryFileManager when  * HDFS is not running normally (either in start phase or  * in safe mode).  */
end_comment

begin_class
DECL|class|TestHistoryFileManagerInitWithNonRunningDFS
specifier|public
class|class
name|TestHistoryFileManagerInitWithNonRunningDFS
block|{
DECL|field|CLUSTER_BASE_DIR
specifier|private
specifier|static
specifier|final
name|String
name|CLUSTER_BASE_DIR
init|=
name|MiniDFSCluster
operator|.
name|getBaseDirectory
argument_list|()
decl_stmt|;
comment|/**    * Verify if JHS keeps retrying to connect to HDFS, if the name node is    * in safe mode, when it creates history directories during service    * initialization. The expected behavior of JHS is to keep retrying for    * a time limit as specified by    * JHAdminConfig.MR_HISTORY_MAX_START_WAIT_TIME, and give up by throwing    * a YarnRuntimeException with a time out message.    */
annotation|@
name|Test
DECL|method|testKeepRetryingWhileNameNodeInSafeMode ()
specifier|public
name|void
name|testKeepRetryingWhileNameNodeInSafeMode
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|// set maximum wait time for JHS to wait for HDFS NameNode to start running
specifier|final
name|long
name|maxJhsWaitTime
init|=
literal|500
decl_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|JHAdminConfig
operator|.
name|MR_HISTORY_MAX_START_WAIT_TIME
argument_list|,
name|maxJhsWaitTime
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MiniDFSCluster
operator|.
name|HDFS_MINIDFS_BASEDIR
argument_list|,
name|CLUSTER_BASE_DIR
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|dfsCluster
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
try|try
block|{
comment|// set up a cluster with its name node in safe mode
name|dfsCluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|setSafeMode
argument_list|(
name|HdfsConstants
operator|.
name|SafeModeAction
operator|.
name|SAFEMODE_ENTER
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|dfsCluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|isInSafeMode
argument_list|()
argument_list|)
expr_stmt|;
name|HistoryFileManager
name|hfm
init|=
operator|new
name|HistoryFileManager
argument_list|()
decl_stmt|;
name|hfm
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"History File Manager did not retry to connect to name node"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnRuntimeException
name|yex
parameter_list|)
block|{
name|String
name|expectedExceptionMsg
init|=
literal|"Timed out '"
operator|+
name|maxJhsWaitTime
operator|+
literal|"ms' waiting for FileSystem to become available"
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Unexpected reconnect timeout exception message"
argument_list|,
name|expectedExceptionMsg
argument_list|,
name|yex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|dfsCluster
operator|.
name|shutdown
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

