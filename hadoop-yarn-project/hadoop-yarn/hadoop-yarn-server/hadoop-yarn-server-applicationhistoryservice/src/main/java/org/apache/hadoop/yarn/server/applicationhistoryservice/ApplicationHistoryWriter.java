begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.applicationhistoryservice
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
name|applicationhistoryservice
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
name|yarn
operator|.
name|server
operator|.
name|applicationhistoryservice
operator|.
name|records
operator|.
name|ApplicationAttemptFinishData
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
name|applicationhistoryservice
operator|.
name|records
operator|.
name|ApplicationAttemptStartData
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
name|applicationhistoryservice
operator|.
name|records
operator|.
name|ApplicationFinishData
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
name|applicationhistoryservice
operator|.
name|records
operator|.
name|ApplicationStartData
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
name|applicationhistoryservice
operator|.
name|records
operator|.
name|ContainerFinishData
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
name|applicationhistoryservice
operator|.
name|records
operator|.
name|ContainerStartData
import|;
end_import

begin_comment
comment|/**  * It is the interface of writing the application history, exposing the methods  * of writing {@link ApplicationStartData}, {@link ApplicationFinishData}  * {@link ApplicationAttemptStartData}, {@link ApplicationAttemptFinishData},  * {@link ContainerStartData} and {@link ContainerFinishData}.  */
end_comment

begin_interface
annotation|@
name|Private
annotation|@
name|Unstable
DECL|interface|ApplicationHistoryWriter
specifier|public
interface|interface
name|ApplicationHistoryWriter
block|{
comment|/**    * This method writes the information of<code>RMApp</code> that is available    * when it starts.    *     * @param appStart    *          the record of the information of<code>RMApp</code> that is    *          available when it starts    * @throws IOException    */
DECL|method|applicationStarted (ApplicationStartData appStart)
name|void
name|applicationStarted
parameter_list|(
name|ApplicationStartData
name|appStart
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * This method writes the information of<code>RMApp</code> that is available    * when it finishes.    *     * @param appFinish    *          the record of the information of<code>RMApp</code> that is    *          available when it finishes    * @throws IOException    */
DECL|method|applicationFinished (ApplicationFinishData appFinish)
name|void
name|applicationFinished
parameter_list|(
name|ApplicationFinishData
name|appFinish
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * This method writes the information of<code>RMAppAttempt</code> that is    * available when it starts.    *     * @param appAttemptStart    *          the record of the information of<code>RMAppAttempt</code> that is    *          available when it starts    * @throws IOException    */
DECL|method|applicationAttemptStarted (ApplicationAttemptStartData appAttemptStart)
name|void
name|applicationAttemptStarted
parameter_list|(
name|ApplicationAttemptStartData
name|appAttemptStart
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * This method writes the information of<code>RMAppAttempt</code> that is    * available when it finishes.    *     * @param appAttemptFinish    *          the record of the information of<code>RMAppAttempt</code> that is    *          available when it finishes    * @throws IOException    */
name|void
DECL|method|applicationAttemptFinished (ApplicationAttemptFinishData appAttemptFinish)
name|applicationAttemptFinished
parameter_list|(
name|ApplicationAttemptFinishData
name|appAttemptFinish
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * This method writes the information of<code>RMContainer</code> that is    * available when it starts.    *     * @param containerStart    *          the record of the information of<code>RMContainer</code> that is    *          available when it starts    * @throws IOException    */
DECL|method|containerStarted (ContainerStartData containerStart)
name|void
name|containerStarted
parameter_list|(
name|ContainerStartData
name|containerStart
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * This method writes the information of<code>RMContainer</code> that is    * available when it finishes.    *     * @param containerFinish    *          the record of the information of<code>RMContainer</code> that is    *          available when it finishes    * @throws IOException    */
DECL|method|containerFinished (ContainerFinishData containerFinish)
name|void
name|containerFinished
parameter_list|(
name|ContainerFinishData
name|containerFinish
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

