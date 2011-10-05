begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server
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
name|fail
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
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileWriter
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
name|ArrayList
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
name|Collections
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
name|avro
operator|.
name|AvroRuntimeException
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
name|CommonConfigurationKeysPublic
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
name|io
operator|.
name|DataInputBuffer
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
name|io
operator|.
name|Text
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
name|AccessControlException
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
name|SecurityInfo
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
name|security
operator|.
name|token
operator|.
name|Token
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
name|AMRMProtocol
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
name|AllocateRequest
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
name|RegisterApplicationMasterRequest
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
name|SubmitApplicationRequest
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
name|ApplicationSubmissionContext
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
name|Priority
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
name|ResourceRequest
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
name|security
operator|.
name|ApplicationTokenIdentifier
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
name|security
operator|.
name|ApplicationTokenSecretManager
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
name|security
operator|.
name|ContainerManagerSecurityInfo
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
name|security
operator|.
name|ContainerTokenIdentifier
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
name|security
operator|.
name|SchedulerSecurityInfo
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
name|ResourceManager
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
name|resource
operator|.
name|Resources
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
name|RMApp
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
name|attempt
operator|.
name|RMAppAttempt
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
name|attempt
operator|.
name|RMAppAttemptState
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

begin_class
DECL|class|TestContainerTokenSecretManager
specifier|public
class|class
name|TestContainerTokenSecretManager
block|{
DECL|field|LOG
specifier|private
specifier|static
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestContainerTokenSecretManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|recordFactory
specifier|private
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
DECL|field|localFS
specifier|private
specifier|static
name|FileContext
name|localFS
init|=
literal|null
decl_stmt|;
DECL|field|localDir
specifier|private
specifier|static
specifier|final
name|File
name|localDir
init|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
name|TestContainerTokenSecretManager
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"-localDir"
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
decl_stmt|;
DECL|field|yarnCluster
specifier|private
specifier|static
name|MiniYARNCluster
name|yarnCluster
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
name|AccessControlException
throws|,
name|FileNotFoundException
throws|,
name|UnsupportedFileSystemException
throws|,
name|IOException
block|{
name|localFS
operator|=
name|FileContext
operator|.
name|getLocalFSFileContext
argument_list|()
expr_stmt|;
name|localFS
operator|.
name|delete
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
literal|true
argument_list|)
expr_stmt|;
name|localDir
operator|.
name|mkdir
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|teardown ()
specifier|public
specifier|static
name|void
name|teardown
parameter_list|()
block|{
name|yarnCluster
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|test ()
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
specifier|final
name|ApplicationId
name|appID
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ApplicationId
operator|.
name|class
argument_list|)
decl_stmt|;
name|appID
operator|.
name|setClusterTimestamp
argument_list|(
literal|1234
argument_list|)
expr_stmt|;
name|appID
operator|.
name|setId
argument_list|(
literal|5
argument_list|)
expr_stmt|;
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SECURITY_AUTHENTICATION
argument_list|,
literal|"kerberos"
argument_list|)
expr_stmt|;
comment|// Set AM expiry interval to be very long.
name|conf
operator|.
name|setLong
argument_list|(
name|YarnConfiguration
operator|.
name|RM_AM_EXPIRY_INTERVAL_MS
argument_list|,
literal|100000L
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|yarnCluster
operator|=
operator|new
name|MiniYARNCluster
argument_list|(
name|TestContainerTokenSecretManager
operator|.
name|class
operator|.
name|getName
argument_list|()
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
name|ResourceManager
name|resourceManager
init|=
name|yarnCluster
operator|.
name|getResourceManager
argument_list|()
decl_stmt|;
specifier|final
name|YarnRPC
name|yarnRPC
init|=
name|YarnRPC
operator|.
name|create
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// Submit an application
name|ApplicationSubmissionContext
name|appSubmissionContext
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ApplicationSubmissionContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|appSubmissionContext
operator|.
name|setApplicationId
argument_list|(
name|appID
argument_list|)
expr_stmt|;
name|ContainerLaunchContext
name|amContainer
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
name|amContainer
operator|.
name|setResource
argument_list|(
name|Resources
operator|.
name|createResource
argument_list|(
literal|1024
argument_list|)
argument_list|)
expr_stmt|;
name|amContainer
operator|.
name|setCommands
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"sleep"
argument_list|,
literal|"100"
argument_list|)
argument_list|)
expr_stmt|;
name|appSubmissionContext
operator|.
name|setUser
argument_list|(
literal|"testUser"
argument_list|)
expr_stmt|;
comment|// TODO: Use a resource to work around bugs. Today NM doesn't create local
comment|// app-dirs if there are no file to download!!
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|localDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|"testFile"
argument_list|)
decl_stmt|;
name|FileWriter
name|tmpFile
init|=
operator|new
name|FileWriter
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|tmpFile
operator|.
name|write
argument_list|(
literal|"testing"
argument_list|)
expr_stmt|;
name|tmpFile
operator|.
name|close
argument_list|()
expr_stmt|;
name|URL
name|testFileURL
init|=
name|ConverterUtils
operator|.
name|getYarnUrlFromPath
argument_list|(
name|FileContext
operator|.
name|getFileContext
argument_list|()
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
name|localDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|"testFile"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|LocalResource
name|rsrc
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
name|rsrc
operator|.
name|setResource
argument_list|(
name|testFileURL
argument_list|)
expr_stmt|;
name|rsrc
operator|.
name|setSize
argument_list|(
name|file
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|rsrc
operator|.
name|setTimestamp
argument_list|(
name|file
operator|.
name|lastModified
argument_list|()
argument_list|)
expr_stmt|;
name|rsrc
operator|.
name|setType
argument_list|(
name|LocalResourceType
operator|.
name|FILE
argument_list|)
expr_stmt|;
name|rsrc
operator|.
name|setVisibility
argument_list|(
name|LocalResourceVisibility
operator|.
name|PRIVATE
argument_list|)
expr_stmt|;
name|amContainer
operator|.
name|setLocalResources
argument_list|(
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"testFile"
argument_list|,
name|rsrc
argument_list|)
argument_list|)
expr_stmt|;
name|SubmitApplicationRequest
name|submitRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|SubmitApplicationRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|submitRequest
operator|.
name|setApplicationSubmissionContext
argument_list|(
name|appSubmissionContext
argument_list|)
expr_stmt|;
name|appSubmissionContext
operator|.
name|setAMContainerSpec
argument_list|(
name|amContainer
argument_list|)
expr_stmt|;
name|resourceManager
operator|.
name|getClientRMService
argument_list|()
operator|.
name|submitApplication
argument_list|(
name|submitRequest
argument_list|)
expr_stmt|;
comment|// Wait till container gets allocated for AM
name|int
name|waitCounter
init|=
literal|0
decl_stmt|;
name|RMApp
name|app
init|=
name|resourceManager
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
decl_stmt|;
name|RMAppAttempt
name|appAttempt
init|=
name|app
operator|==
literal|null
condition|?
literal|null
else|:
name|app
operator|.
name|getCurrentAppAttempt
argument_list|()
decl_stmt|;
name|RMAppAttemptState
name|state
init|=
name|appAttempt
operator|==
literal|null
condition|?
literal|null
else|:
name|appAttempt
operator|.
name|getAppAttemptState
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|app
operator|==
literal|null
operator|||
name|appAttempt
operator|==
literal|null
operator|||
name|state
operator|==
literal|null
operator|||
operator|!
name|state
operator|.
name|equals
argument_list|(
name|RMAppAttemptState
operator|.
name|LAUNCHED
argument_list|)
operator|)
operator|&&
name|waitCounter
operator|++
operator|!=
literal|20
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for applicationAttempt to be created.. "
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|app
operator|=
name|resourceManager
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
expr_stmt|;
name|appAttempt
operator|=
name|app
operator|==
literal|null
condition|?
literal|null
else|:
name|app
operator|.
name|getCurrentAppAttempt
argument_list|()
expr_stmt|;
name|state
operator|=
name|appAttempt
operator|==
literal|null
condition|?
literal|null
else|:
name|appAttempt
operator|.
name|getAppAttemptState
argument_list|()
expr_stmt|;
block|}
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|app
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|appAttempt
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|RMAppAttemptState
operator|.
name|LAUNCHED
argument_list|,
name|state
argument_list|)
expr_stmt|;
name|UserGroupInformation
name|currentUser
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
comment|// Ask for a container from the RM
name|String
name|schedulerAddressString
init|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_ADDRESS
argument_list|)
decl_stmt|;
specifier|final
name|InetSocketAddress
name|schedulerAddr
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|schedulerAddressString
argument_list|)
decl_stmt|;
name|ApplicationTokenIdentifier
name|appTokenIdentifier
init|=
operator|new
name|ApplicationTokenIdentifier
argument_list|(
name|appID
argument_list|)
decl_stmt|;
name|ApplicationTokenSecretManager
name|appTokenSecretManager
init|=
operator|new
name|ApplicationTokenSecretManager
argument_list|()
decl_stmt|;
name|appTokenSecretManager
operator|.
name|setMasterKey
argument_list|(
name|ApplicationTokenSecretManager
operator|.
name|createSecretKey
argument_list|(
literal|"Dummy"
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO: FIX. Be in Sync with
comment|// ResourceManager.java
name|Token
argument_list|<
name|ApplicationTokenIdentifier
argument_list|>
name|appToken
init|=
operator|new
name|Token
argument_list|<
name|ApplicationTokenIdentifier
argument_list|>
argument_list|(
name|appTokenIdentifier
argument_list|,
name|appTokenSecretManager
argument_list|)
decl_stmt|;
name|appToken
operator|.
name|setService
argument_list|(
operator|new
name|Text
argument_list|(
name|schedulerAddressString
argument_list|)
argument_list|)
expr_stmt|;
name|currentUser
operator|.
name|addToken
argument_list|(
name|appToken
argument_list|)
expr_stmt|;
name|AMRMProtocol
name|scheduler
init|=
name|currentUser
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedAction
argument_list|<
name|AMRMProtocol
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|AMRMProtocol
name|run
parameter_list|()
block|{
return|return
operator|(
name|AMRMProtocol
operator|)
name|yarnRPC
operator|.
name|getProxy
argument_list|(
name|AMRMProtocol
operator|.
name|class
argument_list|,
name|schedulerAddr
argument_list|,
name|conf
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|// Register the appMaster
name|RegisterApplicationMasterRequest
name|request
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|RegisterApplicationMasterRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setApplicationAttemptId
argument_list|(
name|resourceManager
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
name|getCurrentAppAttempt
argument_list|()
operator|.
name|getAppAttemptId
argument_list|()
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|registerApplicationMaster
argument_list|(
name|request
argument_list|)
expr_stmt|;
comment|// Now request a container allocation.
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|ask
init|=
operator|new
name|ArrayList
argument_list|<
name|ResourceRequest
argument_list|>
argument_list|()
decl_stmt|;
name|ResourceRequest
name|rr
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ResourceRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|rr
operator|.
name|setCapability
argument_list|(
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|Resource
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|rr
operator|.
name|getCapability
argument_list|()
operator|.
name|setMemory
argument_list|(
literal|1024
argument_list|)
expr_stmt|;
name|rr
operator|.
name|setHostName
argument_list|(
literal|"*"
argument_list|)
expr_stmt|;
name|rr
operator|.
name|setNumContainers
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|rr
operator|.
name|setPriority
argument_list|(
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|Priority
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|rr
operator|.
name|getPriority
argument_list|()
operator|.
name|setPriority
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|ask
operator|.
name|add
argument_list|(
name|rr
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
name|release
init|=
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
decl_stmt|;
name|AllocateRequest
name|allocateRequest
init|=
name|BuilderUtils
operator|.
name|newAllocateRequest
argument_list|(
name|appAttempt
operator|.
name|getAppAttemptId
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|0F
argument_list|,
name|ask
argument_list|,
name|release
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Container
argument_list|>
name|allocatedContainers
init|=
name|scheduler
operator|.
name|allocate
argument_list|(
name|allocateRequest
argument_list|)
operator|.
name|getAMResponse
argument_list|()
operator|.
name|getAllocatedContainers
argument_list|()
decl_stmt|;
name|waitCounter
operator|=
literal|0
expr_stmt|;
while|while
condition|(
operator|(
name|allocatedContainers
operator|==
literal|null
operator|||
name|allocatedContainers
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|)
operator|&&
name|waitCounter
operator|++
operator|!=
literal|20
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for container to be allocated.."
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|allocateRequest
operator|.
name|setResponseId
argument_list|(
name|allocateRequest
operator|.
name|getResponseId
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
name|allocatedContainers
operator|=
name|scheduler
operator|.
name|allocate
argument_list|(
name|allocateRequest
argument_list|)
operator|.
name|getAMResponse
argument_list|()
operator|.
name|getAllocatedContainers
argument_list|()
expr_stmt|;
block|}
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"Container is not allocted!"
argument_list|,
name|allocatedContainers
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Didn't get one container!"
argument_list|,
literal|1
argument_list|,
name|allocatedContainers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now talk to the NM for launching the container.
specifier|final
name|Container
name|allocatedContainer
init|=
name|allocatedContainers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|ContainerToken
name|containerToken
init|=
name|allocatedContainer
operator|.
name|getContainerToken
argument_list|()
decl_stmt|;
name|Token
argument_list|<
name|ContainerTokenIdentifier
argument_list|>
name|token
init|=
operator|new
name|Token
argument_list|<
name|ContainerTokenIdentifier
argument_list|>
argument_list|(
name|containerToken
operator|.
name|getIdentifier
argument_list|()
operator|.
name|array
argument_list|()
argument_list|,
name|containerToken
operator|.
name|getPassword
argument_list|()
operator|.
name|array
argument_list|()
argument_list|,
operator|new
name|Text
argument_list|(
name|containerToken
operator|.
name|getKind
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
name|containerToken
operator|.
name|getService
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|currentUser
operator|.
name|addToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|currentUser
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|run
parameter_list|()
block|{
name|ContainerManager
name|client
init|=
operator|(
name|ContainerManager
operator|)
name|yarnRPC
operator|.
name|getProxy
argument_list|(
name|ContainerManager
operator|.
name|class
argument_list|,
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|allocatedContainer
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Going to make a getContainerStatus() legal request"
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
name|ContainerId
name|containerID
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
name|appID
argument_list|)
expr_stmt|;
name|appAttemptId
operator|.
name|setAttemptId
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|appAttemptId
operator|.
name|setApplicationId
argument_list|(
name|appID
argument_list|)
expr_stmt|;
name|containerID
operator|.
name|setApplicationAttemptId
argument_list|(
name|appAttemptId
argument_list|)
expr_stmt|;
name|containerID
operator|.
name|setId
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|request
operator|.
name|setContainerId
argument_list|(
name|containerID
argument_list|)
expr_stmt|;
name|client
operator|.
name|getContainerStatus
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnRemoteException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Error"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AvroRuntimeException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Got the expected exception"
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|UserGroupInformation
name|maliceUser
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|currentUser
operator|.
name|getShortUserName
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|identifierBytes
init|=
name|containerToken
operator|.
name|getIdentifier
argument_list|()
operator|.
name|array
argument_list|()
decl_stmt|;
name|DataInputBuffer
name|di
init|=
operator|new
name|DataInputBuffer
argument_list|()
decl_stmt|;
name|di
operator|.
name|reset
argument_list|(
name|identifierBytes
argument_list|,
name|identifierBytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|ContainerTokenIdentifier
name|dummyIdentifier
init|=
operator|new
name|ContainerTokenIdentifier
argument_list|()
decl_stmt|;
name|dummyIdentifier
operator|.
name|readFields
argument_list|(
name|di
argument_list|)
expr_stmt|;
name|Resource
name|modifiedResource
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|Resource
operator|.
name|class
argument_list|)
decl_stmt|;
name|modifiedResource
operator|.
name|setMemory
argument_list|(
literal|2048
argument_list|)
expr_stmt|;
name|ContainerTokenIdentifier
name|modifiedIdentifier
init|=
operator|new
name|ContainerTokenIdentifier
argument_list|(
name|dummyIdentifier
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|dummyIdentifier
operator|.
name|getNmHostName
argument_list|()
argument_list|,
name|modifiedResource
argument_list|)
decl_stmt|;
comment|// Malice user modifies the resource amount
name|Token
argument_list|<
name|ContainerTokenIdentifier
argument_list|>
name|modifiedToken
init|=
operator|new
name|Token
argument_list|<
name|ContainerTokenIdentifier
argument_list|>
argument_list|(
name|modifiedIdentifier
operator|.
name|getBytes
argument_list|()
argument_list|,
name|containerToken
operator|.
name|getPassword
argument_list|()
operator|.
name|array
argument_list|()
argument_list|,
operator|new
name|Text
argument_list|(
name|containerToken
operator|.
name|getKind
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
name|containerToken
operator|.
name|getService
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|maliceUser
operator|.
name|addToken
argument_list|(
name|modifiedToken
argument_list|)
expr_stmt|;
name|maliceUser
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|run
parameter_list|()
block|{
name|ContainerManager
name|client
init|=
operator|(
name|ContainerManager
operator|)
name|yarnRPC
operator|.
name|getProxy
argument_list|(
name|ContainerManager
operator|.
name|class
argument_list|,
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|allocatedContainer
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|ContainerId
name|containerID
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Going to contact NM:  ilLegal request"
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
name|containerID
operator|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ContainerId
operator|.
name|class
argument_list|)
expr_stmt|;
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
name|appID
argument_list|)
expr_stmt|;
name|appAttemptId
operator|.
name|setAttemptId
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|appAttemptId
operator|.
name|setApplicationId
argument_list|(
name|appID
argument_list|)
expr_stmt|;
name|containerID
operator|.
name|setApplicationAttemptId
argument_list|(
name|appAttemptId
argument_list|)
expr_stmt|;
name|containerID
operator|.
name|setId
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|request
operator|.
name|setContainerId
argument_list|(
name|containerID
argument_list|)
expr_stmt|;
try|try
block|{
name|client
operator|.
name|getContainerStatus
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Connection initiation with illegally modified "
operator|+
literal|"tokens is expected to fail."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnRemoteException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Got exception"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Cannot get a YARN remote exception as "
operator|+
literal|"it will indicate RPC success"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|UndeclaredThrowableException
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"DIGEST-MD5: digest response format violation. Mismatched response."
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

