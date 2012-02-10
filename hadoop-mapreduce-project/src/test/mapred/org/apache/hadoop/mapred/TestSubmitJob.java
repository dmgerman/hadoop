begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedExceptionAction
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
name|HdfsFileStatus
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
name|protocolPB
operator|.
name|ClientNamenodeProtocolTranslatorPB
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
name|hadoop
operator|.
name|ipc
operator|.
name|RPC
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
name|ipc
operator|.
name|RemoteException
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
name|MRConfig
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
name|SleepJob
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
name|protocol
operator|.
name|ClientProtocol
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
name|server
operator|.
name|jobtracker
operator|.
name|JTConfig
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
name|net
operator|.
name|NetUtils
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
name|security
operator|.
name|UserGroupInformation
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
name|ToolRunner
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
comment|/**  * Test job submission. This test checks if   *   - basic   : job submission via jobclient  *   - cleanup : job client crashes while submitting  *   - invalid job config  *     - invalid memory config  *     */
end_comment

begin_class
DECL|class|TestSubmitJob
specifier|public
class|class
name|TestSubmitJob
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestSubmitJob
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|TEST_DIR
specifier|private
specifier|static
name|Path
name|TEST_DIR
init|=
operator|new
name|Path
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/tmp"
argument_list|)
argument_list|,
literal|"job-submission-testing"
argument_list|)
decl_stmt|;
comment|/**    * Test to verify that jobs with invalid memory requirements are killed at the    * JT.    *     * @throws Exception    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Test
DECL|method|testJobWithInvalidMemoryReqs ()
specifier|public
name|void
name|testJobWithInvalidMemoryReqs
parameter_list|()
throws|throws
name|Exception
block|{
name|MiniMRCluster
name|mrCluster
init|=
literal|null
decl_stmt|;
try|try
block|{
name|JobConf
name|jtConf
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|jtConf
operator|.
name|setLong
argument_list|(
name|MRConfig
operator|.
name|MAPMEMORY_MB
argument_list|,
literal|1
operator|*
literal|1024L
argument_list|)
expr_stmt|;
name|jtConf
operator|.
name|setLong
argument_list|(
name|MRConfig
operator|.
name|REDUCEMEMORY_MB
argument_list|,
literal|2
operator|*
literal|1024L
argument_list|)
expr_stmt|;
name|jtConf
operator|.
name|setLong
argument_list|(
name|JTConfig
operator|.
name|JT_MAX_MAPMEMORY_MB
argument_list|,
literal|3
operator|*
literal|1024L
argument_list|)
expr_stmt|;
name|jtConf
operator|.
name|setLong
argument_list|(
name|JTConfig
operator|.
name|JT_MAX_REDUCEMEMORY_MB
argument_list|,
literal|4
operator|*
literal|1024L
argument_list|)
expr_stmt|;
name|mrCluster
operator|=
operator|new
name|MiniMRCluster
argument_list|(
literal|0
argument_list|,
literal|"file:///"
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|jtConf
argument_list|)
expr_stmt|;
name|JobConf
name|clusterConf
init|=
name|mrCluster
operator|.
name|createJobConf
argument_list|()
decl_stmt|;
comment|// No map-memory configuration
name|JobConf
name|jobConf
init|=
operator|new
name|JobConf
argument_list|(
name|clusterConf
argument_list|)
decl_stmt|;
name|jobConf
operator|.
name|setMemoryForReduceTask
argument_list|(
literal|1
operator|*
literal|1024L
argument_list|)
expr_stmt|;
name|runJobAndVerifyFailure
argument_list|(
name|jobConf
argument_list|,
name|JobConf
operator|.
name|DISABLED_MEMORY_LIMIT
argument_list|,
literal|1
operator|*
literal|1024L
argument_list|,
literal|"Invalid job requirements."
argument_list|)
expr_stmt|;
comment|// No reduce-memory configuration
name|jobConf
operator|=
operator|new
name|JobConf
argument_list|(
name|clusterConf
argument_list|)
expr_stmt|;
name|jobConf
operator|.
name|setMemoryForMapTask
argument_list|(
literal|1
operator|*
literal|1024L
argument_list|)
expr_stmt|;
name|runJobAndVerifyFailure
argument_list|(
name|jobConf
argument_list|,
literal|1
operator|*
literal|1024L
argument_list|,
name|JobConf
operator|.
name|DISABLED_MEMORY_LIMIT
argument_list|,
literal|"Invalid job requirements."
argument_list|)
expr_stmt|;
comment|// Invalid map-memory configuration
name|jobConf
operator|=
operator|new
name|JobConf
argument_list|(
name|clusterConf
argument_list|)
expr_stmt|;
name|jobConf
operator|.
name|setMemoryForMapTask
argument_list|(
literal|4
operator|*
literal|1024L
argument_list|)
expr_stmt|;
name|jobConf
operator|.
name|setMemoryForReduceTask
argument_list|(
literal|1
operator|*
literal|1024L
argument_list|)
expr_stmt|;
name|runJobAndVerifyFailure
argument_list|(
name|jobConf
argument_list|,
literal|4
operator|*
literal|1024L
argument_list|,
literal|1
operator|*
literal|1024L
argument_list|,
literal|"Exceeds the cluster's max-memory-limit."
argument_list|)
expr_stmt|;
comment|// No reduce-memory configuration
name|jobConf
operator|=
operator|new
name|JobConf
argument_list|(
name|clusterConf
argument_list|)
expr_stmt|;
name|jobConf
operator|.
name|setMemoryForMapTask
argument_list|(
literal|1
operator|*
literal|1024L
argument_list|)
expr_stmt|;
name|jobConf
operator|.
name|setMemoryForReduceTask
argument_list|(
literal|5
operator|*
literal|1024L
argument_list|)
expr_stmt|;
name|runJobAndVerifyFailure
argument_list|(
name|jobConf
argument_list|,
literal|1
operator|*
literal|1024L
argument_list|,
literal|5
operator|*
literal|1024L
argument_list|,
literal|"Exceeds the cluster's max-memory-limit."
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|mrCluster
operator|!=
literal|null
condition|)
name|mrCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|runJobAndVerifyFailure (JobConf jobConf, long memForMapTasks, long memForReduceTasks, String expectedMsg)
specifier|private
name|void
name|runJobAndVerifyFailure
parameter_list|(
name|JobConf
name|jobConf
parameter_list|,
name|long
name|memForMapTasks
parameter_list|,
name|long
name|memForReduceTasks
parameter_list|,
name|String
name|expectedMsg
parameter_list|)
throws|throws
name|Exception
throws|,
name|IOException
block|{
name|String
index|[]
name|args
init|=
block|{
literal|"-m"
block|,
literal|"0"
block|,
literal|"-r"
block|,
literal|"0"
block|,
literal|"-mt"
block|,
literal|"0"
block|,
literal|"-rt"
block|,
literal|"0"
block|}
decl_stmt|;
name|boolean
name|throwsException
init|=
literal|false
decl_stmt|;
name|String
name|msg
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ToolRunner
operator|.
name|run
argument_list|(
name|jobConf
argument_list|,
operator|new
name|SleepJob
argument_list|()
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RemoteException
name|re
parameter_list|)
block|{
name|throwsException
operator|=
literal|true
expr_stmt|;
name|msg
operator|=
name|re
operator|.
name|unwrapRemoteException
argument_list|()
operator|.
name|getMessage
argument_list|()
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|throwsException
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|String
name|overallExpectedMsg
init|=
literal|"("
operator|+
name|memForMapTasks
operator|+
literal|" memForMapTasks "
operator|+
name|memForReduceTasks
operator|+
literal|" memForReduceTasks): "
operator|+
name|expectedMsg
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Observed message - "
operator|+
name|msg
operator|+
literal|" - doesn't contain expected message - "
operator|+
name|overallExpectedMsg
argument_list|,
name|msg
operator|.
name|contains
argument_list|(
name|overallExpectedMsg
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|getJobSubmitClient (JobConf conf, UserGroupInformation ugi)
specifier|static
name|ClientProtocol
name|getJobSubmitClient
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|UserGroupInformation
name|ugi
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
name|ClientProtocol
operator|)
name|RPC
operator|.
name|getProxy
argument_list|(
name|ClientProtocol
operator|.
name|class
argument_list|,
name|ClientProtocol
operator|.
name|versionID
argument_list|,
name|JobTracker
operator|.
name|getAddress
argument_list|(
name|conf
argument_list|)
argument_list|,
name|ugi
argument_list|,
name|conf
argument_list|,
name|NetUtils
operator|.
name|getSocketFactory
argument_list|(
name|conf
argument_list|,
name|ClientProtocol
operator|.
name|class
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getDFSClient ( Configuration conf, UserGroupInformation ugi)
specifier|static
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
name|ClientProtocol
name|getDFSClient
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|UserGroupInformation
name|ugi
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ClientNamenodeProtocolTranslatorPB
argument_list|(
name|NameNode
operator|.
name|getAddress
argument_list|(
name|conf
argument_list|)
argument_list|,
name|conf
argument_list|,
name|ugi
argument_list|)
return|;
block|}
comment|/**    * Submit a job and check if the files are accessible to other users.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Test
DECL|method|testSecureJobExecution ()
specifier|public
name|void
name|testSecureJobExecution
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Testing secure job submission/execution"
argument_list|)
expr_stmt|;
name|MiniMRCluster
name|mr
init|=
literal|null
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
specifier|final
name|MiniDFSCluster
name|dfs
init|=
operator|new
name|MiniDFSCluster
argument_list|(
name|conf
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|FileSystem
name|fs
init|=
name|TestMiniMRWithDFSWithDistinctUsers
operator|.
name|DFS_UGI
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|FileSystem
argument_list|>
argument_list|()
block|{
specifier|public
name|FileSystem
name|run
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|dfs
operator|.
name|getFileSystem
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|TestMiniMRWithDFSWithDistinctUsers
operator|.
name|mkdir
argument_list|(
name|fs
argument_list|,
literal|"/user"
argument_list|,
literal|"mapred"
argument_list|,
literal|"mapred"
argument_list|,
operator|(
name|short
operator|)
literal|01777
argument_list|)
expr_stmt|;
name|TestMiniMRWithDFSWithDistinctUsers
operator|.
name|mkdir
argument_list|(
name|fs
argument_list|,
literal|"/mapred"
argument_list|,
literal|"mapred"
argument_list|,
literal|"mapred"
argument_list|,
operator|(
name|short
operator|)
literal|01777
argument_list|)
expr_stmt|;
name|TestMiniMRWithDFSWithDistinctUsers
operator|.
name|mkdir
argument_list|(
name|fs
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|JTConfig
operator|.
name|JT_STAGING_AREA_ROOT
argument_list|)
argument_list|,
literal|"mapred"
argument_list|,
literal|"mapred"
argument_list|,
operator|(
name|short
operator|)
literal|01777
argument_list|)
expr_stmt|;
name|UserGroupInformation
name|MR_UGI
init|=
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
decl_stmt|;
name|mr
operator|=
operator|new
name|MiniMRCluster
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
name|dfs
operator|.
name|getFileSystem
argument_list|()
operator|.
name|getUri
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|MR_UGI
argument_list|)
expr_stmt|;
name|JobTracker
name|jt
init|=
name|mr
operator|.
name|getJobTrackerRunner
argument_list|()
operator|.
name|getJobTracker
argument_list|()
decl_stmt|;
comment|// cleanup
name|dfs
operator|.
name|getFileSystem
argument_list|()
operator|.
name|delete
argument_list|(
name|TEST_DIR
argument_list|,
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|mapSignalFile
init|=
operator|new
name|Path
argument_list|(
name|TEST_DIR
argument_list|,
literal|"map-signal"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|reduceSignalFile
init|=
operator|new
name|Path
argument_list|(
name|TEST_DIR
argument_list|,
literal|"reduce-signal"
argument_list|)
decl_stmt|;
comment|// create a ugi for user 1
name|UserGroupInformation
name|user1
init|=
name|TestMiniMRWithDFSWithDistinctUsers
operator|.
name|createUGI
argument_list|(
literal|"user1"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Path
name|inDir
init|=
operator|new
name|Path
argument_list|(
literal|"/user/input"
argument_list|)
decl_stmt|;
name|Path
name|outDir
init|=
operator|new
name|Path
argument_list|(
literal|"/user/output"
argument_list|)
decl_stmt|;
specifier|final
name|JobConf
name|job
init|=
name|mr
operator|.
name|createJobConf
argument_list|()
decl_stmt|;
name|UtilsForTests
operator|.
name|configureWaitingJobConf
argument_list|(
name|job
argument_list|,
name|inDir
argument_list|,
name|outDir
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|,
literal|"test-submit-job"
argument_list|,
name|mapSignalFile
operator|.
name|toString
argument_list|()
argument_list|,
name|reduceSignalFile
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|job
operator|.
name|set
argument_list|(
name|UtilsForTests
operator|.
name|getTaskSignalParameter
argument_list|(
literal|true
argument_list|)
argument_list|,
name|mapSignalFile
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|job
operator|.
name|set
argument_list|(
name|UtilsForTests
operator|.
name|getTaskSignalParameter
argument_list|(
literal|false
argument_list|)
argument_list|,
name|reduceSignalFile
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Submit job as the actual user ("
operator|+
name|user1
operator|.
name|getUserName
argument_list|()
operator|+
literal|")"
argument_list|)
expr_stmt|;
specifier|final
name|JobClient
name|jClient
init|=
name|user1
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|JobClient
argument_list|>
argument_list|()
block|{
specifier|public
name|JobClient
name|run
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|JobClient
argument_list|(
name|job
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|RunningJob
name|rJob
init|=
name|user1
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|RunningJob
argument_list|>
argument_list|()
block|{
specifier|public
name|RunningJob
name|run
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|jClient
operator|.
name|submitJob
argument_list|(
name|job
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|JobID
name|id
init|=
name|rJob
operator|.
name|getID
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Running job "
operator|+
name|id
argument_list|)
expr_stmt|;
comment|// create user2
name|UserGroupInformation
name|user2
init|=
name|TestMiniMRWithDFSWithDistinctUsers
operator|.
name|createUGI
argument_list|(
literal|"user2"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|JobConf
name|conf_other
init|=
name|mr
operator|.
name|createJobConf
argument_list|()
decl_stmt|;
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
name|ClientProtocol
name|client
init|=
name|getDFSClient
argument_list|(
name|conf_other
argument_list|,
name|user2
argument_list|)
decl_stmt|;
comment|// try accessing mapred.system.dir/jobid/*
try|try
block|{
name|String
name|path
init|=
operator|new
name|URI
argument_list|(
name|jt
operator|.
name|getSystemDir
argument_list|()
argument_list|)
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Try listing the mapred-system-dir as the user ("
operator|+
name|user2
operator|.
name|getUserName
argument_list|()
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|client
operator|.
name|getListing
argument_list|(
name|path
argument_list|,
name|HdfsFileStatus
operator|.
name|EMPTY_NAME
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"JobTracker system dir is accessible to others"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|ioe
operator|.
name|toString
argument_list|()
argument_list|,
name|ioe
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Permission denied"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// try accessing ~/.staging/jobid/*
name|JobInProgress
name|jip
init|=
name|jt
operator|.
name|getJob
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|Path
name|jobSubmitDirpath
init|=
operator|new
name|Path
argument_list|(
name|jip
operator|.
name|getJobConf
argument_list|()
operator|.
name|get
argument_list|(
literal|"mapreduce.job.dir"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Try accessing the job folder for job "
operator|+
name|id
operator|+
literal|" as the user ("
operator|+
name|user2
operator|.
name|getUserName
argument_list|()
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|client
operator|.
name|getListing
argument_list|(
name|jobSubmitDirpath
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
name|HdfsFileStatus
operator|.
name|EMPTY_NAME
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"User's staging folder is accessible to others"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|ioe
operator|.
name|toString
argument_list|()
argument_list|,
name|ioe
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Permission denied"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|UtilsForTests
operator|.
name|signalTasks
argument_list|(
name|dfs
argument_list|,
name|fs
argument_list|,
literal|true
argument_list|,
name|mapSignalFile
operator|.
name|toString
argument_list|()
argument_list|,
name|reduceSignalFile
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// wait for job to be done
name|UtilsForTests
operator|.
name|waitTillDone
argument_list|(
name|jClient
argument_list|)
expr_stmt|;
comment|// check if the staging area is cleaned up
name|LOG
operator|.
name|info
argument_list|(
literal|"Check if job submit dir is cleanup or not"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|jobSubmitDirpath
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|mr
operator|!=
literal|null
condition|)
block|{
name|mr
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|dfs
operator|!=
literal|null
condition|)
block|{
name|dfs
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

