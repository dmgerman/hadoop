begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.resourceestimator.common.exception
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|resourceestimator
operator|.
name|common
operator|.
name|exception
package|;
end_package

begin_comment
comment|/**  * Exception thrown by ResourceEstimatorServer utility classes.  */
end_comment

begin_class
DECL|class|ResourceEstimatorException
specifier|public
class|class
name|ResourceEstimatorException
extends|extends
name|Exception
block|{
DECL|method|ResourceEstimatorException (String message)
specifier|public
name|ResourceEstimatorException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
DECL|method|ResourceEstimatorException (String message, Exception ex)
specifier|public
name|ResourceEstimatorException
parameter_list|(
name|String
name|message
parameter_list|,
name|Exception
name|ex
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

