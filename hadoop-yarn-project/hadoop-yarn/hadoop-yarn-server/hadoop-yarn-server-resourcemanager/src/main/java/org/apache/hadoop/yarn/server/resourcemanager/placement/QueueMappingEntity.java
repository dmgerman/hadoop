begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.placement
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|placement
package|;
end_package

begin_class
DECL|class|QueueMappingEntity
specifier|public
class|class
name|QueueMappingEntity
block|{
DECL|field|source
specifier|private
name|String
name|source
decl_stmt|;
DECL|field|queue
specifier|private
name|String
name|queue
decl_stmt|;
DECL|field|parentQueue
specifier|private
name|String
name|parentQueue
decl_stmt|;
DECL|field|DELIMITER
specifier|public
specifier|final
specifier|static
name|String
name|DELIMITER
init|=
literal|":"
decl_stmt|;
DECL|method|QueueMappingEntity (String source, String queue)
specifier|public
name|QueueMappingEntity
parameter_list|(
name|String
name|source
parameter_list|,
name|String
name|queue
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|queue
operator|=
name|queue
expr_stmt|;
name|this
operator|.
name|parentQueue
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|QueueMappingEntity (String source, String queue, String parentQueue)
specifier|public
name|QueueMappingEntity
parameter_list|(
name|String
name|source
parameter_list|,
name|String
name|queue
parameter_list|,
name|String
name|parentQueue
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|queue
operator|=
name|queue
expr_stmt|;
name|this
operator|.
name|parentQueue
operator|=
name|parentQueue
expr_stmt|;
block|}
DECL|method|getQueue ()
specifier|public
name|String
name|getQueue
parameter_list|()
block|{
return|return
name|queue
return|;
block|}
DECL|method|getParentQueue ()
specifier|public
name|String
name|getParentQueue
parameter_list|()
block|{
return|return
name|parentQueue
return|;
block|}
DECL|method|getSource ()
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
name|source
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|super
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object obj)
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
name|QueueMappingEntity
condition|)
block|{
name|QueueMappingEntity
name|other
init|=
operator|(
name|QueueMappingEntity
operator|)
name|obj
decl_stmt|;
return|return
operator|(
name|other
operator|.
name|source
operator|.
name|equals
argument_list|(
name|source
argument_list|)
operator|&&
name|other
operator|.
name|queue
operator|.
name|equals
argument_list|(
name|queue
argument_list|)
operator|)
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|source
operator|+
name|DELIMITER
operator|+
operator|(
name|parentQueue
operator|!=
literal|null
condition|?
name|parentQueue
operator|+
literal|"."
operator|+
name|queue
else|:
name|queue
operator|)
return|;
block|}
block|}
end_class

end_unit

