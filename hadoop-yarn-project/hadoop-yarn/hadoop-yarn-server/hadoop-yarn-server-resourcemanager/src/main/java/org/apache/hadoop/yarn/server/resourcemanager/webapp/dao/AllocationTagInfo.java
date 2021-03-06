begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.webapp.dao
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
name|webapp
operator|.
name|dao
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessorType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlRootElement
import|;
end_import

begin_comment
comment|/**  * DAO object to display node allocation tag.  */
end_comment

begin_class
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"allocationTagInfo"
argument_list|)
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
DECL|class|AllocationTagInfo
specifier|public
class|class
name|AllocationTagInfo
block|{
DECL|field|allocationTag
specifier|private
name|String
name|allocationTag
decl_stmt|;
DECL|field|allocationsCount
specifier|private
name|long
name|allocationsCount
decl_stmt|;
DECL|method|AllocationTagInfo ()
specifier|public
name|AllocationTagInfo
parameter_list|()
block|{
comment|// JAXB needs this
block|}
DECL|method|AllocationTagInfo (String tag, long count)
specifier|public
name|AllocationTagInfo
parameter_list|(
name|String
name|tag
parameter_list|,
name|long
name|count
parameter_list|)
block|{
name|this
operator|.
name|allocationTag
operator|=
name|tag
expr_stmt|;
name|this
operator|.
name|allocationsCount
operator|=
name|count
expr_stmt|;
block|}
DECL|method|getAllocationTag ()
specifier|public
name|String
name|getAllocationTag
parameter_list|()
block|{
return|return
name|this
operator|.
name|allocationTag
return|;
block|}
DECL|method|getAllocationsCount ()
specifier|public
name|long
name|getAllocationsCount
parameter_list|()
block|{
return|return
name|this
operator|.
name|allocationsCount
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
name|allocationTag
operator|+
literal|"("
operator|+
name|allocationsCount
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

