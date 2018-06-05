begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.report
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
name|report
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|NodeReportProto
import|;
end_import

begin_comment
comment|/**  * Publishes NodeReport which will be sent to SCM as part of heartbeat.  * NodeReport consist of:  *   - NodeIOStats  *   - VolumeReports  */
end_comment

begin_class
DECL|class|NodeReportPublisher
specifier|public
class|class
name|NodeReportPublisher
extends|extends
name|ReportPublisher
argument_list|<
name|NodeReportProto
argument_list|>
block|{
annotation|@
name|Override
DECL|method|getReportFrequency ()
specifier|protected
name|long
name|getReportFrequency
parameter_list|()
block|{
return|return
literal|90000L
return|;
block|}
annotation|@
name|Override
DECL|method|getReport ()
specifier|protected
name|NodeReportProto
name|getReport
parameter_list|()
block|{
return|return
name|NodeReportProto
operator|.
name|getDefaultInstance
argument_list|()
return|;
block|}
block|}
end_class

end_unit

