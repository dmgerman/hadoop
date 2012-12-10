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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|UserGroupInformation
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|MessageFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/** User parameter. */
end_comment

begin_class
DECL|class|UserParam
specifier|public
class|class
name|UserParam
extends|extends
name|StringParam
block|{
comment|/** Parameter name. */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"user.name"
decl_stmt|;
comment|/** Default parameter value. */
DECL|field|DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT
init|=
literal|""
decl_stmt|;
DECL|field|DOMAIN
specifier|private
specifier|static
specifier|final
name|Domain
name|DOMAIN
init|=
operator|new
name|Domain
argument_list|(
name|NAME
argument_list|,
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^[A-Za-z_][A-Za-z0-9._-]*[$]?$"
argument_list|)
argument_list|)
decl_stmt|;
DECL|method|validateLength (String str)
specifier|private
specifier|static
name|String
name|validateLength
parameter_list|(
name|String
name|str
parameter_list|)
block|{
if|if
condition|(
name|str
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"Parameter [{0}], cannot be NULL"
argument_list|,
name|NAME
argument_list|)
argument_list|)
throw|;
block|}
name|int
name|len
init|=
name|str
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|len
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"Parameter [{0}], it's length must be at least 1"
argument_list|,
name|NAME
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|str
return|;
block|}
comment|/**    * Constructor.    * @param str a string representation of the parameter value.    */
DECL|method|UserParam (final String str)
specifier|public
name|UserParam
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
name|str
operator|==
literal|null
operator|||
name|str
operator|.
name|equals
argument_list|(
name|DEFAULT
argument_list|)
condition|?
literal|null
else|:
name|validateLength
argument_list|(
name|str
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Construct an object from a UGI.    */
DECL|method|UserParam (final UserGroupInformation ugi)
specifier|public
name|UserParam
parameter_list|(
specifier|final
name|UserGroupInformation
name|ugi
parameter_list|)
block|{
name|this
argument_list|(
name|ugi
operator|.
name|getShortUserName
argument_list|()
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

