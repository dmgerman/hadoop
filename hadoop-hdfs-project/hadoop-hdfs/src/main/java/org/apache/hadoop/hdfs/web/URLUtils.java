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
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLConnection
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

begin_comment
comment|/**  * Utilities for handling URLs  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|URLUtils
specifier|public
class|class
name|URLUtils
block|{
comment|/**    * Timeout for socket connects and reads    */
DECL|field|SOCKET_TIMEOUT
specifier|public
specifier|static
name|int
name|SOCKET_TIMEOUT
init|=
literal|1
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
comment|// 1 minute
comment|/**    * Opens a url with read and connect timeouts    * @param url to open    * @return URLConnection    * @throws IOException    */
DECL|method|openConnection (URL url)
specifier|public
specifier|static
name|URLConnection
name|openConnection
parameter_list|(
name|URL
name|url
parameter_list|)
throws|throws
name|IOException
block|{
name|URLConnection
name|connection
init|=
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|setTimeouts
argument_list|(
name|connection
argument_list|)
expr_stmt|;
return|return
name|connection
return|;
block|}
comment|/**    * Sets timeout parameters on the given URLConnection.    *     * @param connection URLConnection to set    */
DECL|method|setTimeouts (URLConnection connection)
specifier|static
name|void
name|setTimeouts
parameter_list|(
name|URLConnection
name|connection
parameter_list|)
block|{
name|connection
operator|.
name|setConnectTimeout
argument_list|(
name|SOCKET_TIMEOUT
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setReadTimeout
argument_list|(
name|SOCKET_TIMEOUT
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

