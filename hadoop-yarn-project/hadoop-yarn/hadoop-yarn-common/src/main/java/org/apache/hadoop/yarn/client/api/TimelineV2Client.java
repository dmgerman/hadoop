begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client.api
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|client
operator|.
name|api
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
operator|.
name|Public
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
name|ApplicationId
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
name|CollectorInfo
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
name|client
operator|.
name|api
operator|.
name|impl
operator|.
name|TimelineV2ClientImpl
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
name|exceptions
operator|.
name|YarnException
import|;
end_import

begin_comment
comment|/**  * A client library that can be used to post some information in terms of a  * number of conceptual entities. This client library needs to be used along  * with time line v.2 server version.  * Refer {@link TimelineClient} for ATS V1 interface.  */
end_comment

begin_class
DECL|class|TimelineV2Client
specifier|public
specifier|abstract
class|class
name|TimelineV2Client
extends|extends
name|CompositeService
block|{
comment|/**    * Creates an instance of the timeline v.2 client.    *    * @param appId the application id with which the timeline client is    *          associated    * @return the created timeline client instance    */
annotation|@
name|Public
DECL|method|createTimelineClient (ApplicationId appId)
specifier|public
specifier|static
name|TimelineV2Client
name|createTimelineClient
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
block|{
name|TimelineV2Client
name|client
init|=
operator|new
name|TimelineV2ClientImpl
argument_list|(
name|appId
argument_list|)
decl_stmt|;
return|return
name|client
return|;
block|}
DECL|method|TimelineV2Client (String name)
specifier|protected
name|TimelineV2Client
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
comment|/**    *<p>    * Send the information of a number of conceptual entities within the scope    * of YARN application to the timeline service v.2 collector. It is a blocking    * API. The method will not return until all the put entities have been    * persisted.    *</p>    *    * @param entities the collection of {@link TimelineEntity}    * @throws IOException  if there are I/O errors    * @throws YarnException if entities are incomplete/invalid    */
annotation|@
name|Public
DECL|method|putEntities (TimelineEntity... entities)
specifier|public
specifier|abstract
name|void
name|putEntities
parameter_list|(
name|TimelineEntity
modifier|...
name|entities
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
function_decl|;
comment|/**    *<p>    * Send the information of a number of conceptual entities within the scope    * of YARN application to the timeline service v.2 collector. It is an    * asynchronous API. The method will return once all the entities are    * received.    *</p>    *    * @param entities the collection of {@link TimelineEntity}    * @throws IOException  if there are I/O errors    * @throws YarnException if entities are incomplete/invalid    */
annotation|@
name|Public
DECL|method|putEntitiesAsync (TimelineEntity... entities)
specifier|public
specifier|abstract
name|void
name|putEntitiesAsync
parameter_list|(
name|TimelineEntity
modifier|...
name|entities
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
function_decl|;
comment|/**    *<p>    * Update collector info received in AllocateResponse which contains the    * timeline service address where the request will be sent to and the timeline    * delegation token which will be used to send the request.    *</p>    *    * @param collectorInfo Collector info which contains the timeline service    * address and timeline delegation token.    */
DECL|method|setTimelineCollectorInfo (CollectorInfo collectorInfo)
specifier|public
specifier|abstract
name|void
name|setTimelineCollectorInfo
parameter_list|(
name|CollectorInfo
name|collectorInfo
parameter_list|)
function_decl|;
comment|/**    *<p>    * Send the information of a number of conceptual entities within the scope of    * a sub-application to the timeline service v.2 collector. It is a blocking    * API. The method will not return until all the put entities have been    * persisted.    *</p>    *    * @param entities the collection of {@link TimelineEntity}    * @throws IOException  if there are I/O errors    * @throws YarnException if entities are incomplete/invalid    */
annotation|@
name|Public
DECL|method|putSubAppEntities (TimelineEntity... entities)
specifier|public
specifier|abstract
name|void
name|putSubAppEntities
parameter_list|(
name|TimelineEntity
modifier|...
name|entities
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
function_decl|;
comment|/**    *<p>    * Send the information of a number of conceptual entities within the scope of    * a sub-application to the timeline service v.2 collector. It is an    * asynchronous API. The method will return once all the entities are received    * .    *</p>    *    * @param entities the collection of {@link TimelineEntity}    * @throws IOException  if there are I/O errors    * @throws YarnException if entities are incomplete/invalid    */
annotation|@
name|Public
DECL|method|putSubAppEntitiesAsync (TimelineEntity... entities)
specifier|public
specifier|abstract
name|void
name|putSubAppEntitiesAsync
parameter_list|(
name|TimelineEntity
modifier|...
name|entities
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
function_decl|;
block|}
end_class

end_unit

