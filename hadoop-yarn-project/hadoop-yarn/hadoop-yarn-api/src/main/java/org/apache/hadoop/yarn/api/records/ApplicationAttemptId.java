begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|records
package|;
end_package

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|NumberFormat
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
operator|.
name|Private
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
operator|.
name|Public
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
operator|.
name|Stable
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
name|yarn
operator|.
name|util
operator|.
name|Records
import|;
end_import

begin_comment
comment|/**  *<p><code>ApplicationAttemptId</code> denotes the particular<em>attempt</em>  * of an<code>ApplicationMaster</code> for a given {@link ApplicationId}.</p>  *   *<p>Multiple attempts might be needed to run an application to completion due  * to temporal failures of the<code>ApplicationMaster</code> such as hardware  * failures, connectivity issues etc. on the node on which it was scheduled.</p>  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Stable
DECL|class|ApplicationAttemptId
specifier|public
specifier|abstract
class|class
name|ApplicationAttemptId
implements|implements
name|Comparable
argument_list|<
name|ApplicationAttemptId
argument_list|>
block|{
DECL|field|appAttemptIdStrPrefix
specifier|public
specifier|static
specifier|final
name|String
name|appAttemptIdStrPrefix
init|=
literal|"appattempt_"
decl_stmt|;
annotation|@
name|Private
DECL|method|newInstance (ApplicationId appId, int attemptId)
specifier|public
specifier|static
name|ApplicationAttemptId
name|newInstance
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|int
name|attemptId
parameter_list|)
block|{
name|ApplicationAttemptId
name|appAttemptId
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ApplicationAttemptId
operator|.
name|class
argument_list|)
decl_stmt|;
name|appAttemptId
operator|.
name|setApplicationId
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|appAttemptId
operator|.
name|setAttemptId
argument_list|(
name|attemptId
argument_list|)
expr_stmt|;
name|appAttemptId
operator|.
name|build
argument_list|()
expr_stmt|;
return|return
name|appAttemptId
return|;
block|}
comment|/**    * Get the<code>ApplicationId</code> of the<code>ApplicationAttempId</code>.     * @return<code>ApplicationId</code> of the<code>ApplicationAttempId</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getApplicationId ()
specifier|public
specifier|abstract
name|ApplicationId
name|getApplicationId
parameter_list|()
function_decl|;
annotation|@
name|Private
DECL|method|setApplicationId (ApplicationId appID)
specifier|protected
specifier|abstract
name|void
name|setApplicationId
parameter_list|(
name|ApplicationId
name|appID
parameter_list|)
function_decl|;
comment|/**    * Get the<code>attempt id</code> of the<code>Application</code>.    * @return<code>attempt id</code> of the<code>Application</code>    */
DECL|method|getAttemptId ()
specifier|public
specifier|abstract
name|int
name|getAttemptId
parameter_list|()
function_decl|;
annotation|@
name|Private
DECL|method|setAttemptId (int attemptId)
specifier|protected
specifier|abstract
name|void
name|setAttemptId
parameter_list|(
name|int
name|attemptId
parameter_list|)
function_decl|;
DECL|field|attemptIdFormat
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|NumberFormat
argument_list|>
name|attemptIdFormat
init|=
operator|new
name|ThreadLocal
argument_list|<
name|NumberFormat
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NumberFormat
name|initialValue
parameter_list|()
block|{
name|NumberFormat
name|fmt
init|=
name|NumberFormat
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|fmt
operator|.
name|setGroupingUsed
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|setMinimumIntegerDigits
argument_list|(
literal|6
argument_list|)
expr_stmt|;
return|return
name|fmt
return|;
block|}
block|}
decl_stmt|;
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
comment|// Generated by eclipse.
specifier|final
name|int
name|prime
init|=
literal|347671
decl_stmt|;
name|int
name|result
init|=
literal|5501
decl_stmt|;
name|ApplicationId
name|appId
init|=
name|getApplicationId
argument_list|()
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|appId
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|getAttemptId
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|ApplicationAttemptId
name|other
init|=
operator|(
name|ApplicationAttemptId
operator|)
name|obj
decl_stmt|;
if|if
condition|(
operator|!
name|this
operator|.
name|getApplicationId
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getApplicationId
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|this
operator|.
name|getAttemptId
argument_list|()
operator|!=
name|other
operator|.
name|getAttemptId
argument_list|()
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo (ApplicationAttemptId other)
specifier|public
name|int
name|compareTo
parameter_list|(
name|ApplicationAttemptId
name|other
parameter_list|)
block|{
name|int
name|compareAppIds
init|=
name|this
operator|.
name|getApplicationId
argument_list|()
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|compareAppIds
operator|==
literal|0
condition|)
block|{
return|return
name|this
operator|.
name|getAttemptId
argument_list|()
operator|-
name|other
operator|.
name|getAttemptId
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|compareAppIds
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|appAttemptIdStrPrefix
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|this
operator|.
name|getApplicationId
argument_list|()
operator|.
name|getClusterTimestamp
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"_"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|ApplicationId
operator|.
name|appIdFormat
operator|.
name|get
argument_list|()
operator|.
name|format
argument_list|(
name|this
operator|.
name|getApplicationId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"_"
argument_list|)
operator|.
name|append
argument_list|(
name|attemptIdFormat
operator|.
name|get
argument_list|()
operator|.
name|format
argument_list|(
name|getAttemptId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|build ()
specifier|protected
specifier|abstract
name|void
name|build
parameter_list|()
function_decl|;
block|}
end_class

end_unit

