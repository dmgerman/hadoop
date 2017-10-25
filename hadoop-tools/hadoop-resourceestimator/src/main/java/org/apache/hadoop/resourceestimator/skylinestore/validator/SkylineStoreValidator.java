begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.resourceestimator.skylinestore.validator
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
name|validator
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|resourceestimator
operator|.
name|common
operator|.
name|api
operator|.
name|RecurrenceId
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
name|resourceestimator
operator|.
name|common
operator|.
name|api
operator|.
name|ResourceSkyline
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
name|resourceestimator
operator|.
name|skylinestore
operator|.
name|api
operator|.
name|SkylineStore
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
name|resourceestimator
operator|.
name|skylinestore
operator|.
name|exceptions
operator|.
name|NullPipelineIdException
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
name|resourceestimator
operator|.
name|skylinestore
operator|.
name|exceptions
operator|.
name|NullRLESparseResourceAllocationException
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
name|resourceestimator
operator|.
name|skylinestore
operator|.
name|exceptions
operator|.
name|NullRecurrenceIdException
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
name|resourceestimator
operator|.
name|skylinestore
operator|.
name|exceptions
operator|.
name|NullResourceSkylineException
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

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * SkylineStoreValidator validates input parameters for {@link SkylineStore}.  */
end_comment

begin_class
DECL|class|SkylineStoreValidator
specifier|public
class|class
name|SkylineStoreValidator
block|{
DECL|field|LOGGER
specifier|private
specifier|static
specifier|final
name|Logger
name|LOGGER
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SkylineStoreValidator
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Check if recurrenceId is<em>null</em>.    *    * @param recurrenceId the id of the recurring pipeline job.    * @throws SkylineStoreException if input parameters are invalid.    */
DECL|method|validate (final RecurrenceId recurrenceId)
specifier|public
specifier|final
name|void
name|validate
parameter_list|(
specifier|final
name|RecurrenceId
name|recurrenceId
parameter_list|)
throws|throws
name|SkylineStoreException
block|{
if|if
condition|(
name|recurrenceId
operator|==
literal|null
condition|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"Recurrence id is null, please try again by specifying"
operator|+
literal|" a valid Recurrence id."
argument_list|)
expr_stmt|;
name|LOGGER
operator|.
name|error
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|NullRecurrenceIdException
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**    * Check if pipelineId is<em>null</em>.    *    * @param pipelineId the id of the recurring pipeline job.    * @throws SkylineStoreException if input parameters are invalid.    */
DECL|method|validate (final String pipelineId)
specifier|public
specifier|final
name|void
name|validate
parameter_list|(
specifier|final
name|String
name|pipelineId
parameter_list|)
throws|throws
name|SkylineStoreException
block|{
if|if
condition|(
name|pipelineId
operator|==
literal|null
condition|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"pipelineId is null, please try again by specifying"
operator|+
literal|" a valid pipelineId."
argument_list|)
expr_stmt|;
name|LOGGER
operator|.
name|error
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|NullPipelineIdException
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**    * Check if recurrenceId is<em>null</em> or resourceSkylines is    *<em>null</em>.    *    * @param recurrenceId     the id of the recurring pipeline job.    * @param resourceSkylines the list of {@link ResourceSkyline}s to be added.    * @throws SkylineStoreException if input parameters are invalid.    */
DECL|method|validate (final RecurrenceId recurrenceId, final List<ResourceSkyline> resourceSkylines)
specifier|public
specifier|final
name|void
name|validate
parameter_list|(
specifier|final
name|RecurrenceId
name|recurrenceId
parameter_list|,
specifier|final
name|List
argument_list|<
name|ResourceSkyline
argument_list|>
name|resourceSkylines
parameter_list|)
throws|throws
name|SkylineStoreException
block|{
name|validate
argument_list|(
name|recurrenceId
argument_list|)
expr_stmt|;
if|if
condition|(
name|resourceSkylines
operator|==
literal|null
condition|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"ResourceSkylines for "
operator|+
name|recurrenceId
operator|+
literal|" is null, please try again by "
operator|+
literal|"specifying valid ResourceSkylines."
argument_list|)
expr_stmt|;
name|LOGGER
operator|.
name|error
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|NullResourceSkylineException
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**    * Check if pipelineId is<em>null</em> or resourceOverTime is<em>null</em>.    *    * @param pipelineId       the id of the recurring pipeline.    * @param resourceOverTime predicted {@code Resource} allocation to be added.    * @throws SkylineStoreException if input parameters are invalid.    */
DECL|method|validate (final String pipelineId, final RLESparseResourceAllocation resourceOverTime)
specifier|public
specifier|final
name|void
name|validate
parameter_list|(
specifier|final
name|String
name|pipelineId
parameter_list|,
specifier|final
name|RLESparseResourceAllocation
name|resourceOverTime
parameter_list|)
throws|throws
name|SkylineStoreException
block|{
name|validate
argument_list|(
name|pipelineId
argument_list|)
expr_stmt|;
if|if
condition|(
name|resourceOverTime
operator|==
literal|null
condition|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"Resource allocation for "
operator|+
name|pipelineId
operator|+
literal|" is null."
argument_list|)
expr_stmt|;
name|LOGGER
operator|.
name|error
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|NullRLESparseResourceAllocationException
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

