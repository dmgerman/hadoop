begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager
package|package
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
name|nodemanager
package|;
end_package

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertEquals
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
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|FileContext
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ContainerId
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
name|ContainerLaunchContext
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
name|nodemanager
operator|.
name|ContainerExecutor
operator|.
name|Signal
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|container
operator|.
name|Container
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
comment|/**  * This is intended to test the LinuxContainerExecutor code, but because of  * some security restrictions this can only be done with some special setup  * first.  *<br><ol>  *<li>Compile the code with container-executor.conf.dir set to the location you  * want for testing.  *<br><pre><code>  *> mvn clean install -Pnative -Dcontainer-executor.conf.dir=/etc/hadoop  *                          -DskipTests  *</code></pre>  *   *<li>Set up<code>${container-executor.conf.dir}/container-executor.cfg</code>  * container-executor.cfg needs to be owned by root and have in it the proper  * config values.  *<br><pre><code>  *> cat /etc/hadoop/container-executor.cfg  * yarn.nodemanager.linux-container-executor.group=mapred  * #depending on the user id of the application.submitter option  * min.user.id=1  *> sudo chown root:root /etc/hadoop/container-executor.cfg  *> sudo chmod 444 /etc/hadoop/container-executor.cfg  *</code></pre>  *   *<li>Move the binary and set proper permissions on it. It needs to be owned   * by root, the group needs to be the group configured in container-executor.cfg,   * and it needs the setuid bit set. (The build will also overwrite it so you  * need to move it to a place that you can support it.   *<br><pre><code>  *> cp ./hadoop-mapreduce-project/hadoop-yarn/hadoop-yarn-server/hadoop-yarn-server-nodemanager/src/main/c/container-executor/container-executor /tmp/  *> sudo chown root:mapred /tmp/container-executor  *> sudo chmod 4550 /tmp/container-executor  *</code></pre>  *   *<li>Run the tests with the execution enabled (The user you run the tests as  * needs to be part of the group from the config.  *<br><pre><code>  * mvn test -Dtest=TestLinuxContainerExecutor -Dapplication.submitter=nobody -Dcontainer-executor.path=/tmp/container-executor  *</code></pre>  *</ol>  */
end_comment

