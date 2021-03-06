begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.protocolrecords
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
name|protocolrecords
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|Resource
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
comment|/**  * Response class for getting the details for a particular resource profile.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|GetResourceProfileResponse
specifier|public
specifier|abstract
class|class
name|GetResourceProfileResponse
block|{
DECL|method|newInstance ()
specifier|public
specifier|static
name|GetResourceProfileResponse
name|newInstance
parameter_list|()
block|{
return|return
name|Records
operator|.
name|newRecord
argument_list|(
name|GetResourceProfileResponse
operator|.
name|class
argument_list|)
return|;
block|}
comment|/**    * Get the resources that will be allocated if the profile was used.    *    * @return the resources that will be allocated if the profile was used.    */
DECL|method|getResource ()
specifier|public
specifier|abstract
name|Resource
name|getResource
parameter_list|()
function_decl|;
comment|/**    * Set the resources that will be allocated if the profile is used.    *    * @param r Set the resources that will be allocated if the profile is used.    */
DECL|method|setResource (Resource r)
specifier|public
specifier|abstract
name|void
name|setResource
parameter_list|(
name|Resource
name|r
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|equals (Object other)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|other
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|other
operator|==
literal|null
operator|||
operator|!
operator|(
name|other
operator|instanceof
name|GetResourceProfileResponse
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|this
operator|.
name|getResource
argument_list|()
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|GetResourceProfileResponse
operator|)
name|other
operator|)
operator|.
name|getResource
argument_list|()
argument_list|)
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
name|getResource
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

