begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.node
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
name|node
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
comment|/**  * This class maintains Node related metrics.  */
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
literal|"SCM NodeManager Metrics"
argument_list|,
name|context
operator|=
literal|"ozone"
argument_list|)
DECL|class|SCMNodeMetrics
specifier|public
specifier|final
class|class
name|SCMNodeMetrics
block|{
DECL|field|SOURCE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|SOURCE_NAME
init|=
name|SCMNodeMetrics
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
DECL|field|numHBProcessed
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numHBProcessed
decl_stmt|;
DECL|field|numHBProcessingFailed
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numHBProcessingFailed
decl_stmt|;
DECL|field|numNodeReportProcessed
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numNodeReportProcessed
decl_stmt|;
DECL|field|numNodeReportProcessingFailed
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numNodeReportProcessingFailed
decl_stmt|;
comment|/** Private constructor. */
DECL|method|SCMNodeMetrics ()
specifier|private
name|SCMNodeMetrics
parameter_list|()
block|{ }
comment|/**    * Create and returns SCMNodeMetrics instance.    *    * @return SCMNodeMetrics    */
DECL|method|create ()
specifier|public
specifier|static
name|SCMNodeMetrics
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
literal|"SCM NodeManager Metrics"
argument_list|,
operator|new
name|SCMNodeMetrics
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
comment|/**    * Increments number of heartbeat processed count.    */
DECL|method|incNumHBProcessed ()
name|void
name|incNumHBProcessed
parameter_list|()
block|{
name|numHBProcessed
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
comment|/**    * Increments number of heartbeat processing failed count.    */
DECL|method|incNumHBProcessingFailed ()
name|void
name|incNumHBProcessingFailed
parameter_list|()
block|{
name|numHBProcessingFailed
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
comment|/**    * Increments number of node report processed count.    */
DECL|method|incNumNodeReportProcessed ()
name|void
name|incNumNodeReportProcessed
parameter_list|()
block|{
name|numNodeReportProcessed
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
comment|/**    * Increments number of node report processing failed count.    */
DECL|method|incNumNodeReportProcessingFailed ()
name|void
name|incNumNodeReportProcessingFailed
parameter_list|()
block|{
name|numNodeReportProcessingFailed
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

