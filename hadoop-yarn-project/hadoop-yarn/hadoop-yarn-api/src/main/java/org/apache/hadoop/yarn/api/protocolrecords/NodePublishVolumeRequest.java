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
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|JsonObject
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
name|protocolrecords
operator|.
name|ValidateVolumeCapabilitiesRequest
operator|.
name|VolumeCapability
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
comment|/**  * The request sent by node manager to CSI driver adaptor  * to publish a volume on a node.  */
end_comment

begin_class
DECL|class|NodePublishVolumeRequest
specifier|public
specifier|abstract
class|class
name|NodePublishVolumeRequest
block|{
DECL|method|newInstance (String volumeId, boolean readOnly, String targetPath, String stagingPath, VolumeCapability capability, Map<String, String> publishContext, Map<String, String> secrets)
specifier|public
specifier|static
name|NodePublishVolumeRequest
name|newInstance
parameter_list|(
name|String
name|volumeId
parameter_list|,
name|boolean
name|readOnly
parameter_list|,
name|String
name|targetPath
parameter_list|,
name|String
name|stagingPath
parameter_list|,
name|VolumeCapability
name|capability
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|publishContext
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|secrets
parameter_list|)
block|{
name|NodePublishVolumeRequest
name|request
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|NodePublishVolumeRequest
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
name|setReadonly
argument_list|(
name|readOnly
argument_list|)
expr_stmt|;
name|request
operator|.
name|setTargetPath
argument_list|(
name|targetPath
argument_list|)
expr_stmt|;
name|request
operator|.
name|setStagingPath
argument_list|(
name|stagingPath
argument_list|)
expr_stmt|;
name|request
operator|.
name|setVolumeCapability
argument_list|(
name|capability
argument_list|)
expr_stmt|;
name|request
operator|.
name|setPublishContext
argument_list|(
name|publishContext
argument_list|)
expr_stmt|;
name|request
operator|.
name|setSecrets
argument_list|(
name|secrets
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
DECL|method|setReadonly (boolean readonly)
specifier|public
specifier|abstract
name|void
name|setReadonly
parameter_list|(
name|boolean
name|readonly
parameter_list|)
function_decl|;
DECL|method|getReadOnly ()
specifier|public
specifier|abstract
name|boolean
name|getReadOnly
parameter_list|()
function_decl|;
DECL|method|setTargetPath (String targetPath)
specifier|public
specifier|abstract
name|void
name|setTargetPath
parameter_list|(
name|String
name|targetPath
parameter_list|)
function_decl|;
DECL|method|getTargetPath ()
specifier|public
specifier|abstract
name|String
name|getTargetPath
parameter_list|()
function_decl|;
DECL|method|setStagingPath (String stagingPath)
specifier|public
specifier|abstract
name|void
name|setStagingPath
parameter_list|(
name|String
name|stagingPath
parameter_list|)
function_decl|;
DECL|method|getStagingPath ()
specifier|public
specifier|abstract
name|String
name|getStagingPath
parameter_list|()
function_decl|;
DECL|method|setVolumeCapability (VolumeCapability capability)
specifier|public
specifier|abstract
name|void
name|setVolumeCapability
parameter_list|(
name|VolumeCapability
name|capability
parameter_list|)
function_decl|;
DECL|method|getVolumeCapability ()
specifier|public
specifier|abstract
name|VolumeCapability
name|getVolumeCapability
parameter_list|()
function_decl|;
DECL|method|setPublishContext (Map<String, String> publishContext)
specifier|public
specifier|abstract
name|void
name|setPublishContext
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|publishContext
parameter_list|)
function_decl|;
DECL|method|getPublishContext ()
specifier|public
specifier|abstract
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getPublishContext
parameter_list|()
function_decl|;
DECL|method|setSecrets (Map<String, String> secrets)
specifier|public
specifier|abstract
name|void
name|setSecrets
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|secrets
parameter_list|)
function_decl|;
DECL|method|getSecrets ()
specifier|public
specifier|abstract
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getSecrets
parameter_list|()
function_decl|;
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|JsonObject
name|jsonObject
init|=
operator|new
name|JsonObject
argument_list|()
decl_stmt|;
name|jsonObject
operator|.
name|addProperty
argument_list|(
literal|"VolumeId"
argument_list|,
name|getVolumeId
argument_list|()
argument_list|)
expr_stmt|;
name|jsonObject
operator|.
name|addProperty
argument_list|(
literal|"ReadOnly"
argument_list|,
name|getReadOnly
argument_list|()
argument_list|)
expr_stmt|;
name|jsonObject
operator|.
name|addProperty
argument_list|(
literal|"TargetPath"
argument_list|,
name|getTargetPath
argument_list|()
argument_list|)
expr_stmt|;
name|jsonObject
operator|.
name|addProperty
argument_list|(
literal|"StagingPath"
argument_list|,
name|getStagingPath
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|getVolumeCapability
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|JsonObject
name|jsonCap
init|=
operator|new
name|JsonObject
argument_list|()
decl_stmt|;
name|jsonCap
operator|.
name|addProperty
argument_list|(
literal|"AccessMode"
argument_list|,
name|getVolumeCapability
argument_list|()
operator|.
name|getAccessMode
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|jsonCap
operator|.
name|addProperty
argument_list|(
literal|"VolumeType"
argument_list|,
name|getVolumeCapability
argument_list|()
operator|.
name|getVolumeType
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|jsonObject
operator|.
name|addProperty
argument_list|(
literal|"VolumeCapability"
argument_list|,
name|jsonCap
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|jsonObject
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

