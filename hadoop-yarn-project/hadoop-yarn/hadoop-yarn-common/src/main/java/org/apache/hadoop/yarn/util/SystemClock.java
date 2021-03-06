begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|Stable
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link Clock} that gives the current time from the system  * clock in milliseconds.  *   * NOTE: Do not use this to calculate a duration of expire or interval to sleep,  * because it will be broken by settimeofday. Please use {@link MonotonicClock}  * instead.  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Stable
DECL|class|SystemClock
specifier|public
specifier|final
class|class
name|SystemClock
implements|implements
name|Clock
block|{
DECL|field|INSTANCE
specifier|private
specifier|static
specifier|final
name|SystemClock
name|INSTANCE
init|=
operator|new
name|SystemClock
argument_list|()
decl_stmt|;
DECL|method|getInstance ()
specifier|public
specifier|static
name|SystemClock
name|getInstance
parameter_list|()
block|{
return|return
name|INSTANCE
return|;
block|}
annotation|@
name|Deprecated
DECL|method|SystemClock ()
specifier|public
name|SystemClock
parameter_list|()
block|{
comment|// do nothing
block|}
DECL|method|getTime ()
specifier|public
name|long
name|getTime
parameter_list|()
block|{
return|return
name|System
operator|.
name|currentTimeMillis
argument_list|()
return|;
block|}
block|}
end_class

end_unit

