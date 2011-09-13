begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.hs.webapp
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|hs
operator|.
name|webapp
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
name|conf
operator|.
name|Configuration
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
name|v2
operator|.
name|app
operator|.
name|webapp
operator|.
name|App
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
name|v2
operator|.
name|app
operator|.
name|webapp
operator|.
name|AppController
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
name|webapp
operator|.
name|View
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_comment
comment|/**  * This class renders the various pages that the History Server WebApp supports  */
end_comment

begin_class
DECL|class|HsController
specifier|public
class|class
name|HsController
extends|extends
name|AppController
block|{
DECL|method|HsController (App app, Configuration conf, RequestContext ctx)
annotation|@
name|Inject
name|HsController
parameter_list|(
name|App
name|app
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|RequestContext
name|ctx
parameter_list|)
block|{
name|super
argument_list|(
name|app
argument_list|,
name|conf
argument_list|,
name|ctx
argument_list|,
literal|"History"
argument_list|)
expr_stmt|;
block|}
comment|/*    * (non-Javadoc)    * @see org.apache.hadoop.mapreduce.v2.app.webapp.AppController#index()    */
annotation|@
name|Override
DECL|method|index ()
specifier|public
name|void
name|index
parameter_list|()
block|{
name|setTitle
argument_list|(
literal|"JobHistory"
argument_list|)
expr_stmt|;
block|}
comment|/*    * (non-Javadoc)    * @see org.apache.hadoop.mapreduce.v2.app.webapp.AppController#jobPage()    */
annotation|@
name|Override
DECL|method|jobPage ()
specifier|protected
name|Class
argument_list|<
name|?
extends|extends
name|View
argument_list|>
name|jobPage
parameter_list|()
block|{
return|return
name|HsJobPage
operator|.
name|class
return|;
block|}
comment|/*    * (non-Javadoc)    * @see org.apache.hadoop.mapreduce.v2.app.webapp.AppController#countersPage()    */
annotation|@
name|Override
DECL|method|countersPage ()
specifier|protected
name|Class
argument_list|<
name|?
extends|extends
name|View
argument_list|>
name|countersPage
parameter_list|()
block|{
return|return
name|HsCountersPage
operator|.
name|class
return|;
block|}
comment|/*    * (non-Javadoc)    * @see org.apache.hadoop.mapreduce.v2.app.webapp.AppController#tasksPage()    */
annotation|@
name|Override
DECL|method|tasksPage ()
specifier|protected
name|Class
argument_list|<
name|?
extends|extends
name|View
argument_list|>
name|tasksPage
parameter_list|()
block|{
return|return
name|HsTasksPage
operator|.
name|class
return|;
block|}
comment|/*    * (non-Javadoc)    * @see org.apache.hadoop.mapreduce.v2.app.webapp.AppController#taskPage()    */
annotation|@
name|Override
DECL|method|taskPage ()
specifier|protected
name|Class
argument_list|<
name|?
extends|extends
name|View
argument_list|>
name|taskPage
parameter_list|()
block|{
return|return
name|HsTaskPage
operator|.
name|class
return|;
block|}
comment|/*    * (non-Javadoc)    * @see org.apache.hadoop.mapreduce.v2.app.webapp.AppController#attemptsPage()    */
annotation|@
name|Override
DECL|method|attemptsPage ()
specifier|protected
name|Class
argument_list|<
name|?
extends|extends
name|View
argument_list|>
name|attemptsPage
parameter_list|()
block|{
return|return
name|HsAttemptsPage
operator|.
name|class
return|;
block|}
comment|// Need all of these methods here also as Guice doesn't look into parent
comment|// classes.
comment|/*    * (non-Javadoc)    * @see org.apache.hadoop.mapreduce.v2.app.webapp.AppController#job()    */
annotation|@
name|Override
DECL|method|job ()
specifier|public
name|void
name|job
parameter_list|()
block|{
name|super
operator|.
name|job
argument_list|()
expr_stmt|;
block|}
comment|/*    * (non-Javadoc)    * @see org.apache.hadoop.mapreduce.v2.app.webapp.AppController#jobCounters()    */
annotation|@
name|Override
DECL|method|jobCounters ()
specifier|public
name|void
name|jobCounters
parameter_list|()
block|{
name|super
operator|.
name|jobCounters
argument_list|()
expr_stmt|;
block|}
comment|/*    * (non-Javadoc)    * @see org.apache.hadoop.mapreduce.v2.app.webapp.AppController#tasks()    */
annotation|@
name|Override
DECL|method|tasks ()
specifier|public
name|void
name|tasks
parameter_list|()
block|{
name|super
operator|.
name|tasks
argument_list|()
expr_stmt|;
block|}
comment|/*    * (non-Javadoc)    * @see org.apache.hadoop.mapreduce.v2.app.webapp.AppController#task()    */
annotation|@
name|Override
DECL|method|task ()
specifier|public
name|void
name|task
parameter_list|()
block|{
name|super
operator|.
name|task
argument_list|()
expr_stmt|;
block|}
comment|/*    * (non-Javadoc)    * @see org.apache.hadoop.mapreduce.v2.app.webapp.AppController#attempts()    */
annotation|@
name|Override
DECL|method|attempts ()
specifier|public
name|void
name|attempts
parameter_list|()
block|{
name|super
operator|.
name|attempts
argument_list|()
expr_stmt|;
block|}
comment|/**    * @return the page that will be used to render the /conf page    */
DECL|method|confPage ()
specifier|protected
name|Class
argument_list|<
name|?
extends|extends
name|View
argument_list|>
name|confPage
parameter_list|()
block|{
return|return
name|HsConfPage
operator|.
name|class
return|;
block|}
comment|/**    * Render the /conf page    */
DECL|method|conf ()
specifier|public
name|void
name|conf
parameter_list|()
block|{
name|requireJob
argument_list|()
expr_stmt|;
name|render
argument_list|(
name|confPage
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * @return the page about the current server.    */
DECL|method|aboutPage ()
specifier|protected
name|Class
argument_list|<
name|?
extends|extends
name|View
argument_list|>
name|aboutPage
parameter_list|()
block|{
return|return
name|HsAboutPage
operator|.
name|class
return|;
block|}
comment|/**    * Render a page about the current server.    */
DECL|method|about ()
specifier|public
name|void
name|about
parameter_list|()
block|{
name|render
argument_list|(
name|aboutPage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

