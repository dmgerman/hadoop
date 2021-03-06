begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.rmcontainer
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|rmcontainer
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ContainerId
import|;
end_import

begin_class
DECL|class|AllocationExpirationInfo
specifier|public
class|class
name|AllocationExpirationInfo
implements|implements
name|Comparable
argument_list|<
name|AllocationExpirationInfo
argument_list|>
block|{
DECL|field|containerId
specifier|private
specifier|final
name|ContainerId
name|containerId
decl_stmt|;
DECL|field|increase
specifier|private
specifier|final
name|boolean
name|increase
decl_stmt|;
DECL|method|AllocationExpirationInfo (ContainerId containerId)
specifier|public
name|AllocationExpirationInfo
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
block|{
name|this
argument_list|(
name|containerId
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|AllocationExpirationInfo ( ContainerId containerId, boolean increase)
specifier|public
name|AllocationExpirationInfo
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|boolean
name|increase
parameter_list|)
block|{
name|this
operator|.
name|containerId
operator|=
name|containerId
expr_stmt|;
name|this
operator|.
name|increase
operator|=
name|increase
expr_stmt|;
block|}
DECL|method|getContainerId ()
specifier|public
name|ContainerId
name|getContainerId
parameter_list|()
block|{
return|return
name|this
operator|.
name|containerId
return|;
block|}
DECL|method|isIncrease ()
specifier|public
name|boolean
name|isIncrease
parameter_list|()
block|{
return|return
name|this
operator|.
name|increase
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
operator|(
name|getContainerId
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|<<
literal|16
operator|)
return|;
block|}
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
operator|!
operator|(
name|other
operator|instanceof
name|AllocationExpirationInfo
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|compareTo
argument_list|(
operator|(
name|AllocationExpirationInfo
operator|)
name|other
argument_list|)
operator|==
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo (AllocationExpirationInfo other)
specifier|public
name|int
name|compareTo
parameter_list|(
name|AllocationExpirationInfo
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
comment|// Only need to compare containerId.
return|return
name|getContainerId
argument_list|()
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|getContainerId
argument_list|()
argument_list|)
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
literal|"<container="
operator|+
name|getContainerId
argument_list|()
operator|+
literal|", increase="
operator|+
name|isIncrease
argument_list|()
operator|+
literal|">"
return|;
block|}
block|}
end_class

end_unit

