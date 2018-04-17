begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.webapp
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
name|webapp
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|webapp
operator|.
name|view
operator|.
name|JQueryUI
operator|.
name|tableInit
import|;
end_import

begin_class
DECL|class|WebPageUtils
specifier|public
class|class
name|WebPageUtils
block|{
DECL|method|appsTableInit ()
specifier|public
specifier|static
name|String
name|appsTableInit
parameter_list|()
block|{
return|return
name|appsTableInit
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|appsTableInit (boolean isResourceManager)
specifier|public
specifier|static
name|String
name|appsTableInit
parameter_list|(
name|boolean
name|isResourceManager
parameter_list|)
block|{
return|return
name|appsTableInit
argument_list|(
literal|false
argument_list|,
name|isResourceManager
argument_list|)
return|;
block|}
DECL|method|appsTableInit ( boolean isFairSchedulerPage, boolean isResourceManager)
specifier|public
specifier|static
name|String
name|appsTableInit
parameter_list|(
name|boolean
name|isFairSchedulerPage
parameter_list|,
name|boolean
name|isResourceManager
parameter_list|)
block|{
comment|// id, user, name, queue, starttime, finishtime, state, status, progress, ui
comment|// FairSchedulerPage's table is a bit different
return|return
name|tableInit
argument_list|()
operator|.
name|append
argument_list|(
literal|", 'aaData': appsTableData"
argument_list|)
operator|.
name|append
argument_list|(
literal|", bDeferRender: true"
argument_list|)
operator|.
name|append
argument_list|(
literal|", bProcessing: true"
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n, aoColumnDefs: "
argument_list|)
operator|.
name|append
argument_list|(
name|getAppsTableColumnDefs
argument_list|(
name|isFairSchedulerPage
argument_list|,
name|isResourceManager
argument_list|)
argument_list|)
comment|// Sort by id upon page load
operator|.
name|append
argument_list|(
literal|", aaSorting: [[0, 'desc']]}"
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getAppsTableColumnDefs ( boolean isFairSchedulerPage, boolean isResourceManager)
specifier|private
specifier|static
name|String
name|getAppsTableColumnDefs
parameter_list|(
name|boolean
name|isFairSchedulerPage
parameter_list|,
name|boolean
name|isResourceManager
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"[\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"{'sType':'natural', 'aTargets': [0]"
argument_list|)
operator|.
name|append
argument_list|(
literal|", 'mRender': parseHadoopID }"
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n, {'sType':'numeric', 'aTargets': [6, 7, 8]"
argument_list|)
operator|.
name|append
argument_list|(
literal|", 'mRender': renderHadoopDate }"
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n, {'sType':'numeric', bSearchable:false, 'aTargets':"
argument_list|)
expr_stmt|;
if|if
condition|(
name|isFairSchedulerPage
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"[15]"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isResourceManager
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"[17]"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"[9]"
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|", 'mRender': parseHadoopProgress }]"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|attemptsTableInit ()
specifier|public
specifier|static
name|String
name|attemptsTableInit
parameter_list|()
block|{
return|return
name|tableInit
argument_list|()
operator|.
name|append
argument_list|(
literal|", 'aaData': attemptsTableData"
argument_list|)
operator|.
name|append
argument_list|(
literal|", bDeferRender: true"
argument_list|)
operator|.
name|append
argument_list|(
literal|", bProcessing: true"
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n, aoColumnDefs: "
argument_list|)
operator|.
name|append
argument_list|(
name|getAttemptsTableColumnDefs
argument_list|()
argument_list|)
comment|// Sort by id upon page load
operator|.
name|append
argument_list|(
literal|", aaSorting: [[0, 'desc']]}"
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getAttemptsTableColumnDefs ()
specifier|private
specifier|static
name|String
name|getAttemptsTableColumnDefs
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
return|return
name|sb
operator|.
name|append
argument_list|(
literal|"[\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"{'sType':'natural', 'aTargets': [0]"
argument_list|)
operator|.
name|append
argument_list|(
literal|", 'mRender': parseHadoopID }"
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n, {'sType':'numeric', 'aTargets': [1]"
argument_list|)
operator|.
name|append
argument_list|(
literal|", 'mRender': renderHadoopDate }]"
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|containersTableInit ()
specifier|public
specifier|static
name|String
name|containersTableInit
parameter_list|()
block|{
return|return
name|tableInit
argument_list|()
operator|.
name|append
argument_list|(
literal|", 'aaData': containersTableData"
argument_list|)
operator|.
name|append
argument_list|(
literal|", bDeferRender: true"
argument_list|)
operator|.
name|append
argument_list|(
literal|", bProcessing: true"
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n, aoColumnDefs: "
argument_list|)
operator|.
name|append
argument_list|(
name|getContainersTableColumnDefs
argument_list|()
argument_list|)
comment|// Sort by id upon page load
operator|.
name|append
argument_list|(
literal|", aaSorting: [[0, 'desc']]}"
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getContainersTableColumnDefs ()
specifier|private
specifier|static
name|String
name|getContainersTableColumnDefs
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
return|return
name|sb
operator|.
name|append
argument_list|(
literal|"[\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"{'sType':'natural', 'aTargets': [0]"
argument_list|)
operator|.
name|append
argument_list|(
literal|", 'mRender': parseHadoopID }]"
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|resourceRequestsTableInit ()
specifier|public
specifier|static
name|String
name|resourceRequestsTableInit
parameter_list|()
block|{
return|return
name|tableInit
argument_list|()
operator|.
name|append
argument_list|(
literal|", 'aaData': resourceRequestsTableData"
argument_list|)
operator|.
name|append
argument_list|(
literal|", bDeferRender: true"
argument_list|)
operator|.
name|append
argument_list|(
literal|", bProcessing: true}"
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

