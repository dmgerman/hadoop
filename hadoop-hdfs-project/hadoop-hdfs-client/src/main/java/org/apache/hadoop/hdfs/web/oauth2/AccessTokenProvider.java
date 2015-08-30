begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.web.oauth2
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
name|oauth2
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
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceStability
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
name|Configurable
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Provide an OAuth2 access token to be used to authenticate http calls in  * WebHDFS.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|AccessTokenProvider
specifier|public
specifier|abstract
class|class
name|AccessTokenProvider
implements|implements
name|Configurable
block|{
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
comment|/**    * Obtain the access token that should be added to http connection's header.    * Will be called for each connection, so implementations should be    * performant. Implementations are responsible for any refreshing of    * the token.    *     * @return Access token to be added to connection header.    */
DECL|method|getAccessToken ()
specifier|abstract
name|String
name|getAccessToken
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Return the conf.    *    * @return the conf.    */
annotation|@
name|Override
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
comment|/**    * Set the conf.    *    * @param configuration  New configuration.    */
annotation|@
name|Override
DECL|method|setConf (Configuration configuration)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|configuration
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|configuration
expr_stmt|;
block|}
block|}
end_class

end_unit

