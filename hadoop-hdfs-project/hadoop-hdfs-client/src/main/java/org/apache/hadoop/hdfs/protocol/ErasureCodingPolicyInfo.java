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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|builder
operator|.
name|EqualsBuilder
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
name|lang3
operator|.
name|builder
operator|.
name|HashCodeBuilder
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
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_comment
comment|/**  * HDFS internal presentation of a {@link ErasureCodingPolicy}. Also contains  * additional information such as {@link ErasureCodingPolicyState}.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ErasureCodingPolicyInfo
specifier|public
class|class
name|ErasureCodingPolicyInfo
implements|implements
name|Serializable
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|0x31
decl_stmt|;
DECL|field|policy
specifier|private
specifier|final
name|ErasureCodingPolicy
name|policy
decl_stmt|;
DECL|field|state
specifier|private
name|ErasureCodingPolicyState
name|state
decl_stmt|;
DECL|method|ErasureCodingPolicyInfo (final ErasureCodingPolicy thePolicy, final ErasureCodingPolicyState theState)
specifier|public
name|ErasureCodingPolicyInfo
parameter_list|(
specifier|final
name|ErasureCodingPolicy
name|thePolicy
parameter_list|,
specifier|final
name|ErasureCodingPolicyState
name|theState
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|thePolicy
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|theState
argument_list|)
expr_stmt|;
name|policy
operator|=
name|thePolicy
expr_stmt|;
name|state
operator|=
name|theState
expr_stmt|;
block|}
DECL|method|ErasureCodingPolicyInfo (final ErasureCodingPolicy thePolicy)
specifier|public
name|ErasureCodingPolicyInfo
parameter_list|(
specifier|final
name|ErasureCodingPolicy
name|thePolicy
parameter_list|)
block|{
name|this
argument_list|(
name|thePolicy
argument_list|,
name|ErasureCodingPolicyState
operator|.
name|DISABLED
argument_list|)
expr_stmt|;
block|}
DECL|method|getPolicy ()
specifier|public
name|ErasureCodingPolicy
name|getPolicy
parameter_list|()
block|{
return|return
name|policy
return|;
block|}
DECL|method|getState ()
specifier|public
name|ErasureCodingPolicyState
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
DECL|method|setState (final ErasureCodingPolicyState newState)
specifier|public
name|void
name|setState
parameter_list|(
specifier|final
name|ErasureCodingPolicyState
name|newState
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|newState
argument_list|,
literal|"New state should not be null."
argument_list|)
expr_stmt|;
name|state
operator|=
name|newState
expr_stmt|;
block|}
DECL|method|isEnabled ()
specifier|public
name|boolean
name|isEnabled
parameter_list|()
block|{
return|return
operator|(
name|this
operator|.
name|state
operator|==
name|ErasureCodingPolicyState
operator|.
name|ENABLED
operator|)
return|;
block|}
DECL|method|isDisabled ()
specifier|public
name|boolean
name|isDisabled
parameter_list|()
block|{
return|return
operator|(
name|this
operator|.
name|state
operator|==
name|ErasureCodingPolicyState
operator|.
name|DISABLED
operator|)
return|;
block|}
DECL|method|isRemoved ()
specifier|public
name|boolean
name|isRemoved
parameter_list|()
block|{
return|return
operator|(
name|this
operator|.
name|state
operator|==
name|ErasureCodingPolicyState
operator|.
name|REMOVED
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|o
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|.
name|getClass
argument_list|()
operator|!=
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ErasureCodingPolicyInfo
name|rhs
init|=
operator|(
name|ErasureCodingPolicyInfo
operator|)
name|o
decl_stmt|;
return|return
operator|new
name|EqualsBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|policy
argument_list|,
name|rhs
operator|.
name|policy
argument_list|)
operator|.
name|append
argument_list|(
name|state
argument_list|,
name|rhs
operator|.
name|state
argument_list|)
operator|.
name|isEquals
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
operator|new
name|HashCodeBuilder
argument_list|(
literal|303855623
argument_list|,
literal|582626729
argument_list|)
operator|.
name|append
argument_list|(
name|policy
argument_list|)
operator|.
name|append
argument_list|(
name|state
argument_list|)
operator|.
name|toHashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|policy
operator|.
name|toString
argument_list|()
operator|+
literal|", State="
operator|+
name|state
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

