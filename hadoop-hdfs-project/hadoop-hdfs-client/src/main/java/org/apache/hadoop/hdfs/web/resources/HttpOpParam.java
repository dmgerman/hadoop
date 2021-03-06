begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.web.resources
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
name|resources
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Response
import|;
end_import

begin_comment
comment|/** Http operation parameter. */
end_comment

begin_class
DECL|class|HttpOpParam
specifier|public
specifier|abstract
class|class
name|HttpOpParam
parameter_list|<
name|E
extends|extends
name|Enum
parameter_list|<
name|E
parameter_list|>
operator|&
name|HttpOpParam
operator|.
name|Op
parameter_list|>
extends|extends
name|EnumParam
argument_list|<
name|E
argument_list|>
block|{
comment|/** Parameter name. */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"op"
decl_stmt|;
comment|/** Default parameter value. */
DECL|field|DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT
init|=
name|NULL
decl_stmt|;
comment|/** Http operation types */
DECL|enum|Type
specifier|public
enum|enum
name|Type
block|{
DECL|enumConstant|GET
DECL|enumConstant|PUT
DECL|enumConstant|POST
DECL|enumConstant|DELETE
name|GET
block|,
name|PUT
block|,
name|POST
block|,
name|DELETE
block|}
comment|/** Http operation interface. */
DECL|interface|Op
specifier|public
interface|interface
name|Op
block|{
comment|/** @return the Http operation type. */
DECL|method|getType ()
name|Type
name|getType
parameter_list|()
function_decl|;
comment|/** @return true if the operation cannot use a token */
DECL|method|getRequireAuth ()
name|boolean
name|getRequireAuth
parameter_list|()
function_decl|;
comment|/** @return true if the operation will do output. */
DECL|method|getDoOutput ()
name|boolean
name|getDoOutput
parameter_list|()
function_decl|;
comment|/** @return true if the operation will be redirected. */
DECL|method|getRedirect ()
name|boolean
name|getRedirect
parameter_list|()
function_decl|;
comment|/** @return true the expected http response code. */
DECL|method|getExpectedHttpResponseCode ()
name|int
name|getExpectedHttpResponseCode
parameter_list|()
function_decl|;
comment|/** @return a URI query string. */
DECL|method|toQueryString ()
name|String
name|toQueryString
parameter_list|()
function_decl|;
block|}
comment|/** Expects HTTP response 307 "Temporary Redirect". */
DECL|class|TemporaryRedirectOp
specifier|public
specifier|static
class|class
name|TemporaryRedirectOp
implements|implements
name|Op
block|{
DECL|field|CREATE
specifier|static
specifier|final
name|TemporaryRedirectOp
name|CREATE
init|=
operator|new
name|TemporaryRedirectOp
argument_list|(
name|PutOpParam
operator|.
name|Op
operator|.
name|CREATE
argument_list|)
decl_stmt|;
DECL|field|APPEND
specifier|static
specifier|final
name|TemporaryRedirectOp
name|APPEND
init|=
operator|new
name|TemporaryRedirectOp
argument_list|(
name|PostOpParam
operator|.
name|Op
operator|.
name|APPEND
argument_list|)
decl_stmt|;
DECL|field|OPEN
specifier|static
specifier|final
name|TemporaryRedirectOp
name|OPEN
init|=
operator|new
name|TemporaryRedirectOp
argument_list|(
name|GetOpParam
operator|.
name|Op
operator|.
name|OPEN
argument_list|)
decl_stmt|;
DECL|field|GETFILECHECKSUM
specifier|static
specifier|final
name|TemporaryRedirectOp
name|GETFILECHECKSUM
init|=
operator|new
name|TemporaryRedirectOp
argument_list|(
name|GetOpParam
operator|.
name|Op
operator|.
name|GETFILECHECKSUM
argument_list|)
decl_stmt|;
DECL|field|values
specifier|static
specifier|final
name|List
argument_list|<
name|TemporaryRedirectOp
argument_list|>
name|values
init|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|CREATE
argument_list|,
name|APPEND
argument_list|,
name|OPEN
argument_list|,
name|GETFILECHECKSUM
argument_list|)
argument_list|)
decl_stmt|;
comment|/** Get an object for the given op. */
DECL|method|valueOf (final Op op)
specifier|public
specifier|static
name|TemporaryRedirectOp
name|valueOf
parameter_list|(
specifier|final
name|Op
name|op
parameter_list|)
block|{
for|for
control|(
name|TemporaryRedirectOp
name|t
range|:
name|values
control|)
block|{
if|if
condition|(
name|op
operator|==
name|t
operator|.
name|op
condition|)
block|{
return|return
name|t
return|;
block|}
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|op
operator|+
literal|" not found."
argument_list|)
throw|;
block|}
DECL|field|op
specifier|private
specifier|final
name|Op
name|op
decl_stmt|;
DECL|method|TemporaryRedirectOp (final Op op)
specifier|private
name|TemporaryRedirectOp
parameter_list|(
specifier|final
name|Op
name|op
parameter_list|)
block|{
name|this
operator|.
name|op
operator|=
name|op
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getType ()
specifier|public
name|Type
name|getType
parameter_list|()
block|{
return|return
name|op
operator|.
name|getType
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getRequireAuth ()
specifier|public
name|boolean
name|getRequireAuth
parameter_list|()
block|{
return|return
name|op
operator|.
name|getRequireAuth
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getDoOutput ()
specifier|public
name|boolean
name|getDoOutput
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|getRedirect ()
specifier|public
name|boolean
name|getRedirect
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/** Override the original expected response with "Temporary Redirect". */
annotation|@
name|Override
DECL|method|getExpectedHttpResponseCode ()
specifier|public
name|int
name|getExpectedHttpResponseCode
parameter_list|()
block|{
return|return
name|Response
operator|.
name|Status
operator|.
name|TEMPORARY_REDIRECT
operator|.
name|getStatusCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toQueryString ()
specifier|public
name|String
name|toQueryString
parameter_list|()
block|{
return|return
name|op
operator|.
name|toQueryString
argument_list|()
return|;
block|}
block|}
comment|/** @return the parameter value as a string */
annotation|@
name|Override
DECL|method|getValueString ()
specifier|public
name|String
name|getValueString
parameter_list|()
block|{
return|return
name|value
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|HttpOpParam (final Domain<E> domain, final E value)
name|HttpOpParam
parameter_list|(
specifier|final
name|Domain
argument_list|<
name|E
argument_list|>
name|domain
parameter_list|,
specifier|final
name|E
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|domain
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

