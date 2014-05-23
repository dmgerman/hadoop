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
name|timeline
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
name|timeline
operator|.
name|TimelinePutResponse
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
name|TimelineClientImpl
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
comment|/**  * A client library that can be used to post some information in terms of a  * number of conceptual entities.  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Unstable
DECL|class|TimelineClient
specifier|public
specifier|abstract
class|class
name|TimelineClient
extends|extends
name|AbstractService
block|{
annotation|@
name|Public
DECL|method|createTimelineClient ()
specifier|public
specifier|static
name|TimelineClient
name|createTimelineClient
parameter_list|()
block|{
name|TimelineClient
name|client
init|=
operator|new
name|TimelineClientImpl
argument_list|()
decl_stmt|;
return|return
name|client
return|;
block|}
annotation|@
name|Private
DECL|method|TimelineClient (String name)
specifier|protected
name|TimelineClient
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
comment|/**    *<p>    * Send the information of a number of conceptual entities to the timeline    * server. It is a blocking API. The method will not return until it gets the    * response from the timeline server.    *</p>    *     * @param entities    *          the collection of {@link TimelineEntity}    * @return the error information if the sent entities are not correctly stored    * @throws IOException    * @throws YarnException    */
annotation|@
name|Public
DECL|method|putEntities ( TimelineEntity... entities)
specifier|public
specifier|abstract
name|TimelinePutResponse
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
block|}
end_class

end_unit

