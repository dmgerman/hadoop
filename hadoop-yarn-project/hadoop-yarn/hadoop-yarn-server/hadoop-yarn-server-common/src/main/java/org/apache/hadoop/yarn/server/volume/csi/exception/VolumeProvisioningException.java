begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.volume.csi.exception
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
operator|.
name|exception
package|;
end_package

begin_comment
comment|/**  * Exception throws when volume provisioning is failed.  */
end_comment

begin_class
DECL|class|VolumeProvisioningException
specifier|public
class|class
name|VolumeProvisioningException
extends|extends
name|VolumeException
block|{
DECL|method|VolumeProvisioningException (String message)
specifier|public
name|VolumeProvisioningException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
DECL|method|VolumeProvisioningException (String message, Exception e)
specifier|public
name|VolumeProvisioningException
parameter_list|(
name|String
name|message
parameter_list|,
name|Exception
name|e
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

