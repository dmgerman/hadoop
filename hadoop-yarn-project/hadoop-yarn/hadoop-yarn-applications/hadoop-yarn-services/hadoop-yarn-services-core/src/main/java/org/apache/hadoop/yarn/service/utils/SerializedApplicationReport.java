begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.utils
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|utils
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
name|ApplicationReport
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
name|service
operator|.
name|utils
operator|.
name|ApplicationReportSerDeser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|annotate
operator|.
name|JsonIgnoreProperties
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|map
operator|.
name|annotate
operator|.
name|JsonSerialize
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Serialized form of an service report which can be persisted  * and then parsed. It can not be converted back into a  * real YARN service report  *   * Useful for testing  */
end_comment

begin_class
annotation|@
name|JsonIgnoreProperties
argument_list|(
name|ignoreUnknown
operator|=
literal|true
argument_list|)
annotation|@
name|JsonSerialize
argument_list|(
name|include
operator|=
name|JsonSerialize
operator|.
name|Inclusion
operator|.
name|NON_NULL
argument_list|)
DECL|class|SerializedApplicationReport
specifier|public
class|class
name|SerializedApplicationReport
block|{
DECL|field|applicationId
specifier|public
name|String
name|applicationId
decl_stmt|;
DECL|field|applicationAttemptId
specifier|public
name|String
name|applicationAttemptId
decl_stmt|;
DECL|field|name
specifier|public
name|String
name|name
decl_stmt|;
DECL|field|applicationType
specifier|public
name|String
name|applicationType
decl_stmt|;
DECL|field|user
specifier|public
name|String
name|user
decl_stmt|;
DECL|field|queue
specifier|public
name|String
name|queue
decl_stmt|;
DECL|field|host
specifier|public
name|String
name|host
decl_stmt|;
DECL|field|rpcPort
specifier|public
name|Integer
name|rpcPort
decl_stmt|;
DECL|field|state
specifier|public
name|String
name|state
decl_stmt|;
DECL|field|diagnostics
specifier|public
name|String
name|diagnostics
decl_stmt|;
DECL|field|url
specifier|public
name|String
name|url
decl_stmt|;
comment|/**    * This value is non-null only when a report is generated from a submission context.    * The YARN {@link ApplicationReport} structure does not propagate this value    * from the RM.    */
DECL|field|submitTime
specifier|public
name|Long
name|submitTime
decl_stmt|;
DECL|field|startTime
specifier|public
name|Long
name|startTime
decl_stmt|;
DECL|field|finishTime
specifier|public
name|Long
name|finishTime
decl_stmt|;
DECL|field|finalStatus
specifier|public
name|String
name|finalStatus
decl_stmt|;
DECL|field|origTrackingUrl
specifier|public
name|String
name|origTrackingUrl
decl_stmt|;
DECL|field|progress
specifier|public
name|Float
name|progress
decl_stmt|;
DECL|method|SerializedApplicationReport ()
specifier|public
name|SerializedApplicationReport
parameter_list|()
block|{   }
DECL|method|SerializedApplicationReport (ApplicationReport report)
specifier|public
name|SerializedApplicationReport
parameter_list|(
name|ApplicationReport
name|report
parameter_list|)
block|{
name|this
operator|.
name|applicationId
operator|=
name|report
operator|.
name|getApplicationId
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|ApplicationAttemptId
name|attemptId
init|=
name|report
operator|.
name|getCurrentApplicationAttemptId
argument_list|()
decl_stmt|;
name|this
operator|.
name|applicationAttemptId
operator|=
name|attemptId
operator|!=
literal|null
condition|?
name|attemptId
operator|.
name|toString
argument_list|()
else|:
literal|"N/A"
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|report
operator|.
name|getName
argument_list|()
expr_stmt|;
name|this
operator|.
name|applicationType
operator|=
name|report
operator|.
name|getApplicationType
argument_list|()
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|report
operator|.
name|getUser
argument_list|()
expr_stmt|;
name|this
operator|.
name|queue
operator|=
name|report
operator|.
name|getQueue
argument_list|()
expr_stmt|;
name|this
operator|.
name|host
operator|=
name|report
operator|.
name|getHost
argument_list|()
expr_stmt|;
name|this
operator|.
name|rpcPort
operator|=
name|report
operator|.
name|getRpcPort
argument_list|()
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|report
operator|.
name|getYarnApplicationState
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|this
operator|.
name|diagnostics
operator|=
name|report
operator|.
name|getDiagnostics
argument_list|()
expr_stmt|;
name|this
operator|.
name|startTime
operator|=
name|report
operator|.
name|getStartTime
argument_list|()
expr_stmt|;
name|this
operator|.
name|finishTime
operator|=
name|report
operator|.
name|getFinishTime
argument_list|()
expr_stmt|;
name|FinalApplicationStatus
name|appStatus
init|=
name|report
operator|.
name|getFinalApplicationStatus
argument_list|()
decl_stmt|;
name|this
operator|.
name|finalStatus
operator|=
name|appStatus
operator|==
literal|null
condition|?
literal|""
else|:
name|appStatus
operator|.
name|toString
argument_list|()
expr_stmt|;
name|this
operator|.
name|progress
operator|=
name|report
operator|.
name|getProgress
argument_list|()
expr_stmt|;
name|this
operator|.
name|url
operator|=
name|report
operator|.
name|getTrackingUrl
argument_list|()
expr_stmt|;
name|this
operator|.
name|origTrackingUrl
operator|=
name|report
operator|.
name|getOriginalTrackingUrl
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
try|try
block|{
return|return
name|ApplicationReportSerDeser
operator|.
name|toString
argument_list|(
name|this
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
name|super
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

