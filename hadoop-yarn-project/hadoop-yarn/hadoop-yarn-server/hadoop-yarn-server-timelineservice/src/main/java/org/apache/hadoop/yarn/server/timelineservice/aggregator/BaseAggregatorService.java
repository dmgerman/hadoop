begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.aggregator
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
name|aggregator
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
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
operator|.
name|Private
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
operator|.
name|Unstable
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
name|CompositeService
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

begin_comment
comment|/**  * Service that handles writes to the timeline service and writes them to the  * backing storage.  *  * Classes that extend this can add their own lifecycle management or  * customization of request handling.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|BaseAggregatorService
specifier|public
class|class
name|BaseAggregatorService
extends|extends
name|CompositeService
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|BaseAggregatorService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|BaseAggregatorService (String name)
specifier|public
name|BaseAggregatorService
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
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|protected
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
comment|/**    * Handles entity writes. These writes are synchronous and are written to the    * backing storage without buffering/batching. If any entity already exists,    * it results in an update of the entity.    *    * This method should be reserved for selected critical entities and events.    * For normal voluminous writes one should use the async method    * {@link #postEntitiesAsync(TimelineEntities, UserGroupInformation)}.    *    * @param entities entities to post    * @param callerUgi the caller UGI    */
DECL|method|postEntities (TimelineEntities entities, UserGroupInformation callerUgi)
specifier|public
name|void
name|postEntities
parameter_list|(
name|TimelineEntities
name|entities
parameter_list|,
name|UserGroupInformation
name|callerUgi
parameter_list|)
block|{
comment|// TODO implement
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"postEntities(entities="
operator|+
name|entities
operator|+
literal|", callerUgi="
operator|+
name|callerUgi
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Handles entity writes in an asynchronous manner. The method returns as soon    * as validation is done. No promises are made on how quickly it will be    * written to the backing storage or if it will always be written to the    * backing storage. Multiple writes to the same entities may be batched and    * appropriate values updated and result in fewer writes to the backing    * storage.    *    * @param entities entities to post    * @param callerUgi the caller UGI    */
DECL|method|postEntitiesAsync (TimelineEntities entities, UserGroupInformation callerUgi)
specifier|public
name|void
name|postEntitiesAsync
parameter_list|(
name|TimelineEntities
name|entities
parameter_list|,
name|UserGroupInformation
name|callerUgi
parameter_list|)
block|{
comment|// TODO implement
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"postEntitiesAsync(entities="
operator|+
name|entities
operator|+
literal|", callerUgi="
operator|+
name|callerUgi
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

