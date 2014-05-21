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
name|regex
operator|.
name|Pattern
import|;
end_import

begin_class
DECL|class|XAttrNameParam
specifier|public
class|class
name|XAttrNameParam
extends|extends
name|StringParam
block|{
comment|/** Parameter name. **/
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"xattr.name"
decl_stmt|;
comment|/** Default parameter value. **/
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
literal|"^(user\\.|trusted\\.|system\\.|security\\.).+"
argument_list|)
argument_list|)
decl_stmt|;
DECL|method|XAttrNameParam (final String str)
specifier|public
name|XAttrNameParam
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
name|str
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
DECL|method|getXAttrName ()
specifier|public
name|String
name|getXAttrName
parameter_list|()
block|{
specifier|final
name|String
name|v
init|=
name|getValue
argument_list|()
decl_stmt|;
return|return
name|v
return|;
block|}
block|}
end_class

end_unit

