begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.webapp
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
name|app
operator|.
name|webapp
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Inject
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|webapp
operator|.
name|AMWebApp
operator|.
name|*
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
name|jobhistory
operator|.
name|JobHistoryParser
operator|.
name|AMInfo
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
name|util
operator|.
name|MRApps
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
name|hamlet
operator|.
name|Hamlet
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
name|hamlet
operator|.
name|Hamlet
operator|.
name|*
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
name|view
operator|.
name|HtmlBlock
import|;
end_import

begin_class
DECL|class|NavBlock
specifier|public
class|class
name|NavBlock
extends|extends
name|HtmlBlock
block|{
DECL|field|app
specifier|final
name|App
name|app
decl_stmt|;
DECL|method|NavBlock (App app)
annotation|@
name|Inject
name|NavBlock
parameter_list|(
name|App
name|app
parameter_list|)
block|{
name|this
operator|.
name|app
operator|=
name|app
expr_stmt|;
block|}
DECL|method|render (Block html)
annotation|@
name|Override
specifier|protected
name|void
name|render
parameter_list|(
name|Block
name|html
parameter_list|)
block|{
name|String
name|rmweb
init|=
name|$
argument_list|(
name|RM_WEB
argument_list|)
decl_stmt|;
name|DIV
argument_list|<
name|Hamlet
argument_list|>
name|nav
init|=
name|html
operator|.
name|div
argument_list|(
literal|"#nav"
argument_list|)
operator|.
name|h3
argument_list|(
literal|"Cluster"
argument_list|)
operator|.
name|ul
argument_list|()
operator|.
name|li
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
name|rmweb
argument_list|,
literal|"cluster"
argument_list|,
literal|"cluster"
argument_list|)
argument_list|,
literal|"About"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|li
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
name|rmweb
argument_list|,
literal|"cluster"
argument_list|,
literal|"apps"
argument_list|)
argument_list|,
literal|"Applications"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|li
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
name|rmweb
argument_list|,
literal|"cluster"
argument_list|,
literal|"scheduler"
argument_list|)
argument_list|,
literal|"Scheduler"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|h3
argument_list|(
literal|"Application"
argument_list|)
operator|.
name|ul
argument_list|()
operator|.
name|li
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"app/info"
argument_list|)
argument_list|,
literal|"About"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|li
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"app"
argument_list|)
argument_list|,
literal|"Jobs"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
decl_stmt|;
if|if
condition|(
name|app
operator|.
name|getJob
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|String
name|jobid
init|=
name|MRApps
operator|.
name|toString
argument_list|(
name|app
operator|.
name|getJob
argument_list|()
operator|.
name|getID
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|AMInfo
argument_list|>
name|amInfos
init|=
name|app
operator|.
name|getJob
argument_list|()
operator|.
name|getAMInfos
argument_list|()
decl_stmt|;
name|AMInfo
name|thisAmInfo
init|=
name|amInfos
operator|.
name|get
argument_list|(
name|amInfos
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|String
name|nodeHttpAddress
init|=
name|thisAmInfo
operator|.
name|getNodeManagerHost
argument_list|()
operator|+
literal|":"
operator|+
name|thisAmInfo
operator|.
name|getNodeManagerHttpPort
argument_list|()
decl_stmt|;
name|nav
operator|.
name|h3
argument_list|(
literal|"Job"
argument_list|)
operator|.
name|ul
argument_list|()
operator|.
name|li
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"job"
argument_list|,
name|jobid
argument_list|)
argument_list|,
literal|"Overview"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|li
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"jobcounters"
argument_list|,
name|jobid
argument_list|)
argument_list|,
literal|"Counters"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|li
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"conf"
argument_list|,
name|jobid
argument_list|)
argument_list|,
literal|"Configuration"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|li
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"tasks"
argument_list|,
name|jobid
argument_list|,
literal|"m"
argument_list|)
argument_list|,
literal|"Map tasks"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|li
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"tasks"
argument_list|,
name|jobid
argument_list|,
literal|"r"
argument_list|)
argument_list|,
literal|"Reduce tasks"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|li
argument_list|()
operator|.
name|a
argument_list|(
literal|".logslink"
argument_list|,
name|url
argument_list|(
literal|"http://"
argument_list|,
name|nodeHttpAddress
argument_list|,
literal|"node"
argument_list|,
literal|"containerlogs"
argument_list|,
name|thisAmInfo
operator|.
name|getContainerId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|app
operator|.
name|getJob
argument_list|()
operator|.
name|getUserName
argument_list|()
argument_list|)
argument_list|,
literal|"AM Logs"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
expr_stmt|;
if|if
condition|(
name|app
operator|.
name|getTask
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|String
name|taskid
init|=
name|MRApps
operator|.
name|toString
argument_list|(
name|app
operator|.
name|getTask
argument_list|()
operator|.
name|getID
argument_list|()
argument_list|)
decl_stmt|;
name|nav
operator|.
name|h3
argument_list|(
literal|"Task"
argument_list|)
operator|.
name|ul
argument_list|()
operator|.
name|li
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"task"
argument_list|,
name|taskid
argument_list|)
argument_list|,
literal|"Task Overview"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|li
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"taskcounters"
argument_list|,
name|taskid
argument_list|)
argument_list|,
literal|"Counters"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
block|}
name|nav
operator|.
name|h3
argument_list|(
literal|"Tools"
argument_list|)
operator|.
name|ul
argument_list|()
operator|.
name|li
argument_list|()
operator|.
name|a
argument_list|(
literal|"/conf"
argument_list|,
literal|"Configuration"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|li
argument_list|()
operator|.
name|a
argument_list|(
literal|"/logs"
argument_list|,
literal|"Local logs"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|li
argument_list|()
operator|.
name|a
argument_list|(
literal|"/stacks"
argument_list|,
literal|"Server stacks"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|li
argument_list|()
operator|.
name|a
argument_list|(
literal|"/metrics"
argument_list|,
literal|"Server metrics"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|div
argument_list|(
literal|"#themeswitcher"
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

