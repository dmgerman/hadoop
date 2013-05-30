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
name|File
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
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedAction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|Map
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
name|UnsupportedFileSystemException
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
name|ContainerManager
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
name|protocolrecords
operator|.
name|GetContainerStatusRequest
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
name|protocolrecords
operator|.
name|StartContainerRequest
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
name|ApplicationAttemptId
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
name|api
operator|.
name|records
operator|.
name|Container
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
name|api
operator|.
name|records
operator|.
name|ContainerState
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
name|ContainerStatus
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
name|ContainerToken
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
name|LocalResource
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
name|LocalResourceType
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
name|LocalResourceVisibility
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
name|NodeId
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
name|Resource
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
name|URL
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
name|impl
operator|.
name|pb
operator|.
name|ContainerPBImpl
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
name|event
operator|.
name|Dispatcher
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
name|YarnRemoteException
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
name|factories
operator|.
name|RecordFactory
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
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
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
name|ipc
operator|.
name|YarnRPC
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
name|api
operator|.
name|records
operator|.
name|MasterKey
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
name|util
operator|.
name|BuilderUtils
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
name|util
operator|.
name|ConverterUtils
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

begin_class
DECL|class|TestNodeManagerShutdown
specifier|public
class|class
name|TestNodeManagerShutdown
block|{
DECL|field|basedir
specifier|static
specifier|final
name|File
name|basedir
init|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
name|TestNodeManagerShutdown
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|tmpDir
specifier|static
specifier|final
name|File
name|tmpDir
init|=
operator|new
name|File
argument_list|(
name|basedir
argument_list|,
literal|"tmpDir"
argument_list|)
decl_stmt|;
DECL|field|logsDir
specifier|static
specifier|final
name|File
name|logsDir
init|=
operator|new
name|File
argument_list|(
name|basedir
argument_list|,
literal|"logs"
argument_list|)
decl_stmt|;
DECL|field|remoteLogsDir
specifier|static
specifier|final
name|File
name|remoteLogsDir
init|=
operator|new
name|File
argument_list|(
name|basedir
argument_list|,
literal|"remotelogs"
argument_list|)
decl_stmt|;
DECL|field|nmLocalDir
specifier|static
specifier|final
name|File
name|nmLocalDir
init|=
operator|new
name|File
argument_list|(
name|basedir
argument_list|,
literal|"nm0"
argument_list|)
decl_stmt|;
DECL|field|processStartFile
specifier|static
specifier|final
name|File
name|processStartFile
init|=
operator|new
name|File
argument_list|(
name|tmpDir
argument_list|,
literal|"start_file.txt"
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
decl_stmt|;
DECL|field|recordFactory
specifier|static
specifier|final
name|RecordFactory
name|recordFactory
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
literal|null
argument_list|)
decl_stmt|;
DECL|field|user
specifier|static
specifier|final
name|String
name|user
init|=
literal|"nobody"
decl_stmt|;
DECL|field|localFS
specifier|private
name|FileContext
name|localFS
decl_stmt|;
DECL|field|cId
specifier|private
name|ContainerId
name|cId
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|UnsupportedFileSystemException
block|{
name|localFS
operator|=
name|FileContext
operator|.
name|getLocalFSFileContext
argument_list|()
expr_stmt|;
name|tmpDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|logsDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|remoteLogsDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|nmLocalDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
comment|// Construct the Container-id
name|cId
operator|=
name|createContainerId
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|localFS
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|basedir
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testKillContainersOnShutdown ()
specifier|public
name|void
name|testKillContainersOnShutdown
parameter_list|()
throws|throws
name|IOException
throws|,
name|YarnRemoteException
block|{
name|NodeManager
name|nm
init|=
operator|new
name|TestNodeManager
argument_list|()
decl_stmt|;
name|nm
operator|.
name|init
argument_list|(
name|createNMConfig
argument_list|()
argument_list|)
expr_stmt|;
name|nm
operator|.
name|start
argument_list|()
expr_stmt|;
name|startContainer
argument_list|(
name|nm
argument_list|,
name|cId
argument_list|,
name|localFS
argument_list|,
name|tmpDir
argument_list|,
name|processStartFile
argument_list|)
expr_stmt|;
specifier|final
name|int
name|MAX_TRIES
init|=
literal|20
decl_stmt|;
name|int
name|numTries
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|!
name|processStartFile
operator|.
name|exists
argument_list|()
operator|&&
name|numTries
operator|<
name|MAX_TRIES
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|numTries
operator|++
expr_stmt|;
block|}
name|nm
operator|.
name|stop
argument_list|()
expr_stmt|;
comment|// Now verify the contents of the file.  Script generates a message when it
comment|// receives a sigterm so we look for that.  We cannot perform this check on
comment|// Windows, because the process is not notified when killed by winutils.
comment|// There is no way for the process to trap and respond.  Instead, we can
comment|// verify that the job object with ID matching container ID no longer exists.
if|if
condition|(
name|Shell
operator|.
name|WINDOWS
condition|)
block|{
name|Assert
operator|.
name|assertFalse
argument_list|(
literal|"Process is still alive!"
argument_list|,
name|DefaultContainerExecutor
operator|.
name|containerIsAlive
argument_list|(
name|cId
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|processStartFile
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|foundSigTermMessage
init|=
literal|false
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|String
name|line
init|=
name|reader
operator|.
name|readLine
argument_list|()
decl_stmt|;
if|if
condition|(
name|line
operator|==
literal|null
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|line
operator|.
name|contains
argument_list|(
literal|"SIGTERM"
argument_list|)
condition|)
block|{
name|foundSigTermMessage
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Did not find sigterm message"
argument_list|,
name|foundSigTermMessage
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|startContainer (NodeManager nm, ContainerId cId, FileContext localFS, File scriptFileDir, File processStartFile)
specifier|public
specifier|static
name|void
name|startContainer
parameter_list|(
name|NodeManager
name|nm
parameter_list|,
name|ContainerId
name|cId
parameter_list|,
name|FileContext
name|localFS
parameter_list|,
name|File
name|scriptFileDir
parameter_list|,
name|File
name|processStartFile
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnRemoteException
block|{
name|File
name|scriptFile
init|=
name|createUnhaltingScriptFile
argument_list|(
name|cId
argument_list|,
name|scriptFileDir
argument_list|,
name|processStartFile
argument_list|)
decl_stmt|;
name|ContainerLaunchContext
name|containerLaunchContext
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ContainerLaunchContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|Container
name|mockContainer
init|=
operator|new
name|ContainerPBImpl
argument_list|()
decl_stmt|;
name|mockContainer
operator|.
name|setId
argument_list|(
name|cId
argument_list|)
expr_stmt|;
name|NodeId
name|nodeId
init|=
name|BuilderUtils
operator|.
name|newNodeId
argument_list|(
literal|"localhost"
argument_list|,
literal|1234
argument_list|)
decl_stmt|;
name|mockContainer
operator|.
name|setNodeId
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
name|mockContainer
operator|.
name|setNodeHttpAddress
argument_list|(
literal|"localhost:12345"
argument_list|)
expr_stmt|;
name|URL
name|localResourceUri
init|=
name|ConverterUtils
operator|.
name|getYarnUrlFromPath
argument_list|(
name|localFS
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
name|scriptFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|LocalResource
name|localResource
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|LocalResource
operator|.
name|class
argument_list|)
decl_stmt|;
name|localResource
operator|.
name|setResource
argument_list|(
name|localResourceUri
argument_list|)
expr_stmt|;
name|localResource
operator|.
name|setSize
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|localResource
operator|.
name|setVisibility
argument_list|(
name|LocalResourceVisibility
operator|.
name|APPLICATION
argument_list|)
expr_stmt|;
name|localResource
operator|.
name|setType
argument_list|(
name|LocalResourceType
operator|.
name|FILE
argument_list|)
expr_stmt|;
name|localResource
operator|.
name|setTimestamp
argument_list|(
name|scriptFile
operator|.
name|lastModified
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|destinationFile
init|=
literal|"dest_file"
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|LocalResource
argument_list|>
name|localResources
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|LocalResource
argument_list|>
argument_list|()
decl_stmt|;
name|localResources
operator|.
name|put
argument_list|(
name|destinationFile
argument_list|,
name|localResource
argument_list|)
expr_stmt|;
name|containerLaunchContext
operator|.
name|setLocalResources
argument_list|(
name|localResources
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|commands
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|Shell
operator|.
name|getRunScriptCommand
argument_list|(
name|scriptFile
argument_list|)
argument_list|)
decl_stmt|;
name|containerLaunchContext
operator|.
name|setCommands
argument_list|(
name|commands
argument_list|)
expr_stmt|;
name|Resource
name|resource
init|=
name|BuilderUtils
operator|.
name|newResource
argument_list|(
literal|1024
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|mockContainer
operator|.
name|setResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|ContainerToken
name|containerToken
init|=
name|BuilderUtils
operator|.
name|newContainerToken
argument_list|(
name|cId
argument_list|,
name|nodeId
operator|.
name|getHost
argument_list|()
argument_list|,
name|nodeId
operator|.
name|getPort
argument_list|()
argument_list|,
name|user
argument_list|,
name|resource
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|10000L
argument_list|,
literal|123
argument_list|,
literal|"password"
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|mockContainer
operator|.
name|setContainerToken
argument_list|(
name|containerToken
argument_list|)
expr_stmt|;
name|StartContainerRequest
name|startRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|StartContainerRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|startRequest
operator|.
name|setContainerLaunchContext
argument_list|(
name|containerLaunchContext
argument_list|)
expr_stmt|;
name|startRequest
operator|.
name|setContainer
argument_list|(
name|mockContainer
argument_list|)
expr_stmt|;
name|UserGroupInformation
name|currentUser
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|cId
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|ContainerManager
name|containerManager
init|=
name|currentUser
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedAction
argument_list|<
name|ContainerManager
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ContainerManager
name|run
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|YarnRPC
name|rpc
init|=
name|YarnRPC
operator|.
name|create
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|InetSocketAddress
name|containerManagerBindAddress
init|=
name|NetUtils
operator|.
name|createSocketAddrForHost
argument_list|(
literal|"127.0.0.1"
argument_list|,
literal|12345
argument_list|)
decl_stmt|;
return|return
operator|(
name|ContainerManager
operator|)
name|rpc
operator|.
name|getProxy
argument_list|(
name|ContainerManager
operator|.
name|class
argument_list|,
name|containerManagerBindAddress
argument_list|,
name|conf
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|containerManager
operator|.
name|startContainer
argument_list|(
name|startRequest
argument_list|)
expr_stmt|;
name|GetContainerStatusRequest
name|request
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetContainerStatusRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setContainerId
argument_list|(
name|cId
argument_list|)
expr_stmt|;
name|ContainerStatus
name|containerStatus
init|=
name|containerManager
operator|.
name|getContainerStatus
argument_list|(
name|request
argument_list|)
operator|.
name|getStatus
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ContainerState
operator|.
name|RUNNING
argument_list|,
name|containerStatus
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|createContainerId ()
specifier|public
specifier|static
name|ContainerId
name|createContainerId
parameter_list|()
block|{
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|appAttemptId
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ApplicationAttemptId
operator|.
name|class
argument_list|)
decl_stmt|;
name|appAttemptId
operator|.
name|setApplicationId
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|appAttemptId
operator|.
name|setAttemptId
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|ContainerId
name|containerId
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ContainerId
operator|.
name|class
argument_list|)
decl_stmt|;
name|containerId
operator|.
name|setApplicationAttemptId
argument_list|(
name|appAttemptId
argument_list|)
expr_stmt|;
return|return
name|containerId
return|;
block|}
DECL|method|createNMConfig ()
specifier|private
name|YarnConfiguration
name|createNMConfig
parameter_list|()
block|{
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|NM_PMEM_MB
argument_list|,
literal|5
operator|*
literal|1024
argument_list|)
expr_stmt|;
comment|// 5GB
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_ADDRESS
argument_list|,
literal|"127.0.0.1:12345"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOCALIZER_ADDRESS
argument_list|,
literal|"127.0.0.1:12346"
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
name|logsDir
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
name|NM_REMOTE_APP_LOG_DIR
argument_list|,
name|remoteLogsDir
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
name|NM_LOCAL_DIRS
argument_list|,
name|nmLocalDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
comment|/**    * Creates a script to run a container that will run forever unless    * stopped by external means.    */
DECL|method|createUnhaltingScriptFile (ContainerId cId, File scriptFileDir, File processStartFile)
specifier|private
specifier|static
name|File
name|createUnhaltingScriptFile
parameter_list|(
name|ContainerId
name|cId
parameter_list|,
name|File
name|scriptFileDir
parameter_list|,
name|File
name|processStartFile
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|scriptFile
init|=
name|Shell
operator|.
name|appendScriptExtension
argument_list|(
name|scriptFileDir
argument_list|,
literal|"scriptFile"
argument_list|)
decl_stmt|;
name|PrintWriter
name|fileWriter
init|=
operator|new
name|PrintWriter
argument_list|(
name|scriptFile
argument_list|)
decl_stmt|;
if|if
condition|(
name|Shell
operator|.
name|WINDOWS
condition|)
block|{
name|fileWriter
operator|.
name|println
argument_list|(
literal|"@echo \"Running testscript for delayed kill\""
argument_list|)
expr_stmt|;
name|fileWriter
operator|.
name|println
argument_list|(
literal|"@echo \"Writing pid to start file\""
argument_list|)
expr_stmt|;
name|fileWriter
operator|.
name|println
argument_list|(
literal|"@echo "
operator|+
name|cId
operator|+
literal|">> "
operator|+
name|processStartFile
argument_list|)
expr_stmt|;
name|fileWriter
operator|.
name|println
argument_list|(
literal|"@pause"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fileWriter
operator|.
name|write
argument_list|(
literal|"#!/bin/bash\n\n"
argument_list|)
expr_stmt|;
name|fileWriter
operator|.
name|write
argument_list|(
literal|"echo \"Running testscript for delayed kill\"\n"
argument_list|)
expr_stmt|;
name|fileWriter
operator|.
name|write
argument_list|(
literal|"hello=\"Got SIGTERM\"\n"
argument_list|)
expr_stmt|;
name|fileWriter
operator|.
name|write
argument_list|(
literal|"umask 0\n"
argument_list|)
expr_stmt|;
name|fileWriter
operator|.
name|write
argument_list|(
literal|"trap \"echo $hello>> "
operator|+
name|processStartFile
operator|+
literal|"\" SIGTERM\n"
argument_list|)
expr_stmt|;
name|fileWriter
operator|.
name|write
argument_list|(
literal|"echo \"Writing pid to start file\"\n"
argument_list|)
expr_stmt|;
name|fileWriter
operator|.
name|write
argument_list|(
literal|"echo $$>> "
operator|+
name|processStartFile
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|fileWriter
operator|.
name|write
argument_list|(
literal|"while true; do\ndate>> /dev/null;\n done\n"
argument_list|)
expr_stmt|;
block|}
name|fileWriter
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|scriptFile
return|;
block|}
DECL|class|TestNodeManager
class|class
name|TestNodeManager
extends|extends
name|NodeManager
block|{
annotation|@
name|Override
DECL|method|createNodeStatusUpdater (Context context, Dispatcher dispatcher, NodeHealthCheckerService healthChecker)
specifier|protected
name|NodeStatusUpdater
name|createNodeStatusUpdater
parameter_list|(
name|Context
name|context
parameter_list|,
name|Dispatcher
name|dispatcher
parameter_list|,
name|NodeHealthCheckerService
name|healthChecker
parameter_list|)
block|{
name|MockNodeStatusUpdater
name|myNodeStatusUpdater
init|=
operator|new
name|MockNodeStatusUpdater
argument_list|(
name|context
argument_list|,
name|dispatcher
argument_list|,
name|healthChecker
argument_list|,
name|metrics
argument_list|)
decl_stmt|;
return|return
name|myNodeStatusUpdater
return|;
block|}
DECL|method|setMasterKey (MasterKey masterKey)
specifier|public
name|void
name|setMasterKey
parameter_list|(
name|MasterKey
name|masterKey
parameter_list|)
block|{
name|getNMContext
argument_list|()
operator|.
name|getContainerTokenSecretManager
argument_list|()
operator|.
name|setMasterKey
argument_list|(
name|masterKey
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

