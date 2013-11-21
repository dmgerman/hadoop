begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.applications.distributedshell
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|applications
operator|.
name|distributedshell
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|FileReader
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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
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
name|util
operator|.
name|JarFinder
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
name|Shell
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
name|ApplicationReport
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
name|YarnApplicationState
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
name|client
operator|.
name|api
operator|.
name|YarnClient
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
name|MiniYARNCluster
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
name|NodeManager
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
name|ContainerManagerImpl
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
name|scheduler
operator|.
name|ResourceScheduler
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
name|scheduler
operator|.
name|fifo
operator|.
name|FifoScheduler
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
DECL|class|TestDistributedShell
specifier|public
class|class
name|TestDistributedShell
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
name|TestDistributedShell
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|yarnCluster
specifier|protected
specifier|static
name|MiniYARNCluster
name|yarnCluster
init|=
literal|null
decl_stmt|;
DECL|field|conf
specifier|protected
specifier|static
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
DECL|field|APPMASTER_JAR
specifier|protected
specifier|static
name|String
name|APPMASTER_JAR
init|=
name|JarFinder
operator|.
name|getJar
argument_list|(
name|ApplicationMaster
operator|.
name|class
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
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting up YARN cluster"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER_MINIMUM_ALLOCATION_MB
argument_list|,
literal|128
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER
argument_list|,
name|FifoScheduler
operator|.
name|class
argument_list|,
name|ResourceScheduler
operator|.
name|class
argument_list|)
expr_stmt|;
if|if
condition|(
name|yarnCluster
operator|==
literal|null
condition|)
block|{
name|yarnCluster
operator|=
operator|new
name|MiniYARNCluster
argument_list|(
name|TestDistributedShell
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|yarnCluster
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|yarnCluster
operator|.
name|start
argument_list|()
expr_stmt|;
name|NodeManager
name|nm
init|=
name|yarnCluster
operator|.
name|getNodeManager
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|waitForNMToRegister
argument_list|(
name|nm
argument_list|)
expr_stmt|;
name|URL
name|url
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"yarn-site.xml"
argument_list|)
decl_stmt|;
if|if
condition|(
name|url
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Could not find 'yarn-site.xml' dummy file in classpath"
argument_list|)
throw|;
block|}
name|Configuration
name|yarnClusterConfig
init|=
name|yarnCluster
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|yarnClusterConfig
operator|.
name|set
argument_list|(
literal|"yarn.application.classpath"
argument_list|,
operator|new
name|File
argument_list|(
name|url
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
comment|//write the document to a buffer (not directly to the file, as that
comment|//can cause the file being written to get read -which will then fail.
name|ByteArrayOutputStream
name|bytesOut
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|yarnClusterConfig
operator|.
name|writeXml
argument_list|(
name|bytesOut
argument_list|)
expr_stmt|;
name|bytesOut
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//write the bytes to the file in the classpath
name|OutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|url
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|os
operator|.
name|write
argument_list|(
name|bytesOut
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"setup thread sleep interrupted. message="
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|IOException
block|{
if|if
condition|(
name|yarnCluster
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|yarnCluster
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|yarnCluster
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testDSShell ()
specifier|public
name|void
name|testDSShell
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args
init|=
block|{
literal|"--jar"
block|,
name|APPMASTER_JAR
block|,
literal|"--num_containers"
block|,
literal|"2"
block|,
literal|"--shell_command"
block|,
name|Shell
operator|.
name|WINDOWS
operator|?
literal|"dir"
operator|:
literal|"ls"
block|,
literal|"--master_memory"
block|,
literal|"512"
block|,
literal|"--master_vcores"
block|,
literal|"2"
block|,
literal|"--container_memory"
block|,
literal|"128"
block|,
literal|"--container_vcores"
block|,
literal|"1"
block|}
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Initializing DS Client"
argument_list|)
expr_stmt|;
specifier|final
name|Client
name|client
init|=
operator|new
name|Client
argument_list|(
operator|new
name|Configuration
argument_list|(
name|yarnCluster
operator|.
name|getConfig
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|initSuccess
init|=
name|client
operator|.
name|init
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|initSuccess
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Running DS Client"
argument_list|)
expr_stmt|;
specifier|final
name|AtomicBoolean
name|result
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
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
name|result
operator|.
name|set
argument_list|(
name|client
operator|.
name|run
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
name|YarnClient
name|yarnClient
init|=
name|YarnClient
operator|.
name|createYarnClient
argument_list|()
decl_stmt|;
name|yarnClient
operator|.
name|init
argument_list|(
operator|new
name|Configuration
argument_list|(
name|yarnCluster
operator|.
name|getConfig
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|yarnClient
operator|.
name|start
argument_list|()
expr_stmt|;
name|String
name|hostName
init|=
name|NetUtils
operator|.
name|getHostname
argument_list|()
decl_stmt|;
name|boolean
name|verified
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|!
name|verified
condition|)
block|{
name|List
argument_list|<
name|ApplicationReport
argument_list|>
name|apps
init|=
name|yarnClient
operator|.
name|getApplications
argument_list|()
decl_stmt|;
if|if
condition|(
name|apps
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|ApplicationReport
name|appReport
init|=
name|apps
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|appReport
operator|.
name|getHost
argument_list|()
operator|.
name|startsWith
argument_list|(
name|hostName
argument_list|)
operator|&&
name|appReport
operator|.
name|getRpcPort
argument_list|()
operator|==
operator|-
literal|1
condition|)
block|{
name|verified
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|appReport
operator|.
name|getYarnApplicationState
argument_list|()
operator|==
name|YarnApplicationState
operator|.
name|FINISHED
condition|)
block|{
break|break;
block|}
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|verified
argument_list|)
expr_stmt|;
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Client run completed. Result="
operator|+
name|result
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|result
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testDSShellWithCommands ()
specifier|public
name|void
name|testDSShellWithCommands
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args
init|=
block|{
literal|"--jar"
block|,
name|APPMASTER_JAR
block|,
literal|"--num_containers"
block|,
literal|"2"
block|,
literal|"--shell_command"
block|,
literal|"echo HADOOP YARN MAPREDUCE|wc -w"
block|,
literal|"--master_memory"
block|,
literal|"512"
block|,
literal|"--master_vcores"
block|,
literal|"2"
block|,
literal|"--container_memory"
block|,
literal|"128"
block|,
literal|"--container_vcores"
block|,
literal|"1"
block|}
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Initializing DS Client"
argument_list|)
expr_stmt|;
specifier|final
name|Client
name|client
init|=
operator|new
name|Client
argument_list|(
operator|new
name|Configuration
argument_list|(
name|yarnCluster
operator|.
name|getConfig
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|initSuccess
init|=
name|client
operator|.
name|init
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|initSuccess
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Running DS Client"
argument_list|)
expr_stmt|;
name|boolean
name|result
init|=
name|client
operator|.
name|run
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Client run completed. Result="
operator|+
name|result
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|expectedContent
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|expectedContent
operator|.
name|add
argument_list|(
literal|"3"
argument_list|)
expr_stmt|;
name|verifyContainerLog
argument_list|(
literal|2
argument_list|,
name|expectedContent
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testDSShellWithInvalidArgs ()
specifier|public
name|void
name|testDSShellWithInvalidArgs
parameter_list|()
throws|throws
name|Exception
block|{
name|Client
name|client
init|=
operator|new
name|Client
argument_list|(
operator|new
name|Configuration
argument_list|(
name|yarnCluster
operator|.
name|getConfig
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Initializing DS Client with no args"
argument_list|)
expr_stmt|;
try|try
block|{
name|client
operator|.
name|init
argument_list|(
operator|new
name|String
index|[]
block|{}
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Exception is expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"The throw exception is not expected"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"No args"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Initializing DS Client with no jar file"
argument_list|)
expr_stmt|;
try|try
block|{
name|String
index|[]
name|args
init|=
block|{
literal|"--num_containers"
block|,
literal|"2"
block|,
literal|"--shell_command"
block|,
name|Shell
operator|.
name|WINDOWS
operator|?
literal|"dir"
operator|:
literal|"ls"
block|,
literal|"--master_memory"
block|,
literal|"512"
block|,
literal|"--container_memory"
block|,
literal|"128"
block|}
decl_stmt|;
name|client
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Exception is expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"The throw exception is not expected"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"No jar"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Initializing DS Client with no shell command"
argument_list|)
expr_stmt|;
try|try
block|{
name|String
index|[]
name|args
init|=
block|{
literal|"--jar"
block|,
name|APPMASTER_JAR
block|,
literal|"--num_containers"
block|,
literal|"2"
block|,
literal|"--master_memory"
block|,
literal|"512"
block|,
literal|"--container_memory"
block|,
literal|"128"
block|}
decl_stmt|;
name|client
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Exception is expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"The throw exception is not expected"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"No shell command"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Initializing DS Client with invalid no. of containers"
argument_list|)
expr_stmt|;
try|try
block|{
name|String
index|[]
name|args
init|=
block|{
literal|"--jar"
block|,
name|APPMASTER_JAR
block|,
literal|"--num_containers"
block|,
literal|"-1"
block|,
literal|"--shell_command"
block|,
name|Shell
operator|.
name|WINDOWS
operator|?
literal|"dir"
operator|:
literal|"ls"
block|,
literal|"--master_memory"
block|,
literal|"512"
block|,
literal|"--container_memory"
block|,
literal|"128"
block|}
decl_stmt|;
name|client
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Exception is expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"The throw exception is not expected"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Invalid no. of containers"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Initializing DS Client with invalid no. of vcores"
argument_list|)
expr_stmt|;
try|try
block|{
name|String
index|[]
name|args
init|=
block|{
literal|"--jar"
block|,
name|APPMASTER_JAR
block|,
literal|"--num_containers"
block|,
literal|"2"
block|,
literal|"--shell_command"
block|,
name|Shell
operator|.
name|WINDOWS
operator|?
literal|"dir"
operator|:
literal|"ls"
block|,
literal|"--master_memory"
block|,
literal|"512"
block|,
literal|"--master_vcores"
block|,
literal|"-2"
block|,
literal|"--container_memory"
block|,
literal|"128"
block|,
literal|"--container_vcores"
block|,
literal|"1"
block|}
decl_stmt|;
name|client
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Exception is expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"The throw exception is not expected"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Invalid virtual cores specified"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|waitForNMToRegister (NodeManager nm)
specifier|protected
specifier|static
name|void
name|waitForNMToRegister
parameter_list|(
name|NodeManager
name|nm
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|attempt
init|=
literal|60
decl_stmt|;
name|ContainerManagerImpl
name|cm
init|=
operator|(
operator|(
name|ContainerManagerImpl
operator|)
name|nm
operator|.
name|getNMContext
argument_list|()
operator|.
name|getContainerManager
argument_list|()
operator|)
decl_stmt|;
while|while
condition|(
name|cm
operator|.
name|getBlockNewContainerRequestsStatus
argument_list|()
operator|&&
name|attempt
operator|--
operator|>
literal|0
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testContainerLaunchFailureHandling ()
specifier|public
name|void
name|testContainerLaunchFailureHandling
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args
init|=
block|{
literal|"--jar"
block|,
name|APPMASTER_JAR
block|,
literal|"--num_containers"
block|,
literal|"2"
block|,
literal|"--shell_command"
block|,
name|Shell
operator|.
name|WINDOWS
operator|?
literal|"dir"
operator|:
literal|"ls"
block|,
literal|"--master_memory"
block|,
literal|"512"
block|,
literal|"--container_memory"
block|,
literal|"128"
block|}
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Initializing DS Client"
argument_list|)
expr_stmt|;
name|Client
name|client
init|=
operator|new
name|Client
argument_list|(
name|ContainerLaunchFailAppMaster
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
operator|new
name|Configuration
argument_list|(
name|yarnCluster
operator|.
name|getConfig
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|initSuccess
init|=
name|client
operator|.
name|init
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|initSuccess
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Running DS Client"
argument_list|)
expr_stmt|;
name|boolean
name|result
init|=
name|client
operator|.
name|run
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Client run completed. Result="
operator|+
name|result
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testDebugFlag ()
specifier|public
name|void
name|testDebugFlag
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args
init|=
block|{
literal|"--jar"
block|,
name|APPMASTER_JAR
block|,
literal|"--num_containers"
block|,
literal|"2"
block|,
literal|"--shell_command"
block|,
name|Shell
operator|.
name|WINDOWS
operator|?
literal|"dir"
operator|:
literal|"ls"
block|,
literal|"--master_memory"
block|,
literal|"512"
block|,
literal|"--master_vcores"
block|,
literal|"2"
block|,
literal|"--container_memory"
block|,
literal|"128"
block|,
literal|"--container_vcores"
block|,
literal|"1"
block|,
literal|"--debug"
block|}
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Initializing DS Client"
argument_list|)
expr_stmt|;
name|Client
name|client
init|=
operator|new
name|Client
argument_list|(
operator|new
name|Configuration
argument_list|(
name|yarnCluster
operator|.
name|getConfig
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|client
operator|.
name|init
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Running DS Client"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|client
operator|.
name|run
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
DECL|method|verifyContainerLog (int containerNum, List<String> expectedContent)
name|verifyContainerLog
parameter_list|(
name|int
name|containerNum
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|expectedContent
parameter_list|)
block|{
name|File
name|logFolder
init|=
operator|new
name|File
argument_list|(
name|yarnCluster
operator|.
name|getNodeManager
argument_list|(
literal|0
argument_list|)
operator|.
name|getConfig
argument_list|()
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOG_DIRS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_LOG_DIRS
argument_list|)
argument_list|)
decl_stmt|;
name|File
index|[]
name|listOfFiles
init|=
name|logFolder
operator|.
name|listFiles
argument_list|()
decl_stmt|;
name|int
name|currentContainerLogFileIndex
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|listOfFiles
operator|.
name|length
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
if|if
condition|(
name|listOfFiles
index|[
name|i
index|]
operator|.
name|listFiles
argument_list|()
operator|.
name|length
operator|==
name|containerNum
operator|+
literal|1
condition|)
block|{
name|currentContainerLogFileIndex
operator|=
name|i
expr_stmt|;
break|break;
block|}
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|currentContainerLogFileIndex
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
name|File
index|[]
name|containerFiles
init|=
name|listOfFiles
index|[
name|currentContainerLogFileIndex
index|]
operator|.
name|listFiles
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|containerFiles
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|File
name|output
range|:
name|containerFiles
index|[
name|i
index|]
operator|.
name|listFiles
argument_list|()
control|)
block|{
if|if
condition|(
name|output
operator|.
name|getName
argument_list|()
operator|.
name|trim
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"stdout"
argument_list|)
condition|)
block|{
name|BufferedReader
name|br
init|=
literal|null
decl_stmt|;
try|try
block|{
name|String
name|sCurrentLine
decl_stmt|;
name|br
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|output
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|numOfline
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|sCurrentLine
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
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"The current is"
operator|+
name|sCurrentLine
argument_list|,
name|expectedContent
operator|.
name|get
argument_list|(
name|numOfline
argument_list|)
argument_list|,
name|sCurrentLine
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
name|numOfline
operator|++
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
if|if
condition|(
name|br
operator|!=
literal|null
condition|)
name|br
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

