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
name|locks
operator|.
name|Lock
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
name|locks
operator|.
name|ReadWriteLock
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
name|locks
operator|.
name|ReentrantReadWriteLock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
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
comment|/**  * This is a wrap class of a {@link ReentrantReadWriteLock}.  * It implements the interface {@link ReadWriteLock}, and can be used to  * create instrumented<tt>ReadLock</tt> and<tt>WriteLock</tt>.  */
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
DECL|class|InstrumentedReadWriteLock
specifier|public
class|class
name|InstrumentedReadWriteLock
implements|implements
name|ReadWriteLock
block|{
DECL|field|readLock
specifier|private
specifier|final
name|Lock
name|readLock
decl_stmt|;
DECL|field|writeLock
specifier|private
specifier|final
name|Lock
name|writeLock
decl_stmt|;
DECL|method|InstrumentedReadWriteLock (boolean fair, String name, Log logger, long minLoggingGapMs, long lockWarningThresholdMs)
name|InstrumentedReadWriteLock
parameter_list|(
name|boolean
name|fair
parameter_list|,
name|String
name|name
parameter_list|,
name|Log
name|logger
parameter_list|,
name|long
name|minLoggingGapMs
parameter_list|,
name|long
name|lockWarningThresholdMs
parameter_list|)
block|{
name|ReentrantReadWriteLock
name|readWriteLock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|(
name|fair
argument_list|)
decl_stmt|;
name|readLock
operator|=
operator|new
name|InstrumentedReadLock
argument_list|(
name|name
argument_list|,
name|logger
argument_list|,
name|readWriteLock
argument_list|,
name|minLoggingGapMs
argument_list|,
name|lockWarningThresholdMs
argument_list|)
expr_stmt|;
name|writeLock
operator|=
operator|new
name|InstrumentedWriteLock
argument_list|(
name|name
argument_list|,
name|logger
argument_list|,
name|readWriteLock
argument_list|,
name|minLoggingGapMs
argument_list|,
name|lockWarningThresholdMs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readLock ()
specifier|public
name|Lock
name|readLock
parameter_list|()
block|{
return|return
name|readLock
return|;
block|}
annotation|@
name|Override
DECL|method|writeLock ()
specifier|public
name|Lock
name|writeLock
parameter_list|()
block|{
return|return
name|writeLock
return|;
block|}
block|}
end_class

end_unit

