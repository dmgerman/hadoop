begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer
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
operator|.
name|containermanager
operator|.
name|localizer
package|;
end_package

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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
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
name|Random
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
name|ipc
operator|.
name|Server
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
name|AbstractFileSystem
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
name|io
operator|.
name|DataOutputBuffer
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
name|security
operator|.
name|Credentials
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
name|security
operator|.
name|token
operator|.
name|TokenIdentifier
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
name|Progressable
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
name|event
operator|.
name|AsyncDispatcher
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
name|DrainDispatcher
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
name|EventHandler
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
name|DeletionService
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
name|api
operator|.
name|protocolrecords
operator|.
name|LocalResourceStatus
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
name|api
operator|.
name|protocolrecords
operator|.
name|LocalizerAction
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
name|api
operator|.
name|protocolrecords
operator|.
name|LocalizerHeartbeatResponse
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
name|api
operator|.
name|protocolrecords
operator|.
name|LocalizerStatus
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
name|api
operator|.
name|protocolrecords
operator|.
name|ResourceStatusType
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
name|application
operator|.
name|Application
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
name|application
operator|.
name|ApplicationEvent
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
name|application
operator|.
name|ApplicationEventType
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
name|ContainerEvent
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
name|ContainerEventType
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
name|localizer
operator|.
name|event
operator|.
name|ApplicationLocalizationEvent
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
name|localizer
operator|.
name|event
operator|.
name|ContainerLocalizationRequestEvent
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
name|localizer
operator|.
name|event
operator|.
name|LocalizationEventType
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
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|util
operator|.
name|Records
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatcher
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
name|*
import|;
end_import

begin_import
import|import static
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
name|NMConfig
operator|.
name|NM_LOCAL_DIR
import|;
end_import

