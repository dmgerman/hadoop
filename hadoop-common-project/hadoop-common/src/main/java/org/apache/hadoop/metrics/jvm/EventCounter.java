begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics.jvm
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics
operator|.
name|jvm
package|;
end_package

begin_comment
comment|/**  * A log4J Appender that simply counts logging events in three levels:  * fatal, error and warn.  *  * @deprecated Use org.apache.hadoop.metrics2 package instead.  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|EventCounter
specifier|public
class|class
name|EventCounter
extends|extends
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|log
operator|.
name|metrics
operator|.
name|EventCounter
block|{
static|static
block|{
comment|// The logging system is not started yet.
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"WARNING: "
operator|+
name|EventCounter
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|" is deprecated. Please use "
operator|+
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|log
operator|.
name|metrics
operator|.
name|EventCounter
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|" in all the log4j.properties files."
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

