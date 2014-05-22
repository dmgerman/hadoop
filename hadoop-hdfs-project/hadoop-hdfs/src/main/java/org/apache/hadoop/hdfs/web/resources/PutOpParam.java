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
DECL|class|PutOpParam
specifier|public
class|class
name|PutOpParam
extends|extends
name|HttpOpParam
argument_list|<
name|PutOpParam
operator|.
name|Op
argument_list|>
block|{
comment|/** Put operations. */
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
DECL|enumConstant|CREATE
name|CREATE
argument_list|(
literal|true
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_CREATED
argument_list|)
block|,
DECL|enumConstant|MKDIRS
name|MKDIRS
argument_list|(
literal|false
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|)
block|,
DECL|enumConstant|CREATESYMLINK
name|CREATESYMLINK
argument_list|(
literal|false
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|)
block|,
DECL|enumConstant|RENAME
name|RENAME
argument_list|(
literal|false
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|)
block|,
DECL|enumConstant|SETREPLICATION
name|SETREPLICATION
argument_list|(
literal|false
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|)
block|,
DECL|enumConstant|SETOWNER
name|SETOWNER
argument_list|(
literal|false
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|)
block|,
DECL|enumConstant|SETPERMISSION
name|SETPERMISSION
argument_list|(
literal|false
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|)
block|,
DECL|enumConstant|SETTIMES
name|SETTIMES
argument_list|(
literal|false
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|)
block|,
DECL|enumConstant|RENEWDELEGATIONTOKEN
name|RENEWDELEGATIONTOKEN
argument_list|(
literal|false
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|,
literal|true
argument_list|)
block|,
DECL|enumConstant|CANCELDELEGATIONTOKEN
name|CANCELDELEGATIONTOKEN
argument_list|(
literal|false
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|,
literal|true
argument_list|)
block|,
DECL|enumConstant|MODIFYACLENTRIES
name|MODIFYACLENTRIES
argument_list|(
literal|false
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|)
block|,
DECL|enumConstant|REMOVEACLENTRIES
name|REMOVEACLENTRIES
argument_list|(
literal|false
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|)
block|,
DECL|enumConstant|REMOVEDEFAULTACL
name|REMOVEDEFAULTACL
argument_list|(
literal|false
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|)
block|,
DECL|enumConstant|REMOVEACL
name|REMOVEACL
argument_list|(
literal|false
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|)
block|,
DECL|enumConstant|SETACL
name|SETACL
argument_list|(
literal|false
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|)
block|,
DECL|enumConstant|SETXATTR
name|SETXATTR
argument_list|(
literal|false
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|)
block|,
DECL|enumConstant|REMOVEXATTR
name|REMOVEXATTR
argument_list|(
literal|false
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|)
block|,
DECL|enumConstant|CREATESNAPSHOT
name|CREATESNAPSHOT
argument_list|(
literal|false
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|)
block|,
DECL|enumConstant|RENAMESNAPSHOT
name|RENAMESNAPSHOT
argument_list|(
literal|false
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|)
block|,
DECL|enumConstant|NULL
name|NULL
argument_list|(
literal|false
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_NOT_IMPLEMENTED
argument_list|)
block|;
DECL|field|doOutputAndRedirect
specifier|final
name|boolean
name|doOutputAndRedirect
decl_stmt|;
DECL|field|expectedHttpResponseCode
specifier|final
name|int
name|expectedHttpResponseCode
decl_stmt|;
DECL|field|requireAuth
specifier|final
name|boolean
name|requireAuth
decl_stmt|;
DECL|method|Op (final boolean doOutputAndRedirect, final int expectedHttpResponseCode)
name|Op
parameter_list|(
specifier|final
name|boolean
name|doOutputAndRedirect
parameter_list|,
specifier|final
name|int
name|expectedHttpResponseCode
parameter_list|)
block|{
name|this
argument_list|(
name|doOutputAndRedirect
argument_list|,
name|expectedHttpResponseCode
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|Op (final boolean doOutputAndRedirect, final int expectedHttpResponseCode, final boolean requireAuth)
name|Op
parameter_list|(
specifier|final
name|boolean
name|doOutputAndRedirect
parameter_list|,
specifier|final
name|int
name|expectedHttpResponseCode
parameter_list|,
specifier|final
name|boolean
name|requireAuth
parameter_list|)
block|{
name|this
operator|.
name|doOutputAndRedirect
operator|=
name|doOutputAndRedirect
expr_stmt|;
name|this
operator|.
name|expectedHttpResponseCode
operator|=
name|expectedHttpResponseCode
expr_stmt|;
name|this
operator|.
name|requireAuth
operator|=
name|requireAuth
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getType ()
specifier|public
name|HttpOpParam
operator|.
name|Type
name|getType
parameter_list|()
block|{
return|return
name|HttpOpParam
operator|.
name|Type
operator|.
name|PUT
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
name|requireAuth
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
name|doOutputAndRedirect
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
name|doOutputAndRedirect
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
block|}
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
comment|/**    * Constructor.    * @param str a string representation of the parameter value.    */
DECL|method|PutOpParam (final String str)
specifier|public
name|PutOpParam
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
block|}
end_class

end_unit

