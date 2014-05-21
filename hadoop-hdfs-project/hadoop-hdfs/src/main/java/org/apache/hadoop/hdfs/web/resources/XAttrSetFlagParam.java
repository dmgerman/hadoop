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
name|EnumSet
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
name|XAttrSetFlag
import|;
end_import

begin_class
DECL|class|XAttrSetFlagParam
specifier|public
class|class
name|XAttrSetFlagParam
extends|extends
name|EnumSetParam
argument_list|<
name|XAttrSetFlag
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
literal|"flag"
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
argument_list|<
name|XAttrSetFlag
argument_list|>
name|DOMAIN
init|=
operator|new
name|Domain
argument_list|<
name|XAttrSetFlag
argument_list|>
argument_list|(
name|NAME
argument_list|,
name|XAttrSetFlag
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|XAttrSetFlagParam (final EnumSet<XAttrSetFlag> flag)
specifier|public
name|XAttrSetFlagParam
parameter_list|(
specifier|final
name|EnumSet
argument_list|<
name|XAttrSetFlag
argument_list|>
name|flag
parameter_list|)
block|{
name|super
argument_list|(
name|DOMAIN
argument_list|,
name|flag
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor.    * @param str a string representation of the parameter value.    */
DECL|method|XAttrSetFlagParam (final String str)
specifier|public
name|XAttrSetFlagParam
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
DECL|method|getFlag ()
specifier|public
name|EnumSet
argument_list|<
name|XAttrSetFlag
argument_list|>
name|getFlag
parameter_list|()
block|{
return|return
name|getValue
argument_list|()
return|;
block|}
block|}
end_class

end_unit

