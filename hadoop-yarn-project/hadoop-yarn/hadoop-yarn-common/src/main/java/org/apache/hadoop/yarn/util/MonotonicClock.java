begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
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
name|Time
import|;
end_import

begin_comment
comment|/**  * A monotonic clock from some arbitrary time base in the past, counting in  * milliseconds, and not affected by settimeofday or similar system clock  * changes.  * This is appropriate to use when computing how much longer to wait for an  * interval to expire.  * This function can return a negative value and it must be handled correctly  * by callers. See the documentation of System#nanoTime for caveats.  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Evolving
DECL|class|MonotonicClock
specifier|public
class|class
name|MonotonicClock
implements|implements
name|Clock
block|{
comment|/**    * Get current time from some arbitrary time base in the past, counting in    * milliseconds, and not affected by settimeofday or similar system clock    * changes.    * @return a monotonic clock that counts in milliseconds.    */
DECL|method|getTime ()
specifier|public
name|long
name|getTime
parameter_list|()
block|{
return|return
name|Time
operator|.
name|monotonicNow
argument_list|()
return|;
block|}
block|}
end_class

end_unit

