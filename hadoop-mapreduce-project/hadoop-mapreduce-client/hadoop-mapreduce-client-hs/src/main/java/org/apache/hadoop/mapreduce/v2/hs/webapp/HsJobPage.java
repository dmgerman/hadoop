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
import|import static
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
name|AMParams
operator|.
name|JOB_ID
import|;
end_import

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
name|util
operator|.
name|StringHelper
operator|.
name|join
import|;
end_import

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
name|ACCORDION
import|;
end_import

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
name|initID
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
name|SubView
import|;
end_import

begin_comment
comment|/**  * Render a page that describes a specific job.  */
end_comment

begin_class
DECL|class|HsJobPage
specifier|public
class|class
name|HsJobPage
extends|extends
name|HsView
block|{
comment|/*    * (non-Javadoc)    * @see org.apache.hadoop.mapreduce.v2.hs.webapp.HsView#preHead(org.apache.hadoop.yarn.webapp.hamlet.Hamlet.HTML)    */
DECL|method|preHead (Page.HTML<__> html)
annotation|@
name|Override
specifier|protected
name|void
name|preHead
parameter_list|(
name|Page
operator|.
name|HTML
argument_list|<
name|__
argument_list|>
name|html
parameter_list|)
block|{
name|String
name|jobID
init|=
name|$
argument_list|(
name|JOB_ID
argument_list|)
decl_stmt|;
name|set
argument_list|(
name|TITLE
argument_list|,
name|jobID
operator|.
name|isEmpty
argument_list|()
condition|?
literal|"Bad request: missing job ID"
else|:
name|join
argument_list|(
literal|"MapReduce Job "
argument_list|,
name|$
argument_list|(
name|JOB_ID
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|commonPreHead
argument_list|(
name|html
argument_list|)
expr_stmt|;
comment|//Override the nav config from the commonPreHead
name|set
argument_list|(
name|initID
argument_list|(
name|ACCORDION
argument_list|,
literal|"nav"
argument_list|)
argument_list|,
literal|"{autoHeight:false, active:1}"
argument_list|)
expr_stmt|;
block|}
comment|/**    * The content of this page is the JobBlock    * @return HsJobBlock.class    */
DECL|method|content ()
annotation|@
name|Override
specifier|protected
name|Class
argument_list|<
name|?
extends|extends
name|SubView
argument_list|>
name|content
parameter_list|()
block|{
return|return
name|HsJobBlock
operator|.
name|class
return|;
block|}
block|}
end_class

end_unit

