begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.ksm
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|ksm
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
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
comment|/**  * This class is for maintaining KeySpaceManager statistics.  */
end_comment

begin_class
DECL|class|KSMMetrics
specifier|public
class|class
name|KSMMetrics
block|{
comment|// KSM op metrics
DECL|field|numVolumeCreates
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numVolumeCreates
decl_stmt|;
comment|// Failure Metrics
DECL|field|numVolumeCreateFails
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numVolumeCreateFails
decl_stmt|;
DECL|method|KSMMetrics ()
specifier|public
name|KSMMetrics
parameter_list|()
block|{   }
DECL|method|create ()
specifier|public
specifier|static
name|KSMMetrics
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
literal|"KSMMetrics"
argument_list|,
literal|"Key Space Manager Metrics"
argument_list|,
operator|new
name|KSMMetrics
argument_list|()
argument_list|)
return|;
block|}
DECL|method|incNumVolumeCreates ()
specifier|public
name|void
name|incNumVolumeCreates
parameter_list|()
block|{
name|numVolumeCreates
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumVolumeCreateFails ()
specifier|public
name|void
name|incNumVolumeCreateFails
parameter_list|()
block|{
name|numVolumeCreates
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumVolumeCreates ()
specifier|public
name|long
name|getNumVolumeCreates
parameter_list|()
block|{
return|return
name|numVolumeCreates
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumVolumeCreateFails ()
specifier|public
name|long
name|getNumVolumeCreateFails
parameter_list|()
block|{
return|return
name|numVolumeCreateFails
operator|.
name|value
argument_list|()
return|;
block|}
block|}
end_class

end_unit

