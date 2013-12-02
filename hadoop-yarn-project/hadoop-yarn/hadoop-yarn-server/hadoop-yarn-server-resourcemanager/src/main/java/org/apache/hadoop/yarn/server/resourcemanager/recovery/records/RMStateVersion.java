begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.recovery.records
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
name|recovery
operator|.
name|records
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
name|InterfaceStability
operator|.
name|Unstable
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
comment|/**  * The version information of RM state.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|RMStateVersion
specifier|public
specifier|abstract
class|class
name|RMStateVersion
block|{
DECL|method|newInstance (int majorVersion, int minorVersion)
specifier|public
specifier|static
name|RMStateVersion
name|newInstance
parameter_list|(
name|int
name|majorVersion
parameter_list|,
name|int
name|minorVersion
parameter_list|)
block|{
name|RMStateVersion
name|version
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|RMStateVersion
operator|.
name|class
argument_list|)
decl_stmt|;
name|version
operator|.
name|setMajorVersion
argument_list|(
name|majorVersion
argument_list|)
expr_stmt|;
name|version
operator|.
name|setMinorVersion
argument_list|(
name|minorVersion
argument_list|)
expr_stmt|;
return|return
name|version
return|;
block|}
DECL|method|getMajorVersion ()
specifier|public
specifier|abstract
name|int
name|getMajorVersion
parameter_list|()
function_decl|;
DECL|method|setMajorVersion (int majorVersion)
specifier|public
specifier|abstract
name|void
name|setMajorVersion
parameter_list|(
name|int
name|majorVersion
parameter_list|)
function_decl|;
DECL|method|getMinorVersion ()
specifier|public
specifier|abstract
name|int
name|getMinorVersion
parameter_list|()
function_decl|;
DECL|method|setMinorVersion (int minorVersion)
specifier|public
specifier|abstract
name|void
name|setMinorVersion
parameter_list|(
name|int
name|minorVersion
parameter_list|)
function_decl|;
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getMajorVersion
argument_list|()
operator|+
literal|"."
operator|+
name|getMinorVersion
argument_list|()
return|;
block|}
DECL|method|isCompatibleTo (RMStateVersion version)
specifier|public
name|boolean
name|isCompatibleTo
parameter_list|(
name|RMStateVersion
name|version
parameter_list|)
block|{
return|return
name|getMajorVersion
argument_list|()
operator|==
name|version
operator|.
name|getMajorVersion
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
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|getMajorVersion
argument_list|()
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|getMinorVersion
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
name|RMStateVersion
name|other
init|=
operator|(
name|RMStateVersion
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|getMajorVersion
argument_list|()
operator|==
name|other
operator|.
name|getMajorVersion
argument_list|()
operator|&&
name|this
operator|.
name|getMinorVersion
argument_list|()
operator|==
name|other
operator|.
name|getMinorVersion
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

