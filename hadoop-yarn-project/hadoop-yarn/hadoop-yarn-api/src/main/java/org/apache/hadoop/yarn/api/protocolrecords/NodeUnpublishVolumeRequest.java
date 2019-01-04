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

begin_comment
comment|/**  * The request sent by node manager to CSI driver adaptor  * to un-publish a volume on a node.  */
end_comment

begin_class
DECL|class|NodeUnpublishVolumeRequest
specifier|public
specifier|abstract
class|class
name|NodeUnpublishVolumeRequest
block|{
DECL|method|newInstance (String volumeId, String targetPath)
specifier|public
specifier|static
name|NodeUnpublishVolumeRequest
name|newInstance
parameter_list|(
name|String
name|volumeId
parameter_list|,
name|String
name|targetPath
parameter_list|)
block|{
name|NodeUnpublishVolumeRequest
name|request
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|NodeUnpublishVolumeRequest
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
name|setTargetPath
argument_list|(
name|targetPath
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
DECL|method|getVolumeId ()
specifier|public
specifier|abstract
name|String
name|getVolumeId
parameter_list|()
function_decl|;
DECL|method|getTargetPath ()
specifier|public
specifier|abstract
name|String
name|getTargetPath
parameter_list|()
function_decl|;
block|}
end_class

end_unit

