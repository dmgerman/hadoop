begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|io
operator|.
name|DataInputStream
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|java
operator|.
name|util
operator|.
name|Random
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
name|Callable
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
name|CancellationException
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
name|CompletionService
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
name|ExecutionException
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
name|ExecutorCompletionService
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
name|ExecutorService
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
name|Executors
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
name|Future
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
name|TimeUnit
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
name|FileUtil
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
name|LocalDirAllocator
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
name|yarn
operator|.
name|YarnUncaughtExceptionHandler
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
name|RPCUtil
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
name|nodemanager
operator|.
name|api
operator|.
name|LocalizationProtocol
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
name|localizer
operator|.
name|security
operator|.
name|LocalizerTokenIdentifier
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
name|security
operator|.
name|LocalizerTokenSecretManager
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
name|FSDownload
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ThreadFactoryBuilder
import|;
end_import

begin_class
DECL|class|ContainerLocalizer
specifier|public
class|class
name|ContainerLocalizer
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
name|ContainerLocalizer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|FILECACHE
specifier|public
specifier|static
specifier|final
name|String
name|FILECACHE
init|=
literal|"filecache"
decl_stmt|;
DECL|field|APPCACHE
specifier|public
specifier|static
specifier|final
name|String
name|APPCACHE
init|=
literal|"appcache"
decl_stmt|;
DECL|field|USERCACHE
specifier|public
specifier|static
specifier|final
name|String
name|USERCACHE
init|=
literal|"usercache"
decl_stmt|;
DECL|field|OUTPUTDIR
specifier|public
specifier|static
specifier|final
name|String
name|OUTPUTDIR
init|=
literal|"output"
decl_stmt|;
DECL|field|TOKEN_FILE_NAME_FMT
specifier|public
specifier|static
specifier|final
name|String
name|TOKEN_FILE_NAME_FMT
init|=
literal|"%s.tokens"
decl_stmt|;
DECL|field|WORKDIR
specifier|public
specifier|static
specifier|final
name|String
name|WORKDIR
init|=
literal|"work"
decl_stmt|;
DECL|field|APPCACHE_CTXT_FMT
specifier|private
specifier|static
specifier|final
name|String
name|APPCACHE_CTXT_FMT
init|=
literal|"%s.app.cache.dirs"
decl_stmt|;
DECL|field|USERCACHE_CTXT_FMT
specifier|private
specifier|static
specifier|final
name|String
name|USERCACHE_CTXT_FMT
init|=
literal|"%s.user.cache.dirs"
decl_stmt|;
DECL|field|user
specifier|private
specifier|final
name|String
name|user
decl_stmt|;
DECL|field|appId
specifier|private
specifier|final
name|String
name|appId
decl_stmt|;
DECL|field|localDirs
specifier|private
specifier|final
name|List
argument_list|<
name|Path
argument_list|>
name|localDirs
decl_stmt|;
DECL|field|localizerId
specifier|private
specifier|final
name|String
name|localizerId
decl_stmt|;
DECL|field|lfs
specifier|private
specifier|final
name|FileContext
name|lfs
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|appDirs
specifier|private
specifier|final
name|LocalDirAllocator
name|appDirs
decl_stmt|;
DECL|field|userDirs
specifier|private
specifier|final
name|LocalDirAllocator
name|userDirs
decl_stmt|;
DECL|field|recordFactory
specifier|private
specifier|final
name|RecordFactory
name|recordFactory
decl_stmt|;
DECL|field|pendingResources
specifier|private
specifier|final
name|Map
argument_list|<
name|LocalResource
argument_list|,
name|Future
argument_list|<
name|Path
argument_list|>
argument_list|>
name|pendingResources
decl_stmt|;
DECL|field|appCacheDirContextName
specifier|private
specifier|final
name|String
name|appCacheDirContextName
decl_stmt|;
DECL|method|ContainerLocalizer (FileContext lfs, String user, String appId, String localizerId, List<Path> localDirs, RecordFactory recordFactory)
specifier|public
name|ContainerLocalizer
parameter_list|(
name|FileContext
name|lfs
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|appId
parameter_list|,
name|String
name|localizerId
parameter_list|,
name|List
argument_list|<
name|Path
argument_list|>
name|localDirs
parameter_list|,
name|RecordFactory
name|recordFactory
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
literal|null
operator|==
name|user
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot initialize for null user"
argument_list|)
throw|;
block|}
if|if
condition|(
literal|null
operator|==
name|localizerId
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot initialize for null containerId"
argument_list|)
throw|;
block|}
name|this
operator|.
name|lfs
operator|=
name|lfs
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|appId
operator|=
name|appId
expr_stmt|;
name|this
operator|.
name|localDirs
operator|=
name|localDirs
expr_stmt|;
name|this
operator|.
name|localizerId
operator|=
name|localizerId
expr_stmt|;
name|this
operator|.
name|recordFactory
operator|=
name|recordFactory
expr_stmt|;
name|this
operator|.
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|this
operator|.
name|appCacheDirContextName
operator|=
name|String
operator|.
name|format
argument_list|(
name|APPCACHE_CTXT_FMT
argument_list|,
name|appId
argument_list|)
expr_stmt|;
name|this
operator|.
name|appDirs
operator|=
operator|new
name|LocalDirAllocator
argument_list|(
name|appCacheDirContextName
argument_list|)
expr_stmt|;
name|this
operator|.
name|userDirs
operator|=
operator|new
name|LocalDirAllocator
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|USERCACHE_CTXT_FMT
argument_list|,
name|user
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|pendingResources
operator|=
operator|new
name|HashMap
argument_list|<
name|LocalResource
argument_list|,
name|Future
argument_list|<
name|Path
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
block|}
DECL|method|getProxy (final InetSocketAddress nmAddr)
name|LocalizationProtocol
name|getProxy
parameter_list|(
specifier|final
name|InetSocketAddress
name|nmAddr
parameter_list|)
block|{
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
return|return
operator|(
name|LocalizationProtocol
operator|)
name|rpc
operator|.
name|getProxy
argument_list|(
name|LocalizationProtocol
operator|.
name|class
argument_list|,
name|nmAddr
argument_list|,
name|conf
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|runLocalization (final InetSocketAddress nmAddr)
specifier|public
name|int
name|runLocalization
parameter_list|(
specifier|final
name|InetSocketAddress
name|nmAddr
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
comment|// load credentials
name|initDirs
argument_list|(
name|conf
argument_list|,
name|user
argument_list|,
name|appId
argument_list|,
name|lfs
argument_list|,
name|localDirs
argument_list|)
expr_stmt|;
specifier|final
name|Credentials
name|creds
init|=
operator|new
name|Credentials
argument_list|()
decl_stmt|;
name|DataInputStream
name|credFile
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// assume credentials in cwd
comment|// TODO: Fix
name|credFile
operator|=
name|lfs
operator|.
name|open
argument_list|(
operator|new
name|Path
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|TOKEN_FILE_NAME_FMT
argument_list|,
name|localizerId
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|creds
operator|.
name|readTokenStorageStream
argument_list|(
name|credFile
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|credFile
operator|!=
literal|null
condition|)
block|{
name|credFile
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// create localizer context
name|UserGroupInformation
name|remoteUser
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|LocalizerTokenSecretManager
name|secretManager
init|=
operator|new
name|LocalizerTokenSecretManager
argument_list|()
decl_stmt|;
name|LocalizerTokenIdentifier
name|id
init|=
name|secretManager
operator|.
name|createIdentifier
argument_list|()
decl_stmt|;
name|Token
argument_list|<
name|LocalizerTokenIdentifier
argument_list|>
name|localizerToken
init|=
operator|new
name|Token
argument_list|<
name|LocalizerTokenIdentifier
argument_list|>
argument_list|(
name|id
argument_list|,
name|secretManager
argument_list|)
decl_stmt|;
name|remoteUser
operator|.
name|addToken
argument_list|(
name|localizerToken
argument_list|)
expr_stmt|;
specifier|final
name|LocalizationProtocol
name|nodeManager
init|=
name|remoteUser
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedAction
argument_list|<
name|LocalizationProtocol
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|LocalizationProtocol
name|run
parameter_list|()
block|{
return|return
name|getProxy
argument_list|(
name|nmAddr
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|// create user context
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|user
argument_list|)
decl_stmt|;
for|for
control|(
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|token
range|:
name|creds
operator|.
name|getAllTokens
argument_list|()
control|)
block|{
name|ugi
operator|.
name|addToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
name|ExecutorService
name|exec
init|=
literal|null
decl_stmt|;
try|try
block|{
name|exec
operator|=
name|createDownloadThreadPool
argument_list|()
expr_stmt|;
name|CompletionService
argument_list|<
name|Path
argument_list|>
name|ecs
init|=
name|createCompletionService
argument_list|(
name|exec
argument_list|)
decl_stmt|;
name|localizeFiles
argument_list|(
name|nodeManager
argument_list|,
name|ecs
argument_list|,
name|ugi
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
comment|// Print traces to stdout so that they can be logged by the NM address
comment|// space.
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
finally|finally
block|{
try|try
block|{
if|if
condition|(
name|exec
operator|!=
literal|null
condition|)
block|{
name|exec
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
name|LocalDirAllocator
operator|.
name|removeContext
argument_list|(
name|appCacheDirContextName
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|closeFileSystems
argument_list|(
name|ugi
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|createDownloadThreadPool ()
name|ExecutorService
name|createDownloadThreadPool
parameter_list|()
block|{
return|return
name|Executors
operator|.
name|newSingleThreadExecutor
argument_list|(
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setNameFormat
argument_list|(
literal|"ContainerLocalizer Downloader"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
DECL|method|createCompletionService (ExecutorService exec)
name|CompletionService
argument_list|<
name|Path
argument_list|>
name|createCompletionService
parameter_list|(
name|ExecutorService
name|exec
parameter_list|)
block|{
return|return
operator|new
name|ExecutorCompletionService
argument_list|<
name|Path
argument_list|>
argument_list|(
name|exec
argument_list|)
return|;
block|}
DECL|method|download (LocalDirAllocator lda, LocalResource rsrc, UserGroupInformation ugi)
name|Callable
argument_list|<
name|Path
argument_list|>
name|download
parameter_list|(
name|LocalDirAllocator
name|lda
parameter_list|,
name|LocalResource
name|rsrc
parameter_list|,
name|UserGroupInformation
name|ugi
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|destPath
init|=
name|lda
operator|.
name|getLocalPathForWrite
argument_list|(
literal|"."
argument_list|,
name|getEstimatedSize
argument_list|(
name|rsrc
argument_list|)
argument_list|,
name|conf
argument_list|)
decl_stmt|;
return|return
operator|new
name|FSDownload
argument_list|(
name|lfs
argument_list|,
name|ugi
argument_list|,
name|conf
argument_list|,
name|destPath
argument_list|,
name|rsrc
argument_list|,
operator|new
name|Random
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getEstimatedSize (LocalResource rsrc)
specifier|static
name|long
name|getEstimatedSize
parameter_list|(
name|LocalResource
name|rsrc
parameter_list|)
block|{
if|if
condition|(
name|rsrc
operator|.
name|getSize
argument_list|()
operator|<
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
switch|switch
condition|(
name|rsrc
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|ARCHIVE
case|:
return|return
literal|5
operator|*
name|rsrc
operator|.
name|getSize
argument_list|()
return|;
case|case
name|FILE
case|:
default|default:
return|return
name|rsrc
operator|.
name|getSize
argument_list|()
return|;
block|}
block|}
DECL|method|sleep (int duration)
name|void
name|sleep
parameter_list|(
name|int
name|duration
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
name|duration
argument_list|)
expr_stmt|;
block|}
DECL|method|closeFileSystems (UserGroupInformation ugi)
specifier|protected
name|void
name|closeFileSystems
parameter_list|(
name|UserGroupInformation
name|ugi
parameter_list|)
block|{
try|try
block|{
name|FileSystem
operator|.
name|closeAllForUGI
argument_list|(
name|ugi
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
literal|"Failed to close filesystems: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|localizeFiles (LocalizationProtocol nodemanager, CompletionService<Path> cs, UserGroupInformation ugi)
specifier|protected
name|void
name|localizeFiles
parameter_list|(
name|LocalizationProtocol
name|nodemanager
parameter_list|,
name|CompletionService
argument_list|<
name|Path
argument_list|>
name|cs
parameter_list|,
name|UserGroupInformation
name|ugi
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|LocalizerStatus
name|status
init|=
name|createStatus
argument_list|()
decl_stmt|;
name|LocalizerHeartbeatResponse
name|response
init|=
name|nodemanager
operator|.
name|heartbeat
argument_list|(
name|status
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|response
operator|.
name|getLocalizerAction
argument_list|()
condition|)
block|{
case|case
name|LIVE
case|:
name|List
argument_list|<
name|LocalResource
argument_list|>
name|newResources
init|=
name|response
operator|.
name|getAllResources
argument_list|()
decl_stmt|;
for|for
control|(
name|LocalResource
name|r
range|:
name|newResources
control|)
block|{
if|if
condition|(
operator|!
name|pendingResources
operator|.
name|containsKey
argument_list|(
name|r
argument_list|)
condition|)
block|{
specifier|final
name|LocalDirAllocator
name|lda
decl_stmt|;
switch|switch
condition|(
name|r
operator|.
name|getVisibility
argument_list|()
condition|)
block|{
default|default:
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unknown visibility: "
operator|+
name|r
operator|.
name|getVisibility
argument_list|()
operator|+
literal|", Using userDirs"
argument_list|)
expr_stmt|;
comment|//Falling back to userDirs for unknown visibility.
case|case
name|PUBLIC
case|:
case|case
name|PRIVATE
case|:
name|lda
operator|=
name|userDirs
expr_stmt|;
break|break;
case|case
name|APPLICATION
case|:
name|lda
operator|=
name|appDirs
expr_stmt|;
break|break;
block|}
comment|// TODO: Synchronization??
name|pendingResources
operator|.
name|put
argument_list|(
name|r
argument_list|,
name|cs
operator|.
name|submit
argument_list|(
name|download
argument_list|(
name|lda
argument_list|,
name|r
argument_list|,
name|ugi
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
case|case
name|DIE
case|:
comment|// killall running localizations
for|for
control|(
name|Future
argument_list|<
name|Path
argument_list|>
name|pending
range|:
name|pendingResources
operator|.
name|values
argument_list|()
control|)
block|{
name|pending
operator|.
name|cancel
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|status
operator|=
name|createStatus
argument_list|()
expr_stmt|;
comment|// ignore response
try|try
block|{
name|nodemanager
operator|.
name|heartbeat
argument_list|(
name|status
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnRemoteException
name|e
parameter_list|)
block|{ }
return|return;
block|}
name|cs
operator|.
name|poll
argument_list|(
literal|1000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
return|return;
block|}
catch|catch
parameter_list|(
name|YarnRemoteException
name|e
parameter_list|)
block|{
comment|// TODO cleanup
return|return;
block|}
block|}
block|}
comment|/**    * Create the payload for the HeartBeat. Mainly the list of    * {@link LocalResourceStatus}es    *     * @return a {@link LocalizerStatus} that can be sent via heartbeat.    * @throws InterruptedException    */
DECL|method|createStatus ()
specifier|private
name|LocalizerStatus
name|createStatus
parameter_list|()
throws|throws
name|InterruptedException
block|{
specifier|final
name|List
argument_list|<
name|LocalResourceStatus
argument_list|>
name|currentResources
init|=
operator|new
name|ArrayList
argument_list|<
name|LocalResourceStatus
argument_list|>
argument_list|()
decl_stmt|;
comment|// TODO: Synchronization??
for|for
control|(
name|Iterator
argument_list|<
name|LocalResource
argument_list|>
name|i
init|=
name|pendingResources
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|LocalResource
name|rsrc
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|LocalResourceStatus
name|stat
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|LocalResourceStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|stat
operator|.
name|setResource
argument_list|(
name|rsrc
argument_list|)
expr_stmt|;
name|Future
argument_list|<
name|Path
argument_list|>
name|fPath
init|=
name|pendingResources
operator|.
name|get
argument_list|(
name|rsrc
argument_list|)
decl_stmt|;
if|if
condition|(
name|fPath
operator|.
name|isDone
argument_list|()
condition|)
block|{
try|try
block|{
name|Path
name|localPath
init|=
name|fPath
operator|.
name|get
argument_list|()
decl_stmt|;
name|stat
operator|.
name|setLocalPath
argument_list|(
name|ConverterUtils
operator|.
name|getYarnUrlFromPath
argument_list|(
name|localPath
argument_list|)
argument_list|)
expr_stmt|;
name|stat
operator|.
name|setLocalSize
argument_list|(
name|FileUtil
operator|.
name|getDU
argument_list|(
operator|new
name|File
argument_list|(
name|localPath
operator|.
name|getParent
argument_list|()
operator|.
name|toUri
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|stat
operator|.
name|setStatus
argument_list|(
name|ResourceStatusType
operator|.
name|FETCH_SUCCESS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|stat
operator|.
name|setStatus
argument_list|(
name|ResourceStatusType
operator|.
name|FETCH_FAILURE
argument_list|)
expr_stmt|;
name|stat
operator|.
name|setException
argument_list|(
name|RPCUtil
operator|.
name|getRemoteException
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CancellationException
name|e
parameter_list|)
block|{
name|stat
operator|.
name|setStatus
argument_list|(
name|ResourceStatusType
operator|.
name|FETCH_FAILURE
argument_list|)
expr_stmt|;
name|stat
operator|.
name|setException
argument_list|(
name|RPCUtil
operator|.
name|getRemoteException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// TODO shouldn't remove until ACK
name|i
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|stat
operator|.
name|setStatus
argument_list|(
name|ResourceStatusType
operator|.
name|FETCH_PENDING
argument_list|)
expr_stmt|;
block|}
name|currentResources
operator|.
name|add
argument_list|(
name|stat
argument_list|)
expr_stmt|;
block|}
name|LocalizerStatus
name|status
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|LocalizerStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|status
operator|.
name|setLocalizerId
argument_list|(
name|localizerId
argument_list|)
expr_stmt|;
name|status
operator|.
name|addAllResources
argument_list|(
name|currentResources
argument_list|)
expr_stmt|;
return|return
name|status
return|;
block|}
DECL|method|main (String[] argv)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|argv
parameter_list|)
throws|throws
name|Throwable
block|{
name|Thread
operator|.
name|setDefaultUncaughtExceptionHandler
argument_list|(
operator|new
name|YarnUncaughtExceptionHandler
argument_list|()
argument_list|)
expr_stmt|;
comment|// usage: $0 user appId locId host port app_log_dir user_dir [user_dir]*
comment|// let $x = $x/usercache for $local.dir
comment|// MKDIR $x/$user/appcache/$appid
comment|// MKDIR $x/$user/appcache/$appid/output
comment|// MKDIR $x/$user/appcache/$appid/filecache
comment|// LOAD $x/$user/appcache/$appid/appTokens
try|try
block|{
name|String
name|user
init|=
name|argv
index|[
literal|0
index|]
decl_stmt|;
name|String
name|appId
init|=
name|argv
index|[
literal|1
index|]
decl_stmt|;
name|String
name|locId
init|=
name|argv
index|[
literal|2
index|]
decl_stmt|;
name|InetSocketAddress
name|nmAddr
init|=
operator|new
name|InetSocketAddress
argument_list|(
name|argv
index|[
literal|3
index|]
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|argv
index|[
literal|4
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|String
index|[]
name|sLocaldirs
init|=
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|argv
argument_list|,
literal|5
argument_list|,
name|argv
operator|.
name|length
argument_list|)
decl_stmt|;
name|ArrayList
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
argument_list|(
name|sLocaldirs
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|sLocaldir
range|:
name|sLocaldirs
control|)
block|{
name|localDirs
operator|.
name|add
argument_list|(
operator|new
name|Path
argument_list|(
name|sLocaldir
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|uid
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|user
operator|.
name|equals
argument_list|(
name|uid
argument_list|)
condition|)
block|{
comment|// TODO: fail localization
name|LOG
operator|.
name|warn
argument_list|(
literal|"Localization running as "
operator|+
name|uid
operator|+
literal|" not "
operator|+
name|user
argument_list|)
expr_stmt|;
block|}
name|ContainerLocalizer
name|localizer
init|=
operator|new
name|ContainerLocalizer
argument_list|(
name|FileContext
operator|.
name|getLocalFSFileContext
argument_list|()
argument_list|,
name|user
argument_list|,
name|appId
argument_list|,
name|locId
argument_list|,
name|localDirs
argument_list|,
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|System
operator|.
name|exit
argument_list|(
name|localizer
operator|.
name|runLocalization
argument_list|(
name|nmAddr
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
comment|// Print error to stdout so that LCE can use it.
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
DECL|method|initDirs (Configuration conf, String user, String appId, FileContext lfs, List<Path> localDirs)
specifier|private
specifier|static
name|void
name|initDirs
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|appId
parameter_list|,
name|FileContext
name|lfs
parameter_list|,
name|List
argument_list|<
name|Path
argument_list|>
name|localDirs
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
literal|null
operator|==
name|localDirs
operator|||
literal|0
operator|==
name|localDirs
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot initialize without local dirs"
argument_list|)
throw|;
block|}
name|String
index|[]
name|appsFileCacheDirs
init|=
operator|new
name|String
index|[
name|localDirs
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|String
index|[]
name|usersFileCacheDirs
init|=
operator|new
name|String
index|[
name|localDirs
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|n
init|=
name|localDirs
operator|.
name|size
argument_list|()
init|;
name|i
operator|<
name|n
condition|;
operator|++
name|i
control|)
block|{
comment|// $x/usercache/$user
name|Path
name|base
init|=
name|lfs
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
operator|new
name|Path
argument_list|(
name|localDirs
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|USERCACHE
argument_list|)
argument_list|,
name|user
argument_list|)
argument_list|)
decl_stmt|;
comment|// $x/usercache/$user/filecache
name|Path
name|userFileCacheDir
init|=
operator|new
name|Path
argument_list|(
name|base
argument_list|,
name|FILECACHE
argument_list|)
decl_stmt|;
name|usersFileCacheDirs
index|[
name|i
index|]
operator|=
name|userFileCacheDir
operator|.
name|toString
argument_list|()
expr_stmt|;
name|lfs
operator|.
name|mkdir
argument_list|(
name|userFileCacheDir
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// $x/usercache/$user/appcache/$appId
name|Path
name|appBase
init|=
operator|new
name|Path
argument_list|(
name|base
argument_list|,
operator|new
name|Path
argument_list|(
name|APPCACHE
argument_list|,
name|appId
argument_list|)
argument_list|)
decl_stmt|;
comment|// $x/usercache/$user/appcache/$appId/filecache
name|Path
name|appFileCacheDir
init|=
operator|new
name|Path
argument_list|(
name|appBase
argument_list|,
name|FILECACHE
argument_list|)
decl_stmt|;
name|appsFileCacheDirs
index|[
name|i
index|]
operator|=
name|appFileCacheDir
operator|.
name|toString
argument_list|()
expr_stmt|;
name|lfs
operator|.
name|mkdir
argument_list|(
name|appFileCacheDir
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|conf
operator|.
name|setStrings
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|APPCACHE_CTXT_FMT
argument_list|,
name|appId
argument_list|)
argument_list|,
name|appsFileCacheDirs
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setStrings
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|USERCACHE_CTXT_FMT
argument_list|,
name|user
argument_list|)
argument_list|,
name|usersFileCacheDirs
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

