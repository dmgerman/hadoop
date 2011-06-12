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
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|util
operator|.
name|Collection
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
name|Log
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
name|test
operator|.
name|system
operator|.
name|JTProtocol
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
name|test
operator|.
name|system
operator|.
name|TTClient
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
name|test
operator|.
name|system
operator|.
name|JobInfo
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
name|test
operator|.
name|system
operator|.
name|TaskInfo
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
name|test
operator|.
name|system
operator|.
name|MRCluster
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
name|mapred
operator|.
name|UtilsForTests
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
name|test
operator|.
name|system
operator|.
name|FinishTaskControlAction
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
name|filecache
operator|.
name|DistributedCache
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
name|permission
operator|.
name|FsPermission
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
name|Job
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
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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
comment|/**  * Verify the Distributed Cache functionality.  * This test scenario is for a distributed cache file behaviour  * when the file is private. Once a job uses a distributed   * cache file with private permissions that file is stored in the  * mapred.local.dir, under the directory which has the same name   * as job submitter's username. The directory has 700 permission   * and the file under it, should have 777 permissions.  */
end_comment

begin_class
DECL|class|TestDistributedCachePrivateFile
specifier|public
class|class
name|TestDistributedCachePrivateFile
block|{
DECL|field|cluster
specifier|private
specifier|static
name|MRCluster
name|cluster
init|=
literal|null
decl_stmt|;
DECL|field|dfs
specifier|private
specifier|static
name|FileSystem
name|dfs
init|=
literal|null
decl_stmt|;
DECL|field|client
specifier|private
specifier|static
name|JobClient
name|client
init|=
literal|null
decl_stmt|;
DECL|field|permission
specifier|private
specifier|static
name|FsPermission
name|permission
init|=
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|00770
argument_list|)
decl_stmt|;
DECL|field|uriPath
specifier|private
specifier|static
name|String
name|uriPath
init|=
literal|"hdfs:///tmp/test.txt"
decl_stmt|;
DECL|field|URIPATH
specifier|private
specifier|static
specifier|final
name|Path
name|URIPATH
init|=
operator|new
name|Path
argument_list|(
name|uriPath
argument_list|)
decl_stmt|;
DECL|field|distributedFileName
specifier|private
name|String
name|distributedFileName
init|=
literal|"test.txt"
decl_stmt|;
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
name|TestDistributedCachePrivateFile
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|TestDistributedCachePrivateFile ()
specifier|public
name|TestDistributedCachePrivateFile
parameter_list|()
throws|throws
name|Exception
block|{   }
annotation|@
name|BeforeClass
DECL|method|setUp ()
specifier|public
specifier|static
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|cluster
operator|=
name|MRCluster
operator|.
name|createCluster
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|client
operator|=
name|cluster
operator|.
name|getJTClient
argument_list|()
operator|.
name|getClient
argument_list|()
expr_stmt|;
name|dfs
operator|=
name|client
operator|.
name|getFs
argument_list|()
expr_stmt|;
comment|//Deleting the file if it already exists
name|dfs
operator|.
name|delete
argument_list|(
name|URIPATH
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|TTClient
argument_list|>
name|tts
init|=
name|cluster
operator|.
name|getTTClients
argument_list|()
decl_stmt|;
comment|//Stopping all TTs
for|for
control|(
name|TTClient
name|tt
range|:
name|tts
control|)
block|{
name|tt
operator|.
name|kill
argument_list|()
expr_stmt|;
block|}
comment|//Starting all TTs
for|for
control|(
name|TTClient
name|tt
range|:
name|tts
control|)
block|{
name|tt
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|String
name|input
init|=
literal|"This will be the content of\n"
operator|+
literal|"distributed cache\n"
decl_stmt|;
comment|//Creating the path with the file
name|DataOutputStream
name|file
init|=
name|UtilsForTests
operator|.
name|createTmpFileDFS
argument_list|(
name|dfs
argument_list|,
name|URIPATH
argument_list|,
name|permission
argument_list|,
name|input
argument_list|)
decl_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|tearDown ()
specifier|public
specifier|static
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|cluster
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|dfs
operator|.
name|delete
argument_list|(
name|URIPATH
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|TTClient
argument_list|>
name|tts
init|=
name|cluster
operator|.
name|getTTClients
argument_list|()
decl_stmt|;
comment|//Stopping all TTs
for|for
control|(
name|TTClient
name|tt
range|:
name|tts
control|)
block|{
name|tt
operator|.
name|kill
argument_list|()
expr_stmt|;
block|}
comment|//Starting all TTs
for|for
control|(
name|TTClient
name|tt
range|:
name|tts
control|)
block|{
name|tt
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
comment|/**    * This tests Distributed Cache for private file    * @param none    * @return void    */
DECL|method|testDistributedCache ()
specifier|public
name|void
name|testDistributedCache
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
name|cluster
operator|.
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|JTProtocol
name|wovenClient
init|=
name|cluster
operator|.
name|getJTClient
argument_list|()
operator|.
name|getProxy
argument_list|()
decl_stmt|;
comment|//This counter will check for count of a loop,
comment|//which might become infinite.
name|int
name|count
init|=
literal|0
decl_stmt|;
name|SleepJob
name|job
init|=
operator|new
name|SleepJob
argument_list|()
decl_stmt|;
name|job
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Job
name|slpJob
init|=
name|job
operator|.
name|createJob
argument_list|(
literal|5
argument_list|,
literal|1
argument_list|,
literal|1000
argument_list|,
literal|1000
argument_list|,
literal|100
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|DistributedCache
operator|.
name|createSymlink
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|URI
name|uri
init|=
name|URI
operator|.
name|create
argument_list|(
name|uriPath
argument_list|)
decl_stmt|;
name|DistributedCache
operator|.
name|addCacheFile
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|JobConf
name|jconf
init|=
operator|new
name|JobConf
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|//Controls the job till all verification is done
name|FinishTaskControlAction
operator|.
name|configureControlActionForJob
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|//Submitting the job
name|slpJob
operator|.
name|submit
argument_list|()
expr_stmt|;
name|RunningJob
name|rJob
init|=
name|cluster
operator|.
name|getJTClient
argument_list|()
operator|.
name|getClient
argument_list|()
operator|.
name|getJob
argument_list|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|JobID
operator|.
name|downgrade
argument_list|(
name|slpJob
operator|.
name|getJobID
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|JobStatus
index|[]
name|jobStatus
init|=
name|client
operator|.
name|getAllJobs
argument_list|()
decl_stmt|;
name|String
name|userName
init|=
name|jobStatus
index|[
literal|0
index|]
operator|.
name|getUsername
argument_list|()
decl_stmt|;
name|TTClient
name|tClient
init|=
literal|null
decl_stmt|;
name|JobInfo
name|jInfo
init|=
name|wovenClient
operator|.
name|getJobInfo
argument_list|(
name|rJob
operator|.
name|getID
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"jInfo is :"
operator|+
name|jInfo
argument_list|)
expr_stmt|;
comment|//Assert if jobInfo is null
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"jobInfo is null"
argument_list|,
name|jInfo
argument_list|)
expr_stmt|;
comment|//Wait for the job to start running.
name|count
operator|=
literal|0
expr_stmt|;
while|while
condition|(
name|jInfo
operator|.
name|getStatus
argument_list|()
operator|.
name|getRunState
argument_list|()
operator|!=
name|JobStatus
operator|.
name|RUNNING
condition|)
block|{
name|UtilsForTests
operator|.
name|waitFor
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
name|jInfo
operator|=
name|wovenClient
operator|.
name|getJobInfo
argument_list|(
name|rJob
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
comment|//If the count goes beyond a point, then Assert; This is to avoid
comment|//infinite loop under unforeseen circumstances.
if|if
condition|(
name|count
operator|>
literal|10
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"job has not reached running state for more than"
operator|+
literal|"100 seconds. Failing at this point"
argument_list|)
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"job id is :"
operator|+
name|rJob
operator|.
name|getID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|TaskInfo
index|[]
name|taskInfos
init|=
name|cluster
operator|.
name|getJTClient
argument_list|()
operator|.
name|getProxy
argument_list|()
operator|.
name|getTaskInfo
argument_list|(
name|rJob
operator|.
name|getID
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|distCacheFileIsFound
decl_stmt|;
for|for
control|(
name|TaskInfo
name|taskInfo
range|:
name|taskInfos
control|)
block|{
name|distCacheFileIsFound
operator|=
literal|false
expr_stmt|;
name|String
index|[]
name|taskTrackers
init|=
name|taskInfo
operator|.
name|getTaskTrackers
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|taskTracker
range|:
name|taskTrackers
control|)
block|{
comment|//Getting the exact FQDN of the tasktracker from
comment|//the tasktracker string.
name|taskTracker
operator|=
name|UtilsForTests
operator|.
name|getFQDNofTT
argument_list|(
name|taskTracker
argument_list|)
expr_stmt|;
name|tClient
operator|=
name|cluster
operator|.
name|getTTClient
argument_list|(
name|taskTracker
argument_list|)
expr_stmt|;
name|String
index|[]
name|localDirs
init|=
name|tClient
operator|.
name|getMapredLocalDirs
argument_list|()
decl_stmt|;
name|int
name|distributedFileCount
init|=
literal|0
decl_stmt|;
name|String
name|localDirOnly
init|=
literal|null
decl_stmt|;
name|boolean
name|FileNotPresentForThisDirectoryPath
init|=
literal|false
decl_stmt|;
comment|//Go to every single path
for|for
control|(
name|String
name|localDir
range|:
name|localDirs
control|)
block|{
name|FileNotPresentForThisDirectoryPath
operator|=
literal|false
expr_stmt|;
name|localDirOnly
operator|=
name|localDir
expr_stmt|;
comment|//Public Distributed cache will always be stored under
comment|//mapred.local.dir/tasktracker/archive
name|localDirOnly
operator|=
name|localDir
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
name|TaskTracker
operator|.
name|SUBDIR
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
name|userName
expr_stmt|;
comment|//Private Distributed cache will always be stored under
comment|//mapre.local.dir/taskTracker/<username>/distcache
comment|//Checking for username directory to check if it has the
comment|//proper permissions
name|localDir
operator|=
name|localDir
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
name|TaskTracker
operator|.
name|getPrivateDistributedCacheDir
argument_list|(
name|userName
argument_list|)
expr_stmt|;
name|FileStatus
name|fileStatusMapredLocalDirUserName
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fileStatusMapredLocalDirUserName
operator|=
name|tClient
operator|.
name|getFileStatus
argument_list|(
name|localDirOnly
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"LocalDirOnly :"
operator|+
name|localDirOnly
operator|+
literal|" not found"
argument_list|)
expr_stmt|;
name|FileNotPresentForThisDirectoryPath
operator|=
literal|true
expr_stmt|;
block|}
comment|//File will only be stored under one of the mapred.lcoal.dir
comment|//If other paths were hit, just continue
if|if
condition|(
name|FileNotPresentForThisDirectoryPath
condition|)
continue|continue;
name|Path
name|pathMapredLocalDirUserName
init|=
name|fileStatusMapredLocalDirUserName
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|FsPermission
name|fsPermMapredLocalDirUserName
init|=
name|fileStatusMapredLocalDirUserName
operator|.
name|getPermission
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Directory Permission is not 700"
argument_list|,
name|fsPermMapredLocalDirUserName
operator|.
name|equals
argument_list|(
operator|new
name|FsPermission
argument_list|(
literal|"700"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|//Get file status of all the directories
comment|//and files under that path.
name|FileStatus
index|[]
name|fileStatuses
init|=
name|tClient
operator|.
name|listStatus
argument_list|(
name|localDir
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
for|for
control|(
name|FileStatus
name|fileStatus
range|:
name|fileStatuses
control|)
block|{
name|Path
name|path
init|=
name|fileStatus
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"path is :"
operator|+
name|path
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|//Checking if the received path ends with
comment|//the distributed filename
name|distCacheFileIsFound
operator|=
operator|(
name|path
operator|.
name|toString
argument_list|()
operator|)
operator|.
name|endsWith
argument_list|(
name|distributedFileName
argument_list|)
expr_stmt|;
comment|//If file is found, check for its permission.
comment|//Since the file is found break out of loop
if|if
condition|(
name|distCacheFileIsFound
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"PATH found is :"
operator|+
name|path
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|distributedFileCount
operator|++
expr_stmt|;
name|String
name|filename
init|=
name|path
operator|.
name|getName
argument_list|()
decl_stmt|;
name|FsPermission
name|fsPerm
init|=
name|fileStatus
operator|.
name|getPermission
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"File Permission is not 777"
argument_list|,
name|fsPerm
operator|.
name|equals
argument_list|(
operator|new
name|FsPermission
argument_list|(
literal|"777"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Distributed File count is :"
operator|+
name|distributedFileCount
argument_list|)
expr_stmt|;
if|if
condition|(
name|distributedFileCount
operator|>
literal|1
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"The distributed cache file is more than one"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|distributedFileCount
operator|<
literal|1
condition|)
name|Assert
operator|.
name|fail
argument_list|(
literal|"The distributed cache file is less than one"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|distCacheFileIsFound
condition|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"The distributed cache file does not exist"
argument_list|,
name|distCacheFileIsFound
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
comment|//Allow the job to continue through MR control job.
for|for
control|(
name|TaskInfo
name|taskInfoRemaining
range|:
name|taskInfos
control|)
block|{
name|FinishTaskControlAction
name|action
init|=
operator|new
name|FinishTaskControlAction
argument_list|(
name|TaskID
operator|.
name|downgrade
argument_list|(
name|taskInfoRemaining
operator|.
name|getTaskID
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|TTClient
argument_list|>
name|tts
init|=
name|cluster
operator|.
name|getTTClients
argument_list|()
decl_stmt|;
for|for
control|(
name|TTClient
name|cli
range|:
name|tts
control|)
block|{
name|cli
operator|.
name|getProxy
argument_list|()
operator|.
name|sendAction
argument_list|(
name|action
argument_list|)
expr_stmt|;
block|}
block|}
comment|//Killing the job because all the verification needed
comment|//for this testcase is completed.
name|rJob
operator|.
name|killJob
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

