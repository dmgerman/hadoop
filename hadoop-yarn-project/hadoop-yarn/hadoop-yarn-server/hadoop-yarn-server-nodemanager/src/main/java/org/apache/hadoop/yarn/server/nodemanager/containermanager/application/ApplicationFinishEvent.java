begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.application
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|application
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
name|api
operator|.
name|records
operator|.
name|ApplicationId
import|;
end_import

begin_comment
comment|/**  * Finish/abort event  */
end_comment

begin_class
DECL|class|ApplicationFinishEvent
specifier|public
class|class
name|ApplicationFinishEvent
extends|extends
name|ApplicationEvent
block|{
DECL|field|diagnostic
specifier|private
specifier|final
name|String
name|diagnostic
decl_stmt|;
comment|/**    * Application event to abort all containers associated with the app    * @param appId to abort containers    * @param diagnostic reason for the abort    */
DECL|method|ApplicationFinishEvent (ApplicationId appId, String diagnostic)
specifier|public
name|ApplicationFinishEvent
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|String
name|diagnostic
parameter_list|)
block|{
name|super
argument_list|(
name|appId
argument_list|,
name|ApplicationEventType
operator|.
name|FINISH_APPLICATION
argument_list|)
expr_stmt|;
name|this
operator|.
name|diagnostic
operator|=
name|diagnostic
expr_stmt|;
block|}
comment|/**    * Why the app was aborted    * @return diagnostic message    */
DECL|method|getDiagnostic ()
specifier|public
name|String
name|getDiagnostic
parameter_list|()
block|{
return|return
name|diagnostic
return|;
block|}
block|}
end_class

end_unit

