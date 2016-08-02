begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.core.exceptions
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|exceptions
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|FinalApplicationStatus
import|;
end_import

begin_comment
comment|/**  * An Exception to be thrown for an explicit "shut down the cluster" operation  * raised by the application state or other parts of the AM  */
end_comment

begin_class
DECL|class|TriggerClusterTeardownException
specifier|public
class|class
name|TriggerClusterTeardownException
extends|extends
name|SliderException
block|{
DECL|field|finalApplicationStatus
specifier|private
specifier|final
name|FinalApplicationStatus
name|finalApplicationStatus
decl_stmt|;
DECL|method|TriggerClusterTeardownException (int code, FinalApplicationStatus finalApplicationStatus, String message, Object... args)
specifier|public
name|TriggerClusterTeardownException
parameter_list|(
name|int
name|code
parameter_list|,
name|FinalApplicationStatus
name|finalApplicationStatus
parameter_list|,
name|String
name|message
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
name|super
argument_list|(
name|code
argument_list|,
name|message
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|this
operator|.
name|finalApplicationStatus
operator|=
name|finalApplicationStatus
expr_stmt|;
block|}
DECL|method|getFinalApplicationStatus ()
specifier|public
name|FinalApplicationStatus
name|getFinalApplicationStatus
parameter_list|()
block|{
return|return
name|finalApplicationStatus
return|;
block|}
block|}
end_class

end_unit

