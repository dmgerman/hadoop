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
DECL|field|numVolumeModifies
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numVolumeModifies
decl_stmt|;
DECL|field|numVolumeInfos
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numVolumeInfos
decl_stmt|;
DECL|field|numBucketCreates
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numBucketCreates
decl_stmt|;
DECL|field|numVolumeDeletes
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numVolumeDeletes
decl_stmt|;
DECL|field|numBucketInfos
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numBucketInfos
decl_stmt|;
DECL|field|numBucketModifies
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numBucketModifies
decl_stmt|;
DECL|field|numKeyAllocate
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numKeyAllocate
decl_stmt|;
DECL|field|numKeyLookup
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numKeyLookup
decl_stmt|;
comment|// Failure Metrics
DECL|field|numVolumeCreateFails
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numVolumeCreateFails
decl_stmt|;
DECL|field|numVolumeModifyFails
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numVolumeModifyFails
decl_stmt|;
DECL|field|numVolumeInfoFails
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numVolumeInfoFails
decl_stmt|;
DECL|field|numVolumeDeleteFails
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numVolumeDeleteFails
decl_stmt|;
DECL|field|numBucketCreateFails
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numBucketCreateFails
decl_stmt|;
DECL|field|numBucketInfoFails
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numBucketInfoFails
decl_stmt|;
DECL|field|numBucketModifyFails
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numBucketModifyFails
decl_stmt|;
DECL|field|numKeyAllocateFails
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numKeyAllocateFails
decl_stmt|;
DECL|field|numKeyLookupFails
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numKeyLookupFails
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
DECL|method|incNumVolumeModifies ()
specifier|public
name|void
name|incNumVolumeModifies
parameter_list|()
block|{
name|numVolumeModifies
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumVolumeInfos ()
specifier|public
name|void
name|incNumVolumeInfos
parameter_list|()
block|{
name|numVolumeInfos
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumVolumeDeletes ()
specifier|public
name|void
name|incNumVolumeDeletes
parameter_list|()
block|{
name|numVolumeDeletes
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumBucketCreates ()
specifier|public
name|void
name|incNumBucketCreates
parameter_list|()
block|{
name|numBucketCreates
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumBucketInfos ()
specifier|public
name|void
name|incNumBucketInfos
parameter_list|()
block|{
name|numBucketInfos
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumBucketModifies ()
specifier|public
name|void
name|incNumBucketModifies
parameter_list|()
block|{
name|numBucketModifies
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
name|numVolumeCreateFails
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumVolumeModifyFails ()
specifier|public
name|void
name|incNumVolumeModifyFails
parameter_list|()
block|{
name|numVolumeModifyFails
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumVolumeInfoFails ()
specifier|public
name|void
name|incNumVolumeInfoFails
parameter_list|()
block|{
name|numVolumeInfoFails
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumVolumeDeleteFails ()
specifier|public
name|void
name|incNumVolumeDeleteFails
parameter_list|()
block|{
name|numVolumeDeleteFails
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumBucketCreateFails ()
specifier|public
name|void
name|incNumBucketCreateFails
parameter_list|()
block|{
name|numBucketCreateFails
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumBucketInfoFails ()
specifier|public
name|void
name|incNumBucketInfoFails
parameter_list|()
block|{
name|numBucketInfoFails
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumBucketModifyFails ()
specifier|public
name|void
name|incNumBucketModifyFails
parameter_list|()
block|{
name|numBucketModifyFails
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumKeyAllocates ()
specifier|public
name|void
name|incNumKeyAllocates
parameter_list|()
block|{
name|numKeyAllocate
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumKeyAllocateFails ()
specifier|public
name|void
name|incNumKeyAllocateFails
parameter_list|()
block|{
name|numKeyAllocateFails
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumKeyLookups ()
specifier|public
name|void
name|incNumKeyLookups
parameter_list|()
block|{
name|numKeyLookup
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumKeyLookupFails ()
specifier|public
name|void
name|incNumKeyLookupFails
parameter_list|()
block|{
name|numKeyLookupFails
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
DECL|method|getNumVolumeModifies ()
specifier|public
name|long
name|getNumVolumeModifies
parameter_list|()
block|{
return|return
name|numVolumeModifies
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumVolumeInfos ()
specifier|public
name|long
name|getNumVolumeInfos
parameter_list|()
block|{
return|return
name|numVolumeInfos
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumVolumeDeletes ()
specifier|public
name|long
name|getNumVolumeDeletes
parameter_list|()
block|{
return|return
name|numVolumeDeletes
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumBucketCreates ()
specifier|public
name|long
name|getNumBucketCreates
parameter_list|()
block|{
return|return
name|numBucketCreates
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumBucketInfos ()
specifier|public
name|long
name|getNumBucketInfos
parameter_list|()
block|{
return|return
name|numBucketInfos
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumBucketModifies ()
specifier|public
name|long
name|getNumBucketModifies
parameter_list|()
block|{
return|return
name|numBucketModifies
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
annotation|@
name|VisibleForTesting
DECL|method|getNumVolumeModifyFails ()
specifier|public
name|long
name|getNumVolumeModifyFails
parameter_list|()
block|{
return|return
name|numVolumeModifyFails
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumVolumeInfoFails ()
specifier|public
name|long
name|getNumVolumeInfoFails
parameter_list|()
block|{
return|return
name|numVolumeInfoFails
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumVolumeDeleteFails ()
specifier|public
name|long
name|getNumVolumeDeleteFails
parameter_list|()
block|{
return|return
name|numVolumeDeleteFails
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumBucketCreateFails ()
specifier|public
name|long
name|getNumBucketCreateFails
parameter_list|()
block|{
return|return
name|numBucketCreateFails
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumBucketInfoFails ()
specifier|public
name|long
name|getNumBucketInfoFails
parameter_list|()
block|{
return|return
name|numBucketInfoFails
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumBucketModifyFails ()
specifier|public
name|long
name|getNumBucketModifyFails
parameter_list|()
block|{
return|return
name|numBucketModifyFails
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumKeyAllocates ()
specifier|public
name|long
name|getNumKeyAllocates
parameter_list|()
block|{
return|return
name|numKeyAllocate
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumKeyAllocateFails ()
specifier|public
name|long
name|getNumKeyAllocateFails
parameter_list|()
block|{
return|return
name|numKeyAllocateFails
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumKeyLookups ()
specifier|public
name|long
name|getNumKeyLookups
parameter_list|()
block|{
return|return
name|numKeyLookup
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumKeyLookupFails ()
specifier|public
name|long
name|getNumKeyLookupFails
parameter_list|()
block|{
return|return
name|numKeyLookupFails
operator|.
name|value
argument_list|()
return|;
block|}
block|}
end_class

end_unit

