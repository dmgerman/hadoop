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
name|DFS_HA_NAMENODE_ID_KEY
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
name|DFS_NAMENODE_RPC_ADDRESS_KEY
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
name|DFS_NAMENODE_SHARED_EDITS_DIR_KEY
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
name|protocol
operator|.
name|HdfsConstants
operator|.
name|HA_DT_SERVICE_PREFIX
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
name|hdfs
operator|.
name|NameNodeProxies
operator|.
name|ProxyAndInfo
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
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|DelegationTokenIdentifier
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
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|DelegationTokenSelector
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
name|namenode
operator|.
name|NameNode
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
name|namenode
operator|.
name|ha
operator|.
name|AbstractNNFailoverProxyProvider
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
name|ipc
operator|.
name|RemoteException
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
name|StandbyException
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
name|SecurityUtil
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Joiner
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
name|collect
operator|.
name|Lists
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|HAUtil
specifier|public
class|class
name|HAUtil
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
name|HAUtil
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|tokenSelector
specifier|private
specifier|static
specifier|final
name|DelegationTokenSelector
name|tokenSelector
init|=
operator|new
name|DelegationTokenSelector
argument_list|()
decl_stmt|;
DECL|method|HAUtil ()
specifier|private
name|HAUtil
parameter_list|()
block|{
comment|/* Hidden constructor */
block|}
comment|/**    * Returns true if HA for namenode is configured for the given nameservice    *     * @param conf Configuration    * @param nsId nameservice, or null if no federated NS is configured    * @return true if HA is configured in the configuration; else false.    */
DECL|method|isHAEnabled (Configuration conf, String nsId)
specifier|public
specifier|static
name|boolean
name|isHAEnabled
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|nsId
parameter_list|)
block|{
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
name|addresses
init|=
name|DFSUtil
operator|.
name|getHaNnRpcAddresses
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|addresses
operator|==
literal|null
condition|)
return|return
literal|false
return|;
name|Map
argument_list|<
name|String
argument_list|,
name|InetSocketAddress
argument_list|>
name|nnMap
init|=
name|addresses
operator|.
name|get
argument_list|(
name|nsId
argument_list|)
decl_stmt|;
return|return
name|nnMap
operator|!=
literal|null
operator|&&
name|nnMap
operator|.
name|size
argument_list|()
operator|>
literal|1
return|;
block|}
comment|/**    * Returns true if HA is using a shared edits directory.    *    * @param conf Configuration    * @return true if HA config is using a shared edits dir, false otherwise.    */
DECL|method|usesSharedEditsDir (Configuration conf)
specifier|public
specifier|static
name|boolean
name|usesSharedEditsDir
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
literal|null
operator|!=
name|conf
operator|.
name|get
argument_list|(
name|DFS_NAMENODE_SHARED_EDITS_DIR_KEY
argument_list|)
return|;
block|}
comment|/**    * Get the namenode Id by matching the {@code addressKey}    * with the the address of the local node.    *     * If {@link DFSConfigKeys#DFS_HA_NAMENODE_ID_KEY} is not specifically    * configured, this method determines the namenode Id by matching the local    * node's address with the configured addresses. When a match is found, it    * returns the namenode Id from the corresponding configuration key.    *     * @param conf Configuration    * @return namenode Id on success, null on failure.    * @throws HadoopIllegalArgumentException on error    */
DECL|method|getNameNodeId (Configuration conf, String nsId)
specifier|public
specifier|static
name|String
name|getNameNodeId
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|nsId
parameter_list|)
block|{
name|String
name|namenodeId
init|=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|DFS_HA_NAMENODE_ID_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|namenodeId
operator|!=
literal|null
condition|)
block|{
return|return
name|namenodeId
return|;
block|}
name|String
name|suffixes
index|[]
init|=
name|DFSUtil
operator|.
name|getSuffixIDs
argument_list|(
name|conf
argument_list|,
name|DFS_NAMENODE_RPC_ADDRESS_KEY
argument_list|,
name|nsId
argument_list|,
literal|null
argument_list|,
name|DFSUtil
operator|.
name|LOCAL_ADDRESS_MATCHER
argument_list|)
decl_stmt|;
if|if
condition|(
name|suffixes
operator|==
literal|null
condition|)
block|{
name|String
name|msg
init|=
literal|"Configuration "
operator|+
name|DFS_NAMENODE_RPC_ADDRESS_KEY
operator|+
literal|" must be suffixed with nameservice and namenode ID for HA "
operator|+
literal|"configuration."
decl_stmt|;
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
return|return
name|suffixes
index|[
literal|1
index|]
return|;
block|}
comment|/**    * Similar to    * {@link DFSUtil#getNameServiceIdFromAddress(Configuration,     * InetSocketAddress, String...)}    */
DECL|method|getNameNodeIdFromAddress (final Configuration conf, final InetSocketAddress address, String... keys)
specifier|public
specifier|static
name|String
name|getNameNodeIdFromAddress
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|InetSocketAddress
name|address
parameter_list|,
name|String
modifier|...
name|keys
parameter_list|)
block|{
comment|// Configuration with a single namenode and no nameserviceId
name|String
index|[]
name|ids
init|=
name|DFSUtil
operator|.
name|getSuffixIDs
argument_list|(
name|conf
argument_list|,
name|address
argument_list|,
name|keys
argument_list|)
decl_stmt|;
if|if
condition|(
name|ids
operator|!=
literal|null
operator|&&
name|ids
operator|.
name|length
operator|>
literal|1
condition|)
block|{
return|return
name|ids
index|[
literal|1
index|]
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Get the NN ID of the other node in an HA setup.    *     * @param conf the configuration of this node    * @return the NN ID of the other node in this nameservice    */
DECL|method|getNameNodeIdOfOtherNode (Configuration conf, String nsId)
specifier|public
specifier|static
name|String
name|getNameNodeIdOfOtherNode
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|nsId
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|nsId
operator|!=
literal|null
argument_list|,
literal|"Could not determine namespace id. Please ensure that this "
operator|+
literal|"machine is one of the machines listed as a NN RPC address, "
operator|+
literal|"or configure "
operator|+
name|DFSConfigKeys
operator|.
name|DFS_NAMESERVICE_ID
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|nnIds
init|=
name|DFSUtil
operator|.
name|getNameNodeIds
argument_list|(
name|conf
argument_list|,
name|nsId
argument_list|)
decl_stmt|;
name|String
name|myNNId
init|=
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_NAMENODE_ID_KEY
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|nnIds
operator|!=
literal|null
argument_list|,
literal|"Could not determine namenode ids in namespace '%s'. "
operator|+
literal|"Please configure "
operator|+
name|DFSUtil
operator|.
name|addKeySuffixes
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_NAMENODES_KEY_PREFIX
argument_list|,
name|nsId
argument_list|)
argument_list|,
name|nsId
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|nnIds
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|,
literal|"Expected exactly 2 NameNodes in namespace '%s'. "
operator|+
literal|"Instead, got only %s (NN ids were '%s'"
argument_list|,
name|nsId
argument_list|,
name|nnIds
operator|.
name|size
argument_list|()
argument_list|,
name|Joiner
operator|.
name|on
argument_list|(
literal|"','"
argument_list|)
operator|.
name|join
argument_list|(
name|nnIds
argument_list|)
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|myNNId
operator|!=
literal|null
operator|&&
operator|!
name|myNNId
operator|.
name|isEmpty
argument_list|()
argument_list|,
literal|"Could not determine own NN ID in namespace '%s'. Please "
operator|+
literal|"ensure that this node is one of the machines listed as an "
operator|+
literal|"NN RPC address, or configure "
operator|+
name|DFSConfigKeys
operator|.
name|DFS_HA_NAMENODE_ID_KEY
argument_list|,
name|nsId
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|nnSet
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|nnIds
argument_list|)
decl_stmt|;
name|nnSet
operator|.
name|remove
argument_list|(
name|myNNId
argument_list|)
expr_stmt|;
assert|assert
name|nnSet
operator|.
name|size
argument_list|()
operator|==
literal|1
assert|;
return|return
name|nnSet
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
comment|/**    * Given the configuration for this node, return a Configuration object for    * the other node in an HA setup.    *     * @param myConf the configuration of this node    * @return the configuration of the other node in an HA setup    */
DECL|method|getConfForOtherNode ( Configuration myConf)
specifier|public
specifier|static
name|Configuration
name|getConfForOtherNode
parameter_list|(
name|Configuration
name|myConf
parameter_list|)
block|{
name|String
name|nsId
init|=
name|DFSUtil
operator|.
name|getNamenodeNameServiceId
argument_list|(
name|myConf
argument_list|)
decl_stmt|;
name|String
name|otherNn
init|=
name|getNameNodeIdOfOtherNode
argument_list|(
name|myConf
argument_list|,
name|nsId
argument_list|)
decl_stmt|;
comment|// Look up the address of the active NN.
name|Configuration
name|confForOtherNode
init|=
operator|new
name|Configuration
argument_list|(
name|myConf
argument_list|)
decl_stmt|;
name|NameNode
operator|.
name|initializeGenericKeys
argument_list|(
name|confForOtherNode
argument_list|,
name|nsId
argument_list|,
name|otherNn
argument_list|)
expr_stmt|;
return|return
name|confForOtherNode
return|;
block|}
comment|/**    * This is used only by tests at the moment.    * @return true if the NN should allow read operations while in standby mode.    */
DECL|method|shouldAllowStandbyReads (Configuration conf)
specifier|public
specifier|static
name|boolean
name|shouldAllowStandbyReads
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|conf
operator|.
name|getBoolean
argument_list|(
literal|"dfs.ha.allow.stale.reads"
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|setAllowStandbyReads (Configuration conf, boolean val)
specifier|public
specifier|static
name|void
name|setAllowStandbyReads
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|boolean
name|val
parameter_list|)
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
literal|"dfs.ha.allow.stale.reads"
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
comment|/**    * @return true if the given nameNodeUri appears to be a logical URI.    */
DECL|method|isLogicalUri ( Configuration conf, URI nameNodeUri)
specifier|public
specifier|static
name|boolean
name|isLogicalUri
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|URI
name|nameNodeUri
parameter_list|)
block|{
name|String
name|host
init|=
name|nameNodeUri
operator|.
name|getHost
argument_list|()
decl_stmt|;
comment|// A logical name must be one of the service IDs.
return|return
name|DFSUtil
operator|.
name|getNameServiceIds
argument_list|(
name|conf
argument_list|)
operator|.
name|contains
argument_list|(
name|host
argument_list|)
return|;
block|}
comment|/**    * Check whether the client has a failover proxy provider configured    * for the namenode/nameservice.    *    * @param conf Configuration    * @param nameNodeUri The URI of namenode    * @return true if failover is configured.    */
DECL|method|isClientFailoverConfigured ( Configuration conf, URI nameNodeUri)
specifier|public
specifier|static
name|boolean
name|isClientFailoverConfigured
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|URI
name|nameNodeUri
parameter_list|)
block|{
name|String
name|host
init|=
name|nameNodeUri
operator|.
name|getHost
argument_list|()
decl_stmt|;
name|String
name|configKey
init|=
name|HdfsClientConfigKeys
operator|.
name|Failover
operator|.
name|PROXY_PROVIDER_KEY_PREFIX
operator|+
literal|"."
operator|+
name|host
decl_stmt|;
return|return
name|conf
operator|.
name|get
argument_list|(
name|configKey
argument_list|)
operator|!=
literal|null
return|;
block|}
comment|/**    * Check whether logical URI is needed for the namenode and    * the corresponding failover proxy provider in the config.    *    * @param conf Configuration    * @param nameNodeUri The URI of namenode    * @return true if logical URI is needed. false, if not needed.    * @throws IOException most likely due to misconfiguration.    */
DECL|method|useLogicalUri (Configuration conf, URI nameNodeUri)
specifier|public
specifier|static
name|boolean
name|useLogicalUri
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|URI
name|nameNodeUri
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Create the proxy provider. Actual proxy is not created.
name|AbstractNNFailoverProxyProvider
argument_list|<
name|ClientProtocol
argument_list|>
name|provider
init|=
name|NameNodeProxies
operator|.
name|createFailoverProxyProvider
argument_list|(
name|conf
argument_list|,
name|nameNodeUri
argument_list|,
name|ClientProtocol
operator|.
name|class
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// No need to use logical URI since failover is not configured.
if|if
condition|(
name|provider
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// Check whether the failover proxy provider uses logical URI.
return|return
name|provider
operator|.
name|useLogicalURI
argument_list|()
return|;
block|}
comment|/**    * Parse the file system URI out of the provided token.    */
DECL|method|getServiceUriFromToken (final String scheme, Token<?> token)
specifier|public
specifier|static
name|URI
name|getServiceUriFromToken
parameter_list|(
specifier|final
name|String
name|scheme
parameter_list|,
name|Token
argument_list|<
name|?
argument_list|>
name|token
parameter_list|)
block|{
name|String
name|tokStr
init|=
name|token
operator|.
name|getService
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
name|String
name|prefix
init|=
name|buildTokenServicePrefixForLogicalUri
argument_list|(
name|scheme
argument_list|)
decl_stmt|;
if|if
condition|(
name|tokStr
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
name|tokStr
operator|=
name|tokStr
operator|.
name|replaceFirst
argument_list|(
name|prefix
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
return|return
name|URI
operator|.
name|create
argument_list|(
name|scheme
operator|+
literal|"://"
operator|+
name|tokStr
argument_list|)
return|;
block|}
comment|/**    * Get the service name used in the delegation token for the given logical    * HA service.    * @param uri the logical URI of the cluster    * @param scheme the scheme of the corresponding FileSystem    * @return the service name    */
DECL|method|buildTokenServiceForLogicalUri (final URI uri, final String scheme)
specifier|public
specifier|static
name|Text
name|buildTokenServiceForLogicalUri
parameter_list|(
specifier|final
name|URI
name|uri
parameter_list|,
specifier|final
name|String
name|scheme
parameter_list|)
block|{
return|return
operator|new
name|Text
argument_list|(
name|buildTokenServicePrefixForLogicalUri
argument_list|(
name|scheme
argument_list|)
operator|+
name|uri
operator|.
name|getHost
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * @return true if this token corresponds to a logical nameservice    * rather than a specific namenode.    */
DECL|method|isTokenForLogicalUri (Token<?> token)
specifier|public
specifier|static
name|boolean
name|isTokenForLogicalUri
parameter_list|(
name|Token
argument_list|<
name|?
argument_list|>
name|token
parameter_list|)
block|{
return|return
name|token
operator|.
name|getService
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
name|HA_DT_SERVICE_PREFIX
argument_list|)
return|;
block|}
DECL|method|buildTokenServicePrefixForLogicalUri (String scheme)
specifier|public
specifier|static
name|String
name|buildTokenServicePrefixForLogicalUri
parameter_list|(
name|String
name|scheme
parameter_list|)
block|{
return|return
name|HA_DT_SERVICE_PREFIX
operator|+
name|scheme
operator|+
literal|":"
return|;
block|}
comment|/**    * Locate a delegation token associated with the given HA cluster URI, and if    * one is found, clone it to also represent the underlying namenode address.    * @param ugi the UGI to modify    * @param haUri the logical URI for the cluster    * @param nnAddrs collection of NNs in the cluster to which the token    * applies    */
DECL|method|cloneDelegationTokenForLogicalUri ( UserGroupInformation ugi, URI haUri, Collection<InetSocketAddress> nnAddrs)
specifier|public
specifier|static
name|void
name|cloneDelegationTokenForLogicalUri
parameter_list|(
name|UserGroupInformation
name|ugi
parameter_list|,
name|URI
name|haUri
parameter_list|,
name|Collection
argument_list|<
name|InetSocketAddress
argument_list|>
name|nnAddrs
parameter_list|)
block|{
comment|// this cloning logic is only used by hdfs
name|Text
name|haService
init|=
name|HAUtil
operator|.
name|buildTokenServiceForLogicalUri
argument_list|(
name|haUri
argument_list|,
name|HdfsConstants
operator|.
name|HDFS_URI_SCHEME
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|haToken
init|=
name|tokenSelector
operator|.
name|selectToken
argument_list|(
name|haService
argument_list|,
name|ugi
operator|.
name|getTokens
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|haToken
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|InetSocketAddress
name|singleNNAddr
range|:
name|nnAddrs
control|)
block|{
comment|// this is a minor hack to prevent physical HA tokens from being
comment|// exposed to the user via UGI.getCredentials(), otherwise these
comment|// cloned tokens may be inadvertently propagated to jobs
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|specificToken
init|=
operator|new
name|Token
operator|.
name|PrivateToken
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
argument_list|(
name|haToken
argument_list|)
decl_stmt|;
name|SecurityUtil
operator|.
name|setTokenService
argument_list|(
name|specificToken
argument_list|,
name|singleNNAddr
argument_list|)
expr_stmt|;
name|Text
name|alias
init|=
operator|new
name|Text
argument_list|(
name|buildTokenServicePrefixForLogicalUri
argument_list|(
name|HdfsConstants
operator|.
name|HDFS_URI_SCHEME
argument_list|)
operator|+
literal|"//"
operator|+
name|specificToken
operator|.
name|getService
argument_list|()
argument_list|)
decl_stmt|;
name|ugi
operator|.
name|addToken
argument_list|(
name|alias
argument_list|,
name|specificToken
argument_list|)
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
literal|"Mapped HA service delegation token for logical URI "
operator|+
name|haUri
operator|+
literal|" to namenode "
operator|+
name|singleNNAddr
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
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
literal|"No HA service delegation token found for logical URI "
operator|+
name|haUri
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Get the internet address of the currently-active NN. This should rarely be    * used, since callers of this method who connect directly to the NN using the    * resulting InetSocketAddress will not be able to connect to the active NN if    * a failover were to occur after this method has been called.    *     * @param fs the file system to get the active address of.    * @return the internet address of the currently-active NN.    * @throws IOException if an error occurs while resolving the active NN.    */
DECL|method|getAddressOfActive (FileSystem fs)
specifier|public
specifier|static
name|InetSocketAddress
name|getAddressOfActive
parameter_list|(
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
operator|(
name|fs
operator|instanceof
name|DistributedFileSystem
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"FileSystem "
operator|+
name|fs
operator|+
literal|" is not a DFS."
argument_list|)
throw|;
block|}
comment|// force client address resolution.
name|fs
operator|.
name|exists
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
name|DistributedFileSystem
name|dfs
init|=
operator|(
name|DistributedFileSystem
operator|)
name|fs
decl_stmt|;
name|DFSClient
name|dfsClient
init|=
name|dfs
operator|.
name|getClient
argument_list|()
decl_stmt|;
return|return
name|RPC
operator|.
name|getServerAddress
argument_list|(
name|dfsClient
operator|.
name|getNamenode
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Get an RPC proxy for each NN in an HA nameservice. Used when a given RPC    * call should be made on every NN in an HA nameservice, not just the active.    *     * @param conf configuration    * @param nsId the nameservice to get all of the proxies for.    * @return a list of RPC proxies for each NN in the nameservice.    * @throws IOException in the event of error.    */
DECL|method|getProxiesForAllNameNodesInNameservice ( Configuration conf, String nsId)
specifier|public
specifier|static
name|List
argument_list|<
name|ClientProtocol
argument_list|>
name|getProxiesForAllNameNodesInNameservice
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|nsId
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|ProxyAndInfo
argument_list|<
name|ClientProtocol
argument_list|>
argument_list|>
name|proxies
init|=
name|getProxiesForAllNameNodesInNameservice
argument_list|(
name|conf
argument_list|,
name|nsId
argument_list|,
name|ClientProtocol
operator|.
name|class
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ClientProtocol
argument_list|>
name|namenodes
init|=
operator|new
name|ArrayList
argument_list|<
name|ClientProtocol
argument_list|>
argument_list|(
name|proxies
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|ProxyAndInfo
argument_list|<
name|ClientProtocol
argument_list|>
name|proxy
range|:
name|proxies
control|)
block|{
name|namenodes
operator|.
name|add
argument_list|(
name|proxy
operator|.
name|getProxy
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|namenodes
return|;
block|}
comment|/**    * Get an RPC proxy for each NN in an HA nameservice. Used when a given RPC    * call should be made on every NN in an HA nameservice, not just the active.    *    * @param conf configuration    * @param nsId the nameservice to get all of the proxies for.    * @param xface the protocol class.    * @return a list of RPC proxies for each NN in the nameservice.    * @throws IOException in the event of error.    */
DECL|method|getProxiesForAllNameNodesInNameservice ( Configuration conf, String nsId, Class<T> xface)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|List
argument_list|<
name|ProxyAndInfo
argument_list|<
name|T
argument_list|>
argument_list|>
name|getProxiesForAllNameNodesInNameservice
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|nsId
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|xface
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|InetSocketAddress
argument_list|>
name|nnAddresses
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
name|List
argument_list|<
name|ProxyAndInfo
argument_list|<
name|T
argument_list|>
argument_list|>
name|proxies
init|=
operator|new
name|ArrayList
argument_list|<
name|ProxyAndInfo
argument_list|<
name|T
argument_list|>
argument_list|>
argument_list|(
name|nnAddresses
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|InetSocketAddress
name|nnAddress
range|:
name|nnAddresses
operator|.
name|values
argument_list|()
control|)
block|{
name|NameNodeProxies
operator|.
name|ProxyAndInfo
argument_list|<
name|T
argument_list|>
name|proxyInfo
init|=
literal|null
decl_stmt|;
name|proxyInfo
operator|=
name|NameNodeProxies
operator|.
name|createNonHAProxy
argument_list|(
name|conf
argument_list|,
name|nnAddress
argument_list|,
name|xface
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|proxies
operator|.
name|add
argument_list|(
name|proxyInfo
argument_list|)
expr_stmt|;
block|}
return|return
name|proxies
return|;
block|}
comment|/**    * Used to ensure that at least one of the given HA NNs is currently in the    * active state..    *     * @param namenodes list of RPC proxies for each NN to check.    * @return true if at least one NN is active, false if all are in the standby state.    * @throws IOException in the event of error.    */
DECL|method|isAtLeastOneActive (List<ClientProtocol> namenodes)
specifier|public
specifier|static
name|boolean
name|isAtLeastOneActive
parameter_list|(
name|List
argument_list|<
name|ClientProtocol
argument_list|>
name|namenodes
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|ClientProtocol
name|namenode
range|:
name|namenodes
control|)
block|{
try|try
block|{
name|namenode
operator|.
name|getFileInfo
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|RemoteException
name|re
parameter_list|)
block|{
name|IOException
name|cause
init|=
name|re
operator|.
name|unwrapRemoteException
argument_list|()
decl_stmt|;
if|if
condition|(
name|cause
operator|instanceof
name|StandbyException
condition|)
block|{
comment|// This is expected to happen for a standby NN.
block|}
else|else
block|{
throw|throw
name|re
throw|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

