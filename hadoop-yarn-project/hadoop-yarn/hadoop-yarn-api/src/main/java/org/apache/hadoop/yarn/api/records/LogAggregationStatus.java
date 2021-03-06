begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records
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
name|records
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
name|conf
operator|.
name|YarnConfiguration
import|;
end_import

begin_comment
comment|/**  *<p>Status of Log aggregation.</p>  */
end_comment

begin_enum
DECL|enum|LogAggregationStatus
specifier|public
enum|enum
name|LogAggregationStatus
block|{
comment|/** Log Aggregation is Disabled. */
DECL|enumConstant|DISABLED
name|DISABLED
block|,
comment|/** Log Aggregation does not Start. */
DECL|enumConstant|NOT_START
name|NOT_START
block|,
comment|/** Log Aggregation is Running. */
DECL|enumConstant|RUNNING
name|RUNNING
block|,
comment|/** Log Aggregation is Running, but has failures in previous cycles. */
DECL|enumConstant|RUNNING_WITH_FAILURE
name|RUNNING_WITH_FAILURE
block|,
comment|/**    * Log Aggregation is Succeeded. All of the logs have been aggregated    * successfully.    */
DECL|enumConstant|SUCCEEDED
name|SUCCEEDED
block|,
comment|/**    * Log Aggregation is completed. But at least one of the logs have not been    * aggregated.    */
DECL|enumConstant|FAILED
name|FAILED
block|,
comment|/**    * The application is finished, but the log aggregation status is not updated    * for a long time.     * @see YarnConfiguration#LOG_AGGREGATION_STATUS_TIME_OUT_MS    */
DECL|enumConstant|TIME_OUT
name|TIME_OUT
block|}
end_enum

end_unit

