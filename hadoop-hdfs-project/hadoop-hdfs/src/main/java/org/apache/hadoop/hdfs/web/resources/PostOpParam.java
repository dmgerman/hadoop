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
name|net
operator|.
name|HttpURLConnection
import|;
end_import

begin_comment
comment|/** Http POST operation parameter. */
end_comment

begin_class
DECL|class|PostOpParam
specifier|public
class|class
name|PostOpParam
extends|extends
name|HttpOpParam
argument_list|<
name|PostOpParam
operator|.
name|Op
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
literal|"postOp"
decl_stmt|;
comment|/** Post operations. */
DECL|enum|Op
specifier|public
specifier|static
enum|enum
name|Op
implements|implements
name|HttpOpParam
operator|.
name|Op
block|{
DECL|enumConstant|APPEND
name|APPEND
parameter_list|(
name|HttpURLConnection
operator|.
name|HTTP_OK
parameter_list|)
operator|,
DECL|enumConstant|NULL
constructor|NULL(HttpURLConnection.HTTP_NOT_IMPLEMENTED
block|)
enum|;
DECL|field|expectedHttpResponseCode
specifier|final
name|int
name|expectedHttpResponseCode
decl_stmt|;
DECL|method|Op (final int expectedHttpResponseCode)
name|Op
parameter_list|(
specifier|final
name|int
name|expectedHttpResponseCode
parameter_list|)
block|{
name|this
operator|.
name|expectedHttpResponseCode
operator|=
name|expectedHttpResponseCode
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
name|Type
operator|.
name|POST
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
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|getExpectedHttpResponseCode ()
specifier|public
name|int
name|getExpectedHttpResponseCode
parameter_list|()
block|{
return|return
name|expectedHttpResponseCode
return|;
block|}
comment|/** @return a URI query string. */
DECL|method|toQueryString ()
specifier|public
name|String
name|toQueryString
parameter_list|()
block|{
return|return
name|NAME
operator|+
literal|"="
operator|+
name|this
return|;
block|}
block|}
end_class

begin_decl_stmt
DECL|field|DOMAIN
specifier|private
specifier|static
specifier|final
name|Domain
argument_list|<
name|Op
argument_list|>
name|DOMAIN
init|=
operator|new
name|Domain
argument_list|<
name|PostOpParam
operator|.
name|Op
argument_list|>
argument_list|(
name|NAME
argument_list|,
name|Op
operator|.
name|class
argument_list|)
decl_stmt|;
end_decl_stmt

begin_comment
comment|/**    * Constructor.    * @param str a string representation of the parameter value.    */
end_comment

begin_constructor
DECL|method|PostOpParam (final String str)
specifier|public
name|PostOpParam
parameter_list|(
specifier|final
name|String
name|str
parameter_list|)
block|{
name|super
argument_list|(
name|DOMAIN
argument_list|,
name|DOMAIN
operator|.
name|parse
argument_list|(
name|str
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_constructor

begin_function
annotation|@
name|Override
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|NAME
return|;
block|}
end_function

unit|}
end_unit

