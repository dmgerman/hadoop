begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.ha
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|ha
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|Collection
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
name|Configurable
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
name|hdfs
operator|.
name|DFSUtil
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
name|protocol
operator|.
name|ClientProtocol
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
name|retry
operator|.
name|FailoverProxyProvider
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
name|security
operator|.
name|UserGroupInformation
import|;
end_import

begin_comment
comment|/**  * A FailoverProxyProvider implementation which allows one to configure two URIs  * to connect to during fail-over. The first configured address is tried first,  * and on a fail-over event the other address is tried.  */
end_comment

begin_class
DECL|class|ConfiguredFailoverProxyProvider
specifier|public
class|class
name|ConfiguredFailoverProxyProvider
implements|implements
name|FailoverProxyProvider
implements|,
name|Configurable
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
name|ConfiguredFailoverProxyProvider
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|currentProxyIndex
specifier|private
name|int
name|currentProxyIndex
init|=
literal|0
decl_stmt|;
DECL|field|proxies
specifier|private
name|List
argument_list|<
name|AddressRpcProxyPair
argument_list|>
name|proxies
init|=
operator|new
name|ArrayList
argument_list|<
name|AddressRpcProxyPair
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|ugi
specifier|private
name|UserGroupInformation
name|ugi
decl_stmt|;
annotation|@
name|Override
DECL|method|getInterface ()
specifier|public
name|Class
argument_list|<
name|?
argument_list|>
name|getInterface
parameter_list|()
block|{
return|return
name|ClientProtocol
operator|.
name|class
return|;
block|}
comment|/**    * Lazily initialize the RPC proxy object.    */
annotation|@
name|Override
DECL|method|getProxy ()
specifier|public
specifier|synchronized
name|Object
name|getProxy
parameter_list|()
block|{
name|AddressRpcProxyPair
name|current
init|=
name|proxies
operator|.
name|get
argument_list|(
name|currentProxyIndex
argument_list|)
decl_stmt|;
if|if
condition|(
name|current
operator|.
name|namenode
operator|==
literal|null
condition|)
block|{
try|try
block|{
comment|// TODO(HA): This will create a NN proxy with an underlying retry
comment|// proxy. We don't want this.
name|current
operator|.
name|namenode
operator|=
name|DFSUtil
operator|.
name|createNamenode
argument_list|(
name|current
operator|.
name|address
argument_list|,
name|conf
argument_list|,
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
name|error
argument_list|(
literal|"Failed to create RPC proxy to NameNode"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|current
operator|.
name|namenode
return|;
block|}
annotation|@
name|Override
DECL|method|performFailover (Object currentProxy)
specifier|public
specifier|synchronized
name|void
name|performFailover
parameter_list|(
name|Object
name|currentProxy
parameter_list|)
block|{
name|currentProxyIndex
operator|=
operator|(
name|currentProxyIndex
operator|+
literal|1
operator|)
operator|%
name|proxies
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getConf ()
specifier|public
specifier|synchronized
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
annotation|@
name|Override
DECL|method|setConf (Configuration conf)
specifier|public
specifier|synchronized
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
try|try
block|{
name|ugi
operator|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|InetSocketAddress
argument_list|>
argument_list|>
name|map
init|=
name|DFSUtil
operator|.
name|getHaNnRpcAddresses
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// TODO(HA): currently hardcoding the nameservice used by MiniDFSCluster.
comment|// We need to somehow communicate this into the proxy provider.
name|String
name|nsId
init|=
literal|"nameserviceId1"
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|InetSocketAddress
argument_list|>
name|addressesInNN
init|=
name|map
operator|.
name|get
argument_list|(
name|nsId
argument_list|)
decl_stmt|;
for|for
control|(
name|InetSocketAddress
name|address
range|:
name|addressesInNN
operator|.
name|values
argument_list|()
control|)
block|{
name|proxies
operator|.
name|add
argument_list|(
operator|new
name|AddressRpcProxyPair
argument_list|(
name|address
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
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
comment|/**    * A little pair object to store the address and connected RPC proxy object to    * an NN. Note that {@link AddressRpcProxyPair#namenode} may be null.    */
DECL|class|AddressRpcProxyPair
specifier|private
specifier|static
class|class
name|AddressRpcProxyPair
block|{
DECL|field|address
specifier|public
name|InetSocketAddress
name|address
decl_stmt|;
DECL|field|namenode
specifier|public
name|ClientProtocol
name|namenode
decl_stmt|;
DECL|method|AddressRpcProxyPair (InetSocketAddress address)
specifier|public
name|AddressRpcProxyPair
parameter_list|(
name|InetSocketAddress
name|address
parameter_list|)
block|{
name|this
operator|.
name|address
operator|=
name|address
expr_stmt|;
block|}
block|}
comment|/**    * Close all the proxy objects which have been opened over the lifetime of    * this proxy provider.    */
annotation|@
name|Override
DECL|method|close ()
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|AddressRpcProxyPair
name|proxy
range|:
name|proxies
control|)
block|{
if|if
condition|(
name|proxy
operator|.
name|namenode
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|proxy
operator|.
name|namenode
operator|instanceof
name|Closeable
condition|)
block|{
operator|(
operator|(
name|Closeable
operator|)
name|proxy
operator|.
name|namenode
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|RPC
operator|.
name|stopProxy
argument_list|(
name|proxy
operator|.
name|namenode
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

