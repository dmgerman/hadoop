begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.test.system
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|test
operator|.
name|system
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
name|mapred
operator|.
name|JobTracker
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
name|mapred
operator|.
name|TaskTracker
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
name|mapred
operator|.
name|TaskTrackerStatus
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
name|mapreduce
operator|.
name|TaskID
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
name|mapreduce
operator|.
name|security
operator|.
name|token
operator|.
name|JobTokenSelector
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
name|KerberosInfo
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
name|token
operator|.
name|TokenInfo
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
name|test
operator|.
name|system
operator|.
name|DaemonProtocol
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
comment|/**  * TaskTracker RPC interface to be used for cluster tests.  *  * The protocol has to be annotated so KerberosInfo can be filled in during  * creation of a ipc.Client connection  */
end_comment

begin_interface
annotation|@
name|KerberosInfo
argument_list|(
name|serverPrincipal
operator|=
name|TaskTracker
operator|.
name|TT_USER_NAME
argument_list|)
annotation|@
name|TokenInfo
argument_list|(
name|JobTokenSelector
operator|.
name|class
argument_list|)
DECL|interface|TTProtocol
specifier|public
interface|interface
name|TTProtocol
extends|extends
name|DaemonProtocol
block|{
DECL|field|versionID
specifier|public
specifier|static
specifier|final
name|long
name|versionID
init|=
literal|1L
decl_stmt|;
comment|/**    * Gets latest status which was sent in heartbeat to the {@link JobTracker}.     *<br/>    *     * @return status of the TaskTracker daemon    * @throws IOException in case of errors    */
DECL|method|getStatus ()
name|TaskTrackerStatus
name|getStatus
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Gets list of all the tasks in the {@link TaskTracker}.<br/>    *     * @return list of all the tasks    * @throws IOException in case of errors    */
DECL|method|getTasks ()
name|TTTaskInfo
index|[]
name|getTasks
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Gets the task associated with the id.<br/>    *     * @param taskID of the task.    *     * @return returns task info<code>TTTaskInfo</code>    * @throws IOException in case of errors    */
DECL|method|getTask (TaskID taskID)
name|TTTaskInfo
name|getTask
parameter_list|(
name|TaskID
name|taskID
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Checks if any of process in the process tree of the task is alive    * or not.<br/>    *     * @param pid    *          of the task attempt    * @return true if task process tree is alive.    * @throws IOException in case of errors    */
DECL|method|isProcessTreeAlive (String pid)
name|boolean
name|isProcessTreeAlive
parameter_list|(
name|String
name|pid
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

