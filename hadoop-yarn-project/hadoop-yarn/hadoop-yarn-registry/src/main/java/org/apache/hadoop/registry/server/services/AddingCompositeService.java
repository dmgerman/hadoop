begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.registry.server.services
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|registry
operator|.
name|server
operator|.
name|services
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
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
name|Service
import|;
end_import

begin_comment
comment|/**  * Composite service that exports the add/remove methods.  *<p>  * This allows external classes to add services to these methods, after which  * they follow the same lifecyce.  *<p>  * It is essential that any service added is in a state where it can be moved  * on with that of the parent services. Specifically, do not add an uninited  * service to a parent that is already inited âas the<code>start</code>  * operation will then fail  *  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|AddingCompositeService
specifier|public
class|class
name|AddingCompositeService
extends|extends
name|CompositeService
block|{
DECL|method|AddingCompositeService (String name)
specifier|public
name|AddingCompositeService
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addService (Service service)
specifier|public
name|void
name|addService
parameter_list|(
name|Service
name|service
parameter_list|)
block|{
name|super
operator|.
name|addService
argument_list|(
name|service
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|removeService (Service service)
specifier|public
name|boolean
name|removeService
parameter_list|(
name|Service
name|service
parameter_list|)
block|{
return|return
name|super
operator|.
name|removeService
argument_list|(
name|service
argument_list|)
return|;
block|}
block|}
end_class

end_unit

