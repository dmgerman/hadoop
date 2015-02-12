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
import|import static
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
name|StringHelper
operator|.
name|_split
import|;
end_import

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
name|Iterator
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
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
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

begin_comment
comment|/**  * This class contains a set of utilities which help converting data structures  * from/to 'serializableFormat' to/from hadoop/nativejava data structures.  *  */
end_comment

begin_class
annotation|@
name|Private
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
comment|/**    * return a hadoop path from a given url    *     * @param url    *          url to convert    * @return path from {@link URL}    * @throws URISyntaxException    */
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
name|String
name|scheme
init|=
name|url
operator|.
name|getScheme
argument_list|()
operator|==
literal|null
condition|?
literal|""
else|:
name|url
operator|.
name|getScheme
argument_list|()
decl_stmt|;
name|String
name|authority
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|url
operator|.
name|getHost
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|authority
operator|=
name|url
operator|.
name|getHost
argument_list|()
expr_stmt|;
if|if
condition|(
name|url
operator|.
name|getUserInfo
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|authority
operator|=
name|url
operator|.
name|getUserInfo
argument_list|()
operator|+
literal|"@"
operator|+
name|authority
expr_stmt|;
block|}
if|if
condition|(
name|url
operator|.
name|getPort
argument_list|()
operator|>
literal|0
condition|)
block|{
name|authority
operator|+=
literal|":"
operator|+
name|url
operator|.
name|getPort
argument_list|()
expr_stmt|;
block|}
block|}
return|return
operator|new
name|Path
argument_list|(
operator|(
operator|new
name|URI
argument_list|(
name|scheme
argument_list|,
name|authority
argument_list|,
name|url
operator|.
name|getFile
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|)
operator|.
name|normalize
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * change from CharSequence to string for map key and value    * @param env map for converting    * @return string,string map    */
DECL|method|convertToString ( Map<CharSequence, CharSequence> env)
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|convertToString
parameter_list|(
name|Map
argument_list|<
name|CharSequence
argument_list|,
name|CharSequence
argument_list|>
name|env
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|stringMap
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
for|for
control|(
name|Entry
argument_list|<
name|CharSequence
argument_list|,
name|CharSequence
argument_list|>
name|entry
range|:
name|env
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|stringMap
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|stringMap
return|;
block|}
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
name|getYarnUrlFromURI
argument_list|(
name|path
operator|.
name|toUri
argument_list|()
argument_list|)
return|;
block|}
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
name|URL
name|url
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
literal|null
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
DECL|method|toApplicationId (RecordFactory recordFactory, String appIdStr)
specifier|public
specifier|static
name|ApplicationId
name|toApplicationId
parameter_list|(
name|RecordFactory
name|recordFactory
parameter_list|,
name|String
name|appIdStr
parameter_list|)
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|_split
argument_list|(
name|appIdStr
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// prefix. TODO: Validate application prefix
return|return
name|toApplicationId
argument_list|(
name|recordFactory
argument_list|,
name|it
argument_list|)
return|;
block|}
DECL|method|toApplicationId (RecordFactory recordFactory, Iterator<String> it)
specifier|private
specifier|static
name|ApplicationId
name|toApplicationId
parameter_list|(
name|RecordFactory
name|recordFactory
parameter_list|,
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
parameter_list|)
block|{
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|appId
return|;
block|}
DECL|method|toApplicationAttemptId ( Iterator<String> it)
specifier|private
specifier|static
name|ApplicationAttemptId
name|toApplicationAttemptId
parameter_list|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
parameter_list|)
throws|throws
name|NumberFormatException
block|{
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|appAttemptId
init|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|appAttemptId
return|;
block|}
DECL|method|toApplicationId ( Iterator<String> it)
specifier|private
specifier|static
name|ApplicationId
name|toApplicationId
parameter_list|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
parameter_list|)
throws|throws
name|NumberFormatException
block|{
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|appId
return|;
block|}
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
name|toNodeId
argument_list|(
name|nodeIdStr
operator|+
literal|":0"
argument_list|)
return|;
block|}
return|return
name|toNodeId
argument_list|(
name|nodeIdStr
argument_list|)
return|;
block|}
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
name|String
index|[]
name|parts
init|=
name|nodeIdStr
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
if|if
condition|(
name|parts
operator|.
name|length
operator|!=
literal|2
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid NodeId ["
operator|+
name|nodeIdStr
operator|+
literal|"]. Expected host:port"
argument_list|)
throw|;
block|}
try|try
block|{
name|NodeId
name|nodeId
init|=
name|NodeId
operator|.
name|newInstance
argument_list|(
name|parts
index|[
literal|0
index|]
operator|.
name|trim
argument_list|()
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|parts
index|[
literal|1
index|]
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|nodeId
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid port: "
operator|+
name|parts
index|[
literal|1
index|]
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
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
DECL|method|toApplicationAttemptId ( String applicationAttmeptIdStr)
specifier|public
specifier|static
name|ApplicationAttemptId
name|toApplicationAttemptId
parameter_list|(
name|String
name|applicationAttmeptIdStr
parameter_list|)
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|_split
argument_list|(
name|applicationAttmeptIdStr
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|it
operator|.
name|next
argument_list|()
operator|.
name|equals
argument_list|(
name|APPLICATION_ATTEMPT_PREFIX
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid AppAttemptId prefix: "
operator|+
name|applicationAttmeptIdStr
argument_list|)
throw|;
block|}
try|try
block|{
return|return
name|toApplicationAttemptId
argument_list|(
name|it
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|n
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid AppAttemptId: "
operator|+
name|applicationAttmeptIdStr
argument_list|,
name|n
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|NoSuchElementException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid AppAttemptId: "
operator|+
name|applicationAttmeptIdStr
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
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
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|_split
argument_list|(
name|appIdStr
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|it
operator|.
name|next
argument_list|()
operator|.
name|equals
argument_list|(
name|APPLICATION_PREFIX
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid ApplicationId prefix: "
operator|+
name|appIdStr
operator|+
literal|". The valid ApplicationId should start with prefix "
operator|+
name|APPLICATION_PREFIX
argument_list|)
throw|;
block|}
try|try
block|{
return|return
name|toApplicationId
argument_list|(
name|it
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|n
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid ApplicationId: "
operator|+
name|appIdStr
argument_list|,
name|n
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|NoSuchElementException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid ApplicationId: "
operator|+
name|appIdStr
argument_list|,
name|e
argument_list|)
throw|;
block|}
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

