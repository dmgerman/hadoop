begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|protocol
operator|.
name|ErasureCodingPolicyState
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
name|SystemErasureCodingPolicies
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
name|ErasureCodingPolicy
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
name|io
operator|.
name|erasurecode
operator|.
name|CodecUtil
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
name|erasurecode
operator|.
name|ErasureCodeConstants
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
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_comment
comment|/**  * This manages erasure coding policies predefined and activated in the system.  * It loads customized policies and syncs with persisted ones in  * NameNode image.  *  * This class is instantiated by the FSNamesystem.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|}
argument_list|)
DECL|class|ErasureCodingPolicyManager
specifier|public
specifier|final
class|class
name|ErasureCodingPolicyManager
block|{
DECL|field|LOG
specifier|public
specifier|static
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ErasureCodingPolicyManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|maxCellSize
specifier|private
name|int
name|maxCellSize
init|=
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_EC_POLICIES_MAX_CELLSIZE_DEFAULT
decl_stmt|;
comment|// Supported storage policies for striped EC files
DECL|field|SUITABLE_STORAGE_POLICIES_FOR_EC_STRIPED_MODE
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|SUITABLE_STORAGE_POLICIES_FOR_EC_STRIPED_MODE
init|=
operator|new
name|byte
index|[]
block|{
name|HdfsConstants
operator|.
name|HOT_STORAGE_POLICY_ID
block|,
name|HdfsConstants
operator|.
name|COLD_STORAGE_POLICY_ID
block|,
name|HdfsConstants
operator|.
name|ALLSSD_STORAGE_POLICY_ID
block|}
decl_stmt|;
comment|/**    * All policies sorted by name for fast querying, include built-in policy,    * user defined policy, removed policy.    */
DECL|field|policiesByName
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|ErasureCodingPolicy
argument_list|>
name|policiesByName
decl_stmt|;
comment|/**    * All policies sorted by ID for fast querying, including built-in policy,    * user defined policy, removed policy.    */
DECL|field|policiesByID
specifier|private
name|Map
argument_list|<
name|Byte
argument_list|,
name|ErasureCodingPolicy
argument_list|>
name|policiesByID
decl_stmt|;
comment|/**    * For better performance when query all Policies.    */
DECL|field|allPolicies
specifier|private
name|ErasureCodingPolicy
index|[]
name|allPolicies
decl_stmt|;
comment|/**    * All enabled policies sorted by name for fast querying, including built-in    * policy, user defined policy.    */
DECL|field|enabledPoliciesByName
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|ErasureCodingPolicy
argument_list|>
name|enabledPoliciesByName
decl_stmt|;
comment|/**    * For better performance when query all enabled Policies.    */
DECL|field|enabledPolicies
specifier|private
name|ErasureCodingPolicy
index|[]
name|enabledPolicies
decl_stmt|;
DECL|field|instance
specifier|private
specifier|volatile
specifier|static
name|ErasureCodingPolicyManager
name|instance
init|=
literal|null
decl_stmt|;
DECL|method|getInstance ()
specifier|public
specifier|static
name|ErasureCodingPolicyManager
name|getInstance
parameter_list|()
block|{
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
block|{
name|instance
operator|=
operator|new
name|ErasureCodingPolicyManager
argument_list|()
expr_stmt|;
block|}
return|return
name|instance
return|;
block|}
DECL|method|ErasureCodingPolicyManager ()
specifier|private
name|ErasureCodingPolicyManager
parameter_list|()
block|{}
DECL|method|init (Configuration conf)
specifier|public
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
comment|// Load erasure coding default policy
specifier|final
name|String
name|defaultPolicyName
init|=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_EC_SYSTEM_DEFAULT_POLICY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_EC_SYSTEM_DEFAULT_POLICY_DEFAULT
argument_list|)
decl_stmt|;
name|this
operator|.
name|policiesByName
operator|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|policiesByID
operator|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|enabledPoliciesByName
operator|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
expr_stmt|;
comment|/**      * TODO: load user defined EC policy from fsImage HDFS-7859      * load persistent policies from image and editlog, which is done only once      * during NameNode startup. This can be done here or in a separate method.      */
comment|/*      * Add all System built-in policies into policy map      */
for|for
control|(
name|ErasureCodingPolicy
name|policy
range|:
name|SystemErasureCodingPolicies
operator|.
name|getPolicies
argument_list|()
control|)
block|{
name|policiesByName
operator|.
name|put
argument_list|(
name|policy
operator|.
name|getName
argument_list|()
argument_list|,
name|policy
argument_list|)
expr_stmt|;
name|policiesByID
operator|.
name|put
argument_list|(
name|policy
operator|.
name|getId
argument_list|()
argument_list|,
name|policy
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|defaultPolicyName
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|ErasureCodingPolicy
name|ecPolicy
init|=
name|policiesByName
operator|.
name|get
argument_list|(
name|defaultPolicyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|ecPolicy
operator|==
literal|null
condition|)
block|{
name|String
name|names
init|=
name|policiesByName
operator|.
name|values
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|ErasureCodingPolicy
operator|::
name|getName
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|joining
argument_list|(
literal|", "
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|msg
init|=
name|String
operator|.
name|format
argument_list|(
literal|"EC policy '%s' specified at %s is not a "
operator|+
literal|"valid policy. Please choose from list of available "
operator|+
literal|"policies: [%s]"
argument_list|,
name|defaultPolicyName
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_EC_SYSTEM_DEFAULT_POLICY
argument_list|,
name|names
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
name|enabledPoliciesByName
operator|.
name|put
argument_list|(
name|ecPolicy
operator|.
name|getName
argument_list|()
argument_list|,
name|ecPolicy
argument_list|)
expr_stmt|;
block|}
name|enabledPolicies
operator|=
name|enabledPoliciesByName
operator|.
name|values
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|ErasureCodingPolicy
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|allPolicies
operator|=
name|policiesByName
operator|.
name|values
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|ErasureCodingPolicy
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|maxCellSize
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_EC_POLICIES_MAX_CELLSIZE_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_EC_POLICIES_MAX_CELLSIZE_DEFAULT
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the set of enabled policies.    * @return all policies    */
DECL|method|getEnabledPolicies ()
specifier|public
name|ErasureCodingPolicy
index|[]
name|getEnabledPolicies
parameter_list|()
block|{
return|return
name|enabledPolicies
return|;
block|}
comment|/**    * Get enabled policy by policy name.    */
DECL|method|getEnabledPolicyByName (String name)
specifier|public
name|ErasureCodingPolicy
name|getEnabledPolicyByName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|ErasureCodingPolicy
name|ecPolicy
init|=
name|enabledPoliciesByName
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|ecPolicy
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|name
operator|.
name|equalsIgnoreCase
argument_list|(
name|ErasureCodeConstants
operator|.
name|REPLICATION_POLICY_NAME
argument_list|)
condition|)
block|{
name|ecPolicy
operator|=
name|SystemErasureCodingPolicies
operator|.
name|getReplicationPolicy
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|ecPolicy
return|;
block|}
comment|/**    * @return if the specified storage policy ID is suitable for striped EC    * files.    */
DECL|method|checkStoragePolicySuitableForECStripedMode ( byte storagePolicyID)
specifier|public
specifier|static
name|boolean
name|checkStoragePolicySuitableForECStripedMode
parameter_list|(
name|byte
name|storagePolicyID
parameter_list|)
block|{
name|boolean
name|isPolicySuitable
init|=
literal|false
decl_stmt|;
for|for
control|(
name|byte
name|suitablePolicy
range|:
name|SUITABLE_STORAGE_POLICIES_FOR_EC_STRIPED_MODE
control|)
block|{
if|if
condition|(
name|storagePolicyID
operator|==
name|suitablePolicy
condition|)
block|{
name|isPolicySuitable
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
return|return
name|isPolicySuitable
return|;
block|}
comment|/**    * Get all system defined policies and user defined policies.    * @return all policies    */
DECL|method|getPolicies ()
specifier|public
name|ErasureCodingPolicy
index|[]
name|getPolicies
parameter_list|()
block|{
return|return
name|allPolicies
return|;
block|}
comment|/**    * Get a policy by policy ID, including system policy and user defined policy.    * @return ecPolicy, or null if not found    */
DECL|method|getByID (byte id)
specifier|public
name|ErasureCodingPolicy
name|getByID
parameter_list|(
name|byte
name|id
parameter_list|)
block|{
return|return
name|this
operator|.
name|policiesByID
operator|.
name|get
argument_list|(
name|id
argument_list|)
return|;
block|}
comment|/**    * Get a policy by policy ID, including system policy and user defined policy.    * @return ecPolicy, or null if not found    */
DECL|method|getByName (String name)
specifier|public
name|ErasureCodingPolicy
name|getByName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|this
operator|.
name|policiesByName
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**    * Clear and clean up.    */
DECL|method|clear ()
specifier|public
name|void
name|clear
parameter_list|()
block|{
comment|// TODO: we should only clear policies loaded from NN metadata.
comment|// This is a placeholder for HDFS-7337.
block|}
comment|/**    * Add an erasure coding policy.    * @return the added policy    */
DECL|method|addPolicy ( ErasureCodingPolicy policy)
specifier|public
specifier|synchronized
name|ErasureCodingPolicy
name|addPolicy
parameter_list|(
name|ErasureCodingPolicy
name|policy
parameter_list|)
block|{
comment|// Set policy state into DISABLED when adding into Hadoop.
name|policy
operator|.
name|setState
argument_list|(
name|ErasureCodingPolicyState
operator|.
name|DISABLED
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|CodecUtil
operator|.
name|hasCodec
argument_list|(
name|policy
operator|.
name|getCodecName
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Codec name "
operator|+
name|policy
operator|.
name|getCodecName
argument_list|()
operator|+
literal|" is not supported"
argument_list|)
throw|;
block|}
if|if
condition|(
name|policy
operator|.
name|getCellSize
argument_list|()
operator|>
name|maxCellSize
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Cell size "
operator|+
name|policy
operator|.
name|getCellSize
argument_list|()
operator|+
literal|" should not exceed maximum "
operator|+
name|maxCellSize
operator|+
literal|" bytes"
argument_list|)
throw|;
block|}
name|String
name|assignedNewName
init|=
name|ErasureCodingPolicy
operator|.
name|composePolicyName
argument_list|(
name|policy
operator|.
name|getSchema
argument_list|()
argument_list|,
name|policy
operator|.
name|getCellSize
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|ErasureCodingPolicy
name|p
range|:
name|getPolicies
argument_list|()
control|)
block|{
if|if
condition|(
name|p
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|assignedNewName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"The policy name "
operator|+
name|assignedNewName
operator|+
literal|" already exists"
argument_list|)
throw|;
block|}
if|if
condition|(
name|p
operator|.
name|getSchema
argument_list|()
operator|.
name|equals
argument_list|(
name|policy
operator|.
name|getSchema
argument_list|()
argument_list|)
operator|&&
name|p
operator|.
name|getCellSize
argument_list|()
operator|==
name|policy
operator|.
name|getCellSize
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"A policy with same schema "
operator|+
name|policy
operator|.
name|getSchema
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|" and cell size "
operator|+
name|p
operator|.
name|getCellSize
argument_list|()
operator|+
literal|" already exists"
argument_list|)
throw|;
block|}
block|}
name|policy
operator|.
name|setName
argument_list|(
name|assignedNewName
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setId
argument_list|(
name|getNextAvailablePolicyID
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|policiesByName
operator|.
name|put
argument_list|(
name|policy
operator|.
name|getName
argument_list|()
argument_list|,
name|policy
argument_list|)
expr_stmt|;
name|this
operator|.
name|policiesByID
operator|.
name|put
argument_list|(
name|policy
operator|.
name|getId
argument_list|()
argument_list|,
name|policy
argument_list|)
expr_stmt|;
name|allPolicies
operator|=
name|policiesByName
operator|.
name|values
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|ErasureCodingPolicy
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
return|return
name|policy
return|;
block|}
DECL|method|getNextAvailablePolicyID ()
specifier|private
name|byte
name|getNextAvailablePolicyID
parameter_list|()
block|{
name|byte
name|currentId
init|=
name|this
operator|.
name|policiesByID
operator|.
name|keySet
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|max
argument_list|(
name|Byte
operator|::
name|compareTo
argument_list|)
operator|.
name|filter
argument_list|(
name|id
lambda|->
name|id
operator|>=
name|ErasureCodeConstants
operator|.
name|USER_DEFINED_POLICY_START_ID
argument_list|)
operator|.
name|orElse
argument_list|(
name|ErasureCodeConstants
operator|.
name|USER_DEFINED_POLICY_START_ID
argument_list|)
decl_stmt|;
return|return
call|(
name|byte
call|)
argument_list|(
name|currentId
operator|+
literal|1
argument_list|)
return|;
block|}
comment|/**    * Remove an User erasure coding policy by policyName.    */
DECL|method|removePolicy (String name)
specifier|public
specifier|synchronized
name|void
name|removePolicy
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|ErasureCodingPolicy
name|ecPolicy
init|=
name|policiesByName
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|ecPolicy
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"The policy name "
operator|+
name|name
operator|+
literal|" does not exist"
argument_list|)
throw|;
block|}
if|if
condition|(
name|ecPolicy
operator|.
name|isSystemPolicy
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"System erasure coding policy "
operator|+
name|name
operator|+
literal|" cannot be removed"
argument_list|)
throw|;
block|}
if|if
condition|(
name|enabledPoliciesByName
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|enabledPoliciesByName
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|enabledPolicies
operator|=
name|enabledPoliciesByName
operator|.
name|values
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|ErasureCodingPolicy
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
name|ecPolicy
operator|.
name|setState
argument_list|(
name|ErasureCodingPolicyState
operator|.
name|REMOVED
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Remove erasure coding policy "
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getRemovedPolicies ()
specifier|public
name|List
argument_list|<
name|ErasureCodingPolicy
argument_list|>
name|getRemovedPolicies
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|ErasureCodingPolicy
argument_list|>
name|removedPolicies
init|=
operator|new
name|ArrayList
argument_list|<
name|ErasureCodingPolicy
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ErasureCodingPolicy
name|ecPolicy
range|:
name|policiesByName
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|ecPolicy
operator|.
name|isRemoved
argument_list|()
condition|)
block|{
name|removedPolicies
operator|.
name|add
argument_list|(
name|ecPolicy
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|removedPolicies
return|;
block|}
comment|/**    * Disable an erasure coding policy by policyName.    */
DECL|method|disablePolicy (String name)
specifier|public
specifier|synchronized
name|void
name|disablePolicy
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|ErasureCodingPolicy
name|ecPolicy
init|=
name|policiesByName
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|ecPolicy
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"The policy name "
operator|+
name|name
operator|+
literal|" does not exist"
argument_list|)
throw|;
block|}
if|if
condition|(
name|enabledPoliciesByName
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|enabledPoliciesByName
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|enabledPolicies
operator|=
name|enabledPoliciesByName
operator|.
name|values
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|ErasureCodingPolicy
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
name|ecPolicy
operator|.
name|setState
argument_list|(
name|ErasureCodingPolicyState
operator|.
name|DISABLED
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Disable the erasure coding policy "
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**    * Enable an erasure coding policy by policyName.    */
DECL|method|enablePolicy (String name)
specifier|public
specifier|synchronized
name|void
name|enablePolicy
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|ErasureCodingPolicy
name|ecPolicy
init|=
name|policiesByName
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|ecPolicy
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"The policy name "
operator|+
name|name
operator|+
literal|" does not exist"
argument_list|)
throw|;
block|}
name|enabledPoliciesByName
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|ecPolicy
argument_list|)
expr_stmt|;
name|ecPolicy
operator|.
name|setState
argument_list|(
name|ErasureCodingPolicyState
operator|.
name|ENABLED
argument_list|)
expr_stmt|;
name|enabledPolicies
operator|=
name|enabledPoliciesByName
operator|.
name|values
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|ErasureCodingPolicy
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Enable the erasure coding policy "
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

