begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.webapp.dao
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
name|webapp
operator|.
name|dao
package|;
end_package

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
name|XmlAccessType
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
name|XmlAccessorType
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
name|Public
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
name|Evolving
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
name|ApplicationAttemptReport
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
name|YarnApplicationAttemptState
import|;
end_import

begin_class
annotation|@
name|Public
annotation|@
name|Evolving
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"appAttempt"
argument_list|)
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
DECL|class|AppAttemptInfo
specifier|public
class|class
name|AppAttemptInfo
block|{
DECL|field|appAttemptId
specifier|protected
name|String
name|appAttemptId
decl_stmt|;
DECL|field|host
specifier|protected
name|String
name|host
decl_stmt|;
DECL|field|rpcPort
specifier|protected
name|int
name|rpcPort
decl_stmt|;
DECL|field|trackingUrl
specifier|protected
name|String
name|trackingUrl
decl_stmt|;
DECL|field|originalTrackingUrl
specifier|protected
name|String
name|originalTrackingUrl
decl_stmt|;
DECL|field|diagnosticsInfo
specifier|protected
name|String
name|diagnosticsInfo
decl_stmt|;
DECL|field|appAttemptState
specifier|protected
name|YarnApplicationAttemptState
name|appAttemptState
decl_stmt|;
DECL|field|amContainerId
specifier|protected
name|String
name|amContainerId
decl_stmt|;
DECL|field|startedTime
specifier|protected
name|long
name|startedTime
decl_stmt|;
DECL|field|finishedTime
specifier|protected
name|long
name|finishedTime
decl_stmt|;
DECL|method|AppAttemptInfo ()
specifier|public
name|AppAttemptInfo
parameter_list|()
block|{
comment|// JAXB needs this
block|}
DECL|method|AppAttemptInfo (ApplicationAttemptReport appAttempt)
specifier|public
name|AppAttemptInfo
parameter_list|(
name|ApplicationAttemptReport
name|appAttempt
parameter_list|)
block|{
name|appAttemptId
operator|=
name|appAttempt
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|host
operator|=
name|appAttempt
operator|.
name|getHost
argument_list|()
expr_stmt|;
name|rpcPort
operator|=
name|appAttempt
operator|.
name|getRpcPort
argument_list|()
expr_stmt|;
name|trackingUrl
operator|=
name|appAttempt
operator|.
name|getTrackingUrl
argument_list|()
expr_stmt|;
name|originalTrackingUrl
operator|=
name|appAttempt
operator|.
name|getOriginalTrackingUrl
argument_list|()
expr_stmt|;
name|diagnosticsInfo
operator|=
name|appAttempt
operator|.
name|getDiagnostics
argument_list|()
expr_stmt|;
name|appAttemptState
operator|=
name|appAttempt
operator|.
name|getYarnApplicationAttemptState
argument_list|()
expr_stmt|;
if|if
condition|(
name|appAttempt
operator|.
name|getAMContainerId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|amContainerId
operator|=
name|appAttempt
operator|.
name|getAMContainerId
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|startedTime
operator|=
name|appAttempt
operator|.
name|getStartTime
argument_list|()
expr_stmt|;
name|finishedTime
operator|=
name|appAttempt
operator|.
name|getFinishTime
argument_list|()
expr_stmt|;
block|}
DECL|method|getAppAttemptId ()
specifier|public
name|String
name|getAppAttemptId
parameter_list|()
block|{
return|return
name|appAttemptId
return|;
block|}
DECL|method|getHost ()
specifier|public
name|String
name|getHost
parameter_list|()
block|{
return|return
name|host
return|;
block|}
DECL|method|getRpcPort ()
specifier|public
name|int
name|getRpcPort
parameter_list|()
block|{
return|return
name|rpcPort
return|;
block|}
DECL|method|getTrackingUrl ()
specifier|public
name|String
name|getTrackingUrl
parameter_list|()
block|{
return|return
name|trackingUrl
return|;
block|}
DECL|method|getOriginalTrackingUrl ()
specifier|public
name|String
name|getOriginalTrackingUrl
parameter_list|()
block|{
return|return
name|originalTrackingUrl
return|;
block|}
DECL|method|getDiagnosticsInfo ()
specifier|public
name|String
name|getDiagnosticsInfo
parameter_list|()
block|{
return|return
name|diagnosticsInfo
return|;
block|}
DECL|method|getAppAttemptState ()
specifier|public
name|YarnApplicationAttemptState
name|getAppAttemptState
parameter_list|()
block|{
return|return
name|appAttemptState
return|;
block|}
DECL|method|getAmContainerId ()
specifier|public
name|String
name|getAmContainerId
parameter_list|()
block|{
return|return
name|amContainerId
return|;
block|}
DECL|method|getStartedTime ()
specifier|public
name|long
name|getStartedTime
parameter_list|()
block|{
return|return
name|startedTime
return|;
block|}
DECL|method|getFinishedTime ()
specifier|public
name|long
name|getFinishedTime
parameter_list|()
block|{
return|return
name|finishedTime
return|;
block|}
block|}
end_class

end_unit

