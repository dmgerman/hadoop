begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.router
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
name|federation
operator|.
name|router
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_ROUTER_HEARTBEAT_INTERVAL_MS
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
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_ROUTER_HEARTBEAT_INTERVAL_MS_DEFAULT
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
name|InetAddress
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
name|Map
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
name|ha
operator|.
name|HAServiceProtocol
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
name|ha
operator|.
name|HAServiceStatus
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
name|DFSConfigKeys
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
name|NameNodeProxies
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
name|hdfs
operator|.
name|protocol
operator|.
name|HdfsConstants
operator|.
name|SafeModeAction
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
name|server
operator|.
name|federation
operator|.
name|resolver
operator|.
name|ActiveNamenodeResolver
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
name|server
operator|.
name|federation
operator|.
name|resolver
operator|.
name|NamenodeStatusReport
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
name|server
operator|.
name|protocol
operator|.
name|NamenodeProtocol
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
name|server
operator|.
name|protocol
operator|.
name|NamespaceInfo
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
name|tools
operator|.
name|NNHAServiceTarget
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jettison
operator|.
name|json
operator|.
name|JSONArray
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jettison
operator|.
name|json
operator|.
name|JSONObject
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
comment|/**  * The {@link Router} periodically checks the state of a Namenode (usually on  * the same server) and reports their high availability (HA) state and  * load/space status to the  * {@link org.apache.hadoop.hdfs.server.federation.store.StateStoreService}  * . Note that this is an optional role as a Router can be independent of any  * subcluster.  *<p>  * For performance with Namenode HA, the Router uses the high availability state  * information in the State Store to forward the request to the Namenode that is  * most likely to be active.  *<p>  * Note that this service can be embedded into the Namenode itself to simplify  * the operation.  */
end_comment

