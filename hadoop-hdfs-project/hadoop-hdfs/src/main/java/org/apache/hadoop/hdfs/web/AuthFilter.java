begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.web
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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|FilterConfig
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
name|authentication
operator|.
name|server
operator|.
name|AuthenticationFilter
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
name|authentication
operator|.
name|server
operator|.
name|KerberosAuthenticationHandler
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
name|authentication
operator|.
name|server
operator|.
name|PseudoAuthenticationHandler
import|;
end_import

begin_comment
comment|/**  * Subclass of {@link AuthenticationFilter} that  * obtains Hadoop-Auth configuration for webhdfs.  */
end_comment

begin_class
DECL|class|AuthFilter
specifier|public
class|class
name|AuthFilter
extends|extends
name|AuthenticationFilter
block|{
DECL|field|CONF_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|CONF_PREFIX
init|=
literal|"dfs.web.authentication."
decl_stmt|;
comment|/**    * Returns the filter configuration properties,    * including the ones prefixed with {@link #CONF_PREFIX}.    * The prefix is removed from the returned property names.    *    * @param prefix parameter not used.    * @param config parameter not used.    * @return Hadoop-Auth configuration properties.    */
annotation|@
name|Override
DECL|method|getConfiguration (String prefix, FilterConfig config)
specifier|protected
name|Properties
name|getConfiguration
parameter_list|(
name|String
name|prefix
parameter_list|,
name|FilterConfig
name|config
parameter_list|)
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
specifier|final
name|Properties
name|p
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
comment|//set authentication type
name|p
operator|.
name|setProperty
argument_list|(
name|AUTH_TYPE
argument_list|,
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|?
name|KerberosAuthenticationHandler
operator|.
name|TYPE
else|:
name|PseudoAuthenticationHandler
operator|.
name|TYPE
argument_list|)
expr_stmt|;
comment|//For Pseudo Authentication, allow anonymous.
name|p
operator|.
name|setProperty
argument_list|(
name|PseudoAuthenticationHandler
operator|.
name|ANONYMOUS_ALLOWED
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
comment|//set cookie path
name|p
operator|.
name|setProperty
argument_list|(
name|COOKIE_PATH
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
comment|//set other configurations with CONF_PREFIX
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|conf
control|)
block|{
specifier|final
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
name|CONF_PREFIX
argument_list|)
condition|)
block|{
comment|//remove prefix from the key and set property
name|p
operator|.
name|setProperty
argument_list|(
name|key
operator|.
name|substring
argument_list|(
name|CONF_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|p
return|;
block|}
block|}
end_class

end_unit

