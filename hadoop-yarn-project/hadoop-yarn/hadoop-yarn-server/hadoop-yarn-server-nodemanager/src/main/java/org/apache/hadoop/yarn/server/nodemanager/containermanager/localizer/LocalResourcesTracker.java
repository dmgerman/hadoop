begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer
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
name|localizer
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
name|fs
operator|.
name|Path
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
name|LocalResourceVisibility
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
name|event
operator|.
name|EventHandler
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
name|nodemanager
operator|.
name|DeletionService
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|localizer
operator|.
name|event
operator|.
name|ResourceEvent
import|;
end_import

begin_comment
comment|/**  * Component tracking resources all of the same {@link LocalResourceVisibility}  *   */
end_comment

begin_interface
DECL|interface|LocalResourcesTracker
interface|interface
name|LocalResourcesTracker
extends|extends
name|EventHandler
argument_list|<
name|ResourceEvent
argument_list|>
extends|,
name|Iterable
argument_list|<
name|LocalizedResource
argument_list|>
block|{
comment|// TODO: Not used at all!!
DECL|method|contains (LocalResourceRequest resource)
name|boolean
name|contains
parameter_list|(
name|LocalResourceRequest
name|resource
parameter_list|)
function_decl|;
DECL|method|remove (LocalizedResource req, DeletionService delService)
name|boolean
name|remove
parameter_list|(
name|LocalizedResource
name|req
parameter_list|,
name|DeletionService
name|delService
parameter_list|)
function_decl|;
DECL|method|getPathForLocalization (LocalResourceRequest req, Path localDirPath)
name|Path
name|getPathForLocalization
parameter_list|(
name|LocalResourceRequest
name|req
parameter_list|,
name|Path
name|localDirPath
parameter_list|)
function_decl|;
DECL|method|getUser ()
name|String
name|getUser
parameter_list|()
function_decl|;
comment|// TODO: Remove this in favour of EventHandler.handle
DECL|method|localizationCompleted (LocalResourceRequest req, boolean success)
name|void
name|localizationCompleted
parameter_list|(
name|LocalResourceRequest
name|req
parameter_list|,
name|boolean
name|success
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

