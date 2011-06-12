begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
package|;
end_package

begin_comment
comment|/**  * {@link SimulatorEvent} represents a specific event in Mumak.   *   *  Each {@link SimulatorEvent} has an expected expiry time at which it is fired  *  and an {@link SimulatorEventListener} which will handle the {@link SimulatorEvent} when  *  it is fired.  */
end_comment

begin_class
DECL|class|SimulatorEvent
specifier|public
specifier|abstract
class|class
name|SimulatorEvent
block|{
DECL|field|listener
specifier|protected
specifier|final
name|SimulatorEventListener
name|listener
decl_stmt|;
DECL|field|timestamp
specifier|protected
specifier|final
name|long
name|timestamp
decl_stmt|;
DECL|field|internalCount
specifier|protected
name|long
name|internalCount
decl_stmt|;
DECL|method|SimulatorEvent (SimulatorEventListener listener, long timestamp)
specifier|protected
name|SimulatorEvent
parameter_list|(
name|SimulatorEventListener
name|listener
parameter_list|,
name|long
name|timestamp
parameter_list|)
block|{
name|this
operator|.
name|listener
operator|=
name|listener
expr_stmt|;
name|this
operator|.
name|timestamp
operator|=
name|timestamp
expr_stmt|;
block|}
comment|/**    * Get the expected event expiry time.     * @return the expected event expiry time    */
DECL|method|getTimeStamp ()
specifier|public
name|long
name|getTimeStamp
parameter_list|()
block|{
return|return
name|timestamp
return|;
block|}
comment|/**    * Get the {@link SimulatorEventListener} to handle the {@link SimulatorEvent}.    * @return the {@link SimulatorEventListener} to handle the {@link SimulatorEvent}.    */
DECL|method|getListener ()
specifier|public
name|SimulatorEventListener
name|getListener
parameter_list|()
block|{
return|return
name|listener
return|;
block|}
comment|/**    * Get an internal counter of the {@link SimulatorEvent}. Each {@link SimulatorEvent} holds a    * counter, incremented on every event, to order multiple events that occur    * at the same time.    * @return internal counter of the {@link SimulatorEvent}    */
DECL|method|getInternalCount ()
name|long
name|getInternalCount
parameter_list|()
block|{
return|return
name|internalCount
return|;
block|}
comment|/**    * Set the internal counter of the {@link SimulatorEvent}.    * @param count value to set the internal counter    */
DECL|method|setInternalCount (long count)
name|void
name|setInternalCount
parameter_list|(
name|long
name|count
parameter_list|)
block|{
name|this
operator|.
name|internalCount
operator|=
name|count
expr_stmt|;
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
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"["
operator|+
name|realToString
argument_list|()
operator|+
literal|"]"
return|;
block|}
comment|/**    * Converts the list of fields and values into a human readable format;    * it does not include the class name.    * Override this if you wanted your new fields to show up in toString().    *    * @return String containing the list of fields and their values.    */
DECL|method|realToString ()
specifier|protected
name|String
name|realToString
parameter_list|()
block|{
return|return
literal|"timestamp="
operator|+
name|timestamp
operator|+
literal|", listener="
operator|+
name|listener
return|;
block|}
block|}
end_class

end_unit

