begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.resourceestimator.common.api
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|resourceestimator
operator|.
name|common
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|Resource
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
comment|/**  * ResourceSkyline records the job identification information as well as job's  * requested {@code  * container}s information during its lifespan.  */
end_comment

begin_class
DECL|class|ResourceSkyline
specifier|public
class|class
name|ResourceSkyline
block|{
comment|/**    * The auto-generated {@code ApplicationId} in job's one run.    *<p>    *<p>    * For a pipeline job, we assume the {@code jobId} changes each time we run    * the pipeline job.    */
DECL|field|jobId
specifier|private
name|String
name|jobId
decl_stmt|;
comment|/**    * The input data size of the job.    */
DECL|field|jobInputDataSize
specifier|private
name|double
name|jobInputDataSize
decl_stmt|;
comment|/**    * Job submission time. Different logs could have different time format, so we    * store the original string directly extracted from logs.    */
DECL|field|jobSubmissionTime
specifier|private
name|long
name|jobSubmissionTime
decl_stmt|;
comment|/**    * Job finish time. Different logs could have different time format, so we    * store the original string directly extracted from logs.    */
DECL|field|jobFinishTime
specifier|private
name|long
name|jobFinishTime
decl_stmt|;
comment|/**    * The resource spec of containers allocated to the job.    */
DECL|field|containerSpec
specifier|private
name|Resource
name|containerSpec
decl_stmt|;
comment|/**    * The list of {@link Resource} allocated to the job in its lifespan.    */
DECL|field|skylineList
specifier|private
name|RLESparseResourceAllocation
name|skylineList
decl_stmt|;
comment|// TODO
comment|// We plan to record pipeline job's actual resource consumptions in one run
comment|// here.
comment|// TODO
comment|// We might need to addHistory more features to the ResourceSkyline, such as
comment|// users, VC, etc.
comment|/**    * Constructor.    *    * @param jobIdConfig             the id of the job.    * @param jobInputDataSizeConfig  the input data size of the job.    * @param jobSubmissionTimeConfig the submission time of the job.    * @param jobFinishTimeConfig     the finish time of the job.    * @param containerSpecConfig     the resource spec of containers allocated    *                                to the job.    * @param skylineListConfig       the list of {@link Resource} allocated in    *                                one run.    */
DECL|method|ResourceSkyline (final String jobIdConfig, final double jobInputDataSizeConfig, final long jobSubmissionTimeConfig, final long jobFinishTimeConfig, final Resource containerSpecConfig, final RLESparseResourceAllocation skylineListConfig)
specifier|public
name|ResourceSkyline
parameter_list|(
specifier|final
name|String
name|jobIdConfig
parameter_list|,
specifier|final
name|double
name|jobInputDataSizeConfig
parameter_list|,
specifier|final
name|long
name|jobSubmissionTimeConfig
parameter_list|,
specifier|final
name|long
name|jobFinishTimeConfig
parameter_list|,
specifier|final
name|Resource
name|containerSpecConfig
parameter_list|,
specifier|final
name|RLESparseResourceAllocation
name|skylineListConfig
parameter_list|)
block|{
name|this
operator|.
name|jobId
operator|=
name|jobIdConfig
expr_stmt|;
name|this
operator|.
name|jobInputDataSize
operator|=
name|jobInputDataSizeConfig
expr_stmt|;
name|this
operator|.
name|jobSubmissionTime
operator|=
name|jobSubmissionTimeConfig
expr_stmt|;
name|this
operator|.
name|jobFinishTime
operator|=
name|jobFinishTimeConfig
expr_stmt|;
name|this
operator|.
name|containerSpec
operator|=
name|containerSpecConfig
expr_stmt|;
name|this
operator|.
name|skylineList
operator|=
name|skylineListConfig
expr_stmt|;
block|}
comment|/**    * Empty constructor.    */
DECL|method|ResourceSkyline ()
specifier|public
name|ResourceSkyline
parameter_list|()
block|{   }
comment|/**    * Get the id of the job.    *    * @return the id of this job.    */
DECL|method|getJobId ()
specifier|public
specifier|final
name|String
name|getJobId
parameter_list|()
block|{
return|return
name|jobId
return|;
block|}
comment|/**    * Set jobId.    *    * @param jobIdConfig jobId.    */
DECL|method|setJobId (final String jobIdConfig)
specifier|public
specifier|final
name|void
name|setJobId
parameter_list|(
specifier|final
name|String
name|jobIdConfig
parameter_list|)
block|{
name|this
operator|.
name|jobId
operator|=
name|jobIdConfig
expr_stmt|;
block|}
comment|/**    * Get the job's input data size.    *    * @return job's input data size.    */
DECL|method|getJobInputDataSize ()
specifier|public
specifier|final
name|double
name|getJobInputDataSize
parameter_list|()
block|{
return|return
name|jobInputDataSize
return|;
block|}
comment|/**    * Set jobInputDataSize.    *    * @param jobInputDataSizeConfig jobInputDataSize.    */
DECL|method|setJobInputDataSize (final double jobInputDataSizeConfig)
specifier|public
specifier|final
name|void
name|setJobInputDataSize
parameter_list|(
specifier|final
name|double
name|jobInputDataSizeConfig
parameter_list|)
block|{
name|this
operator|.
name|jobInputDataSize
operator|=
name|jobInputDataSizeConfig
expr_stmt|;
block|}
comment|/**    * Get the job's submission time.    *    * @return job's submission time.    */
DECL|method|getJobSubmissionTime ()
specifier|public
specifier|final
name|long
name|getJobSubmissionTime
parameter_list|()
block|{
return|return
name|jobSubmissionTime
return|;
block|}
comment|/**    * Set jobSubmissionTime.    *    * @param jobSubmissionTimeConfig jobSubmissionTime.    */
DECL|method|setJobSubmissionTime (final long jobSubmissionTimeConfig)
specifier|public
specifier|final
name|void
name|setJobSubmissionTime
parameter_list|(
specifier|final
name|long
name|jobSubmissionTimeConfig
parameter_list|)
block|{
name|this
operator|.
name|jobSubmissionTime
operator|=
name|jobSubmissionTimeConfig
expr_stmt|;
block|}
comment|/**    * Get the job's finish time.    *    * @return job's finish time.    */
DECL|method|getJobFinishTime ()
specifier|public
specifier|final
name|long
name|getJobFinishTime
parameter_list|()
block|{
return|return
name|jobFinishTime
return|;
block|}
comment|/**    * Set jobFinishTime.    *    * @param jobFinishTimeConfig jobFinishTime.    */
DECL|method|setJobFinishTime (final long jobFinishTimeConfig)
specifier|public
specifier|final
name|void
name|setJobFinishTime
parameter_list|(
specifier|final
name|long
name|jobFinishTimeConfig
parameter_list|)
block|{
name|this
operator|.
name|jobFinishTime
operator|=
name|jobFinishTimeConfig
expr_stmt|;
block|}
comment|/**    * Get the resource spec of the job's allocated {@code container}s.    *<p> Key assumption: during job's lifespan, its allocated {@code container}s    * have the same {@link Resource} spec.    *    * @return the {@link Resource} spec of the job's allocated    * {@code container}s.    */
DECL|method|getContainerSpec ()
specifier|public
specifier|final
name|Resource
name|getContainerSpec
parameter_list|()
block|{
return|return
name|containerSpec
return|;
block|}
comment|/**    * Set containerSpec.    *    * @param containerSpecConfig containerSpec.    */
DECL|method|setContainerSpec (final Resource containerSpecConfig)
specifier|public
specifier|final
name|void
name|setContainerSpec
parameter_list|(
specifier|final
name|Resource
name|containerSpecConfig
parameter_list|)
block|{
name|this
operator|.
name|containerSpec
operator|=
name|containerSpecConfig
expr_stmt|;
block|}
comment|/**    * Get the list of {@link Resource}s allocated to the job.    *    * @return the {@link RLESparseResourceAllocation} which contains the list of    * {@link Resource}s allocated to the job.    */
DECL|method|getSkylineList ()
specifier|public
specifier|final
name|RLESparseResourceAllocation
name|getSkylineList
parameter_list|()
block|{
return|return
name|skylineList
return|;
block|}
comment|/**    * Set skylineList.    *    * @param skylineListConfig skylineList.    */
DECL|method|setSkylineList ( final RLESparseResourceAllocation skylineListConfig)
specifier|public
specifier|final
name|void
name|setSkylineList
parameter_list|(
specifier|final
name|RLESparseResourceAllocation
name|skylineListConfig
parameter_list|)
block|{
name|this
operator|.
name|skylineList
operator|=
name|skylineListConfig
expr_stmt|;
block|}
block|}
end_class

end_unit

