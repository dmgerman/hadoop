begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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
name|URI
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|FileSystem
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
name|protocol
operator|.
name|HdfsConstants
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
name|io
operator|.
name|Text
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
name|Credentials
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
name|token
operator|.
name|DtFetcher
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
name|token
operator|.
name|Token
import|;
end_import

begin_comment
comment|/**  *  DtFetcher is an interface which permits the abstraction and separation of  *  delegation token fetch implementaions across different packages and  *  compilation units.  Resolution of fetcher impl will be done at runtime.  */
end_comment

begin_class
DECL|class|HdfsDtFetcher
specifier|public
class|class
name|HdfsDtFetcher
implements|implements
name|DtFetcher
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|HdfsDtFetcher
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SERVICE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|SERVICE_NAME
init|=
name|HdfsConstants
operator|.
name|HDFS_URI_SCHEME
decl_stmt|;
DECL|field|FETCH_FAILED
specifier|private
specifier|static
specifier|final
name|String
name|FETCH_FAILED
init|=
literal|"Fetch of delegation token failed"
decl_stmt|;
comment|/**    * Returns the service name for HDFS, which is also a valid URL prefix.    */
DECL|method|getServiceName ()
specifier|public
name|Text
name|getServiceName
parameter_list|()
block|{
return|return
operator|new
name|Text
argument_list|(
name|SERVICE_NAME
argument_list|)
return|;
block|}
DECL|method|isTokenRequired ()
specifier|public
name|boolean
name|isTokenRequired
parameter_list|()
block|{
return|return
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
return|;
block|}
comment|/**    *  Returns Token object via FileSystem, null if bad argument.    *  @param conf - a Configuration object used with FileSystem.get()    *  @param creds - a Credentials object to which token(s) will be added    *  @param renewer  - the renewer to send with the token request    *  @param url  - the URL to which the request is sent    *  @return a Token, or null if fetch fails.    */
DECL|method|addDelegationTokens (Configuration conf, Credentials creds, String renewer, String url)
specifier|public
name|Token
argument_list|<
name|?
argument_list|>
name|addDelegationTokens
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Credentials
name|creds
parameter_list|,
name|String
name|renewer
parameter_list|,
name|String
name|url
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|url
operator|.
name|startsWith
argument_list|(
name|getServiceName
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|url
operator|=
name|getServiceName
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"://"
operator|+
name|url
expr_stmt|;
block|}
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|URI
operator|.
name|create
argument_list|(
name|url
argument_list|)
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|?
argument_list|>
name|token
init|=
name|fs
operator|.
name|getDelegationToken
argument_list|(
name|renewer
argument_list|)
decl_stmt|;
if|if
condition|(
name|token
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|FETCH_FAILED
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|FETCH_FAILED
argument_list|)
throw|;
block|}
name|creds
operator|.
name|addToken
argument_list|(
name|token
operator|.
name|getService
argument_list|()
argument_list|,
name|token
argument_list|)
expr_stmt|;
return|return
name|token
return|;
block|}
block|}
end_class

end_unit

