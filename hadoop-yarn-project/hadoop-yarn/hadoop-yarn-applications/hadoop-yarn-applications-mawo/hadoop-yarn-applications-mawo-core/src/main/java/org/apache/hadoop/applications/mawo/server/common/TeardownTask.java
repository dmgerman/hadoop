begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.applications.mawo.server.common
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|applications
operator|.
name|mawo
operator|.
name|server
operator|.
name|common
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Define Teardown Task.  */
end_comment

begin_class
DECL|class|TeardownTask
specifier|public
class|class
name|TeardownTask
extends|extends
name|SimpleTask
block|{
comment|/**    * Teardown task default constructor.    */
DECL|method|TeardownTask ()
specifier|public
name|TeardownTask
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|setTaskType
argument_list|(
name|TaskType
operator|.
name|TEARDOWN
argument_list|)
expr_stmt|;
block|}
comment|/**    * Teardown Task constructor.    * @param taskId : Teardown task Id    * @param environment : Environment map for teardown task    * @param taskCMD : Teardown task command    * @param timeout : Timeout for Teardown task    */
DECL|method|TeardownTask (final TaskId taskId, final Map<String, String> environment, final String taskCMD, final long timeout)
specifier|public
name|TeardownTask
parameter_list|(
specifier|final
name|TaskId
name|taskId
parameter_list|,
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|environment
parameter_list|,
specifier|final
name|String
name|taskCMD
parameter_list|,
specifier|final
name|long
name|timeout
parameter_list|)
block|{
name|super
argument_list|(
name|taskId
argument_list|,
name|environment
argument_list|,
name|taskCMD
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
name|setTaskType
argument_list|(
name|TaskType
operator|.
name|TEARDOWN
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

