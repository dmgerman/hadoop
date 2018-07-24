begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.server.events
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|server
operator|.
name|events
package|;
end_package

begin_comment
comment|/**  * Basic event implementation to implement custom events.  *  * @param<T>  */
end_comment

begin_class
DECL|class|TypedEvent
specifier|public
class|class
name|TypedEvent
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Event
argument_list|<
name|T
argument_list|>
block|{
DECL|field|payloadType
specifier|private
specifier|final
name|Class
argument_list|<
name|T
argument_list|>
name|payloadType
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|method|TypedEvent (Class<T> payloadType, String name)
specifier|public
name|TypedEvent
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|payloadType
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|payloadType
operator|=
name|payloadType
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|TypedEvent (Class<T> payloadType)
specifier|public
name|TypedEvent
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|payloadType
parameter_list|)
block|{
name|this
operator|.
name|payloadType
operator|=
name|payloadType
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|payloadType
operator|.
name|getSimpleName
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPayloadType ()
specifier|public
name|Class
argument_list|<
name|T
argument_list|>
name|getPayloadType
parameter_list|()
block|{
return|return
name|payloadType
return|;
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
name|name
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"TypedEvent{"
operator|+
literal|"payloadType="
operator|+
name|payloadType
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|", name='"
operator|+
name|name
operator|+
literal|'\''
operator|+
literal|'}'
return|;
block|}
block|}
end_class

end_unit

