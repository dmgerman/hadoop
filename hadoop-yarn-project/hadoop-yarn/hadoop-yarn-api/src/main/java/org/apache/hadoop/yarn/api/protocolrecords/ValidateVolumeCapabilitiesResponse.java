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
comment|/**  * YARN internal message used to represent the response of  * volume capabilities validation with a CSI driver controller plugin.  */
end_comment

begin_class
DECL|class|ValidateVolumeCapabilitiesResponse
specifier|public
specifier|abstract
class|class
name|ValidateVolumeCapabilitiesResponse
block|{
DECL|method|newInstance ( boolean supported, String responseMessage)
specifier|public
specifier|static
name|ValidateVolumeCapabilitiesResponse
name|newInstance
parameter_list|(
name|boolean
name|supported
parameter_list|,
name|String
name|responseMessage
parameter_list|)
block|{
name|ValidateVolumeCapabilitiesResponse
name|record
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ValidateVolumeCapabilitiesResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|record
operator|.
name|setResponseMessage
argument_list|(
name|responseMessage
argument_list|)
expr_stmt|;
name|record
operator|.
name|setSupported
argument_list|(
name|supported
argument_list|)
expr_stmt|;
return|return
name|record
return|;
block|}
DECL|method|setSupported (boolean supported)
specifier|public
specifier|abstract
name|void
name|setSupported
parameter_list|(
name|boolean
name|supported
parameter_list|)
function_decl|;
DECL|method|isSupported ()
specifier|public
specifier|abstract
name|boolean
name|isSupported
parameter_list|()
function_decl|;
DECL|method|setResponseMessage (String responseMessage)
specifier|public
specifier|abstract
name|void
name|setResponseMessage
parameter_list|(
name|String
name|responseMessage
parameter_list|)
function_decl|;
DECL|method|getResponseMessage ()
specifier|public
specifier|abstract
name|String
name|getResponseMessage
parameter_list|()
function_decl|;
block|}
end_class

end_unit

