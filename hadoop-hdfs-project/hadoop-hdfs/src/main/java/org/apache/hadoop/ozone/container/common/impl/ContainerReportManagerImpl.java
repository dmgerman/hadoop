begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|impl
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|RandomUtils
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
name|conf
operator|.
name|Configuration
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
name|ozone
operator|.
name|OzoneConfigKeys
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
name|ozone
operator|.
name|client
operator|.
name|OzoneClientUtils
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|interfaces
operator|.
name|ContainerReportManager
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ReportState
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
name|util
operator|.
name|Time
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_comment
comment|/**  * Class wraps the container report operations on datanode.  * // TODO: support incremental/delta container report  */
end_comment

begin_class
DECL|class|ContainerReportManagerImpl
specifier|public
class|class
name|ContainerReportManagerImpl
implements|implements
name|ContainerReportManager
block|{
comment|// Last non-empty container report time
DECL|field|lastContainerReportTime
specifier|private
name|long
name|lastContainerReportTime
decl_stmt|;
DECL|field|containerReportInterval
specifier|private
specifier|final
name|long
name|containerReportInterval
decl_stmt|;
DECL|field|heartbeatInterval
specifier|private
specifier|final
name|long
name|heartbeatInterval
decl_stmt|;
DECL|field|reportCount
specifier|private
name|AtomicLong
name|reportCount
decl_stmt|;
DECL|field|NO_CONTAINER_REPORTSTATE
specifier|private
specifier|static
specifier|final
name|ReportState
name|NO_CONTAINER_REPORTSTATE
init|=
name|ReportState
operator|.
name|newBuilder
argument_list|()
operator|.
name|setState
argument_list|(
name|ReportState
operator|.
name|states
operator|.
name|noContainerReports
argument_list|)
operator|.
name|setCount
argument_list|(
literal|0
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|method|ContainerReportManagerImpl (Configuration config)
specifier|public
name|ContainerReportManagerImpl
parameter_list|(
name|Configuration
name|config
parameter_list|)
block|{
name|this
operator|.
name|lastContainerReportTime
operator|=
operator|-
literal|1
expr_stmt|;
name|this
operator|.
name|reportCount
operator|=
operator|new
name|AtomicLong
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|this
operator|.
name|containerReportInterval
operator|=
name|config
operator|.
name|getTimeDuration
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_CONTAINER_REPORT_INTERVAL
argument_list|,
name|OzoneConfigKeys
operator|.
name|OZONE_CONTAINER_REPORT_INTERVAL_DEFAULT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|this
operator|.
name|heartbeatInterval
operator|=
name|OzoneClientUtils
operator|.
name|getScmHeartbeatInterval
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
DECL|method|getContainerReportState ()
specifier|public
name|ReportState
name|getContainerReportState
parameter_list|()
block|{
if|if
condition|(
name|lastContainerReportTime
operator|<
literal|0
condition|)
block|{
return|return
name|getFullContainerReportState
argument_list|()
return|;
block|}
else|else
block|{
comment|// Add a random delay (0~30s) on top of the container report
comment|// interval (60s) so tha the SCM is overwhelmed by the container reports
comment|// sent in sync.
if|if
condition|(
name|Time
operator|.
name|monotonicNow
argument_list|()
operator|-
name|lastContainerReportTime
operator|>
operator|(
name|containerReportInterval
operator|+
name|getRandomReportDelay
argument_list|()
operator|)
condition|)
block|{
return|return
name|getFullContainerReportState
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|getNoContainerReportState
argument_list|()
return|;
block|}
block|}
block|}
DECL|method|getFullContainerReportState ()
specifier|private
name|ReportState
name|getFullContainerReportState
parameter_list|()
block|{
name|ReportState
operator|.
name|Builder
name|rsBuilder
init|=
name|ReportState
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|rsBuilder
operator|.
name|setState
argument_list|(
name|ReportState
operator|.
name|states
operator|.
name|completeContinerReport
argument_list|)
expr_stmt|;
name|rsBuilder
operator|.
name|setCount
argument_list|(
name|reportCount
operator|.
name|incrementAndGet
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|lastContainerReportTime
operator|=
name|Time
operator|.
name|monotonicNow
argument_list|()
expr_stmt|;
return|return
name|rsBuilder
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|getNoContainerReportState ()
specifier|private
name|ReportState
name|getNoContainerReportState
parameter_list|()
block|{
return|return
name|NO_CONTAINER_REPORTSTATE
return|;
block|}
DECL|method|getRandomReportDelay ()
specifier|private
name|long
name|getRandomReportDelay
parameter_list|()
block|{
return|return
name|RandomUtils
operator|.
name|nextLong
argument_list|(
literal|0
argument_list|,
name|heartbeatInterval
argument_list|)
return|;
block|}
block|}
end_class

end_unit

