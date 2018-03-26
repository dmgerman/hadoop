begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
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
name|event
operator|.
name|AbstractEvent
import|;
end_import

begin_comment
comment|/**  * Events are handled by {@link ServiceManager} to manage the service  * state.  */
end_comment

begin_class
DECL|class|ServiceEvent
specifier|public
class|class
name|ServiceEvent
extends|extends
name|AbstractEvent
argument_list|<
name|ServiceEventType
argument_list|>
block|{
DECL|field|type
specifier|private
specifier|final
name|ServiceEventType
name|type
decl_stmt|;
DECL|field|version
specifier|private
name|String
name|version
decl_stmt|;
DECL|method|ServiceEvent (ServiceEventType serviceEventType)
specifier|public
name|ServiceEvent
parameter_list|(
name|ServiceEventType
name|serviceEventType
parameter_list|)
block|{
name|super
argument_list|(
name|serviceEventType
argument_list|)
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|serviceEventType
expr_stmt|;
block|}
DECL|method|getType ()
specifier|public
name|ServiceEventType
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
DECL|method|getVersion ()
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
name|version
return|;
block|}
DECL|method|setVersion (String version)
specifier|public
name|ServiceEvent
name|setVersion
parameter_list|(
name|String
name|version
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

