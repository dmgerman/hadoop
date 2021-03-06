begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
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
name|HAUtilClient
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
comment|/**  * Extends {@link ObserverReadProxyProvider} to support NameNode IP failover.  *  * For Observer reads a client needs to know physical addresses of all  * NameNodes, so that it could switch between active and observer nodes  * for write and read requests.  *  * Traditional {@link IPFailoverProxyProvider} works with a virtual  * address of the NameNode. If active NameNode fails the virtual address  * is assigned to the standby NameNode, and IPFailoverProxyProvider, which  * keeps talking to the same virtual address is in fact now connects to  * the new physical server.  *  * To combine these behaviors ObserverReadProxyProviderWithIPFailover  * should both  *<ol>  *<li> Maintain all physical addresses of NameNodes in order to allow  * observer reads, and  *<li> Should rely on the virtual address of the NameNode in order to  * perform failover by assuming that the virtual address always points  * to the active NameNode.  *</ol>  *  * An example of a configuration to leverage  * ObserverReadProxyProviderWithIPFailover  * should include the following values:  *<pre>{@code  * fs.defaultFS = hdfs://mycluster  * dfs.nameservices = mycluster  * dfs.ha.namenodes.mycluster = ha1,ha2  * dfs.namenode.rpc-address.mycluster.ha1 = nn01-ha1.com:8020  * dfs.namenode.rpc-address.mycluster.ha2 = nn01-ha2.com:8020  * dfs.client.failover.ipfailover.virtual-address.mycluster =  *     hdfs://nn01.com:8020  * dfs.client.failover.proxy.provider.mycluster =  *     org.apache...ObserverReadProxyProviderWithIPFailover  * }</pre>  * Here {@code nn01.com:8020} is the virtual address of the active NameNode,  * while {@code nn01-ha1.com:8020} and {@code nn01-ha2.com:8020}  * are the physically addresses the two NameNodes.  *  * With this configuration, client will use  * ObserverReadProxyProviderWithIPFailover, which creates proxies for both  * nn01-ha1 and nn01-ha2, used for read/write RPC calls, but for the failover,  * it relies on the virtual address nn01.com  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|ObserverReadProxyProviderWithIPFailover
specifier|public
class|class
name|ObserverReadProxyProviderWithIPFailover
parameter_list|<
name|T
extends|extends
name|ClientProtocol
parameter_list|>
extends|extends
name|ObserverReadProxyProvider
argument_list|<
name|T
argument_list|>
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
name|ObserverReadProxyProviderWithIPFailover
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|IPFAILOVER_CONFIG_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|IPFAILOVER_CONFIG_PREFIX
init|=
name|HdfsClientConfigKeys
operator|.
name|Failover
operator|.
name|PREFIX
operator|+
literal|"ipfailover.virtual-address"
decl_stmt|;
comment|/**    * By default ObserverReadProxyProviderWithIPFailover    * uses {@link IPFailoverProxyProvider} for failover.    */
DECL|method|ObserverReadProxyProviderWithIPFailover ( Configuration conf, URI uri, Class<T> xface, HAProxyFactory<T> factory)
specifier|public
name|ObserverReadProxyProviderWithIPFailover
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|URI
name|uri
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|xface
parameter_list|,
name|HAProxyFactory
argument_list|<
name|T
argument_list|>
name|factory
parameter_list|)
block|{
name|this
argument_list|(
name|conf
argument_list|,
name|uri
argument_list|,
name|xface
argument_list|,
name|factory
argument_list|,
operator|new
name|IPFailoverProxyProvider
argument_list|<>
argument_list|(
name|conf
argument_list|,
name|getFailoverVirtualIP
argument_list|(
name|conf
argument_list|,
name|uri
operator|.
name|getHost
argument_list|()
argument_list|)
argument_list|,
name|xface
argument_list|,
name|factory
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|useLogicalURI ()
specifier|public
name|boolean
name|useLogicalURI
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
DECL|method|ObserverReadProxyProviderWithIPFailover ( Configuration conf, URI uri, Class<T> xface, HAProxyFactory<T> factory, AbstractNNFailoverProxyProvider<T> failoverProxy)
specifier|public
name|ObserverReadProxyProviderWithIPFailover
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|URI
name|uri
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|xface
parameter_list|,
name|HAProxyFactory
argument_list|<
name|T
argument_list|>
name|factory
parameter_list|,
name|AbstractNNFailoverProxyProvider
argument_list|<
name|T
argument_list|>
name|failoverProxy
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|,
name|uri
argument_list|,
name|xface
argument_list|,
name|factory
argument_list|,
name|failoverProxy
argument_list|)
expr_stmt|;
name|cloneDelegationTokenForVirtualIP
argument_list|(
name|conf
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
comment|/**    * Clone delegation token for the virtual IP. Specifically    * clone the dt that corresponds to the name service uri,    * to the configured corresponding virtual IP.    *    * @param conf configuration    * @param haURI the ha uri, a name service id in this case.    */
DECL|method|cloneDelegationTokenForVirtualIP ( Configuration conf, URI haURI)
specifier|private
name|void
name|cloneDelegationTokenForVirtualIP
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|URI
name|haURI
parameter_list|)
block|{
name|URI
name|virtualIPURI
init|=
name|getFailoverVirtualIP
argument_list|(
name|conf
argument_list|,
name|haURI
operator|.
name|getHost
argument_list|()
argument_list|)
decl_stmt|;
name|InetSocketAddress
name|vipAddress
init|=
operator|new
name|InetSocketAddress
argument_list|(
name|virtualIPURI
operator|.
name|getHost
argument_list|()
argument_list|,
name|virtualIPURI
operator|.
name|getPort
argument_list|()
argument_list|)
decl_stmt|;
name|HAUtilClient
operator|.
name|cloneDelegationTokenForLogicalUri
argument_list|(
name|ugi
argument_list|,
name|haURI
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|vipAddress
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getFailoverVirtualIP ( Configuration conf, String nameServiceID)
specifier|private
specifier|static
name|URI
name|getFailoverVirtualIP
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|nameServiceID
parameter_list|)
block|{
name|String
name|configKey
init|=
name|IPFAILOVER_CONFIG_PREFIX
operator|+
literal|"."
operator|+
name|nameServiceID
decl_stmt|;
name|String
name|virtualIP
init|=
name|conf
operator|.
name|get
argument_list|(
name|configKey
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Name service ID {} will use virtual IP {} for failover"
argument_list|,
name|nameServiceID
argument_list|,
name|virtualIP
argument_list|)
expr_stmt|;
if|if
condition|(
name|virtualIP
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Virtual IP for failover not found,"
operator|+
literal|"misconfigured "
operator|+
name|configKey
operator|+
literal|"?"
argument_list|)
throw|;
block|}
return|return
name|URI
operator|.
name|create
argument_list|(
name|virtualIP
argument_list|)
return|;
block|}
block|}
end_class

end_unit

