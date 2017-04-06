begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
package|;
end_package

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

begin_comment
comment|/**  * FakeTimer can be used for test purposes to control the return values  * from {{@link Timer}}.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|FakeTimer
specifier|public
class|class
name|FakeTimer
extends|extends
name|Timer
block|{
DECL|field|nowNanos
specifier|private
name|long
name|nowNanos
decl_stmt|;
comment|/** Constructs a FakeTimer with a non-zero value */
DECL|method|FakeTimer ()
specifier|public
name|FakeTimer
parameter_list|()
block|{
name|nowNanos
operator|=
literal|1000
expr_stmt|;
comment|// Initialize with a non-trivial value.
block|}
annotation|@
name|Override
DECL|method|now ()
specifier|public
name|long
name|now
parameter_list|()
block|{
return|return
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|toMillis
argument_list|(
name|nowNanos
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|monotonicNow ()
specifier|public
name|long
name|monotonicNow
parameter_list|()
block|{
return|return
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|toMillis
argument_list|(
name|nowNanos
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|monotonicNowNanos ()
specifier|public
name|long
name|monotonicNowNanos
parameter_list|()
block|{
return|return
name|nowNanos
return|;
block|}
comment|/** Increases the time by milliseconds */
DECL|method|advance (long advMillis)
specifier|public
name|void
name|advance
parameter_list|(
name|long
name|advMillis
parameter_list|)
block|{
name|nowNanos
operator|+=
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toNanos
argument_list|(
name|advMillis
argument_list|)
expr_stmt|;
block|}
comment|/**    * Increases the time by nanoseconds.    * @param advNanos Nanoseconds to advance by.    */
DECL|method|advanceNanos (long advNanos)
specifier|public
name|void
name|advanceNanos
parameter_list|(
name|long
name|advNanos
parameter_list|)
block|{
name|nowNanos
operator|+=
name|advNanos
expr_stmt|;
block|}
block|}
end_class

end_unit

