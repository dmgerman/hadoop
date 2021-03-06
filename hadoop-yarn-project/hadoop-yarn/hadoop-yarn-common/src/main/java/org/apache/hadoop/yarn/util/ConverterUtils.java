begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
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
name|SecurityUtil
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
name|TokenIdentifier
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
name|api
operator|.
name|records
operator|.
name|ApplicationAttemptId
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
name|api
operator|.
name|records
operator|.
name|ApplicationId
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
name|api
operator|.
name|records
operator|.
name|ContainerId
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
name|api
operator|.
name|records
operator|.
name|NodeId
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
name|api
operator|.
name|records
operator|.
name|URL
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
name|factories
operator|.
name|RecordFactory
import|;
end_import

begin_comment
comment|/**  * This class contains a set of utilities which help converting data structures  * from/to 'serializableFormat' to/from hadoop/nativejava data structures.  *  */
end_comment

begin_class
annotation|@
name|Public
DECL|class|ConverterUtils
specifier|public
class|class
name|ConverterUtils
block|{
DECL|field|APPLICATION_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|APPLICATION_PREFIX
init|=
literal|"application"
decl_stmt|;
DECL|field|CONTAINER_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|CONTAINER_PREFIX
init|=
literal|"container"
decl_stmt|;
DECL|field|APPLICATION_ATTEMPT_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|APPLICATION_ATTEMPT_PREFIX
init|=
literal|"appattempt"
decl_stmt|;
comment|/**    * return a hadoop path from a given url    * This method is deprecated, use {@link URL#toPath()} instead.    *     * @param url    *          url to convert    * @return path from {@link URL}    * @throws URISyntaxException    */
annotation|@
name|Public
annotation|@
name|Deprecated
DECL|method|getPathFromYarnURL (URL url)
specifier|public
specifier|static
name|Path
name|getPathFromYarnURL
parameter_list|(
name|URL
name|url
parameter_list|)
throws|throws
name|URISyntaxException
block|{
return|return
name|url
operator|.
name|toPath
argument_list|()
return|;
block|}
comment|/*    * This method is deprecated, use {@link URL#fromPath(Path)} instead.    */
annotation|@
name|Public
annotation|@
name|Deprecated
DECL|method|getYarnUrlFromPath (Path path)
specifier|public
specifier|static
name|URL
name|getYarnUrlFromPath
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
return|return
name|URL
operator|.
name|fromPath
argument_list|(
name|path
argument_list|)
return|;
block|}
comment|/*    * This method is deprecated, use {@link URL#fromURI(URI)} instead.    */
annotation|@
name|Public
annotation|@
name|Deprecated
DECL|method|getYarnUrlFromURI (URI uri)
specifier|public
specifier|static
name|URL
name|getYarnUrlFromURI
parameter_list|(
name|URI
name|uri
parameter_list|)
block|{
return|return
name|URL
operator|.
name|fromURI
argument_list|(
name|uri
argument_list|)
return|;
block|}
comment|/*    * This method is deprecated, use {@link ApplicationId#toString()} instead.    */
annotation|@
name|Public
annotation|@
name|Deprecated
DECL|method|toString (ApplicationId appId)
specifier|public
specifier|static
name|String
name|toString
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
block|{
return|return
name|appId
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/*    * This method is deprecated, use {@link ApplicationId#fromString(String)}    * instead.    */
annotation|@
name|Public
annotation|@
name|Deprecated
DECL|method|toApplicationId (RecordFactory recordFactory, String applicationIdStr)
specifier|public
specifier|static
name|ApplicationId
name|toApplicationId
parameter_list|(
name|RecordFactory
name|recordFactory
parameter_list|,
name|String
name|applicationIdStr
parameter_list|)
block|{
return|return
name|ApplicationId
operator|.
name|fromString
argument_list|(
name|applicationIdStr
argument_list|)
return|;
block|}
comment|/*    * This method is deprecated, use {@link ContainerId#toString()} instead.    */
annotation|@
name|Public
annotation|@
name|Deprecated
DECL|method|toString (ContainerId cId)
specifier|public
specifier|static
name|String
name|toString
parameter_list|(
name|ContainerId
name|cId
parameter_list|)
block|{
return|return
name|cId
operator|==
literal|null
condition|?
literal|null
else|:
name|cId
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|method|toNodeIdWithDefaultPort (String nodeIdStr)
specifier|public
specifier|static
name|NodeId
name|toNodeIdWithDefaultPort
parameter_list|(
name|String
name|nodeIdStr
parameter_list|)
block|{
if|if
condition|(
name|nodeIdStr
operator|.
name|indexOf
argument_list|(
literal|":"
argument_list|)
operator|<
literal|0
condition|)
block|{
return|return
name|NodeId
operator|.
name|fromString
argument_list|(
name|nodeIdStr
operator|+
literal|":0"
argument_list|)
return|;
block|}
return|return
name|NodeId
operator|.
name|fromString
argument_list|(
name|nodeIdStr
argument_list|)
return|;
block|}
comment|/*    * This method is deprecated, use {@link NodeId#fromString(String)} instead.    */
annotation|@
name|Public
annotation|@
name|Deprecated
DECL|method|toNodeId (String nodeIdStr)
specifier|public
specifier|static
name|NodeId
name|toNodeId
parameter_list|(
name|String
name|nodeIdStr
parameter_list|)
block|{
return|return
name|NodeId
operator|.
name|fromString
argument_list|(
name|nodeIdStr
argument_list|)
return|;
block|}
comment|/*    * This method is deprecated, use {@link ContainerId#fromString(String)}    * instead.    */
annotation|@
name|Public
annotation|@
name|Deprecated
DECL|method|toContainerId (String containerIdStr)
specifier|public
specifier|static
name|ContainerId
name|toContainerId
parameter_list|(
name|String
name|containerIdStr
parameter_list|)
block|{
return|return
name|ContainerId
operator|.
name|fromString
argument_list|(
name|containerIdStr
argument_list|)
return|;
block|}
comment|/*    * This method is deprecated, use {@link ApplicationAttemptId#toString()}    * instead.    */
annotation|@
name|Public
annotation|@
name|Deprecated
DECL|method|toApplicationAttemptId ( String applicationAttemptIdStr)
specifier|public
specifier|static
name|ApplicationAttemptId
name|toApplicationAttemptId
parameter_list|(
name|String
name|applicationAttemptIdStr
parameter_list|)
block|{
return|return
name|ApplicationAttemptId
operator|.
name|fromString
argument_list|(
name|applicationAttemptIdStr
argument_list|)
return|;
block|}
comment|/*    * This method is deprecated, use {@link ApplicationId#fromString(String)}    * instead.    */
annotation|@
name|Public
annotation|@
name|Deprecated
DECL|method|toApplicationId ( String appIdStr)
specifier|public
specifier|static
name|ApplicationId
name|toApplicationId
parameter_list|(
name|String
name|appIdStr
parameter_list|)
block|{
return|return
name|ApplicationId
operator|.
name|fromString
argument_list|(
name|appIdStr
argument_list|)
return|;
block|}
comment|/**    * Convert a protobuf token into a rpc token and set its service. Supposed    * to be used for tokens other than RMDelegationToken. For    * RMDelegationToken, use    * {@link #convertFromYarn(org.apache.hadoop.yarn.api.records.Token,    * org.apache.hadoop.io.Text)} instead.    *    * @param protoToken the yarn token    * @param serviceAddr the connect address for the service    * @return rpc token    */
DECL|method|convertFromYarn ( org.apache.hadoop.yarn.api.records.Token protoToken, InetSocketAddress serviceAddr)
specifier|public
specifier|static
parameter_list|<
name|T
extends|extends
name|TokenIdentifier
parameter_list|>
name|Token
argument_list|<
name|T
argument_list|>
name|convertFromYarn
parameter_list|(
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
operator|.
name|Token
name|protoToken
parameter_list|,
name|InetSocketAddress
name|serviceAddr
parameter_list|)
block|{
name|Token
argument_list|<
name|T
argument_list|>
name|token
init|=
operator|new
name|Token
argument_list|<
name|T
argument_list|>
argument_list|(
name|protoToken
operator|.
name|getIdentifier
argument_list|()
operator|.
name|array
argument_list|()
argument_list|,
name|protoToken
operator|.
name|getPassword
argument_list|()
operator|.
name|array
argument_list|()
argument_list|,
operator|new
name|Text
argument_list|(
name|protoToken
operator|.
name|getKind
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
name|protoToken
operator|.
name|getService
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|serviceAddr
operator|!=
literal|null
condition|)
block|{
name|SecurityUtil
operator|.
name|setTokenService
argument_list|(
name|token
argument_list|,
name|serviceAddr
argument_list|)
expr_stmt|;
block|}
return|return
name|token
return|;
block|}
comment|/**    * Convert a protobuf token into a rpc token and set its service.    *    * @param protoToken the yarn token    * @param service the service for the token    */
DECL|method|convertFromYarn ( org.apache.hadoop.yarn.api.records.Token protoToken, Text service)
specifier|public
specifier|static
parameter_list|<
name|T
extends|extends
name|TokenIdentifier
parameter_list|>
name|Token
argument_list|<
name|T
argument_list|>
name|convertFromYarn
parameter_list|(
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
operator|.
name|Token
name|protoToken
parameter_list|,
name|Text
name|service
parameter_list|)
block|{
name|Token
argument_list|<
name|T
argument_list|>
name|token
init|=
operator|new
name|Token
argument_list|<
name|T
argument_list|>
argument_list|(
name|protoToken
operator|.
name|getIdentifier
argument_list|()
operator|.
name|array
argument_list|()
argument_list|,
name|protoToken
operator|.
name|getPassword
argument_list|()
operator|.
name|array
argument_list|()
argument_list|,
operator|new
name|Text
argument_list|(
name|protoToken
operator|.
name|getKind
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
name|protoToken
operator|.
name|getService
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|service
operator|!=
literal|null
condition|)
block|{
name|token
operator|.
name|setService
argument_list|(
name|service
argument_list|)
expr_stmt|;
block|}
return|return
name|token
return|;
block|}
block|}
end_class

end_unit

