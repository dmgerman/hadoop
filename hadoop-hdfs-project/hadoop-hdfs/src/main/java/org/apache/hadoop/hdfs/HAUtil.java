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

begin_class
DECL|class|HAUtil
specifier|public
class|class
name|HAUtil
block|{
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
literal|" must be suffixed with"
operator|+
name|namenodeId
operator|+
literal|" for HA configuration."
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
block|}
end_class

end_unit

