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
comment|/**  * Utility methods for getting the time and computing intervals.  *  * It has the same behavior as {{@link Time}}, with the exception that its  * functions can be overridden for dependency injection purposes.  */
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
DECL|class|Timer
specifier|public
class|class
name|Timer
block|{
comment|/**    * Current system time.  Do not use this to calculate a duration or interval    * to sleep, because it will be broken by settimeofday.  Instead, use    * monotonicNow.    * @return current time in msec.    */
DECL|method|now ()
specifier|public
name|long
name|now
parameter_list|()
block|{
return|return
name|Time
operator|.
name|now
argument_list|()
return|;
block|}
comment|/**    * Current time from some arbitrary time base in the past, counting in    * milliseconds, and not affected by settimeofday or similar system clock    * changes.  This is appropriate to use when computing how much longer to    * wait for an interval to expire.    * @return a monotonic clock that counts in milliseconds.    */
DECL|method|monotonicNow ()
specifier|public
name|long
name|monotonicNow
parameter_list|()
block|{
return|return
name|Time
operator|.
name|monotonicNow
argument_list|()
return|;
block|}
comment|/**    * Same as {@link #monotonicNow()} but returns its result in nanoseconds.    * Note that this is subject to the same resolution constraints as    * {@link System#nanoTime()}.    * @return a monotonic clock that counts in nanoseconds.    */
DECL|method|monotonicNowNanos ()
specifier|public
name|long
name|monotonicNowNanos
parameter_list|()
block|{
return|return
name|Time
operator|.
name|monotonicNowNanos
argument_list|()
return|;
block|}
block|}
end_class

end_unit

