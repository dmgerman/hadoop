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
name|ErasureCodeConstants
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
comment|/**    * TODO: HDFS-8095.    */
DECL|field|DEFAULT_CELLSIZE
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_CELLSIZE
init|=
literal|64
operator|*
literal|1024
decl_stmt|;
DECL|field|SYS_POLICY1
specifier|private
specifier|static
specifier|final
name|ErasureCodingPolicy
name|SYS_POLICY1
init|=
operator|new
name|ErasureCodingPolicy
argument_list|(
name|ErasureCodeConstants
operator|.
name|RS_6_3_SCHEMA
argument_list|,
name|DEFAULT_CELLSIZE
argument_list|,
name|HdfsConstants
operator|.
name|RS_6_3_POLICY_ID
argument_list|)
decl_stmt|;
DECL|field|SYS_POLICY2
specifier|private
specifier|static
specifier|final
name|ErasureCodingPolicy
name|SYS_POLICY2
init|=
operator|new
name|ErasureCodingPolicy
argument_list|(
name|ErasureCodeConstants
operator|.
name|RS_3_2_SCHEMA
argument_list|,
name|DEFAULT_CELLSIZE
argument_list|,
name|HdfsConstants
operator|.
name|RS_3_2_POLICY_ID
argument_list|)
decl_stmt|;
DECL|field|SYS_POLICY3
specifier|private
specifier|static
specifier|final
name|ErasureCodingPolicy
name|SYS_POLICY3
init|=
operator|new
name|ErasureCodingPolicy
argument_list|(
name|ErasureCodeConstants
operator|.
name|RS_6_3_LEGACY_SCHEMA
argument_list|,
name|DEFAULT_CELLSIZE
argument_list|,
name|HdfsConstants
operator|.
name|RS_6_3_LEGACY_POLICY_ID
argument_list|)
decl_stmt|;
DECL|field|SYS_POLICY4
specifier|private
specifier|static
specifier|final
name|ErasureCodingPolicy
name|SYS_POLICY4
init|=
operator|new
name|ErasureCodingPolicy
argument_list|(
name|ErasureCodeConstants
operator|.
name|XOR_2_1_SCHEMA
argument_list|,
name|DEFAULT_CELLSIZE
argument_list|,
name|HdfsConstants
operator|.
name|XOR_2_1_POLICY_ID
argument_list|)
decl_stmt|;
DECL|field|SYS_POLICY5
specifier|private
specifier|static
specifier|final
name|ErasureCodingPolicy
name|SYS_POLICY5
init|=
operator|new
name|ErasureCodingPolicy
argument_list|(
name|ErasureCodeConstants
operator|.
name|RS_10_4_SCHEMA
argument_list|,
name|DEFAULT_CELLSIZE
argument_list|,
name|HdfsConstants
operator|.
name|RS_10_4_POLICY_ID
argument_list|)
decl_stmt|;
comment|//We may add more later.
DECL|field|SYS_POLICIES
specifier|private
specifier|static
specifier|final
name|ErasureCodingPolicy
index|[]
name|SYS_POLICIES
init|=
operator|new
name|ErasureCodingPolicy
index|[]
block|{
name|SYS_POLICY1
block|,
name|SYS_POLICY2
block|,
name|SYS_POLICY3
block|,
name|SYS_POLICY4
block|,
name|SYS_POLICY5
block|}
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
comment|/**    * All supported policies maintained in NN memory for fast querying,    * identified and sorted by its name.    */
DECL|field|SYSTEM_POLICIES_BY_NAME
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|ErasureCodingPolicy
argument_list|>
name|SYSTEM_POLICIES_BY_NAME
decl_stmt|;
static|static
block|{
comment|// Create a hashmap of all available policies for quick lookup by name
name|SYSTEM_POLICIES_BY_NAME
operator|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|ErasureCodingPolicy
name|policy
range|:
name|SYS_POLICIES
control|)
block|{
name|SYSTEM_POLICIES_BY_NAME
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
block|}
block|}
comment|/**    * All enabled policies maintained in NN memory for fast querying,    * identified and sorted by its name.    */
DECL|field|enabledPoliciesByName
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|ErasureCodingPolicy
argument_list|>
name|enabledPoliciesByName
decl_stmt|;
DECL|method|ErasureCodingPolicyManager (Configuration conf)
name|ErasureCodingPolicyManager
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
comment|// Populate the list of enabled policies from configuration
specifier|final
name|String
index|[]
name|policyNames
init|=
name|conf
operator|.
name|getTrimmedStrings
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_EC_POLICIES_ENABLED_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_EC_POLICIES_ENABLED_DEFAULT
argument_list|)
decl_stmt|;
name|this
operator|.
name|enabledPoliciesByName
operator|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|policyName
range|:
name|policyNames
control|)
block|{
if|if
condition|(
name|policyName
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|ErasureCodingPolicy
name|ecPolicy
init|=
name|SYSTEM_POLICIES_BY_NAME
operator|.
name|get
argument_list|(
name|policyName
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
name|sysPolicies
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|SYS_POLICIES
argument_list|)
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
literal|"valid policy. Please choose from list of available policies: "
operator|+
literal|"[%s]"
argument_list|,
name|policyName
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_EC_POLICIES_ENABLED_KEY
argument_list|,
name|sysPolicies
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|IllegalArgumentException
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
comment|/**      * TODO: HDFS-7859 persist into NameNode      * load persistent policies from image and editlog, which is done only once      * during NameNode startup. This can be done here or in a separate method.      */
block|}
comment|/**    * Get system defined policies.    * @return system policies    */
DECL|method|getSystemPolicies ()
specifier|public
specifier|static
name|ErasureCodingPolicy
index|[]
name|getSystemPolicies
parameter_list|()
block|{
return|return
name|SYS_POLICIES
return|;
block|}
comment|/**    * Get a policy by policy ID.    * @return ecPolicy, or null if not found    */
DECL|method|getPolicyByID (byte id)
specifier|public
specifier|static
name|ErasureCodingPolicy
name|getPolicyByID
parameter_list|(
name|byte
name|id
parameter_list|)
block|{
for|for
control|(
name|ErasureCodingPolicy
name|policy
range|:
name|SYS_POLICIES
control|)
block|{
if|if
condition|(
name|policy
operator|.
name|getId
argument_list|()
operator|==
name|id
condition|)
block|{
return|return
name|policy
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Get a policy by policy name.    * @return ecPolicy, or null if not found    */
DECL|method|getPolicyByName (String name)
specifier|public
specifier|static
name|ErasureCodingPolicy
name|getPolicyByName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|SYSTEM_POLICIES_BY_NAME
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**    * Get the set of enabled policies.    * @return all policies    */
DECL|method|getEnabledPolicies ()
specifier|public
name|ErasureCodingPolicy
index|[]
name|getEnabledPolicies
parameter_list|()
block|{
name|ErasureCodingPolicy
index|[]
name|results
init|=
operator|new
name|ErasureCodingPolicy
index|[
name|enabledPoliciesByName
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
return|return
name|enabledPoliciesByName
operator|.
name|values
argument_list|()
operator|.
name|toArray
argument_list|(
name|results
argument_list|)
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
return|return
name|enabledPoliciesByName
operator|.
name|get
argument_list|(
name|name
argument_list|)
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
comment|/**    * Clear and clean up.    */
DECL|method|clear ()
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|enabledPoliciesByName
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

