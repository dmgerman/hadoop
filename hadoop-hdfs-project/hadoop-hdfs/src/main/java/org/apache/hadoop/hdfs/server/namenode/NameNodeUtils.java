begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|hdfs
operator|.
name|DFSUtilClient
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
name|Collection
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
operator|.
name|FS_DEFAULT_NAME_KEY
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
name|DFS_HA_NAMENODES_KEY_PREFIX
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

begin_comment
comment|/**  * Utility functions for the NameNode.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|NameNodeUtils
specifier|public
specifier|final
class|class
name|NameNodeUtils
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|NameNodeUtils
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Return the namenode address that will be used by clients to access this    * namenode or name service. This needs to be called before the config    * is overriden.    *    * This method behaves as follows:    *    * 1. fs.defaultFS is undefined:    *    - return null.    * 2. fs.defaultFS is defined but has no hostname (logical or physical):    *    - return null.    * 3. Single NN (no HA, no federation):    *    - return URI authority from fs.defaultFS    * 4. Current NN is in an HA nameservice (with or without federation):    *    - return nameservice for current NN.    * 5. Current NN is in non-HA namespace, federated cluster:    *    - return value of dfs.namenode.rpc-address.[nsId].[nnId]    *    - If the above key is not defined, then return authority from    *      fs.defaultFS if the port number is> 0.    * 6. If port number in the authority is missing or zero in step 6:    *    - return null    */
annotation|@
name|VisibleForTesting
annotation|@
name|Nullable
DECL|method|getClientNamenodeAddress ( Configuration conf, @Nullable String nsId)
specifier|static
name|String
name|getClientNamenodeAddress
parameter_list|(
name|Configuration
name|conf
parameter_list|,
annotation|@
name|Nullable
name|String
name|nsId
parameter_list|)
block|{
specifier|final
name|Collection
argument_list|<
name|String
argument_list|>
name|nameservices
init|=
name|DFSUtilClient
operator|.
name|getNameServiceIds
argument_list|(
name|conf
argument_list|)
decl_stmt|;
specifier|final
name|String
name|nnAddr
init|=
name|conf
operator|.
name|get
argument_list|(
name|FS_DEFAULT_NAME_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|nnAddr
operator|==
literal|null
condition|)
block|{
comment|// default fs is not set.
return|return
literal|null
return|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"{} is {}"
argument_list|,
name|FS_DEFAULT_NAME_KEY
argument_list|,
name|nnAddr
argument_list|)
expr_stmt|;
specifier|final
name|URI
name|nnUri
init|=
name|URI
operator|.
name|create
argument_list|(
name|nnAddr
argument_list|)
decl_stmt|;
name|String
name|defaultNnHost
init|=
name|nnUri
operator|.
name|getHost
argument_list|()
decl_stmt|;
if|if
condition|(
name|defaultNnHost
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// Current Nameservice is HA.
if|if
condition|(
name|nsId
operator|!=
literal|null
operator|&&
name|nameservices
operator|.
name|contains
argument_list|(
name|nsId
argument_list|)
condition|)
block|{
specifier|final
name|Collection
argument_list|<
name|String
argument_list|>
name|namenodes
init|=
name|conf
operator|.
name|getTrimmedStringCollection
argument_list|(
name|DFS_HA_NAMENODES_KEY_PREFIX
operator|+
literal|"."
operator|+
name|nsId
argument_list|)
decl_stmt|;
if|if
condition|(
name|namenodes
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
return|return
name|nsId
return|;
block|}
block|}
comment|// Federation without HA. We must handle the case when the current NN
comment|// is not in the default nameservice.
name|String
name|currentNnAddress
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|nsId
operator|!=
literal|null
condition|)
block|{
name|String
name|hostNameKey
init|=
name|DFS_NAMENODE_RPC_ADDRESS_KEY
operator|+
literal|"."
operator|+
name|nsId
decl_stmt|;
name|currentNnAddress
operator|=
name|conf
operator|.
name|get
argument_list|(
name|hostNameKey
argument_list|)
expr_stmt|;
block|}
comment|// Fallback to the address in fs.defaultFS.
if|if
condition|(
name|currentNnAddress
operator|==
literal|null
condition|)
block|{
name|currentNnAddress
operator|=
name|nnUri
operator|.
name|getAuthority
argument_list|()
expr_stmt|;
block|}
name|int
name|port
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|currentNnAddress
operator|.
name|contains
argument_list|(
literal|":"
argument_list|)
condition|)
block|{
name|port
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|currentNnAddress
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|port
operator|>
literal|0
condition|)
block|{
return|return
name|currentNnAddress
return|;
block|}
else|else
block|{
comment|// the port is missing or 0. Figure out real bind address later.
return|return
literal|null
return|;
block|}
block|}
DECL|method|NameNodeUtils ()
specifier|private
name|NameNodeUtils
parameter_list|()
block|{
comment|// Disallow construction
block|}
block|}
end_class

end_unit

