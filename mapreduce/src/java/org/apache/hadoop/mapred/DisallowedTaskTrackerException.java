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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * This exception is thrown when a tasktracker tries to register or communicate  * with the jobtracker when it does not appear on the list of included nodes,   * or has been specifically excluded.  *   */
end_comment

begin_class
DECL|class|DisallowedTaskTrackerException
class|class
name|DisallowedTaskTrackerException
extends|extends
name|IOException
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|method|DisallowedTaskTrackerException (TaskTrackerStatus tracker)
specifier|public
name|DisallowedTaskTrackerException
parameter_list|(
name|TaskTrackerStatus
name|tracker
parameter_list|)
block|{
name|super
argument_list|(
literal|"Tasktracker denied communication with jobtracker: "
operator|+
name|tracker
operator|.
name|getTrackerName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

