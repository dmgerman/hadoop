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
name|*
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
name|net
operator|.
name|URISyntaxException
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
name|DFS_FEDERATION_NAMESERVICE_ID
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
name|myConf
argument_list|,
name|nsId
argument_list|)
decl_stmt|;
name|String
name|myNNId
init|=
name|myConf
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
name|String
name|activeNN
init|=
name|nnSet
operator|.
name|get
argument_list|(
literal|0
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
name|activeNN
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
comment|/**    * @return true if the given nameNodeUri appears to be a logical URI.    * This is the case if there is a failover proxy provider configured    * for it in the given configuration.    */
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
name|String
name|configKey
init|=
name|DFS_CLIENT_FAILOVER_PROXY_PROVIDER_KEY_PREFIX
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
comment|/**    * Parse the HDFS URI out of the provided token.    * @throws IOException if the token is invalid    */
DECL|method|getServiceUriFromToken ( Token<DelegationTokenIdentifier> token)
specifier|public
specifier|static
name|URI
name|getServiceUriFromToken
parameter_list|(
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token
parameter_list|)
throws|throws
name|IOException
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
if|if
condition|(
name|tokStr
operator|.
name|startsWith
argument_list|(
name|HA_DT_SERVICE_PREFIX
argument_list|)
condition|)
block|{
name|tokStr
operator|=
name|tokStr
operator|.
name|replaceFirst
argument_list|(
name|HA_DT_SERVICE_PREFIX
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
try|try
block|{
return|return
operator|new
name|URI
argument_list|(
name|HdfsConstants
operator|.
name|HDFS_URI_SCHEME
operator|+
literal|"://"
operator|+
name|tokStr
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid token contents: '"
operator|+
name|tokStr
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Get the service name used in the delegation token for the given logical    * HA service.    * @param uri the logical URI of the cluster    * @return the service name    */
DECL|method|buildTokenServiceForLogicalUri (URI uri)
specifier|public
specifier|static
name|Text
name|buildTokenServiceForLogicalUri
parameter_list|(
name|URI
name|uri
parameter_list|)
block|{
return|return
operator|new
name|Text
argument_list|(
name|HA_DT_SERVICE_PREFIX
operator|+
name|uri
operator|.
name|getHost
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * @return true if this token corresponds to a logical nameservice    * rather than a specific namenode.    */
DECL|method|isTokenForLogicalUri ( Token<DelegationTokenIdentifier> token)
specifier|public
specifier|static
name|boolean
name|isTokenForLogicalUri
parameter_list|(
name|Token
argument_list|<
name|DelegationTokenIdentifier
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
comment|/**    * Locate a delegation token associated with the given HA cluster URI, and if    * one is found, clone it to also represent the underlying namenode address.    * @param ugi the UGI to modify    * @param haUri the logical URI for the cluster    * @param singleNNAddr one of the NNs in the cluster to which the token    * applies    */
DECL|method|cloneDelegationTokenForLogicalUri ( UserGroupInformation ugi, URI haUri, InetSocketAddress singleNNAddr)
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
name|InetSocketAddress
name|singleNNAddr
parameter_list|)
block|{
name|Text
name|haService
init|=
name|buildTokenServiceForLogicalUri
argument_list|(
name|haUri
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|haToken
init|=
name|DelegationTokenSelector
operator|.
name|selectHdfsDelegationToken
argument_list|(
name|haService
argument_list|,
name|ugi
argument_list|)
decl_stmt|;
if|if
condition|(
name|haToken
operator|==
literal|null
condition|)
block|{
comment|// no token
return|return;
block|}
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|specificToken
init|=
operator|new
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
argument_list|(
name|haToken
argument_list|)
decl_stmt|;
name|specificToken
operator|.
name|setService
argument_list|(
name|SecurityUtil
operator|.
name|buildTokenService
argument_list|(
name|singleNNAddr
argument_list|)
argument_list|)
expr_stmt|;
name|ugi
operator|.
name|addToken
argument_list|(
name|specificToken
argument_list|)
expr_stmt|;
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
end_class

end_unit

