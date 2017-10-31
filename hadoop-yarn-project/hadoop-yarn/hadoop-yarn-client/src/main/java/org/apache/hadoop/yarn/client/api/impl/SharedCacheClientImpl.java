begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client.api.impl
package|package
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
name|impl
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Unstable
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
name|FSDataInputStream
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
name|yarn
operator|.
name|api
operator|.
name|ClientSCMProtocol
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
name|ReleaseSharedCacheResourceRequest
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
name|UseSharedCacheResourceRequest
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
name|UseSharedCacheResourceResponse
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
name|client
operator|.
name|api
operator|.
name|SharedCacheClient
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
name|YarnException
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
name|sharedcache
operator|.
name|SharedCacheChecksum
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
name|sharedcache
operator|.
name|SharedCacheChecksumFactory
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * An implementation of the SharedCacheClient API.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|SharedCacheClientImpl
specifier|public
class|class
name|SharedCacheClientImpl
extends|extends
name|SharedCacheClient
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SharedCacheClientImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|scmClient
specifier|private
name|ClientSCMProtocol
name|scmClient
decl_stmt|;
DECL|field|scmAddress
specifier|private
name|InetSocketAddress
name|scmAddress
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|checksum
specifier|private
name|SharedCacheChecksum
name|checksum
decl_stmt|;
DECL|method|SharedCacheClientImpl ()
specifier|public
name|SharedCacheClientImpl
parameter_list|()
block|{
name|super
argument_list|(
name|SharedCacheClientImpl
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getScmAddress (Configuration conf)
specifier|private
specifier|static
name|InetSocketAddress
name|getScmAddress
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|conf
operator|.
name|getSocketAddr
argument_list|(
name|YarnConfiguration
operator|.
name|SCM_CLIENT_SERVER_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_SCM_CLIENT_SERVER_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_SCM_CLIENT_SERVER_PORT
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|protected
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|this
operator|.
name|scmAddress
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|scmAddress
operator|=
name|getScmAddress
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|checksum
operator|=
name|SharedCacheChecksumFactory
operator|.
name|getChecksum
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|scmClient
operator|=
name|createClientProxy
argument_list|()
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Connecting to Shared Cache Manager at "
operator|+
name|this
operator|.
name|scmAddress
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
name|stopClientProxy
argument_list|()
expr_stmt|;
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|createClientProxy ()
specifier|protected
name|ClientSCMProtocol
name|createClientProxy
parameter_list|()
block|{
name|YarnRPC
name|rpc
init|=
name|YarnRPC
operator|.
name|create
argument_list|(
name|getConfig
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|(
name|ClientSCMProtocol
operator|)
name|rpc
operator|.
name|getProxy
argument_list|(
name|ClientSCMProtocol
operator|.
name|class
argument_list|,
name|this
operator|.
name|scmAddress
argument_list|,
name|getConfig
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|stopClientProxy ()
specifier|protected
name|void
name|stopClientProxy
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|scmClient
operator|!=
literal|null
condition|)
block|{
name|RPC
operator|.
name|stopProxy
argument_list|(
name|this
operator|.
name|scmClient
argument_list|)
expr_stmt|;
name|this
operator|.
name|scmClient
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|use (ApplicationId applicationId, String resourceKey)
specifier|public
name|URL
name|use
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|,
name|String
name|resourceKey
parameter_list|)
throws|throws
name|YarnException
block|{
name|Path
name|resourcePath
init|=
literal|null
decl_stmt|;
name|UseSharedCacheResourceRequest
name|request
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|UseSharedCacheResourceRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setAppId
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
name|request
operator|.
name|setResourceKey
argument_list|(
name|resourceKey
argument_list|)
expr_stmt|;
try|try
block|{
name|UseSharedCacheResourceResponse
name|response
init|=
name|this
operator|.
name|scmClient
operator|.
name|use
argument_list|(
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|!=
literal|null
operator|&&
name|response
operator|.
name|getPath
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|resourcePath
operator|=
operator|new
name|Path
argument_list|(
name|response
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Just catching IOException isn't enough.
comment|// RPC call can throw ConnectionException.
comment|// We don't handle different exceptions separately at this point.
throw|throw
operator|new
name|YarnException
argument_list|(
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|resourcePath
operator|!=
literal|null
condition|)
block|{
name|URL
name|pathURL
init|=
name|URL
operator|.
name|fromPath
argument_list|(
name|resourcePath
argument_list|)
decl_stmt|;
return|return
name|pathURL
return|;
block|}
else|else
block|{
comment|// The resource was not in the cache.
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|release (ApplicationId applicationId, String resourceKey)
specifier|public
name|void
name|release
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|,
name|String
name|resourceKey
parameter_list|)
throws|throws
name|YarnException
block|{
name|ReleaseSharedCacheResourceRequest
name|request
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ReleaseSharedCacheResourceRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setAppId
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
name|request
operator|.
name|setResourceKey
argument_list|(
name|resourceKey
argument_list|)
expr_stmt|;
try|try
block|{
comment|// We do not care about the response because it is empty.
name|this
operator|.
name|scmClient
operator|.
name|release
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Just catching IOException isn't enough.
comment|// RPC call can throw ConnectionException.
throw|throw
operator|new
name|YarnException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getFileChecksum (Path sourceFile)
specifier|public
name|String
name|getFileChecksum
parameter_list|(
name|Path
name|sourceFile
parameter_list|)
throws|throws
name|IOException
block|{
name|FileSystem
name|fs
init|=
name|sourceFile
operator|.
name|getFileSystem
argument_list|(
name|this
operator|.
name|conf
argument_list|)
decl_stmt|;
name|FSDataInputStream
name|in
init|=
literal|null
decl_stmt|;
try|try
block|{
name|in
operator|=
name|fs
operator|.
name|open
argument_list|(
name|sourceFile
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|checksum
operator|.
name|computeChecksum
argument_list|(
name|in
argument_list|)
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|in
operator|!=
literal|null
condition|)
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

