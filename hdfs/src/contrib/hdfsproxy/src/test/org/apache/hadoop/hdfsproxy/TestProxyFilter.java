begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|FilterChain
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
name|ServletException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletResponse
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
name|org
operator|.
name|apache
operator|.
name|cactus
operator|.
name|FilterTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|cactus
operator|.
name|WebRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|cactus
operator|.
name|WebResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_class
DECL|class|TestProxyFilter
specifier|public
class|class
name|TestProxyFilter
extends|extends
name|FilterTestCase
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestProxyFilter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|TEST_CLIENT_SSL_CERT
specifier|private
specifier|static
name|String
name|TEST_CLIENT_SSL_CERT
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"javax.net.ssl.clientCert"
argument_list|,
literal|"./src/test/resources/ssl-keys/test.crt"
argument_list|)
decl_stmt|;
DECL|class|DummyFilterChain
specifier|private
class|class
name|DummyFilterChain
implements|implements
name|FilterChain
block|{
DECL|method|doFilter (ServletRequest theRequest, ServletResponse theResponse)
specifier|public
name|void
name|doFilter
parameter_list|(
name|ServletRequest
name|theRequest
parameter_list|,
name|ServletResponse
name|theResponse
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServletException
block|{
name|PrintWriter
name|writer
init|=
name|theResponse
operator|.
name|getWriter
argument_list|()
decl_stmt|;
name|writer
operator|.
name|print
argument_list|(
literal|"<p>some content</p>"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|init (FilterConfig theConfig)
specifier|public
name|void
name|init
parameter_list|(
name|FilterConfig
name|theConfig
parameter_list|)
block|{     }
DECL|method|destroy ()
specifier|public
name|void
name|destroy
parameter_list|()
block|{     }
block|}
DECL|method|beginDoFilterHttp (WebRequest theRequest)
specifier|public
name|void
name|beginDoFilterHttp
parameter_list|(
name|WebRequest
name|theRequest
parameter_list|)
block|{
name|theRequest
operator|.
name|addParameter
argument_list|(
literal|"ugi"
argument_list|,
literal|"nobody,test"
argument_list|)
expr_stmt|;
block|}
DECL|method|testDoFilterHttp ()
specifier|public
name|void
name|testDoFilterHttp
parameter_list|()
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|ProxyFilter
name|filter
init|=
operator|new
name|ProxyFilter
argument_list|()
decl_stmt|;
name|ServletContext
name|context
init|=
name|config
operator|.
name|getServletContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|removeAttribute
argument_list|(
literal|"name.node.address"
argument_list|)
expr_stmt|;
name|context
operator|.
name|removeAttribute
argument_list|(
literal|"name.conf"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|context
operator|.
name|getAttribute
argument_list|(
literal|"name.node.address"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|context
operator|.
name|getAttribute
argument_list|(
literal|"name.conf"
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|.
name|init
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|context
operator|.
name|getAttribute
argument_list|(
literal|"name.node.address"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|context
operator|.
name|getAttribute
argument_list|(
literal|"name.conf"
argument_list|)
argument_list|)
expr_stmt|;
name|request
operator|.
name|removeAttribute
argument_list|(
literal|"authorized.ugi"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|request
operator|.
name|getAttribute
argument_list|(
literal|"authorized.ugi"
argument_list|)
argument_list|)
expr_stmt|;
name|FilterChain
name|mockFilterChain
init|=
operator|new
name|DummyFilterChain
argument_list|()
decl_stmt|;
name|filter
operator|.
name|doFilter
argument_list|(
name|request
argument_list|,
name|response
argument_list|,
name|mockFilterChain
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|request
operator|.
name|getAttribute
argument_list|(
literal|"authorized.ugi"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
literal|"nobody,test"
argument_list|)
expr_stmt|;
block|}
DECL|method|endDoFilterHttp (WebResponse theResponse)
specifier|public
name|void
name|endDoFilterHttp
parameter_list|(
name|WebResponse
name|theResponse
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"<p>some content</p>"
argument_list|,
name|theResponse
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|beginDoFilterHttps (WebRequest theRequest)
specifier|public
name|void
name|beginDoFilterHttps
parameter_list|(
name|WebRequest
name|theRequest
parameter_list|)
throws|throws
name|Exception
block|{
name|theRequest
operator|.
name|addParameter
argument_list|(
literal|"UnitTest"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|theRequest
operator|.
name|addParameter
argument_list|(
literal|"SslPath"
argument_list|,
name|TEST_CLIENT_SSL_CERT
argument_list|)
expr_stmt|;
name|theRequest
operator|.
name|addParameter
argument_list|(
literal|"ugi"
argument_list|,
literal|"nobody,test"
argument_list|)
expr_stmt|;
name|theRequest
operator|.
name|addParameter
argument_list|(
literal|"TestSevletPathInfo"
argument_list|,
literal|"/streamFile"
argument_list|)
expr_stmt|;
name|theRequest
operator|.
name|addParameter
argument_list|(
literal|"filename"
argument_list|,
literal|"/user"
argument_list|)
expr_stmt|;
block|}
DECL|method|testDoFilterHttps ()
specifier|public
name|void
name|testDoFilterHttps
parameter_list|()
throws|throws
name|Exception
block|{
name|ProxyFilter
name|filter
init|=
operator|new
name|ProxyFilter
argument_list|()
decl_stmt|;
name|request
operator|.
name|removeAttribute
argument_list|(
literal|"authorized.ugi"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|request
operator|.
name|getAttribute
argument_list|(
literal|"authorized.ugi"
argument_list|)
argument_list|)
expr_stmt|;
name|FilterChain
name|mockFilterChain
init|=
operator|new
name|DummyFilterChain
argument_list|()
decl_stmt|;
name|filter
operator|.
name|init
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|filter
operator|.
name|doFilter
argument_list|(
name|request
argument_list|,
name|response
argument_list|,
name|mockFilterChain
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Finish setting up X509Certificate"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|request
operator|.
name|getAttribute
argument_list|(
literal|"authorized.ugi"
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|6
argument_list|)
argument_list|,
literal|"nobody"
argument_list|)
expr_stmt|;
block|}
DECL|method|endDoFilterHttps (WebResponse theResponse)
specifier|public
name|void
name|endDoFilterHttps
parameter_list|(
name|WebResponse
name|theResponse
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"<p>some content</p>"
argument_list|,
name|theResponse
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

