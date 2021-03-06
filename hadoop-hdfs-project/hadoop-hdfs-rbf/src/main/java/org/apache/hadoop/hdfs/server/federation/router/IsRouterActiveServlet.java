begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.router
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|router
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
name|http
operator|.
name|IsActiveServlet
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletContext
import|;
end_import

begin_comment
comment|/**  * Detect if the Router is active and ready to serve requests.  */
end_comment

begin_class
DECL|class|IsRouterActiveServlet
specifier|public
class|class
name|IsRouterActiveServlet
extends|extends
name|IsActiveServlet
block|{
annotation|@
name|Override
DECL|method|isActive ()
specifier|protected
name|boolean
name|isActive
parameter_list|()
block|{
specifier|final
name|ServletContext
name|context
init|=
name|getServletContext
argument_list|()
decl_stmt|;
specifier|final
name|Router
name|router
init|=
name|RouterHttpServer
operator|.
name|getRouterFromContext
argument_list|(
name|context
argument_list|)
decl_stmt|;
specifier|final
name|RouterServiceState
name|routerState
init|=
name|router
operator|.
name|getRouterState
argument_list|()
decl_stmt|;
return|return
name|routerState
operator|==
name|RouterServiceState
operator|.
name|RUNNING
return|;
block|}
block|}
end_class

end_unit

