begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.resourceplugin.deviceframework
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|resourceplugin
operator|.
name|deviceframework
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
name|server
operator|.
name|nodemanager
operator|.
name|api
operator|.
name|deviceplugin
operator|.
name|Device
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * Device wrapper class used for NM REST API.  * */
end_comment

begin_class
DECL|class|AssignedDevice
specifier|public
class|class
name|AssignedDevice
implements|implements
name|Serializable
implements|,
name|Comparable
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|544285507952217366L
decl_stmt|;
DECL|field|device
specifier|private
name|Device
name|device
decl_stmt|;
DECL|field|containerId
specifier|private
name|String
name|containerId
decl_stmt|;
DECL|method|AssignedDevice (ContainerId cId, Device dev)
specifier|public
name|AssignedDevice
parameter_list|(
name|ContainerId
name|cId
parameter_list|,
name|Device
name|dev
parameter_list|)
block|{
name|this
operator|.
name|device
operator|=
name|dev
expr_stmt|;
name|this
operator|.
name|containerId
operator|=
name|cId
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
DECL|method|getDevice ()
specifier|public
name|Device
name|getDevice
parameter_list|()
block|{
return|return
name|device
return|;
block|}
DECL|method|getContainerId ()
specifier|public
name|String
name|getContainerId
parameter_list|()
block|{
return|return
name|containerId
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo (Object o)
specifier|public
name|int
name|compareTo
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
operator|||
operator|!
operator|(
name|o
operator|instanceof
name|AssignedDevice
operator|)
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|AssignedDevice
name|other
init|=
operator|(
name|AssignedDevice
operator|)
name|o
decl_stmt|;
name|int
name|result
init|=
name|getDevice
argument_list|()
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|getDevice
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
literal|0
operator|!=
name|result
condition|)
block|{
return|return
name|result
return|;
block|}
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
operator|||
operator|!
operator|(
name|o
operator|instanceof
name|AssignedDevice
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|AssignedDevice
name|other
init|=
operator|(
name|AssignedDevice
operator|)
name|o
decl_stmt|;
return|return
name|getDevice
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getDevice
argument_list|()
argument_list|)
operator|&&
name|getContainerId
argument_list|()
operator|.
name|equals
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
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|getDevice
argument_list|()
argument_list|,
name|getContainerId
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

