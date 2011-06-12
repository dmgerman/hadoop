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
comment|/**  * A clock class - can be mocked out for testing.  */
end_comment

begin_class
DECL|class|SimulatorClock
class|class
name|SimulatorClock
extends|extends
name|Clock
block|{
DECL|field|currentTime
name|long
name|currentTime
decl_stmt|;
DECL|method|SimulatorClock (long now)
name|SimulatorClock
parameter_list|(
name|long
name|now
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|currentTime
operator|=
name|now
expr_stmt|;
block|}
DECL|method|setTime (long now)
name|void
name|setTime
parameter_list|(
name|long
name|now
parameter_list|)
block|{
name|currentTime
operator|=
name|now
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTime ()
name|long
name|getTime
parameter_list|()
block|{
return|return
name|currentTime
return|;
block|}
block|}
end_class

end_unit

