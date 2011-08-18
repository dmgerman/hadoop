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
name|java
operator|.
name|util
operator|.
name|List
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
name|io
operator|.
name|Writable
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
name|JobStatus
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
name|JobID
import|;
end_import

begin_comment
comment|/**  * Job state information as seen by the JobTracker.  */
end_comment

begin_interface
DECL|interface|JobInfo
specifier|public
interface|interface
name|JobInfo
extends|extends
name|Writable
block|{
comment|/**    * Gets the JobId of the job.<br/>    *     * @return id of the job.    */
DECL|method|getID ()
name|JobID
name|getID
parameter_list|()
function_decl|;
comment|/**    * Gets the current status of the job.<br/>    *     * @return status.    */
DECL|method|getStatus ()
name|JobStatus
name|getStatus
parameter_list|()
function_decl|;
comment|/**    * Gets the history location of the job.<br/>    *     * @return the path to the history file.    */
DECL|method|getHistoryUrl ()
name|String
name|getHistoryUrl
parameter_list|()
function_decl|;
comment|/**    * Gets the number of maps which are currently running for the job.<br/>    *     * @return number of running for the job.    */
DECL|method|runningMaps ()
name|int
name|runningMaps
parameter_list|()
function_decl|;
comment|/**    * Gets the number of reduces currently running for the job.<br/>    *     * @return number of reduces running for the job.    */
DECL|method|runningReduces ()
name|int
name|runningReduces
parameter_list|()
function_decl|;
comment|/**    * Gets the number of maps to be scheduled for the job.<br/>    *     * @return number of waiting maps.    */
DECL|method|waitingMaps ()
name|int
name|waitingMaps
parameter_list|()
function_decl|;
comment|/**    * Gets the number of reduces to be scheduled for the job.<br/>    *     * @return number of waiting reduces.    */
DECL|method|waitingReduces ()
name|int
name|waitingReduces
parameter_list|()
function_decl|;
comment|/**    * Gets the number of maps that are finished.<br/>    * @return the number of finished maps.    */
DECL|method|finishedMaps ()
name|int
name|finishedMaps
parameter_list|()
function_decl|;
comment|/**    * Gets the number of map tasks that are to be spawned for the job<br/>    * @return    */
DECL|method|numMaps ()
name|int
name|numMaps
parameter_list|()
function_decl|;
comment|/**    * Gets the number of reduce tasks that are to be spawned for the job<br/>    * @return    */
DECL|method|numReduces ()
name|int
name|numReduces
parameter_list|()
function_decl|;
comment|/**    * Gets the number of reduces that are finished.<br/>    * @return the number of finished reduces.    */
DECL|method|finishedReduces ()
name|int
name|finishedReduces
parameter_list|()
function_decl|;
comment|/**    * Gets if cleanup for the job has been launched.<br/>    *     * @return true if cleanup task has been launched.    */
DECL|method|isCleanupLaunched ()
name|boolean
name|isCleanupLaunched
parameter_list|()
function_decl|;
comment|/**    * Gets if the setup for the job has been launched.<br/>    *     * @return true if setup task has been launched.    */
DECL|method|isSetupLaunched ()
name|boolean
name|isSetupLaunched
parameter_list|()
function_decl|;
comment|/**    * Gets if the setup for the job has been completed.<br/>    *     * @return true if the setup task for the job has completed.    */
DECL|method|isSetupFinished ()
name|boolean
name|isSetupFinished
parameter_list|()
function_decl|;
comment|/**    * Gets list of blacklisted trackers for the particular job.<br/>    *     * @return list of blacklisted tracker name.    */
DECL|method|getBlackListedTrackers ()
name|List
argument_list|<
name|String
argument_list|>
name|getBlackListedTrackers
parameter_list|()
function_decl|;
comment|/**    * Gets if the history file of the job is copied to the done     * location<br/>    *     * @return true if history file copied.    */
DECL|method|isHistoryFileCopied ()
name|boolean
name|isHistoryFileCopied
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

