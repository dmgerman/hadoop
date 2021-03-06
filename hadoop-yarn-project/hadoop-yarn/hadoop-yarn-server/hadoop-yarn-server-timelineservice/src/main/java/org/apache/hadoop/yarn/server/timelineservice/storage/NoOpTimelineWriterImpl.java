begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage
package|package
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
name|timelineservice
operator|.
name|storage
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
name|security
operator|.
name|UserGroupInformation
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
name|service
operator|.
name|AbstractService
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
name|api
operator|.
name|records
operator|.
name|timelineservice
operator|.
name|TimelineDomain
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
name|api
operator|.
name|records
operator|.
name|timelineservice
operator|.
name|TimelineEntities
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
name|api
operator|.
name|records
operator|.
name|timelineservice
operator|.
name|TimelineEntity
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
name|api
operator|.
name|records
operator|.
name|timelineservice
operator|.
name|TimelineWriteResponse
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
name|timelineservice
operator|.
name|collector
operator|.
name|TimelineCollectorContext
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Stub based implementation for TimelineWriter. This implementation will  * not provide a complete implementation of all the necessary features. This  * implementation is provided solely for basic testing purposes.  */
end_comment

begin_class
DECL|class|NoOpTimelineWriterImpl
specifier|public
class|class
name|NoOpTimelineWriterImpl
extends|extends
name|AbstractService
implements|implements
name|TimelineWriter
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|NoOpTimelineWriterImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|NoOpTimelineWriterImpl ()
specifier|public
name|NoOpTimelineWriterImpl
parameter_list|()
block|{
name|super
argument_list|(
name|NoOpTimelineWriterImpl
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"NoOpTimelineWriter is configured. All the writes to the backend"
operator|+
literal|" are ignored"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write (TimelineCollectorContext context, TimelineEntities data, UserGroupInformation callerUgi)
specifier|public
name|TimelineWriteResponse
name|write
parameter_list|(
name|TimelineCollectorContext
name|context
parameter_list|,
name|TimelineEntities
name|data
parameter_list|,
name|UserGroupInformation
name|callerUgi
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"NoOpTimelineWriter is configured. Not storing "
operator|+
literal|"TimelineEntities."
argument_list|)
expr_stmt|;
return|return
operator|new
name|TimelineWriteResponse
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|write (TimelineCollectorContext context, TimelineDomain domain)
specifier|public
name|TimelineWriteResponse
name|write
parameter_list|(
name|TimelineCollectorContext
name|context
parameter_list|,
name|TimelineDomain
name|domain
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"NoOpTimelineWriter is configured. Not storing "
operator|+
literal|"TimelineEntities."
argument_list|)
expr_stmt|;
return|return
operator|new
name|TimelineWriteResponse
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|aggregate (TimelineEntity data, TimelineAggregationTrack track)
specifier|public
name|TimelineWriteResponse
name|aggregate
parameter_list|(
name|TimelineEntity
name|data
parameter_list|,
name|TimelineAggregationTrack
name|track
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"NoOpTimelineWriter is configured. Not aggregating "
operator|+
literal|"TimelineEntities."
argument_list|)
expr_stmt|;
return|return
operator|new
name|TimelineWriteResponse
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|flush ()
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"NoOpTimelineWriter is configured. Ignoring flush call"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

