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
comment|/** Http GET operation parameter. */
end_comment

begin_class
DECL|class|GetOpParam
specifier|public
class|class
name|GetOpParam
extends|extends
name|HttpOpParam
argument_list|<
name|GetOpParam
operator|.
name|Op
argument_list|>
block|{
comment|/** Get operations. */
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
DECL|enumConstant|OPEN
name|OPEN
parameter_list|(
name|HttpURLConnection
operator|.
name|HTTP_OK
parameter_list|)
operator|,
DECL|enumConstant|GETFILEBLOCKLOCATIONS
constructor|GETFILEBLOCKLOCATIONS(HttpURLConnection.HTTP_OK
block|)
enum|,
DECL|enumConstant|GETFILESTATUS
name|GETFILESTATUS
parameter_list|(
name|HttpURLConnection
operator|.
name|HTTP_OK
parameter_list|)
operator|,
DECL|enumConstant|LISTSTATUS
constructor|LISTSTATUS(HttpURLConnection.HTTP_OK
block|)
operator|,
DECL|enumConstant|GETDELEGATIONTOKEN
name|GETDELEGATIONTOKEN
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|)
operator|,
DECL|enumConstant|NULL
name|NULL
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_NOT_IMPLEMENTED
argument_list|)
expr_stmt|;
end_class

begin_decl_stmt
DECL|field|expectedHttpResponseCode
specifier|final
name|int
name|expectedHttpResponseCode
decl_stmt|;
end_decl_stmt

begin_expr_stmt
DECL|method|Op (final int expectedHttpResponseCode)
name|Op
argument_list|(
name|final
name|int
name|expectedHttpResponseCode
argument_list|)
block|{
name|this
operator|.
name|expectedHttpResponseCode
operator|=
name|expectedHttpResponseCode
block|;     }
expr|@
name|Override
DECL|method|getType ()
specifier|public
name|HttpOpParam
operator|.
name|Type
name|getType
argument_list|()
block|{
return|return
name|HttpOpParam
operator|.
name|Type
operator|.
name|GET
return|;
block|}
end_expr_stmt

begin_function
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
end_function

begin_function
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
end_function

begin_function
annotation|@
name|Override
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
end_function

begin_decl_stmt
unit|}    private
DECL|field|DOMAIN
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
DECL|method|GetOpParam (final String str)
specifier|public
name|GetOpParam
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

