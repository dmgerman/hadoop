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
name|commons
operator|.
name|lang
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
name|lang
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
name|HadoopIllegalArgumentException
import|;
end_import

begin_comment
comment|/**  * A response of add an ErasureCoding policy.  */
end_comment

begin_class
DECL|class|AddErasureCodingPolicyResponse
specifier|public
class|class
name|AddErasureCodingPolicyResponse
block|{
DECL|field|succeed
specifier|private
name|boolean
name|succeed
decl_stmt|;
DECL|field|policy
specifier|private
name|ErasureCodingPolicy
name|policy
decl_stmt|;
DECL|field|errorMsg
specifier|private
name|String
name|errorMsg
decl_stmt|;
DECL|method|AddErasureCodingPolicyResponse (ErasureCodingPolicy policy)
specifier|public
name|AddErasureCodingPolicyResponse
parameter_list|(
name|ErasureCodingPolicy
name|policy
parameter_list|)
block|{
name|this
operator|.
name|policy
operator|=
name|policy
expr_stmt|;
name|this
operator|.
name|succeed
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|AddErasureCodingPolicyResponse (ErasureCodingPolicy policy, String errorMsg)
specifier|public
name|AddErasureCodingPolicyResponse
parameter_list|(
name|ErasureCodingPolicy
name|policy
parameter_list|,
name|String
name|errorMsg
parameter_list|)
block|{
name|this
operator|.
name|policy
operator|=
name|policy
expr_stmt|;
name|this
operator|.
name|errorMsg
operator|=
name|errorMsg
expr_stmt|;
name|this
operator|.
name|succeed
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|AddErasureCodingPolicyResponse (ErasureCodingPolicy policy, HadoopIllegalArgumentException e)
specifier|public
name|AddErasureCodingPolicyResponse
parameter_list|(
name|ErasureCodingPolicy
name|policy
parameter_list|,
name|HadoopIllegalArgumentException
name|e
parameter_list|)
block|{
name|this
argument_list|(
name|policy
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|isSucceed ()
specifier|public
name|boolean
name|isSucceed
parameter_list|()
block|{
return|return
name|succeed
return|;
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
DECL|method|getErrorMsg ()
specifier|public
name|String
name|getErrorMsg
parameter_list|()
block|{
return|return
name|errorMsg
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
if|if
condition|(
name|isSucceed
argument_list|()
condition|)
block|{
return|return
literal|"Add ErasureCodingPolicy "
operator|+
name|getPolicy
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" succeed."
return|;
block|}
else|else
block|{
return|return
literal|"Add ErasureCodingPolicy "
operator|+
name|getPolicy
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" failed and "
operator|+
literal|"error message is "
operator|+
name|getErrorMsg
argument_list|()
return|;
block|}
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
operator|instanceof
name|AddErasureCodingPolicyResponse
condition|)
block|{
name|AddErasureCodingPolicyResponse
name|other
init|=
operator|(
name|AddErasureCodingPolicyResponse
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
name|other
operator|.
name|policy
argument_list|)
operator|.
name|append
argument_list|(
name|succeed
argument_list|,
name|other
operator|.
name|succeed
argument_list|)
operator|.
name|append
argument_list|(
name|errorMsg
argument_list|,
name|other
operator|.
name|errorMsg
argument_list|)
operator|.
name|isEquals
argument_list|()
return|;
block|}
return|return
literal|false
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
name|succeed
argument_list|)
operator|.
name|append
argument_list|(
name|errorMsg
argument_list|)
operator|.
name|toHashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

