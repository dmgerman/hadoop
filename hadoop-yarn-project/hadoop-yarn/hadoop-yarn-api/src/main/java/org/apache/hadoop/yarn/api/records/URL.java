begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|records
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
operator|.
name|Public
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
operator|.
name|Private
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
operator|.
name|Stable
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
name|fs
operator|.
name|Path
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
name|yarn
operator|.
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
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
name|yarn
operator|.
name|util
operator|.
name|Records
import|;
end_import

begin_comment
comment|/**  *<p><code>URL</code> represents a serializable {@link java.net.URL}.</p>  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Stable
DECL|class|URL
specifier|public
specifier|abstract
class|class
name|URL
block|{
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|newInstance (String scheme, String host, int port, String file)
specifier|public
specifier|static
name|URL
name|newInstance
parameter_list|(
name|String
name|scheme
parameter_list|,
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|,
name|String
name|file
parameter_list|)
block|{
name|URL
name|url
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|URL
operator|.
name|class
argument_list|)
decl_stmt|;
name|url
operator|.
name|setScheme
argument_list|(
name|scheme
argument_list|)
expr_stmt|;
name|url
operator|.
name|setHost
argument_list|(
name|host
argument_list|)
expr_stmt|;
name|url
operator|.
name|setPort
argument_list|(
name|port
argument_list|)
expr_stmt|;
name|url
operator|.
name|setFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
return|return
name|url
return|;
block|}
comment|/**    * Get the scheme of the URL.    * @return scheme of the URL    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getScheme ()
specifier|public
specifier|abstract
name|String
name|getScheme
parameter_list|()
function_decl|;
comment|/**    * Set the scheme of the URL    * @param scheme scheme of the URL    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setScheme (String scheme)
specifier|public
specifier|abstract
name|void
name|setScheme
parameter_list|(
name|String
name|scheme
parameter_list|)
function_decl|;
comment|/**    * Get the user info of the URL.    * @return user info of the URL    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getUserInfo ()
specifier|public
specifier|abstract
name|String
name|getUserInfo
parameter_list|()
function_decl|;
comment|/**    * Set the user info of the URL.    * @param userInfo user info of the URL    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setUserInfo (String userInfo)
specifier|public
specifier|abstract
name|void
name|setUserInfo
parameter_list|(
name|String
name|userInfo
parameter_list|)
function_decl|;
comment|/**    * Get the host of the URL.    * @return host of the URL    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getHost ()
specifier|public
specifier|abstract
name|String
name|getHost
parameter_list|()
function_decl|;
comment|/**    * Set the host of the URL.    * @param host host of the URL    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setHost (String host)
specifier|public
specifier|abstract
name|void
name|setHost
parameter_list|(
name|String
name|host
parameter_list|)
function_decl|;
comment|/**    * Get the port of the URL.    * @return port of the URL    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getPort ()
specifier|public
specifier|abstract
name|int
name|getPort
parameter_list|()
function_decl|;
comment|/**    * Set the port of the URL    * @param port port of the URL    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setPort (int port)
specifier|public
specifier|abstract
name|void
name|setPort
parameter_list|(
name|int
name|port
parameter_list|)
function_decl|;
comment|/**    * Get the file of the URL.    * @return file of the URL    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getFile ()
specifier|public
specifier|abstract
name|String
name|getFile
parameter_list|()
function_decl|;
comment|/**    * Set the file of the URL.    * @param file file of the URL    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setFile (String file)
specifier|public
specifier|abstract
name|void
name|setFile
parameter_list|(
name|String
name|file
parameter_list|)
function_decl|;
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|toPath ()
specifier|public
name|Path
name|toPath
parameter_list|()
throws|throws
name|URISyntaxException
block|{
return|return
operator|new
name|Path
argument_list|(
operator|new
name|URI
argument_list|(
name|getScheme
argument_list|()
argument_list|,
name|getUserInfo
argument_list|()
argument_list|,
name|getHost
argument_list|()
argument_list|,
name|getPort
argument_list|()
argument_list|,
name|getFile
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Private
annotation|@
name|VisibleForTesting
DECL|method|fromURI (URI uri, Configuration conf)
specifier|public
specifier|static
name|URL
name|fromURI
parameter_list|(
name|URI
name|uri
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|URL
name|url
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
name|conf
argument_list|)
operator|.
name|newRecordInstance
argument_list|(
name|URL
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|uri
operator|.
name|getHost
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|url
operator|.
name|setHost
argument_list|(
name|uri
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|uri
operator|.
name|getUserInfo
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|url
operator|.
name|setUserInfo
argument_list|(
name|uri
operator|.
name|getUserInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|url
operator|.
name|setPort
argument_list|(
name|uri
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
name|url
operator|.
name|setScheme
argument_list|(
name|uri
operator|.
name|getScheme
argument_list|()
argument_list|)
expr_stmt|;
name|url
operator|.
name|setFile
argument_list|(
name|uri
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|url
return|;
block|}
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|fromURI (URI uri)
specifier|public
specifier|static
name|URL
name|fromURI
parameter_list|(
name|URI
name|uri
parameter_list|)
block|{
return|return
name|fromURI
argument_list|(
name|uri
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Private
annotation|@
name|VisibleForTesting
DECL|method|fromPath (Path path, Configuration conf)
specifier|public
specifier|static
name|URL
name|fromPath
parameter_list|(
name|Path
name|path
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|fromURI
argument_list|(
name|path
operator|.
name|toUri
argument_list|()
argument_list|,
name|conf
argument_list|)
return|;
block|}
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|fromPath (Path path)
specifier|public
specifier|static
name|URL
name|fromPath
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
return|return
name|fromURI
argument_list|(
name|path
operator|.
name|toUri
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

