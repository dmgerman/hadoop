begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfsproxy
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfsproxy
package|;
end_package

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
name|namenode
operator|.
name|ListPathsServlet
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
name|conf
operator|.
name|Configuration
import|;
end_import

begin_comment
comment|/** {@inheritDoc} */
end_comment

begin_class
DECL|class|ProxyListPathsServlet
specifier|public
class|class
name|ProxyListPathsServlet
extends|extends
name|ListPathsServlet
block|{
comment|/** For java.io.Serializable */
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getUGI (HttpServletRequest request, Configuration conf)
specifier|protected
name|UserGroupInformation
name|getUGI
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|userID
init|=
operator|(
name|String
operator|)
name|request
operator|.
name|getAttribute
argument_list|(
literal|"org.apache.hadoop.hdfsproxy.authorized.userID"
argument_list|)
decl_stmt|;
return|return
name|ProxyUtil
operator|.
name|getProxyUGIFor
argument_list|(
name|userID
argument_list|)
return|;
block|}
block|}
end_class

end_unit

