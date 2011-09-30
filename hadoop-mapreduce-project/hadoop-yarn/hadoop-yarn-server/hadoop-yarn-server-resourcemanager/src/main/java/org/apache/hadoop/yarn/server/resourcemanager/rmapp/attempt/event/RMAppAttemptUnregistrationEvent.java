begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event
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
name|resourcemanager
operator|.
name|rmapp
operator|.
name|attempt
operator|.
name|event
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
name|ApplicationAttemptId
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
name|FinalApplicationStatus
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
name|resourcemanager
operator|.
name|rmapp
operator|.
name|attempt
operator|.
name|RMAppAttemptEvent
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
name|resourcemanager
operator|.
name|rmapp
operator|.
name|attempt
operator|.
name|RMAppAttemptEventType
import|;
end_import

begin_class
DECL|class|RMAppAttemptUnregistrationEvent
specifier|public
class|class
name|RMAppAttemptUnregistrationEvent
extends|extends
name|RMAppAttemptEvent
block|{
DECL|field|trackingUrl
specifier|private
specifier|final
name|String
name|trackingUrl
decl_stmt|;
DECL|field|finalStatus
specifier|private
specifier|final
name|FinalApplicationStatus
name|finalStatus
decl_stmt|;
DECL|field|diagnostics
specifier|private
specifier|final
name|String
name|diagnostics
decl_stmt|;
DECL|method|RMAppAttemptUnregistrationEvent (ApplicationAttemptId appAttemptId, String trackingUrl, FinalApplicationStatus finalStatus, String diagnostics)
specifier|public
name|RMAppAttemptUnregistrationEvent
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|,
name|String
name|trackingUrl
parameter_list|,
name|FinalApplicationStatus
name|finalStatus
parameter_list|,
name|String
name|diagnostics
parameter_list|)
block|{
name|super
argument_list|(
name|appAttemptId
argument_list|,
name|RMAppAttemptEventType
operator|.
name|UNREGISTERED
argument_list|)
expr_stmt|;
name|this
operator|.
name|trackingUrl
operator|=
name|trackingUrl
expr_stmt|;
name|this
operator|.
name|finalStatus
operator|=
name|finalStatus
expr_stmt|;
name|this
operator|.
name|diagnostics
operator|=
name|diagnostics
expr_stmt|;
block|}
DECL|method|getTrackingUrl ()
specifier|public
name|String
name|getTrackingUrl
parameter_list|()
block|{
return|return
name|this
operator|.
name|trackingUrl
return|;
block|}
DECL|method|getFinalApplicationStatus ()
specifier|public
name|FinalApplicationStatus
name|getFinalApplicationStatus
parameter_list|()
block|{
return|return
name|this
operator|.
name|finalStatus
return|;
block|}
DECL|method|getDiagnostics ()
specifier|public
name|String
name|getDiagnostics
parameter_list|()
block|{
return|return
name|this
operator|.
name|diagnostics
return|;
block|}
block|}
end_class

end_unit

