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
name|yarn
operator|.
name|util
operator|.
name|Records
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * YARN internal message used to validate volume capabilities  * with a CSI driver controller plugin.  */
end_comment

begin_class
DECL|class|ValidateVolumeCapabilitiesRequest
specifier|public
specifier|abstract
class|class
name|ValidateVolumeCapabilitiesRequest
block|{
comment|/**    * Volume access mode.    */
DECL|enum|AccessMode
specifier|public
enum|enum
name|AccessMode
block|{
DECL|enumConstant|UNKNOWN
name|UNKNOWN
block|,
DECL|enumConstant|SINGLE_NODE_WRITER
name|SINGLE_NODE_WRITER
block|,
DECL|enumConstant|SINGLE_NODE_READER_ONLY
name|SINGLE_NODE_READER_ONLY
block|,
DECL|enumConstant|MULTI_NODE_READER_ONLY
name|MULTI_NODE_READER_ONLY
block|,
DECL|enumConstant|MULTI_NODE_SINGLE_WRITER
name|MULTI_NODE_SINGLE_WRITER
block|,
DECL|enumConstant|MULTI_NODE_MULTI_WRITER
name|MULTI_NODE_MULTI_WRITER
block|,   }
comment|/**    * Volume type.    */
DECL|enum|VolumeType
specifier|public
enum|enum
name|VolumeType
block|{
DECL|enumConstant|BLOCK
name|BLOCK
block|,
DECL|enumConstant|FILE_SYSTEM
name|FILE_SYSTEM
block|}
comment|/**    * Volume capability.    */
DECL|class|VolumeCapability
specifier|public
specifier|static
class|class
name|VolumeCapability
block|{
DECL|field|mode
specifier|private
name|AccessMode
name|mode
decl_stmt|;
DECL|field|type
specifier|private
name|VolumeType
name|type
decl_stmt|;
DECL|field|flags
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|flags
decl_stmt|;
DECL|method|VolumeCapability (AccessMode accessMode, VolumeType volumeType, List<String> mountFlags)
specifier|public
name|VolumeCapability
parameter_list|(
name|AccessMode
name|accessMode
parameter_list|,
name|VolumeType
name|volumeType
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|mountFlags
parameter_list|)
block|{
name|this
operator|.
name|mode
operator|=
name|accessMode
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|volumeType
expr_stmt|;
name|this
operator|.
name|flags
operator|=
name|mountFlags
expr_stmt|;
block|}
DECL|method|getAccessMode ()
specifier|public
name|AccessMode
name|getAccessMode
parameter_list|()
block|{
return|return
name|mode
return|;
block|}
DECL|method|getVolumeType ()
specifier|public
name|VolumeType
name|getVolumeType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
DECL|method|getMountFlags ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getMountFlags
parameter_list|()
block|{
return|return
name|flags
return|;
block|}
block|}
DECL|method|newInstance ( String volumeId, List<VolumeCapability> volumeCapabilities, Map<String, String> volumeAttributes)
specifier|public
specifier|static
name|ValidateVolumeCapabilitiesRequest
name|newInstance
parameter_list|(
name|String
name|volumeId
parameter_list|,
name|List
argument_list|<
name|VolumeCapability
argument_list|>
name|volumeCapabilities
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|volumeAttributes
parameter_list|)
block|{
name|ValidateVolumeCapabilitiesRequest
name|request
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ValidateVolumeCapabilitiesRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setVolumeId
argument_list|(
name|volumeId
argument_list|)
expr_stmt|;
name|request
operator|.
name|setVolumeAttributes
argument_list|(
name|volumeAttributes
argument_list|)
expr_stmt|;
for|for
control|(
name|VolumeCapability
name|capability
range|:
name|volumeCapabilities
control|)
block|{
name|request
operator|.
name|addVolumeCapability
argument_list|(
name|capability
argument_list|)
expr_stmt|;
block|}
return|return
name|request
return|;
block|}
DECL|method|newInstance ( String volumeId, Map<String, String> volumeAttributes)
specifier|public
specifier|static
name|ValidateVolumeCapabilitiesRequest
name|newInstance
parameter_list|(
name|String
name|volumeId
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|volumeAttributes
parameter_list|)
block|{
name|ValidateVolumeCapabilitiesRequest
name|request
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ValidateVolumeCapabilitiesRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setVolumeId
argument_list|(
name|volumeId
argument_list|)
expr_stmt|;
name|request
operator|.
name|setVolumeAttributes
argument_list|(
name|volumeAttributes
argument_list|)
expr_stmt|;
return|return
name|request
return|;
block|}
DECL|method|setVolumeId (String volumeId)
specifier|public
specifier|abstract
name|void
name|setVolumeId
parameter_list|(
name|String
name|volumeId
parameter_list|)
function_decl|;
DECL|method|getVolumeId ()
specifier|public
specifier|abstract
name|String
name|getVolumeId
parameter_list|()
function_decl|;
DECL|method|setVolumeAttributes (Map<String, String> attributes)
specifier|public
specifier|abstract
name|void
name|setVolumeAttributes
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
parameter_list|)
function_decl|;
DECL|method|getVolumeAttributes ()
specifier|public
specifier|abstract
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getVolumeAttributes
parameter_list|()
function_decl|;
DECL|method|addVolumeCapability (VolumeCapability volumeCapability)
specifier|public
specifier|abstract
name|void
name|addVolumeCapability
parameter_list|(
name|VolumeCapability
name|volumeCapability
parameter_list|)
function_decl|;
DECL|method|getVolumeCapabilities ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|VolumeCapability
argument_list|>
name|getVolumeCapabilities
parameter_list|()
function_decl|;
block|}
end_class

end_unit

