begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.webapp.dao.gpu
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
name|webapp
operator|.
name|dao
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
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|adapters
operator|.
name|XmlJavaTypeAdapter
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"bar1_memory_usage"
argument_list|)
DECL|class|PerGpuMemoryUsage
specifier|public
class|class
name|PerGpuMemoryUsage
block|{
DECL|field|usedMemoryMiB
name|long
name|usedMemoryMiB
init|=
operator|-
literal|1L
decl_stmt|;
DECL|field|availMemoryMiB
name|long
name|availMemoryMiB
init|=
operator|-
literal|1L
decl_stmt|;
annotation|@
name|XmlJavaTypeAdapter
argument_list|(
name|PerGpuDeviceInformation
operator|.
name|StrToMemAdapter
operator|.
name|class
argument_list|)
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"used"
argument_list|)
DECL|method|getUsedMemoryMiB ()
specifier|public
name|Long
name|getUsedMemoryMiB
parameter_list|()
block|{
return|return
name|usedMemoryMiB
return|;
block|}
DECL|method|setUsedMemoryMiB (Long usedMemoryMiB)
specifier|public
name|void
name|setUsedMemoryMiB
parameter_list|(
name|Long
name|usedMemoryMiB
parameter_list|)
block|{
name|this
operator|.
name|usedMemoryMiB
operator|=
name|usedMemoryMiB
expr_stmt|;
block|}
annotation|@
name|XmlJavaTypeAdapter
argument_list|(
name|PerGpuDeviceInformation
operator|.
name|StrToMemAdapter
operator|.
name|class
argument_list|)
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"free"
argument_list|)
DECL|method|getAvailMemoryMiB ()
specifier|public
name|Long
name|getAvailMemoryMiB
parameter_list|()
block|{
return|return
name|availMemoryMiB
return|;
block|}
DECL|method|setAvailMemoryMiB (Long availMemoryMiB)
specifier|public
name|void
name|setAvailMemoryMiB
parameter_list|(
name|Long
name|availMemoryMiB
parameter_list|)
block|{
name|this
operator|.
name|availMemoryMiB
operator|=
name|availMemoryMiB
expr_stmt|;
block|}
DECL|method|getTotalMemoryMiB ()
specifier|public
name|long
name|getTotalMemoryMiB
parameter_list|()
block|{
return|return
name|usedMemoryMiB
operator|+
name|availMemoryMiB
return|;
block|}
block|}
end_class

end_unit

