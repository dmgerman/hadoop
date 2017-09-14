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
enum|enum
name|Op
implements|implements
name|HttpOpParam
operator|.
name|Op
block|{
DECL|enumConstant|OPEN
name|OPEN
argument_list|(
literal|true
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|)
block|,
DECL|enumConstant|GETFILESTATUS
name|GETFILESTATUS
argument_list|(
literal|false
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|)
block|,
DECL|enumConstant|LISTSTATUS
name|LISTSTATUS
argument_list|(
literal|false
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|)
block|,
DECL|enumConstant|GETCONTENTSUMMARY
name|GETCONTENTSUMMARY
argument_list|(
literal|false
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|)
block|,
DECL|enumConstant|GETFILECHECKSUM
name|GETFILECHECKSUM
argument_list|(
literal|true
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|)
block|,
DECL|enumConstant|GETHOMEDIRECTORY
name|GETHOMEDIRECTORY
argument_list|(
literal|false
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|)
block|,
DECL|enumConstant|GETDELEGATIONTOKEN
name|GETDELEGATIONTOKEN
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
comment|/** GET_BLOCK_LOCATIONS is a private unstable op. */
DECL|enumConstant|GET_BLOCK_LOCATIONS
name|GET_BLOCK_LOCATIONS
argument_list|(
literal|false
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|)
block|,
DECL|enumConstant|GETACLSTATUS
name|GETACLSTATUS
argument_list|(
literal|false
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|)
block|,
DECL|enumConstant|GETXATTRS
name|GETXATTRS
argument_list|(
literal|false
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|)
block|,
DECL|enumConstant|GETTRASHROOT
name|GETTRASHROOT
argument_list|(
literal|false
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|)
block|,
DECL|enumConstant|LISTXATTRS
name|LISTXATTRS
argument_list|(
literal|false
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|)
block|,
DECL|enumConstant|GETALLSTORAGEPOLICY
name|GETALLSTORAGEPOLICY
argument_list|(
literal|false
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|)
block|,
DECL|enumConstant|GETSTORAGEPOLICY
name|GETSTORAGEPOLICY
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
block|,
DECL|enumConstant|CHECKACCESS
name|CHECKACCESS
argument_list|(
literal|false
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|)
block|,
DECL|enumConstant|LISTSTATUS_BATCH
name|LISTSTATUS_BATCH
argument_list|(
literal|false
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|)
block|;
DECL|field|redirect
specifier|final
name|boolean
name|redirect
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
DECL|method|Op (final boolean redirect, final int expectedHttpResponseCode)
name|Op
parameter_list|(
specifier|final
name|boolean
name|redirect
parameter_list|,
specifier|final
name|int
name|expectedHttpResponseCode
parameter_list|)
block|{
name|this
argument_list|(
name|redirect
argument_list|,
name|expectedHttpResponseCode
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|Op (final boolean redirect, final int expectedHttpResponseCode, final boolean requireAuth)
name|Op
parameter_list|(
specifier|final
name|boolean
name|redirect
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
name|redirect
operator|=
name|redirect
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
name|GET
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
name|redirect
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
argument_list|<>
argument_list|(
name|NAME
argument_list|,
name|Op
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Constructor.    * @param str a string representation of the parameter value.    */
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
name|getOp
argument_list|(
name|str
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getOp (String str)
specifier|private
specifier|static
name|Op
name|getOp
parameter_list|(
name|String
name|str
parameter_list|)
block|{
try|try
block|{
return|return
name|DOMAIN
operator|.
name|parse
argument_list|(
name|str
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|str
operator|+
literal|" is not a valid "
operator|+
name|Type
operator|.
name|GET
operator|+
literal|" operation."
argument_list|)
throw|;
block|}
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