begin_class
DECL|class|TestResourceLocalizationService
specifier|public
class|class
name|TestResourceLocalizationService
block|{
DECL|field|basedir
specifier|static
specifier|final
name|Path
name|basedir
init|=
operator|new
name|Path
argument_list|(
literal|"target"
argument_list|,
name|TestResourceLocalizationService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testLocalizationInit ()
specifier|public
name|void
name|testLocalizationInit
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|AsyncDispatcher
name|dispatcher
init|=
operator|new
name|AsyncDispatcher
argument_list|()
decl_stmt|;
name|dispatcher
operator|.
name|init
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|ContainerExecutor
name|exec
init|=
name|mock
argument_list|(
name|ContainerExecutor
operator|.
name|class
argument_list|)
decl_stmt|;
name|DeletionService
name|delService
init|=
name|spy
argument_list|(
operator|new
name|DeletionService
argument_list|(
name|exec
argument_list|)
argument_list|)
decl_stmt|;
name|delService
operator|.
name|init
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|delService
operator|.
name|start
argument_list|()
expr_stmt|;
name|AbstractFileSystem
name|spylfs
init|=
name|spy
argument_list|(
name|FileContext
operator|.
name|getLocalFSFileContext
argument_list|()
operator|.
name|getDefaultFileSystem
argument_list|()
argument_list|)
decl_stmt|;
name|FileContext
name|lfs
init|=
name|FileContext
operator|.
name|getFileContext
argument_list|(
name|spylfs
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|doNothing
argument_list|()
operator|.
name|when
argument_list|(
name|spylfs
argument_list|)
operator|.
name|mkdir
argument_list|(
name|isA
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|,
name|isA
argument_list|(
name|FsPermission
operator|.
name|class
argument_list|)
argument_list|,
name|anyBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|ResourceLocalizationService
name|locService
init|=
name|spy
argument_list|(
operator|new
name|ResourceLocalizationService
argument_list|(
name|dispatcher
argument_list|,
name|exec
argument_list|,
name|delService
argument_list|)
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|lfs
argument_list|)
operator|.
name|when
argument_list|(
name|locService
argument_list|)
operator|.
name|getLocalFileContext
argument_list|(
name|isA
argument_list|(
name|Configuration
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|dispatcher
operator|.
name|start
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|Path
argument_list|>
name|localDirs
init|=
operator|new
name|ArrayList
argument_list|<
name|Path
argument_list|>
argument_list|()
decl_stmt|;
name|String
index|[]
name|sDirs
init|=
operator|new
name|String
index|[
literal|4
index|]
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
literal|4
condition|;
operator|++
name|i
control|)
block|{
name|localDirs
operator|.
name|add
argument_list|(
name|lfs
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
name|basedir
argument_list|,
name|i
operator|+
literal|""
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|sDirs
index|[
name|i
index|]
operator|=
name|localDirs
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|conf
operator|.
name|setStrings
argument_list|(
name|NM_LOCAL_DIR
argument_list|,
name|sDirs
argument_list|)
expr_stmt|;
comment|// initialize ResourceLocalizationService
name|locService
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|// verify directory creation
for|for
control|(
name|Path
name|p
range|:
name|localDirs
control|)
block|{
name|Path
name|usercache
init|=
operator|new
name|Path
argument_list|(
name|p
argument_list|,
name|ContainerLocalizer
operator|.
name|USERCACHE
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|spylfs
argument_list|)
operator|.
name|mkdir
argument_list|(
name|eq
argument_list|(
name|usercache
argument_list|)
argument_list|,
name|isA
argument_list|(
name|FsPermission
operator|.
name|class
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|publicCache
init|=
operator|new
name|Path
argument_list|(
name|p
argument_list|,
name|ContainerLocalizer
operator|.
name|FILECACHE
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|spylfs
argument_list|)
operator|.
name|mkdir
argument_list|(
name|eq
argument_list|(
name|publicCache
argument_list|)
argument_list|,
name|isA
argument_list|(
name|FsPermission
operator|.
name|class
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|nmPriv
init|=
operator|new
name|Path
argument_list|(
name|p
argument_list|,
name|ResourceLocalizationService
operator|.
name|NM_PRIVATE_DIR
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|spylfs
argument_list|)
operator|.
name|mkdir
argument_list|(
name|eq
argument_list|(
name|nmPriv
argument_list|)
argument_list|,
name|eq
argument_list|(
name|ResourceLocalizationService
operator|.
name|NM_PRIVATE_PERM
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|dispatcher
operator|.
name|stop
argument_list|()
expr_stmt|;
name|delService
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
comment|// mocked generics
DECL|method|testLocalizationHeartbeat ()
specifier|public
name|void
name|testLocalizationHeartbeat
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
name|AbstractFileSystem
name|spylfs
init|=
name|spy
argument_list|(
name|FileContext
operator|.
name|getLocalFSFileContext
argument_list|()
operator|.
name|getDefaultFileSystem
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|FileContext
name|lfs
init|=
name|FileContext
operator|.
name|getFileContext
argument_list|(
name|spylfs
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|doNothing
argument_list|()
operator|.
name|when
argument_list|(
name|spylfs
argument_list|)
operator|.
name|mkdir
argument_list|(
name|isA
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|,
name|isA
argument_list|(
name|FsPermission
operator|.
name|class
argument_list|)
argument_list|,
name|anyBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Path
argument_list|>
name|localDirs
init|=
operator|new
name|ArrayList
argument_list|<
name|Path
argument_list|>
argument_list|()
decl_stmt|;
name|String
index|[]
name|sDirs
init|=
operator|new
name|String
index|[
literal|4
index|]
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
literal|4
condition|;
operator|++
name|i
control|)
block|{
name|localDirs
operator|.
name|add
argument_list|(
name|lfs
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
name|basedir
argument_list|,
name|i
operator|+
literal|""
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|sDirs
index|[
name|i
index|]
operator|=
name|localDirs
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|conf
operator|.
name|setStrings
argument_list|(
name|NM_LOCAL_DIR
argument_list|,
name|sDirs
argument_list|)
expr_stmt|;
name|Server
name|ignore
init|=
name|mock
argument_list|(
name|Server
operator|.
name|class
argument_list|)
decl_stmt|;
name|DrainDispatcher
name|dispatcher
init|=
operator|new
name|DrainDispatcher
argument_list|()
decl_stmt|;
name|dispatcher
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|dispatcher
operator|.
name|start
argument_list|()
expr_stmt|;
name|EventHandler
argument_list|<
name|ApplicationEvent
argument_list|>
name|applicationBus
init|=
name|mock
argument_list|(
name|EventHandler
operator|.
name|class
argument_list|)
decl_stmt|;
name|dispatcher
operator|.
name|register
argument_list|(
name|ApplicationEventType
operator|.
name|class
argument_list|,
name|applicationBus
argument_list|)
expr_stmt|;
name|EventHandler
argument_list|<
name|ContainerEvent
argument_list|>
name|containerBus
init|=
name|mock
argument_list|(
name|EventHandler
operator|.
name|class
argument_list|)
decl_stmt|;
name|dispatcher
operator|.
name|register
argument_list|(
name|ContainerEventType
operator|.
name|class
argument_list|,
name|containerBus
argument_list|)
expr_stmt|;
name|ContainerExecutor
name|exec
init|=
name|mock
argument_list|(
name|ContainerExecutor
operator|.
name|class
argument_list|)
decl_stmt|;
name|DeletionService
name|delService
init|=
operator|new
name|DeletionService
argument_list|(
name|exec
argument_list|)
decl_stmt|;
name|delService
operator|.
name|init
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|delService
operator|.
name|start
argument_list|()
expr_stmt|;
name|ResourceLocalizationService
name|rawService
init|=
operator|new
name|ResourceLocalizationService
argument_list|(
name|dispatcher
argument_list|,
name|exec
argument_list|,
name|delService
argument_list|)
decl_stmt|;
name|ResourceLocalizationService
name|spyService
init|=
name|spy
argument_list|(
name|rawService
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|ignore
argument_list|)
operator|.
name|when
argument_list|(
name|spyService
argument_list|)
operator|.
name|createServer
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
name|lfs
argument_list|)
operator|.
name|when
argument_list|(
name|spyService
argument_list|)
operator|.
name|getLocalFileContext
argument_list|(
name|isA
argument_list|(
name|Configuration
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|spyService
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|spyService
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// init application
specifier|final
name|Application
name|app
init|=
name|mock
argument_list|(
name|Application
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|ApplicationId
name|appId
init|=
name|mock
argument_list|(
name|ApplicationId
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|appId
operator|.
name|getClusterTimestamp
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|314159265358979L
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|appId
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|app
operator|.
name|getUser
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"user0"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|app
operator|.
name|getAppId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|spyService
operator|.
name|handle
argument_list|(
operator|new
name|ApplicationLocalizationEvent
argument_list|(
name|LocalizationEventType
operator|.
name|INIT_APPLICATION_RESOURCES
argument_list|,
name|app
argument_list|)
argument_list|)
expr_stmt|;
name|ArgumentMatcher
argument_list|<
name|ApplicationEvent
argument_list|>
name|matchesAppInit
init|=
operator|new
name|ArgumentMatcher
argument_list|<
name|ApplicationEvent
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|ApplicationEvent
name|evt
init|=
operator|(
name|ApplicationEvent
operator|)
name|o
decl_stmt|;
return|return
name|evt
operator|.
name|getType
argument_list|()
operator|==
name|ApplicationEventType
operator|.
name|APPLICATION_INITED
operator|&&
name|appId
operator|==
name|evt
operator|.
name|getApplicationID
argument_list|()
return|;
block|}
block|}
decl_stmt|;
name|dispatcher
operator|.
name|await
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|applicationBus
argument_list|)
operator|.
name|handle
argument_list|(
name|argThat
argument_list|(
name|matchesAppInit
argument_list|)
argument_list|)
expr_stmt|;
comment|// init container rsrc, localizer
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|long
name|seed
init|=
name|r
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"SEED: "
operator|+
name|seed
argument_list|)
expr_stmt|;
name|r
operator|.
name|setSeed
argument_list|(
name|seed
argument_list|)
expr_stmt|;
specifier|final
name|Container
name|c
init|=
name|getMockContainer
argument_list|(
name|appId
argument_list|,
literal|42
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|out
init|=
operator|new
name|FSDataOutputStream
argument_list|(
operator|new
name|DataOutputBuffer
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|out
argument_list|)
operator|.
name|when
argument_list|(
name|spylfs
argument_list|)
operator|.
name|createInternal
argument_list|(
name|isA
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|,
name|isA
argument_list|(
name|EnumSet
operator|.
name|class
argument_list|)
argument_list|,
name|isA
argument_list|(
name|FsPermission
operator|.
name|class
argument_list|)
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|anyShort
argument_list|()
argument_list|,
name|anyLong
argument_list|()
argument_list|,
name|isA
argument_list|(
name|Progressable
operator|.
name|class
argument_list|)
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|anyBoolean
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|LocalResource
name|resource
init|=
name|getMockResource
argument_list|(
name|r
argument_list|)
decl_stmt|;
specifier|final
name|LocalResourceRequest
name|req
init|=
operator|new
name|LocalResourceRequest
argument_list|(
name|resource
argument_list|)
decl_stmt|;
name|spyService
operator|.
name|handle
argument_list|(
operator|new
name|ContainerLocalizationRequestEvent
argument_list|(
name|c
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|req
argument_list|)
argument_list|,
name|LocalResourceVisibility
operator|.
name|PRIVATE
argument_list|)
argument_list|)
expr_stmt|;
comment|// Sigh. Thread init of private localizer not accessible
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|dispatcher
operator|.
name|await
argument_list|()
expr_stmt|;
name|String
name|appStr
init|=
name|ConverterUtils
operator|.
name|toString
argument_list|(
name|appId
argument_list|)
decl_stmt|;
name|String
name|ctnrStr
init|=
name|c
operator|.
name|getContainerID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|verify
argument_list|(
name|exec
argument_list|)
operator|.
name|startLocalizer
argument_list|(
name|isA
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|,
name|isA
argument_list|(
name|InetSocketAddress
operator|.
name|class
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|"user0"
argument_list|)
argument_list|,
name|eq
argument_list|(
name|appStr
argument_list|)
argument_list|,
name|eq
argument_list|(
name|ctnrStr
argument_list|)
argument_list|,
name|isA
argument_list|(
name|List
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
comment|// heartbeat from localizer
name|LocalResourceStatus
name|rsrcStat
init|=
name|mock
argument_list|(
name|LocalResourceStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|LocalizerStatus
name|stat
init|=
name|mock
argument_list|(
name|LocalizerStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|stat
operator|.
name|getLocalizerId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ctnrStr
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|rsrcStat
operator|.
name|getResource
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|rsrcStat
operator|.
name|getLocalSize
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|4344L
argument_list|)
expr_stmt|;
name|URL
name|locPath
init|=
name|getPath
argument_list|(
literal|"/cache/private/blah"
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|rsrcStat
operator|.
name|getLocalPath
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|locPath
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|rsrcStat
operator|.
name|getStatus
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ResourceStatusType
operator|.
name|FETCH_SUCCESS
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|stat
operator|.
name|getResources
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Collections
operator|.
expr|<
name|LocalResourceStatus
operator|>
name|emptyList
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|rsrcStat
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Collections
operator|.
expr|<
name|LocalResourceStatus
operator|>
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
comment|// get rsrc
name|LocalizerHeartbeatResponse
name|response
init|=
name|spyService
operator|.
name|heartbeat
argument_list|(
name|stat
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|LocalizerAction
operator|.
name|LIVE
argument_list|,
name|response
operator|.
name|getLocalizerAction
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|req
argument_list|,
operator|new
name|LocalResourceRequest
argument_list|(
name|response
operator|.
name|getLocalResource
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// empty rsrc
name|response
operator|=
name|spyService
operator|.
name|heartbeat
argument_list|(
name|stat
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|LocalizerAction
operator|.
name|LIVE
argument_list|,
name|response
operator|.
name|getLocalizerAction
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|response
operator|.
name|getAllResources
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// get shutdown
name|response
operator|=
name|spyService
operator|.
name|heartbeat
argument_list|(
name|stat
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|LocalizerAction
operator|.
name|DIE
argument_list|,
name|response
operator|.
name|getLocalizerAction
argument_list|()
argument_list|)
expr_stmt|;
comment|// verify container notification
name|ArgumentMatcher
argument_list|<
name|ContainerEvent
argument_list|>
name|matchesContainerLoc
init|=
operator|new
name|ArgumentMatcher
argument_list|<
name|ContainerEvent
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|ContainerEvent
name|evt
init|=
operator|(
name|ContainerEvent
operator|)
name|o
decl_stmt|;
return|return
name|evt
operator|.
name|getType
argument_list|()
operator|==
name|ContainerEventType
operator|.
name|RESOURCE_LOCALIZED
operator|&&
name|c
operator|.
name|getContainerID
argument_list|()
operator|==
name|evt
operator|.
name|getContainerID
argument_list|()
return|;
block|}
block|}
decl_stmt|;
name|dispatcher
operator|.
name|await
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|containerBus
argument_list|)
operator|.
name|handle
argument_list|(
name|argThat
argument_list|(
name|matchesContainerLoc
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|delService
operator|.
name|stop
argument_list|()
expr_stmt|;
name|dispatcher
operator|.
name|stop
argument_list|()
expr_stmt|;
name|spyService
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getPath (String path)
specifier|static
name|URL
name|getPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|URL
name|uri
init|=
name|mock
argument_list|(
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
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|uri
operator|.
name|getScheme
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"file"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|uri
operator|.
name|getHost
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|uri
operator|.
name|getFile
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|path
argument_list|)
expr_stmt|;
return|return
name|uri
return|;
block|}
DECL|method|getMockResource (Random r)
specifier|static
name|LocalResource
name|getMockResource
parameter_list|(
name|Random
name|r
parameter_list|)
block|{
name|LocalResource
name|rsrc
init|=
name|mock
argument_list|(
name|LocalResource
operator|.
name|class
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|Long
operator|.
name|toHexString
argument_list|(
name|r
operator|.
name|nextLong
argument_list|()
argument_list|)
decl_stmt|;
name|URL
name|uri
init|=
name|getPath
argument_list|(
literal|"/local/PRIVATE/"
operator|+
name|name
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|rsrc
operator|.
name|getResource
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|uri
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|rsrc
operator|.
name|getSize
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
literal|1024
argument_list|)
operator|+
literal|1024L
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|rsrc
operator|.
name|getTimestamp
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
literal|1024
argument_list|)
operator|+
literal|2048L
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|rsrc
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|LocalResourceType
operator|.
name|FILE
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|rsrc
operator|.
name|getVisibility
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|LocalResourceVisibility
operator|.
name|PRIVATE
argument_list|)
expr_stmt|;
return|return
name|rsrc
return|;
block|}
DECL|method|getMockContainer (ApplicationId appId, int id)
specifier|static
name|Container
name|getMockContainer
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|int
name|id
parameter_list|)
block|{
name|Container
name|c
init|=
name|mock
argument_list|(
name|Container
operator|.
name|class
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|appAttemptId
init|=
name|Records
operator|.
name|newRecord
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
name|cId
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ContainerId
operator|.
name|class
argument_list|)
decl_stmt|;
name|cId
operator|.
name|setAppAttemptId
argument_list|(
name|appAttemptId
argument_list|)
expr_stmt|;
name|cId
operator|.
name|setAppId
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|cId
operator|.
name|setId
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|c
operator|.
name|getUser
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"user0"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|c
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
name|Credentials
name|creds
init|=
operator|new
name|Credentials
argument_list|()
decl_stmt|;
name|creds
operator|.
name|addToken
argument_list|(
operator|new
name|Text
argument_list|(
literal|"tok"
operator|+
name|id
argument_list|)
argument_list|,
name|getToken
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|c
operator|.
name|getCredentials
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|creds
argument_list|)
expr_stmt|;
return|return
name|c
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|method|getToken (int id)
specifier|static
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|getToken
parameter_list|(
name|int
name|id
parameter_list|)
block|{
return|return
operator|new
name|Token
argument_list|(
operator|(
literal|"ident"
operator|+
name|id
operator|)
operator|.
name|getBytes
argument_list|()
argument_list|,
operator|(
literal|"passwd"
operator|+
name|id
operator|)
operator|.
name|getBytes
argument_list|()
argument_list|,
operator|new
name|Text
argument_list|(
literal|"kind"
operator|+
name|id
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
literal|"service"
operator|+
name|id
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

