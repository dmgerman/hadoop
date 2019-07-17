begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.protocolPB
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|protocolPB
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|OzoneManagerProtocolProtos
operator|.
name|OMRequest
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|OzoneManagerProtocolProtos
operator|.
name|OMResponse
import|;
end_import

begin_comment
comment|/**  * Handler to handle OM requests in OM HA.  */
end_comment

begin_interface
DECL|interface|OzoneManagerHARequestHandler
specifier|public
interface|interface
name|OzoneManagerHARequestHandler
extends|extends
name|RequestHandler
block|{
comment|/**    * Handle Apply Transaction Requests from OzoneManager StateMachine.    * @param omRequest    * @param transactionLogIndex - ratis transaction log index    * @return OMResponse    */
DECL|method|handleApplyTransaction (OMRequest omRequest, long transactionLogIndex)
name|OMResponse
name|handleApplyTransaction
parameter_list|(
name|OMRequest
name|omRequest
parameter_list|,
name|long
name|transactionLogIndex
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

