begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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
name|hdfs
operator|.
name|DFSClient
operator|.
name|Conf
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

begin_class
DECL|class|DomainSocketFactory
class|class
name|DomainSocketFactory
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|DomainSocketFactory
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Conf
name|conf
decl_stmt|;
DECL|enum|PathStatus
enum|enum
name|PathStatus
block|{
DECL|enumConstant|UNUSABLE
name|UNUSABLE
block|,
DECL|enumConstant|SHORT_CIRCUIT_DISABLED
name|SHORT_CIRCUIT_DISABLED
block|,   }
comment|/**    * Information about domain socket paths.    */
DECL|field|pathInfo
name|Cache
argument_list|<
name|String
argument_list|,
name|PathStatus
argument_list|>
name|pathInfo
init|=
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|expireAfterWrite
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|method|DomainSocketFactory (Conf conf)
specifier|public
name|DomainSocketFactory
parameter_list|(
name|Conf
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|String
name|feature
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|conf
operator|.
name|shortCircuitLocalReads
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
name|domainSocketDataTraffic
condition|)
block|{
name|feature
operator|=
literal|"UNIX domain socket data traffic"
expr_stmt|;
block|}
if|if
condition|(
name|feature
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|conf
operator|.
name|domainSocketPath
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|feature
operator|+
literal|" is disabled because you have not set "
operator|+
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DOMAIN_SOCKET_PATH_KEY
argument_list|)
expr_stmt|;
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
literal|" is disabled because "
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
literal|"is enabled."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Create a DomainSocket.    *     * @param addr        The address of the DataNode    * @param stream      The DFSInputStream the socket will be created for.    *    * @return            null if the socket could not be created; the    *                    socket otherwise.  If there was an error while    *                    creating the socket, we will add the socket path    *                    to our list of failed domain socket paths.    */
DECL|method|create (InetSocketAddress addr, DFSInputStream stream)
name|DomainSocket
name|create
parameter_list|(
name|InetSocketAddress
name|addr
parameter_list|,
name|DFSInputStream
name|stream
parameter_list|)
block|{
comment|// If there is no domain socket path configured, we can't use domain
comment|// sockets.
if|if
condition|(
name|conf
operator|.
name|domainSocketPath
operator|==
literal|null
condition|)
return|return
literal|null
return|;
comment|// UNIX domain sockets can only be used to talk to local peers
if|if
condition|(
operator|!
name|DFSClient
operator|.
name|isLocalAddress
argument_list|(
name|addr
argument_list|)
condition|)
return|return
literal|null
return|;
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
return|return
literal|null
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
name|domainSocketPath
argument_list|,
name|addr
operator|.
name|getPort
argument_list|()
argument_list|)
decl_stmt|;
name|PathStatus
name|info
init|=
name|pathInfo
operator|.
name|getIfPresent
argument_list|(
name|escapedPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|==
name|PathStatus
operator|.
name|UNUSABLE
condition|)
block|{
comment|// We tried to connect to this domain socket before, and it was totally
comment|// unusable.
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|(
operator|!
name|conf
operator|.
name|domainSocketDataTraffic
operator|)
operator|&&
operator|(
operator|(
name|info
operator|==
name|PathStatus
operator|.
name|SHORT_CIRCUIT_DISABLED
operator|)
operator|||
name|stream
operator|.
name|shortCircuitForbidden
argument_list|()
operator|)
condition|)
block|{
comment|// If we don't want to pass data over domain sockets, and we don't want
comment|// to pass file descriptors over them either, we have no use for domain
comment|// sockets.
return|return
literal|null
return|;
block|}
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
name|escapedPath
argument_list|)
expr_stmt|;
name|sock
operator|.
name|setAttribute
argument_list|(
name|DomainSocket
operator|.
name|RCV_TIMEO
argument_list|,
name|conf
operator|.
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
name|pathInfo
operator|.
name|put
argument_list|(
name|escapedPath
argument_list|,
name|PathStatus
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
name|pathInfo
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|PathStatus
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
name|pathInfo
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|PathStatus
operator|.
name|UNUSABLE
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

