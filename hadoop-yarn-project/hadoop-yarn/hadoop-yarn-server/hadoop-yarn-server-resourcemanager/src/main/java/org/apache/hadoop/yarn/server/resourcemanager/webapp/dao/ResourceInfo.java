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
name|XmlElement
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
name|resource
operator|.
name|Resources
import|;
end_import

begin_class
annotation|@
name|XmlRootElement
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|NONE
argument_list|)
DECL|class|ResourceInfo
specifier|public
class|class
name|ResourceInfo
block|{
annotation|@
name|XmlElement
DECL|field|memory
name|long
name|memory
decl_stmt|;
annotation|@
name|XmlElement
DECL|field|vCores
name|int
name|vCores
decl_stmt|;
DECL|field|resources
specifier|private
name|Resource
name|resources
decl_stmt|;
DECL|method|ResourceInfo ()
specifier|public
name|ResourceInfo
parameter_list|()
block|{   }
DECL|method|ResourceInfo (Resource res)
specifier|public
name|ResourceInfo
parameter_list|(
name|Resource
name|res
parameter_list|)
block|{
name|memory
operator|=
name|res
operator|.
name|getMemorySize
argument_list|()
expr_stmt|;
name|vCores
operator|=
name|res
operator|.
name|getVirtualCores
argument_list|()
expr_stmt|;
name|resources
operator|=
name|Resources
operator|.
name|clone
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|getMemorySize ()
specifier|public
name|long
name|getMemorySize
parameter_list|()
block|{
if|if
condition|(
name|resources
operator|==
literal|null
condition|)
block|{
name|resources
operator|=
name|Resource
operator|.
name|newInstance
argument_list|(
name|memory
argument_list|,
name|vCores
argument_list|)
expr_stmt|;
block|}
return|return
name|resources
operator|.
name|getMemorySize
argument_list|()
return|;
block|}
DECL|method|getvCores ()
specifier|public
name|int
name|getvCores
parameter_list|()
block|{
if|if
condition|(
name|resources
operator|==
literal|null
condition|)
block|{
name|resources
operator|=
name|Resource
operator|.
name|newInstance
argument_list|(
name|memory
argument_list|,
name|vCores
argument_list|)
expr_stmt|;
block|}
return|return
name|resources
operator|.
name|getVirtualCores
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
name|resources
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|setMemory (int memory)
specifier|public
name|void
name|setMemory
parameter_list|(
name|int
name|memory
parameter_list|)
block|{
if|if
condition|(
name|resources
operator|==
literal|null
condition|)
block|{
name|resources
operator|=
name|Resource
operator|.
name|newInstance
argument_list|(
name|memory
argument_list|,
name|vCores
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|memory
operator|=
name|memory
expr_stmt|;
name|resources
operator|.
name|setMemorySize
argument_list|(
name|memory
argument_list|)
expr_stmt|;
block|}
DECL|method|setvCores (int vCores)
specifier|public
name|void
name|setvCores
parameter_list|(
name|int
name|vCores
parameter_list|)
block|{
if|if
condition|(
name|resources
operator|==
literal|null
condition|)
block|{
name|resources
operator|=
name|Resource
operator|.
name|newInstance
argument_list|(
name|memory
argument_list|,
name|vCores
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|vCores
operator|=
name|vCores
expr_stmt|;
name|resources
operator|.
name|setVirtualCores
argument_list|(
name|vCores
argument_list|)
expr_stmt|;
block|}
DECL|method|getResource ()
specifier|public
name|Resource
name|getResource
parameter_list|()
block|{
return|return
name|Resource
operator|.
name|newInstance
argument_list|(
name|resources
argument_list|)
return|;
block|}
block|}
end_class

end_unit

