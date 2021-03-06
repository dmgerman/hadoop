begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
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
name|Collections
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

begin_comment
comment|/**  *<p>The set of built-in erasure coding policies.</p>  *<p>Although this is a private class, EC policy IDs need to be treated like a  * stable interface. Adding, modifying, or removing built-in policies can cause  * inconsistencies with older clients.</p>  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|SystemErasureCodingPolicies
specifier|public
specifier|final
class|class
name|SystemErasureCodingPolicies
block|{
comment|// Private constructor, this is a utility class.
DECL|method|SystemErasureCodingPolicies ()
specifier|private
name|SystemErasureCodingPolicies
parameter_list|()
block|{}
comment|// 1 MB
DECL|field|DEFAULT_CELLSIZE
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_CELLSIZE
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|field|RS_6_3_POLICY_ID
specifier|public
specifier|static
specifier|final
name|byte
name|RS_6_3_POLICY_ID
init|=
literal|1
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
name|RS_6_3_POLICY_ID
argument_list|)
decl_stmt|;
DECL|field|RS_3_2_POLICY_ID
specifier|public
specifier|static
specifier|final
name|byte
name|RS_3_2_POLICY_ID
init|=
literal|2
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
name|RS_3_2_POLICY_ID
argument_list|)
decl_stmt|;
DECL|field|RS_6_3_LEGACY_POLICY_ID
specifier|public
specifier|static
specifier|final
name|byte
name|RS_6_3_LEGACY_POLICY_ID
init|=
literal|3
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
name|RS_6_3_LEGACY_POLICY_ID
argument_list|)
decl_stmt|;
DECL|field|XOR_2_1_POLICY_ID
specifier|public
specifier|static
specifier|final
name|byte
name|XOR_2_1_POLICY_ID
init|=
literal|4
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
name|XOR_2_1_POLICY_ID
argument_list|)
decl_stmt|;
DECL|field|RS_10_4_POLICY_ID
specifier|public
specifier|static
specifier|final
name|byte
name|RS_10_4_POLICY_ID
init|=
literal|5
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
name|RS_10_4_POLICY_ID
argument_list|)
decl_stmt|;
comment|// REPLICATION policy is always enabled.
DECL|field|REPLICATION_POLICY
specifier|private
specifier|static
specifier|final
name|ErasureCodingPolicy
name|REPLICATION_POLICY
init|=
operator|new
name|ErasureCodingPolicy
argument_list|(
name|ErasureCodeConstants
operator|.
name|REPLICATION_POLICY_NAME
argument_list|,
name|ErasureCodeConstants
operator|.
name|REPLICATION_1_2_SCHEMA
argument_list|,
name|DEFAULT_CELLSIZE
argument_list|,
name|ErasureCodeConstants
operator|.
name|REPLICATION_POLICY_ID
argument_list|)
decl_stmt|;
DECL|field|SYS_POLICIES
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|ErasureCodingPolicy
argument_list|>
name|SYS_POLICIES
init|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|SYS_POLICY1
argument_list|,
name|SYS_POLICY2
argument_list|,
name|SYS_POLICY3
argument_list|,
name|SYS_POLICY4
argument_list|,
name|SYS_POLICY5
argument_list|)
argument_list|)
decl_stmt|;
comment|/**    * System policies sorted by name for fast querying.    */
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
comment|/**    * System policies sorted by ID for fast querying.    */
DECL|field|SYSTEM_POLICIES_BY_ID
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|Byte
argument_list|,
name|ErasureCodingPolicy
argument_list|>
name|SYSTEM_POLICIES_BY_ID
decl_stmt|;
comment|/**    * Populate the lookup maps in a static block.    */
static|static
block|{
name|SYSTEM_POLICIES_BY_NAME
operator|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
expr_stmt|;
name|SYSTEM_POLICIES_BY_ID
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
name|SYSTEM_POLICIES_BY_ID
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
block|}
comment|/**    * Get system defined policies.    * @return system policies    */
DECL|method|getPolicies ()
specifier|public
specifier|static
name|List
argument_list|<
name|ErasureCodingPolicy
argument_list|>
name|getPolicies
parameter_list|()
block|{
return|return
name|SYS_POLICIES
return|;
block|}
comment|/**    * Get a policy by policy ID.    * @return ecPolicy, or null if not found    */
DECL|method|getByID (byte id)
specifier|public
specifier|static
name|ErasureCodingPolicy
name|getByID
parameter_list|(
name|byte
name|id
parameter_list|)
block|{
return|return
name|SYSTEM_POLICIES_BY_ID
operator|.
name|get
argument_list|(
name|id
argument_list|)
return|;
block|}
comment|/**    * Get a policy by policy name.    * @return ecPolicy, or null if not found    */
DECL|method|getByName (String name)
specifier|public
specifier|static
name|ErasureCodingPolicy
name|getByName
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
comment|/**    * Get the special REPLICATION policy.    */
DECL|method|getReplicationPolicy ()
specifier|public
specifier|static
name|ErasureCodingPolicy
name|getReplicationPolicy
parameter_list|()
block|{
return|return
name|REPLICATION_POLICY
return|;
block|}
block|}
end_class

end_unit

