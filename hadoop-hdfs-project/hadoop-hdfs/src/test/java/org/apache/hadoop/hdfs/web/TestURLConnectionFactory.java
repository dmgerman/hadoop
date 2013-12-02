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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|client
operator|.
name|ConnectionConfigurator
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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_class
DECL|class|TestURLConnectionFactory
specifier|public
specifier|final
class|class
name|TestURLConnectionFactory
block|{
annotation|@
name|Test
DECL|method|testConnConfiguratior ()
specifier|public
name|void
name|testConnConfiguratior
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|URL
name|u
init|=
operator|new
name|URL
argument_list|(
literal|"http://localhost"
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|HttpURLConnection
argument_list|>
name|conns
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|URLConnectionFactory
name|fc
init|=
operator|new
name|URLConnectionFactory
argument_list|(
operator|new
name|ConnectionConfigurator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|HttpURLConnection
name|configure
parameter_list|(
name|HttpURLConnection
name|conn
parameter_list|)
throws|throws
name|IOException
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|u
argument_list|,
name|conn
operator|.
name|getURL
argument_list|()
argument_list|)
expr_stmt|;
name|conns
operator|.
name|add
argument_list|(
name|conn
argument_list|)
expr_stmt|;
return|return
name|conn
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|fc
operator|.
name|openConnection
argument_list|(
name|u
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|conns
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

