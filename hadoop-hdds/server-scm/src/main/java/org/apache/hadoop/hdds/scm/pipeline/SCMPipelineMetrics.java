begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.pipeline
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|pipeline
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
name|classification
operator|.
name|InterfaceAudience
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
name|metrics2
operator|.
name|MetricsSystem
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
name|metrics2
operator|.
name|annotation
operator|.
name|Metric
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
name|metrics2
operator|.
name|annotation
operator|.
name|Metrics
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
name|metrics2
operator|.
name|lib
operator|.
name|DefaultMetricsSystem
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
name|metrics2
operator|.
name|lib
operator|.
name|MutableCounterLong
import|;
end_import

begin_comment
comment|/**  * This class maintains Pipeline related metrics.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|Metrics
argument_list|(
name|about
operator|=
literal|"SCM PipelineManager Metrics"
argument_list|,
name|context
operator|=
literal|"ozone"
argument_list|)
DECL|class|SCMPipelineMetrics
specifier|public
specifier|final
class|class
name|SCMPipelineMetrics
block|{
DECL|field|SOURCE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|SOURCE_NAME
init|=
name|SCMPipelineMetrics
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
DECL|field|numPipelineCreated
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numPipelineCreated
decl_stmt|;
DECL|field|numPipelineCreationFailed
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numPipelineCreationFailed
decl_stmt|;
DECL|field|numPipelineDestroyed
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numPipelineDestroyed
decl_stmt|;
DECL|field|numPipelineDestroyFailed
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numPipelineDestroyFailed
decl_stmt|;
DECL|field|numPipelineReportProcessed
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numPipelineReportProcessed
decl_stmt|;
DECL|field|numPipelineReportProcessingFailed
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numPipelineReportProcessingFailed
decl_stmt|;
comment|/** Private constructor. */
DECL|method|SCMPipelineMetrics ()
specifier|private
name|SCMPipelineMetrics
parameter_list|()
block|{ }
comment|/**    * Create and returns SCMPipelineMetrics instance.    *    * @return SCMPipelineMetrics    */
DECL|method|create ()
specifier|public
specifier|static
name|SCMPipelineMetrics
name|create
parameter_list|()
block|{
name|MetricsSystem
name|ms
init|=
name|DefaultMetricsSystem
operator|.
name|instance
argument_list|()
decl_stmt|;
return|return
name|ms
operator|.
name|register
argument_list|(
name|SOURCE_NAME
argument_list|,
literal|"SCM PipelineManager Metrics"
argument_list|,
operator|new
name|SCMPipelineMetrics
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Unregister the metrics instance.    */
DECL|method|unRegister ()
specifier|public
name|void
name|unRegister
parameter_list|()
block|{
name|MetricsSystem
name|ms
init|=
name|DefaultMetricsSystem
operator|.
name|instance
argument_list|()
decl_stmt|;
name|ms
operator|.
name|unregisterSource
argument_list|(
name|SOURCE_NAME
argument_list|)
expr_stmt|;
block|}
comment|/**    * Increments number of successful pipeline creation count.    */
DECL|method|incNumPipelineCreated ()
name|void
name|incNumPipelineCreated
parameter_list|()
block|{
name|numPipelineCreated
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
comment|/**    * Increments number of failed pipeline creation count.    */
DECL|method|incNumPipelineCreationFailed ()
name|void
name|incNumPipelineCreationFailed
parameter_list|()
block|{
name|numPipelineCreationFailed
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
comment|/**    * Increments number of successful pipeline destroy count.    */
DECL|method|incNumPipelineDestroyed ()
name|void
name|incNumPipelineDestroyed
parameter_list|()
block|{
name|numPipelineDestroyed
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
comment|/**    * Increments number of failed pipeline destroy count.    */
DECL|method|incNumPipelineDestroyFailed ()
name|void
name|incNumPipelineDestroyFailed
parameter_list|()
block|{
name|numPipelineDestroyFailed
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
comment|/**    * Increments number of pipeline report processed count.    */
DECL|method|incNumPipelineReportProcessed ()
name|void
name|incNumPipelineReportProcessed
parameter_list|()
block|{
name|numPipelineReportProcessed
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
comment|/**    * Increments number of pipeline report processing failed count.    */
DECL|method|incNumPipelineReportProcessingFailed ()
name|void
name|incNumPipelineReportProcessingFailed
parameter_list|()
block|{
name|numPipelineReportProcessingFailed
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

