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
name|Controller
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
DECL|class|AHSController
specifier|public
class|class
name|AHSController
extends|extends
name|Controller
block|{
annotation|@
name|Inject
DECL|method|AHSController (RequestContext ctx)
name|AHSController
parameter_list|(
name|RequestContext
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
DECL|method|index ()
specifier|public
name|void
name|index
parameter_list|()
block|{
name|setTitle
argument_list|(
literal|"Application History"
argument_list|)
expr_stmt|;
block|}
DECL|method|app ()
specifier|public
name|void
name|app
parameter_list|()
block|{
name|render
argument_list|(
name|AppPage
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|appattempt ()
specifier|public
name|void
name|appattempt
parameter_list|()
block|{
name|render
argument_list|(
name|AppAttemptPage
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|container ()
specifier|public
name|void
name|container
parameter_list|()
block|{
name|render
argument_list|(
name|ContainerPage
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/**    * Render the logs page.    */
DECL|method|logs ()
specifier|public
name|void
name|logs
parameter_list|()
block|{
name|render
argument_list|(
name|AHSLogsPage
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|errorsAndWarnings ()
specifier|public
name|void
name|errorsAndWarnings
parameter_list|()
block|{
name|render
argument_list|(
name|AHSErrorsAndWarningsPage
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

