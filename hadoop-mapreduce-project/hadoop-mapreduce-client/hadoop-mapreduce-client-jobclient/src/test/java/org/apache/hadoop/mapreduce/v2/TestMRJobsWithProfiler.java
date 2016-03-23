begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2
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
package|;
end_package

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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|mapred
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
name|mapreduce
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
name|mapreduce
operator|.
name|TaskAttemptID
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
name|api
operator|.
name|records
operator|.
name|JobId
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
name|api
operator|.
name|records
operator|.
name|ApplicationId
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
name|conf
operator|.
name|YarnConfiguration
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
name|server
operator|.
name|resourcemanager
operator|.
name|rmapp
operator|.
name|RMAppState
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
name|Test
import|;
end_import

begin_class
DECL|class|TestMRJobsWithProfiler
specifier|public
class|class
name|TestMRJobsWithProfiler
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestMRJobsWithProfiler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|TERMINAL_RM_APP_STATES
specifier|private
specifier|static
specifier|final
name|EnumSet
argument_list|<
name|RMAppState
argument_list|>
name|TERMINAL_RM_APP_STATES
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|RMAppState
operator|.
name|FINISHED
argument_list|,
name|RMAppState
operator|.
name|FAILED
argument_list|,
name|RMAppState
operator|.
name|KILLED
argument_list|)
decl_stmt|;
DECL|field|PROFILED_TASK_ID
specifier|private
specifier|static
specifier|final
name|int
name|PROFILED_TASK_ID
init|=
literal|1
decl_stmt|;
DECL|field|mrCluster
specifier|private
specifier|static
name|MiniMRYarnCluster
name|mrCluster
decl_stmt|;
DECL|field|CONF
specifier|private
specifier|static
specifier|final
name|Configuration
name|CONF
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|field|localFs
specifier|private
specifier|static
specifier|final
name|FileSystem
name|localFs
decl_stmt|;
static|static
block|{
try|try
block|{
name|localFs
operator|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|CONF
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|io
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"problem getting local fs"
argument_list|,
name|io
argument_list|)
throw|;
block|}
block|}
DECL|field|TEST_ROOT_DIR
specifier|private
specifier|static
specifier|final
name|Path
name|TEST_ROOT_DIR
init|=
operator|new
name|Path
argument_list|(
literal|"target"
argument_list|,
name|TestMRJobs
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"-tmpDir"
argument_list|)
operator|.
name|makeQualified
argument_list|(
name|localFs
operator|.
name|getUri
argument_list|()
argument_list|,
name|localFs
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|APP_JAR
specifier|private
specifier|static
specifier|final
name|Path
name|APP_JAR
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"MRAppJar.jar"
argument_list|)
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setup ()
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|IOException
block|{
if|if
condition|(
operator|!
operator|(
operator|new
name|File
argument_list|(
name|MiniMRYarnCluster
operator|.
name|APPJAR
argument_list|)
operator|)
operator|.
name|exists
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"MRAppJar "
operator|+
name|MiniMRYarnCluster
operator|.
name|APPJAR
operator|+
literal|" not found. Not running test."
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|mrCluster
operator|==
literal|null
condition|)
block|{
name|mrCluster
operator|=
operator|new
name|MiniMRYarnCluster
argument_list|(
name|TestMRJobsWithProfiler
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|mrCluster
operator|.
name|init
argument_list|(
name|CONF
argument_list|)
expr_stmt|;
name|mrCluster
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|// Copy MRAppJar and make it private. TODO: FIXME. This is a hack to
comment|// workaround the absent public discache.
name|localFs
operator|.
name|copyFromLocalFile
argument_list|(
operator|new
name|Path
argument_list|(
name|MiniMRYarnCluster
operator|.
name|APPJAR
argument_list|)
argument_list|,
name|APP_JAR
argument_list|)
expr_stmt|;
name|localFs
operator|.
name|setPermission
argument_list|(
name|APP_JAR
argument_list|,
operator|new
name|FsPermission
argument_list|(
literal|"700"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|tearDown ()
specifier|public
specifier|static
name|void
name|tearDown
parameter_list|()
block|{
if|if
condition|(
operator|!
operator|(
operator|new
name|File
argument_list|(
name|MiniMRYarnCluster
operator|.
name|APPJAR
argument_list|)
operator|)
operator|.
name|exists
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"MRAppJar "
operator|+
name|MiniMRYarnCluster
operator|.
name|APPJAR
operator|+
literal|" not found. Not running test."
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|mrCluster
operator|!=
literal|null
condition|)
block|{
name|mrCluster
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|150000
argument_list|)
DECL|method|testDefaultProfiler ()
specifier|public
name|void
name|testDefaultProfiler
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting testDefaultProfiler"
argument_list|)
expr_stmt|;
name|testProfilerInternal
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|150000
argument_list|)
DECL|method|testDifferentProfilers ()
specifier|public
name|void
name|testDifferentProfilers
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting testDefaultProfiler"
argument_list|)
expr_stmt|;
name|testProfilerInternal
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|testProfilerInternal (boolean useDefault)
specifier|private
name|void
name|testProfilerInternal
parameter_list|(
name|boolean
name|useDefault
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
operator|(
operator|new
name|File
argument_list|(
name|MiniMRYarnCluster
operator|.
name|APPJAR
argument_list|)
operator|)
operator|.
name|exists
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"MRAppJar "
operator|+
name|MiniMRYarnCluster
operator|.
name|APPJAR
operator|+
literal|" not found. Not running test."
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|SleepJob
name|sleepJob
init|=
operator|new
name|SleepJob
argument_list|()
decl_stmt|;
specifier|final
name|JobConf
name|sleepConf
init|=
operator|new
name|JobConf
argument_list|(
name|mrCluster
operator|.
name|getConfig
argument_list|()
argument_list|)
decl_stmt|;
name|sleepConf
operator|.
name|setProfileEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|sleepConf
operator|.
name|setProfileTaskRange
argument_list|(
literal|true
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|PROFILED_TASK_ID
argument_list|)
argument_list|)
expr_stmt|;
name|sleepConf
operator|.
name|setProfileTaskRange
argument_list|(
literal|false
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|PROFILED_TASK_ID
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|useDefault
condition|)
block|{
comment|// use hprof for map to profile.out
name|sleepConf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|TASK_MAP_PROFILE_PARAMS
argument_list|,
literal|"-agentlib:hprof=cpu=samples,heap=sites,force=n,thread=y,verbose=n,"
operator|+
literal|"file=%s"
argument_list|)
expr_stmt|;
comment|// use Xprof for reduce to stdout
name|sleepConf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|TASK_REDUCE_PROFILE_PARAMS
argument_list|,
literal|"-Xprof"
argument_list|)
expr_stmt|;
block|}
name|sleepJob
operator|.
name|setConf
argument_list|(
name|sleepConf
argument_list|)
expr_stmt|;
comment|// 2-map-2-reduce SleepJob
specifier|final
name|Job
name|job
init|=
name|sleepJob
operator|.
name|createJob
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|500
argument_list|,
literal|1
argument_list|,
literal|500
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|job
operator|.
name|setJarByClass
argument_list|(
name|SleepJob
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|addFileToClassPath
argument_list|(
name|APP_JAR
argument_list|)
expr_stmt|;
comment|// The AppMaster jar itself.
name|job
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|JobId
name|jobId
init|=
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|job
operator|.
name|getJobID
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|ApplicationId
name|appID
init|=
name|jobId
operator|.
name|getAppId
argument_list|()
decl_stmt|;
name|int
name|pollElapsed
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|pollElapsed
operator|+=
literal|1000
expr_stmt|;
if|if
condition|(
name|TERMINAL_RM_APP_STATES
operator|.
name|contains
argument_list|(
name|mrCluster
operator|.
name|getResourceManager
argument_list|()
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMApps
argument_list|()
operator|.
name|get
argument_list|(
name|appID
argument_list|)
operator|.
name|getState
argument_list|()
argument_list|)
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|pollElapsed
operator|>=
literal|60000
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"application did not reach terminal state within 60 seconds"
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|RMAppState
operator|.
name|FINISHED
argument_list|,
name|mrCluster
operator|.
name|getResourceManager
argument_list|()
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMApps
argument_list|()
operator|.
name|get
argument_list|(
name|appID
argument_list|)
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
comment|// Job finished, verify logs
comment|//
specifier|final
name|Configuration
name|nmConf
init|=
name|mrCluster
operator|.
name|getNodeManager
argument_list|(
literal|0
argument_list|)
operator|.
name|getConfig
argument_list|()
decl_stmt|;
specifier|final
name|String
name|appIdStr
init|=
name|appID
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
name|String
name|appIdSuffix
init|=
name|appIdStr
operator|.
name|substring
argument_list|(
literal|"application_"
operator|.
name|length
argument_list|()
argument_list|,
name|appIdStr
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|String
name|containerGlob
init|=
literal|"container_"
operator|+
name|appIdSuffix
operator|+
literal|"_*_*"
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|TaskAttemptID
argument_list|,
name|Path
argument_list|>
name|taLogDirs
init|=
operator|new
name|HashMap
argument_list|<
name|TaskAttemptID
argument_list|,
name|Path
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|Pattern
name|taskPattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|".*Task:(attempt_"
operator|+
name|appIdSuffix
operator|+
literal|"_[rm]_"
operator|+
literal|"[0-9]+_[0-9]+).*"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|logDir
range|:
name|nmConf
operator|.
name|getTrimmedStrings
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOG_DIRS
argument_list|)
control|)
block|{
comment|// filter out MRAppMaster and create attemptId->logDir map
comment|//
for|for
control|(
name|FileStatus
name|fileStatus
range|:
name|localFs
operator|.
name|globStatus
argument_list|(
operator|new
name|Path
argument_list|(
name|logDir
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
name|appIdStr
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
name|containerGlob
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
name|TaskLog
operator|.
name|LogName
operator|.
name|SYSLOG
argument_list|)
argument_list|)
control|)
block|{
specifier|final
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|localFs
operator|.
name|open
argument_list|(
name|fileStatus
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Matcher
name|m
init|=
name|taskPattern
operator|.
name|matcher
argument_list|(
name|line
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
comment|// found Task done message
name|taLogDirs
operator|.
name|put
argument_list|(
name|TaskAttemptID
operator|.
name|forName
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|,
name|fileStatus
operator|.
name|getPath
argument_list|()
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
name|br
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|taLogDirs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// all 4 attempts found
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|TaskAttemptID
argument_list|,
name|Path
argument_list|>
name|dirEntry
range|:
name|taLogDirs
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|TaskAttemptID
name|tid
init|=
name|dirEntry
operator|.
name|getKey
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|profilePath
init|=
operator|new
name|Path
argument_list|(
name|dirEntry
operator|.
name|getValue
argument_list|()
argument_list|,
name|TaskLog
operator|.
name|LogName
operator|.
name|PROFILE
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|stdoutPath
init|=
operator|new
name|Path
argument_list|(
name|dirEntry
operator|.
name|getValue
argument_list|()
argument_list|,
name|TaskLog
operator|.
name|LogName
operator|.
name|STDOUT
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|useDefault
operator|||
name|tid
operator|.
name|getTaskType
argument_list|()
operator|==
name|TaskType
operator|.
name|MAP
condition|)
block|{
if|if
condition|(
name|tid
operator|.
name|getTaskID
argument_list|()
operator|.
name|getId
argument_list|()
operator|==
name|PROFILED_TASK_ID
condition|)
block|{
comment|// verify profile.out
specifier|final
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|localFs
operator|.
name|open
argument_list|(
name|profilePath
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|String
name|line
init|=
name|br
operator|.
name|readLine
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"No hprof content found!"
argument_list|,
name|line
operator|!=
literal|null
operator|&&
name|line
operator|.
name|startsWith
argument_list|(
literal|"JAVA PROFILE"
argument_list|)
argument_list|)
expr_stmt|;
name|br
operator|.
name|close
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|localFs
operator|.
name|getFileStatus
argument_list|(
name|stdoutPath
argument_list|)
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Assert
operator|.
name|assertFalse
argument_list|(
literal|"hprof file should not exist"
argument_list|,
name|localFs
operator|.
name|exists
argument_list|(
name|profilePath
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|Assert
operator|.
name|assertFalse
argument_list|(
literal|"hprof file should not exist"
argument_list|,
name|localFs
operator|.
name|exists
argument_list|(
name|profilePath
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|tid
operator|.
name|getTaskID
argument_list|()
operator|.
name|getId
argument_list|()
operator|==
name|PROFILED_TASK_ID
condition|)
block|{
comment|// reducer is profiled with Xprof
specifier|final
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|localFs
operator|.
name|open
argument_list|(
name|stdoutPath
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|flatProfFound
init|=
literal|false
decl_stmt|;
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
literal|"Flat profile"
argument_list|)
condition|)
block|{
name|flatProfFound
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
name|br
operator|.
name|close
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Xprof flat profile not found!"
argument_list|,
name|flatProfFound
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|localFs
operator|.
name|getFileStatus
argument_list|(
name|stdoutPath
argument_list|)
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

