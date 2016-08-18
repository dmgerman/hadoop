begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.aliyun.oss
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|aliyun
operator|.
name|oss
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
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
name|URLDecoder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
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
name|lang
operator|.
name|StringUtils
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

begin_comment
comment|/**  * Utility methods for Aliyun OSS code.  */
end_comment

begin_class
DECL|class|AliyunOSSUtils
specifier|final
specifier|public
class|class
name|AliyunOSSUtils
block|{
DECL|method|AliyunOSSUtils ()
specifier|private
name|AliyunOSSUtils
parameter_list|()
block|{   }
comment|/**    * User information includes user name and password.    */
DECL|class|UserInfo
specifier|static
specifier|public
class|class
name|UserInfo
block|{
DECL|field|user
specifier|private
specifier|final
name|String
name|user
decl_stmt|;
DECL|field|password
specifier|private
specifier|final
name|String
name|password
decl_stmt|;
DECL|field|EMPTY
specifier|public
specifier|static
specifier|final
name|UserInfo
name|EMPTY
init|=
operator|new
name|UserInfo
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
DECL|method|UserInfo (String user, String password)
specifier|public
name|UserInfo
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|password
parameter_list|)
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|password
operator|=
name|password
expr_stmt|;
block|}
comment|/**      * Predicate to verify user information is set.      * @return true if the username is defined (not null, not empty).      */
DECL|method|hasLogin ()
specifier|public
name|boolean
name|hasLogin
parameter_list|()
block|{
return|return
name|StringUtils
operator|.
name|isNotEmpty
argument_list|(
name|user
argument_list|)
return|;
block|}
comment|/**      * Equality test matches user and password.      * @param o other object      * @return true if the objects are considered equivalent.      */
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|UserInfo
name|that
init|=
operator|(
name|UserInfo
operator|)
name|o
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|user
argument_list|,
name|that
operator|.
name|user
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|password
argument_list|,
name|that
operator|.
name|password
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|user
argument_list|,
name|password
argument_list|)
return|;
block|}
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
block|{
return|return
name|user
return|;
block|}
DECL|method|getPassword ()
specifier|public
name|String
name|getPassword
parameter_list|()
block|{
return|return
name|password
return|;
block|}
block|}
comment|/**    * Used to get password from configuration, if default value is not available.    * @param conf configuration that contains password information    * @param key the key of the password    * @param val the default value of the key    * @return the value for the key    * @throws IOException if failed to get password from configuration    */
DECL|method|getPassword (Configuration conf, String key, String val)
specifier|static
specifier|public
name|String
name|getPassword
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|key
parameter_list|,
name|String
name|val
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|val
argument_list|)
condition|)
block|{
try|try
block|{
specifier|final
name|char
index|[]
name|pass
init|=
name|conf
operator|.
name|getPassword
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|pass
operator|!=
literal|null
condition|)
block|{
return|return
operator|(
operator|new
name|String
argument_list|(
name|pass
argument_list|)
operator|)
operator|.
name|trim
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|""
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot find password option "
operator|+
name|key
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
else|else
block|{
return|return
name|val
return|;
block|}
block|}
comment|/**    * Extract the user information details from a URI.    * @param name URI of the filesystem.    * @return a login tuple, possibly empty.    */
DECL|method|extractLoginDetails (URI name)
specifier|public
specifier|static
name|UserInfo
name|extractLoginDetails
parameter_list|(
name|URI
name|name
parameter_list|)
block|{
try|try
block|{
name|String
name|authority
init|=
name|name
operator|.
name|getAuthority
argument_list|()
decl_stmt|;
if|if
condition|(
name|authority
operator|==
literal|null
condition|)
block|{
return|return
name|UserInfo
operator|.
name|EMPTY
return|;
block|}
name|int
name|loginIndex
init|=
name|authority
operator|.
name|indexOf
argument_list|(
literal|'@'
argument_list|)
decl_stmt|;
if|if
condition|(
name|loginIndex
operator|<
literal|0
condition|)
block|{
comment|// No user information
return|return
name|UserInfo
operator|.
name|EMPTY
return|;
block|}
name|String
name|login
init|=
name|authority
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|loginIndex
argument_list|)
decl_stmt|;
name|int
name|loginSplit
init|=
name|login
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|loginSplit
operator|>
literal|0
condition|)
block|{
name|String
name|user
init|=
name|login
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|loginSplit
argument_list|)
decl_stmt|;
name|String
name|password
init|=
name|URLDecoder
operator|.
name|decode
argument_list|(
name|login
operator|.
name|substring
argument_list|(
name|loginSplit
operator|+
literal|1
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
return|return
operator|new
name|UserInfo
argument_list|(
name|user
argument_list|,
name|password
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|loginSplit
operator|==
literal|0
condition|)
block|{
comment|// There is no user, just a password.
return|return
name|UserInfo
operator|.
name|EMPTY
return|;
block|}
else|else
block|{
return|return
operator|new
name|UserInfo
argument_list|(
name|login
argument_list|,
literal|""
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
comment|// This should never happen; translate it if it does.
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Skips the requested number of bytes or fail if there are not enough left.    * This allows for the possibility that {@link InputStream#skip(long)} may not    * skip as many bytes as requested (most likely because of reaching EOF).    * @param is the input stream to skip.    * @param n the number of bytes to skip.    * @throws IOException thrown when skipped less number of bytes.    */
DECL|method|skipFully (InputStream is, long n)
specifier|public
specifier|static
name|void
name|skipFully
parameter_list|(
name|InputStream
name|is
parameter_list|,
name|long
name|n
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|total
init|=
literal|0
decl_stmt|;
name|long
name|cur
init|=
literal|0
decl_stmt|;
do|do
block|{
name|cur
operator|=
name|is
operator|.
name|skip
argument_list|(
name|n
operator|-
name|total
argument_list|)
expr_stmt|;
name|total
operator|+=
name|cur
expr_stmt|;
block|}
do|while
condition|(
operator|(
name|total
operator|<
name|n
operator|)
operator|&&
operator|(
name|cur
operator|>
literal|0
operator|)
condition|)
do|;
if|if
condition|(
name|total
operator|<
name|n
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to skip "
operator|+
name|n
operator|+
literal|" bytes, possibly due "
operator|+
literal|"to EOF."
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

