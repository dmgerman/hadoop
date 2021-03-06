begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.shortcircuit
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|shortcircuit
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
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|HadoopIllegalArgumentException
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
name|DFSUtilClient
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
name|client
operator|.
name|HdfsClientConfigKeys
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
name|client
operator|.
name|impl
operator|.
name|DfsClientConf
operator|.
name|ShortCircuitConf
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
name|unix
operator|.
name|DomainSocket
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
name|PerformanceAdvisory
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
name|base
operator|.
name|Preconditions
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
name|cache
operator|.
name|Cache
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
name|cache
operator|.
name|CacheBuilder
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

begin_class
DECL|class|DomainSocketFactory
specifier|public
class|class
name|DomainSocketFactory
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
name|DomainSocketFactory
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|enum|PathState
specifier|public
enum|enum
name|PathState
block|{
DECL|enumConstant|UNUSABLE
name|UNUSABLE
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
block|,
DECL|enumConstant|SHORT_CIRCUIT_DISABLED
name|SHORT_CIRCUIT_DISABLED
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
block|,
DECL|enumConstant|VALID
name|VALID
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
block|;
DECL|method|PathState (boolean usableForDataTransfer, boolean usableForShortCircuit)
name|PathState
parameter_list|(
name|boolean
name|usableForDataTransfer
parameter_list|,
name|boolean
name|usableForShortCircuit
parameter_list|)
block|{
name|this
operator|.
name|usableForDataTransfer
operator|=
name|usableForDataTransfer
expr_stmt|;
name|this
operator|.
name|usableForShortCircuit
operator|=
name|usableForShortCircuit
expr_stmt|;
block|}
DECL|method|getUsableForDataTransfer ()
specifier|public
name|boolean
name|getUsableForDataTransfer
parameter_list|()
block|{
return|return
name|usableForDataTransfer
return|;
block|}
DECL|method|getUsableForShortCircuit ()
specifier|public
name|boolean
name|getUsableForShortCircuit
parameter_list|()
block|{
return|return
name|usableForShortCircuit
return|;
block|}
DECL|field|usableForDataTransfer
specifier|private
specifier|final
name|boolean
name|usableForDataTransfer
decl_stmt|;
DECL|field|usableForShortCircuit
specifier|private
specifier|final
name|boolean
name|usableForShortCircuit
decl_stmt|;
block|}
DECL|class|PathInfo
specifier|public
specifier|static
class|class
name|PathInfo
block|{
DECL|field|NOT_CONFIGURED
specifier|private
specifier|final
specifier|static
name|PathInfo
name|NOT_CONFIGURED
init|=
operator|new
name|PathInfo
argument_list|(
literal|""
argument_list|,
name|PathState
operator|.
name|UNUSABLE
argument_list|)
decl_stmt|;
DECL|field|path
specifier|final
specifier|private
name|String
name|path
decl_stmt|;
DECL|field|state
specifier|final
specifier|private
name|PathState
name|state
decl_stmt|;
DECL|method|PathInfo (String path, PathState state)
name|PathInfo
parameter_list|(
name|String
name|path
parameter_list|,
name|PathState
name|state
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
block|}
DECL|method|getPath ()
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
DECL|method|getPathState ()
specifier|public
name|PathState
name|getPathState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"PathInfo{path="
operator|+
name|path
operator|+
literal|", state="
operator|+
name|state
operator|+
literal|"}"
return|;
block|}
block|}
comment|/**    * Information about domain socket paths.    */
DECL|field|pathExpireSeconds
specifier|private
specifier|final
name|long
name|pathExpireSeconds
decl_stmt|;
DECL|field|pathMap
specifier|private
specifier|final
name|Cache
argument_list|<
name|String
argument_list|,
name|PathState
argument_list|>
name|pathMap
decl_stmt|;
DECL|method|DomainSocketFactory (ShortCircuitConf conf)
specifier|public
name|DomainSocketFactory
parameter_list|(
name|ShortCircuitConf
name|conf
parameter_list|)
block|{
specifier|final
name|String
name|feature
decl_stmt|;
if|if
condition|(
name|conf
operator|.
name|isShortCircuitLocalReads
argument_list|()
operator|&&
operator|(
operator|!
name|conf
operator|.
name|isUseLegacyBlockReaderLocal
argument_list|()
operator|)
condition|)
block|{
name|feature
operator|=
literal|"The short-circuit local reads feature"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|conf
operator|.
name|isDomainSocketDataTraffic
argument_list|()
condition|)
block|{
name|feature
operator|=
literal|"UNIX domain socket data traffic"
expr_stmt|;
block|}
else|else
block|{
name|feature
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|feature
operator|==
literal|null
condition|)
block|{
name|PerformanceAdvisory
operator|.
name|LOG
operator|.
name|debug
argument_list|(
literal|"Both short-circuit local reads and UNIX domain socket are disabled."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|conf
operator|.
name|getDomainSocketPath
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
name|feature
operator|+
literal|" is enabled but "
operator|+
name|HdfsClientConfigKeys
operator|.
name|DFS_DOMAIN_SOCKET_PATH_KEY
operator|+
literal|" is not set."
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|DomainSocket
operator|.
name|getLoadingFailureReason
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|feature
operator|+
literal|" cannot be used because "
operator|+
name|DomainSocket
operator|.
name|getLoadingFailureReason
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|feature
operator|+
literal|" is enabled."
argument_list|)
expr_stmt|;
block|}
block|}
name|pathExpireSeconds
operator|=
name|conf
operator|.
name|getDomainSocketDisableIntervalSeconds
argument_list|()
expr_stmt|;
name|pathMap
operator|=
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|expireAfterWrite
argument_list|(
name|pathExpireSeconds
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
comment|/**    * Get information about a domain socket path.    *    * @param addr         The inet address to use.    * @param conf         The client configuration.    *    * @return             Information about the socket path.    */
DECL|method|getPathInfo (InetSocketAddress addr, ShortCircuitConf conf)
specifier|public
name|PathInfo
name|getPathInfo
parameter_list|(
name|InetSocketAddress
name|addr
parameter_list|,
name|ShortCircuitConf
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
comment|// If there is no domain socket path configured, we can't use domain
comment|// sockets.
if|if
condition|(
name|conf
operator|.
name|getDomainSocketPath
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
name|PathInfo
operator|.
name|NOT_CONFIGURED
return|;
comment|// If we can't do anything with the domain socket, don't create it.
if|if
condition|(
operator|!
name|conf
operator|.
name|isDomainSocketDataTraffic
argument_list|()
operator|&&
operator|(
operator|!
name|conf
operator|.
name|isShortCircuitLocalReads
argument_list|()
operator|||
name|conf
operator|.
name|isUseLegacyBlockReaderLocal
argument_list|()
operator|)
condition|)
block|{
return|return
name|PathInfo
operator|.
name|NOT_CONFIGURED
return|;
block|}
comment|// If the DomainSocket code is not loaded, we can't create
comment|// DomainSocket objects.
if|if
condition|(
name|DomainSocket
operator|.
name|getLoadingFailureReason
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|PathInfo
operator|.
name|NOT_CONFIGURED
return|;
block|}
comment|// UNIX domain sockets can only be used to talk to local peers
if|if
condition|(
operator|!
name|DFSUtilClient
operator|.
name|isLocalAddress
argument_list|(
name|addr
argument_list|)
condition|)
return|return
name|PathInfo
operator|.
name|NOT_CONFIGURED
return|;
name|String
name|escapedPath
init|=
name|DomainSocket
operator|.
name|getEffectivePath
argument_list|(
name|conf
operator|.
name|getDomainSocketPath
argument_list|()
argument_list|,
name|addr
operator|.
name|getPort
argument_list|()
argument_list|)
decl_stmt|;
name|PathState
name|status
init|=
name|pathMap
operator|.
name|getIfPresent
argument_list|(
name|escapedPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|status
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|PathInfo
argument_list|(
name|escapedPath
argument_list|,
name|PathState
operator|.
name|VALID
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|PathInfo
argument_list|(
name|escapedPath
argument_list|,
name|status
argument_list|)
return|;
block|}
block|}
DECL|method|createSocket (PathInfo info, int socketTimeout)
specifier|public
name|DomainSocket
name|createSocket
parameter_list|(
name|PathInfo
name|info
parameter_list|,
name|int
name|socketTimeout
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|info
operator|.
name|getPathState
argument_list|()
operator|!=
name|PathState
operator|.
name|UNUSABLE
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|DomainSocket
name|sock
init|=
literal|null
decl_stmt|;
try|try
block|{
name|sock
operator|=
name|DomainSocket
operator|.
name|connect
argument_list|(
name|info
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|sock
operator|.
name|setAttribute
argument_list|(
name|DomainSocket
operator|.
name|RECEIVE_TIMEOUT
argument_list|,
name|socketTimeout
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
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
literal|"error creating DomainSocket"
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|// fall through
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
if|if
condition|(
name|sock
operator|!=
literal|null
condition|)
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|sock
argument_list|)
expr_stmt|;
block|}
name|pathMap
operator|.
name|put
argument_list|(
name|info
operator|.
name|getPath
argument_list|()
argument_list|,
name|PathState
operator|.
name|UNUSABLE
argument_list|)
expr_stmt|;
name|sock
operator|=
literal|null
expr_stmt|;
block|}
block|}
return|return
name|sock
return|;
block|}
DECL|method|disableShortCircuitForPath (String path)
specifier|public
name|void
name|disableShortCircuitForPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|pathMap
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|PathState
operator|.
name|SHORT_CIRCUIT_DISABLED
argument_list|)
expr_stmt|;
block|}
DECL|method|disableDomainSocketPath (String path)
specifier|public
name|void
name|disableDomainSocketPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|pathMap
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|PathState
operator|.
name|UNUSABLE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|clearPathMap ()
specifier|public
name|void
name|clearPathMap
parameter_list|()
block|{
name|pathMap
operator|.
name|invalidateAll
argument_list|()
expr_stmt|;
block|}
DECL|method|getPathExpireSeconds ()
specifier|public
name|long
name|getPathExpireSeconds
parameter_list|()
block|{
return|return
name|pathExpireSeconds
return|;
block|}
block|}
end_class

end_unit

