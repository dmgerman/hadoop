begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.web.webhdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|web
operator|.
name|webhdfs
package|;
end_package

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|QueryStringDecoder
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
name|CreateFlag
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
name|permission
operator|.
name|FsPermission
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
name|HAUtilClient
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
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|DelegationTokenIdentifier
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
name|web
operator|.
name|resources
operator|.
name|BlockSizeParam
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
name|web
operator|.
name|resources
operator|.
name|BufferSizeParam
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
name|web
operator|.
name|resources
operator|.
name|CreateFlagParam
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
name|web
operator|.
name|resources
operator|.
name|CreateParentParam
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
name|web
operator|.
name|resources
operator|.
name|DelegationParam
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
name|web
operator|.
name|resources
operator|.
name|DoAsParam
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
name|web
operator|.
name|resources
operator|.
name|HttpOpParam
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
name|web
operator|.
name|resources
operator|.
name|LengthParam
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
name|web
operator|.
name|resources
operator|.
name|NamenodeAddressParam
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
name|web
operator|.
name|resources
operator|.
name|NoRedirectParam
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
name|web
operator|.
name|resources
operator|.
name|OffsetParam
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
name|web
operator|.
name|resources
operator|.
name|OverwriteParam
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
name|web
operator|.
name|resources
operator|.
name|PermissionParam
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
name|web
operator|.
name|resources
operator|.
name|ReplicationParam
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
name|web
operator|.
name|resources
operator|.
name|UnmaskedPermissionParam
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
name|web
operator|.
name|resources
operator|.
name|UserParam
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
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import static
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
operator|.
name|HDFS_URI_SCHEME
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|web
operator|.
name|webhdfs
operator|.
name|WebHdfsHandler
operator|.
name|WEBHDFS_PREFIX_LENGTH
import|;
end_import

