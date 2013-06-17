begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.event
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|event
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Public
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
operator|.
name|Evolving
import|;
end_import

begin_comment
comment|/**  * Parent class of all the events. All events extend this class.  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Evolving
DECL|class|AbstractEvent
specifier|public
specifier|abstract
class|class
name|AbstractEvent
parameter_list|<
name|TYPE
extends|extends
name|Enum
parameter_list|<
name|TYPE
parameter_list|>
parameter_list|>
implements|implements
name|Event
argument_list|<
name|TYPE
argument_list|>
block|{
DECL|field|type
specifier|private
specifier|final
name|TYPE
name|type
decl_stmt|;
DECL|field|timestamp
specifier|private
specifier|final
name|long
name|timestamp
decl_stmt|;
comment|// use this if you DON'T care about the timestamp
DECL|method|AbstractEvent (TYPE type)
specifier|public
name|AbstractEvent
parameter_list|(
name|TYPE
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
comment|// We're not generating a real timestamp here.  It's too expensive.
name|timestamp
operator|=
operator|-
literal|1L
expr_stmt|;
block|}
comment|// use this if you care about the timestamp
DECL|method|AbstractEvent (TYPE type, long timestamp)
specifier|public
name|AbstractEvent
parameter_list|(
name|TYPE
name|type
parameter_list|,
name|long
name|timestamp
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|timestamp
operator|=
name|timestamp
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTimestamp ()
specifier|public
name|long
name|getTimestamp
parameter_list|()
block|{
return|return
name|timestamp
return|;
block|}
annotation|@
name|Override
DECL|method|getType ()
specifier|public
name|TYPE
name|getType
parameter_list|()
block|{
return|return
name|type
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
literal|"EventType: "
operator|+
name|getType
argument_list|()
return|;
block|}
block|}
end_class

end_unit

