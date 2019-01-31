begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
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
comment|/**  * This class is for maintaining Ozone Manager statistics.  */
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
literal|"Ozone Manager Metrics"
argument_list|,
name|context
operator|=
literal|"dfs"
argument_list|)
DECL|class|OMMetrics
specifier|public
class|class
name|OMMetrics
block|{
DECL|field|SOURCE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|SOURCE_NAME
init|=
name|OMMetrics
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
comment|// OM request type op metrics
DECL|field|numVolumeOps
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numVolumeOps
decl_stmt|;
DECL|field|numBucketOps
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numBucketOps
decl_stmt|;
DECL|field|numKeyOps
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numKeyOps
decl_stmt|;
comment|// OM op metrics
DECL|field|numVolumeCreates
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numVolumeCreates
decl_stmt|;
DECL|field|numVolumeUpdates
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numVolumeUpdates
decl_stmt|;
DECL|field|numVolumeInfos
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numVolumeInfos
decl_stmt|;
DECL|field|numVolumeCheckAccesses
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numVolumeCheckAccesses
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
DECL|field|numBucketUpdates
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numBucketUpdates
decl_stmt|;
DECL|field|numBucketDeletes
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numBucketDeletes
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
DECL|field|numKeyRenames
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numKeyRenames
decl_stmt|;
DECL|field|numKeyDeletes
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numKeyDeletes
decl_stmt|;
DECL|field|numBucketLists
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numBucketLists
decl_stmt|;
DECL|field|numKeyLists
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numKeyLists
decl_stmt|;
DECL|field|numVolumeLists
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numVolumeLists
decl_stmt|;
DECL|field|numKeyCommits
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numKeyCommits
decl_stmt|;
DECL|field|numAllocateBlockCalls
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numAllocateBlockCalls
decl_stmt|;
DECL|field|numGetServiceLists
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numGetServiceLists
decl_stmt|;
DECL|field|numListS3Buckets
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numListS3Buckets
decl_stmt|;
DECL|field|numInitiateMultipartUploads
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numInitiateMultipartUploads
decl_stmt|;
DECL|field|numCompleteMultipartUploads
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numCompleteMultipartUploads
decl_stmt|;
comment|// Failure Metrics
DECL|field|numVolumeCreateFails
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numVolumeCreateFails
decl_stmt|;
DECL|field|numVolumeUpdateFails
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numVolumeUpdateFails
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
DECL|field|numVolumeCheckAccessFails
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numVolumeCheckAccessFails
decl_stmt|;
DECL|field|numBucketInfoFails
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numBucketInfoFails
decl_stmt|;
DECL|field|numBucketUpdateFails
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numBucketUpdateFails
decl_stmt|;
DECL|field|numBucketDeleteFails
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numBucketDeleteFails
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
DECL|field|numKeyRenameFails
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numKeyRenameFails
decl_stmt|;
DECL|field|numKeyDeleteFails
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numKeyDeleteFails
decl_stmt|;
DECL|field|numBucketListFails
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numBucketListFails
decl_stmt|;
DECL|field|numKeyListFails
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numKeyListFails
decl_stmt|;
DECL|field|numVolumeListFails
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numVolumeListFails
decl_stmt|;
DECL|field|numKeyCommitFails
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numKeyCommitFails
decl_stmt|;
DECL|field|numBlockAllocateCallFails
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numBlockAllocateCallFails
decl_stmt|;
DECL|field|numGetServiceListFails
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numGetServiceListFails
decl_stmt|;
DECL|field|numListS3BucketsFails
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numListS3BucketsFails
decl_stmt|;
DECL|field|numInitiateMultipartUploadFails
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numInitiateMultipartUploadFails
decl_stmt|;
DECL|field|numCommitMultipartUploadParts
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numCommitMultipartUploadParts
decl_stmt|;
DECL|field|getNumCommitMultipartUploadPartFails
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|getNumCommitMultipartUploadPartFails
decl_stmt|;
DECL|field|numCompleteMultipartUploadFails
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numCompleteMultipartUploadFails
decl_stmt|;
DECL|field|numAbortMultipartUploads
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numAbortMultipartUploads
decl_stmt|;
DECL|field|numAbortMultipartUploadFails
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numAbortMultipartUploadFails
decl_stmt|;
DECL|field|numListMultipartUploadParts
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numListMultipartUploadParts
decl_stmt|;
DECL|field|numListMultipartUploadPartFails
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numListMultipartUploadPartFails
decl_stmt|;
comment|// Metrics for total number of volumes, buckets and keys
DECL|field|numVolumes
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numVolumes
decl_stmt|;
DECL|field|numBuckets
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numBuckets
decl_stmt|;
comment|//TODO: This metric is an estimate and it may be inaccurate on restart if the
comment|// OM process was not shutdown cleanly. Key creations/deletions in the last
comment|// few minutes before restart may not be included in this count.
DECL|field|numKeys
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numKeys
decl_stmt|;
DECL|method|OMMetrics ()
specifier|public
name|OMMetrics
parameter_list|()
block|{   }
DECL|method|create ()
specifier|public
specifier|static
name|OMMetrics
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
literal|"Ozone Manager Metrics"
argument_list|,
operator|new
name|OMMetrics
argument_list|()
argument_list|)
return|;
block|}
DECL|method|incNumVolumes ()
specifier|public
name|void
name|incNumVolumes
parameter_list|()
block|{
name|numVolumes
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|decNumVolumes ()
specifier|public
name|void
name|decNumVolumes
parameter_list|()
block|{
name|numVolumes
operator|.
name|incr
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|incNumBuckets ()
specifier|public
name|void
name|incNumBuckets
parameter_list|()
block|{
name|numBuckets
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|decNumBuckets ()
specifier|public
name|void
name|decNumBuckets
parameter_list|()
block|{
name|numBuckets
operator|.
name|incr
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|incNumKeys ()
specifier|public
name|void
name|incNumKeys
parameter_list|()
block|{
name|numKeys
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|decNumKeys ()
specifier|public
name|void
name|decNumKeys
parameter_list|()
block|{
name|numKeys
operator|.
name|incr
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|setNumVolumes (long val)
specifier|public
name|void
name|setNumVolumes
parameter_list|(
name|long
name|val
parameter_list|)
block|{
name|this
operator|.
name|numVolumes
operator|.
name|incr
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
DECL|method|setNumBuckets (long val)
specifier|public
name|void
name|setNumBuckets
parameter_list|(
name|long
name|val
parameter_list|)
block|{
name|this
operator|.
name|numBuckets
operator|.
name|incr
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
DECL|method|setNumKeys (long val)
specifier|public
name|void
name|setNumKeys
parameter_list|(
name|long
name|val
parameter_list|)
block|{
name|this
operator|.
name|numKeys
operator|.
name|incr
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
DECL|method|getNumVolumes ()
specifier|public
name|long
name|getNumVolumes
parameter_list|()
block|{
return|return
name|numVolumes
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|getNumBuckets ()
specifier|public
name|long
name|getNumBuckets
parameter_list|()
block|{
return|return
name|numBuckets
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|getNumKeys ()
specifier|public
name|long
name|getNumKeys
parameter_list|()
block|{
return|return
name|numKeys
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|incNumVolumeCreates ()
specifier|public
name|void
name|incNumVolumeCreates
parameter_list|()
block|{
name|numVolumeOps
operator|.
name|incr
argument_list|()
expr_stmt|;
name|numVolumeCreates
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumVolumeUpdates ()
specifier|public
name|void
name|incNumVolumeUpdates
parameter_list|()
block|{
name|numVolumeOps
operator|.
name|incr
argument_list|()
expr_stmt|;
name|numVolumeUpdates
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
name|numVolumeOps
operator|.
name|incr
argument_list|()
expr_stmt|;
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
name|numVolumeOps
operator|.
name|incr
argument_list|()
expr_stmt|;
name|numVolumeDeletes
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumVolumeCheckAccesses ()
specifier|public
name|void
name|incNumVolumeCheckAccesses
parameter_list|()
block|{
name|numVolumeOps
operator|.
name|incr
argument_list|()
expr_stmt|;
name|numVolumeCheckAccesses
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
name|numBucketOps
operator|.
name|incr
argument_list|()
expr_stmt|;
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
name|numBucketOps
operator|.
name|incr
argument_list|()
expr_stmt|;
name|numBucketInfos
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumBucketUpdates ()
specifier|public
name|void
name|incNumBucketUpdates
parameter_list|()
block|{
name|numBucketOps
operator|.
name|incr
argument_list|()
expr_stmt|;
name|numBucketUpdates
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumBucketDeletes ()
specifier|public
name|void
name|incNumBucketDeletes
parameter_list|()
block|{
name|numBucketOps
operator|.
name|incr
argument_list|()
expr_stmt|;
name|numBucketDeletes
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumBucketLists ()
specifier|public
name|void
name|incNumBucketLists
parameter_list|()
block|{
name|numBucketOps
operator|.
name|incr
argument_list|()
expr_stmt|;
name|numBucketLists
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumKeyLists ()
specifier|public
name|void
name|incNumKeyLists
parameter_list|()
block|{
name|numKeyOps
operator|.
name|incr
argument_list|()
expr_stmt|;
name|numKeyLists
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumVolumeLists ()
specifier|public
name|void
name|incNumVolumeLists
parameter_list|()
block|{
name|numVolumeOps
operator|.
name|incr
argument_list|()
expr_stmt|;
name|numVolumeLists
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumListS3Buckets ()
specifier|public
name|void
name|incNumListS3Buckets
parameter_list|()
block|{
name|numBucketOps
operator|.
name|incr
argument_list|()
expr_stmt|;
name|numListS3Buckets
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumListS3BucketsFails ()
specifier|public
name|void
name|incNumListS3BucketsFails
parameter_list|()
block|{
name|numBucketOps
operator|.
name|incr
argument_list|()
expr_stmt|;
name|numListS3BucketsFails
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumInitiateMultipartUploads ()
specifier|public
name|void
name|incNumInitiateMultipartUploads
parameter_list|()
block|{
name|numKeyOps
operator|.
name|incr
argument_list|()
expr_stmt|;
name|numInitiateMultipartUploads
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumInitiateMultipartUploadFails ()
specifier|public
name|void
name|incNumInitiateMultipartUploadFails
parameter_list|()
block|{
name|numInitiateMultipartUploadFails
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumCommitMultipartUploadParts ()
specifier|public
name|void
name|incNumCommitMultipartUploadParts
parameter_list|()
block|{
name|numKeyOps
operator|.
name|incr
argument_list|()
expr_stmt|;
name|numCommitMultipartUploadParts
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumCommitMultipartUploadPartFails ()
specifier|public
name|void
name|incNumCommitMultipartUploadPartFails
parameter_list|()
block|{
name|numInitiateMultipartUploadFails
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumCompleteMultipartUploads ()
specifier|public
name|void
name|incNumCompleteMultipartUploads
parameter_list|()
block|{
name|numKeyOps
operator|.
name|incr
argument_list|()
expr_stmt|;
name|numCompleteMultipartUploads
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumCompleteMultipartUploadFails ()
specifier|public
name|void
name|incNumCompleteMultipartUploadFails
parameter_list|()
block|{
name|numCompleteMultipartUploadFails
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumAbortMultipartUploads ()
specifier|public
name|void
name|incNumAbortMultipartUploads
parameter_list|()
block|{
name|numKeyOps
operator|.
name|incr
argument_list|()
expr_stmt|;
name|numAbortMultipartUploads
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumAbortMultipartUploadFails ()
specifier|public
name|void
name|incNumAbortMultipartUploadFails
parameter_list|()
block|{
name|numAbortMultipartUploadFails
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumListMultipartUploadParts ()
specifier|public
name|void
name|incNumListMultipartUploadParts
parameter_list|()
block|{
name|numKeyOps
operator|.
name|incr
argument_list|()
expr_stmt|;
name|numListMultipartUploadParts
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumListMultipartUploadPartFails ()
specifier|public
name|void
name|incNumListMultipartUploadPartFails
parameter_list|()
block|{
name|numListMultipartUploadPartFails
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumGetServiceLists ()
specifier|public
name|void
name|incNumGetServiceLists
parameter_list|()
block|{
name|numGetServiceLists
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
DECL|method|incNumVolumeUpdateFails ()
specifier|public
name|void
name|incNumVolumeUpdateFails
parameter_list|()
block|{
name|numVolumeUpdateFails
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
DECL|method|incNumVolumeCheckAccessFails ()
specifier|public
name|void
name|incNumVolumeCheckAccessFails
parameter_list|()
block|{
name|numVolumeCheckAccessFails
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
DECL|method|incNumBucketUpdateFails ()
specifier|public
name|void
name|incNumBucketUpdateFails
parameter_list|()
block|{
name|numBucketUpdateFails
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumBucketDeleteFails ()
specifier|public
name|void
name|incNumBucketDeleteFails
parameter_list|()
block|{
name|numBucketDeleteFails
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
name|numKeyOps
operator|.
name|incr
argument_list|()
expr_stmt|;
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
name|numKeyOps
operator|.
name|incr
argument_list|()
expr_stmt|;
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
DECL|method|incNumKeyRenames ()
specifier|public
name|void
name|incNumKeyRenames
parameter_list|()
block|{
name|numKeyOps
operator|.
name|incr
argument_list|()
expr_stmt|;
name|numKeyRenames
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumKeyRenameFails ()
specifier|public
name|void
name|incNumKeyRenameFails
parameter_list|()
block|{
name|numKeyOps
operator|.
name|incr
argument_list|()
expr_stmt|;
name|numKeyRenameFails
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumKeyDeleteFails ()
specifier|public
name|void
name|incNumKeyDeleteFails
parameter_list|()
block|{
name|numKeyDeleteFails
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumKeyDeletes ()
specifier|public
name|void
name|incNumKeyDeletes
parameter_list|()
block|{
name|numKeyOps
operator|.
name|incr
argument_list|()
expr_stmt|;
name|numKeyDeletes
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumKeyCommits ()
specifier|public
name|void
name|incNumKeyCommits
parameter_list|()
block|{
name|numKeyOps
operator|.
name|incr
argument_list|()
expr_stmt|;
name|numKeyCommits
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumKeyCommitFails ()
specifier|public
name|void
name|incNumKeyCommitFails
parameter_list|()
block|{
name|numKeyCommitFails
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumBlockAllocateCalls ()
specifier|public
name|void
name|incNumBlockAllocateCalls
parameter_list|()
block|{
name|numAllocateBlockCalls
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumBlockAllocateCallFails ()
specifier|public
name|void
name|incNumBlockAllocateCallFails
parameter_list|()
block|{
name|numBlockAllocateCallFails
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumBucketListFails ()
specifier|public
name|void
name|incNumBucketListFails
parameter_list|()
block|{
name|numBucketListFails
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumKeyListFails ()
specifier|public
name|void
name|incNumKeyListFails
parameter_list|()
block|{
name|numKeyListFails
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumVolumeListFails ()
specifier|public
name|void
name|incNumVolumeListFails
parameter_list|()
block|{
name|numVolumeListFails
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumGetServiceListFails ()
specifier|public
name|void
name|incNumGetServiceListFails
parameter_list|()
block|{
name|numGetServiceListFails
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
DECL|method|getNumVolumeUpdates ()
specifier|public
name|long
name|getNumVolumeUpdates
parameter_list|()
block|{
return|return
name|numVolumeUpdates
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
DECL|method|getNumVolumeCheckAccesses ()
specifier|public
name|long
name|getNumVolumeCheckAccesses
parameter_list|()
block|{
return|return
name|numVolumeCheckAccesses
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
DECL|method|getNumBucketUpdates ()
specifier|public
name|long
name|getNumBucketUpdates
parameter_list|()
block|{
return|return
name|numBucketUpdates
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumBucketDeletes ()
specifier|public
name|long
name|getNumBucketDeletes
parameter_list|()
block|{
return|return
name|numBucketDeletes
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumBucketLists ()
specifier|public
name|long
name|getNumBucketLists
parameter_list|()
block|{
return|return
name|numBucketLists
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumVolumeLists ()
specifier|public
name|long
name|getNumVolumeLists
parameter_list|()
block|{
return|return
name|numVolumeLists
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumKeyLists ()
specifier|public
name|long
name|getNumKeyLists
parameter_list|()
block|{
return|return
name|numKeyLists
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumGetServiceLists ()
specifier|public
name|long
name|getNumGetServiceLists
parameter_list|()
block|{
return|return
name|numGetServiceLists
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
DECL|method|getNumVolumeUpdateFails ()
specifier|public
name|long
name|getNumVolumeUpdateFails
parameter_list|()
block|{
return|return
name|numVolumeUpdateFails
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
DECL|method|getNumVolumeCheckAccessFails ()
specifier|public
name|long
name|getNumVolumeCheckAccessFails
parameter_list|()
block|{
return|return
name|numVolumeCheckAccessFails
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
DECL|method|getNumBucketUpdateFails ()
specifier|public
name|long
name|getNumBucketUpdateFails
parameter_list|()
block|{
return|return
name|numBucketUpdateFails
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumBucketDeleteFails ()
specifier|public
name|long
name|getNumBucketDeleteFails
parameter_list|()
block|{
return|return
name|numBucketDeleteFails
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
annotation|@
name|VisibleForTesting
DECL|method|getNumKeyRenames ()
specifier|public
name|long
name|getNumKeyRenames
parameter_list|()
block|{
return|return
name|numKeyRenames
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumKeyRenameFails ()
specifier|public
name|long
name|getNumKeyRenameFails
parameter_list|()
block|{
return|return
name|numKeyRenameFails
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumKeyDeletes ()
specifier|public
name|long
name|getNumKeyDeletes
parameter_list|()
block|{
return|return
name|numKeyDeletes
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumKeyDeletesFails ()
specifier|public
name|long
name|getNumKeyDeletesFails
parameter_list|()
block|{
return|return
name|numKeyDeleteFails
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumBucketListFails ()
specifier|public
name|long
name|getNumBucketListFails
parameter_list|()
block|{
return|return
name|numBucketListFails
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumKeyListFails ()
specifier|public
name|long
name|getNumKeyListFails
parameter_list|()
block|{
return|return
name|numKeyListFails
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumVolumeListFails ()
specifier|public
name|long
name|getNumVolumeListFails
parameter_list|()
block|{
return|return
name|numVolumeListFails
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumKeyCommits ()
specifier|public
name|long
name|getNumKeyCommits
parameter_list|()
block|{
return|return
name|numKeyCommits
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumKeyCommitFails ()
specifier|public
name|long
name|getNumKeyCommitFails
parameter_list|()
block|{
return|return
name|numKeyCommitFails
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumBlockAllocates ()
specifier|public
name|long
name|getNumBlockAllocates
parameter_list|()
block|{
return|return
name|numAllocateBlockCalls
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumBlockAllocateFails ()
specifier|public
name|long
name|getNumBlockAllocateFails
parameter_list|()
block|{
return|return
name|numBlockAllocateCallFails
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumGetServiceListFails ()
specifier|public
name|long
name|getNumGetServiceListFails
parameter_list|()
block|{
return|return
name|numGetServiceListFails
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumListS3Buckets ()
specifier|public
name|long
name|getNumListS3Buckets
parameter_list|()
block|{
return|return
name|numListS3Buckets
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumListS3BucketsFails ()
specifier|public
name|long
name|getNumListS3BucketsFails
parameter_list|()
block|{
return|return
name|numListS3BucketsFails
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|getNumInitiateMultipartUploads ()
specifier|public
name|long
name|getNumInitiateMultipartUploads
parameter_list|()
block|{
return|return
name|numInitiateMultipartUploads
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|getNumInitiateMultipartUploadFails ()
specifier|public
name|long
name|getNumInitiateMultipartUploadFails
parameter_list|()
block|{
return|return
name|numInitiateMultipartUploadFails
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|getNumAbortMultipartUploads ()
specifier|public
name|long
name|getNumAbortMultipartUploads
parameter_list|()
block|{
return|return
name|numAbortMultipartUploads
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|getNumAbortMultipartUploadFails ()
specifier|public
name|long
name|getNumAbortMultipartUploadFails
parameter_list|()
block|{
return|return
name|numAbortMultipartUploadFails
operator|.
name|value
argument_list|()
return|;
block|}
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
block|}
end_class

end_unit

