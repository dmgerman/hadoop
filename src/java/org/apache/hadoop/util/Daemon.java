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
comment|/** A thread that has called {@link Thread#setDaemon(boolean) } with true.*/
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|,
literal|"MapReduce"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|Daemon
specifier|public
class|class
name|Daemon
extends|extends
name|Thread
block|{
block|{
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// always a daemon
block|}
DECL|field|runnable
name|Runnable
name|runnable
init|=
literal|null
decl_stmt|;
comment|/** Construct a daemon thread. */
DECL|method|Daemon ()
specifier|public
name|Daemon
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/** Construct a daemon thread. */
DECL|method|Daemon (Runnable runnable)
specifier|public
name|Daemon
parameter_list|(
name|Runnable
name|runnable
parameter_list|)
block|{
name|super
argument_list|(
name|runnable
argument_list|)
expr_stmt|;
name|this
operator|.
name|runnable
operator|=
name|runnable
expr_stmt|;
name|this
operator|.
name|setName
argument_list|(
operator|(
operator|(
name|Object
operator|)
name|runnable
operator|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Construct a daemon thread to be part of a specified thread group. */
DECL|method|Daemon (ThreadGroup group, Runnable runnable)
specifier|public
name|Daemon
parameter_list|(
name|ThreadGroup
name|group
parameter_list|,
name|Runnable
name|runnable
parameter_list|)
block|{
name|super
argument_list|(
name|group
argument_list|,
name|runnable
argument_list|)
expr_stmt|;
name|this
operator|.
name|runnable
operator|=
name|runnable
expr_stmt|;
name|this
operator|.
name|setName
argument_list|(
operator|(
operator|(
name|Object
operator|)
name|runnable
operator|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getRunnable ()
specifier|public
name|Runnable
name|getRunnable
parameter_list|()
block|{
return|return
name|runnable
return|;
block|}
block|}
end_class

end_unit

