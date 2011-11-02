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
name|io
operator|.
name|IOException
import|;
end_import

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
name|javax
operator|.
name|servlet
operator|.
name|ServletContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
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
name|hdfs
operator|.
name|server
operator|.
name|common
operator|.
name|JspHelper
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
name|security
operator|.
name|UserGroupInformation
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
name|security
operator|.
name|UserGroupInformation
operator|.
name|AuthenticationMethod
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

begin_comment
comment|/** Inject user information to http operations. */
end_comment

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
name|UserGroupInformation
argument_list|>
implements|implements
name|InjectableProvider
argument_list|<
name|Context
argument_list|,
name|Type
argument_list|>
block|{
DECL|field|request
annotation|@
name|Context
name|HttpServletRequest
name|request
decl_stmt|;
DECL|field|servletcontext
annotation|@
name|Context
name|ServletContext
name|servletcontext
decl_stmt|;
annotation|@
name|Override
DECL|method|getValue (final HttpContext context)
specifier|public
name|UserGroupInformation
name|getValue
parameter_list|(
specifier|final
name|HttpContext
name|context
parameter_list|)
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|(
name|Configuration
operator|)
name|servletcontext
operator|.
name|getAttribute
argument_list|(
name|JspHelper
operator|.
name|CURRENT_CONF
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|JspHelper
operator|.
name|getUGI
argument_list|(
name|servletcontext
argument_list|,
name|request
argument_list|,
name|conf
argument_list|,
name|AuthenticationMethod
operator|.
name|KERBEROS
argument_list|,
literal|false
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
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
name|UserGroupInformation
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
name|UserGroupInformation
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

