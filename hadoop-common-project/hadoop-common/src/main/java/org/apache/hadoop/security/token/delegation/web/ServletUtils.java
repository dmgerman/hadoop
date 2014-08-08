begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.token.delegation.web
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|web
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
name|http
operator|.
name|NameValuePair
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|utils
operator|.
name|URLEncodedUtils
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
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Servlet utility methods.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ServletUtils
class|class
name|ServletUtils
block|{
DECL|field|UTF8_CHARSET
specifier|private
specifier|static
specifier|final
name|Charset
name|UTF8_CHARSET
init|=
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
comment|/**    * Extract a query string parameter without triggering http parameters    * processing by the servlet container.    *    * @param request the request    * @param name the parameter to get the value.    * @return the parameter value, or<code>NULL</code> if the parameter is not    * defined.    * @throws IOException thrown if there was an error parsing the query string.    */
DECL|method|getParameter (HttpServletRequest request, String name)
specifier|public
specifier|static
name|String
name|getParameter
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|NameValuePair
argument_list|>
name|list
init|=
name|URLEncodedUtils
operator|.
name|parse
argument_list|(
name|request
operator|.
name|getQueryString
argument_list|()
argument_list|,
name|UTF8_CHARSET
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|NameValuePair
name|nv
range|:
name|list
control|)
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|nv
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|nv
operator|.
name|getValue
argument_list|()
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

