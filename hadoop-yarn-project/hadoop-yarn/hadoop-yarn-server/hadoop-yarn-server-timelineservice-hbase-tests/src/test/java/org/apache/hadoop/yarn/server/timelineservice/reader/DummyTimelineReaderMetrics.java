begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.reader
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
name|reader
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
name|server
operator|.
name|timelineservice
operator|.
name|metrics
operator|.
name|TimelineReaderMetrics
import|;
end_import

begin_comment
comment|/**  * DummyTimelineReaderMetrics for mocking {@link TimelineReaderMetrics} calls.  */
end_comment

begin_class
DECL|class|DummyTimelineReaderMetrics
specifier|public
class|class
name|DummyTimelineReaderMetrics
extends|extends
name|TimelineReaderMetrics
block|{
annotation|@
name|Override
DECL|method|addGetEntitiesLatency ( long durationMs, boolean succeeded)
specifier|public
name|void
name|addGetEntitiesLatency
parameter_list|(
name|long
name|durationMs
parameter_list|,
name|boolean
name|succeeded
parameter_list|)
block|{    }
annotation|@
name|Override
DECL|method|addGetEntityTypesLatency ( long durationMs, boolean succeeded)
specifier|public
name|void
name|addGetEntityTypesLatency
parameter_list|(
name|long
name|durationMs
parameter_list|,
name|boolean
name|succeeded
parameter_list|)
block|{   }
block|}
end_class

end_unit

