begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records.impl.pb
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
operator|.
name|impl
operator|.
name|pb
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
name|proto
operator|.
name|YarnProtos
operator|.
name|ResourceUtilizationProto
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
name|proto
operator|.
name|YarnProtos
operator|.
name|ResourceUtilizationProtoOrBuilder
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
name|ResourceUtilization
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|ResourceUtilizationPBImpl
specifier|public
class|class
name|ResourceUtilizationPBImpl
extends|extends
name|ResourceUtilization
block|{
DECL|field|proto
specifier|private
name|ResourceUtilizationProto
name|proto
init|=
name|ResourceUtilizationProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
specifier|private
name|ResourceUtilizationProto
operator|.
name|Builder
name|builder
init|=
literal|null
decl_stmt|;
DECL|field|viaProto
specifier|private
name|boolean
name|viaProto
init|=
literal|false
decl_stmt|;
DECL|method|ResourceUtilizationPBImpl ()
specifier|public
name|ResourceUtilizationPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|ResourceUtilizationProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|ResourceUtilizationPBImpl (ResourceUtilizationProto proto)
specifier|public
name|ResourceUtilizationPBImpl
parameter_list|(
name|ResourceUtilizationProto
name|proto
parameter_list|)
block|{
name|this
operator|.
name|proto
operator|=
name|proto
expr_stmt|;
name|viaProto
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|getProto ()
specifier|public
name|ResourceUtilizationProto
name|getProto
parameter_list|()
block|{
name|proto
operator|=
name|viaProto
condition|?
name|proto
else|:
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|viaProto
operator|=
literal|true
expr_stmt|;
return|return
name|proto
return|;
block|}
DECL|method|maybeInitBuilder ()
specifier|private
name|void
name|maybeInitBuilder
parameter_list|()
block|{
if|if
condition|(
name|viaProto
operator|||
name|builder
operator|==
literal|null
condition|)
block|{
name|builder
operator|=
name|ResourceUtilizationProto
operator|.
name|newBuilder
argument_list|(
name|proto
argument_list|)
expr_stmt|;
block|}
name|viaProto
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPhysicalMemory ()
specifier|public
name|int
name|getPhysicalMemory
parameter_list|()
block|{
name|ResourceUtilizationProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
return|return
operator|(
name|p
operator|.
name|getPmem
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setPhysicalMemory (int pmem)
specifier|public
name|void
name|setPhysicalMemory
parameter_list|(
name|int
name|pmem
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setPmem
argument_list|(
name|pmem
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getVirtualMemory ()
specifier|public
name|int
name|getVirtualMemory
parameter_list|()
block|{
name|ResourceUtilizationProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
return|return
operator|(
name|p
operator|.
name|getVmem
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setVirtualMemory (int vmem)
specifier|public
name|void
name|setVirtualMemory
parameter_list|(
name|int
name|vmem
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setVmem
argument_list|(
name|vmem
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCPU ()
specifier|public
name|float
name|getCPU
parameter_list|()
block|{
name|ResourceUtilizationProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
return|return
name|p
operator|.
name|getCpu
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setCPU (float cpu)
specifier|public
name|void
name|setCPU
parameter_list|(
name|float
name|cpu
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setCpu
argument_list|(
name|cpu
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compareTo (ResourceUtilization other)
specifier|public
name|int
name|compareTo
parameter_list|(
name|ResourceUtilization
name|other
parameter_list|)
block|{
name|int
name|diff
init|=
name|this
operator|.
name|getPhysicalMemory
argument_list|()
operator|-
name|other
operator|.
name|getPhysicalMemory
argument_list|()
decl_stmt|;
if|if
condition|(
name|diff
operator|==
literal|0
condition|)
block|{
name|diff
operator|=
name|this
operator|.
name|getVirtualMemory
argument_list|()
operator|-
name|other
operator|.
name|getVirtualMemory
argument_list|()
expr_stmt|;
if|if
condition|(
name|diff
operator|==
literal|0
condition|)
block|{
name|diff
operator|=
name|Float
operator|.
name|compare
argument_list|(
name|this
operator|.
name|getCPU
argument_list|()
argument_list|,
name|other
operator|.
name|getCPU
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|diff
return|;
block|}
block|}
end_class

end_unit