begin_class
DECL|class|ParameterParser
class|class
name|ParameterParser
block|{
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|path
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
DECL|field|params
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|params
decl_stmt|;
DECL|method|ParameterParser (QueryStringDecoder decoder, Configuration conf)
name|ParameterParser
parameter_list|(
name|QueryStringDecoder
name|decoder
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|decoder
operator|.
name|path
argument_list|()
operator|.
name|substring
argument_list|(
name|WEBHDFS_PREFIX_LENGTH
argument_list|)
expr_stmt|;
name|this
operator|.
name|params
operator|=
name|decoder
operator|.
name|parameters
argument_list|()
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
block|}
DECL|method|path ()
name|String
name|path
parameter_list|()
block|{
return|return
name|path
return|;
block|}
DECL|method|op ()
name|String
name|op
parameter_list|()
block|{
return|return
name|param
argument_list|(
name|HttpOpParam
operator|.
name|NAME
argument_list|)
return|;
block|}
DECL|method|offset ()
name|long
name|offset
parameter_list|()
block|{
return|return
operator|new
name|OffsetParam
argument_list|(
name|param
argument_list|(
name|OffsetParam
operator|.
name|NAME
argument_list|)
argument_list|)
operator|.
name|getOffset
argument_list|()
return|;
block|}
DECL|method|length ()
name|long
name|length
parameter_list|()
block|{
return|return
operator|new
name|LengthParam
argument_list|(
name|param
argument_list|(
name|LengthParam
operator|.
name|NAME
argument_list|)
argument_list|)
operator|.
name|getLength
argument_list|()
return|;
block|}
DECL|method|namenodeId ()
name|String
name|namenodeId
parameter_list|()
block|{
return|return
operator|new
name|NamenodeAddressParam
argument_list|(
name|param
argument_list|(
name|NamenodeAddressParam
operator|.
name|NAME
argument_list|)
argument_list|)
operator|.
name|getValue
argument_list|()
return|;
block|}
DECL|method|doAsUser ()
name|String
name|doAsUser
parameter_list|()
block|{
return|return
operator|new
name|DoAsParam
argument_list|(
name|param
argument_list|(
name|DoAsParam
operator|.
name|NAME
argument_list|)
argument_list|)
operator|.
name|getValue
argument_list|()
return|;
block|}
DECL|method|userName ()
name|String
name|userName
parameter_list|()
block|{
return|return
operator|new
name|UserParam
argument_list|(
name|param
argument_list|(
name|UserParam
operator|.
name|NAME
argument_list|)
argument_list|)
operator|.
name|getValue
argument_list|()
return|;
block|}
DECL|method|bufferSize ()
name|int
name|bufferSize
parameter_list|()
block|{
return|return
operator|new
name|BufferSizeParam
argument_list|(
name|param
argument_list|(
name|BufferSizeParam
operator|.
name|NAME
argument_list|)
argument_list|)
operator|.
name|getValue
argument_list|(
name|conf
argument_list|)
return|;
block|}
DECL|method|blockSize ()
name|long
name|blockSize
parameter_list|()
block|{
return|return
operator|new
name|BlockSizeParam
argument_list|(
name|param
argument_list|(
name|BlockSizeParam
operator|.
name|NAME
argument_list|)
argument_list|)
operator|.
name|getValue
argument_list|(
name|conf
argument_list|)
return|;
block|}
DECL|method|replication ()
name|short
name|replication
parameter_list|()
block|{
return|return
operator|new
name|ReplicationParam
argument_list|(
name|param
argument_list|(
name|ReplicationParam
operator|.
name|NAME
argument_list|)
argument_list|)
operator|.
name|getValue
argument_list|(
name|conf
argument_list|)
return|;
block|}
DECL|method|permission ()
name|FsPermission
name|permission
parameter_list|()
block|{
return|return
operator|new
name|PermissionParam
argument_list|(
name|param
argument_list|(
name|PermissionParam
operator|.
name|NAME
argument_list|)
argument_list|)
operator|.
name|getFileFsPermission
argument_list|()
return|;
block|}
DECL|method|unmaskedPermission ()
name|FsPermission
name|unmaskedPermission
parameter_list|()
block|{
name|String
name|value
init|=
name|param
argument_list|(
name|UnmaskedPermissionParam
operator|.
name|NAME
argument_list|)
decl_stmt|;
return|return
name|value
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|UnmaskedPermissionParam
argument_list|(
name|value
argument_list|)
operator|.
name|getFileFsPermission
argument_list|()
return|;
block|}
DECL|method|overwrite ()
name|boolean
name|overwrite
parameter_list|()
block|{
return|return
operator|new
name|OverwriteParam
argument_list|(
name|param
argument_list|(
name|OverwriteParam
operator|.
name|NAME
argument_list|)
argument_list|)
operator|.
name|getValue
argument_list|()
return|;
block|}
DECL|method|noredirect ()
name|boolean
name|noredirect
parameter_list|()
block|{
return|return
operator|new
name|NoRedirectParam
argument_list|(
name|param
argument_list|(
name|NoRedirectParam
operator|.
name|NAME
argument_list|)
argument_list|)
operator|.
name|getValue
argument_list|()
return|;
block|}
DECL|method|delegationToken ()
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|delegationToken
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|delegation
init|=
name|param
argument_list|(
name|DelegationParam
operator|.
name|NAME
argument_list|)
decl_stmt|;
specifier|final
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token
init|=
operator|new
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
argument_list|()
decl_stmt|;
name|token
operator|.
name|decodeFromUrlString
argument_list|(
name|delegation
argument_list|)
expr_stmt|;
name|URI
name|nnUri
init|=
name|URI
operator|.
name|create
argument_list|(
name|HDFS_URI_SCHEME
operator|+
literal|"://"
operator|+
name|namenodeId
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|isLogical
init|=
name|HAUtilClient
operator|.
name|isLogicalUri
argument_list|(
name|conf
argument_list|,
name|nnUri
argument_list|)
decl_stmt|;
if|if
condition|(
name|isLogical
condition|)
block|{
name|token
operator|.
name|setService
argument_list|(
name|HAUtilClient
operator|.
name|buildTokenServiceForLogicalUri
argument_list|(
name|nnUri
argument_list|,
name|HDFS_URI_SCHEME
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|token
operator|.
name|setService
argument_list|(
name|SecurityUtil
operator|.
name|buildTokenService
argument_list|(
name|nnUri
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|token
return|;
block|}
DECL|method|createParent ()
specifier|public
name|boolean
name|createParent
parameter_list|()
block|{
return|return
operator|new
name|CreateParentParam
argument_list|(
name|param
argument_list|(
name|CreateParentParam
operator|.
name|NAME
argument_list|)
argument_list|)
operator|.
name|getValue
argument_list|()
return|;
block|}
DECL|method|createFlag ()
specifier|public
name|EnumSet
argument_list|<
name|CreateFlag
argument_list|>
name|createFlag
parameter_list|()
block|{
name|String
name|cf
init|=
name|decodeComponent
argument_list|(
name|param
argument_list|(
name|CreateFlagParam
operator|.
name|NAME
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
return|return
operator|new
name|CreateFlagParam
argument_list|(
name|cf
argument_list|)
operator|.
name|getValue
argument_list|()
return|;
block|}
DECL|method|conf ()
name|Configuration
name|conf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
DECL|method|param (String key)
specifier|private
name|String
name|param
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|p
init|=
name|params
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
return|return
name|p
operator|==
literal|null
condition|?
literal|null
else|:
name|p
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
comment|/**    * The following function behaves exactly the same as netty's    *<code>QueryStringDecoder#decodeComponent</code> except that it    * does not decode the '+' character as space. WebHDFS takes this scheme    * to maintain the backward-compatibility for pre-2.7 releases.    */
DECL|method|decodeComponent (final String s, final Charset charset)
specifier|private
specifier|static
name|String
name|decodeComponent
parameter_list|(
specifier|final
name|String
name|s
parameter_list|,
specifier|final
name|Charset
name|charset
parameter_list|)
block|{
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
return|return
literal|""
return|;
block|}
specifier|final
name|int
name|size
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
name|boolean
name|modified
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|char
name|c
init|=
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|'%'
operator|||
name|c
operator|==
literal|'+'
condition|)
block|{
name|modified
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|modified
condition|)
block|{
return|return
name|s
return|;
block|}
specifier|final
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|size
index|]
decl_stmt|;
name|int
name|pos
init|=
literal|0
decl_stmt|;
comment|// position in `buf'.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|'%'
condition|)
block|{
if|if
condition|(
name|i
operator|==
name|size
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unterminated escape sequence at"
operator|+
literal|" end of string: "
operator|+
name|s
argument_list|)
throw|;
block|}
name|c
operator|=
name|s
operator|.
name|charAt
argument_list|(
operator|++
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|c
operator|==
literal|'%'
condition|)
block|{
name|buf
index|[
name|pos
operator|++
index|]
operator|=
literal|'%'
expr_stmt|;
comment|// "%%" -> "%"
break|break;
block|}
if|if
condition|(
name|i
operator|==
name|size
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"partial escape sequence at end "
operator|+
literal|"of string: "
operator|+
name|s
argument_list|)
throw|;
block|}
name|c
operator|=
name|decodeHexNibble
argument_list|(
name|c
argument_list|)
expr_stmt|;
specifier|final
name|char
name|c2
init|=
name|decodeHexNibble
argument_list|(
name|s
operator|.
name|charAt
argument_list|(
operator|++
name|i
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
name|Character
operator|.
name|MAX_VALUE
operator|||
name|c2
operator|==
name|Character
operator|.
name|MAX_VALUE
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid escape sequence `%"
operator|+
name|s
operator|.
name|charAt
argument_list|(
name|i
operator|-
literal|1
argument_list|)
operator|+
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|+
literal|"' at index "
operator|+
operator|(
name|i
operator|-
literal|2
operator|)
operator|+
literal|" of: "
operator|+
name|s
argument_list|)
throw|;
block|}
name|c
operator|=
call|(
name|char
call|)
argument_list|(
name|c
operator|*
literal|16
operator|+
name|c2
argument_list|)
expr_stmt|;
comment|// Fall through.
block|}
name|buf
index|[
name|pos
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
name|c
expr_stmt|;
block|}
return|return
operator|new
name|String
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|pos
argument_list|,
name|charset
argument_list|)
return|;
block|}
comment|/**    * Helper to decode half of a hexadecimal number from a string.    * @param c The ASCII character of the hexadecimal number to decode.    * Must be in the range {@code [0-9a-fA-F]}.    * @return The hexadecimal value represented in the ASCII character    * given, or {@link Character#MAX_VALUE} if the character is invalid.    */
DECL|method|decodeHexNibble (final char c)
specifier|private
specifier|static
name|char
name|decodeHexNibble
parameter_list|(
specifier|final
name|char
name|c
parameter_list|)
block|{
if|if
condition|(
literal|'0'
operator|<=
name|c
operator|&&
name|c
operator|<=
literal|'9'
condition|)
block|{
return|return
call|(
name|char
call|)
argument_list|(
name|c
operator|-
literal|'0'
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
literal|'a'
operator|<=
name|c
operator|&&
name|c
operator|<=
literal|'f'
condition|)
block|{
return|return
call|(
name|char
call|)
argument_list|(
name|c
operator|-
literal|'a'
operator|+
literal|10
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
literal|'A'
operator|<=
name|c
operator|&&
name|c
operator|<=
literal|'F'
condition|)
block|{
return|return
call|(
name|char
call|)
argument_list|(
name|c
operator|-
literal|'A'
operator|+
literal|10
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Character
operator|.
name|MAX_VALUE
return|;
block|}
block|}
block|}
end_class

end_unit

