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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

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
name|ServletException
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
name|DFSConfigKeys
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestAuthFilter
specifier|public
class|class
name|TestAuthFilter
block|{
DECL|class|DummyFilterConfig
specifier|private
specifier|static
class|class
name|DummyFilterConfig
implements|implements
name|FilterConfig
block|{
DECL|field|map
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
decl_stmt|;
DECL|method|DummyFilterConfig (Map<String,String> map)
name|DummyFilterConfig
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
parameter_list|)
block|{
name|this
operator|.
name|map
operator|=
name|map
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFilterName ()
specifier|public
name|String
name|getFilterName
parameter_list|()
block|{
return|return
literal|"dummy"
return|;
block|}
annotation|@
name|Override
DECL|method|getInitParameter (String arg0)
specifier|public
name|String
name|getInitParameter
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
return|return
name|map
operator|.
name|get
argument_list|(
name|arg0
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getInitParameterNames ()
specifier|public
name|Enumeration
argument_list|<
name|String
argument_list|>
name|getInitParameterNames
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|enumeration
argument_list|(
name|map
operator|.
name|keySet
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getServletContext ()
specifier|public
name|ServletContext
name|getServletContext
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testGetConfiguration ()
specifier|public
name|void
name|testGetConfiguration
parameter_list|()
throws|throws
name|ServletException
block|{
name|AuthFilter
name|filter
init|=
operator|new
name|AuthFilter
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|m
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_WEB_AUTHENTICATION_KERBEROS_PRINCIPAL_KEY
argument_list|,
literal|"xyz/thehost@REALM"
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_WEB_AUTHENTICATION_KERBEROS_KEYTAB_KEY
argument_list|,
literal|"thekeytab"
argument_list|)
expr_stmt|;
name|FilterConfig
name|config
init|=
operator|new
name|DummyFilterConfig
argument_list|(
name|m
argument_list|)
decl_stmt|;
name|Properties
name|p
init|=
name|filter
operator|.
name|getConfiguration
argument_list|(
literal|"random"
argument_list|,
name|config
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"xyz/thehost@REALM"
argument_list|,
name|p
operator|.
name|getProperty
argument_list|(
literal|"kerberos.principal"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"thekeytab"
argument_list|,
name|p
operator|.
name|getProperty
argument_list|(
literal|"kerberos.keytab"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"true"
argument_list|,
name|p
operator|.
name|getProperty
argument_list|(
name|PseudoAuthenticationHandler
operator|.
name|ANONYMOUS_ALLOWED
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

