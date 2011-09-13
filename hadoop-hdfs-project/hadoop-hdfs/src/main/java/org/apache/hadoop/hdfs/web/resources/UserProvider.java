begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.web.resources
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|web
operator|.
name|resources
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Type
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Context
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|ext
operator|.
name|Provider
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|core
operator|.
name|HttpContext
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|core
operator|.
name|spi
operator|.
name|component
operator|.
name|ComponentContext
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|core
operator|.
name|spi
operator|.
name|component
operator|.
name|ComponentScope
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|server
operator|.
name|impl
operator|.
name|inject
operator|.
name|AbstractHttpContextInjectable
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|spi
operator|.
name|inject
operator|.
name|Injectable
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|spi
operator|.
name|inject
operator|.
name|InjectableProvider
import|;
end_import

begin_class
annotation|@
name|Provider
DECL|class|UserProvider
specifier|public
class|class
name|UserProvider
extends|extends
name|AbstractHttpContextInjectable
argument_list|<
name|Principal
argument_list|>
implements|implements
name|InjectableProvider
argument_list|<
name|Context
argument_list|,
name|Type
argument_list|>
block|{
annotation|@
name|Override
DECL|method|getValue (final HttpContext context)
specifier|public
name|Principal
name|getValue
parameter_list|(
specifier|final
name|HttpContext
name|context
parameter_list|)
block|{
comment|//get principal from the request
specifier|final
name|Principal
name|principal
init|=
name|context
operator|.
name|getRequest
argument_list|()
operator|.
name|getUserPrincipal
argument_list|()
decl_stmt|;
if|if
condition|(
name|principal
operator|!=
literal|null
condition|)
block|{
return|return
name|principal
return|;
block|}
comment|//get username from the parameter
specifier|final
name|String
name|username
init|=
name|context
operator|.
name|getRequest
argument_list|()
operator|.
name|getQueryParameters
argument_list|()
operator|.
name|getFirst
argument_list|(
name|UserParam
operator|.
name|NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|username
operator|!=
literal|null
condition|)
block|{
specifier|final
name|UserParam
name|userparam
init|=
operator|new
name|UserParam
argument_list|(
name|username
argument_list|)
decl_stmt|;
return|return
operator|new
name|Principal
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|userparam
operator|.
name|getValue
argument_list|()
return|;
block|}
block|}
return|;
block|}
comment|//user not found
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getScope ()
specifier|public
name|ComponentScope
name|getScope
parameter_list|()
block|{
return|return
name|ComponentScope
operator|.
name|PerRequest
return|;
block|}
annotation|@
name|Override
DECL|method|getInjectable ( final ComponentContext componentContext, final Context context, final Type type)
specifier|public
name|Injectable
argument_list|<
name|Principal
argument_list|>
name|getInjectable
parameter_list|(
specifier|final
name|ComponentContext
name|componentContext
parameter_list|,
specifier|final
name|Context
name|context
parameter_list|,
specifier|final
name|Type
name|type
parameter_list|)
block|{
return|return
name|type
operator|.
name|equals
argument_list|(
name|Principal
operator|.
name|class
argument_list|)
condition|?
name|this
else|:
literal|null
return|;
block|}
block|}
end_class

end_unit

