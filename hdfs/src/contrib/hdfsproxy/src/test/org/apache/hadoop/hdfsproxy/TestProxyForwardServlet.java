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
name|org
operator|.
name|apache
operator|.
name|cactus
operator|.
name|ServletTestCase
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
name|javax
operator|.
name|servlet
operator|.
name|ServletException
import|;
end_import

begin_comment
comment|/** Unit tests for ProxyUtil */
end_comment

begin_class
DECL|class|TestProxyForwardServlet
specifier|public
class|class
name|TestProxyForwardServlet
extends|extends
name|ServletTestCase
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
name|TestProxyForwardServlet
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|beginDoGet (WebRequest theRequest)
specifier|public
name|void
name|beginDoGet
parameter_list|(
name|WebRequest
name|theRequest
parameter_list|)
block|{
name|theRequest
operator|.
name|setURL
argument_list|(
literal|"proxy-test:0"
argument_list|,
literal|null
argument_list|,
literal|"/simple"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|testDoGet ()
specifier|public
name|void
name|testDoGet
parameter_list|()
throws|throws
name|IOException
throws|,
name|ServletException
block|{
name|ProxyForwardServlet
name|servlet
init|=
operator|new
name|ProxyForwardServlet
argument_list|()
decl_stmt|;
name|servlet
operator|.
name|init
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|servlet
operator|.
name|doGet
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
DECL|method|endDoGet (WebResponse theResponse)
specifier|public
name|void
name|endDoGet
parameter_list|(
name|WebResponse
name|theResponse
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|expected
init|=
literal|"<html><head/><body>A GET request</body></html>"
decl_stmt|;
name|String
name|result
init|=
name|theResponse
operator|.
name|getText
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
DECL|method|testForwardRequest ()
specifier|public
name|void
name|testForwardRequest
parameter_list|()
throws|throws
name|Exception
block|{
name|ProxyForwardServlet
name|servlet
init|=
operator|new
name|ProxyForwardServlet
argument_list|()
decl_stmt|;
name|servlet
operator|.
name|forwardRequest
argument_list|(
name|request
argument_list|,
name|response
argument_list|,
name|config
operator|.
name|getServletContext
argument_list|()
argument_list|,
literal|"/simple"
argument_list|)
expr_stmt|;
block|}
DECL|method|endForwardRequest (WebResponse theResponse)
specifier|public
name|void
name|endForwardRequest
parameter_list|(
name|WebResponse
name|theResponse
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|expected
init|=
literal|"<html><head/><body>A GET request</body></html>"
decl_stmt|;
name|String
name|result
init|=
name|theResponse
operator|.
name|getText
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

