begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager
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
name|service
operator|.
name|CompositeService
import|;
end_import

begin_comment
comment|/**  * The class which provides functionality of checking the health of the node and  * reporting back to the service for which the health checker has been asked to  * report.  */
end_comment

begin_class
DECL|class|NodeHealthCheckerService
specifier|public
class|class
name|NodeHealthCheckerService
extends|extends
name|CompositeService
block|{
DECL|field|nodeHealthScriptRunner
specifier|private
name|NodeHealthScriptRunner
name|nodeHealthScriptRunner
decl_stmt|;
DECL|field|dirsHandler
specifier|private
name|LocalDirsHandlerService
name|dirsHandler
decl_stmt|;
DECL|field|SEPARATOR
specifier|static
specifier|final
name|String
name|SEPARATOR
init|=
literal|";"
decl_stmt|;
DECL|method|NodeHealthCheckerService ()
specifier|public
name|NodeHealthCheckerService
parameter_list|()
block|{
name|super
argument_list|(
name|NodeHealthCheckerService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|dirsHandler
operator|=
operator|new
name|LocalDirsHandlerService
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|protected
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|NodeHealthScriptRunner
operator|.
name|shouldRun
argument_list|(
name|conf
argument_list|)
condition|)
block|{
name|nodeHealthScriptRunner
operator|=
operator|new
name|NodeHealthScriptRunner
argument_list|()
expr_stmt|;
name|addService
argument_list|(
name|nodeHealthScriptRunner
argument_list|)
expr_stmt|;
block|}
name|addService
argument_list|(
name|dirsHandler
argument_list|)
expr_stmt|;
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * @return the reporting string of health of the node    */
DECL|method|getHealthReport ()
name|String
name|getHealthReport
parameter_list|()
block|{
name|String
name|scriptReport
init|=
operator|(
name|nodeHealthScriptRunner
operator|==
literal|null
operator|)
condition|?
literal|""
else|:
name|nodeHealthScriptRunner
operator|.
name|getHealthReport
argument_list|()
decl_stmt|;
if|if
condition|(
name|scriptReport
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
return|return
name|dirsHandler
operator|.
name|getDisksHealthReport
argument_list|(
literal|false
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|scriptReport
operator|.
name|concat
argument_list|(
name|SEPARATOR
operator|+
name|dirsHandler
operator|.
name|getDisksHealthReport
argument_list|(
literal|false
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/**    * @return<em>true</em> if the node is healthy    */
DECL|method|isHealthy ()
name|boolean
name|isHealthy
parameter_list|()
block|{
name|boolean
name|scriptHealthStatus
init|=
operator|(
name|nodeHealthScriptRunner
operator|==
literal|null
operator|)
condition|?
literal|true
else|:
name|nodeHealthScriptRunner
operator|.
name|isHealthy
argument_list|()
decl_stmt|;
return|return
name|scriptHealthStatus
operator|&&
name|dirsHandler
operator|.
name|areDisksHealthy
argument_list|()
return|;
block|}
comment|/**    * @return when the last time the node health status is reported    */
DECL|method|getLastHealthReportTime ()
name|long
name|getLastHealthReportTime
parameter_list|()
block|{
name|long
name|diskCheckTime
init|=
name|dirsHandler
operator|.
name|getLastDisksCheckTime
argument_list|()
decl_stmt|;
name|long
name|lastReportTime
init|=
operator|(
name|nodeHealthScriptRunner
operator|==
literal|null
operator|)
condition|?
name|diskCheckTime
else|:
name|Math
operator|.
name|max
argument_list|(
name|nodeHealthScriptRunner
operator|.
name|getLastReportedTime
argument_list|()
argument_list|,
name|diskCheckTime
argument_list|)
decl_stmt|;
return|return
name|lastReportTime
return|;
block|}
comment|/**    * @return the disk handler    */
DECL|method|getDiskHandler ()
specifier|public
name|LocalDirsHandlerService
name|getDiskHandler
parameter_list|()
block|{
return|return
name|dirsHandler
return|;
block|}
comment|/**    * @return the node health script runner    */
DECL|method|getNodeHealthScriptRunner ()
name|NodeHealthScriptRunner
name|getNodeHealthScriptRunner
parameter_list|()
block|{
return|return
name|nodeHealthScriptRunner
return|;
block|}
block|}
end_class

end_unit

