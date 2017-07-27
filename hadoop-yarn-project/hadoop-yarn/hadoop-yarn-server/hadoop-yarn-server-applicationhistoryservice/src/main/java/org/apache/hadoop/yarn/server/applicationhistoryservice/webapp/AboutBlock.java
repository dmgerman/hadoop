begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.applicationhistoryservice.webapp
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
operator|.
name|webapp
package|;
end_package

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
name|timeline
operator|.
name|TimelineAbout
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
name|timeline
operator|.
name|TimelineUtils
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
name|InfoBlock
import|;
end_import

begin_class
DECL|class|AboutBlock
specifier|public
class|class
name|AboutBlock
extends|extends
name|HtmlBlock
block|{
annotation|@
name|Inject
DECL|method|AboutBlock (View.ViewContext ctx)
name|AboutBlock
parameter_list|(
name|View
operator|.
name|ViewContext
name|ctx
parameter_list|)
block|{
name|super
argument_list|(
name|ctx
argument_list|)
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
name|TimelineAbout
name|tsInfo
init|=
name|TimelineUtils
operator|.
name|createTimelineAbout
argument_list|(
literal|"Timeline Server - Generic History Service UI"
argument_list|)
decl_stmt|;
name|info
argument_list|(
literal|"Timeline Server Overview"
argument_list|)
operator|.
name|__
argument_list|(
literal|"Timeline Server Version:"
argument_list|,
name|tsInfo
operator|.
name|getTimelineServiceBuildVersion
argument_list|()
operator|+
literal|" on "
operator|+
name|tsInfo
operator|.
name|getTimelineServiceVersionBuiltOn
argument_list|()
argument_list|)
operator|.
name|__
argument_list|(
literal|"Hadoop Version:"
argument_list|,
name|tsInfo
operator|.
name|getHadoopBuildVersion
argument_list|()
operator|+
literal|" on "
operator|+
name|tsInfo
operator|.
name|getHadoopVersionBuiltOn
argument_list|()
argument_list|)
expr_stmt|;
name|html
operator|.
name|__
argument_list|(
name|InfoBlock
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

