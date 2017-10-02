begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.router.webapp
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
name|router
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
name|server
operator|.
name|resourcemanager
operator|.
name|webapp
operator|.
name|JAXBContextResolver
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
name|router
operator|.
name|Router
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
name|GenericExceptionHandler
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
name|WebApp
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

begin_comment
comment|/**  * The Router webapp.  */
end_comment

begin_class
DECL|class|RouterWebApp
specifier|public
class|class
name|RouterWebApp
extends|extends
name|WebApp
implements|implements
name|YarnWebParams
block|{
DECL|field|router
specifier|private
name|Router
name|router
decl_stmt|;
DECL|method|RouterWebApp (Router router)
specifier|public
name|RouterWebApp
parameter_list|(
name|Router
name|router
parameter_list|)
block|{
name|this
operator|.
name|router
operator|=
name|router
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|bind
argument_list|(
name|JAXBContextResolver
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|RouterWebServices
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|GenericExceptionHandler
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|RouterWebApp
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|router
operator|!=
literal|null
condition|)
block|{
name|bind
argument_list|(
name|Router
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|router
argument_list|)
expr_stmt|;
block|}
name|route
argument_list|(
literal|"/"
argument_list|,
name|RouterController
operator|.
name|class
argument_list|)
expr_stmt|;
name|route
argument_list|(
literal|"/cluster"
argument_list|,
name|RouterController
operator|.
name|class
argument_list|,
literal|"about"
argument_list|)
expr_stmt|;
name|route
argument_list|(
literal|"/about"
argument_list|,
name|RouterController
operator|.
name|class
argument_list|,
literal|"about"
argument_list|)
expr_stmt|;
name|route
argument_list|(
literal|"/apps"
argument_list|,
name|RouterController
operator|.
name|class
argument_list|,
literal|"apps"
argument_list|)
expr_stmt|;
name|route
argument_list|(
literal|"/nodes"
argument_list|,
name|RouterController
operator|.
name|class
argument_list|,
literal|"nodes"
argument_list|)
expr_stmt|;
name|route
argument_list|(
literal|"/federation"
argument_list|,
name|RouterController
operator|.
name|class
argument_list|,
literal|"federation"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

