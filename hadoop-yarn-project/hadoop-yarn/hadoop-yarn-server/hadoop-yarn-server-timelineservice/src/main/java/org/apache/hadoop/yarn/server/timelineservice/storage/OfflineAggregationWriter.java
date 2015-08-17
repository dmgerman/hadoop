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
operator|.
name|common
operator|.
name|OfflineAggregationInfo
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
comment|/**  * YARN timeline service v2 offline aggregation storage interface  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|OfflineAggregationWriter
specifier|public
specifier|abstract
class|class
name|OfflineAggregationWriter
extends|extends
name|AbstractService
block|{
comment|/**    * Construct the offline writer.    *    * @param name service name    */
DECL|method|OfflineAggregationWriter (String name)
specifier|public
name|OfflineAggregationWriter
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**    * Persist aggregated timeline entities to the offline store based on which    * track this entity is to be rolled up to. The tracks along which aggregations    * are to be done are given by {@link OfflineAggregationInfo}.    *    * @param context a {@link TimelineCollectorContext} object that describes the    *                context information of the aggregated data. Depends on the    *                type of the aggregation, some fields of this context maybe    *                empty or null.    * @param entities {@link TimelineEntities} to be persisted.    * @param info an {@link OfflineAggregationInfo} object that describes the    *             detail of the aggregation. Current supported option is    *             {@link OfflineAggregationInfo#FLOW_AGGREGATION}.    * @return a {@link TimelineWriteResponse} object.    * @throws IOException    */
DECL|method|writeAggregatedEntity ( TimelineCollectorContext context, TimelineEntities entities, OfflineAggregationInfo info)
specifier|abstract
name|TimelineWriteResponse
name|writeAggregatedEntity
parameter_list|(
name|TimelineCollectorContext
name|context
parameter_list|,
name|TimelineEntities
name|entities
parameter_list|,
name|OfflineAggregationInfo
name|info
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

