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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|classification
operator|.
name|InterfaceStability
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
name|Service
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

begin_comment
comment|/**  * This interface is for storing application timeline information.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|interface|TimelineWriter
specifier|public
interface|interface
name|TimelineWriter
extends|extends
name|Service
block|{
comment|/**    * Stores the entire information in {@link TimelineEntities} to the    * timeline store. Any errors occurring for individual write request objects    * will be reported in the response.    *    * @param clusterId context cluster ID    * @param userId context user ID    * @param flowName context flow name    * @param flowVersion context flow version    * @param flowRunId    * @param appId context app ID    * @param data    *          a {@link TimelineEntities} object.    * @return a {@link TimelineWriteResponse} object.    * @throws IOException    */
DECL|method|write (String clusterId, String userId, String flowName, String flowVersion, long flowRunId, String appId, TimelineEntities data)
name|TimelineWriteResponse
name|write
parameter_list|(
name|String
name|clusterId
parameter_list|,
name|String
name|userId
parameter_list|,
name|String
name|flowName
parameter_list|,
name|String
name|flowVersion
parameter_list|,
name|long
name|flowRunId
parameter_list|,
name|String
name|appId
parameter_list|,
name|TimelineEntities
name|data
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Aggregates the entity information to the timeline store based on which    * track this entity is to be rolled up to The tracks along which aggregations    * are to be done are given by {@link TimelineAggregationTrack}    *    * Any errors occurring for individual write request objects will be reported    * in the response.    *    * @param data    *          a {@link TimelineEntity} object    *          a {@link TimelineAggregationTrack} enum    *          value.    * @return a {@link TimelineWriteResponse} object.    * @throws IOException    */
DECL|method|aggregate (TimelineEntity data, TimelineAggregationTrack track)
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
function_decl|;
block|}
end_interface

end_unit

