begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics2
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
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
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringJoiner
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_comment
comment|/**  * Immutable tag for metrics (for grouping on host/queue/username etc.)  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|MetricsTag
specifier|public
class|class
name|MetricsTag
implements|implements
name|MetricsInfo
block|{
DECL|field|info
specifier|private
specifier|final
name|MetricsInfo
name|info
decl_stmt|;
DECL|field|value
specifier|private
specifier|final
name|String
name|value
decl_stmt|;
comment|/**    * Construct the tag with name, description and value    * @param info  of the tag    * @param value of the tag    */
DECL|method|MetricsTag (MetricsInfo info, String value)
specifier|public
name|MetricsTag
parameter_list|(
name|MetricsInfo
name|info
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|info
operator|=
name|checkNotNull
argument_list|(
name|info
argument_list|,
literal|"tag info"
argument_list|)
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
DECL|method|name ()
annotation|@
name|Override
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|info
operator|.
name|name
argument_list|()
return|;
block|}
DECL|method|description ()
annotation|@
name|Override
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
name|info
operator|.
name|description
argument_list|()
return|;
block|}
comment|/**    * @return the info object of the tag    */
DECL|method|info ()
specifier|public
name|MetricsInfo
name|info
parameter_list|()
block|{
return|return
name|info
return|;
block|}
comment|/**    * Get the value of the tag    * @return  the value    */
DECL|method|value ()
specifier|public
name|String
name|value
parameter_list|()
block|{
return|return
name|value
return|;
block|}
DECL|method|equals (Object obj)
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|MetricsTag
condition|)
block|{
specifier|final
name|MetricsTag
name|other
init|=
operator|(
name|MetricsTag
operator|)
name|obj
decl_stmt|;
return|return
name|Objects
operator|.
name|equal
argument_list|(
name|info
argument_list|,
name|other
operator|.
name|info
argument_list|()
argument_list|)
operator|&&
name|Objects
operator|.
name|equal
argument_list|(
name|value
argument_list|,
name|other
operator|.
name|value
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|hashCode ()
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hashCode
argument_list|(
name|info
argument_list|,
name|value
argument_list|)
return|;
block|}
DECL|method|toString ()
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|new
name|StringJoiner
argument_list|(
literal|", "
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"{"
argument_list|,
literal|"}"
argument_list|)
operator|.
name|add
argument_list|(
literal|"info="
operator|+
name|info
argument_list|)
operator|.
name|add
argument_list|(
literal|"value="
operator|+
name|value
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