begin_class
DECL|class|TestLinuxContainerExecutor
specifier|public
class|class
name|TestLinuxContainerExecutor
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
name|TestLinuxContainerExecutor
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|workSpace
specifier|private
specifier|static
name|File
name|workSpace
init|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
name|TestLinuxContainerExecutor
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"-workSpace"
argument_list|)
decl_stmt|;
DECL|field|exec
specifier|private
name|LinuxContainerExecutor
name|exec
init|=
literal|null
decl_stmt|;
DECL|field|appSubmitter
specifier|private
name|String
name|appSubmitter
init|=
literal|null
decl_stmt|;
DECL|field|dirsHandler
specifier|private
name|LocalDirsHandlerService
name|dirsHandler
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|FileContext
name|files
init|=
name|FileContext
operator|.
name|getLocalFSFileContext
argument_list|()
decl_stmt|;
name|Path
name|workSpacePath
init|=
operator|new
name|Path
argument_list|(
name|workSpace
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|files
operator|.
name|mkdir
argument_list|(
name|workSpacePath
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|workSpace
operator|.
name|setReadable
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|workSpace
operator|.
name|setExecutable
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|workSpace
operator|.
name|setWritable
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|File
name|localDir
init|=
operator|new
name|File
argument_list|(
name|workSpace
operator|.
name|getAbsoluteFile
argument_list|()
argument_list|,
literal|"localDir"
argument_list|)
decl_stmt|;
name|files
operator|.
name|mkdir
argument_list|(
operator|new
name|Path
argument_list|(
name|localDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|,
operator|new
name|FsPermission
argument_list|(
literal|"777"
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|File
name|logDir
init|=
operator|new
name|File
argument_list|(
name|workSpace
operator|.
name|getAbsoluteFile
argument_list|()
argument_list|,
literal|"logDir"
argument_list|)
decl_stmt|;
name|files
operator|.
name|mkdir
argument_list|(
operator|new
name|Path
argument_list|(
name|logDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|,
operator|new
name|FsPermission
argument_list|(
literal|"777"
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|String
name|exec_path
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"container-executor.path"
argument_list|)
decl_stmt|;
if|if
condition|(
name|exec_path
operator|!=
literal|null
operator|&&
operator|!
name|exec_path
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Setting "
operator|+
name|YarnConfiguration
operator|.
name|NM_LINUX_CONTAINER_EXECUTOR_PATH
operator|+
literal|"="
operator|+
name|exec_path
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LINUX_CONTAINER_EXECUTOR_PATH
argument_list|,
name|exec_path
argument_list|)
expr_stmt|;
name|exec
operator|=
operator|new
name|LinuxContainerExecutor
argument_list|()
expr_stmt|;
name|exec
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOCAL_DIRS
argument_list|,
name|localDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOG_DIRS
argument_list|,
name|logDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|dirsHandler
operator|=
operator|new
name|LocalDirsHandlerService
argument_list|()
expr_stmt|;
name|dirsHandler
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
name|appSubmitter
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"application.submitter"
argument_list|)
expr_stmt|;
if|if
condition|(
name|appSubmitter
operator|==
literal|null
operator|||
name|appSubmitter
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|appSubmitter
operator|=
literal|"nobody"
expr_stmt|;
block|}
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
name|FileContext
operator|.
name|getLocalFSFileContext
argument_list|()
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|workSpace
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|shouldRun ()
specifier|private
name|boolean
name|shouldRun
parameter_list|()
block|{
if|if
condition|(
name|exec
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Not running test because container-executor.path is not set"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|writeScriptFile (String .... cmd)
specifier|private
name|String
name|writeScriptFile
parameter_list|(
name|String
modifier|...
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|f
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"TestLinuxContainerExecutor"
argument_list|,
literal|".sh"
argument_list|)
decl_stmt|;
name|f
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
name|PrintWriter
name|p
init|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|f
argument_list|)
argument_list|)
decl_stmt|;
name|p
operator|.
name|println
argument_list|(
literal|"#!/bin/sh"
argument_list|)
expr_stmt|;
name|p
operator|.
name|print
argument_list|(
literal|"exec"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|part
range|:
name|cmd
control|)
block|{
name|p
operator|.
name|print
argument_list|(
literal|" '"
argument_list|)
expr_stmt|;
name|p
operator|.
name|print
argument_list|(
name|part
operator|.
name|replace
argument_list|(
literal|"\\"
argument_list|,
literal|"\\\\"
argument_list|)
operator|.
name|replace
argument_list|(
literal|"'"
argument_list|,
literal|"\\'"
argument_list|)
argument_list|)
expr_stmt|;
name|p
operator|.
name|print
argument_list|(
literal|"'"
argument_list|)
expr_stmt|;
block|}
name|p
operator|.
name|println
argument_list|()
expr_stmt|;
name|p
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|f
operator|.
name|getAbsolutePath
argument_list|()
return|;
block|}
DECL|field|id
specifier|private
name|int
name|id
init|=
literal|0
decl_stmt|;
DECL|method|getNextId ()
specifier|private
specifier|synchronized
name|int
name|getNextId
parameter_list|()
block|{
name|id
operator|+=
literal|1
expr_stmt|;
return|return
name|id
return|;
block|}
DECL|method|getNextContainerId ()
specifier|private
name|ContainerId
name|getNextContainerId
parameter_list|()
block|{
name|ContainerId
name|cId
init|=
name|mock
argument_list|(
name|ContainerId
operator|.
name|class
argument_list|)
decl_stmt|;
name|String
name|id
init|=
literal|"CONTAINER_"
operator|+
name|getNextId
argument_list|()
decl_stmt|;
name|when
argument_list|(
name|cId
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|id
argument_list|)
expr_stmt|;
return|return
name|cId
return|;
block|}
DECL|method|runAndBlock (String .... cmd)
specifier|private
name|int
name|runAndBlock
parameter_list|(
name|String
modifier|...
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|runAndBlock
argument_list|(
name|getNextContainerId
argument_list|()
argument_list|,
name|cmd
argument_list|)
return|;
block|}
DECL|method|runAndBlock (ContainerId cId, String ... cmd)
specifier|private
name|int
name|runAndBlock
parameter_list|(
name|ContainerId
name|cId
parameter_list|,
name|String
modifier|...
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|appId
init|=
literal|"APP_"
operator|+
name|getNextId
argument_list|()
decl_stmt|;
name|Container
name|container
init|=
name|mock
argument_list|(
name|Container
operator|.
name|class
argument_list|)
decl_stmt|;
name|ContainerLaunchContext
name|context
init|=
name|mock
argument_list|(
name|ContainerLaunchContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|when
argument_list|(
name|container
operator|.
name|getContainerID
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|cId
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|container
operator|.
name|getLaunchContext
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|context
operator|.
name|getEnvironment
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|env
argument_list|)
expr_stmt|;
name|String
name|script
init|=
name|writeScriptFile
argument_list|(
name|cmd
argument_list|)
decl_stmt|;
name|Path
name|scriptPath
init|=
operator|new
name|Path
argument_list|(
name|script
argument_list|)
decl_stmt|;
name|Path
name|tokensPath
init|=
operator|new
name|Path
argument_list|(
literal|"/dev/null"
argument_list|)
decl_stmt|;
name|Path
name|workDir
init|=
operator|new
name|Path
argument_list|(
name|workSpace
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|pidFile
init|=
operator|new
name|Path
argument_list|(
name|workDir
argument_list|,
literal|"pid.txt"
argument_list|)
decl_stmt|;
name|exec
operator|.
name|activateContainer
argument_list|(
name|cId
argument_list|,
name|pidFile
argument_list|)
expr_stmt|;
return|return
name|exec
operator|.
name|launchContainer
argument_list|(
name|container
argument_list|,
name|scriptPath
argument_list|,
name|tokensPath
argument_list|,
name|appSubmitter
argument_list|,
name|appId
argument_list|,
name|workDir
argument_list|,
name|dirsHandler
operator|.
name|getLocalDirs
argument_list|()
argument_list|,
name|dirsHandler
operator|.
name|getLogDirs
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testContainerLaunch ()
specifier|public
name|void
name|testContainerLaunch
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|shouldRun
argument_list|()
condition|)
block|{
return|return;
block|}
name|File
name|touchFile
init|=
operator|new
name|File
argument_list|(
name|workSpace
argument_list|,
literal|"touch-file"
argument_list|)
decl_stmt|;
name|int
name|ret
init|=
name|runAndBlock
argument_list|(
literal|"touch"
argument_list|,
name|touchFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ret
argument_list|)
expr_stmt|;
name|FileStatus
name|fileStatus
init|=
name|FileContext
operator|.
name|getLocalFSFileContext
argument_list|()
operator|.
name|getFileStatus
argument_list|(
operator|new
name|Path
argument_list|(
name|touchFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|appSubmitter
argument_list|,
name|fileStatus
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testContainerKill ()
specifier|public
name|void
name|testContainerKill
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|shouldRun
argument_list|()
condition|)
block|{
return|return;
block|}
specifier|final
name|ContainerId
name|sleepId
init|=
name|getNextContainerId
argument_list|()
decl_stmt|;
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|runAndBlock
argument_list|(
name|sleepId
argument_list|,
literal|"sleep"
argument_list|,
literal|"100"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Caught exception while running sleep"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
empty_stmt|;
block|}
decl_stmt|;
name|t
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|//If it does not exit we shouldn't block the test.
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|t
operator|.
name|isAlive
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|pid
init|=
literal|null
decl_stmt|;
name|int
name|count
init|=
literal|10
decl_stmt|;
while|while
condition|(
operator|(
name|pid
operator|=
name|exec
operator|.
name|getProcessId
argument_list|(
name|sleepId
argument_list|)
operator|)
operator|==
literal|null
operator|&&
name|count
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Sleeping for 200 ms before checking for pid "
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|200
argument_list|)
expr_stmt|;
name|count
operator|--
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
name|pid
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Going to killing the process."
argument_list|)
expr_stmt|;
name|exec
operator|.
name|signalContainer
argument_list|(
name|appSubmitter
argument_list|,
name|pid
argument_list|,
name|Signal
operator|.
name|TERM
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"sleeping for 100ms to let the sleep be killed"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|t
operator|.
name|isAlive
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

