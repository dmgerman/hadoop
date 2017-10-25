begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.resourceestimator.skylinestore.api
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|resourceestimator
operator|.
name|skylinestore
operator|.
name|api
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
name|resourceestimator
operator|.
name|skylinestore
operator|.
name|exceptions
operator|.
name|SkylineStoreException
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
name|reservation
operator|.
name|RLESparseResourceAllocation
import|;
end_import

begin_comment
comment|/**  * PredictionSkylineStore stores the predicted  * {@code RLESparseResourceAllocation} of a job as computed by the  * {@code Estimator} based on the {@code ResourceSkyline}s of past executions in  * the {@code HistorySkylineStore}.  */
end_comment

begin_interface
DECL|interface|PredictionSkylineStore
specifier|public
interface|interface
name|PredictionSkylineStore
block|{
comment|/**    * Add job's predicted {@code Resource} allocation to the<em>store</em>    * indexed by the {@code    * pipelineId}.    *<p> Note that right now we only keep the latest copy of predicted    * {@code Resource} allocation for the recurring pipeline.    *    * @param pipelineId       the id of the recurring pipeline.    * @param resourceOverTime the predicted {@code Resource} allocation for the    *                         pipeline.    * @throws SkylineStoreException if input parameters are invalid.    */
DECL|method|addEstimation (String pipelineId, RLESparseResourceAllocation resourceOverTime)
name|void
name|addEstimation
parameter_list|(
name|String
name|pipelineId
parameter_list|,
name|RLESparseResourceAllocation
name|resourceOverTime
parameter_list|)
throws|throws
name|SkylineStoreException
function_decl|;
comment|/**    * Return the predicted {@code Resource} allocation for the pipeline.    *<p> If the pipelineId does not exist, it will return<em>null</em>.    *    * @param pipelineId the unique id of the pipeline.    * @return the predicted {@code Resource} allocation for the pipeline.    * @throws SkylineStoreException if pipelineId is<em>null</em>.    */
DECL|method|getEstimation (String pipelineId)
name|RLESparseResourceAllocation
name|getEstimation
parameter_list|(
name|String
name|pipelineId
parameter_list|)
throws|throws
name|SkylineStoreException
function_decl|;
block|}
end_interface

end_unit

