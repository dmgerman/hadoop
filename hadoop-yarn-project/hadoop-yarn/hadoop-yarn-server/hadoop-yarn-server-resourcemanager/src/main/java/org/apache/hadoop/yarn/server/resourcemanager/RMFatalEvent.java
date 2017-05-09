begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager
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
name|util
operator|.
name|StringUtils
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
name|yarn
operator|.
name|event
operator|.
name|AbstractEvent
import|;
end_import

begin_comment
comment|/**  * Event that indicates a non-recoverable error for the resource manager.  */
end_comment

begin_class
DECL|class|RMFatalEvent
specifier|public
class|class
name|RMFatalEvent
extends|extends
name|AbstractEvent
argument_list|<
name|RMFatalEventType
argument_list|>
block|{
DECL|field|cause
specifier|private
specifier|final
name|Exception
name|cause
decl_stmt|;
DECL|field|message
specifier|private
specifier|final
name|String
name|message
decl_stmt|;
comment|/**    * Create a new event of the given type with the given cause.    * @param rmFatalEventType The {@link RMFatalEventType} of the event    * @param message a text description of the reason for the event    */
DECL|method|RMFatalEvent (RMFatalEventType rmFatalEventType, String message)
specifier|public
name|RMFatalEvent
parameter_list|(
name|RMFatalEventType
name|rmFatalEventType
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|this
argument_list|(
name|rmFatalEventType
argument_list|,
literal|null
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a new event of the given type around the given source    * {@link Exception}.    * @param rmFatalEventType The {@link RMFatalEventType} of the event    * @param cause the source exception    */
DECL|method|RMFatalEvent (RMFatalEventType rmFatalEventType, Exception cause)
specifier|public
name|RMFatalEvent
parameter_list|(
name|RMFatalEventType
name|rmFatalEventType
parameter_list|,
name|Exception
name|cause
parameter_list|)
block|{
name|this
argument_list|(
name|rmFatalEventType
argument_list|,
name|cause
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a new event of the given type around the given source    * {@link Exception} with the given cause.    * @param rmFatalEventType The {@link RMFatalEventType} of the event    * @param cause the source exception    * @param message a text description of the reason for the event    */
DECL|method|RMFatalEvent (RMFatalEventType rmFatalEventType, Exception cause, String message)
specifier|public
name|RMFatalEvent
parameter_list|(
name|RMFatalEventType
name|rmFatalEventType
parameter_list|,
name|Exception
name|cause
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|rmFatalEventType
argument_list|)
expr_stmt|;
name|this
operator|.
name|cause
operator|=
name|cause
expr_stmt|;
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
block|}
comment|/**    * Get a text description of the reason for the event.  If a cause was, that    * {@link Exception} will be converted to a {@link String} and included in    * the result.    * @return a text description of the reason for the event    */
DECL|method|getExplanation ()
specifier|public
name|String
name|getExplanation
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|message
argument_list|)
expr_stmt|;
if|if
condition|(
name|cause
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|cause
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|cause
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
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
name|String
operator|.
name|format
argument_list|(
literal|"RMFatalEvent of type %s, caused by %s"
argument_list|,
name|getType
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|getExplanation
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

