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
name|java
operator|.
name|util
operator|.
name|Date
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
name|util
operator|.
name|StringUtils
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
name|Context
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
name|ResourceView
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
name|webapp
operator|.
name|dao
operator|.
name|NodeInfo
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
name|HTML
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
DECL|class|NodePage
specifier|public
class|class
name|NodePage
extends|extends
name|NMView
block|{
DECL|field|BYTES_IN_MB
specifier|private
specifier|static
specifier|final
name|long
name|BYTES_IN_MB
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
annotation|@
name|Override
DECL|method|commonPreHead (HTML<_> html)
specifier|protected
name|void
name|commonPreHead
parameter_list|(
name|HTML
argument_list|<
name|_
argument_list|>
name|html
parameter_list|)
block|{
name|super
operator|.
name|commonPreHead
argument_list|(
name|html
argument_list|)
expr_stmt|;
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
annotation|@
name|Override
DECL|method|content ()
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
name|NodeBlock
operator|.
name|class
return|;
block|}
DECL|class|NodeBlock
specifier|public
specifier|static
class|class
name|NodeBlock
extends|extends
name|HtmlBlock
block|{
DECL|field|context
specifier|private
specifier|final
name|Context
name|context
decl_stmt|;
DECL|field|resourceView
specifier|private
specifier|final
name|ResourceView
name|resourceView
decl_stmt|;
annotation|@
name|Inject
DECL|method|NodeBlock (Context context, ResourceView resourceView)
specifier|public
name|NodeBlock
parameter_list|(
name|Context
name|context
parameter_list|,
name|ResourceView
name|resourceView
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|resourceView
operator|=
name|resourceView
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
name|NodeInfo
name|info
init|=
operator|new
name|NodeInfo
argument_list|(
name|this
operator|.
name|context
argument_list|,
name|this
operator|.
name|resourceView
argument_list|)
decl_stmt|;
name|info
argument_list|(
literal|"NodeManager information"
argument_list|)
operator|.
name|_
argument_list|(
literal|"Total Vmem allocated for Containers"
argument_list|,
name|StringUtils
operator|.
name|byteDesc
argument_list|(
name|info
operator|.
name|getTotalVmemAllocated
argument_list|()
operator|*
name|BYTES_IN_MB
argument_list|)
argument_list|)
operator|.
name|_
argument_list|(
literal|"Vmem enforcement enabled"
argument_list|,
name|info
operator|.
name|isVmemCheckEnabled
argument_list|()
argument_list|)
operator|.
name|_
argument_list|(
literal|"Total Pmem allocated for Container"
argument_list|,
name|StringUtils
operator|.
name|byteDesc
argument_list|(
name|info
operator|.
name|getTotalPmemAllocated
argument_list|()
operator|*
name|BYTES_IN_MB
argument_list|)
argument_list|)
operator|.
name|_
argument_list|(
literal|"Pmem enforcement enabled"
argument_list|,
name|info
operator|.
name|isPmemCheckEnabled
argument_list|()
argument_list|)
operator|.
name|_
argument_list|(
literal|"Total VCores allocated for Containers"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|info
operator|.
name|getTotalVCoresAllocated
argument_list|()
argument_list|)
argument_list|)
operator|.
name|_
argument_list|(
literal|"NodeHealthyStatus"
argument_list|,
name|info
operator|.
name|getHealthStatus
argument_list|()
argument_list|)
operator|.
name|_
argument_list|(
literal|"LastNodeHealthTime"
argument_list|,
operator|new
name|Date
argument_list|(
name|info
operator|.
name|getLastNodeUpdateTime
argument_list|()
argument_list|)
argument_list|)
operator|.
name|_
argument_list|(
literal|"NodeHealthReport"
argument_list|,
name|info
operator|.
name|getHealthReport
argument_list|()
argument_list|)
operator|.
name|_
argument_list|(
literal|"NodeManager started on"
argument_list|,
operator|new
name|Date
argument_list|(
name|info
operator|.
name|getNMStartupTime
argument_list|()
argument_list|)
argument_list|)
operator|.
name|_
argument_list|(
literal|"NodeManager Version:"
argument_list|,
name|info
operator|.
name|getNMBuildVersion
argument_list|()
operator|+
literal|" on "
operator|+
name|info
operator|.
name|getNMVersionBuiltOn
argument_list|()
argument_list|)
operator|.
name|_
argument_list|(
literal|"Hadoop Version:"
argument_list|,
name|info
operator|.
name|getHadoopBuildVersion
argument_list|()
operator|+
literal|" on "
operator|+
name|info
operator|.
name|getHadoopVersionBuiltOn
argument_list|()
argument_list|)
expr_stmt|;
name|html
operator|.
name|_
argument_list|(
name|InfoBlock
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

