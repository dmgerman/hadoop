begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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

begin_comment
comment|/**  * HDDS Id generator.  */
end_comment

begin_class
DECL|class|HddsIdFactory
specifier|public
specifier|final
class|class
name|HddsIdFactory
block|{
DECL|method|HddsIdFactory ()
specifier|private
name|HddsIdFactory
parameter_list|()
block|{   }
DECL|field|LONG_COUNTER
specifier|private
specifier|static
specifier|final
name|AtomicLong
name|LONG_COUNTER
init|=
operator|new
name|AtomicLong
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
decl_stmt|;
comment|/**    * Returns an incrementing long. This class doesn't    * persist initial value for long Id's, so incremental id's after restart    * may collide with previously generated Id's.    *    * @return long    */
DECL|method|getLongId ()
specifier|public
specifier|static
name|long
name|getLongId
parameter_list|()
block|{
return|return
name|LONG_COUNTER
operator|.
name|incrementAndGet
argument_list|()
return|;
block|}
comment|/**    * Returns a uuid.    *    * @return UUID.    */
DECL|method|getUUId ()
specifier|public
specifier|static
name|UUID
name|getUUId
parameter_list|()
block|{
return|return
name|UUID
operator|.
name|randomUUID
argument_list|()
return|;
block|}
block|}
end_class

end_unit

