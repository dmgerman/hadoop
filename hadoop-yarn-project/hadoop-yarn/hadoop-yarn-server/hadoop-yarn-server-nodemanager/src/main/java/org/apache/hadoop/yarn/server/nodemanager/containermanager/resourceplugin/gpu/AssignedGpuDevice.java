begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.resourceplugin.gpu
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
name|gpu
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

begin_comment
comment|/**  * In addition to {@link GpuDevice}, this include container id and more runtime  * information related to who is using the GPU device if possible  */
end_comment

begin_class
DECL|class|AssignedGpuDevice
specifier|public
class|class
name|AssignedGpuDevice
extends|extends
name|GpuDevice
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|12983712986315L
decl_stmt|;
DECL|field|containerId
name|String
name|containerId
decl_stmt|;
DECL|method|AssignedGpuDevice (int index, int minorNumber, ContainerId containerId)
specifier|public
name|AssignedGpuDevice
parameter_list|(
name|int
name|index
parameter_list|,
name|int
name|minorNumber
parameter_list|,
name|ContainerId
name|containerId
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|minorNumber
argument_list|)
expr_stmt|;
name|this
operator|.
name|containerId
operator|=
name|containerId
operator|.
name|toString
argument_list|()
expr_stmt|;
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
DECL|method|setContainerId (String containerId)
specifier|public
name|void
name|setContainerId
parameter_list|(
name|String
name|containerId
parameter_list|)
block|{
name|this
operator|.
name|containerId
operator|=
name|containerId
expr_stmt|;
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
name|obj
operator|==
literal|null
operator|||
operator|!
operator|(
name|obj
operator|instanceof
name|AssignedGpuDevice
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|AssignedGpuDevice
name|other
init|=
operator|(
name|AssignedGpuDevice
operator|)
name|obj
decl_stmt|;
return|return
name|index
operator|==
name|other
operator|.
name|index
operator|&&
name|minorNumber
operator|==
name|other
operator|.
name|minorNumber
operator|&&
name|containerId
operator|.
name|equals
argument_list|(
name|other
operator|.
name|containerId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo (Object obj)
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
literal|null
operator|||
operator|(
operator|!
operator|(
name|obj
operator|instanceof
name|AssignedGpuDevice
operator|)
operator|)
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|AssignedGpuDevice
name|other
init|=
operator|(
name|AssignedGpuDevice
operator|)
name|obj
decl_stmt|;
name|int
name|result
init|=
name|Integer
operator|.
name|compare
argument_list|(
name|index
argument_list|,
name|other
operator|.
name|index
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
name|result
operator|=
name|Integer
operator|.
name|compare
argument_list|(
name|minorNumber
argument_list|,
name|other
operator|.
name|minorNumber
argument_list|)
expr_stmt|;
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
name|containerId
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|containerId
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
specifier|final
name|int
name|prime
init|=
literal|47
decl_stmt|;
return|return
name|prime
operator|*
operator|(
name|prime
operator|*
name|index
operator|+
name|minorNumber
operator|)
operator|+
name|containerId
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

