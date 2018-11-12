begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.volume.csi
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
name|volume
operator|.
name|csi
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Strings
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
name|volume
operator|.
name|csi
operator|.
name|exception
operator|.
name|InvalidVolumeException
import|;
end_import

begin_comment
comment|/**  * Volume capability range that specified in a volume resource request,  * this range defines the desired min/max capacity.  */
end_comment

begin_class
DECL|class|VolumeCapabilityRange
specifier|public
specifier|final
class|class
name|VolumeCapabilityRange
block|{
DECL|field|minCapacity
specifier|private
specifier|final
name|long
name|minCapacity
decl_stmt|;
DECL|field|maxCapacity
specifier|private
specifier|final
name|long
name|maxCapacity
decl_stmt|;
DECL|field|unit
specifier|private
specifier|final
name|String
name|unit
decl_stmt|;
DECL|method|VolumeCapabilityRange (long minCapacity, long maxCapacity, String unit)
specifier|private
name|VolumeCapabilityRange
parameter_list|(
name|long
name|minCapacity
parameter_list|,
name|long
name|maxCapacity
parameter_list|,
name|String
name|unit
parameter_list|)
block|{
name|this
operator|.
name|minCapacity
operator|=
name|minCapacity
expr_stmt|;
name|this
operator|.
name|maxCapacity
operator|=
name|maxCapacity
expr_stmt|;
name|this
operator|.
name|unit
operator|=
name|unit
expr_stmt|;
block|}
DECL|method|getMinCapacity ()
specifier|public
name|long
name|getMinCapacity
parameter_list|()
block|{
return|return
name|minCapacity
return|;
block|}
DECL|method|getMaxCapacity ()
specifier|public
name|long
name|getMaxCapacity
parameter_list|()
block|{
return|return
name|maxCapacity
return|;
block|}
DECL|method|getUnit ()
specifier|public
name|String
name|getUnit
parameter_list|()
block|{
return|return
name|unit
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
literal|"MinCapability: "
operator|+
name|minCapacity
operator|+
name|unit
operator|+
literal|", MaxCapability: "
operator|+
name|maxCapacity
operator|+
name|unit
return|;
block|}
DECL|method|newBuilder ()
specifier|public
specifier|static
name|VolumeCapabilityBuilder
name|newBuilder
parameter_list|()
block|{
return|return
operator|new
name|VolumeCapabilityBuilder
argument_list|()
return|;
block|}
comment|/**    * The builder used to build a VolumeCapabilityRange instance.    */
DECL|class|VolumeCapabilityBuilder
specifier|public
specifier|static
class|class
name|VolumeCapabilityBuilder
block|{
comment|// An invalid default value implies this value must be set
DECL|field|minCap
specifier|private
name|long
name|minCap
init|=
operator|-
literal|1L
decl_stmt|;
DECL|field|maxCap
specifier|private
name|long
name|maxCap
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|field|unit
specifier|private
name|String
name|unit
decl_stmt|;
DECL|method|minCapacity (long minCapacity)
specifier|public
name|VolumeCapabilityBuilder
name|minCapacity
parameter_list|(
name|long
name|minCapacity
parameter_list|)
block|{
name|this
operator|.
name|minCap
operator|=
name|minCapacity
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|maxCapacity (long maxCapacity)
specifier|public
name|VolumeCapabilityBuilder
name|maxCapacity
parameter_list|(
name|long
name|maxCapacity
parameter_list|)
block|{
name|this
operator|.
name|maxCap
operator|=
name|maxCapacity
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|unit (String capacityUnit)
specifier|public
name|VolumeCapabilityBuilder
name|unit
parameter_list|(
name|String
name|capacityUnit
parameter_list|)
block|{
name|this
operator|.
name|unit
operator|=
name|capacityUnit
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build ()
specifier|public
name|VolumeCapabilityRange
name|build
parameter_list|()
throws|throws
name|InvalidVolumeException
block|{
name|VolumeCapabilityRange
name|capability
init|=
operator|new
name|VolumeCapabilityRange
argument_list|(
name|minCap
argument_list|,
name|maxCap
argument_list|,
name|unit
argument_list|)
decl_stmt|;
name|validateCapability
argument_list|(
name|capability
argument_list|)
expr_stmt|;
return|return
name|capability
return|;
block|}
DECL|method|validateCapability (VolumeCapabilityRange capability)
specifier|private
name|void
name|validateCapability
parameter_list|(
name|VolumeCapabilityRange
name|capability
parameter_list|)
throws|throws
name|InvalidVolumeException
block|{
if|if
condition|(
name|capability
operator|.
name|getMinCapacity
argument_list|()
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|InvalidVolumeException
argument_list|(
literal|"Invalid volume capability range,"
operator|+
literal|" minimal capability must not be less than 0. Capability: "
operator|+
name|capability
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|capability
operator|.
name|getUnit
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidVolumeException
argument_list|(
literal|"Invalid volume capability range,"
operator|+
literal|" capability unit is missing. Capability: "
operator|+
name|capability
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

