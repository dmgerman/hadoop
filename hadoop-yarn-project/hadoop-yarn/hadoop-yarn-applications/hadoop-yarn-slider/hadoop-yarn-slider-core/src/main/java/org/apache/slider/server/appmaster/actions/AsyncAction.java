begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.actions
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|actions
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|common
operator|.
name|tools
operator|.
name|SliderUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|SliderAppMaster
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|state
operator|.
name|AppState
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Delayed
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_class
DECL|class|AsyncAction
specifier|public
specifier|abstract
class|class
name|AsyncAction
implements|implements
name|Delayed
block|{
DECL|field|sequencer
specifier|private
specifier|static
specifier|final
name|AtomicLong
name|sequencer
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|name
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|nanos
specifier|private
name|long
name|nanos
decl_stmt|;
DECL|field|attrs
specifier|public
specifier|final
name|int
name|attrs
decl_stmt|;
DECL|field|sequenceNumber
specifier|private
specifier|final
name|long
name|sequenceNumber
init|=
name|sequencer
operator|.
name|incrementAndGet
argument_list|()
decl_stmt|;
DECL|method|AsyncAction (String name)
specifier|protected
name|AsyncAction
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|AsyncAction (String name, long delayMillis)
specifier|protected
name|AsyncAction
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|delayMillis
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|delayMillis
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
DECL|method|AsyncAction (String name, long delay, TimeUnit timeUnit)
specifier|protected
name|AsyncAction
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|delay
parameter_list|,
name|TimeUnit
name|timeUnit
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|delay
argument_list|,
name|timeUnit
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|AsyncAction (String name, long delay, TimeUnit timeUnit, int attrs)
specifier|protected
name|AsyncAction
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|delay
parameter_list|,
name|TimeUnit
name|timeUnit
parameter_list|,
name|int
name|attrs
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|setNanos
argument_list|(
name|convertAndOffset
argument_list|(
name|delay
argument_list|,
name|timeUnit
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|attrs
operator|=
name|attrs
expr_stmt|;
block|}
DECL|method|convertAndOffset (long delay, TimeUnit timeUnit)
specifier|protected
name|long
name|convertAndOffset
parameter_list|(
name|long
name|delay
parameter_list|,
name|TimeUnit
name|timeUnit
parameter_list|)
block|{
return|return
name|now
argument_list|()
operator|+
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|convert
argument_list|(
name|delay
argument_list|,
name|timeUnit
argument_list|)
return|;
block|}
comment|/**    * The current time in nanos    * @return now    */
DECL|method|now ()
specifier|protected
name|long
name|now
parameter_list|()
block|{
return|return
name|System
operator|.
name|nanoTime
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getDelay (TimeUnit unit)
specifier|public
name|long
name|getDelay
parameter_list|(
name|TimeUnit
name|unit
parameter_list|)
block|{
return|return
name|unit
operator|.
name|convert
argument_list|(
name|getNanos
argument_list|()
operator|-
name|now
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo (Delayed that)
specifier|public
name|int
name|compareTo
parameter_list|(
name|Delayed
name|that
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|that
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|SliderUtils
operator|.
name|compareTo
argument_list|(
name|getDelay
argument_list|(
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
argument_list|,
name|that
operator|.
name|getDelay
argument_list|(
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
argument_list|)
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
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|super
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" name='"
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
operator|.
name|append
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", delay="
argument_list|)
operator|.
name|append
argument_list|(
name|getDelay
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", attrs="
argument_list|)
operator|.
name|append
argument_list|(
name|attrs
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", sequenceNumber="
argument_list|)
operator|.
name|append
argument_list|(
name|sequenceNumber
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getAttrs ()
specifier|protected
name|int
name|getAttrs
parameter_list|()
block|{
return|return
name|attrs
return|;
block|}
comment|/**    * Ask if an action has an of the specified bits set.     * This is not an equality test.    * @param attr attribute    * @return true iff the action has any of the bits in the attr arg set    */
DECL|method|hasAttr (int attr)
specifier|public
name|boolean
name|hasAttr
parameter_list|(
name|int
name|attr
parameter_list|)
block|{
return|return
operator|(
name|attrs
operator|&
name|attr
operator|)
operator|!=
literal|0
return|;
block|}
comment|/**    * Actual application    * @param appMaster    * @param queueService    * @param appState    * @throws IOException    */
DECL|method|execute (SliderAppMaster appMaster, QueueAccess queueService, AppState appState)
specifier|public
specifier|abstract
name|void
name|execute
parameter_list|(
name|SliderAppMaster
name|appMaster
parameter_list|,
name|QueueAccess
name|queueService
parameter_list|,
name|AppState
name|appState
parameter_list|)
throws|throws
name|Exception
function_decl|;
DECL|method|getNanos ()
specifier|public
name|long
name|getNanos
parameter_list|()
block|{
return|return
name|nanos
return|;
block|}
DECL|method|setNanos (long nanos)
specifier|public
name|void
name|setNanos
parameter_list|(
name|long
name|nanos
parameter_list|)
block|{
name|this
operator|.
name|nanos
operator|=
name|nanos
expr_stmt|;
block|}
DECL|field|ATTR_CHANGES_APP_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|ATTR_CHANGES_APP_SIZE
init|=
literal|1
decl_stmt|;
DECL|field|ATTR_HALTS_APP
specifier|public
specifier|static
specifier|final
name|int
name|ATTR_HALTS_APP
init|=
literal|2
decl_stmt|;
DECL|field|ATTR_REVIEWS_APP_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|ATTR_REVIEWS_APP_SIZE
init|=
literal|4
decl_stmt|;
block|}
end_class

end_unit