begin_class
DECL|class|NamenodeHeartbeatService
specifier|public
class|class
name|NamenodeHeartbeatService
extends|extends
name|PeriodicService
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
name|NamenodeHeartbeatService
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Configuration for the heartbeat. */
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
comment|/** Router performing the heartbeating. */
DECL|field|resolver
specifier|private
specifier|final
name|ActiveNamenodeResolver
name|resolver
decl_stmt|;
comment|/** Interface to the tracked NN. */
DECL|field|nameserviceId
specifier|private
specifier|final
name|String
name|nameserviceId
decl_stmt|;
DECL|field|namenodeId
specifier|private
specifier|final
name|String
name|namenodeId
decl_stmt|;
comment|/** Namenode HA target. */
DECL|field|localTarget
specifier|private
name|NNHAServiceTarget
name|localTarget
decl_stmt|;
comment|/** RPC address for the namenode. */
DECL|field|rpcAddress
specifier|private
name|String
name|rpcAddress
decl_stmt|;
comment|/** Service RPC address for the namenode. */
DECL|field|serviceAddress
specifier|private
name|String
name|serviceAddress
decl_stmt|;
comment|/** Service RPC address for the namenode. */
DECL|field|lifelineAddress
specifier|private
name|String
name|lifelineAddress
decl_stmt|;
comment|/** HTTP address for the namenode. */
DECL|field|webAddress
specifier|private
name|String
name|webAddress
decl_stmt|;
comment|/**    * Create a new Namenode status updater.    * @param resolver Namenode resolver service to handle NN registration.    * @param nameserviceId Identifier of the nameservice.    * @param namenodeId Identifier of the namenode in HA.    */
DECL|method|NamenodeHeartbeatService ( ActiveNamenodeResolver resolver, String nsId, String nnId)
specifier|public
name|NamenodeHeartbeatService
parameter_list|(
name|ActiveNamenodeResolver
name|resolver
parameter_list|,
name|String
name|nsId
parameter_list|,
name|String
name|nnId
parameter_list|)
block|{
name|super
argument_list|(
name|NamenodeHeartbeatService
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
operator|(
name|nsId
operator|==
literal|null
condition|?
literal|""
else|:
literal|" "
operator|+
name|nsId
operator|)
operator|+
operator|(
name|nnId
operator|==
literal|null
condition|?
literal|""
else|:
literal|" "
operator|+
name|nnId
operator|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|resolver
operator|=
name|resolver
expr_stmt|;
name|this
operator|.
name|nameserviceId
operator|=
name|nsId
expr_stmt|;
name|this
operator|.
name|namenodeId
operator|=
name|nnId
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration configuration)
specifier|protected
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|configuration
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|conf
operator|=
name|configuration
expr_stmt|;
name|String
name|nnDesc
init|=
name|nameserviceId
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|namenodeId
operator|!=
literal|null
operator|&&
operator|!
name|this
operator|.
name|namenodeId
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|this
operator|.
name|localTarget
operator|=
operator|new
name|NNHAServiceTarget
argument_list|(
name|conf
argument_list|,
name|nameserviceId
argument_list|,
name|namenodeId
argument_list|)
expr_stmt|;
name|nnDesc
operator|+=
literal|"-"
operator|+
name|namenodeId
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|localTarget
operator|=
literal|null
expr_stmt|;
block|}
comment|// Get the RPC address for the clients to connect
name|this
operator|.
name|rpcAddress
operator|=
name|getRpcAddress
argument_list|(
name|conf
argument_list|,
name|nameserviceId
argument_list|,
name|namenodeId
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"{} RPC address: {}"
argument_list|,
name|nnDesc
argument_list|,
name|rpcAddress
argument_list|)
expr_stmt|;
comment|// Get the Service RPC address for monitoring
name|this
operator|.
name|serviceAddress
operator|=
name|DFSUtil
operator|.
name|getNamenodeServiceAddr
argument_list|(
name|conf
argument_list|,
name|nameserviceId
argument_list|,
name|namenodeId
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|serviceAddress
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot locate RPC service address for NN {}, "
operator|+
literal|"using RPC address {}"
argument_list|,
name|nnDesc
argument_list|,
name|this
operator|.
name|rpcAddress
argument_list|)
expr_stmt|;
name|this
operator|.
name|serviceAddress
operator|=
name|this
operator|.
name|rpcAddress
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"{} Service RPC address: {}"
argument_list|,
name|nnDesc
argument_list|,
name|serviceAddress
argument_list|)
expr_stmt|;
comment|// Get the Lifeline RPC address for faster monitoring
name|this
operator|.
name|lifelineAddress
operator|=
name|DFSUtil
operator|.
name|getNamenodeLifelineAddr
argument_list|(
name|conf
argument_list|,
name|nameserviceId
argument_list|,
name|namenodeId
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|lifelineAddress
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|lifelineAddress
operator|=
name|this
operator|.
name|serviceAddress
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"{} Lifeline RPC address: {}"
argument_list|,
name|nnDesc
argument_list|,
name|lifelineAddress
argument_list|)
expr_stmt|;
comment|// Get the Web address for UI
name|this
operator|.
name|webAddress
operator|=
name|DFSUtil
operator|.
name|getNamenodeWebAddr
argument_list|(
name|conf
argument_list|,
name|nameserviceId
argument_list|,
name|namenodeId
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"{} Web address: {}"
argument_list|,
name|nnDesc
argument_list|,
name|webAddress
argument_list|)
expr_stmt|;
name|this
operator|.
name|setIntervalMs
argument_list|(
name|conf
operator|.
name|getLong
argument_list|(
name|DFS_ROUTER_HEARTBEAT_INTERVAL_MS
argument_list|,
name|DFS_ROUTER_HEARTBEAT_INTERVAL_MS_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|super
operator|.
name|serviceInit
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|periodicInvoke ()
specifier|public
name|void
name|periodicInvoke
parameter_list|()
block|{
name|updateState
argument_list|()
expr_stmt|;
block|}
comment|/**    * Get the RPC address for a Namenode.    * @param conf Configuration.    * @param nsId Name service identifier.    * @param nnId Name node identifier.    * @return RPC address in format hostname:1234.    */
DECL|method|getRpcAddress ( Configuration conf, String nsId, String nnId)
specifier|private
specifier|static
name|String
name|getRpcAddress
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|nsId
parameter_list|,
name|String
name|nnId
parameter_list|)
block|{
comment|// Get it from the regular RPC setting
name|String
name|confKey
init|=
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_RPC_ADDRESS_KEY
decl_stmt|;
name|String
name|ret
init|=
name|conf
operator|.
name|get
argument_list|(
name|confKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|nsId
operator|!=
literal|null
operator|||
name|nnId
operator|!=
literal|null
condition|)
block|{
comment|// Get if for the proper nameservice and namenode
name|confKey
operator|=
name|DFSUtil
operator|.
name|addKeySuffixes
argument_list|(
name|confKey
argument_list|,
name|nsId
argument_list|,
name|nnId
argument_list|)
expr_stmt|;
name|ret
operator|=
name|conf
operator|.
name|get
argument_list|(
name|confKey
argument_list|)
expr_stmt|;
comment|// If not available, get it from the map
if|if
condition|(
name|ret
operator|==
literal|null
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|InetSocketAddress
argument_list|>
name|rpcAddresses
init|=
name|DFSUtil
operator|.
name|getRpcAddressesForNameserviceId
argument_list|(
name|conf
argument_list|,
name|nsId
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|InetSocketAddress
name|sockAddr
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|nnId
operator|!=
literal|null
condition|)
block|{
name|sockAddr
operator|=
name|rpcAddresses
operator|.
name|get
argument_list|(
name|nnId
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|rpcAddresses
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// Get the only namenode in the namespace
name|sockAddr
operator|=
name|rpcAddresses
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|sockAddr
operator|!=
literal|null
condition|)
block|{
name|InetAddress
name|addr
init|=
name|sockAddr
operator|.
name|getAddress
argument_list|()
decl_stmt|;
name|ret
operator|=
name|addr
operator|.
name|getHostName
argument_list|()
operator|+
literal|":"
operator|+
name|sockAddr
operator|.
name|getPort
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
name|ret
return|;
block|}
comment|/**    * Update the state of the Namenode.    */
DECL|method|updateState ()
specifier|private
name|void
name|updateState
parameter_list|()
block|{
name|NamenodeStatusReport
name|report
init|=
name|getNamenodeStatusReport
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|report
operator|.
name|registrationValid
argument_list|()
condition|)
block|{
comment|// Not operational
name|LOG
operator|.
name|error
argument_list|(
literal|"Namenode is not operational: {}"
argument_list|,
name|getNamenodeDesc
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|report
operator|.
name|haStateValid
argument_list|()
condition|)
block|{
comment|// block and HA status available
name|LOG
operator|.
name|debug
argument_list|(
literal|"Received service state: {} from HA namenode: {}"
argument_list|,
name|report
operator|.
name|getState
argument_list|()
argument_list|,
name|getNamenodeDesc
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|localTarget
operator|==
literal|null
condition|)
block|{
comment|// block info available, HA status not expected
name|LOG
operator|.
name|debug
argument_list|(
literal|"Reporting non-HA namenode as operational: "
operator|+
name|getNamenodeDesc
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// block info available, HA status should be available, but was not
comment|// fetched do nothing and let the current state stand
return|return;
block|}
try|try
block|{
if|if
condition|(
operator|!
name|resolver
operator|.
name|registerNamenode
argument_list|(
name|report
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Cannot register namenode {}"
argument_list|,
name|report
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Cannot register namenode in the State Store"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unhandled exception updating NN registration for {}"
argument_list|,
name|getNamenodeDesc
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Get the status report for the Namenode monitored by this heartbeater.    * @return Namenode status report.    */
DECL|method|getNamenodeStatusReport ()
specifier|protected
name|NamenodeStatusReport
name|getNamenodeStatusReport
parameter_list|()
block|{
name|NamenodeStatusReport
name|report
init|=
operator|new
name|NamenodeStatusReport
argument_list|(
name|nameserviceId
argument_list|,
name|namenodeId
argument_list|,
name|rpcAddress
argument_list|,
name|serviceAddress
argument_list|,
name|lifelineAddress
argument_list|,
name|webAddress
argument_list|)
decl_stmt|;
try|try
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Probing NN at service address: {}"
argument_list|,
name|serviceAddress
argument_list|)
expr_stmt|;
name|URI
name|serviceURI
init|=
operator|new
name|URI
argument_list|(
literal|"hdfs://"
operator|+
name|serviceAddress
argument_list|)
decl_stmt|;
comment|// Read the filesystem info from RPC (required)
name|NamenodeProtocol
name|nn
init|=
name|NameNodeProxies
operator|.
name|createProxy
argument_list|(
name|this
operator|.
name|conf
argument_list|,
name|serviceURI
argument_list|,
name|NamenodeProtocol
operator|.
name|class
argument_list|)
operator|.
name|getProxy
argument_list|()
decl_stmt|;
if|if
condition|(
name|nn
operator|!=
literal|null
condition|)
block|{
name|NamespaceInfo
name|info
init|=
name|nn
operator|.
name|versionRequest
argument_list|()
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|report
operator|.
name|setNamespaceInfo
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|report
operator|.
name|registrationValid
argument_list|()
condition|)
block|{
return|return
name|report
return|;
block|}
comment|// Check for safemode from the client protocol. Currently optional, but
comment|// should be required at some point for QoS
try|try
block|{
name|ClientProtocol
name|client
init|=
name|NameNodeProxies
operator|.
name|createProxy
argument_list|(
name|this
operator|.
name|conf
argument_list|,
name|serviceURI
argument_list|,
name|ClientProtocol
operator|.
name|class
argument_list|)
operator|.
name|getProxy
argument_list|()
decl_stmt|;
if|if
condition|(
name|client
operator|!=
literal|null
condition|)
block|{
name|boolean
name|isSafeMode
init|=
name|client
operator|.
name|setSafeMode
argument_list|(
name|SafeModeAction
operator|.
name|SAFEMODE_GET
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|report
operator|.
name|setSafeMode
argument_list|(
name|isSafeMode
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
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot fetch safemode state for {}"
argument_list|,
name|getNamenodeDesc
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|// Read the stats from JMX (optional)
name|updateJMXParameters
argument_list|(
name|webAddress
argument_list|,
name|report
argument_list|)
expr_stmt|;
if|if
condition|(
name|localTarget
operator|!=
literal|null
condition|)
block|{
comment|// Try to get the HA status
try|try
block|{
comment|// Determine if NN is active
comment|// TODO: dynamic timeout
name|HAServiceProtocol
name|haProtocol
init|=
name|localTarget
operator|.
name|getProxy
argument_list|(
name|conf
argument_list|,
literal|30
operator|*
literal|1000
argument_list|)
decl_stmt|;
name|HAServiceStatus
name|status
init|=
name|haProtocol
operator|.
name|getServiceStatus
argument_list|()
decl_stmt|;
name|report
operator|.
name|setHAServiceState
argument_list|(
name|status
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"HA for namenode is not enabled"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"HA for {} is not enabled"
argument_list|,
name|getNamenodeDesc
argument_list|()
argument_list|)
expr_stmt|;
name|localTarget
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
comment|// Failed to fetch HA status, ignoring failure
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot fetch HA status for {}: {}"
argument_list|,
name|getNamenodeDesc
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
literal|"Cannot communicate with {}: {}"
argument_list|,
name|getNamenodeDesc
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
comment|// Generic error that we don't know about
name|LOG
operator|.
name|error
argument_list|(
literal|"Unexpected exception while communicating with {}: {}"
argument_list|,
name|getNamenodeDesc
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|report
return|;
block|}
comment|/**    * Get the description of the Namenode to monitor.    * @return Description of the Namenode to monitor.    */
DECL|method|getNamenodeDesc ()
specifier|public
name|String
name|getNamenodeDesc
parameter_list|()
block|{
if|if
condition|(
name|namenodeId
operator|!=
literal|null
operator|&&
operator|!
name|namenodeId
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|nameserviceId
operator|+
literal|"-"
operator|+
name|namenodeId
operator|+
literal|":"
operator|+
name|serviceAddress
return|;
block|}
else|else
block|{
return|return
name|nameserviceId
operator|+
literal|":"
operator|+
name|serviceAddress
return|;
block|}
block|}
comment|/**    * Get the parameters for a Namenode from JMX and add them to the report.    * @param webAddress Web interface of the Namenode to monitor.    * @param report Namenode status report to update with JMX data.    */
DECL|method|updateJMXParameters ( String address, NamenodeStatusReport report)
specifier|private
name|void
name|updateJMXParameters
parameter_list|(
name|String
name|address
parameter_list|,
name|NamenodeStatusReport
name|report
parameter_list|)
block|{
try|try
block|{
comment|// TODO part of this should be moved to its own utility
name|String
name|query
init|=
literal|"Hadoop:service=NameNode,name=FSNamesystem*"
decl_stmt|;
name|JSONArray
name|aux
init|=
name|FederationUtil
operator|.
name|getJmx
argument_list|(
name|query
argument_list|,
name|address
argument_list|)
decl_stmt|;
if|if
condition|(
name|aux
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|aux
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|JSONObject
name|jsonObject
init|=
name|aux
operator|.
name|getJSONObject
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|jsonObject
operator|.
name|getString
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"Hadoop:service=NameNode,name=FSNamesystemState"
argument_list|)
condition|)
block|{
name|report
operator|.
name|setDatanodeInfo
argument_list|(
name|jsonObject
operator|.
name|getInt
argument_list|(
literal|"NumLiveDataNodes"
argument_list|)
argument_list|,
name|jsonObject
operator|.
name|getInt
argument_list|(
literal|"NumDeadDataNodes"
argument_list|)
argument_list|,
name|jsonObject
operator|.
name|getInt
argument_list|(
literal|"NumDecommissioningDataNodes"
argument_list|)
argument_list|,
name|jsonObject
operator|.
name|getInt
argument_list|(
literal|"NumDecomLiveDataNodes"
argument_list|)
argument_list|,
name|jsonObject
operator|.
name|getInt
argument_list|(
literal|"NumDecomDeadDataNodes"
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"Hadoop:service=NameNode,name=FSNamesystem"
argument_list|)
condition|)
block|{
name|report
operator|.
name|setNamesystemInfo
argument_list|(
name|jsonObject
operator|.
name|getLong
argument_list|(
literal|"CapacityRemaining"
argument_list|)
argument_list|,
name|jsonObject
operator|.
name|getLong
argument_list|(
literal|"CapacityTotal"
argument_list|)
argument_list|,
name|jsonObject
operator|.
name|getLong
argument_list|(
literal|"FilesTotal"
argument_list|)
argument_list|,
name|jsonObject
operator|.
name|getLong
argument_list|(
literal|"BlocksTotal"
argument_list|)
argument_list|,
name|jsonObject
operator|.
name|getLong
argument_list|(
literal|"MissingBlocks"
argument_list|)
argument_list|,
name|jsonObject
operator|.
name|getLong
argument_list|(
literal|"PendingReplicationBlocks"
argument_list|)
argument_list|,
name|jsonObject
operator|.
name|getLong
argument_list|(
literal|"UnderReplicatedBlocks"
argument_list|)
argument_list|,
name|jsonObject
operator|.
name|getLong
argument_list|(
literal|"PendingDeletionBlocks"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot get stat from {} using JMX"
argument_list|,
name|getNamenodeDesc
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

