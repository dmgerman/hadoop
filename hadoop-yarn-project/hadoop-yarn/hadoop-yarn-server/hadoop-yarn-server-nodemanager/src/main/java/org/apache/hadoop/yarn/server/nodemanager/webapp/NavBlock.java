begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.webapp
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
name|webapp
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|impl
operator|.
name|Log4JLogger
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
name|yarn
operator|.
name|util
operator|.
name|Log4jWarningErrorMetricsAppender
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
name|YarnWebParams
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
name|hamlet2
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
name|util
operator|.
name|WebAppUtils
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

begin_class
DECL|class|NavBlock
specifier|public
class|class
name|NavBlock
extends|extends
name|HtmlBlock
implements|implements
name|YarnWebParams
block|{
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
annotation|@
name|Inject
DECL|method|NavBlock (Configuration conf)
specifier|public
name|NavBlock
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|render (Block html)
specifier|protected
name|void
name|render
parameter_list|(
name|Block
name|html
parameter_list|)
block|{
name|boolean
name|addErrorsAndWarningsLink
init|=
literal|false
decl_stmt|;
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|NMErrorsAndWarningsPage
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|log
operator|instanceof
name|Log4JLogger
condition|)
block|{
name|Log4jWarningErrorMetricsAppender
name|appender
init|=
name|Log4jWarningErrorMetricsAppender
operator|.
name|findAppender
argument_list|()
decl_stmt|;
if|if
condition|(
name|appender
operator|!=
literal|null
condition|)
block|{
name|addErrorsAndWarningsLink
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|String
name|RMWebAppURL
init|=
name|WebAppUtils
operator|.
name|getResolvedRMWebAppURLWithScheme
argument_list|(
name|this
operator|.
name|conf
argument_list|)
decl_stmt|;
name|Hamlet
operator|.
name|UL
argument_list|<
name|Hamlet
operator|.
name|DIV
argument_list|<
name|Hamlet
argument_list|>
argument_list|>
name|ul
init|=
name|html
operator|.
name|div
argument_list|(
literal|"#nav"
argument_list|)
operator|.
name|h3
argument_list|()
operator|.
name|__
argument_list|(
literal|"ResourceManager"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|ul
argument_list|()
operator|.
name|li
argument_list|()
operator|.
name|a
argument_list|(
name|RMWebAppURL
argument_list|,
literal|"RM Home"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
operator|.
name|h3
argument_list|()
operator|.
name|__
argument_list|(
literal|"NodeManager"
argument_list|)
operator|.
name|__
argument_list|()
comment|// TODO: Problem if no header like this
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
literal|"node"
argument_list|)
argument_list|,
literal|"Node Information"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|li
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"allApplications"
argument_list|)
argument_list|,
literal|"List of Applications"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|li
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"allContainers"
argument_list|)
argument_list|,
literal|"List of Containers"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
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
name|__
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
name|__
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
name|__
argument_list|()
operator|.
name|li
argument_list|()
operator|.
name|a
argument_list|(
literal|"/jmx?qry=Hadoop:*"
argument_list|,
literal|"Server metrics"
argument_list|)
operator|.
name|__
argument_list|()
decl_stmt|;
if|if
condition|(
name|addErrorsAndWarningsLink
condition|)
block|{
name|ul
operator|.
name|li
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"errors-and-warnings"
argument_list|)
argument_list|,
literal|"Errors/Warnings"
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
block|}
name|ul
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

