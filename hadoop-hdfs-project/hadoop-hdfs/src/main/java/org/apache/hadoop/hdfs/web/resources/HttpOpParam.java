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
specifier|static
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
block|;   }
comment|/** Http operation interface. */
DECL|interface|Op
specifier|public
specifier|static
interface|interface
name|Op
block|{
comment|/** @return the Http operation type. */
DECL|method|getType ()
specifier|public
name|Type
name|getType
parameter_list|()
function_decl|;
comment|/** @return true if the operation will do output. */
DECL|method|getDoOutput ()
specifier|public
name|boolean
name|getDoOutput
parameter_list|()
function_decl|;
comment|/** @return true the expected http response code. */
DECL|method|getExpectedHttpResponseCode ()
specifier|public
name|int
name|getExpectedHttpResponseCode
parameter_list|()
function_decl|;
comment|/** @return a URI query string. */
DECL|method|toQueryString ()
specifier|public
name|String
name|toQueryString
parameter_list|()
function_decl|;
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

